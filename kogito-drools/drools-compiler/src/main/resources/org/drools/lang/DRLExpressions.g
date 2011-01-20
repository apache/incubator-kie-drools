parser grammar DRLExpressions;
              
options { 
	language = Java;
	tokenVocab = DRLLexer;
}
  
@header {
	package org.drools.lang;
	
	import java.util.LinkedList;
	import org.drools.compiler.DroolsParserException;
	import org.drools.lang.ParserHelper;
}

@members {
    private ParserXHelper helper = null;
                                                    
    public DRLExpressions(TokenStream input,
                          RecognizerSharedState state,
                          ParserXHelper helper ) {
        this( input,
              state );
        this.helper = helper;
    }

    public ParserXHelper getHelper()                          { return helper; }
    public boolean hasErrors()                                { return helper.hasErrors(); }
    public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
    public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
    public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
    public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
    public LinkedList<DroolsSentence> getEditorInterface()    { return helper.getEditorInterface(); }
    public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
    public void emitErrorMessage(String msg)                  {}

}

// --------------------------------------------------------
//                      GENERAL RULES
// --------------------------------------------------------
literal
	:	STRING                	{	helper.emit($STRING, DroolsEditorType.STRING_CONST);	}
	|	DECIMAL 		{	helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST);	}
	|	HEX     		{	helper.emit($HEX, DroolsEditorType.NUMERIC_CONST);	}
	|	FLOAT   		{	helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
	|	BOOL                  	{	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
	|	NULL                  	{	helper.emit($NULL, DroolsEditorType.NULL_CONST);	}
	;

typeList
	:	type (COMMA type)*
	;
	
type
options { backtrack=true; memoize=true; }
	: 	(primitiveType) => ( primitiveType ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)* )
	|	( ID ((typeArguments)=>typeArguments)? (DOT ID ((typeArguments)=>typeArguments)? )* ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)* )
	;

typeArguments
	:	LESS typeArgument (COMMA typeArgument)* GREATER
	;
	
typeArgument
	:	type
	|	QUESTION ((extends_key | super_key) type)?
	;

// --------------------------------------------------------
//                      EXPRESSIONS
// --------------------------------------------------------
expression
options { backtrack=true; memoize=true; }
	:	conditionalExpression ((assignmentOperator) => assignmentOperator expression)?
	;

conditionalExpression
        :       conditionalOrExpression ( QUESTION expression COLON expression )?
	;
conditionalOrExpression
    :   conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
	;

conditionalAndExpression
    :   inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
	;

inclusiveOrExpression
    :   exclusiveOrExpression ( PIPE exclusiveOrExpression )*
	;

exclusiveOrExpression
    :   andExpression ( XOR andExpression )*
	;

andExpression
    :   equalityExpression ( AMPER equalityExpression )*
	;

equalityExpression
    :   instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
	;

instanceOfExpression
    :   relationalExpression (instanceof_key type)?
	;

relationalExpression
    :   shiftExpression ( (LESS)=> relationalOp shiftExpression )*
    ;
	
relationalOp
	:	(LESS_EQUALS| GREATER_EQUALS | LESS | GREATER)
	;

shiftExpression
    :   additiveExpression ( (shiftOp)=>shiftOp additiveExpression )*
	;

shiftOp
	:	(LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
	;

additiveExpression
    :   multiplicativeExpression ( (PLUS|MINUS)=> (PLUS | MINUS) multiplicativeExpression )*
	;

multiplicativeExpression
    :   unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
	;
	
unaryExpression
    :   PLUS unaryExpression
    |	MINUS unaryExpression
    |   INCR primary
    |   DECR primary
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
options{ backtrack=true; memoize=true; }
    :   TILDE unaryExpression
    | 	NEGATION unaryExpression
    |   castExpression
    |   primary ((selector)=>selector)* ((INCR|DECR)=> (INCR|DECR))?
    ;
    
castExpression
options { backtrack=true; memoize=true; }
    :  (LEFT_PAREN primitiveType) => LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
    |  (LEFT_PAREN type) => LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
    |  LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus
    ;
    
primitiveType
options { backtrack=true; memoize=true; }
    :   boolean_key
    |	char_key
    |	byte_key
    |	short_key
    |	int_key
    |	long_key
    |	float_key
    |	double_key
    ;

primary
//options{ backtrack=true; memoize=true; }
    :	(parExpression)=> parExpression
    |   (nonWildcardTypeArguments)=> nonWildcardTypeArguments (explicitGenericInvocationSuffix | this_key arguments)
    |   (literal)=> literal
    //|   this_key ({!helper.validateSpecialID(2)}?=> DOT ID)* ({helper.validateIdentifierSufix()}?=> identifierSuffix)?
    |   (super_key)=> super_key superSuffix
    |   (new_key)=> new_key creator
    |   (primitiveType)=> primitiveType (LEFT_SQUARE RIGHT_SQUARE)* DOT class_key
    //|   void_key DOT class_key
    |   (inlineMapExpression)=> inlineMapExpression
    |   (inlineListExpression)=> inlineListExpression
    |   (ID)=>ID ((DOT ID)=>DOT ID)* ((identifierSuffix)=>identifierSuffix)?
    ;

inlineListExpression
    :   LEFT_SQUARE expressionList? RIGHT_SQUARE	
    ;
    
inlineMapExpression
    :	LEFT_SQUARE mapExpressionList+ RIGHT_SQUARE
    ;

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;
    
mapEntry
    :	expression COLON expression
    ;

parExpression
	:	LEFT_PAREN expression RIGHT_PAREN
	;
	
identifierSuffix
options { backtrack=true; memoize=true; }
    :	(LEFT_SQUARE RIGHT_SQUARE)+ DOT class_key
    |	((LEFT_SQUARE) => LEFT_SQUARE expression RIGHT_SQUARE)+ // can also be matched by selector, but do here
    |   arguments 
//    |   DOT class_key
//    |   DOT explicitGenericInvocation
//    |   DOT this_key
//    |   DOT super_key arguments
//    |   DOT new_key (nonWildcardTypeArguments)? innerCreator
	;
	
creator
	:	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRest)
	;

createdName
	:	ID typeArguments?
        ( DOT ID typeArguments?)*
        |	primitiveType
	;
	
innerCreator
	:	{!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=> ID classCreatorRest
	;

arrayCreatorRest
	:   LEFT_SQUARE 
	(   RIGHT_SQUARE (LEFT_SQUARE RIGHT_SQUARE)* arrayInitializer
        |   expression RIGHT_SQUARE ({!helper.validateLT(2,"]")}?=>LEFT_SQUARE expression RIGHT_SQUARE)* ((LEFT_SQUARE RIGHT_SQUARE)=> LEFT_SQUARE RIGHT_SQUARE)*
        )
	;

variableInitializer
	:	arrayInitializer
    	|   expression
	;
	
arrayInitializer
	:	LEFT_CURLY (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RIGHT_CURLY
	;

classCreatorRest
	:	arguments //classBody?		//sotty:  restored classBody to allow for inline, anonymous classes
	;
	
explicitGenericInvocation
	:	nonWildcardTypeArguments arguments
	;
	
nonWildcardTypeArguments
	:	LESS typeList GREATER
	;
	
explicitGenericInvocationSuffix
	:	super_key superSuffix
	|   	ID arguments
	;

selector
options { backtrack=true; memoize=true; }
	:   DOT ID ((LEFT_PAREN) => arguments)?
	//|   DOT this_key
	|   DOT super_key superSuffix
	|   DOT new_key (nonWildcardTypeArguments)? innerCreator
	|   LEFT_SQUARE expression RIGHT_SQUARE
	;
	
superSuffix
	:	arguments
	|   	DOT ID ((LEFT_PAREN) => arguments)?
        ;

arguments
options { backtrack=true; memoize=true; }
	:	LEFT_PAREN expressionList? RIGHT_PAREN
	;

expressionList
    :   expression (COMMA expression)*
    ;

assignmentOperator
options { k=1; }
	:   EQUALS_ASSIGN
        |   PLUS_ASSIGN
        |   MINUS_ASSIGN
        |   MULT_ASSIGN
        |   DIV_ASSIGN
        |   AND_ASSIGN
        |   OR_ASSIGN
        |   XOR_ASSIGN
        |   MOD_ASSIGN
        |   LESS LESS EQUALS_ASSIGN
        |   (GREATER GREATER GREATER)=> GREATER GREATER GREATER EQUALS_ASSIGN
        |   (GREATER GREATER)=> GREATER GREATER EQUALS_ASSIGN
	;

// --------------------------------------------------------
//                      (JAVA) ANNOTATIONS
// --------------------------------------------------------

annotations
	:	annotation+
	;

annotation
	:	AT {	helper.emit($AT, DroolsEditorType.SYMBOL);	}
		ann=annotationName 
			(
				LEFT_PAREN RIGHT_PAREN
				| LEFT_PAREN elementValuePairs RIGHT_PAREN
				| 
			)
	;

	
annotationName returns [String name]
@init{ $name=""; }
	: id=ID 	{	$name += $id.text; helper.emit($id, DroolsEditorType.IDENTIFIER);	}
		(DOT mid=ID { $name += $mid.text; } )*
	;
	
elementValuePairs
	: elementValuePair (COMMA elementValuePair)*
	;
	
elementValuePair
	: (ID EQUALS_ASSIGN)=> key=ID EQUALS_ASSIGN val=elementValue 
	| value=elementValue 
	;
	
elementValue
	:	TimePeriod
	|	conditionalExpression
	|   annotation
	|   elementValueArrayInitializer
	;

elementValueArrayInitializer
	:	LEFT_CURLY (elementValue (COMMA elementValue )*)? RIGHT_CURLY
	;

// --------------------------------------------------------
//                      KEYWORDS
// --------------------------------------------------------
extends_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))}?=> id=ID
	;

super_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))}?=> id=ID
	;

instanceof_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=> id=ID
	;

boolean_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=> id=ID
	;

char_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))}?=> id=ID
	;

byte_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))}?=> id=ID
	;

short_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))}?=> id=ID
	;

int_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.INT))}?=> id=ID
	;

float_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))}?=> id=ID
	;

long_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))}?=> id=ID
	;

double_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))}?=> id=ID
	;

this_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))}?=> id=ID
	;

class_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))}?=> id=ID
	;

new_key
	:      {(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))}?=> id=ID
	;

