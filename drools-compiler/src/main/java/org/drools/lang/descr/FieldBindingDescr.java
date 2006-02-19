package org.drools.lang.descr;

public class FieldBindingDescr extends PatternDescr {
    private String fieldName;
    private String identifier;

    public FieldBindingDescr(String fieldName, String identifier) {
        this.fieldName = fieldName;
        this.identifier = identifier;
    }

    public String getFieldName() {
        return fieldName;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
}
