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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.PredicateExpression;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Parameterized.class)
public class JavaDialectBinaryEqualityTest{

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public JavaDialectBinaryEqualityTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        // This test is explicitly designed for the Java Dialect as implemented in pure drl
        // and doesn't make any sense to try to adapt it the executable model
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }
    
    @Test
    public void test1() {
        KiePackage pkg1 = getKnowledgePackage1();
        KiePackage pkg2 = getKnowledgePackage1();
        KiePackage pkg3 = getKnowledgePackage2();

        RuleImpl rule1 = ((InternalKnowledgePackage)pkg1).getRule( "rule1" );
        RuleImpl rule2 = ((InternalKnowledgePackage)pkg2).getRule("rule1");
        RuleImpl rule3 = ((InternalKnowledgePackage)pkg3).getRule( "rule1" );
        
        // test return value
        Pattern p1 = ( Pattern ) rule1.getLhs().getChildren().get( 0 );
        Constraint rvc1 = p1.getConstraints().get( 0 );
        
        Pattern p2 = ( Pattern ) rule2.getLhs().getChildren().get( 0 );        
        Constraint rvc2 = p2.getConstraints().get( 0 );
        
        assertThat(rvc2).isNotSameAs(rvc1);
        assertThat(rvc2).isEqualTo(rvc1);
        
        Pattern p3 = ( Pattern ) rule3.getLhs().getChildren().get( 0 );
        Constraint rvc3 = p3.getConstraints().get( 0 );
        
        assertThat(rvc1).isNotSameAs(rvc3);
        assertThat(rvc1).isNotEqualTo(rvc3);
        
        // test inline eval
        PredicateConstraint pc1 = getPredicateConstraint(p1);
        PredicateExpression pe1 = ( PredicateExpression ) pc1.getPredicateExpression();

        PredicateConstraint pc2 = getPredicateConstraint(p2);
        PredicateExpression pe2 = ( PredicateExpression ) pc2.getPredicateExpression();
        assertThat(pe2).isNotSameAs(pe1);
        assertThat(pe2).isEqualTo(pe1);
        
        PredicateConstraint pc3 = getPredicateConstraint(p3);
        PredicateExpression pe3 = ( PredicateExpression ) pc3.getPredicateExpression();
        assertThat(pe1).isNotSameAs(pe3);
        assertThat(pe1).isNotEqualTo(pe3);
        
       // test eval
        EvalCondition ec1 = ( EvalCondition ) rule1.getLhs().getChildren().get( 1 );
        EvalExpression ee1 =( EvalExpression) ec1.getEvalExpression();

        EvalCondition ec2 = ( EvalCondition ) rule2.getLhs().getChildren().get( 1 );
        EvalExpression ee2 =( EvalExpression) ec2.getEvalExpression();
        assertThat(ee2).isNotSameAs(ee1);
        assertThat(ee2).isEqualTo(ee1);
        
        EvalCondition ec3 = ( EvalCondition ) rule3.getLhs().getChildren().get( 1 );
        EvalExpression ee3 =( EvalExpression) ec3.getEvalExpression();
        assertThat(ee1).isNotSameAs(ee3);
        assertThat(ee1).isNotEqualTo(ee3);
        
        // test consequence
        assertThat(rule2.getConsequence()).isNotSameAs(rule1.getConsequence());
        assertThat(rule2.getConsequence()).isEqualTo(rule1.getConsequence());
        assertThat(rule1.getConsequence()).isNotSameAs(rule3.getConsequence());
        assertThat(rule1.getConsequence()).isNotEqualTo(rule3.getConsequence());
        
        // check LHS equals
        assertThat(rule2.getLhs()).isNotSameAs(rule1.getLhs());
        assertThat(rule2.getLhs()).isEqualTo(rule1.getLhs());
        
        assertThat(rule1.getLhs()).isNotSameAs(rule3.getLhs());
        assertThat(rule1.getLhs()).isNotEqualTo(rule3.getLhs());
    }

    private PredicateConstraint getPredicateConstraint(Pattern pattern) {
        for (Constraint constraint : pattern.getConstraints()) {
            if (constraint instanceof PredicateConstraint) return (PredicateConstraint)constraint;
        }
        return null;
    }
    
    public KiePackage getKnowledgePackage1() {
      
        String str = "";
        str += "package org.drools.mvel.compiler.test\n";
        str += "import " + Person.class.getName() + ";\n";
        str += "global java.util.List list\n";
        str += "rule rule1 dialect\"java\" \n";
        str += "when\n";
        str += "   $p : Person( age : age == ( 17 + 17 ), eval( age == 34 ))\n";
        str += "   eval( $p.getAge() == 34 )\n";
        str += "then\n";
        str += "   list.add( $p );\n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        
        List<Person> list = new ArrayList<Person>();
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );
        ksession.insert( new Person("darth", 34) );
        ksession.fireAllRules();

        assertThat(list.get(0)).isEqualTo(new Person( "darth", 34 ));
        
        return kbase.getKiePackage( "org.drools.mvel.compiler.test" );

    }
    
    public KiePackage getKnowledgePackage2() {
        
        String str = "";
        str += "package org.drools.mvel.compiler.test\n";
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
        

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        List<Person> list = new ArrayList<Person>();
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal( "list", list );
        ksession.insert( new Person("darth", 36) );
        ksession.fireAllRules();

        assertThat(list.get(0)).isEqualTo(new Person( "darth", 36 ));

        return kbase.getKiePackage( "org.drools.mvel.compiler.test" );
    }
}
