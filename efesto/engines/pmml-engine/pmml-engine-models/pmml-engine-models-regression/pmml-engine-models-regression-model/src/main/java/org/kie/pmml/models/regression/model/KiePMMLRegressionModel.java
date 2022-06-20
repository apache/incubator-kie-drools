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

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.commons.model.IsInterpreted;
import org.kie.pmml.commons.model.KiePMMLModel;

import java.util.Collections;
import java.util.Map;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public class KiePMMLRegressionModel extends KiePMMLModel implements IsInterpreted {

    private static final long serialVersionUID = -6870859552385880008L;
    private AbstractKiePMMLTable regressionTable;

    private KiePMMLRegressionModel(String modelName) {
        super(modelName, Collections.emptyList());
    }

    public static Builder builder(String name, MINING_FUNCTION miningFunction) {
        return new Builder(name, miningFunction);
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLContext context) {
        return regressionTable.evaluateRegression(requestData, context);
    }

    public AbstractKiePMMLTable getRegressionTable() {
        return regressionTable;
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLRegressionModel> {

        private Builder(String name, MINING_FUNCTION miningFunction) {
            super("Regression-", PMML_MODEL.REGRESSION_MODEL, miningFunction, () -> new KiePMMLRegressionModel(name));
        }

        public Builder withAbstractKiePMMLTable(AbstractKiePMMLTable regressionTable) {
            if (regressionTable != null) {
                toBuild.regressionTable = regressionTable;
            }
            return this;
        }
    }
}
