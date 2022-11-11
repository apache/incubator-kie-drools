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

import java.util.Map;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.rest.DMNJSONUtils;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.common.MetaDataContext;
import org.kie.kogito.incubation.decisions.LocalDecisionId;
import org.kie.kogito.incubation.decisions.LocalDecisionServiceId;
import org.kie.kogito.incubation.decisions.services.DecisionService;

class DecisionServiceImpl implements DecisionService {
    private final DecisionModels decisionModels;

    public DecisionServiceImpl(DecisionModels decisionModels) {
        this.decisionModels = decisionModels;
    }

    @Override
    public ExtendedDataContext evaluate(LocalId decisionId, DataContext inputContext) {
        LocalDecisionId localDecisionId;
        LocalDecisionServiceId decisionServiceId = null;
        if (decisionId instanceof LocalDecisionId) {
            localDecisionId = (LocalDecisionId) decisionId;

        } else if (decisionId instanceof LocalDecisionServiceId) {
            decisionServiceId = (LocalDecisionServiceId) decisionId;
            localDecisionId = (LocalDecisionId) decisionServiceId.decisionId();
        } else {
            throw new IllegalArgumentException(
                    "Not a valid decision id " + decisionId.toLocalId().asLocalUri());
        }

        DecisionModel decisionModel =
                decisionModels.getDecisionModel(
                        localDecisionId.namespace(), localDecisionId.name());

        ExtendedDataContext extendedDataContext = inputContext.as(ExtendedDataContext.class);

        Map<String, Object> map = extendedDataContext.data().as(MapDataContext.class).toMap();
        DMNContext ctx = DMNJSONUtils.ctx(decisionModel, map);
        MetaDataContext inputMeta = extendedDataContext.meta();
        MapDataContext mapInputMeta = MapDataContext.from(inputMeta);
        DMNMetadata metadata = ctx.getMetadata();
        for (Map.Entry<String, Object> kv : mapInputMeta.toMap().entrySet()) {
            metadata.set(kv.getKey(), kv.getValue());
        }

        DMNResult dmnResult;

        if (decisionServiceId == null) {
            dmnResult = decisionModel.evaluateAll(ctx);
        } else {
            dmnResult = decisionModel.evaluateDecisionService(
                    ctx, decisionServiceId.serviceId());
        }

        MapDataContext meta = MapDataContext.of(dmnResult.getContext().getMetadata().asMap());
        MapDataContext data = MapDataContext.of(dmnResult.getContext().getAll());
        return ExtendedDataContext.of(meta, data);
    }
}
