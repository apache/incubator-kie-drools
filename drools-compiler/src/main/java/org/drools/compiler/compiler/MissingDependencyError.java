package org.drools.compiler.compiler;

import org.drools.base.common.MissingDependencyException;
import org.drools.drl.parser.DroolsError;
import org.kie.api.io.Resource;

public class MissingDependencyError extends DroolsError {
    private final String message;

    public MissingDependencyError(String message) {
        this.message = message;
    }

    public MissingDependencyError(Resource resource, MissingDependencyException ex) {
        super(resource);
        message = ex.getMessage();
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
