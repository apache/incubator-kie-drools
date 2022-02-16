/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.drools.modelcompiler;

import java.util.Collection;

import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Prototype;
import org.drools.model.PrototypeVariable;
import org.drools.model.Query;
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
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.query;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.field;
import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.prototype;
import static org.drools.model.PrototypeDSL.variable;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FactTemplateTest {

    @Test
    public void testAlphaWithPropertyReactivity() {
        testAlpha(prototype( "org.drools.Person", field("name", String.class), field("age", Integer.class) ), true);
    }

    @Test
    public void testAlphaWithoutPropertyReactivity() {
        testAlpha(prototype( "org.drools.Person" ), false);
    }

    private void testAlpha(Prototype personFact, boolean hasFields) {
        PrototypeVariable markV = variable(personFact);

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mark" ),
                        on(markV).execute((drools, p) ->
                            drools.insert(new Result("Found a " + p.get( "age" ) + " years old Mark"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "Person" ) );

        Fact mark = createMapBasedFact(personFact);
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertEquals( 1, ksession.fireAllRules() );

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result("Found a 40 years old Mark")));

        mark.set( "age", 41 );
        ksession.update(fh, mark, "age");
        // property reactivity should prevent this firing
        assertEquals( hasFields ? 0 : 1, ksession.fireAllRules() );

        ksession.update(fh, mark, "age", "name");
        // saying to the engine that also name is changed to check if this time the update is processed as it should
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testBeta() {
        Result result = new Result();

        Prototype personFact = prototype( "org.drools.Person", "name", "age" );

        PrototypeVariable markV = variable( personFact );
        PrototypeVariable olderV = variable( personFact );

        Rule rule = rule( "beta" )
                .build(
                        protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mark" ),
                        protoPattern(olderV)
                                .expr( "name", Index.ConstraintType.NOT_EQUAL, "Mark" )
                                .expr( "age", Index.ConstraintType.GREATER_THAN, markV, "age" ),
                        on(olderV, markV).execute((p1, p2) -> result.setValue( p1.get( "name" ) + " is older than " + p2.get( "name" )))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "Person" ) );

        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 37 );

        Fact edson = createMapBasedFact( personFact );
        edson.set( "name", "Edson" );
        edson.set( "age", 35 );

        Fact mario = createMapBasedFact( personFact );
        mario.set( "name", "Mario" );
        mario.set( "age", 40 );

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.set( "age", 34 );
        ksession.update( markFH, mark );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testBetaMixingClassAndFact() {
        Prototype personFact = prototype( "org.drools.FactPerson", "name", "age" );

        PrototypeVariable markV = variable( personFact );

        Variable<Person> olderV = declarationOf( Person.class );

        Rule rule = rule( "beta" )
                .build(
                        protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mark" ),
                        pattern(olderV)
                                .expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markV, (p1, p2) -> p1.getAge() > (int) p2.get("age"),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), p -> p.get("age") ),
                                        reactOn( "age" )),
                        on(olderV, markV).execute((drools, p1, p2) ->
                                drools.insert(new Result( p1.getName() + " is older than " + p2.get( "name" ))))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertTrue( hasFactTemplateObjectType( ksession, "FactPerson" ) );
        
        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 37 );

        ksession.insert( mark );

        FactHandle edsonFH = ksession.insert(new Person("Edson", 35));
        FactHandle marioFH = ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result("Mario is older than Mark")));
    }

    private boolean hasFactTemplateObjectType( KieSession ksession, String name ) {
        return getFactTemplateObjectTypeNode( ksession, name ) != null;
    }

    private ObjectTypeNode getFactTemplateObjectTypeNode( KieSession ksession, String name ) {
        EntryPointNode epn = (( InternalKnowledgeBase ) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        for (ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            if (otn.getObjectType() instanceof FactTemplateObjectType && (( FactTemplateObjectType ) otn.getObjectType()).getFactTemplate().getName().equals( name )) {
                return otn;
            }
        }
        return null;
    }

    @Test
    public void testIndexedAlpha() {
        Prototype personFact = prototype( "org.drools.Person", "name", "age" );

        PrototypeVariable personV = variable( personFact );

        Rule rule1 = rule( "alpha1" )
                .build(
                        protoPattern(personV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mark" ),
                        on(personV).execute((drools, p) ->
                                drools.insert(new Result("Found a " + p.get( "age" ) + " years old Mark"))
                        )
                );

        Rule rule2 = rule( "alpha2" )
                .build(
                        protoPattern(personV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mario" ),
                        on(personV).execute((drools, p) ->
                                drools.insert(new Result("Found a " + p.get( "age" ) + " years old Mario"))
                        )
                );

        Rule rule3 = rule( "alpha3" )
                .build(
                        protoPattern(personV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Edson" ),
                        on(personV).execute((drools, p) ->
                                drools.insert(new Result("Found a " + p.get( "age" ) + " years old Edson"))
                        )
                );

        Model model = new ModelImpl().addRule( rule1 ).addRule( rule2 ).addRule( rule3 );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ObjectTypeNode otn = getFactTemplateObjectTypeNode( ksession, "Person" );
        assertEquals(3, ((CompositeObjectSinkAdapter) otn.getObjectSinkPropagator()).getHashedSinkMap().size());

        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );

        ksession.insert( mark );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertThat(results, hasItem(new Result("Found a 40 years old Mark")));
    }

    @Test
    public void testAccumulate() {
        Prototype personFact = prototype( "org.drools.Person", "name", "age" );

        PrototypeVariable person = variable( personFact );

        Result result = new Result();
        Variable<Integer> resultSum = declarationOf(  Integer.class );
        Variable<Double> resultAvg = declarationOf(  Double.class );
        Variable<Integer> age = declarationOf(  Integer.class );

        Rule rule = rule("accumulate")
                .build(
                        accumulate( pattern( person ).expr(p -> ((String)p.get("name")).startsWith("M")).bind(age, p -> (int)p.get("age")),
                                accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, age).as(resultSum),
                                accFunction(org.drools.core.base.accumulators.AverageAccumulateFunction::new, age).as(resultAvg)),
                        on(resultSum, resultAvg)
                                .execute((sum, avg) -> result.setValue( "total = " + sum + "; average = " + avg ))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 37 );
        ksession.insert( mark );

        Fact edson = createMapBasedFact( personFact );
        edson.set( "name", "Edson" );
        edson.set( "age", 35 );
        ksession.insert( edson );

        Fact mario = createMapBasedFact( personFact );
        mario.set( "name", "Mario" );
        mario.set( "age", 40 );
        ksession.insert( mario );

        ksession.fireAllRules();
        assertEquals("total = 77; average = 38.5", result.getValue());
    }

    @Test
    public void testQuery() {
        Prototype customerFact = prototype( "customer" );
        Prototype addressFact = prototype( "address" );

        PrototypeVariable customerV = variable( customerFact, "c" );
        PrototypeVariable addressV = variable( addressFact, "a" );

        Query query = query( "Q0" ).build(
                protoPattern(customerV),
                protoPattern(addressV).expr( "customer_id", Index.ConstraintType.EQUAL, customerV, "id" ));

        Model model = new ModelImpl().addQuery( query );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        MaterializedViewChangedEventListener listener = new MaterializedViewChangedEventListener();
        ksession.openLiveQuery("Q0", new Object[0], listener);

        Fact address1 = createMapBasedFact( addressFact );
        address1.set("id", "100001");
        address1.set("customer_id", "1001");
        address1.set("street", "42 Main Street");
        FactHandle fhA1 = ksession.insert( address1 );
        ksession.fireAllRules();

        Fact customer1 = createMapBasedFact( customerFact );
        customer1.set("id", "1001");
        customer1.set("first_name", "Sally");
        ksession.insert( customer1 );
        ksession.fireAllRules();

        assertEquals(1, listener.inserts);
        assertEquals(0, listener.updates);
        assertEquals(0, listener.deletes);

        Fact address2 = createMapBasedFact( addressFact );
        address2.set("id", "100002");
        address2.set("customer_id", "1001");
        address2.set("street", "11 Post Dr.");
        ksession.insert( address2 );
        ksession.fireAllRules();

        assertEquals(2, listener.inserts);
        assertEquals(0, listener.updates);
        assertEquals(0, listener.deletes);

        ksession.delete( fhA1 );
        ksession.fireAllRules();

        assertEquals(2, listener.inserts);
        assertEquals(0, listener.updates);
        assertEquals(1, listener.deletes);
    }

    private static class MaterializedViewChangedEventListener implements ViewChangedEventListener {

        int inserts = 0;
        int updates = 0;
        int deletes = 0;

        @Override
        public void rowInserted(Row row) {
            inserts++;
            System.out.println("rowInserted: " + row.get("c") + "; " + row.get("a"));
        }

        @Override
        public void rowDeleted(Row row) {
            deletes++;
            System.out.println("rowDeleted: " + row.get("c") + "; " + row.get("a"));
        }

        @Override
        public void rowUpdated(Row row) {
            updates++;
            System.out.println("rowUpdated: " + row.get("c") + "; " + row.get("a"));
        }
    }
}
