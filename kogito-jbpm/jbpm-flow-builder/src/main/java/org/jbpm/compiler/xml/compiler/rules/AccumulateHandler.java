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

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class AccumulateHandler extends BaseAbstractHandler
        implements
        Handler {

    public AccumulateHandler() {
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {

        parser.startElementBuilder(localName, attrs);
        return new AccumulateDescr();
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {

        parser.endElementBuilder();
        final AccumulateDescr accumulateDescr = (AccumulateDescr) parser.getCurrent();

        final Object parent = parser.getParent();

        if (parent.getClass().getName().equals(FromDescr.class.getName())) {
            final PatternDescr result = (PatternDescr) parser.getParent(1);
            result.setSource(accumulateDescr);

        } else if (parent instanceof ConditionalElementDescr) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr(accumulateDescr);
        }

        return accumulateDescr;
    }

    public Class generateNodeFor() {
        return AccumulateDescr.class;
    }

}
