package org.drools.drl.ast.descr;

import java.util.ArrayList;
import java.util.List;

public class FactTemplateDescr extends BaseDescr {
    private static final long serialVersionUID = 510l;

    String                    name;
    List<FieldTemplateDescr>  fields           = new ArrayList<>( 1 );

    public FactTemplateDescr(final String name) {
        this.name = name;
    }

    public FactTemplateDescr() {
    }

    public void addFieldTemplate(final FieldTemplateDescr fieldTemplate) {
        this.fields.add( fieldTemplate );
    }

    public List<FieldTemplateDescr> getFields() {
        return this.fields;
    }

    public String getName() {
        return this.name;
    }

}
