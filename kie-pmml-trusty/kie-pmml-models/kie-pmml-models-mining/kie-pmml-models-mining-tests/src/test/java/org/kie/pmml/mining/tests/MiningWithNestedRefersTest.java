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

package org.kie.pmml.mining.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class MiningWithNestedRefersTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "MiningWithNestedRefers.pmml";
    private static final String MODEL_NAME = "MiningWithNestedRefers";
    private static final String TARGET_FIELD = "class";
    private static final String S_LEN = "s_len";
    private static final String S_WID = "s_wid";
    private static final String P_LEN = "p_len";
    private static final String P_WID = "p_wid";
    private static final String PROBABILITY_IRIS_SETOSA = "probability(Iris-setosa)";
    private static final String PROBABILITY_IRIS_VERSICOLOR = "probability(Iris-versicolor)";
    private static final String PROBABILITY_IRIS_VIRGINICA = "probability(Iris-virginica)";

    private static PMMLRuntime pmmlRuntime;

    private float sLen;
    private float sWid;
    private float pLen;
    private float pWid;
    private String expectedResult;
    private double pSetosa;
    private double pVersicolor;
    private double pVirginica;

    public MiningWithNestedRefersTest(float sLen,
                                      float sWid,
                                      float pLen,
                                      float pWid,
                                      double pSetosa,
                                      double pVersicolor,
                                      double pVirginica,
                                      String expectedResult) {
        this.sLen = sLen;
        this.sWid = sWid;
        this.pLen = pLen;
        this.pWid = pWid;
        this.pSetosa = pSetosa;
        this.pVersicolor = pVersicolor;
        this.pVirginica = pVirginica;
        this.expectedResult = expectedResult;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9, 3.1, 5.1, 2.3, "virginica", 0.0, 0.023333333333333334, 0.9766666666666667},
//                {5.8, 2.6, 4.0, 1.2, "versicolor", 0.0, 0.9966666666666667, 0.0033333333333333335},
//                {5.7, 3.0, 4.2, 1.2, "versicolor", 0.0, 1.0, 0.0},
//                {5.0, 3.3, 1.4, 0.2, "setosa", 1.0, 0.0, 0.0},
//                {5.4, 3.9, 1.3, 0.4, "setosa", 1.0, 0.0, 0.0}
        });
    }

    @Ignore
    @Test
    public void testMiningWithNestedRefers() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(S_LEN, sLen);
        inputData.put(S_WID, sWid);
        inputData.put(P_LEN, pLen);
        inputData.put(P_WID, pWid);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_SETOSA)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_SETOSA)).isEqualTo(pSetosa);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VERSICOLOR)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VERSICOLOR)).isEqualTo(pVersicolor);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VIRGINICA)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VIRGINICA)).isEqualTo(pVirginica);
    }
}
