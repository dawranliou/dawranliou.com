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

## Tachyons

Instead of a CSS framework, I consider Tachyons a CSS library.
Here's the marketing pitch from Tachyons'
[Github page](https://github.com/tachyons-css/tachyons/):

> _Functional css for humans.
> Quickly build and design new UI without writing css._

So what's functional css? Here I quote the explaination from the article
[In defense of Functional CSS](https://www.mikecr.it/ramblings/functional-css/)
by [Mike Crittenden](https://twitter.com/mcrittenden):

> _Functional CSS basically means that you have a ton of __tiny__,
> __single purpose__ classes that are named based on their __visual__ function._

Here's the definition from [John Polacek](http://johnpolacek.com/)'s
[Let’s Define Exactly What Atomic CSS is](https://css-tricks.com/lets-define-exactly-atomic-css/):

> _Atomic CSS is the approach to CSS architecture that favors __small__,
> __single-purpose__ classes with names based on __visual__ function._

There seems to be a theme around "small," "single-purpose," and "visual" here.
But why are small and single-purpose important for CSS classes? It's because it makes
them highly __composable__. And the visual aspect makes the composable classes
very __readable and maintainable__.

In the rest of the post, I'll explain why composibility and maintainability
make Tachyons a great fit for my brain and then why it fits my workflow.
You can also find Tachyons' creator, Adam Morse, talk about his creation
if you'd like to learn his point of view[2].

## Composability matters

## Readability and Maintainability

## Workflow

[1] Cljdoc is a website building & hosting documentation for Clojure/Script libraries.

[2] [![Adam Morse: Tachyons CSS Toolkit -- Devshop London June 2016](http://img.youtube.com/vi/r56fRaWth58/0.jpg)](http://www.youtube.com/watch?v=r56fRaWth58)
