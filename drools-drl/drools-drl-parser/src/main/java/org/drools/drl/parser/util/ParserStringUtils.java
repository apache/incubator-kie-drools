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
package org.drools.drl.parser.util;

/**
 * String utilities used by DRL Parser. Not dependent on ANTLR version.
 */
public class ParserStringUtils {

    private ParserStringUtils() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Strip string delimiters (e.g. "foo" -> foo)
     */
    public static String safeStripStringDelimiters(String value) {
        if (value != null) {
            value = value.trim();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    public static String safeStripDelimiters(String value, String left, String right) {
        if (value != null) {
            value = value.trim();
            if (value.length() >= left.length() + right.length() &&
                    value.startsWith(left) && value.endsWith(right)) {
                value = value.substring(left.length(),
                        value.length() - right.length());
            }
        }
        return value;
    }

    /**
     * Append a prefix to a grouped constraint.
     * Even if the constraint contains || and/or &&, append the prefix to each element.
     */
    public static String appendPrefix(String prefix, String expr) {
        if (prefix.length() == 0) {
            return expr;
        }
        StringBuilder sb = new StringBuilder();
        appendPrefixToOrExpression(sb, prefix, expr);
        return sb.toString();
    }

    private static void appendPrefixToOrExpression(StringBuilder sb, String prefix, String expr) {
        int start = 0;
        int end = expr.indexOf("||");
        do {
            if (start > 0) {
                sb.append(" || ");
            }
            appendPrefixToAndExpression(sb, prefix, end > 0 ? expr.substring(start, end) : expr.substring(start));
            start = end + 2;
            end = expr.indexOf("||", start);
        } while (start > 1);
    }

    private static void appendPrefixToAndExpression(StringBuilder sb, String prefix, String expr) {
        int start = 0;
        int end = expr.indexOf("&&");
        do {
            if (start > 0) {
                sb.append(" && ");
            }
            sb.append(appendPrefixToExpression(prefix, end > 0 ? expr.substring(start, end) : expr.substring(start)));
            start = end + 2;
            end = expr.indexOf("&&", start);
        } while (start > 1);
    }

    private static String appendPrefixToExpression(String prefix, String expr) {
        expr = expr.trim();
        int colonPos = expr.indexOf(":");
        return colonPos < 0 ? prefix + expr : expr.substring(0, colonPos + 1) + " " + prefix + expr.substring(colonPos + 1).trim();
    }
}
