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

package org.kie.kogito.core.decision.incubation.quarkus.support;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.decisions.LocalDecisionId;
import org.kie.kogito.incubation.decisions.LocalDecisionServiceId;
import org.kie.kogito.incubation.decisions.services.DecisionService;

@ApplicationScoped
public class QuarkusDecisionService implements DecisionService {
    @Inject
    Instance<DecisionModels> decisionModelsInstance;

    @Override
    public DataContext evaluate(LocalId decisionId, DataContext inputContext) {
        LocalDecisionId localDecisionId;
        LocalDecisionServiceId decisionServiceId = null;
        if (decisionId instanceof LocalDecisionId) {
            localDecisionId = (LocalDecisionId) decisionId;

        } else if (decisionId instanceof LocalDecisionServiceId) {
            decisionServiceId = (LocalDecisionServiceId) decisionId;
            localDecisionId = (LocalDecisionId) decisionServiceId.decisionId();
        } else {
            // LocalDecisionId.parse(decisionId);
            throw new IllegalArgumentException(
                    "Not a valid decision id " + decisionId.toLocalId().asLocalUri());
        }

        DecisionModels decisionModels = decisionModelsInstance.get();

        DecisionModel decisionModel =
                decisionModels.getDecisionModel(
                        localDecisionId.namespace(), localDecisionId.name());

        DMNContext ctx = DMNJSONUtils.ctx(decisionModel, inputContext.as(MapDataContext.class).toMap());
        DMNResult dmnResult;

        if (decisionServiceId == null) {
            dmnResult = decisionModel.evaluateAll(ctx);
        } else {
            dmnResult = decisionModel.evaluateDecisionService(
                    ctx, decisionServiceId.serviceId());
        }

        return MapDataContext.of(dmnResult.getContext().getAll());
    }
}
