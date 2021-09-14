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

import java.io.FileInputStream;
import java.io.Serializable;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.util.ClassUtils;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.compiler.commons.mocks.ExternalizableMock;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.models.drools.commons.implementations.HasKnowledgeBuilderMock;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TreeModelImplementationProviderTest {

    private static final TreeModelImplementationProvider PROVIDER = new TreeModelImplementationProvider();
    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.TREE_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLTreeModel retrieved = PROVIDER.getKiePMMLModel(PACKAGE_NAME,
                                                                    pmml.getDataDictionary(),
                                                                    pmml.getTransformationDictionary(),
                                                                    (TreeModel) pmml.getModels().get(0),
                                                                    new HasKnowledgeBuilderMock(knowledgeBuilder));
        assertNotNull(retrieved);
        commonVerifyIsDeepCloneable(retrieved);
    }

    @Test
    public void getKiePMMLModelWithSources() throws Exception {
        final PMML pmml = getPMML(SOURCE_1);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final KiePMMLDroolsModel retrieved = PROVIDER.getKiePMMLModelWithSources("PACKAGE_NAME",
                                                                                 pmml.getDataDictionary(),
                                                                                 pmml.getTransformationDictionary(),
                                                                                 (TreeModel) pmml.getModels().get(0),
                                                                                 new HasKnowledgeBuilderMock(knowledgeBuilder));
        assertNotNull(retrieved);
        commonVerifyIsDeepCloneable(retrieved);
    }

    private PMML getPMML(String source) throws Exception {
        final FileInputStream fis = FileUtils.getFileInputStream(source);
        final PMML toReturn = KiePMMLUtil.load(fis, source);
        assertNotNull(toReturn);
        assertEquals(1, toReturn.getModels().size());
        assertTrue(toReturn.getModels().get(0) instanceof TreeModel);
        return toReturn;
    }

    private void commonVerifyIsDeepCloneable(AbstractKiePMMLComponent toVerify) {
        assertTrue(toVerify instanceof Serializable);
        ExternalizableMock externalizableMock = new ExternalizableMock();
        externalizableMock.setKiePMMLComponent(toVerify);
        ClassUtils.deepClone(externalizableMock);
    }
}