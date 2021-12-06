---
title: From Vim to Emacs
category: Journal
authors: Daw-Ran Liou
tags: [emacs, clojure]
---

I was a Vim user a couple years back.
I totally loved (and still love) the philosophy of Vim
as well as the `hjkl` navigation. In contrast, Emacs didn't make any
sense to me. `C-v` for page down and `M-v` for page up? Does that even
make any sense to anyone?

Yet here I am, after all those years,
writing this article on my Emacs with all the keybinding craziness.
I must confess, I enjoy Emacs quite a lot. However, the transition
wasn't easy for me. Here's my journey so far.

<!-- more -->

## The start of the cycle

<img src="//imgs.xkcd.com/comics/lisp_cycles.png" title="I've just received word that the Emperor has dissolved the MIT computer science program permanently." alt="Lisp Cycles">

In the mid 2018 I started teaching myself Clojure, a dialect of the
Lisp family. I fell in love with the language but the thing that amazed
me the most, was how the Clojure community do all the amazing presentations
using Emacs. "Well," I told myself, "maybe it's time to finally see
the other side of the Vim vs Emacs Editor War."

I learned the basics from the [Clojure for the Brave and True](https://www.braveclojure.com/basic-emacs/) website,
It turned out that learning Clojure and Emacs at the same time
was too audacious for me at the time.
Clojure itself alone already has a pretty steep learning curve.
And then there was Emacs... I took a more strategic route
and focus on Clojure solely for a while (using both VSCode and IntelliJ.)
It was fine at the time (because I didn't know
what I was missing.)

Only knowing the foundamentals of Clojure, I started working on
opensource projects to gain more real world experiences. It was then
I was introduced to Spacemacs.

## Into the Space

[Spacemacs](https://www.spacemacs.org/), on the website:

> Spacemacs is a new way to experience Emacs -- a sophisticated and polished set-up focused on ergonomics, mnemonics and consistency.

To me, the main selling point was the exceptional out-of-box configuration.
The layer system makes adding packages almost no-brainers.
And plus, I didn't need to give up my Vim keybindings!
Spacemacs truely made me start liking Emacs and wanted to explore more
aspects of Emacs.

After learning a few basics, I was able to get pretty productive
using Spacemacs and started understanding the superpower of Clojure,
the REPL + Editor integration workflow. The "mnemonics" aspect
of Spacemacs also made discovering and memorizing the useful
functionalities so much easier.

I also made a switch to the `develop` branch of Spacemacs at the time
because of a lot of the features and customizations can only be done
there. I was learning quite a bit from both the official documentation
and from
[Practicalli's spacemacs tutorial.](https://practicalli.github.io/spacemacs/)

Then, it was the late 2019's that I started my first professional job as
a Clojure/Script developer. During the first week at work I saw my collegue's
minimalistic Emacs `init.el` file that also uses evil-mode
(which is the configuration that gives you the access to all the Vim keybindings.)
I was pretty amazed by it.

Using Clojure and Spacemacs day in and
day out, I wasn't completely satisfied with the fact that I can
only understand Emacs through the lense of Spacemacs. And so,
just last week, I started diving into Emacs.

## Embracing the Evil

Aaron Bieber has a great talk on ["Evil Mode: Or, How I Learned
to Stop Worrying and Love Emacs"](https://youtu.be/JWD1Fpdd4Pc).
He helped me to set the right mindset from the start.
I don't really want to ditch all the keybindings, just yet.
I also watched a lot of
[Mike Zamansky's Emacs tutorials](https://cestlaz.github.io/stories/emacs/).
It felt like an achievable goal to recreate the Spacemacs experiences myself.

Actually, someone has already done it:

- [Suyash Bire talked about Spin Your Own Spacemacs-lite](https://youtu.be/6INMXmsCCC8)
- [Bacterial Finches wrote about How to build your own spacemacs](https://sam217pa.github.io/2016/09/02/how-to-build-your-own-spacemacs)
- And here's a repo for [Spacemacs-Lite](https://github.com/balaramadurai/.emacs.d)

Those resources are awesome. They gave me so much hope that I knew
my goal of making my own mini Spacemacs was an achievable project.

I really learned a lot in this stage. One of the most important
lessons I learned is:

> Emacs is a self-documenting editor

Instead of needing to go to Google to search for answers, Emacs
has all those things built-in for you. Just like Clojure, I was
given the tool I need to solve my problems. Learning a few commands
and I was very well-suited to troubleshoot my editor. This is
very powerful.

So far I've got most of the things I want working. You can check out the code
[here](https://github.com/dawranliou/emacs.d/blob/master/init.el).
Currently it's about 500 LOC and I wrote every line of the code.
I feel very good about it. If something isn't working right, I know
where to fix it (most of the time.)

## To infinity and beyond

Emacs is truely an amazing piece of software that I had so much fun with.
The reason it pushed me away in the first place was its out-of-box configuration.
It required me to have opinions on almost every aspect of the editor
on day one, and this intimidated me.

Spacesmacs was a godsend for me when I needed it the most.
Till today, the way of Spacesmacs influences a lot of my opinion
toward my Emacs configurations. I'm not giving up the things I
like about Spacesmacs.

However, will I ever ditch Evil mode and embrace the holly form of Emacs? Maybe.
