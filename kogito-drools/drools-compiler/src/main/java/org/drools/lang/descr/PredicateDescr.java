package org.drools.lang.descr;

public class PredicateDescr extends PatternDescr {
    private String fieldName;
    private String text;
        
    public PredicateDescr(String fieldName,
                          String text) {
        this.fieldName = fieldName;
        this.text = text;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getText() {
        return this.text;
    } 
}
