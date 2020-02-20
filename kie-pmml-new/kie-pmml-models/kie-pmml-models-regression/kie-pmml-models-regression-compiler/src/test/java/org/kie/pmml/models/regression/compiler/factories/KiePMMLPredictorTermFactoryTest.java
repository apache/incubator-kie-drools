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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.regression.PredictorTerm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLPredictorTerm;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLRegressionTablePredictor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLNumericPredictorFactory.getKiePMMLNumericPredictor;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLPredictorTermFactory.getKiePMMLPredictorTerm;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLPredictorTermFactory.getKiePMMLPredictorTerms;

@RunWith(Parameterized.class)
public class KiePMMLPredictorTermFactoryTest {

    private String name;
    private double coefficient;
    private PredictorTerm predictorTerm;
    private Set<KiePMMLRegressionTablePredictor> kiePMMLRegressionTablePredictors;
    private Set<String> kiePMMLRegressionTablePredictorNames;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"firstProfessional", 4, 3.5, "firstClerical", 5, 27.4, "firstTerm", 12.4},
                {"secondProfessional", 2, 12.55, "secondClerical", 11, 2743.22, "secondTerm", 0.33},
        });
    }

    public KiePMMLPredictorTermFactoryTest(String nameA, int exponentA, double coefficientA, String nameB, int exponentB, double coefficientB, String name, double coefficient) {
        this.name = name;
        this.coefficient = coefficient;
        predictorTerm = getPredictorTerm(name, coefficient, Arrays.asList(nameA, nameB));
        KiePMMLNumericPredictor kiePMMLNumericPredictorA = getKiePMMLNumericPredictor(getNumericPredictor(nameA, exponentA, coefficientA));
        KiePMMLNumericPredictor kiePMMLNumericPredictorB = getKiePMMLNumericPredictor(getNumericPredictor(nameB, exponentB, coefficientB));
        kiePMMLRegressionTablePredictors = new HashSet<>(Arrays.asList(kiePMMLNumericPredictorA, kiePMMLNumericPredictorB));
        kiePMMLRegressionTablePredictorNames = new HashSet<>(kiePMMLRegressionTablePredictors.stream().map(KiePMMLRegressionTablePredictor::getName).collect(Collectors.toList()));
    }

    @Test
    public void getKiePMMLPredictorTermsTest() {
        List<PredictorTerm> predictorTerms = Collections.singletonList(predictorTerm);
        final Set<KiePMMLPredictorTerm> retrieved = getKiePMMLPredictorTerms(predictorTerms, kiePMMLRegressionTablePredictors);
        retrieved.forEach(this::commonValidateKiePMMLPredictorTerm);
    }

    @Test
    public void getKiePMMLPredictorTermTest() {
        final KiePMMLPredictorTerm retrieved = getKiePMMLPredictorTerm(predictorTerm, kiePMMLRegressionTablePredictors);
        commonValidateKiePMMLPredictorTerm(retrieved);
    }

    private void commonValidateKiePMMLPredictorTerm(KiePMMLPredictorTerm retrieved) {
        assertEquals(name, retrieved.getName());
        assertEquals(coefficient, retrieved.getCoefficient());
        assertNotNull(retrieved.getPredictors());
        assertEquals(kiePMMLRegressionTablePredictors.size(), retrieved.getPredictors().size());
        retrieved.getPredictors()
                .forEach(predictor -> assertTrue(kiePMMLRegressionTablePredictorNames.contains(predictor.getName())));
    }
}