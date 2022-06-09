package org.optaplanner.quarkus.devui;

import java.util.List;

public class OptaPlannerDevUIProperties {
    private final OptaPlannerModelProperties optaPlannerModelProperties;
    private final String effectiveSolverConfigXML;
    private final List<String> constraintList;

    public OptaPlannerDevUIProperties(OptaPlannerModelProperties optaPlannerModelProperties, String effectiveSolverConfigXML,
            List<String> constraintList) {
        this.optaPlannerModelProperties = optaPlannerModelProperties;
        this.effectiveSolverConfigXML = effectiveSolverConfigXML;
        this.constraintList = constraintList;
    }

    public OptaPlannerModelProperties getOptaPlannerModelProperties() {
        return optaPlannerModelProperties;
    }

    public String getEffectiveSolverConfig() {
        return effectiveSolverConfigXML;
    }

    public List<String> getConstraintList() {
        return constraintList;
    }
}
