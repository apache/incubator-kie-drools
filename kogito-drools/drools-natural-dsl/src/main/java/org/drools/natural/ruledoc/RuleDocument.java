package org.drools.natural.ruledoc;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.ruledoc.dumper.RuleDumper;
import org.drools.natural.ruledoc.html.HTMLDocParser;

/**
 * This is the class that does it all for rule documents.
 * Rule documents are HTML documents marked up to contain rules.
 * Rules are extracted, and then processed according to the grammar specified.
 * 
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleDocument
{

    private NaturalGrammar grammar;
    private DictionaryHelper dictionary;
    
    
    /** 
     * @param dictionary A dictionary to contain the language definition, as well as ruleset info
     */
    public RuleDocument(Properties dictionary) {
        DictionaryHelper dic = new DictionaryHelper(dictionary);
        this.grammar = dic.getGrammar();
        this.dictionary = dic;
    }
    
    public void generateFromDocument(URL document, StringWriter result, String template) {
        List rules = buildRuleListFromDocument(document, grammar);
        RuleDumper dumper = new RuleDumper(template);
        dumper.dump(result, rules, dictionary);            
    }
    
    /** use the default templates to render the document */
    public void generateFromDocument(URL document, StringWriter result) {
        generateFromDocument(document, result, "org/drools/natural/ruledoc/dumper/html_report.vm");
    }
    
    List buildRuleListFromDocument(URL document, NaturalGrammar grammar) {
        HTMLDocParser parser = new HTMLDocParser();
        RuleDocumentListener listener = new RuleDocumentListener();
        parser.parseDocument(document, listener);        
        List fragmentList = new ArrayList();        
        for ( Iterator iter = listener.getRules().iterator(); iter.hasNext(); ) {
            String fragment = (String) iter.next();            
            fragmentList.add(new RuleFragment(fragment, grammar));            
        }
        return fragmentList;
        
    }
    
    
}
