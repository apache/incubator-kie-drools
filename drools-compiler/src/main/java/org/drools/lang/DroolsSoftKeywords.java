package org.drools.lang;

import org.drools.RuntimeDroolsException;
import org.drools.base.evaluators.Operator;

/**
 * Simple holder class identifying all the DRL soft keywords. This is used by
 * DRLParser.
 * 
 * @author porcelli
 */
public class DroolsSoftKeywords {
	public static final String DATE = "date";
	public static final String EFFECTIVE = "effective";
	public static final String EXPIRES = "expires";
	public static final String LOCK = "lock";
	public static final String ON = "on";
	public static final String ACTIVE = "active";
	public static final String NO = "no";
	public static final String LOOP = "loop";
	public static final String AUTO = "auto";
	public static final String FOCUS = "focus";
	public static final String ACTIVATION = "activation";
	public static final String GROUP = "group";
	public static final String AGENDA = "agenda";
	public static final String RULEFLOW = "ruleflow";
    public static final String DURATION = "duration";
    public static final String TIMER = "timer";
	public static final String PACKAGE = "package";
	public static final String IMPORT = "import";
	public static final String DIALECT = "dialect";
	public static final String SALIENCE = "salience";
	public static final String ENABLED = "enabled";
	public static final String ATTRIBUTES = "attributes";
	public static final String RULE = "rule";
	public static final String EXTEND = "extends";
	public static final String TEMPLATE = "template";
	public static final String QUERY = "query";
	public static final String DECLARE = "declare";
	public static final String FUNCTION = "function";
	public static final String GLOBAL = "global";
	public static final String CONTAINS = "contains";
	public static final String MATCHES = "matches";
	public static final String EVAL = "eval";
	public static final String EXCLUDES = "excludes";
	public static final String SOUNDSLIKE = "soundslike";
	public static final String MEMBEROF = "memberof";
	public static final String NOT = "not";
	public static final String IN = "in";
	public static final String OR = "or";
	public static final String AND = "and";
	public static final String EXISTS = "exists";
	public static final String FORALL = "forall";
	public static final String ENTRY = "entry";
	public static final String POINT = "point";
	public static final String ACTION = "action";
	public static final String REVERSE = "reverse";
	public static final String RESULT = "result";
    public static final String END = "end";
    public static final String INIT = "init";
	
	public static boolean isOperator( final String operator, final boolean negated ) {
	    try {
	        Operator.determineOperator( operator, negated );
	        return true;
	    } catch( RuntimeDroolsException rde )  {
	        return false;
	    }
	}
}