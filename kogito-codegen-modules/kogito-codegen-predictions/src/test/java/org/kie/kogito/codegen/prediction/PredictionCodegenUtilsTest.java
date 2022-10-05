/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.prediction;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.drools.codegen.common.GeneratedFileType.STATIC_HTTP_RESOURCE;
import static org.drools.model.codegen.execmodel.GeneratedFile.Type.REST;
import static org.kie.kogito.pmml.CommonTestUtility.getKiePMMLModelInternal;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomMiningFields;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomOutputField;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomOutputFields;

class PredictionCodegenUtilsTest {

    private static KiePMMLModel kiePMMLModel;

    @BeforeAll
    public static void setup() {
        final List<MiningField> miningFields = getRandomMiningFields();
        final List<OutputField> outputFields = getRandomOutputFields();
        outputFields.add(getRandomOutputField(miningFields.get(miningFields.size() - 1).getName()));
        kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void checkModelHasSourcesMap(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel hasSourcesMap = new KiePMMLModelWithSources("fileName", "modelName", "kmodulePackageName",
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyMap(),
                true);

        commonVerifyExceptionThrownCheckModel(hasSourcesMap, "Unexpected HasSourcesMap instance");
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void checkModelNoModelName(KogitoBuildContext.Builder contextBuilder) {

        KiePMMLModel noModelName = getKiePMMLModelInternal("fileName", null, Collections.emptyList(),
                Collections.emptyList());
        commonVerifyExceptionThrownCheckModel(noModelName, "Model name should not be empty");
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void checkModelEmptyModelName(KogitoBuildContext.Builder contextBuilder) {
        KiePMMLModel emptyModelName = getKiePMMLModelInternal("fileName", "", Collections.emptyList(),
                Collections.emptyList());
        commonVerifyExceptionThrownCheckModel(emptyModelName, "Model name should not be empty");
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateModelBaseFiles(KogitoBuildContext.Builder contextBuilder) {
        final Collection<GeneratedFile> files = new ArrayList<>();
        String modelPath = "/model/path/PmmlModel.pmml";
        Map<String, byte[]> compiledClasses = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> compiledClasses.put("generated.package.Class" + i, new byte[0]));
        PMMLResource resource = new PMMLResource(Collections.singletonList(kiePMMLModel),
                Path.of(modelPath),
                modelPath,
                compiledClasses,
                Collections.emptyMap());
        PredictionCodegenUtils.generateModelBaseFiles(files, resource);
        assertThat(files).hasSize(compiledClasses.size());
        compiledClasses.keySet().forEach(fullClassName -> {
            assertThat(files).extracting(file -> file.path().toString()).contains(fullClassName);
            Optional<GeneratedFile> generatedFile =
                    files.stream().filter(file -> file.path().toString().equals(fullClassName))
                            .findFirst();
            assertThat(generatedFile).isPresent();
            assertThat(generatedFile
                    .get()
                    .type()
                    .name())
                            .isEqualTo(COMPILED_CLASS.name());
            assertThat(generatedFile
                    .get()
                    .type()
                    .category())
                            .isEqualTo(GeneratedFileType.Category.COMPILED_CLASS);
        });
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generateModelRESTFiles(KogitoBuildContext.Builder contextBuilder) {
        final Collection<GeneratedFile> files = new ArrayList<>();
        PredictionCodegenUtils.generateModelRESTFiles(files, kiePMMLModel, contextBuilder.build(),
                "ApplicationCanonicalName");
        assertThat(files).hasSize(2);
        assertThat(files).extracting(file -> file.type().name()).contains(STATIC_HTTP_RESOURCE.name(), REST.name());
    }

    private void commonVerifyExceptionThrownCheckModel(KiePMMLModel pmmlModel, String message) {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> PredictionCodegenUtils.checkModel(pmmlModel)).withMessageContaining(message);
    }
}
