/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.io.InputStreamReader;
import java.util.function.Consumer;

import io.quarkus.test.Mock;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.uow.UnitOfWorkManager;

import static org.kie.kogito.tracing.decision.Constants.TEST_EXECUTION_ID;
import static org.kie.kogito.tracing.decision.Constants.MODEL_RESOURCE;
import static org.kie.kogito.tracing.decision.Constants.MODEL_NAME;
import static org.kie.kogito.tracing.decision.Constants.MODEL_NAMESPACE;
import static org.mockito.Mockito.mock;

@Mock
public class ApplicationMock implements Application {

    final static DMNRuntime genericDMNRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(
            QuarkusExplainableResourceIT.class.getResourceAsStream(MODEL_RESOURCE)
    ));

    final static DecisionModels decisionModels;

    static {
        DmnDecisionModel decisionModel = new DmnDecisionModel(genericDMNRuntime, MODEL_NAMESPACE, MODEL_NAME, () -> TEST_EXECUTION_ID);
        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        genericDMNRuntime.addListener(listener);

        decisionModels = new DecisionModels() {
            @Override
            public DecisionModel getDecisionModel(String namespace, String name) {
                if (MODEL_NAMESPACE.equals(namespace) && MODEL_NAME.equals(name)) {
                    return decisionModel;
                }
                throw new RuntimeException("Model not found.");
            }
        };
    }

    @Override
    public Config config() {
        return null;
    }

    @Override
    public UnitOfWorkManager unitOfWorkManager() {
        return null;
    }

    @Override
    public DecisionModels decisionModels() {
        return decisionModels;
    }
}
