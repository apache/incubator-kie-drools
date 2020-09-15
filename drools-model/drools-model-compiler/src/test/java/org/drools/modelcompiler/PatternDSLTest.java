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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.core.ClockType;
import org.drools.core.rule.QueryImpl;
import org.drools.model.BitMask;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Query2Def;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.GroupKey;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Relationship;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;

import static org.drools.model.DSL.supply;
import static org.drools.model.PatternDSL.accFunction;
import static org.drools.model.PatternDSL.accumulate;
import static org.drools.model.PatternDSL.after;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.and;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.declarationOf;
import static org.drools.model.PatternDSL.execute;
import static org.drools.model.PatternDSL.globalOf;
import static org.drools.model.PatternDSL.not;
import static org.drools.model.PatternDSL.on;
import static org.drools.model.PatternDSL.or;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.query;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.reactiveFrom;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PatternDSL.valueOf;
import static org.drools.model.PatternDSL.when;
import static org.drools.modelcompiler.BaseModelTest.getObjectsIntoList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class PatternDSLTest {

    @Test
    public void testBeta() {
        Result result = new Result();
        Variable<Person> markV = declarationOf( Person.class );
        Variable<Person> olderV = declarationOf( Person.class );

        Rule rule = rule( "beta" )
                .build(
                        pattern(markV)
                                .expr("exprA", p -> p.getName().equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name", "age" )),
                        pattern(olderV)
                                .expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markV, (p1, p2) -> p1.getAge() > p2.getAge(),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), p -> p.getAge() ),
                                        reactOn( "age" )),
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
                        pattern(markV)
                                .expr("exprA", p -> p.getName().equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name", "age" ))
                                .bind(markAge, p -> p.getAge(), reactOn("age")
                        ),
                        pattern(olderV)
                                .expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markAge, (p1, age) -> p1.getAge() > age,
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), int.class::cast ),
                                        reactOn( "age" )),
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

    @Test
    public void testOr() {
        Result result = new Result();
        Variable<Person> personV = declarationOf( Person.class );
        Variable<Person> markV = declarationOf( Person.class );
        Variable<String> nameV = declarationOf( String.class );

        Rule rule = rule( "or" )
                .build(
                        or(
                                pattern( personV ).expr("exprA", p -> p.getName().equals("Mark")),
                                and(
                                        pattern( markV ).expr("exprA", p -> p.getName().equals("Mark")),
                                        pattern( personV ).expr("exprB", markV, (p1, p2) -> p1.getAge() > p2.getAge())
                                )
                        ),
                        pattern( nameV ).expr("exprC", personV, (s, p) -> s.equals( p.getName() )),
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
        Variable<Person> oldestV = DSL.declarationOf(  Person.class );
        Variable<Person> otherV = DSL.declarationOf(  Person.class );

        Rule rule = rule("not")
                .build(
                        pattern( oldestV ),
                        not( pattern( otherV ).expr( "exprA", oldestV, (p1, p2) -> p1.getAge() > p2.getAge()) ),
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
    public void testAccumulate() {
        Result result = new Result();
        Variable<Person> person = declarationOf(  Person.class );
        Variable<Integer> resultSum = declarationOf(  Integer.class );
        Variable<Double> resultAvg = declarationOf(  Double.class );
        Variable<Integer> age = declarationOf(  Integer.class );

        Rule rule = rule("accumulate")
                .build(
                        accumulate( pattern( person ).expr(p -> p.getName().startsWith("M")).bind(age, Person::getAge),
                                accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, age).as(resultSum),
                                accFunction(org.drools.core.base.accumulators.AverageAccumulateFunction::new, age).as(resultAvg)),
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
    public void testAccumulateConstant() {
        Result result = new Result();
        Variable<Person> person = declarationOf(Person.class);
        Variable<Integer> resultSum = declarationOf(Integer.class);
        Variable<Integer> constant = declarationOf(Integer.class);

        Rule rule = D.rule("X").build(D.accumulate(D.pattern(person).expr((_this) -> _this.getName().startsWith("M")),
                                                   D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class,
                                                                 valueOf(2)).as(resultSum)),
                                      D.on(resultSum).execute((drools, $sum) -> result.setValue("total = " + $sum)));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();
        assertEquals("total = 4", result.getValue());
    }

    @Test
    public void testAccumuluateWithAnd2() {
        Variable<Object> var_$pattern_Object$1$ = declarationOf(Object.class, "$pattern_Object$1$");
        Variable<Child> var_$c = declarationOf(Child.class, "$c");
        Variable<Adult> var_$a = declarationOf(Adult.class, "$a");
        Variable<Integer> var_$parentAge = declarationOf(Integer.class, "$parentAge");
        Variable<Integer> var_$expr$5$ = declarationOf(Integer.class, "$expr$5$");

        Rule rule = rule("R").build(
                accumulate(
                        and(
                            pattern(var_$c)
                                    .expr("$expr$1$", (_this) -> _this.getAge() < 10,
                                            alphaIndexedBy(int.class, Index.ConstraintType.LESS_THAN, 0, _this -> _this.getAge(), 10),
                                            reactOn("age")),
                            pattern(var_$a)
                                    .expr("$expr$2$", var_$c, (_this, $c) -> _this.getName().equals($c.getParent()), reactOn("name"))
                                    .bind(var_$expr$5$, var_$c, ($a, $c) -> $a.getAge() + $c.getAge())
                        ),
                        accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class, var_$expr$5$).as(var_$parentAge)),
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
    public void testQueryInRule() {
        Variable<Person> personV = DSL.declarationOf(  Person.class );

        Query2Def<Person, Integer> qdef = query( "olderThan", Person.class, Integer.class );
        Query query = qdef.build( pattern( qdef.getArg1() ).expr("exprA", qdef.getArg2(), ( p, a) -> p.getAge() > a) );

        Variable<Person> personVRule = DSL.declarationOf(  Person.class );
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
        Variable<Relationship> relV = DSL.declarationOf( Relationship.class );

        Query2Def<String, String> query1Def = query( "isRelatedTo1", String.class, String.class );
        Query2Def<String, String> query2Def = query( "isRelatedTo2", String.class, String.class );

        Query query2 = query2Def.build(
                pattern( relV )
                        .expr("exprA", query2Def.getArg1(), ( r, s) -> r.getStart().equals( s ))
                        .expr("exprB", query2Def.getArg2(), ( r, e) -> r.getEnd().equals( e ))
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
    public void testQueryInvokingQuery2() {
        final org.drools.model.Query2Def<java.lang.String, java.lang.String> queryDef_isRelatedTo2 = query("isRelatedTo2",
                java.lang.String.class,
                "x",
                java.lang.String.class,
                "y");

        final org.drools.model.Query2Def<java.lang.String, java.lang.String> queryDef_isRelatedTo = query("isRelatedTo",
                java.lang.String.class,
                "x",
                java.lang.String.class,
                "y");

        org.drools.model.Query isRelatedTo_build = queryDef_isRelatedTo.build(queryDef_isRelatedTo2.call(true,
                queryDef_isRelatedTo.getArg1(),
                queryDef_isRelatedTo.getArg2()));

        final org.drools.model.Variable<org.drools.modelcompiler.domain.Relationship> var_$pattern_Relationship$4$ = declarationOf(org.drools.modelcompiler.domain.Relationship.class,
                "$pattern_Relationship$4$");

        org.drools.model.Query isRelatedTo2_build = queryDef_isRelatedTo2.build(
                pattern(var_$pattern_Relationship$4$)
                .expr("$expr$63$",
                        queryDef_isRelatedTo2.getArg1(),
                        (_this, x) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getStart(),
                                x),
                        betaIndexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                0,
                                _this -> _this.getStart(),
                                x -> x),
                        reactOn("start"))
                .expr("$expr$64$",
                        queryDef_isRelatedTo2.getArg2(),
                        (_this, y) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getEnd(),
                                y),
                        betaIndexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                1,
                                _this -> _this.getEnd(),
                                y -> y),
                        reactOn("end")));

        Model model = new ModelImpl().addQuery( isRelatedTo_build ).addQuery( isRelatedTo2_build );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        KieSession ksession = kieBase.newKieSession();

        ksession.insert( new Relationship( "A", "B" ) );
        ksession.insert( new Relationship( "B", "C" ) );

        QueryResults results = ksession.getQueryResults( "isRelatedTo", "A", "B" );

        assertEquals( 1, results.size() );
        String paramName = ((QueryImpl ) ksession.getKieBase().getQuery("defaultpkg", "isRelatedTo" )).getParameters()[1].getIdentifier();
        assertEquals("B", results.iterator().next().get(paramName));
    }

    @Test
    public void testNegatedAfter() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a)
                        .expr("$expr$1$",
                                (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "DROO"),
                                reactOn("company")),
                pattern(var_$b)
                        .expr("$expr$2$", (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "ACME"),
                                reactOn("company"))
                        .expr("$expr$3$",
                                var_$a,
                                not(after(5, java.util.concurrent.TimeUnit.SECONDS, 8, java.util.concurrent.TimeUnit.SECONDS))),
                execute(() -> {
                    System.out.println("fired");
                }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testBreakingNamedConsequence() {
        Variable<Result> resultV = declarationOf( Result.class );
        Variable<Person> markV = declarationOf( Person.class );
        Variable<Person> olderV = declarationOf( Person.class );

        Rule rule = rule( "beta" )
                .build(
                        pattern( resultV ),
                        pattern(markV)
                                .expr("exprA", p -> p.getName().equals( "Mark" ),
                                        alphaIndexedBy( String.class, Index.ConstraintType.EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name", "age" )),
                        when("cond1", markV, p -> p.getAge() < 30).then(
                                on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found young " + p.getName()))
                        ).elseWhen("cond2", markV, p -> p.getAge() > 50).then(
                                on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found old " + p.getName()))
                        ).elseWhen().then(
                                on(markV, resultV).breaking().execute((p, r) -> r.addValue( "Found " + p.getName()))
                        ),
                        pattern(olderV)
                                .expr("exprB", p -> !p.getName().equals("Mark"),
                                        alphaIndexedBy( String.class, Index.ConstraintType.NOT_EQUAL, 1, p -> p.getName(), "Mark" ),
                                        reactOn( "name" ))
                                .expr("exprC", markV, (p1, p2) -> p1.getAge() > p2.getAge(),
                                        betaIndexedBy( int.class, Index.ConstraintType.GREATER_THAN, 0, p -> p.getAge(), p -> p.getAge() ),
                                        reactOn( "age" )),
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
    public void testWatch() {
        Variable<Person> var_$p = declarationOf(Person.class, "$p");
        BitMask mask_$p = BitMask.getPatternMask(org.drools.modelcompiler.domain.Person.class, "age");

        Rule rule = rule("R").build(
                pattern(var_$p)
                    .expr("$expr$1$", (_this) -> _this.getAge() < 50,
                            alphaIndexedBy(int.class, Index.ConstraintType.LESS_THAN, 0, _this -> _this.getAge(), 50),
                            reactOn("age"))
                .watch("!age"),
                on(var_$p).execute((drools, $p) -> {
                    $p.setAge($p.getAge() + 1);
                    drools.update($p, mask_$p);
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertEquals(41, p.getAge());
    }

    @Test
    public void testReactiveOOPath() {
        Global<List> listG = globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = declarationOf( Man.class );
        Variable<Woman> wifeV = declarationOf( Woman.class, reactiveFrom( manV, Man::getWife ) );
        Variable<Child> childV = declarationOf( Child.class, reactiveFrom( wifeV, Woman::getChildren ) );
        Variable<Toy> toyV = declarationOf( Toy.class, reactiveFrom( childV, Child::getToys ) );

        Rule rule = rule( "oopath" )
                .build(
                        pattern(manV),
                        pattern(wifeV),
                        pattern(childV).expr("exprA", c -> c.getAge() > 10),
                        pattern(toyV),
                        on(toyV, listG).execute( (t, l) -> l.add(t.getName()) )
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

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testAccumulateConstrainingValue() {
        Variable<org.drools.modelcompiler.domain.Person> var_$p = declarationOf(org.drools.modelcompiler.domain.Person.class, "$p");
        Variable<Integer> var_$expr$5$ = declarationOf(Integer.class, "$expr$5$");
        Variable<java.lang.Integer> var_$sum = declarationOf(java.lang.Integer.class, "$sum");

        Rule rule = rule("X").build(
                accumulate(pattern(var_$p).expr("$expr$4$",
                (_this) -> _this.getName()
                        .startsWith("M"))
                        .bind(var_$expr$5$,
                                (_this) -> _this.getAge()),
                accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction.class,
                        var_$expr$5$).as(var_$sum)),
                pattern(var_$sum).expr("$expr$3$",
                        (_this) -> _this > 50),
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
    public void testQueryWithDyanmicInsert() throws IOException, ClassNotFoundException {
        org.drools.model.Global<java.util.List> var_list = globalOf(java.util.List.class,
                "org.drools.compiler.test",
                "list");

        org.drools.model.Query3Def<org.drools.modelcompiler.domain.Person, java.lang.String, Integer> queryDef_peeps = query("org.drools.compiler.test",
                "peeps",
                org.drools.modelcompiler.domain.Person.class,
                "$p",
                java.lang.String.class,
                "$name",
                int.class,
                "$age");

        org.drools.model.Query peeps_build = queryDef_peeps.build(D.pattern(queryDef_peeps.getArg1(), D.from(queryDef_peeps.getArg2(), queryDef_peeps.getArg3(), ($name, $age) -> new Person($name, $age))));

        final org.drools.model.Variable<java.lang.String> var_$n1 = D.declarationOf(java.lang.String.class,
                "$n1");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Person> var_$pattern_Person$2$ = D.declarationOf(org.drools.modelcompiler.domain.Person.class,
                "$pattern_Person$2$");
        final org.drools.model.Variable<org.drools.modelcompiler.domain.Person> var_$p = D.declarationOf(org.drools.modelcompiler.domain.Person.class,
                "$p");
        org.drools.model.Rule rule = D.rule("org.drools.compiler.test",
                "x1").build(D.pattern(var_$n1),
                D.not(D.pattern(var_$pattern_Person$2$).expr("$expr$2$",
                        (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getName(),
                                "darth"),
                        D.alphaIndexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                0,
                                _this -> _this.getName(),
                                "darth"),
                        D.reactOn("name"))),
                queryDef_peeps.call(true,
                        var_$p,
                        var_$n1,
                        D.valueOf(100)),
                D.on(var_list,
                        var_$p).execute((list, $p) -> {
                    list.add($p);
                }));

        Model model = new ModelImpl().addRule( rule ).addQuery( peeps_build ).addGlobal( var_list );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        final List<Person> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Person p1 = new Person("darth", 100);

        ksession.insert("darth");
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals(p1, list.get(0));

    }

    @Test
    public void testDynamicSalienceOnGlobal() {
        Global<AtomicInteger> var_salience1 = D.globalOf(AtomicInteger.class, "defaultpkg", "salience1");
        Global<AtomicInteger> var_salience2 = D.globalOf(AtomicInteger.class, "defaultpkg", "salience2");
        Global<List> var_list = D.globalOf(List.class, "defaultpkg", "list");

        Variable<Integer> var_$i = D.declarationOf(Integer.class, "$i");

        Rule rule1 = D.rule("R1")
                .attribute(Rule.Attribute.SALIENCE, supply( var_salience1, salience1 -> salience1.get() ))
                .build(D.pattern(var_$i),
                        D.on(var_$i,
                                var_salience1,
                                var_list).execute((drools, $i, salience1, list) -> {
                            drools.delete($i);
                            salience1.decrementAndGet();
                            list.add(1);
                        }));

        Rule rule2 = D.rule("R2")
                .attribute(Rule.Attribute.SALIENCE, supply( var_salience2, salience2 -> salience2.get() ))
                .build(D.pattern(var_$i),
                        D.on(var_$i,
                                var_list,
                                var_salience2).execute((drools, $i, list, salience2) -> {
                            drools.delete($i);
                            salience2.decrementAndGet();
                            list.add(2);
                        }));

        Model model = new ModelImpl().addRule( rule1 ).addRule( rule2 ).addGlobal( var_salience1 ).addGlobal( var_salience2 ).addGlobal( var_list );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model, EventProcessingOption.STREAM );
        KieSession ksession = kieBase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            ksession.setGlobal("salience1", new AtomicInteger(9));
            ksession.setGlobal("salience2", new AtomicInteger(10));

            for (int i = 0; i < 10; i++) {
                ksession.insert(i);
                ksession.fireAllRules();
            }

            assertEquals(list, Arrays.asList(2, 1, 2, 1, 2, 1, 2, 1, 2, 1));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDynamicSalienceOnDeclarations() {
        Global<List> var_list = D.globalOf( List.class, "defaultpkg", "list" );

        Variable<Integer> var_$i = D.declarationOf(Integer.class, "$i" );
        Variable<String> var_$s = D.declarationOf(String.class, "$s");

        Rule rule1 = D.rule("R1")
                .attribute(Rule.Attribute.SALIENCE, supply(var_$s, s -> s.length()))
                .build(D.pattern(var_$s),
                        D.on(var_list,
                                var_$s).execute((list, $s) -> {
                            list.add($s);
                        }));

        Rule rule2 = D.rule("R2")
                .attribute(Rule.Attribute.SALIENCE, supply(var_$i, i -> i))
                .build(D.pattern(var_$i),
                        D.on(var_$i,
                                var_list).execute(($i, list) -> {
                            list.add($i);
                        }));

        Model model = new ModelImpl().addRule( rule1 ).addRule( rule2 ).addGlobal( var_list );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( "ok" );
        ksession.insert( "test" );
        ksession.insert( 3 );
        ksession.insert( 1 );

        ksession.fireAllRules();
        assertEquals(list, Arrays.asList("test", 3, "ok", 1));
    }

    @Test
    public void testAfterOnLongFields() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a)
                        .expr("$expr$1$",
                                (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "DROO"),
                                reactOn("company")),
                pattern(var_$b)
                        .expr("$expr$2$", (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "ACME"),
                                reactOn("company"))
                        .expr("$expr$3$",
                                _this -> _this.getTimeFieldAsLong(),
                                var_$a,
                                $a -> $a.getTimeFieldAsLong(),
                                after(5, java.util.concurrent.TimeUnit.MILLISECONDS, 8, java.util.concurrent.TimeUnit.MILLISECONDS)),
                execute(() -> {
                    System.out.println("fired");
                }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        clock.advanceTime( 6, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterWithAnd() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a).expr("$expr$3$",
                (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                alphaIndexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getCompany(),
                        "DROO"),
                reactOn("company")),
                pattern(var_$b).and().expr("$expr$5$",
                        (_this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
                        alphaIndexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                0,
                                _this -> _this.getCompany(),
                                "ACME"),
                        reactOn("company"))
                        .expr("$expr$6$",
                                var_$a,
                                after(5L,
                                        java.util.concurrent.TimeUnit.SECONDS,
                                        8L,
                                        java.util.concurrent.TimeUnit.SECONDS)).endAnd(),
                execute(() -> {
                    System.out.println("fired");
                }));


        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);

        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }
}
