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

package org.kie.dmn.core.classloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.model.api.RuleAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class DTAnnotationListenerTest {

    public static final Logger LOG = LoggerFactory.getLogger(DTAnnotationListenerTest.class);

    public static class DTAnnotationListener extends DefaultDMNRuntimeEventListener {
        private List<String> matchedAnns = new ArrayList<>();
        private List<String> selectedAnns = new ArrayList<>();

        @Override
        public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
            DMNModel dmnModel = ((DMNResultImpl) event.getResult()).getModel();
            String decisionTableName = event.getDecisionTableName();
            DRGElement drge = dmnModel.getDefinitions().getDrgElement().stream().filter(e -> e.getName().equals(event.getNodeName())).findFirst().orElseThrow(IllegalStateException::new);
            DecisionTable dt = drge.findAllChildren(DecisionTable.class).stream().filter(d -> decisionTableName.equals(nameOfTable(d).orElse(""))).findFirst().orElseThrow(IllegalStateException::new);
            for (Integer m : event.getMatches()) {
                matchedAnns.add(dt.getRule().get(m - 1).getAnnotationEntry().stream().map(RuleAnnotation::getText).collect(Collectors.joining(", ")));
            }
            for (Integer s : event.getSelected()) {
                selectedAnns.add(dt.getRule().get(s - 1).getAnnotationEntry().stream().map(RuleAnnotation::getText).collect(Collectors.joining(", ")));
            }
        }

        public Optional<String> nameOfTable(DecisionTable sourceDT) {
            if (sourceDT.getOutputLabel() != null && !sourceDT.getOutputLabel().isEmpty()) {
                return Optional.of(sourceDT.getOutputLabel());
            } else if (sourceDT.getParent() instanceof NamedElement) { // DT is decision logic of Decision, and similar cases.
                return Optional.of(((NamedElement) sourceDT.getParent()).getName());
            } else if (sourceDT.getParent() instanceof FunctionDefinition && sourceDT.getParent().getParent() instanceof NamedElement) { // DT is decision logic of BKM.
                return Optional.of(((NamedElement) sourceDT.getParent().getParent()).getName());
            }
            return Optional.empty();
        }

        public List<String> getMatchedAnns() {
            return matchedAnns;
        }

        public List<String> getSelectedAnns() {
            return selectedAnns;
        }
    }

    @Test
    public void test() {
        final DTAnnotationListener listenerUT = new DTAnnotationListener();

        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("license.dmn", this.getClass());
        runtime.addListener(listenerUT);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_1F095E5D-0E50-4564-9A76-DD4735BF938A", "license");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("a person", mapOf(entry("name", "John Doe"), entry("age", 47)));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        assertThat(dmnResult.getDecisionResultByName("can get license").getResult()).isEqualTo(Boolean.TRUE);

        assertThat(listenerUT.getMatchedAnns()).hasSize(1).contains("general for EU must be at least 18y/o");
        assertThat(listenerUT.getSelectedAnns()).hasSize(1).contains("general for EU must be at least 18y/o");
    }
}