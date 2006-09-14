package org.drools.semantics.java;

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.spi.AvailableVariables;
import org.drools.spi.FunctionResolver;
import org.drools.rule.Package;

/**
 * This horrific utility adds in the function class name (which is the same as the functions method name)
 * into the RHS guts of a rule. It has to tip toe around method calls, new declarations and other 
 * stuff like that.
 * A better future solution is to use a static import as found in Java 5, then ALL THIS can 
 * disappear. Oh Happy day.
 * 
 * @author Michael Neale (sadly..)
 * @author Ricardo Barone 
 * (Ricardo actually made this all work !).
 *
 */
public class FunctionFixer {

    static Pattern                 FUNCTION = Pattern.compile( "(\\S*\\s*|\\.\\s*)\\b([\\S&&[^\\.\\(\\)]]+)\\s*\\(([^)]*)\\)",
                                                               Pattern.DOTALL );
    static final Set               KEYWORDS = getJavaKeywords();

    private final FunctionResolver resolver;

    private final Package          pkg;

    public FunctionFixer(Package pkg,
                         FunctionResolver resolver) {
        this.resolver = resolver;
        this.pkg = pkg;
    }

    public String fix(final String raw) {
        //return raw;
        return fix( raw,
                    FunctionFixer.FUNCTION,
                    null );
    }
    
    public String fix(final String raw, final AvailableVariables variables) {
        //return raw;
        return fix( raw,
                    FunctionFixer.FUNCTION,
                    variables);
    }    

    public String fix(final String raw,
                      final Pattern pattern,
                      final AvailableVariables variables ) {
        if ( raw == null ) {
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        int lastIndex = 0, startIndex = 0;

        final Matcher matcher = pattern.matcher( raw );
        while ( matcher.find( startIndex ) ) {

            // instead of just using matcher.end(), grow the endIndex
            // as necessary to close all '(' in the matched String
            final int endIndex = findEndParenthesis( raw,
                                                     matcher );
            if ( endIndex < 0 ) {
                // this means that the first '(' is inside quotes - jump it and try again
                startIndex = matcher.start( 3 );
                continue;
            } else {
                // next iteration will start from here
                startIndex = endIndex;
            }

            String params = matcher.group( 3 ).trim();
            if ( endIndex > matcher.end() ) {
                // params have to grow since endIndex changed
                params += raw.substring( matcher.end() - 1,
                                         endIndex - 1 );
            }
            // Recursively process parameters
            params = fix( params,
                          pattern,
                          variables );

            String function = null;

            final String pre = matcher.group( 1 );
            if ( matcher.group( 1 ) != null ) {
                final String trimmedPre = pre.trim();
                if ( trimmedPre.endsWith( "." ) || trimmedPre.endsWith( "new" ) ) {
                    // leave alone
                    function = raw.substring( matcher.start( 2 ),
                                              matcher.start( 3 ) - 1 );
                }
            }

            if ( function == null ) {
                function = matcher.group( 2 ).trim();
                // if we have a reserved work, DO NOT TOUCH !
                // if we have no function name, DO NOT TOUCH !
                if ( function == null || function.length() == 0 || FunctionFixer.KEYWORDS.contains( function ) ) {
                    function = raw.substring( matcher.start( 2 ),
                                              matcher.start( 3 ) - 1 );
                } else {
                    if ( this.pkg.getFunctions().contains( function ) ) {
                        function = ucFirst( function ) + "." + function;
                    } else {
                        function = resolver.resolveFunction( function,
                                                             params,
                                                             variables) + "." + function;
                    }
                }
            }

            // Every scenario must reach this so that "params"
            // are correctly processed
            final String target = function + "(" + params + ")";

            buf.append( raw.substring( lastIndex,
                                       matcher.start( 2 ) ) );
            buf.append( target );

            lastIndex = endIndex;
        }

        buf.append( raw.substring( lastIndex ) );
        return buf.toString();
    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    /**
     * Search a raw string for all '(' after matcher.start(3), until
     * the match is over and return the end index the contains the last
     * ')' needed to close all opened parenthesis. 
     * 
     * @param raw
     *          The raw String containg the match and everything else.
     * @param matcher
     *          The matched stuff.
     * @return the index of the last ')' needed to close all '(' in the match,
     *         or -1 if the first '(' is inside quotes (and thus being invalid).
     */
    private int findEndParenthesis(final String raw,
                                   final Matcher matcher) {
        // start is the first '('; end is the end of the match
        final int start = matcher.start( 3 ) - 1;
        int end = matcher.end();

        // Count the number of '(' and ')' in raw String
        int oCount = 0, cCount = 0;

        // Handles text inside quotes (""): 
        //  * -1 means no quote opened
        //  * positive int represent the index of the opened quote
        int lastQuoteIndex = -1;

        for ( int i = 0; i < raw.length(); i++ ) {

            // if we reached the end of raw and opened/close parenthesis are OK
            if ( i > end && oCount == cCount ) {
                // Check if there was an opened quote that was never closed
                // (before the first '(')
                if ( lastQuoteIndex >= 0 && lastQuoteIndex <= start ) {
                    return -1;
                }
                // Everything OK - we are done!
                break;
            }

            switch ( raw.charAt( i ) ) {
                case '"' :
                    if ( lastQuoteIndex >= 0 ) {
                        // Check if the quotes contains the first '('
                        if ( lastQuoteIndex <= start && start <= i ) {
                            return -1;
                        }
                        lastQuoteIndex = -1;
                    } else {
                        lastQuoteIndex = i;
                    }
                    break;
                case '(' :
                    if ( lastQuoteIndex < 0 && i >= start ) {
                        ++oCount;
                    }
                    break;
                case ')' :
                    if ( lastQuoteIndex < 0 && i >= start ) {
                        ++cCount;
                        if ( i >= end ) {
                            // found a ')' that needs to be included
                            end = i + 1;
                        }
                    }
            }

        } // for

        return end;
    }

    /**
     * This list was obtained from
     * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/_keywords.html
     */
    private static Set getJavaKeywords() {
        final Set keys = new HashSet();
        keys.add( "abstract" );
        keys.add( "continue" );
        keys.add( "for" );
        keys.add( "new" );
        keys.add( "switch" );
        keys.add( "assert" );
        keys.add( "default" );
        keys.add( "goto" );
        keys.add( "package" );
        keys.add( "synchronized" );
        keys.add( "boolean" );
        keys.add( "do" );
        keys.add( "if" );
        keys.add( "private" );
        keys.add( "this" );
        keys.add( "break" );
        keys.add( "double" );
        keys.add( "implements" );
        keys.add( "protected" );
        keys.add( "throw" );
        keys.add( "byte" );
        keys.add( "else" );
        keys.add( "import" );
        keys.add( "public" );
        keys.add( "throws" );
        keys.add( "case" );
        keys.add( "enum" );
        keys.add( "instanceof" );
        keys.add( "return" );
        keys.add( "transient" );
        keys.add( "catch" );
        keys.add( "extends" );
        keys.add( "int" );
        keys.add( "short" );
        keys.add( "try" );
        keys.add( "char" );
        keys.add( "final" );
        keys.add( "interface" );
        keys.add( "static" );
        keys.add( "void" );
        keys.add( "class" );
        keys.add( "finally" );
        keys.add( "long" );
        keys.add( "strictfp" );
        keys.add( "volatile" );
        keys.add( "const" );
        keys.add( "float" );
        keys.add( "native" );
        keys.add( "super" );
        keys.add( "while" );
        return keys;
    }

}