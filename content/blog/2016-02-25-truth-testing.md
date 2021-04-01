+++
title = "Python Truth Value Testing is Awesome"
slug = "truth-value-testing"
[taxonomies]
tags = ["python"]
+++

I’m constantly amazed by the simplicity and readability Python provides.
(See The [Zen of Python](https://www.python.org/dev/peps/pep-0020/).)
As the industry evolves, more and more codes written each day,
how to read code fast is a rising question every developer would face.
Imaging the times when you faced a new code base and need to ramp up the knowledge in
order to work with it. Lot’s of reading, isn’t it? This is why I think Python is a great
modern programming language. Let’s talk about one of the features Python provides —
the [truth value testing](https://docs.python.org/3.5/library/stdtypes.html#truth-value-testing).

<!-- more -->

(Go ahead and read the docs in Python’s official documentation.
You cannot avoid reading documentations while developing software.)

Basically the doc tells you that any object could be used for if-condition,
while-condition, or boolean operation. Let’s look at the following code:

```python
if 1:
    print('non-zero number is truthy!')
if [1]:
    print('non-empty list is truthy!')
if {'attr': 'value'}:
    print('non-empty dict is truthy!')
if 'a_string':
    print('non-empty string is truthy!')
if 0:
    # print('0 is not truthy!')
if '':
    # print('empty string is not truthy!')
if []:
    # print('empty list is not truthy!')
if {}:
    # print('empty dict is not truthy!')
```

This feature gives us a very consistent way of writing code. Unlike writing code in Java,
I don’t need to remember whether the return type is a null pointer or a boolean false when
calling a method. In Python, a simple truth testing would give me the answer I want,
because they both gives me a False.

The truth testing feature also makes a very sweet combo with the Boolean Operators. Consider the code below:

```python
ENV_VAR = os.environ.get(‘ENV_VAR’) or <default_value>
```

This code reads: get the `ENV_VAR` value from the OS’s environment, if can’t find it,
just use a default value. It looks pretty neat, isn’t it? Imaging this code written in Java:

```python
String ENV_VAR;
if (getEnvVar("ENV_VAR") != null):
    SECRET_KEY = getEnvVar("ENV_VAR");
else:
    SECRET_KEY = <default_value>;
```

Needless to say, lot’s of noise for my eyes when reading it.

Why this would work is because the Python or-operator does something slightly difference
than what we learn on the truth table. When evaluating the line `x or y`, what Python does is
to do `if x is false, then y, else x`. Again, the ‘if x is false’ statement wouldn’t work
without the truth value testing feature in Python.

