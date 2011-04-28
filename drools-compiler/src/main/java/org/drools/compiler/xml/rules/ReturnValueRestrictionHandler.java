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

import java.math.BigDecimal;
import java.util.HashSet;

import org.drools.lang.descr.ConnectiveDescr;
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

/**
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ReturnValueRestrictionHandler extends BaseAbstractHandler
    implements
    Handler {
    public ReturnValueRestrictionHandler() {
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        String evaluator = attrs.getValue( "evaluator" );
        emptyAttributeCheck( localName, "evaluator", evaluator, parser );
        
        return evaluator.trim() + " ";
    }

    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {        
        final Element element = parser.endElementBuilder();
        final String expression =((org.w3c.dom.Text)element.getChildNodes().item( 0 )).getWholeText();
        emptyContentCheck( localName, expression, parser );
        
        ConnectiveDescr c = (ConnectiveDescr) parser.getParent();
        String s = ( (String) parser.getCurrent()) + "(" + expression + ")";

        c.add( s );
        return null;        
    }

    public Class generateNodeFor() {
        return ReturnValueRestrictionDescr.class;
    }
}
