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
package org.drools.base.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.common.DroolsObjectInput;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.util.ClassUtils;

/**
 * This is the supertype for the ASM generated classes for accessing a field.
 */
abstract public class BaseClassFieldReader implements ReadAccessor, Externalizable {

    private int        index;

    private Class< ? > fieldType;

    private ValueType  valueType;

    public BaseClassFieldReader() {

    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     */
    protected BaseClassFieldReader(final int index,
                                   final Class< ? > fieldType,
                                   final ValueType valueType) {
        this.index = index;
        this.fieldType = fieldType;
        this.valueType = valueType;
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
        result = PRIME * result + ( (this.fieldType != null ) ? this.fieldType.hashCode() : 0 );
        result = PRIME * result + this.index;
        result = PRIME * result + ( (this.valueType != null ) ? this.valueType.hashCode() : 0 );
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

    public int getHashCode(Object object) {
        return getHashCode( null,
                            object );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( index );
        out.writeObject( valueType );
        if (fieldType == null) {
            out.writeUTF( "" );
        } else {
            out.writeUTF( fieldType.getName() );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        index = in.readInt();
        valueType = (ValueType) in.readObject();
        String clsName = in.readUTF();

        if (!clsName.isEmpty()) {
            try {
                fieldType = in instanceof DroolsObjectInput ?
                            ClassUtils.getClassFromName( clsName, false, ( (DroolsObjectInput) in ).getClassLoader() ) :
                            ClassUtils.getClassFromName( clsName );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
    }
}
