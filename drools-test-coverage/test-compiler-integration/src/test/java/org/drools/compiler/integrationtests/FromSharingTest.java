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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class FromSharingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FromSharingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testSharingFromWithoutHashCodeEquals() {
        // DROOLS-2557
        final String drl =
                "import " + PersonWithoutHashCodeEquals.class.getCanonicalName() + "\n" +
                        "import " + AddressWithoutHashCodeEquals.class.getCanonicalName() + "\n" +
                        "rule R1 when\n" +
                        "  $p: PersonWithoutHashCodeEquals( age == 30 );\n" +
                        "  $a: AddressWithoutHashCodeEquals( street == null ) from $p.addresses;\n" +
                        "then\n" +
                        "  System.out.println( \"R1 : \" + $a.getStreet() );\n" +
                        "  $a.setStreet( \"MyStreet#1\" );" +
                        "  update( $p );" +
                        "end\n" +

                        "rule R2 when\n" +
                        "  $p: PersonWithoutHashCodeEquals( age == 30 );\n" +
                        "  $a: AddressWithoutHashCodeEquals( street == null ) from $p.addresses;\n" +
                        "then\n" +
                        "  System.out.println( \"R2 : \" + $a.getStreet() );\n" +
                        "  $a.setStreet( \"MyStreet#2\" );" +
                        "  update( $p );" +
                        "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-sharing-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kieBase.newKieSession();
        try {
            final AddressWithoutHashCodeEquals a = new AddressWithoutHashCodeEquals();
            a.setStreet(null);
            final PersonWithoutHashCodeEquals p = new PersonWithoutHashCodeEquals();
            p.setName("John");
            p.setAge(30);
            p.getAddresses().add(a);
            kieSession.insert(p);
            assertThat(kieSession.fireAllRules()).isEqualTo(1);
        } finally {
            kieSession.dispose();
        }
    }

    public class AddressWithoutHashCodeEquals {

        private String street;

        public AddressWithoutHashCodeEquals() {
        }

        public AddressWithoutHashCodeEquals(final String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(final String street) {
            this.street = street;
        }
    }

    public static class PersonWithoutHashCodeEquals {

        private String name;
        private int age;

        private AddressWithoutHashCodeEquals address;

        private List<AddressWithoutHashCodeEquals> addresses = new ArrayList<AddressWithoutHashCodeEquals>();
        private Map<Object, AddressWithoutHashCodeEquals> namedAddresses = new HashMap<Object, AddressWithoutHashCodeEquals>(0);

        public PersonWithoutHashCodeEquals() {
        }

        public PersonWithoutHashCodeEquals(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        public AddressWithoutHashCodeEquals getAddress() {
            return address;
        }

        public void setAddress(final AddressWithoutHashCodeEquals address) {
            this.address = address;
        }

        public List<AddressWithoutHashCodeEquals> getAddresses() {
            return addresses;
        }

        public List getAddressesNoGenerics() {
            return addresses;
        }

        public void setAddresses(final List<AddressWithoutHashCodeEquals> addresses) {
            this.addresses = addresses;
        }

        public void addAddress(final AddressWithoutHashCodeEquals address) {
            this.addresses.add(address);
        }

        public Map<Object, AddressWithoutHashCodeEquals> getNamedAddresses() {
            return namedAddresses;
        }

        public void setNamedAddresses(final Map<Object, AddressWithoutHashCodeEquals> namedAddresses) {
            this.namedAddresses = namedAddresses;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public int getDoubleAge() {
            return this.age * 2;
        }

        public void setAge(final int age) {
            this.age = age;
        }

        public Integer getAgeAsInteger() {
            return this.age;
        }

        public String toString() {
            return "[Person name='" + this.name + " age='" + this.age + "']";
        }
    }

    @Test
    public void testFromSharingWithPropReactvityOnItsConstraint() {
        // DROOLS-3606
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule \"R1\"\n" +
                "    when\n" +
                "        $p : Person()\n" +
                "        Person( age > 10 ) from $p \n" +
                "    then\n" +
                "        modify ($p) {\n" +
                "          setAge(5);\n" +
                "        }\n" +
                "        System.out.println( \"R1, \" + $p);\n" +
                "end\n" +
                "\n" +
                "rule \"R2\"\n" +
                "    when\n" +
                "        $p : Person()\n" +
                "        Person( age > 10 ) from $p \n" +
                "    then\n" +
                "        System.out.println( \"R2, \" + $p);\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("from-sharing-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person ("John", 20));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
