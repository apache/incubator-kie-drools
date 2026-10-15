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

import org.drools.model.codegen.execmodel.domain.DynamicFact;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@code @propertyChangeSupport} on DRL type declarations in the executable model.
 *
 * Related issue: https://github.com/apache/incubator-kie/issues/7137
 */
public class PropertyChangeSupportTest extends BaseModelTest {

    private static final String DRL_HEADER =
            "import " + DynamicFact.class.getCanonicalName() + ";\n" +
            "declare DynamicFact\n" +
            "  @propertyChangeSupport\n" +
            "end\n";

    // -----------------------------------------------------------------------
    //    Plain insert: setter in rule1 fires a PropertyChangeEvent that should
    //    cause rule2 to match without an explicit modify.
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPropertyChangeSupportTriggersUpdate(RUN_TYPE runType) {
        String drl = DRL_HEADER +
                "rule rule1\n" +
                "  when\n" +
                "    $f : DynamicFact( name == \"user1\" )\n" +
                "  then\n" +
                "    $f.setName(\"user2\");\n" +
                "end\n" +
                "rule rule2\n" +
                "  when\n" +
                "    $f : DynamicFact( name == \"user2\" )\n" +
                "  then\n" +
                "    $f.setValue(\"VAL1\");\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl);
        try {
            DynamicFact fact = new DynamicFact();
            fact.setName("user1");

            ksession.insert(fact);
            ksession.fireAllRules();

            assertThat(fact.getValue())
                    .as("rule2 should have fired after the property change triggered by rule1")
                    .isEqualTo("VAL1");
        } finally {
            ksession.dispose();
        }
    }

    // -----------------------------------------------------------------------
    //    Listener lifecycle: registered after insert, removed after delete,
    //    and also removed after dispose.
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("parameters")
    public void testListenerLifecycle(RUN_TYPE runType) {
        String drl = DRL_HEADER +
                "rule dummy when DynamicFact() then end\n";

        DynamicFact fact = new DynamicFact();

        assertThat(fact.getPropertyChangeListeners())
                .as("no listener before insert")
                .isEmpty();

        KieSession ksession = getKieSession(runType, drl);
        FactHandle fh = ksession.insert(fact);

        assertThat(fact.getPropertyChangeListeners())
                .as("one listener must be registered after insert")
                .hasSize(1);

        ksession.delete(fh);

        assertThat(fact.getPropertyChangeListeners())
                .as("listener must be removed after delete")
                .isEmpty();

        // Re-insert so dispose has something to clean up.
        ksession.insert(fact);

        assertThat(fact.getPropertyChangeListeners())
                .as("one listener must be registered after re-insert")
                .hasSize(1);

        ksession.dispose();

        assertThat(fact.getPropertyChangeListeners())
                .as("listener must be removed after dispose")
                .isEmpty();
    }

    // -----------------------------------------------------------------------
    //    RHS insert(fact, true) – deprecated but must keep working.
    //    A trigger fact causes a rule whose RHS uses insert(newFact, true);
    //    the new fact should get a listener and trigger the chain rule.
    // -----------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("parameters")
    public void testDynamicInsertFromRhsStillWorks(RUN_TYPE runType) {
        // A "trigger" fact causes ruleInsert to insert a DynamicFact via the
        // deprecated insert(fact, true) RHS call. The DynamicFact should get a
        // listener, and ruleName + ruleValue should fire on it.
        String drl =
                "import " + DynamicFact.class.getCanonicalName() + ";\n" +
                "import " + Trigger.class.getCanonicalName() + ";\n" +
                "declare DynamicFact\n" +
                "  @propertyChangeSupport\n" +
                "end\n" +
                "rule ruleInsert\n" +
                "  when\n" +
                "    Trigger()\n" +
                "  then\n" +
                "    " + DynamicFact.class.getSimpleName() + " f = new " + DynamicFact.class.getSimpleName() + "();\n" +
                "    f.setName(\"user1\");\n" +
                "    insert(f, true);\n" +
                "end\n" +
                "rule ruleChangeName\n" +
                "  when\n" +
                "    $f : DynamicFact( name == \"user1\" )\n" +
                "  then\n" +
                "    $f.setName(\"user2\");\n" +
                "end\n" +
                "rule ruleSetValue\n" +
                "  when\n" +
                "    $f : DynamicFact( name == \"user2\" )\n" +
                "  then\n" +
                "    $f.setValue(\"VAL1\");\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl);
        try {
            ksession.insert(new Trigger());
            ksession.fireAllRules();

            java.util.Collection<DynamicFact> facts = ksession.getInstancesOf(DynamicFact.class)
                    .stream().collect(java.util.stream.Collectors.toList());

            assertThat(facts).hasSize(1);
            assertThat(facts.iterator().next().getValue())
                    .as("ruleSetValue should have fired after the property change triggered by ruleChangeName (dynamic insert)")
                    .isEqualTo("VAL1");
        } finally {
            ksession.dispose();
        }
    }

    /** Minimal trigger fact used by testDynamicInsertFromRhsStillWorks. */
    public static class Trigger {}
}
