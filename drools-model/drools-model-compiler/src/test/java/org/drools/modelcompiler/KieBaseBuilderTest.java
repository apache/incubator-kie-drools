/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright
 * ownership.  The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package org.drools.modelcompiler;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class KieBaseBuilderTest {

    @Test
    public void createKieBaseFromModelWithKieBaseIncludes() {
        final Result result = new Result();
        final Variable<Person> personVariable = declarationOf(Person.class);

        final Rule rule1 = rule("org.drools.test.included", "rule1")
                .build(
                        pattern(personVariable)
                                .expr("exprA", p -> p.getName().equals("Frantisek"))
                                .expr("exprB", p -> p.getAge() > 20),
                        on(personVariable).execute((p) -> result.setValue(p.getName() + " is older than " + 20)));

        final Rule rule2 = rule("org.drools.test.main", "rule2")
                .build(
                        pattern(personVariable)
                                .expr("exprA", p -> p.getName().equals("Frantisek"))
                                .expr("exprB", p -> p.getAge() > 30),
                        on(personVariable).execute((p) -> result.setValue(p.getName() + " is older than " + 30)));

        final Model model1 = new ModelImpl("org.drools.test.included").addRule(rule1);
        final Model model2 = new ModelImpl("org.drools.test.main").addRule(rule2);

        final KieModuleModel kieModuleModel = new KieModuleModelImpl();
        final KieBaseModel kieBaseModelIncluded = kieModuleModel.newKieBaseModel("IncludedModel");
        kieBaseModelIncluded.addPackage("org.drools.test.included");
        final KieBaseModel kieBaseModelThatIncludes = kieModuleModel.newKieBaseModel("ModelThatIncludesTheOther");
        kieBaseModelThatIncludes.addPackage("org.drools.test.main");
        kieBaseModelThatIncludes.addInclude("IncludedModel");
        final KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(List.of(model1, model2), kieBaseModelThatIncludes, kieModuleModel);
        try (KieSession ksession = kieBase.newKieSession()) {
            final Person personToBeInserted = new Person("Frantisek", 38);
            ksession.insert(personToBeInserted);
            assertThat(ksession.fireAllRules()).isEqualTo(2);
        }
    }
}
