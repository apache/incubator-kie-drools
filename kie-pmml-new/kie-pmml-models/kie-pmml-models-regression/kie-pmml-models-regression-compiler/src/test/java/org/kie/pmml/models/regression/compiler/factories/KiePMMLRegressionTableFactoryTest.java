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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRegressionTable;

@RunWith(Parameterized.class)
public class KiePMMLRegressionTableFactoryTest {

    private double intercept;
    private Object targetCategory;
    private RegressionTable regressionTable;
    private List<CategoricalPredictor> categoricalPredictors;
    private List<NumericPredictor> numericPredictors;
    private List<PredictorTerm> predictorTerms;

    public KiePMMLRegressionTableFactoryTest(double intercept, Object targetCategory) {
        this.intercept = intercept;
        this.targetCategory = targetCategory;
        categoricalPredictors = new ArrayList<>();
        numericPredictors = new ArrayList<>();
        predictorTerms = new ArrayList<>();
        Random random = new Random();
        IntStream.range(0, 3).forEach(i -> {
            categoricalPredictors.add(getCategoricalPredictor("CatPred-" + i, random.nextDouble(), random.nextDouble()));
            numericPredictors.add(getNumericPredictor("NumPred-" + i, random.nextInt(), random.nextDouble()));
            predictorTerms.add(getPredictorTerm("PredTerm-" + i, random.nextDouble(), Arrays.asList("CatPred-" + i, "NumPred-" + i)));
        });
        regressionTable = getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, intercept, targetCategory);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {3.5, "professional"},
                {27.4, "clerical"}
        });
    }

    @Test
    public void getRegressionTablesTest() {
        List<RegressionTable> regressionTables = Collections.singletonList(regressionTable);
        final List<KiePMMLRegressionTable> retrieved = KiePMMLRegressionTableFactory.getRegressionTables(regressionTables);
        retrieved.forEach(this::commonValidateKiePMMLRegressionTable);
    }

    @Test
    public void getRegressionTableTest() {
        KiePMMLRegressionTable retrieved = KiePMMLRegressionTableFactory.getRegressionTable(regressionTable);
        commonValidateKiePMMLRegressionTable(retrieved);
    }

    private void commonValidateKiePMMLRegressionTable(KiePMMLRegressionTable retrieved) {
        assertEquals(intercept, retrieved.getIntercept());
        assertTrue(retrieved.getTargetCategory().isPresent());
        assertEquals(targetCategory, retrieved.getTargetCategory().get());
        // Verify CategoricalPredictors
        assertTrue(retrieved.getCategoricalPredictors().isPresent());
        assertEquals(categoricalPredictors.size(), retrieved.getCategoricalPredictors().get().size());
        retrieved.getCategoricalPredictors().get().forEach(predictor -> {
            Optional<CategoricalPredictor> match = categoricalPredictors.stream()
                    .filter(catPred -> Objects.equals(catPred.getName().getValue(), predictor.getName()))
                    .findFirst();
            assertTrue(match.isPresent());
            assertEquals(match.get().getValue(), predictor.getValue());
            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
        });
        // Verify NumericPredictors
        assertTrue(retrieved.getNumericPredictors().isPresent());
        assertEquals(numericPredictors.size(), retrieved.getNumericPredictors().get().size());
        retrieved.getNumericPredictors().get().forEach(predictor -> {
            Optional<NumericPredictor> match = numericPredictors.stream()
                    .filter(numPred -> Objects.equals(numPred.getName().getValue(), predictor.getName()))
                    .findFirst();
            assertTrue(match.isPresent());
            assertEquals((Integer) match.get().getExponent(), (Integer) predictor.getExponent());
            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
        });
        // Verify PredictorTerms
        assertTrue(retrieved.getPredictorTerms().isPresent());
        assertEquals(predictorTerms.size(), retrieved.getPredictorTerms().get().size());
        retrieved.getPredictorTerms().get().forEach(predictor -> {
            Optional<PredictorTerm> match = predictorTerms.stream()
                    .filter(predTerm -> Objects.equals(predTerm.getName().getValue(), predictor.getName()))
                    .findFirst();
            assertTrue(match.isPresent());
            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
        });
    }
}