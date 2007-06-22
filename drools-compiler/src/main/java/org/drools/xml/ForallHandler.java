/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 *
 */
public class ForallHandler extends BaseAbstractHandler
    implements
    Handler {

    ForallHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AndDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );
            
            this.validPeers.add( AndDescr.class );
            this.validPeers.add( OrDescr.class );
            this.validPeers.add( NotDescr.class );
            this.validPeers.add( ExistsDescr.class );
            this.validPeers.add( EvalDescr.class );
            this.validPeers.add( PatternDescr.class );
            this.validPeers.add( ForallDescr.class );

            this.allowNesting = true;
        }
    }

    /* (non-Javadoc)
     * @see org.drools.xml.Handler#end(java.lang.String, java.lang.String)
     */
    public Object end(final String uri,
                      final String localName) throws SAXException {
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final ForallDescr forallDescr = (ForallDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        final Object parent = it.previous();

        final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
        parentDescr.addDescr( forallDescr );

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
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final ForallDescr forallDescr = new ForallDescr();

        return forallDescr;
    }

}
