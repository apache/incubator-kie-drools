package org.drools.natural.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import junit.framework.TestCase;

public class TemplateContextTest extends TestCase {

    
    public void testAllInOne() {
        TemplateContext ctx = new TemplateContext();        
        //chunks represent a lexed grammar "left hand side"
        ctx.addChunk("baby on board")
            .addChunk("{0}")
            .addChunk("and")
            .addChunk("{1}")
            .addChunk("burt ward");        
        String result = ctx.process("yeah this is an expression baby on board exp1 and exp2 burt ward end.", "something({0}, {1})");
        assertEquals("yeah this is an expression something(exp1, exp2) end.", result);
        
        
        //and check that the iterative one is OK.
        result = ctx.process("yeah this is an expression baby on board exp1 and exp2 burt ward end.", "something({0}, {1})");
        assertEquals("yeah this is an expression something(exp1, exp2) end.", result);        
    }    
    
    public void testBuildStrings() {
        
        TemplateContext ctx = new TemplateContext();
        
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
        
        //match the pattern in nl, put the values in the map        
        HashMap map = new HashMap();
        ctx.processNL(nl, map);
        
        //now get the chunk of nl that will be replaced with the target later.
        String subKey = ctx.getSubstitutionKey();
        assertEquals("baby on board exp1 and exp2 burt ward", subKey);
                
        String target = ctx.populateTargetString( map, grammar_r );
        assertEquals("something(exp1, exp2)", target);
                
        String result = ctx.interpolate(nl, subKey, target);
        
        assertEquals("yeah this is an expression something(exp1, exp2) end.", result);
                
        
        
    }
    

    public void testMultipleReplacement() {

        TemplateContext ctx = new TemplateContext();
        
        //chunks represent a lexed grammar "left hand side"
        ctx.addChunk("{0}")
            .addChunk("likes cheese");
        
        String nl  = "bob likes cheese and michael likes cheese conan likes cheese";
        String grammarTemplate = "{0}.likesCheese()";
        String expected = "bob.likesCheese() and michael.likesCheese() conan.likesCheese()";
        
        
        String result = ctx.processAllInstances(nl, grammarTemplate);
        assertEquals(expected, result);
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

    
    

    
    
}


