package org.drools.lang.dsl.template;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/** 
 * This represents a simple grammar mapping.
 * Order of operations is as stored in the list. 
 * Global expressions are processed first, followed by condition or consequence scoped ones. */
public class NLGrammar
    implements
    Serializable {

    private static final long    serialVersionUID = 1L;
    private final List                 mappings         = new ArrayList();
    // private static final Pattern itemPrefix       = Pattern.compile( "\\[\\s*(when|then)\\s*\\].*" );
    private static final Pattern itemMetadata     = Pattern.compile( "\\[[a-zA-Z\\d\\.]+\\]" );

    private String               description;

    public NLGrammar() {
    }

    public void addNLItem(final NLMappingItem item) {
        this.mappings.add( item );
    }

    public List getMappings() {
        return this.mappings;
    }
    
    /** Get the human readable description of this language definition. This should just be a comment. */
    public String getDescription() {
        return this.description;
    }

    /** Set the human readable description of this language definition. This should just be a comment. */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Will remove the mapping from the grammar.
     */
    public void removeMapping(final NLMappingItem item) {
        this.mappings.remove( item );
    }

    /** 
     * This will load from a reader to an appropriate text DSL config.
     * It it roughly equivalent to a properties file.
     * (you can use a properties file actually), 
     * 
     * But you can also prefix it with things like:
     * 
     * [XXX]Expression=Target
     * 
     * Where XXX is either "when" or "then" which indicates which part of the rule the
     * item relates to.
     *
     * If the "XXX" part if left out, then it will apply to the whole rule when looking for a 
     * match to expand.
     */
    public void load(final Reader reader) {
        final BufferedReader buf = new BufferedReader( reader );
        try {
            String line = null;
            while ( (line = buf.readLine()) != null ) {
                while ( line.endsWith( "\\" ) ) {
                    line = line.substring( 0,
                                           line.length() - 1 ) + buf.readLine();
                }
                line = line.trim();
                if ( line.startsWith( "#" ) ) {
                    this.description = line.substring( 1 );
                } else if ( line.equals( "" ) ) {
                    //ignore
                } else {
                    this.mappings.add( parseLine( line ) );
                }
            }

        } catch ( final IOException e ) {
            throw new IllegalArgumentException( "Unable to read DSL configuration." );
        }
    }

    /** Save out the grammar configuration */
    public void save(final Writer writer) {
        final BufferedWriter buffer = new BufferedWriter( writer );
        try {
            buffer.write( "#" + this.description + "\n" );
            for ( final Iterator iter = this.mappings.iterator(); iter.hasNext(); ) {
                final NLMappingItem item = (NLMappingItem) iter.next();
                if ( item.getScope().equals( "*" ) ) {
                    buffer.write("[" + item.getObjectName() + "]"
							+ item.getNaturalTemplate() + "="
							+ item.getTargetTemplate() + "\n");
                } else {
                    buffer.write("[" + item.getScope() + "]["
							+ item.getObjectName() + "]"
							+ item.getNaturalTemplate() + "="
							+ item.getTargetTemplate() + "\n");
                }
            }
            buffer.flush();
        } catch ( final IOException e ) {
            throw new IllegalStateException( "Unable to save DSL configuration." );
        }
    }

    /**
     * Filter the items for the appropriate scope.
     * Will include global ones.
     */
    public List getMappings(final String scope) {
        final List list = new ArrayList();
        for ( final Iterator iter = this.mappings.iterator(); iter.hasNext(); ) {
            final NLMappingItem item = (NLMappingItem) iter.next();
            if ( item.getScope().equals( "*" ) || item.getScope().equals( scope ) ) {
                list.add( item );
            }
        }
        return list;
    }

    /**
     * This will parse a line into a NLMapping item.
     */
    public NLMappingItem parseLine(final String line) {
        final int split = line.indexOf( "=" );
        String left = line.substring( 0,
                                      split ).trim();
        final String right = line.substring( split + 1 ).trim();

        left = StringUtils.replace( left,
                                    "\\",
                                    "" );

        final Matcher m2 = NLGrammar.itemMetadata.matcher(left);
        //final Matcher matcher = NLGrammar.itemPrefix.matcher( left );
        if ( m2.find()) {
            //get out priority, association
            String type = m2.group( 0 );
            type = type.substring(1,type.length() - 1);
            if (m2.find()) {
                String obj = m2.group( 0 );
                obj = obj.substring(1,obj.length() - 1);
                left = left.substring( left.lastIndexOf( "]" ) + 1 ).trim();
                return new NLMappingItem( left,
                                          right,
                                          type,
                                          obj);
            }
            left = left.substring( left.lastIndexOf( "]" ) + 1 ).trim();
            return new NLMappingItem( left,
                                      right,
                                      type,
                                      "*");
        } else {
            return new NLMappingItem( left,
                                      right,
                                      "*",
                                      "*");

        }
    }

    /**
     * Validades the mapping returning a list of errors found 
     * or an empty list in case of no errors
     * 
     * @return a List of MappingError's found or an empty list in case no one was found
     */
    public List validateMapping(final NLMappingItem item) {
        final List errors = item.validateTokenUsage();
        errors.addAll( item.validateUnmatchingBraces() );
        return errors;
    }

}