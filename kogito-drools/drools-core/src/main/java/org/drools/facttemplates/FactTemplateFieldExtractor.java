package org.drools.facttemplates;

import org.drools.base.ValueType;
import org.drools.spi.FieldExtractor;

public class FactTemplateFieldExtractor
    implements
    FieldExtractor {

    private static final long        serialVersionUID = 320;
    private FactTemplate             factTemplate;
    private int                      fieldIndex;

    public FactTemplateFieldExtractor(final FactTemplate factTemplate,
                                       final int fieldIndex) {
        this.factTemplate = factTemplate;
        this.fieldIndex = fieldIndex;
    }
    
    
    public ValueType getValueType() {
        return this.factTemplate.getFieldTemplate( fieldIndex ).getValueType();
    }

    public Object getValue(Object object) {
        return ((Fact) object).getFieldValue( this.fieldIndex );
    }
    
    public int getIndex() {
        return this.fieldIndex;
    }
    
    public Class getExtractToClass() {
        return Fact.class;//this.factTemplate.getFieldTemplate( fieldIndex ).getValueType().getClass();
    }
}
