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
package org.kie.kogito.serverless.workflow.workitemparams;

import java.util.Map;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.workitems.WorkParametersFactory;

import static java.util.Collections.singletonMap;
import static org.kie.kogito.serverless.workflow.SWFConstants.CONTENT_DATA;

public class ExpressionParametersFactory extends ExpressionWorkItemResolver<Map<String, Object>> implements WorkParametersFactory {

    public ExpressionParametersFactory(String exprLang, Object expr, String paramName) {
        super(exprLang, expr, paramName);
    }

    @Override
    public Map<String, Object> apply(KogitoWorkItem workItem) {
        Object obj = JsonObjectUtils.toJavaValue(super.evalExpression(workItem));
        return obj instanceof Map ? (Map<String, Object>) obj : singletonMap(CONTENT_DATA, obj);
    }
}
