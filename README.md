# FSM-Modulo

The aim of this project is to create a finite state machine library in Java.
The problem at hand is then to find the modulus of a number modulo some provided
base when the number is written in some base.

###Terminology:

Some useful terminology:
- Alphabet: the set of valid characters
  
- State: the state of a machine, with all the data carried by it
  this data tends to remain constant once an FSM is set up.
  
- Finite State Machine (FSM, also machine): a state-based automaton
  with a canonical alphabet that processes strings of characters from
  that alphabet sequentially by transitioning between states.
  The final state of the machine after a string has been processed
  provides the output of that machine from running that string.
  
- Transition table: a tabulation of the mapping from a state and
  character being processed to a new state
  
- Finality: whether the state is a valid final state.
  Otherwise, it results in `null`
  
- Result: the output of the machine, provided by the state.
  If the state is not final, the result is considered `null`.


## Structure

The project is in two parts: the library and the application,
each of which comes with its own host of tests. The tests are in
`tests` directory of the repo, organized in a similar structure
to what they test in the `src` directory, and they are run using JUnit.

### The library

The package `finite.automaton`, or the library, contains all the
generic  code to build a generic FSM, as well as interact with it.
This includes the `FSM` and `State` classes, as well as various
relevant exceptions.

### The application

The application, in package `app`, implements a generator of a
particular  class of FSM's parametrized by a base `b` and a modulo
`m`, both greater than 1. They are capable of resolving, for any
number `n`, as a string written base `b`, what `n` modulo `m` is. 
Note that `b` is restricted by `MAX_RADIX`, the limit of which bases
Java can use to transform numbers into strings. The main function,
which resides here, is for the specific case of `b = 2` and `m = 3`.

## Dependencies
 
This project runs on Java SDK 14.0.2, and uses JUnit 4.13.1 for testing.
It was compiled in IntelliJ, although any modern IDE with JUnit
support should work.
