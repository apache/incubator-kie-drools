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

Trusty PMML Developer Guide
===========================

_kie-pmml-trusty_ is a PMML engine implementing [PMML specs](http://dmg.org/pmml/v4-4-1/GeneralStructure.html).

The overall architectural design is inspired by "Clean architecture" principles.
More details on that may be read [here](https://blog.kie.org/2020/02/pmml-revisited.html).

This project is meant to be used as standalone-library, and clearly defines two lifecycles:

1) compilation: translation of PMML models (xml) to classes
2) evaluation: invocation of above classes with input data to retrieve final result

Some models may rely only on plain java code-generation, while others may use the drools engine.

There are multiple modules and submodules inside _kie-pmml-trusty_, but the most important are

1) kie-pmml-api: api commonly shared across al other modules
2) kie-pmml-compiler: code specific to compilation phase, shared by all models
3) kie-pmml-evaluator: code specific to evaluation phase, shared by all models
4) kie-pmml-models: contains model-specific submodules

For every model' module, there are the following submodules:

1) kie-pmml-models-(_modelName_)-model: defines the kie-pmml representation of the PMML model
2) kie-pmml-models-(_modelName_)-compiler: model-specific compilation code
3) kie-pmml-models-(_modelName_)-evaluator: model-specific evaluation code
4) kie-pmml-models-(_modelName_)-test: model-specific integration tests

Step-by-step guide
==================

1) identify the name of the model to be implemented from [PMML specs](http://dmg.org/pmml/v4-4-1/GeneralStructure.html)
2) choose the kind of implementation (plan java vs drools)
3) based on the above choice, follow the [Java](./kie-pmml-models-archetype/Readme.md) or the [Drools](./kie-pmml-models-drools-archetype/Readme.md) approach to create the base skeleton

Classes to be implemented:
1) kie-pmml-models-(_modelName_)-model:
    1. KiePMML(_modelName_)Model
    2. KiePMML(_modelName_)ModelWithSources
2) kie-pmml-models-(_modelName_)-compiler:
    1. (_modelName_)ModelImplementationProvider 
    2. KiePMML(_modelName_)ModelFactory
3) kie-pmml-models-(_modelName_)-evaluator:
   1. PMML(_modelName_)ModelEvaluator

For integration tests:
1) populate src/main/resources folder with actual _pmml_ files to be used for tests
2) for each of the above, create a testing class that extends _org.kie.pmml.models.tests.AbstractPMMLTest_


Implementation details
======================

Code generation is done using JavaParser library and is strongly based on templates.
Template files  are saved with the _.tmpl_ extension.
The preferred way to work with java generation is to use java methods to create/modify coe, and rely on plain string replacement/manipulation only for specific cases.
_kie-pmml-compiler/kie-pmml-compiler-commons_ module contains a lot of already written/tested functionalities, especially for code-generation.
When a new code-generation-related method is needed, please add it inside the above module, covering it with unit test.

PMML compilation may actually happen in two different phases:

1) during "kjar" creation (invoked by maven plugin) or during Kogito application creation (invoked by Kogito codegen)
2) at runtime, when a PMML file is loaded (e.g. inside Business Central)

In the former case a KiePMML(_modelName_)ModelWithSources has to be created/instantiated, and it will work as DTO. Client code (maven plugin/kogito codegen) will retrieve sources from it and compile/package them.
In the latter case, or even for runtime evaluation, a KiePMML(_modelName_)Model has to be created/instantiated, and it will be used directly for evaluation.

Latest [PMML specs](http://dmg.org/pmml/v4-4-1/GeneralStructure.html) may define new fields, or made some of them mandatory while they were optional previously.
To comply with that, a "normalization" phase is implemented inside _org.kie.pmml.compiler.commons.utils.KiePMMLUtil_.


For each model, a "Factory" class has to be generated. This factory represents the "entry point" for model evaluation.
At runtime, each model should live in its own "kieBase" context to avoid interference between them.

Service discovery is done with standard java SPI. Please check for _org.kie.pmml.compiler.api.provider.ModelImplementationProvider_ (compiler) and 
_org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator_ (evaluator) files if services are not found.

Java models
-----------

Code generation is made directly. Creates one or more abstract classes to contain as much code as possible, and code-generate actual implementations just to pass parameters.

Drools models
-------------

Such models require an additional step, i.e creation of rules files. To do that, AST files have to be generated and transformed in packages.
_kie-pmml-models/kie-pmml-models-drools/kie-pmml-models-drools-commons_ module contains a lot of already written/tested functionalities, especially for AST-generation.
When a new AST-generation-related method is needed, please add it inside the above module, covering it with unit test.
Since this models must live in different kiebases, but the user must not be concerned with that, i.e. he/she should not be forced to create a _kmodule.xml_ to specify the kiebase 
for each of them, even because we may choose to change the kiebase name policy.
To allow that, two additional classes are generated: _PMMLRuleMappersImpl_ and _PMMLRuleMapperImpl_: those are needed to load drools-rules at runtime, without the needs of _kmodule.xml_.

Good citizen policy
===================
Please keep updated [Release](./Release.md) and [CurrentLimitations](./CurrentLimitations.md) file while implementing new features or discovering missing ones.