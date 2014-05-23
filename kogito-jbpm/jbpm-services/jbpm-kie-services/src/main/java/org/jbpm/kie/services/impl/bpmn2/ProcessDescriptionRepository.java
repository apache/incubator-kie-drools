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

package org.jbpm.kie.services.impl.bpmn2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ProcessDescriptionRepository {

    private Map<String, ProcessDescRepoHelper> processRepoHelper = new ConcurrentHashMap<String, ProcessDescRepoHelper>();
    
    private Map<String, String> globalItemDefinitions = new ConcurrentHashMap<String, String>();
    
    public ProcessDescRepoHelper getProcessDesc(String processId) {
        return this.processRepoHelper.get(processId);
    }

    public Map<String, String> getGlobalItemDefinitions() {
        return globalItemDefinitions;
    }
    
    
    public void addProcessDescription(String processId, ProcessDescRepoHelper repoHelper) {
        this.processRepoHelper.put(processId, repoHelper);
    }
    
    public ProcessDescRepoHelper removeProcessDescription(String processId) {
        return this.processRepoHelper.remove(processId);
    }
    
}
