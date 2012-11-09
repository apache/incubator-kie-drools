package org.drools.command;

import static org.drools.persistence.util.PersistenceUtil.*;

import java.util.HashMap;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.util.PersistenceUtil;
import org.drools.persistence.util.RerunWithLocalTransactions;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Rule;

public class MoreBatchExecutionPersistenceTest extends MoreBatchExecutionTest {

    @Rule
    public RerunWithLocalTransactions rerunWithLocalTx = new RerunWithLocalTransactions();
 
    private HashMap<String, Object> context;
    
    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) { 
        if( context == null ) { 
            context = PersistenceUtil.setupWithPoolingDataSource(getPersistenceUnitName());
        }
        KnowledgeSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, createEnvironment(context));
    }  
    
    private String PERSISTENCE_UNIT = PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;

    public void setPersistenceUnitToDroolsLocal() {
        PERSISTENCE_UNIT = DROOLS_LOCAL_PERSISTENCE_UNIT_NAME;
    }

    public void setPersistenceUnitToDroolsJTA() {
        PERSISTENCE_UNIT = DROOLS_PERSISTENCE_UNIT_NAME;
    }

    public void setPersistenceUnitToJbpmLocal() {
        PERSISTENCE_UNIT = JBPM_LOCAL_PERSISTENCE_UNIT_NAME;
    }

    public void setPersistenceUnitToJbpmJTA() {
        PERSISTENCE_UNIT = JBPM_PERSISTENCE_UNIT_NAME;
    }

    public String getPersistenceUnitName() {
        return PERSISTENCE_UNIT;
    }
}
