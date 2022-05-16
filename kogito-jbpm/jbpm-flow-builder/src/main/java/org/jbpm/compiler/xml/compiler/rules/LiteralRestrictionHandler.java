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

import java.math.BigDecimal;

import org.drools.drl.ast.descr.ConnectiveDescr;
import org.drools.drl.ast.descr.LiteralRestrictionDescr;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LiteralRestrictionHandler extends BaseAbstractHandler
        implements
        Handler {
    public LiteralRestrictionHandler() {
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {
        parser.startElementBuilder(localName,
                attrs);

        String evaluator = attrs.getValue("evaluator");
        emptyAttributeCheck(localName, "evaluator", evaluator, parser);

        String text = attrs.getValue("value");

        if (!text.trim().equals("null")) {
            // find out if it's a valid integer or decimal, if not wrap in quotes
            try {
                new BigDecimal(text);
            } catch (NumberFormatException e) {
                text = "\"" + text.trim() + "\"";
            }
        }

        return evaluator.trim() + " " + text.trim();
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        ConnectiveDescr c = (ConnectiveDescr) parser.getParent();
        String s = (String) parser.getCurrent();

        c.add(s);
        return null;
    }

    public Class generateNodeFor() {
        return LiteralRestrictionDescr.class;
    }
}
