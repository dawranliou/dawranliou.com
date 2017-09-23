# Writing descriptors in Python 3.6+

This post goes from explaining descriptors, when to use them, how to write
them in previous Python versions (<= 3.5,) and finally how to
write them in Python 3.6 with the new feature described in [PEP 487 --
Simpler customisation of class creation](https://www.python.org/dev/peps/pep-0487/)

If you are in a hurry or you just want to know what's new, you may scroll
all the way down to the bottom of this article. You'll find the code
snippet in the section "The better way in Python 3.6."

## What are descriptors

The definition of descriptor is explained by Raymond Hettinger in
[Descriptor HowTo Guide](https://docs.python.org/3.6/howto/descriptor.html#definition-and-introduction):

> In general, a descriptor is an object attribute with “binding behavior”,
one whose attribute access has been overridden by methods in the descriptor
protocol. Those methods are \_\_get\_\_(), \_\_set\_\_(), and
\_\_delete\_\_(). If any of those methods are defined for an object,
it is said to be a descriptor.

## Why you want to use descriptors

## How to write them in Python 3.5

## The better way in Python 3.6

## Conclusion
