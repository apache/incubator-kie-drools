package org.drools.facttemplates;

import java.lang.reflect.Method;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import org.drools.RuntimeDroolsException;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.FieldExtractor;
import org.drools.util.ClassUtils;

public class FactTemplateFieldExtractor
    implements
    FieldExtractor {

    private static final long serialVersionUID = 400L;
    private FactTemplate      factTemplate;
    private int               fieldIndex;

    public FactTemplateFieldExtractor() {

    }

    public FactTemplateFieldExtractor(final FactTemplate factTemplate,
                                      final int fieldIndex) {
        this.factTemplate = factTemplate;
        this.fieldIndex = fieldIndex;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factTemplate    = (FactTemplate)in.readObject();
        fieldIndex      = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factTemplate);
        out.writeInt(fieldIndex);
    }

    public ValueType getValueType() {
        return this.factTemplate.getFieldTemplate( this.fieldIndex ).getValueType();
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Fact) object).getFieldValue( this.fieldIndex );
    }

    public int getIndex() {
        return this.fieldIndex;
    }

    public Class getExtractToClass() {
        return this.factTemplate.getFieldTemplate( fieldIndex ).getValueType().getClassType();
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( getExtractToClass() );
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Boolean) ((Fact) object).getFieldValue( this.fieldIndex )).booleanValue();
    }

    public byte getByteValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).byteValue();
    }

    public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Character) ((Fact) object).getFieldValue( this.fieldIndex )).charValue();
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).doubleValue();
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).floatValue();
    }

    public int getIntValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).intValue();
    }

    public long getLongValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).longValue();
    }

    public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).shortValue();
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {
        return getValue( workingMemory, object ).hashCode();
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               Object object) {
        return ((Fact) object).getFieldValue( this.fieldIndex ) == null;
    }
}
