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
public class DefaultDSLMappingEntry
    implements
    DSLMappingEntry {

    private Section  section;
    private MetaData metadata;
    private String   key;
    private String   value;
    
    private Map      variables = Collections.EMPTY_MAP;
    
    private Pattern  keyPattern;
    private String   valuePattern;
    
    // following pattern is used to extract all variables names and positions from a mapping.
    // Example: for the following String:
    //
    // {This} is a {pattern} considered pretty \{{easy}\} by most \{people\}. What do you {say}
    // 
    // it will return variables:
    // This, pattern, easy, say
    //
    private static final Pattern varFinder = Pattern.compile( "(^|[^\\\\])\\{([(\\\\\\{)|[^\\{]]*?)\\}", Pattern.MULTILINE | Pattern.DOTALL );

    public DefaultDSLMappingEntry() {
    }

    public DefaultDSLMappingEntry(Section section,
                                  MetaData metadata,
                                  String key,
                                  String value) {
        this.section = section;
        this.metadata = metadata;
        this.setMappingKey( key );
        this.setMappingValue( value );
    }

    /**
     * @inheritDoc
     */
    public Section getSection() {
        return this.section;
    }

    /**
     * @inheritDoc
     */
    public DSLMappingEntry.MetaData getMetaData() {
        return this.metadata;
    }

    /**
     * @inheritDoc
     */
    public String getMappingKey() {
        return this.key;
    }

    /**
     * @inheritDoc
     */
    public String getMappingValue() {
        return this.value;
    }

    /**
     * @param key the key to set
     */
    public void setMappingKey(String key) {
        this.key = key;
        
        // retrieving variables list and creating key pattern 
        Matcher m = varFinder.matcher( key.replaceAll("\\$", "\\\\\\$") );
        StringBuffer buf = new StringBuffer();
        int counter = 1;
        while( m.find() ) {
            if( this.variables == Collections.EMPTY_MAP ) {
                this.variables = new HashMap(2);
            }
            this.variables.put( m.group( 2 ), new Integer( counter++ ) );
            m.appendReplacement( buf, m.group( 1 )+"(.*?)" );
        }
        m.appendTail( buf );
        if( buf.toString().endsWith( "(.*?)" ) ) {
            buf.append( "$" );
        }
        
        // setting the key pattern and making it space insensitive
        String pat = buf.toString().replaceAll( "\\s+", "\\\\s*" );
        if( pat.trim().startsWith( "-" ) && (! pat.trim().startsWith( "-\\s*" ) )) {
            pat = pat.substring( 0, pat.indexOf( '-' )+1 ) + "\\s*" + pat.substring( pat.indexOf( '-' )+1 );
        }
        this.keyPattern = Pattern.compile( pat, Pattern.DOTALL | Pattern.MULTILINE );
        
        // update value mapping
        this.setMappingValue( this.value );
    }

    /**
     * @param section the section to set
     */
    public void setSection(Section section) {
        this.section = section;
    }

    /**
     * @param value the value to set
     */
    public void setMappingValue(String value) {
        this.valuePattern = value;
        this.value = value;
        if( value != null ) {
            this.valuePattern = this.valuePattern.replaceAll( "\\\\n", "\n" ).replaceAll( "\\$", "\\\\\\$" );
            for( Iterator it = this.variables.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String var = (String) entry.getKey();
                int pos = ((Integer) entry.getValue()).intValue();
                
                this.valuePattern = valuePattern.replaceAll( "\\{"+var+"\\}", "\\$"+pos );
            }
        }
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetaData(MetaData metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the keyPattern
     */
    public Pattern getKeyPattern() {
        return keyPattern;
    }

    /**
     * @return the valuePattern
     */
    public String getValuePattern() {
        return valuePattern;
    }

    /**
     * @return the variables
     */
    public Map getVariables() {
        return variables;
    }
    
    public String toPatternString() {
        return this.section+"["+this.metadata+"]"+this.keyPattern.pattern()+"="+this.valuePattern;
    }
    
    public String toString() {
        return this.section+"["+this.metadata+"]"+this.key+"="+this.value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((key == null) ? 0 : key.hashCode());
        result = PRIME * result + ((metadata == null) ? 0 : metadata.hashCode());
        result = PRIME * result + ((section == null) ? 0 : section.hashCode());
        result = PRIME * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final DefaultDSLMappingEntry other = (DefaultDSLMappingEntry) obj;
        if ( key == null ) {
            if ( other.key != null ) return false;
        } else if ( !key.equals( other.key ) ) return false;
        if ( metadata == null ) {
            if ( other.metadata != null ) return false;
        } else if ( !metadata.equals( other.metadata ) ) return false;
        if ( section == null ) {
            if ( other.section != null ) return false;
        } else if ( !section.equals( other.section ) ) return false;
        if ( value == null ) {
            if ( other.value != null ) return false;
        } else if ( !value.equals( other.value ) ) return false;
        return true;
    }

}
