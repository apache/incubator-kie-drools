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
    public void getNONEProbabilityMap() {
         /*
        pj = yj for j = 1 to N - 1,
        pN = 1 - Sum[ i = 1 to N - 1 ]( pi )
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        resultMap.put("y_trainee", 0.11);
        final double sum = 0.34 + 0.26;
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getNONEProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        String[] retrievedKeys = retrieved.keySet().toArray(new String[0]);
        for (int i = 0; i < retrieved.size(); i++) {
            double expected = (i == retrieved.size() - 1) ? 1 - sum : resultMap.get(retrievedKeys[i]);
            assertEquals(expected, retrieved.get(retrievedKeys[i]), 0.0);
        }
    }

    @Test(expected = KiePMMLModelException.class)
    public void getLOGITProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getLOGITProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getLOGITProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getLOGITProbabilityMap(getResultMap(3));
    }

    @Test
    public void getLOGITProbabilityMap() {
        /*
        p1 = 1 / ( 1 + exp( -y1 ) ),
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("y_clerical", 0.34);
        resultMap.put("y_professional", 0.26);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getLOGITProbabilityMap(resultMap);
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

    @Test(expected = KiePMMLModelException.class)
    public void getPROBITProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getPROBITProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getPROBITProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getPROBITProbabilityMap(getResultMap(3));
    }

    @Test
    public void getPROBITProbabilityMap() {
        /*
        p1 = integral(from -∞ to y1)(1/sqrt(2*π))exp(-0.5*u*u)du,
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("clerical", 3.5503450000000036);
        resultMap.put("professional", 3.725499999999993);
        final LinkedHashMap<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("clerical", 0.9998076366940838);
        expectedMap.put("professional", 1.9236330591620998E-4);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getPROBITProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        retrieved.forEach((s, aDouble) -> assertEquals(expectedMap.get(s), aDouble));
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCLOGLOGProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getCLOGLOGProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCLOGLOGProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getCLOGLOGProbabilityMap(getResultMap(3));
    }

    @Test
    public void getCLOGLOGProbabilityMap() {
        /*
        p1 = 1 - exp( -exp( y1 ) ),
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("clerical", 3.5503450000000036);
        resultMap.put("professional", 3.725499999999993);
        final LinkedHashMap<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("clerical", 0.9999999999999992);
        expectedMap.put("professional", 7.771561172376096E-16);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getCLOGLOGProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        retrieved.forEach((s, aDouble) -> assertEquals(expectedMap.get(s), aDouble));
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCAUCHITProbabilityMapSmallerSize() {
        PMMLClassificationModelEvaluator.getCAUCHITProbabilityMap(new LinkedHashMap<>());
    }

    @Test(expected = KiePMMLModelException.class)
    public void getCAUCHITProbabilityMapBiggerSize() {
        PMMLClassificationModelEvaluator.getCAUCHITProbabilityMap(getResultMap(3));
    }

    @Test
    public void getCAUCHITProbabilityMap() {
        /*
        p1 = 0.5 + (1/π) arctan( y1 ),
        p2 = 1 - p1
         */
        final LinkedHashMap<String, Double> resultMap = new LinkedHashMap<>();
        resultMap.put("clerical", 3.5503450000000036);
        resultMap.put("professional", 3.725499999999993);
        final LinkedHashMap<String, Double> expectedMap = new LinkedHashMap<>();
        expectedMap.put("clerical", 0.9126080459780144);
        expectedMap.put("professional", 0.08739195402198563);
        final LinkedHashMap<String, Double> retrieved = PMMLClassificationModelEvaluator.getCAUCHITProbabilityMap(resultMap);
        commonVerifyMap(resultMap, retrieved);
        retrieved.forEach((s, aDouble) -> assertEquals(expectedMap.get(s), aDouble));
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