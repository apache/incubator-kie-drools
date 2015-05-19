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

import static org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This is a package-level class that is used to manage {@link ProcessDescRepoHelper} instances. 
 */
class ProcessDescriptionRepository {

    private Map<String, ProcessDescRepoHelper> processRepoHelperCache = new ConcurrentHashMap<String, ProcessDescRepoHelper>();
   
    public static ThreadLocal<ProcessDescRepoHelper> LOCAL_PROCESS_REPO_HELPER = new ThreadLocal<ProcessDescRepoHelper>() { 
        @Override
        protected ProcessDescRepoHelper initialValue() { 
            return new ProcessDescRepoHelper();
        }
    };

    
    public ProcessDescRepoHelper getProcessDesc(String processId) {
        return this.processRepoHelperCache.get(processId);
    }
    
    public void addProcessDescription(String processId, ProcessDescRepoHelper helper) {
        // attach the threadLocalHelper to dialect expression builders 
        // in order to retrieve information about classes used in scripts, etc.
        useDataServiceExpressionBuilders(helper);
        
        this.processRepoHelperCache.put(processId, helper);
    }
    
    public ProcessDescRepoHelper removeProcessDescription(String processId) {
        // reset dialects
        resetDialectExpressionBuilders();
      
        ProcessDescRepoHelper repoHelper =  this.processRepoHelperCache.remove(processId);
       
        // resolve unqualified class names
        repoHelper.resolveUnqualifiedClasses();
        
        return repoHelper;
    }
 
}
