/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.bind;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.expr;
import static org.drools.model.PatternDSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PatternDSLTest {

    @Test
    public void testBeta() {
        Result result = new Result();
        Variable<Person> markV = declarationOf( Person.class );
        Variable<Person> olderV = declarationOf( Person.class );

        Rule rule = rule( "beta" )
                .build(
                        pattern(markV,
                                expr("exprA", p -> p.getName().equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name", "age" ))
                        ),
                        pattern(olderV,
                                expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" )),
                                expr("exprC", markV, (p1, p2) -> p1.getAge() > p2.getAge(),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), p -> p.getAge() ),
                                        reactOn( "age" ))
                        ),
                        on(olderV, markV).execute((p1, p2) -> result.setValue( p1.getName() + " is older than " + p2.getName()))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testBetaWithBinding() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);

        Rule rule = rule( "beta" )
                .build(
                        pattern(markV,
                                expr("exprA", p -> p.getName().equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name", "age" )),
                                bind(markAge, p -> p.getAge(), reactOn("age"))
                        ),
                        pattern(olderV,
                                expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" )),
                                expr("exprC", markAge, (p1, age) -> p1.getAge() > age,
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), int.class::cast ),
                                        reactOn( "age" ))
                        ),
                        on(olderV, markV).execute((drools, p1, p2) -> drools.insert(p1.getName() + " is older than " + p2.getName()))
                );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        Collection<String> results = getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark");

        ksession.delete(marioFH);
        ksession.fireAllRules();

        mark.setAge(34);
        ksession.update(markFH, mark, "age");

        ksession.fireAllRules();
        results = getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark", "Edson is older than Mark");
    }

}
