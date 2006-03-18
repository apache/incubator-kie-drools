package org.drools.lang.dsl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
 */
public class DefaultExpander
    implements
    Expander {

    private NLExpressionCompiler compiler; 
    
    public String expand(String scope,
    						String pattern) {  
        
        return compiler.compile(pattern, scope);
    }
    
    /**
     * Use {0} style notation to place "holes" where data will be parsed from the natural text input.
     * 
     * @see org.drools.lang.dsl.template.NLExpressionCompiler for details.
     */
    public DefaultExpander(Reader reader) {
        NLGrammar grammar = new NLGrammar();
        grammar.load( reader );
        compiler = new NLExpressionCompiler(grammar);
    }
    
    

}
