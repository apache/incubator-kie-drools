/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.drools.base.prototype.PrototypeObjectType;
import org.drools.core.ClockType;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.ConstraintOperator;
import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelImpl;
import org.drools.model.prototype.PrototypeExpression;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeEvent;
import org.kie.api.prototype.PrototypeEventInstance;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
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
import static org.drools.model.codegen.execmodel.BaseModelTest.getObjectsIntoList;
import static org.drools.model.prototype.PrototypeDSL.protoPattern;
import static org.drools.model.prototype.PrototypeDSL.variable;
import static org.drools.model.prototype.PrototypeExpression.fixedValue;
import static org.drools.model.prototype.PrototypeExpression.prototypeField;
import static org.drools.model.prototype.PrototypeExpression.thisPrototype;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

public class PrototypeTest {

    private static final ConstraintOperator listContainsOp = new ConstraintOperator() {
        @Override
        public <T, V> BiPredicate<T, V> asPredicate() {
            return (t,v) -> t instanceof Collection && ((Collection) t).contains(v);
        }

        @Override
        public String toString() {
            return "LIST_CONTAINS";
        }
    };

    private static final ConstraintOperator existsFieldOp = new ConstraintOperator() {
        @Override
        public <T, V> BiPredicate<T, V> asPredicate() {
            return (t,v) -> ((PrototypeFactInstance) t).has((String) v);
        }

        @Override
        public String toString() {
            return "EXISTS_FIELD";
        }
    };

    @Test
    public void testAlphaWithPropertyReactivity() {
        testAlpha(prototype( "org.drools.Person").withField("name", String.class).withField("age", Integer.class).asFact(), true);
    }

    @Test
    public void testAlphaWithoutPropertyReactivity() {
        testAlpha(prototype( "org.drools.Person" ).asFact(), false);
    }

    private void testAlpha(PrototypeFact personFact, boolean hasFields) {
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

        assertThat(hasPrototypeObjectType(ksession, "org.drools.Person")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Found a 40 years old Mark"));

        mark.put("age", 41 );
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
        PrototypeFact testPrototype = prototype( "test" ).asFact();
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

        PrototypeFactInstance testFact = testPrototype.newInstance();
        testFact.put("fieldA", 12 );
        testFact.put("fieldB", 8 );

        FactHandle fh = ksession.insert( testFact );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact.put("fieldC", 5 );
        ksession.update(fh, testFact, "fieldC");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testExistsField() {
        // DROOLS-7075
        PrototypeFact testPrototype = prototype( "test" ).asFact();
        PrototypeVariable testV = variable(testPrototype);

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(testV)
                                .expr( thisPrototype(), existsFieldOp, fixedValue("fieldA") ),
                        on(testV).execute((drools, x) ->
                            drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance testFact = testPrototype.newInstance();
        testFact.put("fieldB", 8 );

        FactHandle fh = ksession.insert( testFact );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact.put("fieldA", null );
        ksession.update(fh, testFact, "fieldA");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testNotNull() {
        PrototypeFact personFact = prototype( "org.drools.Person" ).asFact();
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

        PrototypeFactInstance unknown = personFact.newInstance();
        unknown.put("age", 40 );
        ksession.insert( unknown );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 40 );
        ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testBeta() {
        Result result = new Result();

        PrototypeFact personFact = prototype( "org.drools.Person").withField("name").withField("age").asFact();

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

        assertThat(hasPrototypeObjectType(ksession, "org.drools.Person")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 37 );

        PrototypeFactInstance edson = personFact.newInstance();
        edson.put("name", "Edson" );
        edson.put("age", 35 );

        PrototypeFactInstance mario = personFact.newInstance();
        mario.put("name", "Mario" );
        mario.put("age", 40 );

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.put("age", 34 );
        ksession.update( markFH, mark );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
    }

    @Test
    public void testExpressionBetaConstraint() {
        // DROOLS-7075
        PrototypeFact testPrototype = prototype( "test" ).asFact();
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

        PrototypeFactInstance testFact1 = testPrototype.newInstance();
        testFact1.put("fieldA", 12 );
        FactHandle fh1 = ksession.insert( testFact1 );

        PrototypeFactInstance testFact2 = testPrototype.newInstance();
        testFact2.put("fieldB", 8 );
        FactHandle fh2 = ksession.insert( testFact2 );

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        testFact2.put("fieldC", 5 );
        ksession.update(fh2, testFact2, "fieldC");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testBetaMixingClassAndFact() {
        PrototypeFact personFact = prototype( "org.drools.FactPerson").withField("name").withField("age").asFact();

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

        assertThat(hasPrototypeObjectType(ksession, "org.drools.FactPerson")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 37 );

        ksession.insert( mark );

        FactHandle edsonFH = ksession.insert(new Person("Edson", 35));
        FactHandle marioFH = ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Mario is older than Mark"));
    }

    private boolean hasPrototypeObjectType(KieSession ksession, String name ) {
        return getPrototypeObjectTypeNode(ksession, name ) != null;
    }

    private ObjectTypeNode getPrototypeObjectTypeNode(KieSession ksession, String name ) {
        EntryPointNode epn = ((InternalKnowledgeBase) ksession.getKieBase()).getRete().getEntryPointNodes().values().iterator().next();
        for (ObjectTypeNode otn : epn.getObjectTypeNodes().values()) {
            if (otn.getObjectType() instanceof PrototypeObjectType && ((PrototypeObjectType) otn.getObjectType()).getPrototype().getFullName().equals(name )) {
                return otn;
            }
        }
        return null;
    }

    @Test
    public void testIndexedAlpha() {
        PrototypeFact personFact = prototype( "org.drools.Person").withField("name").withField("age").asFact();

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

        ObjectTypeNode otn = getPrototypeObjectTypeNode(ksession, "org.drools.Person" );
        assertThat(((CompositeObjectSinkAdapter) otn.getObjectSinkPropagator()).getHashedSinkMap().size()).isEqualTo(3);

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 40 );

        ksession.insert( mark );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results).contains(new Result("Found a 40 years old Mark"));
    }

    @Test
    public void testAccumulate() {
        PrototypeFact personFact = prototype( "org.drools.Person").withField("name").withField("age").asFact();

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

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 37 );
        ksession.insert( mark );

        PrototypeFactInstance edson = personFact.newInstance();
        edson.put("name", "Edson" );
        edson.put("age", 35 );
        ksession.insert( edson );

        PrototypeFactInstance mario = personFact.newInstance();
        mario.put("name", "Mario" );
        mario.put("age", 40 );
        ksession.insert( mario );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("total = 77; average = 38.5");
    }

    @Test
    public void testQuery() {
        PrototypeFact customerFact = prototype( "customer" ).asFact();
        PrototypeFact addressFact = prototype( "address" ).asFact();

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

        PrototypeFactInstance address1 = addressFact.newInstance();
        address1.put("id", "100001");
        address1.put("customer_id", "1001");
        address1.put("street", "42 Main Street");
        FactHandle fhA1 = ksession.insert( address1 );
        ksession.fireAllRules();

        PrototypeFactInstance customer1 = customerFact.newInstance();
        customer1.put("id", "1001");
        customer1.put("first_name", "Sally");
        ksession.insert( customer1 );
        ksession.fireAllRules();

        assertThat(listener.inserts).isEqualTo(1);
        assertThat(listener.updates).isEqualTo(0);
        assertThat(listener.deletes).isEqualTo(0);

        PrototypeFactInstance address2 = addressFact.newInstance();
        address2.put("id", "100002");
        address2.put("customer_id", "1001");
        address2.put("street", "11 Post Dr.");
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
        PrototypeFact personFact = prototype( "org.drools.Person" ).asFact();
        PrototypeVariable markV = variable(personFact, "m");

        Rule rule = rule( "alpha" )
                .build(
                        not( protoPattern(markV)
                                .expr( thisPrototype(), existsFieldOp, fixedValue("name") ) ),
                        execute(drools ->
                                drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession.delete(fh);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testUndefinedOnAlpha() {
        // DROOLS-7192
        PrototypeFact prototype = prototype( "org.X" ).asFact();

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i", Index.ConstraintType.EQUAL, null ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance f1 = prototype.newInstance();
        f1.put("j", 2 );

        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f2 = prototype.newInstance();
        f2.put("i", 3 );

        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f3 = prototype.newInstance();
        f3.put("i", null );

        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testUndefinedOnBeta() {
        // DROOLS-7192
        PrototypeFact prototype = prototype( "org.X" ).asFact();

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

        PrototypeFactInstance f1 = prototype.newInstance();
        f1.put("custom.expected_index", 2 );

        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f2 = prototype.newInstance();
        f2.put("i", 3 );

        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f3 = prototype.newInstance();
        f3.put("i", 2 );

        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testArrayAccess() {
        // DROOLS-7194
        PrototypeFact prototype = prototype( "org.X" ).asFact();

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i[1]", Index.ConstraintType.EQUAL, 3 ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance f1 = prototype.newInstance();
        f1.put("i", List.of(3));
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f2 = prototype.newInstance();
        f2.put("i", Arrays.asList(1, 3, 5) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        PrototypeFactInstance f3 = prototype.newInstance();
        f3.put("i", new int[] {1, 3, 5} );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testArrayAccessWithOr() {
        // DROOLS-7194
        PrototypeFact prototype = prototype( "org.X" ).asFact();

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).or().expr( "i[1]", Index.ConstraintType.EQUAL, 3 ).expr( "i[2]", Index.ConstraintType.EQUAL, 3 ).endOr(),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance f1 = prototype.newInstance();
        f1.put("i", List.of(3));
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f2 = prototype.newInstance();
        f2.put("i", Arrays.asList(1, 3, 5) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        PrototypeFactInstance f3 = prototype.newInstance();
        f3.put("i", new int[] {1, 5, 3} );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testInnerArrayAccess() {
        // DROOLS-7194
        PrototypeFact prototype = prototype( "org.X" ).asFact();

        PrototypeVariable var1 = variable( prototype );

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(var1).expr( "i[1].a", Index.ConstraintType.EQUAL, 3 ),
                        on(var1).execute((p1, p2) -> { } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance f1 = prototype.newInstance();
        f1.put("i", List.of(Map.of("a", 3)));
        ksession.insert(f1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f2 = prototype.newInstance();
        f2.put("i", Arrays.asList(Map.of("a", 1), Map.of("b", 3), Map.of("c", 5)) );
        ksession.insert(f2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance f3 = prototype.newInstance();
        f3.put("i", Arrays.asList(Map.of("c", 1), Map.of("a", 3), Map.of("b", 5)) );
        ksession.insert(f3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testCep() {
        // DROOLS-7223
        Result result = new Result();

        PrototypeEvent stockTickPrototype = prototype("org.drools.StockTick" ).asEvent();

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

        PrototypeEventInstance tick1 = stockTickPrototype.newInstance();
        tick1.put("name", "RedHat" );
        tick1.put("value", 10 );
        ksession.insert(tick1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        PrototypeEventInstance tick2 = stockTickPrototype.newInstance();
        tick2.put("name", "RedHat" );
        tick2.put("value", 15 );
        ksession.insert(tick2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        PrototypeEventInstance tick3 = stockTickPrototype.newInstance();
        tick3.put("name", "test" );
        tick3.put("value", 12 );
        ksession.insert(tick3);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeEventInstance tick4 = stockTickPrototype.newInstance();
        tick4.put("name", "RedHat" );
        tick4.put("value", 9 );
        ksession.insert(tick4);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        PrototypeEventInstance tick5 = stockTickPrototype.newInstance();
        tick5.put("name", "RedHat" );
        tick5.put("value", 14 );
        ksession.insert(tick5);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(result.getValue()).isEqualTo("RedHat increased its value from 10 to 14");

        clock.advanceTime( 6, TimeUnit.SECONDS );

        PrototypeEventInstance tick6 = stockTickPrototype.newInstance();
        tick6.put("name", "RedHat" );
        tick6.put("value", 13 );
        ksession.insert(tick6);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(result.getValue()).isEqualTo("RedHat increased its value from 9 to 13");
    }

    @Test
    public void testUniqueEventInTimeWindow() {
        Result result = new Result();

        PrototypeEvent stockTickPrototype = prototype( "org.drools.StockTick" ).asEvent();
        PrototypeVariable tick1Var = variable( stockTickPrototype );

        PrototypeEvent controlPrototype = prototype( "org.drools.ControlEvent" ).asEvent();
        PrototypeVariable controlVar = variable( controlPrototype );

        Rule rule = rule( "stock" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 2 ),
                        not( protoPattern(controlVar).expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.put("name", t1.get("name" ) );
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

        PrototypeEventInstance tick1 = stockTickPrototype.newInstance();
        tick1.put("name", "RedHat" );
        tick1.put("value", 10 );
        ksession.insert(tick1);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("RedHat worth 10");
        result.setValue(null);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        PrototypeEventInstance tick2 = stockTickPrototype.newInstance();
        tick2.put("name", "RedHat" );
        tick2.put("value", 12 );
        ksession.insert(tick2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        PrototypeEventInstance tickTest = stockTickPrototype.newInstance();
        tickTest.put("name", "test" );
        tickTest.put("value", 42 );
        FactHandle fhTest = ksession.insert(tickTest);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("test worth 42");
        result.setValue(null);

        clock.advanceTime( 4, TimeUnit.SECONDS );

        PrototypeEventInstance tick3 = stockTickPrototype.newInstance();
        tick3.put("name", "RedHat" );
        tick3.put("value", 14 );
        ksession.insert(tick3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        PrototypeEventInstance tick4 = stockTickPrototype.newInstance();
        tick4.put("name", "RedHat" );
        tick4.put("value", 15 );
        ksession.insert(tick4);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("RedHat worth 15");
    }

    @Test
    public void testNotEvent() {
        // DROOLS-7244
        Result result = new Result();

        PrototypeEvent controlPrototype = prototype( "org.drools.ControlEvent" ).asEvent();
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

        PrototypeEventInstance tick1 = controlPrototype.newInstance();
        tick1.put("name", "start_R" );
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

        PrototypeEvent stockTickPrototype = prototype( "org.drools.StockTick" ).asEvent();
        PrototypeVariable tick1Var = variable( stockTickPrototype );

        PrototypeEvent controlPrototype = prototype( "org.drools.ControlEvent" ).asEvent();
        PrototypeVariable controlVar1 = variable( controlPrototype );
        PrototypeVariable controlVar2 = variable( controlPrototype );

        Rule rule1 = rule( "R1" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 10 ),
                        not( protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "R1" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.put("name", "R1" );
                            controlEvent.put("event", t1 );
                            System.out.println("insert: " + controlEvent);
                            drools.insert(controlEvent);
                        })
                );

        Rule rule2 = rule( "R2" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.LESS_THAN, 5 ),
                        not( protoPattern(controlVar1).expr( "name", Index.ConstraintType.EQUAL, "R2" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.put("name", "R2" );
                            controlEvent.put("event", t1 );
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
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.put("name", "start_R" );
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
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            controlEvent.put("name", "end_R" );
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

        PrototypeEventInstance tick1 = stockTickPrototype.newInstance();
        tick1.put("name", "RedHat" );
        tick1.put("value", 7 );
        ksession.insert(tick1);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 1, TimeUnit.SECONDS );

        PrototypeEventInstance tick2 = stockTickPrototype.newInstance();
        tick2.put("name", "RedHat" );
        tick2.put("value", 17 );
        ksession.insert(tick2);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 9, TimeUnit.SECONDS );

        PrototypeEventInstance tick3 = stockTickPrototype.newInstance();
        tick3.put("name", "RedHat" );
        tick3.put("value", 3 );
        ksession.insert(tick3);

        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        PrototypeEventInstance tick4 = stockTickPrototype.newInstance();
        tick4.put("name", "RedHat" );
        tick4.put("value", 2 );
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

    @Test
    public void testListContains() {
        // DROOLS-7259
        PrototypeFact prototype = prototype( "org.X" ).asFact();
        PrototypeVariable prototypeV = variable(prototype);

        Rule rule = rule( "alpha" )
                .build(
                        protoPattern(prototypeV)
                                .expr( "ids", listContainsOp, 1 ),
                        on(prototypeV).execute((drools, p) ->
                                drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance fact1 = prototype.newInstance();
        fact1.put("name", "fact1" );
        fact1.put("ids", Arrays.asList(2, 4) );
        ksession.insert(fact1);

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance fact2 = prototype.newInstance();
        fact2.put("name", "fact2" );
        fact2.put("ids", Arrays.asList(1, 3, 5) );
        ksession.insert(fact2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Found"));
    }

    @Test
    public void testCollectUniqueEventsAfterTimeWindow() {
        Result result = new Result();

        PrototypeEvent stockTickPrototype = prototype( "org.drools.StockTick" ).asEvent();
        PrototypeVariable tick1Var = variable( stockTickPrototype );

        PrototypeEvent controlPrototype = prototype( "org.drools.ControlEvent" ).asEvent();
        PrototypeVariable controlVar1 = variable( controlPrototype );
        PrototypeVariable controlVar2 = variable( controlPrototype );
        PrototypeVariable controlVar3 = variable( controlPrototype );

        Variable<List> resultsVar = declarationOf( List.class );

        Rule rule = rule( "stock" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 2 ),
                        not( protoPattern(controlVar1).expr( "rule_name", Index.ConstraintType.EQUAL, "stock" ).expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" ) ),
                        on(tick1Var).execute((drools, t1) -> {
                            PrototypeEventInstance controlEvent = controlPrototype.newInstance();
                            controlEvent.put("name", t1.get("name" ) );
                            controlEvent.put("event", t1 );
                            controlEvent.put("rule_name", "stock" );
                            drools.insert(controlEvent);
                            drools.delete(t1);
                        })
                );

        Rule startRule = rule( "start" )
                .build(
                        protoPattern(controlVar1).expr( "rule_name", Index.ConstraintType.EQUAL, "stock" ),
                        not( protoPattern(controlVar2).expr( "end_once_after", Index.ConstraintType.EQUAL, "stock" ) ),
                        on(controlVar1).execute((drools, c1) -> {
                            PrototypeEventInstance startControlEvent = controlPrototype.newInstance().withExpiration(10, TimeUnit.SECONDS);
                            startControlEvent.put("start_once_after", c1.get("rule_name" ) );
                            drools.insert(startControlEvent);

                            PrototypeEventInstance endControlEvent = controlPrototype.newInstance();
                            endControlEvent.put("end_once_after", c1.get("rule_name" ) );
                            drools.insert(endControlEvent);
                        })
                );

        Rule endRule = rule( "end" )
                .build(
                        protoPattern(controlVar1).expr( "end_once_after", Index.ConstraintType.EQUAL, "stock" ),
                        not( protoPattern(controlVar2).expr( "start_once_after", Index.ConstraintType.EQUAL, "stock" ) ),
                        accumulate( protoPattern(controlVar3).expr("rule_name", Index.ConstraintType.EQUAL, "stock" ),
                                accFunction(org.drools.core.base.accumulators.CollectListAccumulateFunction::new, controlVar3).as(resultsVar)),
                        on(controlVar1, resultsVar).execute((drools, c1, results) -> {
                            drools.delete(c1);
                            result.setValue(results.stream().peek(drools::delete).map(r -> ((PrototypeFactInstance) r).get("event")).collect(Collectors.toList()));
                        })
                );

        Rule cleanupRule = rule( "cleanup" )
                .build(
                        protoPattern(tick1Var).expr( "value", Index.ConstraintType.GREATER_THAN, 2 ),
                        protoPattern(controlVar1).expr( "rule_name", Index.ConstraintType.EQUAL, "stock" ).expr( "name", Index.ConstraintType.EQUAL, tick1Var, "name" ),
                        on(tick1Var).execute((drools, t1) -> drools.delete(t1))
                );

        Model model = new ModelImpl().addRule(rule).addRule(startRule).addRule(endRule).addRule(cleanupRule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        PrototypeEventInstance tick1 = stockTickPrototype.newInstance();
        tick1.put("name", "RedHat" );
        tick1.put("value", 10 );
        ksession.insert(tick1);
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        clock.advanceTime( 3, TimeUnit.SECONDS );

        PrototypeEventInstance tick2 = stockTickPrototype.newInstance();
        tick2.put("name", "RedHat" );
        tick2.put("value", 12 );
        ksession.insert(tick2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.getValue()).isNull();

        PrototypeEventInstance tickTest = stockTickPrototype.newInstance();
        tickTest.put("name", "test" );
        tickTest.put("value", 42 );
        ksession.insert(tickTest);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 4, TimeUnit.SECONDS );

        PrototypeEventInstance tick3 = stockTickPrototype.newInstance();
        tick3.put("name", "RedHat" );
        tick3.put("value", 14 );
        ksession.insert(tick3);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(result.getValue()).isNull();

        clock.advanceTime( 5, TimeUnit.SECONDS );

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        List<PrototypeFactInstance> events = (List<PrototypeFactInstance>) result.getValue();
        assertThat(events).hasSize(2);
        for (PrototypeFactInstance event : events) {
            if (event.get("name").equals("RedHat")) {
                assertThat(event.get("value")).isEqualTo(10);
            } else if (event.get("name").equals("test")) {
                assertThat(event.get("value")).isEqualTo(42);
            } else {
                fail("there should be only 2 events with names RedHat and test");
            }
        }
    }

    @Test
    public void testAddOnDifferentPrototypes() {
        PrototypeFact testPrototype = prototype( "test" ).asFact();
        PrototypeVariable test1V = variable(testPrototype);
        PrototypeVariable test2V = variable(testPrototype);
        PrototypeVariable test3V = variable(testPrototype);

        Rule rule = rule( "beta" )
                .build(
                        protoPattern(test1V).expr(prototypeField("i"), Index.ConstraintType.EQUAL, fixedValue(1)),
                        protoPattern(test2V).expr(prototypeField("i"), Index.ConstraintType.EQUAL, fixedValue(3)),
                        protoPattern(test3V)
                                .expr( prototypeField("i"), Index.ConstraintType.EQUAL,
                                        prototypeField(test1V, "i").add(prototypeField(test2V, "i"))),
                        on(test1V, test2V).execute((drools, x, y) ->
                                drools.insert(new Result("Found"))
                        )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance testFact1 = testPrototype.newInstance();
        testFact1.put("i", 1 );
        ksession.insert(testFact1);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance testFact2 = testPrototype.newInstance();
        testFact2.put("i", 2 );
        ksession.insert(testFact2);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance testFact3 = testPrototype.newInstance();
        testFact3.put("i", 3 );
        ksession.insert(testFact3);
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        PrototypeFactInstance testFact4 = testPrototype.newInstance();
        testFact4.put("i", 4 );
        ksession.insert(testFact4);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testComparisonWithDifferentNumericTypes() {
        // DROOLS-7332
        PrototypeFact personFact = prototype( "org.drools.FactPerson" ).withField("name").withField("age").asFact();

        PrototypeVariable markV = variable( personFact );

        Rule rule = rule( "r" )
                .build(
                        protoPattern(markV).expr(prototypeField("age"), Index.ConstraintType.GREATER_THAN, fixedValue(17.5)),
                        DSL.on(markV).execute((drools, p1) ->
                                drools.insert(new Result( p1.get("name") )))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertThat(hasPrototypeObjectType(ksession, "org.drools.FactPerson")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 18 );

        ksession.insert( mark );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Mark"));
    }

    @Test
    public void testEqualityWithDifferentNumericTypes() {
        // DROOLS-7332
        PrototypeFact personFact = prototype( "org.drools.FactPerson" ).withField("name").withField("age").asFact();

        PrototypeVariable markV = variable( personFact );

        Rule rule = rule( "r" )
                .build(
                        protoPattern(markV).expr(prototypeField("age"), Index.ConstraintType.EQUAL, fixedValue(18.0)),
                        DSL.on(markV).execute((drools, p1) ->
                                drools.insert(new Result( p1.get("name") )))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertThat(hasPrototypeObjectType(ksession, "org.drools.FactPerson")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 18 );

        ksession.insert( mark );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Mark"));
    }

    @Test
    public void bigDecimalEqualityWithDifferentScale_shouldBeEqual() {
        // DROOLS-7332
        PrototypeFact personFact = prototype( "org.drools.FactPerson" ).withField("name").withField("age").asFact();

        PrototypeVariable markV = variable( personFact );

        Rule rule = rule( "r" )
                .build(
                        protoPattern(markV).expr(prototypeField("age"), Index.ConstraintType.EQUAL, fixedValue(new BigDecimal("18.0"))),
                        DSL.on(markV).execute((drools, p1) ->
                                drools.insert(new Result( p1.get("name") )))
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        assertThat(hasPrototypeObjectType(ksession, "org.drools.FactPerson")).isTrue();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", new BigDecimal("18.00") );

        ksession.insert( mark );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).contains(new Result("Mark"));
    }

    @Test
    public void testMixFieldNameAndPrototypeExpr() {
        // DROOLS-7517
        PrototypeFact personFact = prototype( "org.drools.FactPerson" ).withField("name").withField("age").asFact();

        PrototypeVariable markV = variable( personFact );

        Rule r1 = rule( "R1" )
                .build(
                        protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mark" ),
                        on(markV).execute((drools, p) ->
                                drools.insert(new Result("R1"))
                        )
                );

        Rule r2 = rule( "R2" )
                .build(
                        protoPattern(markV)
                                .expr( "name", Index.ConstraintType.EQUAL, "Mario" ),
                        on(markV).execute((drools, p) ->
                                drools.insert(new Result("R2"))
                        )
                );

        Rule r3 = rule( "R3" )
                .build(
                        protoPattern(markV)
                                .expr( new MyFieldExpression("name"), Index.ConstraintType.EQUAL, fixedValue("Mark") ),
                        on(markV).execute((drools, p) ->
                                drools.insert(new Result("R3"))
                        )
                );

        Model model = new ModelImpl().addRule( r1 ).addRule( r2 ).addRule( r3 );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 40 );

        FactHandle fh = ksession.insert( mark );
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results).containsExactlyInAnyOrder(new Result("R1"), new Result("R3"));
    }

    class MyFieldExpression implements PrototypeExpression {

        private final String fieldName;

        MyFieldExpression(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public Function1<PrototypeFactInstance, Object> asFunction(Prototype prototype) {
            return prototype.getFieldValueExtractor(fieldName)::apply;
        }

        @Override
        public Optional<String> getIndexingKey() {
            return Optional.of(fieldName);
        }

        @Override
        public String toString() {
            return "MyFieldExpression{" + fieldName + "}";
        }

        @Override
        public Collection<String> getImpactedFields() {
            return Collections.singletonList(fieldName);
        }
    }
}
