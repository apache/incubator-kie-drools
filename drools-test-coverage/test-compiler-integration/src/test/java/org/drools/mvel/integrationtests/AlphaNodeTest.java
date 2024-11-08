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

import java.util.stream.Stream;

import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class AlphaNodeTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAlpha(KieBaseTestConfiguration kieBaseTestConfiguration) {

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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testSharedAlpha(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testBeta(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testSharedAlphaWithBeta(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAlphaModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAlphaDelete(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAlphaModifyDelete(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testBetaModifyWithAlpha(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAlphaModifyWithBeta(KieBaseTestConfiguration kieBaseTestConfiguration) {
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

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void test3Alpha(KieBaseTestConfiguration kieBaseTestConfiguration) {
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
