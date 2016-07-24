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
    import org.kie.dmn.feel.lang.feel11.ParserHelper;
    import org.kie.dmn.feel.lang.feel11.Keywords;
}

@parser::members {
    private ParserHelper helper = new ParserHelper();

    public ParserHelper getHelper() {
        return helper;
    }

    private boolean isKeyword( Keywords k ) {
        System.out.println( "k="+k+" input='"+_input.LT(1).getText()+"'   -> "+k.symbol.equals( _input.LT(1).getText() ) );
        return k.symbol.equals( _input.LT(1).getText() );
    }
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
    | simpleUnaryTest
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
    : for_key iterationContexts return_key expression
    ;

iterationContexts
    : iterationContext ( ',' iterationContext )*
    ;

iterationContext
    : nameDefinition in_key expression
    ;

// #47
ifExpression
    : if_key c=expression then_key t=expression else_key e=expression
    ;

// #48
quantifiedExpression
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : some_key iterationContexts satisfies_key expression     #quantExprSome
    | every_key iterationContexts satisfies_key expression    #quantExprEvery
    ;

// #53
instanceOfExpression
    : conditionalOrExpression instance_key of_key type
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
    : function_key '(' formalParameters? ')' external=external_key? body=expression
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
	:	conditionalAndExpression                                                 #condOrAnd
 	|	left=conditionalOrExpression op=or_key right=conditionalAndExpression    #condOr
	;

conditionalAndExpression
	:	comparisonExpression                                                   #condAndComp
	|	left=conditionalAndExpression op=and_key right=comparisonExpression      #condAnd
	;

comparisonExpression
	:	relationalExpression                                                                   #compExpressionRel
	|   left=comparisonExpression op=('<'|'>'|'<='|'>='|'='|'!=') right=relationalExpression   #compExpression
	;

relationalExpression
	:	additiveExpression                                                                         #relExpressionAdd
	|	val=relationalExpression between_key start=additiveExpression and_key end=additiveExpression   #relExpressionBetween
	|   val=relationalExpression in_key '(' expressionList ')'                                       #relExpressionValueList
	|   val=relationalExpression in_key '(' simpleUnaryTests ')'                                     #relExpressionTestList
	|   val=relationalExpression in_key simpleUnaryTest                                              #relExpressionTest
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
	:	not_key unaryExpression  #logicalNegation
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
    |	booleanLiteral          #boolLiteral
    |	StringLiteral           #stringLiteral
    |	null_key                #nullLiteral
    ;

booleanLiteral
    :   true_key
    |   false_key
    ;

/**************************
 *    OTHER CONSTRUCTS
 **************************/
// #13
simpleUnaryTests
    : simpleUnaryTest ( ',' simpleUnaryTest )*
    ;

// #7
simpleUnaryTest
    : op='<' endpoint    #positiveUnaryTestIneq
    | op='>' endpoint    #positiveUnaryTestIneq
    | op='<=' endpoint   #positiveUnaryTestIneq
    | op='>=' endpoint   #positiveUnaryTestIneq
    | interval           #positiveUnaryTestInterval
    | null_key           #positiveUnaryTestNull
    | '-'                #positiveUnaryTestDash
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
    ( { helper.followUp( _input.LT(1) ) }? ( Identifier | additionalNameSymbol | IntegerLiteral | FloatingPointLiteral )*
    )
    ;

/********************************
 *      SOFT KEYWORDS
 ********************************/
keywords
    : for_key | return_key | in_key | if_key | then_key | else_key
    | some_key | every_key | satisfies_key | instance_key | of_key
    | function_key | external_key | and_key | or_key | not_key
    | between_key | null_key | true_key | false_key
    ;

for_key
//    : {isKeyword(Keywords.FOR)}? Identifier
    : 'for'
    ;

return_key
//    : {isKeyword(Keywords.RETURN)}? Identifier
    : 'return'
    ;

in_key
//    : {isKeyword(Keywords.IN)}? Identifier
    : 'in'
    ;

if_key
//    : {isKeyword(Keywords.IF)}? Identifier
    : 'if'
    ;

then_key
//    : {isKeyword(Keywords.THEN)}? Identifier
    : 'then'
    ;

else_key
//    : {isKeyword(Keywords.ELSE)}? Identifier
    : 'else'
    ;

some_key
//    : {isKeyword(Keywords.SOME)}? Identifier
    : 'some'
    ;

every_key
//    : {isKeyword(Keywords.EVERY)}? Identifier
    : 'every'
    ;

satisfies_key
//    : {isKeyword(Keywords.SATISFIES)}? Identifier
    : 'satisfies'
    ;

instance_key
//    : {isKeyword(Keywords.INSTANCE)}? Identifier
    : 'instance'
    ;

of_key
//    : {isKeyword(Keywords.OF)}? Identifier
    : 'of'
    ;

function_key
//    : {isKeyword(Keywords.FUNCTION)}? Identifier
    : 'function'
    ;

external_key
//    : {isKeyword(Keywords.EXTERNAL)}? Identifier
    : 'external'
    ;

or_key
//    : {isKeyword(Keywords.OR)}? Identifier
    : 'or'
    ;

and_key
//    : {isKeyword(Keywords.AND)}? Identifier
    : 'and'
    ;

between_key
//    : {isKeyword(Keywords.BETWEEN)}? Identifier
    : 'between'
    ;

not_key
//    : {isKeyword(Keywords.NOT)}? Identifier
    : 'not'
    ;

null_key
//    : {isKeyword(Keywords.NULL)}? Identifier
    : 'null'
    ;

true_key
//    : {isKeyword(Keywords.TRUE)}? Identifier
    : 'true'
    ;

false_key
//    : {isKeyword(Keywords.FALSE)}? Identifier
    : 'false'
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