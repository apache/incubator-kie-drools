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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.assertj.core.api.Assertions;
import org.drools.model.Model;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.dsl.pattern.D;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class PatternAccumulateDSLTest {

    @Test
    public void testAccumulateMultipleBindings() {
        final List<Number> resultTotal = new ArrayList<>();

        final List<List<Pair>> resultPair = new ArrayList<>();

        final Variable<Person> var_pattern_person1 = D.declarationOf(Person.class, "GENERATED_$pattern_Person$5$");
        final Variable<String> var_$n1 = D.declarationOf(String.class, "$n1");

        final Variable<Person> var_pattern_person2 = D.declarationOf(Person.class, "GENERATED_$pattern_Person$6$");
        final Variable<String> var_$n2 = D.declarationOf(String.class, "$n2");

        final Variable<Pair> var_pair = D.declarationOf(Pair.class, "FB9DB225D1226EF9D8D5DB8F0F1F5B75");
        final Variable<List> var_$pair_list = D.declarationOf(List.class, "$pair");

        final Variable<Pair> var_pair2 = D.declarationOf(Pair.class, "FAD99C65863774F9ABA8C49DE74E3851");
        final Variable<Long> var_$pair_count = D.declarationOf(Long.class, "$total");

        org.drools.model.Rule rule = D.rule("R").build(D.accumulate(D.and(D.pattern(var_pattern_person1).bind(var_$n1,
                                                                                                              (Person _this) -> _this.getName(),
                                                                                                              D.reactOn("name")),
                                                                          D.pattern(var_pattern_person2).bind(var_pair,
                                                                                                              var_$n1,
                                                                                                              var_$n2,
                                                                                                              (Person _this, String $n1, String $n2) -> Pair.create($n1,
                                                                                                                                                                    $n2)).bind(var_$n2,
                                                                                                                                                                               (Person _this) -> _this.getName(),
                                                                                                                                                                               D.reactOn("name"))),
                                                                    D.accFunction(org.drools.core.base.accumulators.CollectListAccumulateFunction::new,
                                                                                  var_pair).as(var_$pair_list),
                                                                    D.accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new,
                                                                                  var_pair2).as(var_$pair_count)),
                                                       D.on(var_$pair_list,
                                                            var_$pair_count
                                                       ).execute((List $pair, Long $total) -> {
                                                           {
                                                               resultTotal.add($total);
                                                               resultPair.add($pair);
                                                           }
                                                       }));

        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);

        KieSession ksession = kieBase.newKieSession();

        Person mario = new Person("Mario", 40);
        ksession.insert(mario);

        int rulesFired = ksession.fireAllRules();
        Assertions.assertThat(rulesFired).isGreaterThan(0);
        Assertions.assertThat(resultTotal).contains(1l);

        List<Pair> resultPairItem = resultPair.iterator().next();
        Pair firstPair = resultPairItem.iterator().next();
        assertEquals("Mario", firstPair.getFirst());
        assertEquals("Mario", firstPair.getSecond());
    }
}
