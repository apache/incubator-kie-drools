/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl.jpa;

import static org.kie.internal.query.QueryParameterIdentifiers.COMMAND_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_EXECUTIONS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_KEY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_OWNER_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_RETRIES_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MESSAGE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.STACK_TRACE_LIST;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;

import org.jbpm.executor.entities.ErrorInfo_;
import org.jbpm.executor.entities.RequestInfo_;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;

public class ExecutorQueryCriteriaUtil extends QueryCriteriaUtil {

    // Query Field Info -----------------------------------------------------------------------------------------------------------
    
    public final static Map<Class, Map<String, Attribute>> criteriaAttributes 
        = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() { 
        if( ErrorInfo_.id == null ) { 
            // EMF/persistence has not been initialized: 
            // When a persistence unit (EntityManagerFactory) is initialized, 
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        }
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if( ! criteriaAttributes.isEmpty() ) { 
           return true; 
        }
        
        // ErrorInfoImpl
        addCriteria(criteriaAttributes, MESSAGE_LIST, ErrorInfo_.message);
        addCriteria(criteriaAttributes, ID_LIST, ErrorInfo_.id);
        addCriteria(criteriaAttributes, STACK_TRACE_LIST, ErrorInfo_.stacktrace);
        addCriteria(criteriaAttributes, EXECUTOR_TIME_LIST, ErrorInfo_.time);
        
        // RequestInfo
        addCriteria(criteriaAttributes, COMMAND_NAME_LIST, RequestInfo_.commandName);
        addCriteria(criteriaAttributes, DEPLOYMENT_ID_LIST, RequestInfo_.deploymentId);
        addCriteria(criteriaAttributes, EXECUTOR_EXECUTIONS_LIST, RequestInfo_.executions);
        addCriteria(criteriaAttributes, ID_LIST, RequestInfo_.id);
        addCriteria(criteriaAttributes, EXECUTOR_KEY_LIST, RequestInfo_.key);
        addCriteria(criteriaAttributes, MESSAGE_LIST, RequestInfo_.message);
        addCriteria(criteriaAttributes, EXECUTOR_OWNER_LIST, RequestInfo_.owner);
        addCriteria(criteriaAttributes, EXECUTOR_RETRIES_LIST, RequestInfo_.retries);
        addCriteria(criteriaAttributes, EXECUTOR_STATUS_LIST, RequestInfo_.status);
        addCriteria(criteriaAttributes, EXECUTOR_TIME_LIST, RequestInfo_.time);
        
        return true;
    }
   
    // Implementation specific logic ----------------------------------------------------------------------------------------------
    
    private ExecutorJPAAuditService executorAuditService;
    
    public ExecutorQueryCriteriaUtil(ExecutorJPAAuditService service) { 
        super(criteriaAttributes);
        this.executorAuditService = service;
    }
  
    private EntityManager getEntityManager() { 
        return this.executorAuditService.getEntityManager();
    }
  
    private Object joinTransaction(EntityManager em) { 
        return this.executorAuditService.joinTransaction(em);
    }
   
    private void closeEntityManager(EntityManager em, Object transaction) {
        this.executorAuditService.closeEntityManager(em, transaction);
    }
  
    // Implementation specific methods --------------------------------------------------------------------------------------------
   
    protected CriteriaBuilder getCriteriaBuilder() { 
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    protected <T> List<T> createQueryAndCallApplyMetaCriteriaAndGetResult(QueryWhere queryWhere, CriteriaQuery<T> criteriaQuery, CriteriaBuilder builder) { 
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(criteriaQuery);
    
        applyMetaCriteriaToQuery(query, queryWhere);
        
        // execute query
        List<T> result = query.getResultList();

        closeEntityManager(em, newTx);
        
        return result;
    }

    @Override
    protected <R,T> Predicate implSpecificCreatePredicateFromSingleCriteria( 
            CriteriaQuery<R> query, 
            CriteriaBuilder builder, 
            Class queryType,
            QueryCriteria criteria, 
            QueryWhere queryWhere) {
        throw new IllegalStateException("List id " + criteria.getListId() + " is not supported for queries on " + queryType.getSimpleName() + ".");
    }

}
