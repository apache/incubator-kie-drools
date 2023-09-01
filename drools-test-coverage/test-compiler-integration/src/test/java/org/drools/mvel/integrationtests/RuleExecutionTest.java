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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RuleExecutionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleExecutionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with testAllWithBeforeAndAfter, testOnDeleteMatchConsequence. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testNoAll() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertThat(list).isEqualTo(asList(-1, 1, -2, 2, -3, 3));
    }

    @Test
    public void testAll() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 @All when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertThat(list).isEqualTo(asList(-1, -2, -3, 1, 2, 3));
    }

    @Test
    public void testAllWithBeforeAndAfter() throws Exception {
        String str =
                "global java.util.List list\n" +
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "    $s : String( this == $i.toString() )\n" +
                "then\n" +
                "    list.add( $i );\n" +
                "end\n" +
                "\n" +
                "rule R2 @All when\n" +
                "    $i : Integer()\n" +
                "    $s : Long( intValue == $i )\n" +
                "then\n" +
                "    insert( \"\" + $i );\n" +
                "    list.add( -$i );\n" +
                "then[$onBeforeAllFire$]\n" +
                "    list.add( -$i * 5 );\n" +
                "then[$onAfterAllFire$]\n" +
                "    list.add( -$i * 4 );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(2);
        ksession.insert(3);
        ksession.insert(1L);
        ksession.insert(2L);
        ksession.insert(3L);
        ksession.fireAllRules();

        assertThat(list).isEqualTo(asList(-5,         // onBeforeAllFire
                -1, -2, -3, // all R2
                -12,        // onAfterAllFire
                1, 2, 3     // R1
        ));
    }

    @Test
    public void testOnDeleteMatchConsequence() throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $p : Person( age > 30 )\n" +
                "then\n" +
                "    $p.setStatus(\"in\");\n" +
                "then[$onDeleteMatch$]\n" +
                "    $p.setStatus(\"out\");\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mario = new Person("Mario", 40);
        FactHandle fact = ksession.insert(mario);
        ksession.fireAllRules();

        assertThat(mario.getStatus()).isEqualTo("in");

        ksession.delete(fact);
        ksession.fireAllRules();

        assertThat(mario.getStatus()).isEqualTo("out");
    }
}
