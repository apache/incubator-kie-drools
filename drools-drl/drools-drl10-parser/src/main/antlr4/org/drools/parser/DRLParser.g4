parser grammar DRLParser;

options { tokenVocab=DRLLexer; }

import JavaParser;


compilationUnit : packagedef? unitdef? importdef* globaldef* ruledef* ;

packagedef : PACKAGE name=qualifiedName SEMI? ;

unitdef : UNIT name=qualifiedName SEMI? ;

importdef : IMPORT (FUNCTION|STATIC)? qualifiedName (DOT MUL)? SEMI? ;

globaldef : GLOBAL type IDENTIFIER SEMI? ;

ruledef : RULE name=stringId (EXTENDS stringId)? drlAnnotation* attributes? WHEN lhs THEN rhs END ;

lhs : lhsExpression ;
lhsExpression : lhsOr* ;
lhsOr : LPAREN KWD_OR lhsAnd+ RPAREN | lhsAnd (KWD_OR lhsAnd)* ;
lhsAnd : LPAREN KWD_AND lhsUnary+ RPAREN | lhsUnary (KWD_AND lhsUnary)* ;

/*
lhsUnary : ( lhsExists namedConsequence?
           | lhsNot namedConsequence?
           | lhsEval consequenceInvocation*
           | lhsForall
           | lhsAccumulate
           | LPAREN lhsOr RPAREN namedConsequence?
           | lhsPatternBind consequenceInvocation*
           ) SEMI? ;
*/

lhsUnary : (
           lhsExists
           | lhsNot
           | lhsPatternBind
           ) ;
lhsPatternBind : label? ( LPAREN lhsPattern (KWD_OR lhsPattern)* RPAREN | lhsPattern ) ;

/*
lhsPattern : xpathPrimary (OVER patternFilter)? |
             ( QUESTION? qualifiedIdentifier LPAREN positionalConstraints? constraints? RPAREN (OVER patternFilter)? (FROM patternSource)? ) ;
*/

lhsPattern : QUESTION? objectType=qualifiedName LPAREN (positionalConstraints? constraints? ) RPAREN (FROM patternSource)? ;
positionalConstraints : constraint (COMMA constraint)* SEMI ;
constraints : constraint (COMMA constraint)* ;
constraint : label? ( nestedConstraint | conditionalOrExpression ) ;
nestedConstraint : ( IDENTIFIER ( DOT | HASH ) )* IDENTIFIER DOT LPAREN constraints RPAREN ;
conditionalOrExpression : left=conditionalAndExpression (OR right=conditionalAndExpression)* ;
conditionalAndExpression : left=inclusiveOrExpression (AND right=inclusiveOrExpression)* ;
inclusiveOrExpression : left=exclusiveOrExpression (BITOR right=exclusiveOrExpression)* ;
exclusiveOrExpression : left=andExpression (CARET right=andExpression)* ;
andExpression : left=equalityExpression (BITAND right=equalityExpression)* ;
equalityExpression : left=instanceOfExpression ( ( op=EQUAL | op=NOTEQUAL ) right=instanceOfExpression )* ;
instanceOfExpression : left=inExpression ( 'instanceof' right=type )? ;
inExpression : left=relationalExpression ( 'not'? 'in' LPAREN drlExpression (COMMA drlExpression)* RPAREN )? ;
relationalExpression : drlExpression? ;

/* extending JavaParser expression */
drlExpression
    : drlPrimary
    | drlExpression bop=DOT
      (
         identifier
       | methodCall
       | THIS
       | NEW nonWildcardTypeArguments? innerCreator
       | SUPER superSuffix
       | explicitGenericInvocation
      )
    | drlExpression LBRACK drlExpression RBRACK
    | methodCall
    | NEW creator
    | LPAREN annotation* typeType (BITAND typeType)* RPAREN drlExpression
    | drlExpression postfix=(INC | DEC)
    | prefix=(ADD|SUB|INC|DEC) drlExpression
    | prefix=(TILDE|BANG) drlExpression
    | drlExpression bop=(MUL|DIV|MOD) drlExpression
    | drlExpression bop=(ADD|SUB) drlExpression
    | drlExpression (LT LT | GT GT GT | GT GT) drlExpression
    | drlExpression bop=(LE | GE | GT | LT) drlExpression
    | drlExpression bop=INSTANCEOF (typeType | pattern)
    | drlExpression bop=MATCHES drlExpression
    | drlExpression bop=(EQUAL | NOTEQUAL) drlExpression
    | drlExpression bop=BITAND drlExpression
    | drlExpression bop=CARET drlExpression
    | drlExpression bop=BITOR drlExpression
    | drlExpression bop=AND drlExpression
    | drlExpression bop=OR drlExpression
    | <assoc=right> drlExpression bop=QUESTION drlExpression COLON drlExpression
    | <assoc=right> drlExpression
      bop=(ASSIGN | ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | RSHIFT_ASSIGN | URSHIFT_ASSIGN | LSHIFT_ASSIGN | MOD_ASSIGN)
      drlExpression
    | lambdaExpression // Java8
    | switchExpression // Java17

    // Java 8 methodReference
    | drlExpression COLONCOLON typeArguments? identifier
    | typeType COLONCOLON (typeArguments? identifier | NEW)
    | classType COLONCOLON typeArguments? NEW
    ;

/* extending JavaParser primary */
drlPrimary
    : LPAREN drlExpression RPAREN
    | THIS
    | SUPER
    | drlLiteral
    | identifier
    | typeTypeOrVoid DOT CLASS
    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
    | inlineListExpression
    ;

/* extending JavaParser literal */
drlLiteral
    : integerLiteral
    | floatLiteral
    | CHAR_LITERAL
    | DRL_STRING_LITERAL
    | BOOL_LITERAL
    | NULL_LITERAL
    | TEXT_BLOCK // Java17
    ;

inlineListExpression
    :   LBRACK expressionList? RBRACK
    ;

expressionList
    :   drlExpression (COMMA drlExpression)*
    ;

/*
 patternSource := FROM
                ( fromAccumulate
                | fromCollect
                | fromEntryPoint
                | fromWindow
                | fromExpression )
*/
patternSource : fromExpression ;
fromExpression : conditionalOrExpression ;

/*
 lhsExists := EXISTS
           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
           | LEFT_PAREN lhsOr RIGHT_PAREN
           | lhsPatternBind
           )
*/
lhsExists : EXISTS lhsPatternBind ;
/*
 lhsNot := NOT
           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
           | LEFT_PAREN lhsOr RIGHT_PAREN
           | lhsPatternBind
           )
*/
lhsNot : NOT lhsPatternBind ;

rhs : blockStatement* ;

stringId : ( IDENTIFIER | DRL_STRING_LITERAL ) ;

type : IDENTIFIER typeArguments? ( DOT IDENTIFIER typeArguments? )* (LBRACK RBRACK)* ;

//typeArguments : LT typeArgument (COMMA typeArgument)* GT ;
//typeArgument : QUESTION (( EXTENDS | SUPER ) type )? |  type ;

drlArguments : LPAREN drlArgument (COMMA drlArgument)* RPAREN ;
drlArgument : ( stringId | floatLiteral | BOOL_LITERAL | NULL_LITERAL ) ;

drlAnnotation : AT name=qualifiedName drlArguments? ;

attributes : attribute ( COMMA? attribute )* ;
attribute : ( 'salience' DECIMAL_LITERAL )
          | ( 'enabled' | 'no-loop' | 'auto-focus' | 'lock-on-active' | 'refract' | 'direct' ) BOOL_LITERAL?
          | ( 'agenda-group' | 'activation-group' | 'ruleflow-group' | 'date-effective' | 'date-expires' | 'dialect' ) DRL_STRING_LITERAL
          |   'calendars' DRL_STRING_LITERAL ( COMMA DRL_STRING_LITERAL )*
          |   'timer' ( DECIMAL_LITERAL | TEXT )
          |   'duration' ( DECIMAL_LITERAL | TEXT ) ;

assignmentOperator : ASSIGN
                   |   ADD_ASSIGN
                   |   SUB_ASSIGN
                   |   MUL_ASSIGN
                   |   DIV_ASSIGN
                   |   AND_ASSIGN
                   |   OR_ASSIGN
                   |   XOR_ASSIGN
                   |   MOD_ASSIGN
                   |   LT LT ASSIGN ;

label : IDENTIFIER COLON ;
unif : IDENTIFIER UNIFY ;
