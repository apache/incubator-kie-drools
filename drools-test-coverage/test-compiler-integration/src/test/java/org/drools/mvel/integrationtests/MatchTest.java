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

import org.drools.core.rule.consequence.InternalMatch;
import org.drools.mvel.compiler.Bar;
import org.drools.mvel.compiler.Foo;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MatchTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MatchTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testGetObjectsOnePattern() {
        // DROOLS-1470
        String str =
                "import org.drools.mvel.compiler.Foo\n" +
                "import org.drools.mvel.compiler.Bar\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  Foo(id == \"Lotus Elise\")\n" +
                "then\n" +
                "  list.addAll(kcontext.getMatch().getObjects());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Bar roadsterType = new Bar("roadster");
        ksession.insert(roadsterType);
        Foo bmwZ4 = new Foo("BMW Z4", roadsterType);
        ksession.insert(bmwZ4);
        Foo lotusElise = new Foo("Lotus Elise", roadsterType);
        ksession.insert(lotusElise);
        Foo mazdaMx5 = new Foo("Mazda MX-5", roadsterType);
        ksession.insert(mazdaMx5);

        Bar miniVanType = new Bar("minivan");
        ksession.insert(miniVanType);
        Foo kieCarnival = new Foo("Kia Carnival", miniVanType);
        ksession.insert(kieCarnival);
        Foo renaultEspace = new Foo("Renault Espace", miniVanType);
        ksession.insert(renaultEspace);

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(lotusElise)).isTrue();

        ksession.dispose();
    }

    @Test
    public void testGetObjectsTwoPatterns() {
        // DROOLS-1470
        String str =
                "import org.drools.mvel.compiler.Foo\n" +
                "import org.drools.mvel.compiler.Bar\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $b : Bar(id == \"minivan\")\n" +
                "  Foo(bar == $b)\n" +
                "then\n" +
                "  list.addAll(kcontext.getMatch().getObjects());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Bar roadsterType = new Bar("roadster");
        ksession.insert(roadsterType);
        Foo bmwZ4 = new Foo("BMW Z4", roadsterType);
        ksession.insert(bmwZ4);
        Foo lotusElise = new Foo("Lotus Elise", roadsterType);
        ksession.insert(lotusElise);
        Foo mazdaMx5 = new Foo("Mazda MX-5", roadsterType);
        ksession.insert(mazdaMx5);

        Bar miniVanType = new Bar("minivan");
        ksession.insert(miniVanType);
        Foo kieCarnival = new Foo("Kia Carnival", miniVanType);
        ksession.insert(kieCarnival);
        Foo renaultEspace = new Foo("Renault Espace", miniVanType);
        ksession.insert(renaultEspace);

        ksession.fireAllRules();
        assertThat(list.contains(miniVanType)).isTrue();
        assertThat(list.contains(kieCarnival)).isTrue();
        assertThat(list.contains(renaultEspace)).isTrue();

        ksession.dispose();
    }

    @Test
    public void testGetObjectsAccumulateWithNoMatchingFacts() {
        // DROOLS-1470
        String drl =
                "global java.util.List list\n" +
                "rule R when\n" +
                "  accumulate(\n" +
                "    Object(false);\n" +
                "    $total : count()\n" +
                "  )\n" +
                "then\n" +
                "  list.addAll(((" + InternalMatch.class.getCanonicalName() + ")kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.contains(0L)).isTrue();

        ksession.dispose();
    }

    @Test
    public void testGetObjectsExists() {
        // DROOLS-1474
        String str =
                "import org.drools.mvel.compiler.Foo\n" +
                "import org.drools.mvel.compiler.Bar\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $b : Bar(id == \"roadster\")\n" +
                "  exists Foo(bar == $b)\n" +
                "then\n" +
                "  list.addAll(((" + InternalMatch.class.getCanonicalName() + ")kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Bar roadsterType = new Bar("roadster");
        ksession.insert(roadsterType);
        Foo bmwZ4 = new Foo("BMW Z4", roadsterType);
        ksession.insert(bmwZ4);
        Foo lotusElise = new Foo("Lotus Elise", roadsterType);
        ksession.insert(lotusElise);
        Foo mazdaMx5 = new Foo("Mazda MX-5", roadsterType);
        ksession.insert(mazdaMx5);

        Bar miniVanType = new Bar("minivan");
        ksession.insert(miniVanType);
        Foo kiaCarnival = new Foo("Kia Carnival", miniVanType);
        ksession.insert(kiaCarnival);
        Foo renaultEspace = new Foo("Renault Espace", miniVanType);
        ksession.insert(renaultEspace);

        ksession.fireAllRules();
        assertThat(list.contains(roadsterType)).isTrue();
        assertThat(list.contains(bmwZ4)).isTrue();
        assertThat(list.contains(lotusElise)).isTrue();
        assertThat(list.contains(mazdaMx5)).isTrue();
        assertThat(list.contains(miniVanType)).isFalse();
        assertThat(list.contains(kiaCarnival)).isFalse();
        assertThat(list.contains(renaultEspace)).isFalse();

        ksession.dispose();
    }

    @Test
    public void testObjectsDeepOnNestedAccumulate() {
        // DROOLS-1686
        String drl = "package testpkg;\n" +
                     "global java.util.List list;\n" +
                     "rule fairAssignmentCountPerTeam\n" +
                     "    when\n" +
                     "        accumulate(\n" +
                     "            $s : String()\n" +
                     "            and accumulate(\n" +
                     "                Number(this != null);\n" +
                     "                $count : count()\n" +
                     "            );\n" +
                     "            $result : average($count)\n" +
                     "        )\n" +
                     "    then\n" +
                     "        list.addAll( ((" + InternalMatch.class.getCanonicalName() + ") kcontext.getMatch()).getObjectsDeep() );" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.insert("");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(0.0);
    }

    @Test
    public void testObjectsDeepOnAccumulateWithoutReverse() {
        String rule =
                "package testpkg;\n" +
                "import " + CloudComputer.class.getCanonicalName() + "\n;" +
                "import " + CloudProcess.class.getCanonicalName() + "\n;" +
                "global java.util.List list\n" +
                "rule requiredCpuPowerTotal when\n" +
                "        $computer : CloudComputer($cpuPower : cpuPower)\n" +
                "        accumulate(\n" +
                "            CloudProcess(\n" +
                "                computer == $computer,\n" +
                "                $requiredCpuPower : requiredCpuPower);\n" +
                "            $requiredCpuPowerTotal : max($requiredCpuPower);\n" +
                "            (Integer) $requiredCpuPowerTotal > $cpuPower\n" +
                "        )\n" +
                "    then\n" +
                "        list.addAll(((" + InternalMatch.class.getCanonicalName() + ") kcontext.getMatch()).getObjectsDeep());" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession kieSession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        kieSession.setGlobal("list", list);

        CloudProcess proc = new CloudProcess();
        proc.setRequiredCpuPower(5);
        CloudComputer comp = new CloudComputer();
        proc.setComputer(comp);

        kieSession.insert( proc );
        kieSession.insert(comp);

        kieSession.fireAllRules();

        assertThat(list.contains(comp)).isTrue();
        assertThat(list.contains(5)).isTrue();

        kieSession.dispose();
    }

    public static class CloudComputer {
        public int getCpuPower() {
            return 0;
        }
    }

    public static class CloudProcess {

        private int requiredCpuPower;
        private CloudComputer computer;

        public void setRequiredCpuPower(int requiredCpuPower) {
            this.requiredCpuPower = requiredCpuPower;
        }

        public int getRequiredCpuPower() {
            return requiredCpuPower;
        }

        public void setComputer(CloudComputer computer) {
            this.computer = computer;
        }

        public CloudComputer getComputer() {
            return computer;
        }
    }

    @Test
    public void testGetObjectsAccumulate() {
        // DROOLS-1470
        String str =
                "import org.drools.mvel.compiler.Foo\n" +
                "import org.drools.mvel.compiler.Bar\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $b : Bar(id == \"roadster\")\n" +
                "  accumulate(\n" +
                "    $f : Foo(bar == $b);\n" +
                "    $t : count($f)\n" +
                "  )\n" +
                "then\n" +
                "  list.addAll(((" + InternalMatch.class.getCanonicalName() + ")kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Bar roadsterType = new Bar("roadster");
        ksession.insert(roadsterType);
        Foo bmwZ4 = new Foo("BMW Z4", roadsterType);
        ksession.insert(bmwZ4);
        Foo lotusElise = new Foo("Lotus Elise", roadsterType);
        ksession.insert(lotusElise);
        Foo mazdaMx5 = new Foo("Mazda MX-5", roadsterType);
        ksession.insert(mazdaMx5);

        Bar miniVanType = new Bar("minivan");
        ksession.insert(miniVanType);
        Foo kieCarnival = new Foo("Kia Carnival", miniVanType);
        ksession.insert(kieCarnival);
        Foo renaultEspace = new Foo("Renault Espace", miniVanType);
        ksession.insert(renaultEspace);

        ksession.fireAllRules();
        assertThat(list.contains(roadsterType)).isTrue();
        assertThat(list.contains(bmwZ4)).isTrue();
        assertThat(list.contains(lotusElise)).isTrue();
        assertThat(list.contains(mazdaMx5)).isTrue();

        ksession.dispose();
    }

    @Test
    public void testGetObjectsAccumulateWithNestedExists() {
        // DROOLS-1474
        String str =
                "import org.drools.mvel.compiler.Foo\n" +
                "import org.drools.mvel.compiler.Bar\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "  $b1 : Bar(id == \"roadster\")\n" +
                "  accumulate(\n" +
                "    $b2 : Bar(this != $b1) and exists Foo(bar == $b2);\n" +
                "    $t : count($b2)\n" +
                "  )\n" +
                "then\n" +
                "  list.addAll(((" + InternalMatch.class.getCanonicalName() + ")kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Bar roadsterType = new Bar("roadster");
        ksession.insert(roadsterType);
        Foo bmwZ4 = new Foo("BMW Z4", roadsterType);
        ksession.insert(bmwZ4);
        Foo lotusElise = new Foo("Lotus Elise", roadsterType);
        ksession.insert(lotusElise);
        Foo mazdaMx5 = new Foo("Mazda MX-5", roadsterType);
        ksession.insert(mazdaMx5);

        Bar miniVanType = new Bar("minivan");
        ksession.insert(miniVanType);
        Foo kiaCarnival = new Foo("Kia Carnival", miniVanType);
        ksession.insert(kiaCarnival);
        Foo renaultEspace = new Foo("Renault Espace", miniVanType);
        ksession.insert(renaultEspace);

        ksession.fireAllRules();
        assertThat(list.contains(roadsterType)).isTrue();
        assertThat(list.contains(bmwZ4)).isFalse();
        assertThat(list.contains(lotusElise)).isFalse();
        assertThat(list.contains(mazdaMx5)).isFalse();
        assertThat(list.contains(miniVanType)).isTrue();
        assertThat(list.contains(kiaCarnival)).isTrue();
        assertThat(list.contains(renaultEspace)).isTrue();

        ksession.dispose();
    }
}
