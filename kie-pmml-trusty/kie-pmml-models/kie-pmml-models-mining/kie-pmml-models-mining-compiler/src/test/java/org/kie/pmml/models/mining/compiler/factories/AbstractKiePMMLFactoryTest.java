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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segment;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Before;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.test.util.filesystem.FileUtils;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.utils.KiePMMLUtil.SEGMENTID_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getDerivedFields;

public abstract class AbstractKiePMMLFactoryTest {

    protected static final String SOURCE_MIXED = "MiningModel_Mixed.pmml";
    protected static final String PACKAGE_NAME = "packagename";
    protected static DataDictionary DATA_DICTIONARY;
    protected static TransformationDictionary TRANSFORMATION_DICTIONARY;
    protected static MiningModel MINING_MODEL;
    protected static  List<DerivedField> DERIVED_FIELDS;
    protected static KnowledgeBuilderImpl KNOWLEDGE_BUILDER;

    protected static void innerSetup() throws JAXBException, SAXException, IOException {
        FileInputStream fis = FileUtils.getFileInputStream(SOURCE_MIXED);
        PMML pmml = KiePMMLUtil.load(fis, SOURCE_MIXED);
        assertNotNull(pmml);
        DATA_DICTIONARY = pmml.getDataDictionary();
        assertNotNull(DATA_DICTIONARY);
        TRANSFORMATION_DICTIONARY = pmml.getTransformationDictionary();
        assertTrue(pmml.getModels().get(0) instanceof MiningModel);
        MINING_MODEL = (MiningModel) pmml.getModels().get(0);
        assertNotNull(MINING_MODEL);
        populateMissingIds(MINING_MODEL);
        DERIVED_FIELDS = getDerivedFields(TRANSFORMATION_DICTIONARY,
                                          MINING_MODEL.getLocalTransformations());
    }

    @Before
    public void init() {
        KNOWLEDGE_BUILDER = new KnowledgeBuilderImpl();
    }

    /**
     * Recursively populate <code>Segment</code>s with auto generated id
     * if missing in original model
     */
    private static void populateMissingIds(final MiningModel model) {
        final List<Segment> segments =model.getSegmentation().getSegments();
        for (int i = 0; i < segments.size(); i ++) {
            Segment segment = segments.get(i);
            if (segment.getId() == null || segment.getId().isEmpty()) {
                String toSet = String.format(SEGMENTID_TEMPLATE, model.getModelName(), i);
                segment.setId(toSet);
                if (segment.getModel() instanceof MiningModel) {
                    populateMissingIds((MiningModel) segment.getModel());
                }
            }
        }
    }

}