/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import org.jbpm.bpmn2.xml.GlobalHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This handler adds classes used in global declarations 
 * to the list of referenced classes.
 */
public class ProcessGetGlobalHandler extends GlobalHandler {

    private BPMN2DataServiceExtensionSemanticModule module;
    private ProcessDescriptionRepository repository;
    
    public ProcessGetGlobalHandler(BPMN2DataServiceExtensionSemanticModule module) {
        this.module = module;
        this.repository = module.getRepo();
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        // does checks
        super.start(uri, localName, attrs, parser);
        
        final String type = attrs.getValue( "type" );
        
        String mainProcessId = module.getRepoHelper().getProcess().getId();
        ProcessDescRepoHelper repoHelper = repository.getProcessDesc(mainProcessId);
        if( type.contains(".") ) { 
            repoHelper.getReferencedClasses().add(type);
        } else { 
            repoHelper.getUnqualifiedClasses().add(type);
        }
        
        return null;
    }    
    
}
