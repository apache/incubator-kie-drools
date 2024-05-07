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

# Mvel Compiler

This is a compiler that translates from MVEL source code to Java. 
It's used mostly in consequences that use `dialect "MVEL"`

It's also used to preprocess Java consequences that use `modify` and `with` blocks (see [ModifyCompilerTest](src/test/java/org/drools/mvelcompiler/ModifyCompilerTest.java)).
This assumes that every Java consequence that contains a `modify` could be parsed by the `mvel.jj` grammar in `drools-mvel-parser` module.

Transformation rules are described in the [MvelCompilerTest](src/test/java/org/drools/mvelcompiler/MvelCompilerTest.java)

The idea of the algorithm is split into three phases

1. Preprocess `modify` and `with` statements and transform that to java-like (see [PreprocessPhase](src/main/java/org/drools/mvelcompiler/PreprocessPhase.java))
2. Call the Statement Visitor that will ensure that every child statement is processed accordingly   
2. For each ExpressionStatement type the expression and transform accessors to getter in [RHSPhase](src/main/java/org/drools/mvelcompiler/RHSPhase.java)
3. Use the expression type to set the type of the undeclared variables in an assignment expression and transform field accessor 
to setters in [LHSPhase](src/main/java/org/drools/mvelcompiler/LHSPhase.java)
4. Reprocess the RHS in [ReProcessRHSPHase](src/main/java/org/drools/mvelcompiler/ReProcessRHSPhase.java) to further coerce BigDecimals

The first phase consists of a type inference algorithm that uses reflection to type check the fields and methods and transform to valid accessor.
The `.class` files have to be present in the classloader before running the mvel-compiler. If not, this algorithm will fail.

For example this MVEL expression

```java

person.name;

```

when name is a private field gets transformed into

```java

person.getName();

```

If `.name` is a public field, the expression is left as it is.