package org.drools.spi;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ShadowProxy;
import org.drools.base.ValueType;
import org.drools.facttemplates.Fact;

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

public class ColumnExtractor
    implements
    Extractor {

    /**
     * 
     */
    private static final long serialVersionUID = 320L;
    private ObjectType        objectType;

    public ColumnExtractor(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public Object getValue(final Object object) {
        // need to use instanceof because an object may be created in nodes like accumulate and from
        // where no shadow is applied
        return ( object instanceof ShadowProxy ) ? ((ShadowProxy)object).getShadowedObject() : object;
    }
    
    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        // @todo : this is a bit nasty, but does the trick
        if ( this.objectType.getClass() == ClassObjectType.class ) {
            return ((ClassObjectType) this.objectType).getClassType();
        } else {
            return Fact.class;
        }
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(final Object object) {
        if ( this.objectType.getValueType().isBoolean() ) {
            return ((Boolean) object).booleanValue();
        }
        throw new RuntimeDroolsException( "Conversion to boolean not supported for type: " + object.getClass() );
    }

    public byte getByteValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).byteValue();
        }
        throw new RuntimeDroolsException( "Conversion to byte not supported for type: " + object.getClass() );
    }

    public char getCharValue(final Object object) {
        if ( this.objectType.getValueType().isChar() ) {
            return ((Character) object).charValue();
        }
        throw new RuntimeDroolsException( "Conversion to char not supported for type: " + object.getClass() );
    }

    public double getDoubleValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).doubleValue();
        }
        throw new RuntimeDroolsException( "Conversion to double not supported for type: " + object.getClass() );
    }

    public float getFloatValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).floatValue();
        }
        throw new RuntimeDroolsException( "Conversion to float not supported for type: " + object.getClass() );
    }

    public int getIntValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).intValue();
        }
        throw new RuntimeDroolsException( "Conversion to int not supported for type: " + object.getClass() );
    }

    public long getLongValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).longValue();
        }
        throw new RuntimeDroolsException( "Conversion to long not supported for type: " + object.getClass() );
    }

    public short getShortValue(final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).shortValue();
        }
        throw new RuntimeDroolsException( "Conversion to short not supported for type: " + object.getClass() );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue", new Class[] { Object.class } );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("This is a bug. Please report to development team: "+e.getMessage(), e);
        }
    }

    public int getHashCode(Object object) {
        return getValue( object ).hashCode();
    }
}