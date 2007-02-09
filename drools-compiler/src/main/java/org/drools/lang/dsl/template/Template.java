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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.commons.lang.StringUtils;

/**
 * This class takes a linked list of Chunk objects, and will replace what the chunks represent
 * in an nl string with a interpolated grammar template. 
 * The values are obtained by matching the chunks with the nl.
 * 
 * A chunk it kind of a token, but tokens are hard to define when you are talking natural language.
 * Basically chunk == token in parsing nonclamenture.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * This is an alternative approach to the infix parser.
 */
class Template {

    //the start of the linked list.
    Chunk start;

    /**
     * Ad a chunk from the dictionary expression.
     * A chunk is a piece of nl, or a hole.
     * nl & holes must not be mixed.
     */
    Template addChunk(final String chunkText) {
        final Chunk chunk = new Chunk( chunkText );
        if ( this.start == null ) {
            this.start = chunk;
        } else {
            this.start.addToEnd( chunk );
        }
        return this;
    }

    /**
     * This will parse the input nl expression, and build a map of values for the "holes" 
     * in the grammar expression.
     * It does this by getting the Chunks of the grammar to parse themselves.
     */
    void processNL(final String nl,
                   final Map map) {
        this.start.clearValues();
        this.start.process( nl );
        this.start.buildValueMap( map );
    }

    /**
     * This builds a fragment of the nl expression which can be used
     * to swap out a piece of the original with the target expression.
     * 
     * The target expression is the "right hand side" of the grammar map.
     */
    String getSubstitutionKey() {
        final StringBuffer buffer = new StringBuffer();
        this.start.buildSubtitutionKey( buffer );
        return buffer.toString().trim(); //trim so we don't get any erroneous spaces to stop replacing.
    }

    /**
     * This will build the target string that you can use to substitute the original with.
     * @param map The map of values to hole keys.
     * @param grammar_r The grammar item which will have the values plugged into the "holes".
     * @return The final expression ready for substitution.
     */
    String populateTargetString(final Map map,
                                String grammar_r) {
        for ( final Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
            final String key = (String) iter.next();
//            grammar_r = StringUtils.replace( grammar_r,
//                                             key,
//                                             (String) map.get( key ) );
        }
        return grammar_r;
    }

    /**
     * @param nl The natural language expression.
     * @param subKey The part of the nl expression to be swapped out.
     * @param target The chunk to be swapped in to the nl
     * @return The nl with the chunk replaced with the target.
     */
    String interpolate(final String nl,
                       final String subKey,
                       final String target) {
//        return StringUtils.replace( nl,
//                                    subKey,
//                                    target );
        return null;
    }

    /**
     * This does it all as one call. Requires that chunks have been setup.
     * @param nl The nl expression to process. 
     * @param targetTemplate The target grammar expression that will be interpolated (with the values from the original chunks), 
     * and then inserted in to the nl.
     * @return the NL with the populated grammarRHS replacing the original pattern (from the chunks).
     */
    String expandOnce(final String nl,
                      final String targetTemplate) {
        final Map values = new HashMap();
        this.processNL( nl,
                        values );
        final String subKey = this.getSubstitutionKey();
        final String target = this.populateTargetString( values,
                                                   targetTemplate );
        return this.interpolate( nl,
                                 subKey,
                                 target );
    }

    /** 
     * Similar to expandOnce, but processes iteratively until there is
     * no change in the output. This allows for stuff to be repeated in an NL expression.
     */
    public String expandAll(final String nl,
                            final String targetTemplate) {
        String result = nl;

        //put an upper limit
        int i = 0;
        while ( i < 10 ) {
            final String newResult = expandOnce( result,
                                           targetTemplate );
            if ( newResult.equals( result ) ) {
                break;
            }
            result = newResult;

            i++;
            if ( i == 10 ) {
                throw new IllegalArgumentException( "To many iterations in processing the expression: [" + nl + "] with target template: [" + targetTemplate + "]" );
            }
        }
        return result;
    }

}