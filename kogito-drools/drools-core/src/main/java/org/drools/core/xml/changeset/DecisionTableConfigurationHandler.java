/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.xml.changeset;

import java.util.HashSet;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.util.StringUtils;
import org.drools.core.io.internal.InternalResource;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DecisionTableConfigurationHandler extends BaseAbstractHandler
    implements
    Handler {

    public DecisionTableConfigurationHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet( 1 );
            this.validParents.add( Resource.class );

            this.validPeers = new HashSet( 1 );
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        String type = attrs.getValue( "input-type" );
        String worksheetName = attrs.getValue( "worksheet-name" );

        emptyAttributeCheck( localName,
                             "input-type",
                             type,
                             parser );

        DecisionTableConfiguration dtConf = new DecisionTableConfigurationImpl();
        dtConf.setInputType( DecisionTableInputType.valueOf( type ) );
        if ( !StringUtils.isEmpty( worksheetName ) ) {
            dtConf.setWorksheetName( worksheetName );
        }
        
        return dtConf;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        final InternalResource resource = (InternalResource) parser.getParent();
        ResourceConfiguration dtConf = (ResourceConfiguration) parser.getCurrent();
        resource.setConfiguration( dtConf );
        
        return dtConf;
    }

    public Class< ? > generateNodeFor() {
        return ResourceConfiguration.class;
    }

}
