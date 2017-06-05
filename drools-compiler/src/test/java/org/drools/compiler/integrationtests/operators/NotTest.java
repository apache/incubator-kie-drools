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

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.facts.AFact;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class NotTest extends CommonTestMethodBase {

    @Test
    public void testLastMemoryEntryNotBug() {
        // JBRULES-2809
        // This occurs when a blocker is the last in the node's memory, or if there is only one fact in the node
        // And it gets no opportunity to rematch with itself

        String str = "";
        str += "package org.simple \n";
        str += "import " + AFact.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule x1 \n";
        str += "when \n";
        str += "    $s : String( this == 'x1' ) \n";
        str += "    not AFact( this != null ) \n";
        str += "then \n";
        str += "  list.add(\"fired x1\"); \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $s : String( this == 'x2' ) \n";
        str += "    not AFact( field1 == $s, this != null ) \n"; // this ensures an index bucket
        str += "then \n";
        str += "  list.add(\"fired x2\"); \n";
        str += "end  \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert("x1");
        ksession.insert("x2");
        final AFact a1 = new AFact("x1", null);
        final AFact a2 = new AFact("x2", null);

        final FactHandle fa1 = ksession.insert(a1);
        final FactHandle fa2 = ksession.insert(a2);

        // make sure the 'exists' is obeyed when fact is cycled causing add/remove node memory
        ksession.update(fa1, a1);
        ksession.update(fa2, a2);
        ksession.fireAllRules();

        assertEquals(0, list.size());

        ksession.dispose();
    }

}
