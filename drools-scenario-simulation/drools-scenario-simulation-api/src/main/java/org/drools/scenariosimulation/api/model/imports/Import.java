package org.drools.scenariosimulation.api.model.imports;

public class Import {

    private String type;

    public Import() {

    }

    public Import(String t) {
        this.type = t;
    }

    public Import(Class<?> clazz) {
        this(clazz.getName());
    }

    public String getType() {
        return this.type;
    }   

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        // Must do instanceof check because there is a subtype of this class in drools-workbench-models-datamodel-api
        if (!(o instanceof Import)) {
            return false;
        }

        Import anImport = (Import) o;

        if (type != null ? !type.equals(anImport.type) : anImport.type != null) {
            return false;
        }

        return true;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
