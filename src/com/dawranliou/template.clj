(ns com.dawranliou.template)

(defn head
  [{:site/keys [og-image base-url twitter-image twitter-id]
    site-title :site/title
    site-description :site/description
    section-title :section/title
    section-description :section/description
    page-title :page/title
    page-description :page/description
    :as context}]
  (let [title (cond->> site-title
                section-title (format "%s | %s" section-title)
                page-title (format "%s | %s" page-title))
        description (or page-description
                        section-description
                        site-description)]
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
  [{:build/keys [section] :as context}]
  [:nav
   [:a {:class (when (= section :index) "current")
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
   [:a {:href "/atom.xml", :title "Atom"} "Atom"]])

(defn footer
  [context]
  [:footer [:p "© 2016 - 2021 Daw-Ran Liou"]])

(defn page
  [{:page/keys [html]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    html
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
  [{:page/keys [title html]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    [:h1 title]
    html
    cc
    [:hr]
    (footer context)]])

(defn gallery-page
  [{:page/keys [title gallery]
    :as context}]
  (def -context context)
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    [:ul]
    [:h1 title]
    [:main.gallery
     (for [[date alt-text src] (reverse gallery)]
       ;; <img src="{{ image[2] }}" alt="{{ image[1] }} ({{ image[0] }})">
       [:img {:src src
              :alt (format "%s (%s)" alt-text date)}])]
    [:hr]
    (footer context)]])

(defn list
  [{:section/keys [html items render-list]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    html
    (when render-list
      [:ul
       (for [{:page/keys [uri title]}
             (sort-by :page/date #(compare %2 %1) items)]
         [:li [:a {:href uri} title]])])
    [:hr]
    (footer context)]])

(def name->template
  {"page" #'page
   "list" #'list
   "blog-page" #'blog-page
   "gallery-page" #'gallery-page})

(defn hiccup
  [template-name context]
  ((name->template template-name) context))

;; feed

(defn entry
  [& {:keys [title published updated href id content]}]
  [:entry {"xml:lang" "en"}
   [:id id]
   [:title title]
   [:updated updated]
   [:published published]
   [:link {"href" href "type" "text/html"}]
   [:content {"type" "html"} content]])

(defn feed [posts]
  [:feed {"xmlns" "http://www.w3.org/2005/Atom"
          "xml:lang" "en"}
   [:id "https://dawranliou.com/atom.xml"]
   [:title "Daw-Ran Liou's website"]
   [:updated (->> posts
                  (map :updated)
                  (remove nil?)
                  (apply max-key #(.toEpochDay %)))]
   [:link {"href" "https://dawranliou.com/atom.xml"
           "rel" "self"
           "type" "application/atom+xml"}]
   [:link {"href" "https://dawranliou.com"}]
   [:author
    [:name "Daw-Ran Liou"]]
   (for [post posts]
     (entry post))])
