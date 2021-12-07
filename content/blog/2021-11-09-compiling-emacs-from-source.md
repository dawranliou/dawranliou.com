---
title: Compiling Emacs from Source on MacOS Big Sur
authors: Daw-Ran Liou
tags: [emacs]
---

Recently I started to compile the Emacs master branch from source for daily use
instead of using one of the popular formulas on Homebrew.[^1] It's pretty cool
to try the latest features like the native compilation and the native emoji
before the stable releases.  Also it's quite a satisfying experience to be able
to compile Emacs from source.  It took me several tries to get it right but I
think I documented everything.  Here are the steps:

## Install dependencies

- `libxml2`
- Optional for native compilation
  - `gcc`
  - `libgccjit`

```sh
brew install libxml2 gcc libgccjit
```

Follow the post-installation instruction from `libxml2` to fix the path and
environment variables.

```
If you need to have libxml2 first in your PATH, run:
  echo 'export PATH="/usr/local/opt/libxml2/bin:$PATH"' >> ~/.zshrc

For compilers to find libxml2 you may need to set:
  export LDFLAGS="-L/usr/local/opt/libxml2/lib"
  export CPPFLAGS="-I/usr/local/opt/libxml2/include"

For pkg-config to find libxml2 you may need to set:
  export PKG_CONFIG_PATH="/usr/local/opt/libxml2/lib/pkgconfig"
```

## Compile

I configured my Emacs with the following options.  I'm not exactly sure what
each meant but this combination seems to work on my machine:

- `--with-cairo` - enable Cairo drawing
- `--with-imagemagick` - enable ImageMagick support
- `--with-xwidgets`
- `--with-native-compilation` - enable native compilation

```sh
./autogen.sh
./configure --with-cairo --with-imagemagick --with-xwidgets --with-native-compilation
# Build MacOS App bundle, `install` doesn’t actually install anything.
make clean install
```

The Emacs.app App bundle should now be in the `nextstep/` directory.  Simply
copy it to the `/Applications` folder to use it.

## References

-   [Building Emacs 27.1 on macOS Big Sur](https://stuff-things.net/2020/12/28/building-emacs-27-dot-1-on-macos-big-sur/)
-   [Compiling Emacs in the absence of a configurable brew version](https://www.freesteph.info/blog/compiling-emacs.html)

## Footnotes

[^1]: Probably the popular options are: [`emacs-mac`](https://bitbucket.org/mituharu/emacs-mac/src/master/), [`emacs-plus`](https://github.com/d12frosted/homebrew-emacs-plus), and [`emacs-head`](https://github.com/daviderestivo/homebrew-emacs-head).  I was mostly using `emacs-mac` in the past.
