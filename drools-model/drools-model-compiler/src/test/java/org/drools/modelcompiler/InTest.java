/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.util.ArrayList;
import java.util.List;

import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class InTest extends BaseModelTest {

    public InTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testInWithNullFire() {
        String str = "import org.drools.modelcompiler.domain.Child; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\", null))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Child("Ben", 10));
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testInWithNullNoFire() {
        String str = "import org.drools.modelcompiler.domain.Child; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (\"Gustav\", \"Alice\"))\n" +
                "then                               \n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Child("Ben", 10));
        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testInWithNullComments() {
        String str = "import org.drools.modelcompiler.domain.Child; \n" +                        "global java.util.List results; \n" +
                "global java.util.List results; \n" +
                "rule R when                        \n" +
                "  $c : Child(parent in (" +
                "   \"Gustav\", // comment\n" +
                "   \"Alice\"\n" +
                "))\n" +
                "then                               \n" +
                " results.add($c);\n" +
                "end                                ";

        KieSession ksession = getKieSession(str);
        List<Child> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        String parentName = "Gustav";
        Person gustav = new Person(parentName);
        Child ben = new Child("Ben", 10, parentName);
        ksession.insert(ben);
        ksession.insert(gustav);
        assertEquals( 1, ksession.fireAllRules());
        assertThat(results).containsExactly(ben);
    }

    @Test
    public void testInWithJoin() {
        String str = "import org.drools.modelcompiler.domain.Address; \n" +
                "rule R when \n" +
                "    $a1: Address($street: street, city in (\"Brno\", \"Milan\", \"Bratislava\")) \n" +
                "    $a2: Address(city in (\"Krakow\", \"Paris\", $a1.city)) \n" +
                "then \n" +
                "end\n";

        KieSession ksession = getKieSession(str);
        ksession.insert(new Address("Brno"));
        ksession.insert(new Address("Milan"));
        assertEquals( 2, ksession.fireAllRules() );
    }
}
