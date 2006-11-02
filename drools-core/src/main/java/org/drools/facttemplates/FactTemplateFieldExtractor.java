package org.drools.facttemplates;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.ValueType;
import org.drools.spi.FieldExtractor;

public class FactTemplateFieldExtractor
    implements
    FieldExtractor {

    private static final long serialVersionUID = 320;
    private FactTemplate      factTemplate;
    private int               fieldIndex;

    public FactTemplateFieldExtractor(final FactTemplate factTemplate,
                                      final int fieldIndex) {
        this.factTemplate = factTemplate;
        this.fieldIndex = fieldIndex;
    }

    public ValueType getValueType() {
        return this.factTemplate.getFieldTemplate( this.fieldIndex ).getValueType();
    }

    public Object getValue(final Object object) {
        return ((Fact) object).getFieldValue( this.fieldIndex );
    }
    
    public int getIndex() {
        return this.fieldIndex;
    }

    public Class getExtractToClass() {
        return Fact.class;//this.factTemplate.getFieldTemplate( fieldIndex ).getValueType().getClass();
    }

    public boolean getBooleanValue(final Object object) {
        return ((Boolean) ((Fact) object).getFieldValue( this.fieldIndex )).booleanValue();
    }

    public byte getByteValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).byteValue();
    }

    public char getCharValue(final Object object) {
        return ((Character) ((Fact) object).getFieldValue( this.fieldIndex )).charValue();
    }

    public double getDoubleValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).doubleValue();
    }

    public float getFloatValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).floatValue();
    }

    public int getIntValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).intValue();
    }

    public long getLongValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).longValue();
    }

    public short getShortValue(final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).shortValue();
    }
    
    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue", new Class[] { Object.class } );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("This is a bug. Please report to development team: "+e.getMessage(), e);
        }
    }
    
    public int getHashCode(Object object) {
        return getValue( object ).hashCode();
    }
}
