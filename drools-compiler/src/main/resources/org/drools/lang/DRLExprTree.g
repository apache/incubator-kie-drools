tree grammar DRLExprTree;

options { 
    language = Java;
    tokenVocab = DRLLexer;
    ASTLabelType=CommonTree;
}
  
@header {
    package org.drools.lang;

    import java.util.LinkedList;
    import org.drools.compiler.DroolsParserException;
    import org.drools.lang.ParserHelper;
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.CheckedDroolsException;
    import org.drools.lang.descr.*;
}

@members {
    private ParserHelper helper;
                                             
    public void setHelper( ParserHelper helper )              { this.helper = helper; }       
    public ParserHelper getHelper()                           { return helper; }
    public boolean hasErrors()                                { return helper.hasErrors(); }
    public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
    public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
    public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
    public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
    public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
    public void emitErrorMessage(String msg)                  {}
}

// --------------------------------------------------------
//                      EXPRESSIONS
// --------------------------------------------------------
constraint returns [ConstraintConnectiveDescr root]
@init { $root = ConstraintConnectiveDescr.newAnd(); }
@after { $root.addOrMerge( $ex.result ); }
    :  ex=expression  
    ;

expression returns [BaseDescr result]
@init { BaseDescr descr = null; }
@after { $result = descr; }
    :   ^(DOUBLE_PIPE p1=expression p2=expression )  
       { descr = ConstraintConnectiveDescr.newOr(); 
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p1.result );  
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p2.result ); }
    |   ^(DOUBLE_AMPER p1=expression p2=expression )
       { descr = ConstraintConnectiveDescr.newAnd(); 
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p1.result );  
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p2.result ); }
    |   ^(PIPE p1=expression p2=expression )
       { descr = ConstraintConnectiveDescr.newIncOr(); 
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p1.result );  
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p2.result ); }
    |   ^(XOR p1=expression p2=expression )
       { descr = ConstraintConnectiveDescr.newXor(); 
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p1.result );  
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p2.result ); }
    |   ^(AMPER p1=expression p2=expression )
       { descr = ConstraintConnectiveDescr.newIncAnd(); 
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p1.result );  
         ((ConstraintConnectiveDescr)descr).addOrMerge( $p2.result ); }
    |   ^(ro=relationalOp p1=expression p2=expression )
       { descr = new RelationalExprDescr( $ro.text, $p1.result, $p2.result ); }
    |   ^(no=NEG_OPERATOR p1=expression p2=expression )
       { descr = new RelationalExprDescr( "not "+$no.text, $p1.result, $p2.result ); }
    | 	^(ao=assignmentOperator p1=expression p2=expression)    
       { descr = new RelationalExprDescr( $ao.text, $p1.result, $p2.result ); }
    |   ^(PAR_EXPRESSION expression)
    |   ^(mathOp expression expression? )
    |   ^(unaryOp expression)
    |   ^(QUESTION expression expression expression )
    |   ^(CAST TYPE expression)
    |   pr=primary
       { descr = new AtomicExprDescr( $pr.text ); }
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
    |   SHL_ASSIGN
    |   SHRB_ASSIGN
    |   SHR_ASSIGN
    ;
    
relationalOp
    :   EQUALS
    |   NOT_EQUALS
    |   GREATER_EQUALS
    |   LESS_EQUALS
    |   LESS
    |   GREATER
    |   OPERATOR
    ;    

literal
    :	STRING     
    |	DECIMAL 
    |	HEX     
    |	FLOAT   
    |	BOOL    
    |	NULL    
    ;

mathOp
    :	SHL
    |   SHRB
    |   SHR
    |   PLUS
    |   MINUS
    |   STAR
    |   DIV
    |   MOD
    ;
    
unaryOp
    :   INCR
    |   DECR
    |   TILDE
    |   NEGATION
    ;

primaryParent
    :   ^(primary selector* (INCR|DECR)?)
    |   ^(primary nonWildcardTypeArguments (explicitGenericInvocationSuffix | this_key arguments) )
    ;
    
primary
    :	^(PRIMARY literal)
    |   ^(PRIMARY super_key superSuffix)
    |   ^(PRIMARY new_key creator)
    |   ^(PRIMARY TYPE (LEFT_SQUARE RIGHT_SQUARE)* DOT class_key)
    |   ^(PRIMARY inlineMapExpression)
    |   ^(PRIMARY inlineListExpression)
    |   ^(PRIMARY ID (DOT ID)* identifierSuffix?)
    ;

inlineListExpression
    :   ^(LIST expressionList?)	
    ;
    
inlineMapExpression
    :	^(MAP mapExpressionList)
    ;

mapExpressionList
    :	mapEntry+
    ;
    
mapEntry
    :	^(MAP_ENTRY expression expression)
    ;

identifierSuffix
    :	(LEFT_SQUARE RIGHT_SQUARE)=>(LEFT_SQUARE RIGHT_SQUARE)+ DOT class_key
    |	((LEFT_SQUARE) => LEFT_SQUARE expression RIGHT_SQUARE)+ // can also be matched by selector, but do here
    |   arguments 
    ;

creator
    :	nonWildcardTypeArguments? createdName
        (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :	ID typeArguments?
        ( DOT ID typeArguments?)*
        | TYPE
    ;

typeArguments
    :	LESS typeArgument (COMMA typeArgument)* GREATER
    ;

typeArgument
    :	TYPE
    |	QUESTION ((extends_key | super_key) TYPE)?
    ;

innerCreator
    :	ID classCreatorRest
    ;

arrayCreatorRest
    :   LEFT_SQUARE
    (   RIGHT_SQUARE (LEFT_SQUARE RIGHT_SQUARE)* arrayInitializer
        |   expression RIGHT_SQUARE (LEFT_SQUARE expression RIGHT_SQUARE)* (LEFT_SQUARE RIGHT_SQUARE)*
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

typeList
    :	TYPE (COMMA TYPE)* 
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

// --------------------------------------------------------
//                      KEYWORDS
// --------------------------------------------------------
extends_key
    :      ID
    ;

super_key
    :      ID
    ;

instanceof_key
    :      OPERATOR
    ;

this_key
    :      ID
    ;

class_key
    :      ID
    ;

new_key
    :      ID
    ;

not_key
    :      ID
    ;

in_key
  :      OPERATOR
  ;

operator_key
  :      OPERATOR
  ;

neg_operator_key
  :      NEG_OPERATOR
  ;



