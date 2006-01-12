package org.drools.lang;

import jfun.parsec.Lexers;
import jfun.parsec.Parser;
import jfun.parsec.Parsers;
import jfun.parsec.Scanners;
import jfun.parsec.pattern.Pattern;
import jfun.parsec.pattern.Patterns;
import jfun.parsec.tokens.TokenString;

public class RuleParser {
	
	public static final Pattern RULE = Patterns.isString( "rule" );
	public static final Pattern WHEN = Patterns.isString( "when" );
	public static final Pattern THEN = Patterns.isString( "then" );
	
	public static final Pattern LEFT_PAREN = Patterns.isChar( '(' );
	public static final Pattern RIGHT_PAREN = Patterns.isChar( ')' );
	
	public static final Pattern LEFT_BRACE = Patterns.isChar( '{' );
	public static final Pattern RIGHT_BRACE = Patterns.isChar( '}' );
	
	public static final Parser S_RULE = Scanners.isPattern( RULE, "rule" );
	public static final Parser S_WHEN = Scanners.isPattern( WHEN, "when" );
	public static final Parser S_THEN = Scanners.isPattern( THEN, "then" );
	
	public static final Parser S_LEFT_PAREN = Scanners.isPattern( LEFT_PAREN, "(" );
	public static final Parser S_RIGHT_PAREN = Scanners.isPattern( RIGHT_PAREN, ")" );
	
	public static final Parser S_LEFT_BRACE = Scanners.isPattern( LEFT_BRACE, "{" );
	public static final Parser S_RIGHT_BRACE = Scanners.isPattern( RIGHT_BRACE, "}" );
	
	public static final Parser L_RULE = Scanners.lexer( S_RULE, TokenString.getTokenizer() );
	public static final Parser L_WHEN = Scanners.lexer( S_WHEN, TokenString.getTokenizer() );
	public static final Parser L_THEN = Scanners.lexer( S_THEN, TokenString.getTokenizer() );
	
	public static final Parser LEXER = Parsers.sum( new Parser[] { 
			L_RULE,
			L_WHEN,
			L_THEN,
		} );
}
