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
package  org.kie.pmml.models.scorecard.model;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.kie.pmml.commons.model.KiePMMLModel;

public abstract class KiePMMLScorecardModel extends KiePMMLModel {

    private static final long serialVersionUID = 1798360806171346217L;

    /**
     * The first <code>Map</code> is the input data, the second <code>Map</code> is the <b>outputfieldsmap</b>
     */
    protected BiFunction<Map<String, Object>, Map<String, Object>, Object> characteristicsFunction;

    public KiePMMLScorecardModel(String modelName) {
        super(modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Object knowledgeBase, final Map<String, Object> requestData) {
        return characteristicsFunction.apply(requestData, outputFieldsMap);
    }
}
