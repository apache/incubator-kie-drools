package org.drools.decisiontable.parser.csv;

/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a> Break up
 *         a CSV line, with all the normal CSV features.
 */
public class CsvLineParser
{
    private ICsvParser lineParser;

    public CsvLineParser()
    {
        lineParser = new CsvParserImpl( );
    }

    /**
     * Use the current lineParser implementation to return a CSV line as a List
     * of cells. (Strings).
     */
    public List parse(CharSequence line)
    {
        return lineParser.parse( line.toString( ) );
    }

    /**
     * This is insurance incase I need to replace it with more complex Csv
     * handlers in the future.
     */
    static interface ICsvParser
    {
        public List parse(String line);
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
        ICsvParser
    {

        public static final char DEFAULT_SEP = ',';

        /** Construct a CSV parser, with the default separator (','). */
        public CsvParserImpl()
        {
            this( DEFAULT_SEP );
        }

        /**
         * Construct a CSV parser with a given separator.
         * 
         * @param sep
         *            The single char for the separator (not a list of separator
         *            characters)
         */
        public CsvParserImpl(char sep)
        {
            fieldSep = sep;
        }

        /** The fields in the current String */
        protected List list = new ArrayList( );

        /** the separator char for this parser */
        protected char fieldSep;

        /**
         * parse: break the input String into fields
         * 
         * @return java.util.Iterator containing each field from the original as
         *         a String, in order.
         */
        public List parse(String line)
        {
            StringBuffer sb = new StringBuffer( );
            list.clear( ); // recycle to initial state
            int i = 0;

            if ( line.length( ) == 0 )
            {
                list.add( line );
                return list;
            }

            do
            {
                sb.setLength( 0 );
                if ( i < line.length( ) && line.charAt( i ) == '"' ) i = advQuoted( line,
                                                                                    sb,
                                                                                    ++i ); // skip
                // quote
                else i = advPlain( line,
                                   sb,
                                   i );
                list.add( sb.toString( ) );
                i++;
            }
            while ( i < line.length( ) );

            return list;
        }

        /** advQuoted: quoted field; return index of next separator */
        protected int advQuoted(String s,
                                StringBuffer sb,
                                int i)
        {
            int j;
            int len = s.length( );
            for ( j = i; j < len; j++ )
            {
                if ( s.charAt( j ) == '"' && j + 1 < len )
                {
                    if ( s.charAt( j + 1 ) == '"' )
                    {
                        j++; // skip escape char
                    }
                    else if ( s.charAt( j + 1 ) == fieldSep )
                    { // next delimeter
                        j++; // skip end quotes
                        break;
                    }
                }
                else if ( s.charAt( j ) == '"' && j + 1 == len )
                { // end quotes at end of line
                    break; // done
                }
                sb.append( s.charAt( j ) ); // regular character.
            }
            return j;
        }

        /** advPlain: unquoted field; return index of next separator */
        protected int advPlain(String s,
                               StringBuffer sb,
                               int i)
        {
            int j;

            j = s.indexOf( fieldSep,
                           i ); // look for separator
            if ( j == -1 )
            { // none found
                sb.append( s.substring( i ) );
                return s.length( );
            }
            else
            {
                sb.append( s.substring( i,
                                        j ) );
                return j;
            }
        }

    }
}