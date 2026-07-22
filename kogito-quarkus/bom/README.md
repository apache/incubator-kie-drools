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

# Kogito Quarkus BOM

In this module you will find the `kogito-quarkus-bom`
BOM ([Bill of Materials](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms))
.

This BOM goal is to include all Kogito Core and Quarkus specific dependencies in one single file.

> **Note:** Users are **not** encouraged to use this BOM. Rather use [`kogito-bom`](../../kogito-bom) with the [Quarkus BOM]() and [Kogito Quarkus extensions](../extensions).

## Adding new dependencies

When a new dependency is needed only by Quarkus modules add it directly here instead
of [`kogito-dependencies-bom`](../../kogito-build/kogito-dependencies-bom).

Use the same approach you would for adding a new dependency to Kogito Build Parent
by [following our guidelines](../../CONTRIBUTING.md#requirements-for-dependencies).
