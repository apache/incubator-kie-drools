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

package org.kie.pmml.compiler.commons.implementations;

import java.util.List;

import org.dmg.pmml.Model;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.commons.mocks.TestingModelImplementationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModelImplementationProviderFinderImplTest {

    private ModelImplementationProviderFinderImpl modelImplementationProviderFinder;

    @Before
    public void setUp() throws Exception {
        modelImplementationProviderFinder = new ModelImplementationProviderFinderImpl();
    }

    @Test
    public <T extends Model, E extends KiePMMLModel> void getImplementations() {
        final List<ModelImplementationProvider<T, E>> retrieved = modelImplementationProviderFinder.getImplementations(false);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        assertTrue(retrieved.get(0) instanceof TestingModelImplementationProvider);
    }
}