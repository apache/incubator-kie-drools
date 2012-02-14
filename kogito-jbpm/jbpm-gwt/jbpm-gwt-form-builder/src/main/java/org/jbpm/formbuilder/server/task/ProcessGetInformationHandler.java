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
package org.jbpm.formbuilder.server.task;

import org.drools.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessGetInformationHandler extends ProcessHandler {

    private final TaskRepoHelper taskRepository;
    
    public ProcessGetInformationHandler(TaskRepoHelper taskRepository) {
            super();
            this.taskRepository = taskRepository;
    }

    @Override
    public Object start(String uri, String localName, Attributes attrs,
            ExtensibleXmlParser parser) throws SAXException {
        final String processId = attrs.getValue("id");
        final String processName = attrs.getValue("name");
        final String packageName = attrs.getValue("http://www.jboss.org/drools", "packageName");
        this.taskRepository.setDefaultProcessId(processId);
        this.taskRepository.setDefaultProcessName(processName);
        this.taskRepository.setDefaultPackageName(packageName);
        return super.start(uri, localName, attrs, parser);
    }

}
