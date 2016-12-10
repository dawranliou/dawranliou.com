Title: Flask for New Web Developer
Date: 2016-02-18 10:20
Category: Python
Tags: python, flask, web
Slug: flask-for-new-dev
Authors: Daw-Ran Liou
Summary: Thoughts after finishing my blog app using Python Flask
Cover: /images/flask-logo.png

![alt flask](/images/flask-logo.png)
_"Flask logo" by Flask is licensed under [Flask Artwork License](http://flask.pocoo.org/docs/0.11/license/#flask-artwork-license)_

Last long weekend after my geek marathon I finally
finished reading [Flask Web Development: Developing Web Applications with Python](http://www.amazon.com/Flask-Web-Development-Developing-Applications/dp/1449372627)
and deployed my blog app on Heroku.
It was such rewarding to see my web app running online,
not only as a novice web application developer but also
as a adventurer to this whole new world of DevOps.
In this article I want to share some thoughts mostly about
Flask — the Python framework for web developing — ,
and covers a little about what I’d recommend a beginner to
start building web apps, and a little bit about the book.

BTW, here’s the link to my blog: https://blog.dawran6.com

# Flask

_Okay, why Flask? There are tons of web frameworks out there, why Flask?_

Just like starting learning a new programming language,
you’ll find all kinds of people debating and all kinds of
opinions about what’s best for you.
But the truth is, _I don’t know what I don’t know!_
Have you ever had the experience jumping into a late class and
got so lost that you had not a single clue where to start asking questions?
This was because you don’t have the background knowledge to ask the right question, yet.
Yeah, that was my first contact with web development.

Here’s my suggestion: if you are in the same situation as I did,
__go pick the framework that has the coolest icon among those most popular frameworks.__
Sounds ridiculous, isn’t it? Not entirely. Hear me out:
the reason those frameworks are popular is because
they serve the general purpose of web development well and
they are very well-documented.
What you need in your situation is a learning tool that
ramps up your knowledge and skills to a degree so you can drive along the way.
Not fine tuning your product to fit a specific need.
Once you are on the way, I’d say it’ll be easy to change lanes to
other framework because now you know the right questions to ask even before starting.

> “A question well asked is half answered.” — Charles Kettering.

So stop worrying about choosing the first framework and just choose one.
You’ll know how to make the right choice along the way.
What about the coolest icon thing?
Oh, that’s just some none-sense but something I tend to do a lot.
I’m a very visual learner and I got motivated to see pretty images.
__After all, motivation and perseverance are the two things you’ll need.__

If you still can’t make up your mind after all the stuffs I talked above,
here’s the ultimatum: choose Flask!
I have to admit that I’m biased,
since I have no other web developing experience with other frameworks,
but I hope to convince you in the next section.

# Flask as a learning tool

I’ve learned some great properties in Flask as a learning tool along my journey.

## Starting Small
The first thing you know about Flask is —
“___Flask is a microframework for Python…___”.
Flask is designed to be extended.
The core of the framework only provide the bare minimum functionality for
you to build a web application.
I found this to be very different from the famous “battery-included”
philosophy for Python the language itself.
But WTH. This is the best part of Flask in my opinion.

Take a User Login feature for example.
The scope could differ by a hundred miles based on the user story.
One user story could only need one admin login account;
another could be a social networking site allowing login with their Twitter accounts.
Flask does not make any assumption of it.
In the official tutorial — [Flaskr](http://flask.pocoo.org/docs/0.10/tutorial/introduction/),
it simply hard coded one set of username and password in the app and that’s it!
No database nor SQL code needed for user sign-in.
_(Here I’m just giving an example.
In the tutorial it actually shows you how to create a database for the blog posts.
However, there isn’t any restriction if you are going to
save all the blog posts into one gigantic text file and retrieve it later.)_

This is great news for a beginner like I was!
Instead of learning a rich set of features provided in the framework,
I can focus on the minimum set of building an app,
and later on, if I need to extend the feature,
I can always look out for PyPI packages that serves my need.
This leads to my second point— the extensiblility

## Extensibility

Because Flask does not make any assumption about anything
(besides that you are building a web app of course, stop being picky!),
once you acquire the basic knowledge,
it’s easy to extend it from there with Flask.
Take the User Login feature again for example,
if you are looking for a Twitter account login feature,
you’ll be able to find several PyPI packages to support it like Flask-OAuth,
or Flask-Social (or you can implement it yourself.)
Also, you can include the Flask-Login package to
deal with the login session for you and have all kinds of goodies to make your code cleaner.

_(Note: There are a great community support for
Flask extensions and lots of them are great! I won’t talk about those in this article though.)_

## Popularity

Unarguably Flask is one of the most popular web frameworks.
__With great popularity comes great community.__
There are lots of great articles, online tutorials,
and resources in all sorts of forms on the Internet.
So, in the end if you are not using Flask,
be my guest to use any of the popular frameworks out there.

# How I learned in the hard way

It’s such a long route for me to learn web development now think of it.
What I used to do was to read a lot of online tutorials and code along the way.
I’ve always been more of a maker than a thinker.
I am the kid who always love to get hands dirty before the teacher said go.
However, I somehow failed with this approach when
I first learning about web developing.
The information in the articles or video tutorials I found
weren’t sufficient enough to ramp up my speed to
where I can cruise for myself even for a short while.
I got confused a lot and didn’t know what was missing out.

Stumbling myself for a few while, finally I decide to consult to the books.
Sounds like old-school but it was my savior.
The book turned out to fit my needs very well —
it is leading you to build a real world application
in a very systematic way while introduces you different components of web development.
This is what makes all the difference.
Eventually I was able to gain the knowledge in each web components and
having fun coding along with it. If anyone ask me to recommend a book about Flask, this is it.

This approach won’t be suitable for everyone.
But if you happen to face the difficulty to get into the web development,
reading books might be an option.

There’s only one thing I wished the book to tell me more — deployment.
I think the last mile in the book was the longest part.
Anyways, the book isn’t about DevOps after all.

# Final Words

I am sharing my thoughts not because I know a lot about this topic.
On the contrary, I am sharing because I am just some dude who
knows a little more than a beginner,
and I want to help my fellow beginners to get out of the troubles I had.
At least I’m not too far from where beginners stand.
My memory is still quite fresh and relevant.

I also plan to write a series of Flask tutorials in the future. Stay tuned!

Hope you find this article useful for you, even just for a little.
I appreciate any comments or feedback.
Feel free to reach me on Twitter @dawranliou or below in the comment section. Cheers!
