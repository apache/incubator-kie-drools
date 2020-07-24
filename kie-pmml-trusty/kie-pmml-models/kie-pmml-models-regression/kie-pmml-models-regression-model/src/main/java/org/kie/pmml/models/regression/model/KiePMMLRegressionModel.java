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
package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public abstract class KiePMMLRegressionModel extends KiePMMLModel {

    protected KiePMMLRegressionTable regressionTable;

    public KiePMMLRegressionModel(String modelName) {
        super(modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Object knowledgeBase, Map<String, Object> requestData) {
        return regressionTable.evaluateRegression(requestData);
    }

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        return regressionTable.getOutputFieldsMap();
    }

    public KiePMMLRegressionTable getRegressionTable() {
        return regressionTable;
    }
}
