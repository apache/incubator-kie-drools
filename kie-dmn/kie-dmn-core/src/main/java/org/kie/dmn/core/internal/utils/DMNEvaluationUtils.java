/**
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
package org.kie.dmn.core.internal.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNEvaluationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DMNEvaluationUtils.class);

    public static DMNEvaluationResult evaluate(final DMNRuntime dmnRuntime,
                                               final String modelNamespace,
                                               final String modelName,
                                               final Map<String, Object> inputContextValues,
                                               final List<String> decisionNames,
                                               final List<String> decisionIds,
                                               final String decisionServiceName) {
        Supplier<DMNContext> dmnContextProducer = () -> {
            DMNContext ctx = dmnRuntime.newContext();
            for (Entry<String, Object> entry : inputContextValues.entrySet()) {
                ctx.set(entry.getKey(), entry.getValue());
            }
            return ctx;
        };
        return evaluate(dmnRuntime, modelNamespace, modelName, dmnContextProducer, decisionNames, decisionIds, decisionServiceName);
    }

    public static DMNEvaluationResult evaluate(final DMNRuntime dmnRuntime,
                                     final String modelNamespace,
                                     final String modelName,
                                     final Supplier<DMNContext> dmnContextProducer,
                                     final List<String> decisionNames,
                                     final List<String> decisionIds,
                                     final String decisionServiceName) {
        DMNModel model;
        if (modelName == null) {
            if (dmnRuntime.getModels().size() > 1) {
                throw new RuntimeException("more than one (default) model");
            }

            model = dmnRuntime.getModels().get(0);
        } else {
            model = dmnRuntime.getModel(modelNamespace, modelName);
        }
        if (model == null) {
            throw new RuntimeException("Unable to locate DMN Model to evaluate");
        }
        LOG.debug("Will use model: {}", model);

        DMNContext dmnContext = dmnContextProducer.get();
        LOG.debug("Will use dmnContext: {}", dmnContext);

        DMNResult result;

        final List<String> names = Optional.ofNullable(decisionNames).orElse(Collections.emptyList());
        final List<String> ids = Optional.ofNullable(decisionIds).orElse(Collections.emptyList());

        if (decisionServiceName == null && names.isEmpty() && ids.isEmpty()) {
            // then implies evaluate All decisions
            LOG.debug("Invoking evaluateAll...");
            result = dmnRuntime.evaluateAll(model, dmnContext);
        } else if (decisionServiceName != null && names.isEmpty() && ids.isEmpty()) {
            LOG.debug("Invoking evaluateDecisionService using decisionServiceName: {}", decisionServiceName);
            result = dmnRuntime.evaluateDecisionService(model, dmnContext, decisionServiceName);
        } else if (!names.isEmpty() && ids.isEmpty()) {
            LOG.debug("Invoking evaluateDecisionByName using {}", names);
            result = dmnRuntime.evaluateByName(model, dmnContext, names.toArray(new String[]{}));
        } else if (!ids.isEmpty() && names.isEmpty()) {
            LOG.debug("Invoking evaluateDecisionById using {}", ids);
            result = dmnRuntime.evaluateById(model, dmnContext, ids.toArray(new String[]{}));
        } else {
            LOG.debug("Not supported case");
            throw new RuntimeException("Unable to locate DMN Decision to evaluate");
        }

        LOG.debug("Result:");
        LOG.debug("{}", result);
        LOG.debug("{}", result.getContext());
        LOG.debug("{}", result.getDecisionResults());
        LOG.debug("{}", result.getMessages());
        return new DMNEvaluationResult(model, dmnContext, result);
    }

    public static class DMNEvaluationResult {

        public final DMNModel model;
        public final DMNContext inputContext;
        public final DMNResult result;

        public DMNEvaluationResult(DMNModel model, DMNContext inputContext, DMNResult result) {
            this.model = model;
            this.inputContext = inputContext;
            this.result = result;
        }
    }

    private DMNEvaluationUtils() {
        // Constructing instances is not allowed for this class
    }

}
