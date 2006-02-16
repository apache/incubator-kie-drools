package org.drools.lang.dsl.template;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This holds a linked list of chunks of natural language.
 * A chunk is basically some text, which is delimited by "holes" in the template.
 * 
 * eg: "this is {0} an {1} expression"
 * Would have 5 chunks: "this is", "{0}", "an", "{1}" and "expression".
 * 
 * Chunks also know how to parse themselves to work out the value.
 * 
 * This is used by TemplateContext.
 * This class is very recursive, to be prepated to be confused.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
class Chunk {
    
    //the chunk of text from the dicitonary   
    final String text;
    final boolean isHole;
    
    Chunk next;
    
    //for building the substitution string, remember how the {0} was spaced with the rest of the string
    //for example: date of '{0}' ---- this needs to be handled as well as: date of ' {0} '
    String padL = "";
    String padR = "";
    
    //value parsed out if it is a hole, starts out as null.
    String value;
    
    Chunk(String text)  {
        this.text = text;
        if (text.startsWith("{")) {
            isHole = true;
        } else {
            isHole = false;
        }
    }
    
    /**
     * This will build up a key to use to substitute the original string with.
     * Can then swap it with the target text.
     */
    void buildSubtitutionKey(StringBuffer buffer) {
        if (isHole) {
            buffer.append(padL + value + padR);
        } else {
            buffer.append(text);
        }
        if (next != null) {
            next.buildSubtitutionKey(buffer);
        }
    }

    void process(String expression) {
        if (isHole) {
            //value = text until next next.text is found
            if (next == null || next.text == null) {
                storeSpacePadding( expression );
                value = expression.trim();
            } else {
                String val = StringUtils.substringBefore(expression, next.text);
                storeSpacePadding( val );
                value = val.trim();
            }
            
        } else {
            value = text;
        }
        if (next != null) {
            next.process(StringUtils.substringAfter(expression, value));
        }            
    }

    private void storeSpacePadding(String val) {
        if (val.startsWith(" ")) padL = " ";
        if (val.endsWith(" ")) padR = " ";
    }
    
    void buildValueMap(Map map) {
        if (isHole) {
            map.put(text, value);
        }
        if (next != null) {
            next.buildValueMap(map);
        }
    }
    
    void addToEnd(Chunk chunk) {
        if (next == null) {
            next = chunk;
        } else {
            next.addToEnd(chunk);
        }
    }


    /** recursively reset the values */
    public void clearValues() {
        this.value = null;
        if (this.next != null) {
            next.clearValues();
        }
    }
    
}
