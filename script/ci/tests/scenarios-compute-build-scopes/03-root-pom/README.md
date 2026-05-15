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

# 03 — Root `pom.xml` change

**Input:** the repository-root `pom.xml`.

**What this tests:** root-pom changes must cascade to the entire reactor
via the parent-pom edges emitted by the dep-graph-extractor. Every
reactor module declares the root (directly or transitively) as its
parent, so modifying the root rebuilds everything.

**Expected structure:**
- `affected` = every reactor module (≈255)
- `upstream` is empty (the root has no intra-reactor dependencies)
- `affected ∩ upstream = ∅`

**Why snapshot:** the count is a coarse integrity check for parent-pom
edge emission. If the dep-graph-extractor ever stops emitting parent edges, or a
module's parent is moved out of the reactor, `affected` shrinks below
the reactor size and the test breaks.
