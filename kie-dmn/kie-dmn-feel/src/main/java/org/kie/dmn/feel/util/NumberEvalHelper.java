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
package org.kie.dmn.feel.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.feel.util.StringEvalHelper.removeTrailingZeros;

public class NumberEvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger( NumberEvalHelper.class );

    public static BigDecimal getBigDecimalOrNull(Object value) {
        if ( value instanceof BigDecimal ) {
            return (BigDecimal) value;
        }

        if ( value instanceof BigInteger ) {
            return new BigDecimal((BigInteger) value, MathContext.DECIMAL128);
        }

        if ( value instanceof Double || value instanceof Float ) {
            String stringVal = value.toString();
            if (stringVal.equals("NaN") || stringVal.equals("Infinity") || stringVal.equals("-Infinity")) {
                return null;
            }
            // doubleValue() sometimes produce rounding errors, so we need to use toString() instead
            // We also need to remove trailing zeros, if there are some so for 10d we get BigDecimal.valueOf(10)
            // instead of BigDecimal.valueOf(10.0).
            return new BigDecimal( removeTrailingZeros(value.toString()), MathContext.DECIMAL128 );
        }

        if ( value instanceof Number ) {
            return new BigDecimal( ((Number) value).longValue(), MathContext.DECIMAL128 );
        }

        if ( value instanceof String ) {
            try {
                // we need to remove leading zeros to prevent octal conversion
                return new BigDecimal(((String) value).replaceFirst("^0+(?!$)", ""), MathContext.DECIMAL128);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    public static Object coerceNumber(Object value) {
        if ( value instanceof Number && !(value instanceof BigDecimal) ) {
            return getBigDecimalOrNull( value );
        } else {
            return value;
        }
    }

}
