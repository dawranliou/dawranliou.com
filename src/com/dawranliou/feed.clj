(ns com.dawranliou.feed
  (:require [clojure.data.xml :as xml])
  (:import [java.time LocalDateTime ZonedDateTime]))



(defn rss-str [feed]
  (xml/indent-str
   (xml/sexp-as-element feed)))

(comment
  (->
   (feed [{:title "1"
           :published (LocalDate/parse "2001-01-01")
           :updated (LocalDate/parse "2001-01-01")
           :href "/1"
           :id 1
           :content "1"}
          {:title "2"
           :published (LocalDate/parse "2001-01-02")
           :updated (LocalDate/parse "2001-01-02")
           :href "/2"
           :id 2
           :content "2"}
          {:title "3"
           :published (LocalDate/parse "2001-01-03")
           :updated (LocalDate/parse "2001-01-03")
           :href "/3"
           :id 3
           :content "3"}])
   rss-str
   println)
  )
