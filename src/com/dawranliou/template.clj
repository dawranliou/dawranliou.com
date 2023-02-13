(ns com.dawranliou.template)

(defn head
  [{:site/keys [og-image base-url twitter-image twitter-id]
    site-title :site/title
    site-description :site/description
    page-title :title
    page-description :description
    :as context}]
  (let [title (or page-title site-title)
        description (or page-description site-description)]
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge;chrome=1"}]
     [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
     [:meta {:property "og:image" :content og-image}]
     [:meta {:property "og:type" :content "website"}]
     [:meta {:property "og:image:alt" :content "snapshot"}]
     [:meta {:name "description" :content description}]
     [:meta {:property "og:url" :content base-url}]
     [:meta {:property "og:description" :content description}]
     [:meta {:property "og:title" :content title}]
     [:meta {:name "twitter:title" :content title}]
     [:meta {:name "twitter:description" :content description}]
     [:meta {:name "twitter:image" :content twitter-image}]
     [:meta {:name "twitter:site" :content twitter-id}]
     [:meta {:name "twitter:creator" :content twitter-id}]
     [:title title]
     [:link {:rel "icon" :href "/favicon.png"}]
     [:link {:rel "apple-touch-icon" :href "/favicon.png"}]
     [:link {:rel "stylesheet" :href "/css/styles.css"}]
     [:link {:rel "alternate" :type "application/rss+xml" :title "RSS"
             :href "/atom.xml"}]]))

(defn nav
  [{:keys [section] :as context}]
  [:header
   [:a {:href "#main-content"
        :class "skip-nav-link"} "Skip Navigation"]
   [:nav
    [:a {:class (when (= section :home) "current")
         :href "/"
         :title "Home"}
     "Home"]
    [:a {:class (when (= section :about) "current")
         :href "/about/"
         :title "Blog"}
     "About"]
    [:a {:class (when (= section :blog) "current")
         :href "/blog/"
         :title "Blog"}
     "Blog"]
    [:a {:class (when (= section :gallery) "current")
         :href "/gallery/"
         :title "Gallery"}
     "Gallery"]
    [:a {:class (when (= section :contact) "current")
         :href "/contact/"
         :title "Contact"}
     "Contact"]
    [:a {:href "/atom.xml", :title "Atom"} "Atom"]]])

(defn footer
  [context]
  [:footer [:p "© 2016 - 2023 Daw-Ran Liou"]])

(defmacro main-content
  [& body]
  `[:main {:id "main-content"}
    ~@body])

(defn page
  [{:keys [html]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    (main-content html)
    [:hr]
    (footer context)]])

(def cc
  [:p {:class "cc"}
   "This work is licensed under a "
   [:a {:rel "license noopener noreferrer"
        :href "http://creativecommons.org/licenses/by/4.0/"}
    "Creative Commons Attribution 4.0 International License"]
   "."])

(defn blog-page
  [{:keys [title html tags published-str updated-str]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    (main-content
     [:h1 title]
     (when published-str
       [:p
        (str "Published: " published-str)
        (when (and updated-str (not= updated-str published-str))
          [:em " (last updated: " updated-str ")"])])
     (when (seq tags)
       [:div.tags
        (for [tag tags
              :let [tag-name (name tag)]
              :when (#{:clojure :emacs} tag)]
          [:a {:href (str "/tags/" tag-name)}
           (str "#" tag-name)])])
     html)
    cc
    [:hr]
    (footer context)]])

(def gallery
  [["2021-03-19" "Note taking" "/images/profiles-all.png"]
   ["2020-05-25" "Coding 16x14" "/images/2020-5-25-coding.gif"]
   ["2020-05-10" "Tennis" "/images/2020-5-10-tennis.png"]
   ["2020-04-26" "Dawran Bella 24px" "/images/2020-4-26-dawran-bella-24px.png"]
   ["2020-04-23" "Church" "/images/2020-4-23-church.png"]
   ["2020-04-21" "Sumikko Gurashi" "/images/2020-4-21-sumikko-gurashi.png"]
   ["2020-04-20" "Vin" "/images/2020-4-20-figure.gif"]
   ["2020-04-19" "Tatung cooker" "/images/2020-4-19-tatung-cooker.png"]
   ["2020-04-19" "Daw-Ran Liou v2" "/images/2020-4-19-dawran-v2.png"]
   ["2020-04-19" "Isabella v1" "/images/2020-4-19-isabella-v1.png"]
   ["2020-04-18" "Desk" "/images/2020-4-18-desk_dream.gif"]
   ["2020-04-17" "Macbook" "/images/2020-4-17-macbook.gif"]
   ["2020-04-16" "Hario V60" "/images/2020-4-16-hario-v60-animate.gif"]
   ["2020-04-15" "Bouncing Ball" "/images/2020-4-15-bouncing-ball.gif"]])

(defn gallery-page
  [{:keys [html]
    :as context}]
  (def -context context)
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    (main-content
     html
     [:div.gallery
      (for [[date alt-text src] gallery]
        [:img {:src src
               :alt (format "%s (%s)" alt-text date)}])])
    [:hr]
    (footer context)]])

(defn list
  [{:keys [html list-data]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    (main-content
     html
     [:ul
      (for [{:keys [uri title]} (sort-by :published #(compare %2 %1) list-data)]
        [:li [:a {:href uri} title]])])
    [:hr]
    (footer context)]])

(defn blog-list
  [{:keys [html list-data]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    (main-content
     html
     (when list-data
       (let [year-groups
             (->> list-data
                  (map (fn [{:keys [uri published] :as data}]
                         (assoc data
                                :uri uri
                                :year (+ 1900 (.getYear published)))))
                  (group-by :year))]
         (for [[year year-group] (sort-by first > year-groups)]
           [:section
            [:h2 year]
            [:ul
             (for [{:keys [uri title]}
                   (sort-by :published #(compare %2 %1) year-group)]
               [:li [:a {:href uri} title]])]])))
     [:section
      [:h2 "Tags"]
      [:ul
       [:li [:a {:href "/tags/clojure"} "#clojure"]]
       [:li [:a {:href "/tags/emacs"} "#emacs"]]]])
    [:hr]
    (footer context)]])

(def name->template
  {;; generic templates
   :list #'list
   :page #'page
   ;; specific templates
   :home #'page
   :gallery #'gallery-page
   ;; blog templates
   :blog-list #'blog-list
   :blog-page #'blog-page})

(defn hiccup
  [template-name context]
  ((name->template template-name) context))

;; feed

(defn entry
  [{:keys [title published-str updated-str uri html]
    base-url :site/base-url}]
  [:entry {"xml:lang" "en"}
   [:id (str base-url uri)]
   [:author [:name "Daw-Ran Liou"]]
   [:title title]
   [:updated updated-str]
   [:published published-str]
   [:link {"href" (str base-url uri) "type" "text/html"}]
   [:content {"type" "html"} html]])

(defn feed [{:site/keys [updated] :as site-config} posts]
  [:feed {"xmlns:atom" "http://www.w3.org/2005/Atom"
          "xml:lang" "en"}
   [:id "https://dawranliou.com/atom.xml"]
   [:title "Daw-Ran Liou's website"]
   [:updated updated]
   [:link {"href" "https://dawranliou.com/atom.xml"
           "rel" "self"
           "type" "application/atom+xml"}]
   [:link {"href" "https://dawranliou.com"}]
   [:author
    [:name "Daw-Ran Liou"]]
   (for [post posts]
     (entry (merge site-config post)))])
