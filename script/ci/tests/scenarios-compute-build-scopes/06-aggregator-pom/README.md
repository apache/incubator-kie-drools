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

# 06 — Aggregator `pom.xml` change

**Input:** `drools-drl/pom.xml` — the aggregator/parent pom for the DRL
sub-tree (`drools-drl-ast`, `drools-drl-extensions`, `drools-drl-parser`,
`drools-drl-parser-tests`).

**What this tests:** `CiComputeBuildScopes` does not itself do subtree
expansion for pom changes — it relies on **parent-pom edges** in the
reactor graph (emitted by `dep-graph-extractor`). This scenario verifies
those edges exist and cascade correctly: changing an intermediate
aggregator must pull every direct child (plus their transitive
downstream) into `affected`.

This is also the scenario that structurally covers the **"add a module"
case** — adding a new child module means editing this same aggregator's
pom (to register the `<module>` entry). The parent-pom edges will
cascade the change into the new child exactly as they do here.

**Expected structure:**
- `affected` includes `org.drools:drools-drl` and every direct child
  under `drools-drl/`
- `affected` also includes each child's transitive downstream
- `upstream` includes the aggregator's own transitive reactor deps, minus
  anything already in `affected`
- `affected ∩ upstream = ∅`

**Why snapshot:** if the parent-pom edges stop being emitted, or a child
module is moved out of this aggregator's reactor subtree, the expected
cascade changes and the test breaks.
