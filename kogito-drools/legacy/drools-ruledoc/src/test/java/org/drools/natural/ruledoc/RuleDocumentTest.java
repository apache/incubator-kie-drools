package org.drools.natural.ruledoc;

import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import org.drools.natural.grammar.SimpleGrammar;

import junit.framework.TestCase;

public class RuleDocumentTest extends TestCase
{
    
    
    public void testSmoke1() {
        URL url = this.getClass().getResource("simple-ruledoc.html");        
        RuleDocument doc = new RuleDocument(new Properties());
        List rules = doc.buildRuleListFromDocument(url, new SimpleGrammar());
        assertEquals(1, rules.size());
        //System.out.println(rules.get(0));        
    }

    
    public void testSmoke2() {
        URL url = this.getClass().getResource("natural-rules-spec.html");        
        RuleDocument doc = new RuleDocument(new Properties());
        List rules = doc.buildRuleListFromDocument(url, new SimpleGrammar());
        assertEquals(1, rules.size());
        //System.out.println(rules.get(0));        
    }   
    
    public void testIntegration() throws Exception {
        URL url = this.getClass().getResource("natural-rules-spec.html");
        Properties props = new Properties();
        InputStream stream = this.getClass().getResourceAsStream("sample.dictionary.properties");
        props.load(stream);
        RuleDocument doc = new RuleDocument(props);
        StringWriter writer = new StringWriter();
        doc.generateFromDocument(url, writer);
        System.out.println(writer.getBuffer().toString());
        
    }
    
}
