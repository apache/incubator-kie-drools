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

package org.drools.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.lang.DRLLexer;
import org.drools.lang.DRLParser;
import org.drools.lang.DroolsSentence;
import org.drools.lang.Expander;
import org.drools.lang.ExpanderException;
import org.drools.lang.Location;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpanderResolver;

/**
 * This is a low level parser API. This will return textual AST representations
 * of the DRL source, including with DSL expanders if appropriate.
 */
public class DrlParser {

    // TODO: REMOVE THIS GENERIC MESSAGE ASAP
    private static final String     GENERIC_ERROR_MESSAGE = "Unexpected exception raised while parsing. This is a bug. Please contact the Development team :\n";
    private final List<DroolsError> results               = new ArrayList<DroolsError>();
    private List<DroolsSentence>    editorSentences       = null;
    private Location                location              = new Location( Location.LOCATION_UNKNOWN );
    private DRLLexer                lexer                 = null;

    public DrlParser() {
    }

    /** Parse a rule from text */
    public PackageDescr parse(final String text) throws DroolsParserException {
        return parse( false,
                      text );
    }

    public PackageDescr parse(final boolean isEditor,
                              final String text) throws DroolsParserException {
        final DRLParser parser = getParser( text );
        return compile( isEditor,
                        parser );
    }

    public PackageDescr parse(final boolean isEditor,
                              final Reader reader) throws DroolsParserException {
        final DRLParser parser = getParser( reader );
        return compile( isEditor,
                        parser );
    }

    public PackageDescr parse(final Reader reader) throws DroolsParserException {
        return parse( false,
                      reader );
    }

    /**
     * Parse and build a rule package from a DRL source with a domain specific
     * language.
     */
    public PackageDescr parse(final Reader drl,
                              final Reader dsl) throws DroolsParserException,
                                               IOException {
        return parse( false,
                      drl,
                      dsl );
    }

    public PackageDescr parse(boolean isEditor,
                              final Reader drl,
                              final Reader dsl) throws DroolsParserException,
                                               IOException {
        final StringBuilder text = getDRLText( drl );
        return parse( text.toString(),
                      dsl );
    }

    /**
     * Parse and build a rule package from a DRL source with a domain specific
     * language.
     * 
     * @param source
     *            As Text.
     * @param dsl
     * @return
     * @throws DroolsParserException
     */
    public PackageDescr parse(boolean isEditor,
                              final String source,
                              final Reader dsl) throws DroolsParserException {
        DefaultExpanderResolver resolver = getDefaultResolver( dsl );

        final Expander expander = resolver.get( "*",
                                                null );
        final String expanded = expander.expand( source );
        if ( expander.hasErrors() ) {
            this.results.addAll( expander.getErrors() );
        }
        return this.parse( isEditor,
                           expanded );
    }

    public PackageDescr parse(final String source,
                              final Reader dsl) throws DroolsParserException {
        return this.parse( false,
                           source,
                           dsl );
    }

    public PackageDescr parse(final boolean isEditor,
                              final InputStream is) throws DroolsParserException {
        final DRLParser parser = getParser( is );
        return compile( isEditor,
                        parser );
    }

    public PackageDescr parse(final InputStream is) throws DroolsParserException {
        return parse( false,
                      is );
    }

    /**
     * This will expand the DRL. useful for debugging.
     * 
     * @param source -
     *            the source which use a DSL
     * @param dsl -
     *            the DSL itself.
     * @throws DroolsParserException
     *             If unable to expand in any way.
     */
    public String getExpandedDRL(final String source,
                                 final Reader dsl) throws DroolsParserException {
        DefaultExpanderResolver resolver = getDefaultResolver( dsl );
        return getExpandedDRL( source,
                               resolver );
    }

    /**
     * This will expand the DRL using the given expander resolver. useful for
     * debugging.
     * 
     * @param source -
     *            the source which use a DSL
     * @param resolver -
     *            the DSL expander resolver itself.
     * @throws DroolsParserException
     *             If unable to expand in any way.
     */
    public String getExpandedDRL(final String source,
                                 final DefaultExpanderResolver resolver) throws DroolsParserException {

        final Expander expander = resolver.get( "*",
                                                null );
        final String expanded = expander.expand( source );
        if ( expander.hasErrors() ) {
            String err = "";
            for ( ExpanderException ex : expander.getErrors() ) {
                err = err + "\n Line:[" + ex.getLine() + "] " + ex.getMessage();

            }
            throw new DroolsParserException( err );
        }
        return expanded;
    }

    private StringBuilder getDRLText(final Reader reader) throws IOException {
        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text;
    }

    /**
     * @return true if there were parser errors.
     */
    public boolean hasErrors() {
        return !this.results.isEmpty();
    }

    /**
     * @return a list of errors found while parsing. DroolsError: either ParserError, or ExpanderException
     */
    public List<DroolsError> getErrors() {
        return this.results;
    }

    private PackageDescr compile(boolean isEditor,
                                  final DRLParser parser ) throws DroolsParserException {
        PackageDescr pkgDescr = null;
        try {
            if ( isEditor ) {
                parser.enableEditorInterface();
            }
            pkgDescr = parser.compilationUnit();
            editorSentences = parser.getEditorInterface();
            makeErrorList( parser );
            if ( isEditor || !this.hasErrors() ) {
                return pkgDescr;
            } else {
                return null;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            final ParserError err = new ParserError( GENERIC_ERROR_MESSAGE + e.toString()+"\n"+ Arrays.toString( e.getStackTrace() ),
                                                     -1,
                                                     0 );
            this.results.add( err );
            if ( isEditor ) {
                return pkgDescr;
            } else {
                throw new DroolsParserException( GENERIC_ERROR_MESSAGE + e.getMessage(),
                                                 e );
            }
        }
    }

    /** Convert the antlr exceptions to drools parser exceptions */
    private void makeErrorList( final DRLParser parser ) {
        for ( final DroolsParserException recogErr : lexer.getErrors() ) {
            final ParserError err = new ParserError( recogErr.getMessage(),
                                                     recogErr.getLineNumber(),
                                                     recogErr.getColumn() );
            this.results.add( err );
        }
        for ( final DroolsParserException recogErr : parser.getErrors() ) {
            final ParserError err = new ParserError( recogErr.getMessage(),
                                                     recogErr.getLineNumber(),
                                                     recogErr.getColumn() );
            this.results.add( err );
        }
    }

    /**
     * @return An instance of a RuleParser should you need one (most folks will
     *         not).
     */
    private DRLParser getParser( final String text ) {
        lexer = new DRLLexer( new ANTLRStringStream( text ) );
        DRLParser parser = new DRLParser( new CommonTokenStream( lexer ) );
        return parser;
    }

    private DRLParser getParser( final Reader reader ) {
        try {
            lexer = new DRLLexer( new ANTLRReaderStream( reader ) );
            DRLParser parser = new DRLParser( new CommonTokenStream( lexer ) );
            return parser;
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader",
                                        e );
        }
    }

    private DRLParser getParser( final InputStream is ) {
        try {
            lexer = new DRLLexer( new ANTLRInputStream( is ) );
            DRLParser parser = new DRLParser( new CommonTokenStream( lexer ) );
            return parser;
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader",
                                        e );
        }
    }

    public Location getLocation() {
        return this.location;
    }

    public DefaultExpanderResolver getDefaultResolver(final Reader dsl) throws DroolsParserException {
        DefaultExpanderResolver resolver;
        try {
            resolver = new DefaultExpanderResolver( dsl );
        } catch ( final IOException e ) {
            throw new DroolsParserException( "Error parsing the DSL.",
                                             e );
        }
        return resolver;
    }

    public List<DroolsSentence> getEditorSentences() {
        return editorSentences;
    }
}
