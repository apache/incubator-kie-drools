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

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessGetInformationHandler extends ProcessHandler {

    
    private ProcessDescriptionRepository repository;
    
    private BPMN2DataServiceSemanticModule module;

    public ProcessGetInformationHandler() {
    }
    
    public ProcessGetInformationHandler(BPMN2DataServiceSemanticModule module) {
    	this.module = module;
    	this.repository = module.getRepo();
		
	}

    @Override
    public Object start(String uri, String localName, Attributes attrs,
            ExtensibleXmlParser parser) throws SAXException {
    	RuleFlowProcess process = (RuleFlowProcess) super.start(uri, localName, attrs, parser);
        
    	ProcessDescRepoHelper value = new ProcessDescRepoHelper(); 
        ProcessAssetDesc definition = new ProcessAssetDesc(process.getId(), process.getName(), process.getVersion()
                , process.getPackageName(), process.getType(), process.getKnowledgeType().name(), process.getNamespace(), "");
        
        value.setProcess(definition);
        repository.addProcessDescription(definition.getId(), value);
        
        module.getRepoHelper().setProcess(value.getProcess());
        
        return process;
    }


    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }

    
    @Override
    public Object end(String uri, String localName, ExtensibleXmlParser parser)
            throws SAXException {
    	module.getRepoHelper().clear();
        return super.end(uri, localName, parser);
    }
}
