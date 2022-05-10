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
lhsOr : LPAREN OR lhsAnd+ RPAREN | lhsAnd (OR lhsAnd)* ;
lhsAnd : LPAREN AND lhsUnary+ RPAREN | lhsUnary (AND lhsUnary)* ;

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
lhsPatternBind : label? ( LPAREN lhsPattern (OR lhsPattern)* RPAREN | lhsPattern ) ;

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
relationalExpression : expression? ; // TODO

drlExpression : conditionalExpression ( op=assignmentOperator right=drlExpression )? ;
conditionalExpression : left=conditionalOrExpression ternaryExpression? ;
ternaryExpression : QUESTION ts=drlExpression COLON fs=drlExpression ;

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

rhs : blockStatement+ ;

stringId : ( IDENTIFIER | STRING_LITERAL ) ;

type : IDENTIFIER typeArguments? ( DOT IDENTIFIER typeArguments? )* (LBRACK RBRACK)* ;

//typeArguments : LT typeArgument (COMMA typeArgument)* GT ;
//typeArgument : QUESTION (( EXTENDS | SUPER ) type )? |  type ;

drlArguments : LPAREN drlArgument (COMMA drlArgument)* RPAREN ;
drlArgument : ( stringId | floatLiteral | BOOL_LITERAL | NULL_LITERAL ) ;

drlAnnotation : AT name=qualifiedName drlArguments? ;

attributes : attribute ( COMMA? attribute )* ;
attribute : ( 'salience' DECIMAL_LITERAL )
          | ( 'enabled' | 'no-loop' | 'auto-focus' | 'lock-on-active' | 'refract' | 'direct' ) BOOL_LITERAL?
          | ( 'agenda-group' | 'activation-group' | 'ruleflow-group' | 'date-effective' | 'date-expires' | 'dialect' ) STRING_LITERAL
          |   'calendars' STRING_LITERAL ( COMMA STRING_LITERAL )*
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
