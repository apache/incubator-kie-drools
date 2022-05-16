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
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.core.BaseAbstractHandler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class QueryHandler extends BaseAbstractHandler
        implements
        Handler {
    public QueryHandler() {
    }

    public Object start(final String uri,
            final String localName,
            final Attributes attrs,
            final Parser parser) throws SAXException {
        parser.startElementBuilder(localName,
                attrs);

        final String queryName = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", queryName, parser);

        final QueryDescr queryDescr = new QueryDescr(queryName.trim());

        return queryDescr;
    }

    public Object end(final String uri,
            final String localName,
            final Parser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final QueryDescr queryDescr = (QueryDescr) parser.getCurrent();

        final AndDescr lhs = queryDescr.getLhs();

        if (lhs == null || lhs.getDescrs().isEmpty()) {
            throw new SAXParseException("<query> requires a LHS",
                    parser.getLocator());
        }

        ((PackageDescr) parser.getData()).addRule(queryDescr);

        return queryDescr;
    }

    public Class generateNodeFor() {
        return QueryDescr.class;
    }
}
