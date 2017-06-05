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

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class TreeTest extends CommonTestMethodBase {

    @Test
    public void testUnbalancedTrees() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_UnbalancedTrees.drl"));
        final KieSession wm = createKnowledgeSession(kbase);

        wm.insert(new Cheese("a", 10));
        wm.insert(new Cheese("b", 10));
        wm.insert(new Cheese("c", 10));
        wm.insert(new Cheese("d", 10));
        final Cheese e = new Cheese("e", 10);

        wm.insert(e);
        wm.fireAllRules();

        assertEquals("Rule should have fired twice, seting the price to 30", 30, e.getPrice());
    }

}
