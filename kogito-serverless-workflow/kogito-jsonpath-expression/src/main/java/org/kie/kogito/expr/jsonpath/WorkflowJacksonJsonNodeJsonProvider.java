/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.expr.jsonpath;

import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;

public class WorkflowJacksonJsonNodeJsonProvider extends JacksonJsonNodeJsonProvider {

    private KogitoProcessContext context;

    public WorkflowJacksonJsonNodeJsonProvider(KogitoProcessContext context) {
        this.context = context;
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        if (obj instanceof Function) {
            return ((Function<String, Object>) obj).apply(key);
        } else {
            switch (key) {
                case "$" + ExpressionHandlerUtils.SECRET_MAGIC:
                    return (Function<String, Object>) ExpressionHandlerUtils::getSecret;
                case "$" + ExpressionHandlerUtils.CONTEXT_MAGIC:
                    return ExpressionHandlerUtils.getContextFunction(context);
                case "$" + ExpressionHandlerUtils.CONST_MAGIC:
                    return ExpressionHandlerUtils.getConstants(context);
                default:
                    return super.getMapValue(obj, key);
            }
        }
    }

    @Override
    public boolean isMap(Object obj) {
        return super.isMap(obj) || obj instanceof Function;
    }
}
