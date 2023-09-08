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

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PolymorphismTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PolymorphismTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Test
    public void testModifySubclassOverWindow() {
        // DROOLS-1501
        String drl = "declare Number @role( event ) end\n" +
                     "declare Integer @role( event ) end\n" +
                     "\n" +
                     "rule R1 no-loop when\n" +
                     "    $i: Integer()\n" +
                     "then\n" +
                     "    update($i);\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "    $n: Number() over window:length(1)\n" +
                     "then\n" +
                     "end";

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kieBase.newKieSession();

        ksession.insert(1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
    }

    @Test
    public void testModifySubclass() {
        // DROOLS-1501
        String drl = "import " + A.class.getCanonicalName() + "\n;" +
                     "import " + B.class.getCanonicalName() + "\n;" +
                     "import " + C.class.getCanonicalName() + "\n;" +
                     "import " + X.class.getCanonicalName() + "\n;" +
                     "\n" +
                     "rule Ra when\n" +
                     "    $a: A(id == 3)\n" +
                     "then\n" +
                     "    delete($a);\n" +
                     "end\n" +
                     "rule Rb when\n" +
                     "    $a: A(id == 2)\n" +
                     "    $b: B($id : id == 2)\n" +
                     "then\n" +
                     "    modify($b) { setId($id+1) };\n" +
                     "end\n" +
                     "rule Rc when\n" +
                     "    $a: A(id == 1)\n" +
                     "    $c: C($id : id == 1)\n" +
                     "then\n" +
                     "    modify($c) { setId($id+1) };\n" +
                     "end\n" +
                     "rule Rd when\n" +
                     "    $a: A(id == 0)\n" +
                     "    $d: X($id : id == 0)\n" +
                     "then\n" +
                     "    modify($d) { setId($id+1) };\n" +
                     "end";

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kieBase.newKieSession();

        ksession.insert( new X(0) );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().size()).isEqualTo(0);
    }

    @Test
    public void testModifySubclass2() {
        // DROOLS-1501
        String drl = "import " + A.class.getCanonicalName() + "\n;" +
                     "import " + B.class.getCanonicalName() + "\n;" +
                     "import " + C.class.getCanonicalName() + "\n;" +
                     "import " + X.class.getCanonicalName() + "\n;" +
                     "\n" +
                     "rule Rd when\n" +
                     "    $a: X(id == 0)\n" +
                     "    $d: C($id : id == 0)\n" +
                     "then\n" +
                     "    modify($d) { setId($id+1) };\n" +
                     "end\n" +
                     "rule Rc when\n" +
                     "    $a: X(id == 1)\n" +
                     "    $c: B($id : id == 1)\n" +
                     "then\n" +
                     "    modify($c) { setId($id+1) };\n" +
                     "end\n" +
                     "rule Rb when\n" +
                     "    $a: X(id == 2)\n" +
                     "    $b: A($id : id == 2)\n" +
                     "then\n" +
                     "    modify($b) { setId($id+1) };\n" +
                     "end\n" +
                     "rule Ra when\n" +
                     "    $a: X(id == 3)\n" +
                     "then\n" +
                     "    delete($a);\n" +
                     "end";

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kieBase.newKieSession();

        FactHandle fh = ksession.insert( new X(0) );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().size()).isEqualTo(0);
        System.out.println(fh);
    }

    public static class A {
        private int id;

        public A( int id ) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId( int id ) {
            this.id = id;
        }
    }

    public static class B extends A {
        public B( int id ) {
            super( id );
        }
    }

    public static class C extends B {
        public C( int id ) {
            super( id );
        }
    }

    public static class X extends C {
        public X( int id ) {
            super( id );
        }
    }
}
