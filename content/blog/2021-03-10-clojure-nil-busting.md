+++
title = "Clojure nil busting one-liners"
authors = "Daw-Ran Liou"
+++

## TL;DR

To remove all `nil`s from a seq:

```
(remove nil? xs)
```

To transform a seq and remove `nil`s from its result:

```
(keep a-transform xs)
```

To replace all `nil`s with a fallback value from a seq:

```
(map (fnil identity a-fallback) xs)
```

## Preface

`nil` is a value in Clojure that represents "the absence of a value". Working
with `nil`s is quite common in Clojure. Most of the core library functions deal
with `nil`s elegantly, and very consistent. Overtime, it's quite easy to develop
the intuitions that tell you how `nil`s will flow through the
functions. However, in some cases, which I'll explain in the later sections
below, I do need to take some actions to deal with `nil`s. Fortunately, we are
in a great position with all the tools in the `clojure.core` library.

## Most of the time, just do nothing

In most cases, we simply don't need to do anything since so many core functions
work with `nil`s already:

```
(:k nil) ;; => nil
(:k {}) ;; => nil
({:k :v} nil) ;; => nil
(#{:a :b} nil) ;; => nil
('symbol nil) ;; => nil
(conj nil :a) ;; => (:a)
(assoc nil :k :v) ;; => {:k :v}
(str nil) ;; => ""
(-> nil :k1 :k2 :k3) ;; => nil
(map :k nil) ;; => ()
(filter pos? nil) ;; => ()
```

Because of this behavior, functions using the seq abstraction (`map`, `filter`,
`reduce`, etc.) works out of the box even when the seq contains `nil`:

```
(map #{:a :b} [:a nil :b nil :c])
;; => (:a nil :b nil nil)

(filter keyword? [:a nil :b nil :c])
;; => (:a :b :c)
```

However, when functions do give up on `nil`s, we need other strategies.

## Early termination

When piping a value through a series of transformations, if one transformation
turns the value into `nil`, the rest of the transformations might fail. For
example:

```
(-> x
    transform-1  ; yields nil
    transform-2) ; throws NPE
```

In this case we can simply swap the threading macros `->` and `->>` with their
early-termination variants: `some->` and `some->>`.

```
(some-> x
        transform-1  ; return nil
        transform-2) ; execution never reach this step
```

However, this only works if the step yields `nil`. What if we get something like
this:

```
(->> xs
     (map transform-1)  ; yield a list containing some nils
     (map transform-2)) ; throws NPE on (transform-2 nil)

(some->> xs
         (map transform-1)  ; yield a list containing some nils
         (map transform-2)) ; throws NPE on (transform-2 nil)
```

In this case, we'll need to bust those `nil`s from the seq.

## Remove `nil`s

When there's no need to preserve those `nil`s, simply `remove` them from the seq.

```
(remove nil? [:a nil :b nil :c])
;; => (:a :b :c)
```

Furthermore, `map` + `remove` is equivalent to `keep` when we want to remove the
`nil`s from a series of transformations:

```
(->> xs
     (map transform-1)
     (remove nil?)
     (map transform-2)
     (remove nil?))

(->> xs
     (keep transform-1)
     (keep transform-2))
```

## Replace `nil`s

In some case, we do want to preserve the `nil`s (perhaps because we need the
correct count of the seq, or the `nil`s are something meaningful later down the
pipe.)

My first thought was using the `or` form to replace each `nil` with a fallback
value. However, creating a partial function doesn't work because:

1. `partial` cannot take macros like `or` as arguments, and
2. the order of the arguments doesn't work out for us, i.e. `(partial or
   a-fallback)` is not what we want.

To make `or` work, we need to create a function: `#(or % a-fallback)`. However,
creating an anonymous function seems an overkill to me. I prefer function
compositions whenever possible, especially for a conceptually simple functions
like this. Here's solution I came up with using `fnil`:

```
(->> xs
     (map transform-1)
     ;; replaces nils with a-fallback
     (map (fnil identity a-fallback))
     (map transform-2))


;; This might help the readability

(defn self-or [other]
  (fnil identity other))

(->> xs
     (map transform-1)
     (map (self-or a-fallback))
     (map transform-2))
```

## Epilogue

I never did find any real use case for `fnil` until this [tweet][tweet 1]. This
whole article is actually just an excuse for sharing the usage of `fnil` shown
in the last code snippet, which I'm pretty excited about! I did find [another
cool usage][tweet 2] of `fnil` soon after. I'll write more about `fnil` and
other cool thing you can do with higher-order function in the future. Thanks for
reading!

[tweet 1]: https://twitter.com/dawranliou/status/1369266420572319744
[tweet 2]: https://twitter.com/dawranliou/status/1369273509768097794
