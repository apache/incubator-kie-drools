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

import org.jbpm.process.core.TypeObject;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class TypeHandler extends BaseAbstractHandler
    implements
    Handler {
    public TypeHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add( TypeObject.class );

            this.validPeers = new HashSet<Class<?>>();         
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
        TypeObject typeable = (TypeObject) parser.getParent();
        String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        DataType dataType = null;
        
        name = name.replace("org.drools.core.process.core", "org.jbpm.process.core");
        try {
            dataType = (DataType) Class.forName(name).newInstance();
            // TODO make this pluggable so datatypes can read in other properties as well
            if (dataType instanceof ObjectDataType) {
                String className = attrs.getValue("className");
                if (className == null) {
                	className = "java.lang.Object";
                }
                ((ObjectDataType) dataType).setClassName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new SAXParseException(
                "Could not find datatype " + name, parser.getLocator());
        } catch (InstantiationException e) {
            throw new SAXParseException(
                "Could not instantiate datatype " + name, parser.getLocator());
        } catch (IllegalAccessException e) {
            throw new SAXParseException(
                "Could not access datatype " + name, parser.getLocator());
        }
        
        typeable.setType(dataType);
        return dataType;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }

    public Class<?> generateNodeFor() {
        return DataType.class;
    }    

}
