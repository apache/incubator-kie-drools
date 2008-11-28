package org.drools.xml.composition;

import java.util.HashSet;

import org.drools.compiler.KnowledgeComposition;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CompositionHandler extends BaseAbstractHandler
    implements
    Handler {
    
    public CompositionHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add(null );

            this.validPeers = new HashSet(1);
            this.validPeers.add( null );

            this.allowNesting = true;
        }        
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
        KnowledgeComposition composition = new KnowledgeComposition();
        
        parser.setData( composition );
        
        return composition;
    }
    
    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();

        final KnowledgeComposition composition = (KnowledgeComposition) parser.getCurrent();        
        return composition;
    }  
    
    public Class< ? > generateNodeFor() {
        return KnowledgeComposition.class;
    }    

}
