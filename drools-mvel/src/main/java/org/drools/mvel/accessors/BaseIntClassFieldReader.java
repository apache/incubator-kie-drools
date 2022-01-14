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

public abstract class BaseIntClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseIntClassFieldReader(final int index,
                                         final Class fieldType,
                                         final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public BaseIntClassFieldReader() {
    }

    public Object getValue(ReteEvaluator reteEvaluator, final Object object) {
        return getIntValue( reteEvaluator, object );
    }

    public boolean getBooleanValue(ReteEvaluator reteEvaluator, final Object object) {
        throw new RuntimeException( "Conversion to boolean not supported from int" );
    }

    public byte getByteValue(ReteEvaluator reteEvaluator, final Object object) {
        return (byte) getIntValue( reteEvaluator, object );

    }

    public char getCharValue(ReteEvaluator reteEvaluator, final Object object) {
        throw new RuntimeException( "Conversion to char not supported from int" );
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator, final Object object) {
        return getIntValue( reteEvaluator, object );
    }

    public float getFloatValue(ReteEvaluator reteEvaluator, final Object object) {
        return getIntValue( reteEvaluator, object );
    }

    public abstract int getIntValue(ReteEvaluator reteEvaluator, Object object);

    public long getLongValue(ReteEvaluator reteEvaluator, final Object object) {
        return getIntValue( reteEvaluator, object );
    }

    public short getShortValue(ReteEvaluator reteEvaluator, final Object object) {
        return (short) getIntValue( reteEvaluator, object );
    }

    public boolean isNullValue(ReteEvaluator reteEvaluator, final Object object) {
        return false;
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getIntValue",
                                                      new Class[]{ReteEvaluator.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public int getHashCode(ReteEvaluator reteEvaluator, final Object object) {
        return getIntValue( reteEvaluator, object );
    }
}
