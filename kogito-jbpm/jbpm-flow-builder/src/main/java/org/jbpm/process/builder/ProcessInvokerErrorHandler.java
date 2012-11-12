package org.jbpm.process.builder;

import org.drools.lang.descr.BaseDescr;
import org.kie.definition.process.Process;

public class ProcessInvokerErrorHandler extends ProcessErrorHandler {

    public ProcessInvokerErrorHandler(final BaseDescr processDescr,
                                      final Process process,
                                      final String message) {
        super( processDescr,
               process,
               message );
    }
}
