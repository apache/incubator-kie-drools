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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FirstClass;
import org.drools.compiler.SecondClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;

public class BetaTest extends CommonTestMethodBase {

    @Test
    public void testDefaultBetaConstrains() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_DefaultBetaConstraint.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);
        final FirstClass first = new FirstClass("1", "2", "3", "4", "5");
        final FactHandle handle = ksession.insert(first);
        ksession.fireAllRules();
        assertEquals(1, results.size());
        assertEquals("NOT", results.get(0));

        ksession.insert(new SecondClass());
        ksession.update(handle, first);
        ksession.fireAllRules();
        assertEquals(2, results.size());
        assertEquals("NOT", results.get(1));

        ksession.update(handle, first);
        ksession.insert(new SecondClass(null, "2", "3", "4", "5"));
        ksession.fireAllRules();
        assertEquals(3, results.size());
        assertEquals("NOT", results.get(2));

        ksession.update(handle, first);
        ksession.insert(new SecondClass("1", null, "3", "4", "5"));
        ksession.fireAllRules();
        assertEquals(4, results.size());
        assertEquals("NOT", results.get(3));

        ksession.update(handle, first);
        ksession.insert(new SecondClass("1", "2", null, "4", "5"));
        ksession.fireAllRules();
        assertEquals(5, results.size());
        assertEquals("NOT", results.get(4));

        ksession.update(handle, first);
        ksession.insert(new SecondClass("1", "2", "3", null, "5"));
        ksession.fireAllRules();
        assertEquals(6, results.size());
        assertEquals("NOT", results.get(5));

        ksession.update(handle, first);
        ksession.insert(new SecondClass("1", "2", "3", "4", null));
        ksession.fireAllRules();
        assertEquals(7, results.size());
        assertEquals("NOT", results.get(6));

        ksession.insert(new SecondClass("1", "2", "3", "4", "5"));
        ksession.update(handle, first);
        ksession.fireAllRules();
        assertEquals(8, results.size());
        assertEquals("EQUALS", results.get(7));
    }

    @Test(timeout = 5000)
    public void testEfficientBetaNodeNetworkUpdate() {
        // [JBRULES-3372]
        final String str =
                "declare SimpleMembership\n" +
                        "    listId : String\n" +
                        "    patientId : String\n" +
                        "end\n" +
                        "declare SimplePatientFact\n" +
                        "    value : int\n" +
                        "    patientId : String\n" +
                        "end\n" +
                        "rule \"A\"\n" +
                        "when\n" +
                        "$slm : SimpleMembership($pid : patientId, listId == \"5072\" )\n" +
                        "and not (\n" +
                        "    (\n" +
                        "        (\n" +
                        "            SimplePatientFact(value == 1, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 2, patientId == $pid)\n" +
                        "        )\n" +
                        "    ) and (\n" +
                        "        (\n" +
                        "            SimplePatientFact(value == 6, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 7, patientId == $pid)\n" +
                        "        ) or (\n" +
                        "            SimplePatientFact(value == 8, patientId == $pid)\n" +
                        "        )\n" +
                        "    ) and (\n" +
                        "       (\n" +
                        "           SimplePatientFact(value == 9, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 10, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 11, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 12, patientId == $pid)\n" +
                        "       ) or (\n" +
                        "           SimplePatientFact(value == 13, patientId == $pid)\n" +
                        "       )\n" +
                        "   )\n" +
                        ")\n" +
                        "then\n" +
                        "   System.out.println(\"activated\");\n" +
                        "end";

        loadKnowledgeBaseFromString(str);
    }
}
