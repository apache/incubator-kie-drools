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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Pet;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.type.Modifies;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PropertyReactivityTest extends BaseModelTest {

    public PropertyReactivityTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testPropertyReactivity() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testPropertyReactivityWithUpdate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "then\n" +
                "    $p.setAge( $p.getAge()+1 );\n" +
                "    update($p);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testPropertyReactivityMvel() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R dialect\"mvel\" when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "then\n" +
                "    modify($p) { age = $p.age+1 };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testPropertyReactivityMvelWithUpdate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R dialect\"mvel\" when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "then\n" +
                "    $p.age = $p.age+1;\n" +
                "    update($p);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testWatch() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( age < 50 ) @watch(!age)\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testWatchAll() {
        // DROOLS-4509

        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" ) @watch(*)\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(10);

        assertEquals(50, p.getAge());
    }

    @Test
    public void testWatchAllBeforeBeta() {
        // DROOLS-4509

        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" ) @watch(*)\n" +
                "    Address() \n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.insert( new Address( "Milan" ) );
        ksession.fireAllRules(10);

        assertEquals(50, p.getAge());
    }

    @Test
    public void testWatchAllBeforeFrom() {
        // DROOLS-4509

        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" ) @watch(*)\n" +
                "    Address() from $p.addresses\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        p.addAddress( new Address( "Milan" ) );
        p.addAddress( new Address( "Rome" ) );
        ksession.insert( p );
        ksession.fireAllRules(10);

        assertEquals(50, p.getAge());
    }

    @Test
    public void testImplicitWatch() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person()\n" +
                "  $p2 : Person(name != \"Mark\", this != $p1, age > $p1.age)\n" +
                "then\n" +
                "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Result result = new Result();
        ksession.insert(result);

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue(null);
        ksession.delete(marioFH);
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge(34);
        ksession.update(markFH, mark, "age");

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testImplicitWatchWithDeclaration() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person( $a : address )\n" +
                "  $p2 : Person(name != \"Mark\", this != $p1, age > $p1.age)\n" +
                "then\n" +
                "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Result result = new Result();
        ksession.insert(result);

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue(null);
        ksession.delete(marioFH);
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge(34);
        ksession.update(markFH, mark, "age");

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testImmutableField() {
        final String str =
                "declare Integer @propertyReactive end\n" +
                "declare Long @propertyReactive end\n" +
                "rule R when\n" +
                "    $i : Integer( intValue > 0 )\n" +
                "    Long( $l : intValue == $i )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( 42 );
        ksession.insert( 42L );
        assertEquals( 1, ksession.fireAllRules() );
    }


    @Test(timeout = 5000L)
    public void testPRAfterAccumulate() {
        // DROOLS-2427
        final String str =
                "import " + Order.class.getCanonicalName() + "\n" +
                "import " + OrderLine.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "        $o: Order($lines: orderLines)\n" +
                "        Number(intValue >= 15) from accumulate(\n" +
                "            OrderLine($q: quantity) from $lines\n" +
                "            , sum($q)\n" +
                "        )\n" +
                "    then\n" +
                "        modify($o) { setPrice(10) }\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Order order = new Order( Arrays.asList(new OrderLine( 9 ), new OrderLine( 8 )), 12 );
        ksession.insert( order );
        ksession.fireAllRules();

        assertEquals( 10, order.getPrice() );
    }

    public static class Order {
        private final List<OrderLine> orderLines;

        private int price;

        public Order( List<OrderLine> orderLines, int price ) {
            this.orderLines = orderLines;
            this.price = price;
        }

        public List<OrderLine> getOrderLines() {
            return orderLines;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice( int price ) {
            this.price = price;
        }
    }

    public static class OrderLine {
        private final int quantity;

        public OrderLine( int quantity ) {
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static class Bean {
        private final List<String> firings = new ArrayList<>();

        public List<String> getFirings() {
            return firings;
        }

        public String getValue() {
            return "Bean";
        }

        public void setValue(String value) {}
    }

    @Test(timeout = 5000L)
    public void testPRWithAddOnList() {
        final String str =
                "import " + Bean.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    $b : Bean( firings not contains \"R\" )\n" +
                "then\n" +
                "    $b.getFirings().add(\"R\");\n" +
                "    update($b);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Bean() );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testPRWithUpdateOnList() {
        final String str =
                "import " + List.class.getCanonicalName() + "\n" +
                "rule R1 when\n" +
                "    $l : List( empty == true )\n" +
                "then\n" +
                "    $l.add(\"test\");\n" +
                "    update($l);\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $l : List( !this.contains(\"test\") )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new ArrayList() );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testPROnAtomic() {
        final String str =
                "import " + AtomicInteger.class.getCanonicalName() + "\n" +
                "rule R2 when\n" +
                "    $i : AtomicInteger( get() < 3 )\n" +
                "then\n" +
                "    $i.incrementAndGet();" +
                "    insert(\"test\" + $i.get());" +
                "    update($i);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert(new AtomicInteger(0));
        assertEquals( 3, ksession.fireAllRules() );
    }

    @Test(timeout = 10000L)
    public void testPropertyReactivityWith2Rules() {
        checkPropertyReactivityWith2Rules( "age == 41\n" );
    }

    @Test(timeout = 10000L)
    public void testPropertyReactivityWith2RulesUsingAccessor() {
        checkPropertyReactivityWith2Rules( "getAge() == 41\n" );
    }

    @Test(timeout = 10000L)
    public void testPropertyReactivityWith2RulesLiteralFirst() {
        checkPropertyReactivityWith2Rules( "41 == age\n" );
    }

    @Test(timeout = 10000L)
    public void testPropertyReactivityWith2RulesLiteralFirstUsingAccessor() {
        checkPropertyReactivityWith2Rules( "41 == getAge()\n" );
    }

    private void checkPropertyReactivityWith2Rules( String constraint ) {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1 when\n" +
                "    $p : Person( age == 40 )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( " + constraint + " )\n" +
                "then\n" +
                "    modify($p) { setEmployed( true ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person( "Mario", 40 );
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals( 41, p.getAge() );
        assertTrue( p.getEmployed() );
    }

    @Test
    public void testReassignment() {
        // DROOLS-4884
        final String str =
                "package com.example\n" +
                "\n" +
                "declare Counter\n" +
                "    value1: int\n" +
                "    value2: int\n" +
                "end\n" +
                "\n" +
                "rule \"Init\" when\n" +
                "    not Counter()\n" +
                "then\n" +
                "    drools.insert(new Counter(0, 0));\n" +
                "end\n" +
                "\n" +
                "rule \"Loop\"\n" +
                "when\n" +
                "    $c: Counter( value1 == 0 )\n" +
                "then\n" +
                "    $c = new Counter(0, 0);\n" +
                "    $c.setValue2(1);\n" +
                "    update($c);\n" +
                "end\n\n";

        KieSession ksession = getKieSession( str );

        assertEquals( 5, ksession.fireAllRules(5) );
    }

    @Test
    public void testReassignment2() {
        // DROOLS-4884
        final String str =
                "package com.example\n" +
                "\n" +
                "declare Counter\n" +
                "    value1: int\n" +
                "    value2: int\n" +
                "end\n" +
                "\n" +
                "rule \"Init\" when\n" +
                "    not Counter()\n" +
                "then\n" +
                "    drools.insert(new Counter(0, 0));\n" +
                "end\n" +
                "\n" +
                "rule \"Loop\"\n" +
                "when\n" +
                "    $c: Counter( value1 == 0 )\n" +
                "then\n" +
                "    $c = new Counter(0, 0);\n" +
                "    $c.setValue1(1);\n" +
                "    update($c);\n" +
                "end\n\n";

        KieSession ksession = getKieSession( str );

        assertEquals( 2, ksession.fireAllRules(5) );
    }

    @Test
    public void testMultipleFieldUpdate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1 when\n" +
                "    $p : Person( name == \"Mario\" )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ), setLikes(\"Cheese\") };\n" +
                "end\n" +
                "rule R2 when\n" +
                "    $p : Person( name == \"Mario\", likes == \"Cheese\" )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        p.setLikes("Beer");
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(42, p.getAge());
    }

    @Test
    public void testComplexSetterArgument() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R \n" +
                "when\n" +
                "    $p: Person(address.street == \"street1\")\n" +
                "then\n" +
                "    modify($p) { setLikes( String.valueOf(($p.getAddress().getStreet() + $p.getAddress().getCity()))) };\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 40 );
        me.setAddress(new Address("street1", 2, "city1"));
        ksession.insert( me );

        assertEquals(1, ksession.fireAllRules(10));

        assertEquals( "street1city1", me.getLikes() );
    }

    @Test
    public void testNestedPropInRHS() throws Exception {
        // Property Reactivity for "owner"
        final String str =
                "package org.drools.test;\n" +
                           "import " + Pet.class.getCanonicalName() + ";\n" +
                           "rule R1\n" +
                           "when \n" +
                           "  $pet : Pet(age == 3)\n" +
                           "then\n" +
                           "  modify ($pet) { getOwner().setLikes(\"Cookie\") };\n" +
                           "end\n" +
                           "rule R2\n" +
                           "when \n" +
                           "  Pet(owner.likes == \"Cookie\")\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        Pet pet = new Pet(Pet.PetType.cat);
        Person person = new Person("John");
        person.setLikes("Meat");
        pet.setOwner(person);
        pet.setAge(3);

        ksession.insert(pet);
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeeplyNestedPropInRHS() throws Exception {
        // Property Reactivity for "owner"
        final String str =
                "package org.drools.test;\n" +
                           "import " + Pet.class.getCanonicalName() + ";\n" +
                           "rule R1\n" +
                           "when \n" +
                           "  $pet : Pet(age == 3)\n" +
                           "then\n" +
                           "  modify ($pet) { getOwner().getAddress().setStreet(\"XYZ street\") };\n" +
                           "end\n" +
                           "rule R2\n" +
                           "when \n" +
                           "  Pet(owner.address.street == \"XYZ street\")\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(str);

        Pet pet = new Pet(Pet.PetType.cat);
        Person person = new Person("John");
        person.setAddress(new Address("ABC street"));
        pet.setOwner(person);
        pet.setAge(3);

        ksession.insert(pet);
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testOutsideModifyBlockWithGetterAsArgument() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    System.out.println(\"name = \" + $p.getName());\n" +
                           "    modify($p) { setAge(41) };\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testOutsideModifyBlockWithNonGetterAsArgument() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    System.out.println(\"name.length = \" + $p.getName().length());\n" +
                           "    modify($p) { setAge(41) };\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testMultipleModifyBlocksWithNonGetterAsArgument() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +

                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "    $a : Address( street == \"Street\" )\n" +
                           "then\n" +
                           "    System.out.println(\"name.length = \" + $p.getName().length());\n" +
                           "    modify($p) { setAge(41) };\n" +
                           "    System.out.println(\"street.length = \" + $a.getStreet().length());\n" +
                           "    modify($a) { setNumber(20) };\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);

        Address a = new Address("Street", 10, "City");
        ksession.insert(a);

        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
        assertEquals(20, a.getNumber());
    }

    @Test
    public void testUpdateWithGetterAsArgument() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    System.out.println(\"name = \" + $p.getName());\n" +
                           "    $p.setAge(41);\n" +
                           "    update($p);\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testUpdateWithNonGetterAsArgument() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    System.out.println(\"name.length = \" + $p.getName().length());\n" +
                           "    $p.setAge(41);\n" +
                           "    update($p);\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testUpdateWithNonGetterAsDeclaration() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    int length = $p.getName().length();\n" +
                           "    System.out.println(\"length = \" + length);\n" +
                           "    $p.setAge(41);\n" +
                           "    update($p);\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testUpdateWithNonGetterIntentinalLoop() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"Mario\" )\n" +
                           "then\n" +
                           "    $p.getName().length();\n" +
                           "    $p.setAge(41);\n" +
                           "    update($p);\n" +
                           "end\n";

        final KieSession ksession = getKieSession(str);

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        // this is not likely an expected loop but standard-drl considers getter+otherMethod modifies the prop "name".
        // anyway, such "read" method is not written like this (= without assigning to a variable or as an argument of other method)
        // This test is to ensure the same behavior on stadard-drl and executable-model.
        assertEquals(10, fired);
        assertEquals(41, p.getAge());
    }

    @Test
    public void testPropertyReactivityOnBoundVariable() {
        // RHDM-1387
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( $n : name, $n == \"Mario\" )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(5);

        assertEquals(41, p.getAge());
    }

    public static int dummy(int i) {
        return i;
    }

    @Test
    public void testWatchCallingExternalMethod() {
        // DROOLS-5514
        final String str =
                "import static " + this.getClass().getCanonicalName() + ".dummy;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( dummy(age) < 50 ) @watch(!*, age)\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(50, p.getAge());
    }

    @Test
    public void testWatchCallingExternalMethod2() {
        // DROOLS-5514
        final String str =
                "import static " + this.getClass().getCanonicalName() + ".dummy;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( dummy(age) < 50 ) @watch(*)\n" +
                "then\n" +
                "    modify($p) { setName( $p.getName()+\"1\" ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals("Mario111", p.getName());
    }

    @Test
    public void testWatchCallingExternalMethod3() {
        // DROOLS-5514
        final String str =
                "import static " + this.getClass().getCanonicalName() + ".dummy;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( dummy(age) < 50 ) @watch(!*, age)\n" +
                "then\n" +
                "    modify($p) { setName( $p.getName()+\"1\" ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals("Mario1", p.getName());
    }

    @Test
    public void test2PropertiesInOneExpression() {
        // DROOLS-5677
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1a\n" +
                "agenda-group \"group1\"\n" +
                "when\n" +
                "    $p : Person( age == 0 )\n" +
                "then\n" +
                "    modify($p) { setAge( 20 ) };\n" +
                "end\n" +
                "rule R1b \n" +
                "agenda-group \"group1\"\n" +
                "when\n" +
                "    $p : Person( salary == 0 )\n" +
                "then\n" +
                "    modify($p) { setSalary( 20 ) };\n" +
                "end\n" +
                "rule R2 \n" +
                "agenda-group \"group2\"\n" +
                "when\n" +
                "    $p : Person( age > salary )\n" +
                "then\n" +
                "    modify($p) { setSalary( 100 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John", 0);
        p.setSalary(0);
        ksession.insert(p);
        ksession.getAgenda().getAgendaGroup("group1").setFocus();
        ksession.fireAllRules();
        ksession.getAgenda().getAgendaGroup("group2").setFocus();
        ksession.fireAllRules();

        assertEquals(20, p.getSalary().intValue()); // R2 should be cancelled
    }

    @Test
    public void test3PropertiesInOneExpression() {
        // DROOLS-5677
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1a\n" +
                "agenda-group \"group1\"\n" +
                "when\n" +
                "    $p : Person( age == 0 )\n" +
                "then\n" +
                "    modify($p) { setAge( 20 ) };\n" +
                "end\n" +
                "rule R1b \n" +
                "agenda-group \"group1\"\n" +
                "when\n" +
                "    $p : Person( salary == 0 )\n" +
                "then\n" +
                "    modify($p) { setSalary( 10 ) };\n" +
                "end\n" +
                "rule R1c \n" +
                "agenda-group \"group1\"\n" +
                "when\n" +
                "    $p : Person( id == 0 )\n" +
                "then\n" +
                "    modify($p) { setId( 10 ) };\n" +
                "end\n" +
                "rule R2 \n" +
                "agenda-group \"group2\"\n" +
                "when\n" +
                "    $p : Person( age > salary + id )\n" +
                "then\n" +
                "    modify($p) { setSalary( 100 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John", 0);
        p.setSalary(0);
        p.setId(0);
        ksession.insert(p);
        ksession.getAgenda().getAgendaGroup("group1").setFocus();
        ksession.fireAllRules();
        ksession.getAgenda().getAgendaGroup("group2").setFocus();
        ksession.fireAllRules();

        assertEquals(10, p.getSalary().intValue()); // R2 should be cancelled
    }

    public static class Fact {
        private int a;
        private String result;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    public static String convertToString(int num) {
        if (num < 1000) {
            return "SMALL";
        }
        return "BIG";
    }

    @Test
    public void testExternalFunction() {
        // BAPL-1773
        final String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                "import static " + PropertyReactivityTest.class.getCanonicalName() + ".*;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $fact: Fact(convertToString(a) == \"BIG\")\n" +
                "then\n" +
                "    modify($fact) { setResult(\"OK\") };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Fact fact = new Fact();
        fact.setA(99999);
        fact.setResult("NG");

        ksession.insert(fact);
        assertEquals( 1, ksession.fireAllRules(3) );
        assertEquals( "OK", fact.getResult() );
    }

    @Test
    public void testExternalFunction2() {
        // BAPL-1773
        final String str =
                "import " + Fact.class.getCanonicalName() + ";\n" +
                "import static " + PropertyReactivityTest.class.getCanonicalName() + ".*;\n" +
                "\n" +
                "rule R1 when\n" +
                "    $fact: Fact(convertToString(a) == \"BIG\")\n" +
                "then\n" +
                "    modify($fact) { setA(99999) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Fact fact = new Fact();
        fact.setA(99999);
        fact.setResult("NG");

        ksession.insert(fact);
        assertEquals( 3, ksession.fireAllRules(3) );
    }

    @Test
    public void testUnwatch() {
        // RHDM-1553
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( age < 50 ) @watch(!*)\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge() + 1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(41, p.getAge());
    }

    @Test
    public void testUnwatchWithFieldBinding() {
        // RHDM-1553
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( $age : age < 50 ) @watch(!*)\n" +
                "then\n" +
                "    modify($p) { setAge( $age + 1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(41, p.getAge());
    }

    @Test
    public void testUnwatchWithFieldBindingAndMvel() {
        // RHDM-1553
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R dialect \"mvel\" when\n" +
                "    $p : Person( $age : age < 50 ) @watch(!*)\n" +
                "then\n" +
                "    modify($p) { age = $age + 1 };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(41, p.getAge());
    }

    @Test
    public void testUnwatchWithWatchedField() {
        // RHDM-1553
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( name == \"Mario\" ) @watch(!*, age)\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge() + 1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(43, p.getAge());
    }

    @Test
    public void testNoConstraint() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(10);

        assertEquals(41, p.getAge());
    }

    @Test
    public void testNoConstraintWithUpdate() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( )\n" +
                "then\n" +
                "    $p.setAge( $p.getAge()+1 );\n" +
                "    update($p);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(10);

        assertEquals(41, p.getAge());
    }

    @Test
    public void testModifiesAnnotation() {
        final String str =
                "import " + Light.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $l : Light( name == \"Alert\")\n" +
                "then\n" +
                "    modify($l) { turnOn() };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Light l = new Light("Alert");
        ksession.insert( l );
        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
    }

    public static class Light {
        private boolean on;
        private String name;

        public Light(String name) {
            this.name = name;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Modifies( { "on" } )
        public void turnOn() {
            setOn(true);
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testSettersInAndOutModifyBlock() {
        // RHDM-1552
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when\n" +
                "    $p : Person( age < 50 )\n" +
                "then\n" +
                "    $p.setAge( $p.getAge() + 1 );\n" +
                "    modify($p) { setName( \"Mario\" ) };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(43, p.getAge());
    }

    @Test
    public void testSettersInAndOutModifyBlockMvel() {
        // RHDM-1552
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R dialect \"mvel\" when\n" +
                "    $p : Person( age < 50 )\n" +
                "then\n" +
                "    $p.age = $p.age + 1;\n" +
                "    modify($p) { name = \"Mario\" };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(43, p.getAge());
    }

    @Test
    public void testMvelModifyBlockWithComma() {
        // RHDM-1552
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R dialect \"mvel\" when\n" +
                "    $p : Person( age < 50 )\n" +
                "then\n" +
                "    modify($p) { setName(\"Mario\"), age = $p.age + 1 };\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules(3);

        assertEquals(43, p.getAge());
    }

    public static class AssessmentContext {

        private Deque<StackFrame> stackFrames;

        public AssessmentContext() {
            stackFrames = new ArrayDeque<StackFrame>(8);
            stackFrames.push(new StackFrame());
        }

        public Long getTopQuestionGroupId() {
            return stackFrames.peek().questionGroupId;
        }

        public int getTopPhase() {
            return stackFrames.peek().phase;
        }

        public void setTopPhase(int i) {
            stackFrames.peek().phase = i;
        }

        public void pushStackFrame(Long id) {
            StackFrame sf = new StackFrame();
            sf.questionGroupId = id;
            sf.phase = 0;
            stackFrames.push(sf);
        }

        public void popStackFrame() {
            if (stackFrames.size() > 1) {
                stackFrames.pop();
            }
        }

        private static class StackFrame {
            private Long questionGroupId = null;
            private int phase = 0;
        }
    }

    @Test
    public void testUpdateNonPropertyInMvel() {
        // DROOLS-6096
        final String str =
                "import " + AssessmentContext.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R1 \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $ac : AssessmentContext(topQuestionGroupId == 51795 , topPhase == 3)\n" +
                "  then\n" +
                "  $ac.popStackFrame();\n" +
                "  update($ac);\n" +
                "end\n" +
                "\n" +
                "rule R2 \n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    $ac : AssessmentContext(topQuestionGroupId == null, topPhase == 0)\n" +
                "  then\n" +
                "    $ac.setTopPhase(2);\n" +
                "    $ac.pushStackFrame(223L);\n" +
                "    update($ac);\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        AssessmentContext ac1 = new AssessmentContext();
        ac1.pushStackFrame(null);
        ac1.pushStackFrame(51795L);
        ac1.setTopPhase(3);
        ksession.insert( ac1 );

        assertEquals(2, ksession.fireAllRules(2));
    }

    @Test
    public void testPropertyReactivityWithPublicField() {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when\n" +
                           "    $p : Person()\n" +
                           "then\n" +
                           "    modify($p) { publicAge = 41 };\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "    $p : Person(name == \"John\")\n" +
                           "then\n" +
                           "end\n" +
                           "rule R3 when\n" +
                           "    $p : Person(publicAge == 41)\n" +
                           "then\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        Person p = new Person("John");
        p.publicAge = 40;
        ksession.insert(p);
        int fired = ksession.fireAllRules(10);

        assertEquals(3, fired);
        assertEquals(41, p.publicAge);
    }

    @Test
    public void testUnknownPropertyNameInWatch() throws Exception {
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "global java.util.List result;\n" +
                           "rule R1 when\n" +
                           "    $p : Person( name == \"John\" ) @watch( ageX )\n" +
                           "then\n" +
                           "    result.add($p.getName());\n" +
                           "    modify($p) { setLikes(\"stilton\") }\n" +
                           "end\n" +
                           "rule R2 when\n" +
                           "    $p : Person(likes == \"stilton\")\n" +
                           "then\n" +
                           "    modify($p) { setAge(20) }\n" +
                           "end\n";

        KieBuilder kbuilder = createKieBuilder(str);
        assertTrue(kbuilder.getResults().hasMessages(Level.ERROR));
    }
}
