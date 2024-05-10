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

Setup MAC for Native image build
================================

The following instructions use sdkman as jvm manager. While it is not mandatory, it is recommended to ease jvm switching during development. 

1. install sdkman: `curl -s "https://get.sdkman.io" | bash` 
2. install brew: `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"`
3. manually install GraalVM (Note: check [Quarkus documentation](https://quarkus.io/guides/building-native-image#configuring-graalvm) to get current supported version): `brew install --cask graalvm/tap/(*current_graalvm_version*)`
4. link GraalVM inside sdkman: `sdk install java (*current_graalvm_version*)-grl /Library/Java/JavaVirtualMachines/(*current_graalvm_version*)/Contents/Home` 
5. go to directory containing code for native build
6. set sdkman to use GraalVM: `sdk use java (*current_graalvm_version*)-grl` 
7. issue build: `mvn clean package -Pnative`