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

Efesto Developer Guide
======================

Introduction
------------
Efesto framework feature a plugin style following the "Clean Architecture" principles.

This, basically means that:
1. there are *core* components (defined in **efesto-core** module) and *peripheral ones* (the model-specific **plugins**)
2. the *core* components must have no kind of *knowledge*, of any kind, of the *peripheral* ones; i.e. the *core* components should not have any kind of reference, even a name or string, to something that is specific to a *plugin*
3. on the other side, *plugins* implements, uses, and extends classes define in the *core* components
4. the *plugins* must have no direct dependency between them; i.e. if one *plugin* needs another one to correctly work, they should use some ***common, not plugin-specific***, interface to exchange information; and this exchange must be mediated by the *core* components; it is responsibility of the *environment* to provide all the required dependencies (i.e. *maven*)

Those simple guidelines are needed to maintain the good shape of the framework, preventing it to become a *de-facto* monolith.

Api
---

Efesto framework is based on the clear separation of two components: *compilation* and *runtime*.
The first is responsible to perform all the action necessary to take a model definition and make it *executable*, like validate, instantiate objects or code-generate classes (this definition may change on a plugin-in base).
The latter is responsible to use the above *executable* to evaluate responses based on *user* input.

The *compilation* part is defined by the **CompilationManager**, whose work is to
1. find a *plugin* able to *process* a given input
2. retrieve the *processed* artifacts from the plugin
3. returns an **IndexFile** with the information of the created elements

The *runtime* part is defined by the **RuntimeManager**, whose work is to
1. find a *plugin* able to *process* a given input
2. retrieve the *result* from the plugin
3. return the *result* to the client code

### IndexFile
The result of a compilation is listed inside a *model-specific* **IndexFile**. For each model (e.g. *drl*, *dmn*, *pmml*, etc) there should be at most one of such file. The file name format is
`IndexFile.{model}_json` (e.g. *IndexFile.drl_json*, *IndexFile.dmn_json*, *IndexFile.pmml_json*).

This file contains:

1. all the fully qualified names of generated classes
2. Identifier definition, i.e. the unique identifier of the generated *executable*, to be used at runtime.

Currently, this file is written on the `target` directory, but this may be overridden using the `indexfile.directory` *system property*.

## Plugins

The plugins, in turns, are composed on a *compilation* component and a *runtime* component.
To maintain clear dependency separation, those two components should be in separated modules.
If a common class is needed by both, it should be put in a third module. In that case, this third module should not depend on any *core* components, because otherwise it would create an indirect binding between all the components.
The plugin should implement **org.kie.efesto.compilationmanager.api.service.KieCompilerService** and/or **org.kie.efesto.runtimemanager.api.KieRuntimeService**, that are discovered at execution with SPI.

### Steps
1. create a **pom** container module
2. inside container module, create a **jar** compilation module
3. inside container module, create a **jar** runtime module
4. if needed, inside container module, create a **jar** api module

*It is also possible to create multiple **jar** compilation modules and/or **jar** runtime modules: as example, see the `kie-drl` structure*.

### Compilation module

1. create an implementation of **org.kie.efesto.compilationmanager.api.service.KieCompilerService**
2. inside `src/main/resources/META-INF/services`, write the `org.kie.efesto.compilationmanager.api.service.KieCompilerService` file, with the full class name of the implementing class

There are two methods to implement:
1. `<T extends EfestoResource> boolean canManageResource(T toProcess);`
2. `<T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, EfestoCompilationContext context);`

The first method is used by the framework to know if that specific plugin is able to process a given *resource*. For the moment being, as a design decision, there must be at most one plugin that returns `true` for any given resource instance.
The expression *given resource instance* means that the same *kind* of resource may be managed by different plugins, but there is some further identifier based on which only one plugin can manage a specific resource.
An example of this is the `EfestoFileResource`, that is simply a `File` wrapper. Most/all plugins may be able to manage an `EfestoFileResource`, but this class has also a `getModelType()` method, based on which every plugin may "decide" if it can manage it or not.

There is an enforcing check that eventually throws an `Exception` when multiple plugins returns `true`, but developers are the first responsible for that.
The logic behind this boolean is based on the actual type of the resource, on the identifier of the resource, and on other plugin-specific logic.
The second method is responsible to actually process the resource.
The `T` parameter is actually an `org.kie.efesto.compilationmanager.api.model.EfestoResource`.
The `E` parameter is actually an `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput`.

Beside implementing those two methods, there are no strict rules to follow. For example, it is possible that one single engine is able to manage different format of inputs (e.g. the rule engine). In this case, it is possible to create one single implementation to the different kind of inputs, or it is possible to create one implementation for every kind of input (in which case, anyone of them should be defined in the `org.kie.efesto.compilationmanager.api.service.KieCompilerService` SPI file).

#### EfestoResource

The `org.kie.efesto.compilationmanager.api.model.EfestoResource` contains the data to be processed.
The [org.kie.efesto.compilationmanager.api.model](efesto-core/efesto-compilation-manager/efesto-compilation-manager-api/src/main/java/org/kie/efesto/compilationmanager/api/model) package contains all the *default* `EfestoResource` implementations.

Every plugin could create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoResource` to fullfill its goals; e.g. for differentiate the different kind of inputs.

For the moment being, the *container* project (i.e., the project that **uses** the efesto framework) is responsible to instantiate the *resource*. 
Following iterations will provide helper classes for *resource* instantiation.

#### EfestoCompilationOutput

The `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput` contains the results of the processing.
Every plugin could create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput` to fullfill its goals; e.g. to create a "redirection" resource, i.e. a "compilation output" that it is *also* a resource to be processed by another plugin, as for `DrlPackageDescrSetResource`. In case of code-generation, it is possible that `EfestoCallableOutputClassesContainer` would be enough to use.

The [org.kie.efesto.compilationmanager.api.model](efesto-core/efesto-compilation-manager/efesto-compilation-manager-api/src/main/java/org/kie/efesto/compilationmanager/api/model) package contains all the *default* `EfestoCompilationOutput` implementations.


#### EfestoCompilationContext
The `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext` define the environment to use for the processing and requires a `Classloader` as building parameter; `EfestoCompilationContext` embed a `ClassLoader` that wrap the one provided for instantiation. Since the most common use case is to do code-generation, that embedded **classloader** is capable of dynamic class loading, and is responsible to class compilation and classloader manipulation; it should not be exposed publicly to avoid uncontrolled and unforeseeable behaviors.

The [org.kie.efesto.compilationmanager.api.model](efesto-core/efesto-compilation-manager/efesto-compilation-manager-api/src/main/java/org/kie/efesto/compilationmanager/api/model) package contains the *default* `EfestoCompilationContextImpl` implementation.
Every plugin could also create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext`; e.g. `DrlCompilationContext` subclass it to also use `KnowledgeBuilderConfiguration`.
This subclassing activity is important to avoid that plugin-specific needs leaks out of the plugin boundary.

For the moment being, the *container* project (i.e., the project that **uses** the efesto framework) is responsible to instantiate the *context* (providing the required classloader).
Following iterations will provide helper classes for *context* instantiation.

### Runtime module

1. create an implementation of **org.kie.efesto.runtimemanager.api.KieRuntimeService**
2. inside `src/main/resources/META-INF/services`, write the `org.kie.efesto.runtimemanager.api.service.KieRuntimeService` file, with the full class name of the implementing class

There are two methods to implement:
1. `boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context);`
2. `Optional<E> evaluateInput(T toEvaluate, EfestoRuntimeContext context);`

The first method is used by the framework to know if that specific plugin is able to evaluate a given *input*. For the moment being, as a design decision, there must be at most one plugin that returns `true` for any given input.
The expression *given input* means that the same *kind* of input may be managed by different plugins, but there is some further identifier based on which only one plugin can manage a specific input.

There is an enforcing check that eventually throws an `Exception` when multiple plugins returns `true`, but developers are the first responsible for that.
The logic behind this boolean is based on the actual type of the input, on the identifier of the input, and on other plugin-specific logic.
The second method is responsible to actually evalute the input.
The `T` parameter is actually an `org.kie.efesto.runtimemanager.api.model.EfestoInput`
The `E` parameter is actually an `org.kie.efesto.runtimemanager.api.model.EfestoOutput`

Beside implementing those two methods, there are no strict rules to follow. For example, it is possible that one single engine is able to evaluate different format of inputs (e.g. the rule engine). In this case, it is possible to create one single implementation to the different kind of inputs, or it is possible to create one implementation for every kind of input (in which case, anyone of them should be defined in the `org.kie.efesto.runtimemanager.api.service.KieRuntimeService` SPI file).

#### EfestoInput

The `org.kie.efesto.runtimemanager.api.model.EfestoInput` contains the data to be evaluated.
Every plugin could create its own subclass of `org.kie.efesto.runtimemanager.api.model.EfestoInput` to fullfill its goals; e.g. for differentiate the different kind of inputs.

The [org.kie.efesto.runtimemanager.api.model](efesto-core/efesto-runtime-manager/efesto-runtime-manager-api/src/main/java/org/kie/efesto/runtimemanager/api/model) package contains all the *default* `EfestoInput` implementations.

For the moment being, the *container* project (i.e., the project that **uses** the efesto framework) is responsible to instantiate the *input*.
Following iterations will provide helper classes for *input* instantiation.

#### EfestoOutput

The `org.kie.efesto.runtimemanager.api.model.EfestoOutput` contains the result of the evaluation.
Every plugin should create its own subclass of `org.kie.efesto.runtimemanager.api.model.EfestoOutput` to fullfill its goals; e.g. to provide better typed kind of outputs.

The [org.kie.efesto.runtimemanager.api.model](efesto-core/efesto-runtime-manager/efesto-runtime-manager-api/src/main/java/org/kie/efesto/runtimemanager/api/model) package contains the `EfestoOutput` definition and the **default** abstract `AbstractEfestoOutput` implementation.


#### EfestoRuntimeContext

he `org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext` define the environment to use for the evaluation and requires a `Classloader` as building parameter; `EfestoRuntimeContext` embed a `ClassLoader` that wrap the one provided for instantiation. Since the most common use case is to do code-generation, that embedded **classloader** is capable of dynamic class loading, and is responsible to class compilation and classloader manipulation; it should not be exposed publicly to avoid uncontrolled and unforeseeable behaviors. 

The [org.kie.efesto.runtimemanager.api.model](efesto-core/efesto-runtime-manager/efesto-runtime-manager-api/src/main/java/org/kie/efesto/runtimemanager/api/model) package contains the *default* `EfestoRuntimeContextImpl` implementation.
Every plugin could also create its own subclass of `org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextImpl`.
This subclassing activity is important to avoid that plugin-specific needs leaks out of the plugin boundary.

For the moment being, the *container* project (i.e., the project that **uses** the efesto framework) is responsible to instantiate the *context* (providing the required classloader). Especially for code-generating plugins, it is important that the provided classloader is the same as the one used for compilation (for on-the-fly compilation) or it contains all the classes compiled at compilation time.
Following iterations will provide helper classes for *context* instantiation.


Chainability
------------

Chainability refers to the ability of the plugin to invoke each other. To provide that and avoid coupling/binding between them the following guidelines apply
1. if a compilation plugin needs another compilation plugin to complete its task, the former must produce an `EfestoCompilationOutput` that also implements `EfestoResource` (see `DrlPackageDescrSetResource`). The compilation manager will be responsible to forward that intermediary artifact to the latter plugin. As usual, care must be giving to avoid leaking of plugin-specific classes outside the plugin parameters.
2. if a runtime plugin needs another runtime plugin to complete its task, the former must produce an `EfestoInput` containing all the required data, and explicitily send it to the `RuntimeManager`. The compilation manager will be responsible to forward that intermediary input to the latter plugin. As usual, care must be giving to avoid leaking of plugin-specific classes outside the plugin parameters.




















