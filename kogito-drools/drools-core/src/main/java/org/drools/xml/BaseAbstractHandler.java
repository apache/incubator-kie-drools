/*
 * Copyright 2005 JBoss Inc
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

package org.drools.xml;

import java.util.Set;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 */
public abstract class BaseAbstractHandler {
    protected Set<Class<?>>     validPeers;
    protected Set<Class<?>>     validParents;
    protected boolean allowNesting;

    public Set<Class<?>> getValidParents() {
        return this.validParents;
    }

    public Set<Class<?>> getValidPeers() {
        return this.validPeers;
    }

    public boolean allowNesting() {
        return this.allowNesting;
    }

    public void emptyAttributeCheck(final String element,
                                    final String attributeName,
                                    final String attribute,
                                    final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        if ( attribute == null || attribute.trim().equals( "" ) ) {
            throw new SAXParseException( "<" + element + "> requires a '" + attributeName + "' attribute",
                                         xmlPackageReader.getLocator() );
        }
    }

    public void emptyContentCheck(final String element,
                                  final String content,
                                  final ExtensibleXmlParser xmlPackageReader) throws SAXException {
        if ( content == null || content.trim().equals( "" ) ) {
            throw new SAXParseException( "<" + element + "> requires content",
                                         xmlPackageReader.getLocator() );
        }
    }
}
