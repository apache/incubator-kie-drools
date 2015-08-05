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

import org.drools.core.xml.ExtensibleXmlParser;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.xml.ItemDefinitionHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This handler collects information about item definitions, which are
 * basically BPMN2 elements that alias an external (java) type to an 
 * internal alias
 */
public class DataServiceItemDefinitionHandler extends ItemDefinitionHandler {

    private BPMN2DataServiceSemanticModule module;
    private ProcessDescriptionRepository repository;
    
    public DataServiceItemDefinitionHandler(BPMN2DataServiceSemanticModule module) {
		this.module = module;
        this.repository = module.getRepo();
	}

	@Override
    public Object start(final String uri, final String localName,
            final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        ItemDefinition item = (ItemDefinition) super.start(uri, localName, attrs, parser);
        String id = item.getId();
        String structureRef = item.getStructureRef();
        // NPE!
        String itemDefinitionId = module.getRepoHelper().getGlobalItemDefinitions().get(id);
        
        if(itemDefinitionId == null) {
        	module.getRepoHelper().getGlobalItemDefinitions().put(id, structureRef);
        
            // The process id isn't known yet, so we use the thread local process
        	ProcessDescRepoHelper repoHelper = ProcessDescriptionRepository.LOCAL_PROCESS_REPO_HELPER.get();
        	if( structureRef.contains(".") ) { 
        	    repoHelper.getReferencedClasses().add(structureRef);
        	} else { 
        	    repoHelper.getUnqualifiedClasses().add(structureRef);
        	}
        }
        
        return item;

    }

}
