/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr, Sam Harwell
 Copyright (c) 2017 Ivan Kochurkin (upgrade to Java 8)
 Copyright (c) 2021 Michał Lorek (upgrade to Java 11)
 Copyright (c) 2022 Michał Lorek (upgrade to Java 17)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

// source: https://github.com/antlr/grammars-v4/tree/master/java/java

parser grammar JavaParser;

options { tokenVocab=JavaLexer; }

compilationUnit
    : packageDeclaration? importDeclaration* typeDeclaration*
    | moduleDeclaration EOF
    ;

packageDeclaration
    : annotation* PACKAGE qualifiedName SEMI
    ;

importDeclaration
    : IMPORT STATIC? qualifiedName (DOT MUL)? SEMI
    ;

typeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | enumDeclaration | interfaceDeclaration | annotationTypeDeclaration | recordDeclaration)
    | SEMI
    ;

modifier
    : classOrInterfaceModifier
    | NATIVE
    | SYNCHRONIZED
    | TRANSIENT
    | VOLATILE
    ;

classOrInterfaceModifier
    : annotation
    | PUBLIC
    | PROTECTED
    | PRIVATE
    | STATIC
    | ABSTRACT
    | FINAL    // FINAL for class only -- does not apply to interfaces
    | STRICTFP
    | SEALED // Java17
    | NON_SEALED // Java17
    ;

variableModifier
    : FINAL
    | annotation
    ;

classDeclaration
    : CLASS identifier typeParameters?
      (EXTENDS typeType)?
      (IMPLEMENTS typeList)?
      (PERMITS typeList)? // Java17
      classBody
    ;

typeParameters
    : LT typeParameter (COMMA typeParameter)* GT
    ;

typeParameter
    : annotation* identifier (EXTENDS annotation* typeBound)?
    ;

typeBound
    : typeType (BITAND typeType)*
    ;

enumDeclaration
    : ENUM identifier (IMPLEMENTS typeList)? LBRACE enumConstants? COMMA? enumBodyDeclarations? RBRACE
    ;

enumConstants
    : enumConstant (COMMA enumConstant)*
    ;

enumConstant
    : annotation* identifier arguments? classBody?
    ;

enumBodyDeclarations
    : SEMI classBodyDeclaration*
    ;

interfaceDeclaration
    : INTERFACE identifier typeParameters? (EXTENDS typeList)? (PERMITS typeList)? interfaceBody
    ;

classBody
    : LBRACE classBodyDeclaration* RBRACE
    ;

interfaceBody
    : LBRACE interfaceBodyDeclaration* RBRACE
    ;

classBodyDeclaration
    : SEMI
    | STATIC? block
    | modifier* memberDeclaration
    ;

memberDeclaration
    : methodDeclaration
    | genericMethodDeclaration
    | fieldDeclaration
    | constructorDeclaration
    | genericConstructorDeclaration
    | interfaceDeclaration
    | annotationTypeDeclaration
    | classDeclaration
    | enumDeclaration
    | recordDeclaration //Java17
    ;

/* We use rule this even for void methods which cannot have [] after parameters.
   This simplifies grammar and we can consider void to be a type, which
   renders the [] matching as a context-sensitive issue or a semantic check
   for invalid return type after parsing.
 */
methodDeclaration
    : typeTypeOrVoid identifier formalParameters (LBRACK RBRACK)*
      (THROWS qualifiedNameList)?
      methodBody
    ;

methodBody
    : block
    | SEMI
    ;

typeTypeOrVoid
    : typeType
    | VOID
    ;

genericMethodDeclaration
    : typeParameters methodDeclaration
    ;

genericConstructorDeclaration
    : typeParameters constructorDeclaration
    ;

constructorDeclaration
    : identifier formalParameters (THROWS qualifiedNameList)? constructorBody=block
    ;

fieldDeclaration
    : typeType variableDeclarators SEMI
    ;

interfaceBodyDeclaration
    : modifier* interfaceMemberDeclaration
    | SEMI
    ;

interfaceMemberDeclaration
    : constDeclaration
    | interfaceMethodDeclaration
    | genericInterfaceMethodDeclaration
    | interfaceDeclaration
    | annotationTypeDeclaration
    | classDeclaration
    | enumDeclaration
    | recordDeclaration // Java17
    ;

constDeclaration
    : typeType constantDeclarator (COMMA constantDeclarator)* SEMI
    ;

constantDeclarator
    : identifier (LBRACK RBRACK)* ASSIGN variableInitializer
    ;

// Early versions of Java allows brackets after the method name, eg.
// public int[] return2DArray() [] { ... }
// is the same as
// public int[][] return2DArray() { ... }
interfaceMethodDeclaration
    : interfaceMethodModifier* interfaceCommonBodyDeclaration
    ;

// Java8
interfaceMethodModifier
    : annotation
    | PUBLIC
    | ABSTRACT
    | DEFAULT
    | STATIC
    | STRICTFP
    ;

genericInterfaceMethodDeclaration
    : interfaceMethodModifier* typeParameters interfaceCommonBodyDeclaration
    ;

interfaceCommonBodyDeclaration
    : annotation* typeTypeOrVoid identifier formalParameters (LBRACK RBRACK)* (THROWS qualifiedNameList)? methodBody
    ;

variableDeclarators
    : variableDeclarator (COMMA variableDeclarator)*
    ;

variableDeclarator
    : variableDeclaratorId (ASSIGN variableInitializer)?
    ;

variableDeclaratorId
    : identifier (LBRACK RBRACK)*
    ;

variableInitializer
    : arrayInitializer
    | expression
    ;

arrayInitializer
    : LBRACE (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RBRACE
    ;

classOrInterfaceType
    : identifier typeArguments? (DOT identifier typeArguments?)*
    ;

typeArgument
    : typeType
    | annotation* QUESTION ((EXTENDS | SUPER) typeType)?
    ;

qualifiedNameList
    : qualifiedName (COMMA qualifiedName)*
    ;

formalParameters
    : LPAREN ( receiverParameter?
          | receiverParameter (COMMA formalParameterList)?
          | formalParameterList?
          ) RPAREN
    ;

receiverParameter
    : typeType (identifier DOT)* THIS
    ;

formalParameterList
    : formalParameter (COMMA formalParameter)* (COMMA lastFormalParameter)?
    | lastFormalParameter
    ;

formalParameter
    : variableModifier* typeType variableDeclaratorId
    ;

lastFormalParameter
    : variableModifier* typeType annotation* ELLIPSIS variableDeclaratorId
    ;

// local variable type inference
lambdaLVTIList
    : lambdaLVTIParameter (COMMA lambdaLVTIParameter)*
    ;

lambdaLVTIParameter
    : variableModifier* VAR identifier
    ;

qualifiedName
    : identifier (DOT identifier)*
    ;

literal
    : integerLiteral
    | floatLiteral
    | CHAR_LITERAL
    | STRING_LITERAL
    | BOOL_LITERAL
    | NULL_LITERAL
    | TEXT_BLOCK // Java17
    ;

integerLiteral
    : DECIMAL_LITERAL
    | HEX_LITERAL
    | OCT_LITERAL
    | BINARY_LITERAL
    ;

floatLiteral
    : FLOAT_LITERAL
    | HEX_FLOAT_LITERAL
    ;

// ANNOTATIONS
altAnnotationQualifiedName
    : (identifier DOT)* AT identifier
    ;

annotation
    : (AT qualifiedName | altAnnotationQualifiedName) (LPAREN ( elementValuePairs | elementValue )? RPAREN)?
    ;

elementValuePairs
    : elementValuePair (COMMA elementValuePair)*
    ;

elementValuePair
    : identifier ASSIGN elementValue
    ;

elementValue
    : expression
    | annotation
    | elementValueArrayInitializer
    ;

elementValueArrayInitializer
    : LBRACE (elementValue (COMMA elementValue)*)? (COMMA)? RBRACE
    ;

annotationTypeDeclaration
    : AT INTERFACE identifier annotationTypeBody
    ;

annotationTypeBody
    : LBRACE (annotationTypeElementDeclaration)* RBRACE
    ;

annotationTypeElementDeclaration
    : modifier* annotationTypeElementRest
    | SEMI // this is not allowed by the grammar, but apparently allowed by the actual compiler
    ;

annotationTypeElementRest
    : typeType annotationMethodOrConstantRest SEMI
    | classDeclaration SEMI?
    | interfaceDeclaration SEMI?
    | enumDeclaration SEMI?
    | annotationTypeDeclaration SEMI?
    | recordDeclaration SEMI? // Java17
    ;

annotationMethodOrConstantRest
    : annotationMethodRest
    | annotationConstantRest
    ;

annotationMethodRest
    : identifier LPAREN RPAREN defaultValue?
    ;

annotationConstantRest
    : variableDeclarators
    ;

defaultValue
    : DEFAULT elementValue
    ;

// MODULES - Java9

moduleDeclaration
    : OPEN? MODULE qualifiedName moduleBody
    ;

moduleBody
    : LBRACE moduleDirective* RBRACE
    ;

moduleDirective
	: REQUIRES requiresModifier* qualifiedName SEMI
	| EXPORTS qualifiedName (TO qualifiedName)? SEMI
	| OPENS qualifiedName (TO qualifiedName)? SEMI
	| USES qualifiedName SEMI
	| PROVIDES qualifiedName WITH qualifiedName SEMI
	;

requiresModifier
	: TRANSITIVE
	| STATIC
	;

// RECORDS - Java 17

recordDeclaration
    : RECORD identifier typeParameters? recordHeader
      (IMPLEMENTS typeList)?
      recordBody
    ;

recordHeader
    : LPAREN recordComponentList? RPAREN
    ;

recordComponentList
    : recordComponent (COMMA recordComponent)*
    ;

recordComponent
    : typeType identifier
    ;

recordBody
    : LBRACE classBodyDeclaration* RBRACE
    ;

// STATEMENTS / BLOCKS

block
    : LBRACE blockStatement* RBRACE
    ;

blockStatement
    : localVariableDeclaration SEMI
    | statement
    | localTypeDeclaration
    ;

localVariableDeclaration
    : variableModifier* (typeType variableDeclarators | VAR identifier ASSIGN expression)
    ;

identifier
    : IDENTIFIER
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

localTypeDeclaration
    : classOrInterfaceModifier*
      (classDeclaration | interfaceDeclaration | recordDeclaration)
    | SEMI
    ;

statement
    : blockLabel=block
    | ASSERT expression (COLON expression)? SEMI
    | IF parExpression statement (ELSE statement)?
    | FOR LPAREN forControl RPAREN statement
    | WHILE parExpression statement
    | DO statement WHILE parExpression SEMI
    | TRY block (catchClause+ finallyBlock? | finallyBlock)
    | TRY resourceSpecification block catchClause* finallyBlock?
    | SWITCH parExpression LBRACE switchBlockStatementGroup* switchLabel* RBRACE
    | SYNCHRONIZED parExpression block
    | RETURN expression? SEMI
    | THROW expression SEMI
    | BREAK identifier? SEMI
    | CONTINUE identifier? SEMI
    | YIELD expression SEMI // Java17
    | SEMI
    | statementExpression=expression SEMI
    | switchExpression SEMI? // Java17
    | identifierLabel=identifier COLON statement
    ;

catchClause
    : CATCH LPAREN variableModifier* catchType identifier RPAREN block
    ;

catchType
    : qualifiedName (BITOR qualifiedName)*
    ;

finallyBlock
    : FINALLY block
    ;

resourceSpecification
    : LPAREN resources SEMI? RPAREN
    ;

resources
    : resource (SEMI resource)*
    ;

resource
    : variableModifier* ( classOrInterfaceType variableDeclaratorId | VAR identifier ) ASSIGN expression
    | identifier
    ;

/** Matches cases then statements, both of which are mandatory.
 *  To handle empty cases at the end, we add switchLabel* to statement.
 */
switchBlockStatementGroup
    : switchLabel+ blockStatement+
    ;

switchLabel
    : CASE (constantExpression=expression | enumConstantName=IDENTIFIER | typeType varName=identifier) COLON
    | DEFAULT COLON
    ;

forControl
    : enhancedForControl
    | forInit? SEMI expression? SEMI forUpdate=expressionList?
    ;

forInit
    : localVariableDeclaration
    | expressionList
    ;

enhancedForControl
    : variableModifier* (typeType | VAR) variableDeclaratorId COLON expression
    ;

// EXPRESSIONS

parExpression
    : LPAREN expression RPAREN
    ;

expressionList
    : expression (COMMA expression)*
    ;

methodCall
    : identifier LPAREN expressionList? RPAREN
    | THIS LPAREN expressionList? RPAREN
    | SUPER LPAREN expressionList? RPAREN
    ;

expression
    : primary
    | expression bop=DOT
      (
         identifier
       | methodCall
       | THIS
       | NEW nonWildcardTypeArguments? innerCreator
       | SUPER superSuffix
       | explicitGenericInvocation
      )
    | expression LBRACK expression RBRACK
    | methodCall
    | NEW creator
    | LPAREN annotation* typeType (BITAND typeType)* RPAREN expression
    | expression postfix=(INC | DEC)
    | prefix=(ADD|SUB|INC|DEC) expression
    | prefix=(TILDE|BANG) expression
    | expression bop=(MUL|DIV|MOD) expression
    | expression bop=(ADD|SUB) expression
    | expression (LT LT | GT GT GT | GT GT) expression
    | expression bop=(LE | GE | GT | LT) expression
    | expression bop=INSTANCEOF (typeType | pattern)
    | expression bop=(EQUAL | NOTEQUAL) expression
    | expression bop=BITAND expression
    | expression bop=CARET expression
    | expression bop=BITOR expression
    | expression bop=AND expression
    | expression bop=OR expression
    | <assoc=right> expression bop=QUESTION expression COLON expression
    | <assoc=right> expression
      bop=(ASSIGN | ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | RSHIFT_ASSIGN | URSHIFT_ASSIGN | LSHIFT_ASSIGN | MOD_ASSIGN)
      expression
    | lambdaExpression // Java8
    | switchExpression // Java17

    // Java 8 methodReference
    | expression COLONCOLON typeArguments? identifier
    | typeType COLONCOLON (typeArguments? identifier | NEW)
    | classType COLONCOLON typeArguments? NEW
    ;

// Java17
pattern
    : variableModifier* typeType annotation* identifier
    ;

// Java8
lambdaExpression
    : lambdaParameters ARROW lambdaBody
    ;

// Java8
lambdaParameters
    : identifier
    | LPAREN formalParameterList? RPAREN
    | LPAREN identifier (COMMA identifier)* RPAREN
    | LPAREN lambdaLVTIList? RPAREN
    ;

// Java8
lambdaBody
    : expression
    | block
    ;

primary
    : LPAREN expression RPAREN
    | THIS
    | SUPER
    | literal
    | identifier
    | typeTypeOrVoid DOT CLASS
    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
    ;

// Java17
switchExpression
    : SWITCH parExpression LBRACE switchLabeledRule* RBRACE
    ;

// Java17
switchLabeledRule
    : CASE (expressionList | NULL_LITERAL | guardedPattern) (ARROW | COLON) switchRuleOutcome
    | DEFAULT (ARROW | COLON) switchRuleOutcome
    ;

// Java17
guardedPattern
    : LPAREN guardedPattern RPAREN
    | variableModifier* typeType annotation* identifier (AND expression)*
    | guardedPattern AND expression
    ;

// Java17
switchRuleOutcome
    : block
    | blockStatement*
    ;

classType
    : (classOrInterfaceType DOT)? annotation* identifier typeArguments?
    ;

creator
    : nonWildcardTypeArguments createdName classCreatorRest
    | createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    : identifier typeArgumentsOrDiamond? (DOT identifier typeArgumentsOrDiamond?)*
    | primitiveType
    ;

innerCreator
    : identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
    ;

arrayCreatorRest
    : LBRACK (RBRACK (LBRACK RBRACK)* arrayInitializer | expression RBRACK (LBRACK expression RBRACK)* (LBRACK RBRACK)*)
    ;

classCreatorRest
    : arguments classBody?
    ;

explicitGenericInvocation
    : nonWildcardTypeArguments explicitGenericInvocationSuffix
    ;

typeArgumentsOrDiamond
    : LT GT
    | typeArguments
    ;

nonWildcardTypeArgumentsOrDiamond
    : LT GT
    | nonWildcardTypeArguments
    ;

nonWildcardTypeArguments
    : LT typeList GT
    ;

typeList
    : typeType (COMMA typeType)*
    ;

typeType
    : annotation* (classOrInterfaceType | primitiveType) (annotation* LBRACK RBRACK)*
    ;

primitiveType
    : BOOLEAN
    | CHAR
    | BYTE
    | SHORT
    | INT
    | LONG
    | FLOAT
    | DOUBLE
    ;

typeArguments
    : LT typeArgument (COMMA typeArgument)* GT
    ;

superSuffix
    : arguments
    | DOT typeArguments? identifier arguments?
    ;

explicitGenericInvocationSuffix
    : SUPER superSuffix
    | identifier arguments
    ;

arguments
    : LPAREN expressionList? RPAREN
    ;