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

package org.drools.mvel.compiler.rule.builder.dialect.java;

import java.io.StringReader;
import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.PredicateExpression;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.compiler.Person;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaDialectTest {
    
    @Test
    public void testEvalDetectionInAlphaNode() {
        // Tests evals are generated and executed with Java dialect
        String drl = "";
        drl += "package org.drools.mvel.compiler.test\n";
        drl += "import " + Person.class.getCanonicalName() + "\n";
        drl += "global java.util.List list\n";
        drl += "rule test1\n";
        drl += "when\n";
        drl += "   $p1 : Person( eval( name \n != null ), name == ( new String(\"xxx\") ) )\n";
        drl += "then\n";
        drl += "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource(new StringReader(drl)),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        assertFalse( kbuilder.hasErrors() );

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        AlphaNode alphanode = (AlphaNode) node.getObjectSinkPropagator().getSinks()[0];
        PredicateConstraint c = ( PredicateConstraint ) alphanode.getConstraint();
        assertTrue( c.getPredicateExpression() instanceof PredicateExpression );
        assertTrue( c.getPredicateExpression() instanceof CompiledInvoker );

        alphanode = (AlphaNode) alphanode.getObjectSinkPropagator().getSinks()[0];
        AlphaNodeFieldConstraint constraint = alphanode.getConstraint();

        if (constraint instanceof MVELConstraint ) {
            FieldValue fieldVal = (( MVELConstraint ) constraint).getField();
            assertEquals( "xxx", fieldVal.getValue() );
        }
    }
    

    @Test
    public void testEvalDetectionInBetaNode() {
        // Tests evals are generated and executed with Java dialect
        
        String drl = "";
        drl += "package org.drools.mvel.compiler.test\n";
        drl += "import " + Person.class.getCanonicalName() + "\n";
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

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl)kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType)n.getObjectType()).getClassType() == Person.class ) {
                node = n;
                break;
            }
        }
        
        BetaNode betaanode = (BetaNode) node.getObjectSinkPropagator().getSinks()[0];
        BetaNodeFieldConstraint[] constraint = betaanode.getConstraints();
        PredicateConstraint c = ( PredicateConstraint ) constraint[0];
        assertTrue( c.getPredicateExpression() instanceof PredicateExpression );
        assertTrue( c.getPredicateExpression() instanceof CompiledInvoker );
    }
}
