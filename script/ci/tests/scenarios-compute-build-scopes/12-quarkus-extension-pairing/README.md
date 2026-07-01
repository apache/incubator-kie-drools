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

Quarkus extension runtime / deployment pairing.

A change to `drools-quarkus-util-deployment` pulls in `drools-quarkus-deployment`
and `drools-quarkus-ruleunits-deployment` as affected (downstream Maven deps).
The synthetic Quarkus extension edge ensures the paired runtime modules
(`drools-quarkus`, `drools-quarkus-ruleunits`) also land in the affected set
rather than the upstream set, so that `extension-descriptor` can resolve the
deployment artifact within the same Maven reactor.
