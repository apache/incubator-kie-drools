/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.integrationtests;

import java.util.Collection;

import org.drools.mvel.integrationtests.facts.VarargsFact;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a place where known behavior differences between exec-model and non-exec-model.
 * They are not treated as a bug and should be documented in "Migration from non-executable model to executable model" section
 */
@RunWith(Parameterized.class)
public class KnownExecModelDifferenceTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KnownExecModelDifferenceTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void setter_intToWrapperLongCoercion() {
        // DROOLS-7196
        // Java doesn't coerce int to Long
        String str = "package com.example.reproducer\n" +
                     "import " + VarargsFact.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : VarargsFact()\n" +
                     "then\n" +
                     "  $f.setOneWrapperValue(10);\n" +
                     "end";

        if (kieBaseTestConfiguration.isExecutableModel()) {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
            assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue(); // Fail with exec-model
        } else {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
            KieSession ksession = kbase.newKieSession();

            VarargsFact fact = new VarargsFact();
            ksession.insert(fact);
            ksession.fireAllRules();

            assertThat(fact.getValueList()).containsExactly(10L); // Coerced with non-exec-model
        }
    }

    @Test
    public void setter_intToPrimitiveLongCoercion() {
        // DROOLS-7196
        // Java coerces int to long
        String str = "package com.example.reproducer\n" +
                     "import " + VarargsFact.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : VarargsFact()\n" +
                     "then\n" +
                     "  $f.setOnePrimitiveValue(10);\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        VarargsFact fact = new VarargsFact();
        ksession.insert(fact);
        ksession.fireAllRules();

        assertThat(fact.getValueList()).containsExactly(10L); // Coerced with both cases
    }

    @Test
    public void setter_intToWrapperLongCoercionVarargs() {
        // DROOLS-7196
        // Java doesn't coerce int to Long. Same for varargs
        String str = "package com.example.reproducer\n" +
                     "import " + VarargsFact.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : VarargsFact()\n" +
                     "then\n" +
                     "  $f.setWrapperValues(10, 20);\n" +
                     "end";

        if (kieBaseTestConfiguration.isExecutableModel()) {
            KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
            assertThat(kieBuilder.getResults().hasMessages(Level.ERROR)).isTrue(); // Fail with exec-model
        } else {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
            KieSession ksession = kbase.newKieSession();

            VarargsFact fact = new VarargsFact();
            ksession.insert(fact);
            ksession.fireAllRules();

            assertThat(fact.getValueList()).containsExactly(10L, 20L); // Coerced with non-exec-model
        }
    }

    @Test
    public void setter_intToPrimitiveLongCoercionVarargs() {
        // DROOLS-7196
        // Java coerces int to long. Same for varargs
        String str = "package com.example.reproducer\n" +
                     "import " + VarargsFact.class.getCanonicalName() + ";\n" +
                     "rule R\n" +
                     "dialect \"mvel\"\n" +
                     "when\n" +
                     "  $f : VarargsFact()\n" +
                     "then\n" +
                     "  $f.setPrimitiveValues(10, 20);\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        VarargsFact fact = new VarargsFact();
        ksession.insert(fact);
        ksession.fireAllRules();

        assertThat(fact.getValueList()).containsExactly(10L, 20L); // Coerced with both cases
    }
}
