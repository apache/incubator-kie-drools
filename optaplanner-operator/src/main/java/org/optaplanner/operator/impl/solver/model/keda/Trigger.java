package org.optaplanner.operator.impl.solver.model.keda;

import org.optaplanner.operator.impl.solver.model.common.ResourceNameReference;

public final class Trigger {
    private String type;
    private String name;
    private TriggerMetadata metadata;
    private ResourceNameReference authenticationRef;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TriggerMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(TriggerMetadata metadata) {
        this.metadata = metadata;
    }

    public ResourceNameReference getAuthenticationRef() {
        return authenticationRef;
    }

    public void setAuthenticationRef(ResourceNameReference authenticationRef) {
        this.authenticationRef = authenticationRef;
    }
}
