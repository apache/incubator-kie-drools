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
package org.kie.pmml.models.clustering.compiler.executor;

import java.io.Serializable;
import java.util.Map;

import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.dmg.pmml.clustering.ClusteringModel;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModelWithSources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClusteringModelImplementationProviderTest {

    private static final String SOURCE_FILE = "SingleIrisKMeansClustering.pmml";
    private static final String PACKAGE_NAME = "singleiriskmeansclustering";

    private static final ClusteringModelImplementationProvider PROVIDER = new ClusteringModelImplementationProvider();

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.CLUSTERING_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        PMML pmml = TestUtils.loadFromFile(SOURCE_FILE);
        ClusteringModel model = getModel(pmml);

        KiePMMLClusteringModel retrieved = PROVIDER.getKiePMMLModel(PACKAGE_NAME,
                pmml.getDataDictionary(),
                pmml.getTransformationDictionary(),
                model,
                new HasClassLoaderMock());

        assertNotNull(retrieved);
        assertTrue(retrieved instanceof Serializable);
    }

    @Test
    public void getKiePMMLModelWithSources() throws Exception {
        PMML pmml = TestUtils.loadFromFile(SOURCE_FILE);
        ClusteringModel model = getModel(pmml);

        KiePMMLClusteringModel retrieved = PROVIDER.getKiePMMLModelWithSources(PACKAGE_NAME,
                pmml.getDataDictionary(),
                pmml.getTransformationDictionary(),
                model,
                new HasClassLoaderMock());

        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLClusteringModelWithSources);

        KiePMMLClusteringModelWithSources retrievedWithSources = (KiePMMLClusteringModelWithSources) retrieved;
        assertTrue(retrievedWithSources instanceof Serializable);

        Map<String, String> sourcesMap = retrievedWithSources.getSourcesMap();
        assertNotNull(sourcesMap);
        assertFalse(sourcesMap.isEmpty());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Map<String, Class<?>> compiled = KieMemoryCompiler.compile(sourcesMap, classLoader);
        for (Class<?> clazz : compiled.values()) {
            assertTrue(clazz instanceof Serializable);
        }
    }

    private static ClusteringModel getModel(PMML pmml) {
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());

        Model model = pmml.getModels().get(0);
        assertTrue(model instanceof ClusteringModel);

        return (ClusteringModel) model;
    }
}
