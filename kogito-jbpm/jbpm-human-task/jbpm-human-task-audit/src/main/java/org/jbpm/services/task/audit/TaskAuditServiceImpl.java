/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.services.task.audit;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
@Transactional
public class TaskAuditServiceImpl implements TaskAuditService{
    
    @Inject 
    private JbpmServicesPersistenceManager pm;
    
    public List<TaskEvent> getAllTaskEvents(long taskId){
        return (List<TaskEvent>)pm.queryWithParametersInTransaction("getAllTasksEvents", pm.addParametersToMap("taskId", taskId));
    }
}
