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
package org.drools.model.codegen.execmodel;

import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.common.DefaultEventHandle;
import org.drools.model.codegen.execmodel.domain.AnnotatedEvent;
import org.drools.model.codegen.execmodel.domain.PropertyChangeSupportFact;
import org.drools.model.codegen.execmodel.domain.PropertyReactiveFact;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that Java annotations preserved through an empty DRL {@code declare} block
 * produce the correct runtime behavior in both classic and executable models.
 *
 * @see AlignAnnotationTest
 * @see <a href="https://github.com/apache/incubator-kie/issues/7185">incubator-kie#7185</a>
 */
public class AlignAnnotationRuntimeTest extends BaseModelTest {

    private static KieModuleModel getCepKieModuleModel() {
        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.newKieBaseModel("kb")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM)
                .newKieSessionModel("ks")
                .setDefault(true).setClockType(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
        return model;
    }

    // --- @Role(EVENT): fact should be treated as an event ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRoleEventRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + AnnotatedEvent.class.getCanonicalName() + ";\n" +
                "declare AnnotatedEvent end\n" +
                "rule R when AnnotatedEvent() then end\n";

        KieSession ksession = getKieSession(runType, getCepKieModuleModel(), drl);
        FactHandle handle = ksession.insert(new AnnotatedEvent());
        assertThat(handle).isInstanceOf(DefaultEventHandle.class);
    }

    // --- @Expires("10s"): event should expire after 10 seconds ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testExpiresRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + AnnotatedEvent.class.getCanonicalName() + ";\n" +
                "declare AnnotatedEvent end\n" +
                "rule R when AnnotatedEvent() then end\n";

        KieSession ksession = getKieSession(runType, getCepKieModuleModel(), drl);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new AnnotatedEvent());
        ksession.fireAllRules();
        assertThat(ksession.getObjects()).hasSize(1);

        clock.advanceTime(11, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertThat(ksession.getObjects()).isEmpty();
    }

    // --- @Timestamp("start"): temporal operators should use the start field ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testTimestampRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + AnnotatedEvent.class.getCanonicalName() + ";\n" +
                "declare AnnotatedEvent end\n" +
                "rule R when\n" +
                "    $a: AnnotatedEvent(length == 0)\n" +
                "    $b: AnnotatedEvent(length == 1, this after[1ms, 1000ms] $a)\n" +
                "then end\n";

        KieSession ksession = getKieSession(runType, getCepKieModuleModel(), drl);

        // Both inserted at the same pseudo clock time; @Timestamp("start") should
        // make the engine use the start field for temporal ordering.
        ksession.insert(new AnnotatedEvent(0, 0));
        ksession.insert(new AnnotatedEvent(500, 1));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    // --- @Duration("length"): event duration should span [start, start+length] ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testDurationRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + AnnotatedEvent.class.getCanonicalName() + ";\n" +
                "declare AnnotatedEvent end\n" +
                "rule R when\n" +
                "    $a: AnnotatedEvent(length > 0)\n" +
                "    $b: AnnotatedEvent(length == 0, this during $a)\n" +
                "then end\n";

        KieSession ksession = getKieSession(runType, getCepKieModuleModel(), drl);

        // $a spans [0, 2000ms], $b at 500ms is during $a
        ksession.insert(new AnnotatedEvent(0, 2000));
        ksession.insert(new AnnotatedEvent(500, 0));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    // --- @ClassReactive: modifying any property should re-trigger evaluation ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testClassReactiveRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + AnnotatedEvent.class.getCanonicalName() + ";\n" +
                "declare AnnotatedEvent end\n" +
                "rule R when\n" +
                "    $e: AnnotatedEvent(value == null)\n" +
                "then\n" +
                // Modify a property NOT in the condition. With @ClassReactive, this
                // re-triggers evaluation. With property-reactive, it would not.
                "    modify($e) { setStart($e.getStart() + 1) }\n" +
                "end\n";

        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.name());

        KieSession ksession = getKieSession(runType, model, drl);
        ksession.insert(new AnnotatedEvent());
        int fired = ksession.fireAllRules(10);
        assertThat(fired).isEqualTo(10);
    }

    // --- @PropertyReactive: only used-property changes should re-trigger ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPropertyReactiveRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + PropertyReactiveFact.class.getCanonicalName() + ";\n" +
                "declare PropertyReactiveFact end\n" +
                "rule R when\n" +
                "    $f: PropertyReactiveFact(name == \"test\")\n" +
                "then\n" +
                // Modify a property NOT in the condition. With @PropertyReactive,
                // this should NOT re-trigger the rule.
                "    modify($f) { setValue($f.getValue() + 1) }\n" +
                "end\n";

        KieModuleModel model = KieServices.get().newKieModuleModel();
        model.setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALLOWED.name());

        KieSession ksession = getKieSession(runType, model, drl);
        PropertyReactiveFact fact = new PropertyReactiveFact();
        fact.setName("test");
        ksession.insert(fact);
        int fired = ksession.fireAllRules(10);
        assertThat(fired).isEqualTo(1);
    }

    // --- @PropertyChangeSupport: setter calls should trigger re-evaluation without modify ---

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPropertyChangeSupportRuntimeWithEmptyDeclare(RUN_TYPE runType) {
        String drl =
                "import " + PropertyChangeSupportFact.class.getCanonicalName() + ";\n" +
                "declare PropertyChangeSupportFact end\n" +
                "rule R1 when\n" +
                "    $f: PropertyChangeSupportFact(name == \"user1\")\n" +
                "then\n" +
                "    $f.setName(\"user2\");\n" +
                "end\n" +
                "rule R2 when\n" +
                "    PropertyChangeSupportFact(name == \"user2\")\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl);
        PropertyChangeSupportFact fact = new PropertyChangeSupportFact();
        fact.setName("user1");
        ksession.insert(fact);
        // R1 fires and calls setName("user2") directly (no modify block).
        // @PropertyChangeSupport should notify the engine, causing R2 to fire.
        int fired = ksession.fireAllRules(10);
        assertThat(fired).isEqualTo(2);
    }
}
