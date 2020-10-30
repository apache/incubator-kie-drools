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

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNRuntimeListenerPropertyTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeListenerPropertyTest.class);

    public static class TestPropertyListener extends DefaultDMNRuntimeEventListener {
        private static final List<Object> results = new ArrayList<>();

        @Override
        public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
            results.add(event.getResult().getDecisionResultByName(event.getDecision().getName()).getResult());
        }
    }

    @Test
    public void test() {
        final String LISTENER_KEY = "org.kie.dmn.runtime.listeners.DMNRuntimeListenerPropertyTest";
        System.setProperty(LISTENER_KEY, TestPropertyListener.class.getCanonicalName());
        try {
            final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Greetings.dmn", this.getClass());
            final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_2027051c-0030-40f1-8b96-1b1422f8b257", "Drawing 1");
            assertThat(dmnModel, notNullValue());
            assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

            final DMNContext context = DMNFactory.newContext();
            context.set("Name", "John Doe");

            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
            assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

            assertThat(TestPropertyListener.results).contains("Hello John Doe");
        } finally {
            System.clearProperty(LISTENER_KEY);
        }
    }
}
