package org.drools.natural.ruledoc;

import java.util.List;
import java.util.Properties;

import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

public class RuleFragmentTest extends TestCase {
    
    private SimpleGrammar grammar = new SimpleGrammar();
    
    
    public void testGetRuleProperties() {
        String fragment = "Start-rule\n\r " +
                            "name: ruleName\n\t" +
                            "priority: \trule Priority\n\r" +
                            " IF... THEN xxx End-rule";
        RuleFragment helper = new RuleFragment(fragment, grammar);
        Properties props = helper.getProperties();
        assertEquals(2, props.size());
        assertEquals("ruleName", props.getProperty("name"));
        assertEquals("rule Priority", props.getProperty("priority"));
    }
    
    public void testGetConditions() {
        String fragment = "Start-rule\n\r " +
            "name: ruleName\n\t" +
            "priority: \trule Priority\n\r" +
            " IF condition 1   \n \t    condition 2 \r\t\n condition 3 THEN xxx End-rule";
        RuleFragment helper = new RuleFragment(fragment, grammar);
        List list = helper.getConditions();
        assertEquals(3, list.size());
        assertEquals("condition 1", list.get(0));
        assertEquals("condition 2", list.get(1));
        assertEquals("condition 3", list.get(2));
    }
    
    public void testGetConsequences() {
        String fragment = "Start-rule\n\r " +
        "name: ruleName" +
        " IF condition 1   \n \t    condition 2 \r\t\n condition 3 THEN cons1 \r cons2 \n\n End-rule";
        
        RuleFragment helper = new RuleFragment(fragment, grammar);
        List cons = helper.getConsequences();
        assertEquals(2, cons.size());
        assertEquals("cons1", cons.get(0));
        assertEquals("cons2", cons.get(1));
        
        
    }
    
    public void testSingleConsequence() {
        String fragment = "Start-rule\n\r " +
        "name: ruleName" +
        " IF condition 1   \n \t    condition 2 \r\t\n condition 3 THEN [cons  1] End-rule";
        
        RuleFragment helper = new RuleFragment(fragment, grammar);
        List cons = helper.getConsequences();
        assertEquals(1, cons.size());
        assertEquals("cons 1", cons.get(0));
    }    
    
    
    

}
