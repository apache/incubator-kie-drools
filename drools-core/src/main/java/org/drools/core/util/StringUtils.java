/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static java.lang.Character.isWhitespace;

/**
 * Ripped form commons StringUtil, unless specified:
 * 
 * <p>Operations on {@link java.lang.String} that are
 * <code>null</code> safe.</p>
 *
 * <ul>
 *  <li><b>IsEmpty/IsBlank</b>
 *      - checks if a String contains text</li>
 *  <li><b>Trim/Strip</b>
 *      - removes leading and trailing whitespace</li>
 *  <li><b>Equals</b>
 *      - compares two strings null-safe</li>
 *  <li><b>IndexOf/LastIndexOf/Contains</b>
 *      - null-safe index-of checks
 *  <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 *      - index-of any of a set of Strings</li>
 *  <li><b>ContainsOnly/ContainsNone</b>
 *      - does String contains only/none of these characters</li>
 *  <li><b>Substring/Left/Right/Mid</b>
 *      - null-safe substring extractions</li>
 *  <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 *      - substring extraction relative to other strings</li>
 *  <li><b>Split/Join</b>
 *      - splits a String into an array of substrings and vice versa</li>
 *  <li><b>Remove/Delete</b>
 *      - removes part of a String</li>
 *  <li><b>Replace/Overlay</b>
 *      - Searches a String and replaces one String with another</li>
 *  <li><b>Chomp/Chop</b>
 *      - removes the last part of a String</li>
 *  <li><b>LeftPad/RightPad/Center/Repeat</b>
 *      - pads a String</li>
 *  <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 *      - changes the case of a String</li>
 *  <li><b>CountMatches</b>
 *      - counts the number of occurrences of one String in another</li>
 *  <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 *      - checks the characters in a String</li>
 *  <li><b>DefaultString</b>
 *      - protects against a null input String</li>
 *  <li><b>Reverse/ReverseDelimited</b>
 *      - reverses a String</li>
 *  <li><b>Abbreviate</b>
 *      - abbreviates a string using ellipsis</li>
 *  <li><b>Difference</b>
 *      - compares two Strings and reports on their differences</li>
 *  <li><b>LevensteinDistance</b>
 *      - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>The <code>StringUtils</code> class defines certain words related to
 * String handling.</p>
 *
 * <ul>
 *  <li>null - <code>null</code></li>
 *  <li>empty - a zero-length string (<code>""</code>)</li>
 *  <li>space - the space character (<code>' '</code>, char 32)</li>
 *  <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 *  <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p><code>StringUtils</code> handles <code>null</code> input Strings quietly.
 * That is to say that a <code>null</code> input will return <code>null</code>.
 * Where a <code>boolean</code> or <code>int</code> is being returned
 * details vary by method.</p>
 *
 * <p>A side effect of the <code>null</code> handling is that a
 * <code>NullPointerException</code> should be considered a bug in
 * <code>StringUtils</code> (except for deprecated methods).</p>
 *
 * <p>Methods in this class give sample code to explain their operation.
 * The symbol <code>*</code> is used to indicate any input including <code>null</code>.</p>
 *
 * @see java.lang.String
 * @since 1.0
 * @version $Id$
 */
public class StringUtils {

    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    // Performance testing notes (JDK 1.4, Jul03, scolebourne)
    // Whitespace:
    // Character.isWhitespace() is faster than WHITESPACE.indexOf()
    // where WHITESPACE is a string of all whitespace characters
    //
    // Character access:
    // String.charAt(n) versus toCharArray(), then array[n]
    // String.charAt(n) is about 15% worse for a 10K string
    // They are about equal for a length 50 string
    // String.charAt(n) is about 4 times better for a length 3 string
    // String.charAt(n) is best bet overall
    //
    // Append:
    // String.concat about twice as fast as StringBuilder.append
    // (not sure who tested this)

    /**
     * The empty String <code>""</code>.
     * @since 2.0
     */
    public static final String   EMPTY              = "";

    /**
     * Represents a failed index search.
     * @since 2.1
     */
    public static final int      INDEX_NOT_FOUND    = -1;

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int     PAD_LIMIT          = 8192;

    /**
     * <p><code>StringUtils</code> instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * <code>StringUtils.trim(" foo ");</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringUtils() {
        super();
    }

    public static String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    // Empty checks
    //-----------------------------------------------------------------------
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        
        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( !isWhitespace(str.charAt( i )) )  {
                return false;
            }
        }
        
        return true;
    }

    // Padding
    //-----------------------------------------------------------------------
    /**
     * <p>Repeat a String <code>repeat</code> times to form a
     * new String.</p>
     *
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0)   = ""
     * StringUtils.repeat("", 2)   = ""
     * StringUtils.repeat("a", 3)  = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str  the String to repeat, may be null
     * @param repeat  number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  <code>null</code> if null String input
     */
    public static String repeat(final String str,
                                final int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if ( str == null ) {
            return null;
        }
        if ( repeat <= 0 ) {
            return EMPTY;
        }
        final int inputLength = str.length();
        if ( repeat == 1 || inputLength == 0 ) {
            return str;
        }
        if ( inputLength == 1 && repeat <= PAD_LIMIT ) {
            return padding( repeat,
                            str.charAt( 0 ) );
        }

        final int outputLength = inputLength * repeat;
        switch ( inputLength ) {
            case 1 :
                final char ch = str.charAt( 0 );
                final char[] output1 = new char[outputLength];
                for ( int i = repeat - 1; i >= 0; i-- ) {
                    output1[i] = ch;
                }
                return new String( output1 );
            case 2 :
                final char ch0 = str.charAt( 0 );
                final char ch1 = str.charAt( 1 );
                final char[] output2 = new char[outputLength];
                for ( int i = repeat * 2 - 2; i >= 0; i--, i-- ) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String( output2 );
            default :
                final StringBuilder buf = new StringBuilder( outputLength );
                for ( int i = 0; i < repeat; i++ ) {
                    buf.append( str );
                }
                return buf.toString();
        }
    }

    /**
     * <p>Splits the provided text into an array, separators specified, 
     * preserving all tokens, including empty tokens created by adjacent
     * separators. This is an alternative to using StringTokenizer.</p>
     *
     * <p>The separator is not included in the returned String array.
     * Adjacent separators are treated as separators for empty tokens.
     * For more control over the split use the StrTokenizer class.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.</p>
     *
     * <pre>
     * StringUtils.splitPreserveAllTokens(null, *)           = null
     * StringUtils.splitPreserveAllTokens("", *)             = []
     * StringUtils.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * StringUtils.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * StringUtils.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * StringUtils.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * StringUtils.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * StringUtils.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.1
     */
    public static String[] splitPreserveAllTokens(final String str,
                                                  final String separatorChars) {
        return splitWorker( str,
                            separatorChars,
                            -1,
                            true );
    }

    /**
     * Performs the logic for the <code>split</code> and 
     * <code>splitPreserveAllTokens</code> methods that return a maximum array 
     * length.
     *
     * @param str  the String to parse, may be <code>null</code>
     * @param separatorChars the separate character
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit.
     * @param preserveAllTokens if <code>true</code>, adjacent separators are
     * treated as empty token separators; if <code>false</code>, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    private static String[] splitWorker(final String str,
                                        final String separatorChars,
                                        final int max,
                                        final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if ( str == null ) {
            return null;
        }
        final int len = str.length();
        if ( len == 0 ) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if ( separatorChars == null ) {
            // Null separator means use whitespace
            while ( i < len ) {
                if ( isWhitespace(str.charAt(i)) ) {
                    if ( match || preserveAllTokens ) {
                        lastMatch = true;
                        if ( sizePlus1++ == max ) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add( str.substring( start,
                                                 i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else if ( separatorChars.length() == 1 ) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt( 0 );
            while ( i < len ) {
                if ( str.charAt( i ) == sep ) {
                    if ( match || preserveAllTokens ) {
                        lastMatch = true;
                        if ( sizePlus1++ == max ) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add( str.substring( start,
                                                 i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while ( i < len ) {
                if ( separatorChars.indexOf( str.charAt( i ) ) >= 0 ) {
                    if ( match || preserveAllTokens ) {
                        lastMatch = true;
                        if ( sizePlus1++ == max ) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add( str.substring( start,
                                                 i ) );
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        }
        if ( match || (preserveAllTokens && lastMatch) ) {
            list.add( str.substring( start,
                                     i ) );
        }
        return list.toArray( new String[list.size()] );
    }

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     *
     * <pre>
     * StringUtils.padding(0, 'e')  = ""
     * StringUtils.padding(3, 'e')  = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of <code>char</code>s to be represented.
     * If you are needing to support full I18N of your applications
     * consider using {@link #repeat(String, int)} instead. 
     * </p>
     *
     * @param repeat  number of times to repeat delim
     * @param padChar  character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     * @see #repeat(String, int)
     */
    public static String padding(final int repeat,
                                 final char padChar) throws IndexOutOfBoundsException {
        if ( repeat < 0 ) {
            throw new IndexOutOfBoundsException( "Cannot pad a negative amount: " + repeat );
        }
        final char[] buf = new char[repeat];
        for ( int i = 0; i < buf.length; i++ ) {
            buf[i] = padChar;
        }
        return new String( buf );
    }

    public static String readFileAsString(Reader reader) {
        try {
            StringBuilder fileData = new StringBuilder( 1000 );
            char[] buf = new char[1024];
            int numRead;
            while ( (numRead = reader.read( buf )) != -1 ) {
                String readData = String.valueOf( buf,
                                                  0,
                                                  numRead );
                fileData.append( readData );
                buf = new char[1024];
            }
            reader.close();
            return fileData.toString();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
    
    /**
     * <p>Unescapes any Java literals found in the <code>String</code>.
     * For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * 
     * @param str  the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new RuntimeException(ioe);
        }
    }

    /**
     * <p>Unescapes any Java literals found in the <code>String</code> to a
     * <code>Writer</code>.</p>
     *
     * <p>For example, it will turn a sequence of <code>'\'</code> and
     * <code>'n'</code> into a newline character, unless the <code>'\'</code>
     * is preceded by another <code>'\'</code>.</p>
     * 
     * <p>A <code>null</code> string input has no effect.</p>
     * 
     * @param out  the <code>Writer</code> used to output unescaped characters
     * @param str  the <code>String</code> to unescape, may be null
     * @throws IllegalArgumentException if the Writer is <code>null</code>
     * @throws IOException if error occurs on underlying Writer
     */
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write("\"");
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u':
                        {
                            // uh-oh, we're in unicode country....
                            inUnicode = true;
                            break;
                        }
                    default :
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }
    
    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    
    /**
     * Normalize the path by suppressing sequences like "path/.." and
     * inner simple dots.
     * <p>The result is convenient for path comparison. For other uses,
     * notice that Windows separators ("\") are replaced by simple slashes.
     * @param path the original path
     * @return the normalized path
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(":");
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            pathToUse = pathToUse.substring(prefixIndex + 1);
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        List pathElements = new LinkedList();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            }
            else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            }
            else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                }
                else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     * @param coll the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @param prefix the String to start each element with
     * @param suffix the String to end each element with
     * @return the delimited String
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }
    
    /**
     * Convenience method to return a Collection as a delimited (e.g. CSV)
     * String. E.g. useful for <code>toString()</code> implementations.
     * @param coll the Collection to display
     * @param delim the delimiter to use (probably a ",")
     * @return the delimited String
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String collectionToDelimitedString(Collection coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }
    
    
    /**
     * Replace all occurences of a substring within a string with
     * another string.
     * @param inString String to examine
     * @param oldPattern String to replace
     * @param newPattern String to insert
     * @return a String with the replacements
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (isEmpty(inString) || isEmpty(oldPattern) || newPattern == null) {
            return inString;
        }
        StringBuilder sbuf = new StringBuilder();
        // output StringBuilder we'll build up
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString.substring(pos, index));
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));
        // remember to append any characters to the right of a match
        return sbuf.toString();
    }
    
    public static URI toURI(String location) throws URISyntaxException {
        return new URI( replace(location, " ", "%20") );
    }
    
    public static String escapeXmlString(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        // true if last char was blank
        int len = string.length();

        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            if (c == ' ') {
                sb.append(' ');
            } else {
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160 ) {
                        // nothing special only 7 Bit
                        sb.append(c);
                    } else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(Integer.valueOf(ci).toString());
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }    
    
    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will still
     * be considered as single delimiter string, rather than as bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     * @param str the input String
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @return an array of the tokens in the list
     *
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    /**
     * Take a String which is a delimited list and convert it to a String array.
     * <p>A single delimiter can consists of more than one character: It will still
     * be considered as single delimiter string, rather than as bunch of potential
     * delimiter characters - in contrast to <code>tokenizeToStringArray</code>.
     * @param str the input String
     * @param delimiter the delimiter between elements (this is a single delimiter,
     * rather than a bunch individual delimiter characters)
     * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
     * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
     * @return an array of the tokens in the list
     *
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[] {str};
        }
        List result = new ArrayList();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        }
        else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }
    
    /**
     * Copy the given Collection into a String array.
     * The Collection must contain String elements only.
     * @param collection the Collection to copy
     * @return the String array (<code>null</code> if the passed-in
     * Collection was <code>null</code>)
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }
    
    /**
     * Delete any character in a given String.
     * @param inString the original String
     * @param charsToDelete a set of characters to delete.
     * E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting String
     * 
     * Borrowed from Spring, under the ASL2.0 license.
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if (isEmpty(inString) || isEmpty(charsToDelete)) {
            return inString;
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }
    
    public static String toString(Reader reader) throws IOException {
        if ( reader instanceof BufferedReader ) {
            return toString( (BufferedReader) reader );
        } else {
            return toString( new BufferedReader( reader ) );
        }
    }
    
    public static String toString(InputStream is) throws IOException {
        return toString( new BufferedReader(new InputStreamReader(is, "UTF-8") ) );
    }
    
    public static String toString(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            boolean previousLine = false;
            while ((line = reader.readLine()) != null) {
                if ( previousLine ) {
                    sb.append("\n");
                }
                sb.append(line);
                previousLine = true;
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    public static String generateUUID() {
        char[] uuid = new char[32];
        char[] chars = UUID.randomUUID().toString().toCharArray();
        for (int i = 0, j = 0; i < 32; j++) if (chars[j] != '-') uuid[i++] = chars[j];
        return new String(uuid);
    }

    public static String extractFirstIdentifier(String string, int start) {
        StringBuilder builder = new StringBuilder();
        extractFirstIdentifier(string, builder, start);
        return builder.toString();
    }

    public static int extractFirstIdentifier(String string, StringBuilder builder, int start) {
        boolean started = false;
        int i = start;
        for (; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (Character.isJavaIdentifierStart(ch)) {
                builder.append(ch);
                started = true;
            }
            else if (started && Character.isJavaIdentifierPart(ch)) {
                builder.append(ch);
            } else if (started) {
                break;
            }
        }
        return i;
    }

    public static int skipBlanks(String string, int start) {
        int i = start;
        while (i < string.length() && (string.charAt(i) == ' ' || string.charAt(i) == '\t')) {
            i++;
        }
        return i;
    }

    public static List<String> splitStatements(CharSequence string) {
        return codeAwareSplitOnChar(string, ';');
    }

    public static List<String> splitArgumentsList(CharSequence string) {
        return codeAwareSplitOnChar(string, ',');
    }

    private static List<String> codeAwareSplitOnChar(CharSequence string, char ch) {
        List<String> args = new ArrayList<String>();
        int lastStart = 0;
        int nestedParam = 0;
        boolean isQuoted = false;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ch) {
                if (!isQuoted && nestedParam == 0) {
                    args.add(string.subSequence(lastStart, i).toString().trim());
                    lastStart = i+1;
                }
            } else {
                switch (string.charAt(i)) {
                    case '(':
                    case '[':
                    case '{':
                        if (!isQuoted) nestedParam++;
                        break;
                    case ')':
                    case ']':
                    case '}':
                        if (!isQuoted) nestedParam--;
                        break;
                    case '"':
                    case '\'':
                        isQuoted = !isQuoted;
                        break;
                    case '\\':
                        if (i+1 < string.length() && string.charAt(i+1) == '"') {
                            i++;
                        }
                        break;

                }
            }
        }
        String lastArg = string.subSequence(lastStart, string.length()).toString().trim();
        if (lastArg.length() > 0) {
            args.add(lastArg);
        }
        return args;
    }

    public static int findEndOfMethodArgsIndex(CharSequence string, int startOfMethodArgsIndex) {
        boolean isDoubleQuoted = false;
        boolean isSingleQuoted = false;
        int nestingLevel = 0;
        for (int charIndex = startOfMethodArgsIndex; charIndex < string.length(); charIndex++) {
            boolean isCurrentCharEscaped = charIndex > 0 && string.charAt(charIndex - 1) == '\\';
            switch (string.charAt(charIndex)) {
                case '(':
                    if (!isDoubleQuoted && !isSingleQuoted) {
                        nestingLevel++;
                    }
                    break;

                case ')':
                    if (!isDoubleQuoted && !isSingleQuoted) {
                        nestingLevel--;
                        if (nestingLevel == 0) {
                            return charIndex;
                        }
                    }
                    break;

                case '"':
                    if (isCurrentCharEscaped || isSingleQuoted) {
                        // ignore escaped double quote and double quote inside single quotes (e.g 'text " text')
                        continue;
                    }
                    isDoubleQuoted = !isDoubleQuoted;
                    break;

                case '\'':
                    if (isCurrentCharEscaped || isDoubleQuoted) {
                        // ignore escaped single quote and single quote inside double quotes (e.g. "text ' text")
                        continue;
                    }
                    isSingleQuoted = !isSingleQuoted;
                    break;

                default:
                    // nothing to do, just continue with next character
            }
        }

        return -1;
    }

    public static int indexOfOutOfQuotes(String str, String searched) {
        for ( int i = str.indexOf(searched); i >= 0; i = str.indexOf(searched, i+1) ) {
            if ( countCharOccurrences(str, '"', 0, i) % 2 == 0 ) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfOutOfQuotes(String str, char searched) {
        for ( int i = str.indexOf(searched); i >= 0; i = str.indexOf(searched, i+1) ) {
            if ( countCharOccurrences(str, '"', 0, i) % 2 == 0 ) {
                return i;
            }
        }
        return -1;
    }

    private static int countCharOccurrences(String s, char c, int start, int end) {
        int count = 0;
        for (int i = start; i < end; i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public static boolean isIdentifier(String expr) {
        return !expr.equals("true") && !expr.equals("false") &&
               !expr.equals("null") && expr.matches("[a-zA-Z_\\$][a-zA-Z_\\$0-9]*");
    }

    // To be extended in the future with more comparison strategies
    public static enum SIMILARITY_STRATS { DICE };

    public static double stringSimilarity( String s1, String s2, SIMILARITY_STRATS method ) {
        switch ( method ) {
            case DICE:
            default: return stringSimilarityDice( s1, s2 );
        }
    }

    private static double stringSimilarityDice( String s1, String s2 ) {
        int n1 = s1.length() - 1;
        int n2 = s2.length() - 1;

        int n;

        if ( s1.length() < s2.length() ) {
            n = commonBigrams( s1, s2 );
        } else {
            n = commonBigrams( s2, s1 );
        }

        return (2.0 * n) / ( n1 + n2 );
    }

    private static int commonBigrams( String s1, String s2 ) {
        int acc = 0;
        for ( int j = 0; j < s1.length() - 1; j++ ) {
            String bigram = s1.substring( j, j +1 );
            acc += s2.indexOf( bigram ) >= 0 ? 1 : 0;
        }
        return acc;
    }
}
