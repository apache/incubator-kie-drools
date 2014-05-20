/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.task.audit.service;

import java.util.List;
import org.jbpm.services.task.audit.impl.model.api.AuditTask;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.QueryFilter;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 * @author salaboy
 */
public interface TaskAuditService {
    void setTaskService(TaskService taskService);
    
    List<TaskEvent> getAllTaskEvents(long taskId, QueryFilter filter);
    
    List<AuditTask> getAllHistoryAuditTasks( QueryFilter filter);
    List<AuditTask> getAllHistoryAuditTasksByUser(String userId, QueryFilter filter);
    
    
}
