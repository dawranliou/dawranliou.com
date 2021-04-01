+++
title = "Inspect Java Objects with Clojure"
[taxonomies]
tags = ["clojure", "java"]
+++

The more I work with Clojure, the more frustration I found to work with
Java objects. Mostly, it feels an unnecessary process to go through
the class definition to find the getter methods to access the data I want,
especially when the data is buried under multiple layers of classes deep.

In this article, I'll explain a recipe to create a graphical
inspector UI to explore Java objects, frustration free!
The key is to use the `clojure.inspector` for the visualization and
`clojure.org/java.data` for recursively converting Java beans to Clojure
data structure.

<!-- more -->

_Note: Thanks to reddit user @vvvvalvalval to point out
[Programming at the REPL: Data Visualization](https://clojure.org/guides/repl/data_visualization_at_the_repl#_dealing_with_mysterious_values_advanced)
is also a great place to learn more about inspecting values in the REPL.
I was inspired by it to write this article. If you are just starting to learn Clojure
REPL, definitely check out the link first!_

## Tools - `clojure.inspector` and `clojure.java.data`

On the official website,
[`clojure.inspector`](https://clojure.github.io/clojure/clojure.inspector-api.html) is:

> Graphical object inspector for Clojure data structures.

It provides three APIs: `inspect`, `inspect-table`, and `inspect-tree`.
To differentiate them, the first thing to notice is that `inspect` and
`inspect-tree` doesn't have any assumption on the shape of the input data,
while `inspect-table` assumes the data is sequential and its elements
all have the same length. Secondly, `inspect-tree` creates a expendable
tree to explore data if the data is hierarchical. Here are some
examples below. We'll be focusing more on `inspect-tree` in the rest of
the article.

```clj
;; try this in your repl

(require '[clojure.inspector :as insp])
(def d [{:a "a1" :b "b1"} {:a "a2" :b "b2"} {:a "a3" :b "b3"}])
(insp/inspect d)
(insp/inspect-table d)
(insp/inspect-tree d)
```

The built-in `clojure.inspctor` namespace is very useful for
exploring data and the interactive development for Clojure.
Instead of printing everything down in the repl, using a graphical
tool like this is a much superior experience, in my opinion.

Next question is, when we do interop with Java, Java objects
doesn't automatically become Clojure data types like
`clojure.lang.IPersistentMap`. The built-in function
[`bean`](https://clojuredocs.org/clojure.core/bean) is helpful.
However, the time that I really need to inspect a Java object
is usually when the object has too many layers of classes nested. In
this case, I found the `clojure.org/java.data` library is the
right tool.

[`clojure.org/java.data`](https://github.com/clojure/java.data)
is:

> Functions for recursively converting Java beans to Clojure and vice versa. Future home of Java beans and properties support from the old clojure-contrib

## Demo

To demonstrate, let's setup a Java & Clojure polyglot project
with Leiningen like this:

```clj
(defproject inspector "0.1.0-SNAPSHOT"
  ;; ...
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/java.data "0.1.1"]]
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"])
```

In order to make this work, we need to implement our Java class
as a [JavaBean](https://www.geeksforgeeks.org/javabean-class-java/).
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

    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}
    public InnerClassA getA() {return this.a;}
    public void setA(InnerClassA a) {this.a = a;}

    public static class InnerClassA implements java.io.Serializable {
        private String name;
        public InnerClassA() {this.name = "InnerClassA";}
        public String getName() {return this.name;}
        public void setName(String name) {this.name = name;}
    }
}
```

Now you can connect to the REPL and do:

```clj
;; Transform java object to clojure map
(-> (inspector.MyClass.)
    java/from-java)
;; {:a {:name "InnerClassA"}, :name "MyClass"}

;; The inverse works too if the JavaBean is implemented correctly
(->> {:name "hi" :a {:name "hey"}}
     (java/to-java inspector.MyClass))

;; Now you can use inpsect-tree to visualize the map
(-> (inspector.MyClass.)
    java/from-java
    insp/inspect-tree)
```

Here's the inspector GUI you'll see: ![alt clojure inspector](images/clojure-inspector.png)

Though, there're some limitations, for one:

```java
public class MyClass implements java.io.Serializable {
    // ...
    public MyClass getSelf() {return this;}
    // ...
}
```

In this case, you'll find an StackOverflowError when calling
`java/from-java` function. This is because all the `get*` methods
are consider getter methods to access class member and were invoked
recursively. In the example above, you just need to rename `getSelf`
to `returnSelf` or any name without the "get" prefix to avoid the error.

## Conclusion

Clojure is really great for:

1. Transforming data (e.g. `clojure.java.data`), and
1. Exploring data (e.g. `clojure.inspector`)

Using the simple recipe explained in the article, I found it
more and more pleasant to do interop with Java in the Clojure
world.

If you find this article helpful, please help me to share it!
