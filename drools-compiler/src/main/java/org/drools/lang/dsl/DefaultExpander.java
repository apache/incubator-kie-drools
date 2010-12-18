package org.drools.lang.dsl;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.lang.Expander;
import org.drools.lang.ExpanderException;

/** 
 * The default expander uses String templates to provide pseudo natural language,
 * as well as general DSLs.
 * 
 * For most people, this should do the job just fine. 
 */
public class DefaultExpander
implements
Expander {
    private static final String ruleOrQuery = 
        "^(?:                         "             +  // alternatives rule...end, query...end
        "\\p{Blank}*(rule\\b.+?^\\s*when\\b)"  +  // 1: rule, name, attributes. when starts a line
        "(.*?)                       "         +  // 2: condition
        "^(\\s*then)                 "         +  // 3: then starts a line
        "(.*?)                       "         +  // 4: consequence
        "(^\\s*end.*?$)              "         +  // 5: end starts a line
        "|\\s*(query\\s+             "         +
        "(?:\"[^\"]+\"|'[^']+'|\\S+)"        +  
        "(?:\\s+\\([^)]+)?)        "         +  // 6: query, name, arguments
        "(.*?)                       "         +  // 7: condition
        "(^\\s*end.*?$)              "         +  // 8: end starts a line
        ")";

    private static final Pattern finder =
        Pattern.compile( ruleOrQuery,
                Pattern.DOTALL | Pattern.MULTILINE | Pattern.COMMENTS );

    // This pattern is used to find a pattern's constraint list
    private static final Pattern patternFinder = Pattern.compile( "\\((.*?)\\)" );

    // Pattern for finding a variable reference, restricted to Unicode letters and digits.
    private static final Pattern varRefPat = Pattern.compile( "\\{([\\p{L}\\d]+)\\}" );

    // Pattern for initial integer
    private static final Pattern intPat = Pattern.compile( "^(-?\\d+).*$" );
    
    
    private final List<DSLMappingEntry>   keywords      = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   condition     = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   consequence   = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   cleanup       = new LinkedList<DSLMappingEntry>();

    private List<Map<String,String>> substitutions;

    private List<ExpanderException>       errors        = Collections.emptyList();
    private boolean showResult  = false; 
    private boolean showSteps   = false;
    private boolean showWhen    = false;
    private boolean showThen    = false;
    private boolean showKeyword = false;

    /**
     * Creates a new DefaultExpander
     */
    public DefaultExpander() {
        this.cleanup.add( new AntlrDSLMappingEntry( DSLMappingEntry.KEYWORD,
                DSLMappingEntry.EMPTY_METADATA,
                "expander {name}",
                "",
                "expander (.*?)",
        "" ) );
    }

    /**
     * Add the new mapping to this expander.
     * @param mapping
     */
    public void addDSLMapping(final DSLMapping mapping) {
        for ( DSLMappingEntry entry : mapping.getEntries() ) {
            if ( DSLMappingEntry.KEYWORD.equals( entry.getSection() ) ) {
                this.keywords.add( entry );
            } else if ( DSLMappingEntry.CONDITION.equals( entry.getSection() ) ) {
                this.condition.add( entry );
            } else if ( DSLMappingEntry.CONSEQUENCE.equals( entry.getSection() ) ) {
                this.consequence.add( entry );
            } else {
                // if any, then add to them both condition and consequence
                this.condition.add( entry );
                this.consequence.add( entry );
            }
        }
        if( mapping.getOption( "result" ) )  showResult = true;
        if( mapping.getOption( "steps" ) )   showSteps = true;
        if( mapping.getOption( "keyword" ) ) showThen = true;
        if( mapping.getOption( "when" ) )    showWhen = true;
        if( mapping.getOption( "then" ) )    showThen = true;

    }

    /**
     * @inheritDoc
     * @throws IOException 
     */
    public String expand(final Reader drlReader) throws IOException {
        return this.expand( this.loadDrlFile( drlReader ) );
    }

    /**
     * @inheritDoc
     * @throws IOException 
     */
    public String expand(String drl) {
        drl = expandKeywords( drl );
        drl = cleanupExpressions( drl );
        final StringBuffer buf = expandConstructions( drl );

        if( showResult ){
            StringBuffer show = new StringBuffer();
            Formatter fmt = new Formatter( show );
            int offset = 0;
            int nlPos;
            int iLine = 1;
            while( (nlPos = buf.indexOf( "\n", offset ) ) >= 0 ){
                fmt.format( "%4d  %s", Integer.valueOf(iLine++), buf.substring( offset, nlPos + 1 ) );
                offset = nlPos + 1;
            }
            System.out.println( "=== DRL xpanded from DSLR ===" );
            System.out.println( show.toString() );
            System.out.println( "=============================" );
        }

        return buf.toString();
    }

    private static final String nl = System.getProperty( "line.separator" );

    private static int countNewlines( final String drl, int start, int end ){
        int count = 0;
        int pos = start;
        while( (pos = drl.indexOf( nl, pos )) >= 0 ){
            //      System.out.println( "pos at " + pos );
            if( pos >= end ) break;
            pos += nl.length();
            count++;
        }
        return count;
    }

    /**
     * Expand constructions like rules and queries
     * 
     * @param drl
     * @return
     */
    private StringBuffer expandConstructions(final String drl) {
        // display keys if requested
        if( showKeyword ){
            for( DSLMappingEntry entry: this.keywords ){
                System.out.println( "keyword: " + entry.getMappingKey() );
                System.out.println( "         " + entry.getKeyPattern() );
            }
        }
        if( showWhen ){
            for( DSLMappingEntry entry: this.condition ){
                System.out.println( "when: " + entry.getMappingKey() );
                System.out.println( "      " + entry.getKeyPattern() );
//                System.out.println( "      " + entry.getValuePattern() );
            }
        }
        if( showThen ){
            for( DSLMappingEntry entry: this.consequence ){
                System.out.println( "then: " + entry.getMappingKey() );
                System.out.println( "      " + entry.getKeyPattern() );
            }
        }

        // parse and expand specific areas
        final Matcher m = finder.matcher( drl );
        final StringBuffer buf = new StringBuffer();
        int drlPos = 0;
        int linecount = 0;
        while ( m.find() ) {
            final StringBuilder expanded = new StringBuilder();

            int newPos = m.start();
            linecount += countNewlines( drl, drlPos, newPos );
            drlPos = newPos;            

            String constr = m.group().trim();
            if( constr.startsWith( "rule" ) ){
                String headerFragment = m.group( 1 );
                expanded.append( headerFragment ); // adding rule header and attributes
                String lhsFragment = m.group( 2 );
                expanded.append(  this.expandLHS( lhsFragment, linecount + countNewlines( drl, drlPos, m.start(2) ) + 1 ) );
                String thenFragment = m.group( 3 );
                expanded.append( thenFragment );   // adding "then" header
                String rhsFragment = this.expandRHS( m.group( 4 ), linecount + countNewlines( drl, drlPos, m.start(4) ) + 1 );
                expanded.append( rhsFragment );
                expanded.append( m.group( 5 ) ); // adding rule trailer

            } else if( constr.startsWith( "query" ) ){
                String fragment = m.group( 6 );
                expanded.append( fragment ); // adding query header and attributes
                String lhsFragment = this.expandLHS( m.group( 7 ), linecount + countNewlines( drl, drlPos, m.start(7) ) + 1 );
                expanded.append( lhsFragment );
                expanded.append( m.group( 8 ) ); // adding query trailer

            } else {
                // strange behavior
                this.addError( new ExpanderException( "Unable to expand statement: " + constr, 0 ) );
            }
            m.appendReplacement( buf, Matcher.quoteReplacement( expanded.toString() ) );
        }
        m.appendTail( buf );
        return buf;
    }

    /**
     * Clean up constructions that exists only in the unexpanded code
     * 
     * @param drl
     * @return
     */
    private String cleanupExpressions(String drl) {
        // execute cleanup
        for ( final DSLMappingEntry entry : this.cleanup ) {
            drl = entry.getKeyPattern().matcher( drl ).replaceAll( entry.getValuePattern() );
        }
        return drl;
    }

    /**
     * Expand all configured keywords
     * 
     * @param drl
     * @return
     */
    private String expandKeywords(String drl) {
        // apply all keywords templates
        for ( final DSLMappingEntry entry : this.keywords ) {
            drl = entry.getKeyPattern().matcher( drl ).replaceAll( entry.getValuePattern() );
        }
        return drl;
    }

    /**
     * Perform the substitutions.
     * @param exp a DSLR source line to be expanded
     * @param entries the appropriate DSL keys and values 
     * @param line line number
     * @return the expanden line
     */
    private String substitute( String exp, List<DSLMappingEntry> entries, int line ){
        if( entries.size() == 0 ){
            this.addError( new ExpanderException( "No mapping entries for expanding: " + exp, line ) );
            return exp;
        }

        if( showSteps ){
            System.out.println("to expand: |" + exp + "|");
        }

        Map<String,String> key2value = new HashMap<String,String>();
        for ( final DSLMappingEntry entry : entries ) {
            Map<String,Integer> vars = entry.getVariables();
            String vp = entry.getValuePattern();
            Pattern kp = entry.getKeyPattern();
            Matcher m = kp.matcher( exp );
            int startPos = 0;
            boolean match = false;
            while( startPos < exp.length() && m.find( startPos ) ){
                match = true;
                if( showSteps ){
                    System.out.println("  matches: " + kp.toString() );
                }
                // Replace the range of group 0.  
                String target = m.group( 0 );
                if( ! vars.keySet().isEmpty() ){
                    // Build a pattern matching any variable enclosed in braces.
                    StringBuilder sb = new StringBuilder(  );
                    String del = "\\{(";
                    for( String key: vars.keySet() ){
                        sb.append( del ).append( Pattern.quote( key ) );
                        del = "|";
                    }
                    sb.append( ")(?:!(uc|lc|ucfirst|num|.*))?\\}" );                    
                    Pattern allkeyPat = Pattern.compile( sb.toString() );
                    Matcher allkeyMat = allkeyPat.matcher( vp );

                    // While the pattern matches, get the actual key and replace by '$' + index
                    while( allkeyMat.find() ){
                        String theKey = allkeyMat.group( 1 );
                        String theFunc = allkeyMat.group( 2 );
                        String theValue = m.group( vars.get( theKey ) );
                        if( theFunc != null ){
                            if( "uc".equals( theFunc ) ){
                                theValue = theValue.toUpperCase();
                            } else if( "lc".equals( theFunc ) ){
                                theValue = theValue.toLowerCase();
                            } else if( "ucfirst".equals( theFunc ) && theValue.length() > 0 ){
                                theValue = theValue.substring( 0, 1 ).toUpperCase() +
                                theValue.substring( 1 ).toLowerCase();
                            } else if( theFunc.startsWith( "num" ) ){
                                // kill all non-digits, but keep '-'
                                String numStr = theValue.replaceAll( "[^-\\d]+", "" );
                                try {
                                    long numLong = Long.parseLong( numStr );
                                    if( theValue.matches( "^.*[.,]\\d\\d(?:\\D.*|$)") ){
                                        numStr = Long.toString( numLong );
                                        theValue = numStr.substring( 0, numStr.length()-2) + '.' + numStr.substring(numStr.length()-2);
                                    } else {
                                        theValue = Long.toString( numLong );
                                    }
                                } catch( NumberFormatException nfe ){
                                    // silently ignore - keep the value as it is
                                }                               
                            } else {
                                StringTokenizer strTok = new StringTokenizer( theFunc, "?/", true );
                                boolean compare = true;
                                int toks = strTok.countTokens();
                                while( toks >= 4 ) {
                                    String key = strTok.nextToken();
                                    String qmk = strTok.nextToken(); // '?'
                                    String val = strTok.nextToken(); // to use
                                    String sep = strTok.nextToken(); // '/'
                                    if( key.equals( theValue ) ){
                                        theValue = val;
                                        break;
                                    }
                                    toks -= 4;
                                    if( toks < 4 ){
                                        theValue = strTok.nextToken();
                                        break;
                                    }
                                }

                            }
                        }
                        vp = vp.substring(0, allkeyMat.start()) + theValue + vp.substring( allkeyMat.end() );
                        allkeyMat.reset( vp );
                        key2value.put( theKey, theValue );
                    }
                }

                // Try to find any matches from previous lines.
                Matcher varRefMat = varRefPat.matcher( vp );               
                while( varRefMat.find() ){
                    String theKey = varRefMat.group( 1 );
                    for( int ientry = substitutions.size() - 1; ientry >= 0; ientry-- ){
                        String theValue = substitutions.get( ientry ).get( theKey );                
                        if( theValue != null ){
                            // replace it
                            vp = vp.substring(0, varRefMat.start()) + theValue + vp.substring( varRefMat.end() );
                            varRefMat.reset( vp );
                            break;
                        }
                    }
                }

                // add the new set of substitutions
                if( key2value.size() > 0 ){
                    substitutions.add( key2value );
                }

                // now replace the target
                exp = exp.substring(0, m.start()) + vp + exp.substring( m.end() );
                if( match && showSteps ){
                    System.out.println("   result: |" + exp + "|" );
                }
                startPos = m.start() + vp.length();
                m.reset( exp );
            }
        }
        return exp;
    }


    /**
     * Expand LHS for a construction
     * @param lhs
     * @param lineOffset 
     * @return
     */
    private String expandLHS( final String lhs, int lineOffset) {
        substitutions = new ArrayList<Map<String,String>>();

        //        System.out.println( "*** LHS>" + lhs + "<" );
        final StringBuilder buf = new StringBuilder();
        final String[] lines = lhs.split( "\n", -1 ); // since we assembled the string, we know line breaks are \n
        final String[] expanded = new String[lines.length]; // buffer for expanded lines
        int lastExpanded = -1;
        int lastPattern = -1;
        for ( int i = 0; i < lines.length - 1; i++ ) {
            final String trimmed = lines[i].trim();
            expanded[++lastExpanded] = lines[i];

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                // do nothing
            } else if ( trimmed.startsWith( ">" ) ) { // passthrough code
                // simply remove the passthrough mark character
                expanded[lastExpanded] = lines[i].replaceFirst( ">", " " );
            } else { // regular expansion
                // expand the expression
                expanded[lastExpanded] = substitute( expanded[lastExpanded], this.condition, i + lineOffset );

                // do we need to report errors for that?
                if ( lines[i].equals( expanded[lastExpanded] ) ) {
                    // report error
                    this.addError( new ExpanderException( "Unable to expand: " + lines[i].replaceAll( "[\n\r]",
                    "" ).trim(),
                    i + lineOffset ) );
                }
                // but if the original starts with a "-", it means we need to add it
                // as a constraint to the previous pattern
                if ( trimmed.startsWith( "-" ) && (!lines[i].equals( expanded[lastExpanded] )) ) {
                    int lastMatchStart = -1;
                    int lastMatchEnd = -1;
                    String constraints = "";
                    if ( lastPattern >= 0 ) {
                        final Matcher m2 = patternFinder.matcher( expanded[lastPattern] );
                        while ( m2.find() ) {
                            lastMatchStart = m2.start();
                            lastMatchEnd = m2.end();
                            constraints = m2.group( 1 ).trim();
                        }
                    }
                    if ( lastMatchStart > -1 ) {
                        // rebuilding previous pattern structure
                        expanded[lastPattern] = expanded[lastPattern].substring( 0,
                                lastMatchStart ) + "( " + constraints + ((constraints.length() == 0) ? "" : ", ") + expanded[lastExpanded].trim() + " )"
                                + expanded[lastPattern].substring( lastMatchEnd );
                    } else {
                        // error, pattern not found to add constraint to
                        this.addError( new ExpanderException( "No pattern was found to add the constraint to: " + lines[i].trim(),
                                i + lineOffset ) );
                    }
                    lastExpanded--;
                } else {
                    lastPattern = lastExpanded;
                }
            }
        }
        for ( int i = 0; i <= lastExpanded; i++ ) {
            buf.append( expanded[i] );
            buf.append( "\n" );
        }

        return buf.toString();
    }

    /**
     * Expand RHS for rules
     * 
     * @param lhs
     * @return
     */
    private String expandRHS(final String lhs, int lineOffset) {
        final StringBuilder buf = new StringBuilder();
        final String[] lines = lhs.split( "\n", -1 ); // since we assembled the string, we know line breaks are \n
        for ( int i = 0; i < lines.length -1; i++ ) {
            final String trimmed = lines[i].trim();

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                buf.append( lines[i] );
            } else if ( trimmed.startsWith( ">" ) ) { // passthrough code
                buf.append( lines[i].replaceFirst( ">",
                "" ) );
            } else { // regular expansions
                String expanded =  substitute( lines[i], this.consequence, i + lineOffset );
                buf.append( expanded );
                // do we need to report errors for that?
                if ( lines[i].equals( expanded ) ) {
                    // report error
                    this.addError( new ExpanderException( "Unable to expand: " + lines[i],
                            i + lineOffset ) );
                }
            }
            buf.append( "\n" );
        }
        if ( lines.length == 0 ) {
            buf.append( "\n" );
        }
        return buf.toString();
    }

    // Reads the stream into a String
    private String loadDrlFile(final Reader drl) throws IOException {
        final StringBuilder buf = new StringBuilder();
        final BufferedReader input = new BufferedReader( drl );
        String line = null;
        while ( (line = input.readLine()) != null ) {
            buf.append( line );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    private void addError(final ExpanderException error) {
        if ( this.errors == Collections.EMPTY_LIST ) {
            this.errors = new LinkedList<ExpanderException>();
        }
        this.errors.add( error );
    }

    /**
     * @inheritDoc
     */
    public List<ExpanderException> getErrors() {
        return this.errors;
    }

    /**
     * @inheritDoc
     */
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
}