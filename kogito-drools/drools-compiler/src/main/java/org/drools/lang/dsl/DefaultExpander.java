package org.drools.lang.dsl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.drools.lang.Expander;
import org.drools.lang.RuleParser;
import org.drools.lang.dsl.template.NLExpressionCompiler;
import org.drools.lang.dsl.template.NLGrammar;
import org.drools.lang.dsl.template.NLMappingItem;

/** 
 * The default expander uses String templates to provide pseudo natural language,
 * as well as general DSLs.
 * 
 * For most people, this should do the job just fine. 
 * TODO: refactor out the template stuff into the natural module.
 */
public class DefaultExpander
    implements
    Expander {

    private NLExpressionCompiler compiler; 
    
    public String expand(String pattern,
                         RuleParser context) {  
        
        return compiler.compile(pattern, null);
    }
    
    /**
     * Properties contain the mapping between the language expressions, and the target expressions.
     * Use {0} style notation to place "holes" where data will be parsed from the natural text input.
     * 
     * @see org.drools.lang.dsl.template.NLExpressionCompiler for details.
     */
    public DefaultExpander(Properties props) {
        NLGrammar grammar = new NLGrammar();
        for ( Iterator iter = props.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry element = (Map.Entry) iter.next();
            grammar.addNLItem( new NLMappingItem((String)element.getKey(), (String)element.getValue(), "*"));
            
        }
        //grammar.load(null);
        compiler = new NLExpressionCompiler(grammar);
    }

}
