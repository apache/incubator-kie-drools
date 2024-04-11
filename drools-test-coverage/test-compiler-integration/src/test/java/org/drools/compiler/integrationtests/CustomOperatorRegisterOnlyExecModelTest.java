/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.drl.parser.impl.Operator;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class CustomOperatorRegisterOnlyExecModelTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CustomOperatorRegisterOnlyExecModelTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        // Only exec-model
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN});
        return parameters;
    }

    @Test
    public void testCustomOperatorUsingCollections() {
        String constraints =
                "    $alice : Person(name == \"Alice\")\n" +
                        "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n";
        customOperatorUsingCollections(constraints);
    }

    @Test
    public void testCustomOperatorUsingCollectionsNot() {
        String constraints =
                "    $alice : Person(name == \"Alice\")\n" +
                        "    $bob : Person(name == \"Bob\", $alice.addresses not supersetOf this.addresses)\n";
        customOperatorUsingCollections(constraints);
    }

    @Test
    public void testCustomOperatorUsingCollectionsInverted() {
        // DROOLS-6983
        String constraints =
                "    $bob : Person(name == \"Bob\")\n" +
                        "    $alice : Person(name == \"Alice\", $bob.addresses supersetOf this.addresses)\n";
        customOperatorUsingCollections(constraints);
    }

    private void customOperatorUsingCollections(String constraints) {
        final String drl =
                "import " + Address.class.getCanonicalName() + ";\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        constraints +
                        "then\n" +
                        "end\n";

        // Register!
        Operator.addOperatorToRegistry("supersetOf", false);
        Operator.addOperatorToRegistry("supersetOf", true);
        org.drools.model.functions.Operator.Register.register(SupersetOfOperator.INSTANCE);

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person alice = new Person("Alice", 30);
            alice.addAddress(new Address("Large Street", "BigTown", "12345"));
            final Person bob = new Person("Bob", 30);
            bob.addAddress(new Address("Large Street", "BigTown", "12345"));
            bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

            ksession.insert(alice);
            ksession.insert(bob);

            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testCustomOperatorOnKieModule() {
        final String drl = "import " + Address.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    $alice : Person(name == \"Alice\")\n" +
                "    $bob : Person(name == \"Bob\", addresses supersetOf $alice.addresses)\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("custom-operator-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Person alice = new Person("Alice", 30);
            alice.addAddress(new Address("Large Street", "BigTown", "12345"));
            final Person bob = new Person("Bob", 30);
            bob.addAddress(new Address("Large Street", "BigTown", "12345"));
            bob.addAddress(new Address("Long Street", "SmallTown", "54321"));

            ksession.insert(alice);
            ksession.insert(bob);

            assertThat(ksession.fireAllRules()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    public static class SupersetOfOperator implements org.drools.model.functions.Operator.SingleValue<Object, Object> {

        public static final SupersetOfOperator INSTANCE = new SupersetOfOperator();

        @Override
        public boolean eval(Object a, Object b) {
            return ((Collection<?>) a).containsAll((Collection<?>) b); // need better error handling in real use cases
        }

        @Override
        public String getOperatorName() {
            return "supersetOf";
        }

        @Override
        public boolean requiresCoercion() {
            return true;
        }
    }
}
