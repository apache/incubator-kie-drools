package org.mvel.integration.impl;

import org.mvel.CompileException;
import org.mvel.integration.VariableResolver;

public class ClassImportResolver implements VariableResolver {
    private String name;
    private Class knownType;

    public ClassImportResolver(String fqcn, String name) {
        this.name = name;
        try {
            this.knownType = Class.forName(fqcn);
        }
        catch (Exception e) {
            throw new CompileException("failed import", e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKnownType(Class knownType) {
        this.knownType = knownType;
    }


    public String getName() {
        return name;
    }

    public Class getKnownType() {
        return Class.class;
    }

    public Object getValue() {
        return knownType;
    }

    public int getFlags() {
        return 0;
    }


}
