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
package org.kie.pmml.models.tree.compiler.executor;

import java.io.Serializable;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModelWithSources;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class TreeModelImplementationProviderTest {

    private static final TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();
    private static final String SOURCE_1 = "TreeSample.pmml";
    private static PMML pmml;

    @BeforeClass
    public static void setup() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
    }

    @Test
    public void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.TREE_MODEL);
    }

    @Test
    public void getKiePMMLModel() {
        TreeModel treeModel = (TreeModel) pmml.getModels().get(0);
        final CommonCompilationDTO<TreeModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       treeModel,
                                                                       new HasClassLoaderMock());
        final KiePMMLTreeModel retrieved = PROVIDER.getKiePMMLModel(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(Serializable.class);
    }

    @Test
    public void getKiePMMLModelWithSources() {
        TreeModel treeModel = (TreeModel) pmml.getModels().get(0);
        final CommonCompilationDTO<TreeModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       treeModel,
                                                                       new HasClassLoaderMock());
        final KiePMMLModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        final Map<String, String> sourcesMap = retrieved.getSourcesMap();
        assertThat(sourcesMap).isNotNull();
        assertThat(sourcesMap).isNotEmpty();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
            for (Class<?> clazz : compiled.values()) {
                assertThat(clazz).isInstanceOf(Serializable.class);
            }
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }
}