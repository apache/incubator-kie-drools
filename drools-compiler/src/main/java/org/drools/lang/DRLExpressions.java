// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/DRLExpressions.g 2013-09-14 20:02:10

    package org.drools.lang;

    import java.util.LinkedList;
    import org.drools.compiler.DroolsParserException;
    import org.drools.lang.ParserHelper;
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.lang.Location;
    import org.drools.CheckedDroolsException;

    import org.drools.lang.api.AnnotatedDescrBuilder;

    import org.drools.lang.descr.AtomicExprDescr;
    import org.drools.lang.descr.AnnotatedBaseDescr;
    import org.drools.lang.descr.AnnotationDescr;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TIME_INTERVAL", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "UNIFY", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "NULL_SAFE_DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "HASH", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
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
    public static final int NULL_SAFE_DOT=50;
    public static final int DOUBLE_AMPER=51;
    public static final int DOUBLE_PIPE=52;
    public static final int QUESTION=53;
    public static final int NEGATION=54;
    public static final int TILDE=55;
    public static final int PIPE=56;
    public static final int AMPER=57;
    public static final int XOR=58;
    public static final int MOD=59;
    public static final int STAR=60;
    public static final int MINUS=61;
    public static final int PLUS=62;
    public static final int HASH=63;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=64;
    public static final int MULTI_LINE_COMMENT=65;
    public static final int IdentifierStart=66;
    public static final int IdentifierPart=67;
    public static final int ID=68;
    public static final int DIV=69;
    public static final int MISC=70;

    // delegates
    // delegators


        public DRLExpressions(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLExpressions(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DRLExpressions.tokenNames; }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRLExpressions.g"; }


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

        private boolean isNotEOF() {
            if (state.backtracking != 0){
                return false;
            }
            if (input.get( input.index() - 1 ).getType() == DRLLexer.WS){
                return true;
            }
            if (input.LA(-1) == DRLLexer.LEFT_PAREN){
                return true;
            }
            return input.get( input.index() ).getType() != DRLLexer.EOF;
        }


    public static class literal_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "literal"
    // src/main/resources/org/drools/lang/DRLExpressions.g:89:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR );
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
        Token STAR8=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:90:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL | TIME_INTERVAL | STAR )
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:90:7: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal83); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:91:7: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal100); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:92:7: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal116); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:93:7: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal136); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:94:7: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal154); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:95:7: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal173); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:96:9: TIME_INTERVAL
                    {
                    TIME_INTERVAL7=(Token)match(input,TIME_INTERVAL,FOLLOW_TIME_INTERVAL_in_literal194); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                      	helper.emit(TIME_INTERVAL7, DroolsEditorType.NULL_CONST); 
                    }

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:97:9: STAR
                    {
                    STAR8=(Token)match(input,STAR,FOLLOW_STAR_in_literal206); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:100:1: operator returns [boolean negated, String opr] : (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) ;
    public final DRLExpressions.operator_return operator() throws RecognitionException {
        DRLExpressions.operator_return retval = new DRLExpressions.operator_return();
        retval.start = input.LT(1);

        Token x=null;
        Token op=null;
        DRLExpressions.relationalOp_return rop = null;


         if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:103:3: ( (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:103:5: (x= TILDE )? (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
            {
            // src/main/resources/org/drools/lang/DRLExpressions.g:103:6: (x= TILDE )?
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:103:6: x= TILDE
                    {
                    x=(Token)match(input,TILDE,FOLLOW_TILDE_in_operator247); if (state.failed) return retval;

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRLExpressions.g:104:5: (op= EQUALS | op= NOT_EQUALS | rop= relationalOp )
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:104:7: op= EQUALS
                    {
                    op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_operator258); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:105:7: op= NOT_EQUALS
                    {
                    op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator277); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(x != null ? (x!=null?x.getText():null) : "")+(op!=null?op.getText():null); helper.emit(op, DroolsEditorType.SYMBOL); 
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:106:7: rop= relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator292);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:112:1: relationalOp returns [boolean negated, String opr, java.util.List<String> params] : (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key ) ;
    public final DRLExpressions.relationalOp_return relationalOp() throws RecognitionException {
        DRLExpressions.relationalOp_return retval = new DRLExpressions.relationalOp_return();
        retval.start = input.LT(1);

        Token op=null;
        String xop = null;

        DRLExpressions.neg_operator_key_return nop = null;

        DRLExpressions.operator_key_return cop = null;


         if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); helper.setHasOperator( true ); 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:115:3: ( (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:115:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key )
            {
            // src/main/resources/org/drools/lang/DRLExpressions.g:115:5: (op= LESS_EQUALS | op= GREATER_EQUALS | op= LESS | op= GREATER | xop= complexOp | not_key nop= neg_operator_key | cop= operator_key )
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:115:7: op= LESS_EQUALS
                    {
                    op=(Token)match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp333); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:116:7: op= GREATER_EQUALS
                    {
                    op=(Token)match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp349); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:117:7: op= LESS
                    {
                    op=(Token)match(input,LESS,FOLLOW_LESS_in_relationalOp362); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:118:7: op= GREATER
                    {
                    op=(Token)match(input,GREATER,FOLLOW_GREATER_in_relationalOp385); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:119:7: xop= complexOp
                    {
                    pushFollow(FOLLOW_complexOp_in_relationalOp405);
                    xop=complexOp();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = false; retval.opr =(op!=null?op.getText():null); retval.params = null; helper.emit(op, DroolsEditorType.SYMBOL);
                    }

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:120:7: not_key nop= neg_operator_key
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp420);
                    not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp424);
                    nop=neg_operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       retval.negated = true; retval.opr =(nop!=null?input.toString(nop.start,nop.stop):null);
                    }

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:121:7: cop= operator_key
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp436);
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


    // $ANTLR start "complexOp"
    // src/main/resources/org/drools/lang/DRLExpressions.g:125:1: complexOp returns [String opr] : t= TILDE e= EQUALS_ASSIGN ;
    public final String complexOp() throws RecognitionException {
        String opr = null;

        Token t=null;
        Token e=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:126:5: (t= TILDE e= EQUALS_ASSIGN )
            // src/main/resources/org/drools/lang/DRLExpressions.g:126:7: t= TILDE e= EQUALS_ASSIGN
            {
            t=(Token)match(input,TILDE,FOLLOW_TILDE_in_complexOp468); if (state.failed) return opr;
            e=(Token)match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_complexOp472); if (state.failed) return opr;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:129:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:130:5: ( type ( COMMA type )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:130:7: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList493);
            type();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:130:12: ( COMMA type )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:130:13: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList496); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList498);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:133:1: type : tm= typeMatch ;
    public final DRLExpressions.type_return type() throws RecognitionException {
        DRLExpressions.type_return retval = new DRLExpressions.type_return();
        retval.start = input.LT(1);

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:134:5: (tm= typeMatch )
            // src/main/resources/org/drools/lang/DRLExpressions.g:134:8: tm= typeMatch
            {
            pushFollow(FOLLOW_typeMatch_in_type520);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:137:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void typeMatch() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:138:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                int LA11_1 = input.LA(2);

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))))) ) {
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:138:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // src/main/resources/org/drools/lang/DRLExpressions.g:138:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // src/main/resources/org/drools/lang/DRLExpressions.g:138:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_typeMatch546);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:138:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==LEFT_SQUARE) && (synpred2_DRLExpressions())) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch556); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch558); if (state.failed) return ;

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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_typeMatch572); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:12: ( ( typeArguments )=> typeArguments )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==LESS) ) {
                        int LA7_1 = input.LA(2);

                        if ( (LA7_1==ID) && (synpred3_DRLExpressions())) {
                            alt7=1;
                        }
                        else if ( (LA7_1==QUESTION) && (synpred3_DRLExpressions())) {
                            alt7=1;
                        }
                    }
                    switch (alt7) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:139:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_typeMatch579);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==DOT) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:139:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_typeMatch584); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_typeMatch586); if (state.failed) return ;
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:139:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt8=2;
                    	    int LA8_0 = input.LA(1);

                    	    if ( (LA8_0==LESS) ) {
                    	        int LA8_1 = input.LA(2);

                    	        if ( (LA8_1==ID) && (synpred4_DRLExpressions())) {
                    	            alt8=1;
                    	        }
                    	        else if ( (LA8_1==QUESTION) && (synpred4_DRLExpressions())) {
                    	            alt8=1;
                    	        }
                    	    }
                    	    switch (alt8) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRLExpressions.g:139:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_typeMatch593);
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

                    // src/main/resources/org/drools/lang/DRLExpressions.g:139:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==LEFT_SQUARE) && (synpred5_DRLExpressions())) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch608); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch610); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:142:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:143:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // src/main/resources/org/drools/lang/DRLExpressions.g:143:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments631); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments633);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:143:25: ( COMMA typeArgument )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==COMMA) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:143:26: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments636); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments638);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments642); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:146:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:147:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:147:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument659);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:148:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument667); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:148:16: ( ( extends_key | super_key ) type )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:148:17: ( extends_key | super_key ) type
                            {
                            // src/main/resources/org/drools/lang/DRLExpressions.g:148:17: ( extends_key | super_key )
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
                                    // src/main/resources/org/drools/lang/DRLExpressions.g:148:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument671);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // src/main/resources/org/drools/lang/DRLExpressions.g:148:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument675);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument678);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:156:1: dummy : expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) ;
    public final void dummy() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:157:5: ( expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:157:7: expression ( AT | SEMICOLON | EOF | ID | RIGHT_PAREN )
            {
            pushFollow(FOLLOW_expression_in_dummy702);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:159:1: dummy2 : relationalExpression EOF ;
    public final void dummy2() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:160:5: ( relationalExpression EOF )
            // src/main/resources/org/drools/lang/DRLExpressions.g:160:8: relationalExpression EOF
            {
            pushFollow(FOLLOW_relationalExpression_in_dummy2738);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            match(input,EOF,FOLLOW_EOF_in_dummy2740); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:163:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? ;
    public final DRLExpressions.expression_return expression() throws RecognitionException {
        DRLExpressions.expression_return retval = new DRLExpressions.expression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;

        DRLExpressions.expression_return right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:164:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )? )
            // src/main/resources/org/drools/lang/DRLExpressions.g:164:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression759);
            left=conditionalExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:165:9: ( ( assignmentOperator )=>op= assignmentOperator right= expression )?
            int alt16=2;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:165:10: ( assignmentOperator )=>op= assignmentOperator right= expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression780);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_expression_in_expression784);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:168:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( ternaryExpression )? ;
    public final BaseDescr conditionalExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:169:5: (left= conditionalOrExpression ( ternaryExpression )? )
            // src/main/resources/org/drools/lang/DRLExpressions.g:169:9: left= conditionalOrExpression ( ternaryExpression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression811);
            left=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:170:9: ( ternaryExpression )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==QUESTION) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:170:9: ternaryExpression
                    {
                    pushFollow(FOLLOW_ternaryExpression_in_conditionalExpression823);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:173:1: ternaryExpression : QUESTION ts= expression COLON fs= expression ;
    public final void ternaryExpression() throws RecognitionException {
        DRLExpressions.expression_return ts = null;

        DRLExpressions.expression_return fs = null;


         ternOp++; 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:175:5: ( QUESTION ts= expression COLON fs= expression )
            // src/main/resources/org/drools/lang/DRLExpressions.g:175:7: QUESTION ts= expression COLON fs= expression
            {
            match(input,QUESTION,FOLLOW_QUESTION_in_ternaryExpression845); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression849);
            ts=expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_ternaryExpression851); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_ternaryExpression855);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:180:1: fullAnnotation[AnnotatedDescrBuilder inDescrBuilder] returns [AnnotationDescr result] : AT name= ID ( DOT x= ID )* annotationArgs[result] ;
    public final AnnotationDescr fullAnnotation(AnnotatedDescrBuilder inDescrBuilder) throws RecognitionException {
        AnnotationDescr result = null;

        Token name=null;
        Token x=null;

         String n = ""; 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:182:3: ( AT name= ID ( DOT x= ID )* annotationArgs[result] )
            // src/main/resources/org/drools/lang/DRLExpressions.g:182:5: AT name= ID ( DOT x= ID )* annotationArgs[result]
            {
            match(input,AT,FOLLOW_AT_in_fullAnnotation885); if (state.failed) return result;
            name=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation889); if (state.failed) return result;
            if ( state.backtracking==0 ) {
               n = (name!=null?name.getText():null); 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:182:36: ( DOT x= ID )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==DOT) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:182:38: DOT x= ID
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_fullAnnotation895); if (state.failed) return result;
            	    x=(Token)match(input,ID,FOLLOW_ID_in_fullAnnotation899); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       n += "." + (x!=null?x.getText():null); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            if ( state.backtracking==0 ) {
               if( buildDescr ) { result = inDescrBuilder != null ? (AnnotationDescr) inDescrBuilder.newAnnotation( n ).getDescr() : new AnnotationDescr( n ); } 
            }
            pushFollow(FOLLOW_annotationArgs_in_fullAnnotation920);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:187:1: annotationArgs[AnnotationDescr descr] : LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN ;
    public final void annotationArgs(AnnotationDescr descr) throws RecognitionException {
        Token value=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:188:3: ( LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRLExpressions.g:188:5: LEFT_PAREN (value= ID | annotationElementValuePairs[descr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_annotationArgs936); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:189:5: (value= ID | annotationElementValuePairs[descr] )?
            int alt19=3;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==ID) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==EQUALS_ASSIGN) ) {
                    alt19=2;
                }
                else if ( (LA19_1==RIGHT_PAREN) ) {
                    alt19=1;
                }
            }
            switch (alt19) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:190:8: value= ID
                    {
                    value=(Token)match(input,ID,FOLLOW_ID_in_annotationArgs953); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       if ( buildDescr ) { descr.setValue( (value!=null?value.getText():null) ); } 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:191:10: annotationElementValuePairs[descr]
                    {
                    pushFollow(FOLLOW_annotationElementValuePairs_in_annotationArgs966);
                    annotationElementValuePairs(descr);

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_annotationArgs980); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:196:1: annotationElementValuePairs[AnnotationDescr descr] : annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* ;
    public final void annotationElementValuePairs(AnnotationDescr descr) throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:197:3: ( annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:197:5: annotationElementValuePair[descr] ( COMMA annotationElementValuePair[descr] )*
            {
            pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs995);
            annotationElementValuePair(descr);

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:197:39: ( COMMA annotationElementValuePair[descr] )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==COMMA) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:197:41: COMMA annotationElementValuePair[descr]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_annotationElementValuePairs1000); if (state.failed) return ;
            	    pushFollow(FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1002);
            	    annotationElementValuePair(descr);

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "annotationElementValuePairs"


    // $ANTLR start "annotationElementValuePair"
    // src/main/resources/org/drools/lang/DRLExpressions.g:200:1: annotationElementValuePair[AnnotationDescr descr] : key= ID EQUALS_ASSIGN val= annotationValue ;
    public final void annotationElementValuePair(AnnotationDescr descr) throws RecognitionException {
        Token key=null;
        DRLExpressions.annotationValue_return val = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:201:3: (key= ID EQUALS_ASSIGN val= annotationValue )
            // src/main/resources/org/drools/lang/DRLExpressions.g:201:5: key= ID EQUALS_ASSIGN val= annotationValue
            {
            key=(Token)match(input,ID,FOLLOW_ID_in_annotationElementValuePair1023); if (state.failed) return ;
            match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1025); if (state.failed) return ;
            pushFollow(FOLLOW_annotationValue_in_annotationElementValuePair1029);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:204:1: annotationValue : ( expression | annotationArray );
    public final DRLExpressions.annotationValue_return annotationValue() throws RecognitionException {
        DRLExpressions.annotationValue_return retval = new DRLExpressions.annotationValue_return();
        retval.start = input.LT(1);

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:205:3: ( expression | annotationArray )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==FLOAT||(LA21_0>=HEX && LA21_0<=DECIMAL)||(LA21_0>=STRING && LA21_0<=TIME_INTERVAL)||(LA21_0>=BOOL && LA21_0<=NULL)||(LA21_0>=DECR && LA21_0<=INCR)||LA21_0==LESS||LA21_0==LEFT_PAREN||LA21_0==LEFT_SQUARE||(LA21_0>=NEGATION && LA21_0<=TILDE)||(LA21_0>=STAR && LA21_0<=PLUS)||LA21_0==ID) ) {
                alt21=1;
            }
            else if ( (LA21_0==LEFT_CURLY) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:205:5: expression
                    {
                    pushFollow(FOLLOW_expression_in_annotationValue1044);
                    expression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:205:18: annotationArray
                    {
                    pushFollow(FOLLOW_annotationArray_in_annotationValue1048);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:208:1: annotationArray : LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY ;
    public final void annotationArray() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:209:3: ( LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY )
            // src/main/resources/org/drools/lang/DRLExpressions.g:209:6: LEFT_CURLY ( annotationValue ( COMMA annotationValue )* )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_annotationArray1062); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:209:17: ( annotationValue ( COMMA annotationValue )* )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==FLOAT||(LA23_0>=HEX && LA23_0<=DECIMAL)||(LA23_0>=STRING && LA23_0<=TIME_INTERVAL)||(LA23_0>=BOOL && LA23_0<=NULL)||(LA23_0>=DECR && LA23_0<=INCR)||LA23_0==LESS||LA23_0==LEFT_PAREN||LA23_0==LEFT_SQUARE||LA23_0==LEFT_CURLY||(LA23_0>=NEGATION && LA23_0<=TILDE)||(LA23_0>=STAR && LA23_0<=PLUS)||LA23_0==ID) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:209:19: annotationValue ( COMMA annotationValue )*
                    {
                    pushFollow(FOLLOW_annotationValue_in_annotationArray1066);
                    annotationValue();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:209:35: ( COMMA annotationValue )*
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==COMMA) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:209:37: COMMA annotationValue
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_annotationArray1070); if (state.failed) return ;
                    	    pushFollow(FOLLOW_annotationValue_in_annotationArray1072);
                    	    annotationValue();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_annotationArray1080); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:214:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* ;
    public final BaseDescr conditionalOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:215:3: (left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:215:5: left= conditionalAndExpression ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1101);
            left=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:216:3: ( DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==DOUBLE_PIPE) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:216:5: DOUBLE_PIPE (args= fullAnnotation[null] )? right= conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1110); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	        if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR );  
            	    }
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:218:13: (args= fullAnnotation[null] )?
            	    int alt24=2;
            	    int LA24_0 = input.LA(1);

            	    if ( (LA24_0==AT) ) {
            	        alt24=1;
            	    }
            	    switch (alt24) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:218:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_conditionalOrExpression1132);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1138);
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
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // src/main/resources/org/drools/lang/DRLExpressions.g:230:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* ;
    public final BaseDescr conditionalAndExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:231:3: (left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:231:5: left= inclusiveOrExpression ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1173);
            left=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:232:3: ( DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==DOUBLE_AMPER) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:232:5: DOUBLE_AMPER (args= fullAnnotation[null] )? right= inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1181); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); 
            	    }
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:234:13: (args= fullAnnotation[null] )?
            	    int alt26=2;
            	    int LA26_0 = input.LA(1);

            	    if ( (LA26_0==AT) ) {
            	        alt26=1;
            	    }
            	    switch (alt26) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:234:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_conditionalAndExpression1204);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1210);
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
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // src/main/resources/org/drools/lang/DRLExpressions.g:246:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
    public final BaseDescr inclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:247:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:247:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1245);
            left=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:248:3: ( PIPE right= exclusiveOrExpression )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==PIPE) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:248:5: PIPE right= exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression1253); if (state.failed) return result;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1257);
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
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // src/main/resources/org/drools/lang/DRLExpressions.g:259:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
    public final BaseDescr exclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:260:3: (left= andExpression ( XOR right= andExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:260:5: left= andExpression ( XOR right= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1292);
            left=andExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:261:3: ( XOR right= andExpression )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==XOR) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:261:5: XOR right= andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression1300); if (state.failed) return result;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression1304);
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
            	    break loop29;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:272:1: andExpression returns [BaseDescr result] : left= equalityExpression ( AMPER right= equalityExpression )* ;
    public final BaseDescr andExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:273:3: (left= equalityExpression ( AMPER right= equalityExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:273:5: left= equalityExpression ( AMPER right= equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression1339);
            left=equalityExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:274:3: ( AMPER right= equalityExpression )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==AMPER) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:274:5: AMPER right= equalityExpression
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression1347); if (state.failed) return result;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression1351);
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
    // $ANTLR end "andExpression"


    // $ANTLR start "equalityExpression"
    // src/main/resources/org/drools/lang/DRLExpressions.g:285:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* ;
    public final BaseDescr equalityExpression() throws RecognitionException {
        BaseDescr result = null;

        Token op=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:286:3: (left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:286:5: left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1386);
            left=instanceOfExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:287:3: ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>=EQUALS && LA32_0<=NOT_EQUALS)) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:287:5: (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression
            	    {
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:287:5: (op= EQUALS | op= NOT_EQUALS )
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);

            	    if ( (LA31_0==EQUALS) ) {
            	        alt31=1;
            	    }
            	    else if ( (LA31_0==NOT_EQUALS) ) {
            	        alt31=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 31, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:287:7: op= EQUALS
            	            {
            	            op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression1398); if (state.failed) return result;

            	            }
            	            break;
            	        case 2 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:287:19: op= NOT_EQUALS
            	            {
            	            op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression1404); if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    if ( state.backtracking==0 ) {
            	        helper.setHasOperator( true );
            	             if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
            	    }
            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression1420);
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
            	    break loop32;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:298:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
    public final BaseDescr instanceOfExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRLExpressions.instanceof_key_return op = null;

        DRLExpressions.type_return right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:299:3: (left= inExpression (op= instanceof_key right= type )? )
            // src/main/resources/org/drools/lang/DRLExpressions.g:299:5: left= inExpression (op= instanceof_key right= type )?
            {
            pushFollow(FOLLOW_inExpression_in_instanceOfExpression1455);
            left=inExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:300:3: (op= instanceof_key right= type )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==ID) ) {
                int LA33_1 = input.LA(2);

                if ( (LA33_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))) {
                    alt33=1;
                }
            }
            switch (alt33) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:300:5: op= instanceof_key right= type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression1465);
                    op=instanceof_key();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        helper.setHasOperator( true );
                             if( input.LA( 1 ) != DRLLexer.EOF ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_type_in_instanceOfExpression1479);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:311:1: inExpression returns [BaseDescr result] : left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? ;
    public final BaseDescr inExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRLExpressions.expression_return e1 = null;

        DRLExpressions.expression_return e2 = null;


         ConstraintConnectiveDescr descr = null; BaseDescr leftDescr = null; BindingDescr binding = null; 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:314:3: (left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )? )
            // src/main/resources/org/drools/lang/DRLExpressions.g:314:5: left= relationalExpression ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression1524);
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
            // src/main/resources/org/drools/lang/DRLExpressions.g:323:5: ( ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN | in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN )?
            int alt36=3;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==ID) ) {
                int LA36_1 = input.LA(2);

                if ( (LA36_1==ID) ) {
                    int LA36_3 = input.LA(3);

                    if ( (LA36_3==LEFT_PAREN) && ((synpred7_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) {
                        alt36=1;
                    }
                }
                else if ( (LA36_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt36=2;
                }
            }
            switch (alt36) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:323:6: ( not_key in_key )=> not_key in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_not_key_in_inExpression1544);
                    not_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_in_key_in_inExpression1548);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1550); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_expression_in_inExpression1572);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newAnd();
                                  RelationalExprDescr rel = new RelationalExprDescr( "!=", false, null, leftDescr, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:331:7: ( COMMA e2= expression )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( (LA34_0==COMMA) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:331:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1591); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1595);
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
                    	    break loop34;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1616); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_END ); 
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:337:7: in= in_key LEFT_PAREN e1= expression ( COMMA e2= expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_in_key_in_inExpression1632);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1634); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    pushFollow(FOLLOW_expression_in_inExpression1656);
                    e1=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                         descr = ConstraintConnectiveDescr.newOr();
                                  RelationalExprDescr rel = new RelationalExprDescr( "==", false, null, leftDescr, (e1!=null?e1.result:null) );
                                  descr.addOrMerge( rel );
                                  result = descr;
                              
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:345:7: ( COMMA e2= expression )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==COMMA) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:345:8: COMMA e2= expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1675); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1679);
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
                    	    break loop35;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1700); if (state.failed) return result;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:354:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* ;
    public final BaseDescr relationalExpression() throws RecognitionException {
        relationalExpression_stack.push(new relationalExpression_scope());
        BaseDescr result = null;

        DRLExpressions.shiftExpression_return left = null;

        BaseDescr right = null;


         ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = null; 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:357:3: (left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:357:5: left= shiftExpression ( ( operator | LEFT_PAREN )=>right= orRestriction )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression1741);
            left=shiftExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) {
                        if ( (left!=null?left.result:null) == null ) {
                          result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) );
                        } else if ( (left!=null?left.result:null) instanceof AtomicExprDescr ) {
                          if ( (left!=null?input.toString(left.start,left.stop):null).equals(((AtomicExprDescr)(left!=null?left.result:null)).getExpression()) ) {
                            result = (left!=null?left.result:null);
                          } else {
                            result = new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ) ;
                          }
                        } else if ( (left!=null?left.result:null) instanceof BindingDescr ) {
                            if ( (left!=null?input.toString(left.start,left.stop):null).equals(((BindingDescr)(left!=null?left.result:null)).getExpression()) ) {
                              result = (left!=null?left.result:null);
                            } else {
                              BindingDescr bind = (BindingDescr) (left!=null?left.result:null);
                              int offset = bind.isUnification() ? 2 : 1;
                              String fullExpression = (left!=null?input.toString(left.start,left.stop):null).substring( (left!=null?input.toString(left.start,left.stop):null).indexOf( ":" ) + offset ).trim();
                              result = new BindingDescr( bind.getVariable(), bind.getExpression(), fullExpression, bind.isUnification() );
                            }
                        } else {
                            result = (left!=null?left.result:null);
                        }
                        ((relationalExpression_scope)relationalExpression_stack.peek()).lsd = result;
                    } 
                  
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:382:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*
            loop37:
            do {
                int alt37=2;
                alt37 = dfa37.predict(input);
                switch (alt37) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:382:5: ( operator | LEFT_PAREN )=>right= orRestriction
            	    {
            	    pushFollow(FOLLOW_orRestriction_in_relationalExpression1766);
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
            	    break loop37;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:391:1: orRestriction returns [BaseDescr result] : left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? ;
    public final BaseDescr orRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:392:3: (left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )? )
            // src/main/resources/org/drools/lang/DRLExpressions.g:392:5: left= andRestriction ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )* ( EOF )?
            {
            pushFollow(FOLLOW_andRestriction_in_orRestriction1801);
            left=andRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:393:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*
            loop39:
            do {
                int alt39=2;
                alt39 = dfa39.predict(input);
                switch (alt39) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:393:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_orRestriction1823); if (state.failed) return result;
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:393:79: (args= fullAnnotation[null] )?
            	    int alt38=2;
            	    int LA38_0 = input.LA(1);

            	    if ( (LA38_0==AT) ) {
            	        alt38=1;
            	    }
            	    switch (alt38) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:393:79: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_orRestriction1827);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_andRestriction_in_orRestriction1833);
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
            	    break loop39;
                }
            } while (true);

            // src/main/resources/org/drools/lang/DRLExpressions.g:402:7: ( EOF )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==EOF) ) {
                int LA40_1 = input.LA(2);

                if ( (LA40_1==EOF) ) {
                    int LA40_3 = input.LA(3);

                    if ( (LA40_3==EOF) ) {
                        alt40=1;
                    }
                }
                else if ( ((LA40_1>=AT && LA40_1<=MOD_ASSIGN)||(LA40_1>=SEMICOLON && LA40_1<=RIGHT_PAREN)||LA40_1==RIGHT_SQUARE||(LA40_1>=RIGHT_CURLY && LA40_1<=COMMA)||(LA40_1>=DOUBLE_AMPER && LA40_1<=QUESTION)||(LA40_1>=TILDE && LA40_1<=XOR)||LA40_1==ID) ) {
                    alt40=1;
                }
            }
            switch (alt40) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:402:7: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_orRestriction1852); if (state.failed) return result;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:405:1: andRestriction returns [BaseDescr result] : left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* ;
    public final BaseDescr andRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        BaseDescr left = null;

        AnnotationDescr args = null;

        BaseDescr right = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:406:3: (left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:406:5: left= singleRestriction ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
            {
            pushFollow(FOLLOW_singleRestriction_in_andRestriction1872);
            left=singleRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:407:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*
            loop42:
            do {
                int alt42=2;
                alt42 = dfa42.predict(input);
                switch (alt42) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:407:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction
            	    {
            	    lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andRestriction1892); if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if ( isNotEOF() ) helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR ); 
            	    }
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:409:13: (args= fullAnnotation[null] )?
            	    int alt41=2;
            	    int LA41_0 = input.LA(1);

            	    if ( (LA41_0==AT) ) {
            	        alt41=1;
            	    }
            	    switch (alt41) {
            	        case 1 :
            	            // src/main/resources/org/drools/lang/DRLExpressions.g:409:13: args= fullAnnotation[null]
            	            {
            	            pushFollow(FOLLOW_fullAnnotation_in_andRestriction1913);
            	            args=fullAnnotation(null);

            	            state._fsp--;
            	            if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_singleRestriction_in_andRestriction1918);
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
            	    break loop42;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:421:1: singleRestriction returns [BaseDescr result] : (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN );
    public final BaseDescr singleRestriction() throws RecognitionException {
        BaseDescr result = null;

        DRLExpressions.operator_return op = null;

        java.util.List<String> sa = null;

        DRLExpressions.shiftExpression_return value = null;

        BaseDescr or = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:422:3: (op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression ) | LEFT_PAREN or= orRestriction RIGHT_PAREN )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( ((LA44_0>=EQUALS && LA44_0<=LESS)||LA44_0==TILDE) ) {
                alt44=1;
            }
            else if ( (LA44_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt44=1;
            }
            else if ( (LA44_0==LEFT_PAREN) ) {
                alt44=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:422:6: op= operator ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )
                    {
                    pushFollow(FOLLOW_operator_in_singleRestriction1954);
                    op=operator();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit( Location.LOCATION_LHS_INSIDE_CONDITION_ARGUMENT ); 
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:424:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )
                    int alt43=2;
                    alt43 = dfa43.predict(input);
                    switch (alt43) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:424:8: ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression
                            {
                            pushFollow(FOLLOW_squareArguments_in_singleRestriction1983);
                            sa=squareArguments();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_shiftExpression_in_singleRestriction1987);
                            value=shiftExpression();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:425:10: value= shiftExpression
                            {
                            pushFollow(FOLLOW_shiftExpression_in_singleRestriction2000);
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:440:6: LEFT_PAREN or= orRestriction RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_singleRestriction2025); if (state.failed) return result;
                    pushFollow(FOLLOW_orRestriction_in_singleRestriction2029);
                    or=orRestriction();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_singleRestriction2031); if (state.failed) return result;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:445:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final DRLExpressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRLExpressions.shiftExpression_return retval = new DRLExpressions.shiftExpression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:446:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:446:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression2055);
            left=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { retval.result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:447:5: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop45:
            do {
                int alt45=2;
                alt45 = dfa45.predict(input);
                switch (alt45) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:447:7: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression2069);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression2071);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop45;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:450:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:451:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:451:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // src/main/resources/org/drools/lang/DRLExpressions.g:451:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt46=3;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==LESS) ) {
                alt46=1;
            }
            else if ( (LA46_0==GREATER) ) {
                int LA46_2 = input.LA(2);

                if ( (LA46_2==GREATER) ) {
                    int LA46_3 = input.LA(3);

                    if ( (LA46_3==GREATER) ) {
                        alt46=2;
                    }
                    else if ( (LA46_3==EOF||LA46_3==FLOAT||(LA46_3>=HEX && LA46_3<=DECIMAL)||(LA46_3>=STRING && LA46_3<=TIME_INTERVAL)||(LA46_3>=BOOL && LA46_3<=NULL)||(LA46_3>=DECR && LA46_3<=INCR)||LA46_3==LESS||LA46_3==LEFT_PAREN||LA46_3==LEFT_SQUARE||(LA46_3>=NEGATION && LA46_3<=TILDE)||(LA46_3>=STAR && LA46_3<=PLUS)||LA46_3==ID) ) {
                        alt46=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 46, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 46, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:451:9: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp2091); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp2093); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:452:11: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2105); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2107); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2109); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:453:11: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2121); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp2123); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:456:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final BaseDescr additiveExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:457:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:457:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2151);
            left=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:458:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( ((LA47_0>=MINUS && LA47_0<=PLUS)) && (synpred13_DRLExpressions())) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:458:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression2180);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop47;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:461:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final BaseDescr multiplicativeExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:462:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:462:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2208);
            left=unaryExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr  ) { result = left; } 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:463:7: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( ((LA48_0>=MOD && LA48_0<=STAR)||LA48_0==DIV) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:463:9: ( STAR | DIV | MOD ) unaryExpression
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

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression2234);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop48;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:466:1: unaryExpression returns [BaseDescr result] : ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus );
    public final BaseDescr unaryExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr ue = null;

        DRLExpressions.unaryExpressionNotPlusMinus_return left = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:467:5: ( PLUS ue= unaryExpression | MINUS ue= unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus )
            int alt49=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt49=1;
                }
                break;
            case MINUS:
                {
                alt49=2;
                }
                break;
            case INCR:
                {
                alt49=3;
                }
                break;
            case DECR:
                {
                alt49=4;
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
                alt49=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:467:9: PLUS ue= unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression2260); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression2264);
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:474:7: MINUS ue= unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression2282); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression2286);
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:481:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression2306); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression2308);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:482:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression2318); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression2320);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:483:9: left= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2332);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:486:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final DRLExpressions.unaryExpressionNotPlusMinus_return unaryExpressionNotPlusMinus() throws RecognitionException {
        DRLExpressions.unaryExpressionNotPlusMinus_return retval = new DRLExpressions.unaryExpressionNotPlusMinus_return();
        retval.start = input.LT(1);

        Token var=null;
        Token COLON9=null;
        Token UNIFY10=null;
        BaseDescr left = null;


         boolean isLeft = false; BindingDescr bind = null;
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:488:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt53=4;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:488:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2362); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2364);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:489:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2373); if (state.failed) return retval;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2375);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:490:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2389);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:491:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    if ( state.backtracking==0 ) {
                       isLeft = helper.getLeftMostExpr() == null;
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:492:9: ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )?
                    int alt50=3;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==ID) ) {
                        int LA50_1 = input.LA(2);

                        if ( (LA50_1==COLON) ) {
                            int LA50_3 = input.LA(3);

                            if ( ((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON)) ) {
                                alt50=1;
                            }
                        }
                        else if ( (LA50_1==UNIFY) ) {
                            alt50=2;
                        }
                    }
                    switch (alt50) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:492:11: ({...}? (var= ID COLON ) )
                            {
                            // src/main/resources/org/drools/lang/DRLExpressions.g:492:11: ({...}? (var= ID COLON ) )
                            // src/main/resources/org/drools/lang/DRLExpressions.g:492:12: {...}? (var= ID COLON )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.COLON");
                            }
                            // src/main/resources/org/drools/lang/DRLExpressions.g:492:74: (var= ID COLON )
                            // src/main/resources/org/drools/lang/DRLExpressions.g:492:75: var= ID COLON
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2417); if (state.failed) return retval;
                            COLON9=(Token)match(input,COLON,FOLLOW_COLON_in_unaryExpressionNotPlusMinus2419); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(COLON9, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, false); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:494:11: ({...}? (var= ID UNIFY ) )
                            {
                            // src/main/resources/org/drools/lang/DRLExpressions.g:494:11: ({...}? (var= ID UNIFY ) )
                            // src/main/resources/org/drools/lang/DRLExpressions.g:494:12: {...}? (var= ID UNIFY )
                            {
                            if ( !((inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.UNIFY)) ) {
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "inMap == 0 && ternOp == 0 && input.LA(2) == DRLLexer.UNIFY");
                            }
                            // src/main/resources/org/drools/lang/DRLExpressions.g:494:74: (var= ID UNIFY )
                            // src/main/resources/org/drools/lang/DRLExpressions.g:494:75: var= ID UNIFY
                            {
                            var=(Token)match(input,ID,FOLLOW_ID_in_unaryExpressionNotPlusMinus2458); if (state.failed) return retval;
                            UNIFY10=(Token)match(input,UNIFY,FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2460); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                               hasBindings = true; helper.emit(var, DroolsEditorType.IDENTIFIER_VARIABLE); helper.emit(UNIFY10, DroolsEditorType.SYMBOL); if( buildDescr ) { bind = new BindingDescr((var!=null?var.getText():null), null, true); helper.setStart( bind, var ); } 
                            }

                            }


                            }


                            }
                            break;

                    }

                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus2505);
                    left=primary();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                       if( buildDescr ) { retval.result = left; } 
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:498:9: ( ( selector )=> selector )*
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==DOT) && (synpred15_DRLExpressions())) {
                            alt51=1;
                        }
                        else if ( (LA51_0==LEFT_SQUARE) && (synpred15_DRLExpressions())) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:498:10: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus2522);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return retval;

                    	    }
                    	    break;

                    	default :
                    	    break loop51;
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
                    // src/main/resources/org/drools/lang/DRLExpressions.g:517:9: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( ((LA52_0>=DECR && LA52_0<=INCR)) && (synpred16_DRLExpressions())) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:517:10: ( INCR | DECR )=> ( INCR | DECR )
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:520:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        BaseDescr expr = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:521:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LEFT_PAREN) ) {
                int LA54_1 = input.LA(2);

                if ( (synpred17_DRLExpressions()) ) {
                    alt54=1;
                }
                else if ( (synpred18_DRLExpressions()) ) {
                    alt54=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 54, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:521:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2584); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression2586);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2588); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression2592);
                    expr=unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:522:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression2609); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression2611);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression2613); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2615);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:525:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:526:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt55=8;
            alt55 = dfa55.predict(input);
            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:526:9: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType2634);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:527:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType2642);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:528:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType2650);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:529:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType2658);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:530:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType2666);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:531:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType2674);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:532:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType2682);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:533:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType2690);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:536:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final BaseDescr primary() throws RecognitionException {
        BaseDescr result = null;

        Token i1=null;
        Token i2=null;
        Token DOT12=null;
        Token LEFT_PAREN13=null;
        Token COMMA14=null;
        Token RIGHT_PAREN15=null;
        Token HASH16=null;
        Token NULL_SAFE_DOT17=null;
        BaseDescr expr = null;

        DRLExpressions.literal_return literal11 = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:537:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt61=9;
            alt61 = dfa61.predict(input);
            switch (alt61) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:537:7: ( parExpression )=>expr= parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary2718);
                    expr=parExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        if( buildDescr  ) { result = expr; }  
                    }

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:538:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary2735);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return result;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:538:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==ID) ) {
                        int LA56_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt56=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt56=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return result;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 56, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return result;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 56, 0, input);

                        throw nvae;
                    }
                    switch (alt56) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:538:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary2738);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:538:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary2742);
                            this_key();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_arguments_in_primary2744);
                            arguments();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:539:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary2760);
                    literal11=literal();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr  ) { result = new AtomicExprDescr( (literal11!=null?input.toString(literal11.start,literal11.stop):null), true ); }  
                    }

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:541:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary2782);
                    super_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_superSuffix_in_primary2784);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:542:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary2799);
                    new_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_creator_in_primary2801);
                    creator();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:543:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary2816);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return result;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:543:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop57:
                    do {
                        int alt57=2;
                        int LA57_0 = input.LA(1);

                        if ( (LA57_0==LEFT_SQUARE) ) {
                            alt57=1;
                        }


                        switch (alt57) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:543:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary2819); if (state.failed) return result;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary2821); if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop57;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary2825); if (state.failed) return result;
                    pushFollow(FOLLOW_class_key_in_primary2827);
                    class_key();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:545:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary2847);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:546:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary2862);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:547:9: ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    i1=(Token)match(input,ID,FOLLOW_ID_in_primary2878); if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       helper.emit(i1, DroolsEditorType.IDENTIFIER); 
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:548:9: ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )*
                    loop59:
                    do {
                        int alt59=5;
                        int LA59_0 = input.LA(1);

                        if ( (LA59_0==DOT) ) {
                            int LA59_2 = input.LA(2);

                            if ( (LA59_2==ID) ) {
                                int LA59_5 = input.LA(3);

                                if ( (synpred28_DRLExpressions()) ) {
                                    alt59=1;
                                }


                            }
                            else if ( (LA59_2==LEFT_PAREN) && (synpred29_DRLExpressions())) {
                                alt59=2;
                            }


                        }
                        else if ( (LA59_0==HASH) && (synpred30_DRLExpressions())) {
                            alt59=3;
                        }
                        else if ( (LA59_0==NULL_SAFE_DOT) && (synpred31_DRLExpressions())) {
                            alt59=4;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:549:13: ( ( DOT ID )=> DOT i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:549:13: ( ( DOT ID )=> DOT i2= ID )
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:549:15: ( DOT ID )=> DOT i2= ID
                    	    {
                    	    DOT12=(Token)match(input,DOT,FOLLOW_DOT_in_primary2912); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary2916); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(DOT12, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:551:13: ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
                    	    {
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:551:13: ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:551:15: ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary2956); if (state.failed) return result;
                    	    LEFT_PAREN13=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_primary2958); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(DOT12, DroolsEditorType.SYMBOL); helper.emit(LEFT_PAREN13, DroolsEditorType.SYMBOL); 
                    	    }
                    	    pushFollow(FOLLOW_expression_in_primary2998);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:552:48: ( COMMA expression )*
                    	    loop58:
                    	    do {
                    	        int alt58=2;
                    	        int LA58_0 = input.LA(1);

                    	        if ( (LA58_0==COMMA) ) {
                    	            alt58=1;
                    	        }


                    	        switch (alt58) {
                    	    	case 1 :
                    	    	    // src/main/resources/org/drools/lang/DRLExpressions.g:552:49: COMMA expression
                    	    	    {
                    	    	    COMMA14=(Token)match(input,COMMA,FOLLOW_COMMA_in_primary3001); if (state.failed) return result;
                    	    	    if ( state.backtracking==0 ) {
                    	    	       helper.emit(COMMA14, DroolsEditorType.SYMBOL); 
                    	    	    }
                    	    	    pushFollow(FOLLOW_expression_in_primary3005);
                    	    	    expression();

                    	    	    state._fsp--;
                    	    	    if (state.failed) return result;

                    	    	    }
                    	    	    break;

                    	    	default :
                    	    	    break loop58;
                    	        }
                    	    } while (true);

                    	    RIGHT_PAREN15=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_primary3045); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_PAREN15, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 3 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:556:13: ( ( HASH ID )=> HASH i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:556:13: ( ( HASH ID )=> HASH i2= ID )
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:556:15: ( HASH ID )=> HASH i2= ID
                    	    {
                    	    HASH16=(Token)match(input,HASH,FOLLOW_HASH_in_primary3097); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary3101); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(HASH16, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;
                    	case 4 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:558:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
                    	    {
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:558:13: ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID )
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:558:15: ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID
                    	    {
                    	    NULL_SAFE_DOT17=(Token)match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_primary3141); if (state.failed) return result;
                    	    i2=(Token)match(input,ID,FOLLOW_ID_in_primary3145); if (state.failed) return result;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(NULL_SAFE_DOT17, DroolsEditorType.SYMBOL); helper.emit(i2, DroolsEditorType.IDENTIFIER); 
                    	    }

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop59;
                        }
                    } while (true);

                    // src/main/resources/org/drools/lang/DRLExpressions.g:559:12: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt60=2;
                    alt60 = dfa60.predict(input);
                    switch (alt60) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:559:13: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary3167);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:562:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:563:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRLExpressions.g:563:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression3188); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:563:21: ( expressionList )?
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==FLOAT||(LA62_0>=HEX && LA62_0<=DECIMAL)||(LA62_0>=STRING && LA62_0<=TIME_INTERVAL)||(LA62_0>=BOOL && LA62_0<=NULL)||(LA62_0>=DECR && LA62_0<=INCR)||LA62_0==LESS||LA62_0==LEFT_PAREN||LA62_0==LEFT_SQUARE||(LA62_0>=NEGATION && LA62_0<=TILDE)||(LA62_0>=STAR && LA62_0<=PLUS)||LA62_0==ID) ) {
                alt62=1;
            }
            switch (alt62) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:563:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression3190);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression3193); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:566:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
         inMap++; 
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:568:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRLExpressions.g:568:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression3214); if (state.failed) return ;
            pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression3216);
            mapExpressionList();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3218); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:572:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:573:5: ( mapEntry ( COMMA mapEntry )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:573:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList3239);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:573:16: ( COMMA mapEntry )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==COMMA) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:573:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList3242); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList3244);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop63;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:576:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:577:5: ( expression COLON expression )
            // src/main/resources/org/drools/lang/DRLExpressions.g:577:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry3263);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry3265); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry3267);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:580:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
    public final BaseDescr parExpression() throws RecognitionException {
        BaseDescr result = null;

        DRLExpressions.expression_return expr = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:581:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRLExpressions.g:581:7: LEFT_PAREN expr= expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression3288); if (state.failed) return result;
            pushFollow(FOLLOW_expression_in_parExpression3292);
            expr=expression();

            state._fsp--;
            if (state.failed) return result;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression3294); if (state.failed) return result;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:591:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        Token LEFT_SQUARE18=null;
        Token RIGHT_SQUARE19=null;
        Token DOT20=null;
        Token LEFT_SQUARE21=null;
        Token RIGHT_SQUARE22=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:592:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt66=3;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==LEFT_SQUARE) ) {
                int LA66_1 = input.LA(2);

                if ( (LA66_1==RIGHT_SQUARE) && (synpred33_DRLExpressions())) {
                    alt66=1;
                }
                else if ( (LA66_1==FLOAT||(LA66_1>=HEX && LA66_1<=DECIMAL)||(LA66_1>=STRING && LA66_1<=TIME_INTERVAL)||(LA66_1>=BOOL && LA66_1<=NULL)||(LA66_1>=DECR && LA66_1<=INCR)||LA66_1==LESS||LA66_1==LEFT_PAREN||LA66_1==LEFT_SQUARE||(LA66_1>=NEGATION && LA66_1<=TILDE)||(LA66_1>=STAR && LA66_1<=PLUS)||LA66_1==ID) ) {
                    alt66=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA66_0==LEFT_PAREN) ) {
                alt66=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:592:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // src/main/resources/org/drools/lang/DRLExpressions.g:592:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt64=0;
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);

                        if ( (LA64_0==LEFT_SQUARE) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:592:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE18=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3328); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE18, DroolsEditorType.SYMBOL); 
                    	    }
                    	    RIGHT_SQUARE19=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3369); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE19, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt64 >= 1 ) break loop64;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(64, input);
                                throw eee;
                        }
                        cnt64++;
                    } while (true);

                    DOT20=(Token)match(input,DOT,FOLLOW_DOT_in_identifierSuffix3413); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT20, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_class_key_in_identifierSuffix3417);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:595:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // src/main/resources/org/drools/lang/DRLExpressions.g:595:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt65=0;
                    loop65:
                    do {
                        int alt65=2;
                        alt65 = dfa65.predict(input);
                        switch (alt65) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:595:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    LEFT_SQUARE21=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix3432); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(LEFT_SQUARE21, DroolsEditorType.SYMBOL); 
                    	    }
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix3462);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    RIGHT_SQUARE22=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix3490); if (state.failed) return ;
                    	    if ( state.backtracking==0 ) {
                    	       helper.emit(RIGHT_SQUARE22, DroolsEditorType.SYMBOL); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt65 >= 1 ) break loop65;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(65, input);
                                throw eee;
                        }
                        cnt65++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:598:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix3506);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:606:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:607:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:607:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // src/main/resources/org/drools/lang/DRLExpressions.g:607:7: ( nonWildcardTypeArguments )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==LESS) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:607:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator3528);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator3531);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:608:9: ( arrayCreatorRest | classCreatorRest )
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==LEFT_SQUARE) ) {
                alt68=1;
            }
            else if ( (LA68_0==LEFT_PAREN) ) {
                alt68=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:608:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator3542);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:608:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator3546);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:611:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:612:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA72_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt72=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt72=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 72, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:612:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName3564); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:612:10: ( typeArguments )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==LESS) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:612:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName3566);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLExpressions.g:613:9: ( DOT ID ( typeArguments )? )*
                    loop71:
                    do {
                        int alt71=2;
                        int LA71_0 = input.LA(1);

                        if ( (LA71_0==DOT) ) {
                            alt71=1;
                        }


                        switch (alt71) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:613:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName3579); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName3581); if (state.failed) return ;
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:613:18: ( typeArguments )?
                    	    int alt70=2;
                    	    int LA70_0 = input.LA(1);

                    	    if ( (LA70_0==LESS) ) {
                    	        alt70=1;
                    	    }
                    	    switch (alt70) {
                    	        case 1 :
                    	            // src/main/resources/org/drools/lang/DRLExpressions.g:613:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName3583);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop71;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:614:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName3598);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:617:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:618:5: ({...}? => ID classCreatorRest )
            // src/main/resources/org/drools/lang/DRLExpressions.g:618:7: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator3618); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator3620);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:621:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:622:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // src/main/resources/org/drools/lang/DRLExpressions.g:622:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3639); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:623:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==RIGHT_SQUARE) ) {
                alt76=1;
            }
            else if ( (LA76_0==FLOAT||(LA76_0>=HEX && LA76_0<=DECIMAL)||(LA76_0>=STRING && LA76_0<=TIME_INTERVAL)||(LA76_0>=BOOL && LA76_0<=NULL)||(LA76_0>=DECR && LA76_0<=INCR)||LA76_0==LESS||LA76_0==LEFT_PAREN||LA76_0==LEFT_SQUARE||(LA76_0>=NEGATION && LA76_0<=TILDE)||(LA76_0>=STAR && LA76_0<=PLUS)||LA76_0==ID) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:623:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3649); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:623:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop73:
                    do {
                        int alt73=2;
                        int LA73_0 = input.LA(1);

                        if ( (LA73_0==LEFT_SQUARE) ) {
                            alt73=1;
                        }


                        switch (alt73) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:623:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3652); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3654); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop73;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest3658);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:624:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest3672);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3674); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:624:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop74:
                    do {
                        int alt74=2;
                        alt74 = dfa74.predict(input);
                        switch (alt74) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:624:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3679); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest3681);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3683); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop74;
                        }
                    } while (true);

                    // src/main/resources/org/drools/lang/DRLExpressions.g:624:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop75:
                    do {
                        int alt75=2;
                        int LA75_0 = input.LA(1);

                        if ( (LA75_0==LEFT_SQUARE) ) {
                            int LA75_2 = input.LA(2);

                            if ( (LA75_2==RIGHT_SQUARE) && (synpred35_DRLExpressions())) {
                                alt75=1;
                            }


                        }


                        switch (alt75) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:624:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3695); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3697); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop75;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:628:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:629:5: ( arrayInitializer | expression )
            int alt77=2;
            int LA77_0 = input.LA(1);

            if ( (LA77_0==LEFT_CURLY) ) {
                alt77=1;
            }
            else if ( (LA77_0==FLOAT||(LA77_0>=HEX && LA77_0<=DECIMAL)||(LA77_0>=STRING && LA77_0<=TIME_INTERVAL)||(LA77_0>=BOOL && LA77_0<=NULL)||(LA77_0>=DECR && LA77_0<=INCR)||LA77_0==LESS||LA77_0==LEFT_PAREN||LA77_0==LEFT_SQUARE||(LA77_0>=NEGATION && LA77_0<=TILDE)||(LA77_0>=STAR && LA77_0<=PLUS)||LA77_0==ID) ) {
                alt77=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }
            switch (alt77) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:629:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer3726);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:630:13: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer3740);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:633:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:634:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // src/main/resources/org/drools/lang/DRLExpressions.g:634:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer3757); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLExpressions.g:634:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==FLOAT||(LA80_0>=HEX && LA80_0<=DECIMAL)||(LA80_0>=STRING && LA80_0<=TIME_INTERVAL)||(LA80_0>=BOOL && LA80_0<=NULL)||(LA80_0>=DECR && LA80_0<=INCR)||LA80_0==LESS||LA80_0==LEFT_PAREN||LA80_0==LEFT_SQUARE||LA80_0==LEFT_CURLY||(LA80_0>=NEGATION && LA80_0<=TILDE)||(LA80_0>=STAR && LA80_0<=PLUS)||LA80_0==ID) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:634:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3760);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:634:39: ( COMMA variableInitializer )*
                    loop78:
                    do {
                        int alt78=2;
                        int LA78_0 = input.LA(1);

                        if ( (LA78_0==COMMA) ) {
                            int LA78_1 = input.LA(2);

                            if ( (LA78_1==FLOAT||(LA78_1>=HEX && LA78_1<=DECIMAL)||(LA78_1>=STRING && LA78_1<=TIME_INTERVAL)||(LA78_1>=BOOL && LA78_1<=NULL)||(LA78_1>=DECR && LA78_1<=INCR)||LA78_1==LESS||LA78_1==LEFT_PAREN||LA78_1==LEFT_SQUARE||LA78_1==LEFT_CURLY||(LA78_1>=NEGATION && LA78_1<=TILDE)||(LA78_1>=STAR && LA78_1<=PLUS)||LA78_1==ID) ) {
                                alt78=1;
                            }


                        }


                        switch (alt78) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLExpressions.g:634:40: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3763); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer3765);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop78;
                        }
                    } while (true);

                    // src/main/resources/org/drools/lang/DRLExpressions.g:634:68: ( COMMA )?
                    int alt79=2;
                    int LA79_0 = input.LA(1);

                    if ( (LA79_0==COMMA) ) {
                        alt79=1;
                    }
                    switch (alt79) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:634:69: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer3770); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer3777); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:637:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:638:5: ( arguments )
            // src/main/resources/org/drools/lang/DRLExpressions.g:638:7: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest3794);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:641:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:642:5: ( nonWildcardTypeArguments arguments )
            // src/main/resources/org/drools/lang/DRLExpressions.g:642:7: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3812);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation3814);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:645:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:646:5: ( LESS typeList GREATER )
            // src/main/resources/org/drools/lang/DRLExpressions.g:646:7: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments3831); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments3833);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments3835); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:649:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:650:5: ( super_key superSuffix | ID arguments )
            int alt81=2;
            int LA81_0 = input.LA(1);

            if ( (LA81_0==ID) ) {
                int LA81_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt81=1;
                }
                else if ( (true) ) {
                    alt81=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 81, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 81, 0, input);

                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:650:7: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix3852);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3854);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:651:10: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix3865); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix3867);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:654:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        Token DOT23=null;
        Token DOT24=null;
        Token DOT25=null;
        Token ID26=null;
        Token LEFT_SQUARE27=null;
        Token RIGHT_SQUARE28=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:655:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt84=4;
            int LA84_0 = input.LA(1);

            if ( (LA84_0==DOT) ) {
                int LA84_1 = input.LA(2);

                if ( (synpred36_DRLExpressions()) ) {
                    alt84=1;
                }
                else if ( (synpred37_DRLExpressions()) ) {
                    alt84=2;
                }
                else if ( (synpred38_DRLExpressions()) ) {
                    alt84=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 84, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA84_0==LEFT_SQUARE) && (synpred40_DRLExpressions())) {
                alt84=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 84, 0, input);

                throw nvae;
            }
            switch (alt84) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:655:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    DOT23=(Token)match(input,DOT,FOLLOW_DOT_in_selector3892); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT23, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_super_key_in_selector3896);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector3898);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:656:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    DOT24=(Token)match(input,DOT,FOLLOW_DOT_in_selector3914); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT24, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_new_key_in_selector3918);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:656:84: ( nonWildcardTypeArguments )?
                    int alt82=2;
                    int LA82_0 = input.LA(1);

                    if ( (LA82_0==LESS) ) {
                        alt82=1;
                    }
                    switch (alt82) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:656:85: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector3921);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector3925);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:657:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    DOT25=(Token)match(input,DOT,FOLLOW_DOT_in_selector3941); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(DOT25, DroolsEditorType.SYMBOL); 
                    }
                    ID26=(Token)match(input,ID,FOLLOW_ID_in_selector3963); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(ID26, DroolsEditorType.IDENTIFIER); 
                    }
                    // src/main/resources/org/drools/lang/DRLExpressions.g:659:19: ( ( LEFT_PAREN )=> arguments )?
                    int alt83=2;
                    alt83 = dfa83.predict(input);
                    switch (alt83) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:659:20: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector3992);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:661:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    LEFT_SQUARE27=(Token)match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector4013); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(LEFT_SQUARE27, DroolsEditorType.SYMBOL); 
                    }
                    pushFollow(FOLLOW_expression_in_selector4040);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    RIGHT_SQUARE28=(Token)match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector4065); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       helper.emit(RIGHT_SQUARE28, DroolsEditorType.SYMBOL); 
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:666:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:667:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt86=2;
            int LA86_0 = input.LA(1);

            if ( (LA86_0==LEFT_PAREN) ) {
                alt86=1;
            }
            else if ( (LA86_0==DOT) ) {
                alt86=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 86, 0, input);

                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:667:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix4084);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:668:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix4095); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix4097); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLExpressions.g:668:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt85=2;
                    alt85 = dfa85.predict(input);
                    switch (alt85) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLExpressions.g:668:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix4106);
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:671:1: squareArguments returns [java.util.List<String> args] : LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE ;
    public final java.util.List<String> squareArguments() throws RecognitionException {
        java.util.List<String> args = null;

        java.util.List<String> el = null;


        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:672:5: ( LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE )
            // src/main/resources/org/drools/lang/DRLExpressions.g:672:7: LEFT_SQUARE (el= expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments4129); if (state.failed) return args;
            // src/main/resources/org/drools/lang/DRLExpressions.g:672:19: (el= expressionList )?
            int alt87=2;
            int LA87_0 = input.LA(1);

            if ( (LA87_0==FLOAT||(LA87_0>=HEX && LA87_0<=DECIMAL)||(LA87_0>=STRING && LA87_0<=TIME_INTERVAL)||(LA87_0>=BOOL && LA87_0<=NULL)||(LA87_0>=DECR && LA87_0<=INCR)||LA87_0==LESS||LA87_0==LEFT_PAREN||LA87_0==LEFT_SQUARE||(LA87_0>=NEGATION && LA87_0<=TILDE)||(LA87_0>=STAR && LA87_0<=PLUS)||LA87_0==ID) ) {
                alt87=1;
            }
            switch (alt87) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:672:20: el= expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments4134);
                    el=expressionList();

                    state._fsp--;
                    if (state.failed) return args;
                    if ( state.backtracking==0 ) {
                       args = el; 
                    }

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments4140); if (state.failed) return args;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:675:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        Token LEFT_PAREN29=null;
        Token RIGHT_PAREN30=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:676:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // src/main/resources/org/drools/lang/DRLExpressions.g:676:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            LEFT_PAREN29=(Token)match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments4157); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(LEFT_PAREN29, DroolsEditorType.SYMBOL); 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:677:9: ( expressionList )?
            int alt88=2;
            int LA88_0 = input.LA(1);

            if ( (LA88_0==FLOAT||(LA88_0>=HEX && LA88_0<=DECIMAL)||(LA88_0>=STRING && LA88_0<=TIME_INTERVAL)||(LA88_0>=BOOL && LA88_0<=NULL)||(LA88_0>=DECR && LA88_0<=INCR)||LA88_0==LESS||LA88_0==LEFT_PAREN||LA88_0==LEFT_SQUARE||(LA88_0>=NEGATION && LA88_0<=TILDE)||(LA88_0>=STAR && LA88_0<=PLUS)||LA88_0==ID) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:677:9: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments4169);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            RIGHT_PAREN30=(Token)match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments4180); if (state.failed) return ;
            if ( state.backtracking==0 ) {
               helper.emit(RIGHT_PAREN30, DroolsEditorType.SYMBOL); 
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:681:1: expressionList returns [java.util.List<String> exprs] : f= expression ( COMMA s= expression )* ;
    public final java.util.List<String> expressionList() throws RecognitionException {
        java.util.List<String> exprs = null;

        DRLExpressions.expression_return f = null;

        DRLExpressions.expression_return s = null;


         exprs = new java.util.ArrayList<String>();
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:683:3: (f= expression ( COMMA s= expression )* )
            // src/main/resources/org/drools/lang/DRLExpressions.g:683:7: f= expression ( COMMA s= expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList4210);
            f=expression();

            state._fsp--;
            if (state.failed) return exprs;
            if ( state.backtracking==0 ) {
               exprs.add( (f!=null?input.toString(f.start,f.stop):null) ); 
            }
            // src/main/resources/org/drools/lang/DRLExpressions.g:684:7: ( COMMA s= expression )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);

                if ( (LA89_0==COMMA) ) {
                    alt89=1;
                }


                switch (alt89) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLExpressions.g:684:8: COMMA s= expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList4221); if (state.failed) return exprs;
            	    pushFollow(FOLLOW_expression_in_expressionList4225);
            	    s=expression();

            	    state._fsp--;
            	    if (state.failed) return exprs;
            	    if ( state.backtracking==0 ) {
            	       exprs.add( (s!=null?input.toString(s.start,s.stop):null) ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop89;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:687:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:688:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt90=12;
            alt90 = dfa90.predict(input);
            switch (alt90) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:688:9: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4246); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:689:7: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator4254); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:690:7: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator4262); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:691:7: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator4270); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:692:7: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator4278); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:693:7: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator4286); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:694:7: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator4294); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:695:7: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator4302); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:696:7: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator4310); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:697:7: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator4318); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator4320); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4322); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:698:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4339); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4341); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4343); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4345); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // src/main/resources/org/drools/lang/DRLExpressions.g:699:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4360); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator4362); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4364); if (state.failed) return ;

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
    // src/main/resources/org/drools/lang/DRLExpressions.g:705:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:706:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:706:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key4394); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:709:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:710:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:710:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key4423); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:713:1: instanceof_key : {...}? =>id= ID ;
    public final DRLExpressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRLExpressions.instanceof_key_return retval = new DRLExpressions.instanceof_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:714:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:714:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key4452); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:717:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:718:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:718:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key4481); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:721:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:722:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:722:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key4510); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:725:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:726:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:726:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key4539); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:729:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:730:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:730:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key4568); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:733:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:734:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:734:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key4597); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:737:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:738:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:738:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key4626); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:741:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:742:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:742:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key4655); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:745:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:746:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:746:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key4684); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:749:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:750:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:750:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key4713); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:753:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:754:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:754:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key4742); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:757:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:758:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:758:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key4771); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:761:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:762:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:762:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key4801); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:765:1: not_key : {...}? =>id= ID ;
    public final void not_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:766:5: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:766:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key4830); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:769:1: in_key : {...}? =>id= ID ;
    public final void in_key() throws RecognitionException {
        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:770:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:770:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key4857); if (state.failed) return ;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:773:1: operator_key : {...}? =>id= ID ;
    public final DRLExpressions.operator_key_return operator_key() throws RecognitionException {
        DRLExpressions.operator_key_return retval = new DRLExpressions.operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:774:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:774:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key4882); if (state.failed) return retval;
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
    // src/main/resources/org/drools/lang/DRLExpressions.g:777:1: neg_operator_key : {...}? =>id= ID ;
    public final DRLExpressions.neg_operator_key_return neg_operator_key() throws RecognitionException {
        DRLExpressions.neg_operator_key_return retval = new DRLExpressions.neg_operator_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // src/main/resources/org/drools/lang/DRLExpressions.g:778:3: ({...}? =>id= ID )
            // src/main/resources/org/drools/lang/DRLExpressions.g:778:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key4907); if (state.failed) return retval;
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
        // src/main/resources/org/drools/lang/DRLExpressions.g:138:8: ( primitiveType )
        // src/main/resources/org/drools/lang/DRLExpressions.g:138:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRLExpressions539);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:138:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:138:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions550); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions552); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:13: ( typeArguments )
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRLExpressions576);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:55: ( typeArguments )
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRLExpressions590);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:139:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions602); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions604); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:165:10: ( assignmentOperator )
        // src/main/resources/org/drools/lang/DRLExpressions.g:165:11: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRLExpressions773);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:323:6: ( not_key in_key )
        // src/main/resources/org/drools/lang/DRLExpressions.g:323:7: not_key in_key
        {
        pushFollow(FOLLOW_not_key_in_synpred7_DRLExpressions1538);
        not_key();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_in_key_in_synpred7_DRLExpressions1540);
        in_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:382:5: ( operator | LEFT_PAREN )
        int alt91=2;
        int LA91_0 = input.LA(1);

        if ( ((LA91_0>=EQUALS && LA91_0<=LESS)||LA91_0==TILDE) ) {
            alt91=1;
        }
        else if ( (LA91_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
            alt91=1;
        }
        else if ( (LA91_0==LEFT_PAREN) ) {
            alt91=2;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("", 91, 0, input);

            throw nvae;
        }
        switch (alt91) {
            case 1 :
                // src/main/resources/org/drools/lang/DRLExpressions.g:382:7: operator
                {
                pushFollow(FOLLOW_operator_in_synpred8_DRLExpressions1755);
                operator();

                state._fsp--;
                if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRLExpressions.g:382:18: LEFT_PAREN
                {
                match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred8_DRLExpressions1759); if (state.failed) return ;

                }
                break;

        }}
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:393:7: ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )
        // src/main/resources/org/drools/lang/DRLExpressions.g:393:8: DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred9_DRLExpressions1812); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRLExpressions.g:393:20: ( fullAnnotation[null] )?
        int alt92=2;
        int LA92_0 = input.LA(1);

        if ( (LA92_0==AT) ) {
            alt92=1;
        }
        switch (alt92) {
            case 1 :
                // src/main/resources/org/drools/lang/DRLExpressions.g:393:20: fullAnnotation[null]
                {
                pushFollow(FOLLOW_fullAnnotation_in_synpred9_DRLExpressions1814);
                fullAnnotation(null);

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_andRestriction_in_synpred9_DRLExpressions1818);
        andRestriction();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:407:5: ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )
        // src/main/resources/org/drools/lang/DRLExpressions.g:407:6: DOUBLE_AMPER ( fullAnnotation[null] )? operator
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred10_DRLExpressions1881); if (state.failed) return ;
        // src/main/resources/org/drools/lang/DRLExpressions.g:407:19: ( fullAnnotation[null] )?
        int alt93=2;
        int LA93_0 = input.LA(1);

        if ( (LA93_0==AT) ) {
            alt93=1;
        }
        switch (alt93) {
            case 1 :
                // src/main/resources/org/drools/lang/DRLExpressions.g:407:19: fullAnnotation[null]
                {
                pushFollow(FOLLOW_fullAnnotation_in_synpred10_DRLExpressions1883);
                fullAnnotation(null);

                state._fsp--;
                if (state.failed) return ;

                }
                break;

        }

        pushFollow(FOLLOW_operator_in_synpred10_DRLExpressions1887);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:424:8: ( squareArguments shiftExpression )
        // src/main/resources/org/drools/lang/DRLExpressions.g:424:9: squareArguments shiftExpression
        {
        pushFollow(FOLLOW_squareArguments_in_synpred11_DRLExpressions1975);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;
        pushFollow(FOLLOW_shiftExpression_in_synpred11_DRLExpressions1977);
        shiftExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:447:7: ( shiftOp )
        // src/main/resources/org/drools/lang/DRLExpressions.g:447:8: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred12_DRLExpressions2066);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:458:11: ( PLUS | MINUS )
        // src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:490:9: ( castExpression )
        // src/main/resources/org/drools/lang/DRLExpressions.g:490:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred14_DRLExpressions2386);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:498:10: ( selector )
        // src/main/resources/org/drools/lang/DRLExpressions.g:498:11: selector
        {
        pushFollow(FOLLOW_selector_in_synpred15_DRLExpressions2519);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:517:10: ( INCR | DECR )
        // src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:521:8: ( LEFT_PAREN primitiveType )
        // src/main/resources/org/drools/lang/DRLExpressions.g:521:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions2577); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred17_DRLExpressions2579);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:522:8: ( LEFT_PAREN type )
        // src/main/resources/org/drools/lang/DRLExpressions.g:522:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred18_DRLExpressions2602); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred18_DRLExpressions2604);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:537:7: ( parExpression )
        // src/main/resources/org/drools/lang/DRLExpressions.g:537:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred19_DRLExpressions2712);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:538:9: ( nonWildcardTypeArguments )
        // src/main/resources/org/drools/lang/DRLExpressions.g:538:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred20_DRLExpressions2731);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:539:9: ( literal )
        // src/main/resources/org/drools/lang/DRLExpressions.g:539:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred21_DRLExpressions2756);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:541:9: ( super_key )
        // src/main/resources/org/drools/lang/DRLExpressions.g:541:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred22_DRLExpressions2778);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:542:9: ( new_key )
        // src/main/resources/org/drools/lang/DRLExpressions.g:542:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred23_DRLExpressions2795);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:543:9: ( primitiveType )
        // src/main/resources/org/drools/lang/DRLExpressions.g:543:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred24_DRLExpressions2812);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:545:9: ( inlineMapExpression )
        // src/main/resources/org/drools/lang/DRLExpressions.g:545:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred25_DRLExpressions2843);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:546:9: ( inlineListExpression )
        // src/main/resources/org/drools/lang/DRLExpressions.g:546:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred26_DRLExpressions2858);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:547:9: ( ID )
        // src/main/resources/org/drools/lang/DRLExpressions.g:547:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred27_DRLExpressions2873); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:549:15: ( DOT ID )
        // src/main/resources/org/drools/lang/DRLExpressions.g:549:16: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred28_DRLExpressions2907); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred28_DRLExpressions2909); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:551:15: ( DOT LEFT_PAREN )
        // src/main/resources/org/drools/lang/DRLExpressions.g:551:16: DOT LEFT_PAREN
        {
        match(input,DOT,FOLLOW_DOT_in_synpred29_DRLExpressions2951); if (state.failed) return ;
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred29_DRLExpressions2953); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:556:15: ( HASH ID )
        // src/main/resources/org/drools/lang/DRLExpressions.g:556:16: HASH ID
        {
        match(input,HASH,FOLLOW_HASH_in_synpred30_DRLExpressions3092); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred30_DRLExpressions3094); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:558:15: ( NULL_SAFE_DOT ID )
        // src/main/resources/org/drools/lang/DRLExpressions.g:558:16: NULL_SAFE_DOT ID
        {
        match(input,NULL_SAFE_DOT,FOLLOW_NULL_SAFE_DOT_in_synpred31_DRLExpressions3136); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred31_DRLExpressions3138); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:559:13: ( identifierSuffix )
        // src/main/resources/org/drools/lang/DRLExpressions.g:559:14: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred32_DRLExpressions3164);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:592:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:592:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred33_DRLExpressions3322); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred33_DRLExpressions3324); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:595:8: ( LEFT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:595:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred34_DRLExpressions3427); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:624:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:624:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred35_DRLExpressions3689); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred35_DRLExpressions3691); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:655:9: ( DOT super_key )
        // src/main/resources/org/drools/lang/DRLExpressions.g:655:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred36_DRLExpressions3887); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred36_DRLExpressions3889);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:656:9: ( DOT new_key )
        // src/main/resources/org/drools/lang/DRLExpressions.g:656:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred37_DRLExpressions3909); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred37_DRLExpressions3911);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:657:9: ( DOT ID )
        // src/main/resources/org/drools/lang/DRLExpressions.g:657:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred38_DRLExpressions3936); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred38_DRLExpressions3938); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:659:20: ( LEFT_PAREN )
        // src/main/resources/org/drools/lang/DRLExpressions.g:659:21: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions3987); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DRLExpressions

    // $ANTLR start synpred40_DRLExpressions
    public final void synpred40_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:661:9: ( LEFT_SQUARE )
        // src/main/resources/org/drools/lang/DRLExpressions.g:661:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred40_DRLExpressions4010); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_DRLExpressions

    // $ANTLR start synpred41_DRLExpressions
    public final void synpred41_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:668:18: ( LEFT_PAREN )
        // src/main/resources/org/drools/lang/DRLExpressions.g:668:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred41_DRLExpressions4101); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_DRLExpressions

    // $ANTLR start synpred42_DRLExpressions
    public final void synpred42_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:698:7: ( GREATER GREATER GREATER )
        // src/main/resources/org/drools/lang/DRLExpressions.g:698:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRLExpressions4331); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRLExpressions4333); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred42_DRLExpressions4335); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred42_DRLExpressions

    // $ANTLR start synpred43_DRLExpressions
    public final void synpred43_DRLExpressions_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLExpressions.g:699:7: ( GREATER GREATER )
        // src/main/resources/org/drools/lang/DRLExpressions.g:699:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRLExpressions4354); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred43_DRLExpressions4356); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred43_DRLExpressions

    // Delegated rules

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
    public final boolean synpred42_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_DRLExpressions_fragment(); // can never throw exception
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
    public final boolean synpred43_DRLExpressions() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred43_DRLExpressions_fragment(); // can never throw exception
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


    protected DFA16 dfa16 = new DFA16(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA39 dfa39 = new DFA39(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA55 dfa55 = new DFA55(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA74 dfa74 = new DFA74(this);
    protected DFA83 dfa83 = new DFA83(this);
    protected DFA85 dfa85 = new DFA85(this);
    protected DFA90 dfa90 = new DFA90(this);
    static final String DFA16_eotS =
        "\16\uffff";
    static final String DFA16_eofS =
        "\16\uffff";
    static final String DFA16_minS =
        "\1\24\13\0\2\uffff";
    static final String DFA16_maxS =
        "\1\104\13\0\2\uffff";
    static final String DFA16_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA16_specialS =
        "\1\uffff\1\3\1\1\1\12\1\10\1\7\1\6\1\5\1\4\1\2\1\0\1\11\2\uffff}>";
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
                        int LA16_10 = input.LA(1);

                         
                        int index16_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA16_2 = input.LA(1);

                         
                        int index16_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA16_9 = input.LA(1);

                         
                        int index16_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA16_1 = input.LA(1);

                         
                        int index16_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_1);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA16_8 = input.LA(1);

                         
                        int index16_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_8);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA16_7 = input.LA(1);

                         
                        int index16_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_7);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA16_6 = input.LA(1);

                         
                        int index16_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA16_5 = input.LA(1);

                         
                        int index16_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_5);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA16_4 = input.LA(1);

                         
                        int index16_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_4);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA16_11 = input.LA(1);

                         
                        int index16_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_11);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA16_3 = input.LA(1);

                         
                        int index16_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index16_3);
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
    static final String DFA37_eotS =
        "\40\uffff";
    static final String DFA37_eofS =
        "\1\1\37\uffff";
    static final String DFA37_minS =
        "\1\24\1\uffff\3\0\17\uffff\2\0\12\uffff";
    static final String DFA37_maxS =
        "\1\104\1\uffff\3\0\17\uffff\2\0\12\uffff";
    static final String DFA37_acceptS =
        "\1\uffff\1\2\32\uffff\4\1";
    static final String DFA37_specialS =
        "\1\0\1\uffff\1\1\1\2\1\3\17\uffff\1\4\1\5\12\uffff}>";
    static final String[] DFA37_transitionS = {
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
            return "()* loopback of 382:3: ( ( operator | LEFT_PAREN )=>right= orRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA37_0 = input.LA(1);

                         
                        int index37_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA37_0==EOF||(LA37_0>=AT && LA37_0<=MOD_ASSIGN)||(LA37_0>=SEMICOLON && LA37_0<=COLON)||LA37_0==EQUALS_ASSIGN||LA37_0==RIGHT_PAREN||LA37_0==RIGHT_SQUARE||(LA37_0>=RIGHT_CURLY && LA37_0<=COMMA)||(LA37_0>=DOUBLE_AMPER && LA37_0<=QUESTION)||(LA37_0>=PIPE && LA37_0<=XOR)) ) {s = 1;}

                        else if ( (LA37_0==ID) ) {s = 2;}

                        else if ( (LA37_0==EQUALS) ) {s = 3;}

                        else if ( (LA37_0==NOT_EQUALS) ) {s = 4;}

                        else if ( (LA37_0==LESS) ) {s = 20;}

                        else if ( (LA37_0==GREATER) ) {s = 21;}

                        else if ( (LA37_0==TILDE) && (synpred8_DRLExpressions())) {s = 28;}

                        else if ( (LA37_0==LESS_EQUALS) && (synpred8_DRLExpressions())) {s = 29;}

                        else if ( (LA37_0==GREATER_EQUALS) && (synpred8_DRLExpressions())) {s = 30;}

                        else if ( (LA37_0==LEFT_PAREN) && (synpred8_DRLExpressions())) {s = 31;}

                         
                        input.seek(index37_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA37_2 = input.LA(1);

                         
                        int index37_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred8_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT))))||(synpred8_DRLExpressions()&&((helper.isPluggableEvaluator(false)))))) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA37_3 = input.LA(1);

                         
                        int index37_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA37_4 = input.LA(1);

                         
                        int index37_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA37_20 = input.LA(1);

                         
                        int index37_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_20);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA37_21 = input.LA(1);

                         
                        int index37_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 31;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index37_21);
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
    static final String DFA39_eotS =
        "\41\uffff";
    static final String DFA39_eofS =
        "\1\1\40\uffff";
    static final String DFA39_minS =
        "\1\24\10\uffff\1\0\27\uffff";
    static final String DFA39_maxS =
        "\1\104\10\uffff\1\0\27\uffff";
    static final String DFA39_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA39_specialS =
        "\11\uffff\1\0\27\uffff}>";
    static final String[] DFA39_transitionS = {
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

    static final short[] DFA39_eot = DFA.unpackEncodedString(DFA39_eotS);
    static final short[] DFA39_eof = DFA.unpackEncodedString(DFA39_eofS);
    static final char[] DFA39_min = DFA.unpackEncodedStringToUnsignedChars(DFA39_minS);
    static final char[] DFA39_max = DFA.unpackEncodedStringToUnsignedChars(DFA39_maxS);
    static final short[] DFA39_accept = DFA.unpackEncodedString(DFA39_acceptS);
    static final short[] DFA39_special = DFA.unpackEncodedString(DFA39_specialS);
    static final short[][] DFA39_transition;

    static {
        int numStates = DFA39_transitionS.length;
        DFA39_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA39_transition[i] = DFA.unpackEncodedString(DFA39_transitionS[i]);
        }
    }

    class DFA39 extends DFA {

        public DFA39(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 39;
            this.eot = DFA39_eot;
            this.eof = DFA39_eof;
            this.min = DFA39_min;
            this.max = DFA39_max;
            this.accept = DFA39_accept;
            this.special = DFA39_special;
            this.transition = DFA39_transition;
        }
        public String getDescription() {
            return "()* loopback of 393:5: ( ( DOUBLE_PIPE ( fullAnnotation[null] )? andRestriction )=>lop= DOUBLE_PIPE (args= fullAnnotation[null] )? right= andRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA39_9 = input.LA(1);

                         
                        int index39_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index39_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 39, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA42_eotS =
        "\41\uffff";
    static final String DFA42_eofS =
        "\1\1\40\uffff";
    static final String DFA42_minS =
        "\1\24\10\uffff\1\0\27\uffff";
    static final String DFA42_maxS =
        "\1\104\10\uffff\1\0\27\uffff";
    static final String DFA42_acceptS =
        "\1\uffff\1\2\36\uffff\1\1";
    static final String DFA42_specialS =
        "\11\uffff\1\0\27\uffff}>";
    static final String[] DFA42_transitionS = {
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

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        public String getDescription() {
            return "()* loopback of 407:3: ( ( DOUBLE_AMPER ( fullAnnotation[null] )? operator )=>lop= DOUBLE_AMPER (args= fullAnnotation[null] )? right= singleRestriction )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA42_9 = input.LA(1);

                         
                        int index42_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 32;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index42_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 42, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA43_eotS =
        "\24\uffff";
    static final String DFA43_eofS =
        "\24\uffff";
    static final String DFA43_minS =
        "\1\10\1\0\22\uffff";
    static final String DFA43_maxS =
        "\1\104\1\0\22\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\2\20\uffff\1\1";
    static final String DFA43_specialS =
        "\1\uffff\1\0\22\uffff}>";
    static final String[] DFA43_transitionS = {
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
            return "424:6: ( ( squareArguments shiftExpression )=>sa= squareArguments value= shiftExpression | value= shiftExpression )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA43_1 = input.LA(1);

                         
                        int index43_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 19;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index43_1);
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
        "\42\uffff";
    static final String DFA45_eofS =
        "\1\1\41\uffff";
    static final String DFA45_minS =
        "\1\24\5\uffff\2\0\32\uffff";
    static final String DFA45_maxS =
        "\1\104\5\uffff\2\0\32\uffff";
    static final String DFA45_acceptS =
        "\1\uffff\1\2\37\uffff\1\1";
    static final String DFA45_specialS =
        "\6\uffff\1\0\1\1\32\uffff}>";
    static final String[] DFA45_transitionS = {
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
            return "()* loopback of 447:5: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA45_6 = input.LA(1);

                         
                        int index45_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 33;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index45_6);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA45_7 = input.LA(1);

                         
                        int index45_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 33;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index45_7);
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
    static final String DFA53_eotS =
        "\20\uffff";
    static final String DFA53_eofS =
        "\20\uffff";
    static final String DFA53_minS =
        "\1\10\2\uffff\1\0\14\uffff";
    static final String DFA53_maxS =
        "\1\104\2\uffff\1\0\14\uffff";
    static final String DFA53_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\12\uffff\1\3";
    static final String DFA53_specialS =
        "\3\uffff\1\0\14\uffff}>";
    static final String[] DFA53_transitionS = {
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
            return "486:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | ( ({...}? (var= ID COLON ) ) | ({...}? (var= ID UNIFY ) ) )? left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA53_3 = input.LA(1);

                         
                        int index53_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_DRLExpressions()) ) {s = 15;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index53_3);
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
    static final String DFA55_eotS =
        "\12\uffff";
    static final String DFA55_eofS =
        "\12\uffff";
    static final String DFA55_minS =
        "\1\104\1\0\10\uffff";
    static final String DFA55_maxS =
        "\1\104\1\0\10\uffff";
    static final String DFA55_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA55_specialS =
        "\1\1\1\0\10\uffff}>";
    static final String[] DFA55_transitionS = {
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

    static final short[] DFA55_eot = DFA.unpackEncodedString(DFA55_eotS);
    static final short[] DFA55_eof = DFA.unpackEncodedString(DFA55_eofS);
    static final char[] DFA55_min = DFA.unpackEncodedStringToUnsignedChars(DFA55_minS);
    static final char[] DFA55_max = DFA.unpackEncodedStringToUnsignedChars(DFA55_maxS);
    static final short[] DFA55_accept = DFA.unpackEncodedString(DFA55_acceptS);
    static final short[] DFA55_special = DFA.unpackEncodedString(DFA55_specialS);
    static final short[][] DFA55_transition;

    static {
        int numStates = DFA55_transitionS.length;
        DFA55_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA55_transition[i] = DFA.unpackEncodedString(DFA55_transitionS[i]);
        }
    }

    class DFA55 extends DFA {

        public DFA55(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 55;
            this.eot = DFA55_eot;
            this.eof = DFA55_eof;
            this.min = DFA55_min;
            this.max = DFA55_max;
            this.accept = DFA55_accept;
            this.special = DFA55_special;
            this.transition = DFA55_transition;
        }
        public String getDescription() {
            return "525:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA55_1 = input.LA(1);

                         
                        int index55_1 = input.index();
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

                         
                        input.seek(index55_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA55_0 = input.LA(1);

                         
                        int index55_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA55_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index55_0);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 55, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\23\uffff";
    static final String DFA61_eofS =
        "\23\uffff";
    static final String DFA61_minS =
        "\1\10\12\uffff\2\0\6\uffff";
    static final String DFA61_maxS =
        "\1\104\12\uffff\2\0\6\uffff";
    static final String DFA61_acceptS =
        "\1\uffff\1\1\1\2\10\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA61_specialS =
        "\1\0\12\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA61_transitionS = {
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

    static final short[] DFA61_eot = DFA.unpackEncodedString(DFA61_eotS);
    static final short[] DFA61_eof = DFA.unpackEncodedString(DFA61_eofS);
    static final char[] DFA61_min = DFA.unpackEncodedStringToUnsignedChars(DFA61_minS);
    static final char[] DFA61_max = DFA.unpackEncodedStringToUnsignedChars(DFA61_maxS);
    static final short[] DFA61_accept = DFA.unpackEncodedString(DFA61_acceptS);
    static final short[] DFA61_special = DFA.unpackEncodedString(DFA61_specialS);
    static final short[][] DFA61_transition;

    static {
        int numStates = DFA61_transitionS.length;
        DFA61_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA61_transition[i] = DFA.unpackEncodedString(DFA61_transitionS[i]);
        }
    }

    class DFA61 extends DFA {

        public DFA61(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 61;
            this.eot = DFA61_eot;
            this.eof = DFA61_eof;
            this.min = DFA61_min;
            this.max = DFA61_max;
            this.accept = DFA61_accept;
            this.special = DFA61_special;
            this.transition = DFA61_transition;
        }
        public String getDescription() {
            return "536:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=>i1= ID ( ( ( DOT ID )=> DOT i2= ID ) | ( ( DOT LEFT_PAREN )=> DOT LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN ) | ( ( HASH ID )=> HASH i2= ID ) | ( ( NULL_SAFE_DOT ID )=> NULL_SAFE_DOT i2= ID ) )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_0 = input.LA(1);

                         
                        int index61_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA61_0==LEFT_PAREN) && (synpred19_DRLExpressions())) {s = 1;}

                        else if ( (LA61_0==LESS) && (synpred20_DRLExpressions())) {s = 2;}

                        else if ( (LA61_0==STRING) && (synpred21_DRLExpressions())) {s = 3;}

                        else if ( (LA61_0==DECIMAL) && (synpred21_DRLExpressions())) {s = 4;}

                        else if ( (LA61_0==HEX) && (synpred21_DRLExpressions())) {s = 5;}

                        else if ( (LA61_0==FLOAT) && (synpred21_DRLExpressions())) {s = 6;}

                        else if ( (LA61_0==BOOL) && (synpred21_DRLExpressions())) {s = 7;}

                        else if ( (LA61_0==NULL) && (synpred21_DRLExpressions())) {s = 8;}

                        else if ( (LA61_0==TIME_INTERVAL) && (synpred21_DRLExpressions())) {s = 9;}

                        else if ( (LA61_0==STAR) && (synpred21_DRLExpressions())) {s = 10;}

                        else if ( (LA61_0==ID) ) {s = 11;}

                        else if ( (LA61_0==LEFT_SQUARE) ) {s = 12;}

                         
                        input.seek(index61_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_11 = input.LA(1);

                         
                        int index61_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred22_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 13;}

                        else if ( ((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 14;}

                        else if ( (((synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BOOLEAN))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred24_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))))) ) {s = 15;}

                        else if ( (synpred27_DRLExpressions()) ) {s = 16;}

                         
                        input.seek(index61_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA61_12 = input.LA(1);

                         
                        int index61_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred25_DRLExpressions()) ) {s = 17;}

                        else if ( (synpred26_DRLExpressions()) ) {s = 18;}

                         
                        input.seek(index61_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 61, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA60_eotS =
        "\47\uffff";
    static final String DFA60_eofS =
        "\1\3\46\uffff";
    static final String DFA60_minS =
        "\1\24\2\0\44\uffff";
    static final String DFA60_maxS =
        "\1\105\2\0\44\uffff";
    static final String DFA60_acceptS =
        "\3\uffff\1\2\42\uffff\1\1";
    static final String DFA60_specialS =
        "\1\uffff\1\0\1\1\44\uffff}>";
    static final String[] DFA60_transitionS = {
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

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "559:12: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA60_1 = input.LA(1);

                         
                        int index60_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred32_DRLExpressions()) ) {s = 38;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index60_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA60_2 = input.LA(1);

                         
                        int index60_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred32_DRLExpressions()) ) {s = 38;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index60_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 60, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\47\uffff";
    static final String DFA65_eofS =
        "\1\1\46\uffff";
    static final String DFA65_minS =
        "\1\24\43\uffff\1\0\2\uffff";
    static final String DFA65_maxS =
        "\1\105\43\uffff\1\0\2\uffff";
    static final String DFA65_acceptS =
        "\1\uffff\1\2\44\uffff\1\1";
    static final String DFA65_specialS =
        "\44\uffff\1\0\2\uffff}>";
    static final String[] DFA65_transitionS = {
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

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "()+ loopback of 595:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA65_36 = input.LA(1);

                         
                        int index65_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred34_DRLExpressions()) ) {s = 38;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index65_36);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 65, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA74_eotS =
        "\47\uffff";
    static final String DFA74_eofS =
        "\1\2\46\uffff";
    static final String DFA74_minS =
        "\1\24\1\0\45\uffff";
    static final String DFA74_maxS =
        "\1\105\1\0\45\uffff";
    static final String DFA74_acceptS =
        "\2\uffff\1\2\43\uffff\1\1";
    static final String DFA74_specialS =
        "\1\uffff\1\0\45\uffff}>";
    static final String[] DFA74_transitionS = {
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
            return "()* loopback of 624:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
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
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 38;}

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
    static final String DFA83_eotS =
        "\47\uffff";
    static final String DFA83_eofS =
        "\1\2\46\uffff";
    static final String DFA83_minS =
        "\1\24\1\0\45\uffff";
    static final String DFA83_maxS =
        "\1\105\1\0\45\uffff";
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
            return "659:19: ( ( LEFT_PAREN )=> arguments )?";
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
                        if ( (synpred39_DRLExpressions()) ) {s = 38;}

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
    static final String DFA85_eotS =
        "\47\uffff";
    static final String DFA85_eofS =
        "\1\2\46\uffff";
    static final String DFA85_minS =
        "\1\24\1\0\45\uffff";
    static final String DFA85_maxS =
        "\1\105\1\0\45\uffff";
    static final String DFA85_acceptS =
        "\2\uffff\1\2\43\uffff\1\1";
    static final String DFA85_specialS =
        "\1\uffff\1\0\45\uffff}>";
    static final String[] DFA85_transitionS = {
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

    static final short[] DFA85_eot = DFA.unpackEncodedString(DFA85_eotS);
    static final short[] DFA85_eof = DFA.unpackEncodedString(DFA85_eofS);
    static final char[] DFA85_min = DFA.unpackEncodedStringToUnsignedChars(DFA85_minS);
    static final char[] DFA85_max = DFA.unpackEncodedStringToUnsignedChars(DFA85_maxS);
    static final short[] DFA85_accept = DFA.unpackEncodedString(DFA85_acceptS);
    static final short[] DFA85_special = DFA.unpackEncodedString(DFA85_specialS);
    static final short[][] DFA85_transition;

    static {
        int numStates = DFA85_transitionS.length;
        DFA85_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA85_transition[i] = DFA.unpackEncodedString(DFA85_transitionS[i]);
        }
    }

    class DFA85 extends DFA {

        public DFA85(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 85;
            this.eot = DFA85_eot;
            this.eof = DFA85_eof;
            this.min = DFA85_min;
            this.max = DFA85_max;
            this.accept = DFA85_accept;
            this.special = DFA85_special;
            this.transition = DFA85_transition;
        }
        public String getDescription() {
            return "668:17: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA85_1 = input.LA(1);

                         
                        int index85_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred41_DRLExpressions()) ) {s = 38;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index85_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 85, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA90_eotS =
        "\17\uffff";
    static final String DFA90_eofS =
        "\17\uffff";
    static final String DFA90_minS =
        "\1\25\12\uffff\2\47\2\uffff";
    static final String DFA90_maxS =
        "\1\51\12\uffff\1\47\1\51\2\uffff";
    static final String DFA90_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA90_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA90_transitionS = {
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

    static final short[] DFA90_eot = DFA.unpackEncodedString(DFA90_eotS);
    static final short[] DFA90_eof = DFA.unpackEncodedString(DFA90_eofS);
    static final char[] DFA90_min = DFA.unpackEncodedStringToUnsignedChars(DFA90_minS);
    static final char[] DFA90_max = DFA.unpackEncodedStringToUnsignedChars(DFA90_maxS);
    static final short[] DFA90_accept = DFA.unpackEncodedString(DFA90_acceptS);
    static final short[] DFA90_special = DFA.unpackEncodedString(DFA90_specialS);
    static final short[][] DFA90_transition;

    static {
        int numStates = DFA90_transitionS.length;
        DFA90_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA90_transition[i] = DFA.unpackEncodedString(DFA90_transitionS[i]);
        }
    }

    class DFA90 extends DFA {

        public DFA90(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 90;
            this.eot = DFA90_eot;
            this.eof = DFA90_eof;
            this.min = DFA90_min;
            this.max = DFA90_max;
            this.accept = DFA90_accept;
            this.special = DFA90_special;
            this.transition = DFA90_transition;
        }
        public String getDescription() {
            return "687:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA90_12 = input.LA(1);

                         
                        int index90_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA90_12==GREATER) && (synpred42_DRLExpressions())) {s = 13;}

                        else if ( (LA90_12==EQUALS_ASSIGN) && (synpred43_DRLExpressions())) {s = 14;}

                         
                        input.seek(index90_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 90, _s, input);
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
    public static final BitSet FOLLOW_STAR_in_literal206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_operator247 = new BitSet(new long[]{0x008001F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_EQUALS_in_operator258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_complexOp_in_relationalOp405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp420 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_complexOp468 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_complexOp472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList493 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList496 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_typeList498 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_typeMatch_in_type520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_typeMatch546 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch556 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch558 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_ID_in_typeMatch572 = new BitSet(new long[]{0x0002110000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch579 = new BitSet(new long[]{0x0002100000000002L});
    public static final BitSet FOLLOW_DOT_in_typeMatch584 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_typeMatch586 = new BitSet(new long[]{0x0002110000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch593 = new BitSet(new long[]{0x0002100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch608 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch610 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments631 = new BitSet(new long[]{0x0020000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments633 = new BitSet(new long[]{0x0001008000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments636 = new BitSet(new long[]{0x0020000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments638 = new BitSet(new long[]{0x0001008000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument667 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_super_key_in_typeArgument675 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_typeArgument678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy702 = new BitSet(new long[]{0x0000080200100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_set_in_dummy704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_dummy2738 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_dummy2740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression759 = new BitSet(new long[]{0x000003801FE00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression780 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_expression784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression811 = new BitSet(new long[]{0x0020000000000002L});
    public static final BitSet FOLLOW_ternaryExpression_in_conditionalExpression823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ternaryExpression845 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression849 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_ternaryExpression851 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_ternaryExpression855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_fullAnnotation885 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_fullAnnotation889 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_DOT_in_fullAnnotation895 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_fullAnnotation899 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_annotationArgs_in_fullAnnotation920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_annotationArgs936 = new BitSet(new long[]{0x0000080000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_annotationArgs953 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_annotationElementValuePairs_in_annotationArgs966 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_annotationArgs980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs995 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_annotationElementValuePairs1000 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_annotationElementValuePair_in_annotationElementValuePairs1002 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_ID_in_annotationElementValuePair1023 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_annotationElementValuePair1025 = new BitSet(new long[]{0x70C05500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_annotationValue_in_annotationElementValuePair1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_annotationValue1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_annotationArray_in_annotationValue1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_annotationArray1062 = new BitSet(new long[]{0x70C0D500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_annotationValue_in_annotationArray1066 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_COMMA_in_annotationArray1070 = new BitSet(new long[]{0x70C05500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_annotationValue_in_annotationArray1072 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_annotationArray1080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1101 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression1110 = new BitSet(new long[]{0x70C01500C01CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_conditionalOrExpression1132 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1138 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1173 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression1181 = new BitSet(new long[]{0x70C01500C01CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_conditionalAndExpression1204 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression1210 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1245 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression1253 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression1257 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1292 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression1300 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression1304 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1339 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression1347 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression1351 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1386 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression1398 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression1404 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression1420 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression1455 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression1465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression1479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression1524 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_not_key_in_inExpression1544 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_in_key_in_inExpression1548 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1550 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_inExpression1572 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1591 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_inExpression1595 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression1632 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1634 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_inExpression1656 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1675 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_inExpression1679 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1741 = new BitSet(new long[]{0x008005F800000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_orRestriction_in_relationalExpression1766 = new BitSet(new long[]{0x008005F800000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1801 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_orRestriction1823 = new BitSet(new long[]{0x008005F800100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_orRestriction1827 = new BitSet(new long[]{0x008005F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_andRestriction_in_orRestriction1833 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_EOF_in_orRestriction1852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1872 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andRestriction1892 = new BitSet(new long[]{0x008005F800100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_andRestriction1913 = new BitSet(new long[]{0x008005F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_singleRestriction_in_andRestriction1918 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_operator_in_singleRestriction1954 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_squareArguments_in_singleRestriction1983 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_shiftExpression_in_singleRestriction1987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_singleRestriction2000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_singleRestriction2025 = new BitSet(new long[]{0x008005F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_orRestriction_in_singleRestriction2029 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_singleRestriction2031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2055 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression2069 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression2071 = new BitSet(new long[]{0x0000018000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp2091 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp2093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2105 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2107 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2121 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp2123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2151 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression2172 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression2180 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2208 = new BitSet(new long[]{0x1800000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression2220 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression2234 = new BitSet(new long[]{0x1800000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression2260 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression2282 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression2286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression2306 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_primary_in_unaryExpression2308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression2318 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_primary_in_unaryExpression2320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression2332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus2362 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus2373 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus2375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus2389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2417 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_unaryExpressionNotPlusMinus2419 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_unaryExpressionNotPlusMinus2458 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_UNIFY_in_unaryExpressionNotPlusMinus2460 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus2505 = new BitSet(new long[]{0x00021000C0000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus2522 = new BitSet(new long[]{0x00021000C0000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus2552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2584 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression2586 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2588 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression2609 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_castExpression2611 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression2613 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression2615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType2634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType2642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType2650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType2658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType2666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType2674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType2682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType2690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary2718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary2735 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary2738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary2742 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_primary2744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary2782 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary2784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary2799 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_creator_in_primary2801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary2816 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary2819 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary2821 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_DOT_in_primary2825 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_class_key_in_primary2827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary2847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary2862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary2878 = new BitSet(new long[]{0x8006140000000002L});
    public static final BitSet FOLLOW_DOT_in_primary2912 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_primary2916 = new BitSet(new long[]{0x8006140000000002L});
    public static final BitSet FOLLOW_DOT_in_primary2956 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_primary2958 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_primary2998 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_COMMA_in_primary3001 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_primary3005 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_primary3045 = new BitSet(new long[]{0x8006140000000002L});
    public static final BitSet FOLLOW_HASH_in_primary3097 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_primary3101 = new BitSet(new long[]{0x8006140000000002L});
    public static final BitSet FOLLOW_NULL_SAFE_DOT_in_primary3141 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_primary3145 = new BitSet(new long[]{0x8006140000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression3188 = new BitSet(new long[]{0x70C03500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression3190 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression3193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression3214 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression3216 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression3218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3239 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList3242 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList3244 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry3263 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry3265 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_mapEntry3267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression3288 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_parExpression3292 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression3294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3328 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3369 = new BitSet(new long[]{0x0002100000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix3413 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix3417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix3432 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix3462 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix3490 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix3506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator3528 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_createdName_in_creator3531 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator3542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator3546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName3564 = new BitSet(new long[]{0x0002010000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3566 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName3579 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_createdName3581 = new BitSet(new long[]{0x0002010000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName3583 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName3598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator3618 = new BitSet(new long[]{0x0000140000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3639 = new BitSet(new long[]{0x70C03500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3649 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3652 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3654 = new BitSet(new long[]{0x0000500000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest3658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3672 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3674 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3679 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest3681 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3683 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest3695 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest3697 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer3726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer3740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer3757 = new BitSet(new long[]{0x70C0D500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3760 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3763 = new BitSet(new long[]{0x70C05500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer3765 = new BitSet(new long[]{0x0001800000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer3770 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer3777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest3794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation3812 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation3814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments3831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments3833 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments3835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix3852 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix3854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix3865 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix3867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3892 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_super_key_in_selector3896 = new BitSet(new long[]{0x0002040000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector3898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_new_key_in_selector3918 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector3921 = new BitSet(new long[]{0x0000010000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_innerCreator_in_selector3925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector3941 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_selector3963 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_arguments_in_selector3992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector4013 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_selector4040 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector4065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix4095 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_superSuffix4097 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix4106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments4129 = new BitSet(new long[]{0x70C03500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments4134 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments4140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments4157 = new BitSet(new long[]{0x70C01D00C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expressionList_in_arguments4169 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments4180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList4210 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList4221 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_expression_in_expressionList4225 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator4254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator4262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator4270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator4278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator4286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator4294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator4310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator4318 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator4320 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4339 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4341 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4343 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4360 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator4362 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator4364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key4394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key4423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key4452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key4481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key4510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key4539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key4568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key4597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key4626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key4655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key4684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key4713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key4742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key4771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key4801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key4830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key4857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key4882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key4907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions550 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions602 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_synpred7_DRLExpressions1538 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_in_key_in_synpred7_DRLExpressions1540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_in_synpred8_DRLExpressions1755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred8_DRLExpressions1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred9_DRLExpressions1812 = new BitSet(new long[]{0x008005F800100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_synpred9_DRLExpressions1814 = new BitSet(new long[]{0x008005F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_andRestriction_in_synpred9_DRLExpressions1818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred10_DRLExpressions1881 = new BitSet(new long[]{0x008001F800100000L,0x0000000000000010L});
    public static final BitSet FOLLOW_fullAnnotation_in_synpred10_DRLExpressions1883 = new BitSet(new long[]{0x008001F800000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_operator_in_synpred10_DRLExpressions1887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred11_DRLExpressions1975 = new BitSet(new long[]{0x70C01500C00CD900L,0x0000000000000010L});
    public static final BitSet FOLLOW_shiftExpression_in_synpred11_DRLExpressions1977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred12_DRLExpressions2066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred13_DRLExpressions2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred14_DRLExpressions2386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred15_DRLExpressions2519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred16_DRLExpressions2545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions2577 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_primitiveType_in_synpred17_DRLExpressions2579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred18_DRLExpressions2602 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_type_in_synpred18_DRLExpressions2604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred19_DRLExpressions2712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred20_DRLExpressions2731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred21_DRLExpressions2756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred22_DRLExpressions2778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred23_DRLExpressions2795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred24_DRLExpressions2812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred25_DRLExpressions2843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred26_DRLExpressions2858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRLExpressions2873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred28_DRLExpressions2907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_synpred28_DRLExpressions2909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred29_DRLExpressions2951 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred29_DRLExpressions2953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HASH_in_synpred30_DRLExpressions3092 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_synpred30_DRLExpressions3094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_SAFE_DOT_in_synpred31_DRLExpressions3136 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_synpred31_DRLExpressions3138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred32_DRLExpressions3164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred33_DRLExpressions3322 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred33_DRLExpressions3324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred34_DRLExpressions3427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred35_DRLExpressions3689 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred35_DRLExpressions3691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred36_DRLExpressions3887 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_super_key_in_synpred36_DRLExpressions3889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred37_DRLExpressions3909 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_new_key_in_synpred37_DRLExpressions3911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred38_DRLExpressions3936 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_synpred38_DRLExpressions3938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred39_DRLExpressions3987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred40_DRLExpressions4010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred41_DRLExpressions4101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred42_DRLExpressions4331 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred42_DRLExpressions4333 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred42_DRLExpressions4335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred43_DRLExpressions4354 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred43_DRLExpressions4356 = new BitSet(new long[]{0x0000000000000002L});

}