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
package org.drools.core.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.base.ClassFieldInspector;
import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.WriteAccessor;

/**
 * This is the supertype for the ASM generated classes for writing values into fields.
 */
abstract public class BaseClassFieldWriter implements WriteAccessor {
    private int        index;

    private Class< ? > fieldType;

    private ValueType valueType;

    public BaseClassFieldWriter() {
    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     */
    protected BaseClassFieldWriter(final int index,
                                   final Class< ? > fieldType,
                                   final ValueType valueType) {
        this.index = index;
        this.fieldType = fieldType;
        this.valueType = valueType;
    }

    /**
     * This is the constructor to be used
     */
    public BaseClassFieldWriter(final Class< ? > clazz,
                                final String fieldName) {
        try {
            final ClassFieldInspector inspector = CoreComponentsBuilder.get().createClassFieldInspector( clazz );
            this.index = inspector.getFieldNames().get( fieldName );
            this.fieldType = inspector.getFieldType( fieldName );
            this.valueType = ValueType.determineValueType( this.fieldType );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        index = in.readInt();
        fieldType = (Class< ? >) in.readObject();
        valueType = (ValueType) in.readObject();
        if ( valueType != null ) {
            valueType = ValueType.determineValueType( valueType.getClassType() );
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( index );
        out.writeObject( fieldType );
        out.writeObject( valueType );
    }

    public int getIndex() {
        return this.index;
    }

    public Class< ? > getFieldType() {
        return this.fieldType;
    }

    public ValueType getValueType() {
        return this.valueType;
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
        if ( !(object instanceof BaseClassFieldWriter) ) {
            return false;
        }
        final BaseClassFieldWriter other = (BaseClassFieldWriter) object;
        return this.fieldType == other.fieldType && this.index == other.index && this.valueType.equals( other.valueType );
    }
}
