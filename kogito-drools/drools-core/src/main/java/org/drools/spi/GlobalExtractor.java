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
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.util.ClassUtils;

/**
 * This is a global variable extractor used to get a global variable value
 *
 * @author etirelli
 */
public class GlobalExtractor
    implements
    Extractor {

    private static final long serialVersionUID = 400L;
    private String            key;
    private ObjectType        objectType;

    public GlobalExtractor() {

    }
    public GlobalExtractor(final String key,
                           final Map map) {
        this.key = key;
        this.objectType = new ClassObjectType( (Class) map.get( this.key ));
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        return workingMemory.getGlobal( key );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        key = (String)in.readObject();
        objectType  = (ObjectType)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(key);
        out.writeObject(objectType);
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        return this.objectType.getValueType().getClassType();
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( this.objectType.getValueType().getClassType() );
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isBoolean() ) {
            return ((Boolean) workingMemory.getGlobal( key )).booleanValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a boolean.");
    }

    public byte getByteValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).byteValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a byte.");
    }

    public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isChar() ) {
            return ((Character) workingMemory.getGlobal( key )).charValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a char.");
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).doubleValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a double.");
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).floatValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a float.");
    }

    public int getIntValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).intValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into an int.");
    }

    public long getLongValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).longValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a long.");
    }

    public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
        if( this.objectType.getValueType().isNumber() ) {
            return ((Number) workingMemory.getGlobal( key )).shortValue();
        }
        throw new ClassCastException("Not possible to convert global '"+key+"' into a short.");
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {
        final Object value = getValue( workingMemory, object );
        return (value != null) ? value.hashCode() : 0;
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
        return ( key == null ? other.key == null : key.equals( other.key ) ) &&
               ( this.objectType == null ? other.objectType == null : this.objectType.equals( other.objectType ));
    }

    public boolean isNullValue( InternalWorkingMemory workingMemory, Object object ) {
        final Object value = getValue( workingMemory, object );
        return value == null;
    }

    public boolean isGlobal() {
        return true;
    }
}
