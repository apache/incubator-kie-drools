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

import org.dmg.pmml.regression.NumericPredictor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.models.regression.model.predictors.KiePMMLNumericPredictor;

import static org.junit.Assert.assertEquals;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictor;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictors;

@RunWith(Parameterized.class)
public class KiePMMLNumericPredictorFactoryTest {

    private String name;
    private int exponent;
    private double coefficient;
    private NumericPredictor numericPredictor;

    public KiePMMLNumericPredictorFactoryTest(String name, int exponent, double coefficient) {
        this.name = name;
        this.exponent = exponent;
        this.coefficient = coefficient;
        numericPredictor = getNumericPredictor(name, exponent, coefficient);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"professional", 4, 3.5},
                {"clerical", 5, 27.4}
        });
    }

    @Test
    public void getKiePMMLNumericPredictorsTest() {
        List<NumericPredictor> categoricalPredictors = Collections.singletonList(numericPredictor);
        final Set<KiePMMLNumericPredictor> retrieved = getKiePMMLNumericPredictors(categoricalPredictors);
        retrieved.forEach(this::commonValidateKiePMMLNumericPredictor);
    }

    @Test
    public void getKiePMMLNumericPredictorTest() {
        final KiePMMLNumericPredictor retrieved = getKiePMMLNumericPredictor(numericPredictor);
        commonValidateKiePMMLNumericPredictor(retrieved);
    }

    private void commonValidateKiePMMLNumericPredictor(KiePMMLNumericPredictor retrieved) {
        assertEquals(name, retrieved.getName());
        assertEquals(exponent, retrieved.getExponent());
        assertEquals(coefficient, retrieved.getCoefficient());
    }
}