Title: Getting Started with Python Internals
Date: 2017-02-20
Category: Python
Tags: python, internals
Slug: getting-started-python-internals
Authors: Daw-Ran Liou
Summary: How to start exploring Python internals
Cover: images/python-logo.png

_This article is a summary of what I learned from Philip Guo's 
[CPython internals: A ten-hour codewalk through the Python interpreter source code](http://pgbovine.net/cpython-internals.htm).
I highly recommend you to go through his course. He go through great materials
in his videos. You can think of this article as a companion text version
of his course, so you can come back for your own references.
The only major difference between Philip Guo's course and this article is
the Python version. In his course he was using 2.7.8. This article I'm
using the 3.6.0 code.
In this article, you'll know the very basic things about Python internals,
and, hopefully, be able to explore the Python internals on your own._

# What does (C)Python do to my code?

You might have heard people tell you Python is a interepretive language.
You give the Python interpreter your sourcecode, and boom, the interpreter
spits out the output.

However, this might be a little bit confusing, but Python
__does compile your code__. Instead of the one step to get from the sourcecode
to the output, Python compiles your sourcecode into an easier format - bytecodes,
for the Python virtual machine. Then the interpreter program consumes the bytecode
and act on it. We'll be focusing on the later part where we get from bytecode
to the output.

Here's a simple diagram:

```
source code  |                (C)Python                   |       output
             |                                            |
  test.py   --->  compiler -> [bytecode] -> interpreter  ---> 'Hello World!'
                                   ^                                ^
                                   |                                |
                                   |                                |
                                   ----------------------------------
                                        This is more interesting
```

# How does the CPython project look like?

Main subdirectories in the CPython project:

1. `Include/` - all the .h (header) files where the interfaces are defined
1. `Objects/` - the C code that represents Python objects
1. `Python/` - the main Python runtime

Other subdirectories:

1. `Modules/` - built-in modules implemented in C
1. `Libs/` - standard libraries implemented in Python

# Python bytecode and the disassembler

Let's define our testing module:

```python
# test.py
x = 1
y = 2
z = x + y
print(z)
```

In Python's interactive interpreter, we can involk the built-in function:
[`compile`](https://docs.python.org/3/library/functions.html#compile), and
get the code object of this module.

```python
c = compile('test.py', 'test.py', 'exec')
# <code object <module> at 0x..., file "test.py", line 1>

c.co_code
# b'e\x00j\x01\x01\x00d\x00S\x00'

[byte for byte in c.co_code]
# [101, 0, 106, 1, 1, 0, 100, 0, 83, 0]
```

The above code is the bytecode representation of our original sourcecode.
However, looking at those numbers do not really help unless you are a
Python guru. Fortunately, in Python's standard library there's a module
called `dis`, which would translate those byte codes for us.

To disassemble the Python module, run `$ python3 -m dis test.py`:

```
  1           0 LOAD_CONST               0 (1)
              2 STORE_NAME               0 (x)

  2           4 LOAD_CONST               1 (2)
              6 STORE_NAME               1 (y)

  3           8 LOAD_NAME                0 (x)
             10 LOAD_NAME                1 (y)
             12 BINARY_ADD
             14 STORE_NAME               2 (z)

  4          16 LOAD_NAME                3 (print)
             18 LOAD_NAME                2 (z)
             20 CALL_FUNCTION            1
             22 POP_TOP
             24 LOAD_CONST               2 (None)
             26 RETURN_VALUE
```

The format for the disassmbled code is:
```
line_number_in_sourcecode -> byte_offset OP_CODE -> internal_book_keeping_stuff (argument_name)
```

The byte code is mapped to this disassembled code, somehow, with some
optimization. Thus, it's not very obvious to me how to decode each byte code
to which line of the disassembler output. So for now, we'll be sticking
with the more readable disassmbler output.

_If you are interested in how the disassembler works, module `dis` is located at `/Lib/dis.py`._

# Python is a Stack Machine

_If you could quite understand the disassembler output, feel free to skip this
section._

Before we jump into the disassembled code above, you need to know - Python
virtual machine is a "Stack Machine." Python internally keeps track of a
"Value Stack," which stores all the values for the upcoming operations to
use. If you still don't have any idea what a value stack is about, imaging Python
is a lazy guy who always grabs the top-most T-shirt from his drawer. When
he has more clean T-shirts, he simply stacks them on top of the other
T-shirts. This guy sometimes may grab an extra T-shirt to go to the gym,
sometimes may take several T-shirt for donation, or he may do any action
with his T-shirts, but he is very discipline about picking the top-most
T-shirt first. This guy is a Stack Machine.

Let's look at the disassembled code above. Without knowing the internal,
we could sort of guess what's happening in the Python virtual machine
already:

* When a const is loaded by calling `LOAD_CONST`, the
value is pushed onto the Value Stack.

* When `STORE_NAME` is called,
the last value on the Value Stack is popped and saved in the memory
associated with the variable name, `x` for example. the `0` in line
`3 STORE_NAME 0 (x)` indicates that name `x` is the 0th variable name.

* `LOAD_NAME` pushes whatever value the variable name is associated with
on top of the Value Stack. (Only the reference of the value is pushed.
So now the value has the Reference Counting of 2. One from the variable
name, another from the Value Stack.)

* `BINARY_ADD` pops the two values from the Value Stack and pushs the
result on top of the Value Stack again.

* `CALL_FUNCTION` calls the `print` function and use `z` as its argument.
(In legacy Python, or Python 2, `print` was a primitive operation and
has its own opcode.)

* For completeness, the module returns a `None` value.

_TODO add diagrams to help explaining each step_

# Python opcode

We sort of guess our way through the disassembled code. Let's see how they
are formally defined in CPython.

First look at `/Include/opcode.h`. This file defines all the Python opcodes,
which Python could act upon. For example, line 78:

```c
#define LOAD_CONST              100
```

This defines the opcode for `LOAD_CONST`.

> `#define` is the definition of a macro in C language. For example
  `#define LOAD_CONST 100`. This macro means to replace every
  occurrence of `LOAD_CONST` with `100`.

One interesting opcode to notice is in line 68, `HAVE_ARGUMENT`.
This opcode is just a placeholder. Any opcode include and above 90
takes argument(s).

## Main Interpreter Loop

Now let's see file `/Python/ceval.c`. Line 721 is the start of the gigantic
interpreter main function, which ends at line 3692:

```c
PyObject *
_PyEval_EvalFrameDefault(PyFrameObject *f, int throwflag)
{
...
} // line 3692
```

To understand this function:

* Return type - Everything in Python is an object, an `PyObject`
  object in CPython code.
* Input argument - A `PyFrameObject` is a piece of Python code.

One object worth noticing in this function is `PyObject **stack_pointer`
in line 727. This object is a list of pointers pointing to the Value Stack,
which we mentioned previously.

This is it, line 1108, the start of the main interpreter loop!
This is the start of the infinite loop to go through the bytecode.
This main interpreter loop ends at line 3614:
```c
    for (;;) {
        ...
    } /* main loop */  // Line 3614
```

Now see line 1220, a GIANT switch case that tells you what C code to operate
for each opcode case:
```c
        switch (opcode) {
            ...
```

Line 3528, breaking out of the main interpreter loop:
```c
fast_block_end:
        assert(why != WHY_NOT);
        ...
```

Line 3691, return of the main function:
```c
    return _Py_CheckFunctionResult(NULL, retval, "PyEval_EvalFrameEx");
}
```

And that is the structure of your CPython runtime! Now you know how
to look at the CPython code to figure out what's happening internally
when your code is executing. For example, you can locate the switch
case for `LOAD_CONST` in line 1244 to see what's happening when this
opcode is used:

```c
        TARGET(LOAD_CONST) {
            PyObject *value = GETITEM(consts, oparg);
            Py_INCREF(value);
            PUSH(value);
            FAST_DISPATCH();
        }
```

What it does in high level is:

1. Gets the value
1. Increase the reference count of the value by 1
1. Push the value on top of the Value Stack

# Recap

Let's recap some key points in this article:

1. Python does compile your sourcecode.
1. Use `$ python3 -m dis <YOUR_PYTHON_FILE>` to disassemble your code.
1. Python is a stack machine.
1. `/Include/opcode.h` has all the opcodes defined.
1. CPython runtime's main interpreter loop locates in `/Python/ceval.c`.
1. The main interpreter loop is a giant swith case in an infinite loop.
