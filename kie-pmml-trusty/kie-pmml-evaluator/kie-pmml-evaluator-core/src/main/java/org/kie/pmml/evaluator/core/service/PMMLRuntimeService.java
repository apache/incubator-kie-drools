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
package org.kie.pmml.evaluator.core.service;

import org.kie.api.KieBase;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluatorFinderImpl;

public class PMMLRuntimeService implements KieRuntimeService<PMMLRuntime> {

    @Override
    public PMMLRuntime newKieRuntime(KieBase kieBase) {
        return new PMMLRuntimeInternalImpl(kieBase, new PMMLModelEvaluatorFinderImpl());
    }

    @Override
    public Class getServiceInterface() {
        return PMMLRuntime.class;
    }
}
