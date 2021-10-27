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
package org.kie.kogito.process.workitems.impl.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionHandlerFactory {

    private ExpressionHandlerFactory() {
    }

    private static final Map<String, String> expHandlerClasses = new HashMap<>();
    private static final Map<String, ExpressionHandler> expHandlerInstances = new ConcurrentHashMap<>();

    private static final String JSONPATH_CLASSNAME = "org.kie.kogito.expr.jsonpath.JsonPathExpressionHandler";
    private static final String JSONPATH_LANG = "jsonpath";

    private static final String JQ_CLASSNAME = "org.kie.kogito.expr.jq.JqExpressionHandler";
    private static final String JQ_LANG = "jq";

    static {
        expHandlerClasses.put(JSONPATH_LANG, JSONPATH_CLASSNAME);
        expHandlerClasses.put(JQ_LANG, JQ_CLASSNAME);
    }

    public static ExpressionHandler get(String lang) {
        return expHandlerInstances.computeIfAbsent(lang, c -> {
            try {
                return Class.forName(expHandlerClasses.getOrDefault(c, JSONPATH_CLASSNAME)).asSubclass(ExpressionHandler.class).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public static boolean isSupported(String lang) {
        return lang != null && expHandlerClasses.containsKey(lang);
    }
}
