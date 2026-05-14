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

# 11 — Fully removed subtree (no surviving ancestor short of repo root)

**Input:** three paths under a *non-existent* top-level directory
`totally-removed-top-level/` — including its `pom.xml`, a nested
sub-module `pom.xml`, and a Java file. None of the segments exist on
the HEAD filesystem.

**What this tests:** the walk-up logic in `CiComputeBuildScopes` when a
PR removes an entire top-level subtree, so there is no surviving
intermediate `pom.xml` between the deleted paths and the repo root.
Unlike scenario **07** — where the deleted module sits inside a
surviving aggregator (`drools-drl/`) — here the walk-up traverses every
non-existent ancestor and only finds a `pom.xml` at the repo root
itself.

**Expected structure:**
- `changed` collapses to the root reactor pom (`org.kie:drools-parent`),
  identical to scenario **03**'s changed set
- `affected` and `upstream` therefore equal scenario **03**'s
  (full reactor cascade from the root)
- Crucially, the script **does not crash** on the non-existent paths —
  it tolerates missing dirs/files during the walk-up and finds the
  root pom

**Why snapshot:** captures the current "fall through to repo root when
no surviving ancestor exists" behavior. If we later decide that fully
removed subtrees should map to *nothing* (so deleted files contribute
no scope on their own, leaving the parent-pom edits to drive cascade),
this test will fail and force an explicit decision via the goldens.

> **Note:** do not create a real `totally-removed-top-level/` directory
> in the repo — that would defeat the point of the scenario (every
> ancestor segment must be absent so the walk-up has to climb all the
> way to the root).
