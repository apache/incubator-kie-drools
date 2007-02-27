/*
 * Copyright 2006 JBoss Inc
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

package org.drools.spi;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;

/**
 * A special extractor for globals
 * 
 * @author etirelli
 */
public class GlobalExtractor
    implements
    Extractor {

    private static final long serialVersionUID = -756967384190918798L;
    private Object            value;
    private ObjectType        objectType;

    public GlobalExtractor(final Object object) {
        this.value = object;
        this.objectType = new ClassObjectType( object.getClass() );
    }

    public Object getValue(final Object object) {
        return value;
    }
    
    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        return this.value.getClass();
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(final Object object) {
        if ( this.objectType.getValueType().isBoolean() ) {
            return ((Boolean) value).booleanValue();
        }
        throw new RuntimeDroolsException( "Conversion to boolean not supported for type: " + value.getClass() );
    }

    public byte getByteValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).byteValue();
        }
        throw new RuntimeDroolsException( "Conversion to byte not supported for type: " + value.getClass() );
    }

    public char getCharValue(final Object object) {
        if ( this.objectType.getValueType().isChar() ) {
            return ((Character) value).charValue();
        }
        throw new RuntimeDroolsException( "Conversion to char not supported for type: " + value.getClass() );
    }

    public double getDoubleValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).doubleValue();
        }
        throw new RuntimeDroolsException( "Conversion to double not supported for type: " + value.getClass() );
    }

    public float getFloatValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).floatValue();
        }
        throw new RuntimeDroolsException( "Conversion to float not supported for type: " + value.getClass() );
    }

    public int getIntValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).intValue();
        }
        throw new RuntimeDroolsException( "Conversion to int not supported for type: " + value.getClass() );
    }

    public long getLongValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).longValue();
        }
        throw new RuntimeDroolsException( "Conversion to long not supported for type: " + value.getClass() );
    }

    public short getShortValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) value).shortValue();
        }
        throw new RuntimeDroolsException( "Conversion to short not supported for type: " + value.getClass() );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue", new Class[] { Object.class } );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("This is a bug. Please report to development team: "+e.getMessage(), e);
        }
    }

    public int getHashCode(Object object) {
        return value.hashCode();
    }
    
    public int hashCode() {
        return this.objectType.hashCode();
    }
    
    public boolean equals(Object obj) {
        if( this == obj ) {
            return true;
        }
        if( ! ( obj instanceof GlobalExtractor ) ) {
            return false;
        }
        GlobalExtractor other = (GlobalExtractor) obj;
        return this.value.equals( other.value );
    }
}
