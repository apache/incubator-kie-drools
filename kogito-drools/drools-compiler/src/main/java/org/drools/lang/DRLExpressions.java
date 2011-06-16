// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-06-16 09:43:39

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
    


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLExpressions extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TIME_INTERVAL", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "UNIFY", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
    };
    public static final int EOF=-1;
    public static final int EOL=4;
    public static final int WS=5;
    public static final int Exponent=6;
    public static final int FloatTypeSuffix=7;
    public static final int FLOAT=8;
    public static final int HexDigit=9;
    public static final int IntegerTypeSuffix=10;
    public static final int HEX=11;
    public static final int DECIMAL=12;
    public static final int EscapeSequence=13;
    public static final int STRING=14;
    public static final int TIME_INTERVAL=15;
    public static final int UnicodeEscape=16;
    public static final int OctalEscape=17;
    public static final int BOOL=18;
    public static final int NULL=19;
    public static final int AT=20;
    public static final int PLUS_ASSIGN=21;
    public static final int MINUS_ASSIGN=22;
    public static final int MULT_ASSIGN=23;
    public static final int DIV_ASSIGN=24;
    public static final int AND_ASSIGN=25;
    public static final int OR_ASSIGN=26;
    public static final int XOR_ASSIGN=27;
    public static final int MOD_ASSIGN=28;
    public static final int UNIFY=29;
    public static final int DECR=30;
    public static final int INCR=31;
    public static final int ARROW=32;
    public static final int SEMICOLON=33;
    public static final int COLON=34;
    public static final int EQUALS=35;
    public static final int NOT_EQUALS=36;
    public static final int GREATER_EQUALS=37;
    public static final int LESS_EQUALS=38;
    public static final int GREATER=39;
    public static final int LESS=40;
    public static final int EQUALS_ASSIGN=41;
    public static final int LEFT_PAREN=42;
    public static final int RIGHT_PAREN=43;
    public static final int LEFT_SQUARE=44;
    public static final int RIGHT_SQUARE=45;
    public static final int LEFT_CURLY=46;
    public static final int RIGHT_CURLY=47;
    public static final int COMMA=48;
    public static final int DOT=49;
    public static final int DOUBLE_AMPER=50;
    public static final int DOUBLE_PIPE=51;
    public static final int QUESTION=52;
    public static final int NEGATION=53;
    public static final int TILDE=54;
    public static final int PIPE=55;
    public static final int AMPER=56;
    public static final int XOR=57;
    public static final int MOD=58;
    public static final int STAR=59;
    public static final int MINUS=60;
    public static final int PLUS=61;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=62;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=63;
    public static final int MULTI_LINE_COMMENT=64;
    public static final int IdentifierStart=65;
    public static final int IdentifierPart=66;
    public static final int ID=67;
    public static final int DIV=68;
    public static final int MISC=69;

    // delegates
    // delegators


        public DRLExpressions(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLExpressions(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DRLExpressions.tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g"; }


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


    public static class literal_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL );
    public final DRLExpressions.literal_return literal() throws RecognitionException {
        DRLExpressions.literal_return retval = new DRLExpressions.literal_return();
        retval.start = input.LT(1);

        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;
        Token TIME_INTERVAL7=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:73:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL )
            int alt1=7;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt1=1;
                }
                break;
            case DECIMAL:
                {
                alt1=2;
                }
                break;
            case HEX:
                {
                alt1=3;
                }
                break;
            case FLOAT:
                {
                alt1=4;
                }
                break;
            case BOOL:
                {
                alt1=5;
                }
                break;
            case NULL:
                {
                alt1=6;
                }
                break;
            case TIME_INTERVAL:
                {
                alt1=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:73:7: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal83); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:7: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal100); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:7: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal116); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:76:7: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal136); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:77:7: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal154); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:7: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal173); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:9: TIME_INTERVAL
                    {
                    TIME_INTERVAL7=(Token)match(input,TIME_INTERVAL,FOLLOW_TIME_INTERVAL_in_literal194); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(TIME_INTERVAL7, DroolsEditorType.NULL_CONST); 
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class operator_return extends ParserRuleReturnScope {
        public boolean negated;
        public String opr;
        public java.util.List<String> params;
    };

    // $ANTLR start "operator"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:82:1: operator returns [boolean negated, String opr, java.util.List<String> params] : (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) ;
    public final DRLExpressions.operator_return operator() throws RecognitionException {
        DRLExpressions.operator_return retval = new DRLExpressions.operator_return();
        retval.start = input.LT(1);

        Token op=null;
        DRLExpressions.relationalOp_return rop = null;


         if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true );} 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:85:3: ( (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:85:5: (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
            {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:85:5: (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==EQUALS) ) {
                alt2=1;
            }
            else if ( (LA2_0==NOT_EQUALS) ) {
                alt2=2;
            }
            else if ( ((LA2_0>=GREATER_EQUALS && LA2_0<=LESS)) ) {
                alt2=3;
            }
            else if ( (LA2_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt2=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:85:7: op= EQUALS
                    {
                    op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator227); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:7: op= NOT_EQUALS
                    {
                    op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator246); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:7: rop= relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator261);
                    rop=relationalOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = (rop!=null?rop.negated:false); retval.opr =(rop!=null?rop.opr:null); retval.params = (rop!=null?rop.params:null); 
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {
               if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator"

    public static class relationalOp_return extends ParserRuleReturnScope {
        public boolean negated;
        public String opr;
        public java.util.List<String> params;
    };

    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:91:1: relationalOp returns [boolean negated, String opr, java.util.List<String> params] : (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | not_key nop= neg_operator_key ( ( squareArguments )=>sa= squareArguments )? | cop= operator_key ( ( squareArguments )=>sa= squareArguments )? ) ;
    public final DRLExpressions.relationalOp_return relationalOp() throws RecognitionException {
        DRLExpressions.relationalOp_return retval = new DRLExpressions.relationalOp_return();
        retval.start = input.LT(1);

        Token op=null;
        DRLExpressions.neg_operator_key_return nop = null;

        java.util.List<String> sa = null;

        DRLExpressions.operator_key_return cop = null;


         if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); } 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:3: ( (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | not_key nop= neg_operator_key ( ( squareArguments )=>sa= squareArguments )? | cop= operator_key ( ( squareArguments )=>sa= squareArguments )? ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | not_key nop= neg_operator_key ( ( squareArguments )=>sa= squareArguments )? | cop= operator_key ( ( squareArguments )=>sa= squareArguments )? )
            {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | not_key nop= neg_operator_key ( ( squareArguments )=>sa= squareArguments )? | cop= operator_key ( ( squareArguments )=>sa= squareArguments )? )
            int alt5=6;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==LESS_EQUALS) ) {
                alt5=1;
            }
            else if ( (LA5_0==GREATER_EQUALS) ) {
                alt5=2;
            }
            else if ( (LA5_0==LESS) ) {
                alt5=3;
            }
            else if ( (LA5_0==GREATER) ) {
                alt5=4;
            }
            else if ( (LA5_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                int LA5_5 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt5=5;
                }
                else if ( (((helper.isPluggableEvaluator(false)))) ) {
                    alt5=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 5, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:7: op= LESS_EQUALS
                    {
                    op=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp300); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:95:7: op= GREATER_EQUALS
                    {
                    op=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp316); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:96:7: op= LESS
                    {
                    op=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp329); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:97:7: op= GREATER
                    {
                    op=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp352); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:7: not_key nop= neg_operator_key ( ( squareArguments )=>sa= squareArguments )?
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp370);
                    not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp374);
                    nop=neg_operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = true; retval.opr =(nop!=null?input.toString(nop.start,nop.stop):null);
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:7: ( ( squareArguments )=>sa= squareArguments )?
                    int alt3=2;
                    alt3 = dfa3.predict(input);
                    switch (alt3) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:8: ( squareArguments )=>sa= squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp392);
                            sa=squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               retval.params = sa; 
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:100:7: cop= operator_key ( ( squareArguments )=>sa= squareArguments )?
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp407);
                    cop=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(cop!=null?input.toString(cop.start,cop.stop):null);
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:7: ( ( squareArguments )=>sa= squareArguments )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:8: ( squareArguments )=>sa= squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp426);
                            sa=squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               retval.params = sa; 
                            }

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {
               if( state.backtracking == 0 && input.LA( 1 ) != DRLLexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } 
            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "relationalOp"


    // $ANTLR start "typeList"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:5: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:7: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList459);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:12: ( COMMA type )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==COMMA) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:13: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList462); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList464);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "typeList"

    public static class type_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "type"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:109:1: type : tm= typeMatch ;
    public final DRLExpressions.type_return type() throws RecognitionException {
        DRLExpressions.type_return retval = new DRLExpressions.type_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:5: (tm= typeMatch )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:8: tm= typeMatch
            {
            pushFollow(FOLLOW_typeMatch_in_type486);
            typeMatch();

            state._fsp--;
            if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "type"


    // $ANTLR start "typeMatch"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:113:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void typeMatch() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ID) ) {
                int LA12_1 = input.LA(2);

                if ( (((synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred3_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))))) ) {
                    alt12=1;
                }
                else if ( (true) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_typeMatch517);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==LEFT_SQUARE) && (synpred4_DRLExpressions())) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch527); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch529); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_typeMatch543); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:12: ( ( typeArguments )=> typeArguments )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==LESS) ) {
                        int LA8_1 = input.LA(2);

                        if ( (LA8_1==ID) && (synpred5_DRLExpressions())) {
                            alt8=1;
                        }
                        else if ( (LA8_1==QUESTION) && (synpred5_DRLExpressions())) {
                            alt8=1;
                        }
                    }
                    switch (alt8) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_typeMatch550);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==DOT) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_typeMatch555); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_typeMatch557); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt9=2;
                    	    int LA9_0 = input.LA(1);

                    	    if ( (LA9_0==LESS) ) {
                    	        int LA9_1 = input.LA(2);

                    	        if ( (LA9_1==ID) && (synpred6_DRLExpressions())) {
                    	            alt9=1;
                    	        }
                    	        else if ( (LA9_1==QUESTION) && (synpred6_DRLExpressions())) {
                    	            alt9=1;
                    	        }
                    	    }
                    	    switch (alt9) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_typeMatch564);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0==LEFT_SQUARE) && (synpred7_DRLExpressions())) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch579); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch581); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);


                    }


                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "typeMatch"


    // $ANTLR start "typeArguments"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments606); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments608);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:25: ( COMMA typeArgument )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==COMMA) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:119:26: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments611); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments613);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments617); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "typeArguments"


    // $ANTLR start "typeArgument"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID) ) {
                alt16=1;
            }
            else if ( (LA16_0==QUESTION) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:123:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument634);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument642); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:16: ( ( extends_key | super_key ) type )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:17: ( extends_key | super_key ) type
                            {
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:17: ( extends_key | super_key )
                            int alt14=2;
                            int LA14_0 = input.LA(1);

                            if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                                int LA14_1 = input.LA(2);

                                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                                    alt14=1;
                                }
                                else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                                    alt14=2;
                                }
                                else {
                                    if (state.backtracking>0) {state.failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 14, 1, input);

                                    throw nvae;
                                }
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 14, 0, input);

                                throw nvae;
                            }
                            switch (alt14) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument646);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:124:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument650);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument653);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "typeArgument"


    // $ANTLR start "dummy"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:132:1: dummy : expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
    public final void dummy() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:5: ( expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:7: expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN )
            {
            pushFollow(FOLLOW_expression_in_dummy677);
            expression();

            state._fsp--;
            if (state.failed) return ;
            if ( input.LA(1)==EOF||input.LA(1)==AT||input.LA(1)==SEMICOLON||input.LA(1)==RIGHT_PAREN||input.LA(1)==ID ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dummy"


    // $ANTLR start "dummy2"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:135:1: dummy2 : relationalExpression EOF ;
    public final void dummy2() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:136:5: ( relationalExpression EOF )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:136:8: relationalExpression EOF
            {
            pushFollow(FOLLOW_relationalExpression_in_dummy2717);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            match(input,EOF,FOLLOW_EOF_in_dummy2719); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dummy2"

    public static class expression_return extends ParserRuleReturnScope {
        public BaseDescr result;
    };

    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:139:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? ;
    public final DRLExpressions.expression_return expression() throws RecognitionException {
        DRLExpressions.expression_return retval = new DRLExpressions.expression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;

        DRLExpressions.expression_return right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression742);
            left=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            int alt17=2;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:10: ( assignmentOperator )=>op= assignmentOperator right= expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression763);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression767);
                    right=expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "expression"


    // $ANTLR start "conditionalExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:144:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( ternaryExpression )? ;
    public final BaseDescr conditionalExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:145:5: (left= conditionalOrExpression ( ternaryExpression )? )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:145:9: left= conditionalOrExpression ( ternaryExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression794);
            left=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:9: ( ternaryExpression )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==QUESTION) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:9: ternaryExpression
                    {
                    pushFollow(FOLLOW_ternaryExpression_in_conditionalExpression806);
                    ternaryExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "ternaryExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:1: ternaryExpression : QUESTION ts= expression COLON fs= expression ;
    public final void ternaryExpression() throws RecognitionException {
        DRLExpressions.expression_return ts = null;

        DRLExpressions.expression_return fs = null;


         ternOp++; 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:151:5: ( QUESTION ts= expression COLON fs= expression )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:151:7: QUESTION ts= expression COLON fs= expression
            {
            match(input,QUESTION,FOLLOW_QUESTION_in_ternaryExpression833); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression837);
            ts=expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_ternaryExpression839); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression843);
            fs=expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
             ternOp--; 
        }
        return ;
    }
    // $ANTLR end "ternaryExpression"


    // $ANTLR start "conditionalOrExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )* ;
    public final BaseDescr conditionalOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:3: (left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:5: left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression868);
            left=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:3: ( DOUBLE_PIPE right= conditionalAndExpression )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==DOUBLE_PIPE) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:5: DOUBLE_PIPE right= conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression877); if (state.failed) return result;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression881);
            	    right=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )* ;
    public final BaseDescr conditionalAndExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:3: (left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:5: left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression918);
            left=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:3: ( DOUBLE_AMPER right= inclusiveOrExpression )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==DOUBLE_AMPER) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:5: DOUBLE_AMPER right= inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression926); if (state.failed) return result;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression930);
            	    right=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
    public final BaseDescr inclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression966);
            left=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:3: ( PIPE right= exclusiveOrExpression )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==PIPE) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:5: PIPE right= exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression974); if (state.failed) return result;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression978);
            	    right=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncOr(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:194:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
    public final BaseDescr exclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:3: (left= andExpression ( XOR right= andExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:5: left= andExpression ( XOR right= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1014);
            left=andExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:3: ( XOR right= andExpression )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==XOR) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:5: XOR right= andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression1022); if (state.failed) return result;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1026);
            	    right=andExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newXor(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "exclusiveOrExpression"


    // $ANTLR start "andExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:1: andExpression returns [BaseDescr result] : left= equalityExpression ( AMPER right= equalityExpression )* ;
    public final BaseDescr andExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:3: (left= equalityExpression ( AMPER right= equalityExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:5: left= equalityExpression ( AMPER right= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression1064);
            left=equalityExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:209:3: ( AMPER right= equalityExpression )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==AMPER) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:209:5: AMPER right= equalityExpression
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression1072); if (state.failed) return result;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression1076);
            	    right=equalityExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncAnd(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "andExpression"


    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* ;
    public final BaseDescr equalityExpression() throws RecognitionException {
        BaseDescr result = null;

        Token op=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:3: (left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:5: left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1116);
            left=instanceOfExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:3: ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>=EQUALS && LA25_0<=NOT_EQUALS)) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:5: (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression
            	    {
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:5: (op= EQUALS | op= NOT_EQUALS )
            	    int alt24=2;
            	    int LA24_0 = input.LA(1);

            	    if ( (LA24_0==EQUALS) ) {
            	        alt24=1;
            	    }
            	    else if ( (LA24_0==NOT_EQUALS) ) {
            	        alt24=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 24, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:7: op= EQUALS
            	            {
            	            op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression1128); if (state.failed) return result;

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:19: op= NOT_EQUALS
            	            {
            	            op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression1134); if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	        helper.setHasOperator( true );
            	             if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
            	    }
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1151);
            	    right=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     result = new RelationalExprDescr( (op!=null?op.getText():null), false, null, left, right );
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "equalityExpression"


    // $ANTLR start "instanceOfExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:233:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
    public final BaseDescr instanceOfExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRLExpressions.instanceof_key_return op = null;

        DRLExpressions.type_return right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:3: (left= inExpression (op= instanceof_key right= type )? )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:5: left= inExpression (op= instanceof_key right= type )?
            {
            pushFollow(FOLLOW_inExpression_in_instanceOfExpression1187);
            left=inExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:3: (op= instanceof_key right= type )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))) {
                    alt26=1;
                }
            }
            switch (alt26) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:5: op= instanceof_key right= type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression1197);
                    op=instanceof_key();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        helper.setHasOperator( true );
                             if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression1212);
                    right=type();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) {
                                     result = new RelationalExprDescr( (op!=null?input.toString(op.start,op.stop):null), false, null, left, new AtomicExprDescr((right!=null?input.toString(right.start,right.stop):null)) );
                                 }
                               
                    }

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "instanceOfExpression"


    // $ANTLR start "inExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:1: inExpression returns [BaseDescr result] : left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? ;
    public final BaseDescr inExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRLExpressions.expression_return e1 = null;

        DRLExpressions.expression_return e2 = null;


         ConstraintConnectiveDescr descr = null; 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:248:3: (left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:248:5: left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression1253);
            left=relationalExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:5: ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            int alt29=3;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ID) ) {
                int LA29_1 = input.LA(2);

                if ( (LA29_1==ID) ) {
                    int LA29_3 = input.LA(3);

                    if ( (LA29_3==LEFT_PAREN) && ((synpred9_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                        alt29=1;
                    }
                }
                else if ( (LA29_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt29=2;
                }
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:6: ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_not_key_in_inExpression1269);
                    not_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_in_key_in_inExpression1273);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1275); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_inExpression1279);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newAnd();
                                  RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, left, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:7: ( COMMA e2= expression )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==COMMA) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1299); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1303);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	         RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, left, (e2!=null?e2.result:null) );
                    	                  descr.addOrMerge( rel );
                    	              
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1324); if (state.failed) return result;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:260:7: in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_in_key_in_inExpression1334);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1336); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_inExpression1340);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newOr();
                                  RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, left, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:7: ( COMMA e2= expression )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0==COMMA) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1360); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1364);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	         RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, left, (e2!=null?e2.result:null) );
                    	                  descr.addOrMerge( rel );
                    	              
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1385); if (state.failed) return result;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "inExpression"

    protected static class relationalExpression_scope {
        BaseDescr lsd;
    }
    protected Stack relationalExpression_stack = new Stack();


    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:274:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( orRestriction )=>right= orRestriction )* ;
    public final BaseDescr relationalExpression() throws RecognitionException {
        relationalExpression_stack.push(new relationalExpression_scope());
        BaseDescr result = null;

        DRLExpressions.shiftExpression_return left = null;

        BaseDescr right = null;


         ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = null; 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:3: (left= shiftExpression ( ( orRestriction )=>right= orRestriction )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:5: left= shiftExpression ( ( orRestriction )=>right= orRestriction )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression1421);
            left=shiftExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { 
                        result = ( (left!=null?left.result:null) != null && 
                                    ( (!((left!=null?left.result:null) instanceof AtomicExprDescr)) || 
                                      ((left!=null?input.toString(left.start,left.stop):null).equals(((AtomicExprDescr)(left!=null?left.result:null)).getExpression())) )) ? 
                                  (left!=null?left.result:null) : 
                                  new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ) ; 
                        ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = result;
                    } 
                  
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:3: ( ( orRestriction )=>right= orRestriction )*
            loop30:
            do {
                int alt30=2;
                alt30 = dfa30.predict(input);
                switch (alt30) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:5: ( orRestriction )=>right= orRestriction
            	    {
            	    pushFollow(FOLLOW_orRestriction_in_relationalExpression1441);
            	    right=orRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     result = right;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
            relationalExpression_stack.pop();
        }
        return result;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "orRestriction"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:1: orRestriction returns [BaseDescr result] : left= andRestriction ( ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction )* ( EOF )? ;
    public final BaseDescr orRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:3: (left= andRestriction ( ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction )* ( EOF )? )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:5: left= andRestriction ( ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction )* ( EOF )?
            {
            pushFollow(FOLLOW_andRestriction_in_orRestriction1476);
            left=andRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:5: ( ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction )*
            loop31:
            do {
                int alt31=2;
                alt31 = dfa31.predict(input);
                switch (alt31) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:7: ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_orRestriction1494); if (state.failed) return result;
            	    pushFollow(FOLLOW_andRestriction_in_orRestriction1498);
            	    right=andRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:305:7: ( EOF )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==EOF) ) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==EOF) ) {
                    int LA32_3 = input.LA(3);

                    if ( (LA32_3==EOF) ) {
                        alt32=1;
                    }
                }
                else if ( ((LA32_1>=AT && LA32_1<=MOD_ASSIGN)||(LA32_1>=SEMICOLON && LA32_1<=RIGHT_PAREN)||LA32_1==RIGHT_SQUARE||(LA32_1>=RIGHT_CURLY && LA32_1<=COMMA)||(LA32_1>=DOUBLE_AMPER && LA32_1<=QUESTION)||(LA32_1>=PIPE && LA32_1<=XOR)||LA32_1==ID) ) {
                    alt32=1;
                }
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:305:7: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_orRestriction1518); if (state.failed) return result;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "orRestriction"


    // $ANTLR start "andRestriction"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:1: andRestriction returns [BaseDescr result] : left= singleRestriction ( ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction )* ;
    public final BaseDescr andRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:3: (left= singleRestriction ( ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:5: left= singleRestriction ( ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction )*
            {
            pushFollow(FOLLOW_singleRestriction_in_andRestriction1542);
            left=singleRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:3: ( ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction )*
            loop33:
            do {
                int alt33=2;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:5: ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andRestriction1558); if (state.failed) return result;
            	    pushFollow(FOLLOW_singleRestriction_in_andRestriction1562);
            	    right=singleRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "andRestriction"


    // $ANTLR start "singleRestriction"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:321:1: singleRestriction returns [BaseDescr result] : (op= operator value= shiftExpression | LEFT_PAREN or= orRestriction RIGHT_PAREN );
    public final BaseDescr singleRestriction() throws RecognitionException {
        BaseDescr result = null;

        DRLExpressions.operator_return op = null;

        DRLExpressions.shiftExpression_return value = null;

        BaseDescr or = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:3: (op= operator value= shiftExpression | LEFT_PAREN or= orRestriction RIGHT_PAREN )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( ((LA34_0>=EQUALS && LA34_0<=LESS)) ) {
                alt34=1;
            }
            else if ( (LA34_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt34=1;
            }
            else if ( (LA34_0==LEFT_PAREN) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:6: op= operator value= shiftExpression
                    {
                    pushFollow(FOLLOW_operator_in_singleRestriction1605);
                    op=operator();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_shiftExpression_in_singleRestriction1609);
                    value=shiftExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) {
                                     BaseDescr descr = ( (value!=null?value.result:null) != null && 
                                                       ( (!((value!=null?value.result:null) instanceof AtomicExprDescr)) || 
                                                         ((value!=null?input.toString(value.start,value.stop):null).equals(((AtomicExprDescr)(value!=null?value.result:null)).getExpression())) )) ? 
                      		                    (value!=null?value.result:null) : 
                      		                    new AtomicExprDescr( (value!=null?input.toString(value.start,value.stop):null) ) ;
                                     result = new RelationalExprDescr( (op!=null?op.opr:null), (op!=null?op.negated:false), (op!=null?op.params:null), ((relationalExpression_scope)relationalExpression_stack.peek()).lsd, descr );
                      	       if( ((relationalExpression_scope)relationalExpression_stack.peek()).lsd instanceof BindingDescr ) {
                      	           ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = new AtomicExprDescr( ((BindingDescr)((relationalExpression_scope)relationalExpression_stack.peek()).lsd).getExpression() );
                      	       }
                                 }
                               
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:6: LEFT_PAREN or= orRestriction RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_singleRestriction1627); if (state.failed) return result;
                    pushFollow(FOLLOW_orRestriction_in_singleRestriction1631);
                    or=orRestriction();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_singleRestriction1633); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       result = or; 
                    }

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "singleRestriction"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        public BaseDescr result;
    };

    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:340:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final DRLExpressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRLExpressions.shiftExpression_return retval = new DRLExpressions.shiftExpression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression1669);
            left=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:5: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop35:
            do {
                int alt35=2;
                alt35 = dfa35.predict(input);
                switch (alt35) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:7: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression1683);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression1685);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "shiftExpression"


    // $ANTLR start "shiftOp"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt36=3;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==LESS) ) {
                alt36=1;
            }
            else if ( (LA36_0==GREATER) ) {
                int LA36_2 = input.LA(2);

                if ( (LA36_2==GREATER) ) {
                    int LA36_3 = input.LA(3);

                    if ( (LA36_3==GREATER) ) {
                        alt36=2;
                    }
                    else if ( (LA36_3==EOF||LA36_3==FLOAT||(LA36_3>=HEX && LA36_3<=DECIMAL)||(LA36_3>=STRING && LA36_3<=TIME_INTERVAL)||(LA36_3>=BOOL && LA36_3<=NULL)||(LA36_3>=DECR && LA36_3<=INCR)||LA36_3==LESS||LA36_3==LEFT_PAREN||LA36_3==LEFT_SQUARE||(LA36_3>=NEGATION && LA36_3<=TILDE)||(LA36_3>=MINUS && LA36_3<=PLUS)||LA36_3==ID) ) {
                        alt36=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 36, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 36, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:9: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp1705); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp1707); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:347:11: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1720); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1722); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1724); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:11: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1737); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1739); if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "shiftOp"


    // $ANTLR start "additiveExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:351:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final BaseDescr additiveExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:352:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:352:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1767);
            left=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=MINUS && LA37_0<=PLUS)) && (synpred14_DRLExpressions())) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
            	    {
            	    if ( (input.LA(1)>=MINUS && input.LA(1)<=PLUS) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1796);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:356:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final BaseDescr multiplicativeExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1824);
            left=unaryExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:7: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( ((LA38_0>=MOD && LA38_0<=STAR)||LA38_0==DIV) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:9: ( STAR | DIV | MOD ) unaryExpression
            	    {
            	    if ( (input.LA(1)>=MOD && input.LA(1)<=STAR)||input.LA(1)==DIV ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1850);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop38;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:361:1: unaryExpression returns [BaseDescr result] : ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus );
    public final BaseDescr unaryExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr ue = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return left = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:5: ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus )
            int alt39=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt39=1;
                }
                break;
            case MINUS:
                {
                alt39=2;
                }
                break;
            case INCR:
                {
                alt39=3;
                }
                break;
            case DECR:
                {
                alt39=4;
                }
                break;
            case FLOAT:
            case HEX:
            case DECIMAL:
            case STRING:
            case TIME_INTERVAL:
            case BOOL:
            case NULL:
            case LESS:
            case LEFT_PAREN:
            case LEFT_SQUARE:
            case NEGATION:
            case TILDE:
            case ID:
                {
                alt39=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:9: PLUS ue= unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression1876); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1880);
                    ue=unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { 
                                  result = ue; 
                                  if( result instanceof AtomicExprDescr ) {
                                      ((AtomicExprDescr)result).setExpression( "+" + ((AtomicExprDescr)result).getExpression() );
                                  }
                              } 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:369:7: MINUS ue= unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression1899); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1903);
                    ue=unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { 
                                  result = ue; 
                                  if( result instanceof AtomicExprDescr ) {
                                      ((AtomicExprDescr)result).setExpression( "-" + ((AtomicExprDescr)result).getExpression() );
                                  }
                              } 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:376:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression1924); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression1926);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:377:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression1936); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression1938);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:378:9: left= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1950);
                    left=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { result = (left!=null?left.result:null); } 
                    }

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "unaryExpression"

    public static class unaryExpressionNotPlusMinus_return extends ParserRuleReturnScope {
        public BaseDescr result;
    };

    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:381:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        DRLExpressions.unaryExpressionNotPlusMinus_return retval = new DRLExpressions.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        Token var=null;
        BaseDescr left = null;


         boolean isLeft = false; BindingDescr bind = null;
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt43=4;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1980); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1982);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:384:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1991); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1993);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2007);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:386:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    if ( state.backtracking==0 ) {
                       isLeft = helper.getLeftMostExpr() == null;
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
                    int alt40=3;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==ID) ) {
                        int LA40_1 = input.LA(2);

                        if ( (LA40_1==COLON) ) {
                            int LA40_3 = input.LA(3);

                            if ( ((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON)) ) {
                                alt40=1;
                            }
                        }
                        else if ( (LA40_1==UNIFY) ) {
                            alt40=2;
                        }
                    }
                    switch (alt40) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:11: ({...}? (var= ID COLON ) )
                            {
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:11: ({...}? (var= ID COLON ) )
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:12: {...}? (var= ID COLON )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON");
                            }
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:74: (var= ID COLON )
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:75: var= ID COLON
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2035); if (state.failed) return retval;
                            match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2037); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:11: ({...}? (var= ID UNIFY ) )
                            {
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:11: ({...}? (var= ID UNIFY ) )
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:12: {...}? (var= ID UNIFY )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.UNIFY)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.UNIFY");
                            }
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:74: (var= ID UNIFY )
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:75: var= ID UNIFY
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2077); if (state.failed) return retval;
                            match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2079); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2124);
                    left=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { retval.result = left; } 
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:9: ( ( selector )=> selector )*
                    loop41:
                    do {
                        int alt41=2;
                        int LA41_0 = input.LA(1);

                        if ( (LA41_0==DOT) && (synpred16_DRLExpressions())) {
                            alt41=1;
                        }
                        else if ( (LA41_0==LEFT_SQUARE) && (synpred16_DRLExpressions())) {
                            alt41=1;
                        }


                        switch (alt41) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:10: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2141);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop41;
                        }
                    } while (true);

                    if ( state.backtracking==0 ) {
                           
                                  if( buildDescr ) {
                                      String expr = input.toString(retval.start,input.LT(-1));
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
                                          retval.result = bind;
                                      }
                                  }
                              
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt42=2;
                    int LA42_0 = input.LA(1);

                    if ( ((LA42_0>=DECR && LA42_0<=INCR)) && (synpred17_DRLExpressions())) {
                        alt42=1;
                    }
                    switch (alt42) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:10: ( INCR | DECR )=> ( INCR | DECR )
                            {
                            if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpressionNotPlusMinus"


    // $ANTLR start "castExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:415:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        BaseDescr expr = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFT_PAREN) ) {
                int LA44_1 = input.LA(2);

                if ( (synpred18_DRLExpressions()) ) {
                    alt44=1;
                }
                else if ( (synpred19_DRLExpressions()) ) {
                    alt44=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2209); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression2211);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2213); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression2217);
                    expr=unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2235); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression2237);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2239); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2241);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "castExpression"


    // $ANTLR start "primitiveType"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:420:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt45=8;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:9: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType2264);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:422:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType2272);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:423:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType2280);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:424:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType2288);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:425:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType2296);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:426:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType2304);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:427:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType2312);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:428:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType2320);
                    double_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "primitiveType"


    // $ANTLR start "primary"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:431:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( DOT ID )=> DOT i2= ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final BaseDescr primary() throws RecognitionException {
        BaseDescr result = null;

        Token i1=null;
        Token i2=null;
        Token DOT9=null;
        BaseDescr expr = null;

        DRLExpressions.literal_return literal8 = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( DOT ID )=> DOT i2= ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt50=9;
            alt50 = dfa50.predict(input);
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:7: ( parExpression )=>expr= parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary2348);
                    expr=parExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        if( buildDescr  ) { result = expr; }  
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary2365);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==ID) ) {
                        int LA46_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt46=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt46=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return result;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 46, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return result;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 46, 0, input);

                        throw nvae;
                    }
                    switch (alt46) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary2368);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary2372);
                            this_key();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_arguments_in_primary2374);
                            arguments();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:434:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary2390);
                    literal8=literal();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) { result = new AtomicExprDescr( (literal8!=null?input.toString(literal8.start,literal8.stop):null), true ); }  
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:436:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary2412);
                    super_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_superSuffix_in_primary2414);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:437:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary2430);
                    new_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_creator_in_primary2432);
                    creator();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary2448);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==LEFT_SQUARE) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary2451); if (state.failed) return result;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary2453); if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary2457); if (state.failed) return result;
                    pushFollow(FOLLOW_class_key_in_primary2459);
                    class_key();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:440:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary2480);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:441:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary2496);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:442:9: ( ID )=>i1= ID ( ( DOT ID )=> DOT i2= ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    i1=(Token)match(input,ID,FOLLOW_ID_in_primary2512); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit(i1, DroolsEditorType.IDENTIFIER); 
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:9: ( ( DOT ID )=> DOT i2= ID )*
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==DOT) ) {
                            int LA48_2 = input.LA(2);

                            if ( (LA48_2==ID) ) {
                                int LA48_3 = input.LA(3);

                                if ( (synpred29_DRLExpressions()) ) {
                                    alt48=1;
                                }


                            }


                        }


                        switch (alt48) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:10: ( DOT ID )=> DOT i2= ID
                    	    {
                    	    DOT9=(Token)match(input,DOT,FOLLOW_DOT_in_primary2531); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary2535); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(DOT9, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop48;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:444:12: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt49=2;
                    alt49 = dfa49.predict(input);
                    switch (alt49) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:444:13: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary2555);
                            identifierSuffix();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }


                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "primary"


    // $ANTLR start "inlineListExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:447:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:448:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:448:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression2577); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:448:21: ( expressionList )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==FLOAT||(LA51_0>=HEX && LA51_0<=DECIMAL)||(LA51_0>=STRING && LA51_0<=TIME_INTERVAL)||(LA51_0>=BOOL && LA51_0<=NULL)||(LA51_0>=DECR && LA51_0<=INCR)||LA51_0==LESS||LA51_0==LEFT_PAREN||LA51_0==LEFT_SQUARE||(LA51_0>=NEGATION && LA51_0<=TILDE)||(LA51_0>=MINUS && LA51_0<=PLUS)||LA51_0==ID) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:448:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression2579);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression2582); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "inlineListExpression"


    // $ANTLR start "inlineMapExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:451:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
         inMap++; 
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:453:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:453:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression2608); if (state.failed) return ;
            pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression2610);
            mapExpressionList();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression2612); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
             inMap--; 
        }
        return ;
    }
    // $ANTLR end "inlineMapExpression"


    // $ANTLR start "mapExpressionList"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:457:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:458:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:458:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList2634);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:458:16: ( COMMA mapEntry )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==COMMA) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:458:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList2637); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList2639);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "mapExpressionList"


    // $ANTLR start "mapEntry"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:461:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:462:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:462:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry2662);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry2664); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry2666);
            expression();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "mapEntry"


    // $ANTLR start "parExpression"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:465:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
    public final BaseDescr parExpression() throws RecognitionException {
        BaseDescr result = null;

        DRLExpressions.expression_return expr = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:466:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:466:7: LEFT_PAREN expr= expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression2688); if (state.failed) return result;
            pushFollow(FOLLOW_expression_in_parExpression2692);
            expr=expression();

            state._fsp--;
            if (state.failed) return result;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression2694); if (state.failed) return result;
            if ( state.backtracking==0 ) {
                if( buildDescr  ) { 
                             result = (expr!=null?expr.result:null); 
                             if( result instanceof AtomicExprDescr ) {
                                 ((AtomicExprDescr)result).setExpression("(" +((AtomicExprDescr)result).getExpression() + ")" );
                             } 
                         }  
                      
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "parExpression"


    // $ANTLR start "identifierSuffix"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:476:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        Token LEFT_SQUARE10=null;
        Token RIGHT_SQUARE11=null;
        Token DOT12=null;
        Token LEFT_SQUARE13=null;
        Token RIGHT_SQUARE14=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt55=3;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_SQUARE) ) {
                int LA55_1 = input.LA(2);

                if ( (LA55_1==RIGHT_SQUARE) && (synpred31_DRLExpressions())) {
                    alt55=1;
                }
                else if ( (LA55_1==FLOAT||(LA55_1>=HEX && LA55_1<=DECIMAL)||(LA55_1>=STRING && LA55_1<=TIME_INTERVAL)||(LA55_1>=BOOL && LA55_1<=NULL)||(LA55_1>=DECR && LA55_1<=INCR)||LA55_1==LESS||LA55_1==LEFT_PAREN||LA55_1==LEFT_SQUARE||(LA55_1>=NEGATION && LA55_1<=TILDE)||(LA55_1>=MINUS && LA55_1<=PLUS)||LA55_1==ID) ) {
                    alt55=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA55_0==LEFT_PAREN) ) {
                alt55=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt53=0;
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==LEFT_SQUARE) ) {
                            alt53=1;
                        }


                        switch (alt53) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE10=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix2729); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE10, DroolsEditorType.SYMBOL); 
                    	    }
                    	    RIGHT_SQUARE11=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix2770); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE11, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt53 >= 1 ) break loop53;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(53, input);
                                throw eee;
                        }
                        cnt53++;
                    } while (true);

                    DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix2815); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT12, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_class_key_in_identifierSuffix2819);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:480:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:480:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt54=0;
                    loop54:
                    do {
                        int alt54=2;
                        alt54 = dfa54.predict(input);
                        switch (alt54) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:480:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE13=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix2835); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE13, DroolsEditorType.SYMBOL); 
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix2866);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    RIGHT_SQUARE14=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix2895); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE14, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt54 >= 1 ) break loop54;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(54, input);
                                throw eee;
                        }
                        cnt54++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:483:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix2911);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "identifierSuffix"


    // $ANTLR start "creator"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:491:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:492:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:492:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:492:7: ( nonWildcardTypeArguments )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==LESS) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:492:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator2934);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator2937);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:493:9: ( arrayCreatorRest | classCreatorRest )
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==LEFT_SQUARE) ) {
                alt57=1;
            }
            else if ( (LA57_0==LEFT_PAREN) ) {
                alt57=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:493:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator2948);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:493:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator2952);
                    classCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "creator"


    // $ANTLR start "createdName"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:496:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:497:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA61_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt61=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt61=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:497:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName2970); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:497:10: ( typeArguments )?
                    int alt58=2;
                    int LA58_0 = input.LA(1);

                    if ( (LA58_0==LESS) ) {
                        alt58=1;
                    }
                    switch (alt58) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:497:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName2972);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:498:9: ( DOT ID ( typeArguments )? )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==DOT) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:498:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName2985); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName2987); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:498:18: ( typeArguments )?
                    	    int alt59=2;
                    	    int LA59_0 = input.LA(1);

                    	    if ( (LA59_0==LESS) ) {
                    	        alt59=1;
                    	    }
                    	    switch (alt59) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:498:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName2989);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:499:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName3004);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "createdName"


    // $ANTLR start "innerCreator"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:502:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:503:5: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:503:7: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator3024); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator3026);
            classCreatorRest();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "innerCreator"


    // $ANTLR start "arrayCreatorRest"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:506:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:507:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:507:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3045); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:508:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==RIGHT_SQUARE) ) {
                alt65=1;
            }
            else if ( (LA65_0==FLOAT||(LA65_0>=HEX && LA65_0<=DECIMAL)||(LA65_0>=STRING && LA65_0<=TIME_INTERVAL)||(LA65_0>=BOOL && LA65_0<=NULL)||(LA65_0>=DECR && LA65_0<=INCR)||LA65_0==LESS||LA65_0==LEFT_PAREN||LA65_0==LEFT_SQUARE||(LA65_0>=NEGATION && LA65_0<=TILDE)||(LA65_0>=MINUS && LA65_0<=PLUS)||LA65_0==ID) ) {
                alt65=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:508:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3055); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:508:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==LEFT_SQUARE) ) {
                            alt62=1;
                        }


                        switch (alt62) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:508:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3058); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3060); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop62;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3064);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest3078);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3080); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop63:
                    do {
                        int alt63=2;
                        alt63 = dfa63.predict(input);
                        switch (alt63) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3085); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest3087);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3089); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==LEFT_SQUARE) ) {
                            int LA64_2 = input.LA(2);

                            if ( (LA64_2==RIGHT_SQUARE) && (synpred33_DRLExpressions())) {
                                alt64=1;
                            }


                        }


                        switch (alt64) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3101); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3103); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop64;
                        }
                    } while (true);


                    }
                    break;

            }


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arrayCreatorRest"


    // $ANTLR start "variableInitializer"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:513:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:514:5: ( arrayInitializer | expression )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==LEFT_CURLY) ) {
                alt66=1;
            }
            else if ( (LA66_0==FLOAT||(LA66_0>=HEX && LA66_0<=DECIMAL)||(LA66_0>=STRING && LA66_0<=TIME_INTERVAL)||(LA66_0>=BOOL && LA66_0<=NULL)||(LA66_0>=DECR && LA66_0<=INCR)||LA66_0==LESS||LA66_0==LEFT_PAREN||LA66_0==LEFT_SQUARE||(LA66_0>=NEGATION && LA66_0<=TILDE)||(LA66_0>=MINUS && LA66_0<=PLUS)||LA66_0==ID) ) {
                alt66=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:514:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer3132);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:515:13: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer3146);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "variableInitializer"


    // $ANTLR start "arrayInitializer"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:518:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer3163); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt69=2;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==FLOAT||(LA69_0>=HEX && LA69_0<=DECIMAL)||(LA69_0>=STRING && LA69_0<=TIME_INTERVAL)||(LA69_0>=BOOL && LA69_0<=NULL)||(LA69_0>=DECR && LA69_0<=INCR)||LA69_0==LESS||LA69_0==LEFT_PAREN||LA69_0==LEFT_SQUARE||LA69_0==LEFT_CURLY||(LA69_0>=NEGATION && LA69_0<=TILDE)||(LA69_0>=MINUS && LA69_0<=PLUS)||LA69_0==ID) ) {
                alt69=1;
            }
            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3166);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:39: ( COMMA variableInitializer )*
                    loop67:
                    do {
                        int alt67=2;
                        int LA67_0 = input.LA(1);

                        if ( (LA67_0==COMMA) ) {
                            int LA67_1 = input.LA(2);

                            if ( (LA67_1==FLOAT||(LA67_1>=HEX && LA67_1<=DECIMAL)||(LA67_1>=STRING && LA67_1<=TIME_INTERVAL)||(LA67_1>=BOOL && LA67_1<=NULL)||(LA67_1>=DECR && LA67_1<=INCR)||LA67_1==LESS||LA67_1==LEFT_PAREN||LA67_1==LEFT_SQUARE||LA67_1==LEFT_CURLY||(LA67_1>=NEGATION && LA67_1<=TILDE)||(LA67_1>=MINUS && LA67_1<=PLUS)||LA67_1==ID) ) {
                                alt67=1;
                            }


                        }


                        switch (alt67) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:40: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3169); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3171);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop67;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:68: ( COMMA )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==COMMA) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:519:69: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3176); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer3183); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arrayInitializer"


    // $ANTLR start "classCreatorRest"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:522:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:523:5: ( arguments )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:523:7: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest3200);
            arguments();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "classCreatorRest"


    // $ANTLR start "explicitGenericInvocation"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:526:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:527:5: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:527:7: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3218);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation3220);
            arguments();

            state._fsp--;
            if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "explicitGenericInvocation"


    // $ANTLR start "nonWildcardTypeArguments"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:530:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:531:5: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:531:7: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments3237); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments3239);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments3241); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "nonWildcardTypeArguments"


    // $ANTLR start "explicitGenericInvocationSuffix"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:534:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:535:5: ( super_key superSuffix | ID arguments )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==ID) ) {
                int LA70_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt70=1;
                }
                else if ( (true) ) {
                    alt70=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 70, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:535:7: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix3258);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3260);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:536:10: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix3271); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix3273);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "explicitGenericInvocationSuffix"


    // $ANTLR start "selector"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:539:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        Token DOT15=null;
        Token DOT16=null;
        Token DOT17=null;
        Token ID18=null;
        Token LEFT_SQUARE19=null;
        Token RIGHT_SQUARE20=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:540:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt73=4;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==DOT) ) {
                int LA73_1 = input.LA(2);

                if ( (synpred34_DRLExpressions()) ) {
                    alt73=1;
                }
                else if ( (synpred35_DRLExpressions()) ) {
                    alt73=2;
                }
                else if ( (synpred36_DRLExpressions()) ) {
                    alt73=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 73, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA73_0==LEFT_SQUARE) && (synpred38_DRLExpressions())) {
                alt73=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:540:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    DOT15=(Token)match(input,DOT,FOLLOW_DOT_in_selector3298); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT15, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_super_key_in_selector3302);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector3304);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:541:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    DOT16=(Token)match(input,DOT,FOLLOW_DOT_in_selector3320); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT16, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_new_key_in_selector3324);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:541:84: ( nonWildcardTypeArguments )?
                    int alt71=2;
                    int LA71_0 = input.LA(1);

                    if ( (LA71_0==LESS) ) {
                        alt71=1;
                    }
                    switch (alt71) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:541:85: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector3327);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector3331);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:542:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    DOT17=(Token)match(input,DOT,FOLLOW_DOT_in_selector3347); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT17, DroolsEditorType.SYMBOL); 
                    }
                    ID18=(Token)match(input,ID,FOLLOW_ID_in_selector3370); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(ID18, DroolsEditorType.IDENTIFIER); 
                    }
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:544:19: ( ( LEFT_PAREN )=> arguments )?
                    int alt72=2;
                    alt72 = dfa72.predict(input);
                    switch (alt72) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:544:20: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector3399);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:546:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    LEFT_SQUARE19=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector3420); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(LEFT_SQUARE19, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_expression_in_selector3447);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    RIGHT_SQUARE20=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector3473); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(RIGHT_SQUARE20, DroolsEditorType.SYMBOL); 
                    }

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "selector"


    // $ANTLR start "superSuffix"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:551:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:552:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==LEFT_PAREN) ) {
                alt75=1;
            }
            else if ( (LA75_0==DOT) ) {
                alt75=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:552:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix3492);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:553:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix3503); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix3505); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:553:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt74=2;
                    alt74 = dfa74.predict(input);
                    switch (alt74) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:553:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix3514);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "superSuffix"


    // $ANTLR start "squareArguments"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:556:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
    public final java.util.List<String> squareArguments() throws RecognitionException {
        java.util.List<String> args = null;

        java.util.List<String> el = null;


        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:557:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:557:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments3537); if (state.failed) return args;
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:557:19: (el= expressionList )?
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==FLOAT||(LA76_0>=HEX && LA76_0<=DECIMAL)||(LA76_0>=STRING && LA76_0<=TIME_INTERVAL)||(LA76_0>=BOOL && LA76_0<=NULL)||(LA76_0>=DECR && LA76_0<=INCR)||LA76_0==LESS||LA76_0==LEFT_PAREN||LA76_0==LEFT_SQUARE||(LA76_0>=NEGATION && LA76_0<=TILDE)||(LA76_0>=MINUS && LA76_0<=PLUS)||LA76_0==ID) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:557:20: el= expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments3542);
                    el=expressionList();

                    state._fsp--;
                    if (state.failed) return args;
                    if ( state.backtracking==0 ) {
                       args = el; 
                    }

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments3548); if (state.failed) return args;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return args;
    }
    // $ANTLR end "squareArguments"


    // $ANTLR start "arguments"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:560:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        Token LEFT_PAREN21=null;
        Token RIGHT_PAREN22=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:561:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:561:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            LEFT_PAREN21=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments3565); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(LEFT_PAREN21, DroolsEditorType.SYMBOL); 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:562:9: ( expressionList )?
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==FLOAT||(LA77_0>=HEX && LA77_0<=DECIMAL)||(LA77_0>=STRING && LA77_0<=TIME_INTERVAL)||(LA77_0>=BOOL && LA77_0<=NULL)||(LA77_0>=DECR && LA77_0<=INCR)||LA77_0==LESS||LA77_0==LEFT_PAREN||LA77_0==LEFT_SQUARE||(LA77_0>=NEGATION && LA77_0<=TILDE)||(LA77_0>=MINUS && LA77_0<=PLUS)||LA77_0==ID) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:562:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments3577);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            RIGHT_PAREN22=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments3589); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(RIGHT_PAREN22, DroolsEditorType.SYMBOL); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "arguments"


    // $ANTLR start "expressionList"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:566:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
    public final java.util.List<String> expressionList() throws RecognitionException {
        java.util.List<String> exprs = null;

        DRLExpressions.expression_return f = null;

        DRLExpressions.expression_return s = null;


         exprs = new java.util.ArrayList<String>();
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:568:3: (f= expression ( COMMA s= expression )* )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:568:7: f= expression ( COMMA s= expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList3619);
            f=expression();

            state._fsp--;
            if (state.failed) return exprs;
            if ( state.backtracking==0 ) {
               exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); 
            }
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:569:7: ( COMMA s= expression )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==COMMA) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:569:8: COMMA s= expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList3630); if (state.failed) return exprs;
            	    pushFollow(FOLLOW_expression_in_expressionList3634);
            	    s=expression();

            	    state._fsp--;
            	    if (state.failed) return exprs;
            	    if ( state.backtracking==0 ) {
            	       exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
                }
            } while (true);


            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return exprs;
    }
    // $ANTLR end "expressionList"


    // $ANTLR start "assignmentOperator"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:572:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:573:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt79=12;
            alt79 = dfa79.predict(input);
            switch (alt79) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:573:9: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3655); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:574:7: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator3663); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:575:7: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator3671); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:576:7: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator3679); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:577:7: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator3687); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:578:7: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator3695); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:579:7: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator3703); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:580:7: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator3711); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:581:7: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator3719); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:582:7: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator3727); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator3729); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3731); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:583:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator3749); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator3751); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator3753); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3755); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:584:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator3771); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator3773); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3775); if (state.failed) return ;

                    }
                    break;

            }
        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "assignmentOperator"


    // $ANTLR start "extends_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:590:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:591:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:591:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key3807); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "extends_key"


    // $ANTLR start "super_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:594:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:595:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:595:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key3837); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "super_key"

    public static class instanceof_key_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "instanceof_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:598:1: instanceof_key : {...}? =>id= ID ;
    public final DRLExpressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRLExpressions.instanceof_key_return retval = new DRLExpressions.instanceof_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:599:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:599:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key3867); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "instanceof_key"


    // $ANTLR start "boolean_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:602:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:603:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:603:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key3897); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "boolean_key"


    // $ANTLR start "char_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:606:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:607:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:607:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key3927); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "char_key"


    // $ANTLR start "byte_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:610:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:611:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:611:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key3957); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "byte_key"


    // $ANTLR start "short_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:614:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:615:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:615:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key3987); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "short_key"


    // $ANTLR start "int_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:618:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:619:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:619:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key4017); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "int_key"


    // $ANTLR start "float_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:622:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:623:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:623:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key4047); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "float_key"


    // $ANTLR start "long_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:626:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:627:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:627:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key4077); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "long_key"


    // $ANTLR start "double_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:630:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:631:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:631:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key4107); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "double_key"


    // $ANTLR start "void_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:634:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:635:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:635:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key4137); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "void_key"


    // $ANTLR start "this_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:638:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:639:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:639:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key4167); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "this_key"


    // $ANTLR start "class_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:642:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:643:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:643:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key4197); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "class_key"


    // $ANTLR start "new_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:646:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:647:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:647:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key4228); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "new_key"


    // $ANTLR start "not_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:650:1: not_key : {...}? =>id= ID ;
    public final void not_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:651:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:651:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key4258); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "not_key"


    // $ANTLR start "in_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:654:1: in_key : {...}? =>id= ID ;
    public final void in_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:655:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:655:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key4286); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "in_key"

    public static class operator_key_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "operator_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:658:1: operator_key : {...}? =>id= ID ;
    public final DRLExpressions.operator_key_return operator_key() throws RecognitionException {
        DRLExpressions.operator_key_return retval = new DRLExpressions.operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:659:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:659:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4312); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "operator_key"

    public static class neg_operator_key_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "neg_operator_key"
    // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:662:1: neg_operator_key : {...}? =>id= ID ;
    public final DRLExpressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLExpressions.neg_operator_key_return retval = new DRLExpressions.neg_operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:663:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:663:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4338); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               helper.emit(id, DroolsEditorType.KEYWORD); 
            }

            }

            retval.stop = input.LT(-1);

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "neg_operator_key"

    // $ANTLR start synpred1_DRLExpressions
    public final void synpred1_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:8: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:99:9: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred1_DRLExpressions386);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:8: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:9: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred2_DRLExpressions420);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:8: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred3_DRLExpressions510);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred4_DRLExpressions521); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred4_DRLExpressions523); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:13: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred5_DRLExpressions547);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:55: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred6_DRLExpressions561);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred7_DRLExpressions573); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred7_DRLExpressions575); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:10: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:11: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred8_DRLExpressions756);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:6: ( not_key in_key )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:7: not_key in_key
        {
        pushFollow(FOLLOW_not_key_in_synpred9_DRLExpressions1263);
        not_key();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_in_key_in_synpred9_DRLExpressions1265);
        in_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:5: ( orRestriction )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:6: orRestriction
        {
        pushFollow(FOLLOW_orRestriction_in_synpred10_DRLExpressions1435);
        orRestriction();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:7: ( DOUBLE_PIPE andRestriction )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:8: DOUBLE_PIPE andRestriction
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred11_DRLExpressions1487); if (state.failed) return ;
        pushFollow(FOLLOW_andRestriction_in_synpred11_DRLExpressions1489);
        andRestriction();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:5: ( DOUBLE_AMPER singleRestriction )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:6: DOUBLE_AMPER singleRestriction
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred12_DRLExpressions1551); if (state.failed) return ;
        pushFollow(FOLLOW_singleRestriction_in_synpred12_DRLExpressions1553);
        singleRestriction();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:7: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:8: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred13_DRLExpressions1680);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:11: ( PLUS | MINUS )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
        if ( (input.LA(1)>=MINUS && input.LA(1)<=PLUS) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred15_DRLExpressions2004);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:10: ( selector )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:11: selector
        {
        pushFollow(FOLLOW_selector_in_synpred16_DRLExpressions2138);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:10: ( INCR | DECR )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
        {
        if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred18_DRLExpressions2202); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred18_DRLExpressions2204);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred19_DRLExpressions2228); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred19_DRLExpressions2230);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred20_DRLExpressions2342);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:433:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred21_DRLExpressions2361);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:434:9: ( literal )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:434:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred22_DRLExpressions2386);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:436:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:436:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred23_DRLExpressions2408);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:437:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:437:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred24_DRLExpressions2426);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred25_DRLExpressions2444);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:440:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:440:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred26_DRLExpressions2476);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:441:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:441:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred27_DRLExpressions2492);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:442:9: ( ID )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:442:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred28_DRLExpressions2507); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:10: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:11: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred29_DRLExpressions2526); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred29_DRLExpressions2528); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:444:13: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:444:14: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred30_DRLExpressions2552);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:477:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2723); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2725); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:480:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:480:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred32_DRLExpressions2830); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:509:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred33_DRLExpressions3095); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred33_DRLExpressions3097); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:540:9: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:540:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred34_DRLExpressions3293); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred34_DRLExpressions3295);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:541:9: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:541:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred35_DRLExpressions3315); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred35_DRLExpressions3317);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:542:9: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:542:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred36_DRLExpressions3342); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred36_DRLExpressions3344); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:544:20: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:544:21: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions3394); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:546:9: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:546:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred38_DRLExpressions3417); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:553:18: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:553:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions3509); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DRLExpressions

    // $ANTLR start synpred40_DRLExpressions
    public final void synpred40_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:583:7: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:583:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred40_DRLExpressions3741); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred40_DRLExpressions3743); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred40_DRLExpressions3745); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_DRLExpressions

    // $ANTLR start synpred41_DRLExpressions
    public final void synpred41_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:584:7: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:584:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred41_DRLExpressions3765); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred41_DRLExpressions3767); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_DRLExpressions

    // Delegated rules

    public final boolean synpred13_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred23_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred30_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred30_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred33_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred33_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred22_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred21_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred35_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred35_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred28_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred28_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred32_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred32_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred29_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred29_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred41_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred37_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred37_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred38_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred38_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred31_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred31_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred39_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_DRLExpressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA3 dfa3 = new DFA3(this);
    protected DFA4 dfa4 = new DFA4(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA50 dfa50 = new DFA50(this);
    protected DFA49 dfa49 = new DFA49(this);
    protected DFA54 dfa54 = new DFA54(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA72 dfa72 = new DFA72(this);
    protected DFA74 dfa74 = new DFA74(this);
    protected DFA79 dfa79 = new DFA79(this);
    static final String DFA3_eotS =
        "\23\uffff";
    static final String DFA3_eofS =
        "\23\uffff";
    static final String DFA3_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA3_maxS =
        "\1\103\1\0\21\uffff";
    static final String DFA3_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA3_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\2\2\2\uffff\2\2\12\uffff\2\2\10\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\1\1\10\uffff\2\2\5\uffff\2\2\5\uffff"+
            "\1\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "99:7: ( ( squareArguments )=>sa= squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA3_1 = input.LA(1);

                         
                        int index3_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index3_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 3, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA4_eotS =
        "\23\uffff";
    static final String DFA4_eofS =
        "\23\uffff";
    static final String DFA4_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA4_maxS =
        "\1\103\1\0\21\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\2\2\2\uffff\2\2\12\uffff\2\2\10\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\1\1\10\uffff\2\2\5\uffff\2\2\5\uffff"+
            "\1\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "101:7: ( ( squareArguments )=>sa= squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA4_1 = input.LA(1);

                         
                        int index4_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index4_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 4, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA17_eotS =
        "\16\uffff";
    static final String DFA17_eofS =
        "\16\uffff";
    static final String DFA17_minS =
        "\1\24\13\0\2\uffff";
    static final String DFA17_maxS =
        "\1\103\13\0\2\uffff";
    static final String DFA17_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA17_specialS =
        "\1\uffff\1\10\1\3\1\4\1\0\1\1\1\11\1\12\1\6\1\7\1\5\1\2\2\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\14\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\4\uffff\2\14\4\uffff"+
            "\1\13\1\12\1\1\1\uffff\1\14\1\uffff\1\14\1\uffff\2\14\22\uffff"+
            "\1\14",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "141:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA17_4 = input.LA(1);

                         
                        int index17_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA17_5 = input.LA(1);

                         
                        int index17_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA17_11 = input.LA(1);

                         
                        int index17_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_11);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA17_2 = input.LA(1);

                         
                        int index17_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_2);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA17_3 = input.LA(1);

                         
                        int index17_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_3);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA17_10 = input.LA(1);

                         
                        int index17_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_10);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA17_8 = input.LA(1);

                         
                        int index17_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_8);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA17_9 = input.LA(1);

                         
                        int index17_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_9);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA17_1 = input.LA(1);

                         
                        int index17_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_1);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA17_6 = input.LA(1);

                         
                        int index17_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_6);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA17_7 = input.LA(1);

                         
                        int index17_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index17_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 17, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA30_eotS =
        "\37\uffff";
    static final String DFA30_eofS =
        "\1\1\36\uffff";
    static final String DFA30_minS =
        "\1\24\1\uffff\3\0\17\uffff\2\0\11\uffff";
    static final String DFA30_maxS =
        "\1\103\1\uffff\3\0\17\uffff\2\0\11\uffff";
    static final String DFA30_acceptS =
        "\1\uffff\1\2\32\uffff\3\1";
    static final String DFA30_specialS =
        "\1\0\1\uffff\1\1\1\2\1\3\17\uffff\1\4\1\5\11\uffff}>";
    static final String[] DFA30_transitionS = {
            "\11\1\4\uffff\2\1\1\3\1\4\1\35\1\34\1\25\1\24\1\1\1\36\1\1\1"+
            "\uffff\1\1\1\uffff\2\1\1\uffff\3\1\2\uffff\3\1\11\uffff\1\2",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA30_eot = DFA.unpackEncodedString(DFA30_eotS);
    static final short[] DFA30_eof = DFA.unpackEncodedString(DFA30_eofS);
    static final char[] DFA30_min = DFA.unpackEncodedStringToUnsignedChars(DFA30_minS);
    static final char[] DFA30_max = DFA.unpackEncodedStringToUnsignedChars(DFA30_maxS);
    static final short[] DFA30_accept = DFA.unpackEncodedString(DFA30_acceptS);
    static final short[] DFA30_special = DFA.unpackEncodedString(DFA30_specialS);
    static final short[][] DFA30_transition;

    static {
        int numStates = DFA30_transitionS.length;
        DFA30_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA30_transition[i] = DFA.unpackEncodedString(DFA30_transitionS[i]);
        }
    }

    class DFA30 extends DFA {

        public DFA30(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 30;
            this.eot = DFA30_eot;
            this.eof = DFA30_eof;
            this.min = DFA30_min;
            this.max = DFA30_max;
            this.accept = DFA30_accept;
            this.special = DFA30_special;
            this.transition = DFA30_transition;
        }
        public String getDescription() {
            return "()* loopback of 287:3: ( ( orRestriction )=>right= orRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_0 = input.LA(1);

                         
                        int index30_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA30_0==EOF||(LA30_0>=AT && LA30_0<=MOD_ASSIGN)||(LA30_0>=SEMICOLON && LA30_0<=COLON)||LA30_0==EQUALS_ASSIGN||LA30_0==RIGHT_PAREN||LA30_0==RIGHT_SQUARE||(LA30_0>=RIGHT_CURLY && LA30_0<=COMMA)||(LA30_0>=DOUBLE_AMPER && LA30_0<=QUESTION)||(LA30_0>=PIPE && LA30_0<=XOR)) ) {s = 1;}

                        else if ( (LA30_0==ID) ) {s = 2;}

                        else if ( (LA30_0==EQUALS) ) {s = 3;}

                        else if ( (LA30_0==NOT_EQUALS) ) {s = 4;}

                        else if ( (LA30_0==LESS) ) {s = 20;}

                        else if ( (LA30_0==GREATER) ) {s = 21;}

                        else if ( (LA30_0==LESS_EQUALS) && (synpred10_DRLExpressions())) {s = 28;}

                        else if ( (LA30_0==GREATER_EQUALS) && (synpred10_DRLExpressions())) {s = 29;}

                        else if ( (LA30_0==LEFT_PAREN) && (synpred10_DRLExpressions())) {s = 30;}

                         
                        input.seek(index30_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA30_2 = input.LA(1);

                         
                        int index30_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred10_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT))))||(synpred10_DRLExpressions()&&((helper.isPluggableEvaluator(false)))))) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA30_3 = input.LA(1);

                         
                        int index30_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA30_4 = input.LA(1);

                         
                        int index30_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA30_20 = input.LA(1);

                         
                        int index30_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_20);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA30_21 = input.LA(1);

                         
                        int index30_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index30_21);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 30, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA31_eotS =
        "\40\uffff";
    static final String DFA31_eofS =
        "\1\1\37\uffff";
    static final String DFA31_minS =
        "\1\24\10\uffff\1\0\26\uffff";
    static final String DFA31_maxS =
        "\1\103\10\uffff\1\0\26\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\1\2\35\uffff\1\1";
    static final String DFA31_specialS =
        "\11\uffff\1\0\26\uffff}>";
    static final String[] DFA31_transitionS = {
            "\11\1\4\uffff\13\1\1\uffff\1\1\1\uffff\2\1\1\uffff\1\1\1\11"+
            "\1\1\2\uffff\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "()* loopback of 297:5: ( ( DOUBLE_PIPE andRestriction )=>lop= DOUBLE_PIPE right= andRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_9 = input.LA(1);

                         
                        int index31_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index31_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA33_eotS =
        "\40\uffff";
    static final String DFA33_eofS =
        "\1\1\37\uffff";
    static final String DFA33_minS =
        "\1\24\10\uffff\1\0\26\uffff";
    static final String DFA33_maxS =
        "\1\103\10\uffff\1\0\26\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\2\35\uffff\1\1";
    static final String DFA33_specialS =
        "\11\uffff\1\0\26\uffff}>";
    static final String[] DFA33_transitionS = {
            "\11\1\4\uffff\13\1\1\uffff\1\1\1\uffff\2\1\1\uffff\1\11\2\1"+
            "\2\uffff\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "()* loopback of 310:3: ( ( DOUBLE_AMPER singleRestriction )=>lop= DOUBLE_AMPER right= singleRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_9 = input.LA(1);

                         
                        int index33_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index33_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 33, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA35_eotS =
        "\41\uffff";
    static final String DFA35_eofS =
        "\1\1\40\uffff";
    static final String DFA35_minS =
        "\1\24\4\uffff\2\0\32\uffff";
    static final String DFA35_maxS =
        "\1\103\4\uffff\2\0\32\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA35_specialS =
        "\5\uffff\1\0\1\1\32\uffff}>";
    static final String[] DFA35_transitionS = {
            "\11\1\4\uffff\6\1\1\6\1\5\3\1\1\uffff\1\1\1\uffff\2\1\1\uffff"+
            "\3\1\2\uffff\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "()* loopback of 342:5: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA35_5 = input.LA(1);

                         
                        int index35_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_DRLExpressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA35_6 = input.LA(1);

                         
                        int index35_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_DRLExpressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 35, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA43_eotS =
        "\17\uffff";
    static final String DFA43_eofS =
        "\17\uffff";
    static final String DFA43_minS =
        "\1\10\2\uffff\1\0\13\uffff";
    static final String DFA43_maxS =
        "\1\103\2\uffff\1\0\13\uffff";
    static final String DFA43_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\11\uffff\1\3";
    static final String DFA43_specialS =
        "\3\uffff\1\0\13\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\4\2\uffff\2\4\1\uffff\2\4\2\uffff\2\4\24\uffff\1\4\1\uffff"+
            "\1\3\1\uffff\1\4\10\uffff\1\2\1\1\14\uffff\1\4",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA43_eot = DFA.unpackEncodedString(DFA43_eotS);
    static final short[] DFA43_eof = DFA.unpackEncodedString(DFA43_eofS);
    static final char[] DFA43_min = DFA.unpackEncodedStringToUnsignedChars(DFA43_minS);
    static final char[] DFA43_max = DFA.unpackEncodedStringToUnsignedChars(DFA43_maxS);
    static final short[] DFA43_accept = DFA.unpackEncodedString(DFA43_acceptS);
    static final short[] DFA43_special = DFA.unpackEncodedString(DFA43_specialS);
    static final short[][] DFA43_transition;

    static {
        int numStates = DFA43_transitionS.length;
        DFA43_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA43_transition[i] = DFA.unpackEncodedString(DFA43_transitionS[i]);
        }
    }

    class DFA43 extends DFA {

        public DFA43(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 43;
            this.eot = DFA43_eot;
            this.eof = DFA43_eof;
            this.min = DFA43_min;
            this.max = DFA43_max;
            this.accept = DFA43_accept;
            this.special = DFA43_special;
            this.transition = DFA43_transition;
        }
        public String getDescription() {
            return "381:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA43_3 = input.LA(1);

                         
                        int index43_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_DRLExpressions()) ) {s = 14;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index43_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 43, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA45_eotS =
        "\12\uffff";
    static final String DFA45_eofS =
        "\12\uffff";
    static final String DFA45_minS =
        "\1\103\1\0\10\uffff";
    static final String DFA45_maxS =
        "\1\103\1\0\10\uffff";
    static final String DFA45_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA45_specialS =
        "\1\0\1\1\10\uffff}>";
    static final String[] DFA45_transitionS = {
            "\1\1",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        public String getDescription() {
            return "420:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA45_0 = input.LA(1);

                         
                        int index45_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA45_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index45_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA45_1 = input.LA(1);

                         
                        int index45_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {s = 2;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {s = 3;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {s = 4;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {s = 5;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {s = 6;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {s = 7;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {s = 8;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {s = 9;}

                         
                        input.seek(index45_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 45, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA50_eotS =
        "\22\uffff";
    static final String DFA50_eofS =
        "\22\uffff";
    static final String DFA50_minS =
        "\1\10\11\uffff\2\0\6\uffff";
    static final String DFA50_maxS =
        "\1\103\11\uffff\2\0\6\uffff";
    static final String DFA50_acceptS =
        "\1\uffff\1\1\1\2\7\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA50_specialS =
        "\1\0\11\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA50_transitionS = {
            "\1\6\2\uffff\1\5\1\4\1\uffff\1\3\1\11\2\uffff\1\7\1\10\24\uffff"+
            "\1\2\1\uffff\1\1\1\uffff\1\13\26\uffff\1\12",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA50_eot = DFA.unpackEncodedString(DFA50_eotS);
    static final short[] DFA50_eof = DFA.unpackEncodedString(DFA50_eofS);
    static final char[] DFA50_min = DFA.unpackEncodedStringToUnsignedChars(DFA50_minS);
    static final char[] DFA50_max = DFA.unpackEncodedStringToUnsignedChars(DFA50_maxS);
    static final short[] DFA50_accept = DFA.unpackEncodedString(DFA50_acceptS);
    static final short[] DFA50_special = DFA.unpackEncodedString(DFA50_specialS);
    static final short[][] DFA50_transition;

    static {
        int numStates = DFA50_transitionS.length;
        DFA50_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA50_transition[i] = DFA.unpackEncodedString(DFA50_transitionS[i]);
        }
    }

    class DFA50 extends DFA {

        public DFA50(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 50;
            this.eot = DFA50_eot;
            this.eof = DFA50_eof;
            this.min = DFA50_min;
            this.max = DFA50_max;
            this.accept = DFA50_accept;
            this.special = DFA50_special;
            this.transition = DFA50_transition;
        }
        public String getDescription() {
            return "431:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( DOT ID )=> DOT i2= ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA50_0 = input.LA(1);

                         
                        int index50_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA50_0==LEFT_PAREN) && (synpred20_DRLExpressions())) {s = 1;}

                        else if ( (LA50_0==LESS) && (synpred21_DRLExpressions())) {s = 2;}

                        else if ( (LA50_0==STRING) && (synpred22_DRLExpressions())) {s = 3;}

                        else if ( (LA50_0==DECIMAL) && (synpred22_DRLExpressions())) {s = 4;}

                        else if ( (LA50_0==HEX) && (synpred22_DRLExpressions())) {s = 5;}

                        else if ( (LA50_0==FLOAT) && (synpred22_DRLExpressions())) {s = 6;}

                        else if ( (LA50_0==BOOL) && (synpred22_DRLExpressions())) {s = 7;}

                        else if ( (LA50_0==NULL) && (synpred22_DRLExpressions())) {s = 8;}

                        else if ( (LA50_0==TIME_INTERVAL) && (synpred22_DRLExpressions())) {s = 9;}

                        else if ( (LA50_0==ID) ) {s = 10;}

                        else if ( (LA50_0==LEFT_SQUARE) ) {s = 11;}

                         
                        input.seek(index50_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA50_10 = input.LA(1);

                         
                        int index50_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 12;}

                        else if ( ((synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 13;}

                        else if ( (((synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred25_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))))) ) {s = 14;}

                        else if ( (synpred28_DRLExpressions()) ) {s = 15;}

                         
                        input.seek(index50_10);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA50_11 = input.LA(1);

                         
                        int index50_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_DRLExpressions()) ) {s = 16;}

                        else if ( (synpred27_DRLExpressions()) ) {s = 17;}

                         
                        input.seek(index50_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 50, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA49_eotS =
        "\46\uffff";
    static final String DFA49_eofS =
        "\1\3\45\uffff";
    static final String DFA49_minS =
        "\1\24\2\0\43\uffff";
    static final String DFA49_maxS =
        "\1\104\2\0\43\uffff";
    static final String DFA49_acceptS =
        "\3\uffff\1\2\41\uffff\1\1";
    static final String DFA49_specialS =
        "\1\uffff\1\0\1\1\43\uffff}>";
    static final String[] DFA49_transitionS = {
            "\11\3\1\uffff\2\3\1\uffff\11\3\1\2\1\3\1\1\1\3\1\uffff\6\3\2"+
            "\uffff\7\3\5\uffff\2\3",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "444:12: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA49_1 = input.LA(1);

                         
                        int index49_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred30_DRLExpressions()) ) {s = 37;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA49_2 = input.LA(1);

                         
                        int index49_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred30_DRLExpressions()) ) {s = 37;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 49, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA54_eotS =
        "\46\uffff";
    static final String DFA54_eofS =
        "\1\1\45\uffff";
    static final String DFA54_minS =
        "\1\24\42\uffff\1\0\2\uffff";
    static final String DFA54_maxS =
        "\1\104\42\uffff\1\0\2\uffff";
    static final String DFA54_acceptS =
        "\1\uffff\1\2\43\uffff\1\1";
    static final String DFA54_specialS =
        "\43\uffff\1\0\2\uffff}>";
    static final String[] DFA54_transitionS = {
            "\11\1\1\uffff\2\1\1\uffff\13\1\1\43\1\1\1\uffff\6\1\2\uffff"+
            "\7\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA54_eot = DFA.unpackEncodedString(DFA54_eotS);
    static final short[] DFA54_eof = DFA.unpackEncodedString(DFA54_eofS);
    static final char[] DFA54_min = DFA.unpackEncodedStringToUnsignedChars(DFA54_minS);
    static final char[] DFA54_max = DFA.unpackEncodedStringToUnsignedChars(DFA54_maxS);
    static final short[] DFA54_accept = DFA.unpackEncodedString(DFA54_acceptS);
    static final short[] DFA54_special = DFA.unpackEncodedString(DFA54_specialS);
    static final short[][] DFA54_transition;

    static {
        int numStates = DFA54_transitionS.length;
        DFA54_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA54_transition[i] = DFA.unpackEncodedString(DFA54_transitionS[i]);
        }
    }

    class DFA54 extends DFA {

        public DFA54(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 54;
            this.eot = DFA54_eot;
            this.eof = DFA54_eof;
            this.min = DFA54_min;
            this.max = DFA54_max;
            this.accept = DFA54_accept;
            this.special = DFA54_special;
            this.transition = DFA54_transition;
        }
        public String getDescription() {
            return "()+ loopback of 480:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA54_35 = input.LA(1);

                         
                        int index54_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred32_DRLExpressions()) ) {s = 37;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index54_35);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 54, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA63_eotS =
        "\46\uffff";
    static final String DFA63_eofS =
        "\1\2\45\uffff";
    static final String DFA63_minS =
        "\1\24\1\0\44\uffff";
    static final String DFA63_maxS =
        "\1\104\1\0\44\uffff";
    static final String DFA63_acceptS =
        "\2\uffff\1\2\42\uffff\1\1";
    static final String DFA63_specialS =
        "\1\uffff\1\0\44\uffff}>";
    static final String[] DFA63_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\13\2\1\1\1\2\1\uffff\6\2\2\uffff\7"+
            "\2\5\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "()* loopback of 509:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_1 = input.LA(1);

                         
                        int index63_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 37;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index63_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA72_eotS =
        "\46\uffff";
    static final String DFA72_eofS =
        "\1\2\45\uffff";
    static final String DFA72_minS =
        "\1\24\1\0\44\uffff";
    static final String DFA72_maxS =
        "\1\104\1\0\44\uffff";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\42\uffff\1\1";
    static final String DFA72_specialS =
        "\1\uffff\1\0\44\uffff}>";
    static final String[] DFA72_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\11\2\1\1\3\2\1\uffff\6\2\2\uffff\7"+
            "\2\5\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
    static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
    static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
    static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
    static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
    static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
    static final short[][] DFA72_transition;

    static {
        int numStates = DFA72_transitionS.length;
        DFA72_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
        }
    }

    class DFA72 extends DFA {

        public DFA72(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 72;
            this.eot = DFA72_eot;
            this.eof = DFA72_eof;
            this.min = DFA72_min;
            this.max = DFA72_max;
            this.accept = DFA72_accept;
            this.special = DFA72_special;
            this.transition = DFA72_transition;
        }
        public String getDescription() {
            return "544:19: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA72_1 = input.LA(1);

                         
                        int index72_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred37_DRLExpressions()) ) {s = 37;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index72_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 72, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA74_eotS =
        "\46\uffff";
    static final String DFA74_eofS =
        "\1\2\45\uffff";
    static final String DFA74_minS =
        "\1\24\1\0\44\uffff";
    static final String DFA74_maxS =
        "\1\104\1\0\44\uffff";
    static final String DFA74_acceptS =
        "\2\uffff\1\2\42\uffff\1\1";
    static final String DFA74_specialS =
        "\1\uffff\1\0\44\uffff}>";
    static final String[] DFA74_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\11\2\1\1\3\2\1\uffff\6\2\2\uffff\7"+
            "\2\5\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA74_eot = DFA.unpackEncodedString(DFA74_eotS);
    static final short[] DFA74_eof = DFA.unpackEncodedString(DFA74_eofS);
    static final char[] DFA74_min = DFA.unpackEncodedStringToUnsignedChars(DFA74_minS);
    static final char[] DFA74_max = DFA.unpackEncodedStringToUnsignedChars(DFA74_maxS);
    static final short[] DFA74_accept = DFA.unpackEncodedString(DFA74_acceptS);
    static final short[] DFA74_special = DFA.unpackEncodedString(DFA74_specialS);
    static final short[][] DFA74_transition;

    static {
        int numStates = DFA74_transitionS.length;
        DFA74_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA74_transition[i] = DFA.unpackEncodedString(DFA74_transitionS[i]);
        }
    }

    class DFA74 extends DFA {

        public DFA74(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 74;
            this.eot = DFA74_eot;
            this.eof = DFA74_eof;
            this.min = DFA74_min;
            this.max = DFA74_max;
            this.accept = DFA74_accept;
            this.special = DFA74_special;
            this.transition = DFA74_transition;
        }
        public String getDescription() {
            return "553:17: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA74_1 = input.LA(1);

                         
                        int index74_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred39_DRLExpressions()) ) {s = 37;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index74_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 74, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA79_eotS =
        "\17\uffff";
    static final String DFA79_eofS =
        "\17\uffff";
    static final String DFA79_minS =
        "\1\25\12\uffff\2\47\2\uffff";
    static final String DFA79_maxS =
        "\1\51\12\uffff\1\47\1\51\2\uffff";
    static final String DFA79_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA79_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA79_transitionS = {
            "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\12\uffff\1\13\1\12\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\15\1\uffff\1\16",
            "",
            ""
    };

    static final short[] DFA79_eot = DFA.unpackEncodedString(DFA79_eotS);
    static final short[] DFA79_eof = DFA.unpackEncodedString(DFA79_eofS);
    static final char[] DFA79_min = DFA.unpackEncodedStringToUnsignedChars(DFA79_minS);
    static final char[] DFA79_max = DFA.unpackEncodedStringToUnsignedChars(DFA79_maxS);
    static final short[] DFA79_accept = DFA.unpackEncodedString(DFA79_acceptS);
    static final short[] DFA79_special = DFA.unpackEncodedString(DFA79_specialS);
    static final short[][] DFA79_transition;

    static {
        int numStates = DFA79_transitionS.length;
        DFA79_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA79_transition[i] = DFA.unpackEncodedString(DFA79_transitionS[i]);
        }
    }

    class DFA79 extends DFA {

        public DFA79(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 79;
            this.eot = DFA79_eot;
            this.eof = DFA79_eof;
            this.min = DFA79_min;
            this.max = DFA79_max;
            this.accept = DFA79_accept;
            this.special = DFA79_special;
            this.transition = DFA79_transition;
        }
        public String getDescription() {
            return "572:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA79_12 = input.LA(1);

                         
                        int index79_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA79_12==GREATER) && (synpred40_DRLExpressions())) {s = 13;}

                        else if ( (LA79_12==EQUALS_ASSIGN) && (synpred41_DRLExpressions())) {s = 14;}

                         
                        input.seek(index79_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 79, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_INTERVAL_in_literal194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_operator227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp370 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp374 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp407 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList459 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList462 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_type_in_typeList464 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_typeMatch_in_type486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_typeMatch517 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch527 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch529 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_ID_in_typeMatch543 = new BitSet(new long[]{0x0002110000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch550 = new BitSet(new long[]{0x0002100000000002L});
    public static final BitSet FOLLOW_DOT_in_typeMatch555 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_typeMatch557 = new BitSet(new long[]{0x0002110000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch564 = new BitSet(new long[]{0x0002100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch579 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch581 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments606 = new BitSet(new long[]{0x0010000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments608 = new BitSet(new long[]{0x0001008000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments611 = new BitSet(new long[]{0x0010000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments613 = new BitSet(new long[]{0x0001008000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument642 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument646 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_super_key_in_typeArgument650 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_type_in_typeArgument653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy677 = new BitSet(new long[]{0x0000080200100000L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_dummy679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_dummy2717 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_dummy2719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression742 = new BitSet(new long[]{0x000003801FE00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression763 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_expression767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression794 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ternaryExpression833 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression837 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_ternaryExpression839 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression868 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression877 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression881 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression918 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression926 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression930 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression966 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression974 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression978 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1014 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1022 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1026 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1064 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression1072 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1076 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1116 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression1128 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1134 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1151 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1187 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression1212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression1253 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_not_key_in_inExpression1269 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_in_key_in_inExpression1273 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1275 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_inExpression1279 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1299 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_inExpression1303 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression1334 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1336 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_inExpression1340 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1360 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_inExpression1364 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1421 = new BitSet(new long[]{0x000005F800000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_orRestriction_in_relationalExpression1441 = new BitSet(new long[]{0x000005F800000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1476 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1494 = new BitSet(new long[]{0x000005F800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1498 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_EOF_in_orRestriction1518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1542 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1558 = new BitSet(new long[]{0x000005F800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1562 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_operator_in_singleRestriction1605 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_shiftExpression_in_singleRestriction1609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction1627 = new BitSet(new long[]{0x000005F800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_orRestriction_in_singleRestriction1631 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction1633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1669 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression1683 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1685 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1705 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1720 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1722 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1737 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1767 = new BitSet(new long[]{0x3000000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression1788 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1796 = new BitSet(new long[]{0x3000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1824 = new BitSet(new long[]{0x0C00000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression1836 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1850 = new BitSet(new long[]{0x0C00000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1876 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1899 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression1924 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression1936 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1980 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1991 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2035 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2037 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2077 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2079 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2124 = new BitSet(new long[]{0x00021000C0000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2141 = new BitSet(new long[]{0x00021000C0000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2209 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression2211 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2213 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression2217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2235 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_type_in_castExpression2237 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2239 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType2272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType2280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType2288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType2296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType2304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType2312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType2320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary2348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary2365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary2368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary2372 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_primary2374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary2390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary2412 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary2414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary2430 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_creator_in_primary2432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary2448 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary2451 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary2453 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_DOT_in_primary2457 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_class_key_in_primary2459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary2480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary2496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary2512 = new BitSet(new long[]{0x0002140000000002L});
    public static final BitSet FOLLOW_DOT_in_primary2531 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_primary2535 = new BitSet(new long[]{0x0002140000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary2555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression2577 = new BitSet(new long[]{0x30603500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression2579 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression2582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression2608 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression2610 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList2634 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList2637 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList2639 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry2662 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry2664 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_mapEntry2666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression2688 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_parExpression2692 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression2694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix2729 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix2770 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix2815 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix2819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix2835 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix2866 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix2895 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix2911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator2934 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_createdName_in_creator2937 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator2952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName2970 = new BitSet(new long[]{0x0002010000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName2972 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName2985 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_createdName2987 = new BitSet(new long[]{0x0002010000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName2989 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName3004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator3024 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3045 = new BitSet(new long[]{0x30603500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3055 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3058 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3060 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3078 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3080 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3085 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3087 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3089 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3101 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3103 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer3132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer3163 = new BitSet(new long[]{0x3060D500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3166 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3169 = new BitSet(new long[]{0x30605500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3171 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3176 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer3183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest3200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3218 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation3220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments3237 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments3239 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments3241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix3258 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix3271 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix3273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3298 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_super_key_in_selector3302 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector3304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3320 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_new_key_in_selector3324 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector3327 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_innerCreator_in_selector3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_selector3370 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_arguments_in_selector3399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector3420 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_selector3447 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector3473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix3492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix3503 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_superSuffix3505 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix3514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments3537 = new BitSet(new long[]{0x30603500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments3542 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments3548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments3565 = new BitSet(new long[]{0x30601D00C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expressionList_in_arguments3577 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments3589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList3619 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList3630 = new BitSet(new long[]{0x30601500C00CD900L,0x0000000000000008L});
    public static final BitSet FOLLOW_expression_in_expressionList3634 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator3663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator3671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator3679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator3687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator3695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator3703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator3711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator3719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator3727 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator3729 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator3749 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator3751 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator3753 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator3771 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator3773 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator3775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key3807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key3867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key3897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key3927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key3957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key4017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key4047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key4077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key4107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key4137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key4167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key4197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key4228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred1_DRLExpressions386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred2_DRLExpressions420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred3_DRLExpressions510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred4_DRLExpressions521 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred4_DRLExpressions523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred5_DRLExpressions547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred6_DRLExpressions561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred7_DRLExpressions573 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred7_DRLExpressions575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred8_DRLExpressions756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_synpred9_DRLExpressions1263 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_in_key_in_synpred9_DRLExpressions1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_orRestriction_in_synpred10_DRLExpressions1435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred11_DRLExpressions1487 = new BitSet(new long[]{0x000005F800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_andRestriction_in_synpred11_DRLExpressions1489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred12_DRLExpressions1551 = new BitSet(new long[]{0x000005F800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_singleRestriction_in_synpred12_DRLExpressions1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred13_DRLExpressions1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred14_DRLExpressions1781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred15_DRLExpressions2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred16_DRLExpressions2138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred17_DRLExpressions2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred18_DRLExpressions2202 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_primitiveType_in_synpred18_DRLExpressions2204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred19_DRLExpressions2228 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_type_in_synpred19_DRLExpressions2230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred20_DRLExpressions2342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred21_DRLExpressions2361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred22_DRLExpressions2386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred23_DRLExpressions2408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred24_DRLExpressions2426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred25_DRLExpressions2444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred26_DRLExpressions2476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred27_DRLExpressions2492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred28_DRLExpressions2507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred29_DRLExpressions2526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_synpred29_DRLExpressions2528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred30_DRLExpressions2552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2723 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred32_DRLExpressions2830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred33_DRLExpressions3095 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred33_DRLExpressions3097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred34_DRLExpressions3293 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_super_key_in_synpred34_DRLExpressions3295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred35_DRLExpressions3315 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_new_key_in_synpred35_DRLExpressions3317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred36_DRLExpressions3342 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_synpred36_DRLExpressions3344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions3394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred38_DRLExpressions3417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions3509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred40_DRLExpressions3741 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred40_DRLExpressions3743 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred40_DRLExpressions3745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred41_DRLExpressions3765 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred41_DRLExpressions3767 = new BitSet(new long[]{0x0000000000000002L});

}