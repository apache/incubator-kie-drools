/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.grafana.model.functions;

import java.util.SortedMap;

public class ExprBuilder {

    private ExprBuilder() {
    }

    public static String apply(String expr, SortedMap<Integer, GrafanaFunction> functions) {
        if (functions != null) {
            for (GrafanaFunction function : functions.values()) {
                if (function.hasTimeParameter()) {
                    expr = String.format("%s[%s]", expr, function.getTimeParameter());
                }
                expr = String.format("%s(%s)", function.getFunction(), expr);
            }
        }

        return expr;
    }
}
