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

package org.drools.decisiontable.parser.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a> Break up
 *         a CSV line, with all the normal CSV features.
 */
public class CsvLineParser {
    private ICsvParser lineParser;

    public CsvLineParser() {
        this.lineParser = new CsvParserImpl();
    }

    /**
     * Use the current lineParser implementation to return a CSV line as a List
     * of cells. (Strings).
     */
    public List<String> parse(final CharSequence line) {
        return this.lineParser.parse( line.toString() );
    }

    /**
     * This is insurance incase I need to replace it with more complex Csv
     * handlers in the future.
     */
    static interface ICsvParser {
        public List<String> parse(String line);
    }

    /**
     * Parse comma-separated values (CSV), a common Windows file format. Sample
     * input: "LU",86.25,"11/4/1998","2:19PM",+4.0625
     * <p>
     * Inner logic adapted from a C++ original that was Copyright (C) 1999
     * Lucent Technologies Excerpted from 'The Practice of Programming' by Brian
     * W. Kernighan and Rob Pike.
     * <p>
     * Included by permission of the http://tpop.awl.com/ web site, which says:
     * "You may use this code for any purpose, as long as you leave the
     * copyright notice and book citation attached." I have done so.
     * 
     * @author Brian W. Kernighan and Rob Pike (C++ original)
     * @author Ian F. Darwin (translation into Java and removal of I/O)
     * @author Ben Ballard (rewrote advQuoted to handle '""' and for
     *         readability)
     */
    static class CsvParserImpl
        implements
        ICsvParser {

        public static final char DEFAULT_SEP = ',';

        /** Construct a CSV parser, with the default separator (','). */
        public CsvParserImpl() {
            this( CsvParserImpl.DEFAULT_SEP );
        }

        /**
         * Construct a CSV parser with a given separator.
         * 
         * @param sep
         *            The single char for the separator (not a list of separator
         *            characters)
         */
        public CsvParserImpl(final char sep) {
            this.fieldSep = sep;
        }

        /** The fields in the current String */
        protected List<String> list = new ArrayList<String>();

        /** the separator char for this parser */
        protected char fieldSep;

        /**
         * parse: break the input String into fields
         * 
         * @return java.util.Iterator containing each field from the original as
         *         a String, in order.
         */
        public List<String> parse(final String line) {
            final StringBuffer sb = new StringBuffer();
            this.list.clear(); // recycle to initial state
            int i = 0;

            if ( line.length() == 0 ) {
                this.list.add( line );
                return this.list;
            }

            do {
                sb.setLength( 0 );
                if ( i < line.length() && line.charAt( i ) == '"' ) {
                    i = advQuoted( line,
                                                                                       sb,
                                                                                       ++i ); // skip
                } else {
                    i = advPlain( line,
                                       sb,
                                       i );
                }
                this.list.add( sb.toString() );
                i++;
            } while ( i < line.length() );

            return this.list;
        }

        /** advQuoted: quoted field; return index of next separator */
        protected int advQuoted(final String s,
                                final StringBuffer sb,
                                final int i) {
            int j;
            final int len = s.length();
            for ( j = i; j < len; j++ ) {
                if ( s.charAt( j ) == '"' && j + 1 < len ) {
                    if ( s.charAt( j + 1 ) == '"' ) {
                        j++; // skip escape char
                    } else if ( s.charAt( j + 1 ) == this.fieldSep ) { // next delimeter
                        j++; // skip end quotes
                        break;
                    }
                } else if ( s.charAt( j ) == '"' && j + 1 == len ) { // end quotes at end of line
                    break; // done
                }
                sb.append( s.charAt( j ) ); // regular character.
            }
            return j;
        }

        /** advPlain: unquoted field; return index of next separator */
        protected int advPlain(final String s,
                               final StringBuffer sb,
                               final int i) {
            int j;

            j = s.indexOf( this.fieldSep,
                           i ); // look for separator
            if ( j == -1 ) { // none found
                sb.append( s.substring( i ) );
                return s.length();
            } else {
                sb.append( s.substring( i,
                                        j ) );
                return j;
            }
        }

    }
}
