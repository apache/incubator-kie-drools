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
    
    public void signalEvent(String type, Object event) {
        ProcessPersistenceContextManager contextManager 
            = (ProcessPersistenceContextManager) getKnowledgeRuntime().getEnvironment().get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER );
        ProcessPersistenceContext context = contextManager.getProcessPersistenceContext();
        List<Long> processInstancesToSignalList = context.getProcessInstancesWaitingForEvent(type);
        for ( long id : processInstancesToSignalList ) {
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

}
