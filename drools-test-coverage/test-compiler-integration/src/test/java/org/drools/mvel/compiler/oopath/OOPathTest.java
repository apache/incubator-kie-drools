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
package org.drools.mvel.compiler.oopath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveSet;
import org.drools.mvel.compiler.oopath.model.Adult;
import org.drools.mvel.compiler.oopath.model.Child;
import org.drools.mvel.compiler.oopath.model.Group;
import org.drools.mvel.compiler.oopath.model.Man;
import org.drools.mvel.compiler.oopath.model.Person;
import org.drools.mvel.compiler.oopath.model.TMDirectory;
import org.drools.mvel.compiler.oopath.model.TMFile;
import org.drools.mvel.compiler.oopath.model.TMFileSet;
import org.drools.mvel.compiler.oopath.model.TMFileWithParentObj;
import org.drools.mvel.compiler.oopath.model.Toy;
import org.drools.mvel.compiler.oopath.model.Woman;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.mvel.compiler.TestUtil.assertDrlHasCompilationError;

@RunWith(Parameterized.class)
public class OOPathTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testInvalidOOPath() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
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
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
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
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().hasMessages(Message.Level.ERROR)).isTrue();
    }

    @Test
    public void testIndexedAccess() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children[0]/toys[1] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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

        assertThat(list).containsExactlyInAnyOrder("ball");
    }

    @Test
    public void testBackReferenceConstraint() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys[ name.length == ../name.length ] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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

        assertThat(list).containsExactlyInAnyOrder("ball", "guitar");
    }

    @Test
    public void testPrimitives() {
        // DROOLS-1266
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Adult( $x : /children[$y : age]/toys[$t : name] )\n" +
                "then\n" +
                "  list.add( $x.getName() + \":\" + $y + \":\" + $t );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man( "Bob", 40 );
        Child charles = new Child( "Charles", 12 );
        charles.addToy(new Toy("t1"));
        charles.addToy(new Toy("t2"));
        bob.addChild( charles );

        Child deb = new Child( "Debbie", 8 );
        deb.addToy(new Toy("t3"));
        deb.addToy(new Toy("t4"));
        bob.addChild(deb );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).hasSize(4);
        assertThat(list).isEqualTo(Arrays.asList("t2:12:t2", "t1:12:t1", "t4:8:t4", "t3:8:t3"));
    }
    
    @Test   
    public void testDoubleAdd() {
        // DROOLS-1376
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members[age >= 20] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Group x = new Group("X");
        final Group y = new Group("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
        
        final Adult ada = new Adult("Ada", 20);
        final Adult bea = new Adult("Bea", 20);
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isTrue();
        
        x.removePerson(ada);
        x.removePerson(bea);
        y.removePerson(ada);
        y.removePerson(bea);  
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
    }
    
    @Test
    public void testDoubleRemove() {
        // DROOLS-1376
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members[age >= 20] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";
 
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isTrue();
        
        x.removePerson(ada);
        x.removePerson(bea);
        y.removePerson(ada);
        y.removePerson(bea);  
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
    }

    @Test
    public void testAddAllRemoveIdx() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  Group( $id: name, $p: /members[age >= 30] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Group x = new Group("X");
        final Group y = new Group("Y");
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
        
        final Adult ada = new Adult("Ada", 29);
        final Adult bea = new Adult("Bea", 29);
        final List<Person> bothList = Arrays.asList(new Person[]{ada, bea});
        x.getMembers().addAll(bothList);
        y.getMembers().addAll(bothList);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();

        ada.setAge( 30 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
        
        y.getMembers().remove(1); // removing Bea from Y
        bea.setAge( 30 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
    }
    
    @Test
    public void testMiscListMethods() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final TMDirectory x = new TMDirectory("X");
        final TMDirectory y = new TMDirectory("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(0, Arrays.asList(file0, file1));
        y.getFiles().add(0, file2);
        y.getFiles().add(0, file0);
        y.getFiles().add(1, file1);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();

        file0.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        y.getFiles().remove(1); // removing File1 from Y
        file1.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        file2.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        final TMFile file0R = new TMFile("File0R", 999);
        x.getFiles().set(0, file0R);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File0R")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
    }
    
    @Test
    public void testCollectionIteratorRemove() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        java.util.Iterator<TMFile> iterator = x.getFiles().iterator();
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        iterator.next();
        iterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();

        assertThat(iterator.hasNext()).isFalse();
    }
    
    @Test
    public void testListIteratorRemove() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        final ListIterator<TMFile> xIterator = x.getFiles().listIterator(1);
        final ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
        xIterator.next();
        xIterator.remove();
        yIterator.next();
        yIterator.next();
        yIterator.next();
        yIterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();

        assertThat(xIterator.hasNext()).isTrue();
        assertThat(xIterator.hasPrevious()).isTrue();
        assertThat(yIterator.hasNext()).isFalse();
        assertThat(yIterator.hasPrevious()).isTrue();
        
        xIterator.next();
        xIterator.remove();
        yIterator.previous();
        yIterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();

        assertThat(xIterator.hasNext()).isFalse();
        assertThat(xIterator.hasPrevious()).isTrue();
        assertThat(yIterator.hasNext()).isFalse();
        assertThat(yIterator.hasPrevious()).isTrue();
        
        xIterator.previous();
        xIterator.remove();
        yIterator.previous();
        yIterator.remove();
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();

        assertThat(xIterator.hasNext()).isFalse();
        assertThat(xIterator.hasPrevious()).isFalse();
        assertThat(yIterator.hasNext()).isFalse();
        assertThat(yIterator.hasPrevious()).isFalse();
    }
    
    @Test
    public void testListIteratorMisc() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        final TMFile file0 = new TMFile("File0", 999);
        final TMFile file1 = new TMFile("File1", 999);
        final TMFile file2 = new TMFile("File2", 999);
        final ListIterator<TMFile> xIterator = x.getFiles().listIterator();
        final ListIterator<TMFile> yIterator = y.getFiles().listIterator();
        
        xIterator.add(file0);
        yIterator.add(file2);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        xIterator.add(file1);
        yIterator.previous();
        yIterator.add(file1);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        xIterator.add(file2);
        yIterator.previous();
        yIterator.add(file0);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        System.out.println(x.getFiles());
        System.out.println(y.getFiles());
        
        xIterator.previous();
        xIterator.set(new TMFile("File2R", 999));
        yIterator.previous();
        yIterator.set(new TMFile("File0R", 999));
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();

        assertThat(factsCollection(ksession).contains("X.File2R")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0R")).isTrue();
    }
    
    @Test
    public void testRemoveIfSupport() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMDirectory( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        final TMFile file0 = new TMFile("File0", 1000);
        final TMFile file1 = new TMFile("File1", 1001);
        final TMFile file2 = new TMFile("File2", 1002);
        
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));
        y.getFiles().addAll(Arrays.asList(file0, file1, file2));
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
        
        x.getFiles().removeIf( f -> f.getSize() % 2 == 0 );
        y.getFiles().removeIf( f -> f.getSize() % 2 == 1 );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
    }
    
    @Test
    public void testMiscSetMethods() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "\n" +
                "rule R2 when\n" +
                "  TMFileSet( $id: name, $p: /files[size >= 100] )\n" +
                "then\n" +
                "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final TMFileSet x = new TMFileSet("X");
        final TMFileSet y = new TMFileSet("Y");
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File2", 47);
        x.getFiles().add(file2);
        x.getFiles().addAll(Arrays.asList(file0, file1));
        y.getFiles().add(file2);
        y.getFiles().add(file0);
        y.getFiles().add(file1);
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();

        file0.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        y.getFiles().remove( file1 ); // removing File1 from Y
        file1.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isFalse();
        
        file2.setSize( 999 );        
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File1")).isTrue();
        assertThat(factsCollection(ksession).contains("X.File2")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File0")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.File1")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.File2")).isTrue();
    }

    @Test
    public void testDeclarationOutsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files[name == $ic1.name], $ic2 != $ic1 )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertThat(duplicateNames.contains("File0")).isTrue();
        assertThat(duplicateNames.contains("File1")).isFalse();
    }

    @Test
    public void testDereferencedDeclarationOutsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files[this != $ic1], $ic2.name == $ic1.name )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertThat(duplicateNames.contains("File0")).isTrue();
        assertThat(duplicateNames.contains("File1")).isFalse();
    }

    @Test
    public void testDeclarationInsideOOPath() {
        // DROOLS-1411
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files )\n" +
                "  TMFileSet( this == $dir1, $ic2 : /files[name == $ic1.name, this != $ic1] )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Set<String> duplicateNames = new HashSet<>();
        ksession.setGlobal("duplicateNames", duplicateNames);

        final TMFileSet x = new TMFileSet("X");
        final TMFile file0 = new TMFile("File0", 47);
        final TMFile file1 = new TMFile("File1", 47);
        final TMFile file2 = new TMFile("File0", 47);
        x.getFiles().addAll(Arrays.asList(file0, file1, file2));

        ksession.insert(x);
        ksession.fireAllRules();

        assertThat(duplicateNames.contains("File0")).isTrue();
        assertThat(duplicateNames.contains("File1")).isFalse();
    }

    @Test
    public void testCompileErrorOnDoubleOOPathInPattern() {
        // DROOLS-1411
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $dir1 : TMFileSet( $ic1 : /files, /files[name == $ic1.name, this != $ic1] )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        assertDrlHasCompilationError( drl, 1, kieBaseTestConfiguration );
    }

    @Test
    public void testOOPathWithLocalDeclaration() {
        // DROOLS-1411
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $ic1 : TMFileWithParentObj( $curName : name,\n" +
                "                              $ic2: /parent#TMFileSet/files[name == $curName, this != $ic1 ] )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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

        assertThat(duplicateNames.contains("File0")).isTrue();
        assertThat(duplicateNames.contains("File1")).isFalse();
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
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "import "+TMFileSetQuater.class.getCanonicalName()+";\n" +
                "global java.util.Set duplicateNames; \n" +
                "\n" +
                "rule DIFF_FILES_BUT_WITH_SAME_FILENAME when\n" +
                "  $ic1 : TMFileWithParentObj( $curName : name, $curId : id, \n" +
                "                               $ic2: /parent#TMFileSetQuater/files[name == $curName, id != $curId ] )\n" +
                "then\n" +
                "  duplicateNames.add( $ic1.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

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

        assertThat(duplicateNames.contains("File0")).isTrue();
        assertThat(duplicateNames.contains("File1")).isFalse();
    }

    private List<?> factsCollection(KieSession ksession) {
        final List<Object> res = new ArrayList<>();
        res.addAll(ksession.getObjects());
        return res;
    }

    @Test
    public void testWith2Peers() {
        // DROOLS-1589
        String header =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n\n";

        String drl1 =
                "rule R1 when\n" +
                "  Man( $m: /wife[age == 25] )\n" +
                "then\n" +
                "  list.add($m.getName());\n" +
                "end\n\n";

        String drl2 =
                "rule R2 when\n" +
                "  Man( $m: /wife[age == 26] )\n" +
                "then\n" +
                "  list.add($m.getName());\n" +
                "end\n\n";

        String drl3 =
                "rule R3 when\n" +
                "  Man( $m: /wife[age == 27] )\n" +
                "then\n" +
                "  list.add($m.getName());\n" +
                "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, header + drl1 + drl2 + drl3);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man("John", 25);
        bob.setWife( new Woman("Jane", 25) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Jane");
        list.clear();

        bob.getWife().setAge(26);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Jane");
        list.clear();

        bob.getWife().setAge(27);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Jane");
        list.clear();

        bob.getWife().setAge(28);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void testWithExists() {
        String header =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n\n";

        String drl1 =
                "rule R1 when\n" +
                "  exists( Man( $m: /wife[age == 25] ) )\n" +
                "then\n" +
                "  list.add(\"Found\");\n" +
                "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, header + drl1);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man("John", 25);
        bob.setWife( new Woman("Jane", 25) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Found");
        list.clear();
    }

    @Test
    public void testNotReactivePeer() {
        // DROOLS-1727
        String drl1 =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                "global java.util.List list\n\n" +
                "rule R1 when\n" +
                "  not String()\n" +
                "  $a : Man( name == \"Mario\" )\n" +
                "then\n" +
                "  list.add(\"Found\");\n" +
                "  insert($a.getName());\n" +
                "end\n\n" +
                "rule R2 when\n" +
                "  not String()\n" +
                "  $a : Man( $c: /children[age == 6], name == \"Mario\" )\n" +
                "then\n" +
                "  list.add(\"Found\");\n" +
                "  insert($a.getName());\n" +
                "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl1);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Man mario = new Man("Mario", 40);
        mario.addChild( new Child("Sofia", 6) );

        ksession.insert( mario );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testConstraintExternalToOopath() {
        // DROOLS-2135
        final String drl =
                "import "+ Parent.class.getCanonicalName() +";\n" +
                "import "+ Son.class.getCanonicalName() +";\n" +
                "global java.util.List list\n\n" +
                "rule R when\n" +
                "  Parent( $child : /children, $child.name == \"joe\" )\n" +
                "then\n" +
                "  list.add( $child.getName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        Son joe = new Son("joe");
        Son jack = new Son("jack");
        Parent parent = new Parent(Arrays.asList(joe, jack));

        ksession.insert(parent);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("joe");
    }

    public class Son {
        private String name;
        public Son(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String toString() {
            return this.name;
        }
    }

    public class Parent {
        private List<Son> children;
        public Parent(List<Son> children) {
            this.children = children;
        }
        public List<Son> getChildren() {
            return children;
        }
        public void setChildren(List<Son> children) {
            this.children = children;
        }
    }

    @Test
    public void testOopathAfterNot() {
        // DROOLS-6541
        final String drl =
                "import "+ Pojo1.class.getCanonicalName() +";\n" +
                "import "+ Pojo2.class.getCanonicalName() +";\n" +
                "import "+ Pojo3.class.getCanonicalName() +";\n" +
                "rule R1 when\n" +
                "    a : Pojo1() \n" +
                "    not( b : Pojo2( field == \"val\" ) from a.getPojo2List() ) \n  " +
                "    c : Pojo2( /pojo3List[firstName == \"Bob\"] ) from a.getPojo2List() \n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        Pojo3 bob = new Pojo3();
        bob.setFirstName("Bob");

        List<Pojo3> pojo3List = new ArrayList<>();
        pojo3List.add(bob);

        Pojo2 pojo2 = new Pojo2();
        pojo2.setField("not_val");
        pojo2.setPojo3List(pojo3List);

        List<Pojo2> pojo2List = new ArrayList<>();
        pojo2List.add(pojo2);

        Pojo1 pojo1 = new Pojo1();
        pojo1.setPojo2List(pojo2List);

        ksession.insert(pojo1);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class Pojo1 {

        private List<Pojo2> pojo2List = new ArrayList<>();

        public List<Pojo2> getPojo2List() {
            return pojo2List;
        }

        public void setPojo2List(List<Pojo2> pojo2List) {
            this.pojo2List = pojo2List;
        }
    }

    public static class Pojo2 {

        public String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        private List<Pojo3> pojo3List = new ArrayList<>();

        public List<Pojo3> getPojo3List() {
            return pojo3List;
        }

        public void setPojo3List(List<Pojo3> pojo3List) {
            this.pojo3List = pojo3List;
        }
    }

    public static class Pojo3 {

        private String firstName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

}
