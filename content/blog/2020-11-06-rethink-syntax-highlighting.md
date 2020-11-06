+++
title = "Rethink Syntax Highlighting"
authors = "Daw-Ran Liou"
+++

_TODO add a screenshot of my current color theme here_

I'm a minimalist when it comes to how I like to highlighting my code - my code
editor is usually almost monochromatic, just black, white, three shades of gray,
and an accent color like orange. If those colors sounds a lot to you, I have to
assume you either are an extreme minimalist in code highlighting or you've never
give code highlighting a second thought and just use whatever the theme creators
throws at you. If you are the latter, this post is for you.

We live in a world of distractions. Everything is constantly trying to grab your
attentions, your text messages, phone notifications, emails, social medias, ads,
also your friends and families. It is because: your attentions are, indeed,
valuable. We too often got distracted, not because we are bad at focusing,
(maybe some of us are), but more importantly, the world around us is constantly
demanding our attentions. When everything's in focus, nothing actually
is. Each thing becomes another noise when everything are signals.

Same as your code syntax highlighting. When every single thing is highlighted
with different color, they are all just distractions and noises.

I like the [motivations of the alabaster
theme](https://github.com/tonsky/vscode-theme-alabaster#motivation) by
[tonsky](https://tonsky.me/):

> Most color themes highlight everything they can, ending up looking like a
> fireworks show. Instead, Alabaster uses minimal highlighting; it defines just
> four classes:
> * Strings
> * All statically known constants (numbers, symbols, keywords, boolean values)
> * Comments
> * Global definitions

![Alabaster theme
screenshot](https://raw.githubusercontent.com/tonsky/vscode-theme-alabaster/master/screenshot.png)

This makes a lot of sense to me. The reason that we even need syntax
highlighting to begin with is because we, the programmers not machines, need to
parse the text differently when we read it. For example, we don't even need to
parse the code comments when reading code. Strings are in a sense very closed to
code comments. Strings are usually for the end users, not the programmers. When
I saw a string in code, I usually just skim through it and make a mental note
that "there's a string here."

Personally I don't like to highlight numbers, symbols, and keywords in code
because those to me are just parts of the code. There used to be a time that I
embolden the keywords and highlight the global definitions, but now I've made a
decision that leaving keywords unstyled works better for me. I do have global
definitions embolden tho.

I might be highly biased toward how I program in Clojure and Lisp in
general. The point really is this: have you thought about your syntax
highlighting and how they help you, not hinder you, in programming? It's pretty
easy to customize color themes and syntax highlighting for pretty must all the
major code editors. I encourage you to rethink your syntax highlighting and find
what's best for your own workflow.
