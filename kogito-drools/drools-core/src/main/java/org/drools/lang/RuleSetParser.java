package org.drools.lang;

import jfun.parsec.pattern.Pattern;
import jfun.parsec.pattern.Patterns;

public class RuleSetParser {

	public static final Pattern IMPORT = Patterns.isString( "import" );
	
}
