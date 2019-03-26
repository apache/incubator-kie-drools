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

package org.jbpm.services.task.audit.service;

import static org.kie.internal.query.QueryParameterIdentifiers.ACTUAL_OWNER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_BY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.CREATED_ON_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DEPLOYMENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DURATION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.END_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MESSAGE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.START_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ACTIVATION_TIME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DESCRIPTION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_DUE_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PARENT_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PRIORITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_PROCESS_SESSION_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_NAME_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_VALUE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.USER_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;

import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.services.task.audit.impl.model.AuditTaskImpl_;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl_;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl_;
import org.jbpm.services.task.audit.impl.model.TaskVariableImpl_;
import org.jbpm.services.task.persistence.AbstractTaskQueryCriteriaUtil;
import org.kie.internal.task.api.TaskPersistenceContext;

public class TaskAuditQueryCriteriaUtil extends AbstractTaskQueryCriteriaUtil {

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

        // TaskVariableImpl
        addCriteria(criteriaAttributes, ID_LIST, TaskVariableImpl_.id);
        addCriteria(criteriaAttributes, TASK_ID_LIST, TaskVariableImpl_.taskId);
        addCriteria(criteriaAttributes, PROCESS_INSTANCE_ID_LIST, TaskVariableImpl_.processInstanceId);
        addCriteria(criteriaAttributes, PROCESS_ID_LIST, TaskVariableImpl_.processId);
        addCriteria(criteriaAttributes, TASK_VARIABLE_NAME_ID_LIST, TaskVariableImpl_.name);
        addCriteria(criteriaAttributes, TASK_VARIABLE_VALUE_ID_LIST, TaskVariableImpl_.value);
        addCriteria(criteriaAttributes, DATE_LIST, TaskVariableImpl_.modificationDate);
        addCriteria(criteriaAttributes, TYPE_LIST, TaskVariableImpl_.type);

        return true;
    }

    // Implementation specific logic ----------------------------------------------------------------------------------------------

    private final TaskJPAAuditService taskAuditService;

    public TaskAuditQueryCriteriaUtil(TaskJPAAuditService service) {
        super(null);
        initialize(criteriaAttributes);
        this.taskAuditService = service;
    }

    public TaskAuditQueryCriteriaUtil(TaskPersistenceContext context) {
        super(context);
        initialize(criteriaAttributes);
        this.taskAuditService = null;
    }

    @Override
    protected EntityManager getEntityManager() {
        if( this.persistenceContext == null ) {
            return this.taskAuditService.getEntityManager();
        } else  {
            return super.getEntityManager();
        }
    }

    @Override
    protected Object joinTransaction(EntityManager em ) {
        if( this.persistenceContext == null ) {
            return this.taskAuditService.joinTransaction(em);
        } else {
             super.joinTransaction(em);
             return true;
        }
    }

    @Override
    protected void closeEntityManager(EntityManager em, Object transaction) {
        if( this.persistenceContext == null ) {
            this.taskAuditService.closeEntityManager(em, transaction);
        }
        // em closed outside of this class when used within HT
    }

    // Implementation specific methods --------------------------------------------------------------------------------------------

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
