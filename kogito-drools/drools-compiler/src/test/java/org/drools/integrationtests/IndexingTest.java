package org.drools.integrationtests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.mvel.MVELPredicateExpression;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.InternalRuleBase;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.util.LeftTupleIndexHashTable;
import org.drools.core.util.RightTupleIndexHashTable;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.PredicateExpression;
import org.drools.spi.ReturnValueExpression;
import org.junit.Test;


public class IndexingTest {
    
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
        drl += "   $p2 : Person(name == $name)\n";
        drl += "   $p3 : Person(name == $p1.name)\n";
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
        
        List<ObjectTypeNode> nodes = ((InternalRuleBase)((KnowledgeBaseImpl)kbase).ruleBase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        ReteooWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) node.getSinkPropagator().getSinks()[0];
        JoinNode j2 = ( JoinNode ) liaNode.getSinkPropagator().getSinks()[0]; // $p2
        JoinNode j3 = ( JoinNode ) j2.getSinkPropagator().getSinks()[0];  // $p3
        
        SingleBetaConstraints c = ( SingleBetaConstraints ) j2.getRawConstraints();
        assertEquals( "$name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );        
        BetaMemory bm = ( BetaMemory ) wm.getNodeMemory( j2 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );
        
        c = ( SingleBetaConstraints ) j3.getRawConstraints();
        assertEquals( "name", ((VariableConstraint)c.getConstraint()).getRequiredDeclarations()[0].getIdentifier() );
        assertTrue( c.isIndexed() );   
        bm = ( BetaMemory ) wm.getNodeMemory( j3 );
        assertTrue( bm.getLeftTupleMemory() instanceof LeftTupleIndexHashTable );
        assertTrue( bm.getRightTupleMemory() instanceof RightTupleIndexHashTable );
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
        
        ReteooWorkingMemory wm = ((StatefulKnowledgeSessionImpl)kbase.newStatefulKnowledgeSession()).session;
        
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
}
