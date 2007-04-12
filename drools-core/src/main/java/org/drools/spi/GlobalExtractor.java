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
import java.util.Map;

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
    private final String            key;
    private Map               map;
    private ObjectType        objectType;

    public GlobalExtractor(final String key,
                           final Map map) {
        this.key = key;
        this.map = map;
        this.objectType = new ClassObjectType( Object.class );
    }

    public Object getValue(final Object object) {
        return this.map.get( this.key );
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        return Object.class;
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Boolean ) {
                return ((Boolean) value).booleanValue();
            }
            throw new RuntimeDroolsException( "Conversion to boolean not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to boolean not supported for a null value" );
    }

    public byte getByteValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).byteValue();
            }
            throw new RuntimeDroolsException( "Conversion to byte not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to byte not supported for a null value" );
    }

    public char getCharValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Character ) {
                return ((Character) value).charValue();
            }
            throw new RuntimeDroolsException( "Conversion to char not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to char not supported for a null value" );
    }

    public double getDoubleValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).doubleValue();
            }
            throw new RuntimeDroolsException( "Conversion to double not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to double not supported for a null value" );
    }

    public float getFloatValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).floatValue();
            }
            throw new RuntimeDroolsException( "Conversion to float not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to float not supported for a null value" );
    }

    public int getIntValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).intValue();
            }
            throw new RuntimeDroolsException( "Conversion to int not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to int not supported for a null value" );
    }

    public long getLongValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).longValue();
            }
            throw new RuntimeDroolsException( "Conversion to long not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to long not supported for a null value" );
    }

    public short getShortValue(final Object object) {
        final Object value = this.map.get( this.key );
        if ( value != null ) {
            if ( value instanceof Number ) {
                return ((Number) value).shortValue();
            }
            throw new RuntimeDroolsException( "Conversion to short not supported for type: " + value.getClass() );
        }
        throw new RuntimeDroolsException( "Conversion to short not supported for a null value" );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        final Object value = this.map.get( this.key );
        return value != null ? value.hashCode() : 0;
    }

    public int hashCode() {
        return this.objectType.hashCode();
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( !(obj instanceof GlobalExtractor) ) {
            return false;
        }
        final GlobalExtractor other = (GlobalExtractor) obj;
        final Object value = this.map.get( this.key );
        final Object othervalue = other.map.get( this.key );
        return value == null ? othervalue == null : value.equals( othervalue );
    }
}
