/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringEvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger( StringEvalHelper.class );

    public static String normalizeVariableName(String name) {
        // private static final Pattern SPACES_PATTERN = Pattern.compile( "[\\s\u00A0]+" );
        // return SPACES_PATTERN.matcher( name.trim() ).replaceAll( " " );

        // The above code was refactored for performance reasons
        // Check org.drools.benchmarks.dmn.runtime.DMNEvaluateDecisionNameLengthBenchmark
        // This method tries to return the original String whenever possible to avoid allocation of char[]

        if (name == null || name.isEmpty()) {
            return name;
        }

        // Find the first valid char, used to skip leading spaces
        int firstValid = 0, size = name.length();

        for (; firstValid < size; firstValid++) {
            if (isValidChar(name.charAt(firstValid))) {
                break;
            }
        }
        if (firstValid == size) {
            return "";
        }

        // Finds the last valid char, either before a non-regular space, the first of multiple spaces or the last char
        int lastValid = 0, trailing = 0;
        boolean inWhitespace = false;

        for (int i = firstValid; i < size; i++) {
            if (isValidChar(name.charAt(i))) {
                lastValid = i + 1;
                inWhitespace = false;
            } else {
                if (inWhitespace) {
                    break;
                }
                inWhitespace = true;
                if (name.charAt(i) != ' ') {
                    break;
                }
            }
        }

        // Counts the number of spaces after 'lastValid' (to remove possible trailing spaces)
        for (int i = lastValid; i < size && !isValidChar(name.charAt(i)); i++) {
            trailing++;
        }
        if (lastValid + trailing == size) {
            return firstValid != 0 || trailing != 0 ? name.substring(firstValid, lastValid) : name;
        }

        // There are valid chars after 'lastValid' and substring won't do (full normalization is required)
        int pos = 0;
        char[] target = new char[size-firstValid];

        // Copy the chars know to be valid to the new array
        for (int i = firstValid; i < lastValid; i++) {
            target[pos++] = name.charAt(i);
        }

        // Copy valid chars after 'lastValid' to new array
        // Many whitespaces are collapsed into one and trailing spaces are ignored
        for (int i = lastValid + 1; i < size; i++) {
            char c = name.charAt(i);
            if (isValidChar(c)) {
                if (inWhitespace) {
                    target[pos++] = ' ';
                }
                target[pos++] = c;
                inWhitespace = false;
            } else {
                inWhitespace = true;
            }
        }
        return new String(target, 0, pos);
    }

    /**
     * This method defines what characters are valid for the output of normalizeVariableName. Spaces and control characters are invalid.
     * There is a fast-path for well known characters
     */
    private static boolean isValidChar(char c) {
        if ( c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' ) {
            return true;
        }
        return c != ' ' && c != '\u00A0' && !Character.isWhitespace(c);
    }

    public static String unescapeString(String text) {
        if ( text == null ) {
            return null;
        }
        if ( text.length() >= 2 && text.startsWith( "\"" ) && text.endsWith( "\"" ) ) {
            // remove the quotes
            text = text.substring( 1, text.length() - 1 );
        }
        if ( text.indexOf( '\\' ) >= 0 ) {
            // might require un-escaping
            StringBuilder r = new StringBuilder();
            for ( int i = 0; i < text.length(); i++ ) {
                char c = text.charAt( i );
                if ( c == '\\' ) {
                    if ( text.length() > i + 1 ) {
                        i++;
                        char cn = text.charAt( i );
                        switch ( cn ) {
                            case 'b':
                                r.append( '\b' );
                                break;
                            case 't':
                                r.append( '\t' );
                                break;
                            case 'n':
                                r.append( '\n' );
                                break;
                            case 'f':
                                r.append( '\f' );
                                break;
                            case 'r':
                                r.append( '\r' );
                                break;
                            case '"':
                                r.append( '"' );
                                break;
                            case '\'':
                                r.append( '\'' );
                                break;
                            case '\\':
                                r.append( '\\' );
                                break;
                            case 'u':
                                if ( text.length() >= i + 5 ) {
                                    // escape unicode
                                    String hex = text.substring( i + 1, i + 5 );
                                    char[] chars = Character.toChars( Integer.parseInt( hex, 16 ) );
                                    r.append( chars );
                                    i += 4;
                                } else {
                                    // not really unicode
                                    r.append( "\\" ).append( cn );
                                }
                                break;
                            case 'U':
                                if ( text.length() >= i + 7 ) {
                                    // escape unicode
                                    String hex = text.substring( i + 1, i + 7 );
                                    char[] chars = Character.toChars( Integer.parseInt( hex, 16 ) );
                                    r.append( chars );
                                    i += 6;
                                } else {
                                    // not really unicode
                                    r.append( "\\" ).append( cn );
                                }
                                break;
                            default:
                                r.append( "\\" ).append( cn );
                        }
                    } else {
                        r.append( c );
                    }
                } else {
                    r.append( c );
                }
            }
            text = r.toString();
        }
        return text;
    }


    public static String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    public static String lcFirst(final String name) {
        return name.toLowerCase().charAt( 0 ) + name.substring( 1 );
    }

    static String removeTrailingZeros(final String stringNumber) {
        if(stringNumber.contains("E")) {
            return stringNumber;
        }
        final String stringWithoutZeros = stringNumber.replaceAll("0*$", "");
        if (Character.isDigit(stringWithoutZeros.charAt(stringWithoutZeros.length() - 1))) {
            return stringWithoutZeros;
        } else {
            return stringWithoutZeros.substring(0, stringWithoutZeros.length() - 1);
        }
    }
}
