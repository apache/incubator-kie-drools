package org.jbpm.process.builder;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.PackageBuilder.ErrorHandler;
import org.kie.api.definition.process.Process;
import org.drools.compiler.lang.descr.BaseDescr;
import org.jbpm.compiler.ProcessBuildError;

public class ProcessErrorHandler extends ErrorHandler {

    private BaseDescr descr;

    private Process   process;

    public ProcessErrorHandler(final BaseDescr ruleDescr,
                               final Process process,
                               final String message) {
        this.descr = ruleDescr;
        this.process = process;
        this.message = message;
    }

    public DroolsError getError() {
        return new ProcessBuildError( this.process,
                                      this.descr,
                                      collectCompilerProblems(),
                                      this.message );
    }

}
