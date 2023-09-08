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
package org.drools.util;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Utility methods for math classes
 */
public class MathUtils {

    public static BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } else if( value instanceof Integer ) {
                ret = new BigDecimal( (Integer) value );
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = BigDecimal.valueOf( ((Number)value).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }

    public static BigInteger getBigInteger(Object value) {
        BigInteger ret = null;
        if ( value != null ) {
            if ( value instanceof BigInteger ) {
                ret = (BigInteger) value;
            } else if ( value instanceof String ) {
                ret = new BigInteger( (String) value );
            } else if ( value instanceof BigDecimal ) {
                ret = ((BigDecimal) value).toBigInteger();
            } else if ( value instanceof Number ) {
                ret = BigInteger.valueOf( ((Number) value).longValue() );
            } else {
                throw new ClassCastException( "Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigInteger." );
            }
        }
        return ret;
    }

    public static boolean isAddOverflow( final long op1, final long op2, final long result ) {
        // ((op1^result)&(op2^result))<0) is a shorthand for:
        // ( (op1<0 && op2<0 && result>=0) ||
        //   (op1>0 && op2>0 && result<=0) )
        return (( (op1^result) & (op2^result) ) < 0);
    }

    private MathUtils() {
        // It is not allowed to create instances of util classes.
    }
}
