package org.optaplanner.operator.impl.solver.model.keda;

import io.fabric8.kubernetes.api.model.SecretKeySelector;

public final class SecretTargetRef {

    public static SecretTargetRef fromSecretKeySelector(String parameter, SecretKeySelector secretKeySelector) {
        return new SecretTargetRef(parameter, secretKeySelector.getName(), secretKeySelector.getKey());
    }

    private String parameter;
    private String name;
    private String key;

    public SecretTargetRef() {
        // Required by Jackson.
    }

    public SecretTargetRef(String parameter, String name, String key) {
        this.parameter = parameter;
        this.name = name;
        this.key = key;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
