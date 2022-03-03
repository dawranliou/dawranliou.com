(ns com.dawranliou.build
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [clojure.data.xml :as xml]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [com.dawranliou.template :as template]
            [hiccup.core :as hiccup])
  (:import [java.io PushbackReader]
           [java.time OffsetDateTime ZoneOffset]
           [java.time.format DateTimeFormatter]))

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

(def non-index-pages
  (into [] (remove :index) site-data))

(def section-map
  (group-by :section non-index-pages))

(def taxonomy
  #{:emacs :clojure})

(def taxonomy-map
  (into {}
        (map (fn [k]
               [k (into []
                        (filter (comp k :tags))
                        non-index-pages)]))
        taxonomy))

(defn md-file->html
  [path]
  (let [markdown (:out (sh "./bin/pandoc" (str path)))]
    [(str path) markdown]))

(def source->html
  "A map from source path to parsed html string"
  (into {}
        (map md-file->html)
        (fs/glob "content" "**.{md,org}")))

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

(def blog-date-formatter
  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))

(defn blog-date-str [date]
  (.format
   (OffsetDateTime/ofInstant (.toInstant date) ZoneOffset/UTC)
   blog-date-formatter))

(defn -main [& _args]
  (println (format "Ensure directory exists: %s" target-dir))
  (when-not (fs/exists? target-dir)
    (fs/create-dir target-dir))

  (println (format "Copy static directory: %s" static-dir))
  (fs/copy-tree static-dir target-dir {:replace-existing true})

  (println "Build blog atom feed")
  (let [site-updated (->> (section-map :blog)
                          (map :updated)
                          (remove nil?)
                          (apply max-key #(.getTime %))
                          date-to-rfc-3339-str)
        site-config* (assoc site-config :site/updated site-updated)
        posts (->> (section-map :blog)
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
  (doseq [{:keys [uri source template index published updated] :as context} site-data
          :let [dest (fs/file (fs/path target-dir
                                       (str/replace uri #"^/" "")
                                       "index.html"))]]
    (println (format "%s -> %s" source (str dest)))
    (->> (cond-> (merge site-config context)
           true (assoc :html (source->html source))
           published (assoc :published-str (blog-date-str published))
           updated (assoc :updated-str (blog-date-str updated))
           index (assoc :list-data (case index
                                     (:clojure :emacs) (taxonomy-map index)
                                     :blog (section-map index))))
         (template/hiccup template)
         page
         (spit-file-ensure-parent dest))))

#_ (-main)
