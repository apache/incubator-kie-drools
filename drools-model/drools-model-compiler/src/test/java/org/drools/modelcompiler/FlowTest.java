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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.drools.core.ClockType;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.model.Global;
import org.drools.model.Index.ConstraintType;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Query1Def;
import org.drools.model.Query2Def;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.model.operators.InOperator;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Customer;
import org.drools.modelcompiler.domain.Employee;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Relationship;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.TargetPolicy;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.drools.modelcompiler.oopathdtables.InternationalAddress;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;

import static java.util.Arrays.asList;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.bind;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.eval;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.executeScript;
import static org.drools.model.DSL.expr;
import static org.drools.model.DSL.forall;
import static org.drools.model.DSL.from;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.input;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.DSL.or;
import static org.drools.model.DSL.query;
import static org.drools.model.DSL.reactiveFrom;
import static org.drools.model.DSL.rule;
import static org.drools.model.DSL.type;
import static org.drools.model.DSL.valueOf;
import static org.drools.model.DSL.when;
import static org.drools.model.DSL.window;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.drools.modelcompiler.domain.Employee.createEmployee;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FlowTest {

    @Test
    public void testBindWithEval() {
        Variable<Person> var_$p1 = declarationOf(type(Person.class), "$p1");
        Variable<Integer> var_$a1 = declarationOf(type(Integer.class), "$a1");
        org.drools.model.Rule rule = rule("R").build(
                bind(var_$a1).as(var_$p1, (_this) -> _this.getAge()).reactOn("age"),
                expr("$expr$2$",
                     var_$a1,
                     (_this) -> _this > 39),
                on(var_$p1).execute((drools, $p1) -> {
                    drools.insert(new Result($p1.getName()));
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 38 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testBeta() {
        Result result = new Result();
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );

        Rule rule = rule( "beta" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name", "age" ), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, Person::getAge )
                                .reactOn( "age" ),
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
    public void testBetaWithDeclaration() {
        Variable<Person> markV = declarationOf(type(Person.class));
        Variable<Integer> markAge = declarationOf(type(Integer.class));
        Variable<Person> olderV = declarationOf(type(Person.class));

        Rule rule = rule("beta")
                .build(expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy(String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark")
                                .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        bind(markAge).as(markV, Person::getAge).reactOn("age"),
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy(String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark")
                                .reactOn("name"),
                        expr("exprC", olderV, markAge, (p1, age) -> p1.getAge() > age)
                                .indexedBy(int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, int.class::cast)
                                .reactOn("age"),
                        on(olderV, markV).execute((drools, p1, p2) -> drools.insert(p1.getName() + " is older than " + p2.getName())));

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

    @Test
    public void testBetaWithDeclarationBeforePattern() {
        Variable<Person> markV = declarationOf(type(Person.class));
        Variable<Integer> markAge = declarationOf(type(Integer.class));
        Variable<Person> olderV = declarationOf(type(Person.class));

        Rule rule = rule("beta")
                .build( bind(markAge).as(markV, Person::getAge ).reactOn("age"),
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy(String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark")
                                .reactOn("name"), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy(String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark")
                                .reactOn("name"),
                        expr("exprC", olderV, markAge, (p1, age) -> p1.getAge() > age)
                                .indexedBy(int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, int.class::cast)
                                .reactOn("age"),
                        on(olderV, markV).execute((drools, p1, p2) -> drools.insert(p1.getName() + " is older than " + p2.getName())));

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

    @Test
    public void testBetaWithResult() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );
        Variable<Result> resultV = declarationOf( type( Result.class ) );

        Rule rule = rule( "beta" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name", "age" ), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, Person::getAge )
                                .reactOn( "age" ),
                        on(olderV, markV, resultV)
                            .execute((p1, p2, r) -> r.setValue( p1.getName() + " is older than " + p2.getName()) )
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.insert(result);

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals( "Mario is older than Mark", result.getValue() );
    }

    @Test
    public void test3Patterns() {
        Result result = new Result();
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<String> nameV = declarationOf( type( String.class ) );

        Rule rule = rule( "myrule" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark")),
                        expr("exprB", personV, markV, (p1, p2) -> p1.getAge() > p2.getAge()),
                        expr("exprC", nameV, personV, (s, p) -> s.equals( p.getName() )),
                        on(nameV).execute( result::setValue )
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( "Mario" );
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertEquals("Mario", result.getValue());
    }

    @Test
    public void testOr() {
        Result result = new Result();
        Variable<Person> personV = declarationOf( type( Person.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<String> nameV = declarationOf( type( String.class ) );

        Rule rule = rule( "or" )
                .build(
                        or(
                            expr("exprA", personV, p -> p.getName().equals("Mark")),
                            and(
                                    expr("exprA", markV, p -> p.getName().equals("Mark")),
                                    expr("exprB", personV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                               )
                          ),
                        expr("exprC", nameV, personV, (s, p) -> s.equals( p.getName() )),
                        on(nameV).execute( result::setValue )
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( "Mario" );
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertEquals("Mario", result.getValue());
    }

    @Test
    public void testNot() {
        Result result = new Result();
        Variable<Person> oldestV = declarationOf( type( Person.class ) );
        Variable<Person> otherV = declarationOf( type( Person.class ) );

        Rule rule = rule("not")
                .build(
                        not(otherV, oldestV, (p1, p2) -> p1.getAge() > p2.getAge()),
                        on(oldestV).execute(p -> result.setValue( "Oldest person is " + p.getName()))
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("Oldest person is Mario", result.getValue());
    }

    @Test
    public void testForall() {
        Variable<Person> p1V = declarationOf( type( Person.class ) );
        Variable<Person> p2V = declarationOf( type( Person.class ) );

        Rule rule = rule("not")
                .build(
                        forall( expr( "exprA", p1V, p -> p.getName().length() == 5 ),
                                expr( "exprB", p2V, p1V, (p2, p1) -> p2 == p1 ),
                                expr( "exprC", p2V, p -> p.getAge() > 40 ) ),
                        execute(drools -> drools.insert( new Result("ok") ))
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Edson", 42 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testAccumulate1() {
        Result result = new Result();
        Variable<Person> person = declarationOf( type( Person.class));
        Variable<Integer> resultSum = declarationOf( type( Integer.class ) );
        Variable<Integer> age = declarationOf( type( Integer.class ) );

        Rule rule = rule("accumulate")
                .build(
                        bind(age).as(person, Person::getAge),
                        accumulate(expr(person, p -> p.getName().startsWith("M")),
                                   accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class, age).as(resultSum)),
                        on(resultSum).execute(sum -> result.setValue( "total = " + sum) )
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("total = 77", result.getValue());
    }


    @Test
    public void testAccumulate2() {
        Result result = new Result();
        Variable<Person> person = declarationOf( type( Person.class ) );
        Variable<Integer> resultSum = declarationOf( type( Integer.class ) );
        Variable<Double> resultAvg = declarationOf( type( Double.class ) );
        Variable<Integer> age = declarationOf( type( Integer.class ) );

        Rule rule = rule("accumulate")
                .build(
                        bind(age).as(person, Person::getAge),
                        accumulate(expr(person, p -> p.getName().startsWith("M")),
                                   accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class, age).as(resultSum),
                                   accFunction(org.drools.core.base.accumulators.AverageAccumulateFunction.class, age).as(resultAvg)),
                        on(resultSum, resultAvg)
                                .execute((sum, avg) -> result.setValue( "total = " + sum + "; average = " + avg ))
                     );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("total = 77; average = 38.5", result.getValue());
    }


    @Test
    public void testGlobalInConsequence() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Global<Result> resultG = globalOf( type( Result.class ), "org.mypkg" );

        Rule rule = rule( "org.mypkg", "global" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, 0, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        on(markV, resultG)
                              .execute((p, r) -> r.setValue( p.getName() + " is " + p.getAge() ) )
                      );

        Model model = new ModelImpl().addRule( rule ).addGlobal( resultG );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.setGlobal( resultG.getName(), result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mark is 37", result.getValue());
    }

    @Test
    public void testGlobalInConstraint() {
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Global<Result> resultG = globalOf( type( Result.class ), "org.mypkg" );
        Global<String> nameG = globalOf( type( String.class ), "org.mypkg" );

        Rule rule = rule( "org.mypkg", "global" )
                .build(
                        expr("exprA", markV, nameG, (p, n) -> p.getName().equals(n)).reactOn( "name" ),
                        on(markV, resultG).execute((p, r) -> r.setValue( p.getName() + " is " + p.getAge() ) )
                      );

        Model model = new ModelImpl().addRule( rule ).addGlobal( nameG ).addGlobal( resultG );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.setGlobal( nameG.getName(), "Mark" );

        Result result = new Result();
        ksession.setGlobal( resultG.getName(), result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mark is 37", result.getValue());
    }

    @Test
    public void testNotEmptyPredicate() {
        Rule rule = rule("R")
                .build(
                        not(input(declarationOf(type(Person.class)))),
                        execute((drools) -> drools.insert(new Result("ok")) )
                      );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ReteDumper.checkRete(ksession, node -> !(node instanceof AlphaNode) );

        Person mario = new Person("Mario", 40);

        ksession.insert(mario);
        ksession.fireAllRules();

        assertTrue( ksession.getObjects(new ClassObjectFilter( Result.class ) ).isEmpty() );
    }

    @Test
    public void testQuery() {
        Variable<Person> personV = declarationOf( type( Person.class ), "$p" );

        Query1Def<Integer> qdef = query( "olderThan", Integer.class );
        Query query = qdef.build( expr("exprA", personV, qdef.getArg1(), (p, a) -> p.getAge() > a) );

        Model model = new ModelImpl().addQuery( query );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "olderThan", 40 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }


    @Test
    public void testQueryWithNamedArg() {
        Variable<Person> personV = declarationOf( type( Person.class ), "$p" );

        Query1Def<Integer> qdef = query( "olderThan", Integer.class, "ageArg");
        Query query = qdef.build( expr("exprA", personV, qdef.getArg("ageArg", Integer.class), (p, a) -> p.getAge() > a) );

        Model model = new ModelImpl().addQuery( query );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "olderThan", 40 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testQueryInRule() {
        Variable<Person> personV = declarationOf( type( Person.class ) );

        Query2Def<Person, Integer> qdef = query( "olderThan", Person.class, Integer.class );
        Query query = qdef.build( expr("exprA", qdef.getArg1(), qdef.getArg2(), (p, a) -> p.getAge() > a) );

        Variable<Person> personVRule = declarationOf( type( Person.class ) );
        Rule rule = rule("R")
                .build(
                        qdef.call(personVRule, valueOf(40)),
                        on(personVRule).execute((drools, p) -> drools.insert(new Result(p.getName())) )
                      );

        Model model = new ModelImpl().addQuery( query ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = (Collection<Result>) ksession.getObjects( new ClassObjectFilter( Result.class ) );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testQueryInvokingQuery() {
        Variable<Relationship> relV = declarationOf( type( Relationship.class ) );

        Query2Def<String, String> query1Def = query( "isRelatedTo1", String.class, String.class );
        Query2Def<String, String> query2Def = query( "isRelatedTo2", String.class, String.class );

        Query query2 = query2Def.build(
                        expr("exprA", relV, query2Def.getArg1(), (r, s) -> r.getStart().equals( s )),
                        expr("exprB", relV, query2Def.getArg2(), (r, e) -> r.getEnd().equals( e ))
                );

        Query query1 = query1Def.build( query2Def.call(query1Def.getArg1(), query1Def.getArg2()) );

        Model model = new ModelImpl().addQuery( query2 ).addQuery( query1 );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Relationship( "A", "B" ) );
        ksession.insert( new Relationship( "B", "C" ) );

        QueryResults results = ksession.getQueryResults( "isRelatedTo1", "A", "B" );

        assertEquals( 1, results.size() );
        assertEquals( "B", results.iterator().next().get( query1Def.getArg2().getName() ) );
    }

    @Test
    public void testPositionalRecursiveQueryWithUnification() {
        Variable<Relationship> var_$pattern_Relationship$1$ = declarationOf( type( Relationship.class ) );
        Variable<Relationship> var_$pattern_Relationship$2$ = declarationOf( type( Relationship.class ) );
        Variable<String> var_$unificationExpr$1$ = declarationOf( type( String.class ) );

        Query2Def<String, String> queryDef_isRelatedTo = query( "isRelatedTo", String.class, String.class );
        Query query = queryDef_isRelatedTo.build(
                or(
                        and(
                                expr("exprA", var_$pattern_Relationship$1$, queryDef_isRelatedTo.getArg1(), (r, s) -> r.getStart().equals(s)),
                                expr("exprB", var_$pattern_Relationship$1$, queryDef_isRelatedTo.getArg2(), (r, e) -> r.getEnd().equals(e))
                        ),
                        and(
                                and(
                                        bind(var_$unificationExpr$1$).as(var_$pattern_Relationship$2$, relationship -> relationship.getStart()),
                                        expr("exprD", var_$pattern_Relationship$2$, queryDef_isRelatedTo.getArg2(), (r, e) -> r.getEnd().equals(e))
                                ),
                                queryDef_isRelatedTo.call(queryDef_isRelatedTo.getArg1(), var_$unificationExpr$1$)
                        )
                )
        );

        Model model = new ModelImpl().addQuery( query );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Relationship( "A", "B" ) );
        ksession.insert( new Relationship( "B", "C" ) );

        QueryResults results = ksession.getQueryResults( "isRelatedTo", "A", "C" );

        assertEquals( 1, results.size() );
        assertEquals( "B", results.iterator().next().get( var_$unificationExpr$1$.getName() ) );
    }

    @Test
    public void testQueryInRuleWithDeclaration() {
        Variable<Person> personV = declarationOf( type( Person.class ) );

        Query2Def<Person, Integer> qdef = query( "olderThan", Person.class, Integer.class );
        Query query = qdef.build( expr("exprA", qdef.getArg1(), qdef.getArg2(), (p, a) -> p.getAge() > a) );

        Rule rule = rule("R")
                .build(
                        expr( "exprB", personV, p -> p.getName().startsWith( "M" ) ),
                        qdef.call(personV, valueOf(40)),
                        on(personV).execute((drools, p) -> drools.insert(new Result(p.getName())) )
                     );

        Model model = new ModelImpl().addQuery( query ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Edson", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = (Collection<Result>) ksession.getObjects( new ClassObjectFilter( Result.class ) );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testQueryInvokedWithGlobal() {
        Global<Integer> ageG = globalOf(type(Integer.class), "defaultpkg", "ageG");
        Variable<Person> personV = declarationOf( type( Person.class ) );

        Query2Def<Person, Integer> qdef = query("olderThan", Person.class, Integer.class);
        Query query = qdef.build( expr("exprA", qdef.getArg1(), qdef.getArg2(), (_this, $age) -> _this.getAge() > $age) );

        Rule rule = rule("R")
                .build(
                        expr( "exprB", personV, p -> p.getName().startsWith( "M" ) ),
                        qdef.call(personV, ageG),
                        on(personV).execute((drools, p) -> drools.insert(new Result(p.getName())) )
                     );

        Model model = new ModelImpl().addGlobal( ageG ).addQuery( query ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.setGlobal("ageG", 40);

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Edson", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testNamedConsequence() {
        Variable<Result> resultV = declarationOf( type( Result.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );

        Rule rule = rule( "beta" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name", "age" ), // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
                        on(markV, resultV).execute((p, r) -> r.addValue( "Found " + p.getName())),
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, Person::getAge )
                                .reactOn( "age" ),
                        on(olderV, markV, resultV).execute((p1, p2, r) -> r.addValue( p1.getName() + " is older than " + p2.getName()))
                     );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<String> results = (Collection<String>)result.getValue();
        assertEquals(2, results.size());

        assertTrue( results.containsAll( asList("Found Mark", "Mario is older than Mark") ) );
    }

    @Test
    public void testBreakingNamedConsequence() {
        Variable<Result> resultV = declarationOf( type( Result.class ) );
        Variable<Person> markV = declarationOf( type( Person.class ) );
        Variable<Person> olderV = declarationOf( type( Person.class ) );

        Rule rule = rule( "beta" )
                .build(
                        expr("exprA", markV, p -> p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name", "age" ),
                        when("cond1", markV, p -> p.getAge() < 30).then(
                            on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found young " + p.getName()))
                        ).elseWhen("cond2", markV, p -> p.getAge() > 50).then(
                            on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found old " + p.getName()))
                        ).elseWhen().then(
                            on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found " + p.getName()))
                        ),
                        expr("exprB", olderV, p -> !p.getName().equals("Mark"))
                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, 1, Person::getName, "Mark" )
                                .reactOn( "name" ),
                        expr("exprC", olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                .indexedBy( int.class, ConstraintType.GREATER_THAN, 0, Person::getAge, Person::getAge )
                                .reactOn( "age" ),
                        on(olderV, markV, resultV).execute((p1, p2, r) -> r.addValue( p1.getName() + " is older than " + p2.getName()))
                     );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<String> results = (Collection<String>)result.getValue();
        assertEquals(1, results.size());

        assertEquals( "Found Mark", results.iterator().next() );
    }

    @Test
    public void testFrom() throws Exception {
        Variable<Result> resultV = declarationOf( type( Result.class ) );
        Variable<Adult> dadV = declarationOf( type( Adult.class ) );
        Variable<Child> childV = declarationOf( type( Child.class ), from(dadV, adult -> adult.getChildren()) );

        Rule rule = rule( "from" )
                .build(
                        expr("exprA", childV, c -> c.getAge() > 8),
                        on(childV, resultV).execute( (c,r) -> r.setValue( c.getName() ) )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Result result = new Result();
        ksession.insert( result );

        Adult dad = new Adult( "dad", 40 );
        dad.addChild( new Child( "Alan", 10 ) );
        dad.addChild( new Child( "Betty", 7 ) );
        ksession.insert( dad );
        ksession.fireAllRules();

        assertEquals("Alan", result.getValue());
   }

    @Test
    public void testFromGlobal() throws Exception {
        // global java.util.List list
        // rule R when
        //   $o : String(length > 3) from list
        // then
        //   insert($o);
        // end

        final Global<List> var_list = globalOf(type(List.class),
                                                         "defaultpkg",
                                                         "list");

        final Variable<String> var_$o = declarationOf(type(String.class),
                                                      "$o",
                                                      from(var_list, x -> x)); // cannot use Function.identity() here because target type is ?.

        Rule rule = rule("R").build(expr("$expr$1$",
                                         var_$o,
                                         (_this) -> _this.length() > 3).indexedBy(int.class,
                                                                                  org.drools.model.Index.ConstraintType.GREATER_THAN,
                                                                                  0,
                                                                                  _this -> _this.length(),
                                                                                  3)
                                                                       .reactOn("length"),
                                    on(var_$o).execute((drools, $o) -> {
                                        drools.insert($o);
                                    }));

        Model model = new ModelImpl().addGlobal(var_list).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();

        List<String> messages = Arrays.asList("a", "Hello World!", "b");
        ksession.setGlobal("list", messages);

        ksession.fireAllRules();

        List<String> results = getObjectsIntoList(ksession, String.class);
        assertFalse(results.contains("a"));
        assertTrue(results.contains("Hello World!"));
        assertFalse(results.contains("b"));
    }

    @Test
    public void testConcatenatedFrom() {
        Global<List> listG = globalOf(type(List.class), "defaultpkg", "list");
        Variable<Man> manV = declarationOf( type( Man.class ) );
        Variable<Woman> wifeV = declarationOf( type( Woman.class ), from( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( type( Child.class ), from( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( type( Toy.class ), from( childV, Child::getToys ) );

        Rule rule = rule( "froms" )
                .build(
                        expr("exprA", childV, c -> c.getAge() > 10),
                        on(toyV, listG).execute( (t,l) -> l.add(t.getName()) )
                );

        Model model = new ModelImpl().addGlobal( listG ).addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");
    }

    @Test(timeout = 5000)
    public void testNoLoopWithModel() {
        Variable<Person> meV = declarationOf( type( Person.class ) );

        Rule rule = rule( "noloop" ).attribute( Rule.Attribute.NO_LOOP, true )
                .build(
                        expr( "exprA", meV, p -> p.getAge() > 18 ).reactOn( "age" ),
                        on( meV ).execute( ( drools, p ) -> {
                            p.setAge( p.getAge() + 1 );
                            drools.update( p, "age" );
                        } )
                );

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertEquals( 41, me.getAge() );
    }

    @Test
    public void testMetadataBasics() {
        final String PACKAGE_NAME = "org.asd";
        final String RULE_NAME = "hello world";
        final String RULE_KEY = "output";
        final String RULE_VALUE = "Hello world!";

        org.drools.model.Rule rule = rule(PACKAGE_NAME,
                                          RULE_NAME).metadata(RULE_KEY, "\"" + RULE_VALUE + "\"") // equivalent of DRL form:  @output("\"Hello world!\"")
                                                    .build(execute(() -> {
                                                        System.out.println("Hello world!");
                                                    }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();

        final Map<String, Object> metadata = ksession.getKieBase().getRule(PACKAGE_NAME, RULE_NAME).getMetaData();

        Assertions.assertThat(metadata.containsKey(RULE_KEY)).isTrue();
        Assertions.assertThat(metadata.get(RULE_KEY)).isEqualTo("\"" + RULE_VALUE + "\""); // testing of the DRL form:  @output("\"Hello world!\"")
    }


    @Test
    public void testDeclaredSlidingWindow() {
        org.drools.model.WindowReference var_DeclaredWindow = window(org.drools.model.WindowDefinition.Type.TIME,
                                                                     5,
                                                                     java.util.concurrent.TimeUnit.SECONDS,
                                                                     org.drools.modelcompiler.domain.StockTick.class,
                                                                     (_this) -> _this.getCompany().equals("DROO"));


        final org.drools.model.Variable<org.drools.modelcompiler.domain.StockTick> var_$a = declarationOf(type(org.drools.modelcompiler.domain.StockTick.class),
                                                                                                          "$a",
                                                                                                          var_DeclaredWindow);
        org.drools.model.Rule rule = rule("R").build(on(var_$a).execute(($a) -> {
            System.out.println($a.getCompany());
        }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model, EventProcessingOption.STREAM);
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId() ));

        KieSession ksession = kieBase.newKieSession(sessionConfig, null);

        SessionPseudoClock clock = ksession.getSessionClock();

        clock.advanceTime(2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("ACME") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testMVELinsert() {
        final org.drools.model.Variable<java.lang.Integer> var_$pattern_Integer$1$ = declarationOf(type(java.lang.Integer.class),
                                                                                                   "$pattern_Integer$1$");

        org.drools.model.Rule rule = rule("R").build(input(var_$pattern_Integer$1$),
                                                     executeScript("mvel",
                                                                   "System.out.println(\"Hello World\");\n" +
                                                                   "drools.insert(\"Hello World\");")
                                                     );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertTrue(results.contains("Hello World"));
    }

    @Test
    public void testMVELmodify() {
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Person> var_$p = declarationOf(type(org.drools.modelcompiler.domain.Person.class),
                                                                                                       "$p");
        final org.drools.model.BitMask mask_$p = org.drools.model.BitMask.getPatternMask(org.drools.modelcompiler.domain.Person.class,
                                                                                         "age");

        org.drools.model.Rule rule = rule("R").build(input(var_$p),
                                                     on(var_$p).executeScript("mvel", "System.out.println($p); modify($p) { setAge(1); } System.out.println($p);"));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();

        List<Person> results = getObjectsIntoList(ksession, Person.class);
        assertEquals(1, results.get(0).getAge());
    }

    @Test
    public void testAccumuluateWithAnd2() {
        final org.drools.model.Variable<java.lang.Object> var_$pattern_Object$1$ = declarationOf(type(java.lang.Object.class),
                                                                                                 "$pattern_Object$1$");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Child> var_$c = declarationOf(type(org.drools.modelcompiler.domain.Child.class),
                                                                                                      "$c");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Adult> var_$a = declarationOf(type(org.drools.modelcompiler.domain.Adult.class),
                                                                                                      "$a");
        final org.drools.model.Variable<Integer> var_$parentAge = declarationOf(type(Integer.class),
                                                                                "$parentAge");
        final org.drools.model.Variable<Integer> var_$expr$5$ = declarationOf(type(Integer.class),
                                                                              "$expr$5$");
        org.drools.model.Rule rule = rule("R").build(bind(var_$expr$5$).as(var_$a,
                                                                           var_$c,
                                                                           ($a, $c) -> $a.getAge() + $c.getAge()),
                                                     accumulate(and(expr("$expr$1$",
                                                                         var_$c,
                                                                         (_this) -> _this.getAge() < 10).indexedBy(int.class,
                                                                                                                   org.drools.model.Index.ConstraintType.LESS_THAN,
                                                                                                                   0,
                                                                                                                   _this -> _this.getAge(),
                                                                                                                   10)
                                                                            .reactOn("age"),
                                                                    expr("$expr$2$",
                                                                         var_$a,
                                                                         var_$c,
                                                                         (_this, $c) -> _this.getName()
                                                                                 .equals($c.getParent())).reactOn("name")),
                                                                accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class,
                                                                            var_$expr$5$).as(var_$parentAge)),
                                                     on(var_$parentAge).execute((drools, $parentAge) -> {
                                                         drools.insert(new Result($parentAge));
                                                     }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        Adult a = new Adult( "Mario", 43 );
        Child c = new Child( "Sofia", 6, "Mario" );

        ksession.insert( a );
        ksession.insert( c );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results, hasItem(new Result(49)));
    }

    @Test
    public void testQueryOOPathAccumulateTransformed() {
        final org.drools.model.QueryDef queryDef_listSafeCities = query("listSafeCities");

        final org.drools.model.Variable<org.drools.modelcompiler.oopathdtables.Person> var_$p = declarationOf(type(org.drools.modelcompiler.oopathdtables.Person.class),
                                                                                                                  "$p");
        final org.drools.model.Variable<org.drools.modelcompiler.oopathdtables.InternationalAddress> var_$a = declarationOf(type(org.drools.modelcompiler.oopathdtables.InternationalAddress.class),
                                                                                                                                "$a",
                                                                                                                                from(var_$p,
                                                                                                                                     (_this) -> _this.getAddress()));
        final org.drools.model.Variable<java.util.List> var_$cities = declarationOf(type(java.util.List.class),
                                                                                    "$cities");
        final org.drools.model.Variable<java.lang.String> var_$city = declarationOf(type(java.lang.String.class),
                                                                                    "$city",
                                                                                    from(var_$a,
                                                                                         (_this) -> _this.getCity()));
        org.drools.model.Query listSafeCities_build = queryDef_listSafeCities.build(input(var_$p),
                                                                                    expr("$expr$2$",
                                                                                         var_$a,
                                                                                         (_this) -> _this.getState()
                                                                                                 .equals("Safecountry")).indexedBy(java.lang.String.class,
                                                                                                                                   org.drools.model.Index.ConstraintType.EQUAL,
                                                                                                                                   0,
                                                                                                                                   _this -> _this.getState(),
                                                                                                                                   "Safecountry")
                                                                                            .reactOn("state"),
                                                                                    accumulate(expr(var_$city, s -> true),
                                                                                               accFunction(org.drools.core.base.accumulators.CollectListAccumulateFunction.class,
                                                                                                           var_$city).as(var_$cities)));
        Model model = new ModelImpl().addQuery( listSafeCities_build );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();


        org.drools.modelcompiler.oopathdtables.Person person = new org.drools.modelcompiler.oopathdtables.Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        org.drools.modelcompiler.oopathdtables.Person person2 = new org.drools.modelcompiler.oopathdtables.Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults( "listSafeCities");

        assertEquals("Milan", ((List) results.iterator().next().get("$cities" )).iterator().next());
    }

    @Test
    public void testFromAccumulate() {
        final org.drools.model.Variable<Number> var_$sum = declarationOf(type(Number.class), "$sum");
        final org.drools.model.Variable<Person> var_$p = declarationOf(type(Person.class), "$p");
        final org.drools.model.Variable<Integer> var_$expr$3$ = declarationOf(type(Integer.class), "$expr$3$");

        org.drools.model.Rule rule = rule("X").build(
                bind(var_$expr$3$).as(var_$p, (_this) -> _this.getAge()),
                expr("$expr$1$",
                        var_$sum,
                        (_this) -> _this.intValue() > 0).reactOn("intValue"),
                accumulate(expr("$expr$2$",
                        var_$p,
                        (_this) -> _this.getName().startsWith("M")),
                        accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class,
                                var_$expr$3$).as(var_$sum)),
                on(var_$sum).execute((drools, $sum) -> {
                    drools.insert(new Result($sum));
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateAnd() {
        Variable<java.lang.Number> var_$sum = declarationOf(type(Number.class), "$sum");
        Variable<org.drools.modelcompiler.domain.Person> var_$p = declarationOf(type(Person.class), "$p");
        Variable<Integer> var_$expr$3$ = declarationOf(type(Integer.class), "$expr$3$");

        org.drools.model.Rule rule = rule("X").build(bind(var_$expr$3$).as(var_$p,
                (_this) -> _this.getAge()),
                accumulate(and(expr("$expr$1$",
                        var_$p,
                        (_this) -> _this.getAge() > 10).indexedBy(int.class,
                        org.drools.model.Index.ConstraintType.GREATER_THAN,
                        0,
                        _this -> _this.getAge(),
                        10)
                                .reactOn("age"),
                        expr("$expr$2$",
                                var_$p,
                                (_this) -> _this.getName()
                                        .startsWith("M"))),
                        accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class,
                                var_$expr$3$).as(var_$sum)),
                on(var_$sum).execute((drools, $sum) -> {
                    drools.insert(new Result($sum));
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testIn() {
        final org.drools.model.Variable<java.lang.String> var_$pattern_String$1$ = declarationOf(type(java.lang.String.class),
                "$pattern_String$1$");
        org.drools.model.Rule rule = rule("R").build(expr("$expr$1$",
                var_$pattern_String$1$,
                (_this) -> eval( InOperator.INSTANCE, _this, "a", "b") ),
                execute(() -> {
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();
        ksession.insert( "b" );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCustomAccumulate() {
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Customer> var_$customer = declarationOf(type(org.drools.modelcompiler.domain.Customer.class),
                "$customer");
        final org.drools.model.Variable<java.lang.String> var_$code = declarationOf(type(java.lang.String.class),
                "$code");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.TargetPolicy> var_$target = declarationOf(type(org.drools.modelcompiler.domain.TargetPolicy.class),
                "$target");
        final org.drools.model.Variable<java.util.List> var_$pattern_List$1$ = declarationOf(type(java.util.List.class),
                "$pattern_List$1$");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.TargetPolicy> var_$tp = declarationOf(type(org.drools.modelcompiler.domain.TargetPolicy.class),
                "$tp");
        final org.drools.model.BitMask mask_$target = org.drools.model.BitMask.getPatternMask(org.drools.modelcompiler.domain.TargetPolicy.class,
                "coefficient");
        org.drools.model.Rule rule = rule("Customer can only have one Target Policy for Product p1 with coefficient 1").build(bind(var_$code).as(var_$customer,
                (_this) -> _this.getCode())
                        .reactOn("code"),
                expr("$expr$2$",
                        var_$target,
                        var_$code,
                        (_this, $code) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCustomerCode(),
                                $code)).indexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getCustomerCode(),
                        $code -> $code)
                        .reactOn("customerCode"),
                expr("$expr$3$",
                        var_$target,
                        (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getProductCode(),
                                "p1")).indexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        1,
                        _this -> _this.getProductCode(),
                        "p1")
                        .reactOn("productCode"),
                expr("$expr$4$",
                        var_$target,
                        (_this) -> _this.getCoefficient() == 1).indexedBy(int.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        2,
                        _this -> _this.getCoefficient(),
                        1)
                        .reactOn("coefficient"),
                expr("$expr$5$",
                        var_$pattern_List$1$,
                        (_this) -> _this.size() > 1).indexedBy(int.class,
                        org.drools.model.Index.ConstraintType.GREATER_THAN,
                        0,
                        _this -> _this.size(),
                        1)
                        .reactOn("size"),
                accumulate(and(expr("$expr$2$",
                        var_$tp,
                        var_$code,
                        (_this, $code) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCustomerCode(),
                                $code)).indexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getCustomerCode(),
                        $code -> $code)
                                .reactOn("customerCode"),
                        expr("$expr$3$",
                                var_$tp,
                                (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getProductCode(),
                                        "p1")).indexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                1,
                                _this -> _this.getProductCode(),
                                "p1")
                                .reactOn("productCode"),
                        expr("$expr$4$",
                                var_$tp,
                                (_this) -> _this.getCoefficient() == 1).indexedBy(int.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                2,
                                _this -> _this.getCoefficient(),
                                1)
                                .reactOn("coefficient")),
                        accFunction( MyAccumulateFunction.class,
                                var_$tp ).as(var_$pattern_List$1$)),
                on(var_$target).execute((drools, $target) -> {
                    $target.setCoefficient(0);
                    drools.update($target,
                            mask_$target);
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        Customer customer = new Customer();
        customer.setCode("code1");
        TargetPolicy target1 = new TargetPolicy();
        target1.setCustomerCode("code1");
        target1.setProductCode("p1");
        target1.setCoefficient(1);
        TargetPolicy target2 = new TargetPolicy();
        target2.setCustomerCode("code1");
        target2.setProductCode("p1");
        target2.setCoefficient(1);
        TargetPolicy target3 = new TargetPolicy();
        target3.setCustomerCode("code1");
        target3.setProductCode("p1");
        target3.setCoefficient(1);

        ksession.insert(customer);
        ksession.insert(target1);
        ksession.insert(target2);
        ksession.insert(target3);
        ksession.fireAllRules();

        List<TargetPolicy> targetPolicyList = Arrays.asList(target1, target2, target3);
        long filtered = targetPolicyList.stream().filter(c -> c.getCoefficient() == 1).count();
        assertEquals(1, filtered);
    }

    public static class MyAccumulateFunction implements AccumulateFunction<MyAccumulateFunction.MyData> {

        public static class MyData implements Serializable {
            public ArrayList myList;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // functions are stateless, so nothing to serialize
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // functions are stateless, so nothing to serialize
        }

        public MyData createContext() {
            return new MyData();
        }

        public void init( MyData data) {
            data.myList = new ArrayList<Object>();
        }

        public void accumulate( MyData data, Object $tp) {
            data.myList.add( $tp );
        }

        public void reverse( MyData data, Object $tp) {
            data.myList.remove( $tp );
        }

        public Object getResult(MyData data) {
            return data.myList;
        }

        public boolean supportsReverse() {
            return true;
        }

        public Class<?> getResultType() {
            return ArrayList.class;
        }
    }

    @Test
    public void testOrConditional() {
        final org.drools.model.Global<java.util.List> var_list = globalOf(type(java.util.List.class),
                "defaultpkg",
                "list");

        final org.drools.model.Variable<org.drools.modelcompiler.domain.Employee> var_$pattern_Employee$1$ = declarationOf(type(org.drools.modelcompiler.domain.Employee.class),
                "$pattern_Employee$1$");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Address> var_$address = declarationOf(type(org.drools.modelcompiler.domain.Address.class),
                "$address",
                reactiveFrom(var_$pattern_Employee$1$,
                        (_this) -> _this.getAddress()));

        org.drools.model.Rule rule = rule("R").build(or(and(input(var_$pattern_Employee$1$),
                expr("$expr$2$",
                        var_$address,
                        (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCity(),
                                "Big City")).indexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getCity(),
                        "Big City")
                        .reactOn("city")),
                and(input(var_$pattern_Employee$1$),
                        expr("$expr$4$",
                                var_$address,
                                (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCity(),
                                        "Small City")).indexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                0,
                                _this -> _this.getCity(),
                                "Small City")
                                .reactOn("city"))),
                on(var_$address,
                        var_list).execute(($address, list) -> {
                    list.add($address.getCity());
                }));


        Model model = new ModelImpl().addRule( rule ).addGlobal( var_list );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession kieSession = kieBase.newKieSession();

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }
}
