---
title: How I build Bex
date: 2019-08-08
category: Clojure
tags: clojure, deployment
authors: Daw-Ran Liou
---

In this article, I'd like to share with you the technologies and services that I use
to create [Bex](https://bexdesign.io). If you are, and I assume you are, a programmer,
Bex is simply a personal online markdown text editor.

## TL;DR

1. Site: [Hugo](https://gohugo.io/) + vanilla JS, deployed to [Netlify](https://www.netlify.com/)
1. App: [ClojureScript](https://clojurescript.org/), [Re-frame](https://cljdoc.org/d/re-frame/re-frame/0.10.8/doc/introduction), [Shadow CLJS](https://shadow-cljs.github.io/docs/UsersGuide.html), also deployed to [Netlify](https://www.netlify.com/)
1. Authentication and database: [Firebase](https://firebase.google.com/)
1. Payment: [Stripe](https://stripe.com/docs/recipes/elements-react) + [Netlify Functions](https://www.netlify.com/docs/functions/)
1. (CSS: [Tachyons](https://www.netlify.com/)!)

## Site structure

```
# Site:
https://bexdesign.io/
                    /pricing
                    /sign-up
                    /forgot-password
                    /resend-email
                    /blog/...
                    /...

# App:
https://app.bexdesign.io/
                        /log-in
                        /profile
                        /docs/:doc-id
                        /...
```

This is the layout of the site: I want my site to have the Site part
and the App part, separated by the subdomain.
The App part is where the fun things are: all the business logics and the user interactions are here.
The Site part is important for SEO/marketing reasons.
This part is for the search engine robots to crawl.

Originally, the two parts are separated by pre-fixing all the App routes with `/a/`, instead of separated by the subdomain.
However, it didn't work very well to switch route between the Site and the App.
For simplicity, I broke it down to two apps deployed separately.

It's also easier to write the redirect logic for the App. It's simply:

```toml
# netlify.toml

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

## The Site - Static Site Generator

For the Site, I originally wrote all the html markup from scratch, page by page.
For some share markups like the navbar and the footer, I just copy-and-paste
each one of them whenever I make some change.

It worked well and I love the simplicity of it. No build tool. Just html, css,
and some vanilla JS. 
After a while, I wanted to start blogging on the site, and I know I won't be able
to hand-craft all the blog posts. I know a static site generator is a good option for me,
for what I need is to design the html layouts and fragments/partials once and let the tool build the site for me.

[StaticGen](https://www.staticgen.com/) is very
resourceful for comparing static site generators. Most of them can be easily deployed to Netlify.

There were other static site generators I had
used before, like Pelican, Jekyll, and Middleman. They all operate pretty similarly.
I chose Hugo for this project mostly because I want to try a different one and see
if I like it. Hugo is blazing fast, like instantaneously fast. The one thing I don't
like so far is that it always generate html files as index.html and put it under its
own folder. For example, the page `content/pricing.html` will `become public/pricing/index.html` after
building it. The result is a trailing slash for all the pages like `<domain>/pricing/`
but I'd rather see `<docman>/pricing.html`.

## The App - Clojure, ClojureScript, and the Ecosystem

The majority of the application is built with Clojure/Script.
Clojure is currently my favorite programming language so I was very tempted
to build something useful with it. ClojureScript is a dialect of Clojure which
compiles to JavaScript. Being a functional language, ClojureScript works exceptionally
well with React, with the help of a few libraries.

I use `Reagent` to create and render the React components; `Re-frame` to manage the application state;
`Shadow Cljs` to build the application and interop with the npm ecosystem.

Since most tutorials on the web shows you how to build applications with any language and any stack, I won't
get into too much details now for how I build the app.
For people getting into developing ClojureScript applications, I highly
recommend [@JacekSchae](https://twitter.com/JacekSchae)'s [Learn re-frame course](https://www.learnreframe.com/).

## The Backend - Authentication and Database

Firebase offers free plans if your app (actually mine) doesn't have much traffic.
Firebase offers pretty much everything I need for the application backend - authentication
and a database. You can expect the normal authentication functions like sign up, sign in, and
reset password flow to work with the Firebase SDK, it even takes care of the email
services as well. The database I choose is a simple key-value store. For my application,
I set the user id as the key, and all the data associate with the user as the value.
That way the database rule can be set this way to ensure only the user can read or write
his/her own data:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".write": "$uid === auth.uid",
        ".read": "$uid === auth.uid"
      }
    }
  }
}
```

One caveat about the Firestore database is that it doesn't save null as value.
If you assoc null as the value to the key, the database will simply remove the key.
So at one time I found that I need to serialize the user data into a string,
and de-serialize it to retain the null values.

## Payment System

The payment is handled by Stripe. For the SaaS product with subscription plan, you'll
need to create the product and a subscription plan associate with the product.
I did all these on the Stripe dashboard, but you can also do this programmatically.

Once this is done, you should have a couple of keys that you need in your application:

1. Publishable key
1. Secret key (KEEP THIS SECRET!)
1. Plan id(s)

The subscription flow with all these keys is:

1. Create a credit-card token with [Stripe Element](https://stripe.com/docs/stripe-js/elements/quickstart)
and the publishable key (pk_*). Send the token to backend.
1. The backend uses the token with the secret key (sk_*) to create a customer.[1]
1. The backend uses the customer id, the plan id, and the secret key (sk_*) to create a subscription.

Basically the publishable key is for the frontend, and the secret key is for the backend
because frontend can not hide the secret key. Thus, the backend is required here.[2]
Here [Netlify Functions](https://www.netlify.com/products/functions/) is the savior.
I published a function that handles all the backend works for me, and keeps the secret key
in the environment variable.

With the system in place, I still need to manage the user's state of subscription.
There might be a better solution, but my strategy is to keep two values in the database:

```json
{
  "subscription-id": "null|string",
  "current-period-end": "null|int"
}
```

There are four situations/states for `["subscription-id", "current-period-end"]` in this model:

1. No subscription: `[null, null]`
1. Currently subscribed: `[not-null, not-null]`
1. Unsubscribed but has time left in current period: `[null, not-null]`

Every time the user logs in, if the `subscription-id` exists, the backend makes a request to
the Stripe and updates the `current-period-end`.[3]

[1] You can potentially reuse credit card information if you had created a customer instance before. However, I feel that was too
many use cases to consider, so, to keep things simple, I recollect the credit card information every time
the user resubscribe after unsubscribing.

[2] Unless you are using [Stripe Checkout](https://stripe.com/docs/payments/checkout). Personally,
I did not try it because I want to create my own UI.

[3] The documentation says it's better to use a webhook in the backend and let Stripe to update
the user's `current-period-end` when the renewal payment is successful. I might change to
this strategy but then I need to change the rules in the database to let my backend change the
value without user sign in.
