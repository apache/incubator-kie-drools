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

package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.mining.MiningModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.xml.sax.SAXException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;

public class KiePMMLSegmentationFactoryTest extends AbstractKiePMMLFactoryTest {

    @BeforeClass
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
    }

    @Test
    public void getSegmentationSourcesMap() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved = KiePMMLSegmentationFactory.getSegmentationSourcesMap(compilationDTO,
                                                                                                   nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertThat(nestedModels).hasSize(expectedNestedModels);
    }

    @Test
    public void getSegmentationSourcesMapCompiled() {
        final HasKnowledgeBuilderMock hasKnowledgeBuilderMock = new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER);
        final CommonCompilationDTO<MiningModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       MINING_MODEL,
                                                                       hasKnowledgeBuilderMock);
        final MiningModelCompilationDTO compilationDTO =
                MiningModelCompilationDTO.fromCompilationDTO(source);
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final List<String> expectedGeneratedClasses =
                MINING_MODEL.getSegmentation().getSegments().stream().map(this::getExpectedNestedModelClass).collect(Collectors.toList());
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
                fail("Expecting class not found: " + expectedGeneratedClass);
            } catch (Exception e) {
                assertThat(e).isInstanceOf(ClassNotFoundException.class);
            }
        });
        final Map<String, String> retrieved =
                KiePMMLSegmentationFactory.getSegmentationSourcesMapCompiled(compilationDTO, nestedModels);
        assertThat(retrieved).isNotNull();
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertThat(nestedModels).hasSize(expectedNestedModels);
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
            } catch (Exception e) {
                fail("Expecting class to be loaded, but got: " + e.getClass().getName() + " -> " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}