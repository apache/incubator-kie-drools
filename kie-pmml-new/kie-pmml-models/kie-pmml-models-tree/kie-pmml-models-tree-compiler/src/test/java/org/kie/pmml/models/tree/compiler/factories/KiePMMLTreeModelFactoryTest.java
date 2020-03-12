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

package org.kie.pmml.models.tree.compiler.factories;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class KiePMMLTreeModelFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactoryTest.class);
    private static final String modelName = "golfing";
    private PMML pmml;


    @Before
    public void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
     }

    @Test
    public void getKiePMMLTreeModel() {
        KiePMMLTreeModel retrieved = KiePMMLTreeModelFactory.getKiePMMLTreeModel(pmml.getDataDictionary(), (TreeModel) pmml.getModels().get(0));

    }
}