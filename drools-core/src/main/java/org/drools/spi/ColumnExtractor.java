package org.drools.spi;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
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
    private static final long serialVersionUID = 9055898682913511836L;
    private ObjectType        objectType;

    public ColumnExtractor(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public Object getValue(final Object object) {
        return object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        // @todo : this is a bit nasty, but does the trick
        if ( objectType.getClass() == ClassObjectType.class ) {
            return ((ClassObjectType) objectType).getClassType();
        } else {
            return Fact.class;
        }
    }

    public ValueType getValueType() {
        return objectType.getValueType();
    }

    public boolean getBooleanValue(Object object) {
        if( this.objectType.getValueType().isBoolean() ) {
            return ((Boolean)object).booleanValue(); 
        }
        throw new RuntimeDroolsException("Conversion to boolean not supported for type: "+object.getClass());
    }

    public byte getByteValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).byteValue(); 
        }
        throw new RuntimeDroolsException("Conversion to byte not supported for type: "+object.getClass());
    }

    public char getCharValue(Object object) {
        if( this.objectType.getValueType().isChar() ) {
            return ((Character)object).charValue(); 
        }
        throw new RuntimeDroolsException("Conversion to char not supported for type: "+object.getClass());
    }

    public double getDoubleValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).doubleValue(); 
        }
        throw new RuntimeDroolsException("Conversion to double not supported for type: "+object.getClass());
    }

    public float getFloatValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).floatValue(); 
        }
        throw new RuntimeDroolsException("Conversion to float not supported for type: "+object.getClass());
    }

    public int getIntValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).intValue(); 
        }
        throw new RuntimeDroolsException("Conversion to int not supported for type: "+object.getClass());
    }

    public long getLongValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).longValue(); 
        }
        throw new RuntimeDroolsException("Conversion to long not supported for type: "+object.getClass());
    }

    public short getShortValue(Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number)object).shortValue(); 
        }
        throw new RuntimeDroolsException("Conversion to short not supported for type: "+object.getClass());
    }

}