/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;

import org.drools.lang.descr.CollectDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 *
 */
public class CollectHandler  extends BaseAbstractHandler implements Handler  {
    
    CollectHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();

//            this.validParents.add( LiteralRestrictionHandler.class );

            this.validPeers = new HashSet();
//            this.validPeers.add( null );
//            this.validPeers.add( AndDescr.class );

            this.allowNesting = true;
        }
    }
    

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#end(java.lang.String, java.lang.String)
     */
    public Object end(String uri,
                      String localName) throws SAXException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#generateNodeFor()
     */
    public Class generateNodeFor() {
        return CollectDescr.class;
    }

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#start(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {
        // TODO Auto-generated method stub
        return null;
    }

}
