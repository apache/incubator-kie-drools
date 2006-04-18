package org.drools.xml;

import java.util.HashSet;

import org.drools.lang.descr.PackageDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class PackageHandler extends BaseAbstractHandler implements Handler
{
    PackageHandler( PackageReader packageReader )
    {
        this.packageReader = packageReader;
        
        if ( (this.validParents == null) && (validPeers == null) )
        {
            this.validParents = new HashSet( );
            this.validParents.add( null );

            this.validPeers = new HashSet( );
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start( String uri, String localName, Attributes attrs ) throws SAXException
    {

        String ruleSetName = attrs.getValue( "name" );

        if ( ruleSetName == null || ruleSetName.trim( ).equals( "" ) )
        {
            throw new SAXParseException(
                    "<package> requires a 'name' attribute", packageReader.getLocator( ) );
        }

        PackageDescr packageDescr = new PackageDescr( ruleSetName.trim( ) );


        packageReader.setPackageDescr( packageDescr );
        return packageDescr;
    }

    public Object end( String uri, String localName ) throws SAXException
    {
        return null;
    }

    public Class generateNodeFor()
    {
        return PackageDescr.class;
    }
}