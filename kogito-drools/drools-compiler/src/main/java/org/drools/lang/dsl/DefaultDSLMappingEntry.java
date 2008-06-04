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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A default implementation for the DSL Mapping Entry interface
 * 
 * @author etirelli
 */
public class DefaultDSLMappingEntry extends AbstractDSLMappingEntry
    implements
    DSLMappingEntry {

    // following pattern is used to extract all variables names and positions from a mapping.
    // Example: for the following String:
    //
    // {This} is a {pattern} considered pretty \{{easy}\} by most \{people\}. What do you {say}
    // 
    // it will return variables:
    // This, pattern, easy, say
    //
    private static final Pattern VAR_FINDER = Pattern.compile( "(^|[^\\\\])\\{([(\\\\\\{)|[^\\{]]*?)\\}",
                                                      Pattern.MULTILINE | Pattern.DOTALL );
    
    // following pattern is used to find all the non-escaped parenthesis in the input key
    // to correctly calculate the variables offset
    private static final Pattern PAREN_FINDER = Pattern.compile( "(^\\(|[^\\\\]\\(|\\G\\()" );

    public DefaultDSLMappingEntry() {
        this( DSLMappingEntry.ANY,
              DSLMappingEntry.EMPTY_METADATA,
              null,
              null );
    }

    public DefaultDSLMappingEntry(final Section section,
                                  final MetaData metadata,
                                  final String key,
                                  final String value) {
        this.section = section;
        this.metadata = metadata;
        this.setMappingKey( key );
        this.setMappingValue( value );
    }

    /**
     * @param key the key to set
     */
    public void setMappingKey(String key) {
    	if (key != null) {
    		key = key.trim();
    	}
        this.key = key;

        if ( key != null ) {
            int substr = 0;
            // escape '$' to avoid errors
            final String escapedKey = key.replaceAll( "\\$",
                                                "\\\\\\$" );
            final Matcher m = VAR_FINDER.matcher( escapedKey );
            // retrieving variables list and creating key pattern 
            final StringBuffer buf = new StringBuffer();

            int counter = 1;
            if ( !key.startsWith( "^" ) ) {
                // making it start with a space char or a line start
                buf.append( "(\\W|^)" );
                substr += buf.length();
                counter++;
            }

            int lastMatch = 0;
            while ( m.find() ) {
                if ( this.variables == Collections.EMPTY_MAP ) {
                    this.variables = new HashMap( 2 );
                }
                
                // calculating and fixing variable offset 
                String before = escapedKey.substring( lastMatch, Math.max( m.start(), lastMatch) );
                lastMatch = m.end()+1;
                Matcher m2 = PAREN_FINDER.matcher( before );
                while( m2.find() ) {
                    counter++;
                }
                
                // creating capture group for variable
                this.variables.put( m.group( 2 ),
                                    new Integer( counter++ ) );
                m.appendReplacement( buf,
                                     m.group( 1 ) + "(.*?)" );
            }
            m.appendTail( buf );

            // if pattern ends with a variable, append a line end to avoid multiple line matching
            if ( buf.toString().endsWith( "(.*?)" ) ) {
                buf.append( "$" );
            } else {
                buf.append( "(\\W|$)" );
            }

            // setting the key pattern and making it space insensitive
            String pat = buf.toString();
            if ( pat.substring( substr ).trim().startsWith( "-" ) && (!pat.substring( substr ).trim().startsWith( "-\\s*" )) ) {
                pat = pat.substring( 0,
                                     pat.indexOf( '-' ) + 1 ) + "\\s*" + pat.substring( pat.indexOf( '-' ) + 1 ).trim();
            }
            pat = pat.replaceAll( "\\s+",
                                  "\\\\s+" );
            this.keyPattern = Pattern.compile( pat,
                                               Pattern.DOTALL | Pattern.MULTILINE );

        } else {
            this.keyPattern = null;
        }
        // update value mapping
        this.setMappingValue( this.value );
    }

    /**
     * @param value the value to set
     */
    public void setMappingValue(final String value) {
        this.valuePattern = value;
        this.value = value;
        if ( value != null ) {
            this.valuePattern = this.valuePattern.replaceAll( "\\\\n",
                                                              "\n" ).replaceAll( "\\$",
                                                                                 "\\\\\\$" );
            for ( final Iterator it = this.variables.entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) it.next();
                final String var = (String) entry.getKey();
                final int pos = ((Integer) entry.getValue()).intValue();

                this.valuePattern = this.valuePattern.replaceAll( "\\{" + var + "\\}",
                                                                  "\\$" + pos );
            }
        }
    }

}
