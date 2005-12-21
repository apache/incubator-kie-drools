package org.drools.natural.ruledoc;

import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

public class RuleDocumentTest extends TestCase
{
    public void testSimpleParser() {
        URL url = this.getClass().getResource("simple-ruledoc.html");        
        RuleDocument doc = new RuleDocument();
        List rules = doc.buildRuleListFromDocument(url, new Properties());
        assertEquals(1, rules.size());
        System.out.println(rules.get(0));        
    }

    
    public void testTheSpec() {
        URL url = this.getClass().getResource("NaturalRulesSpec.html");        
        RuleDocument doc = new RuleDocument();
        List rules = doc.buildRuleListFromDocument(url, new Properties());
        assertEquals(1, rules.size());
        System.out.println(rules.get(0));        
    }    
    
}
