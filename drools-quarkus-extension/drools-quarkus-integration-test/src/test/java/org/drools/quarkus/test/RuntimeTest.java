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
import org.kie.api.definition.KiePackage;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.api.prototype.PrototypeBuilder.prototype;

@QuarkusTest
public class RuntimeTest {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    public void testDrlEvaluation() {
        // canDrinkKS is the default session
        testSimpleDrl(runtimeBuilder.newKieSession(), "org.drools.drl");
    }

    @Test
    public void testDTableEvaluation() {
        testSimpleDrl(runtimeBuilder.newKieSession("canDrinkKSDTable"), "org.drools.dtable");
    }

    @Test
    public void testYamlEvaluation() {
        testSimpleDrl(runtimeBuilder.newKieSession("canDrinkKSYaml"), "org.drools.yaml");
    }

    private void testSimpleDrl(KieSession ksession, String assetPackage) {
        List<String> pkgNames = ksession.getKieBase().getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        assertEquals(2, pkgNames.size());
        assertTrue(pkgNames.contains("org.drools.quarkus.test"));
        assertTrue(pkgNames.contains(assetPackage));

        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 17));
        ksession.fireAllRules();

        assertEquals("Mark can NOT drink", result.toString());
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

        assertEquals("Mark can NOT drink", result.get("value"));
    }
}
