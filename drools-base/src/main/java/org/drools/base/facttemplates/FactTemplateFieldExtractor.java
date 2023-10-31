/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.facttemplates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.util.ClassUtils;

public class FactTemplateFieldExtractor
    implements
    Externalizable,
        ReadAccessor {

    private static final long serialVersionUID = 510l;
    private FactTemplate factTemplate;
    private String fieldName;
    private int fieldIndex;

    public FactTemplateFieldExtractor() {

    }

    public FactTemplateFieldExtractor(FactTemplate factTemplate, String fieldName) {
        this.factTemplate = factTemplate;
        this.fieldName = fieldName;
        this.fieldIndex = factTemplate.getFieldTemplateIndex(fieldName);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        factTemplate = (FactTemplate) in.readObject();
        fieldName = in.readUTF();
        fieldIndex = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( factTemplate );
        out.writeUTF( fieldName );
        out.writeInt( fieldIndex );
    }

    public ValueType getValueType() {
        return this.factTemplate.getFieldTemplate( this.fieldName ).getValueType();
    }

    public Object getValue(ValueResolver valueResolver,
                           final Object object) {
        return ((Fact) object).get( this.fieldName );
    }

    public int getIndex() {
        return this.fieldIndex;
    }

    public Class getExtractToClass() {
        return this.factTemplate.getFieldTemplate( fieldName ).getValueType().getClassType();
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( getExtractToClass() );
    }

    public boolean getBooleanValue(ValueResolver valueResolver,
                                   final Object object) {
        return ((Boolean) ((Fact) object).get( fieldName )).booleanValue();
    }

    public byte getByteValue(ValueResolver valueResolver,
                             final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).byteValue();
    }

    public char getCharValue(ValueResolver valueResolver,
                             final Object object) {
        return ((Character) ((Fact) object).get( fieldName )).charValue();
    }

    public double getDoubleValue(ValueResolver valueResolver,
                                 final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).doubleValue();
    }

    public float getFloatValue(ValueResolver valueResolver,
                               final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).floatValue();
    }

    public int getIntValue(ValueResolver valueResolver,
                           final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).intValue();
    }

    public long getLongValue(ValueResolver valueResolver,
                             final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).longValue();
    }

    public short getShortValue(ValueResolver valueResolver,
                               final Object object) {
        return ((Number) ((Fact) object).get( fieldName )).shortValue();
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getValue",
                                                        ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public String getNativeReadMethodName() {
        return "getValue";
    }

    public int getHashCode(ValueResolver valueResolver, Object object) {
        return getValue( valueResolver, object ).hashCode();
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public boolean isNullValue(ValueResolver valueResolver, Object object) {
        return ((Fact) object).get( this.fieldName ) == null;
    }

    public int getHashCode(Object object) {
        return getHashCode( null, object );
    }

    public Object getValue(Object object) {
        return getValue( null, object );
    }

    public boolean isNullValue(Object object) {
        return isNullValue( null, object );
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
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        FactTemplateFieldExtractor other = (FactTemplateFieldExtractor) obj;
        
        if ( factTemplate == null ) {
            if ( other.factTemplate != null ) {
            return false;
            }
        } else if ( !factTemplate.equals( other.factTemplate ) ) {
            return false;
        }
        
        if ( fieldIndex != other.fieldIndex ) {
            return false;
        }
        return true;
    }
   
}
