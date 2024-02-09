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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
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
public class MatchesTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MatchesTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testMatchesMVEL() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import java.util.Map;\n" +
                "global java.util.List results;\n" +
                "rule \"Matches mvel\"\n" +
                "when\n" +
                "    Map( this[\"content\"] matches \"hello ;=\" )\n" +
                "then\n" +
                "    results.add( \"OK\" );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("matches-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List results = new ArrayList();
            session.setGlobal("results", results);

            final Map<String, String> map = new HashMap<>();
            map.put("content", "hello ;=");
            session.insert(map);

            session.fireAllRules();

            assertThat(results.size()).isEqualTo(1);
        } finally {
            session.dispose();
        }
    }

    private String getMatchesDRL() {
        return "package org.drools.compiler.integrationtests.operators;\n" +
                "import java.util.Map;\n" +
                "rule \"Matches mvel\"\n" +
                "when\n" +
                "    Map( this[\"content\"] matches \".*\\\\..*\\\\(.*\" )\n" +
                "then\n" +
                "    // succeeded\n" +
                "end\n" +
                "rule \"Matches mvel 2\"\n" +
                "when\n" +
                "    Map( this[\"content\"] matches \"[^\\\\.]*\\\\(.*\" )\n" +
                "then\n" +
                "    // succeeded\n" +
                "end\n" +
                "rule \"Matches mvel 3\"\n" +
                "when\n" +
                "    Map( this[\"content\"] matches \"(?i).*(ROUTINE).*\" )\n" +
                "then\n" +
                "    // succeeded\n" +
                "end";
    }

    @Test
    public void testMatchesMVEL2() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("matches-test",
                                                                         kieBaseTestConfiguration,
                                                                         getMatchesDRL());
        final KieSession ksession = kbase.newKieSession();
        try {
            final Map<String, String> map = new HashMap<>();
            map.put("content", "String with . and (routine)");
            ksession.insert(map);
            final int fired = ksession.fireAllRules();

            assertThat(fired).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMatchesMVEL3() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("matches-test",
                                                                         kieBaseTestConfiguration,
                                                                         getMatchesDRL());
        final KieSession ksession = kbase.newKieSession();
        try {
            final Map<String, String> map = new HashMap<>();
            map.put("content", "String with . and ()");
            ksession.insert(map);
            final int fired = ksession.fireAllRules();

            assertThat(fired).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMatchesNotMatchesCheese() {

        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Cheese matches stilton\"\n" +
                "    salience 10\n" +
                "    when\n" +
                "        stilton : Cheese( type matches \"[Ss]tilto[^0-9]\" )\n" +
                "    then\n" +
                "        list.add( stilton );\n" +
                "end   \n" +
                "\n" +
                "rule \"Cheese not matches\"\n" +
                "    when\n" +
                "        brie : Cheese( type not matches \"(stil.*|mu\\\\w*|brie\\\\d|aged.*|.*prov.*)\" )\n" +
                "    then\n" +
                "        list.add( brie );\n" +
                "end   \n" +
                "\n" +
                "rule \"Cheese matches with space\"\n" +
                "    salience -10\n" +
                "    when\n" +
                "        stilton : Cheese( type matches \"aged stilton\" )\n" +
                "    then\n" +
                "        list.add( stilton );\n" +
                "end   \n" +
                "\n" +
                "rule \"Cheese matches with ^ and escaped s\"\n" +
                "    salience -20\n" +
                "    when\n" +
                "        prov : Cheese( type matches \"^provolone\\\\s*\" )\n" +
                "    then\n" +
                "        list.add( prov );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("matches-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Cheese stilton = new Cheese("stilton", 12);
            final Cheese stilton2 = new Cheese("stilton2", 12);
            final Cheese agedStilton = new Cheese("aged stilton", 12);
            final Cheese brie = new Cheese("brie", 10);
            final Cheese brie2 = new Cheese("brie2", 10);
            final Cheese muzzarella = new Cheese("muzzarella", 10);
            final Cheese muzzarella2 = new Cheese("muzzarella2", 10);
            final Cheese provolone = new Cheese("provolone", 10);
            final Cheese provolone2 = new Cheese("another cheese (provolone)", 10);

            ksession.insert(stilton);
            ksession.insert(stilton2);
            ksession.insert(agedStilton);
            ksession.insert(brie);
            ksession.insert(brie2);
            ksession.insert(muzzarella);
            ksession.insert(muzzarella2);
            ksession.insert(provolone);
            ksession.insert(provolone2);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(4);

            assertThat(list.get(0)).isEqualTo(stilton);
            assertThat(list.get(1)).isEqualTo(brie);
            assertThat(list.get(2)).isEqualTo(agedStilton);
            assertThat(list.get(3)).isEqualTo(provolone);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNotMatchesSucceeds() {
        // JBRULES-2914: Rule misfires due to "not matches" not working
        testMatchesSuccessFail("-..x..xrwx", 0);
    }

    @Test
    public void testNotMatchesFails() {
        // JBRULES-2914: Rule misfires due to "not matches" not working
        testMatchesSuccessFail("d..x..xrwx", 1);
    }

    private void testMatchesSuccessFail(final String personName, final int expectedFireCount) {
        final String drl = "package org.drools.compiler.integrationtests.operators;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule NotMatches\n" +
                "when\n" +
                "    Person( name == null || (name != null && name not matches \"-.{2}x.*\" ) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("matches-test",
                                                                         kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person p = new Person(personName);
            ksession.insert(p);

            final int rules = ksession.fireAllRules();
            assertThat(rules).isEqualTo(expectedFireCount);
        } finally {
            ksession.dispose();
        }
    }
}
