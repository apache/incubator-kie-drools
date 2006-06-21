package org.drools.lang.dsl;

import java.util.StringTokenizer;

import org.drools.lang.Expander;

/**
 * This is a simple line based expander front end for the DRL parser.
 * Kind of a micro parser in itself.
 * This tries to keep the whitespace and lines intact, but it is not 
 * guaranteed to preserve the exact spacing or line numbers.
 * 
 * TODO: To replace the in-parser implementation in 3.1 +
 * 
 * @author Michael Neale
 */
public class LineBasedExpander {

    private String       source;
    private StringBuffer output     = new StringBuffer();
    private boolean      lhs;
    private boolean      rhs;
    private int          lineNumber = 0;
    private Expander     expander;

    /** Pass in the unexpanded rule(s), and and the expander to apply */
    public LineBasedExpander(String rawSource,
                             Expander exp) {
        source = rawSource;
        this.expander = exp;
    }

    /**
     * This will apply the expander. And return the result.
     */
    public String expand() {
        StringTokenizer st = new StringTokenizer( source,
                                                  "\r\n" );

        while ( st.hasMoreTokens() ) {
            lineNumber++;
            String raw = st.nextToken();
            String line = raw.trim();

                if ( matchesKeyword( "when",
                                     line ) ) {
                    lhs();
                    appendLine( raw );
                } else if ( matchesKeyword( "then",
                                            line ) ) {
                    rhs();
                    appendLine( raw );
                } else if ( matchesKeyword( "end",
                                            line ) ) {
                    endRule();
                    appendLine( raw );
                    output.append( "\n" );
                } else if ( matchesKeyword( "query",
                                            line ) ) {
                    query();
                    appendLine( raw );
                } else {
                    consume( raw );
                }
        }
        return output.toString();

    }

    private void appendLine(String raw) {
        output.append( raw );
        output.append( "\n" );
    }
    
    /**
     * @return The expanded rule(s).
     */
    public String getExpanded() {
        return output.toString();
    }

    private void consume(String raw) {
        if ( lhs ) {
            appendLine( expand( "when",
                                raw ) );
        } else if ( rhs ) {
            appendLine( expand( "then",
                                raw ) );
        } else {
            appendLine( raw );

        }

    }

    private String expand(String scope,
                          String raw) {
        String trimmed = raw.trim();

        if ( trimmed.startsWith( ">" ) ) {
            return raw.substring( 1 );
        } else {
            return "\t\t" + //some space to make it look purrty 
                   expander.expand( scope,
                                    normaliseSpaces( raw ) );
        }
    }

    /**
     * This will match the token, ignoring any single line comments.
     */
    boolean matchesKeyword(String token,
                           String line) {
        if ( line.length() < token.length() ) return false;
        if ( !line.startsWith( token ) ) return false;

        String rest = line.substring( token.length() ).trim();

        if ( rest.length() == 0 ) return true;
        char next = rest.charAt( 0 );
        if ( next == '#' || next == '/' ) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * This will normalise all spaces (no 2 spaces in a row).
     * Strings (single or double quoted) are left alone.
     */
    String normaliseSpaces(String original) {

        boolean singleQ = false;
        boolean doubleQ = false;
        boolean prevSpace = false;

        StringBuffer buf = new StringBuffer();
        char[] cs = original.trim().toCharArray();
        for ( int i = 0; i < cs.length; i++ ) {
            char c = cs[i];
            if ( Character.isWhitespace( c ) && !(singleQ || doubleQ) ) {
                if ( !prevSpace ) {
                    buf.append( ' ' );
                    prevSpace = true;
                }
            } else {
                prevSpace = false;
                if ( c == '\'' ) singleQ = !singleQ;
                if ( c == '\"' ) doubleQ = !doubleQ;
                buf.append( c );
            }
        }
        return buf.toString();
    }

    private void query() {
        lhs();
    }

    private void endRule() {
        this.lhs = false;
        this.rhs = false;

    }

    private void rhs() {
        this.rhs = true;
        this.lhs = false;

    }

    private void lhs() {
        this.lhs = true;
    }

}
