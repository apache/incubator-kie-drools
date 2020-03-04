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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;

//@RunWith(Parameterized.class)
public class KiePMMLRegressionTableClassificationFactoryTest {

    //    private double intercept;
//    private Object targetCategory;
//    private RegressionTable regressionTable;
    private List<CategoricalPredictor> categoricalPredictors;
    private List<NumericPredictor> numericPredictors;
    private List<PredictorTerm> predictorTerms;

//    public KiePMMLRegressionTableFactoryTest(double intercept, Object targetCategory) {
//        this.intercept = intercept;
//        this.targetCategory = targetCategory;
//        categoricalPredictors = new ArrayList<>();
//        numericPredictors = new ArrayList<>();
//        predictorTerms = new ArrayList<>();
//        numericPredictors.add(getNumericPredictor("NumPred-" + 3, 1, 32.55));
//        IntStream.range(0, 3).forEach(i -> {
//            IntStream.range(0, 2).forEach(j -> categoricalPredictors.add(getCategoricalPredictor("CatPred-" + i, 27.12, 3.46)));
//            numericPredictors.add(getNumericPredictor("NumPred-" + i, 2, 13.11));
//            predictorTerms.add(getPredictorTerm("PredTerm-" + i, 32.29,
//                                                Arrays.asList(categoricalPredictors.get(0).getName().getValue(),
//                                                              numericPredictors.get(0).getName().getValue())));
//        });
//        regressionTable = getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, intercept, targetCategory);
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() {
//        return Arrays.asList(new Object[][]{
//                {3.5, "professional"},
//                {27.4, "clerical"}
//        });
//    }

    @Test
    public void getRegressionTableTest() throws Exception {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        List<RegressionTable> regressionTables = Arrays.asList(regressionTableProf, regressionTableCler);
        KiePMMLOutputField outputFieldCat = getOutputField("CAT-1", RESULT_FEATURE.PROBABILITY, "CatPred-1");
        KiePMMLOutputField outputFieldNum = getOutputField("NUM-1", RESULT_FEATURE.PROBABILITY, "NumPred-0");
        KiePMMLOutputField outputFieldPrev = getOutputField("PREV", RESULT_FEATURE.PREDICTED_VALUE, null);
        List<KiePMMLOutputField> outputFields = Arrays.asList(outputFieldCat, outputFieldNum, outputFieldPrev);
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionTableClassificationFactory.getRegressionTables(regressionTables, REGRESSION_NORMALIZATION_METHOD.SOFTMAX, outputFields, "targetField");
        assertNotNull(retrieved);
    }

    private RegressionTable getRegressionTable(double intercept, Object targetCategory) {
        categoricalPredictors = new ArrayList<>();
        numericPredictors = new ArrayList<>();
        predictorTerms = new ArrayList<>();
        numericPredictors.add(getNumericPredictor("NumPred-" + 3, 1, 32.55));
        IntStream.range(0, 3).forEach(i -> {
            IntStream.range(0, 2).forEach(j -> categoricalPredictors.add(getCategoricalPredictor("CatPred-" + i, 27.12, 3.46)));
            numericPredictors.add(getNumericPredictor("NumPred-" + i, 2, 13.11));
            predictorTerms.add(getPredictorTerm("PredTerm-" + i, 32.29,
                                                Arrays.asList(categoricalPredictors.get(0).getName().getValue(),
                                                              numericPredictors.get(0).getName().getValue())));
        });
        return PMMLModelTestUtils.getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, intercept, targetCategory);
    }

    private KiePMMLOutputField getOutputField(String name, RESULT_FEATURE resultFeature, String targetField) {
        return KiePMMLOutputField.builder(name, Collections.emptyList())
                .withResultFeature(resultFeature)
                .withTargetField(targetField)
                .build();
    }
}