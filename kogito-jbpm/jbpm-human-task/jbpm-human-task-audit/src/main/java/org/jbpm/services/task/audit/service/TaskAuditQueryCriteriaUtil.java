package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

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

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog_;
import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl_;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl_;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl_;
import org.jbpm.services.task.events.TaskEventImpl;

public class TaskAuditQueryCriteriaUtil extends QueryCriteriaUtil {

    // Query Field Info -----------------------------------------------------------------------------------------------------------
    
    public final static Map<Class, Map<String, Attribute>> criteriaAttributes 
        = new ConcurrentHashMap<Class, Map<String, Attribute>>();

    @Override
    protected synchronized boolean initializeCriteriaAttributes() { 
        if( AuditTaskImpl_.taskId == null ) { 
            // EMF/persistence has not been initialized: 
            // When a persistence unit (EntityManagerFactory) is initialized, 
            // the fields of classes annotated with @StaticMetamodel are filled using reflection
            return false;
        }
        // do not do initialization twice (slow performance, otherwise it doesn't matter)
        if( ! criteriaAttributes.isEmpty() ) { 
           return true; 
        }
        
        // AuditTaskImpl
        addCriteria(criteriaAttributes, TASK_ID_LIST, AuditTaskImpl_.taskId);
        addCriteria(criteriaAttributes, PROCESS_ID_LIST, AuditTaskImpl_.processId);
        addCriteria(criteriaAttributes, TASK_STATUS_LIST, AuditTaskImpl_.status);
        addCriteria(criteriaAttributes, ACTUAL_OWNER_ID_LIST, AuditTaskImpl_.actualOwner);
        addCriteria(criteriaAttributes, DEPLOYMENT_ID_LIST, AuditTaskImpl_.deploymentId);
        addCriteria(criteriaAttributes, ID_LIST, AuditTaskImpl_.id);
        addCriteria(criteriaAttributes, CREATED_ON_LIST, AuditTaskImpl_.createdOn);
        addCriteria(criteriaAttributes, TASK_PARENT_ID_LIST, AuditTaskImpl_.parentId);
        addCriteria(criteriaAttributes, CREATED_BY_LIST, AuditTaskImpl_.createdBy);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, AuditTaskImpl_.processInstanceId);
        addCriteria(criteriaAttributes, TASK_ACTIVATION_TIME_LIST, AuditTaskImpl_.activationTime);
        addCriteria(criteriaAttributes, TASK_DESCRIPTION_LIST, AuditTaskImpl_.description);
        addCriteria(criteriaAttributes, TASK_PRIORITY_LIST, AuditTaskImpl_.priority);
        addCriteria(criteriaAttributes, TASK_NAME_LIST, AuditTaskImpl_.name);
        addCriteria(criteriaAttributes, TASK_PROCESS_SESSION_ID_LIST, AuditTaskImpl_.processSessionId);
        addCriteria(criteriaAttributes, TASK_DUE_DATE_LIST, AuditTaskImpl_.dueDate);
        addCriteria(criteriaAttributes, WORK_ITEM_ID_LIST, AuditTaskImpl_.workItemId);
        
        // BAMTaskSummaryImpl
        addCriteria(criteriaAttributes, TASK_ID_LIST, BAMTaskSummaryImpl_.taskId);
        addCriteria(criteriaAttributes, START_DATE_LIST, BAMTaskSummaryImpl_.startDate);
        addCriteria(criteriaAttributes, DURATION_LIST, BAMTaskSummaryImpl_.duration);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, BAMTaskSummaryImpl_.processInstanceId);
        addCriteria(criteriaAttributes, TASK_STATUS_LIST, BAMTaskSummaryImpl_.status);
        addCriteria(criteriaAttributes, USER_ID_LIST, BAMTaskSummaryImpl_.userId);
        addCriteria(criteriaAttributes, END_DATE_LIST, BAMTaskSummaryImpl_.endDate);
        addCriteria(criteriaAttributes, CREATED_ON_LIST, BAMTaskSummaryImpl_.createdDate);
        addCriteria(criteriaAttributes, TASK_NAME_LIST, BAMTaskSummaryImpl_.taskName);
        addCriteria(criteriaAttributes, ID_LIST, BAMTaskSummaryImpl_.pk);
        
        // TaskEventImpl
        addCriteria(criteriaAttributes, MESSAGE_LIST, TaskEventImpl_.message);
        addCriteria(criteriaAttributes, TASK_ID_LIST, TaskEventImpl_.taskId);
        addCriteria(criteriaAttributes, ID_LIST, TaskEventImpl_.id);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, TaskEventImpl_.processInstanceId);
        addCriteria(criteriaAttributes, DATE_LIST, TaskEventImpl_.logTime);
        addCriteria(criteriaAttributes, USER_ID_LIST, TaskEventImpl_.userId);
        addCriteria(criteriaAttributes, TYPE_LIST, TaskEventImpl_.type);
        addCriteria(criteriaAttributes, WORK_ITEM_ID_LIST, TaskEventImpl_.workItemId);
        
        return true;
    }
   
    // Implementation specific logic ----------------------------------------------------------------------------------------------
    
    private TaskJPAAuditService taskAuditService;
    
    public TaskAuditQueryCriteriaUtil(TaskJPAAuditService service) { 
        super(criteriaAttributes);
        this.taskAuditService = service;
    }
  
    private EntityManager getEntityManager() { 
        return this.taskAuditService.getEntityManager();
    }
  
    private Object joinTransaction(EntityManager em) { 
        return this.taskAuditService.joinTransaction(em);
    }
   
    private void closeEntityManager(EntityManager em, Object transaction) {
        this.taskAuditService.closeEntityManager(em, transaction);
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
