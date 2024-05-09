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
Compilation Manager
===================

The code in these modules will be responsible for transformations of original model to executable representation; most
of the time this is represented by code-generation and class-compilation, but exceptions should be considered as well.

The code in `compilation-manager-api` should be the only one visible outside the `core` of the system, while the code
inside `compilation-manager-common` should be considered **private** and hidden from outside.

Result of *engine-plugin compilation* may be code-generated and compiled classes (most of the time), but it also could
be an *intermediate* artifact that could be further processed by another engine. So, both ***original models*** (e.g.
DRL, DMN) and ***intermediate artifacts*** will be managed as ***Resources***.
Each engine' plugin should declare which *resources* it is able to process/manage.

The overall flow will be that any given **Resource provider** would submit resource to the `compilation-manager` that,
in turn, will look for the matching engine to process it.

A *resource provider* could be the `kie-maven-plugin`, the `kogito` build, but also an *engine-plugin* that output an *
intermediate artifact* at compile time, or an on-the-fly compilation (at runtime).
It is so possible that a chain of *resource providers* will be involved (where one or more engine plugin creates
intermediate artifacts).
To consistently close the loop, the starting *resource provider* will be considered the consumer of *compiled classes*.

