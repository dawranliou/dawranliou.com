+++
title = "Re-frame Effects VS Coeffects"
tags = ["clojure"]
+++

When I first started using `re-frame`, I was a bit confused about what
effects and coeffects are, or, what `fx`s and `cofx`s are.
They both seemed to relate to the “Side Effects” (or “Effects”) functional
programmers dislike. It was hard for me to distinguish them.

<!-- more -->

Before you read on, if you've never came across re-frame's
[official documentation](https://github.com/Day8/re-frame/blob/master/docs/README.md)
or the [cljdoc page](https://cljdoc.org/d/re-frame/re-frame/0.10.8/doc/introduction),
please start there. They are fantastic resources. However, if you find yourself
need some more examples or direct comparisons to help you understand them, please
read on.

## Two types of side effects

![side effects everywhere meme](side-effects-everywhere.jpg)

Out of the box, `re-frame` comes with the mechanisms to update the
application state. When your application needs to cause a side effect
on the application state, you only need to write `reg-event-db`,
to register your event handlers. The side effects that operate
on the application state is well-managed. There’s very little
confusion about them, so this article will not focus on those side effects.

The two types of side effects we are focusing on are the `effects`
and the `coeffects`, or `fx`s and the `cofx`s. These two types
represent things that are not a part of the application state,
such as:

1. Database
1. LocalStore
1. Third-Party APIs
1. (Other external resources that are not deterministic)

The differences are:

* `fx`s are ***pushed*** by an event, whereas,
* `cofx`s are ***pulled*** by an event.

## Examples of `fx`

1. Push data to a database
1. Push data to localStore/cookie
1. Push data to a third party service
1. Push message to `js/alert`
1. Push a function to JavaScript event queue,
e.g. `js/setInterval`, `js/setTimeout`, `js/Promise`

## Example of `cofx`

1. Pull data from a database
1. Pull data from localStore/cookie
1. Pull data from a third party service
1. Pull current datetime from browser
1. Pull a random number

## What about `js/Promise`s that fetch data?

This situation is a bit mind-boggling at first. A more concrete
example is when doing interop with a JavaScript library in an event handler
to fetch data, and it returns a promise immediately.
Thinking sequentially, what I wanted is to wait until the data fetched,
then run the event handler. However, promises don’t work that way.

The solution is to think of this as a push side effect, or `fx`.
For example:

```
dispatch [:fetch-data] -> do-fx [:promise-fetch-data] -> dispatch [:data-fetched %]
```

This ends up with two event handlers, one effect handler, and no coeffect handler.
I doubt this is the best way because I wish I can just have one `:fetch-data`
coeffect handler.

## Summary

1. Effects push,
1. Coeffects pull,
1. (Please let me know if you have a better way to handle javascript promises.)
