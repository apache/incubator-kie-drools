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
import org.drools.compiler.Message;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class KnowledgeContextTest extends CommonTestMethodBase {

    @Test
    public void testKnowledgeContextJava() {
        testKnowledgeContext("test_KnowledgeContextJava.drl");
    }

    @Test
    public void testKnowledgeContextMVEL() {
        testKnowledgeContext("test_KnowledgeContextMVEL.drl");
    }

    private void testKnowledgeContext(final String drlResourceName) {
        final KieBase kbase = loadKnowledgeBase(drlResourceName);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.insert(new Message());
        ksession.fireAllRules();
        assertEquals(1, list.size());
        assertEquals("Hello World", list.get(0));
    }
}
