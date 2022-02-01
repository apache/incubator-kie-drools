/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.utils;

import java.util.Optional;
import java.util.function.Function;

import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ExpressionHandlerUtils {

    private ExpressionHandlerUtils() {
    }

    private static final String EXPR_PREFIX = "${";
    private static final String EXPR_SUFFIX = "}";
    protected static final String SECRET_MAGIC = "$SECRET.";
    protected static final String CONST_MAGIC = "$CONST.";

    public static String prepareExpr(String expr) {
        return replaceMagic(expr, SECRET_MAGIC, SecretResolverFactory.getSecretResolver());
    }

    private static <T extends Object> String replaceMagic(String expr, String magic, Function<String, T> replacer) {
        int indexOf;
        while ((indexOf = expr.indexOf(magic)) != -1) {
            String key = extractKey(expr, indexOf + magic.length());
            String toReplace = magic + key;
            T value = replacer.apply(key);
            expr = expr.replace(toReplace, value.toString());
        }
        return expr;
    }

    private static String extractKey(String expr, int indexOf) {
        StringBuilder sb = new StringBuilder();
        for (int i = indexOf; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (!Character.isAlphabetic(ch)) {
                break;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String trimExpr(String expr) {
        expr = expr.trim();
        if (expr.startsWith(EXPR_PREFIX)) {
            expr = expr.substring(EXPR_PREFIX.length());
            if (expr.endsWith(EXPR_SUFFIX)) {
                expr = expr.substring(0, expr.length() - EXPR_SUFFIX.length());
            }
        }
        return expr.trim();
    }

    public static void assign(JsonNode context, JsonNode target, JsonNode value, String expr) {
        if (context.isObject()) {
            Optional<String> varName = fallbackVarToName(expr);
            if (varName.isPresent()) {
                JsonObjectUtils.addToNode(varName.get(), MergeUtils.merge(value, target), (ObjectNode) context);
            }
        }
    }

    public static Optional<String> fallbackVarToName(String expr) {
        int indexOf = expr.lastIndexOf('.');
        return indexOf < 0 ? Optional.empty() : Optional.of(expr.substring(indexOf + 1));
    }
}
