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

/**
 * A Base class for primitive boolean class field
 * write accessors. This class centralizes type conversions.
 *  
 * @author etirelli
 */
public abstract class BaseBooleanClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseBooleanClassFieldWriter(final Class< ? > clazz,
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
    protected BaseBooleanClassFieldWriter(final int index,
                                          final Class< ? > fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public void setValue(final Object bean,
                         final Object value) {
        setBooleanValue( bean,
                         value == null ? false : ((Boolean) value).booleanValue() );
    }

    public abstract void setBooleanValue(final Object bean,
                                         final boolean value);

    public void setByteValue(final Object bean,
                             final byte value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from byte" );
    }

    public void setCharValue(final Object bean,
                             final char value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from char" );
    }

    public void setDoubleValue(final Object bean,
                               final double value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from double" );
    }

    public void setFloatValue(final Object bean,
                              final float value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from float" );
    }

    public void setIntValue(final Object bean,
                            final int value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from int" );
    }

    public void setLongValue(final Object bean,
                             final long value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from long" );
    }

    public void setShortValue(final Object bean,
                              final short value) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from short" );
    }

    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod( "setBooleanValue",
                                                      new Class[]{Object.class, boolean.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

}
