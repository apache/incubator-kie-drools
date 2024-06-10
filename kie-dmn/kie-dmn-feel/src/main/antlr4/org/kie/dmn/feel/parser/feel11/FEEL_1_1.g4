/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
}

@parser::members {
    private ParserHelper helper = null;
    
    public void setHelper( ParserHelper helper ) {
        this.helper = helper;
    }

    public ParserHelper getHelper() {
        return helper;
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
    | conditionalOrExpression
    ;

// #41
parameters
    : LPAREN RPAREN                       #parametersEmpty
    | LPAREN namedParameters RPAREN       #parametersNamed
    | LPAREN positionalParameters RPAREN  #parametersPositional
    ;

// #42 #43
namedParameters
    : namedParameter (COMMA namedParameter)*
    ;

namedParameter
    : name=nameDefinition COLON value=expression
    ;

// #44
positionalParameters
    : expression ( COMMA expression )*
    ;

// #46
forExpression
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : FOR iterationContexts RETURN {helper.enableDynamicResolution();} expression {helper.disableDynamicResolution();}
    ;

iterationContexts
    : iterationContext ( COMMA iterationContext )*
    ;

iterationContext
    : {helper.isFeatDMN12EnhancedForLoopEnabled()}? iterationNameDefinition IN expression '..' expression
    | iterationNameDefinition IN expression
    ;
    
// #47
ifExpression
    : IF c=expression THEN t=expression ELSE e=expression
    ;

// #48
quantifiedExpression
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : SOME iterationContexts SATISFIES {helper.enableDynamicResolution();} expression {helper.disableDynamicResolution();}    #quantExprSome
    | EVERY iterationContexts SATISFIES {helper.enableDynamicResolution();} expression {helper.disableDynamicResolution();}   #quantExprEvery
    ;

// #54
type
@init {
    helper.pushTypeScope();
}
@after {
    helper.popScope();
}
    : {_input.LT(1).getText().equals("list")}? sk=Identifier LT type GT                                                        #listType
    | {_input.LT(1).getText().equals("context")}? sk=Identifier LT Identifier COLON type ( COMMA Identifier COLON type )* GT   #contextType
    | FUNCTION                                                                                                        #qnType
    | FUNCTION LT (type ( COMMA type )*)? GT RARROW type                                                              #functionType
    | qualifiedName                                                                                                   #qnType
    ;

// #56
list
    : LBRACK RBRACK
    | LBRACK expressionList RBRACK
    ;

// #57
functionDefinition
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : FUNCTION LPAREN formalParameters? RPAREN external=EXTERNAL? {helper.enableDynamicResolution();} body=expression {helper.disableDynamicResolution();}
    ;

formalParameters
    : formalParameter ( COMMA formalParameter )*
    ;

// #58
formalParameter
    : nameDefinition COLON type
    | nameDefinition
    ;

// #59
context
@init {
    helper.pushScope();
}
@after {
    helper.popScope();
}
    : LBRACE RBRACE
    | LBRACE contextEntries RBRACE
    ;

contextEntries
    : contextEntry ( COMMA contextEntry )*
    ;

// #60
contextEntry
    : key { helper.pushName( $key.ctx ); }
      COLON expression { helper.popName(); helper.defineVariable( $key.ctx ); }
    ;

// #61
key
    : nameDefinition   #keyName
    | StringLiteral    #keyString
    ;

nameDefinition
    : nameDefinitionTokens { helper.defineVariable( $nameDefinitionTokens.ctx ); }
    ;
    
nameDefinitionWithEOF
    : nameDefinition EOF
    ;

nameDefinitionTokens
    : Identifier
        ( Identifier
        | additionalNameSymbol
        | IntegerLiteral
        | FloatingPointLiteral
        | reusableKeywords
        | IN
        )*
    ;

iterationNameDefinition
    : iterationNameDefinitionTokens { helper.defineVariable( $iterationNameDefinitionTokens.ctx ); }
    ;

iterationNameDefinitionTokens
    : Identifier
        ( Identifier
        | additionalNameSymbol
        | IntegerLiteral
        | FloatingPointLiteral
        | reusableKeywords
        )*
    ;


additionalNameSymbol
    : //( '.' | '/' | '-' | '\'' | '+' | '*' )
    DOT | DIV | SUB | ADD | MUL | QUOTE
    ;

conditionalOrExpression
	:	conditionalAndExpression                                                 #condOrAnd
 	|	left=conditionalOrExpression op=OR right=conditionalAndExpression    #condOr
	;

conditionalAndExpression
	:	comparisonExpression                                                   #condAndComp
	|	left=conditionalAndExpression op=AND right=comparisonExpression      #condAnd
	;

comparisonExpression
	:	relationalExpression                                                                   #compExpressionRel
	|   left=comparisonExpression op=(LT |
                                      GT |
                                      LE |
                                      GE |
                                      EQUAL |
                                      NOTEQUAL) right=relationalExpression   #compExpression
	;

relationalExpression
	:	additiveExpression                                                                           #relExpressionAdd
	|	val=relationalExpression BETWEEN start=additiveExpression AND end=additiveExpression   #relExpressionBetween
	|   val=relationalExpression IN LPAREN positiveUnaryTests RPAREN                                     #relExpressionTestList
    |   val=relationalExpression IN expression                                                   #relExpressionValue        // includes simpleUnaryTest
    |   val=relationalExpression INSTANCE OF type                                            #relExpressionInstanceOf
	;

expressionList
    :   expression  (COMMA expression)*
    ;

additiveExpression
	:	multiplicativeExpression                            #addExpressionMult
	|	additiveExpression op=ADD multiplicativeExpression  #addExpression
	|	additiveExpression op=SUB multiplicativeExpression  #addExpression
	;

multiplicativeExpression
	:	powerExpression                                              #multExpressionPow
	|	multiplicativeExpression op=( MUL | DIV ) powerExpression    #multExpression
	;

powerExpression
    :   filterPathExpression                           #powExpressionUnary
    |   powerExpression op=POW filterPathExpression   #powExpression
    ;

filterPathExpression
@init {
    int count = 0;
}
    :   unaryExpression
    |   n0=filterPathExpression LBRACK {helper.enableDynamicResolution();} filter=expression {helper.disableDynamicResolution();} RBRACK
    |   n1=filterPathExpression DOT {count = helper.fphStart($n1.ctx, this); helper.enableDynamicResolution();} qualifiedName {helper.disableDynamicResolution(); helper.fphEnd(count);}
    ;

unaryExpression
	:	unaryExpression parameters               #fnInvocation
    |	SUB unaryExpression                      #signedUnaryExpressionMinus
	|   unaryExpressionNotPlusMinus              #nonSignedUnaryExpression
    |	ADD unaryExpressionNotPlusMinus          #signedUnaryExpressionPlus
  	;

unaryExpressionNotPlusMinus
	: primary (DOT {helper.recoverScope();helper.enableDynamicResolution();} qualifiedName parameters? {helper.disableDynamicResolution();helper.dismissScope();} )?   #uenpmPrimary
	;

primary
    : literal                     #primaryLiteral
    | forExpression               #primaryForExpression
    | quantifiedExpression        #primaryQuantifiedExpression
    | ifExpression                #primaryIfExpression
    | interval                    #primaryInterval
    | list                        #primaryList
    | context                     #primaryContext
    | LPAREN expression RPAREN          #primaryParens
    | simplePositiveUnaryTest     #primaryUnaryTest
    | qualifiedName    #primaryName
    ;

// #33 - #39
literal
    :	IntegerLiteral          #numberLiteral
    |	FloatingPointLiteral    #numberLiteral
    |	BooleanLiteral          #boolLiteral
    |   atLiteral               #atLiteralLabel
    |	StringLiteral           #stringLiteral
    |	NULL                #nullLiteral
    ;
    
atLiteral
    : AT atLiteralValue
    ;
    
atLiteralValue 
    : StringLiteral 
    ;

BooleanLiteral
    :   TRUE
    |   FALSE
    ;

/**************************
 *    OTHER CONSTRUCTS
 **************************/

// #7
simplePositiveUnaryTest
    : op=LT  {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneqInterval
    | op=GT  {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneqInterval
    | op=LE {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneqInterval
    | op=GE {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneqInterval
    | op=EQUAL  {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneq
    | op=NOTEQUAL {helper.enableDynamicResolution();}  endpoint {helper.disableDynamicResolution();}   #positiveUnaryTestIneq
    | interval           #positiveUnaryTestInterval
    ;


// #13
simplePositiveUnaryTests
    : simplePositiveUnaryTest ( COMMA simplePositiveUnaryTest )*
    ;


// #14
simpleUnaryTests
    : simplePositiveUnaryTests                     #positiveSimplePositiveUnaryTests
    | NOT LPAREN simplePositiveUnaryTests RPAREN     #negatedSimplePositiveUnaryTests
    | SUB                                          #positiveUnaryTestDash
    ;

// #15
positiveUnaryTest
    : expression
    ;

// #16
positiveUnaryTests
    : positiveUnaryTest ( COMMA positiveUnaryTest )*
    ;


unaryTestsRoot
    : unaryTests EOF
    ;

// #17 (root for decision tables)
unaryTests
    :
    NOT LPAREN positiveUnaryTests RPAREN #unaryTests_negated
    | positiveUnaryTests               #unaryTests_positive
    | SUB                              #unaryTests_empty
    ;

// #18
endpoint
    : additiveExpression
    ;

// #8-#12
interval
    : low=LPAREN start=endpoint ELIPSIS end=endpoint up=RPAREN
    | low=LPAREN start=endpoint ELIPSIS end=endpoint up=LBRACK
    | low=LPAREN start=endpoint ELIPSIS end=endpoint up=RBRACK
    | low=RBRACK start=endpoint ELIPSIS end=endpoint up=RPAREN
    | low=RBRACK start=endpoint ELIPSIS end=endpoint up=LBRACK
    | low=RBRACK start=endpoint ELIPSIS end=endpoint up=RBRACK
    | low=LBRACK start=endpoint ELIPSIS end=endpoint up=RPAREN
    | low=LBRACK start=endpoint ELIPSIS end=endpoint up=LBRACK
    | low=LBRACK start=endpoint ELIPSIS end=endpoint up=RBRACK
    ;

// #20
qualifiedName
locals [ java.util.List<String> qns ]
@init {
    String name = null;
    int count = 0;
    java.util.List<String> qn = new java.util.ArrayList<String>();
}
@after {
    $qns = qn;
    for( int i = 0; i < count; i++ )
        helper.dismissScope();
}
    : n1=nameRef { name = getOriginalText( $n1.ctx ); qn.add( name ); helper.validateVariable( $n1.ctx, qn, name ); }
        ( DOT
            {helper.recoverScope( name ); count++;}
            n2=nameRef
            {name=getOriginalText( $n2.ctx );  qn.add( name ); helper.validateVariable( $n1.ctx, qn, name ); }
        )*
    ;

nameRef
    : ( st=Identifier { helper.startVariable( $st ); }
       | not_st=NOT { helper.startVariable( $not_st ); }
       )  nameRefOtherToken*
    ;

nameRefOtherToken
    : { helper.followUp( _input.LT(1), _localctx==null ) }?
        ~(LPAREN|RPAREN|LBRACK|RBRACK|LBRACE|RBRACE|LT|GT|EQUAL|BANG|COMMA)
    ;

/********************************
 *      KEYWORDS
 ********************************/
reusableKeywords
    : FOR
    | RETURN
    | IF
    | THEN
    | ELSE
    | SOME
    | EVERY
    | SATISFIES
    | INSTANCE
    | OF
    | FUNCTION
    | EXTERNAL
    | OR
    | AND
    | BETWEEN
    | NOT
    | NULL
    | TRUE
    | FALSE
    ;

FOR
    : 'for'
    ;

RETURN
    : 'return'
    ;

// can't be reused
IN
    : 'in'
    ;

IF
    : 'if'
    ;

THEN
    : 'then'
    ;

ELSE
    : 'else'
    ;

SOME
    : 'some'
    ;

EVERY
    : 'every'
    ;

SATISFIES
    : 'satisfies'
    ;

INSTANCE
    : 'instance'
    ;

OF
    : 'of'
    ;

FUNCTION
    : 'function'
    ;

EXTERNAL
    : 'external'
    ;

OR
    : 'or'
    ;

AND
    : 'and'
    ;

BETWEEN
    : 'between'
    ;

NULL
    : 'null'
    ;

TRUE
    : 'true'
    ;

FALSE
    : 'false'
    ;

QUOTE
    :
    '\''
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
	:	'\\' ~[u]     // required to support FEEL regexps
    |   UnicodeEscape // This is not in the spec but prevents having to preprocess the input
	;

fragment
ZeroToThree
	:	[0-3]
	;

// This is not in the spec but prevents having to preprocess the input
fragment
UnicodeEscape
    :   '\\' 'U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
    |   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
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

RARROW : '->';

POW : '**';
ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
BANG
    : '!'
    ;

NOT
    : 'not'
    ;

AT  : '@';

Identifier
    : NameStartChar NameStartCharOrPart*
    ;

fragment
NameStartChar
    : '?' | [A-Z] | '_' | [a-z] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u02FF] | [\u0370-\u037D] | [\u037F-\u1FFF] |
    [\u200C-\u200D] | [\u2070-\u218F] | [\u2C00-\u2FEF] | [\u3001-\uD7FF] | [\uF900-\uFDCF] | [\uFDF0-\uFFFD] | 
    [\u{10000}-\u{EFFFF}];

fragment
NameStartCharOrPart
    : '?' | [A-Z] | '_' | [a-z] | [\u00C0-\u00D6] | [\u00D8-\u00F6] | [\u00F8-\u02FF] | [\u0370-\u037D] | [\u037F-\u1FFF] |
    [\u200C-\u200D] | [\u2070-\u218F] | [\u2C00-\u2FEF] | [\u3001-\uD7FF] | [\uF900-\uFDCF] | [\uFDF0-\uFFFD] | 
    [\u{10000}-\u{EFFFF}]
    | [0-9] | '\u00B7' | [\u0300-\u036F] | [\u203F-\u2040]
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
