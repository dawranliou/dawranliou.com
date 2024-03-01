(defn capture-front
  "Capture the front matter"
  [chunk]
  (def p (parser/new))
  (parser/consume p chunk)
  (if (= :error (parser/status p))
    (error (parser/error p)))
  (parser/eof p)
  (def ret @[])
  (while (parser/has-more p) (array/push ret (parser/produce p)))
  (first ret))

(def index-page (string/trim (slurp "content/index.md")))
(def contact-page (string/trim (slurp "content/contact.md")))

# (string/split "---\n" index-page)
(defn html [content]
  (def p (os/spawn ["pandoc" "--no-highlight"] :px {:in :pipe :out :pipe}))
  (:write (p :in) content)
  (:close (p :in))
  (:read (p :out) :all))

(defn load-md [path]
  (let [text (string/trim (slurp path))]
    (if (= "---" (string/slice text 0 3))
      (let [[_empty front content] (string/split "---\n" text)]
        (merge-into @{:content (html content)}
                    (capture-front front)))
      @{:content (html text)})))

(load-md "content/index.md")
(load-md "content/contact.md")

(def pages @[])

(def root "/")

(defn add-loaders []
  (put module/loaders :md (fn [path _args]
                            (load-md path)))
  (array/insert module/paths 0 [":all:" :md ".md"]))

(defn page-url [page]
  )

(defn read-pages [root &opt path]
  (default path root)
  (case (os/stat path :mode)
    :directory (each f (sort (os/dir path))
                 (read-pages root (string path "/" f)))
    :file (when (and (> (length path) 3)
                     (= ".md" (string/slice path -4)))
            (print "Parsing: " path)
            (def page (require path))
            (put page :input path)
            # (put page :url (page-get-url root page))
            (array/push pages page))))

# (setdyn *redef* true)
# (setdyn *debug* true)
# (read-pages "content")
