+++
title = "Domain Exploring with Clojure Spec"
tags = ["clojure"]
+++

I read a great article "[Domain modelling with clojure.spec](https://adambard.com/blog/domain-modeling-with-clojure-spec/)"
by [Adam Bard](https://twitter.com/adambard) and started to use more
`clojure.spec` for domain modeling. Here I want to share my process, which
I called "Domain Exploring." It really is just a combination of REPL-driven
development with domain modeling in it's essence.

<!-- more -->

## Domain Modeling vs Exploring

From [Wikipedia - Domain Model](https://en.wikipedia.org/wiki/Domain_model):

> *In software engineering, a domain model is a conceptual model of the domain that incorporates both behaviour and data.*

Before we reach the complete domain model, it's typical to start by drawing some borders
of our domain.
Note those borders are not just typical database constraints like `NOT NULL`
or types like `INTEGER`.

## Example: Blog Domain

In this example, I'll describe my exploration process to build the domain model
for blog.

First, create a ns:

```clojure
(ns dawranliou.blog
  (:require [clojure.spec.alpha :as s]))
```

The blog domain model start with just a map:

```clojure
(s/def ::blog map?)
```

And in the comment, try to generate a few samples of the `::blog`:

```clojure
(require '[clojure.spec.gen.alpha :as gen])
(gen/sample (s/gen ::blog))

;; =>
({}
 {}
 {}
 {\3 29N, 0 :-}
 #:M*6{:c+ true}
 {}
 {J 1/3,
  2 -41204876568N,
  f -1.0,
  "'" #uuid "0c7e7887-627f-4f6f-b6d3-05a8197c4d5a",
  2.5 -1.296875,
  "j&3?M" Ez}
  ;; ...
  )
```

It's obvious that this isn't the data we're expecting. What we need
is called an [Entity Map](https://clojure.org/guides/spec#_entity_maps).
Update the `::blog` spec to:

```clojure
(s/def ::blog (s/keys))
(gen/sample (s/gen ::blog))
;; => ({} {} {} {} {} {} {} {} {} {})
```

We got empty map because we didn't define the entities in the map.
Let's start by adding the `title` string:

```clojure
(s/def ::title string?)
(s/def ::blog (s/keys :req [::title]))

;; =>
(#:dawranliou.blog{:title ""}
 #:dawranliou.blog{:title ""}
 #:dawranliou.blog{:title "E5"}
 ;; ...
)
 ```

A good thing is that the keywords in the maps are namespace qualified.
When this blog map is merged with another map, we can be pretty confident
that the data won't be overwritten.

```clojure
(s/def ::body string?)
(s/def ::author string?)
(s/def ::date-publish int?)
(s/def ::blog (s/keys :req [::title ::body ::author ::date-published]))

(gen/sample (s/gen ::blog))
;; =>
(#:dawranliou.blog{:title "", :body "", :author "", :date-publish -1}
 #:dawranliou.blog{:title "", :body "O", :author "", :date-publish -1}
 #:dawranliou.blog{:title "6", :body "", :author "2g", :date-publish 1}
 ;; ...
)
```

One thing to notice is that the timestamps are sometimes negative.
We can use `pos-int?` here instead of `int?`.

```clojure
(s/def ::date-publish pos-int?)

(gen/sample (s/gen ::blog))
;; =>
(#:dawranliou.blog{:title "", :body "", :author "", :date-publish 1}
 #:dawranliou.blog{:title "Q", :body "", :author "", :date-publish 1}
 #:dawranliou.blog{:title "S", :body "", :author "f4", :date-publish 1}
 ;; ...
)
```

To define a behavior under the blog domain, simply write a function
under the same namespace:

```clojure
(defn blog [title body author]
  {::title title ::body body ::author author ::date-publish nil})

(s/fdef blog
  :args (s/cat :title string? :body string? :author string?)
  :ret ::blog)

(blog "title" "body" "author")
;; =>
#:dawranliou.blog{:title "title",
                  :body "body",
                  :author "author",
                  :date-publish nil}
```

The `s/fdef` does not automatically check the function arguments.
To do it, we need to instrument the test to do so. However, instrumentation
isn't suitable for production due to performance.
It should only be used in development and testing.

```clojure
(require '[clojure.spec.test.alpha :as stest])
(stest/instrument)

(blog "title" "body" 123)
;; Execution error - invalid arguments to dawranliou.blog/blog at (REPL:24).
;; 123 - failed: string? at: [:author]
```

At last, we can exercise the function to check if there are any mistake:

```clojure
(s/exercise-fn `blog)
;; =>
([("" "" "")
  #:dawranliou.blog{:title "", :body "", :author "", :date-publish nil}]
 [("" "0" "")
  #:dawranliou.blog{:title "", :body "0", :author "", :date-publish nil}]
 [("M" "TO" "f8")
  #:dawranliou.blog{:title "M", :body "TO", :author "f8", :date-publish nil}]
 ;; ...
)
```

The return value isn't validated so we need to do it ourselves:

```clojure
(for [[args ret] (s/exercise-fn `blog)]
  (s/valid? ::blog ret))
;; => (false false ...)

;; Check the console for what's wrong
(for [[args ret] (s/exercise-fn `blog)]
  (s/explain ::blog ret))
```

Turned out the `::date-publish` key cannot be `nil`. Thus,

```clojure
(s/def ::date-publish (s/or :published pos-int?
                            :not-published nil?))

(for [[args ret] (s/exercise-fn `blog)]
  (s/valid? ::blog ret))
;; => (true true ...)
```

## Summary

The Domain Exploring process is:

1. Define/refine a spec
1. Generate some data
1. Test data against the spec
1. Repeat

## Appendix

Complete example

```clojure
(ns dawranliou.blog
  (:require [clojure.spec.alpha :as s]))

(s/def ::title string?)
(s/def ::body string?)
(s/def ::author string?)
(s/def ::date-publish (s/or :published pos-int?
                            :not-published nil?))
(s/def ::blog (s/keys :req [::title ::body ::author ::date-publish]))

(s/fdef blog
  :args (s/cat :title string? :body string? :author string?)
  :ret ::blog)

(defn blog [title body author]
  {::title title ::body body ::author author ::date-publish nil})

(comment
  (require '[clojure.spec.gen.alpha :as gen])

  (gen/sample (s/gen ::blog))

  (require '[clojure.spec.test.alpha :as stest])
  (stest/instrument)

  (for [[args ret] (s/exercise-fn `blog)]
    (s/valid? ::blog ret)))
```
