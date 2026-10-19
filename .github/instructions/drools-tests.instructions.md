---
applyTo: "drools-*/**/src/test/**/*.java,kie-ci/**/src/test/**/*.java"
---
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

# Drools Unit Test Code Review Guidelines

- In Drools unit tests, it is acceptable to omit `KieSession.dispose()`.
  Do not flag missing `dispose()` calls on `KieSession` in these tests.
  This is an intentional project convention because test sessions are
  typically short-lived and explicit disposal is generally unnecessary
  in this context (see apache/incubator-kie#7187).
- Tests that explicitly verify disposal behavior or resource release
  may still call `dispose()`. Do not flag these calls.