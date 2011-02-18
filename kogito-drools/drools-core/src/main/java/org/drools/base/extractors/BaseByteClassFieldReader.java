/*
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

package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldReader;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;

/**
 * A Base class for primitive byte class field
 * extractors. This class centralizes type conversions.
 *  
 * @author etirelli
 */
public abstract class BaseByteClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseByteClassFieldReader(final Class clazz,
                                       final String fieldName) {
        super( clazz,
               fieldName );
    }

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseByteClassFieldReader(final int index,
                                          final Class fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        return new Byte( getByteValue( workingMemory, object ) );
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from byte" );
    }

    public abstract byte getByteValue(InternalWorkingMemory workingMemory, Object object);

    public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from byte" );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }

    public int getIntValue(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }

    public long getLongValue(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }

    public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory, final Object object) {
        return false;
    }
    
    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getByteValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {
        return getByteValue( workingMemory, object );
    }
}
