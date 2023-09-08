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
package org.kie.pmml.models.regression.compiler.dto;

import java.util.List;
import java.util.Objects;

import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.dto.AbstractSpecificCompilationDTO;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

public class RegressionCompilationDTO extends AbstractSpecificCompilationDTO<RegressionModel> {

    private static final long serialVersionUID = 640809755551594031L;
    private final List<RegressionTable> regressionTables;
    private final RegressionModel.NormalizationMethod defaultNormalizationMethod;
    private final RegressionModel.NormalizationMethod modelNormalizationMethod;

    /**
     * Private constructor that preserve given <b>regressionTables</b> and <b>defaultNormalizationMethod</b>
     *
     * @param source
     * @param regressionTables
     * @param defaultNormalizationMethod This is used by <code>KiePMMLRegressionTableRegressionFactory</code> when it
     *                                   has to been provided with <code>RegressionModel.NormalizationMethod.NONE</code>
     */
    private RegressionCompilationDTO(final CompilationDTO<RegressionModel> source,
                                     final List<RegressionTable> regressionTables,
                                     final RegressionModel.NormalizationMethod defaultNormalizationMethod) {
        super(source);
        this.regressionTables = regressionTables;
        this.defaultNormalizationMethod = defaultNormalizationMethod;
        modelNormalizationMethod = source.getModel().getNormalizationMethod();
    }

    /**
     * Private constructor that use <b>regressionTables</b> and <b>defaultNormalizationMethod</b> from the given
     * <b>source</b>
     *
     * @param source
     */
    private RegressionCompilationDTO(final CompilationDTO<RegressionModel> source) {
        this(source, source.getModel().getRegressionTables(), source.getModel().getNormalizationMethod());
    }

    /**
     * Builder that preserve given <b>regressionTables</b> and <b>defaultNormalizationMethod</b>
     * <p>
     * This is used by <code>KiePMMLRegressionTableRegressionFactory</code> when it
     * has to been provided with <code>RegressionModel.NormalizationMethod.NONE</code>
     *
     * @param source
     * @param regressionTables
     * @param defaultNormalizationMethod
     * @return
     */
    public static RegressionCompilationDTO fromCompilationDTORegressionTablesAndNormalizationMethod(final CompilationDTO<RegressionModel> source,
                                                                                                    final List<RegressionTable> regressionTables,
                                                                                                    final RegressionModel.NormalizationMethod defaultNormalizationMethod) {
        return new RegressionCompilationDTO(source, regressionTables, defaultNormalizationMethod);
    }

    /**
     * Builder that that use <b>regressionTables</b> and <b>defaultNormalizationMethod</b> from the given
     * <b>source</b>
     *
     * @param source
     * @return
     */
    public static RegressionCompilationDTO fromCompilationDTO(final CompilationDTO<RegressionModel> source) {
        return new RegressionCompilationDTO(source);
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

    public OP_TYPE getOP_TYPE() {
        OpType opType = getOpType();
        return opType != null ? OP_TYPE.byName(opType.value()) : null;
    }

    public boolean isBinary(int tableSize) {
        return Objects.equals(OpType.CATEGORICAL, getOpType()) && tableSize == 2;
    }

    public boolean isRegression() {
        final DataField targetDataField = getTargetDataField();
        final OpType targetOpType = targetDataField != null ? targetDataField.getOpType() : null;
        return Objects.equals(MiningFunction.REGRESSION, getMiningFunction()) && (targetDataField == null || Objects.equals(OpType.CONTINUOUS, targetOpType));
    }
}
