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
expression returns [BaseDescr result]
@init { BaseDescr descr = null; }
@after { $result = descr; }
    :   ^(op=DOUBLE_PIPE p1=expression p2=expression )  
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
    |   ^(EQUALS p1=expression p2=expression )
       { descr = new RelationalExprDescr( "==", $p1.result, $p2.result ); }
    |   ^(NOT_EQUALS p1=expression p2=expression )
       { descr = new RelationalExprDescr( "!=", $p1.result, $p2.result ); }
    |   ^(LESS_EQUALS p1=expression p2=expression )
       { descr = new RelationalExprDescr( "<=", $p1.result, $p2.result ); }
    |   ^(GREATER_EQUALS p1=expression p2=expression )
       { descr = new RelationalExprDescr( ">=", $p1.result, $p2.result ); }
    |   ^(LESS p1=expression p2=expression )
       { descr = new RelationalExprDescr( "<", $p1.result, $p2.result ); }
    |   ^(GREATER p1=expression p2=expression )
       { descr = new RelationalExprDescr( ">", $p1.result, $p2.result ); }
    |   ^(op=OPERATOR p1=expression p2=expression )
       { descr = new RelationalExprDescr( $op.text, $p1.result, $p2.result ); }
    |   ^(op=NEG_OPERATOR p1=expression p2=expression )
       { descr = new RelationalExprDescr( "not "+$op.text, $p1.result, $p2.result ); }
    | 	^(ao=assignmentOperator p1=expression p2=expression)    
       { descr = new RelationalExprDescr( $ao.start.getText(), $p1.result, $p2.result ); }
    |   ^(QUESTION expression expression expression )
    |   se=SHIFT_EXPR
       { descr = new EvalDescr( $se.text ); }
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
