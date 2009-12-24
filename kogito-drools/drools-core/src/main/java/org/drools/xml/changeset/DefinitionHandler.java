package org.drools.xml.changeset;

import java.util.Collection;
import java.util.HashSet;

import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefinitionHandler extends BaseAbstractHandler
    implements
    Handler {


    public class DefinitionHandlerData {
        private String packageName;
        private String name;

        public DefinitionHandlerData(String packageName, String name) {
            this.packageName = packageName;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getPackageName() {
            return packageName;
        }

        
    }

    public DefinitionHandler() {
        if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet(1);
            this.validParents.add( Collection.class );

            this.validPeers = new HashSet(2);
            this.validPeers.add( null );
            this.validPeers.add( DefinitionHandler.DefinitionHandlerData.class );

            this.allowNesting = true;
        }        
    }    
    
    public Object start(String uri,
                        String localName,
                        Attributes attrs,
                        ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );      
        
        
        String packageName = attrs.getValue( "package" );
        String name = attrs.getValue( "name" );
        
        emptyAttributeCheck( localName,
                             "package",
                             packageName,
                             parser );
        
        emptyAttributeCheck( localName,
                             "name",
                             name,
                             parser );        
        DefinitionHandler.DefinitionHandlerData data = new DefinitionHandlerData(packageName, name);
        
        return data;
    }

    public Object end(String uri,
                      String localName,
                      ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        
        final Collection collection = (Collection) parser.getParent();
        final DefinitionHandlerData data = ( DefinitionHandlerData ) parser.getCurrent();
        collection.add( data );
        return data;
    }

    
    public Class< ? > generateNodeFor() {
        return DefinitionHandler.DefinitionHandlerData.class;
    }

}
