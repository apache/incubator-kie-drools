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

package org.drools.core.facttemplates;

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

public class FactTemplateFieldExtractor
    implements
    Externalizable,
    org.drools.core.spi.InternalReadAccessor {

    private static final long serialVersionUID = 510l;
    private FactTemplate      factTemplate;
    private int               fieldIndex;

    public FactTemplateFieldExtractor() {

    }

    public FactTemplateFieldExtractor(final FactTemplate factTemplate,
                                      final int fieldIndex) {
        this.factTemplate = factTemplate;
        this.fieldIndex = fieldIndex;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        factTemplate = (FactTemplate) in.readObject();
        fieldIndex = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( factTemplate );
        out.writeInt( fieldIndex );
    }

    public ValueType getValueType() {
        return this.factTemplate.getFieldTemplate( this.fieldIndex ).getValueType();
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
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

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        return ((Boolean) ((Fact) object).getFieldValue( this.fieldIndex )).booleanValue();
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).byteValue();
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return ((Character) ((Fact) object).getFieldValue( this.fieldIndex )).charValue();
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).doubleValue();
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).floatValue();
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).intValue();
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).longValue();
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return ((Number) ((Fact) object).getFieldValue( this.fieldIndex )).shortValue();
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public String getNativeReadMethodName() {
        return "getValue";
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return getValue( workingMemory,
                         object ).hashCode();
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               Object object) {
        return ((Fact) object).getFieldValue( this.fieldIndex ) == null;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factTemplate == null) ? 0 : factTemplate.hashCode());
        result = prime * result + fieldIndex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        FactTemplateFieldExtractor other = (FactTemplateFieldExtractor) obj;
        
        if ( factTemplate == null ) {
            if ( other.factTemplate != null ) return false;
        } else if ( !factTemplate.equals( other.factTemplate ) ) {
            return false;
        }
        
        if ( fieldIndex != other.fieldIndex ) {
            return false;
        }
        return true;
    }
   
}
