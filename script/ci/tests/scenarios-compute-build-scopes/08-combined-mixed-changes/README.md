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

# 08 — Combined mixed changes

**Input:** a realistic multi-module PR combining several of the earlier
scenarios' building blocks in one changed-files list:
- an aggregator pom (`drools-drl/pom.xml`)
- a non-existent path under a removed sibling (simulated deletion)
- a leaf-module source (`drools-beliefs`)
- a hub-module source (`drools-core`)
- a downstream of that hub (`drools-kiesession`)

**What this tests:** end-to-end behavior when several of the interesting
cases interact in a single run:
- union across unrelated cascades (drools-drl subtree, drools-beliefs,
  drools-core) must be correct
- downstream subsumption still holds (`drools-kiesession` inside
  `drools-core`'s cascade)
- walk-up from deleted paths still resolves to the surviving aggregator
- `affected` and `upstream` remain disjoint under the full combination

**Expected structure:**
- `affected` = union of scenarios 06, 01, 02, 05 (minus duplicates)
- `upstream` = union of each above scenario's `upstream`, minus anything
  in the combined `affected`
- `affected ∩ upstream = ∅`

**Why snapshot:** catches interaction bugs that only surface when
multiple kinds of changes land together — exactly the shape of real PRs.
