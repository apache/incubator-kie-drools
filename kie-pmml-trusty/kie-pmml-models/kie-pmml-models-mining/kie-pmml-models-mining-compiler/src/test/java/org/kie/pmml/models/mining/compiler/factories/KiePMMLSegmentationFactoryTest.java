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

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory.SEGMENTATIONNAME_TEMPLATE;

public class KiePMMLSegmentationFactoryTest extends AbstractKiePMMLFactoryTest {

    @BeforeClass
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
    }

    @Test
    public void getSegmentationSourcesMap() {
        final String segmentationName = "SEGMENTATION_NAME";
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> retrieved = KiePMMLSegmentationFactory.getSegmentationSourcesMap(PACKAGE_NAME,
                                                                                                   DERIVED_FIELDS,
                                                                                                   DATA_DICTIONARY,
                                                                                                   TRANSFORMATION_DICTIONARY,
                                                                                                   MINING_MODEL.getSegmentation(),
                                                                                                   segmentationName,
                                                                                                   new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER),
                                                                                                   nestedModels);
        assertNotNull(retrieved);
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
    }

    @Test
    public void getSegmentationSourcesMapCompiled() {
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, MINING_MODEL.getModelName());
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final HasKnowledgeBuilderMock hasKnowledgeBuilderMock = new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER);
        final List<String> expectedGeneratedClasses =
                MINING_MODEL.getSegmentation().getSegments().stream().map(segment -> {
                    String modelName = segment.getModel().getModelName();
                    String sanitizedPackageName = getSanitizedPackageName(PACKAGE_NAME + "."
                                                                                  + segmentationName + "."
                                                                                  + segment.getId() + "."
                                                                                  + modelName);
                    String sanitizedClassName = getSanitizedClassName(modelName);
                    return String.format(PACKAGE_CLASS_TEMPLATE, sanitizedPackageName, sanitizedClassName);
                }).collect(Collectors.toList());expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
                fail("Expecting class not found: " + expectedGeneratedClass);
            } catch (Exception e) {
                assertTrue(e instanceof ClassNotFoundException);
            }
        });
        final Map<String, String> retrieved = KiePMMLSegmentationFactory.getSegmentationSourcesMapCompiled(PACKAGE_NAME,
                                                                                                           DERIVED_FIELDS,
                                                                                                           DATA_DICTIONARY,
                                                                                                           TRANSFORMATION_DICTIONARY,
                                                                                                           MINING_MODEL.getSegmentation(),
                                                                                                           segmentationName,
                                                                                                           hasKnowledgeBuilderMock,
                                                                                                           nestedModels);
        assertNotNull(retrieved);
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
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