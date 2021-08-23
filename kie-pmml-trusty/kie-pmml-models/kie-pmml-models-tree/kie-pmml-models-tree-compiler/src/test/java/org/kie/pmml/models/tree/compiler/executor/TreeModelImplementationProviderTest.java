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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.kie.pmml.models.tree.model.KiePMMLTreeModelWithSources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TreeModelImplementationProviderTest {

    private static final TreeModelImplementationProvider PROVIDER= new TreeModelImplementationProvider();
    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static PMML pmml;

    @BeforeClass
    public static void setup() throws Exception {
         pmml = TestUtils.loadFromFile(SOURCE_1);
    }

    @Test
    public void getPMMLModelType(){
        assertEquals(PMML_MODEL.TREE_MODEL,PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() {
        final KiePMMLTreeModel retrieved = PROVIDER.getKiePMMLModel(PACKAGE_NAME,
                                                                    pmml.getDataDictionary(),
                                                                    pmml.getTransformationDictionary(),
                                                                    (TreeModel) pmml.getModels().get(0),
                                                                    new HasClassLoaderMock());
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof Serializable);
    }

    @Test
    public void getKiePMMLModelWithSources() {
        final KiePMMLTreeModel retrieved = PROVIDER.getKiePMMLModelWithSources("PACKAGE_NAME",
                                                                                 pmml.getDataDictionary(),
                                                                                 pmml.getTransformationDictionary(),
                                                                                 (TreeModel) pmml.getModels().get(0),
                                                                               new HasClassLoaderMock());
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLTreeModelWithSources);
        KiePMMLTreeModelWithSources retrievedWithSources = (KiePMMLTreeModelWithSources) retrieved;
        assertTrue(retrievedWithSources instanceof Serializable);
        final Map<String, String> sourcesMap = retrievedWithSources.getSourcesMap();
        assertNotNull(sourcesMap);
        assertFalse(sourcesMap.isEmpty());
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
            for (Class<?> clazz : compiled.values()) {
                assertTrue(clazz instanceof Serializable);
            }
        } catch (Throwable t) {
            fail(t.getMessage());
        }
    }

}