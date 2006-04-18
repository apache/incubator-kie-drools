package org.drools.xml;

import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
interface Handler
{

    Object start( String uri, String localName, Attributes attrs ) throws SAXException;

    Object end( String uri, String localName ) throws SAXException;

    Set getValidParents();

    Set getValidPeers();

    boolean allowNesting();

    Class generateNodeFor();
}