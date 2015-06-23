/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.xpath;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XpathTest {

    @Test
    public void testClassSimplestXpath() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Adult( $child: /children )\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Man bob = new Man("Bob", 40);
        bob.addChild(new Child("Charles", 12));
        bob.addChild(new Child("Debbie", 8));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("Charles"));
        assertTrue(list.contains("Debbie"));
    }

    @Test
    public void testClassTwoLevelXpath() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(3, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));
        assertTrue(list.contains("doll"));
    }

    @Test
    public void testInvalidXpath() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife.children/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertTrue(results.hasMessages(Message.Level.ERROR));
    }

    @Test
    public void testClassFullXpathNotation() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(3, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));
        assertTrue(list.contains("doll"));
    }

    @Test
    public void testBindList() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toys: /wife/children.toys )\n" +
                "then\n" +
                "  list.add( $toys.size() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
    }

    @Test
    public void testBindListWithConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toys: /wife/children[age > 10].toys )\n" +
                "then\n" +
                "  list.add( $toys.size() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(2, (int) list.get(0));
    }

    @Test
    public void testClassTwoLevelXpathWithAlphaConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[age > 10, name.length > 5]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charlie", 12);
        alice.addChild(charlie);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(debbie);
        Child eric = new Child("Eric", 15);
        alice.addChild(eric);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));
        eric.addToy(new Toy("bike"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));
    }

    @Test
    public void testClassTwoLevelXpathWithBetaConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  Man( $toy: /wife/children[age > 10, name.length > $i]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charlie", 12);
        alice.addChild(charlie);
        Child debbie = new Child("Debbie", 8);
        alice.addChild(debbie);
        Child eric = new Child("Eric", 15);
        alice.addChild(eric);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));
        eric.addToy(new Toy("bike"));

        ksession.insert(5);
        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));
    }

    @Test
    public void testReactiveOnLia() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));

        list.clear();
        debbie.setAge(11);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("doll"));
    }

    @Test
    public void testReactiveOnBeta() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  Man( $toy: /wife/children[age > $i]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(10);
        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));

        list.clear();
        debbie.setAge(11);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("doll"));
    }

    @Test
    public void testReactive2Rules() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List toyList\n" +
                "global java.util.List teenagers\n" +
                "\n" +
                "rule R1 when\n" +
                "  $i : Integer()\n" +
                "  Man( $toy: /wife/children[age >= $i]/toys )\n" +
                "then\n" +
                "  toyList.add( $toy.getName() );\n" +
                "end\n" +
                "rule R2 when\n" +
                "  School( $child: /children[age >= 13] )\n" +
                "then\n" +
                "  teenagers.add( $child.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> toyList = new ArrayList<String>();
        ksession.setGlobal("toyList", toyList);
        List<String> teenagers = new ArrayList<String>();
        ksession.setGlobal("teenagers", teenagers);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 15);
        Child debbie = new Child("Debbie", 12);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        School school = new School("Da Vinci");
        school.addChild(charlie);
        school.addChild(debbie);

        ksession.insert(13);
        ksession.insert(bob);
        ksession.insert(school);
        ksession.fireAllRules();

        assertEquals(2, toyList.size());
        assertTrue(toyList.contains("car"));
        assertTrue(toyList.contains("ball"));

        assertEquals(1, teenagers.size());
        assertTrue(teenagers.contains("Charles"));

        toyList.clear();
        debbie.setAge(13);
        ksession.fireAllRules();

        assertEquals(1, toyList.size());
        assertTrue(toyList.contains("doll"));

        assertEquals(2, teenagers.size());
        assertTrue(teenagers.contains("Charles"));
        assertTrue(teenagers.contains("Debbie"));
    }

    @Test
    public void testInlineCast() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[ #BabyGirl ]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        BabyBoy charlie = new BabyBoy("Charles", 12);
        BabyGirl debbie = new BabyGirl("Debbie", 8);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("doll"));
    }

    @Test
    public void testInlineCastWithConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( name == \"Bob\", $name: /wife/children[ #BabyGirl, favoriteDollName.startsWith(\"A\") ].name )\n" +
                "then\n" +
                "  list.add( $name );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        BabyBoy charlie = new BabyBoy("Charles", 12);
        BabyGirl debbie = new BabyGirl("Debbie", 8, "Anna");
        BabyGirl elisabeth = new BabyGirl("Elisabeth", 5, "Zoe");
        BabyGirl farrah = new BabyGirl("Farrah", 3, "Agatha");
        alice.addChild(charlie);
        alice.addChild(debbie);
        alice.addChild(elisabeth);
        alice.addChild(farrah);

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("Debbie"));
        assertTrue(list.contains("Farrah"));
    }

    @Test
    public void testReactiveList() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        Woman alice = new Woman("Alice", 38);
        Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        Child charlie = new Child("Charles", 12);
        Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));

        ksession.insert(bob);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("car"));
        assertTrue(list.contains("ball"));

        list.clear();
        charlie.addToy(new Toy("gun"));
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("gun"));
    }
}
