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
package org.kie.kogito.dmn;

import java.io.Reader;
import java.util.function.Function;

import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.Application;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.decision.DecisionConfig;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;

public abstract class AbstractDecisionModels implements DecisionModels {

    private static final boolean CAN_PLATFORM_CLASSLOAD = org.kie.dmn.feel.util.ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD;
    private static DMNRuntime dmnRuntime = null;
    private static ExecutionIdSupplier execIdSupplier = null;
    private static Function<DecisionModel, DecisionModel> decisionModelTransformer = null;

    protected static void init(Function<String, KieRuntimeFactory> sKieRuntimeFactoryFunction,
                               ExecutionIdSupplier executionIdSupplier,
                               Function<DecisionModel, DecisionModel> decisionModelTransformerInit,
                               Reader... readers) {
        dmnRuntime = DMNKogito.createGenericDMNRuntime(sKieRuntimeFactoryFunction, readers);
        execIdSupplier = executionIdSupplier;
        decisionModelTransformer = decisionModelTransformerInit;
    }

    public DecisionModel getDecisionModel(String namespace, String name) {
        DecisionModel model = new DmnDecisionModel(dmnRuntime, namespace, name, execIdSupplier);
        return decisionModelTransformer == null
                ? model
                : decisionModelTransformer.apply(model);
    }

    public AbstractDecisionModels() {
        // needed by CDI
    }

    public AbstractDecisionModels(Application app) {
        initApplication(app);
    }

    protected void initApplication(Application app) {
        app.config().get(DecisionConfig.class).decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
    }

    protected static java.io.InputStreamReader readResource(java.io.InputStream stream) {
        if (CAN_PLATFORM_CLASSLOAD) {
            return new java.io.InputStreamReader(stream);
        }

        try {
            byte[] bytes = org.drools.core.util.IoUtils.readBytesFromInputStream(stream);
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(bytes);
            return new java.io.InputStreamReader(byteArrayInputStream);
        } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
        }
    }
}
