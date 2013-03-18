package org.jbpm.runtime.manager.impl.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jbpm.runtime.manager.impl.jpa.ContextMappingInfo;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;
import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

@SuppressWarnings("rawtypes")
public class JPAMapper implements Mapper {
    
    private EntityManagerFactory emf;
    
    public JPAMapper(EntityManagerFactory emf) {
        this.emf = emf;
    }

    
    @Override
    public void saveMapping(Context context, Integer ksessionId) {
       EntityManager em = emf.createEntityManager();
       // handle transaction
       em.joinTransaction();
       em.persist(new ContextMappingInfo(resolveContext(context, em).getContextId().toString(), ksessionId));
       
       em.close();
    }

    @Override
    public Integer findMapping(Context context) {
        EntityManager em = emf.createEntityManager();
        // handle transaction
//        em.joinTransaction();
        ContextMappingInfo contextMapping = findContextByContextId(resolveContext(context, em), em);
        if (contextMapping != null) {
            return contextMapping.getKsessionId();
        }
        return null;
    }

    @Override
    public void removeMapping(Context context) {
        EntityManager em = emf.createEntityManager();
        // handle transaction
        em.joinTransaction();
        
        ContextMappingInfo contextMapping = findContextByContextId(resolveContext(context, em), em);
        if (contextMapping != null) {
            em.remove(contextMapping);
        }
        em.close();
    }
    
    protected Context resolveContext(Context orig, EntityManager em) {
        if (orig instanceof CorrelationKeyContext) {
            return getProcessInstanceByCorrelationKey((CorrelationKey)orig.getContextId(), em);
        }
        
        return orig;
    }
    
    protected ContextMappingInfo findContextByContextId(Context context, EntityManager em) {
        try {
            Query findQuery = em.createNamedQuery("FindContextMapingByContextId").setParameter("contextId", context.getContextId().toString());
            ContextMappingInfo contextMapping = (ContextMappingInfo) findQuery.getSingleResult();
            
            return contextMapping;
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return null;
        }
    }
    
    public Context getProcessInstanceByCorrelationKey(CorrelationKey correlationKey, EntityManager em) {
        Query processInstancesForEvent = em.createNamedQuery( "GetProcessInstanceIdByCorrelation" );
        
        processInstancesForEvent.setParameter( "elem_count", correlationKey.getProperties().size() );
        List<Object> properties = new ArrayList<Object>();
        for (CorrelationProperty<?> property : correlationKey.getProperties()) {
            properties.add(property.getValue());
        }
        processInstancesForEvent.setParameter( "properties", properties );
        try {
            return ProcessInstanceIdContext.get((Long) processInstancesForEvent.getSingleResult());
        } catch (NonUniqueResultException e) {
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

}
