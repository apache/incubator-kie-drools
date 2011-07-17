/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <code>RuleSet</code> loader.
 *
 * Note you can override the default entity resolver by setting the System property of:
 *  <code>org.drools.io.EntityResolve</code> to your own custom entity resolver.
 *  This can be done using -Dorg.drools.io.EntityResolver=YourClassHere on the command line, for instance.
 */
public class ExtensibleXmlParser extends DefaultHandler {
    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------
    public static final String  ENTITY_RESOLVER_PROPERTY_NAME = "org.drools.io.EntityResolver";

    /** Namespace URI for the general tags. */
    public static final String  RULES_NAMESPACE_URI           = "http://drools.org/rules";

    private static final String JAXP_SCHEMA_LANGUAGE          = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String W3C_XML_SCHEMA                = "http://www.w3.org/2001/XMLSchema";

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------
    /** SAX parser. */
    private SAXParser           parser;

    /** isValidating */
    private boolean             isValidating                  = true;

    /** Locator for errors. */
    private Locator             locator;

    // private Map repo;

    /** Stack of configurations. */
    private LinkedList          configurationStack;

    /** Current configuration text. */
    private StringBuilder       characters;

    private SemanticModules     modules;

    private boolean             lastWasEndElement;

    private LinkedList          parents;

    private Object              peer;

    private Object              current;

    private Object              data;

    private final MessageFormat message                       = new MessageFormat( "({0}: {1}, {2}): {3}" );

    private final Map           namespaces                    = new HashMap();

    private EntityResolver      entityResolver;

    private Document            document;
    private DocumentFragment    docFragment;

    private ClassLoader         classLoader;

    private Map                 metaData                      = new HashMap();

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     *
     * <p>
     * Uses the default JAXP SAX parser and the default classpath-based
     * <code>DefaultSemanticModule</code>.
     * </p>
     */
    public ExtensibleXmlParser() {
        // init
        this.configurationStack = new LinkedList();
        this.parents = new LinkedList();

        initEntityResolver();
    }

    public void setSemanticModules(SemanticModules modules) {
        this.modules = modules;
    }

    /**
     * Construct.
     *
     * <p>
     * Uses the default classpath-based <code>DefaultSemanticModule</code>.
     * </p>
     *
     * @param parser
     *            The SAX parser.
     */
    public ExtensibleXmlParser(final SAXParser parser) {
        this();
        this.parser = parser;
    }

    // ----------------------------------------------------------------------
    // Instance methods
    // ----------------------------------------------------------------------

    /**
     * Read a <code>RuleSet</code> from a <code>Reader</code>.
     *
     * @param reader
     *            The reader containing the rule-set.
     *
     * @return The rule-set.
     * @throws ParserConfigurationException 
     */
    public Object read(final Reader reader) throws SAXException,
                                           IOException {
        return read( new InputSource( reader ) );
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputStream</code>.
     *
     * @param inputStream
     *            The input-stream containing the rule-set.
     *
     * @return The rule-set.
     * @throws ParserConfigurationException 
     */
    public Object read(final InputStream inputStream) throws SAXException,
                                                     IOException {
        return read( new InputSource( inputStream ) );
    }

    /**
     * Read a <code>RuleSet</code> from an <code>InputSource</code>.
     *
     * @param in
     *            The rule-set input-source.
     *
     * @return The rule-set.
     * @throws ParserConfigurationException 
     */
    public Object read(final InputSource in) throws SAXException,
                                            IOException {
        if ( this.docFragment == null ) {
            DocumentBuilderFactory f;
            try {
                f =  DocumentBuilderFactory.newInstance();
            } catch ( FactoryConfigurationError e ) {
                // obscure JDK1.5 bug where FactoryFinder in the JRE returns a null ClassLoader, so fall back to hard coded xerces.
                // https://stg.network.org/bugzilla/show_bug.cgi?id=47169
                // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4633368
                try {
                    f = (DocumentBuilderFactory) Class.forName( "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl" ).newInstance();
                } catch ( Exception e1 ) {
                    throw new RuntimeException( "Unable to create new DOM Document",
                                                e1 );
                }
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to create new DOM Document",
                                            e );
            }
            
            try {
                this.document = f.newDocumentBuilder().newDocument();
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to create new DOM Document",
                                            e );
            }
            this.docFragment = this.document.createDocumentFragment();
        }

        SAXParser localParser = null;
        if ( this.parser == null ) {
            SAXParserFactory factory = null;
            try {
                factory = SAXParserFactory.newInstance();
            } catch ( FactoryConfigurationError e) {
                // obscure JDK1.5 bug where FactoryFinder in the JRE returns a null ClassLoader, so fall back to hard coded xerces.
                // https://stg.network.org/bugzilla/show_bug.cgi?id=47169
                // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4633368                
                try {
                    factory = (SAXParserFactory) Class.forName( "org.apache.xerces.jaxp.SAXParserFactoryImpl" ).newInstance();
                } catch ( Exception e1 ) {
                    throw new RuntimeException( "Unable to create new DOM Document",
                                                e1 );
                }
            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to create new DOM Document",
                                            e );
            }
            
            factory.setNamespaceAware( true );

            final String isValidatingString = System.getProperty( "drools.schema.validating" );
            if ( System.getProperty( "drools.schema.validating" ) != null ) {
                this.isValidating = Boolean.getBoolean( "drools.schema.validating" );
            }

            if ( this.isValidating == true ) {
                factory.setValidating( true );
                try {
                    localParser = factory.newSAXParser();
                } catch ( final ParserConfigurationException e ) {
                    throw new RuntimeException( e.getMessage() );
                }

                try {
                    localParser.setProperty( ExtensibleXmlParser.JAXP_SCHEMA_LANGUAGE,
                                             ExtensibleXmlParser.W3C_XML_SCHEMA );
                } catch ( final SAXNotRecognizedException e ) {
                    boolean hideWarnings = Boolean.getBoolean( "drools.schema.hidewarnings" );
                    if ( !hideWarnings ) {
                        System.err.println( "Your SAX parser is not JAXP 1.2 compliant - turning off validation." );
                    }
                    localParser = null;
                }
            }

            if ( localParser == null ) {
                // not jaxp1.2 compliant so turn off validation
                try {
                    this.isValidating = false;
                    factory.setValidating( this.isValidating );
                    localParser = factory.newSAXParser();
                } catch ( final ParserConfigurationException e ) {
                    throw new RuntimeException( e.getMessage() );
                }
            }
        } else {
            localParser = this.parser;
        }

        if ( !localParser.isNamespaceAware() ) {
            throw new RuntimeException( "parser must be namespace-aware" );
        }

        localParser.parse( in,
                           this );

        return this.data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Map getMetaData() {
        return this.metaData;
    }

    /**
     * @see org.xml.sax.ContentHandler
     */
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    /**
     * Get the <code>Locator</code>.
     *
     * @return The locator.
     */
    public Locator getLocator() {
        return this.locator;
    }

    public void startDocument() {
        this.isValidating = true;
        this.current = null;
        this.peer = null;
        this.lastWasEndElement = false;
        this.parents.clear();
        this.characters = null;
        this.configurationStack.clear();
        this.namespaces.clear();
    }

    private int direction = 0;

    /**
     * @param uri
     * @param localName
     * @param qname
     * @param attrs
     * @throws SAXException
     * @see org.xml.sax.ContentHandler
     *
     * @todo: better way to manage unhandled elements
     */
    public void startElement(final String uri,
                             final String localName,
                             final String qname,
                             final Attributes attrs) throws SAXException {
        if ( direction == 1 ) {
            // going down again, so clear 
            this.peer = null;
        } else {
            direction = 1;
        }

        final Handler handler = getHandler( uri,
                                            localName );

        if ( handler == null ) {
            startElementBuilder( localName,
                                 attrs );
            return;
        }

        validate( uri,
                  localName,
                  handler );

        Object node = handler.start( uri,
                                     localName,
                                     attrs,
                                     this );
        
        this.parents.add( node );    
    }

    /**
     * @param uri
     * @param localName
     * @param qname
     * @throws SAXException
     * @see org.xml.sax.ContentHandler
     */

    public void endElement(final String uri,
                           final String localName,
                           final String qname) throws SAXException {
        direction = -1;
        final Handler handler = getHandler( uri,
                                            localName );

        if ( handler == null ) {
            if ( this.configurationStack.size() >= 1 ) {
                endElementBuilder();
            }
            return;
        }

        this.current = removeParent();

        this.peer = handler.end( uri,
                                 localName,
                                 this );
    }

    public static class Null {
        public static final Null instance = new Null();
    }

    private void validate(final String uri,
                          final String localName,
                          final Handler handler) throws SAXParseException {
        boolean validParent = false;
        boolean validPeer = false;
        boolean invalidNesting = false;

        final Set validParents = handler.getValidParents();
        final Set validPeers = handler.getValidPeers();
        boolean allowNesting = handler.allowNesting();
        
        if ( validParents == null || validPeers == null ) {
            return;
        }

        // get parent
        Object parent;
        if ( this.parents.size() != 0 ) {
            parent = this.parents.getLast();
        } else {
            parent = null;
        }

        // check valid parents
        // null parent means localname is rule-set
        // dont process if elements are the same
        // instead check for allowed nesting
        final Class nodeClass = getHandler( uri,
                                            localName ).generateNodeFor();
        if ( nodeClass != null && !nodeClass.isInstance( parent ) ) {
            Object allowedParent;
            final Iterator it = validParents.iterator();
            while ( !validParent && it.hasNext() ) {
                allowedParent = it.next();
                if ( parent == null && allowedParent == null ) {
                    validParent = true;
                } else if ( allowedParent != null && ((Class) allowedParent).isInstance( parent ) ) {
                    validParent = true;
                }
            }
            if ( !validParent ) {
                throw new SAXParseException( "<" + localName + "> has an invalid parent element [" + parent + "]",
                                             getLocator() );
            }
        }

        // check valid peers
        // null peer means localname is rule-set
        final Object peer = this.peer;

        Object allowedPeer;
        Iterator it = validPeers.iterator();
        while ( !validPeer && it.hasNext() ) {
            allowedPeer = it.next();
            if ( peer == null && allowedPeer == null ) {
                validPeer = true;
            } else if ( allowedPeer != null && ((Class) allowedPeer).isInstance( peer ) ) {
                validPeer = true;
            }
        }
        if ( !validPeer ) {
            throw new SAXParseException( "<" + localName + "> is after an invalid element: " + Handler.class.getName(),
                                         getLocator() );
        }

        if ( nodeClass != null && !allowNesting ) {
            it = this.parents.iterator();
            while ( !invalidNesting && it.hasNext() ) {
                if ( nodeClass.isInstance( it.next() ) ) {
                    invalidNesting = true;
                }
            }
        }
        if ( invalidNesting ) {
            throw new SAXParseException( "<" + localName + ">  may not be nested",
                                         getLocator() );
        }

    }

    /**
     * Start a configuration node.
     *
     * @param name
     *            Tag name.
     * @param attrs
     *            Tag attributes.
     */
    public void startElementBuilder(final String tagName,
                                    final Attributes attrs) {
        this.characters = new StringBuilder();

        final Element element = this.document.createElement( tagName );

        //final DefaultConfiguration config = new DefaultConfiguration( tagName );

        final int numAttrs = attrs.getLength();

        for ( int i = 0; i < numAttrs; ++i ) {
            element.setAttribute( attrs.getLocalName( i ),
                                  attrs.getValue( i ) );
        }

        if ( this.configurationStack.isEmpty() ) {
            this.configurationStack.addLast( element );
        } else {
            ((Element) this.configurationStack.getLast()).appendChild( element );
            this.configurationStack.addLast( element );
        }
    }

    Handler getHandler(final String uri,
                       final String localName) {
        SemanticModule module = this.modules.getSemanticModule( uri );
        if ( module != null ) {
            return module.getHandler( localName );
        } else {
            return null;
        }
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

    /**
     * End a configuration node.
     *
     * @return The configuration.
     */
    public Element endElementBuilder() {
        final Element element = (Element) this.configurationStack.removeLast();
        if ( this.characters != null ) {
            element.appendChild( this.document.createTextNode( this.characters.toString() ) );
        }

        this.characters = null;

        return element;
    }

    public Object getParent() {
        return this.parents.getLast();
    }

    public Object getParent(int index) {
        ListIterator it = this.parents.listIterator( this.parents.size() );
        int x = 0;
        Object parent = null;
        while ( x++ <= index ) {
            parent = it.previous();
        }
        return parent;
    }

    public Object removeParent() {
        Object parent = this.parents.removeLast();
        return parent;
    }

    public LinkedList getParents() {
        return this.parents;
    }

    public Object getParent(final Class parent) {
        final ListIterator it = this.parents.listIterator( this.parents.size() );
        Object node = null;
        while ( it.hasPrevious() ) {
            node = it.previous();
            if ( parent.isInstance( node ) ) {
                break;
            }
        }
        return node;
    }

    public Object getPeer() {
        return this.peer;
    }

    public Object getCurrent() {
        return this.current;
    }

    public InputSource resolveEntity(final String publicId,
                                     final String systemId) throws SAXException {
        try {
            final InputSource inputSource = resolveSchema( publicId,
                                                           systemId );
            if ( inputSource != null ) {
                return inputSource;
            }
            if ( this.entityResolver != null ) {
                return this.entityResolver.resolveEntity( publicId,
                                                          systemId );
            }
        } catch ( final IOException ioe ) {
        }
        return null;
    }

    public void startPrefixMapping(final String prefix,
                                   final String uri) throws SAXException {
        super.startPrefixMapping( prefix,
                                  uri );
        this.namespaces.put( prefix,
                             uri );
    }

    public void endPrefixMapping(final String prefix) throws SAXException {
        super.endPrefixMapping( prefix );
        this.namespaces.remove( prefix );
    }

    private void print(final SAXParseException x) {
        final String msg = this.message.format( new Object[]{x.getSystemId(), new Integer( x.getLineNumber() ), new Integer( x.getColumnNumber() ), x.getMessage()} );
        System.out.println( msg );
    }

    public void warning(final SAXParseException x) {
        print( x );
    }

    public void error(final SAXParseException x) {
        print( x );
    }

    public void fatalError(final SAXParseException x) throws SAXParseException {
        print( x );
        throw x;
    }

    private InputSource resolveSchema(final String publicId,
                                      final String systemId) throws SAXException,
                                                            IOException {
        // Schema files must end with xsd
        if ( !systemId.toLowerCase().endsWith( "xsd" ) ) {
            return null;
        }

        // try the actual location given by systemId
        try {
            final URL url = new URL( systemId );
            return new InputSource( url.openStream() );
        } catch ( final Exception e ) {
        }

        // Try and get the index for the filename, else return null
        String xsd;
        int index = systemId.lastIndexOf( "/" );
        if ( index == -1 ) {
            index = systemId.lastIndexOf( "\\" );
        }
        if ( index != -1 ) {
            xsd = systemId.substring( index + 1 );
        } else {
            xsd = systemId;
        }

        // Try looking in META-INF
        {
            final InputStream is = classLoader.getResourceAsStream( "META-INF/" + xsd );
            if ( is != null ) {
                return new InputSource( is );
            }
        }

        // Try looking in /META-INF
        {
            final InputStream is = classLoader.getResourceAsStream( "/META-INF/" + xsd );
            if ( is != null ) {
                return new InputSource( is );
            }
        }

        // Try looking at root of classpath
        {
            final InputStream is = classLoader.getResourceAsStream( "/" + xsd );
            if ( is != null ) {
                return new InputSource( is );
            }
        }

        // Try current working directory
        {
            final File file = new File( xsd );
            if ( file.exists() ) {
                return new InputSource( new BufferedInputStream( new FileInputStream( file ) ) );
            }
        }

        return null;
    }

    /**
     * Intializes EntityResolver that is configured via system property ENTITY_RESOLVER_PROPERTY_NAME.
     */
    private void initEntityResolver() {
        final String entityResolveClazzName = System.getProperty( ExtensibleXmlParser.ENTITY_RESOLVER_PROPERTY_NAME );
        if ( entityResolveClazzName != null && entityResolveClazzName.length() > 0 ) {
            try {

                final Class entityResolverClazz = classLoader.loadClass( entityResolveClazzName );
                this.entityResolver = (EntityResolver) entityResolverClazz.newInstance();
            } catch ( final Exception ignoreIt ) {
            }
        }
    }

}
