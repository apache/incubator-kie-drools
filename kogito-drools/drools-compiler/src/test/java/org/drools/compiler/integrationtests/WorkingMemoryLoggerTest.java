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
