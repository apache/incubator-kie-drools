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
package org.drools.ancompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class AlphaNetworkInitTest extends BaseModelTest {

    // ANC is assembled manually
    public static Stream<RUN_TYPE> parameters() {
        return Stream.of(RUN_TYPE.STANDARD_FROM_DRL);
    }

    // DROOLS-6336
    @ParameterizedTest(name = "{0}")
	@MethodSource("parameters")
    public void testGenerateAndSetInMemoryANC(RUN_TYPE testRunType) {
        final List<Person> results = new ArrayList<>();
        KieSession kSession = setupKieSession(testRunType);
        KieBaseUpdaterANC.generateAndSetInMemoryANC(kSession.getKieBase());
        assertResult(results, kSession);
    }

    // DROOLS-6336
    @ParameterizedTest(name = "{0}")
	@MethodSource("parameters")
    public void testGenerateAndSetInMemoryANCCalledTwice(RUN_TYPE testRunType) {
        final List<Person> results = new ArrayList<>();
        KieSession kSession = setupKieSession(testRunType);
        KieBaseUpdaterANC.generateAndSetInMemoryANC(kSession.getKieBase());
        KieBaseUpdaterANC.generateAndSetInMemoryANC(kSession.getKieBase());
        assertResult(results, kSession);
    }

    private void assertResult(List<Person> results, KieSession ksession) {
        ksession.setGlobal("results", results);
        final Person jamesBond = new Person("James Bond", 40);
        ksession.insert(jamesBond);
        ksession.fireAllRules();

        assertThat(results).containsExactly(jamesBond);
    }

    private KieSession setupKieSession(RUN_TYPE testRunType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List results;\n" +
                        "rule \"Find James Bond\"\n" +
                        "    when\n" +
                        "        $p : Person(name == \"James Bond\")\n" +
                        "    then\n" +
                        "        results.add($p);\n" +
                        "end";

        KieSession ksession = getKieSession(testRunType, str);
        return ksession;
    }
}
