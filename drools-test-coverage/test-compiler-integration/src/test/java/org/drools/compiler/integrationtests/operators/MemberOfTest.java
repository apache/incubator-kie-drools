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
package org.drools.compiler.integrationtests.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Pet;
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
public class MemberOfTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MemberOfTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMemberOfAndNotMemberOf() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Stilton is memberOf Cheesery\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        Cheesery( $cheeses : cheeses )\n" +
                "        cheese : Cheese( type memberOf $cheeses )\n" +
                "    then\n" +
                "        list.add( cheese );\n" +
                "end   \n" +
                "\n" +
                "rule \"Muzzarela is not memberOf Cheesery\"\n" +
                "    when\n" +
                "        Cheesery( $cheeses : cheeses )\n" +
                "        cheese : Cheese( type not memberOf $cheeses )\n" +
                "    then\n" +
                "        list.add( cheese );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("member-of-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Cheese stilton = new Cheese("stilton", 12);
            final Cheese muzzarela = new Cheese("muzzarela", 10);
            final Cheese brie = new Cheese("brie", 15);
            ksession.insert(stilton);
            ksession.insert(muzzarela);

            final Cheesery cheesery = new Cheesery();
            cheesery.getCheeses().add(stilton);
            cheesery.getCheeses().add(brie);
            ksession.insert(cheesery);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(2);

            assertThat(list.get(0)).isEqualTo(stilton);
            assertThat(list.get(1)).isEqualTo(muzzarela);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMemberOfWithOr() {

        final String drl =
            "package org.drools.compiler.integrationtests.operators;\n" +
            "import java.util.ArrayList;\n" +
            "import " + Person.class.getCanonicalName() + ";\n" +
            "rule \"Test Rule\"\n" +
            "when\n" +
            "    $list: ArrayList()                                   \n" +
            "    ArrayList()                                          \n" +
            "            from collect(                                \n" +
            "                  Person(                                \n" +
            "                      (                                  \n" +
            "                          pet memberOf $list             \n" +
            "                      ) || (                             \n" +
            "                          pet == null                    \n" +
            "                      )                                  \n" +
            "                  )                                      \n" +
            "            )\n" +
            "then\n" +
            "  System.out.println(\"hello person\");\n" +
            "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("member-of-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final Person toni = new Person("Toni", 12);
            toni.setPet(new Pet(Pet.PetType.CAT));

            session.insert(new ArrayList());
            session.insert(toni);

            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }
}
