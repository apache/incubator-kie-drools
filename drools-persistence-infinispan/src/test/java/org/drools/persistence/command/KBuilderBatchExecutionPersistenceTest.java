package org.drools.persistence.command;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.cleanUp;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;

import java.util.HashMap;

import org.drools.compiler.command.KBuilderBatchExecutionTest;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.persistence.infinispan.InfinispanKnowledgeService;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class KBuilderBatchExecutionPersistenceTest extends KBuilderBatchExecutionTest {

    private HashMap<String, Object> context;
    
    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        if( context == null ) { 
            context = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        return InfinispanKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, createEnvironment(context));
    }  
    
}
