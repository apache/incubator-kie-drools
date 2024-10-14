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

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class InTest extends BaseModelTest2 {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testInWithNullFire(RUN_TYPE runType) {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\", null))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(runType, str);
        ksession.insert(new Child("Ben", 10));
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testInWithNullNoFire(RUN_TYPE runType) {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\"))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(runType, str);
        ksession.insert(new Child("Ben", 10));
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testInWithNullComments(RUN_TYPE runType) {
        String str = "import " + Child.class.getCanonicalName() + "; \n" +
                "global java.util.List results; \n" +
                "global java.util.List results; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (" +
                "   \"Gustav\", // comment\n" +
                "   \"Alice\"\n" +
                "))\n" +
                "then                               \n" +
                " results.add($c);\n" +
                "end                                ";

        KieSession ksession = getKieSession(runType, str);
        List<Child> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        String parentName = "Gustav";
        Person gustav = new Person(parentName);
        Child ben = new Child("Ben", 10, parentName);
        ksession.insert(ben);
        ksession.insert(gustav);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(results).containsExactly(ben);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testInWithJoin(RUN_TYPE runType) {
        String str = "import " + Address.class.getCanonicalName() + "; \n" +
                "rule R when \n" +
                "    $a1: Address($street: street, city in (\"Brno\", \"Milan\", \"Bratislava\")) \n" +
                "    $a2: Address(city in (\"Krakow\", \"Paris\", $a1.city)) \n" +
                "then \n" +
                "end\n"; 

        KieSession ksession = getKieSession(runType, str);
        ksession.insert(new Address("Brno"));
        ksession.insert(new Address("Milan"));
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
