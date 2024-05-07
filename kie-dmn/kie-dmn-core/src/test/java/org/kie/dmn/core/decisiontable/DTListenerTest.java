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
package org.kie.dmn.core.decisiontable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.BaseInterpretedVsAlphaNetworkTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DTListenerTest extends BaseInterpretedVsAlphaNetworkTest {

    private static final String E1 = "e1";

    public static final Logger LOG = LoggerFactory.getLogger(DTListenerTest.class);

    public static class ExampleDTListener extends DefaultDMNRuntimeEventListener {
        private final List<AfterEvaluateDecisionTableEvent> events = new ArrayList<>();

        @Override
        public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
            events.add(event);
        }

        public List<AfterEvaluateDecisionTableEvent> getEvents() {
            return events;
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void test(boolean useAlphaNetwork) {
        alphaNetwork = useAlphaNetwork;
        final ExampleDTListener listenerUT = new ExampleDTListener();

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("dtevent.dmn", this.getClass());
        runtime.addListener(listenerUT);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_0C86FAC6-5247-45D1-B410-9FCD7A6E07E7", "Untitled");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a", 47);
        context.set("b", 47);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        
        final Object expectedResult = Map.of(E1, Arrays.asList("a gt 0", "b gt 0", "a and b gt 0"), "e2", "r1");
        assertThat(dmnResult.getDecisionResultByName("my decision").getResult()).isEqualTo(expectedResult);
        
        assertThat(listenerUT.getEvents()).hasSize(2)
        .anySatisfy(event1 -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(event1.getDecisionTableName()).isEqualTo(E1);
            softly.assertThat(event1.getDecisionTableId()).isEqualTo("_B861D847-0BE9-4580-8DD0-836B6699963E");
            final DecisionTable locateDT = locateDTbyId(dmnModel, event1.getDecisionTableId());
            softly.assertThat(locateDT.getInput()).hasSize(2);
            softly.assertThat(event1.getMatches()).hasSize(3);
            softly.assertThat(event1.getSelected()).hasSize(3);
            softly.assertAll();
        }).anySatisfy(event2 -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(event2.getDecisionTableName()).isEqualTo(E1); // the event report for a DT having the same name...
            softly.assertThat(event2.getDecisionTableId()).isEqualTo("_24DA98D6-34A6-4267-9667-1DA91C0AF5F9"); // ..but different id...
            final DecisionTable locateDT = locateDTbyId(dmnModel, event2.getDecisionTableId());
            softly.assertThat(locateDT.getInput()).hasSize(1); // ...having the different "shape".
            softly.assertThat(event2.getMatches()).hasSize(1);
            softly.assertThat(event2.getSelected()).hasSize(1);
            softly.assertAll();
        });
    }
    
    public static DecisionTable locateDTbyId(DMNModel dmnModel, String id) {
       return dmnModel.getDefinitions()
               .findAllChildren(DecisionTable.class)
               .stream().filter(d -> d.getId().equals(id))
               .findFirst().orElseThrow(IllegalStateException::new);
    }
}