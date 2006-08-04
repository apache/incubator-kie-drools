package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

public class FactTemplateDescr extends PatternDescr {
    private static final long serialVersionUID = 320;
    
    String name;
    List fields = new ArrayList( 1 );
    
    public FactTemplateDescr(String name) {
        this.name = name;
    }
    
    public void addFieldTemplate(FieldTemplateDescr fieldTemplate) {
        this.fields.add( fieldTemplate );
    }

    public List getFields() {
        return this.fields;
    }

    public String getName() {
        return this.name;
    }
    
    
}
