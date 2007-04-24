grammar JavaParser;

/*
tokens {
	BLOCK; MODIFIERS; OBJBLOCK; SLIST; CTOR_DEF; METHOD_DEF; VARIABLE_DEF;
	INSTANCE_INIT; STATIC_INIT; TYPE; CLASS_DEF; INTERFACE_DEF;
	PACKAGE_DEF; ARRAY_DECLARATOR; EXTENDS_CLAUSE; IMPLEMENTS_CLAUSE;
	PARAMETERS; PARAMETER_DEF; LABELED_STAT; TYPECAST; INDEX_OP;
	POST_INC; POST_DEC; METHOD_CALL; EXPR; ARRAY_INIT;
	UNARY_MINUS; UNARY_PLUS; CASE_GROUP; ELIST; FOR_INIT; FOR_CONDITION;
	FOR_ITERATOR; EMPTY_STAT; SUPER_CTOR_CALL; CTOR_CALL;
}

*/

@parser::header {
	package org.drools.rule.builder.dialect.java.parser;
	import java.util.Iterator;
}

@parser::members {
	private List identifiers = new ArrayList();
	public List getIdentifiers() { return identifiers; }
	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
	private List errors = new ArrayList();
	
	private String source = "unknown";
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return this.source;
	}
		
	public void reportError(RecognitionException ex) {
	        // if we've already reported an error and have not matched a token
                // yet successfully, don't report any errors.
                if ( errorRecovery ) {
                        //System.err.print("[SPURIOUS] ");
                        return;
                }
                errorRecovery = true;

		errors.add( ex ); 
	}
     	
     	/** return the raw RecognitionException errors */
     	public List getErrors() {
     		return errors;
     	}
     	
     	/** Return a list of pretty strings summarising the errors */
     	public List getErrorMessages() {
     		List messages = new ArrayList();
 		for ( Iterator errorIter = errors.iterator() ; errorIter.hasNext() ; ) {
     	     		messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
     	     	}
     	     	return messages;
     	}
     	
     	/** return true if any parser errors were accumulated */
     	public boolean hasErrors() {
  		return ! errors.isEmpty();
     	}
     	
     	/** This will take a RecognitionException, and create a sensible error message out of it */
     	public String createErrorMessage(RecognitionException e)
        {
		StringBuffer message = new StringBuffer();		
                message.append( source + ":"+e.line+":"+e.charPositionInLine+" ");
                if ( e instanceof MismatchedTokenException ) {
                        MismatchedTokenException mte = (MismatchedTokenException)e;
                        message.append("mismatched token: "+
                                                           e.token+
                                                           "; expecting type "+
                                                           tokenNames[mte.expecting]);
                }
                else if ( e instanceof MismatchedTreeNodeException ) {
                        MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                        message.append("mismatched tree node: "+
                                                           mtne.foundNode+
                                                           "; expecting type "+
                                                           tokenNames[mtne.expecting]);
                }
                else if ( e instanceof NoViableAltException ) {
                        NoViableAltException nvae = (NoViableAltException)e;
			message.append( "Unexpected token '" + e.token.getText() + "'" );
                        /*
                        message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
                                                           " state "+nvae.stateNumber+
                                                           " (decision="+nvae.decisionNumber+
                                                           ") no viable alt; token="+
                                                           e.token);
                                                           */
                }
                else if ( e instanceof EarlyExitException ) {
                        EarlyExitException eee = (EarlyExitException)e;
                        message.append("required (...)+ loop (decision="+
                                                           eee.decisionNumber+
                                                           ") did not match anything; token="+
                                                           e.token);
                }
                else if ( e instanceof MismatchedSetException ) {
                        MismatchedSetException mse = (MismatchedSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof MismatchedNotSetException ) {
                        MismatchedNotSetException mse = (MismatchedNotSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof FailedPredicateException ) {
                        FailedPredicateException fpe = (FailedPredicateException)e;
                        message.append("rule "+fpe.ruleName+" failed predicate: {"+
                                                           fpe.predicateText+"}?");
		}
               	return message.toString();
        }   
} 

@lexer::header {
	package org.drools.rule.builder.dialect.java.parser;
}

@lexer::members {
	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
}
 





/** A declaration is the creation of a reference or primitive-type variable
 *  Create a separate Type/Var tree for each var in the var list.
 */
declaration
	:	modifiers typeSpec variableDefinitions
		
	;

// A type specification is a type name with possible brackets afterwards
//   (which would make it an array type).
typeSpec
	: classTypeSpec
	| builtInTypeSpec
	;

// A class type specification is a class type with possible brackets afterwards
//   (which would make it an array type).
classTypeSpec
	:	identifier (LBRACK  RBRACK)*
	;

// A builtin type specification is a builtin type with possible brackets
// afterwards (which would make it an array type).
builtInTypeSpec
	:	builtInType (LBRACK  RBRACK)*
	;

// A type name. which is either a (possibly qualified) class name or
//   a primitive (builtin) type
type
	:	identifier
	|	builtInType
	;

// The primitive types.
builtInType
	:	'void'
	|	'boolean'
	|	'byte'
	|	'char'
	|	'short'
	|	'int'
	|	'float'
	|	'long'
	|	'double'
	;

// A (possibly-qualified) java identifier.  We start with the first IDENT
//   and expand its name by adding dots and following IDENTS
identifier
	:	IDENT  ( DOT IDENT )*
	;

identifierStar
	:	IDENT
		( DOT IDENT )*
		( DOT STAR  )?
	;

// A list of zero or more modifiers.  We could have used (modifier)* in
//   place of a call to modifiers, but I thought it was a good idea to keep
//   this rule separate so they can easily be collected in a Vector if
//   someone so desires
modifiers
	:	( modifier )*
		
	;

// modifiers for Java classes, interfaces, class/instance vars and methods
modifier
	:	'private'
	|	'public'
	|	'protected'
	|	'static'
	|	'transient'
	|	'final'
	|	'abstract'
	|	'native'
	|	'threadsafe'
	|	'synchronized'
//	|	'const'			// reserved word, but not valid
	|	'volatile'
	|	'strictfp'
	;

// Definition of a Java class
classDefinition
	:	'class' IDENT
		// it _might_ have a superclass...
		superClassClause
		// it might implement some interfaces...
		implementsClause
		// now parse the body of the class
		classBlock
	;

superClassClause
	:	( 'extends' identifier )?
		
	;

// Definition of a Java Interface
interfaceDefinition
	:	'interface' IDENT
		// it might extend some other interfaces
		interfaceExtends
		// now parse the body of the interface (looks like a class...)
		classBlock
	;


// This is the body of a class.  You can have fields and extra semicolons,
// That's about it (until you see what a field is...)
classBlock
	:	LCURLY
			( field | SEMI )*
		RCURLY
		
	;

// An interface can extend several other interfaces...
interfaceExtends
	:	(
		'extends'
		identifier ( COMMA identifier )*
		)?
	;

// A class can implement several interfaces...
implementsClause
	:	(
			'implements' identifier ( COMMA identifier )*
		)?
	;

// Now the various things that can be defined inside a class or interface...
// Note that not all of these are really valid in an interface (constructors,
//   for example), and if this grammar were used for a compiler there would
//   need to be some semantic checks to make sure we're doing the right thing...
field
	:	// method, constructor, or variable declaration
		modifiers
		(	ctorHead constructorBody // constructor
			

		|	classDefinition       // inner class
			

		|	interfaceDefinition   // inner interface
			

		|	typeSpec  // method or variable declaration(s)
			(	IDENT  // the name of the method

				// parse the formal parameter declarations.
				LPAREN parameterDeclarationList RPAREN

				declaratorBrackets

				// get the list of exceptions that this method is
				// declared to throw
				(throwsClause)?

				( compoundStatement | SEMI )
			|	variableDefinitions SEMI
//				
				
			)
		)

    // 'static { ... }' class initializer
	|	'static' compoundStatement
		

    // '{ ... }' instance initializer
	|	compoundStatement
		
	;

constructorBody
    :   LCURLY 
            ( options {greedy=true;} : explicitConstructorInvocation)?
            (statement)*
        RCURLY
    ;

/** Catch obvious constructor calls, but not the expr.super(...) calls */
explicitConstructorInvocation
    :   'this' LPAREN argList RPAREN SEMI
		
    |   'super' LPAREN argList RPAREN SEMI
		
    ;

variableDefinitions
	:	variableDeclarator
		(	COMMA
			variableDeclarator
		)*
	;

/** Declaration of a variable.  This can be a class/instance variable,
 *   or a local variable in a method
 * It can also include possible initialization.
 */
variableDeclarator
	:	IDENT declaratorBrackets varInitializer
		
	;

declaratorBrackets
	:	
		(LBRACK  RBRACK)*
	;

varInitializer
	:	( ASSIGN initializer )?
	;

// This is an initializer used to set up an array.
arrayInitializer
	:	LCURLY 
			(	initializer
				(
					// CONFLICT: does a COMMA after an initializer start a new
					//           initializer or start the option ',' at end?
					//           ANTLR generates proper code by matching
					//			 the comma as soon as possible.
					COMMA initializer
				)*
				(COMMA)?
			)?
		RCURLY
	;


// The two 'things' that can initialize an array element are an expression
//   and another (nested) array initializer.
initializer
	:	expression
	|	arrayInitializer
	;

// This is the header of a method.  It includes the name and parameters
//   for the method.
//   This also watches for a list of exception classes in a 'throws' clause.
ctorHead
	:	IDENT  // the name of the method

		// parse the formal parameter declarations.
		LPAREN parameterDeclarationList RPAREN

		// get the list of exceptions that this method is declared to throw
		(throwsClause)?
	;

// This is a list of exception classes that the method is declared to throw
throwsClause
	:	'throws' identifier ( COMMA identifier )*
	;


// A list of formal parameters
parameterDeclarationList
	:	( parameterDeclaration ( COMMA parameterDeclaration )* )?
	;

// A formal parameter.
parameterDeclaration
	:	parameterModifier typeSpec IDENT
		declaratorBrackets
	;

parameterModifier
	:	('final')?
		
	;

// Compound statement.  This is used in many contexts:
//   Inside a class definition prefixed with 'static':
//      it is a class initializer
//   Inside a class definition without 'static':
//      it is an instance initializer
//   As the body of a method
//   As a completely indepdent braced block of code inside a method
//      it starts a new scope for variable definitions

compoundStatement
	:	LCURLY 
			// include the (possibly-empty) list of statements
			(statement)*
		RCURLY
	;


statement
	// A list of statements in curly braces -- start a new scope
	:	compoundStatement

	// declarations are ambiguous with 'ID DOT' relative to expression
	// statements.  Must backtrack to be sure.  Could use a semantic
	// predicate to test symbol table to see what the type was coming
	// up, but that's pretty hard without a symbol table ;)
	// remove (declaration)=>
	|	declaration SEMI

	// An expression statement.  This could be a method call,
	// assignment statement, or any other expression evaluated for
	// side-effects.
	|	expression SEMI

	// class definition
	|	modifiers classDefinition

	// Attach a label to the front of a statement
	|	IDENT COLON  statement

	// If-else statement
	|	'if' LPAREN expression RPAREN statement
		(
			// CONFLICT: the old "dangling-else" problem...
			//           ANTLR generates proper code matching
			//			 as soon as possible.  Hush warning.
			'else' statement
		)?

	// For statement
	|	'for'
			LPAREN
				forInit SEMI   // initializer
				forCond	SEMI   // condition test
				forIter         // updater
			RPAREN
			statement                     // statement to loop over

	// While statement
	|	'while' LPAREN expression RPAREN statement

	// do-while statement
	|	'do' statement 'while' LPAREN expression RPAREN SEMI

	// get out of a loop (or switch)
	|	'break' (IDENT)? SEMI

	// do next iteration of a loop
	|	'continue' (IDENT)? SEMI

	// Return an expression
	|	'return' (expression)? SEMI

	// switch/case statement
	|	'switch' LPAREN expression RPAREN LCURLY
			( casesGroup )*
		RCURLY

	// exception try-catch block
	|	tryBlock

	// throw an exception
	|	'throw' expression SEMI

	// synchronize a statement
	|	'synchronized' LPAREN expression RPAREN compoundStatement

	// asserts (uncomment if you want 1.4 compatibility)
	// |	'assert' expression ( COLON expression )? SEMI

	// empty statement
	|	SEMI 
	;

casesGroup
	:	(	// CONFLICT: to which case group do the statements bind?
			//           ANTLR generates proper code: it groups the
			//           many 'case'/'default' labels together then
			//           follows them with the statements
				options {greedy=true;}
			:
			aCase
		)+
		caseSList
		
	;

aCase
	:	('case' expression | 'default') COLON
	;

caseSList
	:	(statement)*
		
	;

// The initializer for a for loop
forInit
		// if it looks like a declaration, it is
        // remove (declaration)=> 
	:	(	declaration
		// otherwise it could be an expression list...
		|	expressionList
		)?
		
	;

forCond
	:	(expression)?
		
	;

forIter
	:	(expressionList)?
		
	;

// an exception handler try/catch block
tryBlock
	:	'try' compoundStatement
		(handler)*
		( finallyClause )?
	;

finallyClause
	:	'finally' compoundStatement
	;

// an exception handler
handler
	:	'catch' LPAREN parameterDeclaration RPAREN compoundStatement
	;


// expressions
// Note that most of these expressions follow the pattern
//   thisLevelExpression :
//       nextHigherPrecedenceExpression
//           (OPERATOR nextHigherPrecedenceExpression)*
// which is a standard recursive definition for a parsing an expression.
// The operators in java have the following precedences:
//    lowest  (13)  = *= /= %= += -= <<= >>= >>>= &= = |=
//            (12)  ?:
//            (11)  ||
//            (10)  &&
//            ( 9)  |
//            ( 8)  
//            ( 7)  &
//            ( 6)  == =
//            ( 5)  < <= > >=
//            ( 4)  << >>
//            ( 3)  +(binary) -(binary)
//            ( 2)  * / %
//            ( 1)  ++ -- +(unary) -(unary)  ~    (type)
//                  []   () (method call)  . (dot -- identifier qualification)
//                  new   ()  (explicit parenthesis)
//
// the last two are not usually on a precedence chart; I put them in
// to point out that new has a higher precedence than '.', so you
// can validy use
//     new Frame().show()
//
// Note that the above precedence levels map to the rules below...
// Once you have a precedence chart, writing the appropriate rules as below
//   is usually very straightfoward



// the mother of all expressions
expression
	:	assignmentExpression
		
	;


// This is a list of expressions.
expressionList
	:	expression (COMMA expression)*
		
	;


// assignment expression (level 13)
assignmentExpression
	:	conditionalExpression
		(	(	ASSIGN
            |   PLUS_ASSIGN
            |   MINUS_ASSIGN
            |   STAR_ASSIGN
            |   DIV_ASSIGN
            |   MOD_ASSIGN
            |   SR_ASSIGN
            |   BSR_ASSIGN
            |   SL_ASSIGN
            |   BAND_ASSIGN
            |   BXOR_ASSIGN
            |   BOR_ASSIGN
            )
			assignmentExpression
		)?
	;


// conditional test (level 12)
conditionalExpression
	:	logicalOrExpression
		( QUESTION assignmentExpression COLON conditionalExpression )?
	;


// logical or (||)  (level 11)
logicalOrExpression
	:	logicalAndExpression (LOR logicalAndExpression)*
	;


// logical and (&&)  (level 10)
logicalAndExpression
	:	inclusiveOrExpression (LAND inclusiveOrExpression)*
	;


// bitwise or non-short-circuiting or (|)  (level 9)
inclusiveOrExpression
	:	exclusiveOrExpression (BOR exclusiveOrExpression)*
	;


// exclusive or ()  (level 8)
exclusiveOrExpression
	:	andExpression (BXOR andExpression)*
	;


// bitwise or non-short-circuiting and (&)  (level 7)
andExpression
	:	equalityExpression (BAND equalityExpression)*
	;


// equality/inequality (==/=) (level 6)
equalityExpression
	:	relationalExpression ((NOT_EQUAL | EQUAL) relationalExpression)*
	;


// boolean relational expressions (level 5)
relationalExpression
	:	shiftExpression
		(	(	(	LT
				|	GT
				|	LE
				|	GE
				)
				shiftExpression
			)*
		|	'instanceof' typeSpec
		)
	;


// bit shift expressions (level 4)
shiftExpression
	:	additiveExpression ((SL | SR | BSR) additiveExpression)*
	;


// binary addition/subtraction (level 3)
additiveExpression
	:	multiplicativeExpression ((PLUS | MINUS) multiplicativeExpression)*
	;


// multiplication/division/modulo (level 2)
multiplicativeExpression
	:	unaryExpression ((STAR | DIV | MOD ) unaryExpression)*
	;

unaryExpression
	:	INC unaryExpression
	|	DEC unaryExpression
	|	MINUS  unaryExpression
	|	PLUS   unaryExpression
	|	unaryExpressionNotPlusMinus
	;

unaryExpressionNotPlusMinus
	:	BNOT unaryExpression
	|	LNOT unaryExpression

    |   LPAREN builtInTypeSpec RPAREN
        unaryExpression

        // Have to backtrack to see if operator follows.  If no operator
        // follows, it's a typecast.  No semantic checking needed to parse.
        // if it _looks_ like a cast, it _is_ a cast; else it's a '(expr)'
    |	LPAREN classTypeSpec RPAREN
        unaryExpressionNotPlusMinus

    |	postfixExpression
	;

// qualified names, array expressions, method invocation, post inc/dec
postfixExpression
	:   primaryExpression
		(   DOT IDENT
			(	LPAREN 
				argList
				RPAREN
			)?
		|	DOT 'this'

		|	DOT 'super'
            (   // (new Outer()).super()  (create enclosing instance)
                LPAREN argList RPAREN
                
			|   DOT IDENT
                (	LPAREN 
                    argList
                    RPAREN
                )?
            )
		|	DOT newExpression
		|	LBRACK  expression RBRACK
		)*

		(   // possibly add on a post-increment or post-decrement.
            // allows INC/DEC on too much, but semantics can check
			INC 
	 	|	DEC 
		)?
 	;

// the basic element of an expression
primaryExpression
	:	identPrimary ( options {greedy=true;}: DOT 'class' )?
    |   constant
	|	'true'
	|	'false'
	|	'null'
    |   newExpression
	|	'this'
	|	'super'
	|	LPAREN assignmentExpression RPAREN
		// look for int.class and int[].class
	|	builtInType
		( LBRACK  RBRACK )*
		DOT 'class'
	;

/** Match a, a.b.c refs, a.b.c(...) refs, a.b.c[], a.b.c[].class,
 *  and a.b.c.class refs.  Also this(...) and super(...).  Match
 *  this or super.
 */
identPrimary
	:	i=IDENT { identifiers.add( i.getText() );  }
		(
				// .ident could match here or in postfixExpression.
				// We do want to match here.  Turn off warning.
				options {greedy=true; k=2;}
		:	DOT IDENT
		)*
		(
				// ARRAY_DECLARATOR here conflicts with INDEX_OP in
				// postfixExpression on LBRACK RBRACK.
				// We want to match [] here, so greedy.  This overcomes
                // limitation of linear approximate lookahead.
				options {greedy=true;}
		:   ( LPAREN  argList RPAREN )
		|	( options {greedy=true;} :
              LBRACK  RBRACK
            )+
		)?
    ;

/** object instantiation.
 *  Trees are built as illustrated by the following input/tree pairs:
 *
 *  new T()
 *
 *  new
 *   |
 *   T --  ELIST
 *           |
 *          arg1 -- arg2 -- .. -- argn
 *
 *  new int[]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *
 *  new int[] {1,2}
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR -- ARRAY_INIT
 *                                  |
 *                                EXPR -- EXPR
 *                                  |      |
 *                                  1      2
 *
 *  new int[3]
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *                |
 *              EXPR
 *                |
 *                3
 *
 *  new int[1][2]
 *
 *  new
 *   |
 *  int -- ARRAY_DECLARATOR
 *               |
 *         ARRAY_DECLARATOR -- EXPR
 *               |              |
 *             EXPR             1
 *               |
 *               2
 *
 */
newExpression
	:	'new' type
		(	LPAREN argList RPAREN (classBlock)?

			//java 1.1
			// Note: This will allow bad constructs like
			//    new int[4][][3] {exp,exp}.
			//    There needs to be a semantic check here...
			// to make sure:
			//   a) [ expr ] and [ ] are not mixed
			//   b) [ expr ] and an init are not used together

		|	newArrayDeclarator (arrayInitializer)?
		)
	;

argList
	:	(	expressionList
		|	/*nothing*/
			
		)
	;

newArrayDeclarator
	:	(
			// CONFLICT:
			// newExpression is a primaryExpression which can be
			// followed by an array index reference.  This is ok,
			// as the generated code will stay in this loop as
			// long as it sees an LBRACK (proper behavior)
				options {k=1;}
				//options {warnWhenFollowAmbig = false;}
		:
			LBRACK 
				(expression)?
			RBRACK
		)+
	;

constant
	:	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	;


//----------------------------------------------------------------------------
// The Java scanner
//----------------------------------------------------------------------------

// OPERATORS


QUESTION		:	'?'		;


LPAREN			:	'('		;


RPAREN			:	')'		;


LBRACK			:	'['		;


RBRACK			:	']'		;


LCURLY			:	'{'		;


RCURLY			:	'}'		;


COLON			:	':'		;


COMMA			:	','		;

DOT				:	'.'		;

ASSIGN			:	'='		;


EQUAL			:	'=='	;


LNOT			:	'!'		;


BNOT			:	'~'		;


NOT_EQUAL		:	'!='	;


DIV				:	'/'		;


DIV_ASSIGN		:	'/='	;


PLUS			:	'+'		;


PLUS_ASSIGN		:	'+='	;


INC				:	'++'	;


MINUS			:	'-'		;


MINUS_ASSIGN	:	'-='	;


DEC				:	'--'	;


STAR			:	'*'		;


STAR_ASSIGN		:	'*='	;


MOD				:	'%'		;


MOD_ASSIGN		:	'%='	;


SR				:	'>>'	;


SR_ASSIGN		:	'>>='	;


BSR				:	'>>>'	;


BSR_ASSIGN		:	'>>>='	;


GE				:	'>='	;


GT				:	'>'		;


SL				:	'<<'	;


SL_ASSIGN		:	'<<='	;


LE				:	'<='	;


LT				:	'<'		;


BXOR			:	'^'		;


BXOR_ASSIGN		:	'^='	;


BOR				:	'|'		;


BOR_ASSIGN		:	'|='	;


LOR				:	'||'	;


BAND			:	'&'		;


BAND_ASSIGN		:	'&='	;


LAND			:	'&&'	;


SEMI			:	';'		;


// Whitespace -- ignored


WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	'\r\n'  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
		)+
		{ $channel=HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/ }
	;

// Single-line comments


SL_COMMENT
	:	'//' (options {greedy=false;} : .)* ('\r')? '\n'
		{$channel=HIDDEN; /*token = JavaParser.IGNORE_TOKEN;*/}
	;

// multiple-line comments


ML_COMMENT
	:	'/*'
		( options {greedy=false;} : . )*
		'*/'
		{$channel=HIDDEN;/*token = JavaParser.IGNORE_TOKEN;*/}
	;

IDENT
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
	;

// From the java language spec

NUM_INT
    : DECIMAL_LITERAL 
    | HEX_LITERAL
    | OCTAL_LITERAL
    ;

fragment
DECIMAL_LITERAL: '1'..'9' ('0'..'9')* ('l'|'L')? ;

fragment
HEX_LITERAL: '0' ('x'|'X') ('0'..'9'|'a'..'f'|'A'..'F')+ ('l'|'L')? ;

fragment
OCTAL_LITERAL: '0' ('0'..'7')* ('l'|'L')? ;

NUM_FLOAT
    :     DIGITS '.' (DIGITS)? (EXPONENT_PART)? (FLOAT_TYPE_SUFFIX)?
    | '.' DIGITS (EXPONENT_PART)? (FLOAT_TYPE_SUFFIX)?
    |     DIGITS EXPONENT_PART FLOAT_TYPE_SUFFIX
    |     DIGITS EXPONENT_PART
    |     DIGITS FLOAT_TYPE_SUFFIX
    ;


fragment
DIGITS : ('0'..'9')+ ;

/*
fragment
EXPONENT_PART: ('e'|'E') ('+'|'-')? DIGITS ;
*/

fragment
EXPONENT_PART: ('e'|'E') ('+'|'-')? DIGITS ;

fragment
FLOAT_TYPE_SUFFIX :   ('f'|'F'|'d'|'D') ;

CHAR_LITERAL
    :
      '\''
      ( ~('\''|'\\')
      | ESCAPE_SEQUENCE
      )
      '\''
    ;

STRING_LITERAL
    :
      '\"'
      ( ~('\"'|'\\')
      | ESCAPE_SEQUENCE
      )*
      '\"'
        ;

fragment
ESCAPE_SEQUENCE
    :	'\\' 'b'
    |   '\\' 't'
    |   '\\' 'n'
    |   '\\' 'f'
    |   '\\' 'r'
    |   '\\' '\"'
    |   '\\' '\''
    |   '\\' '\\'
    |	'\\' '0'..'3' OCTAL_DIGIT OCTAL_DIGIT
    |   '\\' OCTAL_DIGIT OCTAL_DIGIT
    |   '\\' OCTAL_DIGIT
	|	UNICODE_CHAR
	;

fragment
UNICODE_CHAR
	:	'\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	;

fragment
HEX_DIGIT
	:	'0'..'9'|'a'..'f'|'A'..'F'
	;

fragment
OCTAL_DIGIT
	:	'0'..'7'
	;
