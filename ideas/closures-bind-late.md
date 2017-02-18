Title: Closures bind late
Date:
Category:
Tags:
Slug: closures-bind-late
Authors: Daw-Ran Liou
Summary:
Cover:

"Closure is a turtle carry its shell," taught by 
[Raymond Hettinger](https://twitter.com/raymondh), this is my favorite way
of understanding closures. With closure, lots of great features are possible
in Python like higher order functions and decorators. 

I came across this StackOverflow post - 
[How can I return a function that uses the value of a variable?](http://stackoverflow.com/questions/42003351/how-can-i-return-a-function-that-uses-the-value-of-a-variable)
, which helped me bridging the gap in my knowledge,
the gap that I sort-of understood it but couldn't explain it very well. 
The bridge is just a simple sentence, "closures bind late."

Consider a function generator that generates a series of multiplication
functions starting from 0 to `max`:

```python
def mult_function_generator(max):
    for i in range(max):
        yield lambda x: x * i

mult_functions = list(mult_function_generator(3))
print([func(3) for func in mult_functions])
# Oh crap, it's [6, 6, 6], not [0, 3, 6]
```

Let's take a closer look at what's behind the scene. First of all, how does
a closure represent in Python, or, how does the shell look like to the turtle:

```python
mult_functions = mult_function_generator(3)
mult_zero = next(mult_functions)
mult_zero
# <function ...>
```

To look at the closure, there's a special attribute in each function called
`__closure__` (quite obvious, huh?)

```python
mult_zero.__closure__
# (<cell at 0x..., int object at 0x...>,)
```

Okay, we are getting closer. The `__closure__' attribute is a tuple of `cell`
objects. Let's see what's inside the cell:

```python
cell_zero = mult_zero.__closure__[0]
cell_zero.cell_contents
# 0
```

Bingo! This is the int object `i` that trapped inside the function's closure.

_(I haven't figured out how Python find the reference of `i` so I
won't go any further. For more discussion please refer to the Apendix
section.)_

The interesting thing is, the same cell object is used across our multiplication
functions. Thus, when we get the second function:

```python
mult_one = next(mult_functions)
cell_one = mult_one.__closure__[0]
cell_one.cell_contents
# 1
# So far so good. But...
cell_zero.cell_contents
# 1
# Crap!
cell_zero is cell_one
# True
# Okay fine
```

The same cell object means the same reference to `i`. This explains what happened
in this example. Or, if you like, you could explain all this by saying, "closures
bind late." They are both correct 
