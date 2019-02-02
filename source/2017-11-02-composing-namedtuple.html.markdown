---
title: Composing namedtuple from namedtuples
date: 2017-11-02
tags: python
Slug: composing-namedtuple
Authors: Daw-Ran Liou
Summary: Sharing the recipe of composing namedtuple from namedtuples.
---

The problem:

> One namedtuples is great, two better. But how do I combine the two namedtuples to get the best nametuple of all?

For example, how to combine the Square class with Color class?

```python
Square = namedtuple('Square', 'x0 y0 x1 y1')
Color = namedtuple('Color', 'r g b')
# The opposite of DRY principle - WET (we enjoy typing) principle
ColoredSquare = namedtupe('ColoredSquare', 'x0 y0 x1 y1 r g b')
```

I don’t like this solution, because there are clearly something to reuse in Square and Color , and also, it isn’t fun to call ColoredSquare ‘s __init__ function that has 7 arguments. The alternative might be writing a custom class that takes Square and Color as arguments of the __init__ function, but then we lost all the benefits from using namedtuples. (TODO add a link of something to support this statement.)

So, here’s the recipe:

```python
class ColoredSquare(namedtuple('ColoredSquare',
                               Square._fields + Color._fields)):
    @classmethod
    def from_(cls, square: Square, color: Color):
        return cls(*square, *color)

my_square = ColoredSquare.from_(
        square=Square(0, 0, 1, 1), 
        color=Color(100, 100, 100))

>>> my_square
ColoredSquare(x0=0, y0=0, x1=1, y1=1, r=100, g=100, b=100)
```

Let me know what you think! Please share or like this post if you think it’s helpful!
