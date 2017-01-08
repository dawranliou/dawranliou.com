Title: Generator - The Basics
Date: 2017-01-07
Category: Python
Tags: python, beginner
Slug: generator-the-basics
Authors: Daw-Ran Liou
Summary: The basic things you need to know about generators and how to use it to crack algorithm problem
Cover:

_I've been wanting to write articles about generators for quite some time.
Generator is definitely one of my favorate features since I stop treating
Python like Java or C++ or other programming languages. It was since then
I start having so much joy writing codes.
Generators, unlike other very powerful features (decorators, 
context managers, or classes) sometimes being abused a lot, don't
ever seem to be enough in my code. They have strong purpose in the code, and are very
beautifully sitting around. In this article, I'll explain generators 
with what I know about them. At the end, I'll show you how to use it to solve some classic
algorithm questions._

# 1. Why do I need to know generators?

There're two categories of use cases:

1. To generate a series of data
1. To pause code execution

For category #1, you might think _"why we need another tool while we already
know how to write lists to store a series of data?"_ There are three reasons:
1) list can't solve any problem, 2) performance, and 3) code style. 

1. List can't solve every problem. In the cases that we have a infinite 
series of data (e.g. generating prime numbers,) or the series of data 
that we can't tell when it'll stop (e.g. parsing log files that still have move
logs writing in,) there's no easy way to extract this part of code into
a function that returns a list. On the other hand, generators are perfectly fine 
for these jobs.
1. Performance. Space-wise, if the data series consumer just need one data point at a time, you don't
need to waste the memory to hold the entire copy of the data series. Time-wise,
if the data consumer does not require all the data to be present to do the next
thing, you can use generator to do lazy-evaluation when the consumer requires
the next data point.
1. Code style. This is a bit opinionative. I feel generator code are usually cleaner and
easier to understand then functions that tries to calculate the whole series
data and return them at once. Also opinionative, usually a generator code is
a hint of outputing a series of data. Therefore, I have some idea of what the
code is doing even without looking at the code line-by-line.

For category #2, this is something very powerful. Normal functions, or
called subroutines, execute line by line till they `return` control to
the caller. Generators, or a coroutine, execute till they `yield` a value
to the caller and pause the current state untill their caller invoke them
again to resume the execution.

# 2. What are generators?

Python's generator is a special case of [coroutine](https://en.wikipedia.org/wiki/Coroutine).
If looking at the proper definition gives you an headache, my way of understanding
it is - generators are functions that don't return.
Just like functions are objects in
Python, generators are also objects. They are special objects that looks like
function definitions but behaves very differently. Here's an example:

```python
def a_function():
    return True

def a_generator():
    yield True

>>> a_function()
True

>>> a_generator()
<generator object a_generator at 0x...>
```

Generator objects, like `a_generator()`, follow the Iterator Protocal. So
to invoke them, you use the builtin function `next()`

```python
>>> g = a_generator()
>>> next(g)
True
```

This means that when the iteration ends, an `StopIteration` exception is thrown:

```python
>>> next(g)
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
StopIteration
```

# 3. How do I use generators?

# 4. Generator in action
## Let's crack an algorithm questions using generators!
