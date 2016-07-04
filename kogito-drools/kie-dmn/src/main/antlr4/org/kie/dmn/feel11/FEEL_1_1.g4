/**
 * A FEEL 1.1 grammar for ANTLR 4 based on the DMN Language Specification
 * chapter 10.
 *
 * NOTE: This grammar was created out of the Java 8 feel11 available with
 *       ANTLR 4 source code.
 *
 */
grammar FEEL_1_1;

@parser::header {
    import org.kie.dmn.feel11.ParserHelper;
}

@parser::members {
    private ParserHelper helper = new ParserHelper();
}

/**************************
 *       EXPRESSIONS
 **************************/
// #1
expression
    : expr=textualExpression ( '[' filter=expression ']' )?  #expressionTextual
    | expr=boxedExpression ( '[' filter=expression ']' )?    #expressionBoxed
    ;

// #2
textualExpression
    : functionDefinition
    | forExpression
    | ifExpression
    | quantifiedExpression
    | conditionalOrExpression
    | instanceOfExpression
    | pathExpression
    | functionInvocation
    | simplePositiveUnaryTest
    ;

// #3
textualExpressions
    : textualExpression
    | textualExpressions ',' textualExpression
    ;

// #40
functionInvocation
    : qualifiedName parameters
    ;

// #41
parameters
    : '(' ')'                       #parametersEmpty
    | '(' namedParameters ')'       #parametersNamed
    | '(' positionalParameters ')'  #parametersPositional
    ;

// #42 #43
namedParameters
    : namedParameter (',' namedParameter)*
    ;

namedParameter
    : name=nameDefinition ':' value=expression
    ;

// #44
positionalParameters
    : expression ( ',' expression )*
    ;

// #45
pathExpression
    // might need to change boxedExpression by expression
    : expr=boxedExpression '.' name=nameRef
    ;

// #46
forExpression
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : 'for' iterationContexts 'return' expression
    ;

iterationContexts
    : iterationContext ( ',' iterationContext )*
    ;

iterationContext
    : nameDefinition 'in' expression
    ;

// #47
ifExpression
    : 'if' c=expression 'then' t=expression 'else' e=expression
    ;

// #48
quantifiedExpression
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : k=('some'|'every') iterationContexts 'satisfies' expression
    ;

// #53
instanceOfExpression
    : conditionalOrExpression 'instance' 'of' type
    ;

// #54
type
    : qualifiedName
    ;

// #55
boxedExpression
    : list
    | functionDefinition
    | context
    ;

// #56
list
    : '[' ']'
    | '[' expressionList ']'
    ;

// #57
functionDefinition
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : 'function' '(' formalParameters? ')' external='external'? body=expression
    ;

formalParameters
    : formalParameter ( ',' formalParameter )*
    ;

// #58
formalParameter
    : nameDefinition
    ;

// #59
context
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : '{' '}'
    | '{' contextEntries '}'
    ;

contextEntries
    : contextEntry ( ',' contextEntry )*
    ;

// #60
contextEntry
    : key { helper.pushName( $key.ctx ); }
      ':' expression { helper.popName(); }
    ;

// #61
key
    : nameDefinition   #keyName
    | StringLiteral    #keyString
    ;

nameDefinition
    : nameDefinitionTokens { helper.defineVariable( $nameDefinitionTokens.ctx ); }
    ;

nameDefinitionTokens
    : Identifier ( Identifier | additionalNameSymbol | IntegerLiteral | FloatingPointLiteral )*
    ;


additionalNameSymbol
    : ( '.' | '/' | '-' | '\'' | '+' | '*' )
    ;

conditionalOrExpression
	:	conditionalAndExpression                                               #condOrAnd
 	|	left=conditionalOrExpression op='or' right=conditionalAndExpression    #condOr
	;

conditionalAndExpression
	:	comparisonExpression                                                   #condAndComp
	|	left=conditionalAndExpression op='and' right=comparisonExpression      #condAnd
	;

comparisonExpression
	:	relationalExpression                                                                   #compExpressionRel
	|   left=comparisonExpression op=('<'|'>'|'<='|'>='|'='|'!=') right=relationalExpression   #compExpression
	;

relationalExpression
	:	additiveExpression                                                                         #relExpressionAdd
	|	val=relationalExpression 'between' start=additiveExpression 'and' end=additiveExpression   #relExpressionBetween
	|   val=relationalExpression 'in' '(' expressionList ')'                                       #relExpressionValueList
	|   val=relationalExpression 'in' '(' simplePositiveUnaryTests ')'                             #relExpressionTestList
	|   val=relationalExpression 'in' simplePositiveUnaryTest                                      #relExpressionTest
	;

expressionList
    :   expression  (',' expression)*
    ;

additiveExpression
	:	multiplicativeExpression                            #addExpressionMult
	|	additiveExpression op='+' multiplicativeExpression  #addExpression
	|	additiveExpression op='-' multiplicativeExpression  #addExpression
	;

multiplicativeExpression
	:	powerExpression                                              #multExpressionPow
	|	multiplicativeExpression op=( '*' | '/' ) powerExpression    #multExpression
	;

powerExpression
    :   unaryExpression                           #powExpressionUnary
    |   powerExpression op='**' unaryExpression   #powExpression
    ;

unaryExpression
	:	'+' unaryExpression          #signedUnaryExpression
	|	'-' unaryExpression          #signedUnaryExpression
	|	unaryExpressionNotPlusMinus  #nonSignedUnaryExpression
	;

unaryExpressionNotPlusMinus
	:	'not' unaryExpression  #logicalNegation
	|   primary                #uenpmPrimary
	;

primary
    : literal                   #primaryLiteral
    | '(' expression ')'        #primaryParens
    | qualifiedName             #primaryName
    ;

// #33 - #39
literal
    :	IntegerLiteral          #numberLiteral
    |	FloatingPointLiteral    #numberLiteral
    |	BooleanLiteral          #booleanLiteral
    |	StringLiteral           #stringLiteral
    |	NullLiteral             #nullLiteral
    ;

/**************************
 *    OTHER CONSTRUCTS
 **************************/
// #14
simpleUnaryTests
    : simplePositiveUnaryTests
    | 'not' '(' simplePositiveUnaryTests ')'
    | '-'
    ;

// #13
simplePositiveUnaryTests
    : simplePositiveUnaryTest ( ',' simplePositiveUnaryTest )*
    ;

// #7
simplePositiveUnaryTest
    : op='<' endpoint    #positiveUnaryTestIneq
    | op='>' endpoint    #positiveUnaryTestIneq
    | op='<=' endpoint   #positiveUnaryTestIneq
    | op='>=' endpoint   #positiveUnaryTestIneq
    | interval           #positiveUnaryTestInterval
    | 'null'             #positiveUnaryTestNull
    ;

// #18
endpoint
    : unaryExpression
    ;

// #8-#12
interval
    : low=('('|']'|'[') start=endpoint '..' end=endpoint up=(')'|'['|']')
    ;

// #20
qualifiedName
    : nameRef ( '.' nameRef )*
    ;

nameRef
    : st=Identifier { helper.startVariable( $st ); }
      { helper.followUp( _input.LT(1) ) }? ( Identifier | additionalNameSymbol | IntegerLiteral | FloatingPointLiteral )*
    ;

/********************************
 *      LEXER RULES
 *
 * Include:
 *      - number literals
 *      - boolean literals
 *      - string literals
 *      - null literal
 ********************************/

// Number Literals

// #37
IntegerLiteral
	:	DecimalIntegerLiteral
	|	HexIntegerLiteral
	|	OctalIntegerLiteral
	|	BinaryIntegerLiteral
	;

fragment
DecimalIntegerLiteral
	:	DecimalNumeral IntegerTypeSuffix?
	;

fragment
HexIntegerLiteral
	:	HexNumeral IntegerTypeSuffix?
	;

fragment
OctalIntegerLiteral
	:	OctalNumeral IntegerTypeSuffix?
	;

fragment
BinaryIntegerLiteral
	:	BinaryNumeral IntegerTypeSuffix?
	;

fragment
IntegerTypeSuffix
	:	[lL]
	;

fragment
DecimalNumeral
	:	'0'
	|	NonZeroDigit (Digits? | Underscores Digits)
	;

fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;

fragment
Digit
	:	'0'
	|	NonZeroDigit
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
DigitsAndUnderscores
	:	DigitOrUnderscore+
	;

fragment
DigitOrUnderscore
	:	Digit
	|	'_'
	;

fragment
Underscores
	:	'_'+
	;

fragment
HexNumeral
	:	'0' [xX] HexDigits
	;

fragment
HexDigits
	:	HexDigit (HexDigitsAndUnderscores? HexDigit)?
	;

fragment
HexDigit
	:	[0-9a-fA-F]
	;

fragment
HexDigitsAndUnderscores
	:	HexDigitOrUnderscore+
	;

fragment
HexDigitOrUnderscore
	:	HexDigit
	|	'_'
	;

fragment
OctalNumeral
	:	'0' Underscores? OctalDigits
	;

fragment
OctalDigits
	:	OctalDigit (OctalDigitsAndUnderscores? OctalDigit)?
	;

fragment
OctalDigit
	:	[0-7]
	;

fragment
OctalDigitsAndUnderscores
	:	OctalDigitOrUnderscore+
	;

fragment
OctalDigitOrUnderscore
	:	OctalDigit
	|	'_'
	;

fragment
BinaryNumeral
	:	'0' [bB] BinaryDigits
	;

fragment
BinaryDigits
	:	BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)?
	;

fragment
BinaryDigit
	:	[01]
	;

fragment
BinaryDigitsAndUnderscores
	:	BinaryDigitOrUnderscore+
	;

fragment
BinaryDigitOrUnderscore
	:	BinaryDigit
	|	'_'
	;

// #37
FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;

fragment
DecimalFloatingPointLiteral
	:	Digits '.' Digits ExponentPart? FloatTypeSuffix?
	|	'.' Digits ExponentPart? FloatTypeSuffix?
	|	Digits ExponentPart FloatTypeSuffix?
	|	Digits FloatTypeSuffix
	;

fragment
ExponentPart
	:	ExponentIndicator SignedInteger
	;

fragment
ExponentIndicator
	:	[eE]
	;

fragment
SignedInteger
	:	Sign? Digits
	;

fragment
Sign
	:	[+-]
	;

fragment
FloatTypeSuffix
	:	[fFdD]
	;

fragment
HexadecimalFloatingPointLiteral
	:	HexSignificand BinaryExponent FloatTypeSuffix?
	;

fragment
HexSignificand
	:	HexNumeral '.'?
	|	'0' [xX] HexDigits? '.' HexDigits
	;

fragment
BinaryExponent
	:	BinaryExponentIndicator SignedInteger
	;

fragment
BinaryExponentIndicator
	:	[pP]
	;

// #36
BooleanLiteral
	:	'true'
	|	'false'
	;

// String Literals

StringLiteral
	:	'"' StringCharacters? '"'
	;

fragment
StringCharacters
	:	StringCharacter+
	;

fragment
StringCharacter
	:	~["\\]
	|	EscapeSequence
	;

// Escape Sequences for Character and String Literals

fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	|	OctalEscape
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
	;

fragment
OctalEscape
	:	'\\' OctalDigit
	|	'\\' OctalDigit OctalDigit
	|	'\\' ZeroToThree OctalDigit OctalDigit
	;

fragment
ZeroToThree
	:	[0-3]
	;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// The Null Literal

NullLiteral
	:	'null'
	;

// Separators

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
COMMA : ',';
ELIPSIS : '..';
DOT : '.';

// Operators

EQUAL : '=';
GT : '>';
LT : '<';
LE : '<=';
GE : '>=';
NOTEQUAL : '!=';

QUESTION : '?';
COLON : ':';

POW : '**';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';

Identifier
	:	JavaLetter JavaLetterOrDigit*
	;

fragment
JavaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|   '?'
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment
JavaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

//
// Whitespace and comments
//

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;