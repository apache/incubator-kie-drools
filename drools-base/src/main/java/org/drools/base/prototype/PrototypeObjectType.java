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
package org.drools.base.prototype;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueType;
import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeFactInstance;

public class PrototypeObjectType implements ObjectType {

    protected Prototype prototype;

    private boolean isEvent;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public PrototypeObjectType() {

    }

    public PrototypeObjectType(Prototype prototype) {
        this.prototype = prototype;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        prototype    = (Prototype)in.readObject();
        isEvent      = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(prototype);
        out.writeBoolean(isEvent);

    }

    public Prototype getPrototype() {
        return prototype;
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
        if ( object instanceof PrototypeFactInstance f ) {
            return this.prototype.equals( f.getPrototype() );
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
        if ( !(objectType instanceof PrototypeObjectType) ) {
            return false;
        } else {
            return this.prototype.equals( ((PrototypeObjectType) objectType).getPrototype() );
        }
    }

    @Override
    public ValueType getValueType() {
        return ValueType.PROTOTYPE_TYPE;
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
        return prototype.getName();
    }

    @Override
    public boolean isPrototype() {
        return true;
    }

    @Override
    public String getClassName() {
        return prototype.getFullName();
    }

    @Override
    public boolean hasField(String name) {
        return prototype.getField(name) != null;
    }

    public Collection<String> getFieldNames() {
        return prototype.getFieldNames();
    }

    @Override
    public String toString() {
        return "[PrototypeObjectType "+( this.isEvent ? "event=" : "template=") + this.prototype.getName() + "]";
    }

    @Override
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if (!(object instanceof PrototypeObjectType)) {
            return false;
        }

        final PrototypeObjectType other = (PrototypeObjectType) object;

        return this.prototype.equals( other.prototype );
    }

    @Override
    public int hashCode() {
        return this.prototype.hashCode();
    }
}
