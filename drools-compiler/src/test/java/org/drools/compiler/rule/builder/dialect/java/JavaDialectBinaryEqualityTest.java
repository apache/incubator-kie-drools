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

package org.drools.compiler.rule.builder.dialect.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.Person;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.PredicateExpression;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;


public class JavaDialectBinaryEqualityTest{
    
    @Test
    public void test1() {
        KnowledgePackage pkg1 = getKnowledgePackage1();
        KnowledgePackage pkg2 = getKnowledgePackage1();
        KnowledgePackage pkg3 = getKnowledgePackage2();

        RuleImpl rule1 = ((InternalKnowledgePackage)pkg1).getRule( "rule1" );
        RuleImpl rule2 = ((InternalKnowledgePackage)pkg2).getRule("rule1");
        RuleImpl rule3 = ((InternalKnowledgePackage)pkg3).getRule( "rule1" );
        
        // test return value
        Pattern p1 = ( Pattern ) rule1.getLhs().getChildren().get( 0 );
        Constraint rvc1 = p1.getConstraints().get( 0 );
        
        Pattern p2 = ( Pattern ) rule2.getLhs().getChildren().get( 0 );        
        Constraint rvc2 = p2.getConstraints().get( 0 );
        
        assertNotSame( rvc1, rvc2 );
        assertEquals( rvc1, rvc2 );
        
        Pattern p3 = ( Pattern ) rule3.getLhs().getChildren().get( 0 );
        Constraint rvc3 = p3.getConstraints().get( 0 );
        
        assertNotSame( rvc1, rvc3 );
        assertThat(rvc1, not( equalTo( rvc3 ) ) );
        
        // test inline eval
        PredicateConstraint pc1 = getPredicateConstraint(p1);
        PredicateExpression pe1 = ( PredicateExpression ) pc1.getPredicateExpression();

        PredicateConstraint pc2 = getPredicateConstraint(p2);
        PredicateExpression pe2 = ( PredicateExpression ) pc2.getPredicateExpression();
        assertNotSame( pe1, pe2 );
        assertEquals( pe1, pe2 );
        
        PredicateConstraint pc3 = getPredicateConstraint(p3);
        PredicateExpression pe3 = ( PredicateExpression ) pc3.getPredicateExpression();
        assertNotSame( pe1, pe3 );
        assertThat(pe1, not( equalTo( pe3 ) ) );
        
       // test eval
        EvalCondition ec1 = ( EvalCondition ) rule1.getLhs().getChildren().get( 1 );
        EvalExpression ee1 =( EvalExpression) ec1.getEvalExpression();

        EvalCondition ec2 = ( EvalCondition ) rule2.getLhs().getChildren().get( 1 );
        EvalExpression ee2 =( EvalExpression) ec2.getEvalExpression();
        assertNotSame( ee1, ee2 );
        assertEquals(ee1, ee2 );
        
        EvalCondition ec3 = ( EvalCondition ) rule3.getLhs().getChildren().get( 1 );
        EvalExpression ee3 =( EvalExpression) ec3.getEvalExpression();
        assertNotSame( ee1,ee3 );
        assertThat(ee1, not( equalTo( ee3 ) ) );
        
        // test consequence
        assertNotSame( rule1.getConsequence(), rule2.getConsequence() );
        assertEquals(rule1.getConsequence(), rule2.getConsequence() );
        assertNotSame( rule1.getConsequence(), rule3.getConsequence() );
        assertThat(rule1.getConsequence(), not( equalTo( rule3.getConsequence() ) ) );
        
        // check LHS equals
        assertNotSame(  rule1.getLhs(), rule2.getLhs() );
        assertEquals( rule1.getLhs(), rule2.getLhs() );
        
        assertNotSame( rule1.getLhs(), rule3.getLhs() );
        assertThat(rule1.getLhs(), not( equalTo( rule3.getLhs() ) ) );
    }

    private PredicateConstraint getPredicateConstraint(Pattern pattern) {
        for (Constraint constraint : pattern.getConstraints()) {
            if (constraint instanceof PredicateConstraint) return (PredicateConstraint)constraint;
        }
        return null;
    }
    
    public KnowledgePackage getKnowledgePackage1() {
      
        String str = "";
        str += "package org.drools.compiler.test\n";
        str += "import " + Person.class.getName() + ";\n";
        str += "global java.util.List list\n";
        str += "rule rule1 dialect\"java\" \n";
        str += "when\n";
        str += "   $p : Person( age : age == ( 17 + 17 ), eval( age == 34 ))\n";
        str += "   eval( $p.getAge() == 34 )\n";
        str += "then\n";
        str += "   list.add( $p );\n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        List<Person> list = new ArrayList<Person>();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );
        ksession.insert( new Person("darth", 34) );
        ksession.fireAllRules();
        
        assertEquals( new Person( "darth", 34 ), list.get( 0 ) );
        
        return kbase.getKnowledgePackage( "org.drools.compiler.test" );
    }
    
    public KnowledgePackage getKnowledgePackage2() {
        
        String str = "";
        str += "package org.drools.compiler.test\n";
        str += "import " + Person.class.getName() + ";\n";
        str += "global java.util.List list\n";
        str += "rule rule1 dialect\"java\" \n";
        str += "when\n";
        str += "   $p : Person( age : age == ( 18 + 18 ), eval( age == 36 ))\n";
        str += "   eval( $p.getAge() == 36 )\n";
        str += "then\n";
        str += "   System.out.println( $p );\n";
        str += "   list.add( $p );\n";
        str += "end\n";
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        List<Person> list = new ArrayList<Person>();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "list", list );
        ksession.insert( new Person("darth", 36) );
        ksession.fireAllRules();
        
        assertEquals( new Person( "darth", 36 ), list.get( 0 ) );

        return kbase.getKnowledgePackage( "org.drools.compiler.test" );
    }
}
