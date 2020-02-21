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

package org.kie.pmml.models.regression.evaluator;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;

import static org.junit.Assert.assertEquals;

public class PMMLClassificationModelEvaluatorTest {

    @Test
    public void evaluateClassification() {
    }

    @Test
    public void testEvaluateClassification() {
    }

    @Test
    public void getProbabilityMap() {
    }

    @Test
    public void getSOFTMAXProbabilityMap() {
        /*
        pj = exp(yj) / ( Sum[i = 1 to N](exp(yi) ) )
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final double sum = Math.exp(0.34) + Math.exp(0.26) + Math.exp(0.11);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getSOFTMAXProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        retrieved.forEach((s, aDouble) -> {
            double originalValue = resultMap.get(s);
            double expected = Math.exp(originalValue) / sum;
            assertEquals(expected, aDouble, 0.0);
        });
    }

    @Test
    public void getSIMPLEMAXProbabilityMap() {
         /*
        pj = yj / ( Sum[i = 1 to N ]( yi ) )
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final double sum = 0.34 + 0.26 + 0.11;
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getSIMPLEMAXProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        retrieved.forEach((s, aDouble) -> {
            double originalValue = resultMap.get(s);
            double expected = originalValue / sum;
            assertEquals(expected, aDouble, 0.0);
        });
    }

    @Test
    public void getNONECATEGORICALProbabilityMap() {
         /*
        pj = yj for j = 1 to N - 1,
        pN = 1 - Sum[ i = 1 to N - 1 ]( pi )
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final double sum = 0.34 + 0.26;
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getNONECATEGORICALProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double expected = (i == retrieved.size() - 1) ? 1 - sum : resultMap.get(retrievedKeys[i]);
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test
    public void getNONEORDINALProbabilityMap() {
        /*
        p1 = y1
        pj = yj - yj-1, for 2 ≤ j < N
        pN = 1 - yN-1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getNONEORDINALProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double expected;
            if (i == 0) {
                expected = resultMap.get(retrievedKeys[i]);
            } else if (i < retrieved.size() - 1) {
                expected = resultMap.get(retrievedKeys[i]) - resultMap.get(retrievedKeys[i - 1]);
            } else {
                expected = 1 - retrieved.get(retrievedKeys[i - 1]);
            }
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test(expected = KiePMMLModelException.class)
    public void getLOGITCATEGORICALProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getLOGITCATEGORICALProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getLOGITCATEGORICALProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getLOGITCATEGORICALProbabilityMap(getResultMap(3));
    }

    @Test
    public void getLOGITCATEGORICALProbabilityMap() {
        /*
        p1 = 1 / ( 1 + exp( -y1 ) ),
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getLOGITCATEGORICALProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double value = resultMap.get(retrievedKeys[i]);
            double expected;
            if (i == 0) {
                expected = 1 / (1 + Math.exp(-value));
            } else {
                expected = 1 - retrieved.get(retrievedKeys[i - 1]);
            }
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test
    public void getLOGITORDINALProbabilityMap() {
        /*
        inverse of logit function: F(y)= 1/(1+exp(-y)), e.g. F(15) = 1
        p1 = F(y1)
        pj = F(yj) - F(yj-1), for 2 ≤ j < N
        pN = 1 - F(yN-1)
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getLOGITORDINALProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double value = resultMap.get(retrievedKeys[i]);
            double expected;
            if (i == 0) {
                expected = 1 / (1 + Math.exp(-value));
            } else if (i < retrieved.size() - 1) {
                expected = (1 / (1 + Math.exp(-value))) - retrieved.get(retrievedKeys[i - 1]);
            } else {
                expected = 1 - retrieved.get(retrievedKeys[i - 1]);
            }
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test(expected = KiePMMLModelException.class)
    public void getPROBITCATEGORICALProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getPROBITCATEGORICALProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getPROBITCATEGORICALProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getPROBITCATEGORICALProbabilityMap(getResultMap(3));
    }

    @Test
    public void getPROBITCATEGORICALProbabilityMap() {
        /*
        p1 = integral(from -∞ to y1)(1/sqrt(2*π))exp(-0.5*u*u)du,
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getPROBITCATEGORICALProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double value = resultMap.get(retrievedKeys[i]);
            double expected;
            if (i == 0) {
                expected = 1 / (1 + Math.exp(-value));
            } else if (i < retrieved.size() - 1) {
                expected = (1 / (1 + Math.exp(-value))) - retrieved.get(retrievedKeys[i - 1]);
            } else {
                expected = 1 - retrieved.get(retrievedKeys[i - 1]);
            }
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test
    public void getPROBITORDINALProbabilityMap() {
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCLOGLOGCATEGORICALProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getCLOGLOGCATEGORICALProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCLOGLOGCATEGORICALProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getCLOGLOGCATEGORICALProbabilityMap(getResultMap(3));
    }

    @Test
    public void getCLOGLOGCATEGORICALProbabilityMap() {
    }

    @Test
    public void getCLOGLOGORDINALProbabilityMap() {
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCAUCHITCATEGORICALProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getCAUCHITCATEGORICALProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCAUCHITCATEGORICALProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getCAUCHITCATEGORICALProbabilityMap(getResultMap(3));
    }

    @Test
    public void getCAUCHITCATEGORICALProbabilityMap() {
    }

    @Test
    public void getCAUCHITORDINALProbabilityMap() {
    }

    private void commonVerifyMap(LinkedHashMap<String, Double> resultMap, LinkedHashMap<String, Double> retrieved) {
        assertEquals(resultMap.size(), retrieved.size());
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrievedKeys.length; i++) {
            assertEquals(resultMapKeys[i], retrievedKeys[i]);
        }
    }

    private LinkedHashMap<String, Double> getResultMap(int size) {
        return IntStream.range(0, size).boxed().collect(Collectors.toMap(integer -> "Field-" + integer,
                                                                         Integer::doubleValue,
                                                                         (o1, o2) -> o1,
                                                                         LinkedHashMap::new));
    }
}