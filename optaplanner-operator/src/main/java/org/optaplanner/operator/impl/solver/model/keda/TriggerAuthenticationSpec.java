package org.optaplanner.operator.impl.solver.model.keda;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TriggerAuthenticationSpec {

    @JsonProperty("secretTargetRef")
    private List<SecretTargetRef> secretTargetRefs = new ArrayList<>();

    public TriggerAuthenticationSpec withSecretTargetRef(SecretTargetRef secretTargetRef) {
        secretTargetRefs.add(secretTargetRef);
        return this;
    }

    public List<SecretTargetRef> getSecretTargetRefs() {
        return secretTargetRefs;
    }

    public void setSecretTargetRefs(List<SecretTargetRef> secretTargetRefs) {
        this.secretTargetRefs = secretTargetRefs;
    }
}
