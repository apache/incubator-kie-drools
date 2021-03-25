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

package org.kie.dmn.feel.runtime.functions.extended;

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class InvokeFunction extends BaseFEELFunction {

    public InvokeFunction() {
        super("invoke");
    }

    public FEELFnResult<Object> invoke(final @ParameterName("ctx") EvaluationContext ctx,
                                       final @ParameterName("namespace") String namespace,
                                       final @ParameterName("model name") String modelName,
                                       final @ParameterName("decision name") String decisionName,
                                       final @ParameterName("parameters") Map<String, Object> parameters) {

        final DMNRuntime dmnRuntime = ctx.getDMNRuntime();

        if (namespace == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "namespace", "cannot be null"));
        }

        if (modelName == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "model name", "cannot be null"));
        }

        if (decisionName == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "decision name", "cannot be null"));
        }

        if (parameters == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "parameters", "cannot be null"));
        }

        FEELEvent capturedException = null;
        try {
            ctx.enterFrame();

            final DMNModel dmnModel = dmnRuntime.getModel(namespace, modelName);
            if (dmnModel == null) {
                return FEELFnResult.ofError(
                        new FEELEventBase(FEELEvent.Severity.ERROR, "Cannot find model '" + modelName + "' in namespace " + namespace, null)
                );
            }

            if (dmnModel.getDecisionByName(decisionName) == null) {
                return FEELFnResult.ofError(
                        new FEELEventBase(FEELEvent.Severity.ERROR, "Cannot find decision '" + decisionName + "' in the model", null)
                );
            }

            final DMNContext dmnContext = dmnRuntime.newContext();
            dmnContext.getAll().putAll(parameters);

            final DMNResult requiredDecisionResult = dmnRuntime.evaluateByName(dmnModel, dmnContext, decisionName);

            return FEELFnResult.ofResult(requiredDecisionResult.getContext().get(decisionName));
        } catch (final Exception e) {
            capturedException = new FEELEventBase(FEELEvent.Severity.ERROR, "Error invoking function", new RuntimeException("Error invoking function " + getName() + ".", e));
        } finally {
            ctx.exitFrame();
        }

        return FEELFnResult.ofError(capturedException);
    }
}
