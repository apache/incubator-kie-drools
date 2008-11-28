package org.drools.xml.composition;

import java.net.URLClassLoader;
import java.util.HashSet;

import org.drools.builder.KnowledgeType;
import org.drools.compiler.KnowledgeComposition;
import org.drools.compiler.KnowledgeResource;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.UrlResource;
import org.drools.util.StringUtils;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ResourceHandler extends BaseAbstractHandler
    implements
    Handler {
    
    public ResourceHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add( KnowledgeComposition.class );

            this.validPeers = new HashSet(2);
            this.validPeers.add( null );
            this.validPeers.add( KnowledgeResource.class );

            this.allowNesting = true;
        }        
    }    
    
    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );      
        
        final KnowledgeComposition composition = (KnowledgeComposition) parser.getParent();   
        
        String src = attrs.getValue( "source" );
        String type = attrs.getValue( "type" );
        
        emptyAttributeCheck( localName,
                             "source",
                             src,
                             parser );
        
        emptyAttributeCheck( localName,
                             "type",
                             type,
                             parser );        
        KnowledgeResource part = new KnowledgeResource( src, KnowledgeType.valueOf( type ) );        
        
        return part;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        
        final KnowledgeComposition composition = (KnowledgeComposition) parser.getParent();
        final KnowledgeResource part = ( KnowledgeResource ) parser.getCurrent();
        composition.getResources().add( part );
        return part;
    }

    
    public Class< ? > generateNodeFor() {
        return KnowledgeResource.class;
    }

}
