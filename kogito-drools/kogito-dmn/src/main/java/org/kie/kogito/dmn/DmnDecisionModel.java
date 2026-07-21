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
package org.kie.kogito.dmn;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.decision.DecisionModel;

public class DmnDecisionModel implements DecisionModel {

    private final DMNRuntime dmnRuntime;
    private final ExecutionIdSupplier execIdSupplier;
    private final DMNModel dmnModel;

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name) {
        this(dmnRuntime, namespace, name, null);
    }

    public DmnDecisionModel(DMNRuntime dmnRuntime, String namespace, String name, ExecutionIdSupplier execIdSupplier) {
        this.dmnRuntime = dmnRuntime;
        this.execIdSupplier = execIdSupplier;
        this.dmnModel = dmnRuntime.getModel(namespace, name);
        if (dmnModel == null) {
            throw new IllegalStateException("DMN model '" + name + "' not found with namespace '" + namespace + "' in the inherent DMNRuntime.");
        }
    }

    @Override
    public DMNContext newContext(Map<String, Object> variables) {
        return new org.kie.dmn.core.impl.DMNContextImpl(variables != null ? variables : Collections.emptyMap());
    }

    @Override
    public DMNContext newContext(FEELPropertyAccessible inputSet) {
        return new org.kie.dmn.core.impl.DMNContextFPAImpl(inputSet);
    }

    @Override
    public DMNResult evaluateAll(DMNContext context) {
        return dmnRuntime.evaluateAll(dmnModel, injectExecutionId(context));
    }

    @Override
    public DMNResult evaluateDecisionService(DMNContext context, String decisionServiceName) {
        return dmnRuntime.evaluateDecisionService(dmnModel, injectExecutionId(context), decisionServiceName);
    }

    private DMNContext injectExecutionId(DMNContext context) {
        return execIdSupplier != null
                ? DecisionExecutionIdUtils.inject(context, execIdSupplier)
                : context;
    }

    @Override
    public DMNModel getDMNModel() {
        return dmnModel;
    }

    public List<DMNProfile> getProfiles() {
        return Collections.unmodifiableList(((DMNRuntimeImpl) dmnRuntime).getProfiles());
    }
}
