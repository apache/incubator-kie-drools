package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.List;

import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.util.index.LeftTupleIndexHashTable;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleIndexHashTable;
import org.drools.core.util.index.RightTupleList;
import org.drools.definition.type.FactType;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.CompositeObjectSinkAdapter;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.ObjectSinkNodeList;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemoryInterface;
import org.drools.rule.IndexableConstraint;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;


public class IndexingTest extends CommonTestMethodBase {

    @Test
    public void testBuildsIndexedAlphaNodes() {
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   Person(name == \"Mark\", age == 37)\n";
        drl += "   Person(name == \"Mark\", happy == true)\n";
        drl += "then\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ObjectTypeNode otn = getObjectTypeNode(kbase, Person.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;

        AlphaNode alphaNode1 = ( AlphaNode ) otn.getSinkPropagator().getSinks()[0];
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter)alphaNode1.getSinkPropagator();
        ObjectSinkNodeList hashableSinks = sinkAdapter.getHashableSinks();
        assertNotNull(hashableSinks);
        assertEquals(2, hashableSinks.size());

        AlphaNode alphaNode2 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[0];
        assertSame(hashableSinks.getFirst(), alphaNode2);

        AlphaNode alphaNode3 = ( AlphaNode ) alphaNode1.getSinkPropagator().getSinks()[1];
        assertSame(hashableSinks.getLast(), alphaNode3);
    }

    @Test
    public void testBuildsIndexedMemory() {
        // tests indexes are correctly built        
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        ObjectTypeNode node = getObjectTypeNode(kbase, Person.class );
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
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
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable);
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j3.getRawConstraints();
        assertEquals( "name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier() );
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j3 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j4.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j4 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleList );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleList );
        
        c = ( SingleBetaConstraints ) j5.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j5 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );   
        
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
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );   
        
        c = ( SingleBetaConstraints ) j8.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j8 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );     
        
        c = ( SingleBetaConstraints ) j9.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j9 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleList );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleList );  
        
        c = ( SingleBetaConstraints ) j10.getRawConstraints();
        assertEquals("name", ((IndexableConstraint)c.getConstraint()).getFieldIndex().getDeclaration().getIdentifier());
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j10 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );  
        
        c = ( SingleBetaConstraints ) j11.getRawConstraints();
        assertEquals("$p1", c.getConstraint().getRequiredDeclarations()[0].getIdentifier());
        assertFalse( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j11 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleList);
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleList );          
    }
    
    @Test
    public void testIndexingOnQueryUnification() throws Exception {
        String str = "";
        str += "package org.drools.test  \n";
        str += "import org.drools.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == DroolsQuery.class ) {
                node = n;
                break;
            }
        }    
        
        ReteooWorkingMemoryInterface wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        AlphaNode alphanode = ( AlphaNode ) node.getSinkPropagator().getSinks()[0];
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) alphanode.getSinkPropagator().getSinks()[0];
        JoinNode j = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0]; // $p2
        
        TripleNonIndexSkipBetaConstraints c = ( TripleNonIndexSkipBetaConstraints ) j.getRawConstraints();
        //assertEquals( "$name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );        
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( j );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );        
    }

    public ObjectTypeNode getObjectTypeNode(KnowledgeBase kbase, Class<?> nodeClass) {
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == nodeClass ) {
                return n;
            }
        }
        return null;
    }
    @Test
    public void testRangeIndex() {
        String str = "import org.drools.*;\n" +
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

    @Test
    public void testRangeIndex2() {
        String str = "import org.drools.*;\n" +
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

    @Test
    public void testNotNode() {
        String str = "import org.drools.*;\n" +
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

    @Test
    public void testNotNodeModifyRight() {
        String str = "import org.drools.*;\n" +
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

    @Test
    public void testRange() {
        String str = "import org.drools.*;\n" +
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

    @Test
    public void testRange2() throws Exception {
        String rule = "package org.drools.test\n" +
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

        FactType aType = kbase.getFactType( "org.drools.test", "A" );
        FactType bType = kbase.getFactType( "org.drools.test", "B" );
        FactType cType = kbase.getFactType( "org.drools.test", "C" );

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
}
