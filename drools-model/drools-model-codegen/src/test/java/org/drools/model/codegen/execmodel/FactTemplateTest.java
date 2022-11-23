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
package org.drools.model.codegen.execmodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.facttemplates.Event;
import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Prototype;
import org.drools.model.PrototypeVariable;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.after;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.query;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PrototypeDSL.field;
import static org.drools.model.PrototypeDSL.protoPattern;
import static org.drools.model.PrototypeDSL.prototype;
import static org.drools.model.PrototypeDSL.variable;
import static org.drools.model.PrototypeExpression.fixedValue;
import static org.drools.model.PrototypeExpression.prototypeField;
import static org.drools.model.codegen.execmodel.BaseModelTest.getObjectsIntoList;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedEvent;
import static org.drools.modelcompiler.facttemplate.FactFactory.createMapBasedFact;

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

        assertThat(hasFactTemplateObjectType(ksession, "Person")).isTrue();

        Fact mark = createMapBasedFact(personFact);
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Found a 40 years old Mark"));

        mark.set( "age", 41 );
        ksession.update(fh, mark, "age");
        // property reactivity should prevent this firing
        assertThat(ksession.fireAllRules()).isEqualTo(hasFields ? 0 : 1);

        ksession.update(fh, mark, "age", "name");
        // saying to the engine that also name is changed to check if this time the update is processed as it should
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testExpressionAlphaConstraint() {
        // DROOLS-7075
        Prototype testPrototype = prototype( "test" );
        PrototypeVariable testV = variable(testPrototype);

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(testV)
                                .expr( prototypeField("fieldA"), Index.ConstraintType.EQUAL,
                                        prototypeField("fieldB").add(prototypeField("fieldC")).sub(fixedValue(1)) ),
                        on(testV).execute((drools, x) ->
                            drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Fact testFact = createMapBasedFact(testPrototype);
        testFact.set( "fieldA", 12 );
        testFact.set( "fieldB", 8 );

        FactHandle fh = ksession.insert( testFact );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact.set( "fieldC", 5 );
        ksession.update(fh, testFact, "fieldC");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testExistsField() {
        // DROOLS-7075
        Prototype testPrototype = prototype( "test" );
        PrototypeVariable testV = variable(testPrototype);

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(testV)
                                .expr( prototypeField("fieldA"), Index.ConstraintType.EXISTS_PROTOTYPE_FIELD, fixedValue(true) ),
                        on(testV).execute((drools, x) ->
                            drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Fact testFact = createMapBasedFact(testPrototype);
        testFact.set( "fieldB", 8 );

        FactHandle fh = ksession.insert( testFact );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact.set( "fieldA", null );
        ksession.update(fh, testFact, "fieldA");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNotNull() {
        Prototype personFact = prototype( "org.drools.Person" );
        PrototypeVariable markV = variable(personFact);

        Rule rule = rule("alpha")
                .build(
                        protoPattern(markV)
                                .expr("name", Index.ConstraintType.NOT_EQUAL, null),
                        on(markV).execute((drools, p) ->
                                drools.insert(new Result("Found a " + p.get("age") + " years old Mark"))
                        )
                );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        Fact unknown = createMapBasedFact(personFact);
        unknown.set( "age", 40 );
        ksession.insert( unknown );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact mark = createMapBasedFact(personFact);
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );
        ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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

        assertThat(hasFactTemplateObjectType(ksession, "Person")).isTrue();

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
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.set( "age", 34 );
        ksession.update( markFH, mark );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
    }

    @Test
    public void testExpressionBetaConstraint() {
        // DROOLS-7075
        Prototype testPrototype = prototype( "test" );
        PrototypeVariable test1V = variable(testPrototype);
        PrototypeVariable test2V = variable(testPrototype);

        Rule rule = rule( "beta" )
                .build(
                        protoPattern(test1V),
                        protoPattern(test2V)
                                .expr( prototypeField("fieldA"), Index.ConstraintType.EQUAL, test1V,
                                        prototypeField("fieldB").add(prototypeField("fieldC")).sub(fixedValue(1)) ),
                        on(test1V, test2V).execute((drools, x, y) ->
                                drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Fact testFact1 = createMapBasedFact(testPrototype);
        testFact1.set( "fieldA", 12 );
        FactHandle fh1 = ksession.insert( testFact1 );

        Fact testFact2 = createMapBasedFact(testPrototype);
        testFact2.set( "fieldB", 8 );
        FactHandle fh2 = ksession.insert( testFact2 );

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact2.set( "fieldC", 5 );
        ksession.update(fh2, testFact2, "fieldC");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
                        DSL.on(olderV, markV).execute((drools, p1, p2) ->
                                drools.insert(new Result( p1.getName() + " is older than " + p2.get( "name" ))))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertThat(hasFactTemplateObjectType(ksession, "FactPerson")).isTrue();
        
        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 37 );

        ksession.insert( mark );

        FactHandle edsonFH = ksession.insert(new Person("Edson", 35));
        FactHandle marioFH = ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Mario is older than Mark"));
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
        assertThat(((CompositeObjectSinkAdapter) otn.getObjectSinkPropagator()).getHashedSinkMap().size()).isEqualTo(3);

        Fact mark = createMapBasedFact( personFact );
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );

        ksession.insert( mark );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(new Result("Found a 40 years old Mark"));
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
        assertThat(result.getValue()).isEqualTo("total = 77; average = 38.5");
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

        assertThat(listener.inserts).isEqualTo(1);
        assertThat(listener.updates).isEqualTo(0);
        assertThat(listener.deletes).isEqualTo(0);

        Fact address2 = createMapBasedFact( addressFact );
        address2.set("id", "100002");
        address2.set("customer_id", "1001");
        address2.set("street", "11 Post Dr.");
        ksession.insert( address2 );
        ksession.fireAllRules();

        assertThat(listener.inserts).isEqualTo(2);
        assertThat(listener.updates).isEqualTo(0);
        assertThat(listener.deletes).isEqualTo(0);

        ksession.delete( fhA1 );
        ksession.fireAllRules();

        assertThat(listener.inserts).isEqualTo(2);
        assertThat(listener.updates).isEqualTo(0);
        assertThat(listener.deletes).isEqualTo(1);
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

    @Test
    public void testRetract() {
        Prototype personFact = prototype( "org.drools.Person" );
        PrototypeVariable markV = variable(personFact, "m");

        Rule rule = rule( "alpha" )
                .build(
                        not( protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EXISTS_PROTOTYPE_FIELD, true ) ),
                        execute(drools ->
                                drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Fact mark = createMapBasedFact(personFact);
        mark.set( "name", "Mark" );
        mark.set( "age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession.delete(fh);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testUndefinedOnAlpha() {
        // DROOLS-7192
        Prototype prototype = prototype( "org.X" );

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i", Index.ConstraintType.EQUAL, null ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact f1 = createMapBasedFact( prototype );
        f1.set( "j", 2 );

        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f2 = createMapBasedFact( prototype );
        f2.set( "i", 3 );

        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f3 = createMapBasedFact( prototype );
        f3.set( "i", null );

        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testUndefinedOnBeta() {
        // DROOLS-7192
        Prototype prototype = prototype( "org.X" );

        PrototypeVariable var1 = variable( prototype );
        PrototypeVariable var2 = variable( prototype );

        Rule rule = rule( "beta" )
                .build(
                        protoPattern(var1),
                        protoPattern(var2)
                                .expr( "i", Index.ConstraintType.EQUAL, var1, "custom.expected_index" ),
                        on(var1, var2).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact f1 = createMapBasedFact( prototype );
        f1.set( "custom.expected_index", 2 );

        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f2 = createMapBasedFact( prototype );
        f2.set( "i", 3 );

        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f3 = createMapBasedFact( prototype );
        f3.set( "i", 2 );

        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testArrayAccess() {
        // DROOLS-7194
        Prototype prototype = prototype( "org.X" );

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i[1]", Index.ConstraintType.EQUAL, 3 ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact f1 = createMapBasedFact( prototype );
        f1.set( "i", Arrays.asList(3) );
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f2 = createMapBasedFact( prototype );
        f2.set( "i", Arrays.asList(1, 3, 5) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Fact f3 = createMapBasedFact( prototype );
        f3.set( "i", new int[] {1, 3, 5} );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testArrayAccessWithOr() {
        // DROOLS-7194
        Prototype prototype = prototype( "org.X" );

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).or().expr( "i[1]", Index.ConstraintType.EQUAL, 3 ).expr( "i[2]", Index.ConstraintType.EQUAL, 3 ).endOr(),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact f1 = createMapBasedFact( prototype );
        f1.set( "i", Arrays.asList(3) );
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f2 = createMapBasedFact( prototype );
        f2.set( "i", Arrays.asList(1, 3, 5) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Fact f3 = createMapBasedFact( prototype );
        f3.set( "i", new int[] {1, 5, 3} );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testInnerArrayAccess() {
        // DROOLS-7194
        Prototype prototype = prototype( "org.X" );

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i[1].a", Index.ConstraintType.EQUAL, 3 ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Fact f1 = createMapBasedFact( prototype );
        f1.set( "i", Arrays.asList(Map.of("a", 3)) );
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f2 = createMapBasedFact( prototype );
        f2.set( "i", Arrays.asList(Map.of("a", 1), Map.of("b", 3), Map.of("c", 5)) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Fact f3 = createMapBasedFact( prototype );
        f3.set( "i", Arrays.asList(Map.of("c", 1), Map.of("a", 3), Map.of("b", 5)) );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCep() {
        // DROOLS-7223
        Result result = new Result();

        Prototype stockTickPrototype = prototype( "org.drools.StockTick" );

        PrototypeVariable tick1Var = variable( stockTickPrototype );
        PrototypeVariable tick2Var = variable( stockTickPrototype );

        Rule rule = rule( "after" )
                .build(
                        protoPattern(tick1Var),
                        protoPattern(tick2Var)
                                .expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" )
                                .expr( "value", Index.ConstraintType.GREATER_THAN, tick1Var, "value" )
                                .expr( after(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS), tick1Var ),
                        on(tick1Var, tick2Var).execute((t1, t2) -> result.setValue( t1.get( "name" ) + " increased its value from " + t1.get( "value" ) + " to " + t2.get( "value" )))
                );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        Event tick1 = createMapBasedEvent( stockTickPrototype );
        tick1.set( "name", "RedHat" );
        tick1.set( "value", 10 );
        ksession.insert(tick1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        Event tick2 = createMapBasedEvent( stockTickPrototype );
        tick2.set( "name", "RedHat" );
        tick2.set( "value", 15 );
        ksession.insert(tick2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        Event tick3 = createMapBasedEvent( stockTickPrototype );
        tick3.set( "name", "test" );
        tick3.set( "value", 12 );
        ksession.insert(tick3);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        Event tick4 = createMapBasedEvent( stockTickPrototype );
        tick4.set( "name", "RedHat" );
        tick4.set( "value", 9 );
        ksession.insert(tick4);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        Event tick5 = createMapBasedEvent( stockTickPrototype );
        tick5.set( "name", "RedHat" );
        tick5.set( "value", 14 );
        ksession.insert(tick5);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(result.getValue()).isEqualTo("RedHat increased its value from 10 to 14");

        clock.advanceTime( 6, TimeUnit.SECONDS );

        Event tick6 = createMapBasedEvent( stockTickPrototype );
        tick6.set( "name", "RedHat" );
        tick6.set( "value", 13 );
        ksession.insert(tick6);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(result.getValue()).isEqualTo("RedHat increased its value from 9 to 13");
    }

    @Test
    public void testUniqueEventInTimeWindow() {
        Result result = new Result();

        Prototype stockTickPrototype = prototype( "org.drools.StockTick" );
        PrototypeVariable tick1Var = variable( stockTickPrototype );

        Prototype controlPrototype = prototype( "org.drools.ControlEvent" );
        PrototypeVariable controlVar = variable( controlPrototype );

        Rule rule = rule( "stock" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 2 ),
                        not( protoPattern(controlVar).expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            Event controlEvent = createMapBasedEvent( controlPrototype ).withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.set( "name", t1.get( "name" ) );
                            drools.insert(controlEvent);
                            result.setValue( t1.get( "name" ) + " worth " + t1.get( "value" ));
                        })
                );

        Rule cleanupRule = rule( "cleanup" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 2 ),
                        protoPattern(controlVar).expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" ),
                        on(tick1Var).execute((drools, t1) -> drools.delete(t1))
                );

        Model model = new ModelImpl().addRule(rule).addRule(cleanupRule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        Event tick1 = createMapBasedEvent( stockTickPrototype );
        tick1.set( "name", "RedHat" );
        tick1.set( "value", 10 );
        ksession.insert(tick1);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("RedHat worth 10");
        result.setValue(null);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        Event tick2 = createMapBasedEvent( stockTickPrototype );
        tick2.set( "name", "RedHat" );
        tick2.set( "value", 12 );
        ksession.insert(tick2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Event tickTest = createMapBasedEvent( stockTickPrototype );
        tickTest.set( "name", "test" );
        tickTest.set( "value", 42 );
        FactHandle fhTest = ksession.insert(tickTest);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("test worth 42");
        result.setValue(null);

        clock.advanceTime( 4, TimeUnit.SECONDS );

        Event tick3 = createMapBasedEvent( stockTickPrototype );
        tick3.set( "name", "RedHat" );
        tick3.set( "value", 14 );
        ksession.insert(tick3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        Event tick4 = createMapBasedEvent( stockTickPrototype );
        tick4.set( "name", "RedHat" );
        tick4.set( "value", 15 );
        ksession.insert(tick4);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("RedHat worth 15");
    }

    @Test
    public void testNotEvent() {
        // DROOLS-7244
        Result result = new Result();

        Prototype controlPrototype = prototype( "org.drools.ControlEvent" );
        PrototypeVariable controlVar1 = variable( controlPrototype );
        PrototypeVariable controlVar2 = variable( controlPrototype );

        Rule check = rule( "check" )
                .build(
                        protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "start_R" ),
                        not( protoPattern(controlVar2).expr( "name", Index.ConstraintType.EQUAL, "end_R" ).expr( after(0, TimeUnit.SECONDS, 10, TimeUnit.SECONDS), controlVar1 ) ),
                        on(controlVar1).execute((drools, c1) -> {
                            drools.delete(c1);
                            result.setValue("fired");
                        })
                );

        Model model = new ModelImpl().addRule(check);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        Event tick1 = createMapBasedEvent( controlPrototype );
        tick1.set( "name", "start_R" );
        ksession.insert(tick1);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("fired");
    }

    @Test
    public void testTimeOutBeforeAllEventsArrive() {
        // DROOLS-7244
        Result result = new Result();

        Prototype stockTickPrototype = prototype( "org.drools.StockTick" );
        PrototypeVariable tick1Var = variable( stockTickPrototype );

        Prototype controlPrototype = prototype( "org.drools.ControlEvent" );
        PrototypeVariable controlVar1 = variable( controlPrototype );
        PrototypeVariable controlVar2 = variable( controlPrototype );

        Rule rule1 = rule( "R1" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 10 ),
                        not( protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "R1" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            Event controlEvent = createMapBasedEvent( controlPrototype ).withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.set( "name", "R1" );
                            controlEvent.set( "event", t1 );
                            System.out.println("insert: " + controlEvent);
                            drools.insert(controlEvent);
                        })
                );

        Rule rule2 = rule( "R2" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.LESS_THAN, 5 ),
                        not( protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "R2" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            Event controlEvent = createMapBasedEvent( controlPrototype ).withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.set( "name", "R2" );
                            controlEvent.set( "event", t1 );
                            System.out.println("insert: " + controlEvent);
                            drools.insert(controlEvent);
                        })
                );

        Variable<Long> resultCount = declarationOf( Long.class );

        Rule acc1 = rule("acc1")
                .build(
                        not( protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "start_R" ) ),
                        accumulate( protoPattern( controlVar2 ).expr(p -> ((String)p.get("name")).startsWith("R")),
                                accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new).as(resultCount)),
                        pattern(resultCount).expr(count -> count > 0),
                        on(resultCount).execute((drools, count) -> {
                            Event controlEvent = createMapBasedEvent( controlPrototype ).withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.set( "name", "start_R" );
                            System.out.println("insert: " + controlEvent);
                            drools.insert(controlEvent);
                        })
                );

        Rule acc2 = rule("acc2")
                .build(
                        protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "start_R" ),
                        accumulate( protoPattern( controlVar2 ).expr(p -> ((String)p.get("name")).startsWith("R")),
                                accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new).as(resultCount)),
                        pattern(resultCount).expr(count -> count == 2),
                        on(resultCount).execute((drools, count) -> {
                            Event controlEvent = createMapBasedEvent( controlPrototype ).withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.set( "name", "end_R" );
                            System.out.println("insert: " + controlEvent);
                            drools.insert(controlEvent);
                        })
                );

        Rule check = rule( "check" )
                .build(
                        protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "start_R" ),
                        not( protoPattern(controlVar2).expr( "name", Index.ConstraintType.EQUAL, "end_R" ).expr( after(0, TimeUnit.SECONDS, 10, TimeUnit.SECONDS), controlVar1 ) ),
                        on(controlVar1).execute((drools, c1) -> {
                            drools.delete(c1);
                            System.out.println("FIRE!");
                            result.setValue("fired");
                        })
                );

        Rule cleanupEvents = rule( "cleanupEvents" )
                .build(
                        protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "end_R" ),
                        protoPattern(controlVar2).expr(p -> ((String)p.get("name")).startsWith("R")),
                        on(controlVar1, controlVar2).execute((drools, c1, c2) -> {
                            drools.delete(c2.get("event"));
                            drools.delete(c2);
                        })
                );

        Rule cleanupTerminal = rule( "cleanupTerminal" )
                .build(
                        protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "start_R" ),
                        protoPattern(controlVar2).expr( "name", Index.ConstraintType.EQUAL, "end_R" ),
                        on(controlVar1, controlVar2).execute((drools, c1, c2) -> {
                            drools.delete(c1);
                            drools.delete(c2);
                        })
                );

        Model model = new ModelImpl().addRule(rule1).addRule(rule2).addRule(acc1).addRule(acc2).addRule(check).addRule(cleanupEvents).addRule(cleanupTerminal);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        Event tick1 = createMapBasedEvent( stockTickPrototype );
        tick1.set( "name", "RedHat" );
        tick1.set( "value", 7 );
        ksession.insert(tick1);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 1, TimeUnit.SECONDS );

        Event tick2 = createMapBasedEvent( stockTickPrototype );
        tick2.set( "name", "RedHat" );
        tick2.set( "value", 17 );
        ksession.insert(tick2);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 9, TimeUnit.SECONDS );

        Event tick3 = createMapBasedEvent( stockTickPrototype );
        tick3.set( "name", "RedHat" );
        tick3.set( "value", 3 );
        ksession.insert(tick3);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        Event tick4 = createMapBasedEvent( stockTickPrototype );
        tick4.set( "name", "RedHat" );
        tick4.set( "value", 2 );
        ksession.insert(tick4);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 12, TimeUnit.SECONDS );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("fired");
    }
}
