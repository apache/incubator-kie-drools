/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

import org.apache.commons.math3.util.Pair;
import org.assertj.core.api.Assertions;
import org.drools.core.WorkingMemory;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.CountAccumulateFunction;
import org.drools.core.base.accumulators.IntegerMaxAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.DSL;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Function1;
import org.drools.model.functions.accumulate.GroupKey;
import org.drools.model.impl.ModelImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Parent;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.from;
import static org.junit.Assert.*;

public class GroupByTest {

    @Test
    public void providedInstance() throws Exception {
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
              D.groupBy(
                    // Patterns
                    D.pattern(var_$p).watch("age"),
                    // Grouping Function
                    var_$p, var_$key, person -> person.getName().substring(0, 1),
                    // Accumulate Result (can be more than one)
                    D.accFunction(() -> sumA(Person::getAge)).as(var_$sumOfAges)),
              // FilterIntegerSumAccumulateFunction
              D.pattern(var_$sumOfAges)
               .expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 36)),
              // Consequence
              D.on(var_$key, var_results, var_$sumOfAges)
               .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
                                       );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    public static <A> SumAccumulator sumA(ToIntFunction<? super A> func) {
        return new SumAccumulator(func);
    }

    public static class SumAccumulator<C> implements Accumulator { //extends AbstractAccumulateFunction<C> {
        //UniConstraintCollector<A, ResultContainer_, Result_> collector;
        private ToIntFunction func;

        public <A> SumAccumulator(ToIntFunction<? super A> func) {
            this.func = func;
        }

        @Override public Object createWorkingMemoryContext() {
            return null;
        }

        @Override public Object createContext() {
            return new int[1];
        }

        @Override public Object init(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) {
            ((int[])context)[0] = 0;
            return context;
        }

        @Override public Object accumulate(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
            int[] ctx = (int[]) context;

            int v = func.applyAsInt(handle.getObject());
            ctx[0] += v;

            Runnable undo = () -> ctx[0] -= v;

            return undo;
        }

        @Override public boolean supportsReverse() {
            return true;
        }

        @Override public boolean tryReverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle, Object value, Declaration[] declarations, Declaration[] innerDeclarations, WorkingMemory workingMemory) {
            if (value!=null) {
                ((Runnable) value).run();
            }
            return true;
        }

        @Override public Object getResult(Object workingMemoryContext, Object context, Tuple leftTuple, Declaration[] declarations, WorkingMemory workingMemory) {
            int[] ctx = (int[]) context;
            return ctx[0];
        }
    }


    @Test
    public void testSumPersonAgeGroupByInitialWithAcc() throws Exception {
        final Variable<Person> var_GENERATED_$pattern_Person$4$ = D.declarationOf(Person.class);
        final Variable<String> var_$initial = D.declarationOf(String.class);
        final Variable<GroupKey> var_sCoPe3_GENERATED_$pattern_GroupKey$3$ = D.declarationOf(GroupKey.class);

        Rule rule1 = D.rule("R1").build(
                D.pattern(var_GENERATED_$pattern_Person$4$)
                        .bind(var_$initial, (Person _this) -> _this.getName().substring(0, 1)),
                D.not(D.pattern(var_sCoPe3_GENERATED_$pattern_GroupKey$3$).expr("FF3B1999B2904B3324A471615B8760C9",
                        var_$initial,
                        (GroupKey _this, java.lang.String $initial) -> EvaluationUtil.areNullSafeEquals(_this.getKey(), $initial),
                        D.reactOn("key"))),
                D.on(var_$initial).execute((org.drools.model.Drools drools, java.lang.String $initial) -> {
                    {
                        drools.insert(new GroupKey("a", $initial));
                    }
                }));

        final Variable<GroupKey> var_$k = D.declarationOf(GroupKey.class);
        final Variable<Object> var_$key = D.declarationOf(Object.class);
        final Variable<Person> var_sCoPe4_GENERATED_$pattern_Person$5$ = D.declarationOf(Person.class);

        Rule rule2 = D.rule("R2").build(
                D.pattern(var_$k).expr("8313F8B6FD1C0612B7758BFDB93F0DE4", (GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), "a"),
                        D.reactOn("topic"))
                        .bind(var_$key, (GroupKey _this) -> _this.getKey(),
                                D.reactOn("key")),
                D.not(D.pattern(var_sCoPe4_GENERATED_$pattern_Person$5$).expr("AFBC8D66DD9165C71D89004BBF5B0F9C",
                        var_$key,
                        (Person _this, Object $key) -> EvaluationUtil.areNullSafeEquals(_this.getName().substring(0, 1), $key.toString()),
                        D.reactOn("name"))),
                D.on(var_$k).execute((org.drools.model.Drools drools, GroupKey $k) -> {
                    {
                        drools.delete($k);
                    }
                }));

        final Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        final Variable<GroupKey> var_GENERATED_$pattern_GroupKey$4$ = D.declarationOf(GroupKey.class);
        final Variable<Person> var_GENERATED_$pattern_Person$6$ = D.declarationOf(Person.class);
        final Variable<Integer> var_$age = D.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = D.declarationOf(java.lang.Integer.class);

        Rule rule3 = D.rule("R3").build(
                D.pattern(var_GENERATED_$pattern_GroupKey$4$).expr("8313F8B6FD1C0612B7758BFDB93F0DE4", ( GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), "a"),
                        D.reactOn("topic")).bind(var_$key,
                        (GroupKey _this) -> _this.getKey(),
                        D.reactOn("key")),
                D.accumulate(D.pattern(var_GENERATED_$pattern_Person$6$)
                                .bind(var_$age, (Person _this) -> _this.getAge(), D.reactOn("age"))
                                .expr("AFBC8D66DD9165C71D89004BBF5B0F9C",
                                        var_$key,
                                        (Person _this, Object $key) -> EvaluationUtil.areNullSafeEquals(_this.getName().substring(0, 1), $key),
                                        D.reactOn("name")),
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                D.pattern(var_$sumOfAges).expr("00DE1D5962263283D8D799CF83F1A729",
                        (java.lang.Integer $sumOfAges) -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                D.on(var_$key,
                        var_results,
                        var_$sumOfAges).execute((Object $key, Map results, Integer $sumOfAges) -> {
                    {
                        results.put($key, $sumOfAges);
                    }
                }));

        Model model = new ModelImpl().addRule( rule1 ).addRule( rule2 ).addRule( rule3 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        assertEquals( 35, results.get("G") );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );

    }

    @Test
    public void testGroupPersonsInitial() throws Exception {
        Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        Variable<String> var_$key = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<List> var_$list = D.declarationOf(List.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1)
                         ),
                // Consequence
                D.on(var_$key,var_results)
                        .execute(($key,results) -> {
                            results.add($key);
                            //System.out.println($key +  ": " + $list);
                        })
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List results = new ArrayList();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        Assertions.assertThat(results)
                .containsExactlyInAnyOrder("G", "E", "M");
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        Assertions.assertThat(results)
                .containsExactlyInAnyOrder("M");
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        Assertions.assertThat(results)
                .containsExactlyInAnyOrder("G", "M");
    }

    @Test
    public void testSumPersonAgeGroupByInitial() throws Exception {
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p).bind(var_$age, person -> person.getAge(), D.reactOn("age")),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                D.pattern(var_$sumOfAges)
                        .expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 36)),
                // Consequence
                D.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    @Test
    public void testSumPersonAgeGroupByInitialWithBetaFilter() throws Exception {
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p).bind(var_$age, person -> person.getAge(), D.reactOn("age")),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                D.pattern(var_$sumOfAges)
                        .expr(var_$key, ($sumOfAges, $key) -> EvaluationUtil.greaterThanNumbers($sumOfAges, $key.length())),
                // Consequence
                D.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        assertEquals( 35, results.get("G") );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    @Test
    public void testSumPersonAgeGroupByInitialWithExists() throws Exception {
        final Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        final Variable<String> var_$key = D.declarationOf(String.class);
        final Variable<String> var_$string = D.declarationOf(String.class);
        final Variable<String> var_$initial = D.declarationOf(String.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Integer> var_$age = D.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.and(
                            D.pattern(var_$p)
                                    .bind(var_$age, person -> person.getAge(), D.reactOn("age"))
                                    .bind(var_$initial, person -> person.getName().substring(0, 1)),
                            D.exists(D.pattern(var_$string).expr(var_$initial, (_this, $initial) -> EvaluationUtil.areNullSafeEquals(_this, $initial)))
                        ),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                D.pattern(var_$sumOfAges).expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                D.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));

        ksession.insert( "G" );
        ksession.insert( "M" );
        ksession.insert( "X" );

        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 35, results.get("G") );
        assertNull( results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    private static final class MyType {

        private final MyType nested;

        public MyType(MyType nested) {
            this.nested = nested;
        }

        public MyType getNested() {
            return nested;
        }
    }

    @Test
    public void testWithNull() {
        Variable<MyType> var = D.declarationOf(MyType.class);
        Variable<MyType> groupKey = D.declarationOf(MyType.class);
        Variable<Long> count = D.declarationOf(Long.class);

        AtomicInteger mappingFunctionCallCounter = new AtomicInteger(0);
        Function1<MyType, MyType> mappingFunction = ( a) -> {
            mappingFunctionCallCounter.incrementAndGet();
            return a.getNested();
        };
        D.PatternDef<MyType> onlyOnesWithNested = D.pattern(var)
                .expr(myType -> myType.getNested() != null);
        ExprViewItem groupBy = D.groupBy(onlyOnesWithNested, var, groupKey, mappingFunction,
                D.accFunction( CountAccumulateFunction::new).as(count));

        List<MyType> result = new ArrayList<>();

        Rule rule = D.rule("R")
                .build(groupBy,
                        D.on(groupKey, count)
                                .execute((drools, key, acc) -> result.add(key)));

        Model model = new ModelImpl().addRule( rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        MyType objectWithoutNestedObject = new MyType(null);
        MyType objectWithNestedObject = new MyType(objectWithoutNestedObject);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(objectWithNestedObject);
        ksession.insert(objectWithoutNestedObject);
        ksession.fireAllRules();

        // Side issue: this number is unusually high. Perhaps we should try to implement some cache for this?
        System.out.println("GroupKey mapping function was called " + mappingFunctionCallCounter.get() + " times.");

        Assertions.assertThat(result).containsOnly(objectWithoutNestedObject);
    }

    @Test
    public void testWithGroupByAfterExists() {
        Global<Map> groupResultVar = D.globalOf(Map.class, "defaultPkg", "glob");

        Variable<Integer> patternVar = D.declarationOf(Integer.class);
        Variable<String> existsVar = D.declarationOf(String.class);
        Variable<Integer> keyVar = D.declarationOf(Integer.class);
        Variable<Long> resultVar = D.declarationOf(Long.class);

        D.PatternDef<Integer> pattern = D.pattern(patternVar);
        D.PatternDef<String> exist = D.pattern(existsVar);
        ViewItem patternAndExists = D.and(
                pattern,
                D.exists(exist));

        ViewItem groupBy = D.groupBy(patternAndExists, patternVar, keyVar, Math::abs,
                DSL.accFunction(CountAccumulateFunction::new).as(resultVar));
        ConsequenceBuilder._3 consequence = D.on(keyVar, resultVar, groupResultVar)
                .execute((key, count, result) -> {
                    result.put(key, count.intValue());
                });

        Rule rule = D.rule("R").build(groupBy, consequence);

        Model model = new ModelImpl().addRule(rule).addGlobal( groupResultVar );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession session = kieBase.newKieSession();
        Map<Integer, Integer> global = new HashMap<>();
        session.setGlobal("glob", global);

        session.insert("Something");
        session.insert(-1);
        session.insert(1);
        session.insert(2);
        session.fireAllRules();

        assertEquals(2, global.size());
        assertEquals(2, (int) global.get(1)); // -1 and 1 will map to the same key, and count twice.
        assertEquals(1, (int) global.get(2)); // 2 maps to a key, and counts once.
    }

    @Test
    public void testWithGroupByAfterExistsWithFrom() {
        Global<Map> groupResultVar = D.globalOf(Map.class, "defaultPkg", "glob");

        Variable<Integer> patternVar = D.declarationOf(Integer.class);
        Variable<String> existsVar = D.declarationOf(String.class);
        Variable<Integer> keyVar = D.declarationOf(Integer.class);
        Variable<Long> resultVar = D.declarationOf(Long.class);
        Variable<Integer> mappedResultVar = D.declarationOf(Integer.class);

        D.PatternDef<Integer> pattern = D.pattern(patternVar);
        D.PatternDef<String> exist = D.pattern(existsVar);
        ViewItem patternAndExists = D.and( pattern, D.exists(exist) );

        ViewItem groupBy = D.groupBy(patternAndExists, patternVar, keyVar, Math::abs,
                DSL.accFunction(CountAccumulateFunction::new).as(resultVar));
        PatternDSL.PatternDef mappedResult = D.pattern(resultVar).bind(mappedResultVar, Long::intValue);
        ConsequenceBuilder._3 consequence = D.on(keyVar, mappedResultVar, groupResultVar)
                .execute((key, count, result) -> {
                    result.put(key, count);
                });

        Rule rule = D.rule("R").build(groupBy, mappedResult, consequence);

        Model model = new ModelImpl().addRule(rule).addGlobal( groupResultVar );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession session = kieBase.newKieSession();
        Map<Integer, Integer> global = new HashMap<>();
        session.setGlobal("glob", global);

        session.insert("Something");
        session.insert(-1);
        session.insert(1);
        session.insert(2);
        session.fireAllRules();

        assertEquals(2, global.size());
        assertEquals(2, (int) global.get(1)); // -1 and 1 will map to the same key, and count twice.
        assertEquals(1, (int) global.get(2)); // 2 maps to a key, and counts once.
    }

    @Test
    public void testGroupBy2Vars() throws Exception {
        final Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        final Variable<String> var_$key = D.declarationOf(String.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Integer> var_$age = D.declarationOf(Integer.class);
        final Variable<String> var_$s = D.declarationOf(String.class);
        final Variable<Integer> var_$l = D.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.and(
                            D.pattern(var_$p).bind(var_$age, Person::getAge, D.reactOn("age")),
                            D.pattern(var_$s).bind(var_$l, String::length, D.reactOn("length"))
                        ),
                        // Grouping Function
                        var_$p, var_$s, var_$key, (person, string) -> person.getName().substring(0, 1) + string.length(),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                D.pattern(var_$sumOfAges).expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                D.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert( "test" );
        ksession.insert( "check" );
        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 6, results.size() );
        assertEquals( 35, results.get("G4") );
        assertEquals( 71, results.get("E4") );
        assertEquals( 126, results.get("M4") );
        assertEquals( 35, results.get("G5") );
        assertEquals( 71, results.get("E5") );
        assertEquals( 126, results.get("M5") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 81, results.get("M4") );
        assertEquals( 81, results.get("M5") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 4, results.size() );
        assertEquals( 40, results.get("G4") );
        assertEquals( 119, results.get("M4") );
        assertEquals( 40, results.get("G5") );
        assertEquals( 119, results.get("M5") );
    }

    @Test
    public void testUnexpectedRuleMatch() {
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        // $a: Parent()
        Variable<Parent> patternVar = D.declarationOf(Parent.class);
        PatternDSL.PatternDef<Parent> pattern = D.pattern(patternVar);

        // exists Child($a.getChild() == this)
        Variable<Child> existsPatternVar = D.declarationOf(Child.class);
        PatternDSL.PatternDef<Child> existsPattern = D.pattern(existsPatternVar)
                .expr(patternVar, (child, parent) -> Objects.equals(parent.getChild(), child));

        // count(Parent::getChild)
        Variable<Child> groupKeyVar = D.declarationOf(Child.class);
        Variable<Long> accumulateResult = D.declarationOf(Long.class);
        ExprViewItem groupBy = PatternDSL.groupBy(D.and(pattern, D.exists(existsPattern)),
                patternVar, groupKeyVar, Parent::getChild,
                DSL.accFunction(CountAccumulateFunction::new).as(accumulateResult));

        Rule rule1 = D.rule("R1").build(groupBy,
                D.on(var_results, groupKeyVar, accumulateResult)
                        .execute((results, $child, $count) -> results.add(Arrays.asList($child, $count))));

        Model model = new ModelImpl().addRule(rule1).addGlobal(var_results);
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List results = new ArrayList();
        ksession.setGlobal( "results", results );

        Child child1 = new Child("Child1", 1);
        Parent parent1 = new Parent("Parent1", child1);
        Child child2 = new Child("Child2", 2);
        Parent parent2 = new Parent("Parent2", child2);

        ksession.insert(parent1);
        ksession.insert(parent2);
        FactHandle toRemove = ksession.insert(child1);
        ksession.insert(child2);

        // Remove child1, therefore it does not exist, therefore there should be no groupBy matches for the child.
        ksession.delete(toRemove);

        // Yet, we still get (Child1, 0).
        ksession.fireAllRules();
        Assertions.assertThat(results)
                .containsOnly(Arrays.asList(child2, 1L));
    }

    @Test
    public void testCompositeKey() throws Exception {
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<CompositeKey> var_$key = D.declarationOf(CompositeKey.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);

        // Define key1 with from
        Variable<Object> var_$key1 = D.declarationOf( Object.class );

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p).bind(var_$age, person -> person.getAge(), D.reactOn("age")),
                        // Grouping Function
                        var_$p, var_$key, person -> new CompositeKey( person.getName().substring(0, 1), 1 ),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                D.pattern(var_$sumOfAges)
                        .expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Bind key1
                D.pattern( var_$key).bind(var_$key1, CompositeKey::getKey1),
                // Consequence
                D.on(var_$key1, var_results, var_$sumOfAges)
                        .execute(($key1, results, $sumOfAges) -> results.put($key1, $sumOfAges))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();

        assertEquals( 3, results.size() );
        assertEquals( 35, results.get("G") );
        assertEquals( 71, results.get("E") );
        assertEquals( 126, results.get("M") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 81, results.get("M") );
        results.clear();

        ksession.update(geoffreyFH, new Person("Geoffrey", 40));
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 40, results.get("G") );
        assertEquals( 119, results.get("M") );
    }

    public static class CompositeKey {
        public final Object key1;
        public final Object key2;

        public CompositeKey( Object key1, Object key2 ) {
            this.key1 = key1;
            this.key2 = key2;
        }

        public Object getKey1() {
            return key1;
        }

        public Object getKey2() {
            return key2;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            CompositeKey that = ( CompositeKey ) o;
            return Objects.equals( key1, that.key1 ) &&
                    Objects.equals( key2, that.key2 );
        }

        @Override
        public int hashCode() {
            return Objects.hash( key1, key2 );
        }

        @Override
        public String toString() {
            return "CompositeKey{" +
                    "key1=" + key1 +
                    ", key2=" + key2 +
                    '}';
        }
    }

    @Test
    public void testTwoExpressionsOnSecondPattern() {
        // DROOLS-5704
        Global<Set> var_results = D.globalOf(Set.class, "defaultpkg", "results");

        Variable<Person> var_$p1 = D.declarationOf(Person.class);
        Variable<Person> var_$p2 = D.declarationOf(Person.class);
        Variable<Integer> var_$key = D.declarationOf(Integer.class);
        Variable<Integer> var_$join = D.declarationOf(Integer.class);

        PatternDSL.PatternDef<Person> p1pattern = D.pattern(var_$p1)
                .bind(var_$join, Person::getAge);
        PatternDSL.PatternDef<Person> p2pattern = D.pattern(var_$p2)
                .expr(p -> true)
                .expr("Age less than", var_$join, (p1, age) -> p1.getAge() > age,
                        D.betaIndexedBy(Integer.class, Index.ConstraintType.LESS_THAN, 0, Person::getAge, age -> age));

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        D.and(p1pattern, p2pattern),
                        var_$p1,
                        var_$p2,
                        var_$key,
                        (p1, p2) -> p1.getAge() + p2.getAge()),
                D.on(var_results, var_$key)
                        .execute(Set::add)
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Set<Integer> results = new LinkedHashSet<>();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        ksession.insert(new Person("Edoardo", 33));
        ksession.fireAllRules();

        assertThat(results).contains(80, 75, 71);
    }

    @Test
    public void testFromAfterGroupBy() {
        Global<Set> var_results = D.globalOf( Set.class, "defaultpkg", "results" );

        Variable var_$p1 = D.declarationOf( Person.class );
        Variable var_$key = D.declarationOf( String.class );
        Variable var_$count = D.declarationOf( Long.class );
        Variable var_$remapped1 = D.declarationOf( Object.class, from( var_$key ) );
        Variable var_$remapped2 = D.declarationOf( Long.class, from( var_$count ) );

        PatternDSL.PatternDef<Person> p1pattern = D.pattern( var_$p1 )
                .expr( p -> (( Person ) p).getName() != null );

        Rule rule1 = D.rule( "R1" ).build(
                D.groupBy(
                        p1pattern,
                        var_$p1,
                        var_$key,
                        Person::getName,
                        DSL.accFunction( CountAccumulateFunction::new, var_$p1 ).as( var_$count ) ),
                D.pattern( var_$remapped1 ),
                D.pattern( var_$remapped2 ),
                D.on( var_$remapped1, var_$remapped2 )
                        .execute( ( ctx, name, count ) -> {
                            if ( !(name instanceof String) ) {
                                throw new IllegalStateException( "Name not String, but " + name.getClass() );
                            }
                        } )
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Set<Integer> results = new LinkedHashSet<>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );
        ksession.insert( new Person( "Edson", 38 ) );
        ksession.insert( new Person( "Edoardo", 33 ) );
        int fireCount = ksession.fireAllRules();
        assertThat( fireCount ).isGreaterThan( 0 );
    }

    @Test
    public void testBindingRemappedAfterGroupBy() {
        Global<Set> var_results = D.globalOf( Set.class, "defaultpkg", "results" );

        Variable var_$p1 = D.declarationOf( Person.class );
        Variable var_$key = D.declarationOf( String.class );
        Variable var_$count = D.declarationOf( Long.class );
        Variable var_$remapped1 = D.declarationOf( Object.class);
        Variable var_$remapped2 = D.declarationOf( Long.class);

        PatternDSL.PatternDef<Person> p1pattern = D.pattern( var_$p1 )
                                                   .expr( p -> (( Person ) p).getName() != null );

        Rule rule1 = D.rule( "R1" ).build(
              D.groupBy(
                    p1pattern,
                    var_$p1,
                    var_$key,
                    Person::getName,
                    DSL.accFunction( CountAccumulateFunction::new, var_$p1 ).as( var_$count ) ),
              D.pattern( var_$key).bind(var_$remapped1, o -> o),
              D.pattern( var_$count).bind(var_$remapped2, o -> o),
              D.on( var_$remapped1, var_$remapped2 )
               .execute( ( ctx, name, count ) -> {
                   if ( !(name instanceof String) ) {
                       throw new IllegalStateException( "Name not String, but " + name.getClass() );
                   }
               } )
                                         );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Set<Integer> results = new LinkedHashSet<>();
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );
        ksession.insert( new Person( "Edson", 38 ) );
        ksession.insert( new Person( "Edoardo", 33 ) );
        int fireCount = ksession.fireAllRules();
        assertThat( fireCount ).isGreaterThan( 0 );
    }

    @Test
    public void testGroupByUpdatingKey() throws Exception {
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
        Variable<Long> var_$countOfPersons = D.declarationOf(Long.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p).bind(var_$age, person -> person.getAge(), D.reactOn("age")),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        D.accFunction(org.drools.core.base.accumulators.IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges),
                        D.accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new).as(var_$countOfPersons)),
                // Filter
                D.pattern(var_$sumOfAges)
                        .expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                D.on(var_$key, var_results, var_$sumOfAges, var_$countOfPersons)
                        .execute(($key, results, $sumOfAges, $countOfPersons) -> results.put($key, $sumOfAges + $countOfPersons.intValue()))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        Person me = new Person("Mario", 45);
        FactHandle meFH = ksession.insert(me);

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 73, results.get("E") );
        assertEquals( 129, results.get("M") );
        results.clear();

        me.setName("EMario");
        ksession.update(meFH, me);
        ksession.fireAllRules();

        assertEquals( 2, results.size() );
        assertEquals( 119, results.get("E") );
        assertEquals( 83, results.get("M") );
    }

    @Test
    public void doesNotRemoveProperly() {
        Global<Set> var_results = D.globalOf( Set.class, "defaultpkg", "results" );

        Variable<Person> var_$p1 = D.declarationOf( Person.class );
        Variable<Integer> var_$key = D.declarationOf( Integer.class );

        PatternDSL.PatternDef<Person> p1pattern = D.pattern( var_$p1 )
                .expr( p -> p.getName() != null );

        Set<Integer> results = new LinkedHashSet<>();
        Rule rule1 = D.rule( "R1" ).build(
                D.groupBy(
                        p1pattern,
                        var_$p1,
                        var_$key,
                        Person::getAge),
                D.on( var_$key )
                        .execute( ( ctx, key ) -> {
                            results.add(key);
                        } )
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        (( RuleEventManager ) ksession).addEventListener( new RuleEventListener() {
            @Override
            public void onDeleteMatch( Match match) {
                if (!match.getRule().getName().equals( "R1" )) {
                    return;
                }
                RuleTerminalNodeLeftTuple tuple = (RuleTerminalNodeLeftTuple) match;
                InternalFactHandle handle = tuple.getFactHandle();
                Object[] array = (Object[]) handle.getObject();
                results.remove( array[array.length-1] );
            }

            @Override
            public void onUpdateMatch(Match match) {
                onDeleteMatch(match);
            }
        });
        ksession.setGlobal( "results", results );

        ksession.insert( new Person( "Mark", 42 ) );
        ksession.insert( new Person( "Edson", 38 ) );
        int edoardoAge = 33;
        FactHandle fh1 = ksession.insert( new Person( "Edoardo", edoardoAge ) );
        FactHandle fh2 = ksession.insert( new Person( "Edoardo's clone", edoardoAge ) );
        ksession.fireAllRules();
        assertThat( results ).contains(edoardoAge);

        // Remove first Edoardo. Nothing should happen, because age 33 is still present.
        ksession.delete(fh1);
        ksession.fireAllRules();
        assertThat( results ).contains(edoardoAge);

        // Remove Edoardo's clone. The group for age 33 should be undone.
        ksession.delete(fh2);
        ksession.fireAllRules();
        System.out.println(results);
        assertThat( results ).doesNotContain(edoardoAge);
    }

    @Test
    public void testTwoGroupBy() {
        // DROOLS-5697
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key_1 = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
        Variable<Group> var_$g1 = D.declarationOf(Group.class, "$g1", D.from(var_$key_1, var_$sumOfAges, ($k, $v) -> new Group($k, $v)));
        Variable<Integer> var_$g1_value = D.declarationOf(Integer.class);
        Variable<String> var_$key_2 = D.declarationOf(String.class);
        Variable<Integer> var_$maxOfValues = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        D.and(
                                D.groupBy(
                                        D.pattern(var_$p).bind(var_$age, person -> person.getAge()),
                                        var_$p, var_$key_1, person -> person.getName().substring(0, 3),
                                        D.accFunction( IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                                D.pattern(var_$g1).bind(var_$g1_value, group -> (Integer) group.getValue()) ),
                        var_$g1, var_$key_2, groupResult -> ((String)groupResult.getKey()).substring(0, 2),
                        D.accFunction( IntegerMaxAccumulateFunction::new, var_$g1_value).as(var_$maxOfValues)),
                D.on(var_$key_2, var_results, var_$maxOfValues)
                        .execute(($key, results, $maxOfValues) -> {
                            System.out.println($key + " -> " + $maxOfValues);
                            results.put($key, $maxOfValues);
                        })
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edson", 38));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();
        System.out.println("-----");

        /*
         * In the first groupBy:
         *   Mark+Mario become "(Mar, 87)"
         *   Maciej becomes "(Mac, 39)"
         *   Geoffrey becomes "(Geo, 35)"
         *   Edson becomes "(Eds, 38)"
         *   Edoardo becomes "(Edo, 33)"
         *
         * Then in the second groupBy:
         *   "(Mar, 87)" and "(Mac, 39)" become "(Ma, 87)"
         *   "(Eds, 38)" and "(Edo, 33)" become "(Ed, 38)"
         *   "(Geo, 35)" becomes "(Ge, 35)"
         */

        assertEquals( 3, results.size() );
        assertEquals( 87, results.get("Ma") );
        assertEquals( 38, results.get("Ed") );
        assertEquals( 35, results.get("Ge") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();
        System.out.println("-----");

        // No Mario anymore, so "(Mar, 42)" instead of "(Mar, 87)".
        // Therefore "(Ma, 42)".
        assertEquals( 1, results.size() );
        assertEquals( 42, results.get("Ma") );
        results.clear();

        // "(Geo, 35)" is gone.
        // "(Mat, 38)" is added, but Mark still wins, so "(Ma, 42)" stays.
        ksession.delete(geoffreyFH);
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 42, results.get("Ma") );
    }

    @Test
    @Ignore // FIXME This does not work, because Declaration only works with function1
    public void testTwoGroupByUsingBindings() {
        // DROOLS-5697
        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");

        Variable<String> var_$key_1 = D.declarationOf(String.class);
        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
        Variable<Group> var_$g1 = D.declarationOf(Group.class); // "$g1", D.from(var_$key_1, var_$sumOfAges, ($k, $v) -> new Group($k, $v)));
        Variable<Integer> var_$g1_value = D.declarationOf(Integer.class);
        Variable<String> var_$key_2 = D.declarationOf(String.class);
        Variable<Integer> var_$maxOfValues = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
              D.groupBy(
                    D.and(
                          D.groupBy(
                                D.pattern(var_$p).bind(var_$age, person -> person.getAge()),
                                var_$p, var_$key_1, person -> person.getName().substring(0, 3),
                                D.accFunction( IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                          D.pattern(var_$key_1).bind(var_$g1, var_$sumOfAges, ($k, $v) -> new Group($k, $v)), // Currently this does not work
                          D.pattern(var_$g1).bind(var_$g1_value, group -> (Integer) group.getValue()) ),
                    var_$g1, var_$key_2, groupResult -> ((String)groupResult.getKey()).substring(0, 2),
                    D.accFunction( IntegerMaxAccumulateFunction::new, var_$g1_value).as(var_$maxOfValues)),
              D.on(var_$key_2, var_results, var_$maxOfValues)
               .execute(($key, results, $maxOfValues) -> {
                   System.out.println($key + " -> " + $maxOfValues);
                   results.put($key, $maxOfValues);
               })
                                       );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edoardo", 33));
        FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edson", 38));
        FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
        ksession.fireAllRules();
        System.out.println("-----");

        /*
         * In the first groupBy:
         *   Mark+Mario become "(Mar, 87)"
         *   Maciej becomes "(Mac, 39)"
         *   Geoffrey becomes "(Geo, 35)"
         *   Edson becomes "(Eds, 38)"
         *   Edoardo becomes "(Edo, 33)"
         *
         * Then in the second groupBy:
         *   "(Mar, 87)" and "(Mac, 39)" become "(Ma, 87)"
         *   "(Eds, 38)" and "(Edo, 33)" become "(Ed, 38)"
         *   "(Geo, 35)" becomes "(Ge, 35)"
         */

        assertEquals( 3, results.size() );
        assertEquals( 87, results.get("Ma") );
        assertEquals( 38, results.get("Ed") );
        assertEquals( 35, results.get("Ge") );
        results.clear();

        ksession.delete( meFH );
        ksession.fireAllRules();
        System.out.println("-----");

        // No Mario anymore, so "(Mar, 42)" instead of "(Mar, 87)".
        // Therefore "(Ma, 42)".
        assertEquals( 1, results.size() );
        assertEquals( 42, results.get("Ma") );
        results.clear();

        // "(Geo, 35)" is gone.
        // "(Mat, 38)" is added, but Mark still wins, so "(Ma, 42)" stays.
        ksession.delete(geoffreyFH);
        ksession.insert(new Person("Matteo", 38));
        ksession.fireAllRules();

        assertEquals( 1, results.size() );
        assertEquals( 42, results.get("Ma") );
    }

    public static class Group {
        private final Object key;
        private final Object value;

        public Group( Object key, Object value ) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    @Test
    public void testEmptyPatternOnGroupByKey() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<String> var_$key = D.declarationOf(String.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1)),
                // Filter
                D.pattern(var_$key),
                // Consequence
                D.on(var_$key, var_results)
                        .execute(($key, results) -> results.add($key))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));

        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(1);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
    }

    @Test
    public void testFilterOnGroupByKey() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<String> var_$key = D.declarationOf(String.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);

        Rule rule1 = D.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.pattern(var_$p),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1)),
                // Filter
                D.pattern(var_$key).expr(s -> s.length() > 0).expr(s -> s.length() < 2),
                // Consequence
                D.on(var_$key, var_results)
                        .execute(($key, results) -> results.add($key))
        );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));

        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(1);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
    }

    @Test
    public void testDecomposedGroupByKey() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Pair<String, String>> var_$key = (Variable) D.declarationOf(Pair.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);

        final Variable<String> var_$subkeyA = D.declarationOf(String.class);
        final Variable<String> var_$subkeyB = D.declarationOf(String.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        // Patterns
                        PatternDSL.pattern(var_$p),
                        // Grouping Function
                        var_$p, var_$key, person -> Pair.create(
                                person.getName().substring(0, 1),
                                person.getName().substring(1, 2))),
                // Bindings
                D.pattern(var_$key)
                        .bind(var_$subkeyA, Pair::getKey)
                        .bind(var_$subkeyB, Pair::getValue),
                // Consequence
                D.on(var_$subkeyA, var_$subkeyB, var_results)
                        .execute(($a, $b, results) -> {
                            results.add($a);
                            results.add($b);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<String> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(2);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
        Assertions.assertThat(results.get(1)).isEqualTo("a");
    }

    @Test
    public void testDecomposedGroupByKeyAndAccumulate() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Pair<String, String>> var_$key = (Variable) D.declarationOf(Pair.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);

        final Variable<String> var_$subkeyA = D.declarationOf(String.class);
        final Variable<String> var_$subkeyB = D.declarationOf(String.class);
        final Variable<Long> var_$accresult = D.declarationOf(Long.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        // Patterns
                        PatternDSL.pattern(var_$p),
                        // Grouping Function
                        var_$p, var_$key, person -> Pair.create(
                                person.getName().substring(0, 1),
                                person.getName().substring(1, 2)),
                        D.accFunction(CountAccumulateFunction::new).as(var_$accresult)),
                // Bindings
                D.pattern(var_$key)
                        .bind(var_$subkeyA, Pair::getKey)
                        .bind(var_$subkeyB, Pair::getValue),
                D.pattern(var_$accresult).expr( l -> l > 0 ),
                // Consequence
                D.on(var_$subkeyA, var_$subkeyB, var_$accresult, var_results)
                        .execute(($a, $b, $accresult, results) -> {
                            results.add($a);
                            results.add($b);
                            results.add($accresult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(3);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
        Assertions.assertThat(results.get(1)).isEqualTo("a");
        Assertions.assertThat(results.get(2)).isEqualTo(1L);
    }

    @Test
    public void testDecomposedGroupByKeyAnd2Accumulates() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Pair<String, String>> var_$key = (Variable) D.declarationOf(Pair.class);
        final Variable<Pair> var_$accumulate = D.declarationOf(Pair.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Person> var_$p2 = D.declarationOf(Person.class);

        final Variable<String> var_$subkeyA = D.declarationOf(String.class);
        final Variable<String> var_$subkeyB = D.declarationOf(String.class);
        final Variable<List> var_$accresult = D.declarationOf(List.class);
        final Variable<List> var_$accresult2 = D.declarationOf(List.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.and(
                                D.pattern(var_$p),
                                D.pattern(var_$p2)
                                        .bind(var_$accumulate, var_$p, Pair::create)
                        ),
                        // Grouping Function
                        var_$p, var_$key, person -> Pair.create(
                                person.getName().substring(0, 1),
                                person.getName().substring(1, 2)),
                        D.accFunction(CollectListAccumulateFunction::new, var_$accumulate).as(var_$accresult),
                        D.accFunction(CollectListAccumulateFunction::new, var_$accumulate).as(var_$accresult2)),
                // Bindings
                D.pattern(var_$key)
                        .bind(var_$subkeyA, Pair::getKey)
                        .bind(var_$subkeyB, Pair::getValue),
                D.pattern(var_$accresult),
                // Consequence
                D.on(var_$subkeyA, var_$subkeyB, var_$accresult, var_results)
                        .execute(($a, $b, $accresult, results) -> {
                            results.add($a);
                            results.add($b);
                            results.add($accresult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(3);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
        Assertions.assertThat(results.get(1)).isEqualTo("a");
    }

    @Test
    public void testDecomposedGroupByKeyAnd2AccumulatesInConsequence() throws Exception {
        // DROOLS-6031
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Pair<String, String>> var_$key = (Variable) D.declarationOf(Pair.class);
        final Variable<Pair> var_$accumulate = D.declarationOf(Pair.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Person> var_$p2 = D.declarationOf(Person.class);

        final Variable<String> var_$subkeyA = D.declarationOf(String.class);
        final Variable<String> var_$subkeyB = D.declarationOf(String.class);
        final Variable<List> var_$accresult = D.declarationOf(List.class);
        final Variable<List> var_$accresult2 = D.declarationOf(List.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        // Patterns
                        D.and(
                                D.pattern(var_$p),
                                D.pattern(var_$p2)
                                        .bind(var_$accumulate, var_$p, Pair::create)
                        ),
                        // Grouping Function
                        var_$p, var_$key, person -> Pair.create(
                                person.getName().substring(0, 1),
                                person.getName().substring(1, 2)),
                        D.accFunction(CollectListAccumulateFunction::new, var_$accumulate).as(var_$accresult),
                        D.accFunction(CollectListAccumulateFunction::new, var_$accumulate).as(var_$accresult2)),
                // Bindings
                D.pattern(var_$accresult2),
                // Consequence
                D.on(var_$key, var_$accresult, var_$accresult2, var_results)
                        .execute(($key, $accresult, $accresult2, results) -> {
                            results.add($key);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(1);
    }

    @Test
    public void testNestedGroupBy1a() throws Exception {
        // DROOLS-6045
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Object> var_$key = D.declarationOf(Object.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Object> var_$accresult = D.declarationOf(Object.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.accumulate(
                        D.and(
                                D.groupBy(
                                        // Patterns
                                        D.pattern(var_$p),
                                        // Grouping Function
                                        var_$p, var_$key, Person::getAge),
                                // Bindings
                                D.pattern(var_$key)
                                        .expr(k -> ((Integer)k) > 0)
                        ),
                        D.accFunction(CollectListAccumulateFunction::new, var_$key).as(var_$accresult)
                ),
                // Consequence
                D.on(var_$accresult, var_results)
                        .execute(($accresult, results) -> {
                            results.add($accresult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        System.out.println(results);
        Assertions.assertThat(results).containsOnly(Collections.singletonList(42));
    }

    @Test
    public void testNestedGroupBy1b() throws Exception {
        // DROOLS-6045
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Object> var_$key = D.declarationOf(Object.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Object> var_$accresult = D.declarationOf(Object.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.accumulate(
                        D.and(
                                D.groupBy(
                                        // Patterns
                                        D.pattern(var_$p),
                                        // Grouping Function
                                        var_$p, var_$key, Person::getAge),
                                // Bindings
                                D.pattern(var_$key)
                        ),
                        D.accFunction(CollectListAccumulateFunction::new, var_$key).as(var_$accresult)
                ),
                // Consequence
                D.on(var_$accresult, var_results)
                        .execute(($accresult, results) -> {
                            results.add($accresult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results).containsOnly(Collections.singletonList(42));
    }

    @Test
    public void testNestedGroupBy2() throws Exception {
        // DROOLS-6045
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Object> var_$key = D.declarationOf(Object.class);
        final Variable<Object> var_$keyOuter = D.declarationOf(Object.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Object> var_$accresult = D.declarationOf(Object.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        D.and(
                                D.groupBy(
                                        // Patterns
                                        D.pattern(var_$p),
                                        // Grouping Function
                                        var_$p, var_$key, Person::getAge),
                                // Bindings
                                D.pattern(var_$key)
                                        .expr(k -> ((Integer)k) > 0)
                        ),
                        var_$key, var_$keyOuter, k -> ((Integer)k) * 2,
                        D.accFunction(CollectListAccumulateFunction::new, var_$keyOuter).as(var_$accresult)
                ),
                // Consequence
                D.on(var_$keyOuter, var_$accresult, var_results)
                        .execute(($outerKey, $accresult, results) -> {
                            results.add($accresult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Test
    @Ignore // <- FIXME, see comment inside (@mario)
    public void testNestedGroupBy3() throws Exception {
        // DROOLS-6045
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Object> var_$key = D.declarationOf(Object.class);
        final Variable<Object> var_$keyOuter = D.declarationOf(Object.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Object> var_$accresult = D.declarationOf(Object.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        D.and(
                                D.groupBy(
                                        // Patterns
                                        D.pattern(var_$p),
                                        // Grouping Function
                                        var_$p, var_$key, Person::getName,
                                        D.accFunction(CountAccumulateFunction::new).as(var_$accresult)),
                                // Bindings
                                D.pattern(var_$accresult)
                                        .expr(c -> ((Integer)c) > 0) // FIXME var_$accresult is collection of Long, how did this pass before(mdp) ?
                        ),
                        var_$key, var_$accresult, var_$keyOuter, Pair::create
                ),
                // Consequence
                D.on(var_$keyOuter, var_results)
                        .execute(($outerKey, results) -> {
                            results.add($outerKey);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsOnly(Pair.create("Mark", 1L));
    }

    @Test
    public void testFilterOnAccumulateResultWithDecomposedGroupByKey() throws Exception {
        // DROOLS-6045
        final Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        final Variable<Pair<String, String>> var_$key = (Variable) D.declarationOf(Pair.class);
        final Variable<Person> var_$p = D.declarationOf(Person.class);
        final Variable<Integer> var_$pAge = D.declarationOf(Integer.class);

        final Variable<Object> var_$subkeyA = D.declarationOf(Object.class);
        final Variable<Object> var_$subkeyB = D.declarationOf(Object.class);
        final Variable<Integer> var_$accresult = D.declarationOf(Integer.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                D.groupBy(
                        // Patterns
                        PatternDSL.pattern(var_$p)
                                .bind(var_$pAge, Person::getAge)
                                .expr(Objects::nonNull),
                        // Grouping Function
                        var_$p, var_$key, person -> Pair.create(
                                person.getName().substring(0, 1),
                                person.getName().substring(1, 2)),
                        D.accFunction( IntegerSumAccumulateFunction::new, var_$pAge).as(var_$accresult)),
                // Bindings
                D.pattern(var_$key)
                        .bind(var_$subkeyA, Pair::getKey)
                        .bind(var_$subkeyB, Pair::getValue),
                D.pattern(var_$accresult)
                        .expr("Some expr", var_$subkeyA, var_$subkeyB, (a, b, c) -> true),
                // Consequence
                D.on(var_$subkeyA, var_$subkeyB, var_$accresult, var_results)
                        .execute(($a, $b, $accResult, results) -> {
                            results.add($a);
                            results.add($b);
                            results.add($accResult);
                        })
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final List<Object> results = new ArrayList<>();
        ksession.setGlobal( "results", results );

        ksession.insert( "A" );
        ksession.insert( "test" );
        ksession.insert(new Person("Mark", 42));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        Assertions.assertThat(results.size()).isEqualTo(3);
        Assertions.assertThat(results.get(0)).isEqualTo("M");
        Assertions.assertThat(results.get(1)).isEqualTo("a");
        Assertions.assertThat(results.get(2)).isEqualTo(42);
    }
// These two test are commented out, until we figure out the correct way to do this and limitations.
// If no correct way can be found, the tests can be deleted.
//    @Test
//    public void testErrorOnCompositeBind() {
//        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");
//
//        Variable<String> var_$key_1 = D.declarationOf(String.class);
//        Variable<Person> var_$p = D.declarationOf(Person.class);
//        Variable<Integer> var_$age = D.declarationOf(Integer.class);
//        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
//        Variable<Group> var_$g1 = D.declarationOf(Group.class);
//        Variable<Integer> var_$g1_value = D.declarationOf(Integer.class);
//        Variable<String> var_$key_2 = D.declarationOf(String.class);
//        Variable<Integer> var_$maxOfValues = D.declarationOf(Integer.class);
//
//        Rule rule1 = D.rule("R1").build(
//              D.groupBy(
//                    D.pattern(var_$p).bind(var_$age, person -> person.getAge()),
//                    var_$p, var_$key_1, groupResult -> var_$p.getName().substring(0, 2),
//                    D.accFunction( IntegerMaxAccumulateFunction::new, var_$age).as(var_$maxOfValues)),
//              D.pattern(var_$key_1).bind(var_$g1, var_$maxOfValues, (k, s) -> new Group(k, s)),
//              D.on(var_$key_1)
//               .execute($key -> { System.out.println($key); }));
//
//        try {
//            Model      model    = new ModelImpl().addRule(rule1).addGlobal(var_results);
//            KieSession ksession = KieBaseBuilder.createKieBaseFromModel(model).newKieSession();
//            fail("Composite Bindings are not allowed");
//        } catch(Exception e) {
//
//        }
//    }
//
//    @Test
//    public void testErrorOnNestedCompositeBind() {
//        Global<Map> var_results = D.globalOf(Map.class, "defaultpkg", "results");
//
//        Variable<String> var_$key_1 = D.declarationOf(String.class);
//        Variable<Person> var_$p = D.declarationOf(Person.class);
//        Variable<Integer> var_$age = D.declarationOf(Integer.class);
//        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
//        Variable<Group> var_$g1 = D.declarationOf(Group.class);
//        Variable<Integer> var_$g1_value = D.declarationOf(Integer.class);
//        Variable<String> var_$key_2 = D.declarationOf(String.class);
//        Variable<Integer> var_$maxOfValues = D.declarationOf(Integer.class);
//
//        Rule rule1 = D.rule("R1").build(
//              D.groupBy(
//                    D.and(
//                          D.groupBy(
//                                D.pattern(var_$p).bind(var_$age, person -> person.getAge()),
//                                var_$p, var_$key_1, person -> person.getName().substring(0, 3),
//                                D.accFunction( IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
//                          D.pattern(var_$key_1).bind(var_$g1, var_$sumOfAges, (k, s) -> new Group(k, s))), // this should fail, due to two declarations
//                    var_$g1, var_$key_2, groupResult -> ((String)groupResult.getKey()).substring(0, 2),
//                    D.accFunction( IntegerMaxAccumulateFunction::new, var_$g1_value).as(var_$maxOfValues)),
//              D.on(var_$key_1)
//               .execute($key -> { System.out.println($key); }));
//
//        try {
//            Model      model    = new ModelImpl().addRule(rule1).addGlobal(var_results);
//            KieSession ksession = KieBaseBuilder.createKieBaseFromModel(model).newKieSession();
//            fail("Composite Bindings are not allowed");
//        } catch(Exception e) {
//
//        }
//    }

    @Test
    public void testNestedRewrite() {
        // DROOLS-5697
        Global<List> var_results = D.globalOf(List.class, "defaultpkg", "results");

        Variable<Person> var_$p = D.declarationOf(Person.class);
        Variable<Integer> var_$age = D.declarationOf(Integer.class);
        Variable<Integer> var_$sumOfAges = D.declarationOf(Integer.class);
        Variable<Integer> var_$g1 = D.declarationOf(Integer.class);
        Variable<Integer> var_$maxOfValues = D.declarationOf(Integer.class);

        Rule rule1 = D.rule("R1").build(
              D.accumulate(
                    D.and(
                          D.accumulate(
                                D.pattern(var_$p).bind(var_$age, person -> person.getAge()),
                                D.accFunction( IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                          D.pattern(var_$sumOfAges).bind(var_$g1, (s) -> s+1)),
                    D.accFunction( IntegerMaxAccumulateFunction::new, var_$g1).as(var_$maxOfValues)),
              D.on(var_results, var_$maxOfValues)
               .execute((results, $maxOfValues) -> {
                   System.out.println($maxOfValues);
                   results.add($maxOfValues);
               })
                                       );

        Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        List results = new ArrayList();
        ksession.setGlobal( "results", results );

        FactHandle fhMark = ksession.insert(new Person("Mark", 42));
        FactHandle fhEdoardo = ksession.insert(new Person("Edoardo", 33));
        ksession.fireAllRules();
        assertTrue(results.contains(76));

        ksession.insert(new Person("Edson", 38));
        ksession.fireAllRules();
        assertTrue(results.contains(114));

        ksession.delete(fhEdoardo);
        ksession.fireAllRules();
        assertTrue(results.contains(81));

        ksession.update(fhMark, new Person("Mark", 45));
        ksession.fireAllRules();
        assertTrue(results.contains(84));
    }

}

