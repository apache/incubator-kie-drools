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

TreeModel implementation
========================

General plan for the drools implementation:

1. the compiler (the current module) is invoked at kjar generation (or during runtime for hot-loading of PMML file) (to define: should the assembler check for already existing classes before invoking the compiler? If so, how/if to manage "updated" classes ?)
1. the compiler read the PMML file and transform it to "descr" object (see  [BaseDescr](https://github.com/kiegroup/drools/blob/master/drools-compiler/src/main/java/org/drools/compiler/lang/descr/BaseDescr.java), [DescrFactory](https://github.com/kiegroup/drools/blob/master/drools-compiler/src/main/java/org/drools/compiler/lang/api/DescrFactory.java), [DescrBuilderTest](https://github.com/kiegroup/drools/blob/master/drools-compiler/src/test/java/org/drools/compiler/lang/api/DescrBuilderTest.java) )
1. regardless of how the compiler is invoked, the drools compiler must be invoked soon after it to have java-class generated based on the descr object
1. the runtime assembler put the generated classes in the kie base
1. the runtime executor load the "drools-model" generated and invoke it with the input parameters

DRL details
-----------

1. for each field in the DataDictionary a specific DataType has to be defined
1. for each branch/leaf of the tree a full-path rule has to be generated (i.e. a rule with the path to get to it - e.g. "sunny", "sunny-temperature", "sunny-temperature-humidity")
1. a "status-holder" object is created and contains the value of the rule fired - changing that value will fire the children branch/leaf rules matching it (e.g. the rule "sunny" will fire "sunny-temperature" that - in turns - will fire "sunny-temperature-humidity")
1. such "status-holder" *may* contain informations/partial result of evaluation, to be eventually used where combination of results is needed
1. missing value strategy *may* be implemented inside the status holder or as exploded rules