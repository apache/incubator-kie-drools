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

    private static final Variable<Person> PERSON_VARIABLE = declarationOf(Person.class);

    private static final Rule RULE_1 = rule("org.drools.test.included", "rule1")
            .build(
                    pattern(PERSON_VARIABLE)
                            .expr("exprA", p -> p.getName().equals("Frantisek"))
                            .expr("exprB", p -> p.getAge() > 20),
                    on(PERSON_VARIABLE).execute((p) -> System.out.println(p.getName() + " is older than " + 20)));

    private static final Rule RULE_2 = rule("org.drools.test.main", "rule2")
            .build(
                    pattern(PERSON_VARIABLE)
                            .expr("exprA", p -> p.getName().equals("Frantisek"))
                            .expr("exprB", p -> p.getAge() > 30),
                    on(PERSON_VARIABLE).execute((p) -> System.out.println(p.getName() + " is older than " + 30)));

    private static final Rule RULE_3 = rule("org.drools.test.includedinincluded", "rule3")
            .build(
                    pattern(PERSON_VARIABLE)
                            .expr("exprA", p -> p.getName().equals("Frantisek"))
                            .expr("exprB", p -> p.getAge() > 10),
                    on(PERSON_VARIABLE).execute((p) -> System.out.println(p.getName() + " is older than " + 10)));

    @Test
    public void createKieBaseFromModelWithKieBaseIncludes() {
        final Model model1 = new ModelImpl("org.drools.test.included").addRule(RULE_1);
        final Model model2 = new ModelImpl("org.drools.test.main").addRule(RULE_2);
        final Model model3 = new ModelImpl("org.drools.test.includedinincluded").addRule(RULE_3);

        final KieModuleModel kieModuleModel = new KieModuleModelImpl();

        final KieBaseModel kieBaseModelIncludedInIncluded = kieModuleModel.newKieBaseModel("IncludedInIncluded");
        kieBaseModelIncludedInIncluded.addPackage("org.drools.test.includedinincluded");

        final KieBaseModel kieBaseModelIncluded = kieModuleModel.newKieBaseModel("IncludedModel");
        kieBaseModelIncluded.addPackage("org.drools.test.included");
        kieBaseModelIncluded.addInclude("IncludedInIncluded");

        final KieBaseModel kieBaseModelThatIncludes = kieModuleModel.newKieBaseModel("ModelThatIncludesTheOther");
        kieBaseModelThatIncludes.addPackage("org.drools.test.main");
        kieBaseModelThatIncludes.addInclude("IncludedModel");

        final KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(List.of(model1, model2, model3), kieBaseModelThatIncludes, kieModuleModel);
        try (KieSession ksession = kieBase.newKieSession()) {
            final Person personToBeInserted = new Person("Frantisek", 38);
            ksession.insert(personToBeInserted);
            // This assert checks that all three rules fire. If not, the includes don't work properly.
            assertThat(ksession.fireAllRules()).isEqualTo(3);
        }
    }
}
