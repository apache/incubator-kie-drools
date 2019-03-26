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

package org.jbpm.compiler.xml.processes;

import java.util.HashSet;

import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler implements Handler {
    
    public ProcessHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( null );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        
        final String id = attrs.getValue( "id" );
        final String name = attrs.getValue( "name" );
        final String version = attrs.getValue( "version" );
        final String type = attrs.getValue( "type" );
        final String packageName = attrs.getValue( "package-name" );
        final String routerLayout = attrs.getValue( "routerLayout" );
        
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId( id );
        process.setName( name );
        process.setVersion( version );
        process.setType( type );
        process.setPackageName( packageName );
        if (routerLayout != null) {
            process.setMetaData("routerLayout", new Integer(routerLayout));
        }

        ((ProcessBuildData)parser.getData()).addProcess( process );
        
        return process;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();        
        return parser.getCurrent();
    }

    public Class generateNodeFor() {
        return org.kie.api.definition.process.Process.class;
    }    

}
