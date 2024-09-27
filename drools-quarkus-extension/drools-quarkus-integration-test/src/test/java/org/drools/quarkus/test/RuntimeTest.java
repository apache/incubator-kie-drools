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
package org.drools.quarkus.test;

import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.command.KieCommands;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.utils.KieService;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RuntimeSession;
import org.kie.api.runtime.StatelessKieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

@QuarkusTest
public class RuntimeTest {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    public void testDrlEvaluation() {
        // canDrinkKS is the default session
        testSimpleDrl(runtimeBuilder.newKieSession(), "org.drools.drl", true);
    }

    @Test
    public void testDTableEvaluation() {
        testSimpleDrl(runtimeBuilder.newKieSession("canDrinkKSDTable"), "org.drools.dtable", true);
    }

    @Test
    public void testYamlEvaluation() {
        testSimpleDrl(runtimeBuilder.newKieSession("canDrinkKSYaml"), "org.drools.yaml", true);
    }

    @Test
    public void testStatelessDrlEvaluation() {
        // statelessCanDrinkKS is the default stateless session
        testSimpleDrl(runtimeBuilder.newStatelessKieSession(), "org.drools.drl", false);
    }

    @Test
    public void testStatelessDTableEvaluation() {
        testSimpleDrl(runtimeBuilder.newStatelessKieSession("statelessCanDrinkKSDTable"), "org.drools.dtable", false);
    }

    @Test
    public void testStatelessYamlEvaluation() {
        testSimpleDrl(runtimeBuilder.newStatelessKieSession("statelessCanDrinkKSYaml"), "org.drools.yaml", false);
    }

    private void testSimpleDrl(RuntimeSession session, String assetPackage, boolean stateful) {
        if (stateful) {
            assertThat(session).isInstanceOf(KieSession.class);
        } else {
            assertThat(session).isInstanceOf(StatelessKieSession.class);
        }

        List<String> pkgNames = session.getKieBase().getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        
        assertThat(pkgNames).hasSize(2).containsExactlyInAnyOrder("org.drools.quarkus.test", assetPackage);

        KieCommands kieCommands = KieServices.get().getCommands();

        Result result = new Result();
        session.execute( kieCommands.newBatchExecution(
                kieCommands.newInsert(result),
                kieCommands.newInsert(new Person("Mark", 17)),
                kieCommands.newFireAllRules() ) );

        assertThat(result.toString()).isEqualTo("Mark can NOT drink");

        if (stateful) {
            ((KieSession) session).dispose();
        }
    }

    @Test
    public void testPrototypeEvaluation() {
        KieSession ksession = runtimeBuilder.newKieSession("canDrinkKSPrototype");

        PrototypeFact personFact = prototype("Person" ).asFact();
        PrototypeFactInstance mark = personFact.newInstance();
        mark.put("name", "Mark" );
        mark.put("age", 17 );
        ksession.insert(mark);

        PrototypeFact resultFact = prototype( "Result" ).asFact();
        PrototypeFactInstance result = resultFact.newInstance();
        ksession.insert(result);

        ksession.fireAllRules();
        assertThat(result.get("value")).isEqualTo("Mark can NOT drink");
    }
}
