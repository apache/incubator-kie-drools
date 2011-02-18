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
 * A Base class for primitive boolean class field
 * extractors. This class centralizes type conversions.
 *  
 * @author etirelli
 */
public abstract class BaseBooleanClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseBooleanClassFieldReader(final Class< ? > clazz,
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
    protected BaseBooleanClassFieldReader(final int index,
                                          final Class< ? > fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return getBooleanValue( workingMemory,
                                object ) ? Boolean.TRUE : Boolean.FALSE;
    }

    public abstract boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                            Object object);

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        throw new RuntimeDroolsException( "Conversion to byte not supported from boolean" );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from boolean" );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        throw new RuntimeDroolsException( "Conversion to double not supported from boolean" );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        throw new RuntimeDroolsException( "Conversion to float not supported from boolean" );
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        throw new RuntimeDroolsException( "Conversion to int not supported from boolean" );
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        throw new RuntimeDroolsException( "Conversion to long not supported from boolean" );
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        throw new RuntimeDroolsException( "Conversion to short not supported from boolean" );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getBooleanValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return getBooleanValue( workingMemory,
                                object ) ? 1231 : 1237;
    }

}
