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
package org.drools.model.operators;

import java.util.Collection;

import org.drools.model.functions.Operator;

public enum ContainsOperator implements Operator.SingleValue<Object, Object> {

    INSTANCE;

    @Override
    public boolean isCompatibleWithType(Class<?> type) {
        return Collection.class.isAssignableFrom( type ) || type.isArray() || type == String.class;
    }

    @Override
    public boolean eval( Object container, Object value ) {
        if ( container instanceof Collection ) {
            return (( Collection ) container).contains( value );
        }
        if ( container != null && container.getClass().isArray() && evalArray( container, value ) ) {
            return true;
        }
        if ( container instanceof String && value instanceof String ) {
            return (( String ) container).contains( ( String ) value );
        }
        return false;
    }

    private boolean evalArray( Object a, Object b ) {
        if (a instanceof Object[]) {
            for (Object o : (( Object[] ) a)) {
                if (o.equals( b )) {
                    return true;
                }
            }
        }
        if (a instanceof int[]) {
            for (int o : (( int[] ) a)) {
                if (o == (int)b) {
                    return true;
                }
            }
        }
        if (a instanceof long[]) {
            for (long o : (( long[] ) a)) {
                if (o == (long)b) {
                    return true;
                }
            }
        }
        if (a instanceof double[]) {
            for (double o : (( double[] ) a)) {
                if (o == (double)b) {
                    return true;
                }
            }
        }
        if (a instanceof float[]) {
            for (float o : (( float[] ) a)) {
                if (o == (float)b) {
                    return true;
                }
            }
        }
        if (a instanceof boolean[]) {
            for (boolean o : (( boolean[] ) a)) {
                if (o == (boolean)b) {
                    return true;
                }
            }
        }
        if (a instanceof char[]) {
            for (char o : (( char[] ) a)) {
                if (o == (char)b) {
                    return true;
                }
            }
        }
        if (a instanceof byte[]) {
            for (byte o : (( byte[] ) a)) {
                if (o == (byte)b) {
                    return true;
                }
            }
        }
        if (a instanceof short[]) {
            for (short o : (( short[] ) a)) {
                if (o == (short)b) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getOperatorName() {
        return "contains";
    }
}
