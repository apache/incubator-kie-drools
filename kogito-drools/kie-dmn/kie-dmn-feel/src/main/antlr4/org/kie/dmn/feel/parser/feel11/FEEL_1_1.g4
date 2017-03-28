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
    import org.kie.dmn.feel.parser.feel11.ParserHelper;
    import org.kie.dmn.feel.parser.feel11.Keywords;
}

@parser::members {
    private ParserHelper helper = null;

    public void setHelper( ParserHelper helper ) {
        this.helper = helper;
    }

    public ParserHelper getHelper() {
        return helper;
    }

    private boolean isKeyword( Keywords k ) {
        return k.symbol.equals( _input.LT(1).getText() );
    }

    private String getOriginalText( ParserRuleContext ctx ) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval(a,b);
        return ctx.getStart().getInputStream().getText(interval);
    }
}

/**************************
 *       EXPRESSIONS
 **************************/
compilation_unit
    : expression EOF
    ;

// #1
expression
    : expr=textualExpression  #expressionTextual
    ;

// #2
textualExpression
    : functionDefinition
    | forExpression
    | ifExpression
    | quantifiedExpression
    | conditionalOrExpression
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

// #54
type
    : ( function_key | qualifiedName )
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
      ':' expression { helper.popName(); helper.defineVariable( $key.ctx ); }
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
    : Identifier
        ( Identifier
        | additionalNameSymbol
        | IntegerLiteral
        | FloatingPointLiteral
        | reusableKeywords
        )*
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
	:	additiveExpression                                                                           #relExpressionAdd
	|	val=relationalExpression between_key start=additiveExpression and_key end=additiveExpression   #relExpressionBetween
	|   val=relationalExpression in_key '(' expressionList ')'                                       #relExpressionValueList
	|   val=relationalExpression in_key '(' simpleUnaryTests ')'                                     #relExpressionTestList
	|   val=relationalExpression in_key simpleUnaryTest                                              #relExpressionTest
    |   val=relationalExpression instance_key of_key type                                            #relExpressionInstanceOf
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
    :   filterPathExpression                           #powExpressionUnary
    |   powerExpression op='**' filterPathExpression   #powExpression
    ;

filterPathExpression
    :   unaryExpression
    |   filterPathExpression '[' {helper.enableDynamicResolution();} filter=expression {helper.disableDynamicResolution();} ']'
    |   filterPathExpression '.' {helper.enableDynamicResolution();} qualifiedName {helper.disableDynamicResolution();}
    ;

unaryExpression
	:	'+' unaryExpression          #signedUnaryExpression
	|	'-' unaryExpression          #signedUnaryExpression
	|	unaryExpressionNotPlusMinus  #nonSignedUnaryExpression
	;

unaryExpressionNotPlusMinus
	:   not_key '(' simpleUnaryTests ')'  #negatedUnaryTests
	|	not_key unaryExpression           #logicalNegation
	|   primary ('.' {helper.recoverScope();} qualifiedName {helper.dismissScope();} )?   #uenpmPrimary
	;

primary
    : literal                     #primaryLiteral
    | interval                    #primaryInterval
    | list                        #primaryList
    | context                     #primaryContext
    | '(' expression ')'          #primaryParens
    | simpleUnaryTest             #primaryUnaryTest
    | qualifiedName parameters?   #primaryName
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
    : (simpleUnaryTest|primary) ( ',' (simpleUnaryTest|primary) )*
    ;

// #7
simpleUnaryTest
    : op='<' endpoint    #positiveUnaryTestIneq
    | op='>' endpoint    #positiveUnaryTestIneq
    | op='<=' endpoint   #positiveUnaryTestIneq
    | op='>=' endpoint   #positiveUnaryTestIneq
    | op='=' endpoint    #positiveUnaryTestIneq
    | op='!=' endpoint   #positiveUnaryTestIneq
    | interval           #positiveUnaryTestInterval
    | null_key           #positiveUnaryTestNull
    | '-'                #positiveUnaryTestDash
    ;

// #18
endpoint
    : additiveExpression
    ;

// #8-#12
interval
    : low='(' start=endpoint '..' end=endpoint up=')'
    | low='(' start=endpoint '..' end=endpoint up='['
    | low='(' start=endpoint '..' end=endpoint up=']'
    | low=']' start=endpoint '..' end=endpoint up=')'
    | low=']' start=endpoint '..' end=endpoint up='['
    | low=']' start=endpoint '..' end=endpoint up=']'
    | low='[' start=endpoint '..' end=endpoint up=')'
    | low='[' start=endpoint '..' end=endpoint up='['
    | low='[' start=endpoint '..' end=endpoint up=']'
    ;

// #20
qualifiedName
@init {
    String name = null;
    int count = 0;
    java.util.List<String> qn = new java.util.ArrayList<String>();
}
@after {
    for( int i = 0; i < count; i++ )
        helper.dismissScope();
}
    : n1=nameRef { name = getOriginalText( $n1.ctx ); qn.add( name ); helper.validateVariable( $n1.ctx, qn, name ); }
        ( '.'
            {helper.recoverScope( name ); count++;}
            n2=nameRef
            {name=getOriginalText( $n2.ctx );}
        )*
    ;

nameRef
    : st=Identifier { helper.startVariable( $st ); } nameRefOtherToken*
    ;

nameRefOtherToken
    : { helper.followUp( _input.LT(1), _localctx==null ) }? ~('('|')'|'['|']'|'{'|'}')
    ;

/********************************
 *      KEYWORDS
 ********************************/
reusableKeywords
    : for_key
    | return_key
    | if_key
    | then_key
    | else_key
    | some_key
    | every_key
    | satisfies_key
    | instance_key
    | of_key
    | function_key
    | external_key
    | or_key
    | and_key
    | between_key
    | not_key
    | null_key
    | true_key
    | false_key
    ;

for_key
    : 'for'
    ;

return_key
    : 'return'
    ;

// can't be reused
in_key
    : 'in'
    ;

if_key
    : 'if'
    ;

then_key
    : 'then'
    ;

else_key
    : 'else'
    ;

some_key
    : 'some'
    ;

every_key
    : 'every'
    ;

satisfies_key
    : 'satisfies'
    ;

instance_key
    : 'instance'
    ;

of_key
    : 'of'
    ;

function_key
    : 'function'
    ;

external_key
    : 'external'
    ;

or_key
    : 'or'
    ;

and_key
    : 'and'
    ;

between_key
    : 'between'
    ;

not_key
    : 'not'
    ;

null_key
    : 'null'
    ;

true_key
    : 'true'
    ;

false_key
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
IntegerTypeSuffix
	:	[lL]
	;

fragment
DecimalNumeral
	:	Digit (Digits? | Underscores Digits)
	;

fragment
Digits
	:	Digit (DigitsAndUnderscores? Digit)?
	;

fragment
Digit
	:	[0-9]
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
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
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

WS  :  [ \t\r\n\u000C\u00A0]+ -> skip
    ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

ANY_OTHER_CHAR
    : ~[ \t\r\n\u000c]
    ;
