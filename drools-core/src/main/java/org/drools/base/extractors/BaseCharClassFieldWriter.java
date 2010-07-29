/**
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

public abstract class BaseCharClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseCharClassFieldWriter(final Class< ? > clazz,
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
    protected BaseCharClassFieldWriter(final int index,
                                       final Class< ? > fieldType,
                                       final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public void setValue(final Object bean,
                         final Object value) {
        setCharValue( bean,
                     value == null ? '\0' : ((Character) value).charValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeDroolsException( "Conversion to char not supported from boolean" );
    }

    public void setByteValue(final Object bean,
                             final byte value) {
        setCharValue( bean,
                      (char) value );
    }

    public abstract void setCharValue(final Object object,
                                      final char value);

    public void setDoubleValue(final Object bean,
                               final double value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        setCharValue( bean,
                      (char) value );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        setCharValue( bean,
                      (char) value );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod( "setCharValue",
                                                      new Class[]{Object.class, char.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }
}
