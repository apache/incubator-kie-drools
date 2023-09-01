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

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class AlphaNetworkInitTest extends BaseModelTest {

    public AlphaNetworkInitTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    // ANC is assembled manually
    final static Object[] STANDARD = {
            RUN_TYPE.STANDARD_FROM_DRL,
    };

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return STANDARD;
    }

    @Test // DROOLS-6336
    public void testGenerateAndSetInMemoryANC() {
        final List<Person> results = new ArrayList<>();
        KieSession kSession = setupKieSession();
        KieBaseUpdaterANC.generateAndSetInMemoryANC(kSession.getKieBase());
        assertResult(results, kSession);
    }

    @Test // DROOLS-6336
    public void testGenerateAndSetInMemoryANCCalledTwice() {
        final List<Person> results = new ArrayList<>();
        KieSession kSession = setupKieSession();
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

    private KieSession setupKieSession() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List results;\n" +
                        "rule \"Find James Bond\"\n" +
                        "    when\n" +
                        "        $p : Person(name == \"James Bond\")\n" +
                        "    then\n" +
                        "        results.add($p);\n" +
                        "end";

        KieSession ksession = getKieSession(str);
        return ksession;
    }
}
