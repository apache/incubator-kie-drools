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
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

// DROOLS-5852
public class BetaConditionTest extends BaseModelTest {

    public BetaConditionTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void betaCheckTwoConditionsExplicit() {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed == true || $p1.employed == true )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(drl, 2);
    }

    @Test
    public void betaCheckTwoConditionsImplicit() {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(drl, 2);
    }

    @Test
    public void betaCheckORExplicit() {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed == true || $p1.employed == true )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(drl, 3);
    }

    @Test
    public void betaCheckORImplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(str, 3);
    }

    @Test
    public void betaCheckExplicit() {
        final String drl =
                "global java.util.List list\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "$p1 : Person()" +
                        "$p2 : Person($p1.employed == true)" +
                        "then\n" +
                        "   list.add($p2);" +
                        "end\n";

        verify(drl, 2);
    }


    @Test
    public void betaCheckImplicit() {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person($p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(drl, 2);
    }

    @Test
    public void checkBooleanExplicit() {
        final String str =
                "global java.util.List list\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "$p2 : Person(employed == true)" +
                        "then\n" +
                        "   list.add($p2);" +
                        "end\n";

        verify(str, 1);
    }


    @Test
    public void checkBooleanImplicit() {
        final String drl =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p2 : Person(employed) " +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        verify(drl, 1);
    }

    private void verify(String str, int numberOfResults) {
        KieSession ksession = getKieSession(str);

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
