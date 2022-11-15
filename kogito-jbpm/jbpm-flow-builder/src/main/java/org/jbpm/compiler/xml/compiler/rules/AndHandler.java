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

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.MultiPatternDestinationDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AndHandler extends BaseAbstractHandler
        implements
        Handler {
    public AndHandler() {
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {
        parser.startElementBuilder(localName,
                attrs);
        return new AndDescr();
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {
        parser.endElementBuilder();

        final AndDescr andDescr = (AndDescr) parser.getCurrent();

        final Object parent = parser.getParent();

        if (!andDescr.getDescrs().isEmpty()) {
            if (parent instanceof RuleDescr || parent instanceof QueryDescr) {
                final RuleDescr ruleDescr = (RuleDescr) parent;
                ruleDescr.setLhs(andDescr);
            } else if (parent instanceof MultiPatternDestinationDescr) {
                final MultiPatternDestinationDescr mpDescr = (MultiPatternDestinationDescr) parent;
                mpDescr.setInput(andDescr);
            } else if (parent instanceof ConditionalElementDescr) {
                final ConditionalElementDescr ceDescr = (ConditionalElementDescr) parent;
                ceDescr.addDescr(andDescr);
            }
        }

        return andDescr;
    }

    public Class generateNodeFor() {
        return AndDescr.class;
    }
}
