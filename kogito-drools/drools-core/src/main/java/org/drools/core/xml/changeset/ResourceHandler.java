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

import java.util.Collection;
import java.util.HashSet;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.KnowledgeResource;
import org.drools.core.io.impl.UrlResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
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
        
        String name = attrs.getValue( "name" );
        String description = attrs.getValue( "description" );
        String categories = attrs.getValue( "categories" );

        
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
            resource = new ClassPathResource( src.substring( src.indexOf( ':' ) + 1 ), parser.getClassLoader() );
        } else {
            resource = new UrlResource( src );
            ((UrlResource)resource).setBasicAuthentication(basicAuthentication);
            ((UrlResource)resource).setUsername(username);
            ((UrlResource)resource).setPassword(password);
        }
        
        resource.setResourceType( ResourceType.getResourceType( type ) );
        
        resource.setSourcePath(name);
        resource.setDescription(description);
        resource.setCategories(categories);
        
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
