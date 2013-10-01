package org.drools.base.extractors;

import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.InternalReadAccessor;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ConstantValueReader implements InternalReadAccessor {

    private final Object value;

    public ConstantValueReader(Object value) {
        this.value = value;
    }

    public Object getValue(InternalWorkingMemory workingMemory, Object object) {
        return value;
    }

    public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory, Object object) {
        return (BigDecimal)value;
    }

    public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory, Object object) {
        return (BigInteger)value;
    }

    public char getCharValue(InternalWorkingMemory workingMemory, Object object) {
        return (Character)value;
    }

    public int getIntValue(InternalWorkingMemory workingMemory, Object object) {
        return (Integer)value;
    }

    public byte getByteValue(InternalWorkingMemory workingMemory, Object object) {
        return (Byte)value;
    }

    public short getShortValue(InternalWorkingMemory workingMemory, Object object) {
        return (Short)value;
    }

    public long getLongValue(InternalWorkingMemory workingMemory, Object object) {
        return (Long)value;
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, Object object) {
        return (Float)value;
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, Object object) {
        return (Double)value;
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, Object object) {
        return (Boolean)value;
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory, Object object) {
        return value == null;
    }

    public int getHashCode(InternalWorkingMemory workingMemory, Object object) {
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

    public BigDecimal getBigDecimalValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBigDecimalValue -> TODO");
    }

    public BigInteger getBigIntegerValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBigIntegerValue -> TODO");
    }

    public char getCharValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getCharValue -> TODO");
    }

    public int getIntValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getIntValue -> TODO");
    }

    public byte getByteValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getByteValue -> TODO");
    }

    public short getShortValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getShortValue -> TODO");
    }

    public long getLongValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getLongValue -> TODO");
    }

    public float getFloatValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getFloatValue -> TODO");
    }

    public double getDoubleValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getDoubleValue -> TODO");
    }

    public boolean getBooleanValue(Object object) {
        throw new UnsupportedOperationException("org.drools.core.base.extractors.ConstantValueReader.getBooleanValue -> TODO");
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