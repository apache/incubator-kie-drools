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

Runtime Manager
===================

The code in these modules will be responsible for actual execution of models; most of the time this is represented by
class-loading and method invocation, but exceptions should be considered as well.

The code in `runtime-manager-api` should be the only one visible outside the `core` of the system, while the code
inside `runtime-manager-common` should be considered **private** and hidden from outside.