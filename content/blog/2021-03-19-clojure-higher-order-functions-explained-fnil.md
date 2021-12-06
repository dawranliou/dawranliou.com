---
title: "Clojure higher-order functions explained: fnil"
authors: Daw-Ran Liou
tags: [clojure]
---

_Checkout the [index] for the full series._

## Source code

It's better to read the source code first because source code don't lie. Here's
the [source of fnil in clojure 1.10.1]:

```clj
(defn fnil
  "Takes a function f, and returns a function that calls f, replacing
  a nil first argument to f with the supplied value x. Higher arity
  versions can replace arguments in the second and third
  positions (y, z). Note that the function f can take any number of
  arguments, not just the one(s) being nil-patched."
  {:added "1.2"
   :static true}
  ([f x]
   (fn
     ([a] (f (if (nil? a) x a)))
     ([a b] (f (if (nil? a) x a) b))
     ([a b c] (f (if (nil? a) x a) b c))
     ([a b c & ds] (apply f (if (nil? a) x a) b c ds))))
  ([f x y]
   (fn
     ([a b] (f (if (nil? a) x a) (if (nil? b) y b)))
     ([a b c] (f (if (nil? a) x a) (if (nil? b) y b) c))
     ([a b c & ds] (apply f (if (nil? a) x a) (if (nil? b) y b) c ds))))
  ([f x y z]
   (fn
     ([a b] (f (if (nil? a) x a) (if (nil? b) y b)))
     ([a b c] (f (if (nil? a) x a) (if (nil? b) y b) (if (nil? c) z c)))
     ([a b c & ds] (apply f (if (nil? a) x a) (if (nil? b) y b) (if (nil? c) z c) ds)))))
```

## Usages

Simply put, **fnil patches functions to handle nil arguments**. Usually this is
because you are using functions that you do not maintain and want them to handle
`nil`s in your particular use cases rather than just barfing out NPEs.

Personally, `fnil` has become increasingly handy ever since I come to be more
liberal on passing `nil`s as real values in the front-end presentation
logic. (See my other [article about nil busting here].)

## Examples

There are a couple of great examples by the Clojure community on
[clojuredocs.org's fnil page]. Please go check it out for the examples!

## My use cases

This is my growing list of use cases where I stumble upon and found `fnil`
useful ;)

### Python defaultdict in Clojure

Being a more Object-oriented language than functional, the way Python deal with
inserting default value in a hash-table (or `dict`) through the object,
[defaultdict]. Example:

```python
>>> import collections
>>> d = collections.defaultdict(list)
>>> d['yellow'].append(1)
>>> d
defaultdict(<class 'list'>, {'yellow': [1]})
```

The more functional approach to the problem can be done using `fnil` in
Clojure. Instead of defining the new behavior of an object, just create a
function that knows what to do with `nil`:

```clj
(def conj* (fnil conj []))
(update {} :yellow conj* 1)
;; => {:yellow [1]}

;; Or, inline the function
(update {} :yellow (fnil conj []) 1)
;; => {:yellow [1]}
```

_Credits to [clojuredocs's user Dimagog's example]. It was my initial
inspiration._

### Identity fallback

Again from my previous [article about nil busting here]:

```clj
(defn self-or [other] (fnil identity other))

(->> [nil nil nil nil nil nil nil nil nil nil nil nil nil "Batman!"]
     (map (self-or "na"))
     (clojure.string/join ", "))
```

### Retrofitting clojure string functions

Most of the `clojure.string` functions don't like `nil`s and will throw NPE at
you. `fnil` to the rescue!

```clj
(require '[clojure.string :as str])
(sort-by (fnil str/lower-case "") ["hi" nil "ho"])
;; => (nil "hi" "ho")
```


[index]: @/blog/2021-03-12-clojure-higher-order-functions-explained-index.md

[source of fnil in clojure 1.10.1]: https://github.com/clojure/clojure/blob/clojure-1.10.1/src/clj/clojure/core.clj#L6556

[examples on clojuredocs.org]: https://clojuredocs.org/clojure.core/fnil

[clojuredocs.org's fnil page]: https://clojuredocs.org/clojure.core/fnil

[article about nil busting here]: 2021-03-10-clojure-nil-busting.md

[clojuredocs's user Dimagog's example]: https://clojuredocs.org/clojure.core/fnil#example-54d0443ee4b0e2ac61831cf7

[defaultdict]: https://docs.python.org/3/library/collections.html#defaultdict-objects
