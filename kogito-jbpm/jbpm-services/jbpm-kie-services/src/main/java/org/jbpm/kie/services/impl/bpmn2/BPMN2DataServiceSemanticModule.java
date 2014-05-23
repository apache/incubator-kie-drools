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

import org.jbpm.bpmn2.xml.BPMNSemanticModule;

public class BPMN2DataServiceSemanticModule extends BPMNSemanticModule {
	
	private ProcessDescRepoHelper repoHelper = new ProcessDescRepoHelper();
	private ProcessDescriptionRepository repo = new ProcessDescriptionRepository();
    
    private HumanTaskGetInformationHandler taskHandler = new HumanTaskGetInformationHandler(repoHelper, repo);    
    private ProcessGetInformationHandler processHandler = new ProcessGetInformationHandler(repoHelper, repo);    
    private ProcessGetInputHandler processInputHandler = new ProcessGetInputHandler(repoHelper, repo);    
    private GetReusableSubProcessesHandler reusableSubprocessHandler = new GetReusableSubProcessesHandler(repoHelper, repo);    
    private DataServiceItemDefinitionHandler itemDefinitionHandler = new DataServiceItemDefinitionHandler(repoHelper, repo);    
    private AbstractTaskGetInformationHandler abstractTaskHandler = new AbstractTaskGetInformationHandler(repoHelper, repo);
    
    public BPMN2DataServiceSemanticModule() {
        super();
        init();
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
    
    public void init(){       
        
        addHandler("userTask", taskHandler);
        addHandler("process", processHandler);
        addHandler("property", processInputHandler);
        addHandler("itemDefinition", itemDefinitionHandler);
        addHandler("callActivity", reusableSubprocessHandler);
        addHandler("task", abstractTaskHandler);
    }

	public ProcessDescRepoHelper getRepoHelper() {
		return repoHelper;
	}

	public void setRepoHelper(ProcessDescRepoHelper repoHelper) {
		this.repoHelper = repoHelper;
	}

	public ProcessDescriptionRepository getRepo() {
		return repo;
	}

	public void setRepo(ProcessDescriptionRepository repo) {
		this.repo = repo;
	}    
    
}
