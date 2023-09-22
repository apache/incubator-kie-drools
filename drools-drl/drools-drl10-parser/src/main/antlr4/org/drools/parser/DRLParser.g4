parser grammar DRLParser;

options { tokenVocab=DRLLexer; }

import JavaParser;

    /*
     * statement := importStatement
     *           |  globalStatement
     *           |  declare
     *           |  rule
     *           |  ruleAttribute
     *           |  function
     *           |  query
     *           ;
     */
compilationUnit : packagedef? unitdef? drlStatementdef* ;

drlStatementdef
    : importdef
    | globaldef
    | functiondef
    | attributes
    | ruledef
    ;

packagedef : PACKAGE name=drlQualifiedName SEMI? ;

unitdef : DRL_UNIT name=drlQualifiedName SEMI? ;

importdef : IMPORT (DRL_FUNCTION|STATIC)? drlQualifiedName (DOT MUL)? SEMI? #importStandardDef
          | IMPORT DRL_ACCUMULATE drlQualifiedName IDENTIFIER SEMI?         #importAccumulateDef
          ;

globaldef : DRL_GLOBAL type drlIdentifier SEMI? ;

// rule := RULE stringId (EXTENDS stringId)? annotation* attributes? lhs? rhs END

ruledef : DRL_RULE name=stringId (EXTENDS stringId)? drlAnnotation* attributes? lhs rhs DRL_END ;

lhs : DRL_WHEN lhsExpression* ;

lhsExpression : LPAREN lhsExpression RPAREN             #lhsExpressionEnclosed
              | lhsUnary                                #lhsUnarySingle
              | LPAREN DRL_AND lhsExpression+ RPAREN    #lhsAnd
              | lhsExpression (DRL_AND lhsExpression)+  #lhsAnd
              | LPAREN DRL_OR lhsExpression+ RPAREN     #lhsOr
              | lhsExpression (DRL_OR lhsExpression)+   #lhsOr
              ;

// and is accepted for accumulate
lhsAndForAccumulate : lhsUnary (DRL_AND lhsUnary)*
                    | LPAREN DRL_AND lhsUnary+ RPAREN
                    ;

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
           | lhsAccumulate
           ) ;

lhsPatternBind : label? ( LPAREN lhsPattern (DRL_OR lhsPattern)* RPAREN | lhsPattern ) ;

/*
lhsPattern : xpathPrimary (OVER patternFilter)? |
             ( QUESTION? qualifiedIdentifier LPAREN positionalConstraints? constraints? RPAREN (OVER patternFilter)? (FROM patternSource)? ) ;
*/

lhsPattern : QUESTION? objectType=drlQualifiedName LPAREN positionalConstraints? constraints? RPAREN (DRL_FROM patternSource)? ;
positionalConstraints : constraint (COMMA constraint)* SEMI ;
constraints : constraint (COMMA constraint)* ;
constraint : ( nestedConstraint | conditionalOrExpression ) ;
nestedConstraint : ( IDENTIFIER ( DOT | HASH ) )* IDENTIFIER DOT LPAREN constraints RPAREN ;
conditionalOrExpression : left=conditionalAndExpression (OR right=conditionalAndExpression)* ;
conditionalAndExpression : left=inclusiveOrExpression (AND right=inclusiveOrExpression)* ;
inclusiveOrExpression : left=exclusiveOrExpression (BITOR right=exclusiveOrExpression)* ;
exclusiveOrExpression : left=andExpression (CARET right=andExpression)* ;
andExpression : left=equalityExpression (BITAND right=equalityExpression)* ;
equalityExpression : label? left=instanceOfExpression ( ( op=EQUAL | op=NOTEQUAL ) right=instanceOfExpression )* ;
instanceOfExpression : left=inExpression ( 'instanceof' right=type )? ;
inExpression : left=relationalExpression ( 'not'? 'in' LPAREN drlExpression (COMMA drlExpression)* RPAREN )? ;
relationalExpression : left=drlExpression (right=orRestriction)* ;
orRestriction : left=andRestriction (OR right=andRestriction)* ;
andRestriction : left=singleRestriction (AND right=singleRestriction)* ;
singleRestriction : op=relationalOperator drlExpression ;

relationalOperator
    : EQUAL
    | NOTEQUAL
    | LE
    | GE
    | GT
    | LT
    ;

/* function := FUNCTION type? ID parameters(typed) chunk_{_} */
functiondef : DRL_FUNCTION typeTypeOrVoid? IDENTIFIER formalParameters block ;


/* extending JavaParser qualifiedName */
drlQualifiedName
    : drlIdentifier (DOT drlIdentifier)*
    ;

/* extending JavaParser identifier */
drlIdentifier
    : drlKeywords
    | IDENTIFIER
    | MODULE
    | OPEN
    | REQUIRES
    | EXPORTS
    | OPENS
    | TO
    | USES
    | PROVIDES
    | WITH
    | TRANSITIVE
    | YIELD
    | SEALED
    | PERMITS
    | RECORD
    | VAR
    ;

drlKeywords
    : DRL_UNIT
    | DRL_FUNCTION
    | DRL_GLOBAL
    | DRL_RULE
    | DRL_QUERY
    | DRL_WHEN
    | DRL_THEN
    | DRL_END
    | DRL_AND
    | DRL_OR
    | DRL_EXISTS
    | DRL_NOT
    | DRL_IN
    | DRL_FROM
    | DRL_MATCHES
    | DRL_SALIENCE
    | DRL_ENABLED
    | DRL_NO_LOOP
    | DRL_AUTO_FOCUS
    | DRL_LOCK_ON_ACTIVE
    | DRL_REFRACT
    | DRL_DIRECT
    | DRL_AGENDA_GROUP
    | DRL_ACTIVATION_GROUP
    | DRL_RULEFLOW_GROUP
    | DRL_DATE_EFFECTIVE
    | DRL_DATE_EXPIRES
    | DRL_DIALECT
    | DRL_CALENDARS
    | DRL_TIMER
    | DRL_DURATION
    ;

/* extending JavaParser expression */
drlExpression
    : drlPrimary
    | drlExpression bop=DOT
      (
         drlIdentifier
       | methodCall
       | THIS
       | NEW nonWildcardTypeArguments? innerCreator
       | SUPER superSuffix
       | explicitGenericInvocation
      )
    | drlExpression LBRACK drlExpression RBRACK
    | methodCall
    | NEW drlCreator
    | LPAREN annotation* typeType (BITAND typeType)* RPAREN drlExpression
    | drlExpression postfix=(INC | DEC)
    | prefix=(ADD|SUB|INC|DEC) drlExpression
    | prefix=(TILDE|BANG) drlExpression
    | drlExpression bop=(MUL|DIV|MOD) drlExpression
    | drlExpression bop=(ADD|SUB) drlExpression
    | drlExpression (LT LT | GT GT GT | GT GT) drlExpression
    | drlExpression bop=(LE | GE | GT | LT) drlExpression
    | drlExpression bop=INSTANCEOF (typeType | pattern)
    | drlExpression bop=DRL_MATCHES drlExpression
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
    | drlExpression COLONCOLON typeArguments? drlIdentifier
    | typeType COLONCOLON (typeArguments? drlIdentifier | NEW)
    | classType COLONCOLON typeArguments? NEW
    ;

/* extending JavaParser primary */
drlPrimary
    : LPAREN drlExpression RPAREN
    | THIS
    | SUPER
    | drlLiteral
    | drlIdentifier
    | typeTypeOrVoid DOT CLASS
    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
    | inlineListExpression
    | inlineMapExpression
    ;

/* extending JavaParser literal */
drlLiteral
    : integerLiteral
    | floatLiteral
    | DRL_BIG_DECIMAL_LITERAL
    | DRL_BIG_INTEGER_LITERAL
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

inlineMapExpression
    :	LBRACK mapExpressionList RBRACK
    ;

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;

mapEntry
    :	drlExpression COLON drlExpression
    ;

/*
 patternSource := FROM
                ( fromAccumulate
                | fromCollect
                | fromEntryPoint
                | fromWindow
                | fromExpression )
*/
patternSource : fromExpression
              | fromAccumulate
              | fromEntryPoint
              ;

fromExpression : conditionalOrExpression ;


/*
fromAccumulate := ACCUMULATE LEFT_PAREN lhsAnd (COMMA|SEMICOLON)
                        ( INIT chunk_(_) COMMA ACTION chunk_(_) COMMA
                          ( REVERSE chunk_(_) COMMA)? RESULT chunk_(_)
                        | accumulateFunction
                        ) RIGHT_PAREN
*/
fromAccumulate : DRL_ACCUMULATE LPAREN lhsAndForAccumulate (COMMA|SEMI)
                   ( DRL_INIT LPAREN initBlockStatements=blockStatements RPAREN COMMA DRL_ACTION LPAREN actionBlockStatements=blockStatements RPAREN COMMA ( DRL_REVERSE LPAREN reverseBlockStatements=blockStatements RPAREN COMMA)? DRL_RESULT LPAREN expression RPAREN
                   | accumulateFunction
                   )
                 RPAREN (SEMI)?
                 ;

blockStatements : blockStatement* ;

/*
accumulateFunction := label? ID parameters
*/
accumulateFunction : label? IDENTIFIER LPAREN drlExpression RPAREN;

fromEntryPoint : DRL_ENTRY_POINT stringId ;

/*
 lhsExists := EXISTS
           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
           | LEFT_PAREN lhsOr RIGHT_PAREN
           | lhsPatternBind
           )
*/
lhsExists : DRL_EXISTS lhsPatternBind ;
/*
 lhsNot := NOT
           ( (LEFT_PAREN (or_key|and_key))=> lhsOr  // prevents '((' for prefixed and/or
           | LEFT_PAREN lhsOr RIGHT_PAREN
           | lhsPatternBind
           )
*/
lhsNot : DRL_NOT lhsPatternBind ;

/**
 * lhsAccumulate := (ACCUMULATE|ACC) LEFT_PAREN lhsAnd (COMMA|SEMICOLON)
 *                      accumulateFunctionBinding (COMMA accumulateFunctionBinding)*
 *                      (SEMICOLON constraints)?
 *                  RIGHT_PAREN SEMICOLON?
 */

lhsAccumulate : DRL_ACCUMULATE LPAREN lhsAndForAccumulate (COMMA|SEMI)
                   accumulateFunction (COMMA accumulateFunction)*
                   (SEMI constraints)?
                 RPAREN (SEMI)?
                 ;

rhs : DRL_THEN consequence ;

consequence : RHS_CHUNK* ;

stringId : ( IDENTIFIER | DRL_STRING_LITERAL ) ;

type : IDENTIFIER typeArguments? ( DOT IDENTIFIER typeArguments? )* (LBRACK RBRACK)* ;

//typeArguments : LT typeArgument (COMMA typeArgument)* GT ;
//typeArgument : QUESTION (( EXTENDS | SUPER ) type )? |  type ;

drlArguments : LPAREN drlArgument (COMMA drlArgument)* RPAREN ;
drlArgument : ( stringId | floatLiteral | BOOL_LITERAL | NULL_LITERAL ) ;

drlAnnotation : AT name=drlQualifiedName drlArguments? ;

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

/* extending JavaParser variableInitializer */
drlVariableInitializer
    : arrayInitializer
    | drlExpression
    ;

 drlCreator
     : nonWildcardTypeArguments createdName classCreatorRest
     | createdName (drlArrayCreatorRest | classCreatorRest)
     ;

 drlArrayCreatorRest
     : LBRACK (RBRACK (LBRACK RBRACK)* drlArrayInitializer | expression RBRACK (LBRACK expression RBRACK)* (LBRACK RBRACK)*)
     ;

 drlArrayInitializer
     : LBRACE (drlVariableInitializer (COMMA drlVariableInitializer)* (COMMA)? )? RBRACE
     ;
