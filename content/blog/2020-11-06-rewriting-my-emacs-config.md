+++
title = "Rewriting My Emacs Config"
category = "Journal"
tags = ["emacs"]
authors = "Daw-Ran Liou"
+++

Since the last post, I had switched over to [Doom
Emacs](https://github.com/hlissner/doom-emacs) for a while and then, hopefully,
have switched back to my hand-crafted emacs config for the last time. I wanted
to try Doom for some reasons:

1. I didn't like the startup time of my previous handcrafted emacs config,
1. I found painful to look at my emacs config, and
1. I wanted to learn more from this very successful community-driven project.

Even prior to switching over to Doom, I took a lot of inspirations from it to
enhance my config. One main frustration of using Spacemacs was its think layers
of abstracts. I didn't find such frustration with Doom. Doom is very well
documented and stays true to its mantras:

> __Close to metal.__ There's less between you and vanilla Emacs by design. That's
> less to grok and less to work around when you tinker. Internals ought to be
> written as if reading them were part of Doom's UX, and it is!

Trying various modules, it's fun exploring various pre-configured packages
without the hustle to configure them pre-maturely. As an example, I like the
[`org`
module](https://github.com/hlissner/doom-emacs/tree/develop/modules/lang/org)
just work out of the box with various of plugins.

However, eventually I grew tired of looking at all the keybindings that I wasn't
using. All the keybindings and functionalities that I wasn't using, or didn't
spark joy in me, made me sad. For every single unused keybinding, it is an extra
mental burden in my workflow. If I get rid of all the unnecessary keybindings I
could improve the efficiencies on the ones I use most often, because I no longer
need the long nested key strokes anymore.

Also, I found the [System
Crafters](https://www.youtube.com/channel/UCAiiOTio8Yu69c3XnR7nQBQ) YouTube
serious [Emacs From
Scratch](https://www.youtube.com/playlist?list=PLEoMzSkcN8oPH1au7H6B7bBJ4ZO7BXjSZ). It
really was a godsend to me to help me start writing my Emacs config again. [My
current Emacs Config](https://github.com/dawran6/emacs.d) drew a lot of
inspirations from it and from the author's, David Wilson's, [own
config](https://github.com/daviwil/dotfiles/blob/master/Emacs.org).

So far, I really like that I have total control over my emacs configuration, and
the start time wasn't too terrible either.

Links:
- [Doom Emacs](https://github.com/hlissner/doom-emacs)
- [Emacs From
Scratch](https://www.youtube.com/playlist?list=PLEoMzSkcN8oPH1au7H6B7bBJ4ZO7BXjSZ)
- [My current Emacs Config](https://github.com/dawran6/emacs.d)
- [My current Emacs
  theme](https://github.com/dawran6/emacs.d/blob/master/themes/sketch-white-theme.el)
