package org.drools.semantics.java;


import junit.framework.TestCase;

public class KnowledgeHelperFixerTest extends TestCase {
    
    private static KnowledgeHelperFixer fixer = new  KnowledgeHelperFixer();
  
        
    public void testAdd__Handle__rSimple() {
        String result = fixer.fix("modify(myObject )");
        assertEquals("drools.modifyObject(myObject__Handle__, myObject)", result);
        
        result = fixer.fix("modify ( myObject )");
        assertEquals("drools.modifyObject(myObject__Handle__, myObject)", result);
    }
    
    public void testAdd__Handle__withNewLines() {
        String result = fixer.fix("\n\t\n\tmodify(myObject )");
        assertEquals("\n\t\n\tdrools.modifyObject(myObject__Handle__, myObject)", result);        
    }    
    
    public void testAdd__Handle__rComplex() {
        String result = fixer.fix("something modify(myObject ); other");
        assertEquals("something drools.modifyObject(myObject__Handle__, myObject); other", result);
        
        result = fixer.fix("something modify (myObject )");
        assertEquals("something drools.modifyObject(myObject__Handle__, myObject)", result);
        
        result = fixer.fix(" modify(myObject ) x");
        assertEquals(" drools.modifyObject(myObject__Handle__, myObject) x", result);

        //should not touch, as it is not a stand alone word
        result = fixer.fix("xxmodify(myObject ) x");
        assertEquals("xxmodify(myObject ) x", result);               
    }
    
    public void testMultipleMatches() {
        String result = fixer.fix("modify(myObject) modify(myObject )");
        assertEquals("drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(myObject__Handle__, myObject)", result);
        
        result = fixer.fix("xxx modify(myObject ) modify(myObject ) modify(yourObject ) yyy");
        assertEquals("xxx drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(yourObject__Handle__, yourObject) yyy", result);
    }
    
    public void testAllActions() {
        String result = fixer.fix("assert(myObject ) modify(ourObject);\t retract(herObject)");
        assertEquals("drools.assertObject(myObject__Handle__, myObject) drools.modifyObject(ourObject__Handle__, ourObject);\t drools.retractObject(herObject__Handle__, herObject)", result);        
    }
    
    public void testLeaveLargeAlone() {
        String original = "yeah yeah yeah this is a long() thing Person (name=='drools') modify a thing";
        String result = fixer.fix(original);
        assertEquals(original, result);        
    }
    
    public void testWithNull() {
        String original = null;
        String result = fixer.fix(original);
        assertEquals(original, result);
    }

}
