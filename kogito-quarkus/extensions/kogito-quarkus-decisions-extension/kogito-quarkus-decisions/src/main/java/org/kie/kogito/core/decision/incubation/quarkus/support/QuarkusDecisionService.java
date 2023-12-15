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
package org.kie.kogito.core.decision.incubation.quarkus.support;

import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.decisions.services.DecisionService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusDecisionService implements DecisionService {
    @Inject
    Instance<DecisionModels> decisionModelsInstance;
    DecisionServiceImpl delegate;

    @PostConstruct
    void startup() {
        this.delegate = new DecisionServiceImpl(decisionModelsInstance.get());
    }

    @Override
    public ExtendedDataContext evaluate(LocalId decisionId, DataContext inputContext) {
        return delegate.evaluate(decisionId, inputContext);
    }
}
