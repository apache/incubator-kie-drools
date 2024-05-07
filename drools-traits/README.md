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

Descoping Traits guide
======================

This guide will help you understand how we did the traits descoping:

The idea behind it was to have a pluggable implementation: to use it you can import it by adding the module to Maven.
The new `drools-traits` Maven module depends on `drools-core` and `drools-compiler`, and every specific traits implementation code is hopefully isolated in this module.

Packages
======== 

As the traits feature was originally heavily intertwined in both core and compiler, I decided to move classes from both modules into a single module, preserving the original package name and adding the `.trait` prefix, to avoid the split package problem.

We have now `org.drools.traits.core.*` and `org.drools.traits.compiler.*` depending on whether the original file was inside core or compiler.

You'll find many classes with only the package changed, such as 

`org.drools.base.factmodel.traits.LogicalTypeInconsistencyException.java` 

became

`org.drools.traits.core.factmodel.LogicalTypeInconsistencyException.java` 

Notice the double `.traits`, I'm open for suggestions here
   
Interfaces
===========

Inside the `org.drools.base.factmodel.traits` package, in the `drools-core` module, there are now only interfaces. 
I tried to keep them to the bare minimum but I couldn't remove most of them as the traits-related method (such as `don`, `shed`) are in `DefaultKnowledgeHelper` and they cannot be removed without breaking the API. 

When I moved the classes between modules I tried to keep the source code identical except for changing the package (as described in the Packages section) and to rename the class with the `-Impl` suffix, keeping the interface in the original module, i.e.

`org.drools.base.factmodel.traits.TraitFactory` was transformed into an interface with just one method, and it was created a 
`org.drools.traits.core.factmodel.TraitFactoryImpl` inside the `drools-traits` module.
https://github.com/kiegroup/drools/pull/2887/files#diff-08cd5643f232535394581edeab619a00

Take a look at another example such as `TraitRegistry` https://github.com/kiegroup/drools/pull/2887/files#diff-6a70e4d76093ef8e591283577874b074R7

If a class or an interface was used only by traits specific code, I moved it inside the `drools-traits` module.

Injecting Components
====================

To use traits, you must instantiate traits-specific classes using our service loader mechanism.
The `kie.conf` file provided as an example in the `drools-traits` supports the execution of all the traits related tests in the original modules 
https://github.com/kiegroup/drools/pull/2887/files#diff-ff3ee1dbc977e7b1dd4cfb16c52cbfca

Currently three components are needed
```
org.drools.core.reteoo.KieComponentFactoryFactory=org.drools.traits.core.reteoo.TraitKieComponentFactoryFactory
org.drools.compiler.builder.impl.TypeDeclarationBuilderFactory=org.drools.traits.compiler.builder.impl.TraitTypeDeclarationBuilderFactory
```

When we'll merge the issue, they'll be probably two, I'm not sure if it's possible to have just one. We'll probably need one injected in `-compiler` and one in `-core` but I'm not sure about this. 
Nevertheless I wasn't able to reduce the number yet.
 
Details of the three components are explained later in this document. 


Subtyping and Factories
=======================

`drools-core` delegates most of the instances creation to factory classes. 
For example the constructor of `org.drools.core.reteoo.AlphaNode` is never called directly, we use `org.drools.core.reteoo.builder.PhreakNodeFactory`.

At the same time Alpha Node had inside traits specific code that could be removed, such as in `calculateDeclaredMask` https://github.com/kiegroup/drools/pull/2887/files#diff-daf38b3d53b080cc1724b7d830e78c01L330

By providing a different implementation of the `org.drools.core.reteoo.builder.PhreakNodeFactory` => `org.drools.traits.core.reteoo.TraitPhreakNodeFactory` I could create instances of `org.drools.traits.core.reteoo.TraitAlphaNode` and move all the traits specific code there (https://github.com/kiegroup/drools/pull/2887/files#diff-2a6e70d7a533b488281fea5ccf5cfc39)
 
The problem was that, even though we had factories, the mechanism to provide different implementation of such factories was removed some time ago in the code. 

The root object creating all those factories is `org.drools.core.reteoo.KieComponentFactory` and that was instantiated directly in `org.drools.core.RuleBaseConfiguration`.
If this object hadn't been stateful we could had injected the object itself in the `kie.conf` file, but unfortunately the lifecycle of the component is really important and a new instance has to be created only once in the `init` method in `org.drools.core.RuleBaseConfiguration`. 
By injecting the `KieComponentFactory` in the service loader, we would have had a Singleton KieComponentFactory per class loader.

To inject a different `org.drools.core.reteoo.KieComponentFactory` I created then a `org.drools.core.reteoo.KieComponentFactoryFactory` that can be injected in the `kie.conf` file i.e.:

```
org.drools.core.reteoo.KieComponentFactoryFactory=org.drools.traits.core.reteoo.TraitKieComponentFactoryFactory
```

When missing, it defaults to the creation of a `org.drools.core.reteoo.KieComponentFactory` 

NOTE: I know the whole idea of a `*FactoryFactory` is horripilating, but that's what you get when you put state in a factory, whose job should be only to create object. You get a higher-order factory. 
I'm open to suggestion for better names, such as `*FactoryBuilder` to avoid being mocked exploiting Java programmers stereotypes.

The `TraitKieComponentFactory` created by the `TraitKieComponentFactoryFactory` stores everything needed by the traits specific code, such as https://github.com/kiegroup/drools/pull/2887/files#diff-7accbb25640ec3a967ccbe5079e0306eR46

Other example of subclassed objects:

`org.drools.core.common.NamedEntryPoint` => `org.drools.traits.core.common.TraitNamedEntryPoint`  https://github.com/kiegroup/drools/pull/2887/files#diff-7290855911f5856e5432eb94c66e5ac8R404
`org.drools.core.common.DefaultFactHandle` => `org.drools.traits.core.common.TraitDefaultFactHandle` https://github.com/kiegroup/drools/pull/2887/files#diff-0dc5e56c09ea3b314b741c39d78177baR214

When factory classes were missing (such as in NamedEntryPoint) they were created
https://github.com/kiegroup/drools/pull/2887/files#diff-396566b11c71c507cb114bf327022c95R10

IsA EvaluatorDefinition
=======================

The isA operator used in traits was removed from the default evaluators and moved to the traits module
https://github.com/kiegroup/drools/pull/2887/files#diff-add6d202fbcf44eade6dcbb67a273815

When needed, it has to be defined explicitly such as in 
https://github.com/kiegroup/drools/pull/2887/files#diff-a5a9d687ff07d8c0de153da39d6ba177R36

Static helper Methods
=====================

Some static helper methods were moved in isolated classes such as `static boolean supersetOrEqualset(BitSet n1, BitSet n2 )`
https://github.com/kiegroup/drools/pull/2887/files#diff-c023ae27b398cf56a54054cced5b933dR7

Tests
=======

Every test from the original suite were moved to the traits module and the original behaviour is still guaranteed.
No tests were harmed during this descoping.

Persistence
===========

Persistence related tests were also moved to the traits module. That's why in the `kie.conf` file there are `KieStoreServices` defined for persistence

```properties
org.kie.api.persistence.jpa.KieStoreServices = org.drools.persistence.jpa.KnowledgeStoreServiceImpl
?org.kie.internal.process.CorrelationKeyFactory = org.jbpm.persistence.correlation.JPACorrelationKeyFactory
``` 
