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

package org.kie.pmml.models.drools.tree.tests;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class PlanActivityTreeTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "PlanActivityTree.pmml";
    private static final String MODEL_NAME = "PlanActivityTreeModel";
    private static final String TARGET_FIELD = "Predicted_activity";
    private static PMMLRuntime pmmlRuntime;

    private String workToDo;
    private String weather;
    private boolean friendsAvailable;
    private String activity;

    public PlanActivityTreeTest(String workToDo, String weather, boolean friendsAvailable, String activity) {
        this.workToDo = workToDo;
        this.weather = weather;
        this.friendsAvailable = friendsAvailable;
        this.activity = activity;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"YES", "sunny", false, "stay in"},
                {"YES", "rainy", true, "stay in"},
                {"NO", "sunny", true, "go to beach"},
                {"NO", "overcast", false, "go running"},
                {"NO", "rainy", true, "stay in"},
                {"NO", "rainy", false, "go to movie"}
        });
    }

    @Test
    public void testPlanActivity() {
        final Map<String, Object> inputData = new HashMap<>();

        inputData.put("workToDo", this.workToDo);
        inputData.put("weather", this.weather);
        inputData.put("friendsAvailable", this.friendsAvailable);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Object result = pmml4Result.getResultVariables().get(TARGET_FIELD);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(activity);
    }
}
