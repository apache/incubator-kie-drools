/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.api.bpmn2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.task.TaskDef;

/**
 *
 * @author salaboy
 */
public interface BPMN2DataService {
    Map<String, String> getAssociatedEntities(String bpmn2Content);
    List<String> getAssociatedDomainObjects(String bpmn2Content);
    Map<String, String> getProcessData(String bpmn2Content);
    List<String> getAssociatedForms(String bpmn2Content);
    Collection<TaskDef> getAllTasksDef(String bpmn2Content);
    ProcessDesc getProcessDesc(String bpmn2Content);
    Map<String, String> getTaskInputMappings(String bpmn2Content, String taskName);
    Map<String, String> getTaskOutputMappings(String bpmn2Content, String taskName);
}
