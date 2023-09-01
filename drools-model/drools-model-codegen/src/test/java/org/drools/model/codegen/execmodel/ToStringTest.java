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

import java.util.function.Supplier;

import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.model.DSL;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ExprViewItem;
import org.junit.Test;
import org.kie.api.runtime.rule.AccumulateFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class ToStringTest {

    /**
     * Users may depend on seeing {@link Rule#toString()} in log files giving useful information, in order to understand
     * the rules that are being created. The format is not required to be backwards compatible - this test merely checks
     * that it does not change unknowingly.
     */
    @Test
    public void testToString() {
        Variable<Person> markV = declarationOf(Person.class);
        Variable<Integer> markAge = declarationOf(Integer.class);
        Variable<Person> olderV = declarationOf(Person.class);
        Variable<Double> resultAvg = declarationOf(Double.class);
        Variable<Integer> age = declarationOf(Integer.class);

        String person = "Mark";
        Function1<Person, String> nameGetter = Person::getName;
        Function1<Person, Integer> ageGetter = Person::getAge;
        Predicate1<Person> markPredicate = p -> p.getName().equals(person);
        PatternDSL.PatternDef<Person> pattern1 = pattern(markV)
                .expr("exprA", markPredicate,
                        alphaIndexedBy(String.class, Index.ConstraintType.EQUAL, 0, nameGetter, person))
                .bind(markAge, ageGetter);

        Predicate1<Person> notMarkPredicate = markPredicate.negate();
        Predicate2<Person, Integer> agePredicate = (p1, someAge) -> p1.getAge() > someAge;
        Function1<Integer, Integer> ageCaster = int.class::cast;
        PatternDSL.PatternDef<Person> pattern2 = pattern(olderV)
                .expr("exprB", notMarkPredicate,
                        alphaIndexedBy(String.class, Index.ConstraintType.NOT_EQUAL, 1, nameGetter, person))
                .expr("exprC", markAge, agePredicate,
                        betaIndexedBy(int.class, Index.ConstraintType.GREATER_THAN, 0, ageGetter, ageCaster));

        AccumulateFunction<AverageAccumulateFunction.AverageData> f = new AverageAccumulateFunction();
        Supplier<AccumulateFunction<AverageAccumulateFunction.AverageData>> accumulateSupplier = () -> f;
        org.drools.model.functions.accumulate.AccumulateFunction actualAccumulate = accFunction(accumulateSupplier, age);
        ExprViewItem<Person> accumulate = accumulate(pattern(olderV).expr("exprD", notMarkPredicate)
                .bind(age, ageGetter), actualAccumulate.as(resultAvg));

        Rule rule = rule("beta")
                .build(pattern1,
                        pattern2,
                        accumulate,
                        DSL.on(olderV, markV).execute((drools, p1, p2) -> drools.insert(p1.getName() + " is older than " + p2.getName()))
                );

        String pattern1toString = "PatternImpl (type: PATTERN, inputVars: null, " +
                "outputVar: " + markV + ", " +
                "constraint: Constraint for 'exprA' (index: AlphaIndex #0 (EQUAL, left: lambda " + System.identityHashCode(nameGetter) + ", right: " + person + ")))";
        String pattern2toString = "PatternImpl (type: PATTERN, inputVars: null, " +
                "outputVar: " + olderV + ", " +
                "constraint: MultipleConstraints (constraints: [" +
                "Constraint for 'exprB' (index: AlphaIndex #1 (NOT_EQUAL, left: lambda " + System.identityHashCode(nameGetter) + ", right: " + person + ")), " +
                "Constraint for 'exprC' (index: BetaIndex #0 (GREATER_THAN, left: lambda " + System.identityHashCode(ageGetter) + ", right: lambda " + System.identityHashCode(ageCaster) + "))]))";
        String accumulatePatternToString = "PatternImpl (type: PATTERN, inputVars: null, " +
                        "outputVar: " + olderV + ", " +
                        "constraint: Constraint for 'exprD' (index: null))";
        String accumulateToString = "AccumulatePatternImpl (functions: [" + actualAccumulate + "], " +
                "condition: " + accumulatePatternToString + ", " +
                "pattern: " + accumulatePatternToString + ")";
        String consequenceToString = "ConsequenceImpl (variables: [" + olderV + ", " + markV + "], language: java, breaking: false)";
        String expectedToString = "Rule: defaultpkg.beta (" +
                "view: CompositePatterns of AND (vars: null, patterns: [" + pattern1toString +
                ", " + pattern2toString +
                ", " + accumulateToString +
                ", NamedConsequenceImpl 'default' (breaking: false)], " +
                "consequences: {default=" + consequenceToString +
                "}), consequences: {default=" + consequenceToString + "})";

        assertThat(rule).hasToString(expectedToString);
    }
}
