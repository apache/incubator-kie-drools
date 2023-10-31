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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueType;

public class FactTemplateObjectType
    implements
    ObjectType {


    private static final long serialVersionUID = 510l;

    /** FieldTemplate. */
    protected FactTemplate    factTemplate;

    private boolean           isEvent;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public FactTemplateObjectType() {

    }

    public FactTemplateObjectType(final FactTemplate factTemplate) {
        this.factTemplate = factTemplate;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factTemplate    = (FactTemplate)in.readObject();
        isEvent         = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factTemplate);
        out.writeBoolean(isEvent);

    }
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Return the Fact Template.
     *
     * @return The Fact Template
     */
    public FactTemplate getFactTemplate() {
        return this.factTemplate;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.kie.spi.ObjectType
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Determine if the passed <code>Object</code> belongs to the object type
     * defined by this <code>objectType</code> instance.
     *
     * @param object
     *            The <code>Object</code> to test.
     *
     * @return <code>true</code> if the <code>Object</code> matches this
     *         object type, else <code>false</code>.
     */
    public boolean matches(final Object object) {
        if ( object instanceof Fact ) {
            return this.factTemplate.equals( ((Fact) object).getFactTemplate() );
        } else {
            return false;
        }
    }

    @Override
    public boolean isAssignableFrom(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean isAssignableTo(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean isAssignableFrom(ObjectType objectType) {
        if ( !(objectType instanceof FactTemplateObjectType) ) {
            return false;
        } else {
            return this.factTemplate.equals( ((FactTemplateObjectType) objectType).getFactTemplate() );
        }
    }

    @Override
    public ValueType getValueType() {
        return ValueType.FACTTEMPLATE_TYPE;
    }

    @Override
    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }

    @Override
    public Object getTypeKey() {
        return factTemplate.getName();
    }

    @Override
    public boolean isTemplate() {
        return true;
    }

    @Override
    public String getClassName() {
        return factTemplate.getPackage() + "." + factTemplate.getName();
    }

    @Override
    public boolean hasField(String name) {
        return factTemplate.getFieldTemplate(name) != null;
    }

    public Collection<String> getFieldNames() {
        return factTemplate.getFieldNames();
    }

    @Override
    public String toString() {
        return "[FactTemplateObjectType "+( this.isEvent ? "event=" : "template=") + this.factTemplate.getName() + "]";
    }

    @Override
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if (!(object instanceof FactTemplateObjectType)) {
            return false;
        }

        final FactTemplateObjectType other = (FactTemplateObjectType) object;

        return this.factTemplate.equals( other.factTemplate );
    }

    @Override
    public int hashCode() {
        return this.factTemplate.hashCode();
    }
}
