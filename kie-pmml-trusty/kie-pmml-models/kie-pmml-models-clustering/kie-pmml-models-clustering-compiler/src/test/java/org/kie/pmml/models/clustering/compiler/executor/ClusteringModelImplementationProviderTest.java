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
package org.kie.pmml.models.clustering.compiler.executor;

import java.io.Serializable;
import java.util.Map;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.clustering.ClusteringModel;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class ClusteringModelImplementationProviderTest {

    private static final String SOURCE_FILE = "SingleIrisKMeansClustering.pmml";

    private static final ClusteringModelImplementationProvider PROVIDER = new ClusteringModelImplementationProvider();

    private static ClusteringModel getModel(PMML pmml) {
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);

        Model model = pmml.getModels().get(0);
        assertThat(model).isInstanceOf(ClusteringModel.class);

        return (ClusteringModel) model;
    }

    @Test
    public void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.CLUSTERING_MODEL);
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        PMML pmml = TestUtils.loadFromFile(SOURCE_FILE);
        ClusteringModel model = getModel(pmml);

        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       model,
                                                                       new HasClassLoaderMock());
        KiePMMLClusteringModel retrieved = PROVIDER.getKiePMMLModel(compilationDTO);

        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(Serializable.class);
    }

    @Test
    public void getKiePMMLModelWithSources() throws Exception {
        PMML pmml = TestUtils.loadFromFile(SOURCE_FILE);
        ClusteringModel model = getModel(pmml);
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       model,
                                                                       new HasClassLoaderMock());
        KiePMMLModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);

        assertThat(retrieved).isNotNull();
        Map<String, String> sourcesMap = retrieved.getSourcesMap();
        assertThat(sourcesMap).isNotNull();
        assertThat(sourcesMap).isNotEmpty();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
        for (Class<?> clazz : compiled.values()) {
            assertThat(clazz).isInstanceOf(Serializable.class);
        }
    }
}
