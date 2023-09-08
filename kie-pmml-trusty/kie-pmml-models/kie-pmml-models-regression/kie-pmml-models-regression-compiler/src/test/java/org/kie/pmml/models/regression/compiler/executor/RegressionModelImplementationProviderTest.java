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
package org.kie.pmml.models.regression.compiler.executor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.jupiter.api.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.CAUCHIT;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.CLOGLOG;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.EXP;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.LOGIT;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.LOGLOG;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.NONE;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.PROBIT;
import static org.dmg.pmml.regression.RegressionModel.NormalizationMethod.SOFTMAX;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;

public class RegressionModelImplementationProviderTest {

    private static final RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();
    private static final String RELEASE_ID = "org.drools:kie-pmml-models-testing:1.0";
    private static final String SOURCE_1 = "LinearRegressionSample.pmml";
    private static final String SOURCE_2 = "test_regression.pmml";
    private static final String SOURCE_3 = "test_regression_clax.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static final List<RegressionModel.NormalizationMethod> VALID_NORMALIZATION_METHODS = Arrays.asList(NONE,
                                                                                                               SOFTMAX,
                                                                                                               LOGIT,
                                                                                                               EXP,
                                                                                                               PROBIT,
                                                                                                               CLOGLOG,
                                                                                                               LOGLOG,
                                                                                                               CAUCHIT);

    @Test
    void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.REGRESSION_MODEL);
    }

    @Test
    void getKiePMMLModelWithSources() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);
        assertThat(pmml.getModels().get(0)).isInstanceOf(RegressionModel.class);
        RegressionModel regressionModel = (RegressionModel) pmml.getModels().get(0);
        final CommonCompilationDTO<RegressionModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       SOURCE_1);
        final KiePMMLModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        final Map<String, String> sourcesMap = retrieved.getSourcesMap();
        assertThat(sourcesMap).isNotNull();
        assertThat(sourcesMap).isNotEmpty();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
        for (Class<?> clazz : compiled.values()) {
            assertThat(clazz).isInstanceOf(Serializable.class);
        }
    }

    @Test
    void validateNormalizationMethodValid() {
        VALID_NORMALIZATION_METHODS.forEach(PROVIDER::validateNormalizationMethod);
    }

    @Test
    void validateNormalizationMethodInvalid() {
        for (RegressionModel.NormalizationMethod normalizationMethod : RegressionModel.NormalizationMethod.values()) {
            if (!VALID_NORMALIZATION_METHODS.contains(normalizationMethod)) {
                try {
                    PROVIDER.validateNormalizationMethod(normalizationMethod);
                    fail("Expecting failure due to invalid normalization method " + normalizationMethod);
                } catch (KiePMMLException e) {
                    // Expected
                }
            }
        }
    }

    @Test
    void validateSource2() throws Exception {
        commonValidateSource(SOURCE_2);
    }

    @Test
    void validateSource3() throws Exception {
        commonValidateSource(SOURCE_3);
    }

    @Test
    void validateNoRegressionTables() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);
        assertThat(pmml.getModels().get(0)).isInstanceOf(RegressionModel.class);
        RegressionModel regressionModel = (RegressionModel) pmml.getModels().get(0);
        regressionModel.getRegressionTables().clear();
        final List<Field<?>> fields = getFieldsFromDataDictionary(pmml.getDataDictionary());
        try {
            PROVIDER.validate(fields, regressionModel);
            fail("Expecting validation failure due to missing RegressionTables");
        } catch (KiePMMLException e) {
            // Expected
        }
        regressionModel = new RegressionModel(regressionModel.getMiningFunction(), regressionModel.getMiningSchema(),
                null);
        try {
            PROVIDER.validate(fields, regressionModel);
            fail("Expecting validation failure due to missing RegressionTables");
        } catch (KiePMMLException e) {
            // Expected
        }
    }

    private void commonValidateSource(String sourceFile) throws Exception {
        final PMML pmml = TestUtils.loadFromFile(sourceFile);
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);
        assertThat(pmml.getModels().get(0)).isInstanceOf(RegressionModel.class);
        PROVIDER.validate(getFieldsFromDataDictionary(pmml.getDataDictionary()), (RegressionModel) pmml.getModels().get(0));
    }
}