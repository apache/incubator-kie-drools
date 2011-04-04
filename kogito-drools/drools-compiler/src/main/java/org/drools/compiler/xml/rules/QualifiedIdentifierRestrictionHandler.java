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

package org.drools.compiler.xml.rules;

import java.util.HashSet;

import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class QualifiedIdentifierRestrictionHandler extends BaseAbstractHandler
    implements
    Handler {
    public QualifiedIdentifierRestrictionHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        String evaluator = attrs.getValue( "evaluator" );
        emptyAttributeCheck( localName, "evaluator", evaluator, parser );
        boolean isNegated = evaluator.startsWith( "not " );
        if( isNegated ) {
            evaluator = evaluator.substring( 4 );
        }

        final QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestricionDescr = new QualifiedIdentifierRestrictionDescr( evaluator,
                                                                                                                                isNegated,
                                                                                                                                null,
                                                                                                                                null );

        return qualifiedIdentifierRestricionDescr;
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestricionDescr = (QualifiedIdentifierRestrictionDescr) parser.getCurrent();

        final String expression =((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText();

        emptyContentCheck( localName, expression, parser );

        qualifiedIdentifierRestricionDescr.setText( expression );
        
        final Object parent = parser.getParent();

        if ( parent instanceof FieldConstraintDescr ) {
            final FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) parent;
            fieldConstraintDescr.addRestriction( qualifiedIdentifierRestricionDescr );
        } else if ( parent instanceof RestrictionConnectiveDescr ) {
            final RestrictionConnectiveDescr restrictionConDescr = (RestrictionConnectiveDescr) parent;
            restrictionConDescr.addRestriction( qualifiedIdentifierRestricionDescr );
        }
        return qualifiedIdentifierRestricionDescr;
    }

    public Class generateNodeFor() {
        return QualifiedIdentifierRestrictionDescr.class;
    }
}
