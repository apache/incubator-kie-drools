/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler.xml.rules;

import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternDestinationDescr;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PatternHandler extends BaseAbstractHandler
    implements
    Handler {
    public PatternHandler() {

    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        final String objectType = attrs.getValue( "object-type" );

        if ( objectType == null || objectType.trim().equals( "" ) ) {
            throw new SAXParseException( "<pattern> requires an 'object-type' attribute",
                                         parser.getLocator() );
        }

        PatternDescr patternDescr = null;

        final String identifier = attrs.getValue( "identifier" );
        if ( identifier == null || identifier.trim().equals( "" ) ) {
            patternDescr = new PatternDescr( objectType );
        } else {
            patternDescr = new PatternDescr( objectType,
                                             identifier );
        }

        return patternDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        final PatternDescr patternDescr = (PatternDescr) parser.getCurrent();

        final Object parent = parser.getParent();

        if ( parent instanceof PatternDestinationDescr ) {
            final PatternDestinationDescr parentDescr = (PatternDestinationDescr) parent;
            parentDescr.setInputPattern( patternDescr );
        } else {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( patternDescr );
        }
        return patternDescr;
    }

    public Class generateNodeFor() {
        return PatternDescr.class;
    }
}
