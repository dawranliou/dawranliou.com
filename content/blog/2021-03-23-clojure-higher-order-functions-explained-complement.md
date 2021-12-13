---
title: "Clojure higher-order functions explained: complement"
authors: Daw-Ran Liou
tags: [clojure]
---

_Checkout the [index] for the full series._

## Source code

Here's the [source of complement in clojure 1.10.1][1]:

```clj
(defn complement
  "Takes a fn f and returns a fn that takes the same arguments as f,
  has the same effects, if any, and returns the opposite truth value."
  {:added "1.0"
   :static true}
  [f]
  (fn
    ([] (not (f)))
    ([x] (not (f x)))
    ([x y] (not (f x y)))
    ([x y & zs] (not (apply f x y zs)))))
```

## Usages

From the doc-string:

> Takes a fn f and returns a fn that takes the same arguments as f, has the same
> effects, if any, and returns the opposite truth value.

Although there's no restriction on the number of arguments, I found `complement`
to be most useful to create predicate functions, which take a single argument
and return a value that meant to be consumed as a logical value.

A small caveat when using `complement` is that it returns a **Boolean** instead
of the data type from the original function f. This makes it a bit slightly
inconvenient to use with `keep` or `some` if you expect the return value to be
the inverse of the original function f:

```clj
;; some returns the first logical truthy value
(some #{:a :b} [:1 :2 :a :b])
;; => :a

;; true is a truthy value
(some (complement #{:a :b}) [:1 :2 :a :b])
;; => true

;; keep removes nil
(keep #{:a :b} [:1 :2 :a :b])
;; => (:a :b)

;; doesn't remove the false
(keep (complement #{:a :b}) [:1 :2 :a :b])
;; => (true true false false)

;; better to use remove instead
(remove #{:a :b} [:1 :2 :a :b])
;; => (:1 :2)
```

`filter` on the other hand is the most consistent (the least surprising) one
with `complement`:

```clj
(filter #{:a :b} [:1 :2 :a :b])
;; => (:a :b)

(filter (complement #{:a :b}) [:1 :2 :a :b])
;; => (:1 :2)
```

## Examples

There are a couple of great examples by the Clojure community on
[clojuredocs.org's complement page][2]. Please go check it out for the examples!

## My use cases

This is my growing list of use cases where I stumble upon and found `complement`
useful ;)

### Not contains?

Already mentioned above, not-contain can be written as:

```clj
(filter #(not (contains? #{:a :b} %)) [:1 :2 :a :b])
(filter #(not (#{:a :b} %)) [:1 :2 :a :b])
(filter (complement #{:a :b}) [:1 :2 :a :b])
```

### Not :key

```clj
(filter :disabled [{:v 1} {:v 2} {:v 3 :disabled true} {:v 4}])
;; => ({:v 3, :disabled true})
(filter (complement :disabled) [{:v 1} {:v 2} {:v 3 :disabled true} {:v 4}])
;; => ({:v 1} {:v 2} {:v 4})
```

## Links

- [Source of complement in clojure 1.10.1][1]
- [Clojuredocs.org's complement page][2]

[index]: /blog/clojure-higher-order-functions-explained-index

[1]: https://github.com/clojure/clojure/blob/clojure-1.10.1/src/clj/clojure/core.clj#L1433

[2]: https://clojuredocs.org/clojure.core/complement
