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

# drools-mvel

## Goal

The purpose of this module is isolating all the part of drools requiring mvel and/or asm. The main reason for this refactor
is that the excutable model (and then also Kogito which relies on it) doesn't need them but it is still indirectly depending
on them through drools-core and drools-compiler.

Note that this is unrelated with the dialect in use. Going through "traditional" drl compilation, both java and mvel dialect heavily
relies on mvel for parsing and asm for bytecode generation and in both cases you'll need to have drools-mvel in your classpath. 

Regarding the executable model it is supposed to work fine (with a few minor exceptions) without this module. In the very 
edge cases when this is not true, like for instance when using the legacy accumulate with inline functions, which is 
deprecated anyway, a RuntimeException clearly reporting the problem is thrown. It's planned to also cover without mvel 
these missing edge cases in future, but for now readding drools-mvel to the classpath when necessary solves this problem.

## Design

All classes in drools-core and drools-compiler having a dependency on mvel and/or asm have been moved into this module. 

The points where core or compiler needs to interact with those mvel dependending classes (this never happens when using
the executable model) have been gathered into specific interfaces. This interfaces are only implemented in drools-mvel and,
when this module is present on the classpath, their implementations are loaded through the usual drools service discovery
mechanism. All these interfaces and their implementation are then defined in the `META-INF/kie.conf` of this module.

When the execution requires one of these implementations but drools-mvel is not present on the classpath (for instance 
because an user miseed to add it even if using the "traditional" drl compilation) a `RuntimeException` clearly explaining 
the problem is thrown. This mechanism, together with the exception to be thrown, is defined in `org.drools.core.base.CoreComponentsBuilder` (one of the interfaces 
implemented in drools-mvel) and reused in all other places where this check is necessary.  

In some specific cases it is needed to perform different actions depending on whether drools-mvel is present or not on 
the classpath or not. This test is performed by the `org.drools.core.util.Drools.hasMvel()` method which also check if the
implementation of `org.drools.core.base.CoreComponentsBuilder` is available. This is used for instance in core, to skip
initializations that are not necessary when not using mvel, and in the executable model, to confine and manage the features
mentioned above that are still not available without mvel.