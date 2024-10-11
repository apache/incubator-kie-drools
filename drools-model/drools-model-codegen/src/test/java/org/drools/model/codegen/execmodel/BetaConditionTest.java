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

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

// DROOLS-5852
public class BetaConditionTest extends BaseModelTest2 {


    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckTwoConditionsExplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed == true || $p1.employed == true )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, drl, 2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckTwoConditionsImplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, drl, 2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckORExplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed == true || $p1.employed == true )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, drl, 3);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckORImplicit(RUN_TYPE runType) {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, str, 3);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckExplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "$p1 : Person()" +
                        "$p2 : Person($p1.employed == true)" +
                        "then\n" +
                        "   list.add($p2);" +
                        "end\n";

        verify(runType, drl, 2);
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void betaCheckImplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person($p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, drl, 2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void checkBooleanExplicit(RUN_TYPE runType) {
        final String str =
                "global java.util.List list\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "$p2 : Person(employed == true)" +
                        "then\n" +
                        "   list.add($p2);" +
                        "end\n";

        verify(runType, str, 1);
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void checkBooleanImplicit(RUN_TYPE runType) {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p2 : Person(employed) " +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(runType, drl, 1);
    }

    private void verify(RUN_TYPE runType,  String str, int numberOfResults) {
        KieSession ksession = getKieSession(runType, str);

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(numberOfResults);
        assertThat(rulesFired).isEqualTo(numberOfResults);

        assertThat(results.size()).isEqualTo(numberOfResults);
        assertThat(rulesFired).isEqualTo(numberOfResults);
    }

}
