/*
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

import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.drools.model.DSL.and;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.execute;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;

public class SubnetworkNotDeleteRightNpeTest {

    @Role(Role.Type.EVENT)
    public static class EventA { private final int id; public EventA(int id) { this.id = id; } public int getId() { return id; } }
    @Role(Role.Type.EVENT)
    public static class EventB { private final int id; public EventB(int id) { this.id = id; } public int getId() { return id; } }

    private int exprSeq = 0;

    @Test
    public void twoSubnetworkNots_separatedByPlainNot_doesNotNpeOnDelete() {
        Variable<EventA> eventA1 = declarationOf(EventA.class), eventA2 = declarationOf(EventA.class);
        Variable<EventB> eventB1 = declarationOf(EventB.class), eventB2 = declarationOf(EventB.class);

        Rule rule = rule("twoSubnetworkNots").build(
                not(and(anyOf(eventA1), anyOf(eventB1))),   // subnetwork not
                not(and(anyOf(eventA2), anyOf(eventB2))),   // subnetwork not
                execute(() -> { }));

        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(
                new ModelImpl().addRule(rule),
                EventProcessingOption.STREAM
        );

        KieSession ksession = kieBase.newKieSession(
                KieServices.get().newKieSessionConfiguration(),
                null);

        Object[] objects= {
                new EventA(0),
                new EventB(1)
        };

        assertThatCode(() -> {
            for (Object object : objects) {
                ksession.insert(object);
                ksession.fireAllRules();
            }
            ksession.fireAllRules();
        }).doesNotThrowAnyException();

        ksession.dispose();
    }

    private <T> org.drools.model.PatternDSL.PatternDef<T> anyOf(Variable<T> event) {
        return pattern(event).expr("any_" + (exprSeq++), x -> true);
    }
}
