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

package org.kie.pmml.tree.executor;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.tree.KiePMMLTreeModel;
import org.kie.pmml.library.testutils.TestUtils;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TreeModelImplementationProviderTest {

    private final static TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();


    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.TREE_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws JAXBException, SAXException, IOException, KiePMMLException {
        final PMML pmml = TestUtils.loadFromFile("TreeSample.xml");
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        final TreeModel originalModel = (TreeModel) pmml.getModels().get(0);
        commonVerifyKiePMMLTreeModel(originalModel, PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), originalModel));
    }

    private void commonVerifyKiePMMLTreeModel(TreeModel originalModel, KiePMMLTreeModel kiePMMLTreeModel) {
        assertEquals(originalModel.getModelName(), kiePMMLTreeModel.getName());
        // TODO {gcardosi} complete test

    }
}