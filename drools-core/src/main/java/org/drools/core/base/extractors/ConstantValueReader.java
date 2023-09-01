package org.drools.core.base.extractors;

import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.ReadAccessor;

import static org.drools.base.util.TimeIntervalParser.getTimestampFromDate;

public class ConstantValueReader implements ReadAccessor {

    private final Object value;

    public ConstantValueReader(Object value) {
        this.value = value;
    }

    public Object getValue(ValueResolver valueResolver, Object object) {
        return value;
    }

    public char getCharValue(ValueResolver valueResolver, Object object) {
        return (Character)value;
    }

    public int getIntValue(ValueResolver valueResolver, Object object) {
        return (Integer)value;
    }

    public byte getByteValue(ValueResolver valueResolver, Object object) {
        return (Byte)value;
    }

    public short getShortValue(ValueResolver valueResolver, Object object) {
        return (Short)value;
    }

    public long getLongValue(ValueResolver valueResolver, Object object) {
        return value instanceof Long ? (Long)value : getTimestampFromDate( value );
    }

    public float getFloatValue(ValueResolver valueResolver, Object object) {
        return (Float)value;
    }

    public double getDoubleValue(ValueResolver valueResolver, Object object) {
        return (Double)value;
    }

    public boolean getBooleanValue(ValueResolver valueResolver, Object object) {
        return (Boolean)value;
    }

    public boolean isNullValue(ValueResolver valueResolver, Object object) {
        return value == null;
    }

    public int getHashCode(ValueResolver valueResolver, Object object) {
        return value.hashCode();
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public Object getValue(Object object) {
        return value;
    }

    public boolean isNullValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.isNullValue -> TODO");
    }

    public ValueType getValueType() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getValueType -> TODO");
    }

    public Class<?> getExtractToClass() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getExtractToClass -> TODO");
    }

    public String getExtractToClassName() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getExtractToClassName -> TODO");
    }

    public Method getNativeReadMethod() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getNativeReadMethod -> TODO");
    }

    public String getNativeReadMethodName() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getNativeReadMethodName -> TODO");
    }

    public int getHashCode(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getHashCode -> TODO");
    }

    public int getIndex() {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getIndex -> TODO");
    }
}
