/*
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
package org.kie.kogito.codegen.prediction;

import java.util.Collections;
import java.util.List;

import org.drools.codegen.common.AppPaths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class PredictionCodegenFactoryTest {

    static final String REFLECT_JSON = "reflect-config.json";

    private static final String EMPTY = "";
    private static final String MOCK = "mock";
    private static final String NESTED_MOCK = "nestedMock";

    @BeforeAll
    public static void setup() {
        System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, String.format("%s/test-classes", AppPaths.TARGET_DIR));
    }

    @AfterAll
    public static void cleanup() {
        System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithNullNameInvalidModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel nullNameMock = buildInvalidMockedModel(null);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, nullNameMock);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithEmptyNameInvalidModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel emptyNameMock = buildInvalidMockedModel(EMPTY);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, emptyNameMock);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithInvalidClassInvalidModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel invalidClassMock = buildInvalidMockedModel(MOCK);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, invalidClassMock);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithNullNameNestedModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel nullNameMock = buildMockedModelWithInvalidNestedMockedModel(null);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, nullNameMock);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithEmptyNameNestedModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel emptyNameMock = buildMockedModelWithInvalidNestedMockedModel(EMPTY);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, emptyNameMock);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateThrowsExceptionWithInvalidClassNestedModel(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel invalidClassMock = buildMockedModelWithInvalidNestedMockedModel(NESTED_MOCK);
        commonVerifyExceptionThrownByBuildMockedGenerateExecutable(contextBuilder, invalidClassMock);
    }

    private static KiePMMLModel buildInvalidMockedModel(String name) {
        KiePMMLModel mock = mock(KiePMMLModel.class);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

    private static KiePMMLModel buildMockedModelWithInvalidNestedMockedModel(String name) {
        KiePMMLModel mock = mock(KiePMMLModel.class, withSettings().extraInterfaces(HasSourcesMap.class,
                HasNestedModels.class));
        when(mock.getName()).thenReturn(MOCK);

        HasSourcesMap smMock = (HasSourcesMap) mock;
        when(smMock.getSourcesMap()).thenReturn(Collections.emptyMap());

        List<KiePMMLModel> nestedModelsMock = Collections.singletonList(buildInvalidMockedModel(name));
        HasNestedModels nmMock = (HasNestedModels) mock;
        when(nmMock.getNestedModels()).thenReturn(nestedModelsMock);

        return mock;
    }

    private static PMMLResource buildMockedResource(KiePMMLModel mockedModel) {
        PMMLResource mock = mock(PMMLResource.class);
        when(mock.getModelPath()).thenReturn(EMPTY);
        when(mock.getKiePmmlModels()).thenReturn(Collections.singletonList(mockedModel));
        return mock;
    }

    private static void buildMockedGenerateExecutable(KogitoBuildContext context, KiePMMLModel mockedModel) {
        List<PMMLResource> mockedResourceList = Collections.singletonList(buildMockedResource(mockedModel));
        PredictionCodegen codeGenerator = new PredictionCodegen(context, mockedResourceList);
        codeGenerator.generate();
    }

    private void commonVerifyExceptionThrownByBuildMockedGenerateExecutable(KogitoBuildContext.Builder contextBuilder, KiePMMLModel pmmlModel) {
        KogitoBuildContext context = contextBuilder.build();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> buildMockedGenerateExecutable(context, pmmlModel));
    }
}
