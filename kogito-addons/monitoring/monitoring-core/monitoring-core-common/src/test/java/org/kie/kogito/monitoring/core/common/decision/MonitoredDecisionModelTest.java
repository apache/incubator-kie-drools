/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.monitoring.core.decision;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.monitoring.core.common.decision.MonitoredDecisionModel;
import org.kie.kogito.monitoring.core.common.system.metrics.DMNResultMetricsBuilder;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitoredDecisionModelTest {

    private static final String TEST_MODEL_NAME = "TestModel";
    private static final String TEST_SERVICE_NAME = "TestService";

    @Test
    void test() {
        try (MockedStatic<DMNResultMetricsBuilder> mockedMetricsBuilder = mockStatic(DMNResultMetricsBuilder.class)) {
            DMNModel mockedDMNModel = mock(DMNModel.class);
            when(mockedDMNModel.getName()).thenReturn(TEST_MODEL_NAME);

            DMNResult mockedEvaluateAllResult = mock(DMNResult.class);
            DMNResult mockedEvaluateDecisionServiceResult = mock(DMNResult.class);

            DecisionModel mockedDecisionModel = mock(DecisionModel.class);
            resetMockedDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);

            MonitoredDecisionModel testObject = new MonitoredDecisionModel(mockedDecisionModel);

            // test MonitoredDecisionModel#newContext with Map input
            Map<String, Object> inputSet1 = new HashMap<>();
            testObject.newContext(inputSet1);
            verify(mockedDecisionModel).newContext(refEq(inputSet1));

            resetMockedDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);

            // test MonitoredDecisionModel#newContext with FEELPropertyAccessible input
            FEELPropertyAccessible inputSet2 = mock(FEELPropertyAccessible.class);
            testObject.newContext(inputSet2);
            verify(mockedDecisionModel).newContext(refEq(inputSet2));

            resetMockedDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);

            // test MonitoredDecisionModel#getDMNModel
            DMNModel outputModel = testObject.getDMNModel();
            verify(mockedDecisionModel).getDMNModel();
            assertSame(mockedDMNModel, outputModel);

            resetMockedDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);

            // test MonitoredDecisionModel#evaluateAll
            DMNContext ctx1 = mock(DMNContext.class);
            DMNResult res1 = testObject.evaluateAll(ctx1);
            verify(mockedDecisionModel).evaluateAll(refEq(ctx1));
            assertSame(mockedEvaluateAllResult, res1);
            mockedMetricsBuilder.verify(times(1), () -> DMNResultMetricsBuilder.generateMetrics(refEq(mockedEvaluateAllResult), eq(TEST_MODEL_NAME)));

            resetMockedDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);
            mockedMetricsBuilder.reset();

            // test MonitoredDecisionModel#evaluateDecisionService
            DMNContext ctx2 = mock(DMNContext.class);
            DMNResult res2 = testObject.evaluateDecisionService(ctx2, TEST_SERVICE_NAME);
            verify(mockedDecisionModel).evaluateDecisionService(refEq(ctx2), eq(TEST_SERVICE_NAME));
            assertSame(mockedEvaluateDecisionServiceResult, res2);
            mockedMetricsBuilder.verify(times(1), () -> DMNResultMetricsBuilder.generateMetrics(refEq(mockedEvaluateDecisionServiceResult), eq(TEST_MODEL_NAME)));
        }
    }

    private static void resetMockedDecisionModel(DecisionModel mockedDecisionModel, DMNModel mockedDMNModel, DMNResult mockedEvaluateAllResult, DMNResult mockedEvaluateDecisionServiceResult) {
        reset(mockedDecisionModel);
        when(mockedDecisionModel.getDMNModel()).thenReturn(mockedDMNModel);
        when(mockedDecisionModel.evaluateAll(any())).thenReturn(mockedEvaluateAllResult);
        when(mockedDecisionModel.evaluateDecisionService(any(), eq(TEST_SERVICE_NAME))).thenReturn(mockedEvaluateDecisionServiceResult);
    }

}
