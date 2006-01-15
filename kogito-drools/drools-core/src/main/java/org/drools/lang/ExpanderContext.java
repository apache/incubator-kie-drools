package org.drools.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class ExpanderContext implements Serializable {

	private static final long serialVersionUID = 1806461802987228880L;
	private boolean enabled = true;
	
	private final Set expanders;
	
	/**
	 * This indicates that at least one expander has been configured for 
	 * this parser configuration.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public ExpanderContext(Collection initialExpanders) {
		this.expanders = new HashSet(initialExpanders);
	}
	
	public ExpanderContext() {	
		this.expanders = new HashSet();
	}
	
	public ExpanderContext addExpander(Expander exp) {
		expanders.add(exp);
		return this;
	}
	
	/**
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
	public CharSequence expand(CharSequence expression, RuleParser context) {
		return expression;
	}
	
	
	
}
