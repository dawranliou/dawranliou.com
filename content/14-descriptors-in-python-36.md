# Writing descriptors in Python 3.6+

Have you seen this code or maybe have written code like this:

```python
from sqlalchemy import Column, Integer, String
class User(Base):
    id = Column(Integer, primary_key=True)
    name = Column(String)
```

This code snippet partially comes from the tutorial of a popular ORM
package called [SQLAlchemy](http://docs.sqlalchemy.org/en/latest/orm/tutorial.html#declare-a-mapping).
If you wonder why the attributes `id` and `name` aren't passed
into the `__init__` method and then bind to the instance like
[regular class does](https://docs.python.org/3/tutorial/classes.html#class-objects).
, this post is for you.

This post starts with explaining descriptors, when to use them, how to write
them in previous Python versions (<= 3.5,) and finally writing
them in Python 3.6 with the new feature described in [PEP 487 --
Simpler customisation of class creation](https://www.python.org/dev/peps/pep-0487/)

If you are in a hurry or you just want to know what's new, scroll
all the way down to the bottom of this article. You'll find the code
snippet in the section "The way in Python 3.6."

## What are descriptors

A great definition of descriptor is explained by Raymond Hettinger in
[Descriptor HowTo Guide](https://docs.python.org/3.6/howto/descriptor.html#definition-and-introduction):

> In general, a descriptor is an object attribute with “binding behavior”,
one whose attribute access has been overridden by methods in the descriptor
protocol. Those methods are \_\_get\_\_(), \_\_set\_\_(), and
\_\_delete\_\_(). If any of those methods are defined for an object,
it is said to be a descriptor.

There are three ways to access an attribute. Let's say we have the `a`
attribute on the object `obj`:

1. To lookup its value, (`some_variable = obj.a`,)
1. To change its value, (`obj.a = 'new value'`,) or
1. To delete it, (`del obj.a`)

Python is dynamic and flexible to allow user to bind behaviors while any
of the above expression/statement is invoked.

## Why you want to use descriptors

Let's see an example:

```python
class Order:
    def __init__(self, name, price, quantity):
        self._name = name
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
let's use property to enforce for positivity of `quantity`:

```python
class Order:
    def __init__(self, name, price, quantity):
        self._name = name
        self.price = price
        self._quantity = quantity

    @property
    def quantity(self):
        return self._quantity

    @quantity.setter
    def quantity(self, value):
        if value < 0:
            raise ValueError('Cannot be negative.')
        self._quantity = value
    ...

apple_order.quantity = -10
# ValueError: Cannot be negative
```

We transformed `quantity` from a simple attribute to a non-negative
property. Notice the external APIs are the same. Are we done? No, we
forgot about the `price` attribute cannot be negative neither. It might
be attempting to just create another property for `price`, but remember
the DRY principle: when you find yourself doing the same thing twice,
it's a good sign to extract the reusable code. Also, in our example,
there might be more attributes need to be added into this class in the
future. Repeating the code isn't fun for the writer or the reader. Let's
see how to use descriptors to help us.

## How to write descriptors

With the descriptors in place, our new class definition would become:

```python
class Order:
    price = NonNegative('price')
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
class and implement the descriptor protocals. Here's how:

```python
class NonNegative:
    def __init__(self, name):
        self.name = name
    def __get__(self, instance, owner):
        return instance.__dict__[self.name]
    def __set__(self, instance, value):
        if value < 0:
            raise ValueError('Cannot be negative.')
        instance.__dict__[self.name] = value
```

## The better way in Python 3.6

## Conclusion
