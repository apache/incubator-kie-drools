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
package org.jbpm.kie.services.impl.bpmn2;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;

public class BPMN2DataServiceSemanticModule extends BPMNSemanticModule {

    @Inject 
    private HumanTaskGetInformationHandler taskHandler;
    @Inject 
    private ProcessGetInformationHandler processHandler;
    @Inject 
    private ProcessGetInputHandler processInputHandler;
    @Inject
    private GetReusableSubProcessesHandler reusableSubprocessHandler;
    @Inject
    private DataServiceItemDefinitionHandler itemDefinitionHandler;
    @Inject
    private AbstractTaskGetInformationHandler abstractTaskHandler;
    
    public BPMN2DataServiceSemanticModule() {
        super();
        
    }

    public void setTaskHandler(HumanTaskGetInformationHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    public void setProcessHandler(ProcessGetInformationHandler processHandler) {
        this.processHandler = processHandler;
    }

    public void setProcessInputHandler(ProcessGetInputHandler processInputHandler) {
        this.processInputHandler = processInputHandler;
    }

    public void setReusableSubprocessHandler(GetReusableSubProcessesHandler reusableSubprocessHandler) {
        this.reusableSubprocessHandler = reusableSubprocessHandler;
    }
    public void setItemDefinitionHandler(DataServiceItemDefinitionHandler itemDefinitionHandler) {
        this.itemDefinitionHandler = itemDefinitionHandler;
    }

    public void setAbstractTaskHandler(AbstractTaskGetInformationHandler abstractTaskHandler) {
        this.abstractTaskHandler = abstractTaskHandler;
    }
    
    @PostConstruct
    public void init(){
        ProcessDescRepoHelper repoHelper = new ProcessDescRepoHelper();
        taskHandler.setRepositoryHelper(repoHelper);
        processHandler.setRepositoryHelper(repoHelper);
        processInputHandler.setRepositoryHelper(repoHelper);
        reusableSubprocessHandler.setRepositoryHelper(repoHelper);
        itemDefinitionHandler.setRepositoryHelper(repoHelper);
        abstractTaskHandler.setRepositoryHelper(repoHelper); 
        
        addHandler("userTask", taskHandler);
        addHandler("process", processHandler);
        addHandler("property", processInputHandler);
        addHandler("itemDefinition", itemDefinitionHandler);
        addHandler("callActivity", reusableSubprocessHandler);
        addHandler("task", abstractTaskHandler);
    }    
    
}
