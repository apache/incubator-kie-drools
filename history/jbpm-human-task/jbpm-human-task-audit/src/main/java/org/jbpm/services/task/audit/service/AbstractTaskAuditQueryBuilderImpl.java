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

import org.jbpm.query.jpa.builder.impl.AbstractQueryBuilderImpl;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.jbpm.services.task.commands.TaskCommand;
import org.kie.internal.query.ParametrizedQuery;
import org.kie.internal.task.api.InternalTaskService;

import java.util.Date;
import java.util.List;

import static org.kie.internal.query.QueryParameterIdentifiers.*;

/**
 *
 *
 *
 * @param <Q> The type of the interface of the specific {@link AbstractQueryBuilderImpl} implementation
 * @param <R> The type of result
 */
public abstract class AbstractTaskAuditQueryBuilderImpl<Q, R> extends AbstractQueryBuilderImpl<Q> {

    private final TaskAuditQueryCriteriaUtil queryCriteriaUtil;
    private final InternalTaskService taskService;

    public AbstractTaskAuditQueryBuilderImpl(TaskJPAAuditService jpaAuditService) {
        this(jpaAuditService, null);
    }

    public AbstractTaskAuditQueryBuilderImpl(InternalTaskService taskService) {
        this(null, taskService);
    }

    private AbstractTaskAuditQueryBuilderImpl(TaskJPAAuditService jpaService, InternalTaskService taskService) {
        if( jpaService != null ) {
            this.queryCriteriaUtil = new TaskAuditQueryCriteriaUtil(jpaService);
            this.taskService = null;
        } else if( taskService != null ) {
            this.queryCriteriaUtil = null;
            this.taskService = taskService;
        } else {
            throw new IllegalStateException( "At least one of the " + this.getClass().getSimpleName() + " constructor arguments must be non-null!");
        }
    }

    // query builder result methods


    public Q processInstanceId( long... processInstanceId ) {
        addLongParameter(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceId);
        return (Q) this;
    }

    public Q processInstanceIdRange( Long processInstanceIdMin, Long processInstanceIdMax ) {
        addRangeParameters(PROCESS_INSTANCE_ID_LIST, "process instance id", processInstanceIdMin, processInstanceIdMax);
        return (Q) this;
    }

    public Q processId( String... processId ) {
        addObjectParameter(PROCESS_ID_LIST, "process id", processId);
        return (Q) this;
    }

    public Q date( Date... date ) {
        addObjectParameter(DATE_LIST, "date", date);
        return (Q) this;
    }

    public Q dateRangeStart( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range start", rangeStart, true);
        return (Q) this;
    }

    public Q dateRangeEnd( Date rangeStart ) {
        addRangeParameter(DATE_LIST, "date range end", rangeStart, false);
        return (Q) this;
    }


    protected abstract Class<R> getResultType();
    protected abstract Class getQueryType();

    protected abstract TaskCommand getCommand();

    public ParametrizedQuery<R> build() {
        return new ParametrizedQuery<R>() {
            private QueryWhere queryWhere = new QueryWhere(getQueryWhere());

            @Override
            public List<R> getResultList() {
                if(  queryCriteriaUtil != null ) {
                    List implResult = queryCriteriaUtil.doCriteriaQuery(queryWhere, getQueryType());
                    return QueryCriteriaUtil.convertListToInterfaceList(implResult, getResultType());
                } else {
                    return (List<R>) taskService.execute(getCommand());
                }
            }
        };
    }
}
