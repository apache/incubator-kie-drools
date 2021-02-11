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

import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Prototype;
import org.drools.model.PrototypeVariable;
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

import static org.drools.model.DSL.field;
import static org.drools.model.DSL.prototype;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class FactTemplateTest {

    @Test
    public void testAlpha() {
        Prototype personFact = prototype( "org.drools", "Person", field("name", Integer.class), field("age", String.class) );

        PrototypeVariable markV = declarationOf( personFact );

        Rule rule = rule( "alpha" )
                .build(
                        pattern(markV)
                                .expr("exprA", p -> p.get( "name" ).equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> (String) p.get( "name" ), "Mark" ),
                                        reactOn( "name", "age" )),
                        on(markV).execute((drools, p) ->
                            drools.insert(new Result("Found a " + p.get( "age" ) + " years old Mark"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "Person" ) );

        Fact mark = createMapBasedFact( personFact );
        mark.setFieldValue( "name", "Mark" );
        mark.setFieldValue( "age", 40 );

        ksession.insert( mark );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result("Found a 40 years old Mark")));
    }

    @Test
    public void testBeta() {
        Result result = new Result();

        Prototype personFact = prototype( "org.drools", "Person", field("name", Integer.class), field("age", String.class) );

        PrototypeVariable markV = declarationOf( personFact );
        PrototypeVariable olderV = declarationOf( personFact );

        Rule rule = rule( "beta" )
                .build(
                        pattern(markV)
                                .expr("exprA", p -> p.get( "name" ).equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> (String) p.get( "name" ), "Mark" ),
                                        reactOn( "name", "age" )),
                        pattern(olderV)
                                .expr("exprB", p -> !p.get( "name" ).equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> (String) p.get( "name" ), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markV, (p1, p2) -> (int) p1.get( "age" ) > (int) p2.get( "age" ),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> (int) p.get( "age" ), p -> (int) p.get( "age" ) ),
                                        reactOn( "age" )),
                        on(olderV, markV).execute((p1, p2) -> result.setValue( p1.get( "name" ) + " is older than " + p2.get( "name" )))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "Person" ) );

        Fact mark = createMapBasedFact( personFact );
        mark.setFieldValue( "name", "Mark" );
        mark.setFieldValue( "age", 37 );

        Fact edson = createMapBasedFact( personFact );
        edson.setFieldValue( "name", "Edson" );
        edson.setFieldValue( "age", 35 );

        Fact mario = createMapBasedFact( personFact );
        mario.setFieldValue( "name", "Mario" );
        mario.setFieldValue( "age", 40 );

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setFieldValue( "age", 34 );
        ksession.update( markFH, mark );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testBetaMixingClassAndFact() {
        Prototype personFact = prototype( "org.drools", "FactPerson", field("name", Integer.class), field("age", String.class) );

        PrototypeVariable markV = declarationOf( personFact );

        Variable<Person> olderV = declarationOf( Person.class );

        Rule rule = rule( "beta" )
                .build(
                        pattern(markV)
                                .expr("exprA", p -> p.get( "name" ).equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> (String) p.get( "name" ), "Mark" ),
                                        reactOn( "name", "age" )),
                        pattern(olderV)
                                .expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markV, (p1, p2) -> p1.getAge() > (int) p2.get("age"),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), p -> (int) p.get("age") ),
                                        reactOn( "age" )),
                        on(olderV, markV).execute((drools, p1, p2) ->
                                drools.insert(new Result( p1.getName() + " is older than " + p2.get( "name" ))))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "FactPerson" ) );
        
        Fact mark = createMapBasedFact( personFact );
        mark.setFieldValue( "name", "Mark" );
        mark.setFieldValue( "age", 37 );

        ksession.insert( mark );

        FactHandle edsonFH = ksession.insert(new Person("Edson", 35));
        FactHandle marioFH = ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result("Mario is older than Mark")));
    }

    private boolean hasFactTemplateObjectType( KieSession ksession, String name ) {
        EntryPointNode epn = (( InternalKnowledgeBase ) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        for (ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            if (otn.getObjectType() instanceof FactTemplateObjectType && (( FactTemplateObjectType ) otn.getObjectType()).getFactTemplate().getName().equals( name )) {
                return true;
            }
        }
        return false;
    }
}
