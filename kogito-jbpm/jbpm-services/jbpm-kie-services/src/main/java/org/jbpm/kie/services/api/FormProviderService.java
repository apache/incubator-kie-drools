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
package org.jbpm.kie.services.api;

/**
 *
 * @author salaboy
 */
public interface FormProviderService {
    String getFormDisplayTask(long taskId);
    String getFormDisplayProcess(String deploymentId, String processId);
   // FormRepresentation getAssociatedForm(String bpmn2, String taskName);
  //  FormRepresentation createFormFromTask(Map<String, String> inputs, Map<String, String> outputs, TaskDef task);
}
