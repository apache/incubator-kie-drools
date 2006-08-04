package org.drools.lang.descr;

public class FieldTemplateDescr extends PatternDescr {
    private final String      name;
    private final String      classType;
    public FieldTemplateDescr(final String name,
                              final String type) {
        super();
        this.name = name;
        this.classType = type;
    }
    public String getName() {
        return this.name;
    }
    public String getClassType() {
        return this.classType;
    }
    
    
}
