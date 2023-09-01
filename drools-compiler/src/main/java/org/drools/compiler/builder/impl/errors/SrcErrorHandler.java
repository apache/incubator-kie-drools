package org.drools.compiler.builder.impl.errors;

import org.drools.drl.parser.DroolsError;

public class SrcErrorHandler extends ErrorHandler {

    public SrcErrorHandler(final String message) {
        this.message = message;
    }

    public DroolsError getError() {
        return new SrcError(collectCompilerProblems(),
                            this.message);
    }

}
