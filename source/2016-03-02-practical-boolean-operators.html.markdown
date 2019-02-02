---
title: Practical Uses of Python AND and OR Boolean Operators
date: 2016-03-02 10:20
tags: python
Slug: practicle-boolean-operators
Authors: Daw-Ran Liou
Summary: Why I love Python - 2
---

In my previous post — Python Truth Value Testing is Awesome, 
I mentioned about the sweet combo of the truth value testing and the boolean operators,
and gave an example for it using the or-operator. One of my colleagues gave me a comment
that the or-operator all seems legit, but the and-operator seems pretty anti-intuitive.
This raise the question in me: [is there any practical example of using the and-operator
which also takes the advantage of the generic truth value testing](http://stackoverflow.com/questions/35657101/practical-examples-of-python-and-operator)?
Here’s what I found.

# The Definitions

Let’s include [the definitions](https://docs.python.org/3.5/reference/expressions.html#boolean-operations)
again to make this post more comprehensive:

* The expression `x and y` first evaluates x; if x is false, its value is returned; otherwise, y is evaluated and the resulting value is returned.
* The expression `x or y` first evaluates x; if x is true, its value is returned; otherwise, y is evaluated and the resulting value is returned.

Yes indeed, at first glance neither of the operator seems intuitive compare to the
traditional boolean operator that only works for True and False as the operants.
Yet this is another thing I love about Python — the consistency. According to
[the Zen of Python](https://www.python.org/dev/peps/pep-0020/):

> Special cases aren't special enough to break the rules.

Go ahead and plug in the True/False value into the above definitions. I’m not kidding.
If you haven’t done that, do it now. See where this is going? They still give you the
right values the same as what we expected. This more generic form of the operations does
not break the rules. Now the million dollar question is, what do we do with these generic
boolean operators? Hear me out.

# Practical examples

Here’s my practical example for using both operators:

```python
my_shopping_list = []

next_item_to_buy = my_shopping_list and my_shopping_list.pop()
# >> next_item_to_buy = []

print(my_shopping_list or 'empty list')
# >> empty list

my_shopping_list = ['eggs', 'kales', 'apples']

next_item_to_buy = my_shopping_list and my_shopping_list.pop()
# >> next_item_to_buy = 'apples'

print(my_shopping_list or 'empty list')
# >> ['eggs', 'kales']
```

Also, thanks to [warvariuc](http://stackoverflow.com/users/248296/warvariuc)
on [Stackoverflow](http://stackoverflow.com/questions/35657101/practical-examples-of-python-and-operator/35662540#35662540)
to give an example for getting regex results:

```python
>>> import re

>>> match = re.search(r'\w(\d+)', 'test123')

>>> number = match and match.group(1)

>>> number
>>> '123'

>>> match = re.search(r'\w(\d+)', 'test')

>>> number = match and match.group(1)

>>> number
```

# OR is a fallback, AND is a guard
[This](http://stackoverflow.com/questions/4477850/python-and-or-operators-return-value/28321263#28321263)
is the best analogy I find.

Think of this way for the OR-operator: the second value serves as a __fallback__ value if something’s wrong
(does not exists, or have a False-y value) with the first.

On the other hand, for the AND-operator, the first value serves as a __guard__ for retrieving the second value.
If the first value failed the truth value testing, the second won’t be accessed. With this analogy,
when I look at the and keyword in the code above, I tend to read “and” as “and then…,” because there’s
this hidden truth value testing behind the scene.

This is such a nice short-hand, isn’t it? If you’re like me, a Python programmer transitioning from Java,
think about the fallback and the guard I talked about when you find yourself in the place to write if-else
code blocks just to do variable assignment. You might get your Ah-ha moment just like me while doing the
research for this article.
