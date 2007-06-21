/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author fernandomeyer
 */

public class AccumulateHelperHandler extends BaseAbstractHandler
    implements
    Handler {

    AccumulateHelperHandler(final XmlPackageReader xmlPackageReader) {
        this.xmlPackageReader = xmlPackageReader;

        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( AccumulateDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.validPeers.add( PatternDescr.class );
            this.validPeers.add( BaseDescr.class );

            this.allowNesting = true;
        }
    }

    public Object end(final String uri,
                      final String localName) throws SAXException {

        final Configuration config = this.xmlPackageReader.endConfiguration();
        final BaseDescr baseDescr = (BaseDescr) this.xmlPackageReader.getCurrent();

        final String expression = config.getText();

        if ( expression == null || expression.trim().equals( "" ) ) {
            throw new SAXParseException( "<" + localName + "> must have some content",
                                         this.xmlPackageReader.getLocator() );
        }

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();

        final AccumulateDescr accumulate = (AccumulateDescr) parent;

        if ( localName.equals( "init" ) ) accumulate.setInitCode( expression.trim() );
        else if ( localName.equals( "action" ) ) accumulate.setActionCode( expression.trim() );
        else if ( localName.equals( "result" ) ) accumulate.setResultCode( expression.trim() );
        else if ( localName.equals( "reverse" ) ) accumulate.setReverseCode( expression.trim() );
        else {
            //TODO FM: support for external functions
        }

        return null;
    }

    public Class generateNodeFor() {
        return BaseDescr.class;
    }

    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );

        return new BaseDescr();
    }

}
