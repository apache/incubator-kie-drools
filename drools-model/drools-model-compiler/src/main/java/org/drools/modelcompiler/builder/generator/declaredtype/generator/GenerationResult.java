package org.drools.modelcompiler.builder.generator.declaredtype.generator;

@FunctionalInterface
interface GenerationResult {

    void error(PojoGenerationError error);
}
