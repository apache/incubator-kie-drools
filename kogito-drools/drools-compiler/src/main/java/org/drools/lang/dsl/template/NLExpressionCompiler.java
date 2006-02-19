package org.drools.lang.dsl.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is the utility class for compiling pseudo natural/DSL expression into the target 
 * language, via the supplied mappings.
 * 
 * This version works off "string templates" rather then infix operators.
 * 
 * Note that this is not particularly efficient for large grammars - IN THEORY !
 * However, I have tested it with grammars of 200 000 terms, and it took less then a second per expression, 
 * so its no slouch. This could be a problem for bulk compiling of large pkgs with thousands of conditions.
 * 
 * In general, grammars of < 1000 items should be fine. The cost is a parse time cost for Drools, which can be done
 * incrementally in a suitable environment ideally anyway.
 * 
 * It will go through each item in the grammar, trying to apply it regardless of if it is needed.
 * This may be improved by some early regex scanning, but most likely this will not really save much.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class NLExpressionCompiler {

    //a map of templates to the template contexts (template contexts are used to populate populate
    //the target mappings with data from a real expression.
    private Map templateCache;
    
    
    public NLExpressionCompiler(NLGrammar grammar) {
        Collection grammarItems = grammar.getMappings();        
        this.templateCache = new HashMap();

        //build up a map of templates
        TemplateFactory factory = new TemplateFactory();
        for ( Iterator iter = grammarItems.iterator(); iter.hasNext(); ) {
            NLMappingItem mapping = (NLMappingItem) iter.next();
            Template template = factory.getTemplate(mapping.getNaturalTemplate());
            templateCache.put(mapping, template);
        }
    }
    
    /**
     * This will iterate through the grammar, trying to match any grammar templates with the expression.
     * When it can, it will pull the values out of the expression, put them in the target string, and then swap it out with 
     * the original, and then move on to the next item from the grammar/dictionary.
     */
    public String compile(String expression) {
        String expanded = expression;
        for ( Iterator iter = templateCache.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            NLMappingItem mapping = (NLMappingItem) entry.getKey();
            Template template = (Template) entry.getValue();
            expanded = template.expandAll(expanded, mapping.getTargetTemplate());
        }
        return expanded;
    }
    
    
}
