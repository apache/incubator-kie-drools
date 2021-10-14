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

import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassObjectType;
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
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class JavaDialectTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public JavaDialectTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        // This test is explicitly designed for the Java Dialect as implemented in pure drl
        // and doesn't make any sense to try to adapt it the executable model
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }
    
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

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

        KieSession ksession = kbase.newKieSession();
        ksession.insert(new Person("xxx"));
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
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

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

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
