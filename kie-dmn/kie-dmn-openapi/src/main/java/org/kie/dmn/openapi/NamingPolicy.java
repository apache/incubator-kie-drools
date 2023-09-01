package org.kie.dmn.openapi;

import org.kie.dmn.api.core.DMNType;

public interface NamingPolicy {

    String getName(DMNType type);

    String getRef(DMNType type);
}
