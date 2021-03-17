/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RuleExtensionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleExtensionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testRuleExtendsNonexistingRule() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Bas\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());
    }

    @Test
    public void testRuleExtendsBetweenDRLs() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str, str2);
        KieSession knowledgeSession = kbase.newKieSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals(0, list.size());

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }


    @Test
    public void testRuleExtendsOnIncrementalKB() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str, str2);
        KieSession knowledgeSession = kbase.newKieSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleExtendsMissingOnIncrementalKB() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Bse\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str, str2);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());
    }



    @Test
    public void testRuleExtendsWithCompositeKBuilder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str, str2);
        KieSession knowledgeSession = kbase.newKieSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleExtendsNonExistingWithCompositeKBuilder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"ase\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str, str2);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());
    }


    @Test
    public void testRuleExtendsNonExistingWithCompositeKBuilderOutOfOrder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"ase\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str, str2);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());

        System.out.println( errors );
        assertFalse( errors.toString().contains( "Circular" ) );
        assertTrue( errors.toString().contains( "Base" ) );
    }

    @Test
    public void testRuleExtendsWithCompositeKBuilderFreeOrder() {
        // DROOLS-100
        String str =
                "package org.drools.test;\n" +
                        "\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  String( this == \"go\" )\n" +
                        "then\n" +
                        "end\n" +
                        "";

        String str2 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( 1 );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str, str2);
        KieSession knowledgeSession = kbase.newKieSession();

        List list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testRuleExtendsExtendsWithCompositeKBuilderFreeOrder() {
        // DROOLS-100
        String str1 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"Base\"\n" +
                        "when\n" +
                        "  $i : Integer( this < 5 )\n" +
                        "then\n" +
                        "end\n";

        String str2 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "  $j : Integer( this > 5 )\n" +
                        "then\n" +
                        "end\n";

        String str3 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"FinalRule\" extends \"ExtYes\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( $i + $j );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1, str2, str3);
        KieSession knowledgeSession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertEquals( 0, list.size() );

        knowledgeSession.insert( 4 );
        knowledgeSession.insert( 6 );
        knowledgeSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 10, (int)list.get(0) );
    }

    @Test
    public void testRuleCircularExtension() {
        // DROOLS-100
        String str1 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"Base\" extends \"FinalRule\"\n" +
                        "when\n" +
                        "  $i : Integer( this < 5 )\n" +
                        "then\n" +
                        "end\n";

        String str2 =
                "package org.drools.test;\n" +
                        "\n" +
                        "rule \"ExtYes\" extends \"Base\"\n" +
                        "when\n" +
                        "  $j : Integer( this > 5 )\n" +
                        "then\n" +
                        "end\n";

        String str3 =
                "package org.drools.test;\n" +
                        "global java.util.List list;\n" +
                        "\n" +
                        "rule \"Dummy\"\n" +
                        "when\n" +
                        "then\n" +
                        "end\n" +
                        "rule \"FinalRule\" extends \"ExtYes\"\n" +
                        "when\n" +
                        "then\n" +
                        "  list.add( $i + $j );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1, str2, str3);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Should have an error", errors.isEmpty());

        assertEquals( 1, errors.size() );
        assertTrue( errors.iterator().next().toString().contains("Circular") );
    }
}
