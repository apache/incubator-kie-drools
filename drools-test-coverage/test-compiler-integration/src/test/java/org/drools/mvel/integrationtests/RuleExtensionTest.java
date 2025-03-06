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
package org.drools.mvel.integrationtests;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleExtensionTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsNonexistingRule(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsBetweenDRLs(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(list.size()).isEqualTo(0);

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsOnIncrementalKB(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(list.size()).isEqualTo(0);

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsMissingOnIncrementalKB(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }



    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsWithCompositeKBuilder(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(list.size()).isEqualTo(0);

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsNonExistingWithCompositeKBuilder(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsNonExistingWithCompositeKBuilderOutOfOrder(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();

        System.out.println( errors );
        assertThat(errors.toString().contains("Circular")).isFalse();
        assertThat(errors.toString().contains("Base")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsWithCompositeKBuilderFreeOrder(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(list.size()).isEqualTo(0);

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleExtendsExtendsWithCompositeKBuilderFreeOrder(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(list.size()).isEqualTo(0);

        knowledgeSession.insert( 4 );
        knowledgeSession.insert( 6 );
        knowledgeSession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat((int) list.get(0)).isEqualTo(10);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRuleCircularExtension(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();

        assertThat(errors.iterator().next().toString().contains("Circular")).isTrue();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testManyExtensions(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-7542
        String base =
                "package org.drools.test;\n" +
                "\n" +
                "rule R0 when\n" +
                "  $s : String()\n" +
                "then\n" +
                "end\n";

        StringBuilder drl = new StringBuilder(base);
        for (int i = 1; i < 100; i++) {
            drl.append(getExtendedRule(i));
        }

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, drl.toString());
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).isTrue();
     }

    private String getExtendedRule(int i) {
        return  "rule R" + i +" extends R0 when\n" +
                "  $i : Integer( this == " + i + " )\n" +
                "then\n" +
                "end\n";

    }
}
