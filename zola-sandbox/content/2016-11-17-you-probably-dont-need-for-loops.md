+++
title = "You probably don't need for loops"
tags = ["python"]
slug = "never-write-for-loops"
cover = "http://critical-thinkers.com/wp-content/uploads/2015/01/shutterstock_208347706.jpg"
+++

_Originally titled "Never Write For-loops again"_

This is a challenge. I challenge you to avoid writing for-loops in every scenario.
Also, I challenge you to find the scenarios that are so freaking hard to write
anything else but a for-loop. Please share your findings. I’d like to hear about
them.

It’s been a while since I started exploring the amazing language features in
Python. At the beginning, it’s just a challenge I gave myself to practice using
more language features instead of those I learned from other programming
language. And things are just getting more fun! Not only the code become shorter
and cleaner, but also code looks more structured and disciplined. I’ll get into
those benefits more in this article.

<!-- more -->

But first, let’s take a step back and see what’s the intuition behind writing a
for-loop:

1. To go through a sequence to extract out some information
1. To generate another sequence out of the current sequence
1. This is my second nature to write for-loops because I’m a programmer

Fortunately, there are already great tools that are built into Python to help
you accomplish the goals! All you need is to shift your mind and look at the
things in a different angle.

# What you gain by not writing for-loops everywhere
1. Fewer lines of code
1. Better code readability
1. Leave indentation for managing context only

Let’s see the code skeleton below:

```Python
# 1
with ...:
    for ...:
        if ...:
            try:
            except:
        else:
```

In this example, we are dealing with multiple layers of code. THIS IS HARD TO
READ. The problem I found in this code is that it is mixing the
__administrative__ logic (the `with`, `try-except`) with the __business logic__
(the `for`, `if`) by giving them the indentation ubiquitously. If you are
disciplined about using indentation only for administrative logic, your core
business logic would stand out immediately.

> "Flat is better than nested" - The Zen of Python

# Tools you can use to avoid using for-loops

## 1. List Comprehension / Generator Expression

Let’s see a simple example. Basically you want to compile a sequence based on
another existing sequence:

```Python
result = []
for item in item_list:
    new_item = do_something_with(item)
    result.append(item)
```

You can use `map` if you love MapReduce, or, Python has List Comprehension:

```Python
result = [do_something_with(item) for item in item_list]
```

Similarly, if you wish to get a iterator only, you can use Generator Expression
with almost the same syntax. (How can you not love the consistency in Python?)

```Python
result = (do_something_with(item) for item in item_list)
```

## 2. Functions
Thinking in a higher-order, more functional programming way, if you want to map
a sequence to another, simply call the `map` function. (Be my guest to use list
comprehension here instead.)

```Python
doubled_list = map(lambda x: x * 2, old_list)
```

If you want to reduce a sequence into a single, use `reduce`

```Python
from functools import reduce
summation = reduce(lambda x, y: x + y, numbers)
```

Also, lots of Python's builtin functions consumes iterables:

```Python
>>> a = list(range(10))
>>> a
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
>>> all(a)
False
>>> any(a)
True
>>> max(a)
9
>>> min(a)
0
>>> list(filter(bool, a))
[1, 2, 3, 4, 5, 6, 7, 8, 9]
>>> set(a)
{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
>>> dict(zip(a,a))
{0: 0, 1: 1, 2: 2, 3: 3, 4: 4, 5: 5, 6: 6, 7: 7, 8: 8, 9: 9}
>>> sorted(a, reverse=True)
[9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
>>> str(a)
'[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]'
>>> sum(a)
45
```

## 3. Extract Functions or Generators
The above two methods are great to deal with simpler logic. How about more
complex logic? As a programmer, we write functions to abstract out the difficult
things. Same idea applies here. If you are writing this:

```Python
results = []
for item in item_list:
    # setups
    # condition
    # processing
    # calculation
    results.append(result)
```

Apparently you are giving too much responsibility to a single code block.
Instead, I propose you do:

```Python
def process_item(item):
    # setups
    # condition
    # processing
    # calculation
    return result

results = [process_item(item) for item in item_list]
```

How about nested for-loops?

```Python
results = []
for i in range(10):
    for j in range(i):
        results.append((i, j))
```

List Comprehension got your back:

```Python
results = [(i, j)
           for i in range(10)
           for j in range(i)]
```

How about if you have some internal state in the code block to
keep?

```Python
# finding the max prior to the current item
a = [3, 4, 6, 2, 1, 9, 0, 7, 5, 8]
results = []
current_max = 0
for i in a:
    current_max = max(i, current_max)
    results.append(current_max)

# results = [3, 4, 6, 6, 6, 9, 9, 9, 9, 9]
```

Let's extract a generator to achieve this:

```Python
def max_generator(numbers):
    current_max = 0
    for i in numbers:
        current_max = max(i, current_max)
        yield current_max

a = [3, 4, 6, 2, 1, 9, 0, 7, 5, 8]
results = list(max_generator(a))
```

> "Oh wait, you just used a for-loop in the generator function. That's cheating!"

Fine, smart ass, let's try the following.

## 4. Don't write it yourself. `itertools` got you covered

This module is simply brilliant. I believe this module covers 80% of the cases
that you makes you want to write for-loops. For example, the last example can be
rewritten to:

```Python
from itertools import accumulate
a = [3, 4, 6, 2, 1, 9, 0, 7, 5, 8]
resutls = list(accumulate(a, max))
```

Also, if you are iterating on combinatoric sequences, there are `product()`, 
`permutations()`, `combinations()` to use.

# Conclusion

1. You don’t need to write for-loops in most scenarios
1. You should avoid writing for-loops, so you have better code readability

# Action

1. Look at your code again. Spot any places that you wrote a for-loop previously
by intuition. Think again and see if it make sense to re-write it without using
for-loop.
1. Share your cases that are hard to code without using for-loops

_This [article](https://medium.com/@dawran6/never-write-for-loops-again-91a5a4c84baf) was originally posted in my [Medium blog](https://medium.com/@dawran6)_
