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
package org.drools.modelcompiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.drools.base.base.ClassObjectType;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Accumulate;
import org.drools.base.rule.Pattern;
import org.drools.core.ClockType;
import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Query;
import org.drools.model.Query2Def;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate1;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.constraints.LambdaConstraint;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Pair;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.after;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.DSL.or;
import static org.drools.model.DSL.reactiveFrom;
import static org.drools.model.DSL.supply;
import static org.drools.model.DSL.valueOf;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.query;
import static org.drools.model.PatternDSL.reactOn;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.PatternDSL.when;

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
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
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
        assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark");

        ksession.delete(marioFH);
        ksession.fireAllRules();

        mark.setAge(34);
        ksession.update(markFH, mark, "age");

        ksession.fireAllRules();
        results = getObjectsIntoList(ksession, String.class);
        assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark", "Edson is older than Mark");
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

        assertThat(result.getValue()).isEqualTo("Mario");
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
        assertThat(result.getValue()).isEqualTo("Oldest person is Mario");
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
        assertThat(result.getValue()).isEqualTo("total = 77; average = 38.5");
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
        assertThat(result.getValue()).isEqualTo("total = 4");
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
        assertThat(results).contains(new Result(49));
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

        Result result = ksession.getSingleInstanceOf( Result.class );
        assertThat(result.getValue()).isEqualTo("Mario");
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

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().get(query1Def.getArg2().getName())).isEqualTo("B");
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

        final org.drools.model.Variable<Relationship> var_$pattern_Relationship$4$ = declarationOf(Relationship.class,
                "$pattern_Relationship$4$");

        org.drools.model.Query isRelatedTo2_build = queryDef_isRelatedTo2.build(
                pattern(var_$pattern_Relationship$4$)
                .expr("$expr$63$",
                        queryDef_isRelatedTo2.getArg1(),
                        (_this, x) -> EvaluationUtil.areNullSafeEquals(_this.getStart(),
                                x),
                        betaIndexedBy(java.lang.String.class,
                                org.drools.model.Index.ConstraintType.EQUAL,
                                0,
                                _this -> _this.getStart(),
                                x -> x),
                        reactOn("start"))
                .expr("$expr$64$",
                        queryDef_isRelatedTo2.getArg2(),
                        (_this, y) -> EvaluationUtil.areNullSafeEquals(_this.getEnd(),
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

        assertThat(results.size()).isEqualTo(1);
        String paramName = ((QueryImpl ) ksession.getKieBase().getQuery("defaultpkg", "isRelatedTo" )).getParameters()[1].getIdentifier();
        assertThat(results.iterator().next().get(paramName)).isEqualTo("B");
    }

    @Test
    public void testNegatedAfter() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a)
                        .expr("$expr$1$",
                                (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "DROO"),
                                reactOn("company")),
                pattern(var_$b)
                        .expr("$expr$2$", (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
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

        assertThat(ksession.fireAllRules()).isEqualTo(0);

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(results.size()).isEqualTo(1);

        assertThat(results.iterator().next()).isEqualTo("Found Mark");
    }

    @Test
    public void testWatch() {
        Variable<Person> var_$p = declarationOf(Person.class, "$p");

        Rule rule = rule("R").build(
                pattern(var_$p)
                    .expr("$expr$1$", (_this) -> _this.getAge() < 50,
                            alphaIndexedBy(int.class, Index.ConstraintType.LESS_THAN, 0, _this -> _this.getAge(), 50),
                            reactOn("age"))
                .watch("!age"),
                on(var_$p).execute((drools, $p) -> {
                    $p.setAge($p.getAge() + 1);
                    drools.update($p, "age");
                }));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );
        KieSession ksession = kieBase.newKieSession();

        Person p = new Person("Mario", 40);
        ksession.insert( p );
        ksession.fireAllRules();

        assertThat(p.getAge()).isEqualTo(41);
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

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testAccumulateConstrainingValue() {
        Variable<Person> var_$p = declarationOf(Person.class, "$p");
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(77);
    }

    @Test
    public void testQueryWithDyanmicInsert() throws IOException, ClassNotFoundException {
        org.drools.model.Global<java.util.List> var_list = globalOf(java.util.List.class,
                "org.drools.compiler.test",
                "list");

        org.drools.model.Query3Def<Person, java.lang.String, Integer> queryDef_peeps = query("org.drools.compiler.test",
                "peeps",
                Person.class,
                "$p",
                java.lang.String.class,
                "$name",
                int.class,
                "$age");

        org.drools.model.Query peeps_build = queryDef_peeps.build(D.pattern(queryDef_peeps.getArg1(), D.from(queryDef_peeps.getArg2(), queryDef_peeps.getArg3(), ($name, $age) -> new Person($name, $age))));

        final org.drools.model.Variable<java.lang.String> var_$n1 = D.declarationOf(java.lang.String.class,
                "$n1");
        final org.drools.model.Variable<Person> var_$pattern_Person$2$ = D.declarationOf(Person.class,
                "$pattern_Person$2$");
        final org.drools.model.Variable<Person> var_$p = D.declarationOf(Person.class,
                "$p");
        org.drools.model.Rule rule = D.rule("org.drools.compiler.test",
                "x1").build(D.pattern(var_$n1),
                D.not(D.pattern(var_$pattern_Person$2$).expr("$expr$2$",
                        (_this) -> EvaluationUtil.areNullSafeEquals(_this.getName(),
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
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(p1);

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

            assertThat(list).containsExactly(2, 1, 2, 1, 2, 1, 2, 1, 2, 1);
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

        List<Object> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.insert( "ok" );
        ksession.insert( "test" );
        ksession.insert( 3 );
        ksession.insert( 1 );

        ksession.fireAllRules();
        assertThat(list).containsExactly("test", 3, "ok", 1);
    }

    @Test
    public void testAfterOnLongFields() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a)
                        .expr("$expr$1$",
                                (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                                alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, _this -> _this.getCompany(), "DROO"),
                                reactOn("company")),
                pattern(var_$b)
                        .expr("$expr$2$", (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
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

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @Test
    public void testAfterWithAnd() throws Exception {
        Variable<StockTick> var_$a = declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = declarationOf(StockTick.class, "$b");

        Rule rule = rule("R").build(
                pattern(var_$a).expr("$expr$3$",
                (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO"),
                alphaIndexedBy(java.lang.String.class,
                        org.drools.model.Index.ConstraintType.EQUAL,
                        0,
                        _this -> _this.getCompany(),
                        "DROO"),
                reactOn("company")),
                pattern(var_$b).and().expr("$expr$5$",
                        (_this) -> EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME"),
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

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @Test
    public void testTwoAccumulatesWithVarBindingModel() {
        Variable<Person> a = PatternDSL.declarationOf(Person.class);
        Variable<Pair> accSource = PatternDSL.declarationOf(Pair.class);
        Variable<Collection> accResult = PatternDSL.declarationOf(Collection.class);
        Variable<Collection> accResult2 = PatternDSL.declarationOf(Collection.class);
        Variable<Pair> wrapped = PatternDSL.declarationOf(Pair.class, PatternDSL.from(accResult));
        Variable<Object> unwrapped1 = PatternDSL.declarationOf(Object.class);

        PatternDSL.PatternDef aPattern = PatternDSL.pattern(a)
                .bind(accSource, v -> Pair.create(v.getName(), v.getAge()));
        ViewItem accumulate = PatternDSL.accumulate(aPattern, DSL.accFunction( CollectSetAccumulateFunction::new, accSource).as(accResult));

        PatternDSL.PatternDef secondPattern = PatternDSL.pattern(accResult);
        PatternDSL.PatternDef thirdPattern =
              PatternDSL.pattern(wrapped).bind(unwrapped1, Pair::getKey); // If binding removed, test will pass.
        ViewItem accumulate2 = PatternDSL.accumulate(PatternDSL.and(secondPattern, thirdPattern),
                DSL.accFunction(CollectSetAccumulateFunction::new, wrapped).as(accResult2));
        Rule rule = PatternDSL.rule("R")
                .build(accumulate, accumulate2, PatternDSL.on(accResult2).execute(obj -> {
                    boolean works = obj.contains(Pair.create("Lukas", 35));
                    if (!works) {
                        throw new IllegalStateException("Why is " + obj + " not Set<" + Pair.class + ">?");
                    }
                }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession session = kieBase.newKieSession();

        session.insert(new Person("Lukas", 35));
        session.fireAllRules();
    }

    @Test
    public void testBetaIndexOn2ValuesOnLeftTuple() {
        final Variable<Integer> var_$integer = D.declarationOf(Integer.class);
        final Variable<Integer> var_$i = D.declarationOf(Integer.class);
        final Variable<String> var_$string = D.declarationOf(String.class);
        final Variable<Integer> var_$l = D.declarationOf(Integer.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);

        Rule rule = D.rule("R1").build(D.pattern(var_$integer).bind(var_$i,
                (Integer _this) -> _this),
                D.pattern(var_$string).bind(var_$l,
                        (String _this) -> _this.length()),
                D.pattern(var_$p).expr("8EF302358D7EE770A4D874DF4B3327D2",
                        var_$l,
                        var_$i,
                        (_this, $l, $i) -> EvaluationUtil.areNumbersNullSafeEquals(_this.getAge(), $l + $i),
                        D.betaIndexedBy(int.class, Index.ConstraintType.EQUAL, 3, Person::getAge, ($l, $i) -> $l + $i, int.class),
                        D.reactOn("age")),
                D.execute(() -> { })
        );

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, EventProcessingOption.STREAM);
        KieSession ksession = kieBase.newKieSession();

        ksession.insert( 5 );
        ksession.insert( "test" );
        ksession.insert( new Person("Sofia", 9) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testPatternsAfterAccMovedToEvalsOnResultPattern() throws Exception {
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<String> var_$key = D.declarationOf(String.class);
        final Variable<Person> var_$p   = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
        Variable<Long> var_$countOfPersons = D.declarationOf(Long.class);

        Predicate1<Integer> p1 =  a -> a > 0;
        Predicate1<Long> p2 =  c -> c > 0;

        Rule rule1 = D.rule("R1").build(
                D.accumulate(
                        // Patterns
                        D.pattern(var_$p).bind(var_$age, person -> person.getAge(), D.reactOn("age")),
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges),
                        D.accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new).as(var_$countOfPersons)),
                // Filter
                D.pattern(var_$sumOfAges).expr(p1),
                D.pattern(var_$countOfPersons).expr(p2),
                // Consequence
                D.on(var_$sumOfAges, var_$countOfPersons, var_results)
                        .execute(($ages, $counts, results) -> results.add($ages + ":" + $counts)));

        Model      model    = new ModelImpl().addRule(rule1).addGlobal(var_results);
        KieBase    kbase    = KieBaseBuilder.createKieBaseFromModel(model);
        RuleImpl rule     = ( RuleImpl) kbase.getKiePackage("defaultpkg").getRules().toArray()[0];

        // Ensure there is only a single root child
        assertThat(rule.getLhs().getChildren().size()).isEqualTo(1);

        // The expression must be merged up into the acc pattern
        Pattern p = (Pattern) rule.getLhs().getChildren().get(0);
        assertThat(((ClassObjectType) p.getObjectType()).getClassType()).isEqualTo(Object[].class);
        LambdaConstraint l0 = (LambdaConstraint) p.getConstraints().get(0);
        assertThat(((Predicate1.Impl)l0.getEvaluator().getConstraint().getPredicate1()).getLambda()).isSameAs(p1);

        LambdaConstraint l1 = (LambdaConstraint) p.getConstraints().get(1);
        assertThat(((Predicate1.Impl)l1.getEvaluator().getConstraint().getPredicate1()).getLambda()).isSameAs(p2);

        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );

        ksession.fireAllRules();

        assertThat(results.toString()).isEqualTo("[42:1]");
    }

    @Test
    public void test2AccRewriteToNested() throws Exception {
        final Global<List> var_results = D.globalOf( List.class, "defaultpkg", "results" );

        final Variable<Person> var_$p = D.declarationOf( Person.class );
        Variable<Integer> var_$age = D.declarationOf( Integer.class );
        Variable<Integer> var_$sumOfAges = D.declarationOf( Integer.class );
        Variable<Long> var_$countOfPersons = D.declarationOf( Long.class );

        Predicate1<Long> cp = c -> c > 0;

        Rule rule1 = D.rule( "R1" ).build(
                D.accumulate(
                        D.pattern( var_$p ).bind( var_$age, person -> person.getAge(), D.reactOn( "age" ) ),
                        D.accFunction( org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age ).as( var_$sumOfAges ) ),
                D.accumulate(
                        D.pattern( var_$sumOfAges ),
                        D.accFunction( org.drools.core.base.accumulators.CountAccumulateFunction::new ).as( var_$countOfPersons ) ),
                // Filter
                D.pattern( var_$countOfPersons ).expr(cp),
                // Consequence
                D.on( var_$countOfPersons, var_results )
                        .execute( (drools, i, results) -> {
                            InternalMatch internalMatch = ((org.drools.modelcompiler.consequence.DroolsImpl) drools).asKnowledgeHelper().getMatch();
                            results.add(i + ":" + internalMatch.getObjectsDeep());

                        } ) );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieBase    kbase    = KieBaseBuilder.createKieBaseFromModel(model);
        RuleImpl   rule     = ( RuleImpl) kbase.getKiePackage("defaultpkg").getRules().toArray()[0];
        // Should only be a single child
        assertThat(rule.getLhs().getChildren().size()).isEqualTo(1);

        // Check correct result type and the filter was moved up
        Pattern    p1  = (Pattern) rule.getLhs().getChildren().get(0);
        assertThat(((ClassObjectType) p1.getObjectType()).getClassType()).isEqualTo(Long.class);
        LambdaConstraint l0 = (LambdaConstraint) p1.getConstraints().get(0);
        assertThat(((Predicate1.Impl)l0.getEvaluator().getConstraint().getPredicate1()).getLambda()).isSameAs(cp);

        // The second acc was sucessfully nested inside
        Accumulate acc = (Accumulate) p1.getSource();
        assertThat(acc.getNestedElements().size()).isEqualTo(1);
        Pattern p2 = (Pattern) acc.getNestedElements().get(0);
        assertThat(((ClassObjectType) p2.getObjectType()).getClassType()).isEqualTo( Integer.class);

        KieSession ksession = kbase.newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );

        ksession.fireAllRules();

        assertThat(results.toString()).isEqualTo("[1:[1]]");
    }

    @Test
    public void test2AccsWithGetObjectsDeep() throws Exception {
        // DROOLS-6236
        final Global<List> var_results = D.globalOf( List.class, "defaultpkg", "results" );

        final Variable<Person> var_$p = D.declarationOf( Person.class );
        Variable<Integer> var_$age = D.declarationOf( Integer.class );
        Variable<Integer> var_$sumOfAges = D.declarationOf( Integer.class );
        Variable<Long> var_$countOfPersons = D.declarationOf( Long.class, D.from(var_$p) );

        Rule rule1 = D.rule( "R1" ).build(
                D.accumulate(
                        D.pattern( var_$p ).bind( var_$age, person -> person.getAge(), D.reactOn( "age" ) ),
                        D.accFunction( org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age ).as( var_$sumOfAges ) ),
                D.accumulate(
                        D.pattern( var_$sumOfAges ),
                        D.accFunction( org.drools.core.base.accumulators.CountAccumulateFunction::new ).as( var_$countOfPersons ) ),
                D.pattern( var_$countOfPersons ),
                // Consequence
                D.on( var_$countOfPersons )
                        .execute( (drools, i) -> {
                            System.out.println(i);
                            InternalMatch internalMatch = ((org.drools.modelcompiler.consequence.DroolsImpl) drools).asKnowledgeHelper().getMatch();
                            System.out.println(internalMatch.getObjectsDeep());
                        } ) );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static <T> List<T> getObjectsIntoList(KieSession ksession, Class<T> clazz) {
        return ksession.getInstancesOf(clazz).stream().collect(Collectors.toList());
    }
}
