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

import org.drools.mvel.compiler.Person;
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
public class AlphaNodeTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaNodeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testAlpha() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testSharedAlpha() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testBeta() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testSharedAlphaWithBeta() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testAlphaModify() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\")}" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAlphaDelete() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  delete($p);" +
                "end\n"+
                "rule R2 when\n" +
                "  $p : Person()\n" +
                "then\n" +
                "  delete($p);" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testAlphaModifyDelete() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\")}" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "then\n" +
                "  delete($p);" +
                "end\n" +
                "rule R3 when\n" +
                "  not( Person() )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        assertThat(ksession.fireAllRules()).isEqualTo(3);
    }

    @Test
    public void testBetaModifyWithAlpha() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\") }" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mario" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testAlphaModifyWithBeta() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setName(\"Mark\") }" +
                "end\n" +
                "rule R2 when\n" +
                "  $p : Person(name == \"Mark\")\n" +
                "  $s : String(this == $p.name)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario" ) );
        ksession.insert( "Mark" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void test3Alpha() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge(2) }" +
                "  modify($p) { setAge(2) }" +
                "end\n" +
                "rule R3 when\n" +
                "  $p : Person(age > 1)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Person( "Mario", 0 ) );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
