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

package org.jbpm.process.audit;

import static org.jbpm.query.jpa.data.QueryParameterIdentifiersUtil.*;
import static org.kie.internal.query.QueryParameterIdentifiers.CORRELATION_KEY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DURATION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.END_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXTERNAL_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.IDENTITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.LAST_VARIABLE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.OLD_VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.OUTCOME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_PARENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.START_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VAR_VALUE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VAR_VAL_SEPARATOR;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.SingularAttribute;

import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;

public class AuditQueryCriteriaUtil extends QueryCriteriaUtil {

    // Query Field Info -----------------------------------------------------------------------------------------------------------
    
    public final static Map<Class, Map<String, Attribute>> criteriaAttributes 
        = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() { 
        if( ProcessInstanceLog_.correlationKey == null ) { 
            // EMF/persistence has not been initialized: 
            // When a persistence unit (EntityManagerFactory) is initialized, 
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        }
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if( ! criteriaAttributes.isEmpty() ) { 
           return true; 
        }
        
        // ProcessInstanceLog
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, ProcessInstanceLog_.processInstanceId);
        addCriteria(criteriaAttributes, PROCESS_ID_LIST, ProcessInstanceLog_.processId);
        addCriteria(criteriaAttributes, START_DATE_LIST, ProcessInstanceLog_.start);
        addCriteria(criteriaAttributes, END_DATE_LIST, ProcessInstanceLog_.end);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_STATUS_LIST, ProcessInstanceLog_.status);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_PARENT_ID_LIST, ProcessInstanceLog_.parentProcessInstanceId);
        addCriteria(criteriaAttributes, OUTCOME_LIST, ProcessInstanceLog_.outcome);
        addCriteria(criteriaAttributes, DURATION_LIST, ProcessInstanceLog_.duration);
        addCriteria(criteriaAttributes, IDENTITY_LIST, ProcessInstanceLog_.identity);
        addCriteria(criteriaAttributes, PROCESS_VERSION_LIST, ProcessInstanceLog_.processVersion);
        addCriteria(criteriaAttributes, PROCESS_NAME_LIST, ProcessInstanceLog_.processName);
        addCriteria(criteriaAttributes, CORRELATION_KEY_LIST, ProcessInstanceLog_.correlationKey);
        addCriteria(criteriaAttributes, EXTERNAL_ID_LIST, ProcessInstanceLog_.externalId);
        
        // NodeInstanceLog
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, NodeInstanceLog_.processInstanceId);
        addCriteria(criteriaAttributes, PROCESS_ID_LIST, NodeInstanceLog_.processId);
        addCriteria(criteriaAttributes, EXTERNAL_ID_LIST, NodeInstanceLog_.externalId);
        addCriteria(criteriaAttributes, DATE_LIST, NodeInstanceLog_.date);
        
        addCriteria(criteriaAttributes, NODE_INSTANCE_ID_LIST, NodeInstanceLog_.nodeInstanceId);
        addCriteria(criteriaAttributes, NODE_ID_LIST, NodeInstanceLog_.nodeId);
        addCriteria(criteriaAttributes, NODE_NAME_LIST, NodeInstanceLog_.nodeName);
        addCriteria(criteriaAttributes, TYPE_LIST, NodeInstanceLog_.nodeType);
        addCriteria(criteriaAttributes, WORK_ITEM_ID_LIST, NodeInstanceLog_.workItemId);
        
        // Var
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, VariableInstanceLog_.processInstanceId);
        addCriteria(criteriaAttributes, PROCESS_ID_LIST, VariableInstanceLog_.processId);
        addCriteria(criteriaAttributes, DATE_LIST, VariableInstanceLog_.date);
        addCriteria(criteriaAttributes, EXTERNAL_ID_LIST, VariableInstanceLog_.externalId);
        
        addCriteria(criteriaAttributes, VARIABLE_INSTANCE_ID_LIST, VariableInstanceLog_.variableInstanceId);
        addCriteria(criteriaAttributes, VARIABLE_ID_LIST, VariableInstanceLog_.variableId);
        addCriteria(criteriaAttributes, VALUE_LIST, VariableInstanceLog_.value);
        addCriteria(criteriaAttributes, OLD_VALUE_LIST, VariableInstanceLog_.oldValue);
        
        return true;
    }
   
    // Implementation specific logic ----------------------------------------------------------------------------------------------
    
    protected JPAService jpaService;
    
    public AuditQueryCriteriaUtil(JPAService service) { 
        super(criteriaAttributes);
        this.jpaService = service;
    }
 
    /**
     * This protected constructor is used in the kie-remote-services module
     * 
     * @param criteriaAttributes
     * @param service
     */
    protected AuditQueryCriteriaUtil(Map<Class, Map<String, Attribute>> criteriaAttributes, JPAService service) { 
        super(criteriaAttributes);
        this.jpaService = service;
    }
    
    protected EntityManager getEntityManager() { 
        return this.jpaService.getEntityManager();
    }
  
    protected Object joinTransaction(EntityManager em) { 
        return this.jpaService.joinTransaction(em);
    }
   
    protected void closeEntityManager(EntityManager em, Object transaction) {
        this.jpaService.closeEntityManager(em, transaction);
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
    @SuppressWarnings("unchecked")
    protected <R,T> Predicate implSpecificCreatePredicateFromSingleCriteria( 
            CriteriaQuery<R> query, 
            CriteriaBuilder builder, 
            Class queryType,
            QueryCriteria criteria, 
            QueryWhere queryWhere) {
       
        Root<?> table = getRoot(query, queryType);
      
        return variableInstanceLogSpecificCreatePredicateFromSingleCriteria(query, builder, criteria, table);
    }
    
    @SuppressWarnings("unchecked")
    public static <Q,T> Predicate variableInstanceLogSpecificCreatePredicateFromSingleCriteria(
            CriteriaQuery<Q> query, 
            CriteriaBuilder builder, 
            QueryCriteria criteria, 
            Root<T> table) {
            
        Predicate predicate;
        if( LAST_VARIABLE_LIST.equals(criteria.getListId()) ) {
            Subquery<VariableInstanceLog> maxIdSubQuery = query.subquery(VariableInstanceLog.class);
            Root from = maxIdSubQuery.from(VariableInstanceLog.class);
            maxIdSubQuery.select(builder.max(from.get(VariableInstanceLog_.id)));
            maxIdSubQuery.groupBy(
                    from.get(VariableInstanceLog_.variableId), 
                    from.get(VariableInstanceLog_.processInstanceId));
            Attribute varIdField = VariableInstanceLog_.id;
            
            // TODO: add the current group's criteria list to the subquery, 
            // in order to make the subquery more efficient
            // -- but that requires making the criteria list available here as an argument.. :/
          
            Expression expr;
            if( varIdField instanceof SingularAttribute ) { 
                expr = table.get((SingularAttribute<T,?>)varIdField);
            } else { 
                throw new IllegalStateException("Unexpected " + varIdField.getClass().getName() + " when processing last variable query criteria!");
            }
            predicate = builder.in(expr).value(maxIdSubQuery);
        } else if( VAR_VALUE_ID_LIST.equals(criteria.getListId()) ) { 
            assert criteria.getValues().size() == 1 : "Only 1 var id/value parameter expected!";
            
            // extract var/val information from criteria
            Object varVal = criteria.getValues().get(0);
            String [] parts = ((String) varVal).split(VAR_VAL_SEPARATOR, 2);
            String varId = parts[1].substring(0,Integer.parseInt(parts[0]));
            String val = parts[1].substring(Integer.parseInt(parts[0])+1);
            
            // create predicates
            SingularAttribute varVarIdField = VariableInstanceLog_.variableId;
            Path varVarIdPath = table.get(varVarIdField);
            SingularAttribute varValField = VariableInstanceLog_.value;
            Path varValIdPath = table.get(varValField);
            
            Predicate varIdPredicate = builder.equal(varVarIdPath, varId);
            Predicate valPredicate;
            if( QueryCriteriaType.REGEXP.equals(criteria.getType()) ) { 
                val = convertRegexToJPALikeExpression(val);
                valPredicate = builder.like(varValIdPath, val);
            } else { 
                valPredicate = builder.equal(varValIdPath, val);
            }
            
            // intersect predicates
            predicate = builder.and(varIdPredicate, valPredicate);
        } else { 
            throw new IllegalStateException("List id [" + getQueryParameterIdNameMap().get(Integer.parseInt(criteria.getListId())) 
                   + "] is not supported for queries on " + table.getJavaType().getSimpleName() + ".");
        }
        return predicate;
    }

}
