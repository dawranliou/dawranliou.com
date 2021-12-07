(ns com.dawranliou.template)

(defn head
  [{:site/keys [title description og-image base-url twitter-image twitter-id]
    :as context}]
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
   [:link {:rel "stylesheet" :href "/styles.css"}]
   [:link {:rel "alternate" :type "application/rss+xml" :title "RSS"
           :href "https://dawranliou.com/atom.xml"}]])

(defn nav
  [{:keys [section] :as context}]
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
   [:a {:href "/atom.xml", :title "Atom"} "Atom"]])

(defn footer
  [context]
  [:footer [:p "© 2016 - 2021 Daw-Ran Liou"]])

(defn page
  [{:keys [html]
    :page/keys [title]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    [:h1 title]
    html
    [:hr]
    (footer context)]])

(defn list
  [{:keys [html items]
    :section/keys [render-list]
    :as context}]
  [:html {:lang "en"}
   (head context)
   [:body
    (nav context)
    html
    (when render-list
      [:ul
       (for [{:page/keys [href title]}
             (sort-by :page/date #(compare %2 %1) items)]
         [:li [:a {:href href} title]])])
    [:hr]
    (footer context)]])
