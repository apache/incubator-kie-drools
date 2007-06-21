/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;

import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 *
 */
public class FromHandler  extends BaseAbstractHandler implements Handler  {
    
    FromHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( PatternDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            this.validPeers.add( FieldConstraintDescr.class );
            this.allowNesting = false;
            
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        
        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        final FromDescr fromDesctiptor = new FromDescr();

        return fromDesctiptor;
    }


    public Object end(String uri,
                      String localName) throws SAXException {
        return null;

    }

    public Class generateNodeFor() {
        return FromDescr.class;
    }


}
