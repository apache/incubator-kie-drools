/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.dmn;

import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DMNKogitoTest {

    @Test
    public void testBasic() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(DMNKogitoTest.class.getResourceAsStream("TrafficViolation.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String TRAFFIC_VIOLATION_NS = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        final String TRAFFIC_VIOLATION_NAME = "Traffic Violation";
        DecisionModel kogitoAPI = new DmnDecisionModel(dmnRuntime, TRAFFIC_VIOLATION_NS, TRAFFIC_VIOLATION_NAME);
        assertEquals(TRAFFIC_VIOLATION_NS, kogitoAPI.getDMNModel().getNamespace());
        assertEquals(TRAFFIC_VIOLATION_NAME, kogitoAPI.getDMNModel().getName());
        assertEquals(2, kogitoAPI.getDMNModel().getInputs().size(), "Traffic Violation model has 2 inputs");
    }

    @Test
    public void testProfile() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(DMNKogitoTest.class.getResourceAsStream("profile.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        DMNModel dmnModel = dmnRuntime.getModels().get(0);
        assertEquals(0, dmnModel.getMessages(Severity.ERROR).size()); // nn any() is a Kie-extended built-in function.
    }
}
