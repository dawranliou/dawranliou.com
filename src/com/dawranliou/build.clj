(ns com.dawranliou.build
  (:require [babashka.fs :as fs]
            [clojure.data.xml :as xml]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [com.dawranliou.template :as template]
            [hiccup.core :as hiccup]
            [markdown.core :as markdown])
  (:import [java.io PushbackReader]))

(def content-source-dir "content")
(def target-dir "public")
(def static-dir "static")

(def site-config
  {:site/title "Daw-Ran Liou"
   :site/description "Hi there, my name is Daw-Ran. This is my personal website, where I share my writings and other public information about myself."
   :site/base-url "https://dawranliou.com"
   :site/og-image "https://dawranliou.com/dawranliou-profile.png"
   :site/twitter-id "@dawranliou"
   :site/twitter-image "https://dawranliou.com/dawranliou-profile.png"})

(def site-map
  (edn/read (PushbackReader. (io/reader "site-map.edn"))))

(def site-data
  (into []
        (map (fn [[uri data]] (assoc data :uri uri)))
        site-map))

(def section-map
  (->> (for [{:keys [uri section-key] :as section-data}
             (filter :section-key site-data)]
         [uri (into []
                    (comp (filter (comp (partial = section-key) :section))
                          (filter (comp (partial not= uri) :uri)))
                    site-data)])
       (into {})))

(defn md-file->html
  [path]
  (let [markdown (-> path
                     fs/file
                     slurp
                     ;; allow links with markup over multiple lines
                     (str/replace #"\[[^\]]+\n"
                                  (fn [match]
                                    (str/replace match "\n" " ")))
                     (markdown/md-to-html-string :heading-anchors true
                                                 :reference-links? true
                                                 :footnotes? true))]
    [(str path) markdown]))

(def source->html
  "A map from source path to parsed html string"
  (into {}
        (map md-file->html)
        (fs/glob "content" "**.md")))

(defn page [h]
  (hiccup/html {:mode :html} "<!DOCTYPE html>" h))

(defn rss-str [feed]
  (-> (xml/indent-str (xml/sexp-as-element feed))
      ;; clojure.data.xml preemptively adds the namespace to the xmlns
      ;; attribute, which I don't need.
      (str/replace-first #"xmlns:atom" "xmlns")))

(defn spit-file-ensure-parent [dest-file string]
  (io/make-parents dest-file)
  (spit dest-file string))

(defn date-to-rfc-3339-str [date]
  (str/replace (json/generate-string date) #"\"" ""))

(defn -main [& _args]
  (println (format "Ensure directory exists: %s" target-dir))
  (when-not (fs/exists? target-dir)
    (fs/create-dir target-dir))

  (println (format "Copy static directory: %s" static-dir))
  (fs/copy-tree static-dir target-dir {:replace-existing true})

  (println "Build blog atom feed")
  (let [site-updated (->> (section-map "/blog")
                          (map :updated)
                          (remove nil?)
                          (apply max-key #(.getTime %))
                          date-to-rfc-3339-str)
        site-config* (assoc site-config :site/updated site-updated)
        posts (->> (section-map "/blog")
                   (into [] (map (fn [{:keys [source updated published] :as context}]
                                   (assoc context
                                          :html (source->html source)
                                          :published-str (date-to-rfc-3339-str published)
                                          :updated-str (date-to-rfc-3339-str updated))))))]
    (->> posts
         (template/feed site-config*)
         rss-str
         (spit-file-ensure-parent (fs/file (fs/path target-dir "atom.xml"))))
    (->> posts
         (filter (comp :emacs :tags))
         (template/feed site-config*)
         rss-str
         (spit-file-ensure-parent (fs/file (fs/path target-dir "tags/emacs/atom.xml"))))
    (->> posts
         (filter (comp :clojure :tags))
         (template/feed site-config*)
         rss-str
         (spit-file-ensure-parent (fs/file (fs/path target-dir "tags/clojure/atom.xml")))))

  (println "Build markdown contents")
  (doseq [{:keys [uri source template section-key] :as context} site-data
          :let [dest (fs/file (fs/path target-dir
                                       (str/replace uri #"^/" "")
                                       "index.html"))]]
    (println (format "%s -> %s" source (str dest)))
    (->> (cond-> (merge site-config context)
           true (assoc :html (source->html source))
           section-key (assoc :section-data (section-map uri)))
         (template/hiccup template)
         page
         (spit-file-ensure-parent dest))))

#_ (-main)
