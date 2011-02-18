/*
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

import java.util.Collection;
import java.util.HashSet;

import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.KnowledgeResource;
import org.drools.io.impl.UrlResource;
import org.drools.io.internal.InternalResource;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ResourceHandler extends BaseAbstractHandler
    implements
    Handler {
    
    public ResourceHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add( Collection.class );

            this.validPeers = new HashSet(2);
            this.validPeers.add( null );
            this.validPeers.add( Resource.class );

            this.allowNesting = true;
        }        
    }    
    
    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );      
        
        final Collection collection = (Collection) parser.getParent();   
        
        String src = attrs.getValue( "source" );
        String type = attrs.getValue( "type" );
        String basicAuthentication = attrs.getValue( "basicAuthentication" );
        String username = attrs.getValue( "username" );
        String password = attrs.getValue( "password" );

        
        emptyAttributeCheck( localName,
                             "source",
                             src,
                             parser );
        
        emptyAttributeCheck( localName,
                             "type",
                             type,
                             parser );        
        InternalResource resource = null;
        
        if ( src.trim().startsWith( "classpath:" ) ) {
            resource = new ClassPathResource( src.substring( src.indexOf( ':' ) + 1 ) );
        } else {
            resource = new UrlResource( src );
            ((UrlResource)resource).setBasicAuthentication(basicAuthentication);
            ((UrlResource)resource).setUsername(username);
            ((UrlResource)resource).setPassword(password);           
        }
        
        resource.setResourceType( ResourceType.getResourceType( type ) );
        
        return resource;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        
        final Collection collection = (Collection) parser.getParent();
        final Resource resource = ( Resource ) parser.getCurrent();
        collection.add( resource );
        return resource;
    }

    
    public Class< ? > generateNodeFor() {
        return KnowledgeResource.class;
    }

}
