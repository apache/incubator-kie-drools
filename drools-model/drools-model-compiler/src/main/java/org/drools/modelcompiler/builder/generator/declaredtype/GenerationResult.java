package org.drools.modelcompiler.builder.generator.declaredtype;

import org.drools.compiler.compiler.DroolsError;

@FunctionalInterface
interface GenerationResult {

    void error(DroolsError error);
}
