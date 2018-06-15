/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class FromSharingTest {

    @Test
    public void testSharingFromWithoutHashCodeEquals() {
        // DROOLS-2557
        String str =
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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL).build().newKieSession();

        List<String> list = new ArrayList<>();

        AddressWithoutHashCodeEquals a = new AddressWithoutHashCodeEquals();
        a.setStreet(null);
        PersonWithoutHashCodeEquals p = new PersonWithoutHashCodeEquals();
        p.setName("John");
        p.setAge(30);
        p.getAddresses().add(a);

        ksession.insert(p);

        int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }

    public class AddressWithoutHashCodeEquals {

        private String street;

        public AddressWithoutHashCodeEquals() { }

        public AddressWithoutHashCodeEquals(String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public static class PersonWithoutHashCodeEquals {

        private String name;
        private int age;

        private AddressWithoutHashCodeEquals address;

        private List<AddressWithoutHashCodeEquals> addresses = new ArrayList<AddressWithoutHashCodeEquals>();
        private Map<Object, AddressWithoutHashCodeEquals> namedAddresses = new HashMap<Object, AddressWithoutHashCodeEquals>(0);

        public PersonWithoutHashCodeEquals() { }

        public PersonWithoutHashCodeEquals(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public AddressWithoutHashCodeEquals getAddress() {
            return address;
        }

        public void setAddress(AddressWithoutHashCodeEquals address) {
            this.address = address;
        }

        public List<AddressWithoutHashCodeEquals> getAddresses() {
            return addresses;
        }

        public List getAddressesNoGenerics() {
            return addresses;
        }

        public void setAddresses(List<AddressWithoutHashCodeEquals> addresses) {
            this.addresses = addresses;
        }

        public void addAddress(AddressWithoutHashCodeEquals address) {
            this.addresses.add(address);
        }

        public Map<Object, AddressWithoutHashCodeEquals> getNamedAddresses() {
            return namedAddresses;
        }

        public void setNamedAddresses(Map<Object, AddressWithoutHashCodeEquals> namedAddresses) {
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
}
