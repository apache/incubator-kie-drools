/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.regression.compiler.dto;

import java.util.List;

import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

public class RegressionCompilationDTO extends CompilationDTO<RegressionModel> {

    private static final long serialVersionUID = 640809755551594031L;
    private final List<RegressionTable> regressionTables;
    private final RegressionModel.NormalizationMethod defaultNormalizationMethod;
    private final RegressionModel.NormalizationMethod modelNormalizationMethod;

    /**
     * @param source
     * @param regressionTables
     * @param defaultNormalizationMethod This is used by <code>KiePMMLRegressionTableRegressionFactory</code> when it
     * has to been provided with <code>RegressionModel.NormalizationMethod.NONE</code>
     */
    public RegressionCompilationDTO(final CompilationDTO<RegressionModel> source,
                                    final List<RegressionTable> regressionTables,
                                    final RegressionModel.NormalizationMethod defaultNormalizationMethod) {
        super(source);
        this.regressionTables = regressionTables;
        this.defaultNormalizationMethod = defaultNormalizationMethod;
        modelNormalizationMethod = source.getModel().getNormalizationMethod();
    }

    public List<RegressionTable> getRegressionTables() {
        return regressionTables;
    }

    public RegressionModel.NormalizationMethod getDefaultNormalizationMethod() {
        return defaultNormalizationMethod;
    }

    public REGRESSION_NORMALIZATION_METHOD getDefaultREGRESSION_NORMALIZATION_METHOD() {
        return REGRESSION_NORMALIZATION_METHOD.byName(defaultNormalizationMethod.value());
    }

    public RegressionModel.NormalizationMethod getModelNormalizationMethod() {
        return modelNormalizationMethod;
    }

    public REGRESSION_NORMALIZATION_METHOD getModelREGRESSION_NORMALIZATION_METHOD() {
        return REGRESSION_NORMALIZATION_METHOD.byName(modelNormalizationMethod.value());
    }
}
