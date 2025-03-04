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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class NullSafeDereferencingTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeBinding(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( $streetName : address!.street ) \n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeNullComparison(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( address!.street == null ) \n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeNullComparison2(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " $street : String()\n"+
                     " Person( address!.street == $street ) \n" +
                     "then\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        ksession.insert("Main Street");
        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeWithMethod(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-4095
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " $street : String()\n"+
                     " Person( getAddress()!.street == $street ) \n" +
                     "then\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        ksession.insert("Main Street");
        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeNullComparisonReverse(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-82
        String str =
                "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( \"Main Street\".equalsIgnoreCase(address!.street) )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeNullComparisonReverseComplex(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-82
        String str =
                "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( \"Main\".equalsIgnoreCase(address!.street!.substring(0, address!.street!.indexOf(' '))) )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDoubleNullSafe(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   Person( address!.street!.length > 15 ) \n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        Person alex = new Person("Alex", 34);
        alex.setAddress(new Address("The Main Very Big Street"));
        ksession.insert(alex);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        ksession.dispose();
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMixedNullSafes(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str = "import org.drools.mvel.compiler.*;\n" +
                     "rule R1 when\n" +
                     " $p : Person( " +
                     " address!.street!.length > 0 && ( address!.street!.length < 15 || > 20 && < 30 ) " +
                     " && address!.zipCode!.length > 0 && address.zipCode == \"12345\" " +
                     " ) \n" +
                     "then\n" +
                     " System.out.println( $p ); \n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("Mario", 38));

        Person mark = new Person("Mark", 37);
        mark.setAddress(new Address("Main Street",null,"12345"));
        ksession.insert(mark);

        Person edson = new Person("Edson", 34);
        edson.setAddress(new Address(null));
        ksession.insert(edson);

        Person alex = new Person("Alex", 34);
        alex.setAddress(new Address("The Main Verrry Long Street"));
        ksession.insert(alex);

        Person frank = new Person("Frank", 24);
        frank.setAddress(new Address("Long Street number 21",null,"12345"));
        ksession.insert(frank);

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeMemberOf(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // DROOLS-50
        String str =
                "declare A\n" +
                "    list : java.util.List\n" +
                "end\n" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "    insert( new A( java.util.Arrays.asList( \"test\" ) ) );" +
                "    insert( \"test\" );" +
                "end\n" +
                "rule R when\n" +
                "    $a : A()\n" +
                "    $s : String( this memberOf $a!.list )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        ksession.dispose();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNullSafeInnerConstraint(KieBaseTestConfiguration kieBaseTestConfiguration) {
        String str =
                "declare Content\n" +
                " complexContent : Content\n" +
                " extension : Content\n" +
                "end\n" +
                "\n" +
                "declare Context\n" +
                " ctx : Content\n" +
                "end\n" +
                "\n" +
                "rule \"Complex Type Attribute\"\n" +
                "when\n" +
                " $con : Content()\n" +
                " Context( ctx == $con || ctx == $con!.complexContent!.extension )\n" +
                "then\n" +
                " System.out.println( $con ); \n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                " Content ext = new Content();\n" +
                " Content complex = new Content( new Content( null, ext ), null );\n" +
                " Content complex2 = new Content( null, null );\n" +
                " Context ctx = new Context( ext );\n" +
                " Context ctx2 = new Context( complex2 );\n" +
                " insert( complex );\n" +
                " insert( complex2 );\n" +
                " insert( ctx );\n" +
                " insert( ctx2 );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        assertThat(ksession.fireAllRules()).isEqualTo(3);
        ksession.dispose();
    }

   @ParameterizedTest(name = "KieBase type={0}")
   @MethodSource("parameters")
   public void testNullSafeNestedAccessors(KieBaseTestConfiguration kieBaseTestConfiguration) {
      String str = "package org.drools.test; " +
                   "import " + Person.class.getName() + "; " +
                   "global java.util.List list; " +
                   "rule R1 when " +
                   " $street : String() "+
                   " Person( address!.( street == $street, $zip : zipCode ) ) " +
                   "then " +
                   " list.add( $zip ); " +
                   "end";

      KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
      KieSession ksession = kbase.newKieSession();
      List list = new ArrayList();
      ksession.setGlobal( "list", list );
      
      ksession.insert(new Person("Mario", 38));
      Person mark = new Person("Mark", 37);
      mark.setAddress(new Address("Main Street", "", "123456"));
      ksession.insert(mark);

      ksession.insert("Main Street");

       assertThat(ksession.fireAllRules()).isEqualTo(1);
      ksession.dispose();
       assertThat(list).isEqualTo(List.of("123456"));
   }

}
