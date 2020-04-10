+++
title = "Closures bind late"
tags = ["python"]
slug = "closures-bind-late"
description = "The behavior of closure might trick you"
cover = "images/turtle.jpg"
+++

"Closure is a turtle carrying its shell," my favorite explanation to
closures, quote by [Raymond Hettinger](https://twitter.com/raymondh).
With closures, lots of great features are possible
in Python like higher order functions and decorators.

I came across this StackOverflow post -
[How can I return a function that uses the value of a variable?](http://stackoverflow.com/questions/42003351/how-can-i-return-a-function-that-uses-the-value-of-a-variable)
, which helped me bridging the gap in my knowledge,
the gap that I sort-of understood it but couldn't explain it very well.
The bridge is just a simple sentence, "closures bind late."

READMORE

What does "closures bind late" mean exactly?
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

Seems like all the `i`s in the generated functions are assigned to 2,
which is the last value of `i` after all functions were being generated.
Let's take a closer look at what's behind the scene. First of all, how does
a closure represent in Python, or, how the turtle shell looks like:

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

Okay, we are getting closer. The `__closure__` attribute is a tuple of `cell`
objects. Let's see what's inside the cell, which has an attribute
`cell_contents` to help us:

```python
cell_zero = mult_zero.__closure__[0]
cell_zero.cell_contents
# 0
```

Bingo! This is the int object `i` that trapped inside the function's closure.

_(I haven't figured out how Python find the reference of `i` so I
won't go any further. For more discussion please refer to the Appendix
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
bind late." They are both correct but just with different mental models.

Now we understand the problem that introduced by late binding. Let's see how to
bind the variable during function declaration. The idea is to have `i` also
be a argument passed into the lambda function and then we assign the value
immediately:

```python
def mult_function_generator(max):
    for i in range(max):
        yield (lambda i: lambda x: x * i)(i)
```

Or if you like partial functions:


```python
def mult_function_generator(max):
    for i in range(max):
        yield functools.partial(lambda x, i: x * i, i=i)
```

Or my favorite one:

```python
def mult_function_generator(max):
    for i in range(max):
        yield lambda x, i=i: x * i
```

The last one works because default arguments is assigned when the
function is defined.


### Bonus

My favorite example for taking advantage of the late binding closure
is from [20 Python libraries you aren't using (but should)](https://www.oreilly.com/learning/20-python-libraries-you-arent-using-but-should):

```python
from time import perf_counter
from array import array
from contextlib import contextmanager

@contextmanager
def timing(label: str):
    t0 = perf_counter()
    yield lambda: (label, t1 - t0)
    t1 = perf_counter()

with timing('Array tests') as total:
    with timing('Array creation innermul') as inner:
        x = array('d', [0] * 1000000)

    with timing('Array creation outermul') as outer:
        x = array('d', [0]) * 1000000


print('Total [%s]: %.6f s' % total())
print('    Timing [%s]: %.6f s' % inner())
print('    Timing [%s]: %.6f s' % outer())

# Total [Array tests]: 0.064896 s
#    Timing [Array creation innermul]: 0.064195 s
#    Timing [Array creation outermul]: 0.000659 s
```

### Appendix - discussion on loading a dereferenced variable

Take the `mult_zero` function for example. The dissembled code looks:

```python
  3           0 LOAD_FAST                0 (x)
              2 LOAD_DEREF               0 (i)
              4 BINARY_MULTIPLY
              6 RETURN_VALUE
```

The second line calls the `LOAD_DEREF` op code in Python. My guess
is that the name `i` was never dereferenced because the cell object
still holds a reference on it. However, `i` must be treated specially
since `i` isn't visible to the scope outside the closure functions.
My next move will be looking into the CPython code to see how this
op code works exactly.
