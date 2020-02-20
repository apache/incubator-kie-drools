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

package org.kie.pmml.models.regression.compiler.factories;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dmg.pmml.regression.CategoricalPredictor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;

import static org.junit.Assert.assertEquals;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLCategoricalPredictorFactory.getKiePMMLCategoricalPredictor;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLCategoricalPredictorFactory.getKiePMMLCategoricalPredictors;

@RunWith(Parameterized.class)
public class KiePMMLCategoricalPredictorFactoryTest {

    private String name;
    private double value;
    private double coefficient;
    private CategoricalPredictor categoricalPredictor;

    public KiePMMLCategoricalPredictorFactoryTest(String name, double value, double coefficient) {
        this.name = name;
        this.value = value;
        this.coefficient = coefficient;
        categoricalPredictor = getCategoricalPredictor(name, value, coefficient);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"professional", 27.0, 3.5},
                {"clerical", 64.0, 27.4}
        });
    }

    @Test
    public void getKiePMMLCategoricalPredictorsTest() {
        List<CategoricalPredictor> categoricalPredictors = Collections.singletonList(categoricalPredictor);
        final Set<KiePMMLCategoricalPredictor> retrieved = getKiePMMLCategoricalPredictors(categoricalPredictors);
        retrieved.forEach(this::commonValidateKiePMMLCategoricalPredictor);
    }

    @Test
    public void getKiePMMLCategoricalPredictorTest() {
        final KiePMMLCategoricalPredictor retrieved = getKiePMMLCategoricalPredictor(categoricalPredictor);
        commonValidateKiePMMLCategoricalPredictor(retrieved);
    }

    private void commonValidateKiePMMLCategoricalPredictor(KiePMMLCategoricalPredictor retrieved) {
        assertEquals(name, retrieved.getName());
        assertEquals(value, retrieved.getValue());
        assertEquals(coefficient, retrieved.getCoefficient());
    }
}