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

## drools-test-coverage-jars
This project is to build jars that are used in `drools-test-coverage` project. So we can avoid having jar binaries in the codebase.

### How to add a jar project
If the jar is not a kjar, you can simply add the jar project under this project. `surf` project is an example.

If the jar has a fixed version while requires the current version for dependency or plugin (e.g. jar version is `1.0.0`, but requires `999-SNAPSHOT` kie-maven-plugin to build the kjar), use `drools-test-coverage-jars-with-invoker` to build the jar with maven-invoker-plugin. Place the jar project under `src/it`. `kie-poject-simple` is an example.

In both cases, you would need to copy the jar file to the target test project using `copy-rename-maven-plugin`.