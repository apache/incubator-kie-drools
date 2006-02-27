package org.drools.lang.descr;

public class AttributeDescr extends PatternDescr {
    private String name;    
    private String value;
    
    public AttributeDescr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }            
}
