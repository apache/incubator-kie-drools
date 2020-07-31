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

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.BeforeClass;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.test.util.filesystem.FileUtils;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class AbstractKiePMMLFactoryTest {

    protected static final String SOURCE_REGRESSION = "MiningModel_Regression.pmml";
    protected static final KnowledgeBuilderImpl KNOWLEDGE_BUILDER = new KnowledgeBuilderImpl();
    protected static DataDictionary DATA_DICTIONARY;
    protected static TransformationDictionary TRANSFORMATION_DICTIONARY;
    protected static MiningModel MINING_MODEL;

    @BeforeClass
    public static void setup() throws JAXBException, SAXException, IOException {
        FileInputStream fis = FileUtils.getFileInputStream(SOURCE_REGRESSION);
        PMML pmml = KiePMMLUtil.load(fis, SOURCE_REGRESSION);
        assertNotNull(pmml);
        DATA_DICTIONARY = pmml.getDataDictionary();
        assertNotNull(DATA_DICTIONARY);
        TRANSFORMATION_DICTIONARY = pmml.getTransformationDictionary();
        assertTrue(pmml.getModels().get(0) instanceof MiningModel);
        MINING_MODEL = (MiningModel) pmml.getModels().get(0);
        assertNotNull(MINING_MODEL);
    }

}