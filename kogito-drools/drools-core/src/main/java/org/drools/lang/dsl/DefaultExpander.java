package org.drools.lang.dsl;

import java.util.Properties;

import org.drools.lang.Expander;
import org.drools.lang.Parser;
import org.drools.lang.dsl.template.NLExpressionCompiler;
import org.drools.lang.dsl.template.NLGrammar;

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
                         Parser context) {        
        return compiler.compile(pattern);
    }
    
    /**
     * Properties contain the mapping between the language expressions, and the target expressions.
     * Use {0} style notation to place "holes" where data will be parsed from the natural text input.
     * 
     * @see org.drools.lang.dsl.template.NLExpressionCompiler for details.
     */
    public DefaultExpander(Properties props) {
        NLGrammar grammar = new NLGrammar();
        grammar.loadFromProperties(props);
        compiler = new NLExpressionCompiler(grammar);
    }

}
