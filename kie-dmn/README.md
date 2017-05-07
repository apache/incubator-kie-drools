Developing Drools and jBPM
==========================

**If you want to build or contribute to a kiegroup project, [read this document](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md).**

**It will save you and us a lot of time by setting up your development environment correctly.**
It solves all known pitfalls that can disrupt your development.
It also describes all guidelines, tips and tricks.
If you want your pull requests (or patches) to be merged into master, please respect those guidelines.

KIE DMN Implementation
======================

At the moment of this implementation, there is no TCK or official reference implementation for DMN v1.1.

While this implementation strives to be as compliant as possible with the specification, there are some differences 
listed here. These differences fall into 2 categories: (a) fixes to bugs in the spec, and (b) extensions to the
spec to better support use cases, without prejudice to strict models.

These lists are as comprehensive as possible, but the spec is an ambiguous document by itself and there
might be other differences that were not found yet. Feel free to point them out if you find any.

# FEEL language implementation notes

## a. Fixes to bugs and changes

1. __Space Sensitivity__: this implementation of the FEEL language is space *__insensitive__*. The goal is to avoid 
nondeterministic behavior based on the context and differences in behavior based on invisible characters (e.g., 
 white spaces). This means that for this implementation, a variable named `first name` with one space is exactly
 the same as `first  name` with two spaces in it. 

2. __List functions `or()` and `and()`__: the spec defines two list functions named `or()` and `and()`, but 
according to the FEEL grammar, these are not valid function names, as `and` and `or` are reserved keywords.
 This implementation renamed these functions to `any()` and `all()` respectively, in anticipation for DMN 1.2.
 
3. __Keyword `in` can not be used in variable names__: the spec defines that any keyword can be reused as part
 of a variable name, but the ambiguities caused with the `for ... in ... return` loop prevent the reuse of the `in` 
 keyword. All other keywords are supported as part of variable names.
 
4. __Support for date and time literals on ranges__: according to the grammar rules #8, #18, #19, #34 and #62, `date 
 and time` literals are supported in ranges (pages 110-111). Chapter 10.3.2.7 on page 114, on the other hand, contradicts
 the grammar and says they are not supported. This implementation chose to follow the grammar and support `date and 
 time` literals on ranges, as well as extend the specification to support any arbitrary expression (see extensions below).

5. __Invalid time syntax__: chapter 10.3.2.3.4 on page 112 and bullet point about `time` on page 131 both state that 
 the `time` string lexical representation follows the XML Schema Datatypes specification as well as ISO 8601. According 
 to the XML Schema specification (https://www.w3.org/TR/xmlschema-2/#time), the lexical representation of a time follows 
 the pattern `hh:mm:ss.sss` without any leading character. The DMN specification uses a leading "T" in several examples,
 that we understand is a typo a not in accordance with the standard.

## b. Extensions

1. __Support for scientific and hexadecimal notations__: this implementation supports scientific and hexadecimal
  notation for numbers. E.g.: 1.2e5 (scientific notation), 0xD5 (hexadecimal notation).
 
2. __Support for expressions as end points in ranges__: this implementation supports expressions as endpoints 
  for ranges. E.g: `[date("2016-11-24")..date("2016-11-27")]`

3. __Support for additional types__: the specification only defines the following as basic types of the language:

  * number
  * string
  * boolean
  * days and time duration
  * years and month duration
  * time
  * date and time

  For completeness and orthogonality, this implementation also supports the following types:
 
  * context 
  * list
  * range
  * function
  * unary test

4. __Support for unary tests__: for completeness and orthogonality, unary tests are supported
  as first class citizens in the language. They are effectively functions with an implicit single 
  parameter, and can be invoked in the same way as functions are. E.g.:

  ```
  {
      is minor : < 18,
      Bob is minor : is minor( bob.age )
  }
  ```

5. __Support for additional built-in functions__: additional functions are supported out of the box: 

  * `now()` : returns the current local date and time
  * `today()` : returns the current local date
  * `decision table()` : returns a decision table function. Although the spec mentions a decision table 
  function on page 114, it is not implementable as defined. 
  * `string( mask, p... )` : returns a string formatted as per the mask. See Java String.format() for 
  details on the mask syntax.
  
6. __Support for additional date and time arithmetics__: 

  * subtracting two dates will return a days and time duration with the number of days between the two 
  dates, ignoring daylight savings. E.g.: 
  ```
  date( "2017-05-12" ) - date( "2017-04-25" ) = duration( "P17D" )
  ```

# DMN engine implementation notes

## a. Fixes to bugs

1. __Support for types with spaces on names__: the DMN XML schema defines type refs as QNames, 
  but QNames do not allow spaces. It is then not possible to use types like FEEL `date and time`,
  `days and time duration` or `years and months duration`. This implementation does parse such
  typerefs as strings and allows type names with spaces, but in order to comply with the XML schema,
  it also added the following aliases to such types that can be used instead:

  ```
  "date and time" = "dateTime"
  "days and time duration" = "duration" or "dayTimeDuration" 
  "years and months duration" = "duration" or "yearMonthDuration" 
  ```
  
  Please note that for the "duration" types, the user can simply use `duration` and the engine will
  infer the proper duration, either `days and time duration` or `years and months duration`.

## b. Extensions

1. __Lists support heterogeneous element types__: at the moment, this implementation supports
  lists with heterogeneous element types. This is an experimental extension and does limit the 
  functionality of some functions and filters. We will re-evaluate this decision in the future.
  
2. __TypeRef link between Decision Tables and Item Definitions__: on decision tables/input clause,
  if no values list is defined, the engine automatically checks the type reference and apply the
  allowed values check if it is defined.