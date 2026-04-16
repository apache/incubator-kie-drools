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

# 01 — Leaf-module source change

**Input:** a single `.java` file in `drools-beliefs`, a pure leaf module
(nothing in the reactor depends on it).

**What this tests:** the minimal cascade case. A leaf change must resolve
to exactly one module in `affected`, with `upstream` containing only the
module's own transitive reactor dependencies. No downstream propagation
should happen.

**Expected structure:**
- `affected` = `{org.drools:drools-beliefs}`
- `upstream` = transitive reactor deps of `drools-beliefs` (≈26 modules)
- `affected ∩ upstream = ∅`

**Why snapshot:** if `drools-beliefs` picks up any reactor-internal
downstream dependents (someone starts depending on it), the `affected` set
grows — the test breaks and forces that coupling to be reviewed.
