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
import org.drools.core.xml.Handler;
import org.jbpm.bpmn2.xml.PropertyHandler;
import org.jbpm.process.core.context.variable.Variable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class ProcessGetInputHandler extends PropertyHandler implements Handler {

    private ProcessDescRepoHelper repositoryHelper;
    private ProcessDescriptionRepository repository;
    
    public ProcessGetInputHandler() {
            super();
            
    }
    
    public ProcessGetInputHandler(ProcessDescRepoHelper repoHelper, ProcessDescriptionRepository repo) {
		this.repository = repo;
		this.repositoryHelper = repoHelper;
	}

    @Override
    public Object start(final String uri, final String localName,
                    final Attributes attrs, final ExtensibleXmlParser parser)
                    throws SAXException {
        String mainProcessId = repositoryHelper.getProcess().getId();
        
        Object result = super.start(uri, localName, attrs, parser);
        if(result instanceof Variable){
            String metaData = (String)((Variable)result).getMetaData("ItemSubjectRef");
            if(metaData != null){
            String structureRef = repository.getGlobalItemDefinitions().get(metaData);
                if(structureRef != null){
                    repository.getProcessDesc(mainProcessId).getInputs().put(((Variable)result).getName(), structureRef);
                }else{
                    repository.getProcessDesc(mainProcessId).getInputs().put(((Variable)result).getName(), ((Variable)result).getType().getStringType());
                }
            }
        }
        
        return result;
    }

    public void setRepositoryHelper(ProcessDescRepoHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }
    
    
}
