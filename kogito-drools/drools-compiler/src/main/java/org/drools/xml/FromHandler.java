/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author fernandomeyer
 *
 */
public class FromHandler extends BaseAbstractHandler
    implements
    Handler {

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

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        final FromDescr fromDesctiptor = new FromDescr();
        return fromDesctiptor;
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {

        final Configuration config = this.xmlPackageReader.endConfiguration();

        final FromDescr fromDescr = (FromDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator it = parents.listIterator( parents.size() );
        it.previous();
        Object parent = it.previous();

        final PatternDescr patternDescr = (PatternDescr) parent;
        fromDescr.setPattern( patternDescr );
        parent = it.previous();

        final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;

        if ( (config.getChild( "accumulate" ) == null) && (config.getChild( "collect" ) == null) ) 
            ((ConditionalElementDescr) parent).addDescr( fromDescr );

        return null;
    }

    public Class generateNodeFor() {
        return FromDescr.class;
    }

}
