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
package org.kie.dmn.core.classloader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.impl.DMNEventUtils;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNRuntimeListenerBKMTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeListenerBKMTest.class);

    public static class BKMListener extends DefaultDMNRuntimeEventListener {

        private List<Map<String, Object>> invParams = new ArrayList<>();
        private List<Object> invResults = new ArrayList<>();
        @Override
        public void beforeInvokeBKM(BeforeInvokeBKMEvent event) {
            Map<String, Object> pMap = DMNEventUtils.extractBKMParameters(event);
            invParams.add(pMap);
        }

        @Override
        public void afterInvokeBKM(AfterInvokeBKMEvent event) {
            invResults.add(event.getInvocationResult());
        }

        public List<Map<String, Object>> getInvParams() {
            return invParams;
        }

        public List<Object> getInvResults() {
            return invResults;
        }

    }

    @Test
    void test() {
        final BKMListener listenerUT = new BKMListener();

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("sumWithBKM.dmn", this.getClass());
        runtime.addListener(listenerUT);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_FD426696-6811-494E-9938-10EE9C58DDEA", "sumWithBKM");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a", 1);
        context.set("b", 2);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Decision-1").getResult()).isEqualTo(new BigDecimal(3));

        Map<String, Object> expectedParameters = new LinkedHashMap<String, Object>();
        expectedParameters.put("p1", new BigDecimal(1));
        expectedParameters.put("p2", new BigDecimal(2));
        assertThat(listenerUT.getInvParams()).hasSize(1).contains(expectedParameters);
        assertThat(listenerUT.getInvResults()).hasSize(1).contains(new BigDecimal(3));
    }
}