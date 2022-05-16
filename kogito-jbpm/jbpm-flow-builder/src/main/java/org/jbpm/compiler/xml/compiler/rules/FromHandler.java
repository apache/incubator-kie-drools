/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.xml.compiler.rules;

import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FromHandler extends BaseAbstractHandler
        implements
        Handler {

    public FromHandler() {
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {
        parser.startElementBuilder(localName,
                attrs);
        final FromDescr fromDesctiptor = new FromDescr();
        return fromDesctiptor;
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final FromDescr fromDescr = (FromDescr) parser.getCurrent();

        Object parent = parser.getParent();

        final PatternDescr patternDescr = (PatternDescr) parent;

        final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parser.getParent(1);

        if (element.getElementsByTagName("expression").getLength() > 0) {
            patternDescr.setSource(fromDescr);
        }

        return fromDescr;
    }

    public Class generateNodeFor() {
        return FromDescr.class;
    }

}
