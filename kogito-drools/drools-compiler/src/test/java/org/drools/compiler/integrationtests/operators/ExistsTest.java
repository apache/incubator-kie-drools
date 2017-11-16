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
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.integrationtests.facts.AFact;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;

public class ExistsTest extends CommonTestMethodBase {

    @Test
    public void testExistsIterativeModifyBug() {
        // JBRULES-2809
        // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
        // However before this was done it would attempt to find the next tuple, starting from itself
        // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
        // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

        String str = "";
        str += "package org.simple \n";
        str += "import " + AFact.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "when \n";
        str += "  $f1 : AFact() \n";
        str += "    exists AFact(this != $f1, eval(field2 == $f1.getField2())) \n";
        str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
        str += "then \n";
        str += "  list.add($f1); \n";
        str += "end  \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final AFact a1 = new AFact("2", "2");
        final AFact a2 = new AFact("1", "2");
        final AFact a3 = new AFact("1", "2");

        final FactHandle fa1 = ksession.insert(a1);
        final FactHandle fa2 = ksession.insert(a2);
        final FactHandle fa3 = ksession.insert(a3);

        // a2, a3 are blocked by a1
        // modify a1, so that a1,a3 are now blocked by a2
        a1.setField2("1"); // Do
        ksession.update(fa1, a1);
        a1.setField2("2"); // Undo
        ksession.update(fa1, a1);

        // modify a2, so that a1,a2 are now blocked by a3
        a2.setField2("1"); // Do
        ksession.update(fa2, a2);
        a2.setField2("2"); // Undo
        ksession.update(fa2, a2);

        // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
        ksession.update(fa3, a3);

        a3.setField2("1"); // Do
        ksession.update(fa3, a3);
        ksession.fireAllRules();
        assertEquals(1, list.size()); // a2 should still be blocked by a1, but bug from previous update hanging onto blocked

        ksession.dispose();
    }

    @Test
    public void testNodeSharingNotExists() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_nodeSharingNotExists.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("rule1", list.get(0));

        ksession.insert(new Cheese("stilton", 10));
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertEquals("rule2", list.get(1));
    }

    @Test
    public void testLastMemoryEntryExistsBug() {
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
        str += "    exists AFact( this != null ) \n";
        str += "then \n";
        str += "  list.add(\"fired x1\"); \n";
        str += "end  \n";
        str += "rule x2 \n";
        str += "when \n";
        str += "    $s : String( this == 'x2' ) \n";
        str += "    exists AFact( field1 == $s, this != null ) \n"; // this ensures an index bucket
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

        assertEquals(2, list.size());

        ksession.dispose();
    }
}
