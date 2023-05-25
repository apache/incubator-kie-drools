/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConstraintIdentifierAnalysis {

    private List<String> identifierList = new ArrayList<>();
    private List<String> methodArgumentList = new ArrayList<>();

    private ConstraintIdentifierAnalysis(List<String> identifierList, List<String> methodArgumentList) {
        this.identifierList = identifierList;
        this.methodArgumentList = methodArgumentList;
    }

    public List<String> getIdentifierList() {
        return identifierList;
    }

    public List<String> getMethodArgumentList() {
        return methodArgumentList;
    }

    public static ConstraintIdentifierAnalysis analyze(String expression) {
        expression = stripEval(expression);
        List<String> identifierList = new ArrayList<>();
        List<String> methodArgumentList = new ArrayList<>();
        int methodCallLevel = 0;
        boolean drlIn = false;

        int cursor = 0;
        while (cursor < expression.length()) {
            StringBuilder sb = new StringBuilder();
            cursor = StringUtils.extractFirstIdentifier(expression, sb, cursor);
            String identifier = sb.toString();
            if (identifier.isEmpty()) {
                break;
            }
            identifierList.add(identifier);
            Character nextChar = StringUtils.lookAheadIgnoringSpaces(expression, cursor);
            if (nextChar == null) {
                break;
            }
            if (methodCallLevel > 0) {
                if (!drlIn && nextChar != '(') {
                    // Do not count "in" arguments
                    // if '(', the identifier is a method name, not an argument
                    methodArgumentList.add(identifier);
                }
                if (nextChar == ')') {
                    methodCallLevel--;
                    if (drlIn && methodCallLevel == 1) {
                        drlIn = false;
                    }
                }
            }
            if (nextChar == '(') {
                methodCallLevel++;
                if (identifier.equals("in") && methodCallLevel == 1) {
                    drlIn = true;
                }
                Character nextSecondChar = StringUtils.lookAheadSecondCharIgnoringSpaces(expression, cursor);
                if (nextSecondChar == ')') {
                    // No argument method
                    methodCallLevel--;
                }
            }
        }

        return new ConstraintIdentifierAnalysis(identifierList, methodArgumentList);
    }

    private static String stripEval(String expression) {
        return expression.startsWith("eval") ? expression.substring(4) : expression;
    }

    @Override
    public String toString() {
        return "ConstraintIdentifierAnalysis [identifierList=" + identifierList + ", methodArgumentList=" + methodArgumentList + "]";
    }

    /*
     * Returns true when identifierList contains any of the given properties.
     * Note that this is just a String comparison, not verifying if it's truly used as a property
     */
    public boolean containsProperties(Collection<String> properties) {
        return identifierList.stream().anyMatch(properties::contains);
    }

}
