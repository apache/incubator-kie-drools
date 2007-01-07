package org.mvel.integration.impl;

import org.mvel.integration.VariableResolver;

import java.util.Map;

public class MapVariableResolver implements VariableResolver {
    private String name;
    private Class knownType;

    private Map variableMap;

    public MapVariableResolver(Map variableMap, String name) {
        this.variableMap = variableMap;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKnownType(Class knownType) {
        this.knownType = knownType;
    }

    public void setVariableMap(Map variableMap) {
        this.variableMap = variableMap;
    }

    public String getName() {
        return name;
    }

    public Class getKnownType() {
        if (knownType == null && variableMap.containsKey(name)) {
            knownType = variableMap.get(name) != null ? variableMap.get(name).getClass() : Object.class;
        }
        else {
            knownType = Object.class;
        }

        return knownType;
    }

    public Object getValue() {
        return variableMap.get(name);
    }

    public int getFlags() {
        return 0;
    }
}
