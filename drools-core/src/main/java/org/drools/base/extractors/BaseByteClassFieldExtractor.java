/*
 * Copyright 2005 JBoss Inc
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
import org.drools.base.BaseClassFieldExtractor;
import org.drools.base.ValueType;

/**
 * A Base class for primitive byte class field
 * extractors. This class centralizes type conversions.
 *  
 * @author etirelli
 */
public abstract class BaseByteClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 2031113412868487706L;

    public BaseByteClassFieldExtractor(final Class clazz,
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
    protected BaseByteClassFieldExtractor(final int index,
                                          final Class fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(final Object object) {
        return new Long( getByteValue( object ) );
    }

    public boolean getBooleanValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from byte" );
    }

    public abstract byte getByteValue(Object object);

    public char getCharValue(final Object object) {
        throw new RuntimeDroolsException( "Conversion to char not supported from byte" );
    }

    public double getDoubleValue(final Object object) {
        return getByteValue( object );
    }

    public float getFloatValue(final Object object) {
        return getByteValue( object );
    }

    public int getIntValue(final Object object) {
        return getByteValue( object );
    }

    public long getLongValue(final Object object) {
        return getByteValue( object );
    }

    public short getShortValue(final Object object) {
        return getByteValue( object );
    }

    public boolean isNullValue(final Object object) {
        return false;
    }
    
    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getByteValue",
                                                      new Class[]{Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public int getHashCode(final Object object) {
        return getByteValue( object );
    }
}
