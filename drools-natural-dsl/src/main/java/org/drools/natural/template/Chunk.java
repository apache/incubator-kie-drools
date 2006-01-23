package org.drools.natural.template;

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
 * This is used by TemplatePopulateContext.
 * This class is very recursive, to be prepated to be confused.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
class Chunk {
    
    String text;
    Chunk next;
    
    //value if it is a hole, starts out as null.
    String value;
    
    
    Chunk(String text)  {
        this.text = text;
    }
    
    
    
    /**
     * This will build up a key to use to substitute the original string with.
     * Can then swap it with the target text.
     */
    void buildSubtitutionKey(StringBuffer buffer) {
        if (isHole()) {
            buffer.append(" " + value + " ");
        } else {
            buffer.append(text);
        }
        if (next != null) {
            next.buildSubtitutionKey(buffer);
        }
    }

    boolean isHole() {
        return text.startsWith("{");
    }
    
    void process(String expression) {
        if (isHole()) {
            //value = text until next next.text is found
            if (next == null || next.text == null) {
                value = expression.trim();
            } else {
                value = StringUtils.substringBefore(expression, next.text).trim();
            }
            
        } else {
            value = text;
        }
        if (next != null) {
            next.process(StringUtils.substringAfter(expression, value));
        }            
    }
    
    void buildValueMap(Map map) {
        if (this.isHole()) {
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
