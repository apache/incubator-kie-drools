package org.drools.workbench.models.datamodel.imports;

public class Import {

    private String type;

    public Import() {

    }

    public Import( String t ) {
        this.type = t;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Import anImport = (Import) o;

        if (type != null ? !type.equals(anImport.type) : anImport.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
