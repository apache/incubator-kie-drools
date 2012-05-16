package org.drools.rule.builder.dialect.java;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.base.ClassObjectType;
import org.drools.base.mvel.MVELPredicateExpression;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.FieldValue;
import org.drools.spi.PredicateExpression;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaDialectTest {
    
    @Test
    public void testEvalDetectionInAlphaNode() {
        // Tests evals are generated and executed with Java dialect
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1 : Person( eval( name \n != null ), name == ( new String(\"xxx\") ) )\n";
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
        
        AlphaNode alphanode = (AlphaNode) node.getSinkPropagator().getSinks()[0];
        PredicateConstraint c = ( PredicateConstraint ) alphanode.getConstraint();
        assertTrue( c.getPredicateExpression() instanceof PredicateExpression );
        assertTrue( c.getPredicateExpression() instanceof CompiledInvoker );
        assertTrue( !(c.getPredicateExpression() instanceof MVELPredicateExpression ) );
        
        alphanode = (AlphaNode) alphanode.getSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MvelConstraint) {
            FieldValue fieldVal = (( MvelConstraint ) constraint).getField();
            assertEquals( "xxx", fieldVal.getValue() );
        }
    }
    

    @Test
    public void testEvalDetectionInBetaNode() {
        // Tests evals are generated and executed with Java dialect
        
        String drl = "";
        drl += "package org.test\n";
        drl += "import org.drools.Person\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $s  : String()\n";
        drl += "   $p1 : Person( eval( name \n != $s ), name == ( new String($s+\"xxx\") ) )\n";
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
        
        BetaNode betaanode = (BetaNode) node.getSinkPropagator().getSinks()[0];
        BetaNodeFieldConstraint[] constraint = betaanode.getConstraints();
        PredicateConstraint c = ( PredicateConstraint ) constraint[0];
        assertTrue( c.getPredicateExpression() instanceof PredicateExpression );
        assertTrue( c.getPredicateExpression() instanceof CompiledInvoker );
        assertTrue( !(c.getPredicateExpression() instanceof MVELPredicateExpression ) );
    }
}
