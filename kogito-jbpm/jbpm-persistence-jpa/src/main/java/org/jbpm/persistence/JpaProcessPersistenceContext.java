package org.jbpm.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.drools.persistence.jpa.JpaPersistenceContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public class JpaProcessPersistenceContext extends JpaPersistenceContext
    implements
    ProcessPersistenceContext {

    
    public JpaProcessPersistenceContext(EntityManager em) {
        super( em );
    }

    public void persist(ProcessInstanceInfo processInstanceInfo) {
        getEntityManager().persist( processInstanceInfo );
    }

    public ProcessInstanceInfo findProcessInstanceInfo(Long processId) {
        return getEntityManager().find( ProcessInstanceInfo.class, processId );
    }

    public void remove(ProcessInstanceInfo processInstanceInfo) {
        getEntityManager().remove( processInstanceInfo );
    }

    @SuppressWarnings("unchecked")
    public List<Long> getProcessInstancesWaitingForEvent(String type) {
        Query processInstancesForEvent = getEntityManager().createNamedQuery( "ProcessInstancesWaitingForEvent" );
        processInstancesForEvent.setFlushMode(FlushModeType.COMMIT);
        processInstancesForEvent.setParameter( "type",
                                               type );
        return (List<Long>) processInstancesForEvent.getResultList();
    }
    
}
