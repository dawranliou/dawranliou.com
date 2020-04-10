+++
title = "Don’t think. Just write the code!"
tags = ["python", "dev"]
slug = "just-write-the-code"
cover = "images/16-just-write-code.jpg"
+++

Imaging you are given a brand new web project, some vague requirements,
and a ton of freedom to choose the technical stacks, will you take it?
My answer while a couple of months back was… “Hell yeah!”
Even though I had never really worked on a real-world project except the
blog app that was taught a thousand times from online tutorials.

It had been a wonderful journey, with struggles, of course. I would love
to share what I learned. Hope it sheds some lights on your coding journey
too. To set the expectation right, let me give you some idea of my
experience level before I started the project.

<!-- more -->

# Before I started

Okay, quick inventory check in my developer tool box before I started.
In the fluent scale of 1 to 10, I had:

1. Python: 8. (I use list comprehension here and there, duh.)
1. Flask: 5
1. SQL: 2 (but I can use SQLAlchemy as the ORM: 5)
1. JavaScript ES6: 2 (mostly because the syntax is similar to Python even though I had never learned it.)
1. HTML: 5?
1. CSS with Bootstrap: 5
1. Git: 7 (still need to google how to git squash from time to time)
1. REST architecture: 4? I know GET, POST, PUT, DELETE and 1 point for each.

Hell yeah, I could do this! What could go wrong?

# The Task

The task was to build an internal analytics application that touches
several different other internal services inside the company.
Due to intellectual property issue I cannot show the end product.
However, imaging it’s like a dashboard that gathers several information
into charts and tables. The user can also modify and download the data
from the app.

# The Stack

I wanted to build a single page app (SPA) because everybody was doing it.
(The worst reason or the best reason to choose a technology. Your pick!)
Python Flask backend was a natural choice for me because I didn’t have
experience with the other popular frameworks. I did a blog app using
Flask by following Miguel Grinberg’s Flask book before.
Thus, I’ll use Flask to build a REST API service and leaves all the UI
part to the frontend.

I chose React to be my frontend framework because I had heard how good it
was (and it still is today.) Angular was popular too but I had a bad
experience with it several years back. Vuejs was a great choice but I was
so excited to learn React so WTH.

# Struggles

There. Were. A. Lot. This part I just want to rant on the things I was
struggling. If you don’t like it feel free to skip it to the lessons sections.

I want to skip the struggles with understanding the user story since those
weren’t technical. However, this might be time I struggled the most because
I didn’t feel comfortable discussing out of thin air. I like to discuss on
more concrete thing.

In my opinion, Flask is great for building simple, small project, and stay
simple and small. Don’t be mislead by the term “simple.” A simple problem
does not guarantee a simple solution. As my favorite quote by Leonardo da
Vinci:

> “Simplicity is the ultimate sophistication.”

It takes a lot of thinking to build something that’s truly simple. And that
was not what I was building from the beginning.

Setting up the server and running my application was another struggle. It
was especially hard if the IT policy blocks the external connections. This
forced me to start resolving the dependencies from source code. Gotta say
that I felt like a Linux guru when I compiled Python, Redis, SQLite, and
other software from the source.

Writing ugly React code was another struggle. In the back of my mind I knew
there must be a better way. I was too afraid to refactor it because of the
lack of any test. God I wish I knew how to write React tests.

# Lesson 1 - The Architect vs the Gardener

I like this analogy from one of my favorite author Brandon Sanderson.
He said there are two types of writers — the architect and the gardener.
The architect designs all the nitty-gritty details on the blueprints before
building the house, whereas the gardener improvises while fixing the garden.

To be honest, I would love to be the architect and receive praises like
“he solved the problem by thinking!” I wanted to just think for a while,
sit down, write code, and boom! Done! But the truth was, I was more towards
to the gardener when I wrote the code. I thought only a bit, wrote a lot of
code, deleted a lot of code, and rewrote a lot of code.

Overtime, I found my gardening process actually very beneficial. When I was
making a lot of trial-and-errors, I also learned many ways why things didn’t
work. Those errors surely built up my understanding to the technologies I was
using. Also the more comfortable I dealt with errors, the more experiences I
gained. So, my philosophy is to be the gardener — write as much code as you
can — when you don’t have much experience, and you’ll learn from it!

# Lesson 2 - Trust me, write tests

I’m not a very strict TDD (Test Driven Development) person, but I enjoy
writing tests. Trust me, writing tests is very beneficial if you are making
a lot of changes in the code base. I had written code that was worse than
spaghetti or lasagna. It’d be almost impossible to safely untangled my
spaghetti code without tests.

Writing tests also takes experience. It’s fine to write lots of ad-hoc
tests and remove them later on when broken. The point is to make a conscious
choice when the test breaks. Also, the more bad tests you wrote, the more
experienced you are to write good ones.

One benefit about TDD-ing your REST APIs is that it forces you to think
through the API design before you started. Or, at least, forces you to
design an early version of the API to start with.

# Lesson 3 - Don’t be afraid to go internal

This is one very valuable skill I learned. As I wrote lots of codes, I
constantly saw errors. Seeing errors are horrible, but whenever I saw an
error, I stopped the urge to google the error message immediately. Instead,
I looked at the stack trace (not just the stack trace from your code,) and
try to reason the error from the trace only. If needed, go into the
library/framework code and set a breakpoint there to see the error in action.

Libraries are great resources of learning. Not only are they readable,
but also they are written by many smart people. In my opinion, this is
the best resource to push yourself to the next level when there are no
senior developers around.

# Lesson 4— Watch out for the perfectionist

Sometimes I found myself struggle because I didn’t like the solution at
hand. Somehow I knew there must be a better way… This was when I caught
myself being the perfectionist, again. And my solution was pretty much
always — practicality wins.

I would write down the practical but ugly solution first, and told
myself: “I would come back for you when I knew the answer.” Sometimes
I did come up with a better solution and spent extra efforts refactoring
the code; sometime I didn’t and the code stuck. If the former happened,
I learned to accomplish the task in two different way; if the latter
happened, I still have some code that worked. There wasn’t anything to
lose to write down the ugly but practical code first, except for your
programmer ego.

# Bonus Lesson — Contribute back

I wouldn’t have accomplished my task without the help of the open source
community. Luckily, contributing back to the community not only satisfied
my needs of giving back, but also helped me to gain experiences.

For less experienced developers, there are several ways to contribute
back to the community, from the least amount of efforts to the most in
my opinion:

*. Star the Github repo
*. Answer questions on Stackoverflow or Github issues
*. Post Github issues (To me, it’s not easy to post a well-defined problem.)
*. Improve documentations

Improving documentations takes the most efforts because it really stretches
my understanding of a library. For example, while working on the PyTest
documentation, I learned a cool pattern with the @pytest.mark decorator
to make any mark such as @pytest.mark.hello or @pytest.mark.obsolete.

# Conclusion

Talk is cheap, so does thinking. Until I wrote down the code and ran it
did I know if it worked. So stop thinking and just write the code! My
conclusion really is just one point — write more code! This was crucial
for me when I had very little experience. I don’t think there was a better
way to gain experience other than writing more code.

If you like this post, please help me to share it. I also maintain a
personal blog at https://dawranliou.com/ . Cheers!
