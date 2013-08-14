package org.drools.persistence.command;

import static org.drools.persistence.util.PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.PersistenceUtil.cleanUp;
import static org.drools.persistence.util.PersistenceUtil.createEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.drools.compiler.command.MoreBatchExecutionTest;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@RunWith(Parameterized.class)
public class MoreBatchExecutionPersistenceTest extends MoreBatchExecutionTest {

    private HashMap<String, Object> context;
    private boolean locking;
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] { { false }, { true } };
        return Arrays.asList(locking);
    };
    
    public MoreBatchExecutionPersistenceTest(boolean locking) { 
        this.locking = true;
    }
    
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
        Environment env = createEnvironment(context);
        if( this.locking ) { 
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, env);    }  
    
}
