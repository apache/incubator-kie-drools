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

import java.util.ArrayList;
import java.util.List;

import org.drools.base.facttemplates.Fact;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.prototype.Prototype;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.model.Rule;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.prototype.PrototypeDSL.protoPattern;
import static org.drools.model.prototype.PrototypeDSL.prototype;
import static org.drools.model.prototype.PrototypeDSL.variable;
import static org.drools.model.prototype.facttemplate.FactFactory.createMapBasedFact;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrototypeDialectTest {

    @Test
    public void testModel() {
        PrototypeVariable personV = variable(prototype( "Person" ));

        Rule rule = rule("adult" )
                .build(
                        protoPattern(personV)
                                .expr("age", Index.ConstraintType.GREATER_OR_EQUAL, 18 ),
                        on(personV).execute(p -> p.set("adult", true))
                );

        Model model = new ModelImpl().addRule(rule );
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model );

        KieSession ksession = kieBase.newKieSession();

        Prototype personFact = prototype( "Person" );

        Fact sofia = createMapBasedFact(personFact);
        sofia.set( "name", "Sofia" );
        sofia.set( "age", 12 );

        ksession.insert(sofia);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
        assertThat(sofia.get("adult")).isNull();

        Fact mario = createMapBasedFact(personFact);
        mario.set( "name", "Mario" );
        mario.set( "age", 49 );

        ksession.insert(mario);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(mario.get("adult")).isEqualTo(true);
    }

    @Test
    public void testPrototypeDrl() {
        String str =
                """
                dialect "prototype"
                global java.util.List results
                rule Adult when
                  $p : Person( age >= 18 )
                then
                  $p.adult = true;
                  results.add($p.name);
                end
                """;

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build(ExecutableModelProject.class)
                .newKieSession();

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Prototype personFact = prototype( "Person" );

        Fact sofia = createMapBasedFact(personFact);
        sofia.set( "name", "Sofia" );
        sofia.set( "age", 12 );

        ksession.insert(sofia);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
        assertThat(sofia.get("adult")).isNull();

        Fact mario = createMapBasedFact(personFact);
        mario.set( "name", "Mario" );
        mario.set( "age", 49 );

        ksession.insert(mario);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(mario.get("adult")).isEqualTo(true);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testJoin() {
        String str =
                """
                package org.drools.prototype;
                dialect "prototype";
                
                rule "R1"
                when
                  $r : Result()
                  $p : Person(age >= 18)
                then
                  $r.value = $p.name + " can drink";
                end
               
                rule "R2"
                when
                  $r : Result()
                  $p : Person(age < 18)
                then
                  $r.value = $p.name + " can NOT drink";
                end
                """;

        KieSession ksession = new KieHelper()
                .addContent(str, ResourceType.DRL)
                .build(ExecutableModelProject.class)
                .newKieSession();

        Prototype personFact = prototype( "Person" );
        Fact mark = createMapBasedFact(personFact);
        mark.set( "name", "Mark" );
        mark.set( "age", 17 );
        ksession.insert(mark);

        Prototype resultFact = prototype( "Result" );
        Fact result = createMapBasedFact(resultFact);
        ksession.insert(result);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertEquals("Mark can NOT drink", result.get("value"));
    }
}
