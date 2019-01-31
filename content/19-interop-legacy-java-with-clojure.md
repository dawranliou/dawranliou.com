Title: Interop legacy Java project with Clojure
Date: 2019-01-30
Category: Clojure
Tags: clojure, java
Slug: interop-legacy-java-with-clojure
Authors: Daw-Ran Liou
Summary: Three strategies to inject Clojure new blood in your existing Java project

Recently I got the chance to work on a new feature for our existing Java project.
The feature itself is a project that set out to be a general-purpose asynchronous 
data logging library that can be used company-wide, shared with other projects.
I was very into Clojure these days. Learning Clojure has this side effect of
mutating your brain to view programming difference. At the meanwhile changing your
taste of choosing a programming language. (Please be Clojure!)

I want to do this project in Clojure because, first of all, it's Clojure. And I can't
get more excited about this idea along. Then I did a couple of evaluation:

1. Clojure is a JVM language. It's designed to be "symbiotic with an established Platform"
(see [Rationale](https://clojure.org/about/rationale)"
so I know it's doable to integrate two languages. I just need to figure out how.
This is primarily what this blog post is about.
1. Clojure has great concurrency supports. This is also a designed feature to the language.
(Also see the rationale link above.) I need to develop APIs that are asynchronous to
achieve high performance. And I've been thinking of digging into `core.async` for some time.
1. Our legacy code base really need some new blood and some strategies to split up the
project. I think introducing a new language would be a way to draw a line from our current
project and potentially for other future features.

And here we go.

## Strategy 1 - Air-Supplying Clojure Uberjar

Recipe:
1. Create a new Clojure project
1. Generate classes/interfaces by instrumenting Clojure
1. Create a uberjar
1. Drop in to existing Java project's classpath
1. Use the generated classes/interfaces from Java project

I call this one "air supplying" because, interestingly, our legacy Java project doesn't
use any modern build system like Maven or Gradle. (We use Ant instead.) So I simply copy
the uberjar to the project's classpath to make my compiled code available.

Here, the goal is to generate the class files that a Java project can use. We start the
project with `lein new app airsupply`. The project.clj would look like this:

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot airsupply.core   ;; (1)
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
```

_(1) We don't really use the `airsupply.core` ns and we don't need a main since this isn't
going to be an app but a library. We can just remove the `:main` line.__

Naively, we'll create a namespace called airsupply.java-api and use the `:gen-class` directive
in the `ns` declaration to instrument Clojure to compile it. I recommend reading [@kotarak](https://twitter.com/kotarak)'s
[gen-class – how it works and how to use it](https://kotka.de/blog/2010/02/gen-class_how_it_works_and_how_to_use_it.html)
to know more about `gen-class`. If you are still following, we ended up with:

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :aot [airsupply.java-api]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
  
;; src/airsupply/java_api.clj
(ns airsupply.java-api
  (:gen-class
   :name airsupply.Airsupply
   :state state
   :init init
   :constructors {[] []
                  [String] []}
   :main false
   :methods [[drop [] String]]))

(defn -init
  ([] (-init "supply"))
  ([name] [[] {:name name}]))

(defn -drop
  [this]
  (def -this this)
  (-> this .state :name))
```

Now you can launch the repl and do:

```clojure
user> (def a (airsupply.Airsupply.))
#'user/a
user> (.drop a)
"supply"
user> (def a (airsupply.Airsupply. "food"))
#'user/a
user> (.drop a)
"food"
```

Packaging everything up is one command away: `lein uberjar`.
You should find the `target/uberjar/airsupply-0.1.0-SNAPSHOT` contain
everything including Clojure itself. Simply adding this jar to your
Java project and you can import `airsupply.Airsupply` normally.

However, here's the catch: it's quite difficult to use `:gen-class`
when the custom methods get more creative, e.g. returning an instance
of the class itself like `clone` does. The compiler wouldn't be able
to understand `airsupply.Airsupply` before compiling it.

Although the [StackOverflow has an answer](https://stackoverflow.com/a/29375133/5050657)
on this, my opinion is to define an common interface first
(either using `gen-interface` or just write a Java interface.)
Then Airsupply can simply implement the interface instead of
defining the custom methods all by itself.

My recommendation is to turn this into a polyglot project -
Java interface and Clojure implementation. Leiningen has this use
case support pretty well. I took the [official recommendation](https://github.com/technomancy/leiningen/blob/master/doc/MIXED_PROJECTS.md#source-layout)
and here's the result. (Note that the layout has changed.):

```java
// src/java/airsupply/Supply.java
package airsupply;

public interface Supply {
    public String drop();
    public Supply spawn();
}

```

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :aot [airsupply.java-api]
  :source-paths ["src/clojure"]  ;; (2)
  :java-source-paths ["src/java"]  ;; (3)
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

;; src/clojure/airsupply/java_api.clj
(ns airsupply.java-api
  (:gen-class
   :name airsupply.Airsupply
   :implements [airsupply.Supply]
   :state state
   :init init
   :constructors {[] []
                  [String] []}
   :main false))

(defn -init
  ([] (-init "supply"))
  ([name] [[] {:name name}]))

(defn -drop
  [this]
  (-> this .state :name))

(defn -spawn
  [this]
  (airsupply.Airsupply. (-> this .state :name)))
```

After restarting the repl, Leiningen would know to compile the Java
source first and then the Clojure ns that's in the :aot directive.
This default behavior is exactly what we need in the use case. There
are other ways to interleaving compilation steps but we're good so far.
Here's what you can try in the repl after restarting.

```clojure
user> (def food (airsupply.Airsupply. "food"))
#'user/food
user> (.drop food)
"food"
user> (def food-2 (.spawn food))
#'user/food-2
user> (.drop food-2)
"food"
```

If you don't see the class instance working as expected, try a
`lein clean` to clean up the class files. I don't exactly know why
this has to be done but it helps.

Once you do a `lein uberjar` and drop this into the classpath of your
legacy Java project, you can simply import the class/interface and
create the object like you normally do, for example:

```java
// import airsupply.Supply;
// import airsupply.Airsupply;

Supply foodSupply = new Airsupply("food");
System.out.println(foodSupply.drop());
Supply anotherFoodSupply = foodSupply.spawn();

```

## Strategy 2 - Air-Supply Your Legacy Java App

Recipe
1. Compile the legacy Java App (preferrably to a Jar)
1. Add the Jar to project.clj's resource
1. Use the Jar in Clojure

## Strategy 3 - 

