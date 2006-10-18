package org.drools.base.field;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldValue;

public class ObjectFieldImpl
    implements
    FieldValue {

    private static final long serialVersionUID = 320;
    private Object            value;

    public ObjectFieldImpl(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return this.value.toString();
    }

    public boolean getBooleanValue() {
        if( this.value instanceof Boolean ) {
            return ((Boolean)this.value).booleanValue(); 
        }
        throw new RuntimeDroolsException("Conversion to boolean not supported for type: "+this.value.getClass());
    }

    public byte getByteValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).byteValue(); 
        }
        throw new RuntimeDroolsException("Conversion to byte not supported for type: "+this.value.getClass());
    }

    public char getCharValue() {
        if( this.value instanceof Character ) {
            return ((Character)this.value).charValue(); 
        }
        throw new RuntimeDroolsException("Conversion to char not supported for type: "+this.value.getClass());
    }

    public double getDoubleValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).doubleValue(); 
        }
        throw new RuntimeDroolsException("Conversion to double not supported for type: "+this.value.getClass());
    }

    public float getFloatValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).floatValue(); 
        }
        throw new RuntimeDroolsException("Conversion to float not supported for type: "+this.value.getClass());
    }

    public int getIntValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).intValue(); 
        }
        throw new RuntimeDroolsException("Conversion to int not supported for type: "+this.value.getClass());
    }

    public long getLongValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).longValue(); 
        }
        throw new RuntimeDroolsException("Conversion to long not supported for type: "+this.value.getClass());
    }

    public short getShortValue() {
        if( this.value instanceof Number ) {
            return ((Number)this.value).shortValue(); 
        }
        throw new RuntimeDroolsException("Conversion to short not supported for type: "+this.value.getClass());
    }
    
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || !(object instanceof ObjectFieldImpl) ) {
            return false;
        }
        final ObjectFieldImpl other = (ObjectFieldImpl) object;

        return (((this.value == null) && (other.value == null)) || ((this.value != null) && (this.value.equals( other.value ))));
    }

    public int hashCode() {
        if ( this.value != null ) {
            return this.value.hashCode();
        } else {
            return 0;
        }
    }
}