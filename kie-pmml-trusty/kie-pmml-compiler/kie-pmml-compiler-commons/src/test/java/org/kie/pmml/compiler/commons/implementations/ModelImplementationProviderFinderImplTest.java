/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.commons.implementations;

import java.util.List;

import org.dmg.pmml.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.api.provider.ModelImplementationProvider;
import org.kie.pmml.compiler.commons.mocks.TestingModelImplementationProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelImplementationProviderFinderImplTest {

    private ModelImplementationProviderFinderImpl modelImplementationProviderFinder;

    @BeforeEach
    public void setUp() throws Exception {
        modelImplementationProviderFinder = new ModelImplementationProviderFinderImpl();
    }

    @Test
 <T extends Model, E extends KiePMMLModel> void getImplementations() {
        final List<ModelImplementationProvider<T, E>> retrieved = modelImplementationProviderFinder.getImplementations(false);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
        assertThat(retrieved.get(0)).isInstanceOf(TestingModelImplementationProvider.class);
    }
}