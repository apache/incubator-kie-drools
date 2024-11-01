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
package org.drools.mvel.integrationtests.session;

import java.util.stream.Stream;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cat;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Primitives;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldAccessTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil2.getKieBaseCloudConfigurations(false).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    // this isn't possible, we can only narrow with type safety, not widen.
    // unless typesafe=false is used
    public void testAccessFieldsFromSubClass(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String rule = "";
        rule += "package org.drools.compiler;\n";
        rule += "import org.drools.mvel.compiler.Person;\n";
        rule += "import org.drools.mvel.compiler.Pet;\n";
        rule += "import org.drools.mvel.compiler.Cat;\n";
        rule += "declare Person @typesafe(false) end\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    Person(\n";
        rule += "      pet.breed == \"Siamise\"\n";
        rule += "    )\n";
        rule += "then\n";
        rule += "    System.out.println(\"hello person\");\n";
        rule += "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession session = kbase.newKieSession();

        final Person person = new Person();
        person.setPet(new Cat("Mittens"));
        session.insert(person);
        session.fireAllRules();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testAccessClassTypeField(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String str = "package org.drools.mvel.compiler\n" +
                "rule r1\n" +
                "when\n" +
                "    Primitives( classAttr == null )" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Primitives());
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testGenericsOption(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // JBRULES-3579
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   $c : Cheese( $type: type )\n" +
                "   $p : Person( $name : name, addressOption.get.street == $type )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final Person p = new Person("x");
        p.setAddress(new Address("x", "x", "x"));
        ksession.insert(p);

        ksession.insert(new Cheese("x"));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

}
