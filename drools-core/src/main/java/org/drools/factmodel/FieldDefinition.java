/**
 * Copyright 2010 JBoss Inc
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

package org.drools.factmodel;

/*
 * Copyright 2008 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.ClassFieldAccessor;
import org.drools.definition.type.FactField;

/**
 * Declares a field to be dynamically generated.
 *
 * @author etirelli
 */
public class FieldDefinition
    implements
    FactField {
    private String             name     = null;
    private String             type     = null;
    private boolean            key      = false;

    private ClassFieldAccessor accessor = null;

    public FieldDefinition() {
    }

    /**
     * Default constructor
     * 
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type) {
        this( name,
              type,
              false );
    }

    /**
     * Default constructor
     * 
     * @param name the field's name
     * @param type the fully qualified fields type
     */
    public FieldDefinition(String name,
                           String type,
                           boolean key) {
        this.name = name;
        this.type = type;
        this.key = key;
    }

    public void setReadWriteAccessor(ClassFieldAccessor accessor) {
        this.accessor = accessor;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = (String) in.readObject();
        this.type = (String) in.readObject();
        this.key = in.readBoolean();
        this.accessor = (ClassFieldAccessor) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.name );
        out.writeObject( this.type );
        out.writeBoolean( this.key );
        out.writeObject( this.accessor );
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the fully qualified type.
     */
    public String getTypeName() {
        return this.type;
    }

    /**
     * @param type The fully qualified type to set.
     */
    public void setTypeName(String type) {
        this.type = type;
    }

    public Class< ? > getType() {
        return this.accessor.getFieldType();
    }

    /**
     * @return Returns the key.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * @param key The key to set.
     */
    public void setKey(boolean key) {
        this.key = key;
    }

    /**
     * Creates the String name for the get method for a field with the given name and type
     * @param name
     * @param type
     * @return
     */
    public String getReadMethod() {
        String prefix = null;
        if ( "boolean".equals( this.type ) ) {
            prefix = "is";
        } else {
            prefix = "get";
        }
        return prefix + this.name.substring( 0,
                                             1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * Creates the String name for the set method for a field with the given name and type
     * 
     * @param name
     * @param type
     * @return
     */
    public String getWriteMethod() {
        return "set" + this.name.substring( 0,
                                            1 ).toUpperCase() + this.name.substring( 1 );
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object o) {
        return this.getName().equals( ((FieldDefinition) o).getName() );
    }

    /**
     * @return Returns the field extractor
     */
    public ClassFieldAccessor getFieldAccessor() {
        return this.accessor;
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        return this.getName().hashCode();
    }

    /**
     * Sets the value of this attribute in the target
     * bean instance
     * 
     * @param bean the target bean instance where the attribute shall be set
     * @param value the value to set the attribute to
     * 
     */
    public void setValue(Object bean,
                         Object value) {
        this.accessor.setValue( bean,
                                value );
    }

    /**
     * Returns the value of this attribute in the target bean instance
     * 
     * @param bean the target bean instance
     * 
     * @return target bean instance attribute value
     *  
     */
    public Object getValue(Object bean) {
        return this.accessor.getValue( bean );
    }

    public Object get(Object bean) {
        return this.accessor.getValue( bean );
    }

    public void set(Object bean,
                    Object value) {
        this.accessor.setValue( bean,
                                value );
    }

}