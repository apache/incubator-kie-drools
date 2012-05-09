package org.drools.compiler;

import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

public class DeprecatedResourceTypeWarning extends DroolsWarning {

    private final String deprecatedFormat;

    public DeprecatedResourceTypeWarning(String deprecatedFormat) {
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
