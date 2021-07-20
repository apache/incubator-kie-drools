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

package org.drools.modelcompiler;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.FunctionsTest.Pojo;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Pet;
import org.drools.modelcompiler.domain.PetPerson;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.ToysStore;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FromTest extends BaseModelTest {

    public FromTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testFromGlobal() throws Exception {
        String str = "global java.util.List list         \n" +
                     "rule R when                        \n" +
                     "  $o : String(length > 3) from list\n" +
                     "then                               \n" +
                     "  insert($o);                      \n" +
                     "end                                ";

        KieSession ksession = getKieSession(str);

        List<String> strings = Arrays.asList("a", "Hello World!", "xyz");

        ksession.setGlobal("list", strings);

        assertEquals( 1, ksession.fireAllRules() );

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertFalse(results.contains("a"));
        assertTrue(results.contains("Hello World!"));
        assertFalse(results.contains("xyz"));
    }

    @Test
    public void testFromVariable() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " Man( $children : wife.children )\n" +
                " $child: Child( age > 10 ) from $children\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles");
    }

    @Test
    public void testFromExpression() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " Man( $wife : wife )\n" +
                " $child: Child( age > 10 ) from $wife.children\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles");
    }

    @Test @Ignore
    public void testModifyWithFrom() {
        // DROOLS-6486
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "\n" +
                "rule R when\n" +
                " Man( $wife : wife )\n" +
                " $child: Child( age > 10 ) from $wife.children\n" +
                "then\n" +
                "  modify( $child ) { setName($child.getName() + \"x\") };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.insert( charlie ); // object has to be in the session in order to be modified, but it's retrieved with a FromNode
        ksession.fireAllRules();

        assertEquals("Charlesx", charlie.getName());
    }

    public static Integer getLength(String ignoredParameter, String s, Integer offset) {
        return s.length() + offset;
    }

    @Test
    public void testFromExternalFunction() {
        final String str =
                "import " + FromTest.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $s : String()\n" +
                        "  $i : Integer( this > 10 ) from FromTest.getLength(\"ignoredArgument\", $s, 0)\n" +
                        "then\n" +
                        "  list.add( \"received long message: \" + $s);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert("Hello World!");
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("received long message: Hello World!");
    }

    @Test
    public void testFromExternalFunctionMultipleBindingArguments() {
        final String str =
                "import " + FromTest.class.getCanonicalName() + ";\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $s : String()\n" +
                        "  $n : Integer()\n" +
                        "  $i : Integer( this >= 10 ) from FromTest.getLength(\"ignoredArgument\", $s, $n)\n" +
                        "then\n" +
                        "  list.add( \"received long message: \" + $s);\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert("Hello!");
        ksession.insert(4);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("received long message: Hello!");
    }

    @Test
    public void testFromConstant() {
        String str =
                "package org.drools.compiler.test  \n" +
                "global java.util.List list\n" +
                "rule R\n" +
                "when\n" +
                "    $s : String() from \"test\"\n" +
                "then\n" +
                "   list.add( $s );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( "test", list.get(0) );
    }

    @Test
    public void testFromConstructor() {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "rule R\n" +
                "when\n" +
                "    $s : String()\n" +
                "    $i : Integer()\n" +
                "    $p : Person() from new Person($s, $i)\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert( "Mario" );
        ksession.insert( 44 );

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( new Person("Mario", 44), list.get(0) );
    }

    @Test
    public void testFromMapValues() {
        // DROOLS-3661
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + PetPerson.class.getCanonicalName() + "\n" +
                "import " + Pet.class.getCanonicalName() + "\n" +
                "rule R\n" +
                "when\n" +
                "    $p : PetPerson ( )\n" +
                "    $pet : Pet ( type == Pet.PetType.dog ) from $p.getPets().values()\n\n" +
                "then\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        PetPerson petPerson = new PetPerson( "me" );
        Map<String, Pet> petMap = new HashMap<>();
        petMap.put("Dog", new Pet( Pet.PetType.dog ));
        petMap.put("Cat", new Pet( Pet.PetType.cat ));
        petPerson.setPets( petMap );

        ksession.insert( petPerson );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testGlobalInFromExpression() {
        // DROOLS-4999
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + PetPerson.class.getCanonicalName() + "\n" +
                "import " + Pet.class.getCanonicalName() + "\n" +
                "global String petName;\n" +
                "rule R\n" +
                "when\n" +
                "    $p : PetPerson ( )\n" +
                "    $pet : Pet ( type == Pet.PetType.dog ) from $p.getPet(petName)\n" +
                "then\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal( "petName", "Dog" );

        PetPerson petPerson = new PetPerson( "me" );
        Map<String, Pet> petMap = new HashMap<>();
        petMap.put("Dog", new Pet( Pet.PetType.dog ));
        petMap.put("Cat", new Pet( Pet.PetType.cat ));
        petPerson.setPets( petMap );

        ksession.insert( petPerson );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testLiteralFrom() {
        // DROOLS-5217
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $i: Integer() from [1,3]\n" +
                "    Pojo(intList.contains($i))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,2,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 2, rulesFired );
    }

    @Test
    public void testLiteralFrom2() {
        // DROOLS-5217
        String str =
                "package com.sample\n" +
                "import " + Pojo.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $boundList: java.util.List() from [[1,3]]\n" +
                "    Pojo(intList.containsAll($boundList))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Pojo( Arrays.asList(1,2,3) ) );
        int rulesFired = ksession.fireAllRules();
        assertEquals( 1, rulesFired );
    }

    @Test
    public void testFromCollect() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "import " + List.class.getCanonicalName() + "\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $l : List (size == 2) from collect (Person (age >= 30))\n" +
                     "then\n" +
                     "end \n";

        KieSession ksession = getKieSession(str);

        Person p1 = new Person("John", 32);
        Person p2 = new Person("Paul", 30);
        Person p3 = new Person("George", 29);

        ksession.insert(p1);
        ksession.insert(p2);
        ksession.insert(p3);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testThisArray() {
        // This test verifies the behavior when ArrayType is used as "_this" (which $childrenA is converted to) in from clause.
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + Adult.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    Adult($childrenA : childrenA)\n" +
                     "    $i : Integer() from $childrenA.length\n" +
                     "then\n" +
                     "    list.add($i);\n" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Adult john = new Adult("John", 39);
        john.setChildrenA(new Person[]{new Person("Julian"), new Person("Sean")});

        ksession.insert(john);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder(2);
    }

    @Test
    public void testFromArray() {
        // This test verifies the behavior when the return type is ArrayType
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + Adult.class.getCanonicalName() + "\n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $adult : Adult()\n" +
                     "    $p : Person() from $adult.childrenA\n" +
                     "then\n" +
                     "    list.add($p.getName());\n" +
                     "end \n";
        
        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Adult john = new Adult("John", 39);
        john.setChildrenA(new Person[]{new Person("Julian"), new Person("Sean")});

        ksession.insert(john);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Julian", "Sean");
    }

    @Test
    public void testInnerClassCollection() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + MyPerson.class.getCanonicalName() + "\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $p : MyPerson()\n" +
                     "    $kid : MyPerson() from $p.kids\n" +
                     "then\n" +
                     "end \n";

        KieSession ksession = getKieSession(str);

        MyPerson john = new MyPerson("John");
        Collection<MyPerson> kids = new ArrayList<>();
        kids.add(new MyPerson("Julian"));
        kids.add(new MyPerson("Sean"));
        john.setKids(kids);

        ksession.insert(john);

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testInnerClassWithInstanceMethod() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + MyPerson.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $p : MyPerson()\n" +
                     "    $d : MyPerson() from $p.getDummyPerson()\n" +
                     "then\n" +
                     "    list.add($d.getName());" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        MyPerson john = new MyPerson("John");
        ksession.insert(john);

        assertEquals(1, ksession.fireAllRules());
        Assertions.assertThat(list).containsExactlyInAnyOrder("Dummy");
    }

    @Test
    public void testInnerClassWithStaticMethod() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + MyPerson.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $d : MyPerson() from MyPerson.getDummyPersonStatic()\n" +
                     "then\n" +
                     "    list.add($d.getName());" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        assertEquals(1, ksession.fireAllRules());
        Assertions.assertThat(list).containsExactlyInAnyOrder("Dummy");
    }

    @Test
    public void testInnerClassWithStaticMethodWithArg() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + MyPerson.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $s : String()\n" +
                     "    $d : MyPerson() from MyPerson.getDummyPersonStatic($s)\n" +
                     "then\n" +
                     "    list.add($d.getName());" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert("John");

        assertEquals(1, ksession.fireAllRules());
        Assertions.assertThat(list).containsExactlyInAnyOrder("DummyJohn");
    }

    public static class MyPerson {

        public MyPerson(final String name) {
            this.name = name;
        }

        public MyPerson(final String name, final Integer age, final Collection<MyPerson> kids) {
            this.name = name;
            this.age = age;
            this.kids = kids;
        }

        private String name;

        private Integer age;

        private Collection<MyPerson> kids;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(final Integer age) {
            this.age = age;
        }

        public Collection<MyPerson> getKids() {
            return kids;
        }

        public void setKids(final Collection<MyPerson> kids) {
            this.kids = kids;
        }

        public MyPerson getDummyPerson() {
            return new MyPerson("Dummy");
        }

        public static MyPerson getDummyPersonStatic() {
            return new MyPerson("Dummy");
        }

        public static MyPerson getDummyPersonStatic(String name) {
            return new MyPerson("Dummy" + name);
        }
    }

    @Test
    public void testNew() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    $p : Person() from new Person(\"John\", 30)\n" +
                     "then\n" +
                     "    list.add($p);\n" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals(1, list.size());
    }

    @Test
    public void testFromOr() {
        String str =
                "package org.drools.compiler.test  \n" +
                     "import " + Person.class.getCanonicalName() + "\n" +
                     "import " + Address.class.getCanonicalName() + "\n" +
                     "import " + Toy.class.getCanonicalName() + "\n" +
                     "import " + ToysStore.class.getCanonicalName() + "\n" +
                     "global java.util.List list;\n" +
                     "rule R\n" +
                     "when\n" +
                     "    Person($age : age)\n" +
                     "    Address($c : city)\n" +
                     "    $store : ToysStore(cityName == $c)\n" +
                     "    (or Toy( targetAge == $age ) from $store.firstFloorToys \n" +
                     "               Toy( targetAge == $age ) from $store.secondFloorToys\n" +
                        "        )\n" +
                     "then\n" +
                     "    list.add($store.getStoreName());\n" +
                     "end \n";

        KieSession ksession = getKieSession(str);
        List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(new Person("Leonardo", 3));
        ksession.insert(new Address("Milan"));

        Toy car = new Toy("Car", 3);
        Toy bicycle = new Toy("Bicycle", 3);
        Toy computer = new Toy("Computer", 7);
        ksession.insert(car);
        ksession.insert(bicycle);
        ksession.insert(computer);

        ToysStore ts = new ToysStore( "Milan", "Toystore1");
        ts.getFirstFloorToys().add(car);
        ts.getSecondFloorToys().addAll(Arrays.asList(bicycle, computer));
        ksession.insert(ts);

        ksession.fireAllRules();

        Assertions.assertThat(list).contains("Toystore1");
    }

    public static class Measurement {
        private String id;
        private String val;

        public Measurement(String id, String val) {
            this.id = id;
            this.val = val;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public List<String> getListOfCodes() {
            return Arrays.asList("a", "b", "c");
        }

        public Object getSomethingBy(Object o) {
            return o;
        }
     }

    @Test
    public void testFromFunctionCall() {
        // DROOLS-5548
        String str =
                "package com.sample;" +
                        "global java.util.Set controlSet;\n" +
                        "import " + Measurement.class.getCanonicalName() + ";\n" +
                        "" +
                        "declare A\n" +
                        " x: String\n" +
                        "end\n" +
                        "" +
                        "declare B\n" +
                        " a: A\n" +
                        "end\n" +
                        "" +
                        "function String dummyFunction(A b) {\n" +
                        " return \"test\";\n" +
                        "}\n" +
                        "\n" +
                        "rule \"insertB\"\n" +
                        "when\n" +
                        "then\n" +
                        "drools.insert(new B(new A()));" +
                        "end;" +
                        "rule \"will execute per each Measurement having ID color\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        " Measurement( id == \"color\", $colorVal : val )\n" +
                        " $b: B()\n" +
                        " $val: String() from dummyFunction($b.a)\n" +
                        "then\n" +
                        " controlSet.add($colorVal);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 2, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void testFromMap() {
        // DROOLS-5549
        String str =
                "package com.sample;" +
                        "global java.util.Set controlSet;\n" +
                        "import " + Measurement.class.getCanonicalName() + ";\n" +
                        "import " + Collections.class.getCanonicalName() + ";\n" +
                        "import " + Map.class.getCanonicalName() + ";\n" +
                        "" +
                        "function String dummyFunction(Map m) {\n" +
                        " return \"test\";\n" +
                        "}" +
                        "\n" +
                        "rule \"will execute per each Measurement having ID color\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        " Measurement( id == \"color\", $colorVal : val )\n" +
                        " $val: String() from dummyFunction(Collections.singletonMap($colorVal, \"something\"))\n" +
                        "then\n" +
                        " controlSet.add($colorVal);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void testFromChainedCall() {
        // DROOLS-5608
        String str =
                "package com.sample;" +
                        "global java.util.Set controlSet;\n" +
                        "import " + Measurement.class.getCanonicalName() + ";\n" +
                        "import " + Optional.class.getCanonicalName() + ";\n" +
                        "import java.util.*;\n" +
                        "" +
                        "\n" +
                        "rule \"will execute per each Measurement having ID color\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        " Measurement( id == \"color\", $colorVal : val )\n" +
                        " Object() from Optional.of($colorVal).orElse(\"blah\")\n" +
                        "then\n" +
                        " controlSet.add($colorVal);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    public static class DummyService {
        public String dummy(String a) {
            return "test";
        }
        public String dummy(String a, String b, String c) {
            return "test";
        }

        public String dummy(Object a, Object b) {
            return "test";
        }

        public static <K, V> Map.Entry<K, V> mapEntry(K key, V value) {
            return new AbstractMap.SimpleEntry<K, V>(key, value);
        }
    }

    @Test
    public void testNestedService() {
        // DROOLS-5609
        String str =
                "package com.sample;" +
                "global java.util.Set controlSet;\n" +
                "global " + DummyService.class.getCanonicalName() + " dummyService;\n" +
                "import " + Measurement.class.getCanonicalName() + ";\n" +
                "" +
                "rule \"will execute per each Measurement having ID color\"\n" +
                "no-loop\n" +
                "when\n" +
                " Measurement( id == \"color\", $colorVal : val )\n" +
                " String() from dummyService.dummy(dummyService.dummy($colorVal))\n" +
                "then\n" +
                " controlSet.add($colorVal);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);
        ksession.setGlobal("dummyService", new DummyService());

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }


    @Test
    public void testMultipleFrom() {
        // DROOLS-5542
        String str =
                "package com.sample;" +
                "global java.util.Set controlSet;\n" +
                "global " + DummyService.class.getCanonicalName() + " dummyService;\n" +
                "import " + Measurement.class.getCanonicalName() + ";\n" +
                "" +
                "rule \"will execute per each Measurement having ID color\"\n" +
                "no-loop\n" +
                "when\n" +
                " Measurement( id == \"color\", $colorVal : val )\n" +
                " $var1: String() from dummyService.dummy(\"a\");\n" +
                " $var2: String() from dummyService.dummy(\"b\");\n" +
                " $var3: String() from dummyService.dummy(\"c\");\n" +
                " String() from dummyService.dummy($var1, $var2, $var3)\n" +
                "then\n" +
                " controlSet.add($colorVal);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);
        ksession.setGlobal("dummyService", new DummyService());

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void testMultipleFromFromBinding() {
        // DROOLS-5591
        String str =
                "package com.sample;" +
                "global java.util.Set controlSet;\n" +
                "global " + DummyService.class.getCanonicalName() + " dummyService;\n" +
                "import " + Measurement.class.getCanonicalName() + ";\n" +
                "" +
                "rule \"will execute per each Measurement having ID color\"\n" +
                "no-loop\n" +
                "when\n" +
                " $m : Measurement( id == \"color\", $colorVal : val )\n" +
                " String() from dummyService.dummy($m.getId(), $m.getVal())\n" +
                "then\n" +
                " controlSet.add($colorVal);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);
        ksession.setGlobal("dummyService", new DummyService());

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void testMultipleFromList() {
        // DROOLS-5590
        String str =
                "package com.sample;" +
                "global java.util.Set controlSet;\n" +
                "global " + DummyService.class.getCanonicalName() + " dummyService;\n" +
                "import " + DummyService.class.getCanonicalName() + ";\n" +
                "import " + Measurement.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + Map.class.getCanonicalName() + ";\n" +
                "" +
                "rule \"will execute per each Measurement having ID color\"\n" +
                "no-loop\n" +
                "when\n" +
                " $measurement: Measurement( id == \"color\", $colorVal : val )\n" +
                " $lst : List() from collect(Measurement())\n" +
                " $selectedList: List() from accumulate(Measurement($m: this) from $lst, " +
                        "collectList(DummyService.mapEntry($m, $measurement.getListOfCodes())))\n" +
                "\n" +
                "then\n" +
                " controlSet.add($colorVal);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);
        ksession.setGlobal("dummyService", new DummyService());

        ksession.insert(new Measurement("color", "red"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void tesFromMethodCall() {
        // DROOLS-5641
        String str =
                "package com.sample;" +
                "global java.util.Set controlSet;\n" +
                "global " + DummyService.class.getCanonicalName() + " dummyService;\n" +
                "import " + DummyService.class.getCanonicalName() + ";\n" +
                "import " + Measurement.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "import " + Map.class.getCanonicalName() + ";\n" +
                "" +
                "rule \"test\"\n" +
                "no-loop\n" +
                "when\n" +
                " $m: Measurement( id == \"color\", $colorVal : val )\n" +
                " $p: Person()\n" +
                " String() from dummyService.dummy($m.getSomethingBy($p.age), $p)\n" +
                "then\n" +
                " controlSet.add($colorVal);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        HashSet<Object> hashSet = new HashSet<>();
        ksession.setGlobal("controlSet", hashSet);
        ksession.setGlobal("dummyService", new DummyService());

        ksession.insert(new Measurement("color", "red"));
        ksession.insert(new Person("Luca"));

        int ruleFired = ksession.fireAllRules();

        assertEquals( 1, ruleFired );
        assertEquals( "red", hashSet.iterator().next() );
    }

    @Test
    public void testFromStringConcatenation() {
        // DROOLS-5640
        String str =
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $a : String()\n" +
                "  $b : String()\n" +
                "  $c : String() from $a + $b\n" +
                "then\n" +
                "  list.add($c);\n" +
                "end";

        KieSession ksession = getKieSession( str );
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert( "A" );
        ksession.insert( "B" );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("AA", "AB", "BA", "BB");
    }

    @Test
    public void testFromBoolean() {
        // DROOLS-5830
        String str =
                "rule R when\n" +
                "  $a : String()\n" +
                "  $b : String()\n" +
                "  Boolean(booleanValue) from $a == $b\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "A" );
        assertEquals( 1, ksession.fireAllRules() );
    }
}
