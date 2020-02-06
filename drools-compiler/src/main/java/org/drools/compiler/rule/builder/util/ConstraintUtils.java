/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstraintUtils {

    private static final Pattern RELATIONAL_CONSTRAINT = Pattern.compile("^(\\S*)\\s*(==|!=|<=|>=|<|>)\\s*(\\S*)$");
    private static final Pattern UNPARSABLE_CONSTRAINT = Pattern.compile("^\\(|^eval\\(|\\|\\||&&|\\)$"); // ^(,^eval(,||,&&,)$ 

    public static final String DROOLS_NORMALIZE_CONSTRAINT = "drools.normalize.constraint";

    private static boolean ENABLE_NORMALIZE = Boolean.valueOf(System.getProperty(DROOLS_NORMALIZE_CONSTRAINT, "true"));

    private ConstraintUtils() {}

    public static String normalizeConstraintExpression(String expression, Class<?> clazz) {

        if (!ENABLE_NORMALIZE) {
            return expression;
        }
        String trimmedExpression = expression.trim();
        Matcher unparsableMatcher = UNPARSABLE_CONSTRAINT.matcher(trimmedExpression);
        if (unparsableMatcher.find()) {
            return expression; // cannot normalize
        }

        Matcher m = RELATIONAL_CONSTRAINT.matcher(trimmedExpression);
        boolean isFound = m.find();
        if (!isFound || m.groupCount() != 3) {
            return expression; // cannot normalize
        }
        String left = m.group(1).trim();
        String operator = m.group(2);
        String right = m.group(3).trim();
        
        Set<String> propertyNames;
        try {
            propertyNames = getPropertyNames(clazz);
        } catch (IntrospectionException | RuntimeException e) {
            return expression; // cannot normalize
        }

        if (!propertyNames.contains(left) && propertyNames.contains(right)) {
            operator = inverseOperator(operator);
            return new StringBuilder().append(right).append(operator).append(left).toString();
        } else {
            return expression;
        }
    }

    private static String inverseOperator(String operator) {
        switch (operator) {
            case ">":
                return "<";
            case "<":
                return ">";
            case ">=":
                return "<=";
            case "<=":
                return ">=";
            default:
                return operator;
        }
    }

    private static Set<String> getPropertyNames(Class<?> clazz) throws IntrospectionException {
        PropertyDescriptor[] propDescrs = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
        Set<String> propertyNames = new HashSet<>();
        for (PropertyDescriptor pd : propDescrs) {
            propertyNames.add(pd.getName());
        }
        return propertyNames;
    }
}
