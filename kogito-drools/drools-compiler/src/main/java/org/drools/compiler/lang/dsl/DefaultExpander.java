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

package org.drools.compiler.lang.dsl;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.ExpanderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * The default expander uses String templates to provide pseudo natural
 * language, as well as general DSLs.
 * 
 * For most people, this should do the job just fine.
 */
public class DefaultExpander
    implements
    Expander {

    protected static final transient Logger logger = LoggerFactory.getLogger(DefaultExpander.class);

    private static final String         ruleOrQuery  =
                                                                 "^(?:                         " + // alternatives rule...end, query...end
                                                                         "\\p{Blank}*(rule\\b.+?^\\s*when\\b)" + // 1: rule, name, attributes. when starts a line
                                                                         "(.*?)                       " + // 2: condition
                                                                         "^(\\s*then)                 " + // 3: then starts a line
                                                                         "(.*?)                       " + // 4: consequence
                                                                         "(^\\s*end.*?$)              " + // 5: end starts a line
                                                                         "|\\s*(query\\s+             " +
                                                                         "(?:\"[^\"]+\"|'[^']+'|\\S+) " +
                                                                         "(?:\\s*\\([^)]+\\))?)       " + // 6: query, name, arguments
                                                                         "(.*?)                       " + // 7: condition
                                                                         "(^\\s*end.*?$)              " + // 8: end starts a line
                                                                         ")";

    private static final Pattern        finder       =
                                                                 Pattern.compile( ruleOrQuery,
                                                                                  Pattern.DOTALL | Pattern.MULTILINE | Pattern.COMMENTS );

    private static final Pattern        comments     = Pattern.compile( "/\\*.*?\\*/", Pattern.DOTALL );

    // This pattern finds a statement's modify body
    private static final Pattern        modifyFinder = Pattern.compile( "\\{(.*?)\\}" );

    private static final String         funcPatStr   = "(?:!(uc|lc|ucfirst|num|.*))?";

    // Pattern for finding a variable reference, restricted to Unicode letters and digits.
    private static final Pattern        varRefPat    = Pattern.compile( "\\{([\\p{L}\\d]+)" + funcPatStr + "\\}" );

    // Pattern for initial integer
    private static final Pattern        intPat       = Pattern.compile( "^(-?\\d+).*$" );

    private final List<DSLMappingEntry> keywords     = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry> condition    = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry> consequence  = new LinkedList<DSLMappingEntry>();
    private final List<DSLMappingEntry> cleanup      = new LinkedList<DSLMappingEntry>();

    private Map<String, Integer>        useKeyword;
    private Map<String, Integer>        useWhen;
    private Map<String, Integer>        useThen;

    private List<Map<String, String>>   substitutions;

    private List<ExpanderException>     errors       = Collections.emptyList();
    private boolean                     showResult   = false;
    private boolean                     showSteps    = false;
    private boolean                     showWhen     = false;
    private boolean                     showThen     = false;
    private boolean                     showKeyword  = false;
    private boolean                     showUsage    = false;

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
     * 
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

        if ( mapping.getOption( "result" ) ) showResult = true;
        if ( mapping.getOption( "steps" ) ) showSteps = true;
        if ( mapping.getOption( "keyword" ) ) showKeyword = true;
        if ( mapping.getOption( "when" ) ) showWhen = true;
        if ( mapping.getOption( "then" ) ) showThen = true;
        if ( mapping.getOption( "usage" ) ) showUsage = true;
    }

    /**
     * @inheritDoc
     * @throws IOException
     */
    public String expand(final Reader drlReader) throws IOException {
        return this.expand( this.loadDrlFile( drlReader ) );
    }

    private void displayUsage(String what,
                              Map<String, Integer> use) {
        logger.info( "=== Usage of " + what + " ===" );
        Formatter fmt = new Formatter( System.out );
        for ( Map.Entry<String, Integer> entry : use.entrySet() ) {
            fmt.format( "%4d %s%n",
                        entry.getValue(),
                        entry.getKey() );
        }
    }

    /**
     * @inheritDoc
     * @throws IOException
     */
    public String expand(String drl) {

        if ( showUsage ) {
            useKeyword = new HashMap<String, Integer>();
            useWhen = new HashMap<String, Integer>();
            useThen = new HashMap<String, Integer>();
        }

        drl = removeComments( drl );
        drl = expandKeywords( drl );
        drl = cleanupExpressions( drl );
        final StringBuffer buf = expandConstructions( drl );

        if ( showUsage ) {
            displayUsage( "keyword",
                          useKeyword );
            displayUsage( "when",
                          useWhen );
            displayUsage( "then",
                          useThen );
        }

        if ( showResult ) {
            StringBuffer show = new StringBuffer();
            Formatter fmt = new Formatter( show );
            int offset = 0;
            int nlPos;
            int iLine = 1;
            while ( (nlPos = buf.indexOf( nl,
                                          offset )) >= 0 ) {
                fmt.format( "%4d  %s%n",
                            iLine++,
                            buf.substring( offset,
                                           nlPos ) );
                offset = nlPos + 1;
            }
            logger.info( "=== DRL xpanded from DSLR ===" );
            logger.info( show.toString() );
            logger.info( "=============================" );
        }

        return buf.toString();
    }

    private static final String nl = System.getProperty( "line.separator" );

    private static int countNewlines(final String drl,
                                     int start,
                                     int end) {
        int count = 0;
        int pos = start;
        while ( (pos = drl.indexOf( nl,
                                    pos )) >= 0 ) {
            //      logger.info( "pos at " + pos );
            if ( pos >= end ) break;
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
        if ( showKeyword ) {
            for ( DSLMappingEntry entry : this.keywords ) {
                logger.info( "keyword: " + entry.getMappingKey() );
                logger.info( "         " + entry.getKeyPattern() );
            }
        }
        if ( showWhen ) {
            for ( DSLMappingEntry entry : this.condition ) {
                logger.info( "when: " + entry.getMappingKey() );
                logger.info( "      " + entry.getKeyPattern() );
                //                logger.info( "      " + entry.getValuePattern() );
            }
        }
        if ( showThen ) {
            for ( DSLMappingEntry entry : this.consequence ) {
                logger.info( "then: " + entry.getMappingKey() );
                logger.info( "      " + entry.getKeyPattern() );
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
            linecount += countNewlines( drl,
                                        drlPos,
                                        newPos );
            drlPos = newPos;

            String constr = m.group().trim();
            if ( constr.startsWith( "rule" ) ) {
                String headerFragment = m.group( 1 );
                expanded.append( headerFragment ); // adding rule header and attributes
                String lhsFragment = m.group( 2 );
                expanded.append( this.expandLHS( lhsFragment,
                                                 linecount + countNewlines( drl,
                                                                            drlPos,
                                                                            m.start( 2 ) ) + 1 ) );
                String thenFragment = m.group( 3 );
                expanded.append( thenFragment ); // adding "then" header
                String rhsFragment = this.expandRHS( m.group( 4 ),
                                                     linecount + countNewlines( drl,
                                                                                drlPos,
                                                                                m.start( 4 ) ) + 1 );
                expanded.append( rhsFragment );
                expanded.append( m.group( 5 ) ); // adding rule trailer

            } else if ( constr.startsWith( "query" ) ) {
                String fragment = m.group( 6 );
                expanded.append( fragment ); // adding query header and attributes
                String lhsFragment = this.expandLHS( m.group( 7 ),
                                                     linecount + countNewlines( drl,
                                                                                drlPos,
                                                                                m.start( 7 ) ) + 1 );
                expanded.append( lhsFragment );
                expanded.append( m.group( 8 ) ); // adding query trailer

            } else {
                // strange behavior
                this.addError( new ExpanderException( "Unable to expand statement: " + constr,
                                                      0 ) );
            }
            m.appendReplacement( buf,
                                 Matcher.quoteReplacement( expanded.toString() ) );
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

    //    private int countAll( Matcher matcher ){
    //        int n = 0;
    //        while( matcher.find() ){
    //            n++;
    //        }
    //        return n;
    //    }

    private String removeComments(String drl) {
        return comments.matcher(drl).replaceAll("");
    }

    /**
     * Expand all configured keywords
     * 
     * @param drl
     * @return
     */
    private String expandKeywords(String drl) {
        substitutions = new ArrayList<Map<String, String>>();
        // apply all keywords templates
        drl = substitute( drl,
                          this.keywords,
                          0,
                          useKeyword,
                          false );
        substitutions = null;
        return drl;
    }

    private Pattern letterPat = Pattern.compile( "\\p{L}" );

    private String applyFunc(String theFunc,
                             String theValue) {
        if ( theFunc != null ) {
            if ( "uc".equals( theFunc ) ) {
                theValue = theValue.toUpperCase();
            } else if ( "lc".equals( theFunc ) ) {
                theValue = theValue.toLowerCase();
            } else if ( "ucfirst".equals( theFunc ) ) {
                Matcher letterMat = letterPat.matcher( theValue );
                if ( letterMat.find() ) {
                    int pos = letterMat.start();
                    theValue = theValue.substring( 0,
                                                   pos ) +
                               theValue.substring( pos,
                                                   pos + 1 ).toUpperCase() +
                               theValue.substring( pos + 1 ).toLowerCase();
                }
            } else if ( theFunc.startsWith( "num" ) ) {
                // kill all non-digits, but keep '-'
                String numStr = theValue.replaceAll( "[^-\\d]+",
                                                     "" );
                try {
                    long numLong = Long.parseLong( numStr );
                    if ( theValue.matches( "^.*[.,]\\d\\d(?:\\D.*|$)" ) ) {
                        numStr = Long.toString( numLong );
                        theValue = numStr.substring( 0,
                                                     numStr.length() - 2 ) + '.' + numStr.substring( numStr.length() - 2 );
                    } else {
                        theValue = Long.toString( numLong );
                    }
                } catch ( NumberFormatException nfe ) {
                    // silently ignore - keep the value as it is
                }
            } else {
                StringTokenizer strTok = new StringTokenizer( theFunc,
                                                              "?/",
                                                              true );
                boolean compare = true;
                int toks = strTok.countTokens();
                while ( toks >= 4 ) {
                    String key = strTok.nextToken();
                    String qmk = strTok.nextToken(); // '?'
                    String val = strTok.nextToken(); // to use
                    String sep = strTok.nextToken(); // '/'
                    if ( key.equals( theValue ) ) {
                        theValue = val;
                        break;
                    }
                    toks -= 4;
                    if ( toks < 4 ) {
                        theValue = strTok.nextToken();
                        break;
                    }
                }
            }
        }
        return theValue;
    }

    /**
     * Perform the substitutions.
     * 
     * @param exp
     *            a DSLR source line to be expanded
     * @param entries
     *            the appropriate DSL keys and values
     * @param line
     *            line number
     * @param use
     *            map for registering use
     * @return the expanden line
     */
    private String substitute(String exp,
                              List<DSLMappingEntry> entries,
                              int line,
                              Map<String, Integer> use,
                              boolean showSingleSteps) {
        if ( entries.size() == 0 ) {
            if ( line > 0 ) {
                this.addError( new ExpanderException( "No mapping entries for expanding: " + exp,
                                                      line ) );
            }
            return exp;
        }
        if ( showSingleSteps ) {
            logger.info( "to expand: |" + exp + "|" );
        }
        Map<String, String> key2value = new HashMap<String, String>();
        for ( final DSLMappingEntry entry : entries ) {
            Map<String, Integer> vars = entry.getVariables();
            String mappingKey = entry.getMappingKey();
            String vp = entry.getValuePattern();
            Pattern kp = entry.getKeyPattern();
            Matcher m = kp.matcher( exp );
            int startPos = 0;
            boolean match;
            Integer count;
            if ( showUsage ) {
                count = use.get( mappingKey );
                if ( count == null ) use.put( mappingKey, 0 );
            }
            while ( startPos < exp.length() && m.find( startPos ) ) {
                match = true;
                if ( showSingleSteps ) {
                    logger.info( "  matches: " + kp.toString() );
                }
                if ( showUsage ) {
                    use.put( mappingKey,
                             use.get( mappingKey ) + 1 );
                }
                // Replace the range of group 0.  
                String target = m.group( 0 );
                if ( !vars.keySet().isEmpty() ) {
                    // Build a pattern matching any variable enclosed in braces.
                    StringBuilder sb = new StringBuilder();
                    String del = "\\{(";
                    for ( String key : vars.keySet() ) {
                        sb.append( del ).append( Pattern.quote( key ) );
                        del = "|";
                    }
                    sb.append( ")" ).append( funcPatStr ).append( "\\}" );
                    Pattern allkeyPat = Pattern.compile( sb.toString() );
                    vp = entry.getValuePattern();
                    Matcher allkeyMat = allkeyPat.matcher( vp );

                    // While the pattern matches, get the actual key and replace by '$' + index
                    while ( allkeyMat.find() ) {
                        String theKey = allkeyMat.group( 1 );
                        String theFunc = allkeyMat.group( 2 );
                        String foundValue = m.group( vars.get( theKey ) );
                        String theValue = applyFunc( theFunc,
                                                     foundValue );
                        String newVp = vp.substring( 0,
                                                     allkeyMat.start() ) + theValue + vp.substring( allkeyMat.end() );
                        allkeyMat.reset( newVp );
                        key2value.put( theKey,
                                       foundValue );
                        if (newVp.equals(vp)) {
                            break;
                        } else {
                            vp = newVp;
                        }
                    }
                }

                // Try to find any matches from previous lines.
                Matcher varRefMat = varRefPat.matcher( vp );
                while ( varRefMat.find() ) {
                    String theKey = varRefMat.group( 1 );
                    String theFunc = varRefMat.group( 2 );
                    for ( int ientry = substitutions.size() - 1; ientry >= 0; ientry-- ) {
                        String foundValue = substitutions.get( ientry ).get( theKey );
                        if ( foundValue != null ) {
                            String theValue = applyFunc( theFunc,
                                                         foundValue );
                            // replace it
                            vp = vp.substring( 0,
                                               varRefMat.start() ) + theValue + vp.substring( varRefMat.end() );
                            varRefMat.reset( vp );
                            break;
                        }
                    }
                }

                // add the new set of substitutions
                if ( key2value.size() > 0 ) {
                    substitutions.add( key2value );
                }

                // now replace the target
                exp = exp.substring( 0,
                                     m.start() ) + vp + exp.substring( m.end() );
                if ( match && showSingleSteps ) {
                    logger.info( "   result: |" + exp + "|" );
                }
                startPos = m.start() + vp.length();
                m.reset( exp );
            }
        }
        return exp;
    }

    /**
     * Expand LHS for a construction
     * 
     * @param lhs
     * @param lineOffset
     * @return
     */
    private String expandLHS(final String lhs,
                             int lineOffset) {
        substitutions = new ArrayList<Map<String, String>>();

        //        logger.info( "*** LHS>" + lhs + "<" );
        final StringBuilder buf = new StringBuilder();
        final String[] lines = lhs.split( nl,
                                          -1 ); // since we assembled the string, we know line breaks are \n
        final String[] expanded = new String[lines.length]; // buffer for expanded lines
        int lastExpanded = -1;
        int lastPattern = -1;
        for ( int i = 0; i < lines.length - 1; i++ ) {
            final String trimmed = lines[i].trim();
            expanded[++lastExpanded] = lines[i];

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) {
                // comments - do nothing
            } else if ( trimmed.startsWith( ">" ) ) {
                // passthrough code - simply remove the passthrough mark character
                expanded[lastExpanded] = lines[i].replaceFirst( ">",
                                                                " " );
                lastPattern = lastExpanded;
            } else {
                // regular expansion - expand the expression
                expanded[lastExpanded] =
                        substitute( expanded[lastExpanded],
                                    this.condition,
                                    i + lineOffset,
                                    useWhen,
                                    showSteps );

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
                    if ( lastPattern >= 0 ) {
                        ConstraintInformation c = ConstraintInformation.findConstraintInformationInPattern( expanded[lastPattern] );
                        if ( c.start > -1 ) {
                            // rebuilding previous pattern structure
                            expanded[lastPattern] = expanded[lastPattern].substring( 0,
                                                                                     c.start ) + c.constraints + ((c.constraints.trim().length() == 0) ? "" : ", ") + expanded[lastExpanded].trim() + expanded[lastPattern].substring( c.end );
                        } else {
                            // error, pattern not found to add constraint to
                            this.addError( new ExpanderException( "No pattern was found to add the constraint to: " + lines[i].trim(),
                                                                  i + lineOffset ) );
                        }
                    }
                    lastExpanded--;
                } else {
                    lastPattern = lastExpanded;
                }
            }
        }
        for ( int i = 0; i <= lastExpanded; i++ ) {
            buf.append( expanded[i] );
            buf.append( nl );
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
        final String[] lines = lhs.split( nl,
                                          -1 ); // since we assembled the string, we know line breaks are \n

        final String[] expanded = new String[lines.length]; // buffer for expanded lines
        int lastExpanded = -1;
        int lastPattern = -1;
        for ( int i = 0; i < lines.length - 1; i++ ) {
            final String trimmed = lines[i].trim();
            expanded[++lastExpanded] = lines[i];

            if ( trimmed.length() == 0 || trimmed.startsWith( "#" ) || trimmed.startsWith( "//" ) ) { // comments
                buf.append( lines[i] );
            } else if ( trimmed.startsWith( ">" ) ) {
                // passthrough code
                expanded[lastExpanded] = lines[i].replaceFirst( ">",
                                                                " " );
            } else { // regular expansions
                expanded[lastExpanded] =
                        substitute( expanded[lastExpanded],
                                    this.consequence,
                                    i + lineOffset,
                                    useThen,
                                    showSteps );

                // do we need to report errors for that?
                if ( lines[i].equals( expanded[lastExpanded] ) ) {
                    // report error
                    this.addError( new ExpanderException( "Unable to expand: " + lines[i],
                                                          i + lineOffset ) );
                }

                // If the original starts with a "-", it means we need to add it
                // as a modify term to the previous pattern
                if ( trimmed.startsWith( "-" ) && (!lines[i].equals( expanded[lastExpanded] )) ) {
                    int lastMatchStart = -1;
                    int lastMatchEnd = -1;
                    String modifiers = "";
                    if ( lastPattern >= 0 ) {
                        final Matcher m2 = modifyFinder.matcher( expanded[lastPattern] );
                        while ( m2.find() ) {
                            lastMatchStart = m2.start();
                            lastMatchEnd = m2.end();
                            modifiers = m2.group( 1 ).trim();
                        }
                    }
                    if ( lastMatchStart > -1 ) {
                        // rebuilding previous modify structure
                        expanded[lastPattern] = expanded[lastPattern].substring( 0,
                                                                                 lastMatchStart )
                                                + "{ " + modifiers +
                                                ((modifiers.length() == 0) ? "" : ", ") +
                                                expanded[lastExpanded].trim() + " }" +
                                                expanded[lastPattern].substring( lastMatchEnd );
                    } else {
                        // error, pattern not found to add constraint to
                        this.addError( new ExpanderException( "No modify was found to add the modifier to: " + lines[i].trim(),
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
            buf.append( nl );
        }

        if ( lines.length == 0 ) {
            buf.append( nl );
        }
        return buf.toString();
    }

    // Reads the stream into a String
    private String loadDrlFile(final Reader drl) throws IOException {
        final StringBuilder buf = new StringBuilder();
        final BufferedReader input = new BufferedReader( drl );
        String line;
        while ( (line = input.readLine()) != null ) {
            buf.append( line );
            buf.append( nl );
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

    //Container for information relating to constraint substitution on LHS expansion 
    //where a DSL Sentence begins with "-". The pattern's constraints are deemed to
    //be between the first "(" and related matching ")". The constraint can include
    //other parenthesis.
    private static class ConstraintInformation {
        int    start       = -1;
        int    end         = -1;
        String constraints = "";

        static ConstraintInformation findConstraintInformationInPattern(String pattern) {
            ConstraintInformation ci = new ConstraintInformation();
            int bracketCount = 0;
            for ( int i = 0; i < pattern.length(); i++ ) {
                char c = pattern.charAt( i );
                if ( c == '(' ) {
                    if ( bracketCount == 0 ) {
                        ci.start = i + 1;
                    }
                    bracketCount++;
                }
                if ( c == ')' ) {
                    bracketCount--;
                    if ( bracketCount == 0 ) {
                        ci.end = i;
                        ci.constraints = pattern.substring( ci.start,
                                                            ci.end );
                        return ci;
                    }
                }
            }
            return ci;
        }
    }

}
