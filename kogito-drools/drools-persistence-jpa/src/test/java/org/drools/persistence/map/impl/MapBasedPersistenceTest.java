package org.drools.persistence.map.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.map.EnvironmentBuilder;
import org.drools.persistence.map.KnowledgeSessionStorage;
import org.drools.persistence.map.KnowledgeSessionStorageEnvironmentBuilder;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;

public class MapBasedPersistenceTest extends MapPersistenceTest{
    
    private SimpleKnowledgeSessionStorage storage;
    
    @Before
    public void createStorage(){
        storage = new SimpleKnowledgeSessionStorage();
    }
    
    @Override
    protected StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        
        EnvironmentBuilder envBuilder = new KnowledgeSessionStorageEnvironmentBuilder( storage );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );

        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                null,
                                                                env );
    }
    
    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession,
                                                             KnowledgeBase kbase) {
        int sessionId = ksession.getId();
        ksession.dispose();
        EnvironmentBuilder envBuilder = new KnowledgeSessionStorageEnvironmentBuilder( storage );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env );
    }
    
    @Override
    protected long getSavedSessionsCount() {
        return storage.ksessions.size();
    }
    
    private static class SimpleKnowledgeSessionStorage
        implements
        KnowledgeSessionStorage {

        public Map<Integer, SessionInfo>  ksessions = new HashMap<Integer, SessionInfo>();
        public Map<Long, WorkItemInfo> workItems = new HashMap<Long, WorkItemInfo>();

        public SessionInfo findSessionInfo(Integer id) {
            return ksessions.get( id );
        }

        public void saveOrUpdate(SessionInfo storedObject) {
            ksessions.put( storedObject.getId(),
                           storedObject );
        }

        public void saveOrUpdate(WorkItemInfo workItemInfo) {
            workItems.put( workItemInfo.getId(),
                           workItemInfo );
        }

        public Long getNextWorkItemId() {
            return new Long( workItems.size() + 1 );
        }

        public WorkItemInfo findWorkItemInfo(Long id) {
            return workItems.get( id );
        }

        public void remove(WorkItemInfo workItemInfo) {
            workItems.remove( workItemInfo.getId() );
        }

        public Integer getNextStatefulKnowledgeSessionId() {
            return ksessions.size() + 1 ;
        }
    }
}
