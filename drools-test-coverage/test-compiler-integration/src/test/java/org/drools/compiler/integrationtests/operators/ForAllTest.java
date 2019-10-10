/*
 * Copyright 2005 JBoss Inc
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

package org.drools.compiler.integrationtests.operators;

import java.util.Collection;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ForAllTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ForAllTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void test1P1CFiring1() {
        check("age >= 18", 1, new Person("Mario", 45));
    }

    @Test
    public void test1P1CFiring2() {
        check("age == 8 || == 45", 1, new Person("Mario", 45), new Person("Sofia", 8));
    }

    @Test
    public void test1P1CNotFiring() {
        check("age >= 18", 0, new Person("Sofia", 8));
    }

    @Test
    public void test1P2CFiring() {
        check("age >= 18, name.startsWith(\"M\")", 1, new Person("Mario", 45), new Person("Mark", 43));
    }

    @Test
    public void test1P2CNotFiring1() {
        check("age >= 18, name.startsWith(\"M\")", 0, new Person("Mario", 45), new Person("Mark", 43), new Person("Edson", 40));
    }

    @Test
    public void test1P2CNotFiring2() {
        check("age < 18, name.startsWith(\"M\")", 0, new Person("Sofia", 8));
    }

    private void check(String constraints, int expectedFires, Object... objs) {
        final String drl =
                "package org.drools.compiler.integrationtests.operators;\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "\n" +
                        "rule R1 when\n" +
                        "    forall( Person( " + constraints + " ) )\n" +
                        "then\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("forall-test", kieBaseTestConfiguration, drl);

//        ReteDumper.dumpRete( kbase );

        final KieSession ksession = kbase.newKieSession();
        try {

            for (Object obj : objs) {
                ksession.insert( obj );
            }
            assertEquals(expectedFires, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }

    }
}
