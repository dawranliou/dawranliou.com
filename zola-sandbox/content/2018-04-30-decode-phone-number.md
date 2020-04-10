+++
title = "Decode your phone number with Clojure"
tags = ["clojure"]
slug = "decode-phone-number"
+++

_The original article was shared on my [Medium](https://medium.com/@dawran6/decode-your-phone-number-with-clojure-373ccbd11bb9)_

__The inspiration of this article came from the Cousera course, Functional Programming in Scala. I basically translate the Scala program in section 6 into the Clojure version you’re about to see. Kudos to Martin Odersky.__

You might have seen ads with interesting phone numbers like: 1–800-FLOWER, 1–800-FREE-411, or 1–800-GOT-JUNK. Those are called phonewords, or mnemonic phone numbers. It’d be awesome to have a memorable phone number like 1–RANDY-DA-MAN. However, maybe your phone number already has a meaning. How cool is it to figure it out, well, using Clojure?

<!-- more -->

Let’s clarify the problem. What we want is to: find all the combination of words where the concatenation of words could be encoded to the given numbers. For example, given 5299626, we want to find “jazz man,” “lazy man,” or all other words.

This problem is quite daunting at first glance. Mostly because of the recursive nature of it. For example, given the numbers 23456789, we want to know by splitting the first digit and the rest, we not only want to ask if 2 and 3456789 are both words, but also want to ask the ??

We could break down the problem in the following steps:

1. Get a list of all English words.
1. Get a map from English characters to numbers.
1. Define a function that encode a English word to numbers.
1. Reverse the map (from #2) by grouping all English words with the same word numbers.
1. Define a (recursive) function to convert numbers to a set of possible word list.

Let’s start from the beginning. To get a list of English words, thanks to Cousera course, Functional Programming in Scala, we’ll get it from the Internet:

```clojure
(def words
  (clojure.string/split-lines
    (slurp "https://lamp.epfl.ch/files/content/sites/lamp/files/teaching/progfun/linuxwords.txt")))
```

We then defined the map from character to number. We get this by reversing the map mnem:

```clojure
(def mnem
  {\2 "ABC"
   \3 "DEF"
   \4 "GHI"
   \5 "JKL"
   \6 "MNO"
   \7 "PQRS"
   \8 "TUV"
   \9 "WXYZ"})

(def char-code
  (into {} (for [[k vs] mnem
                 v vs]
             [v k])))
```

Let’s write a function to encode a English word to numbers. This can be done by mapping all the letters to the corresponding number:

```clojure
(defn word-code
  [word]
  (apply str (map char-code (.toUpperCase word))))
```

The next step is to group all English words by their code. This is a one-liner to use the high level function group-by in Clojure:

```clojure
(def words-for-num
  (group-by word-code words))
```

Now this is the hardest part. We want to define a function to decode the number. The recursive part is in bold. This function looks like:

```clojure
(defn decode
  [number]
  (if (empty? number)                                      ;; #1
    #{()}
    (into #{} (for [split (range 1 (inc (.length number))) ;; #2
                    word (get words-for-num                ;; #3
                              (subs number 0 split)) 
                    rest (decode (subs number split))]     ;; #4
                (cons word rest)))))                       ;; #5
```

We use list comprehension to bind the symbols from multiple collections. The split symbol binds to the index number of which we want to split the number. (#2) If first part of the split can be found as a word (#3), we will combine (#5) the the word with the recursive solution (#4) from the rest of the split. The base case of the recursive function is a set of empty list. (#1)

All together, the entire program is 30 lines of code:

<script src="https://gist.github.com/dawran6/b08e6d3059253471050e98c80875ae8b.js"></script>

Thoughts

Discovering Clojure has been the best thing to me as a programmer since Python. It was very challenging initially and it totally changed the way I view programming languages. I’m still pretty new to this language, but I love it because it is beautiful and powerful. Let me know what you think of Clojure!

Also, please share and clap this post if you think this is interesting/helpful to you!
