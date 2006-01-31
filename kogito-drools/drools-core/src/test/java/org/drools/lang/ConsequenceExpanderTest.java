package org.drools.lang;

import junit.framework.TestCase;

public class ConsequenceExpanderTest extends TestCase {

    public static ConsequenceExpander helper = new ConsequenceExpander();
    
    public void testAddHandlerSimple() {
        String result = helper.knowledgeHelperFixer("modify(myObject )");
        assertEquals("drools.modify(myObjectHandle, myObject)", result);
        
        result = helper.knowledgeHelperFixer("modify ( myObject )");
        assertEquals("drools.modify(myObjectHandle, myObject)", result);
    }
    
    public void testAddHandlerComplex() {
        String result = helper.knowledgeHelperFixer("something modify(myObject ); other");
        assertEquals("something drools.modify(myObjectHandle, myObject); other", result);
        
        result = helper.knowledgeHelperFixer("something modify (myObject )");
        assertEquals("something drools.modify(myObjectHandle, myObject)", result);
        
        result = helper.knowledgeHelperFixer(" modify(myObject ) x");
        assertEquals(" drools.modify(myObjectHandle, myObject) x", result);

        //should not touch, as it is not a stand alone word
        result = helper.knowledgeHelperFixer("xxmodify(myObject ) x");
        assertEquals("xxmodify(myObject ) x", result);
        
        
    }
    
    public void testMultipleMatches() {
        String result = helper.knowledgeHelperFixer("modify(myObject) modify(myObject )");
        assertEquals("drools.modify(myObjectHandle, myObject) drools.modify(myObjectHandle, myObject)", result);
        
        result = helper.knowledgeHelperFixer("xxx modify(myObject ) modify(myObject ) modify(yourObject ) yyy");
        assertEquals("xxx drools.modify(myObjectHandle, myObject) drools.modify(myObjectHandle, myObject) drools.modify(yourObjectHandle, yourObject) yyy", result);
    }
    
    public void testAllActions() {
        String result = helper.knowledgeHelperFixer("assert(myObject ) modify(ourObject);\t retract(herObject)");
        assertEquals("drools.assert(myObjectHandle, myObject) drools.modify(ourObjectHandle, ourObject);\t drools.retract(herObjectHandle, herObject)", result);        
    }
    
    public void testLeaveLargeAlone() {
        String original = "yeah yeah yeah this is a long() thing Person (name=='drools') modify a thing";
        String result = helper.knowledgeHelperFixer(original);
        assertEquals(original, result);        
    }
    
    
}
