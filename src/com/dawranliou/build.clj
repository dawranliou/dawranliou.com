(ns com.dawranliou.build
  (:require [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [com.dawranliou.template :as template]
            [hiccup.core :as hiccup]
            [markdown.core :as markdown])
  (:import [java.time LocalDate]))

;;; Configs

(def content-source-dir "content")
(def target-dir "public")

(def config
  {:site/title "Daw-Ran Liou"
   :site/description "Hi there, my name is Daw-Ran. This is my personal website, where I share my writings and other public information about myself."
   :site/base-url "https://dawranliou.com"
   :site/og-image "https://dawranliou.com/dawranliou-profile.png"
   :site/twitter-id "@dawranliou"
   :site/twitter-image "https://dawranliou.com/dawranliou-profile.png"})

(defn path->uri
  [path]
  (-> (re-matches #"^content(/.*).md$" path)
      second
      (str/replace #"index$" "")
      (str/replace #"(?:\d{4}-\d{2}-\d{2}-)" "")))

(comment
  (path->uri "content/index.md")
  (path->uri "content/blog/index.md")
  (path->uri "content/blog/1999-09-09-hi.md")
  (path->uri "content/about.md"))

(defn retrofit-markdown
  [markdown]
  (-> markdown
      ;; allow links with markup over multiple lines
      (str/replace #"\[[^\]]+\n"
                   (fn [match]
                     (str/replace match "\n" " ")))))

(defn parse-markdown
  [path]
  (let [uri (path->uri (str path))
        markdown (-> path
                     fs/file
                     slurp
                     retrofit-markdown
                     (markdown/md-to-html-string-with-meta
                      :heading-anchors true
                      :reference-links? true
                      :footnotes? true))
        date (when-let [[_ date-str]
                        (re-matches #"(\d{4}-\d{2}-\d{2})-.*\.md"
                                    (fs/file-name path))]
               (.parse LocalDate date-str))]
    [(str path) (cond-> markdown
                  true (assoc-in [:metadata :uri] uri)
                  date (assoc-in [:metadata :date] date))]))

(def md-data
  (into {}
        (map parse-markdown)
        (fs/glob "content" "**.md")))

;;; Build steps and plan

(def base-build-plan
  [{:build/op :ensure-dir
    :build/trace "Ensure build target folder exists"
    :build/dir target-dir}
   {:build/op :compile-sass
    :build/trace "Compile sass"
    :build/src "sass/styles.scss"
    :build/dst (str target-dir "/styles.css")}
   {:build/op :copy-dir
    :build/trace "Copying static assets"
    :build/src "static/"
    :build/dst target-dir}])

(defn fully-qualify-map
  [ns m]
  (reduce-kv
   (fn [m k v]
     (assoc m (keyword (name ns) (name k)) v))
   {}
   m))

(defn md-file-path->build-step
  [path]
  (let [file-name (fs/file-name path)
        section? (= "index.md" file-name)
        uri (path->uri path)
        section (-> (fs/path path)
                    second
                    fs/file-name
                    fs/strip-ext
                    keyword)]
    {:build/op (if section? :section :page)
     :build/trace (if section? "Build section" "Build page")
     :build/src path
     :build/uri uri
     :build/section (if (= :content section)
                      :home
                      section)}))

(comment
  (md-file-path->build-step "content/index.md")
  (md-file-path->build-step "content/blog/1999-01-01-hi.md")
  )

;;; Build

(defmulti build! :build/op)

(defmethod build! :ensure-dir
  [{:build/keys [dir trace]}]
  (println trace)
  (when-not (fs/exists? dir)
    (println (format "%s: %s" trace dir))
    (fs/create-dir dir)))

(defmethod build! :compile-sass
  [{:build/keys [src dst trace]}]
  (println trace)
  (when (fs/exists? src)
    (println (format "%s: %s -> %s" trace src dst))
    (let [file (fs/file dst)]
      (io/copy (fs/file "sass/styles.scss") file))))

(defmethod build! :copy-dir
  [{:build/keys [src dst trace]}]
  (println trace)
  (doseq [src (fs/list-dir src)
          :let [dest (fs/path dst (fs/file-name src))]]
    (println (format "%s: %s -> %s" trace (str src) (str dest)))
    (fs/copy-tree src dest {:replace-existing true})))


(defn page [h]
  (hiccup/html {:mode :html} "<!DOCTYPE html>" h))

(defmethod build! :page
  [{:build/keys [src uri trace]
    :as context}]
  (let [target-file (fs/file (fs/path target-dir
                                      (str/replace uri #"^/" "")
                                      "index.html"))
        {page-metadata :metadata page-html :html} (md-data src)
        section-metadata (-> (md-data (str (fs/path (fs/parent src)
                                                    "index.md")))
                             :metadata)
        context' (merge context
                        (fully-qualify-map :section section-metadata)
                        (fully-qualify-map :page page-metadata)
                        {:page/html page-html})
        ;; template inheritance order
        template-name (or (:page-template page-metadata)
                          (:page-template section-metadata)
                          "page")]
    (println (format "%s: %s -> %s" trace src (str target-file)))
    (io/make-parents target-file)
    (->> (template/hiccup template-name context')
         page
         (spit target-file))))

(comment
  (build! {:build/op :page,
           :build/trace "Build page",
           :build/src "content/blog/2021-11-09-compiling-emacs-from-source.md",
           :build/uri "/blog/compiling-emacs-from-source",
           :build/section :blog})

  (fs/glob content-source-dir
           (str (fs/path (fs/parent "content/blog/index.md") "*.md")))
  )

(defmethod build! :section
  [{:build/keys [src uri trace]
    :as context}]
  (let [target-file (fs/file (fs/path target-dir
                                      (str/replace uri #"^/" "")
                                      "index.html"))
        {:keys [metadata html]} (md-data src)
        section-file-paths (fs/glob "."
                                    (str (fs/path (fs/parent src) "*.md")))
        items (into []
                    (comp (filter (comp (partial not= "index.md") fs/file-name))
                          (map str)
                          (map md-data)
                          (map :metadata)
                          (map (partial fully-qualify-map :page)))
                    section-file-paths)
        context' (merge context
                        (fully-qualify-map :section metadata)
                        {:section/items items
                         :section/html html})
        template-name (or (:section-template metadata)
                          "list")]
    (println (format "%s: %s -> %s" trace src (str target-file)))
    (io/make-parents target-file)
    (->> (template/hiccup template-name context')
         page
         (spit target-file))))

(comment
  (build! {:build/op :section,
           :build/trace "Build section",
           :build/template template/list,
           :build/src "content/blog/index.md",
           :build/uri "/blog/",
           :build/section :blog})
  (first md-data)
  )

(defn -main [& _args]
  (let [plan (into base-build-plan
                   (comp (map str)
                         (map md-file-path->build-step))
                   (fs/glob "content" "**.md"))]
    (doseq [step plan]
      (build! (merge step config)))))

(comment
  (-main)
  )
