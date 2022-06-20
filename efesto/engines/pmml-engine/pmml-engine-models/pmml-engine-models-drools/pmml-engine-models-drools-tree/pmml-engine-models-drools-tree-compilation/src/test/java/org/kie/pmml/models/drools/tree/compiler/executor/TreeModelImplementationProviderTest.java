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
package org.kie.pmml.models.drools.tree.compiler.executor;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.utils.FileUtils;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compilation.api.dto.CommonCompilationDTO;
import org.kie.pmml.compilation.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compilation.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class TreeModelImplementationProviderTest {

    private static final TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();
    private static final String SOURCE_BASE = "TreeSample";
    private static final String SOURCE_1 = SOURCE_BASE + ".pmml";

    @Test
    void getPMMLModelType() {
        assertThat(PROVIDER.getPMMLModelType()).isEqualTo(PMML_MODEL.TREE_MODEL);
    }


    @Test
    void getKiePMMLModelWithSources() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        final CommonCompilationDTO<TreeModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                        pmml,
                        (TreeModel) pmml.getModels().get(0),
                        new HasClassLoaderMock(), SOURCE_BASE);
        final KiePMMLDroolsModelWithSources retrieved = PROVIDER.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertThat(toReturn).isNotNull();
        assertThat(toReturn.getModels()).hasSize(1);
        assertThat(toReturn.getModels().get(0)).isInstanceOf(TreeModel.class);
        return toReturn;
    }

}