package org.drools.integrationtests;


import org.drools.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;

public class InlineCastTest extends CommonTestMethodBase {

    @Test
    public void testSuperclass() {
        String drl = "package org.drools.integrationtests\n"
                     + "import org.drools.integrationtests.InlineCastTest.Person\n"
                     + "import org.drools.integrationtests.InlineCastTest.Address\n"
                     + "import org.drools.integrationtests.InlineCastTest.ExtendedAddress\n"
                     + "rule R1\n"
                     + "    when\n"
                     + "        Person( address#ExtendedAddress.country str[startsWith] \"United\" )\n"
                     + "    then\n"
                     + "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try {
            ksession.insert(new Person(new Address("someStreet", "someCity")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "United Kingdom")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "Czech Republic")));

            Assert.assertEquals("wrong number of rules fired", 1, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    @Ignore
    public void testMatchesOperator() {
        String drl = "package org.drools.integrationtests\n"
                     + "import org.drools.integrationtests.InlineCastTest.Person\n"
                     + "import org.drools.integrationtests.InlineCastTest.Address\n"
                     + "import org.drools.integrationtests.InlineCastTest.ExtendedAddress\n"
                     + "rule R1\n"
                     + "    when\n"
                     + "        Person( address#ExtendedAddress.country matches \"[Uu]nited.*\" )\n"
                     + "    then\n"
                     + "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();


        Address address = null;
        boolean x = address instanceof ExtendedAddress && ((ExtendedAddress)address).country.matches( "[Uu]nited.*" );

        try {
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "United States")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "united kingdom")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "Czech Republic")));

            Assert.assertTrue("someStreet".matches("[sS]ome.*"));
            Assert.assertEquals("wrong number of rules fired", 2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testGroupedAccess() {
        String drl = "package org.drools.integrationtests\n"
                     + "import org.drools.integrationtests.InlineCastTest.Person\n"
                     + "import org.drools.integrationtests.InlineCastTest.Address\n"
                     + "import org.drools.integrationtests.InlineCastTest.ExtendedAddress\n"
                     + "rule R1\n"
                     + "    when\n"
                     + "        Person( address#ExtendedAddress.(country == \"United States\" || country == \"United Kingdom\", street == \"someStreet\") )\n"
                     + "    then\n"
                     + "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try {
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "United States")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "United Kingdom")));
            ksession.insert(new Person(new ExtendedAddress("someStreet", "someCity", "Czech Republic")));

            Assert.assertTrue("someStreet".matches("[sS]ome.*"));
            Assert.assertEquals("wrong number of rules fired", 2, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    public static class Person {
        private Address address;

        public Person() {
        }

        public Person(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address {
        String street;
        String city;

        public Address() {
        }

        public Address(String street, String city) {
            this.street = street;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }
        public void setStreet(String street) {
            this.street = street;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class ExtendedAddress extends Address {
        String country;

        public ExtendedAddress() {
        }

        public ExtendedAddress(String street, String city, String country) {
            super(street, city);
            this.country = country;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
