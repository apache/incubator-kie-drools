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
import org.drools.base.BaseClassFieldWriter;
import org.drools.base.ValueType;

/**
 * A Base class for primitive byte class field
 * writer. This class centralizes type conversions.
 *  
 */
public abstract class BaseByteClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseByteClassFieldWriter(final Class< ? > clazz,
                                    final String fieldName) {
        super( clazz,
               fieldName );
    }

    /**
     * This constructor is not supposed to be used from outside the class hierarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseByteClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public void setValue(final Object bean,
                         final Object value) {
        setByteValue( bean,
                      value == null ? 0 : ((Number) value).byteValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeDroolsException( "Conversion to byte not supported from boolean" );
    }

    public abstract void setByteValue(final Object bean,
                                      final byte value);

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeDroolsException( "Conversion to byte not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setByteValue( bean,
                      (byte) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setByteValue( bean,
                      (byte) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod( "setByteValue",
                                                      new Class[]{Object.class, byte.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

}
