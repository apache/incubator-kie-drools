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

import java.util.HashSet;

import org.drools.io.impl.ChangeSetImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ChangeSetHandler extends BaseAbstractHandler
    implements
    Handler {
    
    public ChangeSetHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add(null );

            this.validPeers = new HashSet(1);
            this.validPeers.add( null );

            this.allowNesting = true;
        }        
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        ChangeSetImpl changeSet = new ChangeSetImpl();
        
        parser.setData( changeSet );
        
        return changeSet;
    }
    
    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final ChangeSetImpl changeSet = (ChangeSetImpl) parser.getCurrent();        
        return changeSet;
    }  
    
    public Class< ? > generateNodeFor() {
        return ChangeSetImpl.class;
    }    

}
