package org.drools.lang.dsl.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private NLGrammar grammar;
    
    public NLExpressionCompiler(NLGrammar grammar) {
        List grammarItems = grammar.getMappings();        
        this.templateCache = new HashMap();
        this.grammar = grammar;
        
        //build up a cache of templates
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
     * 
     * scope is to indicate if it is expanding condition or consequence.
     * (null will only expand globals).
     */
    public String compile(String expression, String scope) {
        String expanded = expression;
        
        List mappings = grammar.getMappings();

        expanded = processMappings( expanded,
                                    mappings,
                                    "*");
        if (scope != null) {
            expanded = processMappings( expanded, 
                                        mappings, 
                                        scope );
        }
        if (expanded.equals( expression )) {
            throw new IllegalArgumentException("Expression was not expandable: " + expression);
        }
        return expanded;
    }
    

    private String processMappings(String input,
                                   List mappings, 
                                   String scope) {
        for ( Iterator iter = mappings.iterator(); iter.hasNext(); ) {
            //get the template and apply it
            NLMappingItem mapping = (NLMappingItem) iter.next();
            if (mapping.getScope().equals( scope )) { 
                Template template = (Template) templateCache.get( mapping );
                input = template.expandAll(input, mapping.getTargetTemplate());
            }
        }
        return input;
    }
    
    
    
    
}
