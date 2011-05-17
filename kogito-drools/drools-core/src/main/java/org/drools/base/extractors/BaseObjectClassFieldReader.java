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
import java.util.Date;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldReader;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;

public abstract class BaseObjectClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseObjectClassFieldReader() {

    }

    protected BaseObjectClassFieldReader(final int index,
                                         final Class< ? > fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseObjectClassFieldReader(final Class< ? > clazz,
                                      final String fieldName) {
        super( clazz,
               fieldName );
    }

    public abstract Object getValue(InternalWorkingMemory workingMemory,
                                    Object object);

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Boolean ) {
            return ((Boolean) value).booleanValue();
        }
        throw new RuntimeDroolsException( "Conversion to boolean not supported from " + value.getClass().getName() );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).byteValue();
        } else if ( value instanceof Date ) {
            return (byte) ((Date) value).getTime();
        }
        throw new RuntimeDroolsException( "Conversion to byte not supported from " + value.getClass().getName() );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Character ) {
            return ((Character) value).charValue();
        } else if ( value instanceof String && ((String) value).length() == 1 ) {
            return ((String) value).charAt( 0 );
        }
        throw new RuntimeDroolsException( "Conversion to char not supported from " + value.getClass().getName() );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).doubleValue();
        } else if ( value instanceof Date ) {
            return (double) ((Date) value).getTime();
        }
        throw new RuntimeDroolsException( "Conversion to double not supported from " + value.getClass().getName() );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).floatValue();
        } else if ( value instanceof Date ) {
            return (float) ((Date) value).getTime();
        }
        throw new RuntimeDroolsException( "Conversion to float not supported from " + value.getClass().getName() );
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).intValue();
        } else if ( value instanceof Date ) {
            return (int) ((Date) value).getTime();
        } else if( value instanceof Character ) {
            return ((Character) value).charValue();
        }
        throw new RuntimeDroolsException( "Conversion to int not supported from " + value.getClass().getName() );
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).longValue();
        } else if ( value instanceof Date ) {
            return ((Date) value).getTime();
        } else if( value instanceof Character ) {
            return ((Character) value).charValue();
        }
        throw new RuntimeDroolsException( "Conversion to long not supported from " + value.getClass().getName() );
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        // this can be improved by generating specific
        // bytecode generation in the subclass, avoiding the if instanceof
        final Object value = getValue( workingMemory,
                                       object );

        if ( value instanceof Number ) {
            return ((Number) value).shortValue();
        } else if ( value instanceof Date ) {
            return (short) ((Date) value).getTime();
        }
        throw new RuntimeDroolsException( "Conversion to short not supported from " + value.getClass().getName() );
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
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        final Object value = getValue( workingMemory,
                                       object );
        return (value != null) ? value.hashCode() : 0;
    }

}
