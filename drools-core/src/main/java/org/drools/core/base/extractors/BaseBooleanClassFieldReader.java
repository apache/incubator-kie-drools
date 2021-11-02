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

import org.drools.core.base.BaseClassFieldReader;
import org.drools.core.base.ValueType;
import org.drools.core.common.ReteEvaluator;

/**
 * A Base class for primitive boolean class field
 * extractors. This class centralizes type conversions.
 */
public abstract class BaseBooleanClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseBooleanClassFieldReader(final int index,
                                          final Class< ? > fieldType,
                                          final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseBooleanClassFieldReader() {
    }

    public Object getValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return getBooleanValue( reteEvaluator,
                                object ) ? Boolean.TRUE : Boolean.FALSE;
    }

    public abstract boolean getBooleanValue(ReteEvaluator reteEvaluator,
                                            Object object);

    public byte getByteValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        throw new RuntimeException( "Conversion to byte not supported from boolean" );
    }

    public char getCharValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        throw new RuntimeException( "Conversion to char not supported from boolean" );
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator,
                                 final Object object) {
        throw new RuntimeException( "Conversion to double not supported from boolean" );
    }

    public float getFloatValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        throw new RuntimeException( "Conversion to float not supported from boolean" );
    }

    public int getIntValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        throw new RuntimeException( "Conversion to int not supported from boolean" );
    }

    public long getLongValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        throw new RuntimeException( "Conversion to long not supported from boolean" );
    }

    public short getShortValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        throw new RuntimeException( "Conversion to short not supported from boolean" );
    }

    public boolean isNullValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getBooleanValue",
                                                      new Class[]{ReteEvaluator.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ReteEvaluator reteEvaluator,
                           final Object object) {
        return getBooleanValue( reteEvaluator,
                                object ) ? 1231 : 1237;
    }

}
