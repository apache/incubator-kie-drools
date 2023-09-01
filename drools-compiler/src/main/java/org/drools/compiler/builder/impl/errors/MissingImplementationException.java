package org.drools.compiler.builder.impl.errors;

import org.kie.api.io.Resource;

public class MissingImplementationException extends RuntimeException {

    private final Resource resource;
    private final String dependency;

    public MissingImplementationException( Resource resource, String dependency ) {
        this.resource = resource;
        this.dependency = dependency;
    }

    @Override
    public String getMessage() {
        return "Unable to compile " + resource.getSourcePath() + ". Maybe you need to add " + dependency + " to your project dependencies.";
    }
}
