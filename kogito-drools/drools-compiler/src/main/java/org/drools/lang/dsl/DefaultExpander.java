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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.DroolsError;
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
    private static final String rulesExpr = "(^\\s*rule.*?$.*?^\\s*when.*?$)(.*?)(^\\s*then.*?$)(.*?)(^\\s*end)";
    // bellow regexp is used to find and parse query parts: header, condition, trailer
    private static final String queryExpr = "(^\\s*query.*?$)(.*?)(^\\s*end)";
    
    // bellow we combine and compile above expressions into a pattern object
    private static final Pattern finder = Pattern.compile( "("+rulesExpr+"|"+queryExpr+")" , Pattern.DOTALL | Pattern.MULTILINE );
    // bellow pattern is used to find a column's constraint list
    private static final Pattern columnFinder = Pattern.compile( "\\((.*?)\\)" );
    
    private Map     mappings    = new HashMap();
    private List    keywords    = new LinkedList();
    private List    condition   = new LinkedList();
    private List    consequence = new LinkedList();
    private List    cleanup     = new LinkedList();
    
    private List    errors      = Collections.EMPTY_LIST;

    /**
     * Creates a new DefaultExpander
     */
    public DefaultExpander() {
        this.cleanup.add( new DefaultDSLMappingEntry(DSLMappingEntry.KEYWORD,
                          null,
                          "expander {name}",
                          ""));
    }

    /**
     * Add the new mapping to this expander.
     * @param mapping
     */
    public void addDSLMapping(DSLMapping mapping) {
        this.mappings.put( mapping.getIdentifier(),
                           mapping );
        for ( Iterator it = mapping.getEntries().iterator(); it.hasNext(); ) {
            DSLMappingEntry entry = (DSLMappingEntry) it.next();
            if ( entry.getSection() == DSLMappingEntry.KEYWORD ) {
                this.keywords.add( entry );
            } else if ( entry.getSection() == DSLMappingEntry.CONDITION ) {
                this.condition.add( entry );
            } else if ( entry.getSection() == DSLMappingEntry.CONSEQUENCE ) {
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
        StringBuffer buf = expandConstructions( drl );
        return buf.toString();
    }

    /**
     * Expand constructions like rules and queries
     * 
     * @param drl
     * @return
     */
    private StringBuffer expandConstructions(String drl) {
        // parse and expand specific areas
        Matcher m = finder.matcher( drl );
        StringBuffer buf = new StringBuffer();
        while( m.find() ) {
            StringBuffer expanded = new StringBuffer();
            String constr = m.group( 1 ).trim();
            if( constr.startsWith( "rule" ) ) {
                // match rule
                expanded.append( m.group( 2 ) ); // adding rule header and attributes
                expanded.append( this.expandLHS( m.group( 3 ) ) ); // adding expanded LHS
                expanded.append( m.group( 4 ) ); // adding "then" header
                expanded.append( this.expandRHS( m.group( 5 ) ) ); // adding expanded RHS
                expanded.append( m.group( 6 ) ); // adding rule trailer
                expanded.append( "\n" );
            } else if( constr.startsWith( "query" ) ) {
                // match query
                expanded.append( m.group( 7 ) ); // adding query header and attributes
                expanded.append( this.expandLHS( m.group( 8 ) ) ); // adding expanded LHS
                expanded.append( m.group( 9 ) ); // adding query trailer
                expanded.append( "\n" );
            } else {
                // strange behavior
                this.addError( new ExpanderException("Unable to expand statement: "+constr, 0) );
            }
            m.appendReplacement( buf, expanded.toString().replaceAll( "\\$", "\\\\\\$" ) );
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
        for( Iterator it = this.cleanup.iterator(); it.hasNext(); ) {
            DSLMappingEntry entry = (DSLMappingEntry) it.next();
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
        for( Iterator it = this.keywords.iterator(); it.hasNext(); ) {
            DSLMappingEntry entry = (DSLMappingEntry) it.next();
            drl = entry.getKeyPattern().matcher( drl ).replaceAll( entry.getValuePattern() );
        }
        return drl;
    }

    /**
     * Expand LHS for a construction
     * @param lhs
     * @return
     */
    private String expandLHS(String lhs) {
        StringBuffer buf = new StringBuffer();
        String[] lines = lhs.split( "\n" ); // since we assembled the string, we know line breaks are \n
        String[] expanded = new String[lines.length]; // buffer for expanded lines
        int lastExpanded = -1;
        int lastColumn = -1;
        for( int i = 0; i < lines.length; i++ ){
            String trimmed = lines[i].trim();
            expanded[++lastExpanded] = lines[i];
            
            if( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                // do nothing
            } else if( trimmed.startsWith( ">" ) ) { // passthrough code
                // simply remove the passthrough mark character
                expanded[lastExpanded] =  lines[i].replaceFirst( ">", " " );
            } else { // regular expansion
                // expand the expression
                for( Iterator it = this.condition.iterator(); it.hasNext(); ) {
                    DSLMappingEntry entry = (DSLMappingEntry) it.next();
                    expanded[lastExpanded] = entry.getKeyPattern().matcher( expanded[lastExpanded] ).replaceAll( entry.getValuePattern() ); 
                }

                // do we need to report errors for that?
                if( lines[i].equals( expanded[lastExpanded] ) ) {
                    // report error
                    this.addError( new ExpanderException("Unable to expand: ["+lines[i]+"]", i) );
                }
                // but if the original starts with a "-", it means we need to add it
                // as a constraint to the previous column
                if( trimmed.startsWith( "-" ) && (! lines[i].equals( expanded[lastExpanded] )) ) {
                    int lastMatchStart = -1;
                    int lastMatchEnd = -1;
                    String constraints = "";
                    if( lastColumn >= 0) {
                        Matcher m2 = columnFinder.matcher( expanded[lastColumn] );
                        while(m2.find()) {
                            lastMatchStart = m2.start();
                            lastMatchEnd = m2.end();
                            constraints = m2.group(1).trim();
                        }
                    }
                    if( lastMatchStart > -1 ) {
                        // rebuilding previous column structure
                        expanded[lastColumn] = expanded[lastColumn].substring( 0, lastMatchStart ) +
                                               "( "+constraints+((constraints.length()==0)?"":", ")+expanded[lastExpanded].trim()+" )" +
                                               expanded[lastColumn].substring( lastMatchEnd );
                    } else {
                        // error, column not found to add constraint to
                        // TODO: can we report character position?
                        this.addError( new ExpanderException("No column was found to add the constraint to: "+lines[i], i) );
                    }
                    lastExpanded--;
                } else {
                    lastColumn = lastExpanded;
                }
            }
        }
        for( int i = 0; i <= lastExpanded; i++ ) {
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
    private String expandRHS(String lhs) {
        StringBuffer buf = new StringBuffer();
        String[] lines = lhs.split( "\n" ); // since we assembled the string, we know line breaks are \n
        for( int i = 0; i < lines.length; i++ ){
            String trimmed = lines[i].trim();
            
            if( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                buf.append( lines[i] );
            } else if( trimmed.startsWith( ">" ) ) { // passthrough code
                buf.append( lines[i].replaceFirst( ">", "" ) );
            } else { // regular expansions
                String expanded = lines[i];
                for( Iterator it = this.consequence.iterator(); it.hasNext(); ) {
                    DSLMappingEntry entry = (DSLMappingEntry) it.next();
                    expanded = entry.getKeyPattern().matcher( expanded ).replaceAll( entry.getValuePattern() ); 
                }
                buf.append( expanded );
                // do we need to report errors for that?
                if( lines[i].equals( expanded ) ) {
                    // report error
                    this.addError( new ExpanderException("Unable to expand: "+lines[i], i) );
                }
            }
            buf.append( "\n" );
        }
        
        return buf.toString();
    }

    // Reads the stream into a String
    private String loadDrlFile( Reader drl ) throws IOException {
        StringBuffer buf = new StringBuffer();
        BufferedReader input = new BufferedReader( drl );
        String line = null;
        while( (line = input.readLine()) != null ) {
            buf.append( line );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    private void addError( DroolsError error ) {
        if( this.errors == Collections.EMPTY_LIST ) {
            this.errors = new LinkedList();
        }
        this.errors.add( error );
    }
    
    /**
     * @inheritDoc
     */
    public List getErrors() {
        return this.errors;
    }

    /**
     * @inheritDoc
     */
    public boolean hasErrors() {
        return ! this.errors.isEmpty();
    }


}