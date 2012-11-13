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
package org.droolsjbpm.services.impl.helpers;

import org.kie.runtime.process.ProcessInstance;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;

/**
 *
 * @author salaboy
 */
public class ProcessInstanceDescFactory {
    public static ProcessInstanceDesc newProcessInstanceDesc(String domainName, int sessionId, ProcessInstance processInstance){
       return new ProcessInstanceDesc(processInstance.getId(),
                                      processInstance.getProcessId(),
                                      processInstance.getProcessName(), 
                                      processInstance.getState(), 
                                      processInstance.getEventTypes() , 
                                      processInstance.getProcess().getId(), 
                                      domainName, sessionId);
    }
}
