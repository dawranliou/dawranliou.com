## Story

My history of playing with static site generator starts with Jekyll, Pelican,
Hugo, Middleman, and eventually landed on [Zola][3], which is my personal
favourite among these off-the-shelf static site generators.[^1] There were very
little things to complain about Zola, but one major painpoint was that - it
isn't built on Clojure (or any Lisp) so I didn't know how to hack the sourcecode
if there were something I want to change nor could I really understand the code.
After reading [Dominic][1]'s [I wrote myself a static site generator][2] and
[Kenton's Build Your Own Static Site Generator][4], I was really inspired and
decided to follow suit.

It took several tries to get to my ideal level of complexity.  Initially I was
really annoyed by how impative the main function was, and so I rewrote the logic
into a desciptive `build-plan`, a series of descriptive steps each with a
`:build/op` keyword, such as `:ensure-folder`, `:copy-dir`, or `:page`.  My main
function becomes a `doseq` on the `build-plan` that will dispatch on the
`:build/op` key.[^2] This indirection of imparativeness is pleasing to look at
but it's really difficult to fit into my workflow.  After a detour into the
Common Lisp world with my other gamedev project, I decided to scratch that
implementation and imbrace the imparativeness nature of the code.  At this time,
I also introduced the `site-map.edn` file, which is inspired by the flexibility
of how Common Lisp assembles a package.  I think I just prefer configuration
over convention.  I decided not to impose any significant meaning to the file
structure to the markdown files under the `contents` folder.  Instead, I
explicitly link the source file to a target URI in this `site-map.edn` file.

Building atom feeds (RSS feeds) was also a bit challenging because this is the
one thing I really didn't want to break backward compatibility.  (I'm really
sorry if I still break your RSS client feed.  I tried hard not to.)  A really
useful resource is [W3G's Feed Validation Service][5].  One thing I found weird
was that the `clojure.data.xml` would compile the Hiccup form `[:feed {"xmlns"
"http://www.w3.org/2005/Atom"} ...]` into `<feed
xmlns:b="http://www.w3.org/2005/Atom" ...`.  Notice that the `xmlns` attribute
turned into `xmlns:b` here and thus invalidate the feed.  For now I just YOLO'd
it with a `str/replace-first` function.[^3] 🤷 (Also, did I mention that I use
`python3 -m http.server` to serve the site locally?)

## Outcome

With Dominic's `build.sh` script, I was able to deploy my site on Netlify 🎉

As the time of writing, my code count is around:
- ~180 lines for all hiccup templates
- ~110 lines for parsing and generating the site
- And... ~500 lines of site-map.edn

## Footnotes

[^1]: Zola has a relatively simple templating structure, and it's very fast.

[^2]: This was highly inspired by the ClojureScript compiler code I read earlier that year.

[^3]: https://github.com/dawranliou/dawranliou.com/blob/master/src/com/dawranliou/build.clj#L65-L69

[1]: https://freeston.me
[2]: https://freeston.me/posts/2021-11-29-new-site-generator/
[3]: https://getzola.org
[4]: https://blog.hamaluik.ca/posts/build-your-own-static-site-generator/
[5]: https://validator.w3.org/feed/check.cgi
