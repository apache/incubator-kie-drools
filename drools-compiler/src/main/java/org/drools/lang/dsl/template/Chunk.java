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

import java.util.Map;

/**
 * This holds a linked list of chunks of natural language.
 * A chunk is basically some text, which is delimited by "holes" in the template.
 * 
 * eg: "this is {0} an {1} expression"
 * Would have 5 chunks: "this is", "{0}", "an", "{1}" and "expression".
 * 
 * Chunks also know how to parse themselves to work out the value.
 * 
 * This is used by Template to do the bulk of the work.
 * This class is very recursive, to be prepated to be confused.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
class Chunk {

    //the chunk of text from the dicitonary   
    final String  text;
    final boolean isHole;

    Chunk         next;

    //for building the substitution string, remember how the {0} was spaced with the rest of the string
    //for example: date of '{0}' ---- this needs to be handled as well as: date of ' {0} '
    String        padL = "";
    String        padR = "";

    //value parsed out if it is a hole, starts out as null.
    String        value;

    Chunk(final String text) {
        this.text = text;
        if ( text.startsWith( "{" ) ) {
            this.isHole = true;
        } else {
            this.isHole = false;
        }
    }

    /**
     * This will build up a key to use to substitute the original string with.
     * Can then swap it with the target text.
     */
    void buildSubtitutionKey(final StringBuffer buffer) {
        if ( this.isHole ) {
            buffer.append( this.padL + this.value + this.padR );
        } else {
            buffer.append( this.text );
        }
        if ( this.next != null ) {
            this.next.buildSubtitutionKey( buffer );
        }
    }

    void process(final String expression) {
        if ( this.isHole ) {
            //value = text until next next.text is found
            if ( this.next == null || this.next.text == null ) {
                storeSpacePadding( expression );
                this.value = expression.trim();
            } else {
//                final String val = StringUtils.substringBefore( expression,
//                                                          this.next.text );
//                storeSpacePadding( val );
//                this.value = val.trim();
            }

        } else {
            this.value = this.text;
        }
        if ( this.next != null ) {
//            this.next.process( StringUtils.substringAfter( expression,
//                                                      this.value ) );
        }
    }

    private void storeSpacePadding(final String val) {
        if ( val.startsWith( " " ) ) {
            this.padL = " ";
        }
        if ( val.endsWith( " " ) ) {
            this.padR = " ";
        }
    }

    void buildValueMap(final Map map) {
        if ( this.isHole ) {
            map.put( this.text,
                     this.value );
        }
        if ( this.next != null ) {
            this.next.buildValueMap( map );
        }
    }

    void addToEnd(final Chunk chunk) {
        if ( this.next == null ) {
            this.next = chunk;
        } else {
            this.next.addToEnd( chunk );
        }
    }

    /** recursively reset the values */
    public void clearValues() {
        this.value = null;
        if ( this.next != null ) {
            this.next.clearValues();
        }
    }

}