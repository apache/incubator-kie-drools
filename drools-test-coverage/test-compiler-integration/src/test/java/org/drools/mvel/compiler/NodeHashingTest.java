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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

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
public class NodeHashingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NodeHashingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testNodeHashTypeMismatch() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( status == 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( status == 2 )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setStatus( "1" );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( status == 1 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( status == 2 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( status == 3 )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setStatus( "1" );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigInteger() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"1\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"2\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setBigInteger( new BigInteger( "1" ) );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"1\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"2\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigInteger == \"3\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setBigInteger( new BigInteger( "1" ) );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"2.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setBigDecimal( new BigDecimal( "1.00" ) );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"2.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == \"3.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setBigDecimal( new BigDecimal( "1.00" ) );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchFromBigDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 1 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 2 ) )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setAge( 1 );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 1 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 2 ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( age == new BigDecimal( 3 ) )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setAge( 1 );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithPrimitiveDouble() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == 1.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == 2.0 )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setAge( 1 );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( age == 1.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( age == 2.0 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( age == 3.0 )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setAge( 1 );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testNodeHashTypeMismatchWithBigIntegerAndDecimal() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigInteger.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"1\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"2\" ) )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        Person p1 = new Person();
        p1.setBigDecimal( new BigDecimal( 1 ) );
        ksession1.insert( p1 );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + Person.class.getCanonicalName() + ";\n" +
                      "import " + BigInteger.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"1\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"2\" ) )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    Person( bigDecimal == new BigInteger( \"3\" ) )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        Person p2 = new Person();
        p2.setBigDecimal( new BigDecimal( 1 ) );
        ksession2.insert( p2 );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    public static class DoubleValue {
        private final Double value;

        public DoubleValue( Double value ) {
            this.value = value;
        }

        public Double getValue() {
            return value;
        }
    }

    @Test
    public void testNodeHashTypeMismatchWithDouble() throws Exception {
        // BZ-1328380

        // 2 rules -- Mvel coercion
        String drl1 = "import " + DoubleValue.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"2.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase1 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession1 = kbase1.newKieSession();

        ksession1.insert( new DoubleValue(1.00) );

        assertThat(ksession1.fireAllRules()).isEqualTo(1);
        ksession1.dispose();

        // 3 rules -- Node Hashing
        String drl2 = "import " + DoubleValue.class.getCanonicalName() + ";\n" +
                      "rule \"rule1\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"1.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule2\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"2.00\" )\n" +
                      "then\n" +
                      "end\n" +
                      "rule \"rule3\"\n" +
                      "when\n" +
                      "    DoubleValue( value == \"3.00\" )\n" +
                      "then\n" +
                      "end\n";

        KieBase kbase2 = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl2);
        KieSession ksession2 = kbase2.newKieSession();

        ksession2.insert( new DoubleValue(1.00) );

        assertThat(ksession2.fireAllRules()).isEqualTo(1);
        ksession2.dispose();
    }

    @Test
    public void testHashingOnClassConstraint() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "    A( configClass == String.class );\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    A( configClass == String.class );\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    A( configClass == String.class );\n" +
                "then\n" +
                "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession kieSession = kbase.newKieSession();

        kieSession.insert( new A( ) );

        assertThat(kieSession.fireAllRules()).isEqualTo(3);
    }

    public static class A {
        public Class<?> getConfigClass() {
            return String.class;
        }
    }
}
