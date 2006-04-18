package org.drools.xml;

import java.util.HashSet;

import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author mproctor
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class ImportHandler extends BaseAbstractHandler implements Handler
{
    ImportHandler( PackageReader packageReader )
    {
        this.packageReader = packageReader;

        if ( (this.validParents == null) && (validPeers == null) )
        {
            this.validParents = new HashSet( );
            this.validParents.add( Package.class );

            this.validPeers = new HashSet( );
            this.validPeers.add( null );
            this.validPeers.add( ImportEntryDummy.class );
            this.validPeers.add( GlobalDummy.class );
            this.validPeers.add( FunctionDescr.class );
            this.validPeers.add( RuleDescr.class );            

            this.allowNesting = false;
        }
    }

    public Object start( String uri, String localName, Attributes attrs ) throws SAXException
    {
        packageReader.startConfiguration( localName, attrs );
        return null;
    }

    public Object end( String uri, String localName ) throws SAXException
    {
        Configuration config = packageReader.endConfiguration( );
        
        PackageDescr packageDescr = this.packageReader.getPackageDescr();
        
        packageDescr.addImport( config.getText() );
        
        return new ImportEntryDummy();
    }

    public Class generateNodeFor()
    {
        return ImportEntryDummy.class;
    }
}