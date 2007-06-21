/**
 * 
 */
package org.drools.xml;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.PatternDescr;
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

            this.validParents.add( FromDescr.class );

            this.validPeers = new HashSet();
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }
    

    public Object start(String uri,
                        String localName,
                        Attributes attrs) throws SAXException {

        this.xmlPackageReader.startConfiguration( localName,
                                                  attrs );
        final CollectDescr collectDescr = new CollectDescr();
        return collectDescr;
    }

    public Object end(String uri,
                      String localName) throws SAXException {
        
        final Configuration config = this.xmlPackageReader.endConfiguration();

        final CollectDescr collectDescr = (CollectDescr) this.xmlPackageReader.getCurrent();

        final LinkedList parents = this.xmlPackageReader.getParents();
        final ListIterator ite = parents.listIterator( parents.size() );
        ite.previous();
        final Object parent = ite.previous();
        
        if ( parent.getClass().getName().equals( FromDescr.class.getName() ) ) {
            final PatternDescr source = (PatternDescr) ite.previous();
            collectDescr.setSourcePattern( source );
            
            AndDescr andDescr = (AndDescr) ite.previous();
            andDescr.addDescr( collectDescr );
            
        } else if ( parent instanceof ConditionalElementDescr ) {
            final ConditionalElementDescr parentDescr = (ConditionalElementDescr) parent;
            parentDescr.addDescr( collectDescr );
        } 

        return null;
    }

    public Class generateNodeFor() {
        return CollectDescr.class;
    }

}
