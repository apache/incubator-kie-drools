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

package org.kie.kogito.core.prediction.incubation.quarkus.support;

import java.util.Collections;
import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.common.objectmapper.InternalObjectMapper;
import org.kie.kogito.incubation.predictions.LocalPredictionId;
import org.kie.kogito.incubation.predictions.services.PredictionService;
import org.kie.kogito.prediction.PredictionModel;
import org.kie.kogito.prediction.PredictionModels;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;

public class PredictionServiceImpl implements PredictionService {
    private final PredictionModels predictionModels;

    public PredictionServiceImpl(PredictionModels predictionModels) {
        this.predictionModels = predictionModels;
    }

    @Override
    public ExtendedDataContext evaluate(LocalId predictionId, DataContext inputContext) {
        LocalPredictionId localPredictionId;
        if (predictionId instanceof LocalPredictionId) {
            localPredictionId = (LocalPredictionId) predictionId;
        } else {
            throw new IllegalArgumentException(
                    "Not a valid prediction id " + predictionId.toLocalId());
        }
        PredictionModel predictionModel =
                predictionModels.getPredictionModel(localPredictionId.getFileName(), localPredictionId.name());

        PMMLRuntimeContext ctx = predictionModel.newContext(inputContext.as(MapDataContext.class).toMap());

        PMML4Result pmml4Result = predictionModel.evaluateAll(ctx);
        Map<String, Object> resultMap = Collections.singletonMap(
                pmml4Result.getResultObjectName(),
                pmml4Result.getResultVariables().get(
                        pmml4Result.getResultObjectName()));

        MapDataContext meta = MapDataContext.of(InternalObjectMapper.objectMapper().convertValue(pmml4Result, Map.class));
        MapDataContext data = MapDataContext.of(resultMap);
        return ExtendedDataContext.of(meta, data);
    }
}
