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

package org.kie.pmml.compiler.executor;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class PMMLKiePMMLRegressionCompilerImplTest {

    private static final PMMLCompiler EXECUTOR = new PMMLCompilerImpl();

    private KieBuilder kieBuilder;

    @Before
    public void setup() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kieBuilder = ks.newKieBuilder(kfs);
    }

    @Test
    public void getModels() throws Exception {
        final List<KiePMMLModel> results = EXECUTOR.getModels(getFileInputStream("LinearRegressionSample.pmml"), kieBuilder);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0) instanceof KiePMMLRegressionModel);
        commonVerifyKiePMMLRegressionModel((KiePMMLRegressionModel) results.get(0));
    }

    // TODO {gcardosi} Carbon-copy of org.kie.pmml.regression.evaluator.RegressionModelImplementationProviderTest

    private void commonVerifyKiePMMLRegressionModel(KiePMMLRegressionModel retrieved) {
        assertNotNull(retrieved);
        assertEquals(MINING_FUNCTION.REGRESSION, retrieved.getMiningFunction());
    }
}