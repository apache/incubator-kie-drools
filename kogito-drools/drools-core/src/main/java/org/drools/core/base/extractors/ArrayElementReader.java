/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.extractors;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.base.ValueType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MathUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.InternalReadAccessor;

public class ArrayElementReader
    implements
    AcceptsReadAccessor,
    InternalReadAccessor,
    ClassWireable,
    Externalizable {
    private InternalReadAccessor arrayReadAccessor;
    private int                  index;
    private Class                type;

    public ArrayElementReader() {

    }

    public ArrayElementReader(InternalReadAccessor arrayExtractor,
                              int index,
                              Class<?> type) {
        this.arrayReadAccessor = arrayExtractor;
        this.index = index;
        this.type = type;
    }

    public Class< ? > getExtractToClass() {
        return type;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        arrayReadAccessor = (InternalReadAccessor) in.readObject();
        index = in.readInt();
        type = (Class<?>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( arrayReadAccessor );
        out.writeInt( index );
        out.writeObject( type );
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.arrayReadAccessor = readAccessor;
    }
    
    public InternalReadAccessor getReadAccessor() {
        return this.arrayReadAccessor;
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( type );
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Boolean) array[this.index]).booleanValue();
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).byteValue();
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Character) array[this.index]).charValue();
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).doubleValue();
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).floatValue();
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).intValue();
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).longValue();
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( getNativeReadMethodName(),
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }
    public String getNativeReadMethodName() {
        String method = "";
        if ( type != null && type.isPrimitive() ) {
            method = StringUtils.ucFirst( type.getName () );
        }
        return "get" + method + "Value";
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return ((Number) array[this.index]).shortValue();
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return array[this.index];
    }

    public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return MathUtils.getBigDecimal( array[this.index] );
    }

    public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return MathUtils.getBigInteger( array[this.index] );
    }

    public ValueType getValueType() {
        return ValueType.OBJECT_TYPE;
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        return array[this.index] == null;
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           Object object) {
        Object[] array = (Object[]) this.arrayReadAccessor.getValue( workingMemory,
                                                                     object );
        
        Object value = array[this.index];
        return (value != null) ? value.hashCode() : 0;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((arrayReadAccessor == null) ? 0 : arrayReadAccessor.hashCode());
        result = PRIME * result + index;
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ArrayElementReader other = (ArrayElementReader) obj;
        if ( arrayReadAccessor == null ) {
            if ( other.arrayReadAccessor != null ) return false;
        } else if ( !arrayReadAccessor.equals( other.arrayReadAccessor ) ) return false;
        if ( index != other.index ) return false;
        return true;
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public boolean getBooleanValue(Object object) {
        return getBooleanValue( null,
                                object );
    }

    public byte getByteValue(Object object) {
        return getByteValue( null,
                             object );
    }

    public char getCharValue(Object object) {
        return getCharValue( null,
                             object );
    }

    public double getDoubleValue(Object object) {
        return getDoubleValue( null,
                               object );
    }

    public float getFloatValue(Object object) {
        return getFloatValue( null,
                              object );
    }

    public int getHashCode(Object object) {
        return getHashCode( null,
                            object );
    }

    public int getIndex() {
        return this.index;
    }

    public int getIntValue(Object object) {
        return getIntValue( null,
                            object );
    }

    public long getLongValue(Object object) {
        return getLongValue( null,
                             object );
    }

    public short getShortValue(Object object) {
        return getShortValue( null,
                              object );
    }

    public Object getValue(Object object) {
        return getValue( null,
                         object );
    }

    public boolean isNullValue(Object object) {
        return isNullValue( null,
                            object );
    }

    public BigDecimal getBigDecimalValue(Object object) {
        return null;
    }

    public BigInteger getBigIntegerValue(Object object) {
        return null;
    }

    public void wire( Class<?> klass ) {
        this.type = klass;
    }

    public String getClassName() {
        return type.getName();
    }

    public Class<?> getClassType() {
        return type;
    }
}
