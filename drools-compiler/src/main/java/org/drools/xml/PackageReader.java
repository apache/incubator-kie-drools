package org.drools.xml;
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



import java.io.BufferedInputStream;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
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
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class PackageReader extends DefaultHandler {
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
    private StringBuffer        characters;

    private Map                 handlers;

    private boolean             lastWasEndElement;

    private LinkedList          parents;

    private Object              peer;

    private Object              current;

    private PackageDescr        packageDescr;

    private boolean             inHandledRuleSubElement;

    private MessageFormat       message                       = new MessageFormat( "({0}: {1}, {2}): {3}" );

    private Map                 namespaces                    = new HashMap();

    EntityResolver              entityResolver;

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
    public PackageReader() {
        // init
        this.configurationStack = new LinkedList();
        this.parents = new LinkedList();

        this.handlers = new HashMap();
        this.handlers.put( "package",
                           new PackageHandler( this ) );
        this.handlers.put( "rule",
                           null );
        this.handlers.put( "query",
                           null );
        this.handlers.put( "attribute",
                           null );

        this.handlers.put( "function",
                           null );

        // Conditional Elements
        this.handlers.put( "and",
                           null );
        this.handlers.put( "or",
                           null );
        this.handlers.put( "not",
                           null );
        this.handlers.put( "exists",
                           null );

        this.handlers.put( "column",
                           null );

        // Field Constraints
        this.handlers.put( "literal",
                           null );
        this.handlers.put( "predicate",
                           null );
        this.handlers.put( "return-value",
                           null );
        this.handlers.put( "bound-variable",
                           null );

        initEntityResolver();

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
    public PackageReader(SAXParser parser) {
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
     */
    public PackageDescr read(Reader reader) throws SAXException,
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
     */
    public PackageDescr read(InputStream inputStream) throws SAXException,
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
     */
    public PackageDescr read(InputSource in) throws SAXException,
                                            IOException {
        SAXParser localParser = null;
        if ( this.parser == null ) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware( true );

            String isValidatingString = System.getProperty( "drools.schema.validating" );
            if ( System.getProperty( "drools.schema.validating" ) != null ) {
                this.isValidating = Boolean.getBoolean( "drools.schema.validating" );
            }

            if ( this.isValidating == true ) {
                factory.setValidating( true );
                try {
                    localParser = factory.newSAXParser();
                } catch ( ParserConfigurationException e ) {
                    throw new RuntimeException( e.getMessage() );
                }

                try {
                    localParser.setProperty( JAXP_SCHEMA_LANGUAGE,
                                             W3C_XML_SCHEMA );
                } catch ( SAXNotRecognizedException e ) {
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
                } catch ( ParserConfigurationException e ) {
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

        return this.packageDescr;
    }

    void setPackageDescr(PackageDescr packageDescr) {
        this.packageDescr = packageDescr;
    }

    PackageDescr getPackageDescr() {
        return this.packageDescr;
    }
    /**
     * @see org.xml.sax.ContentHandler
     */
    public void setDocumentLocator(Locator locator) {
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
        this.packageDescr = null;
        this.current = null;
        this.peer = null;
        this.lastWasEndElement = false;
        this.parents.clear();
        this.characters = null;
        this.configurationStack.clear();
        this.namespaces.clear();
    }

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
    public void startElement(String uri,
                             String localName,
                             String qname,
                             Attributes attrs) throws SAXException {
        // going down so no peer
        if ( !this.lastWasEndElement ) {
            this.peer = null;
        }

        Handler handler = getHandler( localName );

        if ( (handler != null) && (!this.parents.isEmpty() && this.parents.getLast() instanceof RuleDescr) ) {
            this.inHandledRuleSubElement = true;
        }

        if ( handler == null ) {
//            if ( ((this.inHandledRuleSubElement == false) && (this.parents.getLast() instanceof RuleDescr)) || (this.parents.getLast() instanceof PackageDescr) ) {
//                throw new SAXParseException( "unknown tag '" + localName + "' in namespace '" + uri + "'",
//                                             getLocator() );
//            }
            // no handler so build up the configuration
            startConfiguration( localName,
                                attrs );
            return;
        }

        validate( uri,
                  localName,
                  handler );

        Object node = handler.start( uri,
                                     localName,
                                     attrs );

        if ( node != null ) {
            this.parents.add( node );
            this.current = node;
        }
        this.lastWasEndElement = false;
    }

    /**
     * @param uri
     * @param localName
     * @param qname
     * @throws SAXException
     * @see org.xml.sax.ContentHandler
     */
    public void endElement(String uri,
                           String localName,
                           String qname) throws SAXException {
        Handler handler = getHandler( localName );

        if ( (handler != null) && (!this.parents.isEmpty() && this.parents.getLast() instanceof RuleDescr) ) {
            this.inHandledRuleSubElement = false;
        }

        if ( handler == null ) {
            if ( this.configurationStack.size() >= 1 ) {
                endConfiguration();
            }
            return;
        }

        this.current = getParent( handler.generateNodeFor() );

        Object node = handler.end( uri,
                                   localName );

        // next
        if ( node != null && !this.lastWasEndElement ) {
            this.peer = node;
        }
        // up or no children
        else if ( this.lastWasEndElement || (this.parents.getLast()).getClass().isInstance( this.current ) ) {
            this.peer = this.parents.removeLast();
        }

        this.lastWasEndElement = true;
    }

    private void validate(String uri,
                          String localName,
                          Handler handler) throws SAXParseException {
        boolean validParent = false;
        boolean validPeer = false;
        boolean invalidNesting = false;

        Set validParents = handler.getValidParents();
        Set validPeers = handler.getValidPeers();
        boolean allowNesting = handler.allowNesting();

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
        Class nodeClass = getHandler( localName ).generateNodeFor();
        if ( !nodeClass.isInstance( parent ) ) {
            Object allowedParent;
            Iterator it = validParents.iterator();
            while ( !validParent && it.hasNext() ) {
                allowedParent = it.next();
                if ( parent == null && allowedParent == null ) {
                    validParent = true;
                } else if ( allowedParent != null && ((Class) allowedParent).isInstance( parent ) ) {
                    validParent = true;
                }
            }
            if ( !validParent ) {
                throw new SAXParseException( "<" + localName + "> has an invalid parent element",
                                             getLocator() );
            }
        }

        // check valid peers
        // null peer means localname is rule-set
        Object peer = this.peer;

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
            throw new SAXParseException( "<" + localName + "> is after an invalid element",
                                         getLocator() );
        }

        if ( !allowNesting ) {
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
    protected void startConfiguration(String name,
                                      Attributes attrs) {
        this.characters = new StringBuffer();

        DefaultConfiguration config = new DefaultConfiguration( name );

        int numAttrs = attrs.getLength();

        for ( int i = 0; i < numAttrs; ++i ) {
            config.setAttribute( attrs.getLocalName( i ),
                                 attrs.getValue( i ) );
        }

        // lets add the namespaces as attributes
        for ( Iterator iter = namespaces.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String ns = (String) entry.getKey();
            String value = (String) entry.getValue();
            if ( ns == null || ns.length() == 0 ) {
                ns = "xmlns";
            } else {
                ns = "xmlns:" + ns;
            }
            config.setAttribute( ns,
                                 value );
        }

        if ( this.configurationStack.isEmpty() ) {
            this.configurationStack.addLast( config );
        } else {
            ((DefaultConfiguration) this.configurationStack.getLast()).addChild( config );
            this.configurationStack.addLast( config );
        }
    }

    Handler getHandler(String localName) {
        return (Handler) this.handlers.get( localName );
    }

    /**
     * @param chars
     * @param start
     * @param len
     * @see org.xml.sax.ContentHandler
     */
    public void characters(char[] chars,
                           int start,
                           int len) {
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
    protected Configuration endConfiguration() {
        DefaultConfiguration config = (DefaultConfiguration) this.configurationStack.removeLast();
        if ( this.characters != null ) {
            config.setText( this.characters.toString() );
        }

        this.characters = null;

        return config;
    }

    LinkedList getParents() {
        return this.parents;
    }

    Object getParent(Class parent) {
        ListIterator it = this.parents.listIterator( this.parents.size() );
        Object node = null;
        while ( it.hasPrevious() ) {
            node = it.previous();
            if ( parent.isInstance( node ) ) break;
        }
        return node;
    }

    Object getPeer() {
        return this.peer;
    }

    Object getCurrent() {
        return this.current;
    }

    public InputSource resolveEntity(String publicId,
                                     String systemId) throws SAXException {
        try {
            InputSource inputSource = resolveSchema( publicId,
                                                     systemId );
            if ( inputSource != null ) {
                return inputSource;
            }
            if ( entityResolver != null ) {
                return entityResolver.resolveEntity( publicId,
                                                     systemId );
            }
        } catch ( IOException ioe ) {
        }
        return null;
    }

    public void startPrefixMapping(String prefix,
                                   String uri) throws SAXException {
        super.startPrefixMapping( prefix,
                                  uri );
        namespaces.put( prefix,
                        uri );
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping( prefix );
        namespaces.remove( prefix );
    }

    private void print(SAXParseException x) {
        String msg = this.message.format( new Object[]{x.getSystemId(), new Integer( x.getLineNumber() ), new Integer( x.getColumnNumber() ), x.getMessage()} );
        System.out.println( msg );
    }

    public void warning(SAXParseException x) {
        print( x );
    }

    public void error(SAXParseException x) {
        print( x );
    }

    public void fatalError(SAXParseException x) throws SAXParseException {
        print( x );
        throw x;
    }

    private InputSource resolveSchema(String publicId,
                                      String systemId) throws SAXException,
                                                      IOException {
        // Schema files must end with xsd
        if ( !systemId.toLowerCase().endsWith( "xsd" ) ) {
            return null;
        }

        // try the actual location given by systemId
        try {
            URL url = new URL( systemId );
            return new InputSource( url.openStream() );
        } catch ( Exception e ) {
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

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if ( cl == null ) {
            cl = PackageReader.class.getClassLoader();
        }

        // Try looking in META-INF
        try {
            return new InputSource( cl.getResourceAsStream( "META-INF/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try looking in /META-INF
        try {
            return new InputSource( cl.getResourceAsStream( "/META-INF/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try looking at root of classpath
        try {
            return new InputSource( cl.getResourceAsStream( "/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try current working directory
        try {
            return new InputSource( new BufferedInputStream( new FileInputStream( xsd ) ) );
        } catch ( Exception e ) {
        }

        cl = ClassLoader.getSystemClassLoader();

        // Try looking in META-INF
        try {
            return new InputSource( cl.getResourceAsStream( "META-INF/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try looking in /META-INF
        try {
            return new InputSource( cl.getResourceAsStream( "/META-INF/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try looking at root of classpath
        try {
            return new InputSource( cl.getResourceAsStream( "/" + xsd ) );
        } catch ( Exception e ) {
        }

        // Try current working directory
        try {
            return new InputSource( new BufferedInputStream( new FileInputStream( xsd ) ) );
        } catch ( Exception e ) {
        }
        return null;
    }

    /**
     * Intializes EntityResolver that is configured via system property ENTITY_RESOLVER_PROPERTY_NAME.
     */
    private void initEntityResolver() {
        String entityResolveClazzName = System.getProperty( ENTITY_RESOLVER_PROPERTY_NAME );
        if ( entityResolveClazzName != null && entityResolveClazzName.length() > 0 ) {
            try {
                Class entityResolverClazz = Thread.currentThread().getContextClassLoader().loadClass( entityResolveClazzName );
                entityResolver = (EntityResolver) entityResolverClazz.newInstance();
            } catch ( Exception ignoreIt ) {
            }
        }
    }

}