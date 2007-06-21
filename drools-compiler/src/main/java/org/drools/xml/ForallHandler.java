/**
 * 
 */
package org.drools.xml;

import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.AndDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.HashSet;

/**
 * @author fernandomeyer
 *
 */
public class ForallHandler extends BaseAbstractHandler implements Handler {
    
    ForallHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            
            this.allowNesting = true;
        }
    }
    

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#end(java.lang.String, java.lang.String)
     */
    public Object end(String uri,
                      String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#generateNodeFor()
     */
    public Class generateNodeFor() {
        return ForallDescr.class;
    }

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#start(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        return null;
    }

}
