package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsError;

public class SrcErrorHandler extends ErrorHandler {

    public SrcErrorHandler(final String message) {
        this.message = message;
    }

    public DroolsError getError() {
        return new SrcError(collectCompilerProblems(),
                            this.message);
    }

}
