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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class SegmentationMaxMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "segmentationMaxMining.pmml";
    private static final String MODEL_NAME = "SegmentationMaxMining";
    private static final String TARGET_FIELD = "result";
    private PMMLRuntime pmmlRuntime;

    private double x;
    private double y;
    private double result;

    public SegmentationMaxMiningTest(double x, double y, double result) {
        this.x = x;
        this.y = y;
        this.result = result;
    }

    @Before
    public void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, 0, 50},
                {1, 1, 55},
                {20, 30, 121004},
                {25, 31, 167502},
                {5, 5, 1004}
        });
    }

    @Test
    public void testSegmentationMedianMiningTest() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("x", x);
        inputData.put("y", y);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(result);
    }
}
