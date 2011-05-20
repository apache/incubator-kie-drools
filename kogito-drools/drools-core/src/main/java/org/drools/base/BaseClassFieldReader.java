/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MathUtils;
import org.drools.core.util.asm.ClassFieldInspector;

/**
 * This is the supertype for the ASM generated classes for accessing a field.
 */
abstract public class BaseClassFieldReader
    implements
    org.drools.spi.InternalReadAccessor {

    private int        index;

    private Class< ? > fieldType;

    private ValueType  valueType;

    public BaseClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     *
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseClassFieldReader(final int index,
                                   final Class< ? > fieldType,
                                   final ValueType valueType) {
        this.index = index;
        this.fieldType = fieldType;
        this.valueType = valueType;
    }

    /**
     * This is the constructor to be used
     *
     * @param clazz
     * @param fieldName
     */
    public BaseClassFieldReader(final Class< ? > clazz,
                                final String fieldName) {
        try {
            final ClassFieldInspector inspector = new ClassFieldInspector( clazz );
            this.index = ((Integer) inspector.getFieldNames().get( fieldName )).intValue();
            this.fieldType = (Class< ? >) inspector.getFieldTypes().get( fieldName );
            this.valueType = ValueType.determineValueType( this.fieldType );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }    

    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(int i) {
        this.index = i;
    }

    public Class< ? > getExtractToClass() {
        return this.fieldType;
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( this.fieldType );
    }

    public void setFieldType(Class< ? > fieldType) {
        this.fieldType = fieldType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.fieldType.hashCode();
        result = PRIME * result + this.index;
        result = PRIME * result + this.valueType.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( !(object instanceof BaseClassFieldReader) ) {
            return false;
        }
        final BaseClassFieldReader other = (BaseClassFieldReader) object;
        return this.fieldType == other.fieldType && this.index == other.index && this.valueType.equals( other.valueType );
    }

    public Object getValue(Object object) {
        return getValue( null,
                         object );
    }

    public char getCharValue(Object object) {
        return getCharValue( null,
                             object );
    }

    public int getIntValue(Object object) {
        return getIntValue( null,
                            object );
    }

    public byte getByteValue(Object object) {
        return getByteValue( null,
                             object );
    }

    public short getShortValue(Object object) {
        return getShortValue( null,
                              object );
    }

    public long getLongValue(Object object) {
        return getLongValue( null,
                             object );
    }

    public float getFloatValue(Object object) {
        return getFloatValue( null,
                              object );
    }

    public double getDoubleValue(Object object) {
        return getDoubleValue( null,
                               object );
    }

    public boolean getBooleanValue(Object object) {
        return getBooleanValue( null,
                                object );
    }

    public BigDecimal getBigDecimalValue(Object object) {
        return getBigDecimalValue( null,
                                   object );
    }

    public BigInteger getBigIntegerValue(Object object) {
        return getBigIntegerValue( null,
                                   object );
    }

    public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        return MathUtils.getBigDecimal( getValue( workingMemory,
                                                  object ) );
    }

    public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        return MathUtils.getBigInteger( getValue( workingMemory,
                                                  object ) );
    }

    public boolean isNullValue(Object object) {
        return isNullValue( null,
                            object );
    }

    public int getHashCode(Object object) {
        return getHashCode( null,
                            object );
    }

}
