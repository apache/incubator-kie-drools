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
    import org.kie.dmn.lang.types.SymbolTable;
}

@parser::members {
    private SymbolTable symbols = new SymbolTable();

    public SymbolTable getSymbolTable() {
        return symbols;
    }
}

/********************************
 *      GENERAL RULES
 ********************************/

literal
	:	IntegerLiteral          #numberLiteral
	|	FloatingPointLiteral    #numberLiteral
	|	BooleanLiteral          #booleanLiteral
	|	CharacterLiteral        #charLiteral
	|	StringLiteral           #stringLiteral
	|	NullLiteral             #nullLiteral
	|   dateTimeLiteral         #dtLiteral
	;

// #62
dateTimeLiteral
    :   'date and time' '(' StringLiteral ')'
    |   'date' '(' StringLiteral ')'
    |   'time' '(' StringLiteral ')'
    |   'duration' '(' StringLiteral ')'
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
    : simplePositiveUnaryTest
    | simplePositiveUnaryTests ',' simplePositiveUnaryTest
    ;

// #7
simplePositiveUnaryTest
    : '<' endpoint
    | '>' endpoint
    | '<=' endpoint
    | '>=' endpoint
    | interval
    | 'null'
    ;

// #18
endpoint
    : additiveExpression
    ;

// #8-#12
interval
    : '(' endpoint '..' endpoint ')'
    | '(' endpoint '..' endpoint '['
    | '(' endpoint '..' endpoint ']'
    | ']' endpoint '..' endpoint ')'
    | ']' endpoint '..' endpoint '['
    | ']' endpoint '..' endpoint ']'
    | '[' endpoint '..' endpoint ')'
    | '[' endpoint '..' endpoint '['
    | '[' endpoint '..' endpoint ']'
    ;

// #20
qualifiedName
    : Identifier
    | qualifiedName '.' Identifier
    ;

/**************************
 *       EXPRESSIONS
 **************************/
expressions
    : expression
    | expressions ',' expression
    ;

// #1
expression
    : textualExpression
    | boxedExpression
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
    | filterExpression
    | functionInvocation
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
    : '(' ')'
    | '(' namedParameters ')'
    | '(' positionalParameters ')'
    ;

// #42 #43
namedParameters
    : namedParameter
    | namedParameters ',' namedParameter
    ;

namedParameter
    : Identifier ':' expression
    ;

// #44
positionalParameters
    : expression
    | positionalParameters ',' expression
    ;

// #45
pathExpression
    : boxedExpression '.' Identifier
    ;

// #46
forExpression
    : 'for' idInExpressions 'return' expression
    ;

idInExpressions
    : idInExpression
    | idInExpressions ',' idInExpression
    ;

idInExpression
    : Identifier 'in' expression
    ;

// #47
ifExpression
    : 'if' expression 'then' expression 'else' expression
    ;

// #48
quantifiedExpression
    : 'some' idInExpressions 'satisfies' expression
    | 'every' idInExpressions 'satisfies' expression
    ;

// #52
filterExpression
    : list '[' expression ']'
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
    | '[' expressions ']'
    ;

// #57
functionDefinition
    : 'function' '(' ')' functionBody
    | 'function' '(' formalParameters ')' functionBody
    ;

functionBody
    : 'external' expression
    | expression
    ;

formalParameters
    : formalParameter
    | formalParameters ',' formalParameter
    ;

// #58
formalParameter
    : Identifier
    ;

// #59
context
    : '{' '}'
    | '{' contextEntries '}'
    ;

contextEntries
    : contextEntry
    | contextEntries ',' contextEntry
    ;

// #60
contextEntry
    : key ':' expression
    ;

// #61
key
    : Identifier
    | StringLiteral
    ;

// several rules recursivelly
conditionalOrExpression
	:	conditionalAndExpression
	|	conditionalOrExpression 'or' conditionalAndExpression
	;

conditionalAndExpression
	:	equalityExpression
	|	conditionalAndExpression 'and' equalityExpression
	;

equalityExpression
	:	relationalExpression
	|	equalityExpression '=' relationalExpression
	|	equalityExpression '!=' relationalExpression
	;

relationalExpression
	:	additiveExpression                                                          #relationalExpression_single
	|   relationalExpression '<' additiveExpression                                 #relationalExpression_less
	|	relationalExpression '>' additiveExpression                                 #relationalExpression_greater
	|	relationalExpression '<=' additiveExpression                                #relationalExpression_lessEqual
	|	relationalExpression '>=' additiveExpression                                #relationalExpression_greaterEqual
	|	relationalExpression 'between' additiveExpression 'and' additiveExpression  #relationalExpression_between
	|   relationalExpression 'in' '(' valueList ')'                                 #relationalExpression_valueList
	|   relationalExpression 'in' '(' simplePositiveUnaryTests ')'                  #relationalExpression_unaryList
	|   relationalExpression 'in' simplePositiveUnaryTest                           #relationalExpression_unary
	;

valueList
    :   additiveExpression
    |   valueList ',' additiveExpression
    ;

additiveExpression
	:	multiplicativeExpression
	|	additiveExpression '+' multiplicativeExpression
	|	additiveExpression '-' multiplicativeExpression
	;

multiplicativeExpression
	:	unaryExpression
	|	multiplicativeExpression '*' unaryExpression
	|	multiplicativeExpression '/' unaryExpression
	|	multiplicativeExpression '**' unaryExpression
	;

unaryExpression
	:	'+' unaryExpression          #signedUnaryExpression
	|	'-' unaryExpression          #signedUnaryExpression
	|	unaryExpressionNotPlusMinus  #nonSignedUnaryExpression
	;

unaryExpressionNotPlusMinus
	:	'not' unaryExpression
	|   primary
	;

primary
    : literal
    | '(' expression ')'
    | Identifier
    ;

// LEXER

// Keywords

ELSE : 'else';
FOR : 'for';
IF : 'if';
INSTANCE : 'instance';
OF : 'of';
DATETIME : 'date and time';
DATE : 'date';
TIME : 'time';
DURATION : 'duration';

// Integer Literals

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

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
	:	DecimalFloatingPointLiteral
	|	HexadecimalFloatingPointLiteral
	;

fragment
DecimalFloatingPointLiteral
	:	Digits '.' Digits? ExponentPart? FloatTypeSuffix?
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

// §3.10.3 Boolean Literals

BooleanLiteral
	:	'true'
	|	'false'
	;

// §3.10.4 Character Literals

CharacterLiteral
	:	'\'' SingleCharacter '\''
	|	'\'' EscapeSequence '\''
	;

fragment
SingleCharacter
	:	~['\\]
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