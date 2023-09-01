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
package org.drools.mvel.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class InlineCastTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public InlineCastTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testInlineCast() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address#LongAddress.country == \"uk\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastWithBinding() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", $country : address#LongAddress.country == \"uk\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastOnlyBinding() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", $country : address#LongAddress.country )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastWithFQN() throws Exception {
        String str = "import org.drools.mvel.compiler.Person;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address#org.drools.mvel.compiler.LongAddress.country == \"uk\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastOnRightOperand() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   $person : Person( )\n" +
                "   String( this == $person.address#LongAddress.country )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);
        ksession.insert("uk");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastOnRightOperandWithFQN() throws Exception {
        String str = "import org.drools.mvel.compiler.Person;\n" +
                "rule R1 when\n" +
                "   $person : Person( )\n" +
                "   String( this == $person.address#org.drools.mvel.compiler.LongAddress.country )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);
        ksession.insert("uk");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInferredCast() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", address instanceof LongAddress, address.country == \"uk\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineTypeCast() throws Exception {
        // DROOLS-136
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " Person( name == \"mark\", address#LongAddress )\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastWithNestedAccces() throws Exception {
        // DROOLS-127
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     "   Person( name == \"mark\", address#LongAddress.country.length == 2 )\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new Address());
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastWithNestedAcccesAndNullSafeDereferencing() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " Person( name == \"mark\", address#LongAddress.country!.length == 2 )\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new LongAddress( null ) );
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testInlineCastWithNestedAcccesAndNullSafeDereferencing2() throws Exception {
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " Person( " +
                     " name == \"mark\", " +
                     " name == \"john\" || address#LongAddress.country!.length == 2 )\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        Person mark2 = new Person("mark");
        ksession.insert(mark2);

        Person mark3 = new Person("mark");
        mark3.setAddress(new LongAddress( null ) );
        ksession.insert(mark3);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testSuperclass() {
        String drl = "package org.drools.mvel.compiler.integrationtests\n"
                     + "import org.drools.mvel.compiler.*;\n"
                     + "rule R1\n"
                     + " when\n"
                     + " Person( address#LongAddress.country str[startsWith] \"United\" )\n"
                     + " then\n"
                     + "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            Person mark1 = new Person("mark");
            mark1.setAddress(new Address());
            ksession.insert(mark1);

            Person mark2 = new Person("mark");
            mark2.setAddress(new LongAddress("United Kingdom"));
            ksession.insert(mark2);

            Person mark3 = new Person("mark");
            mark3.setAddress(new LongAddress("Czech Republic"));
            ksession.insert(mark3);

            assertThat(ksession.fireAllRules()).as("wrong number of rules fired").isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testGroupedAccess() {
        String drl = "package org.drools.mvel.compiler.integrationtests\n"
                     + "import org.drools.mvel.compiler.*;\n"
                     + "rule R1\n"
                     + " when\n"
                     + " Person( address#LongAddress.(country == \"United States\" || country == \"United Kingdom\") )\n"
                     + " then\n"
                     + "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            Person mark1 = new Person("mark");
            mark1.setAddress(new LongAddress("United States"));
            ksession.insert(mark1);

            Person mark2 = new Person("mark");
            mark2.setAddress(new LongAddress("United Kingdom"));
            ksession.insert(mark2);

            Person mark3 = new Person("mark");
            mark3.setAddress(new LongAddress("Czech Republic"));
            ksession.insert(mark3);

            assertThat(ksession.fireAllRules()).as("wrong number of rules fired").isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMatchesOperator() {
        // BZ-971008
        String drl = "package org.drools.mvel.compiler.integrationtests\n"
                     + "import org.drools.mvel.compiler.*;\n"
                     + "rule R1\n"
                     + " when\n"
                     + " Person( address#LongAddress.country matches \"[Uu]nited.*\" )\n"
                     + " then\n"
                     + "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            Person mark1 = new Person("mark");
            mark1.setAddress(new LongAddress("United States"));
            ksession.insert(mark1);

            Person mark2 = new Person("mark");
            mark2.setAddress(new LongAddress("United Kingdom"));
            ksession.insert(mark2);

            Person mark3 = new Person("mark");
            mark3.setAddress(new LongAddress("Czech Republic"));
            ksession.insert(mark3);

            assertThat(ksession.fireAllRules()).as("wrong number of rules fired").isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testInlineCastWithThis() {
        String drl = "package org.drools.mvel.compiler.integrationtests "
                     + "import org.drools.compiler.*; "
                     + "rule R1 "
                     + " when "
                     + " Object( this#String matches \"[Uu]nited.*\" ) "
                     + " then "
                     + "end ";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert( "United States" );
            ksession.insert( "United Kingdom" );
            ksession.insert( "Italy" );
            assertThat(ksession.fireAllRules()).as("wrong number of rules fired").isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }
    
    @Test
    public void testInlineCastWithFQNAndMethodInvocation() throws Exception {
        // DROOLS-1337
        String str =
                "import org.drools.mvel.compiler.Person;\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "   Person( name == \"mark\", $x : address#org.drools.mvel.compiler.LongAddress.country.substring(1) )\n" +
                "then\n" +
                "   list.add($x);" +
                "end\n";
 
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
 
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
 
        Person mark1 = new Person("mark");
        mark1.setAddress(new LongAddress("uk"));
        ksession.insert(mark1);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("k");
 
        ksession.dispose();
    }
}
