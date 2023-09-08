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
package org.drools.compiler.rule.builder.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.drl.ast.descr.LiteralRestrictionDescr;

import static org.drools.base.util.TimeIntervalParser.getTimestampFromDate;

public class PatternBuilderUtil {

    public static String getNormalizeDate( ValueType vtype, FieldValue field ) {
        Object value = field.getValue();
        if (value == null) {
            return "null";
        }

        if (vtype == ValueType.DATE_TYPE) {
            return " new java.util.Date(" + getTimestampFromDate( value ) + ")";
        }
        if (vtype == ValueType.LOCAL_DATE_TYPE) {
            if (value instanceof LocalDate) {
                return " java.time.LocalDate.parse(\"" + value + "\")";
            }
            if (value instanceof LocalDateTime) {
                String dateValue = value.toString();
                int timePos = dateValue.indexOf('T');
                if (timePos > 0) {
                    dateValue = dateValue.substring(0, timePos);
                }
                return " java.time.LocalDate.parse(\"" + dateValue + "\")";
            }
            return " java.time.Instant.ofEpochMilli(" + getTimestampFromDate( value ) + ").atZone(java.time.ZoneId.systemDefault()).toLocalDate()";
        }
        if (vtype == ValueType.LOCAL_TIME_TYPE) {
            if (value instanceof LocalDate) {
                return " java.time.LocalDateTime.parse(\"" + value + "T00:00:00\")";
            }
            if (value instanceof LocalDateTime) {
                return " java.time.LocalDateTime.parse(\"" + value + "\")";
            }
            return " java.time.Instant.ofEpochMilli(" + getTimestampFromDate( value ) + ").atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()";
        }

        throw new IllegalArgumentException( "Unsupported type " + vtype );
    }

    public static String normalizeStringOperator( String leftValue, String rightValue, LiteralRestrictionDescr restrictionDescr ) {
        String method = restrictionDescr.getParameterText();
        if (method.equals("length")) {
            return leftValue + ".length()" + (restrictionDescr.isNegated() ? " != " : " == ") + rightValue;
        }
        return (restrictionDescr.isNegated() ? "!" : "") + leftValue + "." + method + "(" + rightValue + ")";
    }

    public static String normalizeEmptyKeyword( String expr, String operator ) {
        return expr.startsWith("empty") && (operator.equals("==") || operator.equals("!=")) && !Character.isJavaIdentifierPart(expr.charAt(5)) ?
                "isEmpty()" + expr.substring(5) :
                expr;
    }

}
