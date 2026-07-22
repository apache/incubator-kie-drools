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

# Kogito Codegen Manager

The aim of this module is to be consumed by a build automation and project management tool such as Maven or Gradle.

This module manages all the required logic to actually execute the KIE assets Generators defined in the other `kogito-codegen-*`
, togheter with the glue code that perform the following actions:
- It generates the CodeGen classes based on the given Apache KIE assets (BPMN, DMN, DRL, PMML), using its specific Generator;
- It writes the CodeGen classes;
- It compiles the CodeGen classes, based on the Java version used in the user Apache KIE project
- It writes the compiled CodeGen classes;

