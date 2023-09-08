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
package  org.kie.pmml.models.tree.model;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;

public abstract class KiePMMLTreeModel extends KiePMMLModel {

    private static final long serialVersionUID = -5158590062736070465L;

    protected Function<Map<String, Object>, KiePMMLNodeResult> nodeFunction;

    protected KiePMMLTreeModel(String fileName, String modelName) {
        super(fileName, modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        KiePMMLNodeResult kiePMMLNodeResult = nodeFunction.apply(requestData);
        context.setProbabilityResultMap(kiePMMLNodeResult.getProbabilityMap());
        return kiePMMLNodeResult.getScore();
    }

}
