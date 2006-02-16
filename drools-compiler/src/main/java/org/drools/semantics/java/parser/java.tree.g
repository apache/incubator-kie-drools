header 
{
	package org.drools.semantics.java.parser;

	import java.util.List;	
	import java.util.ArrayList;	
}

/** Java 1.3 AST Recognizer.
 *
 *  This grammar is in the PUBLIC DOMAIN
 * 
 *	@author John Mitchell		johnm@non.net
 *	@author Terence Parr		parrt@magelang.com
 *	@author John Lilley			jlilley@empathy.com
 *	@author Scott Stanchfield	thetick@magelang.com
 *	@author Markus Mohnen       mohnen@informatik.rwth-aachen.de
 *  @author Peter Williams      pete.williams@sun.com
 *  @author Allan Jacobs        Allan.Jacobs@eng.sun.com
 *  @author Steve Messick       messick@redhills.com
 *
 */
class JavaTreeParser extends TreeParser;

options {
	importVocab = Java;
}

{
	private List variableRefs;

	public void init()
	{
		this.variableRefs = new ArrayList();
	}

	public List getVariableReferences()
	{
		return this.variableRefs;
	}
}

compilationUnit
	:	(packageDefinition)?
		(importDefinition)*
		(typeDefinition)*
	;

ruleFile
	:
		(importDefinition)*
		ruleSet
	;

ruleSet
	:
		#( RULE_SET IDENT 
			( rule )+ )
	;

rule
	:
		#( RULE IDENT 
			#( PARAMETERS ( #(PARAMETER_DEF typeSpec IDENT ) )+ ) 
			whenBlock 
			thenBlock )
	;

whenBlock
	:
		#( WHEN . )
	;

thenBlock
	:
		#( THEN (slist)? )
	;

packageDefinition
	:	#( PACKAGE_DEF identifier )
	;

importDefinition
	:	#( IMPORT identifierStar )
	;

typeDefinition
	:	#(CLASS_DEF modifiers IDENT extendsClause implementsClause objBlock )
	|	#(INTERFACE_DEF modifiers IDENT extendsClause interfaceBlock )
	;

typeSpec
	:	#(TYPE typeSpecArray)
	;

typeSpecArray
	:	#( ARRAY_DECLARATOR typeSpecArray )
	|	type
	;

type:	identifier
	|	builtInType
	;

builtInType
    :   "void"
    |   "boolean"
    |   "byte"
    |   "char"
    |   "short"
    |   "int"
    |   "float"
    |   "long"
    |   "double"
    ;

modifiers
	:	#( MODIFIERS (modifier)* )
	;

modifier
    :   "private"
    |   "public"
    |   "protected"
    |   "static"
    |   "transient"
    |   "final"
    |   "abstract"
    |   "native"
    |   "threadsafe"
    |   "synchronized"
    |   "const"
    |   "volatile"
	|	"strictfp"
    ;

extendsClause
	:	#(EXTENDS_CLAUSE (identifier)* )
	;

implementsClause
	:	#(IMPLEMENTS_CLAUSE (identifier)* )
	;


interfaceBlock
	:	#(	OBJBLOCK
			(	methodDecl
			|	variableDef
			)*
		)
	;
	
objBlock
	:	#(	OBJBLOCK
			(	ctorDef
			|	methodDef
			|	variableDef
			|	typeDefinition
			|	#(STATIC_INIT slist)
			|	#(INSTANCE_INIT slist)
			)*
		)
	;

ctorDef
	:	#(CTOR_DEF modifiers methodHead ctorSList)
	;

methodDecl
	:	#(METHOD_DEF modifiers typeSpec methodHead)
	;

methodDef
	:	#(METHOD_DEF modifiers typeSpec methodHead (slist)?)
	;

variableDef
	:	#(VARIABLE_DEF modifiers typeSpec variableDeclarator varInitializer)
	;

parameterDef
	:	#(PARAMETER_DEF modifiers typeSpec IDENT )
	;

objectinitializer
	:	#(INSTANCE_INIT slist)
	;

variableDeclarator
	:	IDENT
	|	LBRACK variableDeclarator
	;

varInitializer
	:	#(ASSIGN initializer)
	|
	;

initializer
	:	expression
	|	arrayInitializer
	;

arrayInitializer
	:	#(ARRAY_INIT (initializer)*)
	;

methodHead
	:	IDENT #( PARAMETERS (parameterDef)* ) (throwsClause)?
	;

throwsClause
	:	#( "throws" (identifier)* )
	;

identifier
	:	IDENT
	|	#( DOT identifier IDENT )
	;

identifierStar
	:	IDENT
	|	#( DOT identifier (STAR|IDENT) )
	;

ctorSList
	:	#( SLIST (ctorCall)? (stat)* )
	;

slist
	:	#( SLIST (stat)* )
	;

stat:	typeDefinition
	|	variableDef
	|	expression
	|	#(LABELED_STAT IDENT stat)
	|	#("if" expression stat (stat)? )
	|	#(	"for"
			#(FOR_INIT (variableDef | elist)?)
			#(FOR_CONDITION (expression)?)
			#(FOR_ITERATOR (elist)?)
			stat
		)
	|	#("while" expression stat)
	|	#("do" stat expression)
	|	#("break" (IDENT)? )
	|	#("continue" (IDENT)? )
	|	#("return" (expression)? )
	|	#("switch" expression (caseGroup)*)
	|	#("throw" expression)
	|	#("synchronized" expression stat)
	|	tryBlock
	|	slist // nested SLIST
	|	EMPTY_STAT
	;

caseGroup
	:	#(CASE_GROUP (#("case" expression) | "default")+ slist)
	;

tryBlock
	:	#( "try" slist (handler)* (#("finally" slist))? )
	;

handler
	:	#( "catch" parameterDef slist )
	;

elist
	:	#( ELIST (expression)* )
	;

expression
	:	#(EXPR expr)
	;

assignmentCondition
	:
		#(ASSIGN
			i:IDENT
			{
				this.variableRefs.add( i.getText() );
			}
			expr)
	;	

exprCondition
	:
		expr
	;

expr:	#(QUESTION expr expr expr)	// trinary operator
	|	#(ASSIGN expr expr)			// binary operators...
	|	#(PLUS_ASSIGN expr expr)
	|	#(MINUS_ASSIGN expr expr)
	|	#(STAR_ASSIGN expr expr)
	|	#(DIV_ASSIGN expr expr)
	|	#(MOD_ASSIGN expr expr)
	|	#(SR_ASSIGN expr expr)
	|	#(BSR_ASSIGN expr expr)
	|	#(SL_ASSIGN expr expr)
	|	#(BAND_ASSIGN expr expr)
	|	#(BXOR_ASSIGN expr expr)
	|	#(BOR_ASSIGN expr expr)
	|	#(LOR expr expr)
	|	#(LAND expr expr)
	|	#(BOR expr expr)
	|	#(BXOR expr expr)
	|	#(BAND expr expr)
	|	#(NOT_EQUAL expr expr)
	|	#(EQUAL expr expr)
	|	#(LT expr expr)
	|	#(GT expr expr)
	|	#(LE expr expr)
	|	#(GE expr expr)
	|	#(SL expr expr)
	|	#(SR expr expr)
	|	#(BSR expr expr)
	|	#(PLUS expr expr)
	|	#(MINUS expr expr)
	|	#(DIV expr expr)
	|	#(MOD expr expr)
	|	#(STAR expr expr)
	|	#(INC expr)
	|	#(DEC expr)
	|	#(POST_INC expr)
	|	#(POST_DEC expr)
	|	#(BNOT expr)
	|	#(LNOT expr)
	|	#("instanceof" expr expr)
	|	#(UNARY_MINUS expr)
	|	#(UNARY_PLUS expr)
	|	primaryExpression
	;

primaryExpression
    :   i:IDENT
		{
			this.variableRefs.add( i.getText() );
		}
    |   #(	DOT
			(	expr
				(	IDENT
				|	arrayIndex
				|	"this"
				|	"class"
				|	#( "new" IDENT elist )
				|   "super"
				)
			|	#(ARRAY_DECLARATOR typeSpecArray)
			|	builtInType ("class")?
			)
		)
	|	arrayIndex
	|	#(METHOD_CALL primaryExpression elist)
	|	#(TYPECAST typeSpec expr)
	|   newExpression
	|   constant
    |   "super"
    |   "true"
    |   "false"
    |   "this"
    |   "null"
	|	typeSpec // type name used with instanceof
	;

ctorCall
	:	#( CTOR_CALL elist )
	|	#( SUPER_CTOR_CALL
			(	elist
			|	primaryExpression elist
			)
		 )
	;

arrayIndex
	:	#(INDEX_OP primaryExpression expression)
	;

constant
    :   NUM_INT
    |   CHAR_LITERAL
    |   STRING_LITERAL
    |   NUM_FLOAT
    |   NUM_DOUBLE
    |   NUM_LONG
    ;

newExpression
	:	#(	"new" type
			(	newArrayDeclarator (arrayInitializer)?
			|	elist (objBlock)?
			)
		)
			
	;

newArrayDeclarator
	:	#( ARRAY_DECLARATOR (newArrayDeclarator)? (expression)? )
	;
