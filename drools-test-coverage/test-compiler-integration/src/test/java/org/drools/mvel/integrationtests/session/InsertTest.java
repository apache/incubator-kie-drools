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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Move;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonFinal;
import org.drools.mvel.compiler.Pet;
import org.drools.mvel.compiler.Win;
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
public class InsertTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InsertTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testInsert() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "import org.drools.mvel.compiler.Person\n";
        drl += "import org.drools.mvel.compiler.Pet\n";
        drl += "import java.util.ArrayList\n";
        drl += "global java.util.List list\n";
        drl += "rule test\n";
        drl += "when\n";
        drl += "$person:Person()\n";
        drl += "$pets : ArrayList()\n";
        drl += "   from collect( \n";
        drl += "      Pet(\n";
        drl += "         ownerName == $person.name\n";
        drl += "      )\n";
        drl += "   )\n";
        drl += "then\n";
        drl += "  list.add( $person );\n";
        drl += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p = new Person("Toni");
        ksession.insert(p);
        ksession.insert(new Pet("Toni"));

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isSameAs(p);
    }

    @Test
    public void testInsertionOrder() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_InsertionOrder.drl");
        KieSession ksession = kbase.newKieSession();
        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);
        ksession.insert(new Move(1, 2));
        ksession.insert(new Move(2, 3));

        final Win win2 = new Win(2);
        final Win win3 = new Win(3);

        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(win2)).isTrue();
        assertThat(results.contains(win3)).isTrue();

        ksession.dispose();
        ksession = kbase.newKieSession();
        results = new ArrayList<>();
        ksession.setGlobal("results", results);
        // reverse the order of the inserts
        ksession.insert(new Move(2, 3));
        ksession.insert(new Move(1, 2));

        ksession.fireAllRules();
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains(win2)).isTrue();
        assertThat(results.contains(win3)).isTrue();
    }

    @Test
    public void testInsertFinalClassInstance() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_FinalClass.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final PersonFinal bob = new PersonFinal();
        bob.setName("bob");
        bob.setStatus(null);

        ksession.insert(bob);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }
}
