/*
 * Copyright 2006 JBoss Inc
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

package org.drools.lang.dsl;

import java.util.Map;
import java.util.regex.Pattern;

import org.mvel2.util.ParseTools;

/**
 * An ANTLR-driven implementation for the DSL Mapping Entry interface
 * 
 * @author mattgeis
 */
public class AntlrDSLMappingEntry extends AbstractDSLMappingEntry {

    private static final String HEAD_TAG            = "__HEAD__";
    private static final String TAIL_TAG            = "__TAIL__";

    private boolean             headMatchGroupAdded = false;
    private boolean             tailMatchGroupAdded = false;

    public AntlrDSLMappingEntry() {
        this( DSLMappingEntry.ANY,
              DSLMappingEntry.EMPTY_METADATA,
              null,
              null,
              null,
              null );
    }

    public AntlrDSLMappingEntry(final Section section,
                                final MetaData metadata,
                                final String key,
                                final String value,
                                final String keyPattern,
                                final String valuePattern) {
        setSection( section );
        setMetaData( metadata );
        setMappingKey( key );
        setMappingValue( value );
        setKeyPattern( keyPattern );
        setValuePattern( valuePattern );
    }

    /**
     * @param KEY
     *            the key to set
     */
    public void setKeyPattern(final String keyPat) {
        //the "key" in this case is already mostly formed into 
        //a pattern by ANTLR, and just requires a bit of post-processing.
        if ( keyPat != null ) {
            String trimmed = keyPat.trim();
            // escaping the special character $
            String keyPattern = trimmed.replaceAll( "\\$",
                                                    "\\\\\\$" );
            // unescaping the special character #
            keyPattern = keyPattern.replaceAll( "\\\\#",
                                                "#" );

            if ( !keyPattern.startsWith( "^" ) ) {
                // making it start with a space char or a line start
                keyPattern = "(\\s|^)" + keyPattern;
                // adding a dummy variable due to index shift
                getVariables().put( HEAD_TAG,
                                    Integer.valueOf( 0 ) );
                headMatchGroupAdded = true;
            }

            // if pattern ends with a pure variable whose pattern could create
            // a greedy match, append a line end to avoid multiple line matching
            if ( keyPattern.endsWith( "(.*?)" ) ) {
                keyPattern += "$";
            } else {
                keyPattern += "(\\s|$)";
                getVariables().put( TAIL_TAG,
                                    Integer.valueOf( 1 ) );
                tailMatchGroupAdded = true;
            }

            // fix variables offset
            fixVariableOffsets();

            // setting the key pattern and making it space insensitive
            //first, look to see if it's 
            if ( trimmed.startsWith( "-" ) && (!trimmed.startsWith( "-\\s*" )) ) {
                int index = keyPattern.indexOf( '-' ) + 1;
                keyPattern = keyPattern.substring( 0,
                                                   index ) + "\\s*" + keyPattern.substring( index ).trim();
            }

            // making the pattern space insensitive
            keyPattern = keyPattern.replaceAll( "\\s+",
                                                "\\\\s+" );
            // normalize duplications
            keyPattern = keyPattern.replaceAll( "(\\\\s\\+)+",
                                                "\\\\s+" );

            setKeyPattern( Pattern.compile( keyPattern,
                                            Pattern.DOTALL | Pattern.MULTILINE ) );

        } else {
            setKeyPattern( (Pattern) null );
        }
    }

    private void fixVariableOffsets() {
        char[] input = getMappingKey().toCharArray();
        int counter = 1;
        boolean insideCurly = false;
        if ( headMatchGroupAdded ) {
            getVariables().put( HEAD_TAG,
                                Integer.valueOf( counter ) );
            counter++;
        }
        for ( int i = 0; i < input.length; i++ ) {
            switch ( input[i] ) {
                case '\\' :
                    // next char is escaped
                    i++;
                    break;
                case '(' :
                    // All groups starting with "(?" are non-capturing.
                    if( i == input.length - 1 || input[i+1] != '?' ) counter++;
                    break;
                case '{' :
                    if ( insideCurly ) {
                        i = ParseTools.balancedCapture( input,
                                                        i,
                                                        '{' );
                    } else {
                        insideCurly = true;
                        updateVariableIndex( i,
                                             counter );
                        counter++;
                    }
                    break;
                case '}' :
                    if ( insideCurly ) insideCurly = false;
            }
        }
        if ( tailMatchGroupAdded ) {
            getVariables().put( TAIL_TAG,
                                Integer.valueOf( counter ) );
        }
    }

    private void updateVariableIndex(int offset,
                                     int counter) {
        String subs = getMappingKey().substring( offset );
        for ( Map.Entry<String, Integer> entry : getVariables().entrySet() ) {
            if ( subs.startsWith( "{" + entry.getKey() ) && ((subs.charAt( entry.getKey().length() + 1 ) == '}') || (subs.charAt( entry.getKey().length() + 1 ) == ':')) ) {
                entry.setValue( Integer.valueOf( counter ) );
                break;
            }
        }
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValuePattern(String value) {
        if ( value != null ) {
            StringBuilder valuePatternBuffer = new StringBuilder();

            if ( headMatchGroupAdded ) {
                valuePatternBuffer.append( "$1" );
            }
            valuePatternBuffer.append( value );
            if ( value.endsWith( " " ) ) {
                valuePatternBuffer.deleteCharAt( valuePatternBuffer.length() - 1 );
            }
            if ( tailMatchGroupAdded ) {
                int tailIndex = getVariables().get( TAIL_TAG ).intValue();
                valuePatternBuffer.append( "$" + tailIndex );
            }
            // unescaping the special character # and creating the line breaks
            String pat = valuePatternBuffer.toString().replaceAll( "\\\\#",
                                                                   "#" ).replaceAll( "\\\\n", 
                                                                                     "\n" );
            for ( Map.Entry<String, Integer> entry : getVariables().entrySet() ) {
                pat = pat.replaceAll( "\\{" + entry.getKey() + "(:(.*?))?\\}",
                                      "\\$" + entry.getValue() );
            }
            super.setValuePattern( pat );
        }

    }
}
