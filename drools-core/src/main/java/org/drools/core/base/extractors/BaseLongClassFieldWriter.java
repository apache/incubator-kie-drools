/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.extractors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.core.base.ValueType;

public abstract class BaseLongClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseLongClassFieldWriter(final Class< ? > clazz,
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
    protected BaseLongClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseLongClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setLongValue( bean,
                      value == null ? 0 : ((Number) value).longValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to long not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setLongValue( bean,
                      (long) value );

    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeException( "Conversion to long not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        setLongValue( bean,
                      (long) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setLongValue( bean,
                      (long) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setLongValue( bean,
                      (long) value );
    }

    public abstract void setLongValue(final Object object,
                                      final long value);

    public void setShortValue(final Object bean,
                              final short value) {
        setLongValue( bean,
                      (long) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod( "setLongValue",
                                                      new Class[]{Object.class, long.class} );
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
