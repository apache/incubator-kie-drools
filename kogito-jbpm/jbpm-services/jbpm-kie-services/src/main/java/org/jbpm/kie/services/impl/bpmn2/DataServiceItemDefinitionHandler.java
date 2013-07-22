/*
 * Copyright 2013 JBoss by Red Hat.
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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.xml.ItemDefinitionHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class DataServiceItemDefinitionHandler extends ItemDefinitionHandler {

    private ProcessDescRepoHelper repositoryHelper;
    
    @Inject
    private ProcessDescriptionRepository repository;
    
    @Override
    public Object start(final String uri, final String localName,
            final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        ItemDefinition item = (ItemDefinition) super.start(uri, localName, attrs, parser);
        String id = item.getId();
        String structureRef = item.getStructureRef();
        String itemDefinitionId = repository.getGlobalItemDefinitions().get(id);
        if(itemDefinitionId == null){
            repository.getGlobalItemDefinitions().put(id, structureRef);
        }
        
        return item;

    }

    public void setRepositoryHelper(ProcessDescRepoHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
    }

    public void setRepository(ProcessDescriptionRepository repository) {
        this.repository = repository;
    }
    
    
    
    
}
