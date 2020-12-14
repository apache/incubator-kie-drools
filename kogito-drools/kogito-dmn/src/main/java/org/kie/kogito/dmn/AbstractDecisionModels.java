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

import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.ExecutionIdSupplier;
import org.kie.kogito.decision.DecisionModels;

import java.io.Reader;
import java.util.function.Function;

public abstract class AbstractDecisionModels implements DecisionModels {

    private static final boolean CAN_PLATFORM_CLASSLOAD = org.kie.dmn.feel.util.ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD;
    private static DMNRuntime dmnRuntime = null;
    private static ExecutionIdSupplier execIdSupplier = null;

    protected static void init(Function<String, KieRuntimeFactory> sKieRuntimeFactoryFunction,
                               ExecutionIdSupplier executionIdSupplier,
                               Reader... readers) {
        dmnRuntime = DMNKogito.createGenericDMNRuntime(sKieRuntimeFactoryFunction, readers);
        execIdSupplier = executionIdSupplier;
    }

    public org.kie.kogito.decision.DecisionModel getDecisionModel(String namespace, String name) {
        return new org.kie.kogito.dmn.DmnDecisionModel(dmnRuntime, namespace, name, execIdSupplier);
    }

    public AbstractDecisionModels() {
        // needed by CDI
    }

    public AbstractDecisionModels(org.kie.kogito.Application app) {
        initApplication(app);
    }

    protected void initApplication(org.kie.kogito.Application app) {
        app.config().decision().decisionEventListeners().listeners().forEach(dmnRuntime::addListener);
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
