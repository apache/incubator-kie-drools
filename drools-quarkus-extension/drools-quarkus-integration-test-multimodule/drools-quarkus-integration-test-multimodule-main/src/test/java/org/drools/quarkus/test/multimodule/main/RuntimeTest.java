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
package org.drools.quarkus.test.multimodule.main;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.drools.quarkus.test.multimodule.dep.Person;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class RuntimeTest {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    void drlInDependencyJar() {
        // pick drl from dependency jar
        KieSession kieSession = runtimeBuilder.newKieSession();

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("results", results);

        Person john = new Person("John", 25);
        Person paul = new Person("Paul", 17);
        kieSession.insert(john);
        kieSession.insert(paul);
        kieSession.fireAllRules();

        assertThat(results).containsExactly("John");

        kieSession.dispose();
    }
}
