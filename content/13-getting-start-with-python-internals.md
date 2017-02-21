Title: Getting Start with Python Internals
Date: 2017-02-20
Category: Python
Tags: python, internals
Slug: getting-start-python-internals
Authors: Daw-Ran Liou
Summary: How to start exploring Python internals
Cover:

_This article is a summary of what I learned from Philip Guo's 
[CPython internals: A ten-hour codewalk through the Python interpreter source code](http://pgbovine.net/cpython-internals.htm).
In this article, you'll know the very basic things to the Python internals,
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

To disassemble the Python module, run `$ python -m dis test.py`:

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
use them. If you still don't have any idea what I meant, imaging Python
is a guy who always picks the top-most T-shirt from his drawer. When
he has more clean T-shirts, he simply stacks them on top of the other
T-shirts. This guy sometimes may get a extra T-shirt to go to the gym,
sometimes may take several T-shirt for donation, or he may do any operation
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

Let's look at `opcode.h`

```c
#define LOAD_CONST	100	/* Index in const list */
```

Any opcode above 90 takes an argument

```c
#define HAVE_ARGUMENT	90	/* Opcodes from here have an argument: */
```

## Main Interpreter Loop

From `ceval.c` line 693 to line 3021, this is the main interpreter loop:

```c
PyObject *
PyEval_EvalFrameEx(PyFrameObject *f, int throwflag)
{
...
} // line 3021
```

* Everything in Python is an object, an `PyObject`.
* A `PyFrameObject` is a piece of code.

Inside the main interpreter loop:
* `PyObject **stack_pointer` is a list of pointers to the Value Stack.
It points to the Next free slot in value stack.

> `#define` is Macro in C. For example `#define LOAD_CONST 100`. This macro
means to replace every occurrence of `LOAD_CONST` with `100`.

Line 964, Infinite loop to go through the byte code:
```c
    for (;;) {
        ...
```

Line 1078, extract opcode:
```c
        /* Extract opcode and argument */

        opcode = NEXTOP();
        oparg = 0;   /* allows oparg to be stored in a register because
            it doesn't have to be remembered across a full loop */
```

Line 1112, GIANT switch case:
```c
        switch (opcode) {
            ...
```

Line 2959, breaking out of the main loop:
```c
        if (why != WHY_NOT)
            break;
        READ_TIMESTAMP(loop1);

    } /* main loop */
```

Line 3020, return retval
```c
    return retval;
}
```
