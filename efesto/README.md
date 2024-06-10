<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# Efesto
-------------------------

This module contains a rewriting of the `drools` framework, that is intended to be used to provide clear separation and composition for each compiler/runtime of the different engines that Drools project contains (Rules engine, DMN engine, PMML engine).
The first step has been to include `efesto` as self-contained module inside `drools`.


The main focus of this project is on:

1. clear separation between "container" and "content" contexts; the "content" is represented by functionalities exposed
   by the compile-time and runtime; the "context" is the environment in which such functionalities are invoked (
   see [architectural choices](https://docs.google.com/document/d/1n9rKcMh0qnP7R4DUb3xqanFZcN0q7SL8aBRoAdQDSH0) for more
   details)
2. clear separation between compile-time and runtime
3. uniform overall management of different models/engines

Regarding the overall module organization/relationship, this project should follow the guidelines of the "Clean
Architecture" principle.
As per the design style, the microkernel-style will be featured, where each model/engine will represent a
specific `plugin`.

Each plugin should provide a `compilation` and a `runtime` component.

The [documentation](./documentation) folder contains diagrams (PUML, Archimate), images, and other resources related to
design/architectural choices.

This effort can not be full-filled by a single person, since it involve decisions at every level and it will have an
impact on the all `drools` codebase; everyone may be interested would be warmly welcomed for ideas, suggestions,
criticism, contribution.


ArchUnit
========
ArchUnit has been introduced to enforce (as much as possible) some general architectural and design guidelines.

See [official documentation](https://www.archunit.org/userguide/html/000_Index.html) and [maven plugin information](https://github.com/societe-generale/arch-unit-maven-plugin) for further details.

JDepend
=======
JDepend is a tool that traverses Java class file directories and generates design quality metrics for each Java package. JDepend allows you to automatically measure the quality of a design in terms of its extensibility, reusability, and maintainability to manage package dependencies effectively.
See [official documentation](http://clarkware.com/software/JDepend.html">http://clarkware.com/software/JDepend.html)
for details.






