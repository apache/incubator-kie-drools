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

import static org.kie.internal.query.QueryParameterIdentifiers.TASK_VARIABLE_DATE_ID_LIST;

import java.util.Date;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.query.AbstractAuditDeleteBuilderImpl;
import org.jbpm.services.task.audit.impl.model.TaskEventImpl;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.task.query.TaskVariableDeleteBuilder;

public class TaskVariableDeleteBuilderImpl extends AbstractAuditDeleteBuilderImpl<TaskVariableDeleteBuilder> implements TaskVariableDeleteBuilder {

    private static String TASK_VARIABLE_IMPL_DELETE =
            "DELETE\n" + "FROM TaskVariableImpl l\n";

    public TaskVariableDeleteBuilderImpl(CommandExecutor cmdExecutor) {
        super(cmdExecutor);
        intersect();
    }

    public TaskVariableDeleteBuilderImpl(JPAAuditLogService jpaAuditService) {
        super(jpaAuditService);
        intersect();
    }

    @Override
    public TaskVariableDeleteBuilder date(Date... date) {
        if (checkIfNull(date)) {
            return this;
        }
        addObjectParameter(TASK_VARIABLE_DATE_ID_LIST, "created on date", ensureDateNotTimestamp(date));
        return this;
    }

    @Override
    public TaskVariableDeleteBuilder dateRangeStart(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        addRangeParameter(TASK_VARIABLE_DATE_ID_LIST, "created on date range start", ensureDateNotTimestamp(rangeStart)[0], true);
        return this;
    }

    @Override
    public TaskVariableDeleteBuilder dateRangeEnd(Date rangeStart) {
        if (checkIfNull(rangeStart)) {
            return this;
        }
        addRangeParameter(TASK_VARIABLE_DATE_ID_LIST, "created on date range end", ensureDateNotTimestamp(rangeStart)[0], false);
        return this;
    }

    @Override
    protected Class getQueryType() {
        return TaskEventImpl.class;
    }

    @Override
    protected String getQueryBase() {
        return TASK_VARIABLE_IMPL_DELETE;
    }

    @Override
    protected String getSubQuery() {
        return ONLY_COMPLETED_PROCESS_INSTANCES;
    }
}
