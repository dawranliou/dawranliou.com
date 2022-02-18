(ns com.dawranliou.build
  (:require [babashka.fs :as fs]
            [clojure.data.xml :as xml]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [com.dawranliou.template :as template]
            [hiccup.core :as hiccup]
            [markdown.core :as markdown])
  (:import [java.io PushbackReader]
           [java.time LocalDate]))

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

(def section-tree
  (->> (for [[uri {:keys [section-key] :as section-data}]
             (filter (comp :section-key second) site-map)]
         [uri (into {}
                    (comp (filter (comp (partial = section-key) :section second))
                          (filter (comp (partial not= uri) first)))
                    site-map)])
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

(defn spit-file-ensure-parent [dest-file string]
  (io/make-parents dest-file)
  (spit dest-file string))

(defn -main [& _args]
  (println (format "Ensure directory exists: %s" target-dir))
  (when-not (fs/exists? target-dir)
    (fs/create-dir target-dir))

  (println (format "Copy static directory: %s" static-dir))
  (fs/copy-tree static-dir target-dir {:replace-existing true})

  ;;(println "TODO build atom feed")

  (println "Build markdown contents")
  (doseq [[uri {:keys [source template section-key] :as context}] site-map
          :let [dest (fs/file (fs/path target-dir
                                       (str/replace uri #"^/" "")
                                       "index.html"))]]
    (println (format "%s -> %s" source (str dest)))
    (->> (cond-> (merge site-config context)
           true (assoc :html (source->html source))
           section-key (assoc :section-data (section-tree uri)))
         (template/hiccup template)
         page
         (spit-file-ensure-parent dest))))

#_ (-main)
