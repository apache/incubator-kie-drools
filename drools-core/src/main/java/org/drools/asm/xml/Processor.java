/***
 * ASM XML Adapter
 * Copyright (c) 2004, Eugene Kuleshov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.drools.asm.xml;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.drools.asm.ClassReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Processor is a command line tool that can be used for bytecode waving
 * directed by XSL transformation. <p> In order to use a concrete XSLT engine,
 * system property <tt>javax.xml.transform.TransformerFactory</tt> must be set
 * to one of the following values.
 * 
 * <blockquote> <table border="1" cellspacing="0" cellpadding="3"> <tr> <td>jd.xslt</td>
 * <td>jd.xml.xslt.trax.TransformerFactoryImpl</td> </tr>
 * 
 * <tr> <td>Saxon</td> <td>net.sf.saxon.TransformerFactoryImpl</td> </tr>
 * 
 * <tr> <td>Caucho</td> <td>com.caucho.xsl.Xsl</td> </tr>
 * 
 * <tr> <td>Xalan interpeter</td> <td>org.apache.xalan.processor.TransformerFactory</td>
 * </tr>
 * 
 * <tr> <td>Xalan xsltc</td> <td>org.apache.xalan.xsltc.trax.TransformerFactoryImpl</td>
 * </tr> </table> </blockquote>
 * 
 * @author Eugene Kuleshov
 */
public class Processor {

    public static final int     BYTECODE        = 1;

    public static final int     MULTI_XML       = 2;

    public static final int     SINGLE_XML      = 3;

    private static final String SINGLE_XML_NAME = "classes.xml";

    private int                 inRepresentation;

    private int                 outRepresentation;

    private InputStream         input           = null;

    private OutputStream        output          = null;

    private Source              xslt            = null;

    private boolean             computeMax;

    private int                 n               = 0;

    public Processor(final int inRepresenation,
                     final int outRepresentation,
                     final InputStream input,
                     final OutputStream output,
                     final Source xslt) {
        this.inRepresentation = inRepresenation;
        this.outRepresentation = outRepresentation;
        this.input = input;
        this.output = output;
        this.xslt = xslt;
        this.computeMax = true;
    }

    public int process() throws TransformerException,
                        IOException,
                        SAXException {
        final ZipInputStream zis = new ZipInputStream( this.input );
        final ZipOutputStream zos = new ZipOutputStream( this.output );
        final OutputStreamWriter osw = new OutputStreamWriter( zos );

        Thread.currentThread().setContextClassLoader( getClass().getClassLoader() );

        final TransformerFactory tf = TransformerFactory.newInstance();
        if ( !tf.getFeature( SAXSource.FEATURE ) || !tf.getFeature( SAXResult.FEATURE ) ) {
            return 0;
        }

        final SAXTransformerFactory saxtf = (SAXTransformerFactory) tf;
        Templates templates = null;
        if ( this.xslt != null ) {
            templates = saxtf.newTemplates( this.xslt );
        }

        // configuring outHandlerFactory
        // ///////////////////////////////////////////////////////

        final EntryElement entryElement = getEntryElement( zos );

        ContentHandler outDocHandler = null;
        switch ( this.outRepresentation ) {
            case BYTECODE :
                outDocHandler = new OutputSlicingHandler( new ASMContentHandlerFactory( zos,
                                                                                        this.computeMax ),
                                                          entryElement,
                                                          false );
                break;

            case MULTI_XML :
                outDocHandler = new OutputSlicingHandler( new SAXWriterFactory( osw,
                                                                                true ),
                                                          entryElement,
                                                          true );
                break;

            case SINGLE_XML :
                final ZipEntry outputEntry = new ZipEntry( Processor.SINGLE_XML_NAME );
                zos.putNextEntry( outputEntry );
                outDocHandler = new SAXWriter( osw,
                                               false );
                break;

        }

        // configuring inputDocHandlerFactory
        // /////////////////////////////////////////////////
        ContentHandler inDocHandler = null;
        if ( templates == null ) {
            inDocHandler = outDocHandler;
        } else {
            inDocHandler = new InputSlicingHandler( "class",
                                                    outDocHandler,
                                                    new TransformerHandlerFactory( saxtf,
                                                                                   templates,
                                                                                   outDocHandler ) );
        }
        final ContentHandlerFactory inDocHandlerFactory = new SubdocumentHandlerFactory( inDocHandler );

        if ( inDocHandler != null && this.inRepresentation != Processor.SINGLE_XML ) {
            inDocHandler.startDocument();
            inDocHandler.startElement( "",
                                       "classes",
                                       "classes",
                                       new AttributesImpl() );
        }

        int i = 0;
        ZipEntry ze = null;
        while ( (ze = zis.getNextEntry()) != null ) {
            update( ze.getName(),
                    this.n++ );
            if ( isClassEntry( ze ) ) {
                processEntry( zis,
                              ze,
                              inDocHandlerFactory );
            } else {
                final OutputStream os = entryElement.openEntry( getName( ze ) );
                copyEntry( zis,
                           os );
                entryElement.closeEntry();
            }

            i++;
        }

        if ( inDocHandler != null && this.inRepresentation != Processor.SINGLE_XML ) {
            inDocHandler.endElement( "",
                                     "classes",
                                     "classes" );
            inDocHandler.endDocument();
        }

        if ( this.outRepresentation == Processor.SINGLE_XML ) {
            zos.closeEntry();
        }
        zos.flush();
        zos.close();

        return i;
    }

    private void copyEntry(final InputStream is,
                           final OutputStream os) throws IOException {
        if ( this.outRepresentation == Processor.SINGLE_XML ) {
            return;
        }

        final byte[] buff = new byte[2048];
        int i;
        while ( (i = is.read( buff )) != -1 ) {
            os.write( buff,
                      0,
                      i );
        }
    }

    private boolean isClassEntry(final ZipEntry ze) {
        final String name = ze.getName();
        return this.inRepresentation == Processor.SINGLE_XML && name.equals( Processor.SINGLE_XML_NAME ) || name.endsWith( ".class" ) || name.endsWith( ".class.xml" );
    }

    private void processEntry(final ZipInputStream zis,
                              final ZipEntry ze,
                              final ContentHandlerFactory handlerFactory) {
        final ContentHandler handler = handlerFactory.createContentHandler();
        try {

            // if (CODE2ASM.equals(command)) { // read bytecode and process it
            // // with TraceClassVisitor
            // ClassReader cr = new ClassReader(readEntry(zis, ze));
            // cr.accept(new TraceClassVisitor(null, new PrintWriter(os)),
            // false);
            // }

            final boolean singleInputDocument = this.inRepresentation == Processor.SINGLE_XML;
            if ( this.inRepresentation == Processor.BYTECODE ) { // read bytecode and process it
                // with handler
                final ClassReader cr = new ClassReader( readEntry( zis,
                                                                   ze ) );
                cr.accept( new SAXClassAdapter( handler,
                                                singleInputDocument ),
                           false );

            } else { // read XML and process it with handler
                final XMLReader reader = XMLReaderFactory.createXMLReader();
                reader.setContentHandler( handler );
                reader.parse( new InputSource( singleInputDocument ? (InputStream) new ProtectedInputStream( zis ) : new ByteArrayInputStream( readEntry( zis,
                                                                                                                                                          ze ) ) ) );

            }
        } catch ( final Exception ex ) {
            update( ze.getName(),
                    0 );
            update( ex,
                    0 );
        }
    }

    private EntryElement getEntryElement(final ZipOutputStream zos) {
        if ( this.outRepresentation == Processor.SINGLE_XML ) {
            return new SingleDocElement( zos );
        }
        return new ZipEntryElement( zos );
    }

    // private ContentHandlerFactory getHandlerFactory(
    // OutputStream os,
    // SAXTransformerFactory saxtf,
    // Templates templates)
    // {
    // ContentHandlerFactory factory = null;
    // if (templates == null) {
    // if (outputRepresentation == BYTECODE) { // factory used to write
    // // bytecode
    // factory = new ASMContentHandlerFactory(os, computeMax);
    // } else { // factory used to write XML
    // factory = new SAXWriterFactory(os, true);
    // }
    // } else {
    // if (outputRepresentation == BYTECODE) { // factory used to transform
    // // and then write bytecode
    // factory = new ASMTransformerHandlerFactory(saxtf,
    // templates,
    // os,
    // computeMax);
    // } else { // factory used to transformand then write XML
    // factory = new TransformerHandlerFactory(saxtf,
    // templates,
    // os,
    // outputRepresentation == SINGLE_XML);
    // }
    // }
    // return factory;
    // }

    private String getName(final ZipEntry ze) {
        String name = ze.getName();
        if ( isClassEntry( ze ) ) {
            if ( this.inRepresentation != Processor.BYTECODE && this.outRepresentation == Processor.BYTECODE ) {
                name = name.substring( 0,
                                       name.length() - 4 ); // .class.xml to
                // .class
            } else if ( this.inRepresentation == Processor.BYTECODE && this.outRepresentation != Processor.BYTECODE ) {
                name = name.concat( ".xml" ); // .class to .class.xml
            }
            // } else if( CODE2ASM.equals( command)) {
            // name = name.substring( 0, name.length()-6).concat( ".asm");
        }
        return name;
    }

    private byte[] readEntry(final ZipInputStream zis,
                             final ZipEntry ze) throws IOException {
        final long size = ze.getSize();
        if ( size > -1 ) {
            final byte[] buff = new byte[(int) size];
            int k = 0;
            int n;
            while ( (n = zis.read( buff,
                                   k,
                                   buff.length - k )) > 0 ) {
                k += n;
            }
            return buff;
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final byte[] buff = new byte[4096];
        int i;
        while ( (i = zis.read( buff )) != -1 ) {
            bos.write( buff,
                       0,
                       i );
        }
        return bos.toByteArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    protected void update(final Object arg,
                          final int n) {
        if ( arg instanceof Throwable ) {
            ((Throwable) arg).printStackTrace();
        } else {
            if ( (n % 100) == 0 ) {
                System.err.println( n + " " + arg );
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        if ( args.length < 2 ) {
            showUsage();
            return;
        }

        final int inRepresentation = getRepresentation( args[0] );
        final int outRepresentation = getRepresentation( args[1] );

        InputStream is = System.in;
        OutputStream os = new BufferedOutputStream( System.out );

        Source xslt = null;
        // boolean computeMax = true;

        for ( int i = 2; i < args.length; i++ ) {
            if ( "-in".equals( args[i] ) ) {
                is = new FileInputStream( args[++i] );

            } else if ( "-out".equals( args[i] ) ) {
                os = new BufferedOutputStream( new FileOutputStream( args[++i] ) );

            } else if ( "-xslt".equals( args[i] ) ) {
                xslt = new StreamSource( new FileInputStream( args[++i] ) );

                // } else if( "-computemax".equals( args[ i].toLowerCase())) {
                // computeMax = true;

            } else {
                showUsage();
                return;

            }
        }

        if ( inRepresentation == 0 || outRepresentation == 0 ) {
            showUsage();
            return;
        }

        final Processor m = new Processor( inRepresentation,
                                           outRepresentation,
                                           is,
                                           os,
                                           xslt );

        final long l1 = System.currentTimeMillis();
        final int n = m.process();
        final long l2 = System.currentTimeMillis();
        System.err.println( n );
        System.err.println( "" + (l2 - l1) + "ms  " + (1000f * n / (l2 - l1)) + " resources/sec" );
    }

    private static int getRepresentation(final String s) {
        if ( "code".equals( s ) ) {
            return Processor.BYTECODE;
        } else if ( "xml".equals( s ) ) {
            return Processor.MULTI_XML;
        } else if ( "singlexml".equals( s ) ) {
            return Processor.SINGLE_XML;
        }
        return 0;
    }

    private static void showUsage() {
        System.err.println( "Usage: Main <in format> <out format> [-in <input jar>] [-out <output jar>] [-xslt <xslt file>]" );
        System.err.println( "  when -in or -out is omitted sysin and sysout would be used" );
        System.err.println( "  <in format> and <out format> - code | xml | singlexml" );
    }

    /**
     * IputStream wrapper class used to protect input streams from being closed
     * by some stupid XML parsers.
     */
    private static final class ProtectedInputStream extends InputStream {
        private final InputStream is;

        private ProtectedInputStream(final InputStream is) {
            super();
            this.is = is;
        }

        public final void close() throws IOException {
        }

        public final int read() throws IOException {
            return this.is.read();
        }

        public final int read(final byte[] b,
                              final int off,
                              final int len) throws IOException {
            return this.is.read( b,
                                 off,
                                 len );
        }

        public final int available() throws IOException {
            return this.is.available();
        }
    }

    /**
     * A {@link ContentHandlerFactory ContentHandlerFactory} is used to create
     * {@link org.xml.sax.ContentHandler ContentHandler} instances for concrete
     * context.
     */
    private static interface ContentHandlerFactory {

        /**
         * Creates an instance of the content handler.
         * 
         * @return content handler
         */
        ContentHandler createContentHandler();

    }

    /**
     * SAXWriterFactory
     */
    private static final class SAXWriterFactory
        implements
        ContentHandlerFactory {
        private Writer  w;

        private boolean optimizeEmptyElements;

        public SAXWriterFactory(final Writer w,
                                final boolean optimizeEmptyElements) {
            this.w = w;
            this.optimizeEmptyElements = optimizeEmptyElements;
        }

        public final ContentHandler createContentHandler() {
            return new SAXWriter( this.w,
                                  this.optimizeEmptyElements );
        }

    }

    /**
     * ASMContentHandlerFactory
     */
    private static final class ASMContentHandlerFactory
        implements
        ContentHandlerFactory {
        private OutputStream os;

        private boolean      computeMax;

        public ASMContentHandlerFactory(final OutputStream os,
                                        final boolean computeMax) {
            this.os = os;
            this.computeMax = computeMax;
        }

        public final ContentHandler createContentHandler() {
            return new ASMContentHandler( this.os,
                                          this.computeMax );
        }

    }

    /**
     * TransformerHandlerFactory
     */
    private static final class TransformerHandlerFactory
        implements
        ContentHandlerFactory {
        private SAXTransformerFactory saxtf;

        private Templates             templates;

        private ContentHandler        outputHandler;

        public TransformerHandlerFactory(final SAXTransformerFactory saxtf,
                                         final Templates templates,
                                         final ContentHandler outputHandler) {
            this.saxtf = saxtf;
            this.templates = templates;
            this.outputHandler = outputHandler;
        }

        public final ContentHandler createContentHandler() {
            try {
                final TransformerHandler handler = this.saxtf.newTransformerHandler( this.templates );
                handler.setResult( new SAXResult( this.outputHandler ) );
                return handler;
            } catch ( final TransformerConfigurationException ex ) {
                throw new RuntimeException( ex.toString() );
            }
        }
    }

    /**
     * SubdocumentHandlerFactory
     */
    private final static class SubdocumentHandlerFactory
        implements
        ContentHandlerFactory {
        private ContentHandler subdocumentHandler;

        public SubdocumentHandlerFactory(final ContentHandler subdocumentHandler) {
            this.subdocumentHandler = subdocumentHandler;
        }

        public final ContentHandler createContentHandler() {
            return this.subdocumentHandler;
        }

    }

    /**
     * A {@link org.xml.sax.ContentHandler ContentHandler} and
     * {@link org.xml.sax.ext.LexicalHandler LexicalHandler} that serializes XML
     * from SAX 2.0 events into {@link java.io.Writer Writer}.
     * 
     * <i><blockquote> This implementation does not support namespaces, entity
     * definitions (uncluding DTD), CDATA and text elements. </blockquote></i>
     */
    private final static class SAXWriter extends DefaultHandler
        implements
        LexicalHandler {
        private static final char[] OFF         = "                                                                                                        ".toCharArray();

        private Writer              w;

        private boolean             optimizeEmptyElements;

        private boolean             openElement = false;

        private int                 ident       = 0;

        /**
         * Creates <code>SAXWriter</code>.
         * 
         * @param w writer
         * @param optimizeEmptyElements if set to <code>true</code>, short
         *        XML syntax will be used for empty elements
         */
        public SAXWriter(final Writer w,
                         final boolean optimizeEmptyElements) {
            this.w = w;
            this.optimizeEmptyElements = optimizeEmptyElements;
        }

        public final void startElement(final String ns,
                                       final String localName,
                                       final String qName,
                                       final Attributes atts) throws SAXException {
            try {
                closeElement();

                writeIdent();
                this.w.write( "<".concat( qName ) );
                if ( atts != null && atts.getLength() > 0 ) {
                    writeAttributes( atts );
                }

                if ( !this.optimizeEmptyElements ) {
                    this.w.write( ">\n" );
                } else {
                    this.openElement = true;
                }
                this.ident += 2;

            } catch ( final IOException ex ) {
                throw new SAXException( ex );

            }
        }

        public final void endElement(final String ns,
                                     final String localName,
                                     final String qName) throws SAXException {
            this.ident -= 2;
            try {
                if ( this.openElement ) {
                    this.w.write( "/>\n" );
                    this.openElement = false;
                } else {
                    writeIdent();
                    this.w.write( "</" + qName + ">\n" );
                }

            } catch ( final IOException ex ) {
                throw new SAXException( ex );

            }
        }

        public final void endDocument() throws SAXException {
            try {
                this.w.flush();

            } catch ( final IOException ex ) {
                throw new SAXException( ex );

            }
        }

        public final void comment(final char[] ch,
                                  final int off,
                                  final int len) throws SAXException {
            try {
                closeElement();

                writeIdent();
                this.w.write( "<!-- " );
                this.w.write( ch,
                              off,
                              len );
                this.w.write( " -->\n" );

            } catch ( final IOException ex ) {
                throw new SAXException( ex );

            }
        }

        public final void startDTD(final String arg0,
                                   final String arg1,
                                   final String arg2) throws SAXException {
        }

        public final void endDTD() throws SAXException {
        }

        public final void startEntity(final String arg0) throws SAXException {
        }

        public final void endEntity(final String arg0) throws SAXException {
        }

        public final void startCDATA() throws SAXException {
        }

        public final void endCDATA() throws SAXException {
        }

        private final void writeAttributes(final Attributes atts) throws IOException {
            final StringBuffer sb = new StringBuffer();
            final int len = atts.getLength();
            for ( int i = 0; i < len; i++ ) {
                sb.append( " " ).append( atts.getLocalName( i ) ).append( "=\"" ).append( esc( atts.getValue( i ) ) ).append( "\"" );
            }
            this.w.write( sb.toString() );
        }

        /**
         * Encode string with escaping.
         * 
         * @param str string to encode.
         * @return encoded string
         */
        private final String esc(final String str) {
            final StringBuffer sb = new StringBuffer( str.length() );
            for ( int i = 0; i < str.length(); i++ ) {
                final char ch = str.charAt( i );
                switch ( ch ) {
                    case '&' :
                        sb.append( "&amp;" );
                        break;

                    case '<' :
                        sb.append( "&lt;" );
                        break;

                    case '>' :
                        sb.append( "&gt;" );
                        break;

                    case '\"' :
                        sb.append( "&quot;" );
                        break;

                    default :
                        if ( ch > 0x7f ) {
                            sb.append( "&#" ).append( Integer.toString( ch ) ).append( ';' );
                        } else {
                            sb.append( ch );
                        }

                }
            }
            return sb.toString();
        }

        private final void writeIdent() throws IOException {
            int n = this.ident;
            while ( n > 0 ) {
                if ( n > SAXWriter.OFF.length ) {
                    this.w.write( SAXWriter.OFF );
                    n -= SAXWriter.OFF.length;
                } else {
                    this.w.write( SAXWriter.OFF,
                                  0,
                                  n );
                    n = 0;
                }
            }
        }

        private final void closeElement() throws IOException {
            if ( this.openElement ) {
                this.w.write( ">\n" );
            }
            this.openElement = false;
        }

    }

    /**
     * A {@link org.xml.sax.ContentHandler ContentHandler} that splits XML
     * documents into smaller chunks. Each chunk is processed by the nested
     * {@link org.xml.sax.ContentHandler ContentHandler} obtained from
     * {@link java.net.ContentHandlerFactory ContentHandlerFactory}. This is
     * useful for running XSLT engine against large XML document that will
     * hardly fit into the memory all together. <p> TODO use complete path for
     * subdocumentRoot
     */
    private final static class InputSlicingHandler extends DefaultHandler {
        private String                subdocumentRoot;

        private ContentHandler        rootHandler;

        private ContentHandlerFactory subdocumentHandlerFactory;

        private boolean               subdocument = false;

        private ContentHandler        subdocumentHandler;

        /**
         * Constructs a new {@link InputSlicingHandler SubdocumentHandler}
         * object.
         * 
         * @param subdocumentRoot name/path to the root element of the
         *        subdocument
         * @param rootHandler content handler for the entire document
         *        (subdocument envelope).
         * @param subdocumentHandlerFactory a
         *        {@link ContentHandlerFactory ContentHandlerFactory} used to
         *        create {@link ContentHandler ContentHandler} instances for
         *        subdocuments.
         */
        public InputSlicingHandler(final String subdocumentRoot,
                                   final ContentHandler rootHandler,
                                   final ContentHandlerFactory subdocumentHandlerFactory) {
            this.subdocumentRoot = subdocumentRoot;
            this.rootHandler = rootHandler;
            this.subdocumentHandlerFactory = subdocumentHandlerFactory;
        }

        public final void startElement(final String namespaceURI,
                                       final String localName,
                                       final String qName,
                                       final Attributes list) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.startElement( namespaceURI,
                                                      localName,
                                                      qName,
                                                      list );
            } else if ( localName.equals( this.subdocumentRoot ) ) {
                this.subdocumentHandler = this.subdocumentHandlerFactory.createContentHandler();
                this.subdocumentHandler.startDocument();
                this.subdocumentHandler.startElement( namespaceURI,
                                                      localName,
                                                      qName,
                                                      list );
                this.subdocument = true;
            } else if ( this.rootHandler != null ) {
                this.rootHandler.startElement( namespaceURI,
                                               localName,
                                               qName,
                                               list );
            }
        }

        public final void endElement(final String namespaceURI,
                                     final String localName,
                                     final String qName) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.endElement( namespaceURI,
                                                    localName,
                                                    qName );
                if ( localName.equals( this.subdocumentRoot ) ) {
                    this.subdocumentHandler.endDocument();
                    this.subdocument = false;
                }
            } else if ( this.rootHandler != null ) {
                this.rootHandler.endElement( namespaceURI,
                                             localName,
                                             qName );
            }
        }

        public final void startDocument() throws SAXException {
            if ( this.rootHandler != null ) {
                this.rootHandler.startDocument();
            }
        }

        public final void endDocument() throws SAXException {
            if ( this.rootHandler != null ) {
                this.rootHandler.endDocument();

            }
        }

        public final void characters(final char[] buff,
                                     final int offset,
                                     final int size) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.characters( buff,
                                                    offset,
                                                    size );
            } else if ( this.rootHandler != null ) {
                this.rootHandler.characters( buff,
                                             offset,
                                             size );
            }
        }

    }

    /**
     * A {@link org.xml.sax.ContentHandler ContentHandler} that splits XML
     * documents into smaller chunks. Each chunk is processed by the nested
     * {@link org.xml.sax.ContentHandler ContentHandler} obtained from
     * {@link java.net.ContentHandlerFactory ContentHandlerFactory}. This is
     * useful for running XSLT engine against large XML document that will
     * hardly fit into the memory all together. <p> TODO use complete path for
     * subdocumentRoot
     */
    private static final class OutputSlicingHandler extends DefaultHandler {
        private String                subdocumentRoot;

        private ContentHandlerFactory subdocumentHandlerFactory;

        private EntryElement          entryElement;

        private boolean               isXml;

        private boolean               subdocument = false;

        private ContentHandler        subdocumentHandler;

        /**
         * Constructs a new {@link OutputSlicingHandler SubdocumentHandler}
         * object.
         * 
         * @param subdocumentHandlerFactory a
         *        {@link ContentHandlerFactory ContentHandlerFactory} used to
         *        create {@link ContentHandler ContentHandler} instances for
         *        subdocuments.
         * @param entryElement TODO.
         * @param isXml TODO.
         */
        public OutputSlicingHandler(final ContentHandlerFactory subdocumentHandlerFactory,
                                    final EntryElement entryElement,
                                    final boolean isXml) {
            this.subdocumentRoot = "class";
            this.subdocumentHandlerFactory = subdocumentHandlerFactory;
            this.entryElement = entryElement;
            this.isXml = isXml;
        }

        public final void startElement(final String namespaceURI,
                                       final String localName,
                                       final String qName,
                                       final Attributes list) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.startElement( namespaceURI,
                                                      localName,
                                                      qName,
                                                      list );
            } else if ( localName.equals( this.subdocumentRoot ) ) {
                final String name = list.getValue( "name" );
                if ( name == null || name.length() == 0 ) {
                    throw new SAXException( "Class element without name attribute." );
                }
                try {
                    this.entryElement.openEntry( this.isXml ? name.concat( ".class.xml" ) : name.concat( ".class" ) );
                } catch ( final IOException ex ) {
                    throw new SAXException( ex.toString(),
                                            ex );
                }
                this.subdocumentHandler = this.subdocumentHandlerFactory.createContentHandler();
                this.subdocumentHandler.startDocument();
                this.subdocumentHandler.startElement( namespaceURI,
                                                      localName,
                                                      qName,
                                                      list );
                this.subdocument = true;
            }
        }

        public final void endElement(final String namespaceURI,
                                     final String localName,
                                     final String qName) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.endElement( namespaceURI,
                                                    localName,
                                                    qName );
                if ( localName.equals( this.subdocumentRoot ) ) {
                    this.subdocumentHandler.endDocument();
                    this.subdocument = false;
                    try {
                        this.entryElement.closeEntry();
                    } catch ( final IOException ex ) {
                        throw new SAXException( ex.toString(),
                                                ex );
                    }
                }
            }
        }

        public final void startDocument() throws SAXException {
        }

        public final void endDocument() throws SAXException {
        }

        public final void characters(final char[] buff,
                                     final int offset,
                                     final int size) throws SAXException {
            if ( this.subdocument ) {
                this.subdocumentHandler.characters( buff,
                                                    offset,
                                                    size );
            }
        }

    }

    private static interface EntryElement {

        OutputStream openEntry(String name) throws IOException;

        void closeEntry() throws IOException;

    }

    private static final class SingleDocElement
        implements
        EntryElement {
        private OutputStream os;

        public SingleDocElement(final OutputStream os) {
            this.os = os;
        }

        public OutputStream openEntry(final String name) throws IOException {
            return this.os;
        }

        public void closeEntry() throws IOException {
            this.os.flush();
        }

    }

    private static final class ZipEntryElement
        implements
        EntryElement {
        private ZipOutputStream zos;

        public ZipEntryElement(final ZipOutputStream zos) {
            this.zos = zos;
        }

        public OutputStream openEntry(final String name) throws IOException {
            final ZipEntry entry = new ZipEntry( name );
            this.zos.putNextEntry( entry );
            return this.zos;
        }

        public void closeEntry() throws IOException {
            this.zos.flush();
            this.zos.closeEntry();
        }

    }

}
