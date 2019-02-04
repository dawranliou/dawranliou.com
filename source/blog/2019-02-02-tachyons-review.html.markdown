---
title: Tachyons CSS Framework / Library Review
tags: css
---

I've tried to learn many CSS frameworks (Bootstrap, Foundation, and Bulmar)
over the years. They never really clicked and styling felt just as painful
as ever. But this has changed since I learned about [Tachyons](https://tachyons.io/).
First introduced by [Martin Klepsch](https://twitter.com/martinklepsch),
I learned and started to use Tachyons since late 2018 for the [Cljdoc.org](https://cljdoc.org/)
project [1]. This is the only CSS framework / library that fit my brain and my workflow.
If you are struggling with learning any CSS framework, I hope I could shed some lights
by introducing you Tachyons.

READMORE

Instead of a CSS framework, I consider Tachyons a CSS library.
Here's the marketing pitch from Tachyons'
[Github page](https://github.com/tachyons-css/tachyons/):

> Functional css for humans.
> Quickly build and design new UI without writing css.

So what's functional css? Here I quote the explaination from the article
[In defense of Functional CSS](https://www.mikecr.it/ramblings/functional-css/)
by [Mike Crittenden](https://twitter.com/mcrittenden):

> Functional CSS basically means that you have a ton of __tiny__,
> __single purpose__ classes that are named based on their __visual__ function.

Here's the definition from [John Polacek](http://johnpolacek.com/)'s
[Let’s Define Exactly What Atomic CSS is](https://css-tricks.com/lets-define-exactly-atomic-css/):

> Atomic CSS is the approach to CSS architecture that favors __small__,
> __single-purpose__ classes with names based on __visual__ function.

They are both telling the same thing with different words.
There seems to be a theme about __small__, __single-purpose__, and __visual__ here.
But why is small and single-purpose important for CSS classes? It's because it makes
them highly __composable__. And the visual aspect makes the composable classes
very __readable and maintainable__.

In the rest of the post, I'll explain why composibility and maintainability
make Tachyons a great fit for my brain and then why it fits my workflow.

[1] Cljdoc is a website building & hosting documentation for Clojure/Script libraries.
