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
package org.drools.compiler.integrationtests.drl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.drl.parser.DrlParser;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.State;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class NestingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NestingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testNesting() throws Exception {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "\n" +
                "dialect \"mvel\"\n" +
                "\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"test something\"\n" +
                "\n" +
                "    when\n" +
                "        p: Person( name==\"Michael\",\n" +
                "                                (addresses[1].street == \"Low\" &&\n" +
                "                                addresses[0].street == \"High\"  )\n" +
                "                                )\n" +
                "    then\n" +
                "        p.name = \"goober\";\n" +
                "        System.out.println(p.name);\n" +
                "        insert(new Address(\"Latona\"));\n" +
                "end";

        final Person p = new Person();
        p.setName("Michael");

        final Address add1 = new Address();
        add1.setStreet("High");

        final Address add2 = new Address();
        add2.setStreet("Low");

        final List<Address> l = new ArrayList<>();
        l.add(add1);
        l.add(add2);

        p.setAddresses(l);

        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr desc = parser.parse(new StringReader(drl));
        final List packageAttrs = desc.getAttributes();
        assertThat(desc.getRules().size()).isEqualTo(1);
        assertThat(packageAttrs.size()).isEqualTo(1);

        final RuleDescr rule = desc.getRules().get(0);
        final Map<String, AttributeDescr> ruleAttrs = rule.getAttributes();
        assertThat(ruleAttrs.size()).isEqualTo(1);

        assertThat(ruleAttrs.get("dialect").getValue()).isEqualTo("mvel");
        assertThat(ruleAttrs.get("dialect").getName()).isEqualTo("dialect");

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("nesting-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(p);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNestedConditionalElements() {

        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + State.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "\n" +
                "rule \"test nested CEs\"  salience 100\n" +
                "    when\n" +
                "        not ( State( $state : state ) and\n" +
                "              not( Person( name == $state, $likes : likes ) and\n" +
                "                   Cheese( type == $likes ) ) )\n" +
                "    then\n" +
                "        results.add(\"OK1\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("nesting-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("results", list);

            final State state = new State("SP");
            ksession.insert(state);

            final Person bob = new Person(state.getState());
            bob.setLikes("stilton");
            ksession.insert(bob);

            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(0);

            ksession.insert(new Cheese(bob.getLikes(), 10));
            ksession.fireAllRules();

            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

}
