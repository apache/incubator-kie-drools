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

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.validator.Java8Validator;
import com.github.javaparser.ast.validator.ProblemReporter;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Test;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;

//@RunWith(Parameterized.class)
public class KiePMMLRegressionTableRegressionFactoryTest {

    //    private double intercept;
//    private Object targetCategory;
    private RegressionTable regressionTable;
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
        regressionTable = getRegressionTable(3.5, "professional");
        List<RegressionTable> regressionTables = Collections.singletonList(regressionTable);
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, "targetField");
        assertNotNull(retrieved);
//        AbstractKiePMMLRegressionTable abstractKiePMMLRegressionTable =retrieved.values().iterator().next().newInstance();
//        System.out.printf(abstractKiePMMLRegressionTable.toString());
        //        retrieved.forEach(retr -> commonValidateKiePMMLRegressionTable(retr, "KiePMMLRegressionTable1.java"));
//        CompilationUnit templateCU = StaticJavaParser.parseResource(KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA);
//        String retrievedString = KiePMMLRegressionTableRegressionFactory.getRegressionTable(templateCU, regressionTable, "targetField");
//        commonValidateKiePMMLRegressionTable(retrievedString, "KiePMMLRegressionTable2.java");
//        regressionTable = getRegressionTable(27.4, "clerical");
//        regressionTables = Collections.singletonList(regressionTable);
//        retrieved = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, "targetField");
//        retrieved.forEach(retr -> commonValidateKiePMMLRegressionTable(retr, "KiePMMLRegressionTable3.java"));
//        retrievedString = KiePMMLRegressionTableRegressionFactory.getRegressionTable(templateCU, regressionTable, "targetField");
//        commonValidateKiePMMLRegressionTable(retrievedString, "KiePMMLRegressionTable4.java");
    }

//    @Test
//    public void getRegressionTableTest() {
//        AbstractKiePMMLRegressionTable retrieved = KiePMMLRegressionTableFactory.getRegressionTable(regressionTable, "targetField");
////        commonValidateKiePMMLRegressionTable(retrieved);
//    }

    private void commonValidateKiePMMLRegressionTable(String retrieved, String reference) {
        try {
            String comparison = new String(Files.readAllBytes(FileUtils.getFile(reference).toPath()));
            assertEquals(comparison, retrieved);
            final CompilationUnit parsed = StaticJavaParser.parse(retrieved);
            final Java8Validator validator = new Java8Validator();
            final ProblemReporter problemReporter = new ProblemReporter(problem -> fail(problem.getMessage()));
            validator.accept(parsed.findRootNode(), problemReporter);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to match with " + reference + " due to " + e.getMessage());
        }
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

//    private void commonValidateKiePMMLRegressionTable(KiePMMLRegressionTable retrieved) {
//        assertEquals(intercept, retrieved.getIntercept());
//        assertTrue(retrieved.getTargetCategory().isPresent());
//        assertEquals(targetCategory, retrieved.getTargetCategory().get());
//        // Verify CategoricalPredictors
//        assertTrue(retrieved.getCategoricalPredictors().isPresent());
//        assertEquals(categoricalPredictors.size(), retrieved.getCategoricalPredictors().get().size());
//        retrieved.getCategoricalPredictors().get().forEach(predictor -> {
//            Optional<CategoricalPredictor> match = categoricalPredictors.stream()
//                    .filter(catPred -> Objects.equals(catPred.getName().getValue(), predictor.getName()))
//                    .findFirst();
//            assertTrue(match.isPresent());
//            assertEquals(match.get().getValue(), predictor.getValue());
//            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
//        });
//        // Verify NumericPredictors
//        assertTrue(retrieved.getNumericPredictors().isPresent());
//        assertEquals(numericPredictors.size(), retrieved.getNumericPredictors().get().size());
//        retrieved.getNumericPredictors().get().forEach(predictor -> {
//            Optional<NumericPredictor> match = numericPredictors.stream()
//                    .filter(numPred -> Objects.equals(numPred.getName().getValue(), predictor.getName()))
//                    .findFirst();
//            assertTrue(match.isPresent());
//            assertEquals((Integer) match.get().getExponent(), (Integer) predictor.getExponent());
//            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
//        });
//        // Verify PredictorTerms
//        assertTrue(retrieved.getPredictorTerms().isPresent());
//        assertEquals(predictorTerms.size(), retrieved.getPredictorTerms().get().size());
//        retrieved.getPredictorTerms().get().forEach(predictor -> {
//            Optional<PredictorTerm> match = predictorTerms.stream()
//                    .filter(predTerm -> Objects.equals(predTerm.getName().getValue(), predictor.getName()))
//                    .findFirst();
//            assertTrue(match.isPresent());
//            assertEquals(match.get().getCoefficient(), predictor.getCoefficient());
//        });
//    }
}