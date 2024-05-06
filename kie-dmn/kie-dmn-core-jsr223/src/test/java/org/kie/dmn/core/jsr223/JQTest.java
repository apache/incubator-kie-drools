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
package org.kie.dmn.core.jsr223;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class JQTest {
    private static final Logger LOG = LoggerFactory.getLogger( JQTest.class );

    @Test
    void jqBmi() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
            .setDecisionLogicCompilerFactory(new JSR223EvaluatorCompilerFactory())
            .buildConfiguration()
            .fromClasspathResource("/jq/BMI.dmn", this.getClass())
            .getOrElseThrow(e -> new RuntimeException(e));
        DMNModel model = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Height", 72);
        dmnContext.set("Mass", 180);
        DMNResult evaluateAll = dmnRuntime.evaluateAll(model, dmnContext);
        LOG.info("{}", evaluateAll.getContext());
        LOG.info("{}", evaluateAll.getMessages());
        assertThat(evaluateAll.getDecisionResultByName("BMI value classification").getResult()).isEqualTo("Normal range");
    }

    @Test
    void jQIsAdult() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
            .setDecisionLogicCompilerFactory(new JSR223EvaluatorCompilerFactory())
            .buildConfiguration()
            .fromClasspathResource("/jq/isAdult.dmn", this.getClass())
            .getOrElseThrow(e -> new RuntimeException(e));
        DMNModel model = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Age", 47);
        DMNResult evaluateAll = dmnRuntime.evaluateAll(model, dmnContext);
        LOG.info("{}", evaluateAll.getContext());
        LOG.info("{}", evaluateAll.getMessages());
        assertThat(evaluateAll.getDecisionResultByName("Is Adult?").getResult()).isEqualTo(true);
    }

    @Test
    void jQIsAdult2() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
            .setDecisionLogicCompilerFactory(new JSR223EvaluatorCompilerFactory())
            .buildConfiguration()
            .fromClasspathResource("/jq/isAdult.dmn", this.getClass())
            .getOrElseThrow(e -> new RuntimeException(e));
        DMNModel model = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Age", 17);
        DMNResult evaluateAll = dmnRuntime.evaluateAll(model, dmnContext);
        LOG.info("{}", evaluateAll.getContext());
        LOG.info("{}", evaluateAll.getMessages());
        assertThat(evaluateAll.getDecisionResultByName("Is Adult?").getResult()).isEqualTo(false);
    }

    @Test
    void isPersonNameAnAdult() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
            .setDecisionLogicCompilerFactory(new JSR223EvaluatorCompilerFactory())
            .buildConfiguration()
            .fromClasspathResource("/jq/IsPersonNameAnAdult.dmn", this.getClass())
            .getOrElseThrow(e -> new RuntimeException(e));
        DMNModel model = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Full Name", "John Doe");
        dmnContext.set("Age", 47);
        DMNResult evaluateAll = dmnRuntime.evaluateAll(model, dmnContext);
        LOG.info("{}", evaluateAll.getContext());
        LOG.info("{}", evaluateAll.getMessages());
        assertThat(evaluateAll.getDecisionResultByName("expr").getResult()).isEqualTo("The person John Doe is an Adult");
    }
}
