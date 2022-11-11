/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.monitoring.core.common.decision;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMetadata;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.monitoring.core.common.system.metrics.DMNResultMetricsBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.monitoring.core.common.Constants.SKIP_MONITORING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MonitoredDecisionModelTest {

    private static final String TEST_MODEL_NAME = "TestModel";
    private static final String TEST_SERVICE_NAME = "TestService";

    private DMNModel mockedDMNModel;
    private DMNResult mockedEvaluateAllResult;
    private DMNResult mockedEvaluateDecisionServiceResult;
    private DecisionModel mockedDecisionModel;
    private MonitoredDecisionModel testObject;
    private MeterRegistry meterRegistry;
    private DMNResultMetricsBuilder dmnResultMetricsBuilder;

    @BeforeEach
    void setup() {
        mockedDMNModel = mock(DMNModel.class);
        when(mockedDMNModel.getName()).thenReturn(TEST_MODEL_NAME);

        mockedEvaluateAllResult = mock(DMNResult.class);
        mockedEvaluateDecisionServiceResult = mock(DMNResult.class);

        mockedDecisionModel = mock(DecisionModel.class);
        mockDecisionModel(mockedDecisionModel, mockedDMNModel, mockedEvaluateAllResult, mockedEvaluateDecisionServiceResult);

        meterRegistry = mock(SimpleMeterRegistry.class);
        dmnResultMetricsBuilder = mock(DMNResultMetricsBuilder.class);
        testObject = new MonitoredDecisionModel(mockedDecisionModel, dmnResultMetricsBuilder);
    }

    @Test
    void testMonitoredDecisionModelNewContext() {
        Map<String, Object> inputSet1 = new HashMap<>();
        testObject.newContext(inputSet1);
        verify(mockedDecisionModel).newContext(refEq(inputSet1));
    }

    @Test
    void testMonitoredDecisionModelNewContextWithFEELPropertyAccessibleInput() {
        FEELPropertyAccessible inputSet2 = mock(FEELPropertyAccessible.class);
        testObject.newContext(inputSet2);
        verify(mockedDecisionModel).newContext(refEq(inputSet2));
    }

    @Test
    void testMonitoredDecisionModelGetDMNModel() {
        DMNModel outputModel = testObject.getDMNModel();
        verify(mockedDecisionModel).getDMNModel();
        assertThat(outputModel).isSameAs(mockedDMNModel);
    }

    @Test
    void testMonitoredDecisionModelEvaluateAll() {
        DMNContext ctx = mock(DMNContext.class);
        DMNResult res = testObject.evaluateAll(ctx);
        verify(mockedDecisionModel).evaluateAll(refEq(ctx));
        assertThat(res).isSameAs(mockedEvaluateAllResult);
        verify(dmnResultMetricsBuilder, times(1)).generateMetrics(refEq(mockedEvaluateAllResult), eq(TEST_MODEL_NAME));
    }

    @Test
    void testMonitoredDecisionModelEvaluateDecisionService() {
        DMNContext ctx = mock(DMNContext.class);
        DMNResult res = testObject.evaluateDecisionService(ctx, TEST_SERVICE_NAME);
        verify(mockedDecisionModel).evaluateDecisionService(refEq(ctx), eq(TEST_SERVICE_NAME));
        assertThat(res).isSameAs(mockedEvaluateDecisionServiceResult);
        verify(dmnResultMetricsBuilder, times(1)).generateMetrics(refEq(mockedEvaluateDecisionServiceResult), eq(TEST_MODEL_NAME));
    }

    @Test
    void testMonitoredDecisionModelWithSkipMonitoringMetadata() {
        DMNContext ctx = mock(DMNContext.class);
        when(ctx.getMetadata()).thenReturn(new DMNMetadata() {
            @Override
            public Object set(String s, Object o) {
                return null;
            }

            @Override
            public Object get(String s) {
                return null;
            }

            @Override
            public Map<String, Object> asMap() {
                Map<String, Object> map = new HashMap();
                map.put(SKIP_MONITORING, true);
                return map;
            }
        });

        DMNResult res = testObject.evaluateDecisionService(ctx, TEST_SERVICE_NAME);
        verify(mockedDecisionModel).evaluateDecisionService(refEq(ctx), eq(TEST_SERVICE_NAME));
        assertThat(res).isSameAs(mockedEvaluateDecisionServiceResult);
        verify(dmnResultMetricsBuilder, times(0)).generateMetrics(refEq(mockedEvaluateDecisionServiceResult), eq(TEST_MODEL_NAME));

        res = testObject.evaluateAll(ctx);
        verify(mockedDecisionModel).evaluateAll(refEq(ctx));
        assertThat(res).isSameAs(mockedEvaluateAllResult);
        verify(dmnResultMetricsBuilder, times(0)).generateMetrics(refEq(mockedEvaluateDecisionServiceResult), eq(TEST_MODEL_NAME));
    }

    private static void mockDecisionModel(DecisionModel mockedDecisionModel, DMNModel mockedDMNModel, DMNResult mockedEvaluateAllResult, DMNResult mockedEvaluateDecisionServiceResult) {
        reset(mockedDecisionModel);
        when(mockedDecisionModel.getDMNModel()).thenReturn(mockedDMNModel);
        when(mockedDecisionModel.evaluateAll(any())).thenReturn(mockedEvaluateAllResult);
        when(mockedDecisionModel.evaluateDecisionService(any(), eq(TEST_SERVICE_NAME))).thenReturn(mockedEvaluateDecisionServiceResult);
    }
}
