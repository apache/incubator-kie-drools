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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.decisiontable.DTListenerTest;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.RuleAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DTAnnotationListenerTest {

    public static final Logger LOG = LoggerFactory.getLogger(DTAnnotationListenerTest.class);

    public static class DTAnnotationListener extends DefaultDMNRuntimeEventListener {
        private List<AfterEvaluateDecisionTableEvent> events = new ArrayList<>();

        @Override
        public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
            events.add(event);
        }

        public static DecisionTable locateDTfromEvent(AfterEvaluateDecisionTableEvent event) {
            return DTListenerTest.locateDTbyId(((DMNResultImpl) event.getResult()).getModel(), event.getDecisionTableId());
        }

        public List<String> getMatchedAnns() {
            List<String> matchedAnns = new ArrayList<>();
            for (AfterEvaluateDecisionTableEvent event : events) {
                DecisionTable dt = locateDTfromEvent(event);
                for (Integer m : event.getMatches()) {
                    matchedAnns.add(dt.getRule().get(m - 1).getAnnotationEntry().stream().map(RuleAnnotation::getText).collect(Collectors.joining(", ")));
                }
            }
            return matchedAnns;
        }

        public List<String> getSelectedAnns() {
            List<String> selectedAnns = new ArrayList<>();
            for (AfterEvaluateDecisionTableEvent event : events) {
                for (Integer s : event.getSelected()) {
                    DecisionTable dt = locateDTfromEvent(event);
                    selectedAnns.add(dt.getRule().get(s - 1).getAnnotationEntry().stream().map(RuleAnnotation::getText).collect(Collectors.joining(", ")));
                }
            }
            return selectedAnns;
        }
    }

    @Test
    void test() {
        final DTAnnotationListener listenerUT = new DTAnnotationListener();

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("license.dmn", this.getClass());
        runtime.addListener(listenerUT);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_1F095E5D-0E50-4564-9A76-DD4735BF938A", "license");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a person", mapOf(entry("name", "John Doe"), entry("age", 47)));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        assertThat(dmnResult.getDecisionResultByName("can get license").getResult()).isEqualTo(Boolean.TRUE);

        assertThat(listenerUT.getMatchedAnns()).hasSize(1).contains("general for EU must be at least 18y/o");
        assertThat(listenerUT.getSelectedAnns()).hasSize(1).contains("general for EU must be at least 18y/o");
    }
}