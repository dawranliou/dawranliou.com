---
title: Inspect Java Objects with Clojure
date: 2019-02-09
category: Clojure
tags: clojure, java, tip
authors: Daw-Ran Liou
---

A recipe to create a graphical inspector UI to explore Java objects.
The key is to use the `clojure.inspector` for the visualization and
`clojure.org/java.data` for recursively converting Java beans to Clojure
data structure.

READMORE

On the official website,
[`clojure.inspector`](https://clojure.github.io/clojure/clojure.inspector-api.html) is:

> Graphical object inspector for Clojure data structures.

It provides three APIs: `inspect`, `inspect-table`, and `inspect-tree`.
To differentiate them, the first thing to notice is that `inspect` and
`inspect-tree` doesn't have any assumption on the shape of the input data,
while `inspect-table` assumes the data is sequential and its elements
all have the same length. Secondly, `inspect-tree` creates a expendable
tree for us to explore the data if the data is hierarchical. Here are some
examples below. We'll be focusing more on `inspect-tree` in the rest of
the article.

```clojure
;; try this in your repl

(require '[clojure.inspector :as insp])
(def d [{:a "a1" :b "b1"} {:a "a2" :b "b2"} {:a "a3" :b "b3"}])
(insp/inspect d)
(insp/inspect-table d)
(insp/inspect-tree d)
```

The built-in `clojure.inspctor` namespace is very useful for
exploring data and the interactive development for Clojure.
Instead of printing everything down in the repl, a graphical
tool like this is a much superior experience, in my opinion.

Next question is, when we do interop with Java, Java objects
doesn't automatically become Clojure data types like
`clojure.lang.IPersistentMap`. The built-in function
[`bean`](https://clojuredocs.org/clojure.core/bean) is helpful.
However, the time that I really need to inspect a Java object
is usually when the object has too many layers nested. In
this case, I found the `clojure.org/java.data` library is the
right tool.

[`clojure.org/java.data`](https://github.com/clojure/java.data)
is:

> Functions for recursively converting Java beans to Clojure and vice versa. Future home of Java beans and properties support from the old clojure-contrib

To demostrate, let's setup a Java & Clojure polyglot project
with leiningen like this:

```clojure
(defproject inspector "0.1.0-SNAPSHOT"
  ;; ...
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/java.data "0.1.1"]]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"])
```

Here's the Java class to inspect:

```java
package inspector;

public class MyClass implements java.io.Serializable {
    private String name;
    private InnerClassA a;

    public MyClass() {
        this.name = "MyClass";
        this.a = new InnerClassA();
    }

    public String getName() {
        return this.name;
    }
    public InnerClassA getA() {
        return this.a;
    }

    public class InnerClassA {
        private String name;
        public InnerClassA() {
            this.name = "InnerClassA";
        }
        public String getName() {
            return this.name;
        }
    }
}
```

Now you can connect to the REPL and do:

```clojure
(-> (inspector.MyClass.)
    java/from-java)
;; {:a {:name "InnerClassA"}, :name "MyClass"}

(-> (inspector.MyClass.)
    java/from-java
    insp/inspect-tree)
```
