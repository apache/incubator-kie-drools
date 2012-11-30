package org.drools.xml;

import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MinimalPomParser extends DefaultHandler {

    private int           depth;

    private boolean       inParent;

    private PomModel      model;

    private StringBuilder characters;    
    
    private Document            document;    

    public MinimalPomParser() {
        model = new PomModel();
    }
    
    public static PomModel parse(String path, InputStream is) {
        MinimalPomParser handler = new MinimalPomParser();        
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating( false );
            factory.setNamespaceAware( false );
            
            SAXParser parser = factory.newSAXParser();
            parser.parse( is, handler );
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse File '" + path + "'", e);
        }
        
        return handler.getPomModel();
        

    }

    public PomModel getPomModel() {
        return this.model;
    }
    
    public void startElement(final String uri,
                             final String localName,
                             final String qname,
                             final Attributes attrs) throws SAXException {
        if ( "parent".equals( qname ) && depth == 1 ) {
            inParent = true;
        } else if ( "groupId".equals( qname ) || "artifactId".equals( qname ) || "version".equals( qname ) ) {
            this.characters = new StringBuilder();            
        }
        
        depth++;
    }

    public void endElement(final String uri,
                           final String localName,
                           final String qname) throws SAXException {
        if ( inParent ) {
            String text = ( this.characters != null ) ? this.characters.toString() : null;
            if ( text != null ) {
                if ( "groupId".equals( qname ) ) {
                    model.setParentGroupId( text );
                } else if ( "artifactId".equals( qname ) ) {
                    model.setParentArtifactId( text );
                } else if ( "version".equals( qname ) ) {
                    model.setParentVersion( text );
                }
            }
            if ( "parent".equals( qname ) && depth == 2 ) {
                inParent = false;
            }
        } else {
            String text = ( this.characters != null ) ? this.characters.toString() : null;
            if ( text != null ) {
                if ( "groupId".equals( qname ) ) {
                    model.setGroupId( text );
                } else if ( "artifactId".equals( qname ) ) {
                    model.setArtifactId( text );                
                } else if ( "version".equals( qname ) ) {
                    model.setVersion( text );
                }            
            }
        }
        this.characters = null;
        depth--;
    }
    
    /**
     * @param chars
     * @param start
     * @param len
     * @see org.xml.sax.ContentHandler
     */
    public void characters(final char[] chars,
                           final int start,
                           final int len) {
        if ( this.characters != null ) {
            this.characters.append( chars,
                                    start,
                                    len );
        }
    }  
}
