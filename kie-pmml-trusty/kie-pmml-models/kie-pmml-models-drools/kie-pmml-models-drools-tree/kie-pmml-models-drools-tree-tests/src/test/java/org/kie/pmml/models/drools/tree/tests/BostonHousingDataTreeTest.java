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
package org.kie.pmml.models.drools.tree.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class BostonHousingDataTreeTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "BostonHousingTree";
    private static final String MODEL_NAME = "BostonHousingTreeModel";
    private static final String TARGET_FIELD = "Predicted_medv";
    private static PMMLRuntime pmmlRuntime;

    private double crim;
    private double zn;
    private double indus;
    private String chas;
    private double nox;
    private double rm;
    private double age;
    private double dis;
    private double rad;
    private double tax;
    private double ptratio;
    private double b;
    private double lstat;
    private double expectedResult;

    public void initBostonHousingDataTreeTest(double crim, double zn, double indus, String chas, double nox, double rm,
                                     double age, double dis, double rad, double tax, double ptratio, double b,
                                     double lstat, double expectedResult) {
        this.crim = crim;
        this.zn = zn;
        this.indus = indus;
        this.chas = chas;
        this.nox = nox;
        this.rm = rm;
        this.age = age;
        this.dis = dis;
        this.rad = rad;
        this.tax = tax;
        this.ptratio = ptratio;
        this.b = b;
        this.lstat = lstat;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.00632, 18, 2.31, "0", 0.538, 6.575, 65.2, 4.0900, 1, 296, 15.3, 396.90, 4.98, 27.4272727272727},
                {0.02729, 0, 7.07, "0", 0.469, 7.185, 61.1, 4.9671, 2, 242, 17.8, 392.83, 4.03, 33.7384615384615},
                {0.06905, 0, 2.18, "0", 0.458, 7.147, 54.2, 6.0622, 3, 222, 18.7, 396.90, 5.33, 33.7384615384615},
                {0.02985, 0, 2.18, "0", 0.458, 6.430, 58.7, 6.0622, 3, 222, 18.7, 394.12, 5.21, 21.6564766839378},
                {0.78420, 0.0, 8.14, "0", 0.538, 5.990, 81.7, 4.2579, 4, 307, 21.0, 386.75, 14.67, 17.1376237623762},
                {3.53501, 0, 19.58, "1", 0.871, 6.152, 82.6, 1.7455, 5, 403, 14.7, 88.01, 15.02, 17.1376237623762},
                {8.26725, 0, 18.1, "1", 0.668, 5.875, 89.6, 1.1296, 24, 666, 20.2, 347.88, 8.88, 38.0},
                {3.47428, 0, 18.1, "1", 0.718, 8.780, 82.9, 1.9047, 24, 666, 20.2, 354.55, 5.29, 45.0966666666667},
                {5.20177, 0, 18.1, "1", 0.770, 6.127, 83.4, 2.7227, 24, 666, 20.2, 395.43, 11.48, 21.6564766839378},
                {4.22239, 0, 18.1, "1", 0.770, 5.803, 89.0, 1.9047, 24, 666, 20.2, 353.04, 14.64, 17.1376237623762}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testBostonHousesTree(double crim, double zn, double indus, String chas, double nox, double rm, double age, double dis, double rad, double tax, double ptratio, double b, double lstat, double expectedResult) {
        initBostonHousingDataTreeTest(crim, zn, indus, chas, nox, rm, age, dis, rad, tax, ptratio, b, lstat, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("crim", crim);
        inputData.put("zn", zn);
        inputData.put("indus", indus);
        inputData.put("chas", chas);
        inputData.put("nox", nox);
        inputData.put("rm", rm);
        inputData.put("age", age);
        inputData.put("dis", dis);
        inputData.put("rad", rad);
        inputData.put("tax", tax);
        inputData.put("ptratio", ptratio);
        inputData.put("b", b);
        inputData.put("lstat", lstat);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
