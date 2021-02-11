# Sample generator

This repository contains an example of a generator. The output of this generator is quite simple, but it implements all
the methods defined in the `Generator` interface, so it can be used as reference implementation.

The goal of this `Sample` engine is to consume `txt` files from the project, expose the content of each of them via a `/sample` REST 
endpoint. The user can also configure the number of times the content should be replicated

There are two modules:
- `kogito-codegen-sample-generator`: this module contains the implementation of the generator
- `kogito-codegen-sample-runtime`: this module is intended to provide the runtime support of this engine. In general engines
  impl should not be inside the codegen module, so it is here only for reference 
  
This generator also implement `GeneratorFactory` and provides `META-INF/services/org.kie.kogito.codegen.api.GeneratorFactory`
for automatic wiring via SPI