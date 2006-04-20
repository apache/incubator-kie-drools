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

import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class PredicateHandler extends BaseAbstractHandler
    implements
    Handler {
    PredicateHandler(XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( ColumnDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( LiteralDescr.class );
            this.validPeers.add( PredicateDescr.class );
            this.validPeers.add( ReturnValueDescr.class );
            this.validPeers.add( FieldBindingDescr.class );
            this.validPeers.add( BoundVariableDescr.class );            
            this.allowNesting = false;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        xmlPackageReader.startConfiguration( localName,
                                             attrs );
         
        String identifier = attrs.getValue( "identifier" );        
        if ( identifier == null || identifier.trim( ).equals( "" ) )
        {
            throw new SAXParseException(
                    "<predicate> requires an 'identifier' attribute", xmlPackageReader.getLocator( ) );
        }         
        
        String fieldName = attrs.getValue( "field-name" );        
        if ( fieldName == null || fieldName.trim( ).equals( "" ) )
        {
            throw new SAXParseException(
                    "<predicate> requires a 'field-name' attribute", xmlPackageReader.getLocator( ) );
        }               
        
        String expression = attrs.getValue( "expression" );        
        if ( expression == null || expression.trim( ).equals( "" ) )
        {
            throw new SAXParseException(
                    "<predicate> requires an expression", xmlPackageReader.getLocator( ) );
        }        
        
        PredicateDescr predicateDescr = new PredicateDescr( fieldName, identifier, expression );
        
        return predicateDescr;
    }

    public Object end(String uri,
                      String localName) throws SAXException {
        Configuration config = xmlPackageReader.endConfiguration();                

        PredicateDescr predicateDescr = ( PredicateDescr )  this.xmlPackageReader.getCurrent();
        
        LinkedList parents = this.xmlPackageReader.getParents();
        ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        ColumnDescr columnDescr = ( ColumnDescr ) it.previous();
        
        columnDescr.addDescr( predicateDescr );        
        
        return null;
    }

    public Class generateNodeFor() {
        return PredicateDescr.class;
    }
}