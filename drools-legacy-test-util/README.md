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

# drools-legacy-test-util

This module is created for a transition period when `drools-mvel` will be refactored/removed.

Most of tests in `drools-mvel` have been migrated to `test-compiler-integration` but some modules (e.g. `drools-serialization-protobuf`, `drools-persistence-jpa`) have a dependency to drools-mvel test-jar. It's not possible to have a dependency to `test-compiler-integration` because of a circular dependency. So we have moved such dependent classes and resources to a separated `drools-legacy-test-util`.

In a mid-and-long-term, we will brush up those related test cases and will likely be able to remove this `drools-legacy-test-util` module.
