package org.drools.beliefs.bayes.assembler;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.kie.api.io.Resource;

public class BayesNetworkAssemblerError extends DroolsError {
    private String    message;

    public BayesNetworkAssemblerError(Resource resource,
                                      final String message) {
        super( resource );
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
