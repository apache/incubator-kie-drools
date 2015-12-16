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

import org.drools.compiler.lang.descr.ConnectiveDescr;
import org.drools.compiler.lang.descr.ConnectiveDescr.RestrictionConnectiveType;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FieldConstraintHandler extends BaseAbstractHandler
    implements
    Handler {
    public FieldConstraintHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        final String fieldName = attrs.getValue( "field-name" );
        emptyAttributeCheck( localName,
                             "field-name",
                             fieldName,
                             parser );
        final ConnectiveDescr connective = new ConnectiveDescr( RestrictionConnectiveType.AND );
        connective.setParen( false );

        connective.setPrefix( fieldName );

        return connective;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final ConnectiveDescr c = (ConnectiveDescr) parser.getCurrent();

        Object p = parser.getParent();
        if ( p instanceof PatternDescr ) {
            StringBuilder sb = new StringBuilder();
            c.buildExpression( sb );

            ExprConstraintDescr expr = new ExprConstraintDescr();
            expr.setExpression( sb.toString() );

            final PatternDescr patternDescr = (PatternDescr) parser.getParent();
            patternDescr.addConstraint( expr );

        } else if ( p instanceof ConnectiveDescr ) {
            ((ConnectiveDescr) p).add( c );
        }

        return c;
    }

    public Class generateNodeFor() {
        return ExprConstraintHandler.class;
    }
}
