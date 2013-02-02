package org.drools.command;

import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;

import java.util.HashMap;

import static org.drools.persistence.util.PersistenceUtil.*;

public class MoreBatchExecutionPersistenceTest extends MoreBatchExecutionTest {

    private HashMap<String, Object> context;

    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        if (context == null) {
            context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, createEnvironment(context));
    }

}
