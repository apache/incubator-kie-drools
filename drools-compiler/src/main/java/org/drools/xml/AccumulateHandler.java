/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 */
public class AccumulateHandler extends BaseAbstractHandler
    implements
    Handler {

    AccumulateHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();

            this.validParents.add( FromDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        final AccumulateDescr accumulateDesrc = new AccumulateDescr();
        return accumulateDesrc;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {

        final Configuration config = this.xmlPackageReader.endConfiguration();
        final AccumulateDescr accumulateDescr = (AccumulateDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();

        if ( parent.getClass().getName().equals( FromDescr.class.getName() ) ) {
            final PatternDescr result = (PatternDescr) ite.previous();
            accumulateDescr.setResultPattern( result );

            final AndDescr andDescr = (AndDescr) ite.previous();
            andDescr.addDescr( accumulateDescr );

        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( accumulateDescr );
        }

        return null;
    }

    public Class generateNodeFor() {
        return AccumulateDescr.class;
    }

}
