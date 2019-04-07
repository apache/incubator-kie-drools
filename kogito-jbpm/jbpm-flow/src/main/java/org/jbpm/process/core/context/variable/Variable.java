/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.context.variable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.TypeObject;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.UndefinedDataType;
import org.jbpm.process.core.ValueObject;

/**
 * Default implementation of a variable.
 * 
 */
public class Variable implements TypeObject, ValueObject, Serializable {

    private static final long serialVersionUID = 510l;

    private String name;
    private DataType type;
    private Object value;
    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Variable() {
        this.type = UndefinedDataType.getInstance();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DataType getType() {
        return this.type;
    }

    public void setType(final DataType type) {
        if ( type == null ) {
            throw new IllegalArgumentException( "type is null" );
        }
        this.type = type;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(final Object value) {
        if ( this.type.verifyDataType( value ) ) {
            this.value = value;
        } else {
            final StringBuilder sb = new StringBuilder();
            sb.append( "Value <" );
            sb.append( value );
            sb.append( "> is not valid for datatype: " );
            sb.append( this.type );
            throw new IllegalArgumentException( sb.toString() );
        }
    }

    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }
    
    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }
    
    public Map<String, Object> getMetaData() {
    	return this.metaData;
    }
    
    public String toString() {
        return this.name;
    }
}
