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

import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Child;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

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
