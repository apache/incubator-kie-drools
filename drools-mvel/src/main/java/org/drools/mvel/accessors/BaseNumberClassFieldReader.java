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

package org.drools.mvel.accessors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldReader;
import org.drools.core.base.ValueType;
import org.drools.core.common.ReteEvaluator;

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

    public abstract Object getValue(ReteEvaluator reteEvaluator,
                                    Object object);

    public boolean getBooleanValue(ReteEvaluator reteEvaluator,
                                   final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from Number" );
    }

    public byte getByteValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Number) value).byteValue();
    }

    public char getCharValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Character) value).charValue();
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator,
                                 final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Number) value).doubleValue();
    }

    public float getFloatValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Number) value).floatValue();
    }

    public int getIntValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Number) value).intValue();
    }

    public long getLongValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        
        return ((Number) value).longValue();
    }

    public short getShortValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        return ((Number) value).shortValue();
    }

    public boolean isNullValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        if ( object == null ) {
            return true;
        } else {
            return getValue( reteEvaluator,
                             object ) == null;
        }
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getMethod( getNativeReadMethodName(),
                                              new Class[]{ReteEvaluator.class, Object.class} );
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

    public int getHashCode(ReteEvaluator reteEvaluator,
                           final Object object) {
        final Object value = getValue( reteEvaluator,
                                       object );
        return (value != null) ? value.hashCode() : 0;
    }

}
