package org.kie.pmml.pmml_4_2.compiler;

import org.kie.api.definition.type.Key;

public class FieldScope {

    @Key
    private String name;
    private boolean functionLocal;

    public FieldScope(String name, boolean functionLocal) {
        super();
        this.name = name;
        this.functionLocal = functionLocal;
    }

    public FieldScope() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFunctionLocal() {
        return functionLocal;
    }

    public void setFunctionLocal(boolean functionLocal) {
        this.functionLocal = functionLocal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FieldScope other = (FieldScope) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (functionLocal != other.functionLocal) {
            return false;
        }
        return true;
    }

}
