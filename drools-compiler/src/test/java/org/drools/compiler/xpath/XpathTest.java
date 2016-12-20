/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.Iterator;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XpathTest {
    public static final Logger LOG = LoggerFactory.getLogger(XpathTest.class);
    
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "Charles" ) );
        assertTrue( list.contains( "Debbie" ) );
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
        assertTrue( list.contains( "doll" ) );
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
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
        assertTrue( list.contains( "doll" ) );
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

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
    }

    @Test
    public void testBindListWithConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toys: /wife/children{age > 10}.toys )\n" +
                "then\n" +
                "  list.add( $toys.size() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 2, (int) list.get( 0 ) );
    }

    @Test
    public void testClassTwoLevelXpathWithAlphaConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10, name.length > 5}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charlie", 12 );
        alice.addChild( charlie );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( debbie );
        Child eric = new Child( "Eric", 15 );
        alice.addChild( eric );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        eric.addToy( new Toy( "bike" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
    }

    @Test
    public void testClassTwoLevelXpathWithBetaConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  Man( $toy: /wife/children{age > 10, name.length > $i}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charlie", 12 );
        alice.addChild( charlie );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( debbie );
        Child eric = new Child( "Eric", 15 );
        alice.addChild( eric );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        eric.addToy( new Toy( "bike" ) );

        ksession.insert( 5 );
        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
    }

    @Test
    public void testReactiveOnLia() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "doll" ) );
    }

    @Test
    public void testReactiveDeleteOnLia() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        EntryPointNode epn = ( (InternalKnowledgeBase) ksession.getKieBase() ).getRete().getEntryPointNodes().values().iterator().next();
        ObjectTypeNode otn = epn.getObjectTypeNodes().values().iterator().next();
        LeftInputAdapterNode lian = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];
        ReactiveFromNode from1 = (ReactiveFromNode)lian.getSinkPropagator().getSinks()[0];
        ReactiveFromNode from2 = (ReactiveFromNode)from1.getSinkPropagator().getSinks()[0];
        ReactiveFromNode from3 = (ReactiveFromNode)from2.getSinkPropagator().getSinks()[0];

        BetaMemory betaMemory = ( (InternalWorkingMemory) ksession ).getNodeMemory(from3).getBetaMemory();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 11 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 3, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );
        assertTrue( list.contains( "doll" ) );

        TupleMemory tupleMemory = betaMemory.getLeftTupleMemory();
        assertEquals( 2, betaMemory.getLeftTupleMemory().size() );
        Iterator<LeftTuple> it = tupleMemory.iterator();
        for ( LeftTuple next = it.next(); next != null; next = it.next() ) {
            Object obj = next.getFactHandle().getObject();
            assertTrue( obj == charlie || obj == debbie );
        }

        list.clear();
        debbie.setAge( 10 );
        ksession.fireAllRules();

        assertEquals( 0, list.size() );

        assertEquals( 1, betaMemory.getLeftTupleMemory().size() );
        it = tupleMemory.iterator();
        for ( LeftTuple next = it.next(); next != null; next = it.next() ) {
            Object obj = next.getFactHandle().getObject();
            assertTrue( obj == charlie );
        }
    }
    
    @Test
    public void testRemoveFromReactiveListBasic() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  School( $child: /children{age >= 13 && age < 20} )\n" +
                "then\n" +
                "  System.out.println( $child );\n" +
                "  insertLogical( $child );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Child charlie = new Child( "Charles", 15 );
        Child debbie = new Child( "Debbie", 19 );
        School school = new School( "Da Vinci" );
        school.addChild( charlie );
        
        ksession.insert( school );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));
        
        school.addChild( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertTrue(ksession.getObjects().contains(debbie));
        
        school.getChildren().remove( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));
        
        school.addChild( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertTrue(ksession.getObjects().contains(debbie));

        debbie.setAge( 20 );        
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));
    }
    
    @Test
    public void testRemoveFromReactiveListExtended() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Adult ada = new Adult("Ada", 19);
        Adult bea = new Adult("Bea", 19);
        Group x = new Group("X");
        Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 20 );        
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        y.removePerson(bea);
        bea.setAge( 20 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }
    
    /**
     * Same test as above but with serialization.
     */
    @Test
    public void testRemoveFromReactiveListExtendedWithSerialization() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        try {
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true, false );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        
        Adult ada = new Adult("Ada", 19);
        Adult bea = new Adult("Bea", 19);
        Group x = new Group("X");
        Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 20 );        
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        y.removePerson(bea);
        bea.setAge( 20 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }
    
    @Test
    public void testAddAllRemoveAll() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Group x = new Group("X");
        Group y = new Group("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        Adult ada = new Adult("Ada", 20);
        Adult bea = new Adult("Bea", 20);
        x.getMembers().addAll(Arrays.asList(new Adult[]{ada, bea}));
        y.getMembers().addAll(Arrays.asList(new Adult[]{ada, bea}));
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertTrue  (factsCollection(ksession).contains("Y.Bea"));
        
        x.getMembers().removeAll(Arrays.asList(new Adult[]{ada, bea}));
        y.getMembers().removeAll(Arrays.asList(new Adult[]{ada, bea}));
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }
    
    @Test
    public void testRemoveAndAddForReplaceFromReactiveList() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Adult ada = new Adult("Ada", 19);
        Adult bea = new Adult("Bea", 19);
        Group x = new Group("X");
        Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 20 );        
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        Adult zelda = new Adult("Zelda", 47);  
        x.getMembers().remove(ada);
        x.getMembers().add(zelda);
        y.getMembers().remove(ada);
        y.getMembers().add(zelda);
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Zelda"));    
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Zelda"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        y.removePerson(bea);
        bea.setAge( 20 );        
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Zelda"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Zelda"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }

    private List<?> factsCollection(KieSession ksession) {
        List<Object> res = new ArrayList<>();
        res.addAll(ksession.getObjects());
        return res;
    }

    @Test
    public void testReactiveOnBeta() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  Man( $toy: /wife/children{age > $i}?/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( 10 );
        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "doll" ) );
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
                "  Man( $toy: /wife/children{age >= $i}/toys )\n" +
                "then\n" +
                "  toyList.add( $toy.getName() );\n" +
                "end\n" +
                "rule R2 when\n" +
                "  School( $child: /children{age >= 13} )\n" +
                "then\n" +
                "  teenagers.add( $child.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> toyList = new ArrayList<String>();
        ksession.setGlobal( "toyList", toyList );
        List<String> teenagers = new ArrayList<String>();
        ksession.setGlobal( "teenagers", teenagers );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 15 );
        Child debbie = new Child( "Debbie", 12 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        School school = new School( "Da Vinci" );
        school.addChild( charlie );
        school.addChild( debbie );

        ksession.insert( 13 );
        ksession.insert( bob );
        ksession.insert( school );
        ksession.fireAllRules();

        assertEquals( 2, toyList.size() );
        assertTrue( toyList.contains( "car" ) );
        assertTrue( toyList.contains( "ball" ) );

        assertEquals( 1, teenagers.size() );
        assertTrue( teenagers.contains( "Charles" ) );

        toyList.clear();
        debbie.setAge( 13 );
        ksession.fireAllRules();

        assertEquals( 1, toyList.size() );
        assertTrue( toyList.contains( "doll" ) );

        assertEquals( 2, teenagers.size() );
        assertTrue( teenagers.contains( "Charles" ) );
        assertTrue( teenagers.contains( "Debbie" ) );
    }

    @Test
    public void testInlineCast() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{ #BabyGirl }/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        BabyBoy charlie = new BabyBoy( "Charles", 12 );
        BabyGirl debbie = new BabyGirl( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "doll" ) );
    }

    @Test
    public void testInlineCastWithConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( name == \"Bob\", $name: /wife/children{ #BabyGirl, favoriteDollName.startsWith(\"A\") }.name )\n" +
                "then\n" +
                "  list.add( $name );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        BabyBoy charlie = new BabyBoy( "Charles", 12 );
        BabyGirl debbie = new BabyGirl( "Debbie", 8, "Anna" );
        BabyGirl elisabeth = new BabyGirl( "Elisabeth", 5, "Zoe" );
        BabyGirl farrah = new BabyGirl( "Farrah", 3, "Agatha" );
        alice.addChild( charlie );
        alice.addChild( debbie );
        alice.addChild( elisabeth );
        alice.addChild( farrah );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "Debbie" ) );
        assertTrue( list.contains( "Farrah" ) );
    }

    @Test
    public void testReactiveList() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();
        charlie.addToy( new Toy( "gun" ) );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "gun" ) );
    }

    @Test
    public void testIndexedAccess() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[0]{age > 10}/toys[1] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 11 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertTrue( list.contains( "ball" ) );
    }

    @Test
    public void testRecursiveXPathQuery() {
        String drl =
                "import org.drools.compiler.xpath.Thing;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule \"Print all things contained in the Office\" when\n" +
                "    $office : Thing( name == \"office\" )\n" +
                "    isContainedIn( $office, thing; )\n" +
                "then\n" +
                "    list.add( thing.getName() );\n" +
                "end\n" +
                "\n" +
                "query isContainedIn( Thing $x, Thing $y )\n" +
                "    $y := /$x/children\n" +
                "or\n" +
                "    ( $z := /$x/children and isContainedIn( $z, $y; ) )\n" +
                "end\n";

        Thing house = new Thing( "house" );
        Thing office = new Thing( "office" );
        house.addChild( office );
        Thing kitchen = new Thing( "kitchen" );
        house.addChild( kitchen );

        Thing knife = new Thing( "knife" );
        kitchen.addChild( knife );
        Thing cheese = new Thing( "cheese" );
        kitchen.addChild( cheese );

        Thing desk = new Thing( "desk" );
        office.addChild( desk );
        Thing chair = new Thing( "chair" );
        office.addChild( chair );

        Thing computer = new Thing( "computer" );
        desk.addChild( computer );
        Thing draw = new Thing( "draw" );
        desk.addChild( draw );
        Thing key = new Thing( "key" );
        draw.addChild( key );

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.insert(house);
        ksession.insert(office);
        ksession.insert(kitchen);
        ksession.insert(knife);
        ksession.insert(cheese);
        ksession.insert(desk);
        ksession.insert(chair);
        ksession.insert(computer);
        ksession.insert(draw);
        ksession.insert(key);

        ksession.fireAllRules();
        System.out.println(list);
        assertEquals( 5, list.size() );
        assertTrue( list.containsAll( asList( "desk", "chair", "key", "draw", "computer" ) ) );
    }

    @Test
    public void testBackReferenceConstraint() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys{ name.length == ../name.length } )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Carl", 12 );
        Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        debbie.addToy( new Toy( "guitar" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "ball" ) );
        assertTrue( list.contains( "guitar" ) );
    }

    @Test
    public void testNonReactivePart() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10}?/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test
    public void testAllNonReactiveAfterNonReactivePart() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: ?/wife/children{age > 10}/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        Child charlie = new Child( "Charles", 12 );
        Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( "car" ) );
        assertTrue( list.contains( "ball" ) );

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        assertEquals( 0, list.size() );
    }

    @Test
    public void testInvalidDoubleNonReactivePart() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife?/children{age > 10}?/toys )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
    }
    
    @Test
    public void testAccumulate() {
        // DROOLS-1265
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.lang.Number avg\n" +
                "\n" +
                "rule R when\n" +
                "  accumulate ( Adult( $child: /children ) ; $avg: average ($child.getAge()) )\n" +
                "then\n" +
                "  kcontext.getKieRuntime().setGlobal(\"avg\", $avg);\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals(10, ((Number)ksession.getGlobal("avg")).doubleValue(), 0);
    }

    @Test
    public void testPrimitives() {
        // DROOLS-1266
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Adult( $x: /children.age )\n" +
                "then\n" +
                "  list.add( $x );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertEquals(2, list.size());
    }

    @Test
    public void testSingleFireOnReactiveChange() {
        // DROOLS-1302
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                "then\n" +
                "  list.add( $toy );\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Woman alice = new Woman( "Alice", 38 );
        Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        ksession.insert( bob );
        ksession.fireAllRules();

        list.clear();
        Child eleonor = new Child( "Eleonor", 10 );
        alice.addChild( eleonor );
        Toy toy = new Toy( "eleonor toy 1" );
        eleonor.addToy( toy );
        eleonor.setAge(11);

        ksession.fireAllRules();
        System.out.println(list);
        assertEquals( 1, list.size() );

        list.clear();

        toy.setName( "eleonor toy 2" );
        ksession.fireAllRules();
        System.out.println(list);
        assertEquals( 1, list.size() );
    }
    
    @Test   
    public void testDoubleAdd() {
        // DROOLS-1376
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Group x = new Group("X");
        Group y = new Group("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        Adult ada = new Adult("Ada", 20);
        Adult bea = new Adult("Bea", 20);
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertTrue  (factsCollection(ksession).contains("Y.Bea"));
        
        x.removePerson(ada);
        x.removePerson(bea);
        y.removePerson(ada);
        y.removePerson(bea);  
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }
    
    @Test
    public void testDoubleRemove() {
        // DROOLS-1376
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Adult ada = new Adult("Ada", 20);
        Adult bea = new Adult("Bea", 20);
        Group x = new Group("X");
        Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertTrue  (factsCollection(ksession).contains("Y.Bea"));
        
        x.removePerson(ada);
        x.removePerson(bea);
        y.removePerson(ada);
        y.removePerson(bea);  
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }

    @Test
    public void testAddAllRemoveIdx() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 30} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        Group x = new Group("X");
        Group y = new Group("Y");
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        Adult ada = new Adult("Ada", 29);
        Adult bea = new Adult("Bea", 29);
        List<Person> bothList = Arrays.asList(new Person[]{ada, bea});
        x.getMembers().addAll(bothList);
        y.getMembers().addAll(bothList);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 30 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        y.getMembers().remove(1); // removing Bea from Y
        bea.setAge( 30 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }
    
    @Test
    public void testMiscListMethods() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        TMDirectory x = new TMDirectory("X");
        TMDirectory y = new TMDirectory("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        
        TMFile file0 = new TMFile("File0", 47);
        TMFile file1 = new TMFile("File1", 47);
        TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(0, Arrays.asList(new TMFile[]{file0, file1}));
        y.getFiles().add(0, file2);
        y.getFiles().add(0, file0);
        y.getFiles().add(1, file1);
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));

        file0.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        y.getFiles().remove(1); // removing File1 from Y
        file1.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        file2.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        TMFile file0R = new TMFile("File0R", 999);
        x.getFiles().set(0, file0R);
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File0R"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
    }
    
    @Test
    public void testCollectionIteratorRemove() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        TMDirectory x = new TMDirectory("X");
        TMDirectory y = new TMDirectory("Y");
        TMFile file0 = new TMFile("File0", 999);
        TMFile file1 = new TMFile("File1", 999);
        TMFile file2 = new TMFile("File2", 999);
        x.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        y.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        java.util.Iterator<TMFile> iterator = x.getFiles().iterator();
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        assertFalse( iterator.hasNext() );
    }
    
    @Test
    public void testListIteratorRemove() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        ksession.addEventListener(new DefaultRuleRuntimeEventListener() {
            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                System.out.println(event.getOldObject() + " -> " + "_");
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                System.out.println("_" + " -> " + event.getObject());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                System.out.println(event.getOldObject() + " -> " + event.getObject());
            }
        });
        
        TMDirectory x = new TMDirectory("X");
        TMDirectory y = new TMDirectory("Y");
        TMFile file0 = new TMFile("File0", 999);
        TMFile file1 = new TMFile("File1", 999);
        TMFile file2 = new TMFile("File2", 999);
        x.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        y.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        ListIterator<TMFile> xIterator = x.getFiles().listIterator(1);
        ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
        xIterator.next();
        xIterator.remove();
        yIterator.next();
        yIterator.next();
        yIterator.next();
        yIterator.remove();
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        assertTrue  ( xIterator.hasNext()     );
        assertTrue  ( xIterator.hasPrevious() );
        assertFalse ( yIterator.hasNext()     );
        assertTrue  ( yIterator.hasPrevious() );
        
        xIterator.next();
        xIterator.remove();
        yIterator.previous();
        yIterator.remove();
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        assertFalse ( xIterator.hasNext()     );
        assertTrue  ( xIterator.hasPrevious() );
        assertFalse ( yIterator.hasNext()     );
        assertTrue  ( yIterator.hasPrevious() );
        
        xIterator.previous();
        xIterator.remove();
        yIterator.previous();
        yIterator.remove();
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        assertFalse ( xIterator.hasNext()     );
        assertFalse ( xIterator.hasPrevious() );
        assertFalse ( yIterator.hasNext()     );
        assertFalse ( yIterator.hasPrevious() );
    }
    
    @Test
    public void testListIteratorMisc() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        ksession.addEventListener(new DefaultRuleRuntimeEventListener() {
            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                System.out.println(event.getOldObject() + " -> " + "_");
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                System.out.println("_" + " -> " + event.getObject());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                System.out.println(event.getOldObject() + " -> " + event.getObject());
            }
        });
        
        TMDirectory x = new TMDirectory("X");
        TMDirectory y = new TMDirectory("Y");

        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        TMFile file0 = new TMFile("File0", 999);
        TMFile file1 = new TMFile("File1", 999);
        TMFile file2 = new TMFile("File2", 999);
        ListIterator<TMFile> xIterator = x.getFiles().listIterator();
        ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
        xIterator.add(file0);
        yIterator.add(file2);
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        xIterator.add(file1);
        yIterator.previous();
        yIterator.add(file1);
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        xIterator.add(file2);
        yIterator.previous();
        yIterator.add(file0);
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        System.out.println(x.getFiles());
        System.out.println(y.getFiles());
        
        xIterator.previous();
        xIterator.set(new TMFile("File2R", 999));
        yIterator.previous();
        yIterator.set(new TMFile("File0R", 999));
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        assertTrue  (factsCollection(ksession).contains("X.File2R"));
        assertTrue  (factsCollection(ksession).contains("Y.File0R"));
    }
    
    @Test
    public void testRemoveIfSupport() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        ksession.addEventListener(new DefaultRuleRuntimeEventListener() {
            @Override
            public void objectDeleted(ObjectDeletedEvent event) {
                System.out.println(event.getOldObject() + " -> " + "_");
            }

            @Override
            public void objectInserted(ObjectInsertedEvent event) {
                System.out.println("_" + " -> " + event.getObject());
            }

            @Override
            public void objectUpdated(ObjectUpdatedEvent event) {
                System.out.println(event.getOldObject() + " -> " + event.getObject());
            }
        });
        
        TMDirectory x = new TMDirectory("X");
        TMDirectory y = new TMDirectory("Y");

        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        TMFile file0 = new TMFile("File0", 1000);
        TMFile file1 = new TMFile("File1", 1001);
        TMFile file2 = new TMFile("File2", 1002);
        
        x.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        y.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1, file2}));
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        x.getFiles().removeIf( f -> f.getSize() % 2 == 0 );
        y.getFiles().removeIf( f -> f.getSize() % 2 == 1 );
        ksession.fireAllRules();
        assertFalse  (factsCollection(ksession).contains("X.File0"));
        assertTrue   (factsCollection(ksession).contains("X.File1"));
        assertFalse  (factsCollection(ksession).contains("X.File2"));
        assertTrue   (factsCollection(ksession).contains("Y.File0"));
        assertFalse  (factsCollection(ksession).contains("Y.File1"));
        assertTrue   (factsCollection(ksession).contains("Y.File2"));
    }
    
    @Test
    public void testMiscSetMethods() {
        String drl =
                "import org.drools.compiler.xpath.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMFileSet( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        TMFileSet x = new TMFileSet("X");
        TMFileSet y = new TMFileSet("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        
        TMFile file0 = new TMFile("File0", 47);
        TMFile file1 = new TMFile("File1", 47);
        TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(Arrays.asList(new TMFile[]{file0, file1}));
        y.getFiles().add(file2);
        y.getFiles().add(file0);
        y.getFiles().add(file1);
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));

        file0.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        y.getFiles().remove( file1 ); // removing File1 from Y
        file1.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        file2.setSize( 999 );        
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
    }
}
