/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static org.drools.scenariosimulation.backend.util.ScenarioBeanUtil.convertValue;

public enum BaseExpressionOperator {

    LIST_OF_CONDITION(0, ";") {
        @Override
        protected Optional<String> match(String value) {
            return symbols.stream().filter(value::contains).findFirst();
        }

        @Override
        protected boolean eval(Object raw, Object resultValue, Class<?> resultClass, ClassLoader classLoader) {
            if (!(raw instanceof String) || !match((String) raw).isPresent()) {
                return false;
            }
            String rawValue = (String) raw;
            String[] expressionParts = rawValue.split(symbols.get(0));
            List<Boolean> results = Arrays.stream(expressionParts.length == 0 ? new String[]{""} : expressionParts)
                    .map(elem -> findOperator(elem.trim()).eval(elem.trim(), resultValue, resultClass, classLoader))
                    .collect(Collectors.toList());
            return results.size() != 0 && results.stream().allMatch(a -> a);
        }

        @Override
        public String toString() {
            return "AND ( ; )";
        }
    },
    LIST_OF_VALUES(1, "[") {
        @Override
        public boolean eval(Object rawValue, Object resultValue, Class<?> resultClass, ClassLoader classLoader) {
            List<Boolean> results = getValues(rawValue).stream()
                    .map(e -> findOperator(e).eval(e, resultValue, resultClass, classLoader)).collect(Collectors.toList());
            return results.stream().anyMatch(a -> a);
        }

        private List<String> getValues(Object raw) {
            if (!(raw instanceof String) || !match((String) raw).isPresent()) {
                return Collections.emptyList();
            }
            String rawValue = ((String) raw).trim();
            if (!rawValue.endsWith("]")) {
                throw new IllegalArgumentException(new StringBuilder().append("Malformed expression: ").append(rawValue).toString());
            }
            return Stream.of(rawValue.substring(1, ((String) raw).length() - 1).split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "OR ( [ ] )";
        }
    },
    EQUALS(2, "=") {
        @Override
        protected Object evaluateLiteralExpression(String className, String value, ClassLoader classLoader) {
            String returnValue = removeOperator(value);
            return convertValue(className, returnValue, classLoader);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean eval(Object rawValue, Object resultValue, Class<?> resultClass, ClassLoader classLoader) {
            Object parsedResults = rawValue;
            if (parsedResults instanceof String) {
                parsedResults = evaluateLiteralExpression(resultClass != null ? resultClass.getCanonicalName() : null, (String) rawValue, classLoader);
            }
            if (parsedResults == null) {
                return resultValue == null;
            }
            if (areComparable(resultValue, parsedResults)) {
                return ((Comparable) resultValue).compareTo(parsedResults) == 0;
            }
            return Objects.equals(resultValue, parsedResults);
        }

        @Override
        public String toString() {
            return "Equal ( = )";
        }
    },
    NOT_EQUALS(3, "!", "!=", "<>") {
        @SuppressWarnings("unchecked")
        @Override
        public boolean eval(Object rawValue, Object resultValue, Class<?> resultClass, ClassLoader classLoader) {
            Object valueToTest = rawValue;
            BaseExpressionOperator operator = EQUALS;
            // remove symbol to reuse EQUALS.eval
            if (valueToTest instanceof String) {
                String rawStringValue = (String) valueToTest;
                valueToTest = removeOperator(rawStringValue);
                operator = findOperator((String) valueToTest);
            }

            return !operator.eval(valueToTest, resultValue, resultClass, classLoader);
        }

        @Override
        public String toString() {
            return "Not Equal ( !, !=, <> )";
        }
    },
    RANGE(4, "<", ">", "<=", ">=") {
        @Override
        public boolean eval(Object raw, Object resultValue, Class<?> resultClass, ClassLoader classLoader) {
            if (!(raw instanceof String) || !match((String) raw).isPresent()) {
                return false;
            }

            String rawValue = (String) raw;
            String operator = match(rawValue).orElseThrow(() -> new IllegalStateException("Cannot determine operator!"));
            String cleanValue = removeOperator(rawValue);
            Object stepValue = convertValue(resultClass.getCanonicalName(), cleanValue, classLoader);
            if (!areComparable(stepValue, resultValue)) {
                return false;
            }
            Comparable a = (Comparable) resultValue;
            Comparable b = (Comparable) stepValue;
            switch (operator) {
                case "<":
                    return a.compareTo(b) < 0;
                case ">":
                    return a.compareTo(b) > 0;
                case "<=":
                    return a.compareTo(b) <= 0;
                case ">=":
                    return a.compareTo(b) >= 0;
                default:
                    throw new IllegalStateException("This should not happen " + operator);
            }
        }

        @Override
        public String toString() {
            return "Range ( <, >, <=, >= )";
        }
    };

    final List<String> symbols;
    final int precedence;

    BaseExpressionOperator(int precedence, String... symbols) {
        this.precedence = precedence;
        this.symbols = Arrays.asList(symbols);
        // sort symbols by descending length to match longer symbols first
        this.symbols.sort((a, b) -> Integer.compare(a.length(), b.length()) * -1);
    }

    public static BaseExpressionOperator findOperator(String rawValue) {
        String value = rawValue.trim();
        List<BaseExpressionOperator> sortedOperators = Arrays.stream(values()).sorted(comparingInt(BaseExpressionOperator::getPrecedence))
                .collect(Collectors.toList());
        for (BaseExpressionOperator factMappingValueOperator : sortedOperators) {
            if (factMappingValueOperator.match(value).isPresent()) {
                return factMappingValueOperator;
            }
        }

        // Equals is the default
        return BaseExpressionOperator.EQUALS;
    }

    protected abstract boolean eval(Object rawValue, Object resultValue, Class<?> resultClass, ClassLoader classLoader);

    protected Object evaluateLiteralExpression(String className, String value, ClassLoader classLoader) {
        throw new IllegalStateException(toString() + " operator cannot be used in a GIVEN clause");
    }

    protected Optional<String> match(String value) {
        if (value == null) {
            return Optional.empty();
        }
        value = value.trim();
        return symbols.stream().filter(value::startsWith).findFirst();
    }

    protected String removeOperator(String fullString) {
        Optional<String> operatorSymbol = match(fullString);
        String value = fullString;
        if (operatorSymbol.isPresent()) {
            String symbolToRemove = operatorSymbol.get();
            int index = value.indexOf(symbolToRemove);
            value = value.substring(index + symbolToRemove.length()).trim();
        }
        return value == null ? null : value.trim();
    }

    private static boolean areComparable(Object a, Object b) {
        return a instanceof Comparable && b instanceof Comparable;
    }

    private int getPrecedence() {
        return precedence;
    }
}