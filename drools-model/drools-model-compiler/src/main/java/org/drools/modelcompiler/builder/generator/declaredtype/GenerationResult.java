package org.drools.modelcompiler.builder.generator.declaredtype;

@FunctionalInterface
interface GenerationResult {

    void error(PojoGenerationError error);
}
