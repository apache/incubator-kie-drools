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
    import org.drools.lang.Location;
    import org.drools.CheckedDroolsException;

    import org.drools.lang.descr.AtomicExprDescr;
    import org.drools.lang.descr.BaseDescr;
    import org.drools.lang.descr.ConstraintConnectiveDescr;
    import org.drools.lang.descr.RelationalExprDescr;
    import org.drools.lang.descr.BindingDescr;
    
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
    private int inMap = 0;
    private int ternOp = 0;
    private boolean hasBindings;
    public void setBuildDescr( boolean build ) { this.buildDescr = build; }
    public boolean isBuildDescr() { return this.buildDescr; }
    
    public void setLeftMostExpr( String value ) { helper.setLeftMostExpr( value ); }
    public String getLeftMostExpr() { return helper.getLeftMostExpr(); }
    
    public void setHasBindings( boolean value ) { this.hasBindings = value; }
    public boolean hasBindings() { return this.hasBindings; }
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
    :	STRING        {	helper.emit($STRING, DroolsEditorType.STRING_CONST);	}
    |	DECIMAL       {	helper.emit($DECIMAL, DroolsEditorType.NUMERIC_CONST);	}
    |	HEX           {	helper.emit($HEX, DroolsEditorType.NUMERIC_CONST);	}
    |	FLOAT         {	helper.emit($FLOAT, DroolsEditorType.NUMERIC_CONST);	}
    |	BOOL          {	helper.emit($BOOL, DroolsEditorType.BOOLEAN_CONST);	}
    |	NULL          {	helper.emit($NULL, DroolsEditorType.NULL_CONST);	}
    |   TIME_INTERVAL {	helper.emit($TIME_INTERVAL, DroolsEditorType.NULL_CONST); }
    ;

operator returns [boolean negated, String opr, java.util.List<String> params]
@init{ if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true );} }
@after{ if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
  : ( op=EQUALS        { $negated = false; $opr=$op.text; $params = null; }
    | op=NOT_EQUALS    { $negated = false; $opr=$op.text; $params = null; }
    | rop=relationalOp { $negated = $rop.negated; $opr=$rop.opr; $params = $rop.params; }
    )
    ;

relationalOp returns [boolean negated, String opr, java.util.List<String> params]
@init{ if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); } }
@after{ if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } }
  : ( op=LESS_EQUALS     { $negated = false; $opr=$op.text; $params = null; }
    | op=GREATER_EQUALS  { $negated = false; $opr=$op.text; $params = null; }
    | op=LESS            { $negated = false; $opr=$op.text; $params = null; }
    | op=GREATER         { $negated = false; $opr=$op.text; $params = null; }
    | not_key nop=neg_operator_key { $negated = true; $opr=$nop.text;}
      ((squareArguments)=> sa=squareArguments { $params = $sa.args; } )?
    | cop=operator_key  { $negated = false; $opr=$cop.text;}
      ((squareArguments)=> sa=squareArguments { $params = $sa.args; } )? 
    )
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
    :	expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
    
dummy2
    :  relationalExpression EOF;
    
// top level entry point for arbitrary expression parsing
expression returns [BaseDescr result]
    :	left=conditionalExpression { if( buildDescr  ) { $result = $left.result; } }
        ((assignmentOperator) => op=assignmentOperator right=expression)?
    ;

conditionalExpression returns [BaseDescr result]
    :   left=conditionalOrExpression { if( buildDescr  ) { $result = $left.result; } }
        ternaryExpression? 
    ;
    
ternaryExpression
@init{ ternOp++; }
    :	QUESTION ts=expression COLON fs=expression
    ;
finally { ternOp--; }

conditionalOrExpression returns [BaseDescr result]
  : left=conditionalAndExpression  { if( buildDescr  ) { $result = $left.result; } }
  ( DOUBLE_PIPE right=conditionalAndExpression 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )* 
  ;

conditionalAndExpression returns [BaseDescr result]
  : left=inclusiveOrExpression { if( buildDescr  ) { $result = $left.result; } }
  ( DOUBLE_AMPER right=inclusiveOrExpression 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;

inclusiveOrExpression returns [BaseDescr result]
  : left=exclusiveOrExpression { if( buildDescr  ) { $result = $left.result; } }
  ( PIPE right=exclusiveOrExpression 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncOr(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;

exclusiveOrExpression returns [BaseDescr result]
  : left=andExpression { if( buildDescr  ) { $result = $left.result; } }
  ( XOR right=andExpression 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newXor(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;
  
andExpression returns [BaseDescr result]
  : left=equalityExpression { if( buildDescr  ) { $result = $left.result; } }
  ( AMPER right=equalityExpression 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncAnd(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )*
  ;
    
equalityExpression returns [BaseDescr result]
  : left=instanceOfExpression { if( buildDescr  ) { $result = $left.result; } }
  ( ( op=EQUALS | op=NOT_EQUALS ) 
    {  helper.setHasOperator( true );
       if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
    right=instanceOfExpression 
         { if( buildDescr  ) {
               $result = new RelationalExprDescr( $op.text, false, null, $left.result, $right.result );
           }
         }
  )*
  ;

instanceOfExpression returns [BaseDescr result]
  : left=inExpression { if( buildDescr  ) { $result = $left.result; } }
  ( op=instanceof_key 
    {  helper.setHasOperator( true );
       if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); }
    right=type
         { if( buildDescr  ) {
               $result = new RelationalExprDescr( $op.text, false, null, $left.result, new AtomicExprDescr($right.text) );
           }
         }
  )?
  ;

inExpression returns [BaseDescr result]
@init { ConstraintConnectiveDescr descr = null; BaseDescr leftDescr = null; BindingDescr binding = null; } 
@after { if( binding != null && descr != null ) descr.addOrMerge( binding ); }
  : left=relationalExpression 
    { if( buildDescr  ) { $result = $left.result; } 
      if( $left.result instanceof BindingDescr ) {
          binding = (BindingDescr)$left.result;
          leftDescr = new AtomicExprDescr( binding.getExpression() );
      } else {
          leftDescr = $left.result;
      }
    }
    ((not_key in_key)=> not_key in=in_key LEFT_PAREN e1=expression 
        {   descr = ConstraintConnectiveDescr.newAnd();
            RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, $e1.result );
            descr.addOrMerge( rel );
            $result = descr;
        }
      (COMMA e2=expression
        {   RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, $e2.result );
            descr.addOrMerge( rel );
        }
      )* RIGHT_PAREN
    | in=in_key LEFT_PAREN e1=expression 
        {   descr = ConstraintConnectiveDescr.newOr();
            RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, $e1.result );
            descr.addOrMerge( rel );
            $result = descr;
        }
      (COMMA e2=expression
        {   RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, $e2.result );
            descr.addOrMerge( rel );
        }
      )* RIGHT_PAREN 
    )?
  ;

relationalExpression returns [BaseDescr result]
scope { BaseDescr lsd; }
@init { $relationalExpression::lsd = null; }
  : left=shiftExpression 
    { if( buildDescr  ) { 
          $result = ( $left.result != null && 
                      ( (!($left.result instanceof AtomicExprDescr)) || 
                        ($left.text.equals(((AtomicExprDescr)$left.result).getExpression())) )) ? 
                    $left.result : 
                    new AtomicExprDescr( $left.text ) ; 
          $relationalExpression::lsd = $result;
      } 
    }
  ( (orRestriction)=> right=orRestriction
         { if( buildDescr  ) {
               $result = $right.result;
           }
         }
  )*
  ;

orRestriction returns [BaseDescr result]
  : left=andRestriction { if( buildDescr  ) { $result = $left.result; } }
    ( (DOUBLE_PIPE andRestriction)=>lop=DOUBLE_PIPE right=andRestriction 
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
   )* EOF?
  ;    

andRestriction returns [BaseDescr result]
  : left=singleRestriction { if( buildDescr  ) { $result = $left.result; } }
  ( (DOUBLE_AMPER singleRestriction)=>lop=DOUBLE_AMPER right=singleRestriction
         { if( buildDescr  ) {
               ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
               descr.addOrMerge( $result );  
               descr.addOrMerge( $right.result ); 
               $result = descr;
           }
         }
  )* 
  ;    
  
singleRestriction returns [BaseDescr result]
  :  op=operator value=shiftExpression
         { if( buildDescr  ) {
               BaseDescr descr = ( $value.result != null && 
                                 ( (!($value.result instanceof AtomicExprDescr)) || 
                                   ($value.text.equals(((AtomicExprDescr)$value.result).getExpression())) )) ? 
		                    $value.result : 
		                    new AtomicExprDescr( $value.text ) ;
               $result = new RelationalExprDescr( $op.opr, $op.negated, $op.params, $relationalExpression::lsd, descr );
	       if( $relationalExpression::lsd instanceof BindingDescr ) {
	           $relationalExpression::lsd = new AtomicExprDescr( ((BindingDescr)$relationalExpression::lsd).getExpression() );
	       }
           }
         }
  |  LEFT_PAREN or=orRestriction RIGHT_PAREN  { $result = $or.result; }
  ;  
  
    
    
shiftExpression returns [BaseDescr result]
  : left=additiveExpression { if( buildDescr  ) { $result = $left.result; } }
    ( (shiftOp)=>shiftOp additiveExpression )*
  ;

shiftOp
    :	( LESS LESS 
        | GREATER GREATER GREATER 
        | GREATER GREATER  )
    ;

additiveExpression returns [BaseDescr result]
    :   left=multiplicativeExpression { if( buildDescr  ) { $result = $left.result; } }
        ( (PLUS|MINUS)=> (PLUS | MINUS) multiplicativeExpression )*
    ;

multiplicativeExpression returns [BaseDescr result]
    :   left=unaryExpression { if( buildDescr  ) { $result = $left.result; } }
      ( ( STAR | DIV | MOD ) unaryExpression )*
    ;

unaryExpression returns [BaseDescr result]
    :   PLUS ue=unaryExpression 
        { if( buildDescr ) { 
            $result = $ue.result; 
            if( $result instanceof AtomicExprDescr ) {
                ((AtomicExprDescr)$result).setExpression( "+" + ((AtomicExprDescr)$result).getExpression() );
            }
        } }
    |	MINUS ue=unaryExpression 
        { if( buildDescr ) { 
            $result = $ue.result; 
            if( $result instanceof AtomicExprDescr ) {
                ((AtomicExprDescr)$result).setExpression( "-" + ((AtomicExprDescr)$result).getExpression() );
            }
        } }
    |   INCR primary
    |   DECR primary
    |   left=unaryExpressionNotPlusMinus { if( buildDescr ) { $result = $left.result; } }
    ;

unaryExpressionNotPlusMinus returns [BaseDescr result]
@init { boolean isLeft = false; BindingDescr bind = null;}
    :   TILDE unaryExpression
    | 	NEGATION unaryExpression
    |   (castExpression)=>castExpression
    |   { isLeft = helper.getLeftMostExpr() == null;}
        ( ({inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON}? (var=ID COLON 
                { hasBindings = true; if( buildDescr ) { bind = new BindingDescr($var.text, null, false); helper.setStart( bind, $var ); } } ))
        | ({inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.UNIFY}? (var=ID UNIFY 
                { hasBindings = true; if( buildDescr ) { bind = new BindingDescr($var.text, null, true); helper.setStart( bind, $var ); } } ))
        )?
        left=primary { if( buildDescr ) { $result = $left.result; } }
        ((selector)=>selector)* 
        {     
            if( buildDescr ) {
                String expr = $unaryExpressionNotPlusMinus.text;
                if( isLeft ) {
                    helper.setLeftMostExpr( expr ); 
                }
                if( bind != null ) {
                    if( bind.isUnification() ) {
                        expr = expr.substring( expr.indexOf( ":=" ) + 2 ).trim();
                    } else {
                        expr = expr.substring( expr.indexOf( ":" ) + 1 ).trim();
                    }
                    bind.setExpression( expr );
                    helper.setEnd( bind );
                    $result = bind;
                }
            }
        }
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
    :	(parExpression)=> expr=parExpression {  if( buildDescr  ) { $result = $expr.result; }  }
    |   (nonWildcardTypeArguments)=> nonWildcardTypeArguments (explicitGenericInvocationSuffix | this_key arguments)
    |   (literal)=> literal { if( buildDescr  ) { $result = new AtomicExprDescr( $literal.text, true ); }  }
    //|   this_key ({!helper.validateSpecialID(2)}?=> DOT ID)* ({helper.validateIdentifierSufix()}?=> identifierSuffix)?
    |   (super_key)=> super_key superSuffix 
    |   (new_key)=> new_key creator 
    |   (primitiveType)=> primitiveType (LEFT_SQUARE RIGHT_SQUARE)* DOT class_key 
    //|   void_key DOT class_key
    |   (inlineMapExpression)=> inlineMapExpression 
    |   (inlineListExpression)=> inlineListExpression
    |   (ID)=>i1=ID { helper.emit($i1, DroolsEditorType.IDENTIFIER); }
        ((DOT ID)=>DOT i2=ID { helper.emit($DOT, DroolsEditorType.SYMBOL); helper.emit($i2, DroolsEditorType.IDENTIFIER); }
        )* ((identifierSuffix)=>identifierSuffix)? 
    ;

inlineListExpression
    :   LEFT_SQUARE expressionList? RIGHT_SQUARE 
    ;
    
inlineMapExpression
@init{ inMap++; }
    :	LEFT_SQUARE mapExpressionList RIGHT_SQUARE 
    ;
finally { inMap--; }

mapExpressionList
    :	mapEntry (COMMA mapEntry)*
    ;
    
mapEntry
    :	expression COLON expression 
    ;

parExpression returns [BaseDescr result]
    :	LEFT_PAREN expr=expression RIGHT_PAREN 
        {  if( buildDescr  ) { 
               $result = $expr.result; 
               if( $result instanceof AtomicExprDescr ) {
                   ((AtomicExprDescr)$result).setExpression("(" +((AtomicExprDescr)$result).getExpression() + ")" );
               } 
           }  
        }
    ;

identifierSuffix
    :	(LEFT_SQUARE RIGHT_SQUARE)=>(LEFT_SQUARE { helper.emit($LEFT_SQUARE, DroolsEditorType.SYMBOL); }
                                     RIGHT_SQUARE { helper.emit($RIGHT_SQUARE, DroolsEditorType.SYMBOL); } )+ 
                                     DOT { helper.emit($DOT, DroolsEditorType.SYMBOL); } class_key 
    |	((LEFT_SQUARE) => LEFT_SQUARE { helper.emit($LEFT_SQUARE, DroolsEditorType.SYMBOL); } 
                          expression 
                          RIGHT_SQUARE { helper.emit($RIGHT_SQUARE, DroolsEditorType.SYMBOL); } )+ // can also be matched by selector, but do here
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
    :   (DOT super_key)=>DOT { helper.emit($DOT, DroolsEditorType.SYMBOL); } super_key superSuffix
    |   (DOT new_key)=>DOT { helper.emit($DOT, DroolsEditorType.SYMBOL); } new_key (nonWildcardTypeArguments)? innerCreator
    |   (DOT ID)=>DOT { helper.emit($DOT, DroolsEditorType.SYMBOL); } 
                  ID { helper.emit($ID, DroolsEditorType.IDENTIFIER); }
                  ((LEFT_PAREN) => arguments)?
    //|   DOT this_key
    |   (LEFT_SQUARE)=>LEFT_SQUARE { helper.emit($LEFT_SQUARE, DroolsEditorType.SYMBOL); }
                       expression 
                       RIGHT_SQUARE { helper.emit($RIGHT_SQUARE, DroolsEditorType.SYMBOL); }
    ;

superSuffix
    :	arguments
    |   	DOT ID ((LEFT_PAREN) => arguments)?
    ;

squareArguments returns [java.util.List<String> args]
    : LEFT_SQUARE (el=expressionList { $args = $el.exprs; })? RIGHT_SQUARE
    ;

arguments
    :	LEFT_PAREN { helper.emit($LEFT_PAREN, DroolsEditorType.SYMBOL); }
        expressionList? 
        RIGHT_PAREN { helper.emit($RIGHT_PAREN, DroolsEditorType.SYMBOL); }
    ;

expressionList returns [java.util.List<String> exprs]
@init { $exprs = new java.util.ArrayList<String>();}
  :   f=expression { $exprs.add( $f.text ); }
      (COMMA s=expression { $exprs.add( $s.text ); })*
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
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

super_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

instanceof_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

boolean_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

char_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

byte_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

short_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

int_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.INT))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

float_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

long_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

double_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

void_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

this_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

class_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))}?=> id=ID  { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

new_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

not_key
    :      {(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
    ;

in_key
  :      {(helper.validateIdentifierKey(DroolsSoftKeywords.IN))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
  ;

operator_key
  :      {(helper.isPluggableEvaluator(false))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
  ;

neg_operator_key
  :      {(helper.isPluggableEvaluator(true))}?=> id=ID { helper.emit($ID, DroolsEditorType.KEYWORD); } 
  ;
