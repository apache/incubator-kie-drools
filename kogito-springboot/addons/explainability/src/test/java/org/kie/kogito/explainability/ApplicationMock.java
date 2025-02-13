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
package org.kie.kogito.explainability;

import java.io.InputStreamReader;
import java.util.Collections;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.Application;
import org.kie.kogito.Config;
import org.kie.kogito.KogitoEngine;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.uow.UnitOfWorkManager;

public class ApplicationMock implements Application {

    final static DMNRuntime genericDMNRuntime = DMNKogito.createGenericDMNRuntime(Collections.emptySet(), false, new InputStreamReader(
            ApplicationMock.class.getResourceAsStream(Constants.MODEL_RESOURCE)));

    final static DecisionModels decisionModels;

    static {
        DmnDecisionModel decisionModel = new DmnDecisionModel(genericDMNRuntime, Constants.MODEL_NAMESPACE, Constants.MODEL_NAME, () -> Constants.TEST_EXECUTION_ID);

        decisionModels = (namespace, name) -> {
            if (Constants.MODEL_NAMESPACE.equals(namespace) && Constants.MODEL_NAME.equals(name)) {
                return decisionModel;
            }
            throw new RuntimeException("Model " + namespace + ":" + name + " not found.");
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

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KogitoEngine> T get(Class<T> clazz) {
        return (T) decisionModels;
    }
}
