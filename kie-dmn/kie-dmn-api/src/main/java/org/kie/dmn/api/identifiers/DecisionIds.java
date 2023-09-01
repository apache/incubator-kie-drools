package org.kie.dmn.api.identifiers;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class DecisionIds implements ComponentRoot {
    public LocalDecisionId get(String namespace, String name) {
        return new LocalDecisionId(namespace, name);
    }
}
