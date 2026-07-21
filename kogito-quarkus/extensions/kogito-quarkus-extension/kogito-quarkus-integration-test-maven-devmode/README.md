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

# KOGITO-4499 Hot reload false negative instrumentation for DecisionModels

The DecisionModels is meant to be reloaded, but it would seems that for some reasons the quarkus dev mode hotreload instrumentation thinks the class is equivalent hence not reloaded.

The problem can be reproduced only using DevMojo maven invoker style, not with ShrinkWrap.

This is a simple module which would test maven invoker style per the linked DevMojoIT, as suggested here https://quarkusio.zulipchat.com/#narrow/stream/187038-dev/topic/Failed.20to.20replace.20classes.20via.20instrumentation/near/225045416

The second change of the .dmn resource fails, and I believe this might be a good demonstration of how the Kogito hot reload is currently broken.

The test prepares a basic Kogito-on-Quarkus maven app with a simple DMN, containing a decision which prefix "Hello, " the name given in input.
The first http call works as expected, the returned value is "Hello, v1".
The test then mutates the content of the dmn, now the decision should prefix "Ciao, ".
This second http call works again as expected, the returned value is "Ciao, v1".
The test then mutates again the content of the dmn, now it is expecting to prefix "Bonjour, ".
As can be seen in the log, attached, this fails, since the Ciao-prefix is still occuring.
build-project-intrumentation-reload-dmn.log

This module and test demonstrate the fix for KOGITO-4499.
