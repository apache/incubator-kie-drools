package org.jbpm.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.drools.persistence.jpa.JpaPersistenceContext;
import org.jbpm.persistence.correlation.CorrelationKeyInfo;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

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
        List<CorrelationKeyInfo> correlations = getEntityManager().createNamedQuery("GetCorrelationKeysByProcessInstanceId")
        .setParameter("pId", processInstanceInfo.getId()).getResultList();
        if (correlations != null) {
            for (CorrelationKeyInfo key : correlations) {
                getEntityManager().remove(key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Long> getProcessInstancesWaitingForEvent(String type) {
        Query processInstancesForEvent = getEntityManager().createNamedQuery( "ProcessInstancesWaitingForEvent" );
        processInstancesForEvent.setFlushMode(FlushModeType.COMMIT);
        processInstancesForEvent.setParameter( "type",
                                               type );
        return (List<Long>) processInstancesForEvent.getResultList();
    }

    @Override
    public void persist(CorrelationKeyInfo correlationKeyInfo) {
        Long processInstanceId = getProcessInstanceByCorrelationKey(correlationKeyInfo);
        if (processInstanceId != null) {
            throw new RuntimeException(correlationKeyInfo + " already exists");
        }
        getEntityManager().persist( correlationKeyInfo );
        
    }

    @Override
    public Long getProcessInstanceByCorrelationKey(CorrelationKey correlationKey) {
        Query processInstancesForEvent = getEntityManager().createNamedQuery( "GetProcessInstanceIdByCorrelation" );
        processInstancesForEvent.setFlushMode(FlushModeType.COMMIT);
        processInstancesForEvent.setParameter( "elem_count", correlationKey.getProperties().size() );
        List<Object> properties = new ArrayList<Object>();
        for (CorrelationProperty<?> property : correlationKey.getProperties()) {
            properties.add(property.getValue());
        }
        processInstancesForEvent.setParameter( "properties", properties );
        try {
            return (Long) processInstancesForEvent.getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }
    
}
