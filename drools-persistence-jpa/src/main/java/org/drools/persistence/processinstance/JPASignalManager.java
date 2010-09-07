package org.drools.persistence.processinstance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.process.instance.event.DefaultSignalManager;
import org.drools.runtime.EnvironmentName;

public class JPASignalManager extends DefaultSignalManager {

    public JPASignalManager(InternalKnowledgeRuntime kruntime) {
        super(kruntime);
    }
    
    public void signalEvent(String type,
                            Object event) {
        for ( long id : getProcessInstancesForEvent( type ) ) {
            getKnowledgeRuntime().getProcessInstance( id );
        }
        super.signalEvent( type,
                           event );
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProcessInstancesForEvent(String type) {
        EntityManager em = (EntityManager) getKnowledgeRuntime().getEnvironment().get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        
        Query processInstancesForEvent = em.createNamedQuery( "ProcessInstancesWaitingForEvent" );
        processInstancesForEvent.setFlushMode(FlushModeType.COMMIT);
        processInstancesForEvent.setParameter( "type",
                                               type );
        List<Long> list = (List<Long>) processInstancesForEvent.getResultList();
        return list;
    }

}