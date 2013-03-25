package org.drools.guvnor.models.commons.shared.imports;

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

}
