/*
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
package org.kie.kogito.dmn;

import java.io.InputStreamReader;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNKogitoTest {

    @Test
    public void testBasic() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(Collections.emptySet(), false, new InputStreamReader(DMNKogitoTest.class.getResourceAsStream("TrafficViolation.dmn")));
        assertThat(dmnRuntime.getModels()).hasSize(1);

        final String TRAFFIC_VIOLATION_NS = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        final String TRAFFIC_VIOLATION_NAME = "Traffic Violation";
        DecisionModel kogitoAPI = new DmnDecisionModel(dmnRuntime, TRAFFIC_VIOLATION_NS, TRAFFIC_VIOLATION_NAME);
        assertThat(kogitoAPI.getDMNModel().getNamespace()).isEqualTo(TRAFFIC_VIOLATION_NS);
        assertThat(kogitoAPI.getDMNModel().getName()).isEqualTo(TRAFFIC_VIOLATION_NAME);
        assertThat(kogitoAPI.getDMNModel().getInputs()).as("Traffic Violation model has 2 inputs").hasSize(2);
    }

    @Test
    public void testProfile() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(Collections.emptySet(), false, new InputStreamReader(DMNKogitoTest.class.getResourceAsStream("profile.dmn")));
        assertThat(dmnRuntime.getModels()).hasSize(1);

        DMNModel dmnModel = dmnRuntime.getModels().get(0);
        assertThat(dmnModel.getMessages(Severity.ERROR)).isEmpty(); // nn any() is a Kie-extended built-in function.
    }
}
