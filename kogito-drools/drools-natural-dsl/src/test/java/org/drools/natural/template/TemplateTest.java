package org.drools.natural.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class TemplateTest extends TestCase {

    String grammar_l = "baby on board {0} and {1} burt ward";
    String grammar_r = "something({0}, {1})";
    String result = "yeah this is an expression something(exp1, exp2)";
    

    
    /**
     * Lets try it all together. 
     * Print out the target string.
     * Print out substitution string.
     */
    public void testBuildStrings() {
        Chunk chunk1 = new Chunk("baby on board");
        Chunk chunk2 = new Chunk("{0}");
        Chunk chunk3 = new Chunk("and");
        Chunk chunk4 = new Chunk("{1}");
        Chunk chunk5 = new Chunk("burt ward");
        
        chunk1.next = chunk2;
        chunk2.next = chunk3;
        chunk3.next = chunk4;
        chunk4.next = chunk5;        
        
        String nl = "yeah this is an expression baby on board exp1 and exp2 burt ward end.";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);

        StringBuffer buffer = new StringBuffer();
        chunk1.buildSubtitutionKey(buffer);
        
        assertEquals("baby on board exp1 and exp2 burt ward", buffer.toString());
        
        String grammar_r = "something({0}, {1})";
        
        String target = buildTarget( map,
                                     grammar_r );
        
        String result = StringUtils.replace(nl, buffer.toString(), target);
        assertEquals("yeah this is an expression something(exp1, exp2) end.", result);
        
        
    }


    private String buildTarget(HashMap map,
                               String grammar_r) {
        for ( Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            grammar_r = StringUtils.replace(grammar_r, key, (String) map.get(key));
        }
        return grammar_r;
    }
    
    
    public void testBasicExpression() {

        Chunk chunk1 = new Chunk("baby on board");
        Chunk chunk2 = new Chunk("{0}");
        Chunk chunk3 = new Chunk("and");
        Chunk chunk4 = new Chunk("{1}");
        Chunk chunk5 = new Chunk("burt ward");
        
        chunk1.next = chunk2;
        chunk2.next = chunk3;
        chunk3.next = chunk4;
        chunk4.next = chunk5;        
        
        String nl = "yeah this is an expression baby on board exp1 and exp2 burt ward";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);
        
        assertEquals("exp1", map.get("{0}"));
        assertEquals("exp2", map.get("{1}"));
        
    }
    
    public void testStartWith() {

        Chunk chunk1 = new Chunk("{0}");
        Chunk chunk2 = new Chunk("a thing");
        Chunk chunk3 = new Chunk("and");
        Chunk chunk4 = new Chunk("{1}");
        Chunk chunk5 = new Chunk("one more");
        
        chunk1.next = chunk2;
        chunk2.next = chunk3;
        chunk3.next = chunk4;
        chunk4.next = chunk5;        
        
        String nl = "exp1 a thing and exp2 one more";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);
        
        assertEquals("exp1", map.get("{0}"));
        assertEquals("exp2", map.get("{1}"));
        
    }

    
    public void testEndWith() {

        Chunk chunk1 = new Chunk("blah blah blah");
        Chunk chunk2 = new Chunk("{1}");
        
        chunk1.next = chunk2;
        
        String nl = "blah blah blah exp1";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);
        
        assertEquals("exp1", map.get("{1}"));
        assertEquals(1, map.size());
    }
    
    public void testOneInTheMiddle() {
        Chunk chunk1 = new Chunk("yeah ");
        Chunk chunk2 = new Chunk("{abc}");
        Chunk chunk3 = new Chunk("one more");
        
        chunk1.next = chunk2;
        chunk2.next = chunk3;
        
        String nl = "yeah exp1 one more ";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);
        
        assertEquals("exp1", map.get("{abc}"));
                
    }
    
    public void testNoTokens() {
        Chunk chunk1 = new Chunk("yeah ");
        
        String nl = "yeah exp1 one more ";
        chunk1.process(nl);
        
        HashMap map = new HashMap();
        chunk1.buildValueMap(map);
        
        assertEquals(0, map.size());
    }

    
    static class Context {
        
                
        
    }
    
    static class Chunk {
        String text;
        Chunk next;
        
        //value if it is a hole
        String value;
        
        
        Chunk(String text)  {
            this.text = text;
        }
        
        
        
        /**
         * This will build up a key to use to substitute the original string with.
         * Can then swap it with the target text.
         */
        public void buildSubtitutionKey(StringBuffer buffer) {
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
                next.process(StringUtils.substringAfterLast(expression, value));
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
        
    }
    
    
}


