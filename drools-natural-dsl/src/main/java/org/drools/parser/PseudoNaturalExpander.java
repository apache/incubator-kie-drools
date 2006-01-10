package org.drools.parser;

import java.util.Properties;

import org.drools.natural.NaturalLanguageCompiler;
import org.drools.natural.grammar.SimpleGrammar;

public class PseudoNaturalExpander
    implements
    ExpressionExpander {

    private NaturalLanguageCompiler compiler;
    
    public PseudoNaturalExpander(Properties grammar) {
        SimpleGrammar g = new SimpleGrammar(grammar);
        compiler = new NaturalLanguageCompiler(g);
    }
    
    public String expandExpression(String expr) {        
        return compiler.compileNaturalExpression(expr);
    }

    public boolean isExpanded(String expression) {
        if (expression.startsWith("--")) {
            return true;
        } else {
            return false;
        }
    }

}
