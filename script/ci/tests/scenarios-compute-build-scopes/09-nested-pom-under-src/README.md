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

# 09 — Nested `pom.xml` under `src/`

**Input:** a file inside
`kie-maven-plugin/src/it/kie-maven-plugin-test-kjar-3/` — a Maven
invoker fixture that is itself a Maven project (has its own `pom.xml`)
but is **not** part of the reactor.

**What this tests:** the walk-up-to-nearest-pom logic must skip any
`pom.xml` that lives under a `src/` folder (invoker fixtures under
`src/it/`, test projects under `src/test/resources/`, etc.). Those poms
exist on disk but aren't registered in the reactor, so the nearest
*reactor* pom is the enclosing module — `kie-maven-plugin` in this
case.

**Expected structure:**
- `changed` = `org.kie:kie-maven-plugin` (the enclosing reactor module,
  **not** the nested fixture pom)
- `affected` / `upstream` follow from `kie-maven-plugin`'s position in
  the reactor graph

**Why snapshot:** without the `src/` skip, the walk-up stops at the
fixture pom and `CiComputeBuildScopes` emits a
`warn: no maven project at …` plus an empty scope — silently causing
the build step to do nothing on PRs that only touch invoker fixtures.
