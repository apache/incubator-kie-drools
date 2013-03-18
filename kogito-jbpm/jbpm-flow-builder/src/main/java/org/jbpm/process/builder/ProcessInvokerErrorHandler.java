package org.jbpm.process.builder;

import org.kie.api.definition.process.Process;
import org.drools.compiler.lang.descr.BaseDescr;

public class ProcessInvokerErrorHandler extends ProcessErrorHandler {

    public ProcessInvokerErrorHandler(final BaseDescr processDescr,
                                      final Process process,
                                      final String message) {
        super( processDescr,
               process,
               message );
    }
}
