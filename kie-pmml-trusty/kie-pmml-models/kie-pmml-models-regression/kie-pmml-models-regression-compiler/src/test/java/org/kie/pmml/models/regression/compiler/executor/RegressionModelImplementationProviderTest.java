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

import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RegressionModelImplementationProviderTest {

    private final static RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();
    private final static String RELEASE_ID = "org.drools:kie-pmml-models-testing:1.0";
    private static final String SOURCE_1 = "LinearRegressionSample.xml";
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
        final KiePMMLRegressionModel kiePMMLModel = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), pmml.getTransformationDictionary(), (RegressionModel) pmml.getModels().get(0), RELEASE_ID);
        assertNotNull(kiePMMLModel);
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