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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.drools.xml.ExtensibleXmlParser;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Transactional
public class ProcessGetInformationHandler extends ProcessHandler {

    @Inject
    private TaskServiceEntryPoint taskService;
    @Inject
    private EntityManager em;
    private ProcessDescRepoHelper repo;

    public ProcessGetInformationHandler() {
    }

    @Override
    public Object start(String uri, String localName, Attributes attrs,
            ExtensibleXmlParser parser) throws SAXException {
        final String processId = attrs.getValue("id");
        final String processName = attrs.getValue("name");
        final String packageName = attrs.getValue("http://www.jboss.org/drools", "packageName");
        final String processType = attrs.getValue("type");
        final String namespace = attrs.getValue("namespace");
        final String version = attrs.getValue("version");

        repo.setProcess(new ProcessDesc(processId, processName, version, packageName, processType, "", namespace, ""));

        return super.start(uri, localName, attrs, parser);
    }

    
    public void setRepo(ProcessDescRepoHelper repo) {
        this.repo = repo;
    }
}
