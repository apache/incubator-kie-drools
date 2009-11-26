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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    // Be EXTREMELLY careful if you decide to change bellow regexp's
    //
    // bellow regexp is used to find and parse rule parts: header, LHS, RHS, trailer, etc
    private static final String           rulesExpr     = "(^\\s*rule.*?$.*?^\\s*when.*?)$(.*?)(^\\s*then.*?$)(.*?)(^\\s*end)";
    // bellow regexp is used to find and parse query parts: header, condition, trailer
    private static final String           queryExpr     = "(^\\s*query.*?)$(.*?)(^\\s*end)";

    // bellow we combine and compile above expressions into a pattern object
    private static final Pattern          finder        = Pattern.compile( "(" + rulesExpr + "|" + queryExpr + ")",
                                                                           Pattern.DOTALL | Pattern.MULTILINE );
    // bellow pattern is used to find a pattern's constraint list
    private static final Pattern          patternFinder = Pattern.compile( "\\((.*?)\\)" );

    private final Map<String, DSLMapping> mappings      = new HashMap<String, DSLMapping>();
    private final List<DSLMappingEntry>   keywords      = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   condition     = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   consequence   = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry>   cleanup       = new LinkedList<DSLMappingEntry>();

    private List<ExpanderException>       errors        = Collections.emptyList();

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
        this.mappings.put( mapping.getIdentifier(),
                           mapping );
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
        return buf.toString();
    }

    /**
     * Expand constructions like rules and queries
     * 
     * @param drl
     * @return
     */
    private StringBuffer expandConstructions(final String drl) {
        // parse and expand specific areas
        final Matcher m = finder.matcher( drl );

        final StringBuffer buf = new StringBuffer();
        while ( m.find() ) {
            final StringBuilder expanded = new StringBuilder();
            final String constr = m.group( 1 ).trim();
            if ( constr.startsWith( "rule" ) ) {
                // match rule
                String headerFragment = m.group( 2 );
                expanded.append( headerFragment ); // adding rule header and attributes
                String lhsFragment = m.group( 3 );
                expanded.append( this.expandLHS( lhsFragment,
                                                 countNewlines( headerFragment ) + 1 ) ); // adding expanded LHS
                String thenFragment = m.group( 4 );

                expanded.append( thenFragment ); // adding "then" header
                expanded.append( this.expandRHS( m.group( 5 ),
                                                 countNewlines( headerFragment + lhsFragment + thenFragment ) + 1 ) ); // adding expanded RHS
                expanded.append( m.group( 6 ) ); // adding rule trailer
                expanded.append( "\n" );
            } else if ( constr.startsWith( "query" ) ) {
                // match query
                String fragment = m.group( 7 );
                expanded.append( fragment ); // adding query header and attributes
                expanded.append( this.expandLHS( m.group( 8 ),
                                                 countNewlines( fragment ) + 1 ) ); // adding expanded LHS
                expanded.append( m.group( 9 ) ); // adding query trailer
                expanded.append( "\n" );
            } else {
                // strange behavior
                this.addError( new ExpanderException( "Unable to expand statement: " + constr,
                                                      0 ) );
            }
            m.appendReplacement( buf,
                                 expanded.toString().replaceAll( "\\$",
                                                                 "\\\\\\$" ) );
        }
        m.appendTail( buf );
        return buf;
    }

    private int countNewlines(final String drl) {
        char[] cs = drl.toCharArray();
        int count = 0;
        for ( int i = 0; i < cs.length; i++ ) {
            {
                if ( cs[i] == '\n' ) count++;
            }
        }
        return count;
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
     * Expand LHS for a construction
     * @param lhs
     * @param lineOffset 
     * @return
     */
    private String expandLHS(final String lhs,
                             int lineOffset) {
        final StringBuilder buf = new StringBuilder();
        final String[] lines = lhs.split( "\n" ); // since we assembled the string, we know line breaks are \n
        final String[] expanded = new String[lines.length]; // buffer for expanded lines
        int lastExpanded = -1;
        int lastPattern = -1;
        for ( int i = 0; i < lines.length; i++ ) {
            final String trimmed = lines[i].trim();
            expanded[++lastExpanded] = lines[i];

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                // do nothing
            } else if ( trimmed.startsWith( ">" ) ) { // passthrough code
                // simply remove the passthrough mark character
                expanded[lastExpanded] = lines[i].replaceFirst( ">",
                                                                " " );
            } else { // regular expansion
                // expand the expression
                for ( final DSLMappingEntry entry : this.condition ) {
                    String vp = entry.getValuePattern();
                    //                    System.out.println("toExpand, st: " + expanded[lastExpanded] + "|");
                    //                    System.out.println("kp: " + entry.getKeyPattern());
                    //                    System.out.println("vp: " + vp);
                    expanded[lastExpanded] = entry.getKeyPattern().matcher( expanded[lastExpanded] ).replaceAll( vp );
                }

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
    private String expandRHS(final String lhs,
                             int lineOffset) {
        final StringBuilder buf = new StringBuilder();
        final String[] lines = lhs.split( "\n" ); // since we assembled the string, we know line breaks are \n
        for ( int i = 0; i < lines.length; i++ ) {
            final String trimmed = lines[i].trim();

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                buf.append( lines[i] );
            } else if ( trimmed.startsWith( ">" ) ) { // passthrough code
                buf.append( lines[i].replaceFirst( ">",
                                                   "" ) );
            } else { // regular expansions
                String expanded = lines[i];
                for ( final DSLMappingEntry entry : this.consequence ) {
                    expanded = entry.getKeyPattern().matcher( expanded ).replaceAll( entry.getValuePattern() );
                }
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