/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldWriter;
import org.drools.base.base.ValueType;

public abstract class BaseDecimalClassFieldWriter extends BaseClassFieldWriter {

    private static final long serialVersionUID = 510l;

    public BaseDecimalClassFieldWriter(final Class< ? > clazz,
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
    protected BaseDecimalClassFieldWriter(final int index,
                                          final Class< ? > fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseDecimalClassFieldWriter() {
    }

    public void setValue(final Object bean,
                         final Object value) {
        setDecimalValue( bean,
                        value == null ? 0 : ((Number) value).doubleValue() );
    }

    public void setBooleanValue(final Object bean,
                                final boolean value) {
        throw new RuntimeException( "Conversion to double not supported from boolean" );
    }


    public abstract void setDecimalValue(final Object object,
                                         final double value);

    public void setDoubleValue(final Object bean,
                               final double value) {
        setDecimalValue( bean,
                        value );
    }

    public void setWholeNumberValue(final Object bean,
                                    final long value) {
        setDecimalValue( bean,
                        (double) value );
    }


    public Method getNativeWriteMethod() {
        try {
            return this.getClass().getDeclaredMethod("setDecimalValue",
                                                     Object.class, double.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

}
