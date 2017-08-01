/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml;

import java.util.HashSet;

import org.kie.api.definition.process.Process;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.StartNode;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StoreHandler extends BaseAbstractHandler
    implements
    Handler {
    public StoreHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( Process.class );

            this.validPeers = new HashSet<Class<?>>();            
            this.validPeers.add( StartNode.class );
            this.validPeers.add( ActionNode.class );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        xmlPackageReader.startElementBuilder( localName,
                                                  attrs );
        
        WorkflowProcessImpl  process = ( WorkflowProcessImpl ) xmlPackageReader.getParent();
        
        ActionNode actionNode = new ActionNode();
        
        final String name = attrs.getValue( "name" );        
        emptyAttributeCheck( localName, "name", name, xmlPackageReader );        
        actionNode.setName( name );
        
        final String id = attrs.getValue( "id" );        
        emptyAttributeCheck( localName, "id", name, xmlPackageReader );        
        actionNode.setId( new Long(id) );
        
        process.addNode( actionNode );
        ((ProcessBuildData)xmlPackageReader.getData()).addNode( actionNode );
        
        return actionNode;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        final Element element = xmlPackageReader.endElementBuilder();

        ActionNode actionNode = ( ActionNode ) xmlPackageReader.getCurrent();
        
        String text = ((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText();
        
        DroolsConsequenceAction actionText = new DroolsConsequenceAction( "mvel", "list.add(\"" + text + "\")" );
        
        actionNode.setAction( actionText );
        
        return actionNode;
    }

    public Class<?> generateNodeFor() {
        return ActionNode.class;
    }    

}
