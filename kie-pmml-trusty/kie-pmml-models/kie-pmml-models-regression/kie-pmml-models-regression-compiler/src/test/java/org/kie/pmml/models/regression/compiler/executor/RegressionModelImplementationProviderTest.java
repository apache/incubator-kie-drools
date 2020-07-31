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

package org.kie.pmml.models.regression.compiler.executor;

import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModelWithSources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RegressionModelImplementationProviderTest {

    private static final RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();
    private static final String RELEASE_ID = "org.drools:kie-pmml-models-testing:1.0";
    private static final String SOURCE_1 = "LinearRegressionSample.pmml";
    private static final String SOURCE_2 = "test_regression.pmml";
    private static final String SOURCE_3 = "test_regression_clax.pmml";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);
        final KiePMMLRegressionModel retrieved = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), pmml.getTransformationDictionary(), (RegressionModel) pmml.getModels().get(0), RELEASE_ID);
        assertNotNull(retrieved);
    }

    @Test
    public void getKiePMMLModelFromPlugin() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);
        final String packageName = "packagename";
        final KiePMMLRegressionModel retrieved = PROVIDER.getKiePMMLModelFromPlugin(
                packageName,
                pmml.getDataDictionary(),
                pmml.getTransformationDictionary(),
                (RegressionModel) pmml.getModels().get(0), RELEASE_ID);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLRegressionModelWithSources);
        KiePMMLRegressionModelWithSources retrievedWithSources = (KiePMMLRegressionModelWithSources)retrieved;
        final Map<String, String> sourcesMap = retrievedWithSources.getSourcesMap();
        assertNotNull(sourcesMap);
        assertFalse(sourcesMap.isEmpty());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void validateSource2() throws Exception {
        commonValidateSource(SOURCE_2);
    }

    @Test
    public void validateSource3() throws Exception {
        commonValidateSource(SOURCE_3);
    }

    private void commonValidateSource(String sourceFile) throws Exception {
        final PMML pmml = TestUtils.loadFromFile(sourceFile);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);
        PROVIDER.validate(pmml.getDataDictionary(), (RegressionModel) pmml.getModels().get(0));
    }
}