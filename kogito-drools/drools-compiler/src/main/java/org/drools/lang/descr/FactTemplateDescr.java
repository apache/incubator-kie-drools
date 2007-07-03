package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

public class FactTemplateDescr extends BaseDescr {
    private static final long serialVersionUID = 400L;

    String                    name;
    List                      fields           = new ArrayList( 1 );

    public FactTemplateDescr(final String name) {
        this.name = name;
    }

    public void addFieldTemplate(final FieldTemplateDescr fieldTemplate) {
        this.fields.add( fieldTemplate );
    }

    public List getFields() {
        return this.fields;
    }

    public String getName() {
        return this.name;
    }

}
