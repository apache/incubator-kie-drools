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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
