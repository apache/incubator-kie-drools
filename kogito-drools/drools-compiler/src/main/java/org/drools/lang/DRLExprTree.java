// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g 2011-02-23 18:34:52

    package org.drools.lang;

    import java.util.LinkedList;
    import org.drools.compiler.DroolsParserException;
    import org.drools.lang.ParserHelper;
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.CheckedDroolsException;
    import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class DRLExprTree extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OPERATOR", "NEG_OPERATOR", "SHIFT_EXPR", "SHL_ASSIGN", "SHRB_ASSIGN", "SHR_ASSIGN", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
    };
    public static final int EOF=-1;
    public static final int OPERATOR=4;
    public static final int NEG_OPERATOR=5;
    public static final int SHIFT_EXPR=6;
    public static final int SHL_ASSIGN=7;
    public static final int SHRB_ASSIGN=8;
    public static final int SHR_ASSIGN=9;
    public static final int EOL=10;
    public static final int WS=11;
    public static final int Exponent=12;
    public static final int FloatTypeSuffix=13;
    public static final int FLOAT=14;
    public static final int HexDigit=15;
    public static final int IntegerTypeSuffix=16;
    public static final int HEX=17;
    public static final int DECIMAL=18;
    public static final int EscapeSequence=19;
    public static final int STRING=20;
    public static final int TimePeriod=21;
    public static final int UnicodeEscape=22;
    public static final int OctalEscape=23;
    public static final int BOOL=24;
    public static final int NULL=25;
    public static final int AT=26;
    public static final int PLUS_ASSIGN=27;
    public static final int MINUS_ASSIGN=28;
    public static final int MULT_ASSIGN=29;
    public static final int DIV_ASSIGN=30;
    public static final int AND_ASSIGN=31;
    public static final int OR_ASSIGN=32;
    public static final int XOR_ASSIGN=33;
    public static final int MOD_ASSIGN=34;
    public static final int DECR=35;
    public static final int INCR=36;
    public static final int ARROW=37;
    public static final int SEMICOLON=38;
    public static final int COLON=39;
    public static final int EQUALS=40;
    public static final int NOT_EQUALS=41;
    public static final int GREATER_EQUALS=42;
    public static final int LESS_EQUALS=43;
    public static final int GREATER=44;
    public static final int LESS=45;
    public static final int EQUALS_ASSIGN=46;
    public static final int LEFT_PAREN=47;
    public static final int RIGHT_PAREN=48;
    public static final int LEFT_SQUARE=49;
    public static final int RIGHT_SQUARE=50;
    public static final int LEFT_CURLY=51;
    public static final int RIGHT_CURLY=52;
    public static final int COMMA=53;
    public static final int DOT=54;
    public static final int DOUBLE_AMPER=55;
    public static final int DOUBLE_PIPE=56;
    public static final int QUESTION=57;
    public static final int NEGATION=58;
    public static final int TILDE=59;
    public static final int PIPE=60;
    public static final int AMPER=61;
    public static final int XOR=62;
    public static final int MOD=63;
    public static final int STAR=64;
    public static final int MINUS=65;
    public static final int PLUS=66;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=67;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=68;
    public static final int MULTI_LINE_COMMENT=69;
    public static final int IdentifierStart=70;
    public static final int IdentifierPart=71;
    public static final int ID=72;
    public static final int DIV=73;
    public static final int MISC=74;

    // delegates
    // delegators


        public DRLExprTree(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLExprTree(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DRLExprTree.tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g"; }


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



    // $ANTLR start "constraint"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:37:1: constraint returns [ConstraintConnectiveDescr root] : ex= expression ;
    public final ConstraintConnectiveDescr constraint() throws RecognitionException {
        ConstraintConnectiveDescr root = null;

        BaseDescr ex = null;


         root = ConstraintConnectiveDescr.newAnd(); 
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:40:5: (ex= expression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:40:8: ex= expression
            {
            pushFollow(FOLLOW_expression_in_constraint87);
            ex=expression();

            state._fsp--;


            }

             root.addOrMerge( ex ); 
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return root;
    }
    // $ANTLR end "constraint"


    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:43:1: expression returns [BaseDescr result] : ( ^(op= DOUBLE_PIPE p1= expression p2= expression ) | ^( DOUBLE_AMPER p1= expression p2= expression ) | ^( PIPE p1= expression p2= expression ) | ^( XOR p1= expression p2= expression ) | ^( AMPER p1= expression p2= expression ) | ^( EQUALS p1= expression p2= expression ) | ^( NOT_EQUALS p1= expression p2= expression ) | ^( LESS_EQUALS p1= expression p2= expression ) | ^( GREATER_EQUALS p1= expression p2= expression ) | ^( LESS p1= expression p2= expression ) | ^( GREATER p1= expression p2= expression ) | ^(op= OPERATOR p1= expression p2= expression ) | ^(op= NEG_OPERATOR p1= expression p2= expression ) | ^(ao= assignmentOperator p1= expression p2= expression ) | ^( QUESTION expression expression expression ) | se= SHIFT_EXPR );
    public final BaseDescr expression() throws RecognitionException {
        BaseDescr result = null;

        CommonTree op=null;
        CommonTree se=null;
        BaseDescr p1 = null;

        BaseDescr p2 = null;

        DRLExprTree.assignmentOperator_return ao = null;


         BaseDescr descr = null; 
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:46:5: ( ^(op= DOUBLE_PIPE p1= expression p2= expression ) | ^( DOUBLE_AMPER p1= expression p2= expression ) | ^( PIPE p1= expression p2= expression ) | ^( XOR p1= expression p2= expression ) | ^( AMPER p1= expression p2= expression ) | ^( EQUALS p1= expression p2= expression ) | ^( NOT_EQUALS p1= expression p2= expression ) | ^( LESS_EQUALS p1= expression p2= expression ) | ^( GREATER_EQUALS p1= expression p2= expression ) | ^( LESS p1= expression p2= expression ) | ^( GREATER p1= expression p2= expression ) | ^(op= OPERATOR p1= expression p2= expression ) | ^(op= NEG_OPERATOR p1= expression p2= expression ) | ^(ao= assignmentOperator p1= expression p2= expression ) | ^( QUESTION expression expression expression ) | se= SHIFT_EXPR )
            int alt1=16;
            switch ( input.LA(1) ) {
            case DOUBLE_PIPE:
                {
                alt1=1;
                }
                break;
            case DOUBLE_AMPER:
                {
                alt1=2;
                }
                break;
            case PIPE:
                {
                alt1=3;
                }
                break;
            case XOR:
                {
                alt1=4;
                }
                break;
            case AMPER:
                {
                alt1=5;
                }
                break;
            case EQUALS:
                {
                alt1=6;
                }
                break;
            case NOT_EQUALS:
                {
                alt1=7;
                }
                break;
            case LESS_EQUALS:
                {
                alt1=8;
                }
                break;
            case GREATER_EQUALS:
                {
                alt1=9;
                }
                break;
            case LESS:
                {
                alt1=10;
                }
                break;
            case GREATER:
                {
                alt1=11;
                }
                break;
            case OPERATOR:
                {
                alt1=12;
                }
                break;
            case NEG_OPERATOR:
                {
                alt1=13;
                }
                break;
            case SHL_ASSIGN:
            case SHRB_ASSIGN:
            case SHR_ASSIGN:
            case PLUS_ASSIGN:
            case MINUS_ASSIGN:
            case MULT_ASSIGN:
            case DIV_ASSIGN:
            case AND_ASSIGN:
            case OR_ASSIGN:
            case XOR_ASSIGN:
            case MOD_ASSIGN:
            case EQUALS_ASSIGN:
                {
                alt1=14;
                }
                break;
            case QUESTION:
                {
                alt1=15;
                }
                break;
            case SHIFT_EXPR:
                {
                alt1=16;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:46:9: ^(op= DOUBLE_PIPE p1= expression p2= expression )
                    {
                    op=(CommonTree)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_expression125); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression129);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression133);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = ConstraintConnectiveDescr.newOr(); 
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p1 );  
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p2 ); 

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:50:9: ^( DOUBLE_AMPER p1= expression p2= expression )
                    {
                    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_expression157); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression161);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression165);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = ConstraintConnectiveDescr.newAnd(); 
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p1 );  
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p2 ); 

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:54:9: ^( PIPE p1= expression p2= expression )
                    {
                    match(input,PIPE,FOLLOW_PIPE_in_expression187); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression191);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression195);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = ConstraintConnectiveDescr.newIncOr(); 
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p1 );  
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p2 ); 

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:58:9: ^( XOR p1= expression p2= expression )
                    {
                    match(input,XOR,FOLLOW_XOR_in_expression217); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression221);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression225);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = ConstraintConnectiveDescr.newXor(); 
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p1 );  
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p2 ); 

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:62:9: ^( AMPER p1= expression p2= expression )
                    {
                    match(input,AMPER,FOLLOW_AMPER_in_expression247); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression251);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression255);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = ConstraintConnectiveDescr.newIncAnd(); 
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p1 );  
                             ((ConstraintConnectiveDescr)descr).addOrMerge( p2 ); 

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:66:9: ^( EQUALS p1= expression p2= expression )
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_expression277); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression281);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression285);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( "==", p1, p2 ); 

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:68:9: ^( NOT_EQUALS p1= expression p2= expression )
                    {
                    match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_expression307); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression311);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression315);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( "!=", p1, p2 ); 

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:70:9: ^( LESS_EQUALS p1= expression p2= expression )
                    {
                    match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_expression337); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression341);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression345);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( "<=", p1, p2 ); 

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:72:9: ^( GREATER_EQUALS p1= expression p2= expression )
                    {
                    match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_expression367); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression371);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression375);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( ">=", p1, p2 ); 

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:74:9: ^( LESS p1= expression p2= expression )
                    {
                    match(input,LESS,FOLLOW_LESS_in_expression397); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression401);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression405);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( "<", p1, p2 ); 

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:76:9: ^( GREATER p1= expression p2= expression )
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_expression427); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression431);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression435);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( ">", p1, p2 ); 

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:78:9: ^(op= OPERATOR p1= expression p2= expression )
                    {
                    op=(CommonTree)match(input,OPERATOR,FOLLOW_OPERATOR_in_expression459); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression463);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression467);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( (op!=null?op.getText():null), p1, p2 ); 

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:80:9: ^(op= NEG_OPERATOR p1= expression p2= expression )
                    {
                    op=(CommonTree)match(input,NEG_OPERATOR,FOLLOW_NEG_OPERATOR_in_expression491); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression495);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression499);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( "not "+(op!=null?op.getText():null), p1, p2 ); 

                    }
                    break;
                case 14 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:82:8: ^(ao= assignmentOperator p1= expression p2= expression )
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression522);
                    ao=assignmentOperator();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression526);
                    p1=expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression530);
                    p2=expression();

                    state._fsp--;


                    match(input, Token.UP, null); 
                     descr = new RelationalExprDescr( (ao!=null?((CommonTree)ao.start):null).getText(), p1, p2 ); 

                    }
                    break;
                case 15 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:84:9: ^( QUESTION expression expression expression )
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_expression555); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_expression_in_expression557);
                    expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression559);
                    expression();

                    state._fsp--;

                    pushFollow(FOLLOW_expression_in_expression561);
                    expression();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 16 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:85:9: se= SHIFT_EXPR
                    {
                    se=(CommonTree)match(input,SHIFT_EXPR,FOLLOW_SHIFT_EXPR_in_expression575); 
                     descr = new AtomicExprDescr( (se!=null?se.getText():null) ); 

                    }
                    break;

            }
             result = descr; 
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "expression"

    public static class assignmentOperator_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "assignmentOperator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:89:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | SHL_ASSIGN | SHRB_ASSIGN | SHR_ASSIGN );
    public final DRLExprTree.assignmentOperator_return assignmentOperator() throws RecognitionException {
        DRLExprTree.assignmentOperator_return retval = new DRLExprTree.assignmentOperator_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:90:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | SHL_ASSIGN | SHRB_ASSIGN | SHR_ASSIGN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExprTree.g:
            {
            if ( (input.LA(1)>=SHL_ASSIGN && input.LA(1)<=SHR_ASSIGN)||(input.LA(1)>=PLUS_ASSIGN && input.LA(1)<=MOD_ASSIGN)||input.LA(1)==EQUALS_ASSIGN ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "assignmentOperator"

    // Delegated rules


 

    public static final BitSet FOLLOW_expression_in_constraint87 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_expression125 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression129 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression133 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_expression157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression161 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression165 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PIPE_in_expression187 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression191 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression195 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_XOR_in_expression217 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression221 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression225 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AMPER_in_expression247 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression251 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression255 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_EQUALS_in_expression277 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression281 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression285 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_expression307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression311 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression315 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_expression337 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression341 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression345 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_expression367 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression371 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression375 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_LESS_in_expression397 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression401 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression405 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_GREATER_in_expression427 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression431 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression435 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPERATOR_in_expression459 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression463 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression467 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NEG_OPERATOR_in_expression491 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression495 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression499 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression522 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression526 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression530 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_QUESTION_in_expression555 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression557 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression559 = new BitSet(new long[]{0x73807F07F80003F0L});
    public static final BitSet FOLLOW_expression_in_expression561 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SHIFT_EXPR_in_expression575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_assignmentOperator0 = new BitSet(new long[]{0x0000000000000002L});

}