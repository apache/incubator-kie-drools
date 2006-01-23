package org.drools.natural.template;

import java.util.Collection;
import java.util.Iterator;

/**
 * This is the utility class for compiling pseudo natural/DSL expression into the target 
 * language, via the supplied mappings.
 * 
 * This version works off "string templates" rather then infix operators.
 * 
 * Note that this is not particularly efficient for large grammars - IN THEORY !
 * However, I have tested it with grammars of 200 000 terms, and it took less then a second per expression, 
 * so its no slouch. This could be a problem for bulk compiling of large rulesets with thousands of conditions.
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

    private TemplateFactory factory;
    private Collection grammar;
    
    public NLExpressionCompiler(NLGrammar grammar) {
        this.grammar = grammar.getMappings();
        this.factory = new TemplateFactory();
    }
    
    public String compile(String expression) {
        String nl = expression;
        for ( Iterator iter = grammar.iterator(); iter.hasNext(); ) {
            NLMappingItem mapping = (NLMappingItem) iter.next();
            TemplateContext ctx = factory.buildContext(mapping.getNaturalTemplate());
            nl = ctx.processAllInstances(nl, mapping.getGrammarTemplate());
        }
        return nl;
    }
    
    
}
