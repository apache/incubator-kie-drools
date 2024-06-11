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

PMML Compiler
-------------

PMML Compiler uses Java SPI to retrieve AlgorithmImplementationProvider available at runtime.

To test it:

1. cd kie-pmml-compiler
2. mvn clean package
3. java -cp ./kie-pmml-commons/target/kie-pmml-commons-7.32.0-SNAPSHOT.jar:./kie-pmml-marshaller/target/kie-pmml-marshaller-7.32.0-SNAPSHOT.jar:./kie-pmml-compiler/target/kie-pmml-compiler-7.32.0-SNAPSHOT.jar org.kie.pmml.compiler.Main (No provider found expected)
4. java -cp ./kie-pmml-commons/target/kie-pmml-commons-7.32.0-SNAPSHOT.jar:./kie-pmml-marshaller/target/kie-pmml-marshaller-7.32.0-SNAPSHOT.jar:./kie-pmml-regression/target/kie-pmml-regression-7.32.0-SNAPSHOT.jar:./kie-pmml-compiler/target/kie-pmml-compiler-7.32.0-SNAPSHOT.jar org.kie.pmml.compiler.Main (Expected Regression provider found)
