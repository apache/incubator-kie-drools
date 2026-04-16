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

# 02 — Hub-module source change

**Input:** a single `.java` file in `drools-core`, a central hub on which
most of the reactor transitively depends.

**What this tests:** the large-cascade case. A hub-module change must pull
its entire transitive downstream into `affected`, while its own intra-
reactor dependencies end up in `upstream`. The two sets are large but
still disjoint.

**Expected structure:**
- `affected` includes `org.drools:drools-core` plus ~135 downstream modules
- `upstream` includes `drools-core`'s ~12 intra-reactor dependencies
- `affected ∩ upstream = ∅`

**Why snapshot:** `drools-core`'s position in the graph is load-bearing
for build performance. If a module silently starts or stops depending on
`drools-core` (directly or transitively), the cascade shifts. The test
break draws a reviewer's attention to that.
