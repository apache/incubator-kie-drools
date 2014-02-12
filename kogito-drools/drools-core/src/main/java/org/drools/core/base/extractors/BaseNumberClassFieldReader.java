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

package org.drools.core.base.extractors;

import java.lang.reflect.Method;

import org.drools.core.base.BaseClassFieldReader;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalWorkingMemory;

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

    public abstract Object getValue(InternalWorkingMemory workingMemory,
                                    Object object);

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from Number" );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Number) value).byteValue();
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Character) value).charValue();
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Number) value).doubleValue();
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Number) value).floatValue();
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Number) value).intValue();
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        
        return ((Number) value).longValue();
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        return ((Number) value).shortValue();
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        if ( object == null ) {
            return true;
        } else {
            return getValue( workingMemory,
                             object ) == null;
        }
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getMethod( getNativeReadMethodName(),
                                              new Class[]{InternalWorkingMemory.class, Object.class} );
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

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        return (value != null) ? value.hashCode() : 0;
    }

}
