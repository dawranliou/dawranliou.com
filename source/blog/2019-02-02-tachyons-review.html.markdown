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

> _Functional CSS basically means that you have a ton of tiny,
> single purpose classes that are named based on their visual function._

Here's the definition from [John Polacek](https://twitter.com/johnpolacek)'s
[Let’s Define Exactly What Atomic CSS is](https://css-tricks.com/lets-define-exactly-atomic-css/):

> _Atomic CSS is the approach to CSS architecture that favors small,
> single-purpose classes with names based on visual function._

There seems to be a theme around "small," "single-purpose," and "visual" here.
But why are small and single-purpose important for CSS classes? It's because it makes
them highly __composable__. And the visual aspect makes the composable classes
very __readable and maintainable__.

In the rest of the post, I'll explain why composibility and maintainability
make Tachyons a great fit for my brain and then why it fits my workflow.
You can also find Tachyons' creator, Adam Morse, talk about his creation
if you'd like to learn his point of view[2].

## Composability matters

Coming into CSS with functional programming background, it really troubles me
that all the CSS classes are effectively global-mutable-stateful objects. Without a good methodology
adopted by the developer, like [BEM](http://getbem.com/), all CSS classes have
side-effect on the whole document. (See example 1.) This makes containing
any CSS class a goal to solve the problem. Otherwise, changing the class might
have an effect on other elements. However, this also elimiates the
reusability of the classes. (See example 2.)

```css
// Example 1
// Globally, any tag with class "link" will have the same style.
// What if some of the link elements want to have a different color, red?
.link {
  color: black;
  text-decoration: none;
}

// Example 2
// To contain the style within the context, one might create
// many duplicate modifier class like below:
.resulttable--link-hide {
	display: none;
}
.header--item-hide {
  display: none;
}
.article--timestamp-unimportant {
  display: none;
}
```

Tachyons (and other functional css libraries) addresses the composibility problems by
introducing small, single-purpose classes.
Consider the examples above with the functional CSS aproach:

```css
// Functional CSS aproach for Example 1
// Now the black link is just a composition of .black and .text-decoration-none;
// red link is just .red and .text-decoration-none.
.black {
  color: black;
}
.red {
  color: red;
}
.text-decoration-none {
  text-decoration: none;
}

// Functional CSS aproach for Example 2
// Only one utility class for the modifier.
.display-none {
  display: none;
}
```

"This almost feels like writing inline-css!" and I kind of agree with you.
It seems like an common knowledge that inline-css is consider bad
(see [StackOverflow](https://stackoverflow.com/questions/2612483/whats-so-bad-about-in-line-css).)
But this seems to be the only obvious way for me to comprehend CSS.
I don't really understand how to effectively take advantage of the
cascading aspect of CSS, and functional CSS frees me from worrying
about it anymore. With functional CSS like Tachyons, the classes are
very composable and give me very few surprises. That is a powerful thing.
I'm able to focus more on the design and less about CSS.

## Readability and Maintainability



## Workflow

[1] Cljdoc is a website building & hosting documentation for Clojure/Script libraries.

[2] [![Adam Morse: Tachyons CSS Toolkit -- Devshop London June 2016](https://img.youtube.com/vi/r56fRaWth58/0.jpg)](https://www.youtube.com/watch?v=r56fRaWth58)
