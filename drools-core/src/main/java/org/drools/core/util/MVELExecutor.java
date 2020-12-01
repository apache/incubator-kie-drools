/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.util.Map;

public interface MVELExecutor {
    Object eval(String expression);

    Object eval(String expression, Object ctx);

    Object eval(String expression, Map<String, Object> vars);

    Object eval(String expression, Object ctx, Map<String, Object> vars);

    <T> T eval(String expression, Class<T> toType);

    <T> T eval(String expression, Object ctx, Class<T> toType);

    <T> T eval(String expression, Map<String, Object> vars, Class<T> toType);

    <T> T eval(String expression, Object ctx, Map<String, Object> vars, Class<T> toType);

    String evalToString(String singleValue);

    Object executeExpression(Object compiledExpression);

    Object executeExpression(final Object compiledExpression, final Object ctx, final Map vars);

    Object executeExpression(final Object compiledExpression, final Object ctx);

    Object executeExpression(final Object compiledExpression, final Map vars);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, final Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, Map vars, Class<T> toType);

    <T> T executeExpression(final Object compiledExpression, final Object ctx, Class<T> toType);

    String soundex(String s);
}
