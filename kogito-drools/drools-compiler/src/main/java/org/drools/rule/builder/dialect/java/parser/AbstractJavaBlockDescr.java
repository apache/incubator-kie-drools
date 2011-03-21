package org.drools.rule.builder.dialect.java.parser;

import java.util.Map;

public abstract class AbstractJavaBlockDescr implements JavaBlockDescr {
    private Map<String, Class< ? >> variables;


    public Map<String, Class< ? >> getInputs() {
        return variables;
    }

    public void setInputs(Map<String, Class< ? >> variables) {
        this.variables = variables;
    }    
}
