/**
 * Copyright 2010 JBoss Inc
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

package org.drools.xml.changeset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.drools.ChangeSet;
import org.drools.io.Resource;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RemoveHandler extends BaseAbstractHandler
    implements
    Handler {
    
    public RemoveHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add( ChangeSet.class );

            this.validPeers = new HashSet(2);
            this.validPeers.add( null );
            this.validPeers.add( Collection.class );

            this.allowNesting = true;
        }        
    }    
    
    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );      
        
        final ChangeSet changeSet = (ChangeSet) parser.getParent();          
        
        return new ArrayList();
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        
        final ChangeSetImpl changeSet = (ChangeSetImpl) parser.getParent();
        
        Collection<Resource> removedResources = new ArrayList<Resource>();
        Collection<String> removedDefinitions = new ArrayList<String>();

        for (Object object : ( Collection ) parser.getCurrent()) {
            if (object instanceof DefinitionHandler.DefinitionHandlerData){
                DefinitionHandler.DefinitionHandlerData data = (DefinitionHandler.DefinitionHandlerData)object;

                String fullName = data.getPackageName();

                if (fullName == null || fullName.equals("")){
                    fullName = data.getName();
                }else{
                    fullName += "."+data.getName();
                }

                removedDefinitions.add(fullName);

            }else if (object instanceof Resource){
                removedResources.add((Resource)object);
            }
        }

        changeSet.setResourcesRemoved( removedResources );
        changeSet.setKnowledgeDefinitionsRemoved( removedDefinitions );
        return ( Collection ) parser.getCurrent();
    }

    
    public Class< ? > generateNodeFor() {
        return Collection.class;
    }

}
