---
title: My Emacs Clojure Development Environment Overview
authors: Daw-Ran Liou
tags: [clojure, emacs]
---

# Preface

To me, Emacs isn't a text editor but rather an extremely flexible development
environment that allows anyone to optimize their workflow. I used to be a Vim
user and did everything inside the terminal: Vim for code editing, bash commands
for running/testing code, and git cli for all other git operations. I wasn't in
any sense a Vim superuser. However, I felt pretty comfortable to do most of my
day-to-day work inside the terminal.

For the past two years, I got into the Clojure programming community and made a
very audacious move to learn Emacs at the same time. To me, learning Emacs was
just as hard, if not harder, as learning Clojure itself. However, learning
either subjects has paid off far more than I had expected. In this post, I will
share an overview of my current development environment setup and explain my
rationale of some of the key bindings that I use.

# Magit - git porcelain

[Magit](https://magit.vc/) is one of the most powerful powerful packages
available on Emacs. It is also the package that made me rethink my entire
workflow. If you don't known, Magit is a git porcelain that makes all the fancy
git operations within Emacs. I used to enjoy doing all git operations inside
terminal. I rarely do it lately because of how great Magit is. If I were to stop
using Emacs for writing code, I'd definitely continue using Magit until I find
another comparable tool.

I bind `magit-status` to `command+g` (also `<SPC> g g` because of old habit using
Doom Emacs.)

# Cider - advanced Clojure integrated environment

I use [Cider](https://cider.mx/) to manage the nrepl sessions and to evaluate
code. I used to rely on Cider for more things like looking up code definition
but lately I've been experimenting that with `clojure-lsp` which I'll describe
in the next section.

I bind the Cider keys under a local leader key `<,>`, which is only available in
the clojure(script)-mode. This is a habit I developed during the time using
Spacemacs and Doom Emacs though their keymaps are slightly different. Currently
I only use it to evaluate code in three levels - to evaluate buffer, evaluate
top-level vars, and evaluate s-expression. They are currently bound to `<,> e
b`, `<,> e f`, and `<,> e e`, which I'll probably simplify by removing the
middle `e` later.

# LSP - code jumping

LSP is the text editor-agnostic protocol for IDE-like operations. It separates
the concerns of those IDE-like capabilities and the actual implementation. For
example, the LSP defines the capability of `textDocument/definition` and the
[clojure-lsp](https://github.com/snoe/clojure-lsp) implements how to find the
definition of a particular Clojure symbol. In Emacs, AFAIK there are two
packages to support LSP interactions,
[lsp-mode](https://emacs-lsp.github.io/lsp-mode/) and
[eglot](https://github.com/joaotavora/eglot). I've tried both in the past but
I'm sticking with `lsp-mode` at the moment.

I bind `command+l` to the lsp-keymap-prefix while I'm still learning it. So far
I only need two functions: `lsp-find-definition` and `lsp-find-references` which
are bound to `command+l g g` and `command+l g r` respectively. I'll definitely
re-bind those keys for easier access.

# Lispy + Lispyville - general lisp s-expression editing

I use [Lispy](https://github.com/abo-abo/lispy) and
[Lispyville](https://github.com/noctuid/lispyville) for most of the e-expression
editing in Clojure mode instead of using smartparens or paredit or parinfer. I
like the safe evil operations that are remapped by Lispyville so I don't need to
worry about unbalanced parens. However, I don't use a lot of the keys other than
slurping, barfing, and dragging s-expressions.

# Dired - file management

I bind dired to `<SPC> d` with just two key strokes for easier access. Dired mode
was a powerful package that I overlooked for so long. It finally came into my
radar thanks to the amazing [video](https://youtu.be/PMWwM8QJAtU) by [David
Wilson](https://twitter.com/daviwil). Dired was truly a missing piece in my
workflow centered around Emacs. Having the ability to browse the filesystem
naturally inside the Emacs buffer was absolutely a joy.

# Eshell - direct shell access

This is bound to `<SPC> e` also for easier access. With all the packages above,
there are rarely things I need to do directly in shell. For those things that
has to be done inside shell, I usually do it in Eshell. However, there are times
I do need something closer to the native terminal app on OSX, something I can
start a long running process and bury it in the background. In these cases I'll
launch vterm instead. However, for some those off tasks like compiling code or
start a docker container in the background, I prefer Eshell.

Links:
- [My current Emacs Config](https://github.com/dawranliou/emacs.d)
- [Magit](https://magit.vc/)
- [Cider](https://cider.mx/)
- [clojure-lsp](https://github.com/snoe/clojure-lsp)
- [lsp-mode](https://emacs-lsp.github.io/lsp-mode/)
- [eglot](https://github.com/joaotavora/eglot)
- [Lispy](https://github.com/abo-abo/lispy)
- [Lispyville](https://github.com/noctuid/lispyville)
- [David Wilson](https://twitter.com/daviwil)
- [System Crafters](https://www.youtube.com/c/SystemCrafters/videos)
