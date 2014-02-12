package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.FunctionError;
import org.drools.compiler.lang.descr.FunctionDescr;

public class FunctionErrorHandler extends ErrorHandler {

    private FunctionDescr descr;

    public FunctionErrorHandler(final FunctionDescr functionDescr,
                                final String message) {
        this.descr = functionDescr;
        this.message = message;
    }

    public DroolsError getError() {
        return new FunctionError(this.descr,
                                 collectCompilerProblems(),
                                 this.message);
    }

}
