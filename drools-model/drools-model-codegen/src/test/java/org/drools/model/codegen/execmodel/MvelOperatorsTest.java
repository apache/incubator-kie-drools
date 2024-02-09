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
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MvelOperatorsTest extends BaseModelTest {

    public MvelOperatorsTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testIn() {
        String str =
            "rule R when\n" +
            "    String(this in (\"a\", \"b\"))" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "b" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testStr() {
        String str =
            "rule R when\n" +
            "    String(this str[startsWith] \"M\")" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "Mario" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testStrNot() {
        String str =
            "rule R when\n" +
            "    String(this not str[startsWith] \"M\")" +
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "Mario" );
        ksession.insert( "Luca" );
        ksession.insert( "Edoardo" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testStrHalf() {
        String str =
            "rule R when\n" +
            "    String(this str[startsWith] \"M\" || str[endsWith] \"a\" || str[length] 10)"+
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "Mario" );
        ksession.insert( "Luca" );
        ksession.insert( "Edoardo" );
        ksession.insert( "Valentina" );
        assertThat(ksession.fireAllRules()).isEqualTo(3);
    }

    @Test
    public void testStrHalfOrAndAmpersand() {
        String str =
            "rule R when\n" +
            "    String(this str[startsWith] \"M\" || str[endsWith] \"a\" && str[length] 4)"+
            "then\n" +
            "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "Mario" );
        ksession.insert( "Luca" );
        ksession.insert( "Edoardo" );
        ksession.insert( "Valentina" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testRange() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( age > 30 && <= 40, $name : name )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mario", 44));
        ksession.insert(new Person("Mark", 40));
        ksession.insert(new Person("Edson", 31));
        ksession.insert(new Person("Luca", 30));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.containsAll(asList("Mark", "Edson"))).isTrue();
    }

    @Test
    public void testExcludedRange() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( age <= 30 || > 40, $name : name )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mario", 44));
        ksession.insert(new Person("Mark", 40));
        ksession.insert(new Person("Edson", 31));
        ksession.insert(new Person("Luca", 30));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.containsAll(asList("Luca", "Mario"))).isTrue();
    }

    @Test
    public void testBinding() {
        String str =
            "import " + Person.class.getCanonicalName() + "\n" +
            "global java.util.List list\n" +
            "rule R when\n" +
            "    Person( $name : name in (\"Mario\", \"Mark\"))" +
            "then\n" +
            "    list.add($name);" +
            "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("Mario");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testMatches() {
        String str =
                "rule R when\n" +
                "    String(this matches \"\\\\w\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( "b" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testExcludes() {
        String str =
                "import java.util.List\n" +
                "rule R when\n" +
                "    List(this excludes \"test\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        ksession.insert( asList("ciao", "test") );
        assertThat(ksession.fireAllRules()).isEqualTo(0);
        ksession.insert( asList("hello", "world") );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNotContains() {
        String str =
                "import java.util.List\n" +
                "rule R when\n" +
                "    List(this not contains \"test\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);
        ksession.insert( asList("ciao", "test") );
        assertThat(ksession.fireAllRules()).isEqualTo(0);
        ksession.insert( asList("hello", "world") );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testStartsWithWithChar() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R\n" +
                "when\n" +
                "  $p : Person(name.startsWith('L'))\n" +
                "then\n" +
                "    list.add($p.getName());" +
                "end";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Luca", 35));
        ksession.insert(new Person("Mario", 45));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Luca");
    }

    @Test
    public void testNotIn() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name, age not in (40, 37))" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mark", 40));
        ksession.insert(new Person("Mario", 44));
        ksession.insert(new Person("Edson", 37));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testNotInUsingShort() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name, ageAsShort not in (40, 37))" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mark", 40));
        ksession.insert(new Person("Mario", 44));
        ksession.insert(new Person("Edson", 37));
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testMatchesWithFunction() {
        // DROOLS-4382
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "function String addStar(String s) { return s + \"*\"; }\n" +
                "rule R when\n" +
                "    Person(name matches addStar(likes))" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        Person p = new Person("Mark", 40);
        p.setLikes( "M." );
        ksession.insert(p);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testMatchesOnNullString() {
        // DROOLS-4525
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person(name matches \"^[0-9]{3}.*$\")\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  $p : Person(likes matches \"^[0-9]{3}.*$\")\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        Person first = new Person("686878");
        ksession.insert(first);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class DoubleFact {

        private double primitiveDoubleVal;
        private Double doubleVal;

        public double getPrimitiveDoubleVal() {
            return primitiveDoubleVal;
        }
        public void setPrimitiveDoubleVal(double primitiveDoubleVal) {
            this.primitiveDoubleVal = primitiveDoubleVal;
        }
        public Double getDoubleVal() {
            return doubleVal;
        }
        public void setDoubleVal(Double doubleVal) {
            this.doubleVal = doubleVal;
        }

        @Override
        public String toString() {
            return "DoubleFact [primitiveDoubleVal=" + primitiveDoubleVal + ", doubleVal=" + doubleVal + "]";
        }
    }

    @Test
    public void testInDouble() {
        // DROOLS-4892
        String str =
                "import " + DoubleFact.class.getCanonicalName() + ";" +
                "rule \"Double nnn\" when\n" +
                "	f : DoubleFact( doubleVal in ( 100, 200, 300 ) )\n" +
                "then\n" +
                "	System.out.println(\"Rule[\"+kcontext.getRule().getName()+\"] fires.\");\n" +
                "end\n" +
                "\n" +
                "rule \"Double nnn.n\" when\n" +
                "    f : DoubleFact( doubleVal in ( 100.0, 200.0, 300.0) )\n" +
                "then\n" +
                "	 System.out.println(\"Rule[\"+kcontext.getRule().getName()+\"] fires.\");\n" +
                "end\n" +
                "\n" +
                "rule \"double nnn\" when\n" +
                "    f : DoubleFact( primitiveDoubleVal in ( 100, 200, 300) ) \n" +
                "then \n" +
                "	 System.out.println(\"Rule[\"+kcontext.getRule().getName()+\"] fires.\");\n" +
                "end\n" +
                "\n" +
                "rule \"double nnn.n\" when\n" +
                "    f : DoubleFact( primitiveDoubleVal in ( 100.0, 200.0, 300.0) )\n" +
                "then\n" +
                "    System.out.println(\"Rule[\"+kcontext.getRule().getName()+\"] fires.\");\n" +
                "end";

        KieSession ksession = getKieSession(str);

        DoubleFact f = new DoubleFact();
        f.setDoubleVal(new Double(100));
        f.setPrimitiveDoubleVal(200);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(4);
        ksession.dispose();
    }

    public static class ListContainer {
        private final List<Integer> intList;

        public ListContainer() {
            this(null);
        }

        public ListContainer( List<Integer> intList ) {
            this.intList = intList;
        }

        public List<Integer> getIntList() {
            return intList;
        }
    }

    @Test
    public void testContainsOnNull() {
        // DROOLS-5315
        String str =
                "import " + ListContainer.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    ListContainer(intList contains 3)" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( new ListContainer() );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession.insert( new ListContainer( Collections.singletonList( 3 ) ) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNumericStringsWithLeadingZero() {
        // DROOLS-5926
        String str =
                "rule R when\n" +
                "    Integer(this == \"0800\")" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( 800 );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNumericHexadecimal() {
        // DROOLS-5926
        String str =
                "rule R when\n" +
                "    Integer(this == 0x800)" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.insert( 2048 );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testListLiteralCreation() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List result;" +
                "rule R when\n" +
                "    Person( $myList : [\"aaa\", \"bbb\", \"ccc\"] )" +
                "then\n" +
                "    result.add($myList);" +
                "end ";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);
        ksession.insert( new Person() );
        ksession.fireAllRules();

        Object obj = result.get(0);
        assertThat(obj instanceof List).isTrue();
        assertThat((List)obj).containsExactlyInAnyOrder("aaa", "bbb", "ccc");
    }

    @Test
    public void testMapLiteralCreation() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List result;" +
                     "rule R when\n" +
                     "    Person( $myMap : [\"key\" : \"value\"] )" +
                     "then\n" +
                     "    result.add($myMap);" +
                     "end ";

        KieSession ksession = getKieSession(str);
        List<Object> result = new ArrayList<>();
        ksession.setGlobal("result", result);
        ksession.insert(new Person());
        ksession.fireAllRules();

        Object obj = result.get(0);
        assertThat(obj instanceof Map).isTrue();
        assertThat(((Map) obj).get("key")).isEqualTo("value");
    }

    @Test
    public void testEmptySingleApexString() {
        // DROOLS-6057
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R when\n" +
                "    Person( $name : name == '' )" +
                "then\n" +
                "    list.add($name);" +
                "end ";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Person person1 = new Person("");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("");
    }

    @Test
    public void testContainsOnString() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Person( name contains \"test\" )" +
                "then\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        Person person1 = new Person("");
        ksession.insert(new Person("mario", 47));
        ksession.insert(new Person("atesta", 47));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testContainsOnMapShouldntCompile() {
        // BAPL-1957
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Person( itemsString contains \"test\" )" +
                "then\n" +
                "end ";

        Results results = createKieBuilder( str ).getResults();
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testContainsOnIntShouldntCompile() {
        // BAPL-1957
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    Person( age contains \"test\" )" +
                "then\n" +
                "end ";

        Results results = createKieBuilder( str ).getResults();
        assertThat(results.getMessages(Message.Level.ERROR).isEmpty()).isFalse();
    }
}
