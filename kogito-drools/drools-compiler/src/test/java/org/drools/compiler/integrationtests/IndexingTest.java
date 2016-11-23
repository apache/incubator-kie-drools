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

package org.drools.compiler.integrationtests;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectSinkNodeList;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.util.FastIterator;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.core.util.DroolsTestUtil.rulestoMap;


public class IndexingTest extends CommonTestMethodBase {

    @Test()
    public void testAlphaNodeSharing() {
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import " + Person.class.getCanonicalName() + "\n";
        drl += "rule r1\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\")\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r2\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 40)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r3\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 50)\n";
        drl += "then\n";
        drl += "end\n";
        drl += "rule r4\n";
        drl += "when\n";
        drl += "   Person(name == \"John\", age == 60)\n";
        drl += "then\n";
        drl += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );

        Map<String, Rule> rules = rulestoMap(kbase);

        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        assertEquals( 2, otn.getObjectSinkPropagator().size() );

        AlphaNode a1 = ( AlphaNode ) otn.getObjectSinkPropagator().getSinks()[0];
        assertEquals( 3, a1.getObjectSinkPropagator().size() );
        assertEquals( 3, a1.getAssociationsSize() );
        assertTrue( a1.isAssociatedWith(rules.get("r1")));
        assertTrue( a1.isAssociatedWith(rules.get("r2")));
        assertTrue( a1.isAssociatedWith(rules.get("r3")));


        AlphaNode a2 = ( AlphaNode ) otn.getObjectSinkPropagator().getSinks()[1];
        assertEquals( 1, a2.getAssociationsSize() );
        assertEquals( 1, a2.getObjectSinkPropagator().size() );
        assertTrue( a2.isAssociatedWith(rules.get("r4")));
    }



    @Test(timeout=10000)
    public void testBuildsIndexedAlphaNodes() {
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 37)\n";
        drl += "   Person(name == \"Mark\", happy == true)\n";
        drl += "then\n";
        drl += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );

        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getObjectSinkPropagator().getSinks()[0];
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter)alphaNode1.getObjectSinkPropagator();
        ObjectSinkNodeList hashableSinks = sinkAdapter.getHashableSinks();
        assertNotNull(hashableSinks);
        assertEquals(2, hashableSinks.size());

        AlphaNode alphaNode2 = ( AlphaNode ) alphaNode1.getObjectSinkPropagator().getSinks()[0];
        assertSame(hashableSinks.getFirst(), alphaNode2);

        AlphaNode alphaNode3 = ( AlphaNode ) alphaNode1.getObjectSinkPropagator().getSinks()[1];
        assertSame(hashableSinks.getLast(), alphaNode3);
    }

    @Test(timeout=10000)
    public void testBuildsIndexedMemory() {
        // tests indexes are correctly built        
        String drl = "";
        drl += "package org.drools.compiler.test\n";
        drl += "import org.drools.compiler.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1  : Person($name : name )\n";
        drl += "   $p2 : Person(name == $name)\n"; //indexed
        drl += "   $p3 : Person(name == $p1.name)\n"; //indexed
        drl += "   $p4 : Person(address.street == $p1.address.street)\n"; //not indexed
        drl += "   $p5 : Person(address.street == $p1.name)\n";  // indexed
        //drl += "   $p6 : Person( $name == name)\n"; // not indexed and won't compile
        drl += "   $p7 : Person(addresses[\"key\"].street == $p1.name)\n";  // indexed
        drl += "   $p8 : Person(addresses[0].street == $p1.name)\n";  // indexed
        drl += "   $p9 : Person(name == $p1.address.street)\n"; //not indexed
        drl += "   $p10 : Person(addresses[0].street + 'xx' == $p1.name)\n";  // indexed
        drl += "   $p11 : Person(addresses[$p1].street == $p1.name)\n";  // not indexed
        drl += "then\n";
        drl += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, Person.class );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getObjectSinkPropagator().getSinks()[0];
        JoinNode j2 = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0]; // $p2
        JoinNode j3 = ( JoinNode ) j2.getSinkPropagator().getSinks()[0];  // $p3
        JoinNode j4 = ( JoinNode ) j3.getSinkPropagator().getSinks()[0];  // $p4
        JoinNode j5 = ( JoinNode ) j4.getSinkPropagator().getSinks()[0];  // $p5
        //JoinNode j6 = ( JoinNode ) j5.getSinkPropagator().getSinks()[0];  // $p6 // won't compile
        JoinNode j7 = ( JoinNode ) j5.getSinkPropagator().getSinks()[0];  // $p7
        JoinNode j8 = ( JoinNode ) j7.getSinkPropagator().getSinks()[0];  // $p8
        JoinNode j9 = ( JoinNode ) j8.getSinkPropagator().getSinks()[0];  // $p9
        JoinNode j10 = ( JoinNode ) j9.getSinkPropagator().getSinks()[0];  // $p10
        JoinNode j11 = ( JoinNode ) j10.getSinkPropagator().getSinks()[0];  // $p11
        
        SingleBetaConstraints c = ( SingleBetaConstraints ) j2.getRawConstraints();
        assertEquals( "$name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier() );
        assertTrue( c.isIndexed() );        
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( j2 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j3.getRawConstraints();
        assertEquals( "name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier() );
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j3 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j4.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j4 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleList );
        assertTrue( bm.getRightTupleMemory() instanceof TupleList );
        
        c = ( SingleBetaConstraints ) j5.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j5 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        // won't compile
//        c = ( SingleBetaConstraints ) j6.getRawConstraints();
//        assertEquals( "name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
//        assertFalse( c.isIndexed() );   
//        bm = ( BetaMemory ) wm.getNodeMemory( j6 );
//        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleList );
//        assertTrue( bm.getRightTupleMemory() instanceof RightTupleList );   
        
        c = ( SingleBetaConstraints ) j7.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j7 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j8.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j8 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j9.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j9 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleList );
        assertTrue( bm.getRightTupleMemory() instanceof TupleList );
        
        c = ( SingleBetaConstraints ) j10.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j10 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j11.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j11 );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleList);
        assertTrue( bm.getRightTupleMemory() instanceof TupleList );
    }
    
    @Test(timeout=10000)
    public void testIndexingOnQueryUnification() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );
        
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == DroolsQuery.class ) {
                node = n;
                break;
            }
        }

        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());
        
        AlphaNode alphanode = ( AlphaNode ) node.getObjectSinkPropagator().getSinks()[0];
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        JoinNode j = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0]; // $p2
        
        TripleNonIndexSkipBetaConstraints c = ( TripleNonIndexSkipBetaConstraints ) j.getRawConstraints();
        //assertEquals( "$name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );        
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( j );
        assertTrue( bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );
    }

    @Test(timeout=10000)
    public void testIndexingOnQueryUnificationWithNot() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( String $name, int $age ) \n";
        str += "    not $p2 : Person( $name := name, age > $age ) \n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == DroolsQuery.class ) {
                node = n;
                break;
            }
        }

        StatefulKnowledgeSessionImpl wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        AlphaNode alphanode = ( AlphaNode ) node.getObjectSinkPropagator().getSinks()[0];
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];

        NotNode n = (NotNode) liaNode.getSinkPropagator().getSinks()[0];

        DoubleNonIndexSkipBetaConstraints c = (DoubleNonIndexSkipBetaConstraints) n.getRawConstraints();
        //assertEquals( "$name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( n );
        System.out.println( bm.getLeftTupleMemory().getClass() );
        System.out.println( bm.getRightTupleMemory().getClass() );
        assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );


        final Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("inserted", new Integer(0));
        map.put("deleted", new Integer(0));
        map.put("updated", new Integer(0));
        wm.openLiveQuery("peeps", new Object[] {Variable.v, 99 }, new ViewChangedEventListener() {
            @Override
            public void rowInserted(Row row) {
                System.out.println( "inserted" );
                Integer integer = map.get("inserted");
                map.put("inserted", integer.intValue() + 1 );
            }

            @Override
            public void rowDeleted(Row row) {
                System.out.println( "deleted" );
                Integer integer = map.get("deleted");
                map.put("deleted", integer.intValue() + 1 );
            }

            @Override
            public void rowUpdated(Row row) {
                System.out.println( "updated" );
                Integer integer = map.get("updated");
                map.put("updated", integer.intValue() + 1 );
            }
        });

        System.out.println( "inserted: " + map.get("inserted"));
        System.out.println( "deleted: " + map.get("deleted"));
        System.out.println( "updated: " + map.get("updated"));

        Map<String, InternalFactHandle> peeps = new HashMap<String, InternalFactHandle>();

        Person p = null;
        InternalFactHandle fh = null;

        int max = 3;

        // 1 matched, prior to any insertions
        assertEquals( 1, map.get("inserted").intValue() );
        assertEquals( 0, map.get("deleted").intValue() );
        assertEquals( 0, map.get("updated").intValue() );

        // x0 is the blocker
        for ( int i = 0; i < max; i++ ) {
            p = new Person( "x" + i, 100);
            fh = ( InternalFactHandle ) wm.insert( p );
            wm.fireAllRules();
            peeps.put(p.getName(), fh);
        }

        // insertions case 1 deletion
        assertEquals( 1, map.get("inserted").intValue() );
        assertEquals( 1, map.get("deleted").intValue() );
        assertEquals( 0, map.get("updated").intValue() );

        // each x is blocker in turn up to x99
        for ( int i = 0; i < (max-1); i++ ) {
            fh = peeps.get("x" + i);
            p = (Person) fh.getObject();
            p.setAge( 90 );
            wm.update( fh, p );
            wm.fireAllRules();
            assertEquals( "i=" + i, 1, map.get("inserted").intValue() ); // make sure this doesn't change
        }

        // no change
        assertEquals( 1, map.get("inserted").intValue() );
        assertEquals( 1, map.get("deleted").intValue() );
        assertEquals( 0, map.get("updated").intValue() );

        // x99 is still the blocker, everything else is just added
        for ( int i = 0; i < (max-1); i++ ) {
            fh = peeps.get("x" + i);
            p = (Person) fh.getObject();
            p.setAge( 102 );
            wm.update( fh, p );
            wm.fireAllRules();
            assertEquals( "i=" + i, 1, map.get("inserted").intValue() ); // make sure this doesn't change
        }

        // no change
        assertEquals( 1, map.get("inserted").intValue() );
        assertEquals( 1, map.get("deleted").intValue() );
        assertEquals( 0, map.get("updated").intValue() );

        // x99 is still the blocker
        for ( int i = (max-2); i >= 0; i-- ) {
            fh = peeps.get("x" + i);
            p = (Person) fh.getObject();
            p.setAge( 90 );
            wm.update( fh, p );
            wm.fireAllRules();
            assertEquals( "i=" + i, 1, map.get("inserted").intValue() ); // make sure this doesn't change
        }

        // move x99, should no longer be a blocker, now it can increase
        fh = peeps.get("x" + (max-1));
        p = (Person) fh.getObject();
        p.setAge( 90 );
        wm.update( fh, p );
        wm.fireAllRules();
        assertEquals( 2, map.get("inserted").intValue() );
    }

    @Test(timeout=10000)
    public void testFullFastIteratorResume() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.compiler.Person \n";
        str += "query peeps( String $name, int $age ) \n";
        str += "    not $p2 : Person( $name := name, age > $age ) \n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( str );

        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == DroolsQuery.class ) {
                node = n;
                break;
            }
        }

        StatefulKnowledgeSessionImpl wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession());

        AlphaNode alphanode = ( AlphaNode ) node.getObjectSinkPropagator().getSinks()[0];
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getObjectSinkPropagator().getSinks()[0];

        NotNode n = (NotNode) liaNode.getSinkPropagator().getSinks()[0];

        DoubleNonIndexSkipBetaConstraints c = (DoubleNonIndexSkipBetaConstraints) n.getRawConstraints();
        //assertEquals( "$name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( n );
        System.out.println( bm.getLeftTupleMemory().getClass() );
        System.out.println( bm.getRightTupleMemory().getClass() );
        assertTrue(bm.getLeftTupleMemory() instanceof TupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof TupleIndexHashTable );


        final Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("inserted", new Integer(0));
        map.put("deleted", new Integer(0));
        map.put("updated", new Integer(0));
        wm.openLiveQuery("peeps", new Object[] {Variable.v, 99 }, new ViewChangedEventListener() {
            @Override
            public void rowInserted(Row row) {
            }

            @Override
            public void rowDeleted(Row row) {
            }

            @Override
            public void rowUpdated(Row row) {
            }
        });

        Map<String, InternalFactHandle> peeps = new HashMap<String, InternalFactHandle>();

        Person p = new Person( "x0", 100);
        InternalFactHandle fh = ( InternalFactHandle ) wm.insert( p );

        peeps.put(p.getName(), fh);

        for ( int i = 1; i < 100; i++ ) {
            p = new Person( "x" + i, 101);
            fh = ( InternalFactHandle ) wm.insert( p );
            wm.fireAllRules();
            peeps.put(p.getName(), fh);
        }

        List<RightTuple> list = new ArrayList<RightTuple>(100);
        FastIterator it = n.getRightIterator( bm.getRightTupleMemory() );
        for ( RightTuple rt =n.getFirstRightTuple(null, bm.getRightTupleMemory(), null, it); rt != null; rt = (RightTuple)it.next(rt) ) {
            list.add(rt);
        }
        assertEquals( 100, list.size() );

        // check we can resume from each entry in the list above.
        for ( int i = 0; i < 100; i++ ) {
            RightTuple rightTuple = list.get(i);
            it = n.getRightIterator( bm.getRightTupleMemory(), rightTuple ); // resumes from the current rightTuple
            int j = i + 1;
            for ( RightTuple rt = ( RightTuple ) it.next(rightTuple); rt != null; rt = (RightTuple)it.next(rt) ) {
                assertSame( list.get(j), rt);
                j++;
            }
        }
    }

    public static ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }

    @Test(timeout=10000)
    public void testRangeIndex() {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1\n" +
                "when\n" +
                "   $s : String()" +
                "   exists Cheese( type > $s )\n" +
                "then\n" +
                "   System.out.println( $s );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( "cheddar" );
        ksession.insert( "gorgonzola" );
        ksession.insert( "stilton" );
        ksession.insert( new Cheese( "gorgonzola", 10 ) );
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testRangeIndex2() {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1\n" +
                "when\n" +
                "   $s : String()" +
                "   exists Cheese( type < $s )\n" +
                "then\n" +
                "   System.out.println( $s );\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( "gorgonzola" );
        ksession.insert( new Cheese( "cheddar", 10 ) );
        ksession.insert( new Cheese( "gorgonzola", 10 ) );
        ksession.insert( new Cheese( "stilton", 10 ) );
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testNotNode() {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 salience 10\n" +
                "when\n" +
                "   Person( $age : age )" +
                "   not Cheese( price < $age )\n" +
                "then\n" +
                "   System.out.println( $age );\n" +
                "end\n" +
                "rule R2 salience 1\n" +
                "when\n" +
                "   $p : Person( age == 10 )" +
                "then\n" +
                "   modify($p) { setAge(15); }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "mario", 10 ) );
        ksession.insert( new Cheese( "gorgonzola", 20 ) );
        assertEquals(3, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testNotNodeModifyRight() {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 salience 10 when\n" +
                "   Person( $age : age )\n" +
                "   not Cheese( price < $age )\n" +
                "then\n" +
                "   System.out.println( $age );\n" +
                "end\n" +
                "rule R3 salience 5 when\n" +
                "   $c : Cheese( price == 8 )\n" +
                "then\n" +
                "   modify($c) { setPrice(15); }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "A", 10 ) );
        ksession.insert( new Cheese( "C1", 20 ) );
        ksession.insert( new Cheese( "C2", 8 ) );
        assertEquals(2, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testRange() {
        String str = "import org.drools.compiler.*;\n" +
                "rule R1 salience 10 when\n" +
                "   Person( $age : age, $doubleAge : doubleAge )\n" +
                "   not Cheese( this.price > $age && < $doubleAge )\n" +
                "then\n" +
                "   System.out.println( $age );\n" +
                "end\n" +
                "rule R3 salience 5 when\n" +
                "   $c : Cheese( price == 15 )\n" +
                "then\n" +
                "   System.out.println( \"modify\" );\n" +
                "   modify($c) { setPrice(8); }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert( new Person( "A", 10 ) );
        ksession.insert( new Cheese( "C1", 30 ) );
        ksession.insert( new Cheese( "C2", 15 ) );
        assertEquals(2, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test(timeout=10000)
    public void testRange2() throws Exception {
        String rule = "package org.drools.compiler.test\n" +
                "declare A\n" +
                "    a: int\n" +
                "end\n" +
                "declare B\n" +
                "    b: int\n" +
                "end\n" +
                "declare C\n" +
                "    c: int\n" +
                "end\n" +
                "rule R1 when\n" +
                "   A( $a : a )\n" +
                "   B( $b : b )\n" +
                "   exists C( c > $a && < $b )\n" +
                "then\n" +
                "   System.out.println( $a + \", \" + $b );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString( rule );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactType aType = kbase.getFactType( "org.drools.compiler.test", "A" );
        FactType bType = kbase.getFactType( "org.drools.compiler.test", "B" );
        FactType cType = kbase.getFactType( "org.drools.compiler.test", "C" );

        Object a1 = aType.newInstance();
        aType.set( a1, "a", 5 );
        ksession.insert( a1 );
        Object a2 = aType.newInstance();
        aType.set( a2, "a", 11 );
        ksession.insert( a2 );

        Object b1 = bType.newInstance();
        bType.set( b1, "b", 10 );
        ksession.insert( b1 );
        Object b2 = bType.newInstance();
        bType.set( b2, "b", 6 );
        ksession.insert( b2 );

        Object c = cType.newInstance();
        cType.set( c, "c", 7 );
        ksession.insert( c );

        ksession.fireAllRules();
    }
    @Test
    public void testHashingAfterRemoveRightTuple() {
        // DROOLS-1326
        String drl = "package "+this.getClass().getPackage().getName()+";\n" +
                     "import "+MyPojo.class.getCanonicalName()+"\n" +
                     "global java.util.List list;\n" +
                     "rule R1\n" +
                     "when\n" +
                     "    $my: MyPojo(\n" +
                     "        vBoolean == true,\n" +
                     "        $s : vString != \"y\",\n" +
                     "        $l : vLong\n" +
                     "    )\n" +
                     "    not MyPojo(\n" +
                     "        vBoolean == true,\n" +
                     "        vString == $s,\n" +
                     "        vLong != $l\n" +
                     "    )\n" +
                     "then\n" +
                     "    list.add($my.getName());\n" +
                     "end";

        KieSession session = new KieHelper().addContent( drl, ResourceType.DRL ).build().newKieSession();

        List<Long> check = new ArrayList<Long>();
        session.setGlobal("list", check);

        MyPojo a = new MyPojo("A", true, "x", 0);
        MyPojo b = new MyPojo("B", true, "x", 7);
        MyPojo c = new MyPojo("C", false, "y", 7);

        FactHandle fh_a = session.insert( a );
        FactHandle fh_b = session.insert(b);
        FactHandle fh_c = session.insert(c);

        session.fireAllRules();
        assertFalse( check.contains("A") );         // A should be blocked by B.

        c.setvBoolean(true);
        c.setvString("x");
        session.update(fh_c, c);

        b.setvBoolean(false);
        b.setvString("y");
        session.update(fh_b, b);

        session.fireAllRules();
        assertFalse( check.contains("A") );       // A is no longer blocked by B, *however* it is now blocked by C !
    }

    public static class MyPojo {
        private final String name;
        private boolean vBoolean;
        private String vString;
        private long vLong;

        public MyPojo(String name, boolean vBoolean, String vString, long vLong) {
            this.name = name;
            this.vBoolean = vBoolean;
            this.vString = vString;
            this.vLong = vLong;
        }

        public String getName() {
            return name;
        }

        public boolean isvBoolean() {
            return vBoolean;
        }

        public String getvString() {
            return vString;
        }

        public long getvLong() {
            return vLong;
        }

        public void setvBoolean(boolean vBoolean) {
            this.vBoolean = vBoolean;
        }

        public void setvString(String vString) {
            this.vString = vString;
        }

        public void setvLong(long vLong) {
            this.vLong = vLong;
        }

        @Override
        public String toString() {
            return "MyPojo: " + name;
        }
    }

    @Test
    public void testRequireLeftReorderingWithRangeIndex() {
        // DROOLS-1326
        String drl = "import " + Queen.class.getCanonicalName() + ";\n"
                     + "\n"
                     + "rule \"multipleQueensHorizontal\"\n"
                     + "when\n"
                     + "    Queen($id : id, row != null, $i : rowIndex)\n"
                     + "    Queen(id > $id, rowIndex == $i)\n"
                     + "then\n"
                     + "end\n";
        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        Queen queen1 = new Queen(1);
        Queen queen2 = new Queen(2);
        Integer row1 = 1;
        Integer row2 = 2;

        FactHandle fq1 = kieSession.insert(queen1);
        FactHandle fq2 = kieSession.insert(queen2);
        // initially both queens have null row
        Assert.assertEquals( 0, kieSession.fireAllRules() );

        // now Q1 is the only queen on row1
        kieSession.update(fq1, queen1.setRow(row1));
        Assert.assertEquals(0, kieSession.fireAllRules());

        // Q1 moved to row2 but it's still alone
        kieSession.update(fq1, queen1.setRow(row2));
        Assert.assertEquals(0, kieSession.fireAllRules());

        // now Q2 is on row2 together with Q1 -> rule should fire
        kieSession.update(fq2, queen2.setRow(row2));
        Assert.assertEquals(1, kieSession.fireAllRules());
    }

    public static class Queen {

        private final int id;
        private Integer row;

        public Queen(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public Integer getRow() {
            return row;
        }

        public Queen setRow(Integer row) {
            this.row = row;
            return this;
        }

        public int getRowIndex() {
            if (row == null) {
                return Integer.MIN_VALUE;
            }
            return row;
        }
    }
}
