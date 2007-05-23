package org.drools.compiler;

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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.drools.lang.DRLLexer;
import org.drools.lang.DRLParser;
import org.drools.lang.Expander;
import org.drools.lang.Location;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpanderResolver;

/**
 * This is a low level parser API. This will return textual AST representations
 * of the DRL source, including with DSL expanders if appropriate.
 */
public class DrlParser {

    private final List results = new ArrayList();
    private Location location = null;

    public DrlParser() {

    }

    /** Parse a rule from text */
    public PackageDescr parse(final String text) throws DroolsParserException {
        final DRLParser parser = getParser( text );
        compile( parser );
        this.location = parser.getLocation();
        return parser.getPackageDescr();

    }

    public PackageDescr parse(final Reader reader) throws DroolsParserException {
        final DRLParser parser = getParser( reader );
        compile( parser );
        this.location = parser.getLocation();
        return parser.getPackageDescr();

    }

    //    /** Parse and build a rule package from a DRL source */
    //    public PackageDescr parse(final Reader reader) throws IOException,
    //                                            DroolsParserException {
    //        DRLParser parser = 
    //        
    //        //final StringBuffer text = getDRLText( reader );
    //        //return parse( text.toString() );
    //    }

    /** 
     * Parse and build a rule package from a DRL source with a 
     * domain specific language.
     */
    public PackageDescr parse(final Reader drl,
                              final Reader dsl) throws DroolsParserException,
                                               IOException {
        final StringBuffer text = getDRLText( drl );
        return parse( text.toString(),
                      dsl );
    }

    /**
     * Parse and build a rule package from a DRL source with a domain specific language.
     * @param source As Text.
     * @param dsl 
     * @return
     * @throws DroolsParserException
     */
    public PackageDescr parse(final String source,
                              final Reader dsl) throws DroolsParserException {
        DefaultExpanderResolver resolver;
        try {
            resolver = new DefaultExpanderResolver( dsl );
        } catch ( final IOException e ) {
            throw new DroolsParserException( "Error parsing the DSL.",
                                             e );
        }
        final Expander expander = resolver.get( "*",
                                          null );
        final String expanded = expander.expand( source );
        if ( expander.hasErrors() ) {
            this.results.addAll( expander.getErrors() );
        }
        return this.parse( expanded );
    }

    private StringBuffer getDRLText(final Reader reader) throws IOException {
        final StringBuffer text = new StringBuffer();

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
        return this.results.size() > 0;
    }

    /**
     * @return a list of ParserError's.
     */
    public List getErrors() {
        return this.results;
    }

    private void compile(final DRLParser parser) throws DroolsParserException {
        try {
            parser.compilation_unit();

            makeErrorList( parser );
        } catch ( final RecognitionException e ) {
            throw new DroolsParserException( e );
        }
    }

    /** Convert the antlr exceptions to drools parser exceptions */
    private void makeErrorList(final DRLParser parser) {
        for ( final Iterator iter = parser.getErrors().iterator(); iter.hasNext(); ) {
            final RecognitionException recogErr = (RecognitionException) iter.next();
            final ParserError err = new ParserError( parser.createErrorMessage( recogErr ),
                                                     recogErr.line,
                                                     recogErr.charPositionInLine );
            this.results.add( err );
        }
    }

    /**
     * @return An instance of a RuleParser should you need one (most folks will not).
     */
    private DRLParser getParser(final String text) {
        return new DRLParser( new SwitchingCommonTokenStream( new DRLLexer( new ANTLRStringStream( text ) ) ) );
    }

    private DRLParser getParser(final Reader reader) {
        try {
            return new DRLParser( new SwitchingCommonTokenStream( new DRLLexer( new ANTLRReaderStream( reader ) ) ) );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader",
                                        e );
        }
    }
    
    public Location getLocation() {
        return this.location; 
    }
}