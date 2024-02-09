/**
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

import org.drools.base.base.ValueResolver;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.base.base.ValueType;

public abstract class BaseNumberClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseNumberClassFieldReader() {

    }

    protected BaseNumberClassFieldReader(final int index,
                                         final Class< ? > fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public abstract Object getValue(ValueResolver valueResolver,
                                    Object object);

    public boolean getBooleanValue(ValueResolver valueResolver,
                                   final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from Number" );
    }

    public byte getByteValue(final ValueResolver valueResolver,
                             final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Number) value).byteValue();
    }

    public char getCharValue(final ValueResolver valueResolver,
                             final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Character) value).charValue();
    }

    public double getDoubleValue(final ValueResolver valueResolver,
                                 final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Number) value).doubleValue();
    }

    public float getFloatValue(final ValueResolver valueResolver,
                               final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Number) value).floatValue();
    }

    public int getIntValue(final ValueResolver valueResolver,
                           final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Number) value).intValue();
    }

    public long getLongValue(final ValueResolver valueResolver,
                             final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        
        return ((Number) value).longValue();
    }

    public short getShortValue(final ValueResolver valueResolver,
                               final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        return ((Number) value).shortValue();
    }

    public boolean isNullValue(final ValueResolver valueResolver,
                               final Object object) {
        if ( object == null ) {
            return true;
        } else {
            return getValue( valueResolver,
                             object ) == null;
        }
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getMethod(getNativeReadMethodName(),
                                             ValueResolver.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public String getNativeReadMethodName() {
        Class<?> type = getExtractToClass();
        if (!type.isPrimitive()) {
            return "getValue";
        }
        return "get" + type.getName().substring(0, 1).toUpperCase() + type.getName().substring(1) + "Value";
    }

    public int getHashCode(final ValueResolver valueResolver,
                           final Object object) {
        final Object value = getValue( valueResolver,
                                       object );
        return (value != null) ? value.hashCode() : 0;
    }

}
