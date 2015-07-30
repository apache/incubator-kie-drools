/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.audit.WorkingMemoryFileLogger;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class WorkingMemoryLoggerTest extends CommonTestMethodBase {
    private static final String LOG = "session";

    @Test
    public void testOutOfMemory() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase( "empty.drl");

        for (int i = 0; i < 10000; i++) {
            StatefulKnowledgeSession session = createKnowledgeSession(kbase);
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
            session.fireAllRules();
            session.dispose();
        }
    }

}
