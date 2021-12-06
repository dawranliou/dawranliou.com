---
title: Implementing an Event-Driven ClojureScript mini-framework with core.async
authors: Daw-Ran Liou
tags: [clojure]
---

This was my experiment of re-implementing a lighter version of the popular
ClojureScript front-end framework, [`re-frame`][2], for the purpose of
studying.[^1] A demo project of the result can be found here -
[`mini-reframe`][1]. In the project README, you can see the section
["Self-guided source code tour"][3] if you like to read the code. In this post,
I'd walk you through the steps of implementing it from scratch.

**Disclaimer:** It is not my goal to convince you to implement your own
`re-frame` from scratch for your apps. I merely wanted to share what I learned
from `re-frame` by replicating what I deem to be its most essential concepts in
this tutorial-like article.

If you come to this article wondering whether to use `re-frame` or just roll
your own framework, please use `re-frame`.

If you are pretty familiar with using `re-frame`, never looked at its source
code, and wondering what's happening under the cover, this article is for you.

## Prerequisite

To get started, you need a ClojureScript project and preferably any
live-reloading tool like [`figwheel-main`][5] or [`shadow-cljs`][6]. If you are
starting from scratch, using [`figwheel-main`][7] is a good option.

Once your project is ready, add the following dependencies if you haven't done
so already:

1. [`core.async`][8]
1. [`reagent`][9]

## Why event-driven?

The way I see it, an event-driven architecture is modeling after systems that
need to handle concurrent events by separating the **when (it happens)** from
the **what (to do about it)**. Take web UIs for example. Browser JavaScript uses
this model to handle mouse events, keyboard events, focus events, etc.,
internally in the [event loop][12]. We can define what to do on receiving
certain events and let the browser figure out when those events happen.

Nowadays, apps can be complex. Sometimes the mouse and keyboard events might not
be the right level of abstraction for an app. You might want the events to be
more abstract such as a "user-agrees-to-terms-of-service" event or a
"server-cannot-validate-user-credentials" event. In this case, an application
level event-driven framework, such as `re-frame`, would be helpful.

We are going to build one for ourselves today.

## Core event loop

`re-frame` at its core runs the `EventQueue` that is implemented as a finite
state machine (FSM for short). You can find the source [here][10] if you are
interested. The finite states of the `EventQueue` are: `:idle`, `:scheduled`,
`:running`, or `:paused`. it maintains a queue of events and [`handle`][11]s the
events one at a time.

Instead of implementing our own FSM from scratch, we'll be using `core.async`,
_"a Clojure library providing facilities for async programming and
communication."_ Interestingly enough, under the covers core.async creates a
**state machine** to turn synchronous / blocking looking code into asynchronous
/ non-blocking code. [^2] Using the state machine provided by `core.async` to
implement our event queue FSM seems to me that we are on the right track.

A naive implementation is to run an infinite `go-loop` that will park (pause)
the execution at the input event channel:

```
(require '[clojure.core.async :as a])

(def events-ch (a/chan))  ; The input event channel

(a/go-loop []
  (let [event (a/<! events-ch)]  ; Park the execution till receiving an event
    ;; Do something ...
    (js/console.log event)
    )
  (recur))
```

The Inversion-of-Control (IoC) happens at the async take function (`a/<!`). Only
when an event is put onto the channel will the `go-loop` continue executing the
rest of its body until it is parked again.

Next, we'll be re-implementing some of the `re-frame.core` functions, following
`re-frame`'s data loop [^3] concept.

## Event dispatching

For event dispatching, all we need is to put events onto the shared `events-ch`:

```
(defn dispatch!
  [event]
  (a/put! events-ch event))

(comment
  (dispatch! [:event-name :event-data]))
```

I dig the re-frame's convention that every event is a vector of `[EVENT_TYPE
EVENT_DATA...]` so from the rest of the article, an `event` is a vector.

## Event handling

Once the core event loop received an `event`, we'll need to decide what to do
with it. `re-frame` has a great way to push the side effects to the application
boundary by adding an extra layer of indirection between events and their
effects. Side effects only happen in a very controlled manner, and effect
handlers can be entirely descriptive pure functions. This is what we are going
to do too!

Event handlers receive two arguments, the snapshot of the current app-state, the
`db`, and the `event` vector. Depending on your tastes, there are many ways to
handle events. You need a mechanism to dispatch the functionalities based on the
`event` type, which is the first element in the `event`. Here are a couple of
options for you:

```
;; 1. Implemented as a case form
(defn handle-event
  [db [event-type & event-data :as event]]
  (case event-type
    :event-1 (fn [db event] ...)
    :event-2 (fn [db event] ...)))

;; 2. Implemented as a multimethod
(defmulti handle-event (fn [_db event] (first event)))
(defmethod handle-event :event-1
  [db event]
  ...)

;; 3. Implemented as a map
(def event-handlers
  {:event-1 (fn [db event] ...)
   :event-2 (fn [db event] ...)})

(defn handle-event
  [db event-handlers [event-type & _event-params :as event]]
  ((event-handlers event-type) db event))
```

I prefer to implement the handlers using the 3rd option because that feels more
portable to me.

## Effect (`fx`) handling

In the section above, we haven't defined what the event handler functions
return. Again, learning from our beloved project `re-frame`, it's a good idea
that our event handlers return a descriptive map of effects, such as:

1. `:db` effect to mutate the app-state ratom,
1. `:dispatch` effect to dispatch another event
1. `:http` effect to handle AJAX,

Unlike event handlers, which are pure functions, the effect handlers are messier
that it receives the app-state ratom as an argument and can cause side
effects. Although they are side-effect-y, their implementations are pretty
predictable. Out-of-the-box `re-frame` provides several of the standard effect
handlers like `:db` and `:dispatch`.

Below is an example of using a plain map to implement our `fx-handlers`. Again,
there are many ways to write the effect handling logic as we did for event
handling.

```
(defn do-db!
  [page-state _effect-key new-db]
  (when-not (identical? new-db @page-state)
    (reset! page-state new-db)))

(defn do-dispatch!
  [_page-state _effect-key event-v]
  (dispatch! event-v))

(def fx-handlers
  {:db       do-db!
   :dispatch do-dispatch!})

(defn handle-fx!
  [state fx-handlers {:keys [db] :as effects-map}]
  ;; Do the :db effect before any other effects
  (when db
    ((fx-handler :db) state :db db))
  (doseq [[effect-key effect-value] (dissoc effects-map :db)]
    ((fx-handler effect-key) state effect-key effect-value)))
```

## Revisit the core event loop

Previously, we conveniently hide the details in the core event loop. Let's get
back to it now since we (sort of) implemented the `handle-event` and
`handle-fx!` functions.

```
(require '[reagent.core :as r])
(defonce app-state (r/atom {}))

(a/go-loop []
  (let [event (a/<! events-ch)
        fxs   (handle-event @app-state event-handlers event)]
    (handle-fx! app-state fx-handlers fxs)
  (recur)))
```

We finally declared our app state ratom to use it in the event handlers and the
effect handlers. Interestingly, we ended up with a very straightforward
transducer that transduces an `event` to `fxs`, then to side effects, and
finally discards the return value. If you are interested in the transducer
implementation you can find it [here in the repo][14].

## Query subscription

Since we don't want to re-render the whole app when we only touched a particular
part of the app-state, we need to be more specific. `re-frame.core/subscribe` is
a nice API to do such a thing. Under the hood, the API returns a
`reagent.ratom/Reaction` for you and handles caching so you don't run the same
subscription handler twice. `Reaction` is how we avoid re-rendering the whole
app because it only signals a re-rendering when its body updates. Although
`Reaction`s are a powerful tool, we should not use them directly inside a form-1
component.[^3]

For simplicity, we'll pre-calculate every subscription in a subscription
map. Although we cannot create subscriptions lazily since every subscription is
bound at compile time, we also don't have to worry about caching and the problem
with component life-cycle. Here's an example of subscriptions in the demo
project:

```
(def subscribe
  {:h    (reagent.ratom/make-reaction
           #(str "H - " (or (get-in @page-state [:h]) 0)))
   :j    (reagent.ratom/make-reaction
           #(str "j - " (or (get-in @page-state [:j]) 0)))
   :k    (reagent.ratom/make-reaction
           #(str "k - " (or (get-in @page-state [:k]) 0)))
   :l    (reagent.ratom/make-reaction
           #(str "L - " (or (get-in @page-state [:l]) 0)))
   :http (reagent.ratom/make-reaction
           #(str "Server status - "
                 (name (or (get-in @page-state [:http]) :unknown))))})
```

## View and DOM

With the functions we've built so far, the app page can now look like this:

```
(defn main []
  [:<>
   [:h1 "App"]
   [:button {:on-click #(dispatch! [:clicked {:element :h}])} "H"]
   [:button {:on-click #(dispatch! [:clicked {:element :j}])} "J"]
   [:button {:on-click #(dispatch! [:clicked {:element :k}])} "K"]
   [:button {:on-click #(dispatch! [:clicked {:element :l}])} "L"]
   [:button {:on-click #(dispatch! [:reset])} "Reset"]
   [:ul
    [:li @(subscribe :h)]
    [:li @(subscribe :j)]
    [:li @(subscribe :k)]
    [:li @(subscribe :l)]]
   [:p @(subscribe :http)]])
```

## Putting it all together

Our app can now look like this:

```
(ns ^:figwheel-hooks mini-reframe.app
  (:require
   [clojure.core.async :as a]
   [goog.dom :as gdom]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reagent.ratom]))

(def events-ch (a/chan))

(defn dispatch! [event]
  (a/put! events-ch event))

(defonce app-state (r/atom {}))

(def subscribe
  {:h    (reagent.ratom/make-reaction
           #(str "H - " (or (get-in @app-state [:h]) 0)))
   :j    (reagent.ratom/make-reaction
           #(str "j - " (or (get-in @app-state [:j]) 0)))
   :k    (reagent.ratom/make-reaction
           #(str "k - " (or (get-in @app-state [:k]) 0)))
   :l    (reagent.ratom/make-reaction
           #(str "L - " (or (get-in @app-state [:l]) 0)))
   :http (reagent.ratom/make-reaction
           #(str "Server status - "
                 (name (or (get-in @app-state [:http]) :unknown))))})

(defn handle-init
  [_db _event]
  {:http {:method     :get
          :url        "http://localhost:1111"
          :on-success [:good-http-result]
          :on-failure [:bad-http-result]}})

(defn handle-good-http-result
  [db [_event-type data]]
  {:db (assoc db :http (:body data))})

(defn handle-bad-http-result
  [db [_event-type data]]
  {:db (assoc db :http (:body data))})

(defn handle-clicked
  [db [_event-type {:keys [element] :as _data}]]
  {:db (update db element inc)})

(defn handle-reset
  [db _event]
  {:db (dissoc db :h :j :k :l)})

(def event-handlers
  {:init             handle-init
   :good-http-result handle-good-http-result
   :bad-http-result  handle-bad-http-result
   :clicked          handle-clicked
   :reset            handle-reset})

(defn handle-event
  [db event-handlers [event-type & _event-params :as event]]
  ((event-handlers event-type) db event))

(defn do-db!
  [page-state _effect-key new-db]
  (when-not (identical? new-db @page-state)
    (reset! page-state new-db)))

(defn do-http!
  [_page-state _effect-key {:keys [_method _url on-success on-failure]}]
  (if (zero? (rand-int 3))
    ;; Simulate HTTP failures once out of every 3 tries
    (js/setTimeout
      #(dispatch! (conj on-failure {:body :bad}))
      (+ 2000 (rand-int 1000)))
    (js/setTimeout
      #(dispatch! (conj on-success {:body :good}))
      (+ 2000 (rand-int 1000)))))

(defn do-dispatch!
  [_page-state _effect-key event-v]
  (dispatch! event-v))

(def fx-handlers
  {:db       do-db!
   :http     do-http!
   :dispatch do-dispatch!})

(defn handle-fx!
  [state fx-handlers {:keys [db] :as effects-map}]
  ;; Do the :db effect before any other effects
  (when db
    ((fx-handlers :db) state :db db))
  (doseq [[effect-key effect-value] (dissoc effects-map :db)]
    ((fx-handlers effect-key) state effect-key effect-value)))

(a/go-loop []
  (let [event (a/<! events-ch)
        fxs   (handle-event @app-state event-handlers event)]
    (handle-fx! app-state fx-handlers fxs)
  (recur)))

(defn main []
  [:<>
   [:h1 "App"]
   [:button {:on-click #(dispatch! [:clicked {:element :h}])} "H"]
   [:button {:on-click #(dispatch! [:clicked {:element :j}])} "J"]
   [:button {:on-click #(dispatch! [:clicked {:element :k}])} "K"]
   [:button {:on-click #(dispatch! [:clicked {:element :l}])} "L"]
   [:button {:on-click #(dispatch! [:reset])} "Reset"]
   [:ul
    [:li @(subscribe :h)]
    [:li @(subscribe :j)]
    [:li @(subscribe :k)]
    [:li @(subscribe :l)]]
   [:p @(subscribe :http)]])

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (rdom/render [main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(defn init! []
  ;; conditionally start your application based on the presence of an "app"
  ;; element this is particularly helpful for testing this ns without launching
  ;; the app
  (dispatch! [:init])
  (mount-app-element))

(init!)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))
```

## Conclusion

There you go! That's our event-driven framework in `core.async`. Although it is
quite possible to implement our own poor man's `re-frame`, I wouldn't recommend
doing it for serious work. In this article, we cut a lot of corners to get the
happy codepath working. Honestly, there might be some serious bugs, or it might
break your reloading workflow in some profound ways.

There is a large community behind `re-frame` developing toolings and libraries
around it. It's also quite beneficial to do things the `re-frame` way [^5]. If
you are looking for a way to model your `reagent` app using an event-driven
approach, I'd recommend `re-frame`.

I had a lot of fun studying `re-frame` source code. In doing so, I got a lot
more confident to use it as a dependency for my projects. Hopefully, this
article will give you some ideas of what `re-frame` does for you under the
cover.

## Footnotes

[^1] To be honest, I wasn't a big fan of `re-frame` before because I didn't see
the value of using a framework for managing my states while being fully capable
of managing them myself. I also didn't like having a big dependency unless I
have to. After studying quite a significant bit of `re-frame`'s source code
(which doesn't seem so big of a dependency anymore), I am now convinced that the
cost of having it as a dependency is pretty well justified.

[^2] From ["The State Machines of core.async"]

[^3] The Six Dominoes of `re-frame`'s data loop are:

1. Event dispatch
1. Event handling
1. Effect handling
1. Query
1. View
1. DOM

[^4] From the `reagent.core/reaction` doc:

> New Reaction is created everytime reaction is called, so caller needs to take
> care that new reaction isn't created e.g. every component render, by using
> with-let, form-2 or form-3 components or other solutions. Consider using
> reagent.core/track, for function that caches the derafable value, and can thus
> be safely used in e.g. render function safely.

[^5] See ["6 things Reacters do that Re-framers avoid" by
PurelyFunctional.tv][16].

**Links**

- [`mini-reframe` Github page][1]
- [re-frame][2]
- [reagent][9]
- [core.async][8]

[1]:https://github.com/dawranliou/mini-reframe
[2]:http://day8.github.io/re-frame/
[3]:https://github.com/dawranliou/mini-reframe#self-guided-source-code-tour
[4]:https://github.com/dawranliou/mini-reframe/blob/main/src/mini_reframe/event_loop.cljs
[5]:https://figwheel.org/
[6]:https://github.com/thheller/shadow-cljs
[7]:https://rigsomelight.com/figwheel-main-template/
[8]:https://github.com/clojure/core.async
[9]:https://github.com/reagent-project/reagent/
[10]:https://github.com/day8/re-frame/blob/master/src/re_frame/router.cljc#L71
[11]:https://github.com/day8/re-frame/blob/master/src/re_frame/router.cljc#L179
[12]:https://developer.mozilla.org/en-US/docs/Web/JavaScript/EventLoop
[13]:http://hueypetersen.com/posts/2013/08/02/the-state-machines-of-core-async/
[14]:https://github.com/dawranliou/mini-reframe/blob/main/src/mini_reframe/event_loop.cljs#L21-L25
[15]:http://reagent-project.github.io/docs/master/reagent.core.html#var-reaction
[16]:https://purelyfunctional.tv/article/react-vs-re-frame/
