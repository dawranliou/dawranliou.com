---
title: Writing descriptors in Python 3.6+
date: 2017-09-23
Category: Python
tags: python
Slug: python36-descriptors
Authors: Daw-Ran Liou
Summary: How to write descriptors in Python 3.6 and beyond.
Cover: images/14-intercept.jpg
---

![alt intercep](images/14-intercept.jpg)
_American football players "blocking" the kick, not "intercepting."_

_Special thanks to [Luciano Ramalho](https://twitter.com/ramalhoorg).
I learned most of the knowledge about descriptors from his workshop in PyBay 2017_

Have you seen this code or maybe have written code like this?

```python
from sqlalchemy import Column, Integer, String
class User(Base):
    id = Column(Integer, primary_key=True)
    name = Column(String)
```

This code snippet partially comes from the tutorial of a popular ORM
package called [SQLAlchemy](http://docs.sqlalchemy.org/en/latest/orm/tutorial.html#declare-a-mapping).
If you ever wonder why the attributes `id` and `name` aren't passed
into the `__init__` method and bind to the instance like
[regular class does](https://docs.python.org/3/tutorial/classes.html#class-objects).
, this post is for you.

This post starts with explaining descriptors, why to use them, how to write
them in previous Python versions (<= 3.5,) and finally writing
them in Python 3.6 with the new feature described in [PEP 487 --
Simpler customisation of class creation](https://www.python.org/dev/peps/pep-0487/)

If you are in a hurry or you just want to know what's new, scroll
all the way down to the bottom of this article. You'll find the whole code.

# What are descriptors

A great definition of descriptor is explained by Raymond Hettinger in
[Descriptor HowTo Guide](https://docs.python.org/3.6/howto/descriptor.html#definition-and-introduction):

> In general, a descriptor is an object attribute with “binding behavior”,
one whose attribute access has been overridden by methods in the descriptor
protocol. Those methods are \_\_get\_\_(), \_\_set\_\_(), and
\_\_delete\_\_(). If any of those methods are defined for an object,
it is said to be a descriptor.

There are three ways to access an attribute. Let's say we have the `a`
attribute on the object `obj`:

1. To lookup its value, `some_variable = obj.a`,
1. To change its value, `obj.a = 'new value'`, or
1. To delete it, `del obj.a`

Python is dynamic and flexible to allow users intercept the above
expression/statement and bind behaviors to them.

# Why you want to use descriptors

Let's see an example:

```python
class Order:
    def __init__(self, name, price, quantity):
        self.name = name
        self.price = price
        self.quantity = quantity

    def total(self):
        return self.price * self.quantity

apple_order = Order('apple', 1, 10)
apple_order.total()
# 10
```

Despite the lack of proper documentation, there is a bug:

```python
apple_order.quantity = -10
apple_order.total
# -10, too good of a deal!
```

Instead of using getter and setter methods and break the APIs,
let's use property to enforce `quantity` be positive:

```python
class Order:
    def __init__(self, name, price, quantity):
        self._name = name
        self.price = price
        self._quantity = quantity  # (1)

    @property
    def quantity(self):
        return self._quantity

    @quantity.setter
    def quantity(self, value):
        if value < 0:
            raise ValueError('Cannot be negative.')
        self._quantity = value  # (2)
    ...

apple_order.quantity = -10
# ValueError: Cannot be negative
```

We transformed `quantity` from a simple attribute to a non-negative
property. Notice line `(1)` that the attribute are renamed to `_quantity`
to avoid line `(2)` getting a `RecursionError`.

Are we done? Hell no. We
forgot about the `price` attribute cannot be negative neither. It might
be attempting to just create another property for `price`, but remember
the DRY principle: when you find yourself doing the same thing twice,
it's a good sign to extract the reusable code. Also, in our example,
there might be more attributes need to be added into this class in the
future. Repeating the code isn't fun for the writer or the reader. Let's
see how to use descriptors to help us.

# How to write descriptors

With the descriptors in place, our new class definition would become:

```python
class Order:
    price = NonNegative('price')  # (3)
    quantity = NonNegative('quantity')

    def __init__(self, name, price, quantity):
        self._name = name
        self.price = price
        self.quantity = quantity

    def total(self):
        return self.price * self.quantity

apple_order = Order('apple', 1, 10)
apple_order.total()
# 10
apple_order.price = -10
# ValueError: Cannot be negative
apple_order.quantity = -10
# ValueError: Cannot be negative
```

Notice the class attributes defined before the `__init__` method? It's
a lot like the SQLAlchemy example showed on the very beginning of this
post. This is where we are heading. We need to define the `NonNegative`
class and implement the descriptor protocols. Here's how:

```python
class NonNegative:
    def __init__(self, name):
        self.name = name  # (4)
    def __get__(self, instance, owner):
        return instance.__dict__[self.name]  # (5)
    def __set__(self, instance, value):
        if value < 0:
            raise ValueError('Cannot be negative.')
        instance.__dict__[self.name] = value  # (6)
```

Line `(4)`: the `name` attribute is needed because when the `NonNegative`
object is created on line `(3)`, the assignment to attribute named `price`
hasn't happen yet. Thus, we need to explicitly pass the name `price` to the
initializer of the object to use as the key for the instance's `__dict__`.

Later, we'll see how in Python 3.6+ we can avoid the redundancy.

_The redundancy could be avoid in earlier versions of Python,
but I think this would take too much effort to explain
and is not the purpose of this post. Thus, not included._

Line `(5)` and `(6)`: instead of using builtin function `getattr` and
`setattr`, we need to reach into the `__dict__` object directly, because
the builtins would be intercepted by the descriptor protocols too and
cause the `RecursionError`.

# Welcome to Python 3.6+

We are still repeating ourself in line `(3)`. How do I get a cleaner API
to use such that we write:

```python
class Order:
    price = NonNegative()
    quantity = NonNegative()

    def __init__(self, name, price, quantity):
        ...
```

Let's look at the [new descriptor protocol](https://docs.python.org/3/reference/datamodel.html#object.__set_name__)
in Python 3.6:

* `object.__set_name__(self, owner, name)`
  * Called at the time the owning class owner is created. The descriptor
    has been assigned to name.

With this protocol, we could remove the `__init__` and bind the attribute
name to the descriptor:

```python
class NonNegative:
    ...
    def __set_name__(self, owner, name):
        self.name = name
```

To put all the codes together:

```python
class NonNegative:
    def __get__(self, instance, owner):
        return instance.__dict__[self.name]
    def __set__(self, instance, value):
        if value < 0:
            raise ValueError('Cannot be negative.')
        instance.__dict__[self.name] = value
    def __set_name__(self, owner, name):
        self.name = name

class Order:
    price = NonNegative()
    quantity = NonNegative()

    def __init__(self, name, price, quantity):
        self._name = name
        self.price = price
        self.quantity = quantity

    def total(self):
        return self.price * self.quantity

apple_order = Order('apple', 1, 10)
apple_order.total()
# 10
apple_order.price = -10
# ValueError: Cannot be negative
apple_order.quantity = -10
# ValueError: Cannot be negative
```

# Conclusion

Python is a general purpose programming language. I love that it
not only has very powerful features that are highly flexible and could possibly
bend the language tremendously (e.g. Meta Classes,) but also has high-level
APIs/protocols to serve 99% of the needs (e.g. Descriptors.) I believe there's
the right tool for the job. Descriptors are clearly the right tool
for binding behaviors to attributes. Although Meta Classes could potentially
do the same thing, Descriptor could solve the problem more
gracefully. It's also pleasing to see Python evolve for serving general people's
needs better.

Here's my conclusion:

1. Python 3.6 is by far the greatest Python.
1. Descriptors are used to bind behaviors to accessing attributes.
