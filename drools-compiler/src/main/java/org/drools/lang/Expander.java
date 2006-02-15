package org.drools.lang;

/**
 * Expanders are extension points for expanding 
 * expressions in DRL at parse time.
 * This is just-in-time translation, or macro expansion, or
 * whatever you want.
 * 
 * The important thing is that it happens at the last possible moment, 
 * so any errors in expansion are included in the parsers errors.
 * 
 * Just-in-time expansions may include complex pre-compilers, 
 * or just macros, and everything in between.
 * 
 * Expanders should ideally not make presumptions on any embedded semantic 
 * language. For instance, java aware pre processing should be done in
 * drools-java semantic module, not in the parser itself. Expanders should 
 * be reusable across semantic languages. 
 * 
 * @author Michael Neale
 *
 */
public interface Expander {

    /**
     * The parser should call this on an expression/line that potentially needs expanding 
     * BEFORE it parses that line (as the line may change radically as the result of expansion).
     * 
     * Expands the expression Just-In-Time for the parser.
     * If the expression is not meant to be expanded, or if no
     * appropriate expander is found, it will echo back the same 
     * expression.
     * 
     * @param expression The "line" or expression to be expanded/pre-compiled.
     * @param context The context of the current state of parsing. This can help
     * the expander know if it needs to expand, what to do etc.
     * 
     * If <code>isEnabled()</code> is false then it is not required to 
     * call this method.
     */
    public String expand(String pattern, Parser context);
    
    
    
}
