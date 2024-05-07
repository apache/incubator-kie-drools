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

Notes on compilation/runtime phases relationship
================================================

1. Common code/informations from compile time to runtime
    1. informations needed at runtime, common for all the models, must be put inside code-generated model-specific KiePMMLModel, as it currently happen for KiePMMLRegressionModel (e.g. Transformation dictionary)
    2. whenever possible, generated code should contain model-specific data, to limit as much as possible runtime computation (again, see KiePMMLRegressionModel)
2. General conventions
    1. for every PMML file there should be generated a package with generated classes for **all** the models contained in the PMML file   
    2. package name should be the sanitized package name (lowercase, no spaces) from file name of the original PMML
    3. for every PMML **model** there should be a generated class extending _KiePMMLModel_
    4. inside this package there also should be a _Factory_ class, whose name should be sanitized class name (first letter uppercase, no dot/spaces, etc) from file name of the original PMML
3. Compilation
    1. the PMMLCompiler should be invocable by  both the PMMLAssembler (at runtime) and the kie maven plugin (during kjar creation)
    2. for drools-related models, the PMMLCompiler should generate the java classes (currently, it stop at PackageDescr creation)
    3. for internal use, model' unique identifier (name) will be the full path of the pmml file
    4. for drools-related models, a "kmodule.xml" descriptor will be provided to identify kie-bases with specific model (one kie-base for each pmml file); name of such kie-bases will be the above unique identifier
    5. for drools-unrelated models, the PMMLCompiler should generate a Factory class to instantiate (at runtime) the other generated classes
4. Runtime
    1. Start time
        1. the PMMLAssembler should verify if, for any given PMML file, there are the corresponding _Factory_ classes
        2. if _Factory_ classes are found, the _getKiePMMLModels_ method should be invoked (and the result returned and put inside the _knowledgebase_)
        3. if generated classes are not found, the PMMLAssembler must invoke the PMMLCompiler to generate them (see 3)
    2. Evaluation time (on user input)
        1. Common
            1. the input data must contain the name of the model (see 3.iii) - full path of the pmml file) to be used as parameter to retrieve the model-specific entry point from the (generated) factory method (model-specific KiePMMLModel must be instantiated to retrieve informations created/stored at 1.i)
        2. Drools-implemented models
            1. the name of the model (4.ii.a.a) will represent the kie-base name (see 3.iv) and will be passed as parameter to KiePMMLSessionUtils.builder to instantiate a kie-session specific for such kie-base, so that rules from different pmml models does not get mixed;
