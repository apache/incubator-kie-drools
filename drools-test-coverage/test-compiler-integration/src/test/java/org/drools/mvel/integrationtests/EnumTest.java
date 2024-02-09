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

import java.util.Collection;
import java.util.List;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.compiler.Triangle;
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
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for declared Enums
 */
@RunWith(Parameterized.class)
public class EnumTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EnumTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEnums() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Enums.drl");
        KieSession ksession = kbase.newKieSession();
        final java.util.List list = new java.util.ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertThat(list.contains(4)).isTrue();
        assertThat(list.contains(5.976e+24)).isTrue();
        assertThat(list.contains("Mercury")).isTrue();

        ksession.dispose();
    }

    @Test
    public void testEnumsWithCompositeBuildingProcess() throws Exception {
        final String drl = "package org.test; " +
                     "" +
                     "declare enum DaysOfWeek " +
                     "    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;\n" +
                     "end\n" +

                     "declare Test " +
                     "  field: DaysOfWeek " +
                     "end";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @Test
    public void testQueryEnum() {
        final String str = "package org.kie.test;\n" +
                "\n" +
                "declare enum Ennumm\n" +
                "  ONE, TWO;\n" +
                "end\n" +
                "\n" +
                "declare Bean\n" +
                "  fld : Ennumm\n" +
                "end\n" +
                "\n" +
                "query seeWhat( Ennumm $e, Bean $b )\n" +
                "  $b := Bean( $e == Ennumm.ONE )\n" +
                "end\n" +
                "\n" +
                "rule rool\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( Ennumm.ONE ) );\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule rool2\n" +
                "when\n" +
                "  seeWhat( $ex, $bx ; )\n" +
                "then\n" +
                "  System.out.println( $bx );\n" +
                "end";

        final String str2 = "package org.drools.mvel.compiler.test2; \n" +
                        "" +
                        "declare Naeb \n" +
                        "   fld : String \n" +
                        "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
        
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder);

        Collection<KiePackage> kpkgs = KieBaseUtil.getKieBaseFromKieModuleFromDrl("tmp", kieBaseTestConfiguration, str2).getKiePackages();
        kbase.addPackages(kpkgs);

        final KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testInnerEnum() throws Exception {
        final StringBuilder rule = new StringBuilder();
        rule.append( "package org.drools.mvel.compiler\n" );
        rule.append( "rule X\n" );
        rule.append( "when\n" );
        rule.append( "    Triangle( type == Triangle.Type.UNCLASSIFIED )\n" );
        rule.append( "then\n" );
        rule.append( "end\n" );

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule.toString());
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Triangle());
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
        ksession.dispose();
    }
}

