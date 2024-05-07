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
DRL Engine
==========

The rule(DRL) engine feature different use cases/code paths.

As per the compilation phase, currently only the `ExecutableModel` is implemented, and relevant code is present in `drl-engine-compilation-common` (to be inherited by all drl-engine implementations)

As per the runtime phase, there are at least four different scenarios:

1. direct usage of kiesession in local mode -> `drl-engine-kiesession-local`
2. usage of kiesession (via proxy) in remote mode -> to be implemented
3. usage of map of objects (for inter-engine communications)
4. ruleunit usage (e.g. for Rest endpoints ?)


At Runtime, the `FRI` should univocal define
1. the kind of runtime
2. (eventually) the session id

E.g.
1. at compile time a given `/drl/something` resource is compiled
2. at runtime, the `/drl/kiesessionlocal/something` will be asked for
3. `kiesessionlocal` would be the submodule identifier, and the specific implementation will return `true` on `canManage` method
