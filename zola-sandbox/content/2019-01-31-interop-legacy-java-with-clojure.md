+++
title = "Interop legacy Java project with Clojure"
tags = ["clojure", "java"]
slug = "interop-legacy-java-with-clojure"
description = "Three strategies to inject Clojure new blood in your existing Java project"
+++

Recently I got the chance to work on a new feature for our existing Java project.
The feature itself is a project that set out to be a general-purpose asynchronous 
data logging library that can be used company-wide, shared with other projects.
I was very into Clojure these days. Learning Clojure has this side effect of
mutating your brain to view programming difference. At the meanwhile changing your
taste of choosing a programming language. (Please be Clojure!)

READMORE

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

## Strategy 1 - Drop-in Clojure Uberjar

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
  :url "FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot airsupply.core   ;; (1)
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
```

_(1) We don't really use the `airsupply.core` ns and we don't need a main since this isn't
going to be an app but a library. We can just remove the `:main` line._

Naively, we'll create a namespace called airsupply.java-api and use the `:gen-class` directive
in the `ns` declaration to instrument Clojure to compile it. I recommend reading [@kotarak](https://twitter.com/kotarak)'s
[gen-class – how it works and how to use it](https://kotka.de/blog/2010/02/gen-class_how_it_works_and_how_to_use_it.html)
to know more about `gen-class`. If you are still following, we ended up with:

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "FIXME"
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
  :url "FIXME"
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
_(2) (3) are added for our polyglot project._

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

## Strategy 2 - Use Legacy Java Jar as resource

Recipe
1. Compile the legacy Java App (preferrably to a Jar)
1. Add the Jar to project.clj's resource
1. Use the Jar in Clojure
1. Create a uberjar if we need to use the Clojure library back in the java app

In this strategy, we want to pull in some dependencies from the Legacy
Java app. I found it the easiest just drop in the entire legacy project
as a Jar into the Clojure project like:

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :aot [airsupply.java-api]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     {:aot            [airsupply.java-api]  ;; (4)
                       :resource-paths ["lib/legacy-999.0.0.jar"]}}) ;; (5)
```

_(4) Use the :dev profile for isolation. Also note that the :aot is moved here._

_(5) Put the legacy java project jar under the lib directory._ 

This way the you can develop the Clojure project that depends on some of the
existing Java project.

## Strategy 3 - Connect Clojure repl with Java JVM

Recipe:

1. Drop-in the uberjar containing Clojure, nrepl, and all other dependencies
1. Start the nrepl server in the Java app
1. Connect the nrepl
1. Access everything in the legacy Java's JVM

This one is really amazing. REPL has been the most important part for me
to do development in Clojure. With REPL-driven development, you can get immediate
feed back about the code change and, best, not loosing the application state
in the process.

_I took the inspiration from Gert-Jan van de Streek's [How to inspect a legacy Java application with the Clojure REPL](https://www.avisi.nl/blog/2015/05/18/how-to-inspect-a-legacy-java-application-with-the-clojure-repl).
I also use the code example in the post._

We need to make some change to the `project.clj` to include the [`nrepl`](https://github.com/clojure/tools.nrepl)
first:

```clojure
;; project.clj
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.nrepl "0.2.13"]]]  ;; (6)
  :aot [airsupply.java-api]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev     {:aot            [airsupply.java-api]
                       :resource-paths ["lib/legacy-999.0.0.jar"]}})
```

_(6) Add `org.clojure/tools.nrepl`_

Then we need to sprinkle some Java in the legacy project to make magic happen:

```java
// legacy java project's main class
package legacy.code;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class App {

    public static void main(String[] args) {
      IFn require = Clojure.var("clojure.core", "require");
      require.invoke(Clojure.read("clojure.tools.nrepl.server"));
      
      IFn server = Clojure.var("clojure.tools.nrepl.server", "start-server");
      server.invoke(Clojure.read(":port"), Clojure.read("8888"));  // (7)
      
      // the rest of the main
    }
}
```

_(7) I like to set a determinstic nrepl port but you can also skip this like Gert-Jan van de Streek's article_

With the code snippet, we effectively launched a Clojure nrepl inside the
legacy app's JVM, at runtime. Start the application. Now you can connect
to the nrepl from the Clojure project to access the application runtime
by `lein repl :connect 8888` (or from any IDE integration tool.)

## Conclusion

I really like the way how Clojure is "symbiotic" with JVM. Injecting some
new blood into a legacy project usually isn't easy, but I'm glad to take the
initiative to set on this journey. I must say I actually felt quite confident
when I worked on the technical issues along the way, even though I was still
learning Clojure. It's amazing to see things just work like I want it to in
the end.

This post hows you 3 strategies that I came across to help you integrate
Clojure into your Java project. Hope it helps you to overcome some technical
issues faster then I did! The strategies are:

1. Drop-in Clojure Uberjar
1. Use Legacy Java Jar as resource
1. Connect Clojure repl with Java JVM

## Action

If you have a Java codebase why not give this article a try and start integrating
Clojure?

[Share this article if you like it!](https://twitter.com/home?status=%22Interop%20legacy%20Java%20project%20with%20Clojure%22%20by%20%40dawranliou.%20https://dawranliou.com/interop-legacy-java-with-clojure.html)
