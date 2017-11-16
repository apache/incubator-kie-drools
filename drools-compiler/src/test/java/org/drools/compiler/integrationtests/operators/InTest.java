/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.operators;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class InTest extends CommonTestMethodBase {

    @Test
    public void testInOperator() {
        final String str = "package org.drools.compiler\n" +
                "rule \"test in\"\n" +
                "when\n" +
                "    Person( $name : name in (\"bob\", \"mark\") )\n" +
                "then\n" +
                "    boolean test = $name != null;" +
                "end\n" +
                "rule \"test not in\"\n" +
                "when\n" +
                "    Person( $name : name not in (\"joe\", \"doe\") )\n" +
                "then\n" +
                "    boolean test = $name != null;" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final Person person = new Person("bob");
        ksession.insert(person);

        final int rules = ksession.fireAllRules();
        assertEquals(2, rules);
    }
}
