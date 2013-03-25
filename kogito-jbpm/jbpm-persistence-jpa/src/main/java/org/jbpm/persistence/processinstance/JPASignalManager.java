package org.jbpm.persistence.processinstance;

import java.util.List;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.process.instance.event.DefaultSignalManager;
import org.kie.api.runtime.EnvironmentName;

public class JPASignalManager extends DefaultSignalManager {

    public JPASignalManager(InternalKnowledgeRuntime kruntime) {
        super(kruntime);
    }
    
    public void signalEvent(String type,
                            Object event) {
        for ( long id : getProcessInstancesForEvent( type ) ) {
            try {
                getKnowledgeRuntime().getProcessInstance( id );
            } catch (IllegalStateException e) {
                // IllegalStateException can be thrown when using RuntimeManager
                // and invalid ksession was used for given context
            }
        }
        super.signalEvent( type,
                           event );
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProcessInstancesForEvent(String type) {
//        EntityManager em = (EntityManager) getKnowledgeRuntime().getEnvironment().get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
//        Query processInstancesForEvent = em.createNamedQuery( "ProcessInstancesWaitingForEvent" );
//        processInstancesForEvent.setFlushMode(FlushModeType.COMMIT);
//        processInstancesForEvent.setParameter( "type",
//                                               type );
//        List<Long> list = (List<Long>) processInstancesForEvent.getResultList();
//        return list;
        ProcessPersistenceContext context = ((ProcessPersistenceContextManager) getKnowledgeRuntime().getEnvironment().get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getProcessPersistenceContext();
        return context.getProcessInstancesWaitingForEvent(type);
    }

}
