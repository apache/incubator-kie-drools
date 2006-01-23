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
     */
    public void testBuildStrings() {
        
        Context ctx = new Context();
        
        //chunks represent a lexed grammar "left hand side"
        ctx.addChunk("baby on board")
            .addChunk("{0}")
            .addChunk("and")
            .addChunk("{1}")
            .addChunk("burt ward");
        
        //and this is the right hand side grammar mapping (no lexing required, simple hole filling !).
        String grammar_r = "something({0}, {1})";

        
        //and this is the full expression
        String nl = "yeah this is an expression baby on board exp1 and exp2 burt ward end.";
        
        //match the pattern, put the values in the map        
        HashMap map = new HashMap();
        ctx.processNL(nl, map);
        
        //now get the chunk of nl that will be replaced with the target later.
        String subKey = ctx.getSubstitutionKey();
        assertEquals("baby on board exp1 and exp2 burt ward", subKey);
                
        String target = ctx.populateTargetString( map, grammar_r );
        assertEquals("something(exp1, exp2)", target);
                
        String result = ctx.replaceNlWithTarget(nl, subKey, target);
        
        assertEquals("yeah this is an expression something(exp1, exp2) end.", result);
                
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
        
        Chunk start;
        
        /**
         * Ad a chunk from the dictionary expression.
         * A chunk is a piece of nl, or a hole.
         * nl & holes should not be mixed.
         */
        Context addChunk(String chunkText) {
            Chunk chunk = new Chunk(chunkText);
            if (start == null) {
                start = chunk;
            } else {
                start.addToEnd(chunk);
            }
            return this;
        }
        
        /**
         * This will parse the input nl expression, and build a map of values for the "holes" 
         * in the grammar expression.
         * It does this by getting the Chunks of the grammar to parse themselves.
         */
        void processNL(String nl, Map map) {
           start.process(nl);
           start.buildValueMap(map);
        }
        
        /**
         * This builds a fragment of the nl expression which can be used
         * to swap out a piece of the original with the target expression.
         * 
         *  The target expression is the "right hand side" of the grammar map.
         */
        String getSubstitutionKey() {
            StringBuffer buffer = new StringBuffer();
            start.buildSubtitutionKey(buffer);
            return buffer.toString();
        }
        
        /**
         * This will build the target string that you can use to substitute the original with.
         * @param map The map of values to hole keys.
         * @param grammar_r The grammar item which will have the values plugged into the "holes".
         * @return The final expression ready for substitution.
         */
        String populateTargetString(HashMap map,
                                   String grammar_r) {
            for ( Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                grammar_r = StringUtils.replace(grammar_r, key, (String) map.get(key));
            }
            return grammar_r;
        }   
        
        /**
         * @param nl The natural language expression.
         * @param subKey The part of the nl expression to be swapped out.
         * @param target The chunk to be swapped in to the nl
         * @return The nl with the chunk replaced with the target.
         */
        String replaceNlWithTarget(String nl, String subKey, String target) {
            return StringUtils.replace(nl, subKey, target);            
        }
                
        
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
        
        void addToEnd(Chunk chunk) {
            if (next == null) {
                next = chunk;
            } else {
                next.addToEnd(chunk);
            }
        }
        
    }
    
    
}


