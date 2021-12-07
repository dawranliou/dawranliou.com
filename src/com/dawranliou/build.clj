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

;;; Build steps and plan

(def base-build-plan
  [{:op :ensure-dir
    :trace "Ensure build target folder exists"
    :dir target-dir}
   {:op :compile-sass
    :trace "Compile sass"
    :src "sass/styles.scss"
    :dst (str target-dir "/styles.css")}
   {:op :copy-dir
    :trace "Copying static assets"
    :src "static/"
    :dst target-dir}])

(defn content-path->uri
  [path]
  (-> (re-matches #"^content/(.*).md$" (str path))
      second
      (str/replace #"(?:\d{4}-\d{2}-\d{2}-)" "")))

(defn md-file->build-step
  [file]
  (let [src (str file)
        file-name (fs/file-name file)
        section? (= "index.md" file-name)
        uri (content-path->uri src)
        section (-> (fs/path file)
                    seq
                    second
                    fs/file-name
                    fs/strip-ext
                    keyword)]
    {:op :page
     :trace "Build page"
     :template (if section? template/list template/page)
     :src src
     :uri uri
     :type (if section? :section :page)
     :section (if (= :content section)
                :home
                section)}))

;;; Build

(defmulti build! :op)

(defmethod build! :ensure-dir
  [{:keys [dir trace]}]
  (println trace)
  (when-not (fs/exists? dir)
    (println (format "%s: %s" trace dir))
    (fs/create-dir dir)))

(defmethod build! :compile-sass
  [{:keys [src dst trace]}]
  (println trace)
  (when (fs/exists? src)
    (println (format "%s: %s -> %s" trace src dst))
    (let [file (fs/file dst)]
      (io/copy (fs/file "sass/styles.scss") file))))

(defmethod build! :copy-dir
  [{:keys [src dst trace]}]
  (println trace)
  (doseq [src (fs/list-dir src)
          :let [dest (fs/path dst (fs/file-name src))]]
    (println (format "%s: %s -> %s" trace (str src) (str dest)))
    (fs/copy-tree src dest {:replace-existing true})))

(defn page [h]
  (hiccup/html {:mode :html}
               "<!DOCTYPE html>"
               h))

(defn fully-qualify-map
  [m ns]
  (reduce-kv
   (fn [m k v]
     (assoc m (keyword (name ns) (name k)) v))
   {}
   m))

(defn parse-markdown-page
  [page-file]
  (let [path (str page-file)
        href (str "/" (content-path->uri path))
        markdown (-> (slurp page-file)
                     ;; allow links with markup over multiple lines
                     (str/replace #"\[[^\]]+\n"
                                  (fn [match]
                                    (str/replace match "\n" " ")))
                     ;; allow reference links without refernce id
                     (str/replace #"\[(\w+)\][^:]"
                                  (fn [[_match link]]
                                    (format "[%s][%s]" link link))))
        metadata (fully-qualify-map
                  (:metadata markdown)
                  :page)
        date (when-let [[_ date-str]
                        (re-matches #"(\d{4}-\d{2}-\d{2})-.*\.md"
                                    (fs/file-name page-file))]
               (.parse LocalDate date-str))]
    (cond-> (assoc metadata
                   :page/href href
                   :html (:html markdown))
      date (assoc :page/date date))))

#_#_#_
(def section-file "content/blog/index.md")
(template/list
 (parse-section-list section-file)
 )

(parse-markdown-page "content/about.md")

(defn parse-section-list
  [section-file]
  (let [{:keys [html metadata]} (markdown/md-to-html-string-with-meta
                                 (slurp section-file)
                                 :heading-anchors true
                                 :reference-links? true
                                 :footnotes? true)
        section-file-paths (fs/glob "." (str (fs/path (fs/parent section-file)
                                                      "*.md")))
        items (into []
                    (comp (filter (comp (partial not= "index.md") fs/file-name))
                          (map fs/file)
                          (map parse-markdown-page))
                    section-file-paths)]
    (assoc (fully-qualify-map metadata :section)
           :html html
           :items items)))

(defmethod build! :page
  [{:keys [src type uri template trace]
    :as context}]
  (let [target-file (fs/file
                     (case type
                       :page (fs/path target-dir uri "index.html")
                       :section (fs/path target-dir (str uri ".html"))))
        html-data (case type
                    :page (parse-markdown-page src)
                    :section (parse-section-list src))]
    (println (format "%s: %s -> %s" trace src (str target-file)))
    (io/make-parents target-file)
    (->> (merge context html-data)
         template
         page
         (spit target-file))))

#_
(-main)

(defn -main [& _args]
  (let [plan (into base-build-plan
                   (comp (map fs/file)
                         (map md-file->build-step))
                   (fs/glob "content" "**.md"))]
    (doseq [step plan]
      (build! (merge step config)))))
