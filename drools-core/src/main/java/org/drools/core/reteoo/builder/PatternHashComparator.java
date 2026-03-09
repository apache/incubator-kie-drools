/*
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
package org.drools.core.reteoo.builder;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.base.rule.constraint.Constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;

/**
 * Compares patterns by creating normalized hashes that abstract away variable names
 * while preserving the semantic structure of patterns and constraints.
 *
 * The hash generation process normalizes variable names to ensure that patterns
 * with identical structure but different variable names are considered equal.
 */
public class PatternHashComparator {

    private static final java.util.regex.Pattern FIELD_PATTERN = java.util.regex.Pattern.compile(
            "(?:get|is)([A-Z][a-zA-Z0-9_]*)|\\b([a-z][a-zA-Z0-9_]*)\\s*[=<>!]|this\\.([a-zA-Z_][a-zA-Z0-9_]*)");

    private static final java.util.regex.Pattern LITERAL_PATTERN = java.util.regex.Pattern.compile(
            "\"([^\"]*)\"|'([^']*)'|(\\b\\d+\\.?\\d*\\b)|(\\btrue\\b|\\bfalse\\b|\\bnull\\b)|==\\s*([a-zA-Z_][a-zA-Z0-9_]*)");

    private static final java.util.regex.Pattern FIELD_NAME_PATTERN = java.util.regex.Pattern.compile(
            "([a-zA-Z_][a-zA-Z0-9_]*)(?=\\s*$|\\s*[\\(\\)\\[\\]])");

    public static String generateNormalizedHash(Pattern pattern) {
        if (pattern == null) {
            return "null";
        }

        StringBuilder hash = new StringBuilder();

        hash.append("TYPE:").append(pattern.getObjectType().getClassName());

        List<String> normalizedConstraints = new ArrayList<>();

        for (Constraint constraint : pattern.getConstraints()) {
            String normalizedConstraint = normalizeConstraint(constraint);
            normalizedConstraints.add(normalizedConstraint);
        }

        Collections.sort(normalizedConstraints);

        if (!normalizedConstraints.isEmpty()) {
            hash.append(";CONSTRAINTS:[");
            for (int i = 0; i < normalizedConstraints.size(); i++) {
                if (i > 0) {
                    hash.append(",");
                }
                hash.append(normalizedConstraints.get(i));
            }
            hash.append("]");
        }

        return hash.toString();
    }

    /**
     * Normalizes a constraint by replacing variable names with normalized placeholders
     * while preserving field names, operators, and literal values.
     */
    private static String normalizeConstraint(Constraint constraint) {
        StringBuilder normalized = new StringBuilder();

        normalized.append(constraint.getClass().getSimpleName());

        if (constraint instanceof BetaConstraint betaConstraint) {
            normalized.append(":BETA");

            // Add required declarations with normalized variable names
            Declaration[] declarations = betaConstraint.getRequiredDeclarations();
            if (declarations != null && declarations.length > 0) {
                normalized.append(":DECLS[");

                Map<String, String> variableMap = new HashMap<>();

                for (int i = 0; i < declarations.length; i++) {
                    if (i > 0) {
                        normalized.append(",");
                    }

                    Declaration decl = declarations[i];
                    String varName = decl.getIdentifier();
                    String normalizedVar = variableMap.get(varName);
                    if (normalizedVar == null) {
                        normalizedVar = "VAR" + variableMap.size();
                        variableMap.put(varName, normalizedVar);
                    }

                    normalized.append(normalizedVar).append(":");

                    Class<?> declarationClass = decl.getDeclarationClass();
                    normalized.append(declarationClass != null ? declarationClass.getSimpleName() : "UNKNOWN_TYPE");

                    // Add field information if available
                    if (decl.getExtractor() != null) {
                        normalized.append(".").append(normalizeFieldName(decl.getExtractor().toString()));
                    }
                }

                normalized.append("]");
            }

            normalized.append(":OP:").append(extractOperator(betaConstraint.toString()));

        } else {
            normalized.append(":ALPHA:");
            normalized.append(extractDetailedAlphaConstraintInfo(constraint));
        }

        return normalized.toString();
    }

    /**
     * Extracts detailed information from alpha constraints including field names,
     * operators, and literal values while normalizing variable references.
     */
    private static String extractDetailedAlphaConstraintInfo(Constraint constraint) {
        StringBuilder info = new StringBuilder();

        String constraintStr = constraint.toString();

        String fieldInfo = extractFieldInfo(constraintStr);
        if (!fieldInfo.isEmpty()) {
            info.append("FIELD:").append(fieldInfo);
        }

        String operator = extractOperator(constraintStr);
        if (!operator.isEmpty()) {
            info.append(":OP:").append(operator);
        }

        String valueInfo = extractValueInfo(constraintStr);
        if (!valueInfo.isEmpty()) {
            info.append(":VAL:").append(valueInfo);
        }

        if (info.isEmpty()) {
            info.append(normalizeConstraintString(constraintStr));
        }

        return info.toString();
    }

    private static String extractFieldInfo(String constraintStr) {
        Matcher matcher = FIELD_PATTERN.matcher(constraintStr);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    return matcher.group(i).toLowerCase();
                }
            }
        }
        return "";
    }

    private static String extractOperator(String constraintStr) {
        if (constraintStr.contains("==")) return "EQUALS";
        if (constraintStr.contains("!=")) return "NOT_EQUALS";
        if (constraintStr.contains(">=")) return "GREATER_EQUAL";
        if (constraintStr.contains("<=")) return "LESS_EQUAL";
        if (constraintStr.contains(">")) return "GREATER";
        if (constraintStr.contains("<")) return "LESS";
        if (constraintStr.contains("matches")) return "MATCHES";
        if (constraintStr.contains("contains")) return "CONTAINS";
        if (constraintStr.contains("memberOf")) return "MEMBER_OF";
        if (constraintStr.contains("soundslike")) return "SOUNDS_LIKE";
        return "";
    }

    private static String extractValueInfo(String constraintStr) {
        Matcher matcher = LITERAL_PATTERN.matcher(constraintStr);
        TreeSet<String> foundValues = new TreeSet<>();

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    foundValues.add("LIT:" + matcher.group(i));
                }
            }
        }

        return String.join(",", foundValues);
    }

    /**
     * Normalizes a constraint string by replacing variable names with placeholders
     * while preserving field names and literal values.
     */
    private static String normalizeConstraintString(String constraintStr) {
        if (constraintStr == null) {
            return "null";
        }

        String normalized = constraintStr.replaceAll("[a-zA-Z_][a-zA-Z0-9_]*\\.[a-zA-Z_][a-zA-Z0-9_]*\\.", "");

        normalized = normalized.replaceAll("\\$[a-zA-Z_][a-zA-Z0-9_]*", "\\$VAR");

        normalized = normalized.replaceAll("\\b[a-z][a-zA-Z0-9_]*(?=\\s*[=<>!])", "VAR");

        normalized = normalized.replaceAll("\\s+", " ").trim();

        return normalized;
    }

    private static String normalizeFieldName(String fieldStr) {
        if (fieldStr == null) {
            return "FIELD";
        }

        Matcher matcher = FIELD_NAME_PATTERN.matcher(fieldStr);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return fieldStr.replaceAll("[^a-zA-Z0-9_]", "").trim();
    }
}
