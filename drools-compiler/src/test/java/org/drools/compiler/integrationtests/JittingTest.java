/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.assertj.core.api.Assertions;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.facts.AnEnum;
import org.drools.compiler.integrationtests.facts.FactWithEnum;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

public class JittingTest extends CommonTestMethodBase {

    @Test
    public void testJitConstraintInvokingConstructor() {
        // JBRULES-3628
        final String str = "import org.drools.compiler.Person;\n" +
                "rule R1 when\n" +
                "   Person( new Integer( ageAsInteger ) < 40 ) \n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testJittingConstraintWithInvocationOnLiteral() {
        final String str = "package com.sample\n" +
                "import org.drools.compiler.Person\n" +
                "rule XXX when\n" +
                "  Person( name.toString().toLowerCase().contains( \"mark\".toString().toLowerCase() ) )\n" +
                "then\n" +
                "end\n";

        testJitting(str);
    }

    @Test
    public void testJittingMethodWithCharSequenceArg() {
        final String str = "package com.sample\n" +
                "import org.drools.compiler.Person\n" +
                "rule XXX when\n" +
                "  Person( $n : name, $n.contains( \"mark\" ) )\n" +
                "then\n" +
                "end\n";

        testJitting(str);
    }

    private void testJitting(final String drl) {
        final KieBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("mark", 37));
        ksession.insert(new Person("mario", 38));

        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testJittingEnum() {
        final String drl = "import " + AnEnum.class.getCanonicalName() + ";\n" +
                " rule R1 \n" +
                " when \n" +
                "    $enumFact: AnEnum(this == AnEnum.FIRST)\n" +
                " then \n" +
                " end ";

        final KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( drl, ResourceType.DRL );
        final KieBase kieBase = kieHelper.build(ConstraintJittingThresholdOption.get(0));
        final KieSession kieSession = kieBase.newKieSession();

        kieSession.insert(AnEnum.FIRST);
        Assertions.assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testJittingEnumAttribute() {
        final String drl = "import " + AnEnum.class.getCanonicalName() + ";\n" +
                "import " + FactWithEnum.class.getCanonicalName() + ";\n" +
                " rule R1 \n" +
                " when \n" +
                "    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n" +
                " then \n" +
                " end ";

        final KieHelper kieHelper = new KieHelper();
        kieHelper.addContent( drl, ResourceType.DRL );
        final KieBase kieBase = kieHelper.build(ConstraintJittingThresholdOption.get(0));

        final KieSession kieSession = kieBase.newKieSession();
        kieSession.insert(new FactWithEnum(AnEnum.FIRST));
        Assertions.assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }
}
