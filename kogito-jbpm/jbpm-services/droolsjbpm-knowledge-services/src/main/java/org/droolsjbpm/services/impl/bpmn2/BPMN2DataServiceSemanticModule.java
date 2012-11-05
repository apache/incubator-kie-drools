/*
 * Copyright 2011 JBoss Inc 
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
package org.droolsjbpm.services.impl.bpmn2;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;

public class BPMN2DataServiceSemanticModule extends BPMNSemanticModule {

    
    private ProcessDescRepoHelper repo;
    
    @Inject 
    private HumanTaskGetInformationHandler taskHandler;
    @Inject 
    private ProcessGetInformationHandler processHandler;
    @Inject 
    private ProcessGetInputHandler processInputHandler;
    
    public BPMN2DataServiceSemanticModule() {
        super();
        
    }
    
    @PostConstruct
    public void init(){

        addHandler("userTask", taskHandler);
        addHandler("process", processHandler);
        addHandler("property", processInputHandler);
    }

    public void setRepo(ProcessDescRepoHelper repo) {
        this.repo = repo;
        taskHandler.setRepo(repo);
        processHandler.setRepo(repo);
        processInputHandler.setRepo(repo);
    }
    
    
    
}
