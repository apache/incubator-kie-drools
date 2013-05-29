/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.runtime.manager.impl.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.jbpm.runtime.manager.impl.jpa.ContextMappingInfo;
import org.kie.api.runtime.manager.Context;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * Data base based mapper implementation backed by JPA to store
 * context to <code>KieSession</code> id mapping. It used <code>ContextMappingInfo</code>
 * entity for persistence.
 * 
 * @see ContextMappingInfo
 *
 */
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


    @Override
    public Object findContextId(Integer ksessionId) {
        EntityManager em = emf.createEntityManager();
        try {
            Query findQuery = em.createNamedQuery("FindContextMapingByKSessionId").setParameter("ksessionId", ksessionId);
            ContextMappingInfo contextMapping = (ContextMappingInfo) findQuery.getSingleResult();
            
            return contextMapping.getContextId();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

}
