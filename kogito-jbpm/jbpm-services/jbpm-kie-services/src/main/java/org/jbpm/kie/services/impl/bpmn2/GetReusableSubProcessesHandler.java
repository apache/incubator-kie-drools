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

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.CallActivityHandler;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class GetReusableSubProcessesHandler extends CallActivityHandler {

    
    private ProcessDescriptionRepository repository;
    
    private ProcessDescRepoHelper repositoryHelper;
    
    public GetReusableSubProcessesHandler(ProcessDescRepoHelper repoHelper, ProcessDescriptionRepository repo) {
		this.repository = repo;
		this.repositoryHelper = repoHelper;
	}
    
    @Override
    protected void handleNode(Node node, Element element, String uri,
            String localName, ExtensibleXmlParser parser) throws SAXException {
        super.handleNode(node, element, uri, localName, parser);
        String mainProcessId = repositoryHelper.getProcess().getId();
        SubProcessNode subProcess = (SubProcessNode) node;
        repository.getProcessDesc(mainProcessId).getReusableSubProcesses().add(subProcess.getProcessId());
    }

    
    public void setRepositoryHelper(ProcessDescRepoHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }
    
}
