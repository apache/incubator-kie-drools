package org.drools.xml.changeset;

import java.util.HashSet;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.io.Resource;
import org.drools.io.impl.KnowledgeResource;
import org.drools.io.internal.InternalResource;
import org.drools.util.StringUtils;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DecisionTableConfigurationHandler extends BaseAbstractHandler
    implements
    Handler {

    public DecisionTableConfigurationHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet( 1 );
            this.validParents.add( Resource.class );

            this.validPeers = new HashSet( 1 );
            this.validPeers.add( null );

            this.allowNesting = false;
        }
    }

    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );

        String type = attrs.getValue( "input-type" );
        String worksheetName = attrs.getValue( "worksheet-name" );

        emptyAttributeCheck( localName,
                             "input-type",
                             type,
                             parser );

        DecisionTableConfiguration dtConf = new DecisionTableConfigurationImpl();
        dtConf.setInputType( DecisionTableInputType.valueOf( type ) );
        if ( !StringUtils.isEmpty( worksheetName ) ) {
            dtConf.setWorksheetName( worksheetName );
        }
        
        return dtConf;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        final InternalResource resource = (InternalResource) parser.getParent();
        ResourceConfiguration dtConf = (ResourceConfiguration) parser.getCurrent();
        resource.setConfiguration( dtConf );
        
        return dtConf;
    }

    public Class< ? > generateNodeFor() {
        return ResourceConfiguration.class;
    }

}
