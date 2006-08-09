package org.drools.natural.ruledoc;

import java.util.List;

import junit.framework.TestCase;

public class RuleDocumentListenerTest extends TestCase
{

    public void testListBuilder() {
        RuleDocumentListener listener = new RuleDocumentListener();
        listener.handleText("ignore me\t \n");
        listener.handleText("Start-rule");
        listener.handleText("in a rule");
        listener.startComment();
        listener.handleText("a comment");
        listener.endComment();
        listener.handleText("End-rule");
        
        listener.handleText("Start-rule something here");
        listener.handleText("in a rule");
        listener.handleText("End-rule");

        
        List list = listener.getRules();
        assertEquals(2, list.size());
        String rule = (String) list.get(0);
        assertEquals("Start-rulein a ruleEnd-rule", rule);
        
    }
    
}
