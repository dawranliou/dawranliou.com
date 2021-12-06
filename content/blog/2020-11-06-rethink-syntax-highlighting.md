---
title: Rethink Syntax Highlighting
authors: Daw-Ran Liou
tags: [emacs]
---

_Update 2021-02-28: My color themes are now
[released](https://github.com/dawranliou/sketch-themes)! Check it out!_

![My light editor theme](/images/sketch-theme-white.png)
_A screenshot of my current editor theme - sketch-white_

I'm a minimalist when it comes to how I like to highlight my code - my code
editor is usually almost monochromatic, just black, white, three shades of gray,
and an accent color like orange. If those colors sounds a lot to you, I have to
assume you either are an extreme minimalist in code highlighting or you've never
give code highlighting a second thought and just use whatever the theme creators
throws at you. If you are the latter, this post is for you.

![My dark editor theme](/images/sketch-theme-black.png)
_A screenshot of my alternative editor theme - sketch-black_

We live in a world of distractions. Everything is constantly trying to grab your
attentions, your text messages, phone notifications, emails, social medias, ads,
also your friends and families. It is because: your attentions are, indeed,
valuable. We too often got distracted, not because we are bad at focusing,
(maybe some of us are), but more importantly, the world around us is constantly
demanding our attentions. When everything's in focus, nothing actually
is. When everything are signals, every one becomes a noise.

Same as your code syntax highlighting. When every single text is highlighted
with different colors, they are all just distractions and noises. Take a look at
the [Alabaster
theme](https://github.com/tonsky/vscode-theme-alabaster#motivation) by
[tonsky](https://tonsky.me/):

![Alabaster theme
screenshot](https://raw.githubusercontent.com/tonsky/vscode-theme-alabaster/master/screenshot.png)
_A screenshot of alabaster theme_

I like how the motivations describes:

> Most color themes highlight everything they can, ending up looking like a
> fireworks show. Instead, Alabaster uses minimal highlighting; it defines just
> four classes:
> * Strings
> * All statically known constants (numbers, symbols, keywords, boolean values)
> * Comments
> * Global definitions

This makes a lot of sense to me. The reason that we even need syntax
highlighting to begin with is because we, the programmers rather than the
machines, need to parse the text differently when we read it. For example, we
don't even need to parse the comments when reading code. Strings are in a sense
very similar to code comments. Strings are usually for the end users, not the
programmers. When I saw a string in code, I usually just skim through it and
make a mental note that "there's a string here."

Personally I don't like to highlight numbers, symbols, and keywords in code
because those to me are just parts of the code. I don't want them to stand out
too much because I still need to read them. I don't need additional visual aids
to help me understand they are different from the rest of the code. There used
to be a time that I embolden the keywords and highlight the global definitions,
but now I've made a conscious decision that leaving keywords unstyled works
better for me. I do have global definitions embolden because I like them to
stand out a little more.

How I like to highlight my code aligns with how I think about code. Your mileage
might vary. But remember this: less is more. The less colors you use will have
greater impacts. Have some thoughts about your syntax highlighting. How do they
help you in your work flow?  Do you really need all the colors to help you
program? It's pretty easy to customize color themes and syntax highlighting for
pretty much all the major code editors. Try building your own color theme if
necessary.

Cheers!
