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
1) list can't solve all problems, 2) performance, and 3) code style. 

1. List can't solve all problems. In the cases that we have a infinite 
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
again to resume the execution. My favorite examples for this case is the
`@contextlib.contextmanager` decorator in [20 Python libraries you aren't using (but should)](https://www.oreilly.com/learning/20-python-libraries-you-arent-using-but-should#contextlib-ZKsPtpTX)
and also David Beazley's incredible live demo in PyCon 2015 -[Python Concurrency From the Ground Up: LIVE!](https://www.youtube.com/watch?v=MCs5OvhV9S4).

# 2. What are generators?

Python's generator is a special case of [coroutine](https://en.wikipedia.org/wiki/Coroutine).
If looking at the proper definition gives you an headache, my way of understanding
it is - _generators are functions that don't return_. I admit there are many
flaws in my definition, but this definition helps me to understand some key
concepts. Ask yourself a question, _what does `return` mean?_

`return` means to return control to the caller. Python virtual machine is a
stack machine. When a function is called, a frame object is PUSH onto the
execution stack. This frame object is a mini enviroment for the function.
Having this enclosing mini environment, the function could have its own
variables without worring about variable names colliding with the outter
environment. When the funtion returns, the frame object is POPPED from the
stack. All the variables sitting inside the mini environment are gone. The
caller regains full control and continues.

Generators, however, do not return full control to the caller. Instead, they
pause (or yield.) Thus, the mini enviroment of the generators, are still
sitting somewhere in the memory, waiting to be invoked.

Before I bored you to death with all the discriptions, let's see some code
in action.

Just like functions are objects in Python, generators are also objects.
They are special objects that looks very similar to regular function 
definitions but behaves very differently:

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

When generator objects are created, they don't start executing immediately.
Generator objects, like `a_generator()`, follow the Iterator Protocal. So
to invoke them, you can use the builtin function `next()`

```python
>>> g = a_generator()
>>> next(g)
True
>>> next(g)
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
StopIteration
```
No only do generators are iterators, they are also iterables!
(You can know more about the distinction between iterables and iterators from 
[]().) The builtin `iter` function call simply returns the generator itself,
which is a iterator:

```python
>>> iter(g) is g
True
```

Thus, all the builtin functions and external APIs that takes iterables
as input, works for generators! _(This is one of the most exciting realization
I had learning Python.)_ Keep in mind that generators are iterables. We're
counting on it for the next section.

# 3. How do I use generators?

Recall that I said generators are good to generate a series of data. Assuming
we have a function to calculate the incredible series of data - the first n
fibonacci numbers. The recursive solution most of us learned (with caching to
boost performance):

```python
# Recursive solution to get the first 10 fibonacci numbers
import functools

@functools.lru_cache()
def fibonacci(nth):
    "Calculate the nth fibonacci number."
    if nth < 2:
        return 1
    return fibonacci(nth-2) + fibonacci(nth-1)

def fibonacci_series(n):
    result = []
    for i in range(n):
        result.append(fibonacci(i))
    return result

>>> fibonacci_series(10)
[1, 1, 2, 3, 5, 8, 13, 21, 34, 55]
```

And here's an iterative solution with generator:

```python
# Iterative solution to get the first 10 fibonacci numbers using generator
def fibonacci_generator(nth):
    "Yield the first nth fibonacci number."
    n, m = 0, 1
    for _ in range(nth):
        yield m
        n, m = m, n+m

>>> f = fibonacci_generator(10)
>>> list(f)
[1, 1, 2, 3, 5, 8, 13, 21, 34, 55]
```


# 4. Generator in action
## Let's crack an algorithm questions using generators!

Foot notes
1. For instance, you can mix in the return statement,
but to keep it simple, (unless there's a good reason to do so,) let's not
mix them together. 

