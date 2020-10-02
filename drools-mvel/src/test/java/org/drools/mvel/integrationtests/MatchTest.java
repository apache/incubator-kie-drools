/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.ArrayList;
import java.util.List;

import org.drools.mvel.compiler.Bar;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Foo;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MatchTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(MatchTest.class);

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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

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
        assertEquals(1, list.size());
        assertTrue(list.contains(lotusElise));

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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

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
        assertTrue(list.contains(miniVanType));
        assertTrue(list.contains(kieCarnival));
        assertTrue(list.contains(renaultEspace));

        ksession.dispose();
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
                "  list.addAll(((org.drools.core.spi.Activation)kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

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
        assertTrue(list.contains(roadsterType));
        assertTrue(list.contains(bmwZ4));
        assertTrue(list.contains(lotusElise));
        assertTrue(list.contains(mazdaMx5));

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
                "  list.addAll(((org.drools.core.spi.Activation)kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertTrue(list.contains(0L));

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
                "  list.addAll(((org.drools.core.spi.Activation)kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

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
        assertTrue(list.contains(roadsterType));
        assertTrue(list.contains(bmwZ4));
        assertTrue(list.contains(lotusElise));
        assertTrue(list.contains(mazdaMx5));
        assertFalse(list.contains(miniVanType));
        assertFalse(list.contains(kiaCarnival));
        assertFalse(list.contains(renaultEspace));

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
                "  list.addAll(((org.drools.core.spi.Activation)kcontext.getMatch()).getObjectsDeep());\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

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
        assertTrue(list.contains(roadsterType));
        assertFalse(list.contains(bmwZ4));
        assertFalse(list.contains(lotusElise));
        assertFalse(list.contains(mazdaMx5));
        assertTrue(list.contains(miniVanType));
        assertTrue(list.contains(kiaCarnival));
        assertTrue(list.contains(renaultEspace));

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
                     "        list.addAll( ((org.drools.core.spi.Activation) kcontext.getMatch()).getObjectsDeep() );" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        List<Object> list = new ArrayList<>();
        ksession.setGlobal( "list", list );
        ksession.insert("");
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( 0.0, list.get(0) );
    }

    @Test
    public void testObjectsDeepOnAccumulateNotSupportingReverse() {
        // DROOLS-1687
        String rule = "package testpkg;\n" +
                      "import " + CloudComputer.class.getCanonicalName() + "\n;" +
                      "import " + CloudProcess.class.getCanonicalName() + "\n;" +
                      "rule requiredCpuPowerTotal\n" +
                      "    when\n" +
                      "        $computer : CloudComputer($cpuPower : cpuPower)\n" +
                      "        accumulate(\n" +
                      "            CloudProcess(\n" +
                      "                computer == $computer,\n" +
                      "                $requiredCpuPower : requiredCpuPower);\n" +
                      "            $requiredCpuPowerTotal : max($requiredCpuPower);\n" +
                      "            (Integer) $requiredCpuPowerTotal > $cpuPower\n" +
                      "        )\n" +
                      "    then\n" +
                      "        ((org.drools.core.spi.Activation) kcontext.getMatch()).getObjectsDeep();" +
                      "end";
        KieSession kieSession = new KieHelper().addContent(rule, ResourceType.DRL).build().newKieSession();

        CloudProcess proc = new CloudProcess();
        proc.setRequiredCpuPower(1);
        CloudComputer comp = new CloudComputer();
        proc.setComputer(comp);

        kieSession.insert( proc );
        kieSession.insert(comp);
        try {
            kieSession.fireAllRules();
            fail("Using getObjectsDeep() on a rule using an accumulate function not supporting reverse must fail");
        } catch (Exception e) {
            assertTrue(e.toString().contains( "max" ));
            assertTrue(e.toString().contains( "requiredCpuPowerTotal" ));
        }
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

}
