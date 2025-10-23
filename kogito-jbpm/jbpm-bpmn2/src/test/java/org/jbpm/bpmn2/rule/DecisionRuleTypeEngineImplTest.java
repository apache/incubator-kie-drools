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
package org.jbpm.bpmn2.rule;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.transformation.JsonResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.FEELProperty;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionRuleTypeEngineImplTest {

    private static DecisionRuleTypeEngineImpl DECISION_RULE_TYPE_ENGINE;
    private static JsonResolver JSON_RESOLVER;

    @BeforeAll
    static void setup() {
        DECISION_RULE_TYPE_ENGINE = new DecisionRuleTypeEngineImpl();
        JSON_RESOLVER = new JsonResolver();
    }

    @Test
    void getDMNAnnotatedAdjustedMap() {
        Map<String, Object> rsniInputs = new HashMap<>();
        rsniInputs.put("SOMETHING", "true");
        DMNAnnotated dmnAnnotated = new DMNAnnotated("first", "last");
        rsniInputs.put("dmnAnnotated", dmnAnnotated);
        NOTDMNAnnotated notDMNAnnotated = new NOTDMNAnnotated("first", "last");
        rsniInputs.put("notDMNAnnotated", notDMNAnnotated);
        Map<String, Object> jsonResolvedInputs = JSON_RESOLVER.resolveAll(rsniInputs);
        Map<String, Object> retrieved = DECISION_RULE_TYPE_ENGINE.getDMNAnnotatedAdjustedMap(rsniInputs, jsonResolvedInputs);
        assertThat(retrieved)
                .containsEntry("SOMETHING", "true")
                .containsEntry("dmnAnnotated", dmnAnnotated);
        assertThat(retrieved.get("notDMNAnnotated")).isInstanceOf(Map.class);
    }

    @Test
    void isDMNAnnotatedBean() {
        DMNAnnotated dmnAnnotated = new DMNAnnotated("first", "last");
        assertThat(DECISION_RULE_TYPE_ENGINE.isDMNAnnotatedBean(dmnAnnotated)).isTrue();
        NOTDMNAnnotated notDMNAnnotated = new NOTDMNAnnotated("first", "last");
        assertThat(DECISION_RULE_TYPE_ENGINE.isDMNAnnotatedBean(notDMNAnnotated)).isFalse();
    }

    @Test
    void isDMNAnnotatedClass() {
        assertThat(DECISION_RULE_TYPE_ENGINE.isDMNAnnotatedClass(DMNAnnotated.class)).isTrue();
        assertThat(DECISION_RULE_TYPE_ENGINE.isDMNAnnotatedClass(NOTDMNAnnotated.class)).isFalse();
    }

    private static class DMNAnnotated {
        private String firstName;
        private String lastName;

        public DMNAnnotated(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @FEELProperty("first name")
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    private static class NOTDMNAnnotated {
        private String firstName;
        private String lastName;

        public NOTDMNAnnotated(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
