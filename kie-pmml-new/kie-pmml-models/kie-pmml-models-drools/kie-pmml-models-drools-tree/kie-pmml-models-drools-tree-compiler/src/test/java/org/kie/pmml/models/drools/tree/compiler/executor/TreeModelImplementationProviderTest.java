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
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TreeModelImplementationProviderTest {

    private static final TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();
    private final static KnowledgeBuilder KNOWLEDGE_BUILDER = new KnowledgeBuilderImpl();
    private static final String SOURCE_1 = "TreeSample.pmml";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.TREE_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        final KiePMMLTreeModel kiePMMLModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), (TreeModel) pmml.getModels().get(0), KNOWLEDGE_BUILDER);
        assertNotNull(kiePMMLModel);
    }
}