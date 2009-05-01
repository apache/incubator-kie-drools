/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 * 		
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *	Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *	and forVarControl to use variableModifier* not 'final'? (annotation)?
 */
grammar Java;
options {k=2; backtrack=true; memoize=true;}

@parser::header {
	package org.drools.rule.builder.dialect.java.parser;
	import java.util.Iterator;
}

@parser::members {
	private List identifiers = new ArrayList();
	public List getIdentifiers() { return identifiers; }
	private List localDeclarations = new ArrayList();
	public List getLocalDeclarations() { return localDeclarations; }
	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
	private List errors = new ArrayList();
	private int localVariableLevel = 0;
	private List<JavaBlockDescr> blocks = new ArrayList<JavaBlockDescr>();
	public List<JavaBlockDescr> getBlockDescr() { return blocks; }
	
	private String source = "unknown";
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return this.source;
	}
		
	public void reportError(RecognitionException ex) {
	        // if we've already reported an error and have not matched a token
                // yet successfully, don't report any errors.
                if ( state.errorRecovery ) {
                        //System.err.print("[SPURIOUS] ");
                        return;
                }
                state.errorRecovery = true;

		errors.add( ex ); 
	}
     	
     	/** return the raw RecognitionException errors */
     	public List getErrors() {
     		return errors;
     	}
     	
     	/** Return a list of pretty strings summarising the errors */
     	public List getErrorMessages() {
     		List messages = new ArrayList();
 		for ( Iterator errorIter = errors.iterator() ; errorIter.hasNext() ; ) {
     	     		messages.add( createErrorMessage( (RecognitionException) errorIter.next() ) );
     	     	}
     	     	return messages;
     	}
     	
     	/** return true if any parser errors were accumulated */
     	public boolean hasErrors() {
  		return ! errors.isEmpty();
     	}
     	
     	/** This will take a RecognitionException, and create a sensible error message out of it */
     	public String createErrorMessage(RecognitionException e)
        {
		StringBuffer message = new StringBuffer();		
                message.append( source + ":"+e.line+":"+e.charPositionInLine+" ");
                if ( e instanceof MismatchedTokenException ) {
                        MismatchedTokenException mte = (MismatchedTokenException)e;
                        message.append("mismatched token: "+
                                                           e.token+
                                                           "; expecting type "+
                                                           tokenNames[mte.expecting]);
                }
                else if ( e instanceof MismatchedTreeNodeException ) {
                        MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
                        message.append("mismatched tree node: "+
                                                           mtne.node+
                                                           "; expecting type "+
                                                           tokenNames[mtne.expecting]);
                }
                else if ( e instanceof NoViableAltException ) {
                        NoViableAltException nvae = (NoViableAltException)e;
			message.append( "Unexpected token '" + e.token.getText() + "'" );
                        /*
                        message.append("decision=<<"+nvae.grammarDecisionDescription+">>"+
                                                           " state "+nvae.stateNumber+
                                                           " (decision="+nvae.decisionNumber+
                                                           ") no viable alt; token="+
                                                           e.token);
                                                           */
                }
                else if ( e instanceof EarlyExitException ) {
                        EarlyExitException eee = (EarlyExitException)e;
                        message.append("required (...)+ loop (decision="+
                                                           eee.decisionNumber+
                                                           ") did not match anything; token="+
                                                           e.token);
                }
                else if ( e instanceof MismatchedSetException ) {
                        MismatchedSetException mse = (MismatchedSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof MismatchedNotSetException ) {
                        MismatchedNotSetException mse = (MismatchedNotSetException)e;
                        message.append("mismatched token '"+
                                                           e.token+
                                                           "' expecting set "+mse.expecting);
                }
                else if ( e instanceof FailedPredicateException ) {
                        FailedPredicateException fpe = (FailedPredicateException)e;
                        message.append("rule "+fpe.ruleName+" failed predicate: {"+
                                                           fpe.predicateText+"}?");
		}
               	return message.toString();
        }   
} 

@lexer::header {
	package org.drools.rule.builder.dialect.java.parser;
}

@lexer::members {
	public static final CommonToken IGNORE_TOKEN = new CommonToken(null,0,99,0,0);
	protected boolean enumIsKeyword = true;
}
 

// starting point for parsing a java file
compilationUnit
	:	annotations?
		packageDeclaration?
        importDeclaration*
        typeDeclaration*
	;

packageDeclaration
	:	'package' qualifiedName ';'
	;
	
importDeclaration
	:	'import' 'static'? Identifier ('.' Identifier)* ('.' '*')? ';'
	;
	
typeDeclaration
	:	classOrInterfaceDeclaration
    |   ';'
	;
	
classOrInterfaceDeclaration
	:	modifier* (classDeclaration | interfaceDeclaration)
	;
	
classDeclaration
	:	normalClassDeclaration
    |   enumDeclaration
	;
	
normalClassDeclaration
	:	'class' Identifier (typeParameters)?
        ('extends' type)?
        ('implements' typeList)?
        classBody
	;
	
typeParameters
	:	'<' typeParameter (',' typeParameter)* '>'
	;

typeParameter
	:	Identifier ('extends' bound)?
	;
		
bound
	:	type ('&' type)*
	;

enumDeclaration
	:	ENUM Identifier ('implements' typeList)? enumBody
	;
	
enumBody
	:	'{' enumConstants? ','? enumBodyDeclarations? '}'
	;

enumConstants
	:	enumConstant (',' enumConstant)*
	;
	
enumConstant
	:	annotations? Identifier (arguments)? (classBody)?
	;
	
enumBodyDeclarations
	:	';' (classBodyDeclaration)*
	;
	
interfaceDeclaration
	:	normalInterfaceDeclaration
		| annotationTypeDeclaration
	;
	
normalInterfaceDeclaration
	:	'interface' Identifier typeParameters? ('extends' typeList)? interfaceBody
	;
	
typeList
	:	type (',' type)*
	;
	
classBody
	:	'{' classBodyDeclaration* '}'
	;
	
interfaceBody
	:	'{' interfaceBodyDeclaration* '}'
	;

classBodyDeclaration
	:	';'
	|	'static'? block
	|	modifier* memberDecl
	;
	
memberDecl
	:	genericMethodOrConstructorDecl
	|	methodDeclaration
	|	fieldDeclaration
	|	'void' Identifier voidMethodDeclaratorRest
	|	Identifier constructorDeclaratorRest
	|	interfaceDeclaration
	|	classDeclaration
	;
	
genericMethodOrConstructorDecl
	:	typeParameters genericMethodOrConstructorRest
	;
	
genericMethodOrConstructorRest
	:	(type | 'void') Identifier methodDeclaratorRest
	|	Identifier constructorDeclaratorRest
	;

methodDeclaration
	:	type Identifier methodDeclaratorRest
	;

fieldDeclaration
	:	type variableDeclarators ';'
	;
		
interfaceBodyDeclaration
	:	modifier* interfaceMemberDecl
	|   ';'
	;

interfaceMemberDecl
	:	interfaceMethodOrFieldDecl
	|   interfaceGenericMethodDecl
    |   'void' Identifier voidInterfaceMethodDeclaratorRest
    |   interfaceDeclaration
    |   classDeclaration
	;
	
interfaceMethodOrFieldDecl
	:	type Identifier interfaceMethodOrFieldRest
	;
	
interfaceMethodOrFieldRest
	:	constantDeclaratorsRest ';'
	|	interfaceMethodDeclaratorRest
	;
	
methodDeclaratorRest
	:	formalParameters ('[' ']')*
        ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
	;
	
voidMethodDeclaratorRest
	:	formalParameters ('throws' qualifiedNameList)?
        (   methodBody
        |   ';'
        )
	;
	
interfaceMethodDeclaratorRest
	:	formalParameters ('[' ']')* ('throws' qualifiedNameList)? ';'
	;
	
interfaceGenericMethodDecl
	:	typeParameters (type | 'void') Identifier
        interfaceMethodDeclaratorRest
	;
	
voidInterfaceMethodDeclaratorRest
	:	formalParameters ('throws' qualifiedNameList)? ';'
	;
	
constructorDeclaratorRest
	:	formalParameters ('throws' qualifiedNameList)? methodBody
	;

constantDeclarator
	:	Identifier constantDeclaratorRest
	;
	
variableDeclarators
	:	variableDeclarator (',' variableDeclarator)*
	;

variableDeclarator
	scope {
		JavaLocalDeclarationDescr.IdentifierDescr ident;
	}
	@init {
		if( this.localVariableLevel == 1 ) { // we only want top level local vars
			$variableDeclarator::ident = new JavaLocalDeclarationDescr.IdentifierDescr();
		}
	}
	@after {
	        if( this.localVariableLevel == 1 ) { // we only want top level local vars
	        	$localVariableDeclaration::descr.addIdentifier( $variableDeclarator::ident );
	        }
	}
	:	id=Identifier rest=variableDeclaratorRest 
		{ 
			if( this.localVariableLevel == 1 ) { // we only want top level local vars
				$variableDeclarator::ident.setIdentifier( $id.text );
				$variableDeclarator::ident.setStart( ((CommonToken)$id).getStartIndex() - 1 );
				if( $rest.stop != null ) {
   					$variableDeclarator::ident.setEnd( ((CommonToken)$rest.stop).getStopIndex() );
				}
			}
		}
	;
	
variableDeclaratorRest
	:	('[' ']')+ ('=' variableInitializer)?
	|	'=' variableInitializer
	|
	;
	
constantDeclaratorsRest
    :   constantDeclaratorRest (',' constantDeclarator)*
    ;

constantDeclaratorRest
	:	('[' ']')* '=' variableInitializer
	;
	
variableDeclaratorId
	:	Identifier ('[' ']')*
	;

variableInitializer
	:	arrayInitializer
    |   expression
	;
	
arrayInitializer
	:	'{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
	;

modifier
    :   annotation
    |   'public'
    |   'protected'
    |   'private'
    |   'static'
    |   'abstract'
    |   'final'
    |   'native'
    |   'synchronized'
    |   'transient'
    |   'volatile'
    |   'strictfp'
    ;

packageOrTypeName
	:	Identifier ('.' Identifier)*
	;

enumConstantName
    :   Identifier
    ;

typeName
	:   Identifier
    |   packageOrTypeName '.' Identifier
	;

type
	:	Identifier (typeArguments)? ('.' Identifier (typeArguments)? )* ('[' ']')*
	|	primitiveType ('[' ']')*
	;

primitiveType
    :   'boolean'
    |	'char'
    |	'byte'
    |	'short'
    |	'int'
    |	'long'
    |	'float'
    |	'double'
    ;

variableModifier
	:	'final'
    |   annotation
	;

typeArguments
	:	'<' typeArgument (',' typeArgument)* '>'
	;
	
typeArgument
	:	type
	|	'?' (('extends' | 'super') type)?
	;
	
qualifiedNameList
	:	qualifiedName (',' qualifiedName)*
	;
	
formalParameters
	:	'(' formalParameterDecls? ')'
	;
	
formalParameterDecls
	:	variableModifier* type formalParameterDeclsRest?
	;
	
formalParameterDeclsRest
	:	variableDeclaratorId (',' formalParameterDecls)?
	|   '...' variableDeclaratorId
	;
	
methodBody
	:	block
	;

qualifiedName
	:	Identifier ('.' Identifier)*
	;
	
literal	
	:   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   booleanLiteral
    |   'null'
	;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   'true'
    |   'false'
    ;

// ANNOTATIONS

annotations
	:	annotation+
	;

annotation
	:	'@' annotationName ('(' elementValuePairs? ')')?
	;
	
annotationName
	: Identifier ('.' Identifier)*
	;
	
elementValuePairs
	: elementValuePair (',' elementValuePair)*
	;
	
elementValuePair
	: (Identifier '=')? elementValue
	;
	
elementValue
	:	conditionalExpression
	|   annotation
	|   elementValueArrayInitializer
	;
	
elementValueArrayInitializer
	:	'{' (elementValue (',' elementValue )*)? '}'
	;
	
annotationTypeDeclaration
	:	'@' 'interface' Identifier annotationTypeBody
	;
	
annotationTypeBody
	:	'{' (annotationTypeElementDeclarations)? '}'
	;
	
annotationTypeElementDeclarations
	:	(annotationTypeElementDeclaration) (annotationTypeElementDeclaration)*
	;
	
annotationTypeElementDeclaration
	:	(modifier)* annotationTypeElementRest
	;
	
annotationTypeElementRest
	:	type annotationMethodOrConstantRest ';'
	|   classDeclaration ';'?
	|   interfaceDeclaration ';'?
	|   enumDeclaration ';'?
	|   annotationTypeDeclaration ';'?
	;
	
annotationMethodOrConstantRest
	:	annotationMethodRest
	|   annotationConstantRest
	;
	
annotationMethodRest
 	:	Identifier '(' ')' (defaultValue)?
 	;
 	
annotationConstantRest
 	:	variableDeclarators
 	;
 	
defaultValue
 	:	'default' elementValue
 	;

// STATEMENTS / BLOCKS

block
        @init {
            this.localVariableLevel++;
        }
        @after {
            this.localVariableLevel--;
        }
	:	'{' blockStatement* '}'
	;
	
blockStatement
	:	localVariableDeclaration
	|	classOrInterfaceDeclaration
    	|	statement
	;
	
localVariableDeclaration
scope   {
            JavaLocalDeclarationDescr descr;
        }
        @init {
            $localVariableDeclaration::descr = new JavaLocalDeclarationDescr();
        }
        @after {
            localDeclarations.add( $localVariableDeclaration::descr );
        }
	:	
	( variableModifier 
	    { 
	        $localVariableDeclaration::descr.updateStart( ((CommonToken)$variableModifier.start).getStartIndex() - 1 ); 
	        $localVariableDeclaration::descr.addModifier( $variableModifier.text ); 
	    } 
	)* 
	type 
	    { 
	        $localVariableDeclaration::descr.updateStart( ((CommonToken)$type.start).getStartIndex() - 1 ); 
	        $localVariableDeclaration::descr.setType( $type.text ); 
	        $localVariableDeclaration::descr.setEnd( ((CommonToken)$type.stop).getStopIndex() ); 
	    }
	variableDeclarators ';'
	;
	
statement
	: block
    | 'assert' expression (':' expression)? ';'
    | 'if' parExpression statement (options {k=1;}:'else' statement)?
    | 'for' '(' forControl ')' statement
    | 'while' parExpression statement
    | 'do' statement 'while' parExpression ';'
    | 'try' block
      (	catches 'finally' block
      | catches
      | 'finally' block
      )
    | 'switch' parExpression '{' switchBlockStatementGroups '}'
    | 'synchronized' parExpression block
    | 'return' expression? ';'
    | 'throw' expression ';'
    | 'break' Identifier? ';'
    | 'continue' Identifier? ';'
    // adding support to drools modify block
    | modifyStatement
    | ';'
    | statementExpression ';'
    | Identifier ':' statement
	;
	
modifyStatement
	@init {
	    JavaModifyBlockDescr d = null;
	}
	: s='modify' parExpression 
	{
	    d = new JavaModifyBlockDescr( $parExpression.text );
	    d.setStart( ((CommonToken)$s).getStartIndex() );
	    this.blocks.add( d );
	    
	}
	'{' ( e = expression { d.getExpressions().add( $e.text ); }
	       (',' e=expression { d.getExpressions().add( $e.text ); } )*
	    )? 
	c='}' 
        {
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
        }
	;	
	
epStatement
	@init {
	    JavaInterfacePointsDescr d = null;
	}
        : 
        ( s='exitPoints' '[' id=StringLiteral c=']' 
        {
	    d = new JavaInterfacePointsDescr( $id.text );
	    d.setType( JavaBlockDescr.BlockType.EXIT );
	    d.setStart( ((CommonToken)$s).getStartIndex() );
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
	    this.blocks.add( d );
        }
        |  s='entryPoints' '[' id=StringLiteral c=']' 
        {
	    d = new JavaInterfacePointsDescr( $id.text );
	    d.setType( JavaBlockDescr.BlockType.ENTRY );
	    d.setStart( ((CommonToken)$s).getStartIndex() );
            d.setEnd( ((CommonToken)$c).getStopIndex() ); 
	    this.blocks.add( d );
        }
        ) 
        ;	
	
catches
	:	catchClause (catchClause)*
	;
	
catchClause
	:	'catch' '(' formalParameter ')' block
	;

formalParameter
	:	variableModifier* type variableDeclaratorId
	;
		
switchBlockStatementGroups
	:	(switchBlockStatementGroup)*
	;
	
switchBlockStatementGroup
	:	switchLabel blockStatement*
	;
	
switchLabel
	:	'case' constantExpression ':'
	|   'case' enumConstantName ':'
	|   'default' ':'
	;
	
moreStatementExpressions
	:	(',' statementExpression)*
	;

forControl
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
	:	forVarControl
	|	forInit? ';' expression? ';' forUpdate?
	;

forInit
        @init {
            this.localVariableLevel++;
        }
        @after {
            this.localVariableLevel--;
        }
	:	variableModifier* type variableDeclarators
	|	expressionList
	;
	
forVarControl
	:	variableModifier* type Identifier ':' expression
	;

forUpdate
	:	expressionList
	;

// EXPRESSIONS

parExpression
	:	'(' expression ')'
	;
	
expressionList
    :   expression (',' expression)*
    ;

statementExpression
	:	expression
	;
	
constantExpression
	:	expression
	;
	
expression
	:	conditionalExpression (assignmentOperator expression)?
	;
	
assignmentOperator
	:	'='
    |   '+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |   '<' '<' '='
    |   '>' '>' '='
    |   '>' '>' '>' '='
	;

conditionalExpression
    :   conditionalOrExpression ( '?' expression ':' expression )?
	;

conditionalOrExpression
    :   conditionalAndExpression ( '||' conditionalAndExpression )*
	;

conditionalAndExpression
    :   inclusiveOrExpression ( '&&' inclusiveOrExpression )*
	;

inclusiveOrExpression
    :   exclusiveOrExpression ( '|' exclusiveOrExpression )*
	;

exclusiveOrExpression
    :   andExpression ( '^' andExpression )*
	;

andExpression
    :   equalityExpression ( '&' equalityExpression )*
	;

equalityExpression
    :   instanceOfExpression ( ('==' | '!=') instanceOfExpression )*
	;

instanceOfExpression
    :   relationalExpression ('instanceof' type)?
	;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
	;
	
relationalOp
	:	('<' '=' | '>' '=' | '<' | '>')
	;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
	;

        // TODO: need a sem pred to check column on these >>>
shiftOp
	:	('<' '<' | '>' '>' '>' | '>' '>')
	;


additiveExpression
    :   multiplicativeExpression ( ('+' | '-') multiplicativeExpression )*
	;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
	;
	
unaryExpression
    :   '+' unaryExpression
    |	'-' unaryExpression
    |   '++' primary
    |   '--' primary
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    | 	'!' unaryExpression
    |   castExpression
    |   primary selector* ('++'|'--')?
    ;

castExpression
    :  '(' primitiveType ')' unaryExpression
    |  '(' (type | expression) ')' unaryExpressionNotPlusMinus
    ;

primary
    :	parExpression
    |   nonWildcardTypeArguments
        (explicitGenericInvocationSuffix | 'this' arguments)
    |   'this' ('.' Identifier)* (identifierSuffix)?
    |   'super' superSuffix
    |   epStatement ('.' Identifier)* (identifierSuffix)?
    |   literal
    |   'new' creator
    |   i=Identifier { if( ! "(".equals( input.LT(1) == null ? "" : input.LT(1).getText() ) ) identifiers.add( $i.text );  } ('.' Identifier)* (identifierSuffix)?
    |   primitiveType ('[' ']')* '.' 'class'
    |   'void' '.' 'class'
	;

identifierSuffix
	:	('[' ']')+ '.' 'class'
	|	('[' expression ']')+ // can also be matched by selector, but do here
    |   arguments
    |   '.' 'class'
    |   '.' explicitGenericInvocation
    |   '.' 'this'
    |   '.' 'super' arguments
    |   '.' 'new' (nonWildcardTypeArguments)? innerCreator
	;
	
creator
	:	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRest)
	;

createdName
	:	Identifier typeArguments?
        ('.' Identifier typeArguments?)*
    |	primitiveType
	;
	
innerCreator
	:	Identifier classCreatorRest
	;

arrayCreatorRest
	:	'['
        (   ']' ('[' ']')* arrayInitializer
        |   expression ']' ('[' expression ']')* ('[' ']')*
        )
	;

classCreatorRest
	:	arguments classBody?
	;
	
explicitGenericInvocation
	:	nonWildcardTypeArguments explicitGenericInvocationSuffix
	;
	
nonWildcardTypeArguments
	:	'<' typeList '>'
	;
	
explicitGenericInvocationSuffix
	:	'super' superSuffix
	|   Identifier arguments
	;
	
selector
	:	'.' Identifier (arguments)?
	|   '.' 'this'
	|   '.' 'super' superSuffix
	|   '.' 'new' (nonWildcardTypeArguments)? innerCreator
	|   '[' expression ']'
	;
	
superSuffix
	:	arguments
	|   '.' Identifier (arguments)?
    ;

arguments
	:	'(' expressionList? ')'
	;

// LEXER

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ Exponent? FloatTypeSuffix
	;

fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

ENUM:	'enum' {if ( !enumIsKeyword ) $type=Identifier;}
	;
	
Identifier 
    :   Letter (Letter|JavaIDDigit)*
    ;

/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049' 
    |   '\uff10'..'\uff19'
   ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
