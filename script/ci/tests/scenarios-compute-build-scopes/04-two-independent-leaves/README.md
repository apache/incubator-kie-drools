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

# 04 — Two independent leaf modules changed

**Input:** one source file in `drools-beliefs` and one in `drools-fastutil`.
Both are leaves; neither depends on the other.

**What this tests:** the multi-module case with **no overlap** between the
two cascades. `affected` must be the set-union of each module's individual
cascade (here: just the two modules themselves, since both are leaves).
`upstream` must be the union of each module's transitive reactor deps,
with nothing added or double-counted.

**Expected structure:**
- `affected` = `{org.drools:drools-beliefs, org.drools:drools-fastutil}`
- `upstream` = union of both modules' transitive reactor deps
- `affected ∩ upstream = ∅`

**Why snapshot:** guards the union-logic against regressions that would
either miss one changed module or double-count its upstream.
