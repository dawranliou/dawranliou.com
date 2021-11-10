+++
title = "Structural editing in vanilla Emacs"
authors = "Daw-Ran Liou"
[taxonomies]
tags = ["emacs"]
+++

## Strict structural editing

[Last time][back-to-paredit] I switched from the [lispy]/[lispyville] combo for
structural editing s-expressions to [paredit] for its simplicity.  However, I
started to realize that I kept unbinding commands from the `paredit-mode-map`
such as `C-d`, `C-k` because they were unnecessarily restraining.  Quote from
this [reddit thread]:

> I tend to think that all of the slurping, burping, gurgling, snorting, and
> barfing, which devotees consider handy, are just contrivances to try to make a
> virtue out of necessity.  From the moment that you've automatically,
> prematurely introduced a closing paren, you naturally find a need to try to
> find a way to work around it. If you don't box yourself in, in the first
> place, then there's no such need.

I still do premature paren closing with `electric-pair-mode` in my current
setup.  However, this thread really got me thinking.  _"Do I really want to have
balanced parenthesis at all times, at all costs?"_  To answer the question, I
ask myself: a) What problems does it solves, and b) What are the costs?

To me, the main cost is the freedom of expressing experimental code as I laid
out in the [Back to paredit](back-to-paredit) post.

I think the main problem it solves is to __"Avoid uncompilable source files by
accident."__

However, I don't care much about this problem nowadays since I don't often edit
the source code line-by-line or character-by-character and unbalanced parens are
rarely an issue.  Whenever I do accidentally introduce unbalanced parens, I can
usually quickly spot the error and fix it[^1].  It isn't very hard once I learn
to navigate and edit S-exps, which I'll show in the next section.

From there, it's a simple decision to ditch paredit and the strict structural
editing rules it imposes and revise my workflow with vanilla Emacs commands.[^2]

## S-expression workflow in vanilla Emacs

For the code examples in the following sections, I'll be using `<!>` as the
_point_, also known as the _cursor_ or the _caret_, and `<@>` as the _mark_.

### Navigate S-exp

Here are the ones I use most often:
- `C-M-d` - Go down the S-exp tree
- `C-M-u` - Go up the S-exp tree
- `C-M-f` - Go forward to the next S-exp at the same level
- `C-M-b` - Go backward to the previous S-exp at the same level
- `C-M-a` - Go to the start of the top-level S-exp (or the previous top-level
  S-exp)
- `C-M-e` - Go to the end of the top-level S-exp (or the next top-level S-exp)

I don't use these that often because they kinda overlap with `C-M-(f|b)` in a way:
- `C-M-n` - Go to the next S-exp
- `C-M-p` - Go to the previous S-exp

I often use the navigation commands to quickly put the point at the beginning of
the S-exp or the end of the S-exp.  This is quite important because the next few
operations require the point to be at the right spot.

Example (point is `<!>`, mark is `<|>`):

```
(+ (+ 1 2<!>) 3 4)
```

After hitting `C-M-u`:

```
(+ <!>(+ 1 2) 3 4)
```

### Mark S-exp

- `C-M-<SPC>` - `mark-sexp` (Mark the S-exp after the point)

Example:

```
(+ <!>(+ 1 2) 3 4)
```

Hit `C-M-<SPC>` the first time will mark the first S-exp following the point,
i.e. `(+ 1 2)`.

```
(+ <!>(+ 1 2)<@> 3 4)
```

Hit `C-M-<SPC>` the second time will mark the next two S-exps, i.e. `(+ 1 2) 3`.

```
(+ <!>(+ 1 2) 3<@> 4)
```

### Kill S-exp

- `C-M-k` - Kill S-exp after the point

When I'm working on lispy languages, I rarely use `C-k` to kill stuff.  I use
line-based navigation commands + the S-exp navigation commands to locate the
opening paren than do `C-M-k`.  I found this method more precise than using
`C-k`.

Example:

```
(+ <!>(+ 1 2) 3 4)
```

After hitting `C-M-k`,

```
(+ <!> 3 4)
```

### Drag S-exp

- `C-M-t` - Drag downward
- `C-M-- C-M-t` - Drag upward

Dragging downward is almost equivalent to transposing.  The only difference is
that transposing requires the point to be after the S-exp to be dragged
downward.

Example:

```
(+ (+ 1 2)<!> 3 4)
```

After hitting `C-M-t`:

```
(+ 3 (+ 1 2)<!> 4)
```

In the case of dragging upward, I usually kill the S-exp and then yank it to the
position I want.  Alternatively, using the negative argument with transposing
does it too.

Example:

```
(+ 3 (+ 1 2)<!> 4)
```

After hitting `C-M-- C-M-t`:

```
(+ (+ 1 2)<!> 3 4)
```

### Wrap S-exp

This requires the `electric-pair-mode`.  To do wrapping, I'll place the point at
the beginning of the S-exp(s) that I want to wrap, press `C-M-<SPC>` to mark the
following few S-exp(s), and then hit the opening paren `(` (or `"`, `[`, `{`) to
wrap the S-exp.

Example:

```
(+ <!>(+ 1 2) 3 4)
```

Mark the next two S-exp by hitting `C-M-<SPC>` twice

```
(+ <!>(+ 1 2) 3<@> 4)
```

Hit `"`:

```
(+ "<!>(+ 1 2) 3" 4)
```

### Raise S-exp

By default, Emacs doesn't bind the `raise-sexp` to any keys.  In my current
setup, I bind this to:

- `C-M-r`

Example:

```
(+ <!>(+ 1 2) 3 4)
```

After hitting `C-M-r`:

```
(+ 1 2)
```

### Splice S-exp

Splicing is really a special case of raising S-exp - basically, it "raises" all
the sibling S-exps.  So my workflow is to use `C-M-<SPC>` to select all the
siblings and then hit `C-M-r` to raise all of them.

Example:

```
(+ <!>(+ 1 2) 3 4)
```

Mark the next three S-exp with three `C-M-<SPC>`s.

```
(+ <!>(+ 1 2) 3 4<@>)
```

Hit `C-M-r`:

```
<!>(+ 1 2) 3 4
```

### Slurp & barf

There are no equivalent commands out-of-the-box.  However, I don't miss those
commands much.  In the case of slurping, I can just remove the closing paren,
forward one S-exp, and then insert the closing paren back:

Example:

```
(+ (+ 1 2)<!> 3 4)
```

`DEL`:

```
(+ (+ 1 2<!> 3 4)
```

`C-M-f`:

```
(+ (+ 1 2 3<!> 4)
```

Insert `)`:

```
(+ (+ 1 2 3)<!> 4)
```

Alternatively, kill the next S-exp and yank it before the closing paren.[^3]

## Conclusion

I think the more I work on lispy languages, the better I become at working with
S-exps.  Previously, I had to rely on the external packages' safety nets to edit
the source code safely.  Nowadays, I think I'm dangerous enough that I feel more
productive without those safety nets.  😈

I think my experiment so far has been a success.  I've been using the revised
workflows for my day-to-day work for a while, and vanilla Emacs is all I need to
be productive at navigating and editing lispy code.[^4]

## Footnotes

[^1]: With the help of `M-x check-parens` and `show-paren-mode`.

[^2]: I also came across the blog [Why is Paredit is so un-Emacsy?] after the
switch.  It's a nice read!  It kinda reassured me of my decision.

[^3]: I might be able to write a function to automate slurping and barfing in the
future.  Stay tuned!

[^4]: I also ditched evil-mode and learned code editing the Emacs way.  That's a
story for another time.

[paredit]:https://www.emacswiki.org/emacs/ParEdit
[lispy]:https://github.com/abo-abo/lispy
[lispyville]:https://github.com/noctuid/lispyville

[back-to-paredit]:@/blog/2021-08-14-back-to-paredit.md
[reddit thread]:https://www.reddit.com/r/emacs/comments/l7khmk/what_key_binding_scheme_do_you_use_to_handle/gl9fcqs?utm_source=share&utm_medium=web2x&context=3
[Why is Paredit is so un-Emacsy?]:https://andreyorst.gitlab.io/posts/2021-09-30-why-is-paredit-is-so-un-emacsy/
