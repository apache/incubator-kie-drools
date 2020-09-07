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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.core.base.accumulators.CountAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Global;
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
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GroupByTest {
    @Test
    public void testSumPersonAgeGroupByInitialWithAcc() {
        final Variable<Person> var_GENERATED_$pattern_Person$4$ = DSL.declarationOf(Person.class);
        final Variable<String> var_$initial = DSL.declarationOf(String.class);
        final Variable<GroupKey> var_sCoPe3_GENERATED_$pattern_GroupKey$3$ = DSL.declarationOf(GroupKey.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                PatternDSL.pattern(var_GENERATED_$pattern_Person$4$)
                        .bind(var_$initial, (Person _this) -> _this.getName().substring(0, 1)),
                DSL.not(PatternDSL.pattern(var_sCoPe3_GENERATED_$pattern_GroupKey$3$).expr("FF3B1999B2904B3324A471615B8760C9",
                        var_$initial,
                        (GroupKey _this, String $initial) -> EvaluationUtil.areNullSafeEquals(_this.getKey(), $initial),
                        PatternDSL.reactOn("key"))),
                DSL.on(var_$initial).execute((Drools drools, String $initial) -> {
                    {
                        drools.insert(new GroupKey("a", $initial));
                    }
                }));

        final Variable<GroupKey> var_$k = DSL.declarationOf(GroupKey.class);
        final Variable<Object> var_$key = DSL.declarationOf(Object.class);
        final Variable<Person> var_sCoPe4_GENERATED_$pattern_Person$5$ = DSL.declarationOf(Person.class);

        final Rule rule2 = PatternDSL.rule("R2").build(
                PatternDSL.pattern(var_$k).expr("8313F8B6FD1C0612B7758BFDB93F0DE4", (GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), "a"),
                        PatternDSL.reactOn("topic"))
                        .bind(var_$key, (GroupKey _this) -> _this.getKey(),
                                PatternDSL.reactOn("key")),
                DSL.not(PatternDSL.pattern(var_sCoPe4_GENERATED_$pattern_Person$5$).expr("AFBC8D66DD9165C71D89004BBF5B0F9C",
                        var_$key,
                        (Person _this, Object $key) -> EvaluationUtil.areNullSafeEquals(_this.getName().substring(0, 1), $key.toString()),
                        PatternDSL.reactOn("name"))),
                DSL.on(var_$k).execute((Drools drools, GroupKey $k) -> {
                    {
                        drools.delete($k);
                    }
                }));

        final Global<Map> var_results = DSL.globalOf(Map.class, "defaultpkg", "results");

        final Variable<GroupKey> var_GENERATED_$pattern_GroupKey$4$ = DSL.declarationOf(GroupKey.class);
        final Variable<Person> var_GENERATED_$pattern_Person$6$ = DSL.declarationOf(Person.class);
        final Variable<Integer> var_$age = DSL.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = DSL.declarationOf(Integer.class);

        final Rule rule3 = PatternDSL.rule("R3").build(
                PatternDSL.pattern(var_GENERATED_$pattern_GroupKey$4$).expr("8313F8B6FD1C0612B7758BFDB93F0DE4", ( GroupKey _this) -> EvaluationUtil.areNullSafeEquals(_this.getTopic(), "a"),
                        PatternDSL.reactOn("topic")).bind(var_$key,
                        (GroupKey _this) -> _this.getKey(),
                        PatternDSL.reactOn("key")),
                DSL.accumulate(PatternDSL.pattern(var_GENERATED_$pattern_Person$6$)
                                .bind(var_$age, (Person _this) -> _this.getAge(), PatternDSL.reactOn("age"))
                                .expr("AFBC8D66DD9165C71D89004BBF5B0F9C",
                                        var_$key,
                                        (Person _this, Object $key) -> EvaluationUtil.areNullSafeEquals(_this.getName().substring(0, 1), $key),
                                        PatternDSL.reactOn("name")),
                        DSL.accFunction(IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                PatternDSL.pattern(var_$sumOfAges).expr("00DE1D5962263283D8D799CF83F1A729",
                        (Integer $sumOfAges) -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                DSL.on(var_$key,
                        var_results,
                        var_$sumOfAges).execute((Object $key, Map results, Integer $sumOfAges) -> {
                    {
                        results.put($key, $sumOfAges);
                    }
                }));

        final Model model = new ModelImpl().addRule( rule1 ).addRule( rule2 ).addRule( rule3 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        final FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        final FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
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
    public void testSumPersonAgeGroupByInitial() throws Exception {
        final Global<Map> var_results = DSL.globalOf(Map.class, "defaultpkg", "results");

        final Variable<String> var_$key = DSL.declarationOf(String.class);
        final Variable<Person> var_$p = DSL.declarationOf(Person.class);
        final Variable<Integer> var_$age = DSL.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = DSL.declarationOf(Integer.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                DSL.groupBy(
                        // Patterns
                        PatternDSL.pattern(var_$p).bind(var_$age, person -> person.getAge(), PatternDSL.reactOn("age")),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        DSL.accFunction(IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                PatternDSL.pattern(var_$sumOfAges).expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                DSL.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        final FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        final FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
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
        final Global<Map> var_results = DSL.globalOf(Map.class, "defaultpkg", "results");

        final Variable<String> var_$key = DSL.declarationOf(String.class);
        final Variable<String> var_$string = DSL.declarationOf(String.class);
        final Variable<String> var_$initial = DSL.declarationOf(String.class);
        final Variable<Person> var_$p = DSL.declarationOf(Person.class);
        final Variable<Integer> var_$age = DSL.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = DSL.declarationOf(Integer.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                DSL.groupBy(
                        // Patterns
                        DSL.and(
                                PatternDSL.pattern(var_$p)
                                        .bind(var_$age, person -> person.getAge(), PatternDSL.reactOn("age"))
                                        .bind(var_$initial, person -> person.getName().substring(0, 1)),
                                DSL.exists(PatternDSL.pattern(var_$string).expr(var_$initial, (_this, $initial) -> EvaluationUtil.areNullSafeEquals(_this, $initial)))
                        ),
                        // Grouping Function
                        var_$p, var_$key, person -> person.getName().substring(0, 1),
                        // Accumulate Result (can be more than one)
                        DSL.accFunction(IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                PatternDSL.pattern(var_$sumOfAges).expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                DSL.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        final FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        final FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));

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

        private final GroupByTest.MyType nested;

        public MyType(final GroupByTest.MyType nested) {
            this.nested = nested;
        }

        public GroupByTest.MyType getNested() {
            return nested;
        }
    }

    @Test
    public void testWithNull() {
        final Variable<GroupByTest.MyType> var = DSL.declarationOf(GroupByTest.MyType.class);
        final Variable<GroupByTest.MyType> groupKey = DSL.declarationOf(GroupByTest.MyType.class);
        final Variable<Long> count = DSL.declarationOf(Long.class);

        final AtomicInteger mappingFunctionCallCounter = new AtomicInteger(0);
        final Function1<GroupByTest.MyType, GroupByTest.MyType> mappingFunction = ( a) -> {
            mappingFunctionCallCounter.incrementAndGet();
            return a.getNested();
        };
        final D.PatternDef<GroupByTest.MyType> onlyOnesWithNested = PatternDSL.pattern(var)
                .expr(myType -> myType.getNested() != null);
        final ExprViewItem groupBy = DSL.groupBy(onlyOnesWithNested, var, groupKey, mappingFunction,
                DSL.accFunction(CountAccumulateFunction::new).as(count));

        final List<GroupByTest.MyType> result = new ArrayList<>();

        final Rule rule = PatternDSL.rule("R")
                .build(groupBy,
                        DSL.on(groupKey, count)
                                .execute((drools, key, acc) -> result.add(key)));

        final Model model = new ModelImpl().addRule( rule );
        final KieBase kieBase = KieBaseBuilder.createKieBaseFromModel( model );

        final GroupByTest.MyType objectWithoutNestedObject = new GroupByTest.MyType(null);
        final GroupByTest.MyType objectWithNestedObject = new GroupByTest.MyType(objectWithoutNestedObject);
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(objectWithNestedObject);
        ksession.insert(objectWithoutNestedObject);
        ksession.fireAllRules();

        // Side issue: this number is unusually high. Perhaps we should try to implement some cache for this?
        System.out.println("GroupKey mapping function was called " + mappingFunctionCallCounter.get() + " times.");

        Assertions.assertThat(result).containsOnly(objectWithoutNestedObject);
    }

    @Test
    public void testWithGroupByAfterExists() {
        final Global<Map> groupResultVar = DSL.globalOf(Map.class, "defaultPkg", "glob");

        final Variable<Integer> patternVar = DSL.declarationOf(Integer.class);
        final Variable<String> existsVar = DSL.declarationOf(String.class);
        final Variable<Integer> keyVar = DSL.declarationOf(Integer.class);
        final Variable<Long> resultVar = DSL.declarationOf(Long.class);

        final D.PatternDef<Integer> pattern = PatternDSL.pattern(patternVar);
        final D.PatternDef<String> exist = PatternDSL.pattern(existsVar);
        final ViewItem patternAndExists = DSL.and(
                pattern,
                DSL.exists(exist));

        final ViewItem groupBy = DSL.groupBy(patternAndExists, patternVar, keyVar, Math::abs,
                DSL.accFunction(CountAccumulateFunction::new).as(resultVar));
        final ConsequenceBuilder._3 consequence = DSL.on(keyVar, resultVar, groupResultVar)
                .execute((key, count, result) -> {
                    result.put(key, count.intValue());
                });

        final Rule rule = PatternDSL.rule("R").build(groupBy, consequence);

        final Model model = new ModelImpl().addRule(rule).addGlobal( groupResultVar );
        final KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        final KieSession session = kieBase.newKieSession();
        final Map<Integer, Integer> global = new HashMap<>();
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
        final Global<Map> var_results = DSL.globalOf(Map.class, "defaultpkg", "results");

        final Variable<String> var_$key = DSL.declarationOf(String.class);
        final Variable<Person> var_$p = DSL.declarationOf(Person.class);
        final Variable<Integer> var_$age = DSL.declarationOf(Integer.class);
        final Variable<String> var_$s = DSL.declarationOf(String.class);
        final Variable<Integer> var_$l = DSL.declarationOf(Integer.class);
        final Variable<Integer> var_$sumOfAges = DSL.declarationOf(Integer.class);

        final Rule rule1 = PatternDSL.rule("R1").build(
                DSL.groupBy(
                        // Patterns
                        DSL.and(
                                PatternDSL.pattern(var_$p).bind(var_$age, Person::getAge, PatternDSL.reactOn("age")),
                                PatternDSL.pattern(var_$s).bind(var_$l, String::length, PatternDSL.reactOn("length"))
                        ),
                        // Grouping Function
                        var_$p, var_$s, var_$key, (person, string) -> person.getName().substring(0, 1) + string.length(),
                        // Accumulate Result (can be more than one)
                        DSL.accFunction(IntegerSumAccumulateFunction::new, var_$age).as(var_$sumOfAges)),
                // Filter
                PatternDSL.pattern(var_$sumOfAges).expr($sumOfAges -> EvaluationUtil.greaterThanNumbers($sumOfAges, 10)),
                // Consequence
                DSL.on(var_$key, var_results, var_$sumOfAges)
                        .execute(($key, results, $sumOfAges) -> results.put($key, $sumOfAges))
        );

        final Model model = new ModelImpl().addRule( rule1 ).addGlobal( var_results );
        final KieSession ksession = KieBaseBuilder.createKieBaseFromModel( model ).newKieSession();

        final Map results = new HashMap();
        ksession.setGlobal( "results", results );

        ksession.insert( "test" );
        ksession.insert( "check" );
        ksession.insert(new Person("Mark", 42));
        ksession.insert(new Person("Edson", 38));
        final FactHandle meFH = ksession.insert(new Person("Mario", 45));
        ksession.insert(new Person("Maciej", 39));
        ksession.insert(new Person("Edoardo", 33));
        final FactHandle geoffreyFH = ksession.insert(new Person("Geoffrey", 35));
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
}
