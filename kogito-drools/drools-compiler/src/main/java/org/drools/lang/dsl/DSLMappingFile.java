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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.lang.dsl.DSLMappingEntry;

/**
 * A class that represents a DSL Mapping file
 * @author etirelli
 *
 */
public class DSLMappingFile
    implements
    DSLMapping {

    // following pattern will be used to parse dsl mapping entries in the DSL file.
    // It is capable of parsing entries that follows the pattern:
    // [<section>][<metadata>]?<key>=<value>
    private static final Pattern pattern     = Pattern.compile( "((\\[[^\\[]*\\])\\s*(\\[([^\\[]*)\\])?)?\\s*([^=]*)=(.*)" );
    private static final String  KEYWORD     = "[keyword]";
    private static final String  CONDITION   = "[when]";
    private static final String  CONSEQUENCE = "[then]";
    //private static final String  ANY         = "[*]";

    private String               dslFileName;
    private BufferedReader       dslFileReader;
    private List                 entries;
    private List                 errors;
    private boolean              closed;
    private boolean              parsed;

    public DSLMappingFile(String dslFileName,
                          Reader dslFileReader) {
        this.dslFileName = dslFileName;
        this.dslFileReader = new BufferedReader( dslFileReader );
        this.closed = false;
        this.parsed = false;
        this.entries = new ArrayList();
        this.errors = new ArrayList();
    }

    public String getIdentifier() {
        return this.dslFileName;
    }

    /**
     * Returns the name of the DSL Mapping file associated
     * with this object
     * 
     * @return
     */
    public String getDslFileName() {
        return this.dslFileName;
    }

    /**
     * @inheritDoc
     */
    public List getEntries() {
        if ( parsed && this.errors.isEmpty() ) {
            return Collections.unmodifiableList( this.entries );
        }
        return null;
    }

    /**
     * Returns the list of parsing errors
     * @return
     */
    public List getErrors() {
        return Collections.unmodifiableList( this.errors );
    }

    /**
     * Closes the file stream
     * @throws IOException
     */
    public void close() throws IOException {
        this.closed = true;
        this.dslFileReader.close();
    }

    /**
     * Returns true if this file is already closed. False otherwise.
     * 
     * @return
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * Parses the file. Throws IOException in case there is any problem
     * reading the file;
     * 
     * @return true in case no error was found parsing the file. false 
     *         otherwise. Use getErrors() to check for the actual errors.
     */
    public boolean parseFile() throws IOException {
        String line = null;
        int linecounter = 0;
        this.parsed = true;
        while ( (line = this.dslFileReader.readLine()) != null ) {
            linecounter++;
            Matcher mat = pattern.matcher( line );
            if ( mat.matches() ) {
                String sectionStr = mat.group( 2 );
                String metadataStr = mat.group( 4 );
                String key = mat.group( 5 );
                String value = mat.group( 6 );

                DSLMappingEntry.Section section = DSLMappingEntry.ANY;
                if ( KEYWORD.equals( sectionStr ) ) {
                    section = DSLMappingEntry.KEYWORD;
                } else if ( CONDITION.equals( sectionStr ) ) {
                    section = DSLMappingEntry.CONDITION;
                } else if ( CONSEQUENCE.equals( sectionStr ) ) {
                    section = DSLMappingEntry.CONSEQUENCE;
                } 

                DSLMappingEntry.MetaData metadata = new StandardDSLEntryMetaData( metadataStr );

                DSLMappingEntry entry = new DefaultDSLMappingEntry( section,
                                                                    metadata,
                                                                    key,
                                                                    value );

                this.entries.add( entry );
            } else if ( !line.trim().startsWith( "#" ) ) { // it is not a comment 
                String error = "Error parsing mapping entry: " + line;
                DSLMappingParseException exception = new DSLMappingParseException( error,
                                                                                   linecounter );
                this.errors.add( exception );
            }
        }
        return this.errors.isEmpty();
    }

    public String dumpFile() {
        StringBuffer buf = new StringBuffer();
        for ( Iterator it = this.entries.iterator(); it.hasNext(); ) {
            buf.append( it.next() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    public String dumpPatternFile() {
        StringBuffer buf = new StringBuffer();
        for ( Iterator it = this.entries.iterator(); it.hasNext(); ) {
            buf.append( ((DefaultDSLMappingEntry)it.next()).toPatternString() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

    public static class StandardDSLEntryMetaData
        implements
        DSLMappingEntry.MetaData {

        private String metadata;

        public StandardDSLEntryMetaData(String metadata) {
            this.metadata = metadata;
        }

        public String getMetaData() {
            return this.metadata;
        }

        public String toString() {
            return (this.metadata == null) ? "" : this.metadata;
        }

    }

}
