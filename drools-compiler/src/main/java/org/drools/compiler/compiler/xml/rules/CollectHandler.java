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

import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CollectHandler extends BaseAbstractHandler
    implements
    Handler {

    public CollectHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        final CollectDescr collectDescr = new CollectDescr();
        return collectDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {

        final Element element = parser.endElementBuilder();
        final CollectDescr collectDescr = (CollectDescr) parser.getCurrent();

        final Object parent = parser.getParent();

        if ( parent.getClass().getName().equals( FromDescr.class.getName() ) ) {
            final PatternDescr resultPattern = (PatternDescr) parser.getParent( 1 );
            resultPattern.setSource( collectDescr );
        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( collectDescr );
        }

        return collectDescr;
    }

    public Class generateNodeFor() {
        return CollectDescr.class;
    }

}
