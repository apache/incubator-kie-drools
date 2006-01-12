package org.drools.lang;

import jfun.parsec.Parsers;
import jfun.parsec.PositionedToken;
import junit.framework.TestCase;

public class RuleParserTest extends TestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		
	}
	
	public void testLexer() throws Exception {
		PositionedToken result = (PositionedToken) Parsers.runParser( "when", RuleParser.LEXER, "test" );
		
		System.err.println( "result:" + result.getClass() );
	}

}
