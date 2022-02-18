## To simplify my s-exp workflow

I had been using [lispy] and [lispyville] for [general s-expression editing and
navigation][1] previously. My setup works pretty well, but I often found the
packages are a bit of a feature bloat. Plus, I don't use most of the transitive
dependencies (ace-window, counsel, ivy, swiper, hydra). Over time, I started to
realize what I like or want and what I don't care about for my workflow. I
decided to switch to a lighter package to help me to build the workflow. So here
we are, back to the good old [paredit], again, for its simplicity.

Here's a list of features I _actually_ want to use:

1. S-exp navigation
1. Killing s-exp
1. Closing parentheses automatically
1. Slurping and barfing s-exp
1. Wrapping and unwrapping (or splicing) s-exp
1. Dragging (or transposing) s-exp

Surprisingly, vanilla Emacs already supports half of the list! By default,
`C-M-(f|b|u|d|n|p)` bounds to the s-exp navigation commands; `C-M-k` kills
s-exp; `C-M-t` transposes s-exp that acts sort of like dragging s-exps. So I
only need paredit to fill in the rest of the list.

## To not-handle parentheses

I recently came across this [reddit thread][2], which gave me a new perspective
about handling parentheses for lispy languages. Actually, it's about how __not__
handling parentheses. The constraint of keeping parentheses balanced all the
time is surprisingly unnecessary and restrictive. Without this constraint, I
started finding myself enjoying a lot more freedom in my REPL workflow. For
example, when I have the Clojure code:

```
(-> data
    transform-1
    transform-2
    transform-3
    transform-4)

(let [a 1
      b 1
      c (+ a b)
      d (+ b c)
      e (+ c d)]
  ;; some more computation
  )
```

To inspect the transformation, I can temporarily insert unbalanced closing
parentheses and code and then evaluate the code by putting the point at `-!-`:

```
(-> data
    transform-1
    transform-2)-!-  ; eval this s-exp
    transform-3
    transform-4)

(let [a 1
      b 1
      c (+ a b)] c)-!-  ; eval this s-exp
      d (+ b c)
      e (+ c d)]
  ;; some more computation
  )
```

Once I got the results, removing the temporary code is all it takes to have
perfectly balanced parentheses again.

> This is more or less the same as using `cider-eval-sexp-up-to-point`, but I
> don't want to memorize too many lesser-used keybindings. Plus, I prefer
> workflows that are more generic to other lispy languages.

To do this, I unbound a couple of commands from the `paredit-mode-map`, such as
`C-d` and `DEL`. `C-k` is sometimes useful to delete code in bulk, so I still
keep it bind to the `paredit-kill` command.

However, unlike what the reddit thread mentioned, I still find automatically
inserting paren pairs helpful. This is probably because I work a lot in Clojure,
which considers square brackets `[]` and curly braces `{}` as valid s-exp as
well as parentheses `()`, so there are multiple ways to close an s-exp. Also,
it's one less keystroke to type 😛.

[paredit]:https://www.emacswiki.org/emacs/ParEdit
[lispy]:https://github.com/abo-abo/lispy
[lispyville]:https://github.com/noctuid/lispyville

[1]:/blog/my-emacs-clojure-dev-env-overview.md#lispy-lispyville-general-lisp-s-expression-editing
[2]:https://www.reddit.com/r/emacs/comments/l7khmk/what_key_binding_scheme_do_you_use_to_handle/gl9fcqs?utm_source=share&utm_medium=web2x&context=3

<!--  LocalWords:  paredit lispy lispyville swiper exps
 -->
