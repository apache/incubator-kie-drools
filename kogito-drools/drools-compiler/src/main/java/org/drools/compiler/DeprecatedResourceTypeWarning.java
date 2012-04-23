package org.drools.compiler;

import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

public class DeprecatedResourceTypeWarning extends DroolsWarning {

    private final String deprecatedFormat;

    public DeprecatedResourceTypeWarning(Resource resource) {
        this(resource, ((InternalResource) resource).getResourceType().getName());
    }

    public DeprecatedResourceTypeWarning(Resource resource, String deprecatedFormat) {
        super(resource);
        this.deprecatedFormat = deprecatedFormat;
    }

    @Override
    public String getMessage() {
        return deprecatedFormat + " format usage detected. This format is deprecated and will be removed in future";
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
