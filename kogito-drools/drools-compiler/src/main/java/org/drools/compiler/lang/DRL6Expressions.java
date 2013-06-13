// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/compiler/lang/DRL6Expressions.g 2013-06-13 10:31:36

    package org.drools.compiler.lang;

    import java.util.LinkedList;
    import org.drools.compiler.compiler.DroolsParserException;
    import org.drools.compiler.lang.ParserHelper;
    import org.drools.compiler.lang.DroolsParserExceptionFactory;
    import org.drools.compiler.lang.Location;
    import org.drools.core.CheckedDroolsException;

    import org.drools.compiler.lang.api.AnnotatedDescrBuilder;

    import org.drools.compiler.lang.descr.AtomicExprDescr;
    import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
    import org.drools.compiler.lang.descr.AnnotationDescr;
    import org.drools.compiler.lang.descr.BaseDescr;
    import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
    import org.drools.compiler.lang.descr.RelationalExprDescr;
    import org.drools.compiler.lang.descr.BindingDescr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRL6Expressions extends DRLExpressions {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SHARP", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TIME_INTERVAL", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "UNIFY", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "NULL_SAFE_DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "HASH", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
    };
    public static final int EOF=-1;
    public static final int SHARP=4;
    public static final int EOL=5;
    public static final int WS=6;
    public static final int Exponent=7;
    public static final int FloatTypeSuffix=8;
    public static final int FLOAT=9;
    public static final int HexDigit=10;
    public static final int IntegerTypeSuffix=11;
    public static final int HEX=12;
    public static final int DECIMAL=13;
    public static final int EscapeSequence=14;
    public static final int STRING=15;
    public static final int TIME_INTERVAL=16;
    public static final int UnicodeEscape=17;
    public static final int OctalEscape=18;
    public static final int BOOL=19;
    public static final int NULL=20;
    public static final int AT=21;
    public static final int PLUS_ASSIGN=22;
    public static final int MINUS_ASSIGN=23;
    public static final int MULT_ASSIGN=24;
    public static final int DIV_ASSIGN=25;
    public static final int AND_ASSIGN=26;
    public static final int OR_ASSIGN=27;
    public static final int XOR_ASSIGN=28;
    public static final int MOD_ASSIGN=29;
    public static final int UNIFY=30;
    public static final int DECR=31;
    public static final int INCR=32;
    public static final int ARROW=33;
    public static final int SEMICOLON=34;
    public static final int COLON=35;
    public static final int EQUALS=36;
    public static final int NOT_EQUALS=37;
    public static final int GREATER_EQUALS=38;
    public static final int LESS_EQUALS=39;
    public static final int GREATER=40;
    public static final int LESS=41;
    public static final int EQUALS_ASSIGN=42;
    public static final int LEFT_PAREN=43;
    public static final int RIGHT_PAREN=44;
    public static final int LEFT_SQUARE=45;
    public static final int RIGHT_SQUARE=46;
    public static final int LEFT_CURLY=47;
    public static final int RIGHT_CURLY=48;
    public static final int COMMA=49;
    public static final int DOT=50;
    public static final int NULL_SAFE_DOT=51;
    public static final int DOUBLE_AMPER=52;
    public static final int DOUBLE_PIPE=53;
    public static final int QUESTION=54;
    public static final int NEGATION=55;
    public static final int TILDE=56;
    public static final int PIPE=57;
    public static final int AMPER=58;
    public static final int XOR=59;
    public static final int MOD=60;
    public static final int STAR=61;
    public static final int MINUS=62;
    public static final int PLUS=63;
    public static final int HASH=64;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=65;
    public static final int MULTI_LINE_COMMENT=66;
    public static final int IdentifierStart=67;
    public static final int IdentifierPart=68;
    public static final int ID=69;
    public static final int DIV=70;
    public static final int MISC=71;

    // delegates
    // delegators


        public DRL6Expressions(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRL6Expressions(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DRL6Expressions.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/compiler/lang/DRL6Expressions.g"; }


        private ParserHelper helper;

        public DRL6Expressions(TokenStream input,
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

        private boolean isNotEOF() {
            if (state.backtracking != 0){
                return false;
            }
            if (input.get( input.index() - 1 ).getType() == DRL6Lexer.WS){
                return true;
            }
            if (input.LA(-1) == DRL6Lexer.LEFT_PAREN){
                return true;
            }
            return input.get( input.index() ).getType() != DRL6Lexer.EOF;
        }


    public static class literal_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "literal"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:89:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR );
    public final DRL6Expressions.literal_return literal() throws RecognitionException {
        DRL6Expressions.literal_return retval = new DRL6Expressions.literal_return();
        retval.start = input.LT(1);

        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;
        Token TIME_INTERVAL7=null;
        Token STAR8=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:90:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR )
            int alt1=8;
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
            case STAR:
                {
                alt1=8;
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:90:7: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal92); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:91:7: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal109); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:92:7: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal125); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:93:7: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal145); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:94:7: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal163); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:95:7: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal182); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:96:9: TIME_INTERVAL
                    {
                    TIME_INTERVAL7=(Token)match(input,TIME_INTERVAL,FOLLOW_TIME_INTERVAL_in_literal203); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(TIME_INTERVAL7, DroolsEditorType.NULL_CONST); 
                    }

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:97:9: STAR
                    {
                    STAR8=(Token)match(input,STAR,FOLLOW_STAR_in_literal215); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       helper.emit(STAR8, DroolsEditorType.NUMERIC_CONST); 
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
    };

    // $ANTLR start "operator"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:100:1: operator returns [boolean negated, String opr] : (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) ;
    public final DRL6Expressions.operator_return operator() throws RecognitionException {
        DRL6Expressions.operator_return retval = new DRL6Expressions.operator_return();
        retval.start = input.LT(1);

        Token x=null;
        Token op=null;
        DRL6Expressions.relationalOp_return rop = null;


         if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:3: ( (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:5: (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
            {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:6: (x= TILDE )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==TILDE) ) {
                int LA2_1 = input.LA(2);

                if ( ((LA2_1>=EQUALS && LA2_1<=LESS)||LA2_1==TILDE||LA2_1==ID) ) {
                    alt2=1;
                }
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:103:6: x= TILDE
                    {
                    x=(Token)match(input,TILDE,FOLLOW_TILDE_in_operator256); if (state.failed) return retval;

                    }
                    break;

            }

            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:104:5: (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
            int alt3=3;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==EQUALS) ) {
                alt3=1;
            }
            else if ( (LA3_0==NOT_EQUALS) ) {
                alt3=2;
            }
            else if ( ((LA3_0>=GREATER_EQUALS && LA3_0<=LESS)||LA3_0==TILDE) ) {
                alt3=3;
            }
            else if ( (LA3_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt3=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:104:7: op= EQUALS
                    {
                    op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator267); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:105:7: op= NOT_EQUALS
                    {
                    op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator286); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); 
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:106:7: rop= relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator301);
                    rop=relationalOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = (rop!=null?rop.negated:false); retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(rop!=null?rop.opr:null); 
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {
               if( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } 
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:112:1: relationalOp returns [boolean negated, String opr, java.util.List<String> params] : (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key ) ;
    public final DRL6Expressions.relationalOp_return relationalOp() throws RecognitionException {
        DRL6Expressions.relationalOp_return retval = new DRL6Expressions.relationalOp_return();
        retval.start = input.LT(1);

        Token op=null;
        String xop = null;

        DRL6Expressions.neg_operator_key_return nop = null;

        DRL6Expressions.operator_key_return cop = null;


         if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:3: ( (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key )
            {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key )
            int alt4=7;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==LESS_EQUALS) ) {
                alt4=1;
            }
            else if ( (LA4_0==GREATER_EQUALS) ) {
                alt4=2;
            }
            else if ( (LA4_0==LESS) ) {
                alt4=3;
            }
            else if ( (LA4_0==GREATER) ) {
                alt4=4;
            }
            else if ( (LA4_0==TILDE) ) {
                alt4=5;
            }
            else if ( (LA4_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                int LA4_6 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt4=6;
                }
                else if ( (((helper.isPluggableEvaluator(false)))) ) {
                    alt4=7;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 6, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:115:7: op= LESS_EQUALS
                    {
                    op=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp342); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:116:7: op= GREATER_EQUALS
                    {
                    op=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp358); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:117:7: op= LESS
                    {
                    op=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp371); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:118:7: op= GREATER
                    {
                    op=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp394); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:119:7: xop= complexOp
                    {
                    pushFollow(FOLLOW_complexOp_in_relationalOp414);
                    xop=complexOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:120:7: not_key nop= neg_operator_key
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp429);
                    not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp433);
                    nop=neg_operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = true; retval.opr =(nop!=null?input.toString(nop.start,nop.stop):null);
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:121:7: cop= operator_key
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp445);
                    cop=operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(cop!=null?input.toString(cop.start,cop.stop):null);
                    }

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {
               if( state.backtracking == 0 && input.LA( 1 ) != DRL6Lexer.EOF) { helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); } 
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


    // $ANTLR start "complexOp"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:125:1: complexOp returns [String opr] : t= TILDE e= EQUALS_ASSIGN ;
    public final String complexOp() throws RecognitionException {
        String opr = null;

        Token t=null;
        Token e=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:126:5: (t= TILDE e= EQUALS_ASSIGN )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:126:7: t= TILDE e= EQUALS_ASSIGN
            {
            t=(Token)match(input,TILDE,FOLLOW_TILDE_in_complexOp477); if (state.failed) return opr;
            e=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_complexOp481); if (state.failed) return opr;
            if ( state.backtracking==0 ) {
               opr =(t!=null?t.getText():null)+(e!=null?e.getText():null); 
            }

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return opr;
    }
    // $ANTLR end "complexOp"


    // $ANTLR start "typeList"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:129:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:5: ( type ( COMMA type )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:7: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList502);
            type();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:12: ( COMMA type )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:130:13: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList505); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList507);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop5;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:133:1: type : tm= typeMatch ;
    public final DRL6Expressions.type_return type() throws RecognitionException {
        DRL6Expressions.type_return retval = new DRL6Expressions.type_return();
        retval.start = input.LT(1);

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:134:5: (tm= typeMatch )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:134:8: tm= typeMatch
            {
            pushFollow(FOLLOW_typeMatch_in_type529);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:137:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void typeMatch() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                int LA11_1 = input.LA(2);

                if ( (((synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))))) ) {
                    alt11=1;
                }
                else if ( (true) ) {
                    alt11=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_typeMatch555);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==LEFT_SQUARE) && (synpred2_DRL6Expressions())) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch565); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch567); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_typeMatch581); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:12: ( ( typeArguments )=> typeArguments )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==LESS) ) {
                        int LA7_1 = input.LA(2);

                        if ( (LA7_1==ID) && (synpred3_DRL6Expressions())) {
                            alt7=1;
                        }
                        else if ( (LA7_1==QUESTION) && (synpred3_DRL6Expressions())) {
                            alt7=1;
                        }
                    }
                    switch (alt7) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_typeMatch588);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==DOT) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_typeMatch593); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_typeMatch595); if (state.failed) return ;
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt8=2;
                    	    int LA8_0 = input.LA(1);

                    	    if ( (LA8_0==LESS) ) {
                    	        int LA8_1 = input.LA(2);

                    	        if ( (LA8_1==ID) && (synpred4_DRL6Expressions())) {
                    	            alt8=1;
                    	        }
                    	        else if ( (LA8_1==QUESTION) && (synpred4_DRL6Expressions())) {
                    	            alt8=1;
                    	        }
                    	    }
                    	    switch (alt8) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_typeMatch602);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==LEFT_SQUARE) && (synpred5_DRL6Expressions())) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch617); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch619); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:142:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments640); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments642);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:25: ( COMMA typeArgument )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==COMMA) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:143:26: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments645); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments647);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments651); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:146:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:147:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ID) ) {
                alt15=1;
            }
            else if ( (LA15_0==QUESTION) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:147:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument668);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument676); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:16: ( ( extends_key | super_key ) type )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key ) type
                            {
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:17: ( extends_key | super_key )
                            int alt13=2;
                            int LA13_0 = input.LA(1);

                            if ( (LA13_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                                int LA13_1 = input.LA(2);

                                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                                    alt13=1;
                                }
                                else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                                    alt13=2;
                                }
                                else {
                                    if (state.backtracking>0) {state.failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 13, 1, input);

                                    throw nvae;
                                }
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 13, 0, input);

                                throw nvae;
                            }
                            switch (alt13) {
                                case 1 :
                                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument680);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:148:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument684);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument687);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:156:1: dummy : expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
    public final void dummy() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:157:5: ( expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:157:7: expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN )
            {
            pushFollow(FOLLOW_expression_in_dummy711);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:159:1: dummy2 : relationalExpression EOF ;
    public final void dummy2() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:160:5: ( relationalExpression EOF )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:160:8: relationalExpression EOF
            {
            pushFollow(FOLLOW_relationalExpression_in_dummy2747);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            match(input,EOF,FOLLOW_EOF_in_dummy2749); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:163:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? ;
    public final DRL6Expressions.expression_return expression() throws RecognitionException {
        DRL6Expressions.expression_return retval = new DRL6Expressions.expression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;

        DRL6Expressions.expression_return right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:164:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:164:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression768);
            left=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            int alt16=2;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:10: ( assignmentOperator )=>op= assignmentOperator right= expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression789);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression793);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:168:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( ternaryExpression )? ;
    public final BaseDescr conditionalExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:169:5: (left= conditionalOrExpression ( ternaryExpression )? )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:169:9: left= conditionalOrExpression ( ternaryExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression820);
            left=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:170:9: ( ternaryExpression )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==QUESTION) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:170:9: ternaryExpression
                    {
                    pushFollow(FOLLOW_ternaryExpression_in_conditionalExpression832);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:173:1: ternaryExpression : QUESTION ts= expression COLON fs= expression ;
    public final void ternaryExpression() throws RecognitionException {
        DRL6Expressions.expression_return ts = null;

        DRL6Expressions.expression_return fs = null;


         ternOp++; 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:175:5: ( QUESTION ts= expression COLON fs= expression )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:175:7: QUESTION ts= expression COLON fs= expression
            {
            match(input,QUESTION,FOLLOW_QUESTION_in_ternaryExpression854); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression858);
            ts=expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_ternaryExpression860); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression864);
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


    // $ANTLR start "fullAnnotation"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:180:1: fullAnnotation[AnnotatedDescrBuilder inDescrBuilder] returns [AnnotationDescr result] : AT name= ID annotationArgs[result] ;
    public final AnnotationDescr fullAnnotation(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
        AnnotationDescr result = null;

        Token name=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:181:3: ( AT name= ID annotationArgs[result] )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:181:5: AT name= ID annotationArgs[result]
            {
            match(input,AT,FOLLOW_AT_in_fullAnnotation890); if (state.failed) return result;
            name=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation894); if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr ) { result = inDescrBuilder != null ? (AnnotationDescr) inDescrBuilder.newAnnotation( (name!=null?name.getText():null) ).getDescr() : new AnnotationDescr( (name!=null?name.getText():null) ); } 
            }
            pushFollow(FOLLOW_annotationArgs_in_fullAnnotation902);
            annotationArgs(result);

            state._fsp--;
            if (state.failed) return result;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return result;
    }
    // $ANTLR end "fullAnnotation"


    // $ANTLR start "annotationArgs"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:185:1: annotationArgs[AnnotationDescr descr] : LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN ;
    public final void annotationArgs(AnnotationDescr descr) throws RecognitionException {
        Token value=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:186:3: ( LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:186:5: LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_annotationArgs918); if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:187:5: (value= ID | annotationElementValuePairs[descr] )?
            int alt18=3;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ID) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==EQUALS_ASSIGN) ) {
                    alt18=2;
                }
                else if ( (LA18_1==RIGHT_PAREN) ) {
                    alt18=1;
                }
            }
            switch (alt18) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:188:8: value= ID
                    {
                    value=(Token)match(input,ID,FOLLOW_ID_in_annotationArgs935); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       if ( buildDescr ) { descr.setValue( (value!=null?value.getText():null) ); } 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:189:10: annotationElementValuePairs[descr]
                    {
                    pushFollow(FOLLOW_annotationElementValuePairs_in_annotationArgs948);
                    annotationElementValuePairs(descr);

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_annotationArgs962); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotationArgs"


    // $ANTLR start "annotationElementValuePairs"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:194:1: annotationElementValuePairs[AnnotationDescr descr] : annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* ;
    public final void annotationElementValuePairs(AnnotationDescr descr) throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:195:3: ( annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:195:5: annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )*
            {
            pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs977);
            annotationElementValuePair(descr);

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:195:39: ( COMMA annotationElementValuePair[descr] )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==COMMA) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:195:41: COMMA annotationElementValuePair[descr]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_annotationElementValuePairs982); if (state.failed) return ;
            	    pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs984);
            	    annotationElementValuePair(descr);

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "annotationElementValuePairs"


    // $ANTLR start "annotationElementValuePair"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:198:1: annotationElementValuePair[AnnotationDescr descr] : key= ID EQUALS_ASSIGN val= annotationValue ;
    public final void annotationElementValuePair(AnnotationDescr descr) throws RecognitionException {
        Token key=null;
        DRL6Expressions.annotationValue_return val = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:199:3: (key= ID EQUALS_ASSIGN val= annotationValue )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:199:5: key= ID EQUALS_ASSIGN val= annotationValue
            {
            key=(Token)match(input,ID,FOLLOW_ID_in_annotationElementValuePair1005); if (state.failed) return ;
            match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1007); if (state.failed) return ;
            pushFollow(FOLLOW_annotationValue_in_annotationElementValuePair1011);
            val=annotationValue();

            state._fsp--;
            if (state.failed) return ;
            if ( state.backtracking==0 ) {
               if ( buildDescr ) { descr.setKeyValue( (key!=null?key.getText():null), (val!=null?input.toString(val.start,val.stop):null) ); } 
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
    // $ANTLR end "annotationElementValuePair"

    public static class annotationValue_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "annotationValue"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:202:1: annotationValue : ( expression | annotationArray );
    public final DRL6Expressions.annotationValue_return annotationValue() throws RecognitionException {
        DRL6Expressions.annotationValue_return retval = new DRL6Expressions.annotationValue_return();
        retval.start = input.LT(1);

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:203:3: ( expression | annotationArray )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==FLOAT||(LA20_0>=HEX && LA20_0<=DECIMAL)||(LA20_0>=STRING && LA20_0<=TIME_INTERVAL)||(LA20_0>=BOOL && LA20_0<=NULL)||(LA20_0>=DECR && LA20_0<=INCR)||LA20_0==LESS||LA20_0==LEFT_PAREN||LA20_0==LEFT_SQUARE||(LA20_0>=NEGATION && LA20_0<=TILDE)||(LA20_0>=STAR && LA20_0<=PLUS)||LA20_0==ID) ) {
                alt20=1;
            }
            else if ( (LA20_0==LEFT_CURLY) ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:203:5: expression
                    {
                    pushFollow(FOLLOW_expression_in_annotationValue1026);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:203:18: annotationArray
                    {
                    pushFollow(FOLLOW_annotationArray_in_annotationValue1030);
                    annotationArray();

                    state._fsp--;
                    if (state.failed) return retval;

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
    // $ANTLR end "annotationValue"


    // $ANTLR start "annotationArray"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:206:1: annotationArray : LEFT_CURLY annotationValue ( COMMA annotationValue )* RIGHT_CURLY ;
    public final void annotationArray() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:207:3: ( LEFT_CURLY annotationValue ( COMMA annotationValue )* RIGHT_CURLY )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:207:6: LEFT_CURLY annotationValue ( COMMA annotationValue )* RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_annotationArray1044); if (state.failed) return ;
            pushFollow(FOLLOW_annotationValue_in_annotationArray1046);
            annotationValue();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:207:33: ( COMMA annotationValue )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==COMMA) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:207:35: COMMA annotationValue
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_annotationArray1050); if (state.failed) return ;
            	    pushFollow(FOLLOW_annotationValue_in_annotationArray1052);
            	    annotationValue();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_annotationArray1057); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "annotationArray"


    // $ANTLR start "conditionalOrExpression"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:212:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* ;
    public final BaseDescr conditionalOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:213:3: (left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:213:5: left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1078);
            left=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:214:3: ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==DOUBLE_PIPE) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:214:5: DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1087); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	        if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR );  
            	    }
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:216:13: (args= fullAnnotation[null] )?
            	    int alt22=2;
            	    int LA22_0 = input.LA(1);

            	    if ( (LA22_0==AT) ) {
            	        alt22=1;
            	    }
            	    switch (alt22) {
            	        case 1 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:216:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_conditionalOrExpression1109);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1115);
            	    right=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr();
            	                     descr.addOrMerge( result );
            	                     descr.addOrMerge( right );
            	                     if ( args != null ) { descr.addAnnotation( args ); }
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
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:228:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* ;
    public final BaseDescr conditionalAndExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:229:3: (left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:229:5: left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1150);
            left=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:230:3: ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==DOUBLE_AMPER) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:230:5: DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1158); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); 
            	    }
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:232:13: (args= fullAnnotation[null] )?
            	    int alt24=2;
            	    int LA24_0 = input.LA(1);

            	    if ( (LA24_0==AT) ) {
            	        alt24=1;
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:232:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_conditionalAndExpression1181);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1187);
            	    right=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd();
            	                     descr.addOrMerge( result );
            	                     descr.addOrMerge( right );
            	                     if ( args != null ) { descr.addAnnotation( args ); }
            	                     result = descr;
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
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:244:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
    public final BaseDescr inclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:245:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:245:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1222);
            left=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:246:3: ( PIPE right= exclusiveOrExpression )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==PIPE) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:246:5: PIPE right= exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression1230); if (state.failed) return result;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1234);
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
            	    break loop26;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:257:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
    public final BaseDescr exclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:258:3: (left= andExpression ( XOR right= andExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:258:5: left= andExpression ( XOR right= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1269);
            left=andExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:259:3: ( XOR right= andExpression )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==XOR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:259:5: XOR right= andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression1277); if (state.failed) return result;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1281);
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
            	    break loop27;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:270:1: andExpression returns [BaseDescr result] : left= equalityExpression ( AMPER right= equalityExpression )* ;
    public final BaseDescr andExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:271:3: (left= equalityExpression ( AMPER right= equalityExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:271:5: left= equalityExpression ( AMPER right= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression1316);
            left=equalityExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:272:3: ( AMPER right= equalityExpression )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==AMPER) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:272:5: AMPER right= equalityExpression
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression1324); if (state.failed) return result;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression1328);
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
            	    break loop28;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:283:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* ;
    public final BaseDescr equalityExpression() throws RecognitionException {
        BaseDescr result = null;

        Token op=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:284:3: (left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:284:5: left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1363);
            left=instanceOfExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:285:3: ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>=EQUALS && LA30_0<=NOT_EQUALS)) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:285:5: (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression
            	    {
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:285:5: (op= EQUALS | op= NOT_EQUALS )
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);

            	    if ( (LA29_0==EQUALS) ) {
            	        alt29=1;
            	    }
            	    else if ( (LA29_0==NOT_EQUALS) ) {
            	        alt29=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 29, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:285:7: op= EQUALS
            	            {
            	            op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression1375); if (state.failed) return result;

            	            }
            	            break;
            	        case 2 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:285:19: op= NOT_EQUALS
            	            {
            	            op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression1381); if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	        helper.setHasOperator( true );
            	             if( input.LA( 1 ) != DRL6Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
            	    }
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1397);
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
            	    break loop30;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:296:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
    public final BaseDescr instanceOfExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRL6Expressions.instanceof_key_return op = null;

        DRL6Expressions.type_return right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:297:3: (left= inExpression (op= instanceof_key right= type )? )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:297:5: left= inExpression (op= instanceof_key right= type )?
            {
            pushFollow(FOLLOW_inExpression_in_instanceOfExpression1432);
            left=inExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:298:3: (op= instanceof_key right= type )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==ID) ) {
                int LA31_1 = input.LA(2);

                if ( (LA31_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))) {
                    alt31=1;
                }
            }
            switch (alt31) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:298:5: op= instanceof_key right= type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression1442);
                    op=instanceof_key();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        helper.setHasOperator( true );
                             if( input.LA( 1 ) != DRL6Lexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression1456);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:309:1: inExpression returns [BaseDescr result] : left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? ;
    public final BaseDescr inExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRL6Expressions.expression_return e1 = null;

        DRL6Expressions.expression_return e2 = null;


         ConstraintConnectiveDescr descr = null; BaseDescr leftDescr = null; BindingDescr binding = null; 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:312:3: (left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:312:5: left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression1501);
            left=relationalExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; }
                    if( left instanceof BindingDescr ) {
                        binding = (BindingDescr)left;
                        leftDescr = new AtomicExprDescr( binding.getExpression() );
                    } else {
                        leftDescr = left;
                    }
                  
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:321:5: ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            int alt34=3;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) ) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==ID) ) {
                    int LA34_3 = input.LA(3);

                    if ( (LA34_3==LEFT_PAREN) && ((synpred7_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                        alt34=1;
                    }
                }
                else if ( (LA34_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt34=2;
                }
            }
            switch (alt34) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:321:6: ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_not_key_in_inExpression1521);
                    not_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_in_key_in_inExpression1525);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1527); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_expression_in_inExpression1549);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newAnd();
                                  RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:329:7: ( COMMA e2= expression )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0==COMMA) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:329:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1568); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1572);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	         RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e2!=null?e2.result:null) );
                    	                  descr.addOrMerge( rel );
                    	              
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop32;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1593); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:335:7: in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_in_key_in_inExpression1609);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1611); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_expression_in_inExpression1633);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newOr();
                                  RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:343:7: ( COMMA e2= expression )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==COMMA) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:343:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1652); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1656);
                    	    e2=expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	         RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e2!=null?e2.result:null) );
                    	                  descr.addOrMerge( rel );
                    	              
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1677); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); 
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               if( binding != null && descr != null ) descr.addOrMerge( binding ); 
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:352:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* ;
    public final BaseDescr relationalExpression() throws RecognitionException {
        relationalExpression_stack.push(new relationalExpression_scope());
        BaseDescr result = null;

        DRL6Expressions.shiftExpression_return left = null;

        BaseDescr right = null;


         ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = null; 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:355:3: (left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:355:5: left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression1718);
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
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:365:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*
            loop35:
            do {
                int alt35=2;
                alt35 = dfa35.predict(input);
                switch (alt35) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:365:5: ( operator | LEFT_PAREN )=>right= orRestriction
            	    {
            	    pushFollow(FOLLOW_orRestriction_in_relationalExpression1743);
            	    right=orRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     result = right;
            	                     ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = result;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop35;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:374:1: orRestriction returns [BaseDescr result] : left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? ;
    public final BaseDescr orRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:375:3: (left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:375:5: left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )?
            {
            pushFollow(FOLLOW_andRestriction_in_orRestriction1778);
            left=andRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*
            loop37:
            do {
                int alt37=2;
                alt37 = dfa37.predict(input);
                switch (alt37) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_orRestriction1800); if (state.failed) return result;
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:79: (args= fullAnnotation[null] )?
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==AT) ) {
            	        alt36=1;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:79: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_orRestriction1804);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_andRestriction_in_orRestriction1810);
            	    right=andRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr();
            	                     descr.addOrMerge( result );
            	                     descr.addOrMerge( right );
            	                     if ( args != null ) { descr.addAnnotation( args ); }
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);

            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:385:7: ( EOF )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==EOF) ) {
                int LA38_1 = input.LA(2);

                if ( (LA38_1==EOF) ) {
                    int LA38_3 = input.LA(3);

                    if ( (LA38_3==EOF) ) {
                        alt38=1;
                    }
                }
                else if ( ((LA38_1>=AT && LA38_1<=MOD_ASSIGN)||(LA38_1>=SEMICOLON && LA38_1<=RIGHT_PAREN)||LA38_1==RIGHT_SQUARE||(LA38_1>=RIGHT_CURLY && LA38_1<=COMMA)||(LA38_1>=DOUBLE_AMPER && LA38_1<=QUESTION)||(LA38_1>=TILDE && LA38_1<=XOR)||LA38_1==ID) ) {
                    alt38=1;
                }
            }
            switch (alt38) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:385:7: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_orRestriction1829); if (state.failed) return result;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:388:1: andRestriction returns [BaseDescr result] : left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* ;
    public final BaseDescr andRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:389:3: (left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:389:5: left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
            {
            pushFollow(FOLLOW_singleRestriction_in_andRestriction1849);
            left=singleRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
            loop40:
            do {
                int alt40=2;
                alt40 = dfa40.predict(input);
                switch (alt40) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andRestriction1869); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); 
            	    }
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:392:13: (args= fullAnnotation[null] )?
            	    int alt39=2;
            	    int LA39_0 = input.LA(1);

            	    if ( (LA39_0==AT) ) {
            	        alt39=1;
            	    }
            	    switch (alt39) {
            	        case 1 :
            	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:392:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_andRestriction1890);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_singleRestriction_in_andRestriction1895);
            	    right=singleRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr  ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd();
            	                     descr.addOrMerge( result );
            	                     descr.addOrMerge( right );
            	                     if ( args != null ) { descr.addAnnotation( args ); }
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop40;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:404:1: singleRestriction returns [BaseDescr result] : (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN );
    public final BaseDescr singleRestriction() throws RecognitionException {
        BaseDescr result = null;

        DRL6Expressions.operator_return op = null;

        java.util.List<String> sa = null;

        DRL6Expressions.shiftExpression_return value = null;

        BaseDescr or = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:405:3: (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( ((LA42_0>=EQUALS && LA42_0<=LESS)||LA42_0==TILDE) ) {
                alt42=1;
            }
            else if ( (LA42_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt42=1;
            }
            else if ( (LA42_0==LEFT_PAREN) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:405:6: op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )
                    {
                    pushFollow(FOLLOW_operator_in_singleRestriction1931);
                    op=operator();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )
                    int alt41=2;
                    alt41 = dfa41.predict(input);
                    switch (alt41) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:8: ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression
                            {
                            pushFollow(FOLLOW_squareArguments_in_singleRestriction1960);
                            sa=squareArguments();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_shiftExpression_in_singleRestriction1964);
                            value=shiftExpression();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:408:10: value= shiftExpression
                            {
                            pushFollow(FOLLOW_shiftExpression_in_singleRestriction1977);
                            value=shiftExpression();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }

                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) {
                                     BaseDescr descr = ( (value!=null?value.result:null) != null &&
                                                       ( (!((value!=null?value.result:null) instanceof AtomicExprDescr)) ||
                                                         ((value!=null?input.toString(value.start,value.stop):null).equals(((AtomicExprDescr)(value!=null?value.result:null)).getExpression())) )) ?
                      		                    (value!=null?value.result:null) :
                      		                    new AtomicExprDescr( (value!=null?input.toString(value.start,value.stop):null) ) ;
                                     result = new RelationalExprDescr( (op!=null?op.opr:null), (op!=null?op.negated:false), sa, ((relationalExpression_scope)relationalExpression_stack.peek()).lsd, descr );
                      	       if( ((relationalExpression_scope)relationalExpression_stack.peek()).lsd instanceof BindingDescr ) {
                      	           ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = new AtomicExprDescr( ((BindingDescr)((relationalExpression_scope)relationalExpression_stack.peek()).lsd).getExpression() );
                      	       }
                                 }
                                 helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END );
                               
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:423:6: LEFT_PAREN or= orRestriction RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_singleRestriction2002); if (state.failed) return result;
                    pushFollow(FOLLOW_orRestriction_in_singleRestriction2006);
                    or=orRestriction();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_singleRestriction2008); if (state.failed) return result;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:428:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final DRL6Expressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRL6Expressions.shiftExpression_return retval = new DRL6Expressions.shiftExpression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:429:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:429:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression2032);
            left=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:430:5: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop43:
            do {
                int alt43=2;
                alt43 = dfa43.predict(input);
                switch (alt43) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:430:7: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression2046);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression2048);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop43;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:433:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:434:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:434:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:434:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt44=3;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LESS) ) {
                alt44=1;
            }
            else if ( (LA44_0==GREATER) ) {
                int LA44_2 = input.LA(2);

                if ( (LA44_2==GREATER) ) {
                    int LA44_3 = input.LA(3);

                    if ( (LA44_3==GREATER) ) {
                        alt44=2;
                    }
                    else if ( (LA44_3==EOF||LA44_3==FLOAT||(LA44_3>=HEX && LA44_3<=DECIMAL)||(LA44_3>=STRING && LA44_3<=TIME_INTERVAL)||(LA44_3>=BOOL && LA44_3<=NULL)||(LA44_3>=DECR && LA44_3<=INCR)||LA44_3==LESS||LA44_3==LEFT_PAREN||LA44_3==LEFT_SQUARE||(LA44_3>=NEGATION && LA44_3<=TILDE)||(LA44_3>=STAR && LA44_3<=PLUS)||LA44_3==ID) ) {
                        alt44=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 2, input);

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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:434:9: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp2068); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp2070); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:435:11: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2082); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2084); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2086); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:436:11: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2098); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2100); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:439:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final BaseDescr additiveExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:440:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:440:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2128);
            left=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:441:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( ((LA45_0>=MINUS && LA45_0<=PLUS)) && (synpred13_DRL6Expressions())) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:441:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2157);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop45;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:444:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final BaseDescr multiplicativeExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:445:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:445:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2185);
            left=unaryExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:446:7: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);

                if ( ((LA46_0>=MOD && LA46_0<=STAR)||LA46_0==DIV) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:446:9: ( STAR | DIV | MOD ) unaryExpression
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

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2211);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop46;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:449:1: unaryExpression returns [BaseDescr result] : ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus );
    public final BaseDescr unaryExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr ue = null;

        DRL6Expressions.unaryExpressionNotPlusMinus_return left = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:450:5: ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus )
            int alt47=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt47=1;
                }
                break;
            case MINUS:
                {
                alt47=2;
                }
                break;
            case INCR:
                {
                alt47=3;
                }
                break;
            case DECR:
                {
                alt47=4;
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
            case STAR:
            case ID:
                {
                alt47=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:450:9: PLUS ue= unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression2237); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression2241);
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:457:7: MINUS ue= unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression2259); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression2263);
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:464:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression2283); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression2285);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:465:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression2295); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression2297);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:466:9: left= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2309);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:469:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final DRL6Expressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        DRL6Expressions.unaryExpressionNotPlusMinus_return retval = new DRL6Expressions.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        Token var=null;
        Token COLON9=null;
        Token UNIFY10=null;
        BaseDescr left = null;


         boolean isLeft = false; BindingDescr bind = null;
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:471:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt51=4;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:471:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2339); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2341);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:472:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2350); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2352);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:473:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2366);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:474:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    if ( state.backtracking==0 ) {
                       isLeft = helper.getLeftMostExpr() == null;
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
                    int alt48=3;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==ID) ) {
                        int LA48_1 = input.LA(2);

                        if ( (LA48_1==COLON) ) {
                            int LA48_3 = input.LA(3);

                            if ( ((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON)) ) {
                                alt48=1;
                            }
                        }
                        else if ( (LA48_1==UNIFY) ) {
                            alt48=2;
                        }
                    }
                    switch (alt48) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:11: ({...}? (var= ID COLON ) )
                            {
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:11: ({...}? (var= ID COLON ) )
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:12: {...}? (var= ID COLON )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.COLON");
                            }
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:75: (var= ID COLON )
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:475:76: var= ID COLON
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2394); if (state.failed) return retval;
                            COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2396); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(COLON9, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:11: ({...}? (var= ID UNIFY ) )
                            {
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:11: ({...}? (var= ID UNIFY ) )
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:12: {...}? (var= ID UNIFY )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRL6Lexer.UNIFY");
                            }
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:75: (var= ID UNIFY )
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:477:76: var= ID UNIFY
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2435); if (state.failed) return retval;
                            UNIFY10=(Token)match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2437); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(UNIFY10, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2482);
                    left=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { retval.result = left; } 
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:9: ( ( selector )=> selector )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==DOT) && (synpred15_DRL6Expressions())) {
                            alt49=1;
                        }
                        else if ( (LA49_0==LEFT_SQUARE) && (synpred15_DRL6Expressions())) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:10: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2499);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop49;
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:500:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( ((LA50_0>=DECR && LA50_0<=INCR)) && (synpred16_DRL6Expressions())) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:500:10: ( INCR | DECR )=> ( INCR | DECR )
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:503:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        BaseDescr expr = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==LEFT_PAREN) ) {
                int LA52_1 = input.LA(2);

                if ( (synpred17_DRL6Expressions()) ) {
                    alt52=1;
                }
                else if ( (synpred18_DRL6Expressions()) ) {
                    alt52=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2561); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression2563);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2565); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression2569);
                    expr=unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:505:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2586); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression2588);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2590); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2592);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:508:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:509:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt53=8;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:509:9: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType2611);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:510:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType2619);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:511:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType2627);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:512:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType2635);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:513:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType2643);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:514:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType2651);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:515:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType2659);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:516:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType2667);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:519:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final BaseDescr primary() throws RecognitionException {
        BaseDescr result = null;

        Token i1=null;
        Token i2=null;
        Token DOT12=null;
        Token LEFT_PAREN13=null;
        Token COMMA14=null;
        Token RIGHT_PAREN15=null;
        Token SHARP16=null;
        Token HASH17=null;
        Token NULL_SAFE_DOT18=null;
        BaseDescr expr = null;

        DRL6Expressions.literal_return literal11 = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:520:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt59=9;
            alt59 = dfa59.predict(input);
            switch (alt59) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:520:7: ( parExpression )=>expr= parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary2695);
                    expr=parExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        if( buildDescr  ) { result = expr; }  
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary2712);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return result;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==ID) ) {
                        int LA54_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt54=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt54=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return result;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 54, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return result;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 0, input);

                        throw nvae;
                    }
                    switch (alt54) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary2715);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary2719);
                            this_key();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_arguments_in_primary2721);
                            arguments();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary2737);
                    literal11=literal();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) { result = new AtomicExprDescr( (literal11!=null?input.toString(literal11.start,literal11.stop):null), true ); }  
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:524:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary2759);
                    super_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_superSuffix_in_primary2761);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:525:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary2776);
                    new_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_creator_in_primary2778);
                    creator();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:526:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary2793);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return result;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:526:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);

                        if ( (LA55_0==LEFT_SQUARE) ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:526:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary2796); if (state.failed) return result;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary2798); if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop55;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary2802); if (state.failed) return result;
                    pushFollow(FOLLOW_class_key_in_primary2804);
                    class_key();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:528:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary2824);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:529:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary2839);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:530:9: ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    i1=(Token)match(input,ID,FOLLOW_ID_in_primary2855); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit(i1, DroolsEditorType.IDENTIFIER); 
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:531:9: ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )*
                    loop57:
                    do {
                        int alt57=6;
                        int LA57_0 = input.LA(1);

                        if ( (LA57_0==DOT) ) {
                            int LA57_2 = input.LA(2);

                            if ( (LA57_2==ID) ) {
                                int LA57_6 = input.LA(3);

                                if ( (synpred28_DRL6Expressions()) ) {
                                    alt57=1;
                                }


                            }
                            else if ( (LA57_2==LEFT_PAREN) && (synpred29_DRL6Expressions())) {
                                alt57=2;
                            }


                        }
                        else if ( (LA57_0==SHARP) && (synpred30_DRL6Expressions())) {
                            alt57=3;
                        }
                        else if ( (LA57_0==HASH) && (synpred31_DRL6Expressions())) {
                            alt57=4;
                        }
                        else if ( (LA57_0==NULL_SAFE_DOT) && (synpred32_DRL6Expressions())) {
                            alt57=5;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:532:13: ( ( DOT ID )=> DOT i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:532:13: ( ( DOT ID )=> DOT i2= ID )
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:532:15: ( DOT ID )=> DOT i2= ID
                    	    {
                    	    DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_primary2889); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary2893); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(DOT12, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:534:13: ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
                    	    {
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:534:13: ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:534:15: ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary2933); if (state.failed) return result;
                    	    LEFT_PAREN13=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_primary2935); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(DOT12, DroolsEditorType.SYMBOL); helper.emit(LEFT_PAREN13, DroolsEditorType.SYMBOL); 
                    	    }
                    	    pushFollow(FOLLOW_expression_in_primary2975);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:535:48: ( COMMA expression )*
                    	    loop56:
                    	    do {
                    	        int alt56=2;
                    	        int LA56_0 = input.LA(1);

                    	        if ( (LA56_0==COMMA) ) {
                    	            alt56=1;
                    	        }


                    	        switch (alt56) {
                    	    	case 1 :
                    	    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:535:49: COMMA expression
                    	    	    {
                    	    	    COMMA14=(Token)match(input,COMMA,FOLLOW_COMMA_in_primary2978); if (state.failed) return result;
                    	    	    if ( state.backtracking==0 ) {
                    	    	       helper.emit(COMMA14, DroolsEditorType.SYMBOL); 
                    	    	    }
                    	    	    pushFollow(FOLLOW_expression_in_primary2982);
                    	    	    expression();

                    	    	    state._fsp--;
                    	    	    if (state.failed) return result;

                    	    	    }
                    	    	    break;

                    	    	default :
                    	    	    break loop56;
                    	        }
                    	    } while (true);

                    	    RIGHT_PAREN15=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_primary3022); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_PAREN15, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 3 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:13: ( ( SHARP ID )=> SHARP i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:13: ( ( SHARP ID )=> SHARP i2= ID )
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:15: ( SHARP ID )=> SHARP i2= ID
                    	    {
                    	    SHARP16=(Token)match(input,SHARP,FOLLOW_SHARP_in_primary3074); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary3078); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(SHARP16, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 4 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:13: ( ( HASH ID )=> HASH i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:13: ( ( HASH ID )=> HASH i2= ID )
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:15: ( HASH ID )=> HASH i2= ID
                    	    {
                    	    HASH17=(Token)match(input,HASH,FOLLOW_HASH_in_primary3118); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary3122); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(HASH17, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 5 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:15: ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID
                    	    {
                    	    NULL_SAFE_DOT18=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_primary3162); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary3166); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(NULL_SAFE_DOT18, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop57;
                        }
                    } while (true);

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:12: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt58=2;
                    alt58 = dfa58.predict(input);
                    switch (alt58) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:13: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3188);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:547:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:548:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:548:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression3209); if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:548:21: ( expressionList )?
            int alt60=2;
            int LA60_0 = input.LA(1);

            if ( (LA60_0==FLOAT||(LA60_0>=HEX && LA60_0<=DECIMAL)||(LA60_0>=STRING && LA60_0<=TIME_INTERVAL)||(LA60_0>=BOOL && LA60_0<=NULL)||(LA60_0>=DECR && LA60_0<=INCR)||LA60_0==LESS||LA60_0==LEFT_PAREN||LA60_0==LEFT_SQUARE||(LA60_0>=NEGATION && LA60_0<=TILDE)||(LA60_0>=STAR && LA60_0<=PLUS)||LA60_0==ID) ) {
                alt60=1;
            }
            switch (alt60) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:548:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression3211);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression3214); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:551:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
         inMap++; 
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:553:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:553:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression3235); if (state.failed) return ;
            pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression3237);
            mapExpressionList();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3239); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:557:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:5: ( mapEntry ( COMMA mapEntry )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList3260);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:16: ( COMMA mapEntry )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==COMMA) ) {
                    alt61=1;
                }


                switch (alt61) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:558:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList3263); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList3265);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop61;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:561:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:562:5: ( expression COLON expression )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:562:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry3284);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry3286); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry3288);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:565:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
    public final BaseDescr parExpression() throws RecognitionException {
        BaseDescr result = null;

        DRL6Expressions.expression_return expr = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:566:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:566:7: LEFT_PAREN expr= expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression3309); if (state.failed) return result;
            pushFollow(FOLLOW_expression_in_parExpression3313);
            expr=expression();

            state._fsp--;
            if (state.failed) return result;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression3315); if (state.failed) return result;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:576:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        Token LEFT_SQUARE19=null;
        Token RIGHT_SQUARE20=null;
        Token DOT21=null;
        Token LEFT_SQUARE22=null;
        Token RIGHT_SQUARE23=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt64=3;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFT_SQUARE) ) {
                int LA64_1 = input.LA(2);

                if ( (LA64_1==RIGHT_SQUARE) && (synpred34_DRL6Expressions())) {
                    alt64=1;
                }
                else if ( (LA64_1==FLOAT||(LA64_1>=HEX && LA64_1<=DECIMAL)||(LA64_1>=STRING && LA64_1<=TIME_INTERVAL)||(LA64_1>=BOOL && LA64_1<=NULL)||(LA64_1>=DECR && LA64_1<=INCR)||LA64_1==LESS||LA64_1==LEFT_PAREN||LA64_1==LEFT_SQUARE||(LA64_1>=NEGATION && LA64_1<=TILDE)||(LA64_1>=STAR && LA64_1<=PLUS)||LA64_1==ID) ) {
                    alt64=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA64_0==LEFT_PAREN) ) {
                alt64=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt62=0;
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==LEFT_SQUARE) ) {
                            alt62=1;
                        }


                        switch (alt62) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE19=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3349); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE19, DroolsEditorType.SYMBOL); 
                    	    }
                    	    RIGHT_SQUARE20=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3390); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE20, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt62 >= 1 ) break loop62;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(62, input);
                                throw eee;
                        }
                        cnt62++;
                    } while (true);

                    DOT21=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix3434); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT21, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_class_key_in_identifierSuffix3438);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt63=0;
                    loop63:
                    do {
                        int alt63=2;
                        alt63 = dfa63.predict(input);
                        switch (alt63) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE22=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3453); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE22, DroolsEditorType.SYMBOL); 
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix3483);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    RIGHT_SQUARE23=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3511); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE23, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt63 >= 1 ) break loop63;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(63, input);
                                throw eee;
                        }
                        cnt63++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:583:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix3527);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:591:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:7: ( nonWildcardTypeArguments )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==LESS) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:592:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3549);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator3552);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:9: ( arrayCreatorRest | classCreatorRest )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==LEFT_SQUARE) ) {
                alt66=1;
            }
            else if ( (LA66_0==LEFT_PAREN) ) {
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator3563);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:593:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator3567);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:596:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:597:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt70=2;
            int LA70_0 = input.LA(1);

            if ( (LA70_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA70_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt70=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:597:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName3585); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:597:10: ( typeArguments )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==LESS) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:597:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName3587);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:9: ( DOT ID ( typeArguments )? )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);

                        if ( (LA69_0==DOT) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName3600); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName3602); if (state.failed) return ;
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:18: ( typeArguments )?
                    	    int alt68=2;
                    	    int LA68_0 = input.LA(1);

                    	    if ( (LA68_0==LESS) ) {
                    	        alt68=1;
                    	    }
                    	    switch (alt68) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:598:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName3604);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop69;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:599:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName3619);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:602:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:5: ({...}? => ID classCreatorRest )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:603:7: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator3639); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator3641);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:606:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:607:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:607:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3660); if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:608:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==RIGHT_SQUARE) ) {
                alt74=1;
            }
            else if ( (LA74_0==FLOAT||(LA74_0>=HEX && LA74_0<=DECIMAL)||(LA74_0>=STRING && LA74_0<=TIME_INTERVAL)||(LA74_0>=BOOL && LA74_0<=NULL)||(LA74_0>=DECR && LA74_0<=INCR)||LA74_0==LESS||LA74_0==LEFT_PAREN||LA74_0==LEFT_SQUARE||(LA74_0>=NEGATION && LA74_0<=TILDE)||(LA74_0>=STAR && LA74_0<=PLUS)||LA74_0==ID) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:608:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3670); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:608:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop71:
                    do {
                        int alt71=2;
                        int LA71_0 = input.LA(1);

                        if ( (LA71_0==LEFT_SQUARE) ) {
                            alt71=1;
                        }


                        switch (alt71) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:608:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3673); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3675); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop71;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3679);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest3693);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3695); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop72:
                    do {
                        int alt72=2;
                        alt72 = dfa72.predict(input);
                        switch (alt72) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3700); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest3702);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3704); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop72;
                        }
                    } while (true);

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop73:
                    do {
                        int alt73=2;
                        int LA73_0 = input.LA(1);

                        if ( (LA73_0==LEFT_SQUARE) ) {
                            int LA73_2 = input.LA(2);

                            if ( (LA73_2==RIGHT_SQUARE) && (synpred36_DRL6Expressions())) {
                                alt73=1;
                            }


                        }


                        switch (alt73) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3716); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3718); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop73;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:613:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:614:5: ( arrayInitializer | expression )
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==LEFT_CURLY) ) {
                alt75=1;
            }
            else if ( (LA75_0==FLOAT||(LA75_0>=HEX && LA75_0<=DECIMAL)||(LA75_0>=STRING && LA75_0<=TIME_INTERVAL)||(LA75_0>=BOOL && LA75_0<=NULL)||(LA75_0>=DECR && LA75_0<=INCR)||LA75_0==LESS||LA75_0==LEFT_PAREN||LA75_0==LEFT_SQUARE||(LA75_0>=NEGATION && LA75_0<=TILDE)||(LA75_0>=STAR && LA75_0<=PLUS)||LA75_0==ID) ) {
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
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:614:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer3747);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:615:13: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer3761);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:618:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer3778); if (state.failed) return ;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt78=2;
            int LA78_0 = input.LA(1);

            if ( (LA78_0==FLOAT||(LA78_0>=HEX && LA78_0<=DECIMAL)||(LA78_0>=STRING && LA78_0<=TIME_INTERVAL)||(LA78_0>=BOOL && LA78_0<=NULL)||(LA78_0>=DECR && LA78_0<=INCR)||LA78_0==LESS||LA78_0==LEFT_PAREN||LA78_0==LEFT_SQUARE||LA78_0==LEFT_CURLY||(LA78_0>=NEGATION && LA78_0<=TILDE)||(LA78_0>=STAR && LA78_0<=PLUS)||LA78_0==ID) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3781);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:39: ( COMMA variableInitializer )*
                    loop76:
                    do {
                        int alt76=2;
                        int LA76_0 = input.LA(1);

                        if ( (LA76_0==COMMA) ) {
                            int LA76_1 = input.LA(2);

                            if ( (LA76_1==FLOAT||(LA76_1>=HEX && LA76_1<=DECIMAL)||(LA76_1>=STRING && LA76_1<=TIME_INTERVAL)||(LA76_1>=BOOL && LA76_1<=NULL)||(LA76_1>=DECR && LA76_1<=INCR)||LA76_1==LESS||LA76_1==LEFT_PAREN||LA76_1==LEFT_SQUARE||LA76_1==LEFT_CURLY||(LA76_1>=NEGATION && LA76_1<=TILDE)||(LA76_1>=STAR && LA76_1<=PLUS)||LA76_1==ID) ) {
                                alt76=1;
                            }


                        }


                        switch (alt76) {
                    	case 1 :
                    	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:40: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3784); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3786);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop76;
                        }
                    } while (true);

                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:68: ( COMMA )?
                    int alt77=2;
                    int LA77_0 = input.LA(1);

                    if ( (LA77_0==COMMA) ) {
                        alt77=1;
                    }
                    switch (alt77) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:619:69: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3791); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer3798); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:622:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:623:5: ( arguments )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:623:7: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest3815);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:626:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:5: ( nonWildcardTypeArguments arguments )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:627:7: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3833);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation3835);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:630:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:631:5: ( LESS typeList GREATER )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:631:7: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments3852); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments3854);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments3856); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:634:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:635:5: ( super_key superSuffix | ID arguments )
            int alt79=2;
            int LA79_0 = input.LA(1);

            if ( (LA79_0==ID) ) {
                int LA79_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt79=1;
                }
                else if ( (true) ) {
                    alt79=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 79, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:635:7: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix3873);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3875);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:636:10: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix3886); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix3888);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:639:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        Token DOT24=null;
        Token DOT25=null;
        Token DOT26=null;
        Token ID27=null;
        Token LEFT_SQUARE28=null;
        Token RIGHT_SQUARE29=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt82=4;
            int LA82_0 = input.LA(1);

            if ( (LA82_0==DOT) ) {
                int LA82_1 = input.LA(2);

                if ( (synpred37_DRL6Expressions()) ) {
                    alt82=1;
                }
                else if ( (synpred38_DRL6Expressions()) ) {
                    alt82=2;
                }
                else if ( (synpred39_DRL6Expressions()) ) {
                    alt82=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 82, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA82_0==LEFT_SQUARE) && (synpred41_DRL6Expressions())) {
                alt82=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 82, 0, input);

                throw nvae;
            }
            switch (alt82) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    DOT24=(Token)match(input,DOT,FOLLOW_DOT_in_selector3913); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT24, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_super_key_in_selector3917);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector3919);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:641:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    DOT25=(Token)match(input,DOT,FOLLOW_DOT_in_selector3935); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT25, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_new_key_in_selector3939);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:641:84: ( nonWildcardTypeArguments )?
                    int alt80=2;
                    int LA80_0 = input.LA(1);

                    if ( (LA80_0==LESS) ) {
                        alt80=1;
                    }
                    switch (alt80) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:641:85: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector3942);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector3946);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    DOT26=(Token)match(input,DOT,FOLLOW_DOT_in_selector3962); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT26, DroolsEditorType.SYMBOL); 
                    }
                    ID27=(Token)match(input,ID,FOLLOW_ID_in_selector3984); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(ID27, DroolsEditorType.IDENTIFIER); 
                    }
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:19: ( ( LEFT_PAREN )=> arguments )?
                    int alt81=2;
                    alt81 = dfa81.predict(input);
                    switch (alt81) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:20: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector4013);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:646:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    LEFT_SQUARE28=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector4034); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(LEFT_SQUARE28, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_expression_in_selector4061);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    RIGHT_SQUARE29=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector4086); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(RIGHT_SQUARE29, DroolsEditorType.SYMBOL); 
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:651:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:652:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt84=2;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==LEFT_PAREN) ) {
                alt84=1;
            }
            else if ( (LA84_0==DOT) ) {
                alt84=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:652:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix4105);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix4116); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix4118); if (state.failed) return ;
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt83=2;
                    alt83 = dfa83.predict(input);
                    switch (alt83) {
                        case 1 :
                            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix4127);
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:656:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
    public final java.util.List<String> squareArguments() throws RecognitionException {
        java.util.List<String> args = null;

        java.util.List<String> el = null;


        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:657:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:657:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments4150); if (state.failed) return args;
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:657:19: (el= expressionList )?
            int alt85=2;
            int LA85_0 = input.LA(1);

            if ( (LA85_0==FLOAT||(LA85_0>=HEX && LA85_0<=DECIMAL)||(LA85_0>=STRING && LA85_0<=TIME_INTERVAL)||(LA85_0>=BOOL && LA85_0<=NULL)||(LA85_0>=DECR && LA85_0<=INCR)||LA85_0==LESS||LA85_0==LEFT_PAREN||LA85_0==LEFT_SQUARE||(LA85_0>=NEGATION && LA85_0<=TILDE)||(LA85_0>=STAR && LA85_0<=PLUS)||LA85_0==ID) ) {
                alt85=1;
            }
            switch (alt85) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:657:20: el= expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments4155);
                    el=expressionList();

                    state._fsp--;
                    if (state.failed) return args;
                    if ( state.backtracking==0 ) {
                       args = el; 
                    }

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments4161); if (state.failed) return args;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:660:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        Token LEFT_PAREN30=null;
        Token RIGHT_PAREN31=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:661:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:661:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            LEFT_PAREN30=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments4178); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(LEFT_PAREN30, DroolsEditorType.SYMBOL); 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:662:9: ( expressionList )?
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==FLOAT||(LA86_0>=HEX && LA86_0<=DECIMAL)||(LA86_0>=STRING && LA86_0<=TIME_INTERVAL)||(LA86_0>=BOOL && LA86_0<=NULL)||(LA86_0>=DECR && LA86_0<=INCR)||LA86_0==LESS||LA86_0==LEFT_PAREN||LA86_0==LEFT_SQUARE||(LA86_0>=NEGATION && LA86_0<=TILDE)||(LA86_0>=STAR && LA86_0<=PLUS)||LA86_0==ID) ) {
                alt86=1;
            }
            switch (alt86) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:662:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments4190);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            RIGHT_PAREN31=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments4201); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(RIGHT_PAREN31, DroolsEditorType.SYMBOL); 
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:666:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
    public final java.util.List<String> expressionList() throws RecognitionException {
        java.util.List<String> exprs = null;

        DRL6Expressions.expression_return f = null;

        DRL6Expressions.expression_return s = null;


         exprs = new java.util.ArrayList<String>();
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:668:3: (f= expression ( COMMA s= expression )* )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:668:7: f= expression ( COMMA s= expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList4231);
            f=expression();

            state._fsp--;
            if (state.failed) return exprs;
            if ( state.backtracking==0 ) {
               exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); 
            }
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:669:7: ( COMMA s= expression )*
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);

                if ( (LA87_0==COMMA) ) {
                    alt87=1;
                }


                switch (alt87) {
            	case 1 :
            	    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:669:8: COMMA s= expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList4242); if (state.failed) return exprs;
            	    pushFollow(FOLLOW_expression_in_expressionList4246);
            	    s=expression();

            	    state._fsp--;
            	    if (state.failed) return exprs;
            	    if ( state.backtracking==0 ) {
            	       exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop87;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:672:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:673:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt88=12;
            alt88 = dfa88.predict(input);
            switch (alt88) {
                case 1 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:673:9: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4267); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:674:7: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator4275); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:675:7: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator4283); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:676:7: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator4291); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:677:7: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator4299); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:678:7: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator4307); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:679:7: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator4315); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:680:7: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator4323); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:681:7: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator4331); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:682:7: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator4339); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator4341); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4343); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:683:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4360); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4362); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4364); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4366); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:684:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4381); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4383); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4385); if (state.failed) return ;

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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:690:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:691:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key4415); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:694:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:695:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:695:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key4444); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:698:1: instanceof_key : {...}? =>id= ID ;
    public final DRL6Expressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRL6Expressions.instanceof_key_return retval = new DRL6Expressions.instanceof_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:699:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:699:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key4473); if (state.failed) return retval;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:702:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:703:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key4502); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:706:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:707:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key4531); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:710:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:711:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:711:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key4560); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:714:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:715:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:715:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key4589); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:718:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:719:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:719:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key4618); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:722:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:723:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:723:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key4647); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:726:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:727:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:727:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key4676); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:730:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:731:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key4705); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:734:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:735:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:735:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key4734); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:738:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:739:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:739:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key4763); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:742:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:743:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:743:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key4792); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:746:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:747:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:747:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key4822); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:750:1: not_key : {...}? =>id= ID ;
    public final void not_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:751:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:751:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key4851); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:754:1: in_key : {...}? =>id= ID ;
    public final void in_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:755:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:755:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key4878); if (state.failed) return ;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:758:1: operator_key : {...}? =>id= ID ;
    public final DRL6Expressions.operator_key_return operator_key() throws RecognitionException {
        DRL6Expressions.operator_key_return retval = new DRL6Expressions.operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:759:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:759:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4903); if (state.failed) return retval;
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
    // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:762:1: neg_operator_key : {...}? =>id= ID ;
    public final DRL6Expressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRL6Expressions.neg_operator_key_return retval = new DRL6Expressions.neg_operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:763:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:763:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4928); if (state.failed) return retval;
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

    // $ANTLR start synpred1_DRL6Expressions
    public final void synpred1_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:8: ( primitiveType )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRL6Expressions548);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRL6Expressions

    // $ANTLR start synpred2_DRL6Expressions
    public final void synpred2_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:138:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRL6Expressions559); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRL6Expressions561); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRL6Expressions

    // $ANTLR start synpred3_DRL6Expressions
    public final void synpred3_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:13: ( typeArguments )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRL6Expressions585);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRL6Expressions

    // $ANTLR start synpred4_DRL6Expressions
    public final void synpred4_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:55: ( typeArguments )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRL6Expressions599);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRL6Expressions

    // $ANTLR start synpred5_DRL6Expressions
    public final void synpred5_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:139:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRL6Expressions611); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRL6Expressions613); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRL6Expressions

    // $ANTLR start synpred6_DRL6Expressions
    public final void synpred6_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:10: ( assignmentOperator )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:165:11: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRL6Expressions782);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRL6Expressions

    // $ANTLR start synpred7_DRL6Expressions
    public final void synpred7_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:321:6: ( not_key in_key )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:321:7: not_key in_key
        {
        pushFollow(FOLLOW_not_key_in_synpred7_DRL6Expressions1515);
        not_key();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_in_key_in_synpred7_DRL6Expressions1517);
        in_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRL6Expressions

    // $ANTLR start synpred8_DRL6Expressions
    public final void synpred8_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:365:5: ( operator | LEFT_PAREN )
        int alt89=2;
        int LA89_0 = input.LA(1);

        if ( ((LA89_0>=EQUALS && LA89_0<=LESS)||LA89_0==TILDE) ) {
            alt89=1;
        }
        else if ( (LA89_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
            alt89=1;
        }
        else if ( (LA89_0==LEFT_PAREN) ) {
            alt89=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 89, 0, input);

            throw nvae;
        }
        switch (alt89) {
            case 1 :
                // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:365:7: operator
                {
                pushFollow(FOLLOW_operator_in_synpred8_DRL6Expressions1732);
                operator();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:365:18: LEFT_PAREN
                {
                match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRL6Expressions1736); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred8_DRL6Expressions

    // $ANTLR start synpred9_DRL6Expressions
    public final void synpred9_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:8: DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred9_DRL6Expressions1789); if (state.failed) return ;
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:20: ( fullAnnotation[null] )?
        int alt90=2;
        int LA90_0 = input.LA(1);

        if ( (LA90_0==AT) ) {
            alt90=1;
        }
        switch (alt90) {
            case 1 :
                // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:376:20: fullAnnotation[null]
                {
                pushFollow(FOLLOW_fullAnnotation_in_synpred9_DRL6Expressions1791);
                fullAnnotation(null);

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_andRestriction_in_synpred9_DRL6Expressions1795);
        andRestriction();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRL6Expressions

    // $ANTLR start synpred10_DRL6Expressions
    public final void synpred10_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:6: DOUBLE_AMPER ( fullAnnotation[null] )? operator
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred10_DRL6Expressions1858); if (state.failed) return ;
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:19: ( fullAnnotation[null] )?
        int alt91=2;
        int LA91_0 = input.LA(1);

        if ( (LA91_0==AT) ) {
            alt91=1;
        }
        switch (alt91) {
            case 1 :
                // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:390:19: fullAnnotation[null]
                {
                pushFollow(FOLLOW_fullAnnotation_in_synpred10_DRL6Expressions1860);
                fullAnnotation(null);

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_operator_in_synpred10_DRL6Expressions1864);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRL6Expressions

    // $ANTLR start synpred11_DRL6Expressions
    public final void synpred11_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:8: ( squareArguments shiftExpression )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:407:9: squareArguments shiftExpression
        {
        pushFollow(FOLLOW_squareArguments_in_synpred11_DRL6Expressions1952);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_shiftExpression_in_synpred11_DRL6Expressions1954);
        shiftExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRL6Expressions

    // $ANTLR start synpred12_DRL6Expressions
    public final void synpred12_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:430:7: ( shiftOp )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:430:8: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred12_DRL6Expressions2043);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_DRL6Expressions

    // $ANTLR start synpred13_DRL6Expressions
    public final void synpred13_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:441:11: ( PLUS | MINUS )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:
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
    // $ANTLR end synpred13_DRL6Expressions

    // $ANTLR start synpred14_DRL6Expressions
    public final void synpred14_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:473:9: ( castExpression )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:473:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred14_DRL6Expressions2363);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRL6Expressions

    // $ANTLR start synpred15_DRL6Expressions
    public final void synpred15_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:10: ( selector )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:481:11: selector
        {
        pushFollow(FOLLOW_selector_in_synpred15_DRL6Expressions2496);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DRL6Expressions

    // $ANTLR start synpred16_DRL6Expressions
    public final void synpred16_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:500:10: ( INCR | DECR )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:
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
    // $ANTLR end synpred16_DRL6Expressions

    // $ANTLR start synpred17_DRL6Expressions
    public final void synpred17_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:8: ( LEFT_PAREN primitiveType )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:504:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRL6Expressions2554); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred17_DRL6Expressions2556);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRL6Expressions

    // $ANTLR start synpred18_DRL6Expressions
    public final void synpred18_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:505:8: ( LEFT_PAREN type )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:505:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred18_DRL6Expressions2579); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred18_DRL6Expressions2581);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRL6Expressions

    // $ANTLR start synpred19_DRL6Expressions
    public final void synpred19_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:520:7: ( parExpression )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:520:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred19_DRL6Expressions2689);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRL6Expressions

    // $ANTLR start synpred20_DRL6Expressions
    public final void synpred20_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:9: ( nonWildcardTypeArguments )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:521:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred20_DRL6Expressions2708);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRL6Expressions

    // $ANTLR start synpred21_DRL6Expressions
    public final void synpred21_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:9: ( literal )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:522:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred21_DRL6Expressions2733);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRL6Expressions

    // $ANTLR start synpred22_DRL6Expressions
    public final void synpred22_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:524:9: ( super_key )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:524:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred22_DRL6Expressions2755);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRL6Expressions

    // $ANTLR start synpred23_DRL6Expressions
    public final void synpred23_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:525:9: ( new_key )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:525:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred23_DRL6Expressions2772);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRL6Expressions

    // $ANTLR start synpred24_DRL6Expressions
    public final void synpred24_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:526:9: ( primitiveType )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:526:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred24_DRL6Expressions2789);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRL6Expressions

    // $ANTLR start synpred25_DRL6Expressions
    public final void synpred25_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:528:9: ( inlineMapExpression )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:528:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred25_DRL6Expressions2820);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRL6Expressions

    // $ANTLR start synpred26_DRL6Expressions
    public final void synpred26_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:529:9: ( inlineListExpression )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:529:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred26_DRL6Expressions2835);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRL6Expressions

    // $ANTLR start synpred27_DRL6Expressions
    public final void synpred27_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:530:9: ( ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:530:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred27_DRL6Expressions2850); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRL6Expressions

    // $ANTLR start synpred28_DRL6Expressions
    public final void synpred28_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:532:15: ( DOT ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:532:16: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred28_DRL6Expressions2884); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred28_DRL6Expressions2886); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRL6Expressions

    // $ANTLR start synpred29_DRL6Expressions
    public final void synpred29_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:534:15: ( DOT LEFT_PAREN )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:534:16: DOT LEFT_PAREN
        {
        match(input,DOT,FOLLOW_DOT_in_synpred29_DRL6Expressions2928); if (state.failed) return ;
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred29_DRL6Expressions2930); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRL6Expressions

    // $ANTLR start synpred30_DRL6Expressions
    public final void synpred30_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:15: ( SHARP ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:539:16: SHARP ID
        {
        match(input,SHARP,FOLLOW_SHARP_in_synpred30_DRL6Expressions3069); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred30_DRL6Expressions3071); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRL6Expressions

    // $ANTLR start synpred31_DRL6Expressions
    public final void synpred31_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:15: ( HASH ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:541:16: HASH ID
        {
        match(input,HASH,FOLLOW_HASH_in_synpred31_DRL6Expressions3113); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred31_DRL6Expressions3115); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRL6Expressions

    // $ANTLR start synpred32_DRL6Expressions
    public final void synpred32_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:15: ( NULL_SAFE_DOT ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:543:16: NULL_SAFE_DOT ID
        {
        match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred32_DRL6Expressions3157); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred32_DRL6Expressions3159); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRL6Expressions

    // $ANTLR start synpred33_DRL6Expressions
    public final void synpred33_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:13: ( identifierSuffix )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:544:14: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred33_DRL6Expressions3185);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRL6Expressions

    // $ANTLR start synpred34_DRL6Expressions
    public final void synpred34_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:577:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred34_DRL6Expressions3343); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred34_DRL6Expressions3345); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRL6Expressions

    // $ANTLR start synpred35_DRL6Expressions
    public final void synpred35_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:8: ( LEFT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:580:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred35_DRL6Expressions3448); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRL6Expressions

    // $ANTLR start synpred36_DRL6Expressions
    public final void synpred36_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:609:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred36_DRL6Expressions3710); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred36_DRL6Expressions3712); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRL6Expressions

    // $ANTLR start synpred37_DRL6Expressions
    public final void synpred37_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:9: ( DOT super_key )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:640:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred37_DRL6Expressions3908); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred37_DRL6Expressions3910);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRL6Expressions

    // $ANTLR start synpred38_DRL6Expressions
    public final void synpred38_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:641:9: ( DOT new_key )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:641:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred38_DRL6Expressions3930); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred38_DRL6Expressions3932);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRL6Expressions

    // $ANTLR start synpred39_DRL6Expressions
    public final void synpred39_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:9: ( DOT ID )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:642:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred39_DRL6Expressions3957); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred39_DRL6Expressions3959); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DRL6Expressions

    // $ANTLR start synpred40_DRL6Expressions
    public final void synpred40_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:20: ( LEFT_PAREN )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:644:21: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred40_DRL6Expressions4008); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_DRL6Expressions

    // $ANTLR start synpred41_DRL6Expressions
    public final void synpred41_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:646:9: ( LEFT_SQUARE )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:646:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred41_DRL6Expressions4031); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_DRL6Expressions

    // $ANTLR start synpred42_DRL6Expressions
    public final void synpred42_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:18: ( LEFT_PAREN )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:653:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred42_DRL6Expressions4122); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred42_DRL6Expressions

    // $ANTLR start synpred43_DRL6Expressions
    public final void synpred43_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:683:7: ( GREATER GREATER GREATER )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:683:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRL6Expressions4352); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRL6Expressions4354); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRL6Expressions4356); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred43_DRL6Expressions

    // $ANTLR start synpred44_DRL6Expressions
    public final void synpred44_DRL6Expressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:684:7: ( GREATER GREATER )
        // src/main/resources/org/drools/compiler/lang/DRL6Expressions.g:684:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred44_DRL6Expressions4375); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred44_DRL6Expressions4377); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred44_DRL6Expressions

    // Delegated rules

    public final boolean synpred21_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred21_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred33_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred33_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred42_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred22_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred22_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred39_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred24_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred26_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred23_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred23_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred30_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred30_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred41_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred38_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred38_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred44_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred44_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred37_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred37_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred20_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred20_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred28_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred28_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred25_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred43_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred43_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred27_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred29_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred29_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred31_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred31_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred36_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred36_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred35_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred35_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred32_DRL6Expressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred32_DRL6Expressions_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA16 dfa16 = new DFA16(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA59 dfa59 = new DFA59(this);
    protected DFA58 dfa58 = new DFA58(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA72 dfa72 = new DFA72(this);
    protected DFA81 dfa81 = new DFA81(this);
    protected DFA83 dfa83 = new DFA83(this);
    protected DFA88 dfa88 = new DFA88(this);
    static final String DFA16_eotS =
        "\16\uffff";
    static final String DFA16_eofS =
        "\16\uffff";
    static final String DFA16_minS =
        "\1\25\13\0\2\uffff";
    static final String DFA16_maxS =
        "\1\105\13\0\2\uffff";
    static final String DFA16_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA16_specialS =
        "\1\uffff\1\2\1\10\1\7\1\12\1\11\1\1\1\0\1\5\1\4\1\6\1\3\2\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\14\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\4\uffff\2\14\4\uffff"+
            "\1\13\1\12\1\1\1\uffff\1\14\1\uffff\1\14\1\uffff\2\14\23\uffff"+
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

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "165:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA16_7 = input.LA(1);

                         
                        int index16_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_7);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA16_6 = input.LA(1);

                         
                        int index16_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA16_1 = input.LA(1);

                         
                        int index16_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA16_11 = input.LA(1);

                         
                        int index16_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_11);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA16_9 = input.LA(1);

                         
                        int index16_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_9);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA16_8 = input.LA(1);

                         
                        int index16_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_8);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA16_10 = input.LA(1);

                         
                        int index16_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_10);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA16_3 = input.LA(1);

                         
                        int index16_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_3);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA16_2 = input.LA(1);

                         
                        int index16_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_2);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA16_5 = input.LA(1);

                         
                        int index16_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_5);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA16_4 = input.LA(1);

                         
                        int index16_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRL6Expressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 16, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA35_eotS =
        "\40\uffff";
    static final String DFA35_eofS =
        "\1\1\37\uffff";
    static final String DFA35_minS =
        "\1\25\1\uffff\3\0\17\uffff\2\0\12\uffff";
    static final String DFA35_maxS =
        "\1\105\1\uffff\3\0\17\uffff\2\0\12\uffff";
    static final String DFA35_acceptS =
        "\1\uffff\1\2\32\uffff\4\1";
    static final String DFA35_specialS =
        "\1\0\1\uffff\1\1\1\2\1\3\17\uffff\1\4\1\5\12\uffff}>";
    static final String[] DFA35_transitionS = {
            "\11\1\4\uffff\2\1\1\3\1\4\1\36\1\35\1\25\1\24\1\1\1\37\1\1\1"+
            "\uffff\1\1\1\uffff\2\1\2\uffff\3\1\1\uffff\1\34\3\1\11\uffff"+
            "\1\2",
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
            return "()* loopback of 365:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA35_0 = input.LA(1);

                         
                        int index35_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA35_0==EOF||(LA35_0>=AT && LA35_0<=MOD_ASSIGN)||(LA35_0>=SEMICOLON && LA35_0<=COLON)||LA35_0==EQUALS_ASSIGN||LA35_0==RIGHT_PAREN||LA35_0==RIGHT_SQUARE||(LA35_0>=RIGHT_CURLY && LA35_0<=COMMA)||(LA35_0>=DOUBLE_AMPER && LA35_0<=QUESTION)||(LA35_0>=PIPE && LA35_0<=XOR)) ) {s = 1;}

                        else if ( (LA35_0==ID) ) {s = 2;}

                        else if ( (LA35_0==EQUALS) ) {s = 3;}

                        else if ( (LA35_0==NOT_EQUALS) ) {s = 4;}

                        else if ( (LA35_0==LESS) ) {s = 20;}

                        else if ( (LA35_0==GREATER) ) {s = 21;}

                        else if ( (LA35_0==TILDE) && (synpred8_DRL6Expressions())) {s = 28;}

                        else if ( (LA35_0==LESS_EQUALS) && (synpred8_DRL6Expressions())) {s = 29;}

                        else if ( (LA35_0==GREATER_EQUALS) && (synpred8_DRL6Expressions())) {s = 30;}

                        else if ( (LA35_0==LEFT_PAREN) && (synpred8_DRL6Expressions())) {s = 31;}

                         
                        input.seek(index35_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA35_2 = input.LA(1);

                         
                        int index35_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred8_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT))))||(synpred8_DRL6Expressions()&&((helper.isPluggableEvaluator(false)))))) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA35_3 = input.LA(1);

                         
                        int index35_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL6Expressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA35_4 = input.LA(1);

                         
                        int index35_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL6Expressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA35_20 = input.LA(1);

                         
                        int index35_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL6Expressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_20);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA35_21 = input.LA(1);

                         
                        int index35_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRL6Expressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index35_21);
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
    static final String DFA37_eotS =
        "\41\uffff";
    static final String DFA37_eofS =
        "\1\1\40\uffff";
    static final String DFA37_minS =
        "\1\25\10\uffff\1\0\27\uffff";
    static final String DFA37_maxS =
        "\1\105\10\uffff\1\0\27\uffff";
    static final String DFA37_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA37_specialS =
        "\11\uffff\1\0\27\uffff}>";
    static final String[] DFA37_transitionS = {
            "\11\1\4\uffff\13\1\1\uffff\1\1\1\uffff\2\1\2\uffff\1\1\1\11"+
            "\1\1\1\uffff\4\1\11\uffff\1\1",
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
            "",
            ""
    };

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "()* loopback of 376:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA37_9 = input.LA(1);

                         
                        int index37_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRL6Expressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 37, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA40_eotS =
        "\41\uffff";
    static final String DFA40_eofS =
        "\1\1\40\uffff";
    static final String DFA40_minS =
        "\1\25\10\uffff\1\0\27\uffff";
    static final String DFA40_maxS =
        "\1\105\10\uffff\1\0\27\uffff";
    static final String DFA40_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA40_specialS =
        "\11\uffff\1\0\27\uffff}>";
    static final String[] DFA40_transitionS = {
            "\11\1\4\uffff\13\1\1\uffff\1\1\1\uffff\2\1\2\uffff\1\11\2\1"+
            "\1\uffff\4\1\11\uffff\1\1",
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
            "",
            ""
    };

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        public String getDescription() {
            return "()* loopback of 390:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA40_9 = input.LA(1);

                         
                        int index40_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRL6Expressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index40_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 40, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA41_eotS =
        "\24\uffff";
    static final String DFA41_eofS =
        "\24\uffff";
    static final String DFA41_minS =
        "\1\11\1\0\22\uffff";
    static final String DFA41_maxS =
        "\1\105\1\0\22\uffff";
    static final String DFA41_acceptS =
        "\2\uffff\1\2\20\uffff\1\1";
    static final String DFA41_specialS =
        "\1\uffff\1\0\22\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\2\2\2\uffff\2\2\12\uffff\2\2\10\uffff"+
            "\1\2\1\uffff\1\2\1\uffff\1\1\11\uffff\2\2\4\uffff\3\2\5\uffff"+
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
            "",
            ""
    };

    static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
    static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
    static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
    static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
    static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
    static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
    static final short[][] DFA41_transition;

    static {
        int numStates = DFA41_transitionS.length;
        DFA41_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
        }
    }

    class DFA41 extends DFA {

        public DFA41(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA41_eot;
            this.eof = DFA41_eof;
            this.min = DFA41_min;
            this.max = DFA41_max;
            this.accept = DFA41_accept;
            this.special = DFA41_special;
            this.transition = DFA41_transition;
        }
        public String getDescription() {
            return "407:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA41_1 = input.LA(1);

                         
                        int index41_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRL6Expressions()) ) {s = 19;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index41_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 41, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA43_eotS =
        "\42\uffff";
    static final String DFA43_eofS =
        "\1\1\41\uffff";
    static final String DFA43_minS =
        "\1\25\5\uffff\2\0\32\uffff";
    static final String DFA43_maxS =
        "\1\105\5\uffff\2\0\32\uffff";
    static final String DFA43_acceptS =
        "\1\uffff\1\2\37\uffff\1\1";
    static final String DFA43_specialS =
        "\6\uffff\1\0\1\1\32\uffff}>";
    static final String[] DFA43_transitionS = {
            "\11\1\4\uffff\6\1\1\7\1\6\3\1\1\uffff\1\1\1\uffff\2\1\2\uffff"+
            "\3\1\1\uffff\4\1\11\uffff\1\1",
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
            return "()* loopback of 430:5: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA43_6 = input.LA(1);

                         
                        int index43_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRL6Expressions()) ) {s = 33;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index43_6);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA43_7 = input.LA(1);

                         
                        int index43_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRL6Expressions()) ) {s = 33;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index43_7);
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
    static final String DFA51_eotS =
        "\20\uffff";
    static final String DFA51_eofS =
        "\20\uffff";
    static final String DFA51_minS =
        "\1\11\2\uffff\1\0\14\uffff";
    static final String DFA51_maxS =
        "\1\105\2\uffff\1\0\14\uffff";
    static final String DFA51_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\12\uffff\1\3";
    static final String DFA51_specialS =
        "\3\uffff\1\0\14\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\4\2\uffff\2\4\1\uffff\2\4\2\uffff\2\4\24\uffff\1\4\1\uffff"+
            "\1\3\1\uffff\1\4\11\uffff\1\2\1\1\4\uffff\1\4\7\uffff\1\4",
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
            ""
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "469:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA51_3 = input.LA(1);

                         
                        int index51_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_DRL6Expressions()) ) {s = 15;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index51_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 51, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA53_eotS =
        "\12\uffff";
    static final String DFA53_eofS =
        "\12\uffff";
    static final String DFA53_minS =
        "\1\105\1\0\10\uffff";
    static final String DFA53_maxS =
        "\1\105\1\0\10\uffff";
    static final String DFA53_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA53_specialS =
        "\1\1\1\0\10\uffff}>";
    static final String[] DFA53_transitionS = {
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

    static final short[] DFA53_eot = DFA.unpackEncodedString(DFA53_eotS);
    static final short[] DFA53_eof = DFA.unpackEncodedString(DFA53_eofS);
    static final char[] DFA53_min = DFA.unpackEncodedStringToUnsignedChars(DFA53_minS);
    static final char[] DFA53_max = DFA.unpackEncodedStringToUnsignedChars(DFA53_maxS);
    static final short[] DFA53_accept = DFA.unpackEncodedString(DFA53_acceptS);
    static final short[] DFA53_special = DFA.unpackEncodedString(DFA53_specialS);
    static final short[][] DFA53_transition;

    static {
        int numStates = DFA53_transitionS.length;
        DFA53_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA53_transition[i] = DFA.unpackEncodedString(DFA53_transitionS[i]);
        }
    }

    class DFA53 extends DFA {

        public DFA53(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 53;
            this.eot = DFA53_eot;
            this.eof = DFA53_eof;
            this.min = DFA53_min;
            this.max = DFA53_max;
            this.accept = DFA53_accept;
            this.special = DFA53_special;
            this.transition = DFA53_transition;
        }
        public String getDescription() {
            return "508:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA53_1 = input.LA(1);

                         
                        int index53_1 = input.index();
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

                         
                        input.seek(index53_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA53_0 = input.LA(1);

                         
                        int index53_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA53_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index53_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 53, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA59_eotS =
        "\23\uffff";
    static final String DFA59_eofS =
        "\23\uffff";
    static final String DFA59_minS =
        "\1\11\12\uffff\2\0\6\uffff";
    static final String DFA59_maxS =
        "\1\105\12\uffff\2\0\6\uffff";
    static final String DFA59_acceptS =
        "\1\uffff\1\1\1\2\10\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA59_specialS =
        "\1\0\12\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA59_transitionS = {
            "\1\6\2\uffff\1\5\1\4\1\uffff\1\3\1\11\2\uffff\1\7\1\10\24\uffff"+
            "\1\2\1\uffff\1\1\1\uffff\1\14\17\uffff\1\12\7\uffff\1\13",
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
            ""
    };

    static final short[] DFA59_eot = DFA.unpackEncodedString(DFA59_eotS);
    static final short[] DFA59_eof = DFA.unpackEncodedString(DFA59_eofS);
    static final char[] DFA59_min = DFA.unpackEncodedStringToUnsignedChars(DFA59_minS);
    static final char[] DFA59_max = DFA.unpackEncodedStringToUnsignedChars(DFA59_maxS);
    static final short[] DFA59_accept = DFA.unpackEncodedString(DFA59_acceptS);
    static final short[] DFA59_special = DFA.unpackEncodedString(DFA59_specialS);
    static final short[][] DFA59_transition;

    static {
        int numStates = DFA59_transitionS.length;
        DFA59_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA59_transition[i] = DFA.unpackEncodedString(DFA59_transitionS[i]);
        }
    }

    class DFA59 extends DFA {

        public DFA59(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 59;
            this.eot = DFA59_eot;
            this.eof = DFA59_eof;
            this.min = DFA59_min;
            this.max = DFA59_max;
            this.accept = DFA59_accept;
            this.special = DFA59_special;
            this.transition = DFA59_transition;
        }
        public String getDescription() {
            return "519:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( SHARP ID )=> SHARP i2= ID ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA59_0 = input.LA(1);

                         
                        int index59_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA59_0==LEFT_PAREN) && (synpred19_DRL6Expressions())) {s = 1;}

                        else if ( (LA59_0==LESS) && (synpred20_DRL6Expressions())) {s = 2;}

                        else if ( (LA59_0==STRING) && (synpred21_DRL6Expressions())) {s = 3;}

                        else if ( (LA59_0==DECIMAL) && (synpred21_DRL6Expressions())) {s = 4;}

                        else if ( (LA59_0==HEX) && (synpred21_DRL6Expressions())) {s = 5;}

                        else if ( (LA59_0==FLOAT) && (synpred21_DRL6Expressions())) {s = 6;}

                        else if ( (LA59_0==BOOL) && (synpred21_DRL6Expressions())) {s = 7;}

                        else if ( (LA59_0==NULL) && (synpred21_DRL6Expressions())) {s = 8;}

                        else if ( (LA59_0==TIME_INTERVAL) && (synpred21_DRL6Expressions())) {s = 9;}

                        else if ( (LA59_0==STAR) && (synpred21_DRL6Expressions())) {s = 10;}

                        else if ( (LA59_0==ID) ) {s = 11;}

                        else if ( (LA59_0==LEFT_SQUARE) ) {s = 12;}

                         
                        input.seek(index59_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA59_11 = input.LA(1);

                         
                        int index59_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred22_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 13;}

                        else if ( ((synpred23_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 14;}

                        else if ( (((synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred24_DRL6Expressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) ) {s = 15;}

                        else if ( (synpred27_DRL6Expressions()) ) {s = 16;}

                         
                        input.seek(index59_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA59_12 = input.LA(1);

                         
                        int index59_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred25_DRL6Expressions()) ) {s = 17;}

                        else if ( (synpred26_DRL6Expressions()) ) {s = 18;}

                         
                        input.seek(index59_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 59, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA58_eotS =
        "\47\uffff";
    static final String DFA58_eofS =
        "\1\3\46\uffff";
    static final String DFA58_minS =
        "\1\25\2\0\44\uffff";
    static final String DFA58_maxS =
        "\1\106\2\0\44\uffff";
    static final String DFA58_acceptS =
        "\3\uffff\1\2\42\uffff\1\1";
    static final String DFA58_specialS =
        "\1\uffff\1\0\1\1\44\uffff}>";
    static final String[] DFA58_transitionS = {
            "\11\3\1\uffff\2\3\1\uffff\11\3\1\2\1\3\1\1\1\3\1\uffff\3\3\1"+
            "\uffff\3\3\1\uffff\10\3\5\uffff\2\3",
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
            "",
            ""
    };

    static final short[] DFA58_eot = DFA.unpackEncodedString(DFA58_eotS);
    static final short[] DFA58_eof = DFA.unpackEncodedString(DFA58_eofS);
    static final char[] DFA58_min = DFA.unpackEncodedStringToUnsignedChars(DFA58_minS);
    static final char[] DFA58_max = DFA.unpackEncodedStringToUnsignedChars(DFA58_maxS);
    static final short[] DFA58_accept = DFA.unpackEncodedString(DFA58_acceptS);
    static final short[] DFA58_special = DFA.unpackEncodedString(DFA58_specialS);
    static final short[][] DFA58_transition;

    static {
        int numStates = DFA58_transitionS.length;
        DFA58_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA58_transition[i] = DFA.unpackEncodedString(DFA58_transitionS[i]);
        }
    }

    class DFA58 extends DFA {

        public DFA58(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 58;
            this.eot = DFA58_eot;
            this.eof = DFA58_eof;
            this.min = DFA58_min;
            this.max = DFA58_max;
            this.accept = DFA58_accept;
            this.special = DFA58_special;
            this.transition = DFA58_transition;
        }
        public String getDescription() {
            return "544:12: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA58_1 = input.LA(1);

                         
                        int index58_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred33_DRL6Expressions()) ) {s = 38;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA58_2 = input.LA(1);

                         
                        int index58_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred33_DRL6Expressions()) ) {s = 38;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index58_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 58, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA63_eotS =
        "\47\uffff";
    static final String DFA63_eofS =
        "\1\1\46\uffff";
    static final String DFA63_minS =
        "\1\25\43\uffff\1\0\2\uffff";
    static final String DFA63_maxS =
        "\1\106\43\uffff\1\0\2\uffff";
    static final String DFA63_acceptS =
        "\1\uffff\1\2\44\uffff\1\1";
    static final String DFA63_specialS =
        "\44\uffff\1\0\2\uffff}>";
    static final String[] DFA63_transitionS = {
            "\11\1\1\uffff\2\1\1\uffff\13\1\1\44\1\1\1\uffff\3\1\1\uffff"+
            "\3\1\1\uffff\10\1\5\uffff\2\1",
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
            "\1\uffff",
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
            return "()+ loopback of 580:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_36 = input.LA(1);

                         
                        int index63_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred35_DRL6Expressions()) ) {s = 38;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index63_36);
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
        "\47\uffff";
    static final String DFA72_eofS =
        "\1\2\46\uffff";
    static final String DFA72_minS =
        "\1\25\1\0\45\uffff";
    static final String DFA72_maxS =
        "\1\106\1\0\45\uffff";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\43\uffff\1\1";
    static final String DFA72_specialS =
        "\1\uffff\1\0\45\uffff}>";
    static final String[] DFA72_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\13\2\1\1\1\2\1\uffff\3\2\1\uffff\3"+
            "\2\1\uffff\10\2\5\uffff\2\2",
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
            return "()* loopback of 609:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
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
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 38;}

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
    static final String DFA81_eotS =
        "\47\uffff";
    static final String DFA81_eofS =
        "\1\2\46\uffff";
    static final String DFA81_minS =
        "\1\25\1\0\45\uffff";
    static final String DFA81_maxS =
        "\1\106\1\0\45\uffff";
    static final String DFA81_acceptS =
        "\2\uffff\1\2\43\uffff\1\1";
    static final String DFA81_specialS =
        "\1\uffff\1\0\45\uffff}>";
    static final String[] DFA81_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\11\2\1\1\3\2\1\uffff\3\2\1\uffff\3"+
            "\2\1\uffff\10\2\5\uffff\2\2",
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
            "",
            ""
    };

    static final short[] DFA81_eot = DFA.unpackEncodedString(DFA81_eotS);
    static final short[] DFA81_eof = DFA.unpackEncodedString(DFA81_eofS);
    static final char[] DFA81_min = DFA.unpackEncodedStringToUnsignedChars(DFA81_minS);
    static final char[] DFA81_max = DFA.unpackEncodedStringToUnsignedChars(DFA81_maxS);
    static final short[] DFA81_accept = DFA.unpackEncodedString(DFA81_acceptS);
    static final short[] DFA81_special = DFA.unpackEncodedString(DFA81_specialS);
    static final short[][] DFA81_transition;

    static {
        int numStates = DFA81_transitionS.length;
        DFA81_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA81_transition[i] = DFA.unpackEncodedString(DFA81_transitionS[i]);
        }
    }

    class DFA81 extends DFA {

        public DFA81(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 81;
            this.eot = DFA81_eot;
            this.eof = DFA81_eof;
            this.min = DFA81_min;
            this.max = DFA81_max;
            this.accept = DFA81_accept;
            this.special = DFA81_special;
            this.transition = DFA81_transition;
        }
        public String getDescription() {
            return "644:19: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA81_1 = input.LA(1);

                         
                        int index81_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred40_DRL6Expressions()) ) {s = 38;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index81_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 81, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA83_eotS =
        "\47\uffff";
    static final String DFA83_eofS =
        "\1\2\46\uffff";
    static final String DFA83_minS =
        "\1\25\1\0\45\uffff";
    static final String DFA83_maxS =
        "\1\106\1\0\45\uffff";
    static final String DFA83_acceptS =
        "\2\uffff\1\2\43\uffff\1\1";
    static final String DFA83_specialS =
        "\1\uffff\1\0\45\uffff}>";
    static final String[] DFA83_transitionS = {
            "\11\2\1\uffff\2\2\1\uffff\11\2\1\1\3\2\1\uffff\3\2\1\uffff\3"+
            "\2\1\uffff\10\2\5\uffff\2\2",
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
            "",
            ""
    };

    static final short[] DFA83_eot = DFA.unpackEncodedString(DFA83_eotS);
    static final short[] DFA83_eof = DFA.unpackEncodedString(DFA83_eofS);
    static final char[] DFA83_min = DFA.unpackEncodedStringToUnsignedChars(DFA83_minS);
    static final char[] DFA83_max = DFA.unpackEncodedStringToUnsignedChars(DFA83_maxS);
    static final short[] DFA83_accept = DFA.unpackEncodedString(DFA83_acceptS);
    static final short[] DFA83_special = DFA.unpackEncodedString(DFA83_specialS);
    static final short[][] DFA83_transition;

    static {
        int numStates = DFA83_transitionS.length;
        DFA83_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA83_transition[i] = DFA.unpackEncodedString(DFA83_transitionS[i]);
        }
    }

    class DFA83 extends DFA {

        public DFA83(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 83;
            this.eot = DFA83_eot;
            this.eof = DFA83_eof;
            this.min = DFA83_min;
            this.max = DFA83_max;
            this.accept = DFA83_accept;
            this.special = DFA83_special;
            this.transition = DFA83_transition;
        }
        public String getDescription() {
            return "653:17: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA83_1 = input.LA(1);

                         
                        int index83_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred42_DRL6Expressions()) ) {s = 38;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index83_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 83, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA88_eotS =
        "\17\uffff";
    static final String DFA88_eofS =
        "\17\uffff";
    static final String DFA88_minS =
        "\1\26\12\uffff\2\50\2\uffff";
    static final String DFA88_maxS =
        "\1\52\12\uffff\1\50\1\52\2\uffff";
    static final String DFA88_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA88_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA88_transitionS = {
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

    static final short[] DFA88_eot = DFA.unpackEncodedString(DFA88_eotS);
    static final short[] DFA88_eof = DFA.unpackEncodedString(DFA88_eofS);
    static final char[] DFA88_min = DFA.unpackEncodedStringToUnsignedChars(DFA88_minS);
    static final char[] DFA88_max = DFA.unpackEncodedStringToUnsignedChars(DFA88_maxS);
    static final short[] DFA88_accept = DFA.unpackEncodedString(DFA88_acceptS);
    static final short[] DFA88_special = DFA.unpackEncodedString(DFA88_specialS);
    static final short[][] DFA88_transition;

    static {
        int numStates = DFA88_transitionS.length;
        DFA88_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA88_transition[i] = DFA.unpackEncodedString(DFA88_transitionS[i]);
        }
    }

    class DFA88 extends DFA {

        public DFA88(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 88;
            this.eot = DFA88_eot;
            this.eof = DFA88_eof;
            this.min = DFA88_min;
            this.max = DFA88_max;
            this.accept = DFA88_accept;
            this.special = DFA88_special;
            this.transition = DFA88_transition;
        }
        public String getDescription() {
            return "672:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA88_12 = input.LA(1);

                         
                        int index88_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA88_12==GREATER) && (synpred43_DRL6Expressions())) {s = 13;}

                        else if ( (LA88_12==EQUALS_ASSIGN) && (synpred44_DRL6Expressions())) {s = 14;}

                         
                        input.seek(index88_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 88, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_INTERVAL_in_literal203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_literal215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_operator256 = new BitSet(new long[]{0x010003F000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_EQUALS_in_operator267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_complexOp_in_relationalOp414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp429 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_complexOp477 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_complexOp481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList502 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList505 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_typeList507 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_typeMatch_in_type529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_typeMatch555 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch565 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch567 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_ID_in_typeMatch581 = new BitSet(new long[]{0x0004220000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch588 = new BitSet(new long[]{0x0004200000000002L});
    public static final BitSet FOLLOW_DOT_in_typeMatch593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_typeMatch595 = new BitSet(new long[]{0x0004220000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch602 = new BitSet(new long[]{0x0004200000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch617 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch619 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments640 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments642 = new BitSet(new long[]{0x0002010000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments645 = new BitSet(new long[]{0x0040000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments647 = new BitSet(new long[]{0x0002010000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument676 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument680 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_typeArgument684 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_typeArgument687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy711 = new BitSet(new long[]{0x0000100400200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_set_in_dummy713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_dummy2747 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_dummy2749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression768 = new BitSet(new long[]{0x000007003FC00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression789 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_expression793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression820 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ternaryExpression854 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression858 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COLON_in_ternaryExpression860 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_fullAnnotation890 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_fullAnnotation894 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_annotationArgs_in_fullAnnotation902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_annotationArgs918 = new BitSet(new long[]{0x0000100000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_annotationArgs935 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_annotationElementValuePairs_in_annotationArgs948 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_annotationArgs962 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs977 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_COMMA_in_annotationElementValuePairs982 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs984 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ID_in_annotationElementValuePair1005 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1007 = new BitSet(new long[]{0xE180AA018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_annotationValue_in_annotationElementValuePair1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_annotationValue1026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationArray_in_annotationValue1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_annotationArray1044 = new BitSet(new long[]{0xE180AA018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_annotationValue_in_annotationArray1046 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_COMMA_in_annotationArray1050 = new BitSet(new long[]{0xE180AA018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_annotationValue_in_annotationArray1052 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_annotationArray1057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1078 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1087 = new BitSet(new long[]{0xE1802A018039B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_conditionalOrExpression1109 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1115 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1150 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1158 = new BitSet(new long[]{0xE1802A018039B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_conditionalAndExpression1181 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1187 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1222 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression1230 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1234 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1269 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1277 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1281 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1316 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression1324 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1328 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1363 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression1375 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1381 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1397 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1432 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1442 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression1456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression1501 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_not_key_in_inExpression1521 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_in_key_in_inExpression1525 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1527 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression1549 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1568 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression1572 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression1609 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1611 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression1633 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1652 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_inExpression1656 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1718 = new BitSet(new long[]{0x01000BF000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_orRestriction_in_relationalExpression1743 = new BitSet(new long[]{0x01000BF000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1778 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1800 = new BitSet(new long[]{0x01000BF000200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_orRestriction1804 = new BitSet(new long[]{0x01000BF000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1810 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_EOF_in_orRestriction1829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1849 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1869 = new BitSet(new long[]{0x01000BF000200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_andRestriction1890 = new BitSet(new long[]{0x01000BF000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1895 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_operator_in_singleRestriction1931 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_squareArguments_in_singleRestriction1960 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_shiftExpression_in_singleRestriction1964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_singleRestriction1977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction2002 = new BitSet(new long[]{0x01000BF000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_orRestriction_in_singleRestriction2006 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction2008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2032 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression2046 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2048 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp2068 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp2070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2082 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2084 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2098 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2128 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression2149 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2157 = new BitSet(new long[]{0xC000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2185 = new BitSet(new long[]{0x3000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression2197 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2211 = new BitSet(new long[]{0x3000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression2237 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression2259 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression2283 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_primary_in_unaryExpression2285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression2295 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_primary_in_unaryExpression2297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2339 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2350 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2394 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2396 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2435 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2437 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2482 = new BitSet(new long[]{0x0004200180000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2499 = new BitSet(new long[]{0x0004200180000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2561 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression2563 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2565 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression2569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2586 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_castExpression2588 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2590 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType2611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType2619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType2627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType2635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType2643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType2651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType2667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary2695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary2712 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary2715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary2719 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_arguments_in_primary2721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary2737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary2759 = new BitSet(new long[]{0x0004080000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary2761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary2776 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_creator_in_primary2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary2793 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary2796 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary2798 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_DOT_in_primary2802 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_class_key_in_primary2804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary2839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary2855 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_DOT_in_primary2889 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_primary2893 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_DOT_in_primary2933 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_primary2935 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_primary2975 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_COMMA_in_primary2978 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_primary2982 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_primary3022 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_SHARP_in_primary3074 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_primary3078 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_HASH_in_primary3118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_primary3122 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_NULL_SAFE_DOT_in_primary3162 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_primary3166 = new BitSet(new long[]{0x000C280000000012L,0x0000000000000001L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression3209 = new BitSet(new long[]{0xE1806A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression3211 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression3214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression3235 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression3237 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3260 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList3263 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3265 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry3284 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry3286 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_mapEntry3288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression3309 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_parExpression3313 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression3315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3349 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3390 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix3434 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix3438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3453 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix3483 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3511 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix3527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3549 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_createdName_in_creator3552 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator3563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator3567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName3585 = new BitSet(new long[]{0x0004020000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3587 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName3600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_createdName3602 = new BitSet(new long[]{0x0004020000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3604 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName3619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator3639 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3660 = new BitSet(new long[]{0xE1806A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3670 = new BitSet(new long[]{0x0000A00000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3673 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3675 = new BitSet(new long[]{0x0000A00000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3693 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3695 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3700 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3702 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3704 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3716 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3718 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer3747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer3761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer3778 = new BitSet(new long[]{0xE181AA018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3781 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3784 = new BitSet(new long[]{0xE180AA018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3786 = new BitSet(new long[]{0x0003000000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3791 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer3798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest3815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3833 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments3852 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments3854 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments3856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix3873 = new BitSet(new long[]{0x0004080000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix3886 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix3888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3913 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_selector3917 = new BitSet(new long[]{0x0004080000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector3919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3935 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_new_key_in_selector3939 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector3942 = new BitSet(new long[]{0x0000020000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_innerCreator_in_selector3946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3962 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_selector3984 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arguments_in_selector4013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector4034 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_selector4061 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector4086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix4116 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_superSuffix4118 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments4150 = new BitSet(new long[]{0xE1806A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments4155 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments4161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments4178 = new BitSet(new long[]{0xE1803A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expressionList_in_arguments4190 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments4201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4231 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList4242 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_expression_in_expressionList4246 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator4275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator4283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator4291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator4299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator4307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator4315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator4323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator4331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator4339 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator4341 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4360 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4362 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4364 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4366 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4381 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4383 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key4415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key4444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key4473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key4502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key4531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key4560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key4589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key4618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key4647 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key4676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key4705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key4734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key4763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key4792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key4822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRL6Expressions548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRL6Expressions559 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRL6Expressions561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRL6Expressions585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRL6Expressions599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRL6Expressions611 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRL6Expressions613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRL6Expressions782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_synpred7_DRL6Expressions1515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_in_key_in_synpred7_DRL6Expressions1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_synpred8_DRL6Expressions1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRL6Expressions1736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred9_DRL6Expressions1789 = new BitSet(new long[]{0x01000BF000200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_synpred9_DRL6Expressions1791 = new BitSet(new long[]{0x01000BF000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_andRestriction_in_synpred9_DRL6Expressions1795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred10_DRL6Expressions1858 = new BitSet(new long[]{0x010003F000200000L,0x0000000000000020L});
    public static final BitSet FOLLOW_fullAnnotation_in_synpred10_DRL6Expressions1860 = new BitSet(new long[]{0x010003F000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_operator_in_synpred10_DRL6Expressions1864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred11_DRL6Expressions1952 = new BitSet(new long[]{0xE1802A018019B200L,0x0000000000000020L});
    public static final BitSet FOLLOW_shiftExpression_in_synpred11_DRL6Expressions1954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred12_DRL6Expressions2043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred13_DRL6Expressions2142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred14_DRL6Expressions2363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred15_DRL6Expressions2496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred16_DRL6Expressions2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRL6Expressions2554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_primitiveType_in_synpred17_DRL6Expressions2556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred18_DRL6Expressions2579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_type_in_synpred18_DRL6Expressions2581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred19_DRL6Expressions2689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred20_DRL6Expressions2708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred21_DRL6Expressions2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred22_DRL6Expressions2755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred23_DRL6Expressions2772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred24_DRL6Expressions2789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred25_DRL6Expressions2820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred26_DRL6Expressions2835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRL6Expressions2850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred28_DRL6Expressions2884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred28_DRL6Expressions2886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred29_DRL6Expressions2928 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred29_DRL6Expressions2930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SHARP_in_synpred30_DRL6Expressions3069 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred30_DRL6Expressions3071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HASH_in_synpred31_DRL6Expressions3113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred31_DRL6Expressions3115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred32_DRL6Expressions3157 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred32_DRL6Expressions3159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred33_DRL6Expressions3185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred34_DRL6Expressions3343 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred34_DRL6Expressions3345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred35_DRL6Expressions3448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRL6Expressions3710 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred36_DRL6Expressions3712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred37_DRL6Expressions3908 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_super_key_in_synpred37_DRL6Expressions3910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred38_DRL6Expressions3930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_new_key_in_synpred38_DRL6Expressions3932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred39_DRL6Expressions3957 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_synpred39_DRL6Expressions3959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred40_DRL6Expressions4008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred41_DRL6Expressions4031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred42_DRL6Expressions4122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred43_DRL6Expressions4352 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred43_DRL6Expressions4354 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred43_DRL6Expressions4356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred44_DRL6Expressions4375 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred44_DRL6Expressions4377 = new BitSet(new long[]{0x0000000000000002L});

}