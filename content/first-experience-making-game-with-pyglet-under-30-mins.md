Title: First Game Programming Experience with Python and Pyglet under 30 Minutes
Date: 2016-01-27 10:20
Category: Python
Tags: python, game
Authors: Daw-Ran Liou
Summary: Make games with Python
Cover: /images/pyglet-logo.png

This is my first experience with game programming,
and also my first time working with the
Python [Pyglet](https://bitbucket.org/pyglet/pyglet/wiki/Home) package.
I got to say I feel so much accomplishment even though it’s just a dumb,
simple, ghetto game that could be done within 100 lines of codes.
It really amazes me how things could get done so easily these days.

I was in the [San Francisco Python Meetup Group](http://www.meetup.com/sfpython/)
and joined
[Simeon’s Pyglet tutorial session](http://simeonfranklin.com/talk/pyglet/slides.html),
which really gave me a blast.
In his tutorial, he showed some fundamental building blocks for
Pyglet game programming like showing images on window, how to do animation,
listening to keyboard or mouse event, and so on.
After half an hour diving into the tutorial, suddenly he said,
“_are you ready to make a game now?_”

That was a shock. I don’t even know where to start!
However, the goal was simple, to create a simple game with
50 to 100 lines of code within the next hour.
So I start with the simplest game idea that I could think of —
to dodge whatever falls from the sky.
The more you dodge, the more score you have,
till you got hit and the game’s over.
Simple enough, eh?
Now I’m going to find a nice 8-bit character for my game.
Guess what I found? Mega Man!

_It’s time to get hands dirty!_

Surprisingly, the coding process wasn’t as hard as expected.
I don’t have a really good code design for my game so
I simply make incremental progress along the way.
The result might have been chaotic but it turned out
good enough for my little game. This is what I did in order:

1. Make a window
2. Show a still picture of Mega Man at the bottom of the window
3. Show a still picture of the ball at the top of the window
4. Make Pyglet listen to the left and right arrow keys and shifts the Mega Man accordingly
5. Let Pyglet update the ball position every time the clock ticks
6. Add a Game class that saves the “state of the game”. I’ll explain this later. OOP rocks!
7. Refactor the code to use a Game object for updating the Mega Man position and the ball position
8. Add the logic to detect and handle collision between Mega Man and the ball
9. Add the score label and the score logic
10. Done!

My git repo is here: https://github.com/dawran6/pyglet-tutorial/blob/master/megaman.py.
The code is less than 100 lines so it shouldn’t be hard to
follow even though I didn’t put too much thought on the structure.
Like the famous quote says: “___if your first product does not embarrass you,
you are shipping it too late.___”
Leave me comments or tweet me @randydliou if you have any thought want to share.

Come back to the step number 6 above.
There’re some good reasons to have a game state class defined in the code:

1. __You don’t need to declare global variable everywhere.__
I don’t like global variables. They make my code harder to maintain.

2. __Games are full of states that need to be tracked and
it’s a good idea to wrap those together for clarity.__
In my case, the states I am tracking is the position of Mega Man,
the position of the ball, the score, and if the game is terminated.
If the game gets bigger with more objects on the window, this list could increase a lot.

That’s it! It was a very entertaining and accomplishing experience. I had a lot of fun trying out something new. I think Pyglet did a pretty decent job maintain their documentation. It wasn’t hard for me to find the information that I needed. Highly recommend to give it a try if you’ve never done game programming before!
