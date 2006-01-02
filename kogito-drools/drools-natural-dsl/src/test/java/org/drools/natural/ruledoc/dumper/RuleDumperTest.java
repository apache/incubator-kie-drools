package org.drools.natural.ruledoc.dumper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.natural.grammar.SimpleGrammar;
import org.drools.natural.ruledoc.DictionaryHelper;
import org.drools.natural.ruledoc.RuleFragment;

import junit.framework.TestCase;

public class RuleDumperTest extends TestCase {
    
    
    public void testDump() throws Exception {
        
        RuleDumper dumper = new RuleDumper("org/drools/natural/ruledoc/dumper/test.vm");
        StringWriter writer = new StringWriter();
        
        SimpleGrammar g = new SimpleGrammar();
        
        RuleFragment frag = new RuleFragment("Start-rule  Name: michael \n IF cond THEN cons End-rule", g);
        List list = new ArrayList();
        list.add(frag);
        list.add(frag);
        
        Properties props = new Properties();
        props.setProperty("imports", "String, Integer");
        props.setProperty("application-data", "String appName");
        props.setProperty("functions", "public boolean yes();");
        props.setProperty("ruleset-name", "rulename here");
        DictionaryHelper dic = new DictionaryHelper(props);
        dumper.dump(writer, list, dic);
        assertTrue(writer.getBuffer().toString().indexOf("String, Integer") > 0);
        
        
    }
    
    
}
