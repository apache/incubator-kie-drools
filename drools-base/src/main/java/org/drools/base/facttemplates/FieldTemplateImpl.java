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

import org.drools.base.base.ValueType;

public class FieldTemplateImpl implements FieldTemplate, Externalizable {

    private static final long serialVersionUID = 510l;

    private String      name;
    private ValueType   valueType;

    public FieldTemplateImpl() {

    }

    public FieldTemplateImpl(String name, Class clazz) {
        this.name = name;
        this.valueType = clazz != null ? ValueType.OBJECT_TYPE : ValueType.determineValueType( clazz );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        valueType   = (ValueType)in.readObject();
        if (valueType != null) {
            valueType   = ValueType.determineValueType(valueType.getClassType());
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(valueType);
    }

    public String getName() {
        return this.name;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.name.hashCode();
        result = PRIME * result + this.valueType.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final FieldTemplateImpl other = (FieldTemplateImpl) object;

        return this.name.equals( other.name ) && this.valueType.equals( other.valueType );
    }

}
