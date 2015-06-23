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
import org.jbpm.bpmn2.xml.ImportHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This handler adds classes imported (via the &gt;extensionElement&lt;) to the list
 * of referenced classes.
 */
public class ProcessGetImportHandler extends ImportHandler {

    private BPMN2DataServiceExtensionSemanticModule module;
    private ProcessDescriptionRepository repository;

    public ProcessGetImportHandler(BPMN2DataServiceExtensionSemanticModule module) {
        this.module = module;
        this.repository = module.getRepo();
    }

    public Object start( final String uri, final String localName, final Attributes attrs, final ExtensibleXmlParser parser )
            throws SAXException {
        // does checks
        super.start(uri, localName, attrs, parser);

        final String name = attrs.getValue("name");
        final String type = attrs.getValue("importType");
        final String location = attrs.getValue("location");
        final String namespace = attrs.getValue("namespace");

        if( type == null || location == null || namespace == null ) {
            String mainProcessId = module.getRepoHelper().getProcess().getId();
            ProcessDescRepoHelper repoHelper = repository.getProcessDesc(mainProcessId);
            if( name.contains(".") ) { 
                repoHelper.getReferencedClasses().add(name);
            } else { 
                repoHelper.getUnqualifiedClasses().add(name);
            }
        }

        return null;
    }

}
