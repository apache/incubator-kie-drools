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

package org.drools.compiler.oopath;

import static org.drools.compiler.TestUtil.assertDrlHasCompilationError;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.drools.compiler.oopath.model.Adult;
import org.drools.compiler.oopath.model.Child;
import org.drools.compiler.oopath.model.Group;
import org.drools.compiler.oopath.model.Man;
import org.drools.compiler.oopath.model.Person;
import org.drools.compiler.oopath.model.TMDirectory;
import org.drools.compiler.oopath.model.TMFile;
import org.drools.compiler.oopath.model.TMFileSet;
import org.drools.compiler.oopath.model.TMFileWithParentObj;
import org.drools.compiler.oopath.model.Thing;
import org.drools.compiler.oopath.model.Toy;
import org.drools.compiler.oopath.model.Woman;
import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveSet;
import org.junit.Test;
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

public class OOPathTest {

    @Test
    public void testInvalidOOPath() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife.children/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        testInvalid(drl);
    }

    @Test
    public void testInvalidOOPathProperty() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children/toys/wrongProperty )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        testInvalid(drl);
    }

    private void testInvalid(final String drl) {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        final Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
    }

    @Test
    public void testIndexedAccess() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[0]{age > 10}/toys[1] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 11 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("ball");
    }

    @Test
    public void testRecursiveOOPathQuery() {
        final String drl =
                "import org.drools.compiler.oopath.model.Thing;\n" +
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

        final Thing house = new Thing( "house" );
        final Thing office = new Thing( "office" );
        house.addChild( office );
        final Thing kitchen = new Thing( "kitchen" );
        house.addChild( kitchen );

        final Thing knife = new Thing( "knife" );
        kitchen.addChild( knife );
        final Thing cheese = new Thing( "cheese" );
        kitchen.addChild( cheese );

        final Thing desk = new Thing( "desk" );
        office.addChild( desk );
        final Thing chair = new Thing( "chair" );
        office.addChild( chair );

        final Thing computer = new Thing( "computer" );
        desk.addChild( computer );
        final Thing draw = new Thing( "draw" );
        desk.addChild( draw );
        final Thing key = new Thing( "key" );
        draw.addChild( key );

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final List<String> list = new ArrayList<>();
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
        Assertions.assertThat(list).containsExactlyInAnyOrder("desk", "chair", "key", "draw", "computer");
    }

    @Test
    public void testBackReferenceConstraint() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys{ name.length == ../name.length } )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Carl", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        debbie.addToy( new Toy( "guitar" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("ball", "guitar");
    }

    @Test
    public void testPrimitives() {
        // DROOLS-1266
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Adult( $x: /children.age )\n" +
                "then\n" +
                "  list.add( $x );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).hasSize(2);
    }
    
    @Test   
    public void testDoubleAdd() {
        // DROOLS-1376
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final Group x = new Group("X");
        final Group y = new Group("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        final Adult ada = new Adult("Ada", 20);
        final Adult bea = new Adult("Bea", 20);
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 20} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final Adult ada = new Adult("Ada", 20);
        final Adult bea = new Adult("Bea", 20);
        final Group x = new Group("X");
        final Group y = new Group("Y");
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members{age >= 30} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final Group x = new Group("X");
        final Group y = new Group("Y");
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
        
        final Adult ada = new Adult("Ada", 29);
        final Adult bea = new Adult("Bea", 29);
        final List<Person> bothList = Arrays.asList(new Person[]{ada, bea});
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(0, Arrays.asList(file0, file1));
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
        
        final TMFile file0R = new TMFile("File0R", 999);
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");
        final TMFile file0 = new TMFile("File0", 999);
        final TMFile file1 = new TMFile("File1", 999);
        final TMFile file2 = new TMFile("File2", 999);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));
        y.getFiles().addAll(Arrays.asList(file0, file1, file2));
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
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
        
        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");
        final TMFile file0 = new TMFile("File0", 999);
        final TMFile file1 = new TMFile("File1", 999);
        final TMFile file2 = new TMFile("File2", 999);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));
        y.getFiles().addAll(Arrays.asList(file0, file1, file2));
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.File0"));
        assertTrue  (factsCollection(ksession).contains("X.File1"));
        assertTrue  (factsCollection(ksession).contains("X.File2"));
        assertTrue  (factsCollection(ksession).contains("Y.File0"));
        assertTrue  (factsCollection(ksession).contains("Y.File1"));
        assertTrue  (factsCollection(ksession).contains("Y.File2"));
        
        final ListIterator<TMFile> xIterator = x.getFiles().listIterator(1);
        final ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
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
        
        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");

        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        final TMFile file0 = new TMFile("File0", 999);
        final TMFile file1 = new TMFile("File1", 999);
        final TMFile file2 = new TMFile("File2", 999);
        final ListIterator<TMFile> xIterator = x.getFiles().listIterator();
        final ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
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
        
        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");

        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("X.File2"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File2"));
        
        final TMFile file0 = new TMFile("File0", 1000);
        final TMFile file1 = new TMFile("File1", 1001);
        final TMFile file2 = new TMFile("File2", 1002);
        
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));
        y.getFiles().addAll(Arrays.asList(file0, file1, file2));
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
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMFileSet( $id: name, $p: /files{size >= 100} )\n" +
                "then\n" +
                "  System.out.println( $id + \".\" + $p.getName() );\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();
        
        final TMFileSet x = new TMFileSet("X");
        final TMFileSet y = new TMFileSet("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertFalse (factsCollection(ksession).contains("X.File0"));
        assertFalse (factsCollection(ksession).contains("X.File1"));
        assertFalse (factsCollection(ksession).contains("Y.File0"));
        assertFalse (factsCollection(ksession).contains("Y.File1"));
        
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(Arrays.asList(file0, file1));
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

    @Test
    public void testDeclarationOutsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files{name == $ic1.name}, $ic2 != $ic1 )\n" +
                "then\n" +
                "  System.out.println( $dir1 + \".: \" + $ic1 + \" \" + $ic2 );\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertTrue( duplicateNames.contains("File0") );
        assertFalse( duplicateNames.contains("File1") );
    }

    @Test
    public void testDereferencedDeclarationOutsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files{this != $ic1}, $ic2.name == $ic1.name )\n" +
                "then\n" +
                "  System.out.println( $dir1 + \".: \" + $ic1 + \" \" + $ic2 );\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertTrue( duplicateNames.contains("File0") );
        assertFalse( duplicateNames.contains("File1") );
    }

    @Test
    public void testDeclarationInsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files{name == $ic1.name, this != $ic1} )\n" +
                "then\n" +
                "  System.out.println( $dir1 + \".: \" + $ic1 + \" \" + $ic2 );\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertTrue( duplicateNames.contains("File0") );
        assertFalse( duplicateNames.contains("File1") );
    }

    @Test
    public void testCompileErrorOnDoubleOOPathInPattern() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files, /files{name == $ic1.name, this != $ic1} )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        assertDrlHasCompilationError( drl, 1 );
    }

    @Test
    public void testOOPathWithLocalDeclaration() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $ic1 : TMFileWithParentObj( $curName : name,\n" +
                "                              $ic2: /parent{#TMFileSet}/files{name == $curName, this != $ic1 } )\n" +
                "then\n" +
                "  System.out.println( $ic1 + \" \" + $ic2 );\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFileWithParentObj file0 = new TMFileWithParentObj(0, "File0", 47, x);
        final TMFileWithParentObj file1 = new TMFileWithParentObj(1, "File1", 47, x);
        final TMFileWithParentObj file2 = new TMFileWithParentObj(2, "File0", 47, x);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert( x );
        ksession.insert( file0 );
        ksession.insert( file1 );
        ksession.insert( file2 );
        ksession.fireAllRules();

        assertTrue( duplicateNames.contains("File0") );
        assertFalse( duplicateNames.contains("File1") );
    }

    public static class TMFileSetQuater extends AbstractReactiveObject {
        private final String name;
        private final Set<TMFileWithParentObj> members = new ReactiveSet<>();
        public TMFileSetQuater(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public Set<TMFileWithParentObj> getFiles() {
            return members;
        }
    }

    @Test
    public void testOOPathWithLocalInnerDeclaration() {
        // DROOLS-1411
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                "import "+TMFileSetQuater.class.getCanonicalName()+";\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $ic1 : TMFileWithParentObj( $curName : name, $curId : id, \n" +
                "                               $ic2: /parent{#TMFileSetQuater}/files{name == $curName, id != $curId } )\n" +
                "then\n" +
                "  System.out.println( $ic1 + \" \" + $ic2 );\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        final Set duplicateNames = new HashSet();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSetQuater x = new TMFileSetQuater("X");
        final TMFileWithParentObj file0 = new TMFileWithParentObj(0, "File0", 47, x);
        final TMFileWithParentObj file1 = new TMFileWithParentObj(1, "File1", 47, x);
        final TMFileWithParentObj file2 = new TMFileWithParentObj(2, "File0", 47, x);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert( x );
        ksession.insert( file0 );
        ksession.insert( file1 );
        ksession.insert( file2 );
        ksession.fireAllRules();

        assertTrue( duplicateNames.contains("File0") );
        assertFalse( duplicateNames.contains("File1") );
    }

    private List<?> factsCollection(KieSession ksession) {
        final List<Object> res = new ArrayList<>();
        res.addAll(ksession.getObjects());
        return res;
    }
}
