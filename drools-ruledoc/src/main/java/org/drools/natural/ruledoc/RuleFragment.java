package org.drools.natural.ruledoc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.drools.natural.NaturalLanguageCompiler;
import org.drools.natural.NaturalLanguageException;
import org.drools.natural.grammar.NaturalGrammar;

/**
 * Handles the plain text rule fragment format.
 * This is the stuff between the start rule and end rule markers
 * in a rule document, based on keywords.properties.
 * All HTML stuff is already removed.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RuleFragment {
    
    private String fragment;
    private List conditionList;
    private Properties properties;
    private List consequenceList;
    
    private final String IF = Keywords.getKeyword("rule.if");  
    private final String START = Keywords.getKeyword("rule.start");        
    private final String THEN = Keywords.getKeyword("rule.then");    
    private final String END = Keywords.getKeyword("rule.end");
    private final NaturalLanguageCompiler naturalCompiler;
    
    public RuleFragment(String fragment, NaturalGrammar grammar) {
        this.naturalCompiler = new NaturalLanguageCompiler(grammar);
        this.fragment = StringUtils.substringBetween(fragment, START, END);
        this.properties = calcProperties();
        this.conditionList = calcConditions(); 
        this.consequenceList = calcConsequences();       
        
    }
    
    public List getConditions(){
        return conditionList;
    }

    public String getFragment(){
        return fragment;
    }

    public Properties getProperties(){
        return properties;
    }
    
    public String getName() {
        return properties.getProperty(Keywords.getKeyword("rule.name"));
    }
    
    public String getSalience() {
        return properties.getProperty(Keywords.getKeyword("rule.salience"));
    }
    
    public String getDuration() {
        return properties.getProperty(Keywords.getKeyword("rule.duration"));
    }
    
    public String getXorGroup() {
        return properties.getProperty(Keywords.getKeyword("rule.xor-group"));        
    }
    
    public String getNoLoop() {
        return properties.getProperty(Keywords.getKeyword("rule.no-loop"));
    }
    
    public String getParameterDeclarations() {
        return properties.getProperty(Keywords.getKeyword("rule.parameters"));
    }
    
    
    
    

    /**
     * Return properties representing the rule properties
     * (the stuff between the start rule marker and the start of the conditions).
     * @return
     */
    private Properties calcProperties() {        
              
        String props = StringUtils.substringBefore(fragment, IF);
        Properties p = new Properties();        
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(props.getBytes("UTF-8"));
            p.load(in);
            return p;
        }
        catch ( IOException e ) {
            throw new NaturalLanguageException("Unable to load rule properties.");
        }        
    }

    private List calcConditions(){
        
        List conditionLines = new ArrayList();
        
        String conditions = StringUtils.substringBetween(fragment, IF, THEN);
        if (conditions == null || conditions.trim().equals("")) throw new NaturalLanguageException("No conditions found in rule.");
        
        StringTokenizer tk = new StringTokenizer(conditions.trim(), "\n\r");
        while (tk.hasMoreTokens()) {
            String line = tk.nextToken().trim();
            if (!line.equals("")) {
                conditionLines.add(compileToRule(line.trim()));
            }
        }
        return conditionLines;
    }
    
    private List calcConsequences(){
        List consequenceLines = new ArrayList();
        
        String consequences = StringUtils.substringAfter(fragment, THEN);
        if (consequences == null || consequences.trim().equals("")) throw new NaturalLanguageException("No consquences found in rule.");
        
        StringTokenizer tk = new StringTokenizer(consequences.trim(), "\n\r");
        while (tk.hasMoreTokens()) {
            String line = tk.nextToken().trim();
            if (!line.equals("")) consequenceLines.add(compileToRule(line.trim()));
        }
        return consequenceLines;
    }    




    private Object compileToRule(String string){        
        return naturalCompiler.compileNaturalExpression(string);        
    }

    public List getConsequences(){
        return this.consequenceList;
    }
    
    public String toString() {
        return this.fragment;
    }
    
    
}
