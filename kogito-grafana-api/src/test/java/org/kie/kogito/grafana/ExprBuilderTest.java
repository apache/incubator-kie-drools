/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.grafana;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.kie.kogito.grafana.model.functions.ExprBuilder;
import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.functions.IncreaseFunction;
import org.kie.kogito.grafana.model.functions.SumFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprBuilderTest {

    @Test
    public void GivenATarget_WhenNullGrafanaFunctionsAreApplied_ThenTheOriginalTargetIsReturned() {
        // Arrange
        String target = "api_test{hanlder=\"hello\"}";

        // Act
        String result = ExprBuilder.apply(target, null);

        // Assert
        assertEquals(target, result);
    }

    @Test
    public void GivenATarget_WhenNoGrafanaFunctionsAreApplied_ThenTheOriginalTargetIsReturned() {
        // Arrange
        String target = "api_test{hanlder=\"hello\"}";

        // Act
        String result = ExprBuilder.apply(target, new TreeMap<>());

        // Assert
        assertEquals(target, result);
    }

    @Test
    public void GivenATarget_WhenGrafanaFunctionsAreApplied_ThenTheOriginalTargetIsReturned() {
        // Arrange
        String target = "api_test{hanlder=\"hello\"}";
        SortedMap<Integer, GrafanaFunction> map = new TreeMap<>();
        map.put(1, new SumFunction());
        map.put(2, new IncreaseFunction("10m"));
        String expectedResult = "increase(sum(api_test{hanlder=\"hello\"})[10m])";

        // Act
        String result = ExprBuilder.apply(target, map);

        // Assert
        assertEquals(expectedResult, result);
    }
}
