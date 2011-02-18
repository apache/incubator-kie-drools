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

import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefinitionHandler extends BaseAbstractHandler
    implements
    Handler {


    public class DefinitionHandlerData {
        private String packageName;
        private String name;

        public DefinitionHandlerData(String packageName, String name) {
            this.packageName = packageName;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getPackageName() {
            return packageName;
        }

        
    }

    public DefinitionHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add( Collection.class );

            this.validPeers = new HashSet(2);
            this.validPeers.add( null );
            this.validPeers.add( DefinitionHandler.DefinitionHandlerData.class );

            this.allowNesting = true;
        }        
    }    
    
    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        
        
        String packageName = attrs.getValue( "package" );
        String name = attrs.getValue( "name" );
        
        emptyAttributeCheck( localName,
                             "package",
                             packageName,
                             parser );
        
        emptyAttributeCheck( localName,
                             "name",
                             name,
                             parser );
        DefinitionHandler.DefinitionHandlerData data = new DefinitionHandlerData(packageName, name);
        
        return data;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        
        final Collection collection = (Collection) parser.getParent();
        final DefinitionHandlerData data = ( DefinitionHandlerData ) parser.getCurrent();
        collection.add( data );
        return data;
    }

    
    public Class< ? > generateNodeFor() {
        return DefinitionHandler.DefinitionHandlerData.class;
    }

}
