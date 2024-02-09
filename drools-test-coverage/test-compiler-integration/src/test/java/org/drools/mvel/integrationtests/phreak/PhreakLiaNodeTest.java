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
package org.drools.mvel.integrationtests.phreak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
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
public class PhreakLiaNodeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PhreakLiaNodeTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void test() {
        String str = "package org.drools.mvel.compiler.test\n" +
                     "\n" +
                     "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "\n" +
                     "global java.util.List result;\n" +
                     "rule r1 \n" +
                     "    when \n" +
                     "        $a : A( object == 1 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n" +
                     "rule r2 \n" +
                     "    when \n" +
                     "        $a : A( object == 2 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n " +
                     "rule r3 \n" +
                     "    when \n" +
                     "        $a : A( object == 2 )\n" +
                     "        $b : B( )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n " +
                     "rule r4 \n" +
                     "    when \n" +
                     "        $a : A( object == 3 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        InternalFactHandle fhB = (InternalFactHandle) ksession.insert(B.b(1));
        InternalFactHandle fhA = (InternalFactHandle) ksession.insert(A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---1---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---2---");

        ksession.update(fhA, A.a(2));
        ksession.fireAllRules();
        assertFiredRules(result, "r2", "r3");
        System.out.println("---3---");

        ksession.update(fhA, A.a(2));
        ksession.fireAllRules();
        assertFiredRules(result, "r2", "r3");
        System.out.println("---4---");

        ksession.update(fhA, A.a(3));
        ksession.fireAllRules();
        assertFiredRules(result, "r4");
        System.out.println("---5---");

        ksession.update(fhB, B.b(1));
        ksession.update(fhA, A.a(3));
        ksession.fireAllRules();
        assertFiredRules(result, "r4");
        System.out.println("---6---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---7---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---8---");

        ksession.dispose();
    }

    private void assertFiredRules(List<String> result, String... ruleNames) {
        assertThat(result).containsExactlyInAnyOrder(ruleNames);
        result.clear();
    }

    @Test
    public void test2() {
        String str = "package org.drools.mvel.compiler.test\n" +
                     "\n" +
                     "import " + A.class.getCanonicalName() + "\n" +
                     "import " + B.class.getCanonicalName() + "\n" +
                     "\n" +
                     "global java.util.List result;\n" +
                     "rule r1 \n" +
                     "    when \n" +
                     "        $a : A( object == 1 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n" +
                     "rule r2 \n" +
                     "    when \n" +
                     "        $a : A( object == 2 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n " +
                     "rule r3 \n" +
                     "    when \n" +
                     "        $a : A( object == 2 )\n" +
                     "        $b : B( )\n" +
                     "    then \n" +
                     "        System.out.println( $a + \" : \" + $b  );" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "        modify($a) { setObject(3) };\n" +
                     "end \n " +
                     "rule r4 \n" +
                     "    when \n" +
                     "        $a : A( object == 3 )\n" +
                     "    then \n" +
                     "        System.out.println( $a ); \n" +
                     "        result.add(kcontext.getRule().getName());\n" +
                     "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        InternalFactHandle fhB = (InternalFactHandle) ksession.insert(B.b(1));
        InternalFactHandle fhA = (InternalFactHandle) ksession.insert(A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---1---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---2---");

        ksession.insert(B.b(2));
        ksession.insert(B.b(3));
        ksession.update(fhA, A.a(2));
        ksession.fireAllRules();
        assertFiredRules(result, "r2", "r3", "r4");
        System.out.println("---3---");

        ksession.update(fhA, A.a(2));
        ksession.fireAllRules();
        assertFiredRules(result, "r2", "r3", "r4");
        System.out.println("---4---");

        ksession.update(fhA, A.a(3));
        ksession.fireAllRules();
        assertFiredRules(result, "r4");
        System.out.println("---5---");

        ksession.update(fhB, B.b(1));
        ksession.update(fhA, A.a(3));
        ksession.fireAllRules();
        assertFiredRules(result, "r4");
        System.out.println("---6---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---7---");

        ksession.update(fhA, A.a(1));
        ksession.fireAllRules();
        assertFiredRules(result, "r1");
        System.out.println("---8---");

        ksession.dispose();
    }
}
