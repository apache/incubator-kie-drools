package org.drools.xml;

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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author fmeyer
 */

class QualifiedIdentifierRestrictionHandler extends BaseAbstractHandler
    implements
    Handler {
    QualifiedIdentifierRestrictionHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( FieldConstraintDescr.class );
            this.validParents.add( RestrictionConnectiveDescr.class );

            this.validPeers = new HashSet();

            this.validPeers.add( null );
            this.validPeers.add( LiteralRestrictionDescr.class );
            this.validPeers.add( ReturnValueRestrictionDescr.class );
            this.validPeers.add( VariableRestrictionDescr.class );
            this.validPeers.add( RestrictionConnectiveDescr.class );
            this.validPeers.add( QualifiedIdentifierRestrictionDescr.class );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {
        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final String evaluator = attrs.getValue( "evaluator" );
        if ( evaluator == null || evaluator.trim().equals( "" ) ) {
            throw new SAXParseException( "<bound-variable> requires an 'evaluator' attribute",
                                         this.xmlPackageReader.getLocator() );
        }
        
//        qualifiedIdentifierRestricionDescr.set


        final QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestricionDescr = new QualifiedIdentifierRestrictionDescr( evaluator,
                                                                                                                                null );

        return qualifiedIdentifierRestricionDescr;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final QualifiedIdentifierRestrictionDescr qualifiedIdentifierRestricionDescr = 
            (QualifiedIdentifierRestrictionDescr) this.xmlPackageReader.getCurrent();
        
        
        final String expression = config.getText();

        if ( expression == null || expression.trim().equals( "" ) ) {
            throw new SAXParseException( "<qualified-identifier-restriction> must have an expression content",
                                         this.xmlPackageReader.getLocator() );
        }

        qualifiedIdentifierRestricionDescr.setText( expression );
        
        
        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();

        Object parent = it.previous();

        if ( parent instanceof FieldConstraintDescr ) {
            final FieldConstraintDescr fieldConstraintDescr = (FieldConstraintDescr) parent;
            fieldConstraintDescr.addRestriction( qualifiedIdentifierRestricionDescr );
        } else if ( parent instanceof RestrictionConnectiveDescr ) {
            final RestrictionConnectiveDescr restrictionConDescr = (RestrictionConnectiveDescr) parent;
            restrictionConDescr.addRestriction( qualifiedIdentifierRestricionDescr );
        }
        return null;
    }

    public Class generateNodeFor() {
        return QualifiedIdentifierRestrictionDescr.class;
    }
}