+++
title = "Better performance with Java arrays in Clojure"
authors = "Daw-Ran Liou"
[taxonomies]
tags = ["clojure"]
+++

I was working on re-implementing the [Stochastic gradient descent] algorithm in
Clojure based on the blog post [How to Implement Linear Regression From Scratch
in Python]. Because I wasn't happy about the performance when the dataset is in
the order of thousands rows of thousands features, so I started looking into
ways to go lower-level to use the Java arrays and found this amazing article on
Clojure Goes Fast: [Java arrays and unchecked math]. (It's a great article that
goes into details of Clojure type-hinting and other tips. Highly recommended!)

Following the instructions in the Clojure Goes Fast article, I'm quite amazed by
the performance boost. Below is a comparison of the same `predict` function: one
expects Clojure vector and the other Java arrays. The only thing novel thing
that didn't cover in the Clojure Goes Fast article is the usage of
`areduce`. Instead of the explicit `loop-recur` shown in the article, I stumbled
upon this handy `clojure.core/areduce` form and used it to better convey the
implementation of the `predict` function.

The results are quite promising.

```clojure
(require '[criterium.core :as c])

(defn predict-v [xs [bias & coefs]]
  (reduce + bias (map * xs coefs)))

(defn predict-a [^doubles xs ^doubles coefs]
  (areduce xs idx ret (aget coefs 0)
           (+ ret (* (aget xs idx) (aget coefs (inc idx))))))

(let [xs      (range 5000)
      coefs   (range 5001)
      xs-v    (into [] xs)
      coefs-v (into [] coefs)
      xs-a    (into-array Double/TYPE xs)
      coefs-a (into-array Double/TYPE coefs)]
  (c/quick-bench (predict-v xs-v coefs-v))
  (c/quick-bench (predict-a xs-a coefs-a)))
```

The `*out*`:

```
Evaluation count : 834 in 6 samples of 139 calls.
             Execution time mean : 736.026481 µs
    Execution time std-deviation : 26.489882 µs
   Execution time lower quantile : 715.074957 µs ( 2.5%)
   Execution time upper quantile : 777.624246 µs (97.5%)
                   Overhead used : 6.224349 ns
Evaluation count : 106956 in 6 samples of 17826 calls.
             Execution time mean : 5.865484 µs
    Execution time std-deviation : 221.419267 ns
   Execution time lower quantile : 5.580842 µs ( 2.5%)
   Execution time upper quantile : 6.125437 µs (97.5%)
                   Overhead used : 6.224349 ns
```

It's about `736 µs` versus `5.87 µs`.  That's about a 125X speedup by using Java array.

It's really nice that I don't have to sacrifice the functional programming
constructs (like `map`[^1] and `reduce`) because I'm working on a different data
structure. Under the hood, `areduce` is a macro that does the `loop-recur` for
you. Here's the source code of [`clojure.core/areduce`]:

```clojure
(defmacro areduce
  "Reduces an expression across an array a, using an index named idx,
  and return value named ret, initialized to init, setting ret to the
  evaluation of expr at each step, returning ret."
  {:added "1.0"}
  [a idx ret init expr]
  `(let [a# ~a l# (alength a#)]
     (loop  [~idx 0 ~ret ~init]
       (if (< ~idx l#)
         (recur (unchecked-inc-int ~idx) ~expr)
         ~ret))))
```

Using this macro has an advantage over using the `loop-recur`: you don't need to
explicitly call the `unchecked-inc-int` to get the full benefit of speeding up
looping on the array `a`.

**Reference Links**

- [Java arrays and unchecked math]
- [Stochastic gradient descent]
- [How to Implement Linear Regression From Scratch in Python]
- [`clojure.core/areduce`]

[Java arrays and unchecked math]: http://clojure-goes-fast.com/blog/java-arrays-and-unchecked-math/
[Stochastic gradient descent]: https://en.wikipedia.org/wiki/Stochastic_gradient_descent
[How to Implement Linear Regression From Scratch in Python]: https://machinelearningmastery.com/implement-linear-regression-stochastic-gradient-descent-scratch-python/
[`clojure.core/areduce`]: https://github.com/clojure/clojure/blob/clojure-1.10.1/src/clj/clojure/core.clj#L5265

**Footnotes**

[^1] Besides the `clojure.core/areduce` shown in this article, there's also the `clojure.core/amap`.
