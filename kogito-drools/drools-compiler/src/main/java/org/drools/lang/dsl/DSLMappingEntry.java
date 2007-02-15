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

/**
 * A single entry in a DSL mapping file
 * 
 * @author etirelli
 */
public interface DSLMappingEntry {

    public static final Section KEYWORD     = new KeywordSection();
    public static final Section CONDITION   = new ConditionSection();
    public static final Section CONSEQUENCE = new ConsequenceSection();
    public static final Section ANY         = new AnySection();

    /**
     * Returns the section this mapping entry refers to
     * 
     * @return
     */
    public DSLMappingEntry.Section getSection();
    
    /**
     * Returns the meta data info about this mapping entry
     * 
     * @return
     */
    public DSLMappingEntry.MetaData getMetaData();
    
    /**
     * Returns the key of this mapping, i.e., the source
     * that needs to be translated
     * 
     * @return
     */
    public String getMappingKey();
    
    /**
     * Returns the result of the translation
     * 
     * @return
     */
    public String getMappingValue();

    
    /**
     * Returns the compiled pattern based on the given MappingKey
     * @return the keyPattern
     */
    public Pattern getKeyPattern();

    /**
     * Returns the transformed mapping value using place holders for variables 
     * @return the valuePattern
     */
    public String getValuePattern();

    /**
     * Returns the list of variables found in the given pattern key
     * @return the variables
     */
    public Map getVariables();
    
    
    /**
     * An inner interface for DSL mapping sections
     * @author etirelli
     *
     */
    public static interface Section {
        public String getSymbol();
    }
    
    /**
     * An inner interface to represent any metadata
     * associated with this entry. It is obviously
     * implementation dependent.
     * 
     * @author etirelli
     *
     */
    public static interface MetaData {
        public String toString();

        public String getMetaData();
    }

    /**
     * The keyword section, to allow mapping of keywords
     * 
     * @author etirelli
     */
    public static class KeywordSection
        implements
        Section {
        private static final String symbol = "[keyword]";

        private KeywordSection() {
        }

        public String getSymbol() {
            return symbol;
        }
        
        public String toString() {
            return symbol;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((symbol == null) ? 0 : symbol.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final KeywordSection other = (KeywordSection) obj;
            if ( symbol == null ) {
                if ( other.getSymbol() != null ) return false;
            } else if ( !symbol.equals( other.getSymbol() ) ) return false;
            return true;
        }
    }

    /**
     * The condition section, to allow mapping of the conditions
     * 
     * @author etirelli
     */
    public static class ConditionSection
        implements
        Section {
        private static String symbol = "[condition]";

        private ConditionSection() {
        }

        public String getSymbol() {
            return symbol;
        }

        public String toString() {
            return symbol;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((symbol == null) ? 0 : symbol.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final KeywordSection other = (KeywordSection) obj;
            if ( symbol == null ) {
                if ( other.getSymbol() != null ) return false;
            } else if ( !symbol.equals( other.getSymbol() ) ) return false;
            return true;
        }
    }

    /**
     * The consequence section to allow the mapping 
     * of consequence elements
     * 
     * @author etirelli
     */
    public static class ConsequenceSection
        implements
        Section {
        private static String symbol = "[consequence]";

        private ConsequenceSection() {
        }

        public String getSymbol() {
            return symbol;
        }

        public String toString() {
            return symbol;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((symbol == null) ? 0 : symbol.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final KeywordSection other = (KeywordSection) obj;
            if ( symbol == null ) {
                if ( other.getSymbol() != null ) return false;
            } else if ( !symbol.equals( other.getSymbol() ) ) return false;
            return true;
        }
    }

    /**
     * An element to indicate the mapping should be applicable
     * to any section
     *  
     * @author etirelli
     */
    public static class AnySection
        implements
        Section {
        private static String symbol = "*";

        private AnySection() {
        }

        public String getSymbol() {
            return symbol;
        }

        public String toString() {
            return symbol;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((symbol == null) ? 0 : symbol.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final KeywordSection other = (KeywordSection) obj;
            if ( symbol == null ) {
                if ( other.getSymbol() != null ) return false;
            } else if ( !symbol.equals( other.getSymbol() ) ) return false;
            return true;
        }
    }

}
