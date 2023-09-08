/**
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
package org.kie.pmml.models.clustering.evaluator;

import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.evaluator.core.executor.PMMLModelEvaluator;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;

import static org.kie.pmml.api.enums.ResultCode.OK;
import static org.kie.pmml.evaluator.core.utils.Converter.getUnwrappedParametersMap;

/**
 * Default <code>PMMLModelExecutor</code> for <b>Clustering</b>
 */
public class PMMLClusteringModelEvaluator implements PMMLModelEvaluator<KiePMMLClusteringModel> {

    @Override
    public PMML_MODEL getPMMLModelType() {
        return PMML_MODEL.CLUSTERING_MODEL;
    }

    @Override
    public PMML4Result evaluate(final KiePMMLClusteringModel model,
                                final PMMLRuntimeContext context) {
        final Map<String, Object> requestData =
                getUnwrappedParametersMap(context.getRequestData().getMappedRequestParams());

        Object result = model.evaluate(requestData, context);

        String targetField = model.getTargetField();
        PMML4Result toReturn = new PMML4Result();
        toReturn.addResultVariable(targetField, result);
        toReturn.setResultObjectName(targetField);
        toReturn.setResultCode(OK.getName());
        return toReturn;
    }
}
