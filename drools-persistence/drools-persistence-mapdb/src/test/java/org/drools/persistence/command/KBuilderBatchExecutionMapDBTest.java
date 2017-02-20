package org.drools.persistence.command;

import java.util.Map;

import org.drools.compiler.command.KBuilderBatchExecutionTest;
import org.drools.persistence.mapdb.util.MapDBPersistenceUtil;
import org.junit.After;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class KBuilderBatchExecutionMapDBTest extends KBuilderBatchExecutionTest {

    private Map<String, Object> context;

    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        MapDBPersistenceUtil.cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
        if( context == null ) { 
            context = MapDBPersistenceUtil.setupMapDB();
        }
        KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        Environment env = MapDBPersistenceUtil.createEnvironment(context);
        return (StatefulKnowledgeSession) KieServices.Factory.get().getStoreServices().newKieSession(kbase, ksconf, env);
    }  
    

}
