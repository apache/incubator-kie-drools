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

    import org.drools.lang.descr.AtomicExprDescr;
    import org.drools.lang.descr.BaseDescr;
    import org.drools.lang.descr.ConstraintConnectiveDescr;
    import org.drools.lang.descr.RelationalExprDescr;
    
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
    
    private boolean buildDescr;
    public void setBuildDescr( boolean build ) { this.buildDescr = build; }
    public boolean isBuildDescr() { return this.buildDescr; }
    
    public void setLeftMostExpr( String value ) { helper.setLeftMostExpr( value ); }
    public String getLeftMostExpr() { return helper.getLeftMostExpr(); }
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
    |	DECIMAL     {	helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST);	}
    |	HEX         {	helper.emit($HEX, DroolsEditorType.NUMERIC_CONST);	}
    |	FLOAT       {	helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
    |	BOOL        {	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
    |	NULL        {	helper.emit($NULL, DroolsEditorType.NULL_CONST);	}
    ;

typeList
    :	type (COMMA type)*
    ;

type
    : 	tm=typeMatch 
    ;
    
typeMatch
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
    :	expression ( AT | SEMICOLON | EOF | ID ) ;
    
dummy2
    :  relationalExpression EOF;
    
// top level entry point for arbitrary expression parsing
expression returns [BaseDescr result]
    :	left=conditionalExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
        ((assignmentOperator) => op=assignmentOperator left=expression)?
    ;

conditionalExpression returns [BaseDescr result]
    :   left=conditionalOrExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
        ( QUESTION ts=expression COLON fs=expression )? 
    ;

conditionalOrExpression returns [BaseDescr result]
  : left=conditionalAndExpression  { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( DOUBLE_PIPE right=conditionalAndExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )* 
  ;

conditionalAndExpression returns [BaseDescr result]
  : left=inclusiveOrExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( DOUBLE_AMPER right=inclusiveOrExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;

inclusiveOrExpression returns [BaseDescr result]
  : left=exclusiveOrExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( PIPE right=exclusiveOrExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncOr(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;

exclusiveOrExpression returns [BaseDescr result]
  : left=andExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( XOR right=andExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newXor(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;

andExpression returns [BaseDescr result]
  : left=andOrRestriction { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( AMPER right=andOrRestriction 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncAnd(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;
    
andOrRestriction returns [BaseDescr result]
  : left=equalityExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( ((DOUBLE_PIPE|DOUBLE_AMPER) operator)=>(lop=DOUBLE_PIPE|lop=DOUBLE_AMPER) op=operator right=shiftExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               ConstraintConnectiveDescr descr = $lop.text.equals("||") ? ConstraintConnectiveDescr.newOr() : ConstraintConnectiveDescr.newAnd(); 
               descr.addOrMerge( $result );  
               
               RelationalExprDescr re = new RelationalExprDescr( $op.text, new AtomicExprDescr( $left.text ), new AtomicExprDescr( $right.text ) );
               descr.addOrMerge( re ); 
               $result = descr;
           }
         }
  )* EOF?
  ;    

equalityExpression returns [BaseDescr result]
  : left=instanceOfExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  ( ( op=EQUALS | op=NOT_EQUALS ) right=instanceOfExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               $result = new RelationalExprDescr( $op.text, $left.result, $right.result );
           }
         }
  )*
  ;

instanceOfExpression returns [BaseDescr result]
  : left=inExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
  (op=instanceof_key right=type
         { if( buildDescr && state.backtracking == 0 ) {
               $result = new RelationalExprDescr( $op.text, $left.result, new AtomicExprDescr($right.text) );
           }
         }
  )?
  ;

inExpression returns [BaseDescr result]
  : left=relationalExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
    ( not_key in=in_key LEFT_PAREN expression (COMMA expression)* RIGHT_PAREN
    | in=in_key LEFT_PAREN expression (COMMA expression)* RIGHT_PAREN 
    )?
  ;

relationalExpression returns [BaseDescr result]
  : left=shiftExpression { if( buildDescr && state.backtracking == 0 ) { $result = ( $left.result != null ) ? $left.result : new AtomicExprDescr( $left.text ) ; } }
  ( (relationalOp)=> op=relationalOp right=shiftExpression 
         { if( buildDescr && state.backtracking == 0 ) {
               $result = new RelationalExprDescr( $op.text, $result,  ( $right.result != null ) ? $right.result : new AtomicExprDescr( $right.text ) );
           }
         }
  )*
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
    
shiftExpression returns [BaseDescr result]
  : left=additiveExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
    ( (shiftOp)=>shiftOp additiveExpression )*
  ;

shiftOp
    :	( LESS LESS 
        | GREATER GREATER GREATER 
        | GREATER GREATER  )
    ;

additiveExpression returns [BaseDescr result]
    :   left=multiplicativeExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
        ( (PLUS|MINUS)=> (PLUS | MINUS) multiplicativeExpression )*
    ;

multiplicativeExpression returns [BaseDescr result]
    :   left=unaryExpression { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
      ( ( STAR | DIV | MOD ) unaryExpression )*
    ;

unaryExpression returns [BaseDescr result]
    :   PLUS unaryExpression
    |	MINUS unaryExpression
    |   INCR primary
    |   DECR primary
    |   left=unaryExpressionNotPlusMinus { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
    ;

unaryExpressionNotPlusMinus returns [BaseDescr result]
    :   TILDE unaryExpression
    | 	NEGATION unaryExpression
    |   (castExpression)=>castExpression
    |   left=primary { if( buildDescr && state.backtracking == 0 ) { $result = $left.result; } }
        ((selector)=>selector)* { if( buildDescr && state.backtracking == 0 && helper.getLeftMostExpr() == null ) { helper.setLeftMostExpr( $unaryExpressionNotPlusMinus.text ); } }
        ((INCR|DECR)=> (INCR|DECR))? 
    ;
    
castExpression
    :  (LEFT_PAREN primitiveType) => LEFT_PAREN primitiveType RIGHT_PAREN expr=unaryExpression 
    |  (LEFT_PAREN type) => LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
    ;
    
primitiveType
    :   boolean_key
    |	char_key
    |	byte_key
    |	short_key
    |	int_key
    |	long_key
    |	float_key
    |	double_key
    ;

primary returns [BaseDescr result]
    :	(parExpression)=> expr=parExpression {  if( buildDescr && state.backtracking == 0 ) { $result = $expr.result; }  }
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
    :	LEFT_SQUARE mapExpressionList RIGHT_SQUARE 
    ;

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;
    
mapEntry
    :	expression COLON expression 
    ;

parExpression returns [BaseDescr result]
    :	LEFT_PAREN expr=expression RIGHT_PAREN 
        {  if( buildDescr && state.backtracking == 0 ) { $result = $expr.result; }  }
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


