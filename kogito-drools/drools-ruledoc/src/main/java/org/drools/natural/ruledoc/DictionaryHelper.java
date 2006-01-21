package org.drools.natural.ruledoc;

import java.util.Iterator;
import java.util.Properties;

import org.drools.natural.grammar.NaturalGrammar;
import org.drools.natural.grammar.SimpleGrammar;

/** 
 * For working with the ruledoc dictionary.
 * This will get ruleset level properties, such as imports, 
 * as well as providing the DSL dictionary itself.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class DictionaryHelper
{
    
    private Properties props;
    
    public DictionaryHelper(Properties properties) {
        props = properties;
    }
    
    public String getItem(String key) {
        return props.getProperty(key);
    }
    
    public String getFunctions() {
        return props.getProperty("functions");
    }
    
    public String getApplicationData() {
        return props.getProperty("application-data");
    }
    
    public String getRulesetName() {
        return props.getProperty("ruleset-name");
    }    
    
    public NaturalGrammar getGrammar() {
        SimpleGrammar grammar = new SimpleGrammar();
        for ( Iterator iter = props.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            if (key.equals("import") ||
                    key.equals("imports") ||
                    key.equals("functions") ||
                    key.equals("application-data") ||
                    key.equals("ruleset-name")) {
                //ignore
            } else {
                grammar.addToDictionary(key, props.getProperty(key));
            }
            
        }
        return grammar;
    }
    
    public String getImports() {
        if (props.containsKey("import")) return props.getProperty("import");        
        return props.getProperty("imports");
    }
}
