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
package org.kie.pmml.mining.tests;

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

public class MiningWithNestedRefersTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MiningWithNestedRefers";
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

    public void initMiningWithNestedRefersTest(float sLen,
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

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
//                {6.9f, 3.1f, 5.1f, 2.3f, 0.04871813890555572, 0.04509596950852268, 0.9061858915859216, "virginica"},
//                {5.8f, 2.6f, 4.0f, 1.2f, 0.16500426591949635, 0.5910742531758129, 0.2439214809046908, "versicolor"},
//                {5.7f, 3.0f, 4.2f, 1.2f, 0.21060905537789087, 0.45897688276004667, 0.33041406186206246, "versicolor"},
//                {5.0f, 3.3f, 1.4f, 0.2f, 0.9237551991667617, 0.21583248228936047, -0.13958768145612233, "setosa"},
                {5.4f, 3.9f, 1.3f, 0.4f, 1.1068470421580194, -0.1805270582839955, 0.07368001612597608, "setosa"}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMiningWithNestedRefers(float sLen, float sWid, float pLen, float pWid, double pSetosa, double pVersicolor, double pVirginica, String expectedResult) throws Exception {
        initMiningWithNestedRefersTest(sLen, sWid, pLen, pWid, pSetosa, pVersicolor, pVirginica, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(S_LEN, sLen);
        inputData.put(S_WID, sWid);
        inputData.put(P_LEN, pLen);
        inputData.put(P_WID, pWid);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_SETOSA)).isNotNull();
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_SETOSA)).isEqualTo(pSetosa);
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VERSICOLOR)).isNotNull();
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VERSICOLOR)).isEqualTo(pVersicolor);
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VIRGINICA)).isNotNull();
//        assertThat(pmml4Result.getResultVariables().get(PROBABILITY_IRIS_VIRGINICA)).isEqualTo(pVirginica);
    }
}
