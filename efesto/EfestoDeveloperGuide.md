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
The first is responsible to translate a model definition to an *executable* component (whose meaning may change on a plugin-base). The latter is responsible to use the above *executable* to evaluate responses based on *user* input.

The *compilation* part is defined by the **CompilationManager**, whose work is to
1. find a *plugin* able to *process* a given input
2. retrieve the *processed* artifacts from the plugin
3. returns an **IndexFile** with the information of the created elements

The *runtime* part is defined by the **RuntimeManager**, whose work is to
1. find a *plugin* able to *process* a given input
2. retrieve the *result* from the plugin
3. return the *result* to the client code

## Plugins

The plugins, in turns, are composed on a *compilation* component and a *runtime* component.
To maintain clear dependency separation, those two components should be in separated modules.
If a common class is needed by both, it should be put in a third module. In that case, this third module should not depend on any *core* components, because otherwise it would create an indirect binding between all the components.

### Steps
1. create a **pom** container module
2. inside container module, create a **jar** compilation module
3. inside container module, create a **jar** runtime module
4. if needed, inside container module, create a **jar** api module

*It is also possible to create multiple **jar** compilation modules and/or **jar** runtime modules: as example, see the `kie-drl` structure*.

### Compilation module

1. create an implementation of **org.kie.efesto.compilationmanager.api.serviceKieCompilerService**
2. write the `org.kie.efesto.compilationmanager.api.service.KieCompilerService` file, with the full class name of the implementing class

There are two methods to implement:
1. `<T extends EfestoResource> boolean canManageResource(T toProcess);`
2. `<T extends EfestoResource, E extends EfestoCompilationOutput> List<E> processResource(T toProcess, EfestoCompilationContext context);`

The first method is used by the framework to know if that specific plugin is able to process a given *resource*. There must be at most one plugin that returns `true` for any given resource.
The logic behind this boolean is based on the actual type of the resource, on the identifier of the resource, and on other plugin-specific logic.
The second method is responsible to actually process the resource.
The `T` parameter is actually an `org.kie.efesto.compilationmanager.api.model.EfestoResource`.
The `E` parameter is actually an `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput`.

Beside implementing those two methods, there are no strict rules to follow. For example, it is possible that one single engine is able to manage different format of inputs (e.g. the rule engine). In this case, it is possible to create one single implementation to the different kind of inputs, or it is possible to create one implementation for every kind of input (in which case, anyone of them should be defined in the `org.kie.efesto.compilationmanager.api.service.KieCompilerService` SPI file).

#### EfestoResource

The `org.kie.efesto.compilationmanager.api.model.EfestoResource` contains the data to be processed.
Every plugin could create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoResource` to fullfill its goals; e.g for differentiate the different kind of inputs.
The `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext` define the environment to use for the processing.
Since the most common use case is to do code-generation, `EfestoCompilationContext` embed a `KieMemoryCompiler.MemoryCompilerClassLoader` and is responsible to class compilation and classloader manipulation. It should not be exposed publicly to avoid uncontrolled and unforeseeable behaviors.
Every plugin could also create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext`; e.g. `DrlCompilationContext` that subclass it to also use `KnowledgeBuilderConfiguration`.
This subclassing activity is important to avoid that plugin-specific needs leaks out of the plugin boundary.

#### EfestoCompilationOutput

The `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput` contains the results of the processing.
Every plugin could create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput` to fullfill its goals; e.g to create a "redirection" resource, i.e. a "compilation output" that it is *also* a resource to be processed by another plugin, as for `DrlPackageDescrSetResource`. In case of code-generation, it is possible that `EfestoCallableOutputClassesContainer` would be enough to use.


#### EfestoCompilationContext

The `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext` define the environment to use for the processing.
Since the most common use case is to do code-generation, `EfestoCompilationContext` embed a `KieMemoryCompiler.MemoryCompilerClassLoader` and is responsible to class compilation and classloader manipulation. It should not be exposed publicly to avoid uncontrolled and unforeseeable behaviors.
Every plugin could also create its own subclass of `org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext`; e.g. `DrlCompilationContext` that subclass it to also use `KnowledgeBuilderConfiguration`.
This subclassing activity is important to avoid that plugin-specific needs leaks out of the plugin boundary.

### Runtime module

1. create an implementation of **org.kie.efesto.runtimemanager.api.KieRuntimeService**
2. write the `org.kie.efesto.runtimemanager.api.service.KieRuntimeService` file, with the full class name of the implementing class

There are two methods to implement:
1. `boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context);`
2. `Optional<E> evaluateInput(T toEvaluate, EfestoRuntimeContext context);`

The first method is used by the framework to know if that specific plugin is able to evaluate a given *input*. There must be at most one plugin that returns `true` for any given input.
The logic behind this boolean is based on the actual type of the input, on the identifier of the input, and on other plugin-specific logic.
The second method is responsible to actually evalute the input.
The `T` parameter is actually an `org.kie.efesto.runtimemanager.api.model.EfestoInput`
The `E` parameter is actually an `org.kie.efesto.runtimemanager.api.model.EfestoOutput`


Beside implementing those two methods, there are no strict rules to follow. For example, it is possible that one single engine is able to evaluate different format of inputs (e.g. the rule engine). In this case, it is possible to create one single implementation to the different kind of inputs, or it is possible to create one implementation for every kind of input (in which case, anyone of them should be defined in the `org.kie.efesto.runtimemanager.api.service.KieRuntimeService` SPI file).

#### EfestoInput

The `org.kie.efesto.runtimemanager.api.model.EfestoInput` contains the data to be evaluated.
Every plugin could create its own subclass of `org.kie.efesto.runtimemanager.api.model.EfestoInput` to fullfill its goals; e.g for differentiate the different kind of inputs.

#### EfestoOutput
The `org.kie.efesto.runtimemanager.api.model.EfestoOutput` contains the result of the evaluation.
Every plugin could create its own subclass of `org.kie.efesto.runtimemanager.api.model.EfestoOutput` to fullfill its goals; e.g for differentiate the different kind of outputs.


#### EfestoRuntimeContext

The `org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext` define the environment to use for the evaluation.
Since the most common use case is to do code-generation, `EfestoRuntimeContext` embed a `KieMemoryCompiler.MemoryCompilerClassLoader` and is responsible to class compilation and classloader manipulation. It should not be exposed publicly to avoid uncontrolled and unforeseeable behaviors.
Every plugin could also create its own subclass of `org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext`.
This subclassing activity is important to avoid that plugin-specific needs leaks out of the plugin boundary.


Chainability
------------

Chainability refers to the ability of the plugin to invoke each other. To provide that and avoidi coupling/binding between them the following guidelines apply
1. if a compilation plugin needs another compilation plugin to complete its task, the former must produce an `EfestoCompilationOutput` that also implements `EfestoResource` (see `DrlPackageDescrSetResource`). The compilation manager will be responsible to forward that intermediary artifact to the latter plugin. As usual, care must be giving to avoid leaking of plugin-specific classes outside the plugin parameters.
2. if a runtime plugin needs another runtime plugin to complete its task, the former must produce an `EfestoInput` containing all the required data, and explicitily send it to the `RuntimeManager`. The compilation manager will be responsible to forward that intermediary input to the latter plugin. As usual, care must be giving to avoid leaking of plugin-specific classes outside the plugin parameters.




















