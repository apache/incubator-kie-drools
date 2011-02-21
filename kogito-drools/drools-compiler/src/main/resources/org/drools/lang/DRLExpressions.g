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
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.CheckedDroolsException;
}

@members {
    private ParserHelper helper;
                                                    
    public DRLExpressions(TokenStream input,
                          RecognizerSharedState state,
                          ParserHelper helper ) {
        this( input,
              state );
        this.helper = helper;
    }

    public ParserHelper getHelper()                           { return helper; }
    public boolean hasErrors()                                { return helper.hasErrors(); }
    public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
    public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
    public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
    public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
    public LinkedList<DroolsSentence> getEditorInterface()    { return helper.getEditorInterface(); }
    public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
    public void emitErrorMessage(String msg)                  {}
    
    private boolean buildConstraint;
    public void setBuildConstraint( boolean build ) { this.buildConstraint = build; }
    public boolean isBuildConstraint() { return this.buildConstraint; }

}

// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
catch (RecognitionException re) {
    throw re;
}
}

// --------------------------------------------------------
//                      GENERAL RULES
// --------------------------------------------------------
literal
    :	STRING      {	helper.emit($STRING, DroolsEditorType.STRING_CONST);	}
    |	DECIMAL 		{	helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST);	}
    |	HEX     		{	helper.emit($HEX, DroolsEditorType.NUMERIC_CONST);	}
    |	FLOAT   		{	helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
    |	BOOL        {	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
    |	NULL        {	helper.emit($NULL, DroolsEditorType.NULL_CONST);	}
    ;

typeList
    :	type (COMMA type)*
    ;

type
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
// the following dymmy rule is to force the AT symbol to be
// included in the follow set of the expression on the DFAs
dummy
    :	expression ( AT | SEMICOLON )
    ;

// top level entry point for arbitrary expression parsing
expression
    :	conditionalExpression ((assignmentOperator) => assignmentOperator expression)?
    ;

conditionalExpression
  : conditionalOrExpression ( QUESTION expression COLON expression )?
    ;

conditionalOrExpression
  : conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
    ;

conditionalAndExpression 
  : inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
    ;

inclusiveOrExpression
  : exclusiveOrExpression ( PIPE exclusiveOrExpression )*
    ;

exclusiveOrExpression
  : andExpression ( XOR andExpression )*
    ;

andExpression 
  : andOrRestriction ( AMPER andOrRestriction )*
    ;
    
andOrRestriction
  	: equalityExpression ( ((DOUBLE_PIPE|DOUBLE_AMPER) operator)=>(DOUBLE_PIPE|DOUBLE_AMPER) operator shiftExpression )*	
  	;    

equalityExpression 
  : instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
    ;

instanceOfExpression 
  : inExpression (instanceof_key type)?
    ;

inExpression
  : relationalExpression (not_key? in_key LEFT_PAREN expression (COMMA expression)* RIGHT_PAREN )?
  ;

relationalExpression
  : shiftExpression ( (relationalOp)=> relationalOp shiftExpression )*
  ;

operator
  : ( EQUALS
    | NOT_EQUALS
    | relationalOp
    )
    ;

relationalOp
  : ( LESS_EQUALS
    | GREATER_EQUALS 
    | LESS 
    | GREATER
    | not_key neg_operator_key ((squareArguments)=> squareArguments)?
    | operator_key  ((squareArguments)=> squareArguments)?
    )
    ;

shiftExpression
  : additiveExpression ( (shiftOp)=>shiftOp additiveExpression )*
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
    :   TILDE unaryExpression
    | 	NEGATION unaryExpression
    |   (castExpression)=>castExpression
    |   primary ((selector)=>selector)* ((INCR|DECR)=> (INCR|DECR))?
    ;
    
castExpression
    :  (LEFT_PAREN primitiveType) => LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
    |  (LEFT_PAREN type) => LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
    ;
    
primitiveType
    : boolean_key
    |	char_key
    |	byte_key
    |	short_key
    |	int_key
    |	long_key
    |	float_key
    |	double_key
    ;

primary
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
    :	(LEFT_SQUARE RIGHT_SQUARE)=>(LEFT_SQUARE RIGHT_SQUARE)+ DOT class_key
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
    :   (DOT super_key)=>DOT super_key superSuffix
    |   (DOT new_key)=>DOT new_key (nonWildcardTypeArguments)? innerCreator
    |   (DOT ID)=>DOT ID ((LEFT_PAREN) => arguments)?
    //|   DOT this_key
    |   (LEFT_SQUARE)=>LEFT_SQUARE expression RIGHT_SQUARE
    ;

superSuffix
    :	arguments
    |   	DOT ID ((LEFT_PAREN) => arguments)?
        ;

squareArguments
    : LEFT_SQUARE expressionList? RIGHT_SQUARE
    ;

arguments
    :	LEFT_PAREN expressionList? RIGHT_PAREN
    ;

expressionList
  :   expression (COMMA expression)*
  ;

assignmentOperator
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

void_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))}?=> id=ID
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

not_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))}?=> id=ID
    ;

in_key
  :      {(helper.validateIdentifierKey(DroolsSoftKeywords.IN))}?=> id=ID
  ;

operator_key
  :      {(helper.isPluggableEvaluator(false))}?=> id=ID
  ;

neg_operator_key
  :      {(helper.isPluggableEvaluator(true))}?=> id=ID 
  ;


