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

## Strategy 1 - Air Supplying Clojure Uberjar

1. Create a new Clojure project
1. Generate classes/interfaces by instrumenting Clojure
1. Create a uberjar
1. Drop in to existing Java project's classpath
1. Use the generated classes/interfaces from Java project

I call this one "air supplying" because, interestingly, our legacy Java project doesn't
use any modern build system like Maven or Gradle. (We use Ant instead.) So I simply copy
the uberjar to the project's classpath to make my compiled code available. Here's how I
do it with some code:

Start the project with `lein new app airsupply`. The project.clj would look like this:

```clojure
(defproject airsupply "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot airsupply.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
```

## Strategy 2 - 

