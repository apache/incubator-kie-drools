// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-03-03 20:40:10

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
    


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLExpressions extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "WS", "Exponent", "FloatTypeSuffix", "FLOAT", "HexDigit", "IntegerTypeSuffix", "HEX", "DECIMAL", "EscapeSequence", "STRING", "TimePeriod", "UnicodeEscape", "OctalEscape", "BOOL", "NULL", "AT", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "DECR", "INCR", "ARROW", "SEMICOLON", "COLON", "EQUALS", "NOT_EQUALS", "GREATER_EQUALS", "LESS_EQUALS", "GREATER", "LESS", "EQUALS_ASSIGN", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "COMMA", "DOT", "DOUBLE_AMPER", "DOUBLE_PIPE", "QUESTION", "NEGATION", "TILDE", "PIPE", "AMPER", "XOR", "MOD", "STAR", "MINUS", "PLUS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "ID", "DIV", "MISC"
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
    public static final int TimePeriod=15;
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
    public static final int DECR=29;
    public static final int INCR=30;
    public static final int ARROW=31;
    public static final int SEMICOLON=32;
    public static final int COLON=33;
    public static final int EQUALS=34;
    public static final int NOT_EQUALS=35;
    public static final int GREATER_EQUALS=36;
    public static final int LESS_EQUALS=37;
    public static final int GREATER=38;
    public static final int LESS=39;
    public static final int EQUALS_ASSIGN=40;
    public static final int LEFT_PAREN=41;
    public static final int RIGHT_PAREN=42;
    public static final int LEFT_SQUARE=43;
    public static final int RIGHT_SQUARE=44;
    public static final int LEFT_CURLY=45;
    public static final int RIGHT_CURLY=46;
    public static final int COMMA=47;
    public static final int DOT=48;
    public static final int DOUBLE_AMPER=49;
    public static final int DOUBLE_PIPE=50;
    public static final int QUESTION=51;
    public static final int NEGATION=52;
    public static final int TILDE=53;
    public static final int PIPE=54;
    public static final int AMPER=55;
    public static final int XOR=56;
    public static final int MOD=57;
    public static final int STAR=58;
    public static final int MINUS=59;
    public static final int PLUS=60;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=61;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=62;
    public static final int MULTI_LINE_COMMENT=63;
    public static final int IdentifierStart=64;
    public static final int IdentifierPart=65;
    public static final int ID=66;
    public static final int DIV=67;
    public static final int MISC=68;

    // delegates
    // delegators


        public DRLExpressions(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DRLExpressions(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return DRLExpressions.tokenNames; }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g"; }


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



    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:61:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL );
    public final void literal() throws RecognitionException {
        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL )
            int alt1=6;
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
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:7: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal83); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:7: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal98); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:7: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal112); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:7: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal130); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:7: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal146); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:67:7: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal163); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(NULL6, DroolsEditorType.NULL_CONST);	
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
    // $ANTLR end "literal"


    // $ANTLR start "typeList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:5: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:7: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList189);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:12: ( COMMA type )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:13: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList192); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList194);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop2;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:1: type : tm= typeMatch ;
    public final DRLExpressions.type_return type() throws RecognitionException {
        DRLExpressions.type_return retval = new DRLExpressions.type_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:5: (tm= typeMatch )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:8: tm= typeMatch
            {
            pushFollow(FOLLOW_typeMatch_in_type216);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:1: typeMatch : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void typeMatch() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                int LA8_1 = input.LA(2);

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))))) ) {
                    alt8=1;
                }
                else if ( (true) ) {
                    alt8=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_typeMatch247);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==LEFT_SQUARE) && (synpred2_DRLExpressions())) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch257); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch259); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_typeMatch273); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:12: ( ( typeArguments )=> typeArguments )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==LESS) ) {
                        int LA4_1 = input.LA(2);

                        if ( (LA4_1==ID) && (synpred3_DRLExpressions())) {
                            alt4=1;
                        }
                        else if ( (LA4_1==QUESTION) && (synpred3_DRLExpressions())) {
                            alt4=1;
                        }
                    }
                    switch (alt4) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_typeMatch280);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==DOT) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_typeMatch285); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_typeMatch287); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt5=2;
                    	    int LA5_0 = input.LA(1);

                    	    if ( (LA5_0==LESS) ) {
                    	        int LA5_1 = input.LA(2);

                    	        if ( (LA5_1==ID) && (synpred4_DRLExpressions())) {
                    	            alt5=1;
                    	        }
                    	        else if ( (LA5_1==QUESTION) && (synpred4_DRLExpressions())) {
                    	            alt5=1;
                    	        }
                    	    }
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_typeMatch294);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==LEFT_SQUARE) && (synpred5_DRLExpressions())) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_typeMatch309); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_typeMatch311); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:83:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments336); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments338);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:25: ( COMMA typeArgument )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:26: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments341); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments343);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments347); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:88:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ID) ) {
                alt12=1;
            }
            else if ( (LA12_0==QUESTION) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:88:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument364);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument372); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:16: ( ( extends_key | super_key ) type )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:17: ( extends_key | super_key ) type
                            {
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:17: ( extends_key | super_key )
                            int alt10=2;
                            int LA10_0 = input.LA(1);

                            if ( (LA10_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                                int LA10_1 = input.LA(2);

                                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                                    alt10=1;
                                }
                                else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                                    alt10=2;
                                }
                                else {
                                    if (state.backtracking>0) {state.failed=true; return ;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 10, 1, input);

                                    throw nvae;
                                }
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 0, input);

                                throw nvae;
                            }
                            switch (alt10) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument376);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument380);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument383);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:97:1: dummy : expression ( AT | SEMICOLON | EOF | ID ) ;
    public final void dummy() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:5: ( expression ( AT | SEMICOLON | EOF | ID ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:7: expression ( AT | SEMICOLON | EOF | ID )
            {
            pushFollow(FOLLOW_expression_in_dummy407);
            expression();

            state._fsp--;
            if (state.failed) return ;
            if ( input.LA(1)==EOF||input.LA(1)==AT||input.LA(1)==SEMICOLON||input.LA(1)==ID ) {
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:100:1: dummy2 : relationalExpression EOF ;
    public final void dummy2() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:5: ( relationalExpression EOF )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:8: relationalExpression EOF
            {
            pushFollow(FOLLOW_relationalExpression_in_dummy2443);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            match(input,EOF,FOLLOW_EOF_in_dummy2445); if (state.failed) return ;

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


    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:104:1: expression returns [BaseDescr result] : left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator left= expression )? ;
    public final BaseDescr expression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:5: (left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator left= expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:7: left= conditionalExpression ( ( assignmentOperator )=>op= assignmentOperator left= expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression468);
            left=conditionalExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:9: ( ( assignmentOperator )=>op= assignmentOperator left= expression )?
            int alt13=2;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:10: ( assignmentOperator )=>op= assignmentOperator left= expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression489);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_expression493);
                    left=expression();

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
    // $ANTLR end "expression"


    // $ANTLR start "conditionalExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:109:1: conditionalExpression returns [BaseDescr result] : left= conditionalOrExpression ( QUESTION ts= expression COLON fs= expression )? ;
    public final BaseDescr conditionalExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr ts = null;

        BaseDescr fs = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:5: (left= conditionalOrExpression ( QUESTION ts= expression COLON fs= expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:9: left= conditionalOrExpression ( QUESTION ts= expression COLON fs= expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression520);
            left=conditionalOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:9: ( QUESTION ts= expression COLON fs= expression )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==QUESTION) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:111:11: QUESTION ts= expression COLON fs= expression
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_conditionalExpression534); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_conditionalExpression538);
                    ts=expression();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression540); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_conditionalExpression544);
                    fs=expression();

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


    // $ANTLR start "conditionalOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:1: conditionalOrExpression returns [BaseDescr result] : left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )* ;
    public final BaseDescr conditionalOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:3: (left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:115:5: left= conditionalAndExpression ( DOUBLE_PIPE right= conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression569);
            left=conditionalAndExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:116:3: ( DOUBLE_PIPE right= conditionalAndExpression )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==DOUBLE_PIPE) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:116:5: DOUBLE_PIPE right= conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression578); if (state.failed) return result;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression582);
            	    right=conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newOr(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop15;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:127:1: conditionalAndExpression returns [BaseDescr result] : left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )* ;
    public final BaseDescr conditionalAndExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:128:3: (left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:128:5: left= inclusiveOrExpression ( DOUBLE_AMPER right= inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression619);
            left=inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:129:3: ( DOUBLE_AMPER right= inclusiveOrExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==DOUBLE_AMPER) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:129:5: DOUBLE_AMPER right= inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression627); if (state.failed) return result;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression631);
            	    right=inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newAnd(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop16;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:140:1: inclusiveOrExpression returns [BaseDescr result] : left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* ;
    public final BaseDescr inclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:3: (left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:5: left= exclusiveOrExpression ( PIPE right= exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression667);
            left=exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:3: ( PIPE right= exclusiveOrExpression )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==PIPE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:5: PIPE right= exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression675); if (state.failed) return result;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression679);
            	    right=exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncOr(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:1: exclusiveOrExpression returns [BaseDescr result] : left= andExpression ( XOR right= andExpression )* ;
    public final BaseDescr exclusiveOrExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:3: (left= andExpression ( XOR right= andExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:5: left= andExpression ( XOR right= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression715);
            left=andExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:3: ( XOR right= andExpression )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==XOR) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:5: XOR right= andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression723); if (state.failed) return result;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression727);
            	    right=andExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newXor(); 
            	                     descr.addOrMerge( result );  
            	                     descr.addOrMerge( right ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop18;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:1: andExpression returns [BaseDescr result] : left= andOrRestriction ( AMPER right= andOrRestriction )* ;
    public final BaseDescr andExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:3: (left= andOrRestriction ( AMPER right= andOrRestriction )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:5: left= andOrRestriction ( AMPER right= andOrRestriction )*
            {
            pushFollow(FOLLOW_andOrRestriction_in_andExpression763);
            left=andOrRestriction();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:3: ( AMPER right= andOrRestriction )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AMPER) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:5: AMPER right= andOrRestriction
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression771); if (state.failed) return result;
            	    pushFollow(FOLLOW_andOrRestriction_in_andExpression775);
            	    right=andOrRestriction();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = ConstraintConnectiveDescr.newIncAnd(); 
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
    // $ANTLR end "andExpression"


    // $ANTLR start "andOrRestriction"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:1: andOrRestriction returns [BaseDescr result] : left= equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression )* ( EOF )? ;
    public final BaseDescr andOrRestriction() throws RecognitionException {
        BaseDescr result = null;

        Token lop=null;
        DRLExpressions.equalityExpression_return left = null;

        DRLExpressions.operator_return op = null;

        DRLExpressions.shiftExpression_return right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:3: (left= equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression )* ( EOF )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:5: left= equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression )* ( EOF )?
            {
            pushFollow(FOLLOW_equalityExpression_in_andOrRestriction815);
            left=equalityExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = (left!=null?left.result:null); } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:3: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression )*
            loop21:
            do {
                int alt21=2;
                alt21 = dfa21.predict(input);
                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:5: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:44: (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER )
            	    int alt20=2;
            	    int LA20_0 = input.LA(1);

            	    if ( (LA20_0==DOUBLE_PIPE) ) {
            	        alt20=1;
            	    }
            	    else if ( (LA20_0==DOUBLE_AMPER) ) {
            	        alt20=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return result;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 20, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt20) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:45: lop= DOUBLE_PIPE
            	            {
            	            lop=(Token)match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_andOrRestriction836); if (state.failed) return result;

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:61: lop= DOUBLE_AMPER
            	            {
            	            lop=(Token)match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_andOrRestriction840); if (state.failed) return result;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_operator_in_andOrRestriction845);
            	    op=operator();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    pushFollow(FOLLOW_shiftExpression_in_andOrRestriction849);
            	    right=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     ConstraintConnectiveDescr descr = (lop!=null?lop.getText():null).equals("||") ? ConstraintConnectiveDescr.newOr() : ConstraintConnectiveDescr.newAnd(); 
            	                     descr.addOrMerge( result );  
            	                     
            	                     RelationalExprDescr re = new RelationalExprDescr( (op!=null?input.toString(op.start,op.stop):null), new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ), new AtomicExprDescr( (right!=null?input.toString(right.start,right.stop):null) ) );
            	                     descr.addOrMerge( re ); 
            	                     result = descr;
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:6: ( EOF )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==EOF) ) {
                int LA22_1 = input.LA(2);

                if ( ((LA22_1>=AT && LA22_1<=MOD_ASSIGN)||(LA22_1>=SEMICOLON && LA22_1<=COLON)||(LA22_1>=GREATER && LA22_1<=EQUALS_ASSIGN)||LA22_1==RIGHT_PAREN||LA22_1==RIGHT_SQUARE||(LA22_1>=RIGHT_CURLY && LA22_1<=COMMA)||(LA22_1>=DOUBLE_AMPER && LA22_1<=QUESTION)||(LA22_1>=PIPE && LA22_1<=XOR)||LA22_1==ID) ) {
                    alt22=1;
                }
                else if ( (LA22_1==EOF) ) {
                    int LA22_4 = input.LA(3);

                    if ( (LA22_4==EOF) ) {
                        alt22=1;
                    }
                }
            }
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:6: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_andOrRestriction868); if (state.failed) return result;

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
    // $ANTLR end "andOrRestriction"

    public static class equalityExpression_return extends ParserRuleReturnScope {
        public BaseDescr result;
    };

    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:194:1: equalityExpression returns [BaseDescr result] : left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* ;
    public final DRLExpressions.equalityExpression_return equalityExpression() throws RecognitionException {
        DRLExpressions.equalityExpression_return retval = new DRLExpressions.equalityExpression_return();
        retval.start = input.LT(1);

        Token op=null;
        BaseDescr left = null;

        BaseDescr right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:3: (left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:5: left= instanceOfExpression ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression892);
            left=instanceOfExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { retval.result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:3: ( (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>=EQUALS && LA24_0<=NOT_EQUALS)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:5: (op= EQUALS | op= NOT_EQUALS ) right= instanceOfExpression
            	    {
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:5: (op= EQUALS | op= NOT_EQUALS )
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==EQUALS) ) {
            	        alt23=1;
            	    }
            	    else if ( (LA23_0==NOT_EQUALS) ) {
            	        alt23=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 23, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:7: op= EQUALS
            	            {
            	            op=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_equalityExpression904); if (state.failed) return retval;

            	            }
            	            break;
            	        case 2 :
            	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:19: op= NOT_EQUALS
            	            {
            	            op=(Token)match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_equalityExpression910); if (state.failed) return retval;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression916);
            	    right=instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     retval.result = new RelationalExprDescr( (op!=null?op.getText():null), left, right );
            	                 }
            	               
            	    }

            	    }
            	    break;

            	default :
            	    break loop24;
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
    // $ANTLR end "equalityExpression"


    // $ANTLR start "instanceOfExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:1: instanceOfExpression returns [BaseDescr result] : left= inExpression (op= instanceof_key right= type )? ;
    public final BaseDescr instanceOfExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;

        DRLExpressions.instanceof_key_return op = null;

        DRLExpressions.type_return right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:3: (left= inExpression (op= instanceof_key right= type )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:5: left= inExpression (op= instanceof_key right= type )?
            {
            pushFollow(FOLLOW_inExpression_in_instanceOfExpression952);
            left=inExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:3: (op= instanceof_key right= type )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ID) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))) {
                    alt25=1;
                }
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:4: op= instanceof_key right= type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression961);
                    op=instanceof_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_type_in_instanceOfExpression965);
                    right=type();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr && state.backtracking == 0 ) {
                                     result = new RelationalExprDescr( (op!=null?input.toString(op.start,op.stop):null), left, new AtomicExprDescr((right!=null?input.toString(right.start,right.stop):null)) );
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:1: inExpression returns [BaseDescr result] : left= relationalExpression ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )? ;
    public final BaseDescr inExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:3: (left= relationalExpression ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:5: left= relationalExpression ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )?
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression1000);
            left=relationalExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:5: ( not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN | in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )?
            int alt28=3;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==ID) ) {
                int LA28_1 = input.LA(2);

                if ( (LA28_1==ID) ) {
                    int LA28_3 = input.LA(3);

                    if ( (LA28_3==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                        alt28=1;
                    }
                }
                else if ( (LA28_1==LEFT_PAREN) && (((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) {
                    alt28=2;
                }
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:7: not_key in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_not_key_in_inExpression1010);
                    not_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_in_key_in_inExpression1014);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1016); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_inExpression1018);
                    expression();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:47: ( COMMA expression )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==COMMA) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:48: COMMA expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1021); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1023);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1027); if (state.failed) return result;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:7: in= in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    {
                    pushFollow(FOLLOW_in_key_in_inExpression1037);
                    in_key();

                    state._fsp--;
                    if (state.failed) return result;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression1039); if (state.failed) return result;
                    pushFollow(FOLLOW_expression_in_inExpression1041);
                    expression();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:39: ( COMMA expression )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==COMMA) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:217:40: COMMA expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression1044); if (state.failed) return result;
                    	    pushFollow(FOLLOW_expression_in_inExpression1046);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression1050); if (state.failed) return result;

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


    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:221:1: relationalExpression returns [BaseDescr result] : left= shiftExpression ( ( relationalOp )=>op= relationalOp right= shiftExpression )* ;
    public final BaseDescr relationalExpression() throws RecognitionException {
        BaseDescr result = null;

        DRLExpressions.shiftExpression_return left = null;

        DRLExpressions.relationalOp_return op = null;

        DRLExpressions.shiftExpression_return right = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:3: (left= shiftExpression ( ( relationalOp )=>op= relationalOp right= shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:5: left= shiftExpression ( ( relationalOp )=>op= relationalOp right= shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression1077);
            left=shiftExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = ( (left!=null?left.result:null) != null ) ? (left!=null?left.result:null) : new AtomicExprDescr( (left!=null?input.toString(left.start,left.stop):null) ) ; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:3: ( ( relationalOp )=>op= relationalOp right= shiftExpression )*
            loop29:
            do {
                int alt29=2;
                alt29 = dfa29.predict(input);
                switch (alt29) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:5: ( relationalOp )=>op= relationalOp right= shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression1092);
            	    op=relationalOp();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression1096);
            	    right=shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return result;
            	    if ( state.backtracking==0 ) {
            	       if( buildDescr && state.backtracking == 0 ) {
            	                     result = new RelationalExprDescr( (op!=null?input.toString(op.start,op.stop):null), result,  ( (right!=null?right.result:null) != null ) ? (right!=null?right.result:null) : new AtomicExprDescr( (right!=null?input.toString(right.start,right.stop):null) ) );
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
    // $ANTLR end "relationalExpression"

    public static class operator_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "operator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:1: operator : ( EQUALS | NOT_EQUALS | relationalOp ) ;
    public final DRLExpressions.operator_return operator() throws RecognitionException {
        DRLExpressions.operator_return retval = new DRLExpressions.operator_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:3: ( ( EQUALS | NOT_EQUALS | relationalOp ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:5: ( EQUALS | NOT_EQUALS | relationalOp )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:5: ( EQUALS | NOT_EQUALS | relationalOp )
            int alt30=3;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==EQUALS) ) {
                alt30=1;
            }
            else if ( (LA30_0==NOT_EQUALS) ) {
                alt30=2;
            }
            else if ( ((LA30_0>=GREATER_EQUALS && LA30_0<=LESS)) ) {
                alt30=3;
            }
            else if ( (LA30_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt30=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:7: EQUALS
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_operator1128); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:233:7: NOT_EQUALS
                    {
                    match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator1136); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:7: relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator1144);
                    relationalOp();

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
    // $ANTLR end "operator"

    public static class relationalOp_return extends ParserRuleReturnScope {
    };

    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:1: relationalOp : ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) ;
    public final DRLExpressions.relationalOp_return relationalOp() throws RecognitionException {
        DRLExpressions.relationalOp_return retval = new DRLExpressions.relationalOp_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:3: ( ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            int alt33=6;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==LESS_EQUALS) ) {
                alt33=1;
            }
            else if ( (LA33_0==GREATER_EQUALS) ) {
                alt33=2;
            }
            else if ( (LA33_0==LESS) ) {
                alt33=3;
            }
            else if ( (LA33_0==GREATER) ) {
                alt33=4;
            }
            else if ( (LA33_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                int LA33_5 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt33=5;
                }
                else if ( (((helper.isPluggableEvaluator(false)))) ) {
                    alt33=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 5, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:7: LESS_EQUALS
                    {
                    match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp1167); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:7: GREATER_EQUALS
                    {
                    match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp1175); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:7: LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_relationalOp1184); if (state.failed) return retval;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:7: GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_relationalOp1193); if (state.failed) return retval;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:7: not_key neg_operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp1201);
                    not_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp1203);
                    neg_operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:32: ( ( squareArguments )=> squareArguments )?
                    int alt31=2;
                    alt31 = dfa31.predict(input);
                    switch (alt31) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:33: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1211);
                            squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:7: operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp1221);
                    operator_key();

                    state._fsp--;
                    if (state.failed) return retval;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:21: ( ( squareArguments )=> squareArguments )?
                    int alt32=2;
                    alt32 = dfa32.predict(input);
                    switch (alt32) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:22: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp1230);
                            squareArguments();

                            state._fsp--;
                            if (state.failed) return retval;

                            }
                            break;

                    }


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
    // $ANTLR end "relationalOp"

    public static class shiftExpression_return extends ParserRuleReturnScope {
        public BaseDescr result;
    };

    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:248:1: shiftExpression returns [BaseDescr result] : left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final DRLExpressions.shiftExpression_return shiftExpression() throws RecognitionException {
        DRLExpressions.shiftExpression_return retval = new DRLExpressions.shiftExpression_return();
        retval.start = input.LT(1);

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:3: (left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:5: left= additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression1263);
            left=additiveExpression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { retval.result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:5: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop34:
            do {
                int alt34=2;
                alt34 = dfa34.predict(input);
                switch (alt34) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:7: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression1277);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression1279);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return retval;

            	    }
            	    break;

            	default :
            	    break loop34;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:253:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt35=3;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==LESS) ) {
                alt35=1;
            }
            else if ( (LA35_0==GREATER) ) {
                int LA35_2 = input.LA(2);

                if ( (LA35_2==GREATER) ) {
                    int LA35_3 = input.LA(3);

                    if ( (LA35_3==GREATER) ) {
                        alt35=2;
                    }
                    else if ( (LA35_3==EOF||LA35_3==FLOAT||(LA35_3>=HEX && LA35_3<=DECIMAL)||LA35_3==STRING||(LA35_3>=BOOL && LA35_3<=NULL)||(LA35_3>=DECR && LA35_3<=INCR)||LA35_3==LESS||LA35_3==LEFT_PAREN||LA35_3==LEFT_SQUARE||(LA35_3>=NEGATION && LA35_3<=TILDE)||(LA35_3>=MINUS && LA35_3<=PLUS)||LA35_3==ID) ) {
                        alt35=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 35, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:9: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp1299); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp1301); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:11: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1314); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1316); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1318); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:11: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1331); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp1333); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:259:1: additiveExpression returns [BaseDescr result] : left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final BaseDescr additiveExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:260:5: (left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:260:9: left= multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1361);
            left=multiplicativeExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:9: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=MINUS && LA36_0<=PLUS)) && (synpred12_DRLExpressions())) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:11: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1390);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return result;

            	    }
            	    break;

            	default :
            	    break loop36;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:264:1: multiplicativeExpression returns [BaseDescr result] : left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final BaseDescr multiplicativeExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:5: (left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:9: left= unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1418);
            left=unaryExpression();

            state._fsp--;
            if (state.failed) return result;
            if ( state.backtracking==0 ) {
               if( buildDescr && state.backtracking == 0 ) { result = left; } 
            }
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:7: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=MOD && LA37_0<=STAR)||LA37_0==DIV) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:9: ( STAR | DIV | MOD ) unaryExpression
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

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1444);
            	    unaryExpression();

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
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:269:1: unaryExpression returns [BaseDescr result] : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus );
    public final BaseDescr unaryExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | left= unaryExpressionNotPlusMinus )
            int alt38=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt38=1;
                }
                break;
            case MINUS:
                {
                alt38=2;
                }
                break;
            case INCR:
                {
                alt38=3;
                }
                break;
            case DECR:
                {
                alt38=4;
                }
                break;
            case FLOAT:
            case HEX:
            case DECIMAL:
            case STRING:
            case BOOL:
            case NULL:
            case LESS:
            case LEFT_PAREN:
            case LEFT_SQUARE:
            case NEGATION:
            case TILDE:
            case ID:
                {
                alt38=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return result;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:9: PLUS unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression1470); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1472);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:7: MINUS unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression1480); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1482);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression1492); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression1494);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression1504); if (state.failed) return result;
                    pushFollow(FOLLOW_primary_in_unaryExpression1506);
                    primary();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:274:9: left= unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1518);
                    left=unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr && state.backtracking == 0 ) { result = left; } 
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


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final BaseDescr unaryExpressionNotPlusMinus() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr left = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt41=4;
            alt41 = dfa41.predict(input);
            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1543); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1545);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1554); if (state.failed) return result;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1556);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1570);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:9: left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1582);
                    left=primary();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                       if( buildDescr && state.backtracking == 0 ) { result = left; } 
                    }
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:9: ( ( selector )=> selector )*
                    loop39:
                    do {
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==DOT) && (synpred14_DRLExpressions())) {
                            alt39=1;
                        }
                        else if ( (LA39_0==LEFT_SQUARE) && (synpred14_DRLExpressions())) {
                            alt39=1;
                        }


                        switch (alt39) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:10: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus1599);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop39;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:33: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( ((LA40_0>=DECR && LA40_0<=INCR)) && (synpred15_DRLExpressions())) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:34: ( INCR | DECR )=> ( INCR | DECR )
                            {
                            if ( (input.LA(1)>=DECR && input.LA(1)<=INCR) ) {
                                input.consume();
                                state.errorRecovery=false;state.failed=false;
                            }
                            else {
                                if (state.backtracking>0) {state.failed=true; return result;}
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


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
    // $ANTLR end "unaryExpressionNotPlusMinus"


    // $ANTLR start "castExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:285:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        BaseDescr expr = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==LEFT_PAREN) ) {
                int LA42_1 = input.LA(2);

                if ( (synpred16_DRLExpressions()) ) {
                    alt42=1;
                }
                else if ( (synpred17_DRLExpressions()) ) {
                    alt42=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN expr= unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1649); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression1651);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1653); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression1657);
                    expr=unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1675); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression1677);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1679); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1681);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:290:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:291:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt43=8;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:291:9: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType1704);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType1712);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType1720);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType1728);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType1736);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType1744);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType1752);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:298:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType1760);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final BaseDescr primary() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr expr = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:5: ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt48=9;
            alt48 = dfa48.predict(input);
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:7: ( parExpression )=>expr= parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary1788);
                    expr=parExpression();

                    state._fsp--;
                    if (state.failed) return result;
                    if ( state.backtracking==0 ) {
                        if( buildDescr && state.backtracking == 0 ) { result = expr; }  
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary1805);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==ID) ) {
                        int LA44_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt44=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt44=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return result;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 44, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return result;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 0, input);

                        throw nvae;
                    }
                    switch (alt44) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary1808);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary1812);
                            this_key();

                            state._fsp--;
                            if (state.failed) return result;
                            pushFollow(FOLLOW_arguments_in_primary1814);
                            arguments();

                            state._fsp--;
                            if (state.failed) return result;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary1830);
                    literal();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary1850);
                    super_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_superSuffix_in_primary1852);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary1868);
                    new_key();

                    state._fsp--;
                    if (state.failed) return result;
                    pushFollow(FOLLOW_creator_in_primary1870);
                    creator();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary1886);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop45:
                    do {
                        int alt45=2;
                        int LA45_0 = input.LA(1);

                        if ( (LA45_0==LEFT_SQUARE) ) {
                            alt45=1;
                        }


                        switch (alt45) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary1889); if (state.failed) return result;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary1891); if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop45;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary1895); if (state.failed) return result;
                    pushFollow(FOLLOW_class_key_in_primary1897);
                    class_key();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary1918);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary1934);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return result;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    match(input,ID,FOLLOW_ID_in_primary1948); if (state.failed) return result;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:18: ( ( DOT ID )=> DOT ID )*
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==DOT) ) {
                            int LA46_2 = input.LA(2);

                            if ( (LA46_2==ID) ) {
                                int LA46_3 = input.LA(3);

                                if ( (synpred27_DRLExpressions()) ) {
                                    alt46=1;
                                }


                            }


                        }


                        switch (alt46) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:19: ( DOT ID )=> DOT ID
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary1957); if (state.failed) return result;
                    	    match(input,ID,FOLLOW_ID_in_primary1959); if (state.failed) return result;

                    	    }
                    	    break;

                    	default :
                    	    break loop46;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt47=2;
                    alt47 = dfa47.predict(input);
                    switch (alt47) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:39: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary1968);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression1990); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:21: ( expressionList )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==FLOAT||(LA49_0>=HEX && LA49_0<=DECIMAL)||LA49_0==STRING||(LA49_0>=BOOL && LA49_0<=NULL)||(LA49_0>=DECR && LA49_0<=INCR)||LA49_0==LESS||LA49_0==LEFT_PAREN||LA49_0==LEFT_SQUARE||(LA49_0>=NEGATION && LA49_0<=TILDE)||(LA49_0>=MINUS && LA49_0<=PLUS)||LA49_0==ID) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression1992);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression1995); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:1: inlineMapExpression : LEFT_SQUARE mapExpressionList RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:5: ( LEFT_SQUARE mapExpressionList RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:7: LEFT_SQUARE mapExpressionList RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression2017); if (state.failed) return ;
            pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression2019);
            mapExpressionList();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression2021); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "inlineMapExpression"


    // $ANTLR start "mapExpressionList"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList2039);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:16: ( COMMA mapEntry )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==COMMA) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList2042); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList2044);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop50;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry2067);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry2069); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry2071);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:1: parExpression returns [BaseDescr result] : LEFT_PAREN expr= expression RIGHT_PAREN ;
    public final BaseDescr parExpression() throws RecognitionException {
        BaseDescr result = null;

        BaseDescr expr = null;


        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:332:5: ( LEFT_PAREN expr= expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:332:7: LEFT_PAREN expr= expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression2093); if (state.failed) return result;
            pushFollow(FOLLOW_expression_in_parExpression2097);
            expr=expression();

            state._fsp--;
            if (state.failed) return result;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression2099); if (state.failed) return result;
            if ( state.backtracking==0 ) {
                if( buildDescr && state.backtracking == 0 ) { result = expr; }  
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:336:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt53=3;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==LEFT_SQUARE) ) {
                int LA53_1 = input.LA(2);

                if ( (LA53_1==RIGHT_SQUARE) && (synpred29_DRLExpressions())) {
                    alt53=1;
                }
                else if ( (LA53_1==FLOAT||(LA53_1>=HEX && LA53_1<=DECIMAL)||LA53_1==STRING||(LA53_1>=BOOL && LA53_1<=NULL)||(LA53_1>=DECR && LA53_1<=INCR)||LA53_1==LESS||LA53_1==LEFT_PAREN||LA53_1==LEFT_SQUARE||(LA53_1>=NEGATION && LA53_1<=TILDE)||(LA53_1>=MINUS && LA53_1<=PLUS)||LA53_1==ID) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA53_0==LEFT_PAREN) ) {
                alt53=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt51=0;
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==LEFT_SQUARE) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix2134); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix2136); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt51 >= 1 ) break loop51;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(51, input);
                                throw eee;
                        }
                        cnt51++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix2140); if (state.failed) return ;
                    pushFollow(FOLLOW_class_key_in_identifierSuffix2142);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt52=0;
                    loop52:
                    do {
                        int alt52=2;
                        alt52 = dfa52.predict(input);
                        switch (alt52) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix2157); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix2159);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix2161); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt52 >= 1 ) break loop52;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(52, input);
                                throw eee;
                        }
                        cnt52++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:339:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix2174);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:347:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:7: ( nonWildcardTypeArguments )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==LESS) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:348:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator2197);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator2200);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:9: ( arrayCreatorRest | classCreatorRest )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_SQUARE) ) {
                alt55=1;
            }
            else if ( (LA55_0==LEFT_PAREN) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator2211);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator2215);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:352:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA59_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt59=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt59=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }
            switch (alt59) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName2233); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:10: ( typeArguments )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==LESS) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName2235);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:9: ( DOT ID ( typeArguments )? )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==DOT) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName2248); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName2250); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:18: ( typeArguments )?
                    	    int alt57=2;
                    	    int LA57_0 = input.LA(1);

                    	    if ( (LA57_0==LESS) ) {
                    	        alt57=1;
                    	    }
                    	    switch (alt57) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName2252);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:355:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName2267);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:359:5: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:359:7: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator2287); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator2289);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:363:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:363:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2308); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt63=2;
            int LA63_0 = input.LA(1);

            if ( (LA63_0==RIGHT_SQUARE) ) {
                alt63=1;
            }
            else if ( (LA63_0==FLOAT||(LA63_0>=HEX && LA63_0<=DECIMAL)||LA63_0==STRING||(LA63_0>=BOOL && LA63_0<=NULL)||(LA63_0>=DECR && LA63_0<=INCR)||LA63_0==LESS||LA63_0==LEFT_PAREN||LA63_0==LEFT_SQUARE||(LA63_0>=NEGATION && LA63_0<=TILDE)||(LA63_0>=MINUS && LA63_0<=PLUS)||LA63_0==ID) ) {
                alt63=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2318); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LEFT_SQUARE) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:364:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2321); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2323); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest2327);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest2341);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2343); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop61:
                    do {
                        int alt61=2;
                        alt61 = dfa61.predict(input);
                        switch (alt61) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2348); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest2350);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2352); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop62:
                    do {
                        int alt62=2;
                        int LA62_0 = input.LA(1);

                        if ( (LA62_0==LEFT_SQUARE) ) {
                            int LA62_2 = input.LA(2);

                            if ( (LA62_2==RIGHT_SQUARE) && (synpred31_DRLExpressions())) {
                                alt62=1;
                            }


                        }


                        switch (alt62) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2364); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2366); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop62;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:369:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:370:5: ( arrayInitializer | expression )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFT_CURLY) ) {
                alt64=1;
            }
            else if ( (LA64_0==FLOAT||(LA64_0>=HEX && LA64_0<=DECIMAL)||LA64_0==STRING||(LA64_0>=BOOL && LA64_0<=NULL)||(LA64_0>=DECR && LA64_0<=INCR)||LA64_0==LESS||LA64_0==LEFT_PAREN||LA64_0==LEFT_SQUARE||(LA64_0>=NEGATION && LA64_0<=TILDE)||(LA64_0>=MINUS && LA64_0<=PLUS)||LA64_0==ID) ) {
                alt64=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:370:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer2395);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:371:13: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer2409);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:374:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer2426); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==FLOAT||(LA67_0>=HEX && LA67_0<=DECIMAL)||LA67_0==STRING||(LA67_0>=BOOL && LA67_0<=NULL)||(LA67_0>=DECR && LA67_0<=INCR)||LA67_0==LESS||LA67_0==LEFT_PAREN||LA67_0==LEFT_SQUARE||LA67_0==LEFT_CURLY||(LA67_0>=NEGATION && LA67_0<=TILDE)||(LA67_0>=MINUS && LA67_0<=PLUS)||LA67_0==ID) ) {
                alt67=1;
            }
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2429);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:39: ( COMMA variableInitializer )*
                    loop65:
                    do {
                        int alt65=2;
                        int LA65_0 = input.LA(1);

                        if ( (LA65_0==COMMA) ) {
                            int LA65_1 = input.LA(2);

                            if ( (LA65_1==FLOAT||(LA65_1>=HEX && LA65_1<=DECIMAL)||LA65_1==STRING||(LA65_1>=BOOL && LA65_1<=NULL)||(LA65_1>=DECR && LA65_1<=INCR)||LA65_1==LESS||LA65_1==LEFT_PAREN||LA65_1==LEFT_SQUARE||LA65_1==LEFT_CURLY||(LA65_1>=NEGATION && LA65_1<=TILDE)||(LA65_1>=MINUS && LA65_1<=PLUS)||LA65_1==ID) ) {
                                alt65=1;
                            }


                        }


                        switch (alt65) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:40: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2432); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer2434);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop65;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:68: ( COMMA )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==COMMA) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:375:69: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer2439); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer2446); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:378:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:379:5: ( arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:379:7: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest2463);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:382:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:5: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:383:7: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2481);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation2483);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:386:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:5: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:387:7: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments2500); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments2502);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments2504); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:390:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:391:5: ( super_key superSuffix | ID arguments )
            int alt68=2;
            int LA68_0 = input.LA(1);

            if ( (LA68_0==ID) ) {
                int LA68_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt68=1;
                }
                else if ( (true) ) {
                    alt68=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 68, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:391:7: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix2521);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2523);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:392:10: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix2534); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix2536);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:395:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt71=4;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==DOT) ) {
                int LA71_1 = input.LA(2);

                if ( (synpred32_DRLExpressions()) ) {
                    alt71=1;
                }
                else if ( (synpred33_DRLExpressions()) ) {
                    alt71=2;
                }
                else if ( (synpred34_DRLExpressions()) ) {
                    alt71=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 71, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA71_0==LEFT_SQUARE) && (synpred36_DRLExpressions())) {
                alt71=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2561); if (state.failed) return ;
                    pushFollow(FOLLOW_super_key_in_selector2563);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector2565);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2581); if (state.failed) return ;
                    pushFollow(FOLLOW_new_key_in_selector2583);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:36: ( nonWildcardTypeArguments )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==LESS) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:37: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector2586);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector2590);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2606); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_selector2608); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:26: ( ( LEFT_PAREN )=> arguments )?
                    int alt70=2;
                    int LA70_0 = input.LA(1);

                    if ( (LA70_0==LEFT_PAREN) && (synpred35_DRLExpressions())) {
                        alt70=1;
                    }
                    switch (alt70) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:27: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector2617);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector2638); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_selector2640);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector2642); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:403:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:404:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==LEFT_PAREN) ) {
                alt73=1;
            }
            else if ( (LA73_0==DOT) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:404:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix2659);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix2670); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix2672); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt72=2;
                    int LA72_0 = input.LA(1);

                    if ( (LA72_0==LEFT_PAREN) && (synpred37_DRLExpressions())) {
                        alt72=1;
                    }
                    switch (alt72) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix2681);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:408:1: squareArguments : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void squareArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:7: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments2704); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:19: ( expressionList )?
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==FLOAT||(LA74_0>=HEX && LA74_0<=DECIMAL)||LA74_0==STRING||(LA74_0>=BOOL && LA74_0<=NULL)||(LA74_0>=DECR && LA74_0<=INCR)||LA74_0==LESS||LA74_0==LEFT_PAREN||LA74_0==LEFT_SQUARE||(LA74_0>=NEGATION && LA74_0<=TILDE)||(LA74_0>=MINUS && LA74_0<=PLUS)||LA74_0==ID) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:19: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments2706);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments2709); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "squareArguments"


    // $ANTLR start "arguments"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:412:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments2726); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:18: ( expressionList )?
            int alt75=2;
            int LA75_0 = input.LA(1);

            if ( (LA75_0==FLOAT||(LA75_0>=HEX && LA75_0<=DECIMAL)||LA75_0==STRING||(LA75_0>=BOOL && LA75_0<=NULL)||(LA75_0>=DECR && LA75_0<=INCR)||LA75_0==LESS||LA75_0==LEFT_PAREN||LA75_0==LEFT_SQUARE||(LA75_0>=NEGATION && LA75_0<=TILDE)||(LA75_0>=MINUS && LA75_0<=PLUS)||LA75_0==ID) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:18: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments2728);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments2731); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:416:1: expressionList : expression ( COMMA expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:3: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:7: expression ( COMMA expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList2748);
            expression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:18: ( COMMA expression )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==COMMA) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:417:19: COMMA expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList2751); if (state.failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList2753);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop76;
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
    // $ANTLR end "expressionList"


    // $ANTLR start "assignmentOperator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:420:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt77=12;
            alt77 = dfa77.predict(input);
            switch (alt77) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:421:9: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2772); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:422:7: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator2780); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:423:7: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator2788); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:424:7: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator2796); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:425:7: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator2804); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:426:7: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator2812); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:427:7: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator2820); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:428:7: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator2828); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:429:7: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator2836); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:430:7: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2844); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2846); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2848); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:431:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2866); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2868); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2870); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2872); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2888); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2890); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2892); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:438:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:439:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:439:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key2924); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:442:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:443:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key2951); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:446:1: instanceof_key : {...}? =>id= ID ;
    public final DRLExpressions.instanceof_key_return instanceof_key() throws RecognitionException {
        DRLExpressions.instanceof_key_return retval = new DRLExpressions.instanceof_key_return();
        retval.start = input.LT(1);

        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:447:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:447:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key2978); if (state.failed) return retval;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:450:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:451:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:451:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key3006); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:454:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:455:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:455:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key3034); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:458:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:459:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:459:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key3062); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:462:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:463:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:463:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key3090); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:466:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:467:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:467:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key3118); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:470:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:471:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:471:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key3146); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:474:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:475:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:475:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key3174); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:478:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:479:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:479:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key3201); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:482:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:483:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:483:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key3229); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:486:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:487:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:487:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key3257); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:490:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:491:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:491:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key3284); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:494:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:495:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:495:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key3311); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:498:1: not_key : {...}? =>id= ID ;
    public final void not_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:499:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:499:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key3338); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:502:1: in_key : {...}? =>id= ID ;
    public final void in_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:503:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:503:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key3363); if (state.failed) return ;

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


    // $ANTLR start "operator_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:506:1: operator_key : {...}? =>id= ID ;
    public final void operator_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:507:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:507:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key3387); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "operator_key"


    // $ANTLR start "neg_operator_key"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:510:1: neg_operator_key : {...}? =>id= ID ;
    public final void neg_operator_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:511:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:511:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key3411); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "neg_operator_key"

    // $ANTLR start synpred1_DRLExpressions
    public final void synpred1_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:8: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRLExpressions240);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions251); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions253); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:13: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRLExpressions277);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:55: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRLExpressions291);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions303); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions305); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:10: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:11: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRLExpressions482);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:5: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:181:6: ( DOUBLE_PIPE | DOUBLE_AMPER ) operator
        {
        if ( (input.LA(1)>=DOUBLE_AMPER && input.LA(1)<=DOUBLE_PIPE) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_operator_in_synpred7_DRLExpressions830);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:5: ( relationalOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:6: relationalOp
        {
        pushFollow(FOLLOW_relationalOp_in_synpred8_DRLExpressions1086);
        relationalOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:33: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:243:34: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred9_DRLExpressions1207);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:22: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:23: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred10_DRLExpressions1226);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:7: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:8: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred11_DRLExpressions1274);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:11: ( PLUS | MINUS )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred13_DRLExpressions1567);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:10: ( selector )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:11: selector
        {
        pushFollow(FOLLOW_selector_in_synpred14_DRLExpressions1596);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:34: ( INCR | DECR )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1642); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred16_DRLExpressions1644);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1668); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred17_DRLExpressions1670);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred18_DRLExpressions1782);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1801);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:9: ( literal )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:304:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred20_DRLExpressions1826);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred21_DRLExpressions1846);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred22_DRLExpressions1864);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred23_DRLExpressions1882);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1914);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1930);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:9: ( ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred26_DRLExpressions1945); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:20: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred27_DRLExpressions1952); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred27_DRLExpressions1954); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1965);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions2128); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions2130); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions2152); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2358); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2360); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:9: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:396:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred32_DRLExpressions2556); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred32_DRLExpressions2558);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:9: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred33_DRLExpressions2576); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred33_DRLExpressions2578);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:9: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred34_DRLExpressions2601); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred34_DRLExpressions2603); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:27: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:28: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2612); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:9: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:400:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2635); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:18: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2676); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:431:7: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:431:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2858); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2860); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2862); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:7: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:432:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2882); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2884); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DRLExpressions

    // Delegated rules

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


    protected DFA13 dfa13 = new DFA13(this);
    protected DFA21 dfa21 = new DFA21(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA32 dfa32 = new DFA32(this);
    protected DFA34 dfa34 = new DFA34(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA48 dfa48 = new DFA48(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA77 dfa77 = new DFA77(this);
    static final String DFA13_eotS =
        "\16\uffff";
    static final String DFA13_eofS =
        "\1\14\15\uffff";
    static final String DFA13_minS =
        "\1\24\13\0\2\uffff";
    static final String DFA13_maxS =
        "\1\102\13\0\2\uffff";
    static final String DFA13_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA13_specialS =
        "\1\uffff\1\5\1\10\1\11\1\0\1\1\1\2\1\3\1\7\1\4\1\6\1\12\2\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\14\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\3\uffff\2\14\4\uffff"+
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

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "106:9: ( ( assignmentOperator )=>op= assignmentOperator left= expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_6 = input.LA(1);

                         
                        int index13_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_6);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_7 = input.LA(1);

                         
                        int index13_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_7);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_9 = input.LA(1);

                         
                        int index13_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_9);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA13_1 = input.LA(1);

                         
                        int index13_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_1);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA13_10 = input.LA(1);

                         
                        int index13_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_10);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA13_8 = input.LA(1);

                         
                        int index13_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_2);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA13_3 = input.LA(1);

                         
                        int index13_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_3);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA13_11 = input.LA(1);

                         
                        int index13_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_11);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 13, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA21_eotS =
        "\32\uffff";
    static final String DFA21_eofS =
        "\1\1\31\uffff";
    static final String DFA21_minS =
        "\1\24\4\uffff\2\0\23\uffff";
    static final String DFA21_maxS =
        "\1\102\4\uffff\2\0\23\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\2\27\uffff\1\1";
    static final String DFA21_specialS =
        "\5\uffff\1\0\1\1\23\uffff}>";
    static final String[] DFA21_transitionS = {
            "\11\1\3\uffff\2\1\4\uffff\3\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\2\1\1\uffff\1\5\1\6\1\1\2\uffff\3\1\11\uffff\1\1",
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
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "()* loopback of 181:3: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> (lop= DOUBLE_PIPE | lop= DOUBLE_AMPER ) op= operator right= shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_5 = input.LA(1);

                         
                        int index21_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 25;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index21_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA21_6 = input.LA(1);

                         
                        int index21_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 25;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index21_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 21, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA29_eotS =
        "\36\uffff";
    static final String DFA29_eofS =
        "\1\1\35\uffff";
    static final String DFA29_minS =
        "\1\24\1\uffff\1\0\21\uffff\2\0\10\uffff";
    static final String DFA29_maxS =
        "\1\102\1\uffff\1\0\21\uffff\2\0\10\uffff";
    static final String DFA29_acceptS =
        "\1\uffff\1\2\32\uffff\2\1";
    static final String DFA29_specialS =
        "\1\0\1\uffff\1\1\21\uffff\1\2\1\3\10\uffff}>";
    static final String[] DFA29_transitionS = {
            "\11\1\3\uffff\4\1\1\35\1\34\1\25\1\24\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\2\1\1\uffff\3\1\2\uffff\3\1\11\uffff\1\2",
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
            "\1\uffff",
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

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "()* loopback of 223:3: ( ( relationalOp )=>op= relationalOp right= shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_0 = input.LA(1);

                         
                        int index29_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_0==EOF||(LA29_0>=AT && LA29_0<=MOD_ASSIGN)||(LA29_0>=SEMICOLON && LA29_0<=NOT_EQUALS)||LA29_0==EQUALS_ASSIGN||LA29_0==RIGHT_PAREN||LA29_0==RIGHT_SQUARE||(LA29_0>=RIGHT_CURLY && LA29_0<=COMMA)||(LA29_0>=DOUBLE_AMPER && LA29_0<=QUESTION)||(LA29_0>=PIPE && LA29_0<=XOR)) ) {s = 1;}

                        else if ( (LA29_0==ID) ) {s = 2;}

                        else if ( (LA29_0==LESS) ) {s = 20;}

                        else if ( (LA29_0==GREATER) ) {s = 21;}

                        else if ( (LA29_0==LESS_EQUALS) && (synpred8_DRLExpressions())) {s = 28;}

                        else if ( (LA29_0==GREATER_EQUALS) && (synpred8_DRLExpressions())) {s = 29;}

                         
                        input.seek(index29_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_2 = input.LA(1);

                         
                        int index29_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred8_DRLExpressions()&&((helper.isPluggableEvaluator(false))))||(synpred8_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) ) {s = 29;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index29_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_20 = input.LA(1);

                         
                        int index29_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 29;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index29_20);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA29_21 = input.LA(1);

                         
                        int index29_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 29;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index29_21);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA31_eotS =
        "\23\uffff";
    static final String DFA31_eofS =
        "\1\2\22\uffff";
    static final String DFA31_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA31_maxS =
        "\1\102\1\0\21\uffff";
    static final String DFA31_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA31_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA31_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\11\uffff\2\2\10\uffff"+
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
            return "243:32: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_1 = input.LA(1);

                         
                        int index31_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index31_1);
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
    static final String DFA32_eotS =
        "\23\uffff";
    static final String DFA32_eofS =
        "\1\2\22\uffff";
    static final String DFA32_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA32_maxS =
        "\1\102\1\0\21\uffff";
    static final String DFA32_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA32_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA32_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\11\uffff\2\2\10\uffff"+
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

    static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
    static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
    static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
    static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
    static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
    static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }
        public String getDescription() {
            return "244:21: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA32_1 = input.LA(1);

                         
                        int index32_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index32_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 32, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA34_eotS =
        "\37\uffff";
    static final String DFA34_eofS =
        "\1\1\36\uffff";
    static final String DFA34_minS =
        "\1\24\20\uffff\2\0\14\uffff";
    static final String DFA34_maxS =
        "\1\102\20\uffff\2\0\14\uffff";
    static final String DFA34_acceptS =
        "\1\uffff\1\2\34\uffff\1\1";
    static final String DFA34_specialS =
        "\21\uffff\1\0\1\1\14\uffff}>";
    static final String[] DFA34_transitionS = {
            "\11\1\3\uffff\6\1\1\22\1\21\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\2\1\1\uffff\3\1\2\uffff\3\1\11\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
            ""
    };

    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;

    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }

    class DFA34 extends DFA {

        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "()* loopback of 250:5: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA34_17 = input.LA(1);

                         
                        int index34_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index34_17);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA34_18 = input.LA(1);

                         
                        int index34_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 30;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index34_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 34, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA41_eotS =
        "\16\uffff";
    static final String DFA41_eofS =
        "\16\uffff";
    static final String DFA41_minS =
        "\1\10\2\uffff\1\0\12\uffff";
    static final String DFA41_maxS =
        "\1\102\2\uffff\1\0\12\uffff";
    static final String DFA41_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\10\uffff\1\3";
    static final String DFA41_specialS =
        "\3\uffff\1\0\12\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\4\2\uffff\2\4\1\uffff\1\4\3\uffff\2\4\23\uffff\1\4\1\uffff"+
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
            return "277:1: unaryExpressionNotPlusMinus returns [BaseDescr result] : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | left= primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA41_3 = input.LA(1);

                         
                        int index41_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index41_3);
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
        "\12\uffff";
    static final String DFA43_eofS =
        "\12\uffff";
    static final String DFA43_minS =
        "\1\102\1\0\10\uffff";
    static final String DFA43_maxS =
        "\1\102\1\0\10\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA43_specialS =
        "\1\1\1\0\10\uffff}>";
    static final String[] DFA43_transitionS = {
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
            return "290:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
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
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {s = 2;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {s = 3;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {s = 4;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {s = 5;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {s = 6;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {s = 7;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {s = 8;}

                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {s = 9;}

                         
                        input.seek(index43_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA43_0 = input.LA(1);

                         
                        int index43_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA43_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index43_0);
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
    static final String DFA48_eotS =
        "\21\uffff";
    static final String DFA48_eofS =
        "\21\uffff";
    static final String DFA48_minS =
        "\1\10\10\uffff\2\0\6\uffff";
    static final String DFA48_maxS =
        "\1\102\10\uffff\2\0\6\uffff";
    static final String DFA48_acceptS =
        "\1\uffff\1\1\1\2\6\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA48_specialS =
        "\1\0\10\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA48_transitionS = {
            "\1\6\2\uffff\1\5\1\4\1\uffff\1\3\3\uffff\1\7\1\10\23\uffff\1"+
            "\2\1\uffff\1\1\1\uffff\1\12\26\uffff\1\11",
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

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        public String getDescription() {
            return "301:1: primary returns [BaseDescr result] : ( ( parExpression )=>expr= parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA48_0 = input.LA(1);

                         
                        int index48_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA48_0==LEFT_PAREN) && (synpred18_DRLExpressions())) {s = 1;}

                        else if ( (LA48_0==LESS) && (synpred19_DRLExpressions())) {s = 2;}

                        else if ( (LA48_0==STRING) && (synpred20_DRLExpressions())) {s = 3;}

                        else if ( (LA48_0==DECIMAL) && (synpred20_DRLExpressions())) {s = 4;}

                        else if ( (LA48_0==HEX) && (synpred20_DRLExpressions())) {s = 5;}

                        else if ( (LA48_0==FLOAT) && (synpred20_DRLExpressions())) {s = 6;}

                        else if ( (LA48_0==BOOL) && (synpred20_DRLExpressions())) {s = 7;}

                        else if ( (LA48_0==NULL) && (synpred20_DRLExpressions())) {s = 8;}

                        else if ( (LA48_0==ID) ) {s = 9;}

                        else if ( (LA48_0==LEFT_SQUARE) ) {s = 10;}

                         
                        input.seek(index48_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA48_9 = input.LA(1);

                         
                        int index48_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred21_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 11;}

                        else if ( ((synpred22_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 12;}

                        else if ( (((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))))) ) {s = 13;}

                        else if ( (synpred26_DRLExpressions()) ) {s = 14;}

                         
                        input.seek(index48_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA48_10 = input.LA(1);

                         
                        int index48_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred24_DRLExpressions()) ) {s = 15;}

                        else if ( (synpred25_DRLExpressions()) ) {s = 16;}

                         
                        input.seek(index48_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 48, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA47_eotS =
        "\44\uffff";
    static final String DFA47_eofS =
        "\1\3\43\uffff";
    static final String DFA47_minS =
        "\1\24\1\0\42\uffff";
    static final String DFA47_maxS =
        "\1\103\1\0\42\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\1\1\1\2\40\uffff";
    static final String DFA47_specialS =
        "\1\0\1\1\42\uffff}>";
    static final String[] DFA47_transitionS = {
            "\13\3\1\uffff\11\3\1\2\1\3\1\1\1\3\1\uffff\6\3\2\uffff\7\3\5"+
            "\uffff\2\3",
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
            ""
    };

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        public String getDescription() {
            return "312:38: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_0 = input.LA(1);

                         
                        int index47_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA47_0==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA47_0==LEFT_PAREN) && (synpred28_DRLExpressions())) {s = 2;}

                        else if ( (LA47_0==EOF||(LA47_0>=AT && LA47_0<=INCR)||(LA47_0>=SEMICOLON && LA47_0<=EQUALS_ASSIGN)||LA47_0==RIGHT_PAREN||LA47_0==RIGHT_SQUARE||(LA47_0>=RIGHT_CURLY && LA47_0<=QUESTION)||(LA47_0>=PIPE && LA47_0<=PLUS)||(LA47_0>=ID && LA47_0<=DIV)) ) {s = 3;}

                         
                        input.seek(index47_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA47_1 = input.LA(1);

                         
                        int index47_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_DRLExpressions()) ) {s = 2;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index47_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA52_eotS =
        "\44\uffff";
    static final String DFA52_eofS =
        "\1\1\43\uffff";
    static final String DFA52_minS =
        "\1\24\40\uffff\1\0\2\uffff";
    static final String DFA52_maxS =
        "\1\103\40\uffff\1\0\2\uffff";
    static final String DFA52_acceptS =
        "\1\uffff\1\2\41\uffff\1\1";
    static final String DFA52_specialS =
        "\41\uffff\1\0\2\uffff}>";
    static final String[] DFA52_transitionS = {
            "\13\1\1\uffff\11\1\1\uffff\1\1\1\41\1\1\1\uffff\6\1\2\uffff"+
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
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA52_eot = DFA.unpackEncodedString(DFA52_eotS);
    static final short[] DFA52_eof = DFA.unpackEncodedString(DFA52_eofS);
    static final char[] DFA52_min = DFA.unpackEncodedStringToUnsignedChars(DFA52_minS);
    static final char[] DFA52_max = DFA.unpackEncodedStringToUnsignedChars(DFA52_maxS);
    static final short[] DFA52_accept = DFA.unpackEncodedString(DFA52_acceptS);
    static final short[] DFA52_special = DFA.unpackEncodedString(DFA52_specialS);
    static final short[][] DFA52_transition;

    static {
        int numStates = DFA52_transitionS.length;
        DFA52_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA52_transition[i] = DFA.unpackEncodedString(DFA52_transitionS[i]);
        }
    }

    class DFA52 extends DFA {

        public DFA52(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 52;
            this.eot = DFA52_eot;
            this.eof = DFA52_eof;
            this.min = DFA52_min;
            this.max = DFA52_max;
            this.accept = DFA52_accept;
            this.special = DFA52_special;
            this.transition = DFA52_transition;
        }
        public String getDescription() {
            return "()+ loopback of 338:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_33 = input.LA(1);

                         
                        int index52_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred30_DRLExpressions()) ) {s = 35;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index52_33);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 52, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA61_eotS =
        "\44\uffff";
    static final String DFA61_eofS =
        "\1\2\43\uffff";
    static final String DFA61_minS =
        "\1\24\1\0\42\uffff";
    static final String DFA61_maxS =
        "\1\103\1\0\42\uffff";
    static final String DFA61_acceptS =
        "\2\uffff\1\2\40\uffff\1\1";
    static final String DFA61_specialS =
        "\1\uffff\1\0\42\uffff}>";
    static final String[] DFA61_transitionS = {
            "\13\2\1\uffff\11\2\1\uffff\1\2\1\1\1\2\1\uffff\6\2\2\uffff\7"+
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
            return "()* loopback of 365:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA61_1 = input.LA(1);

                         
                        int index61_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 35;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index61_1);
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
    static final String DFA77_eotS =
        "\17\uffff";
    static final String DFA77_eofS =
        "\17\uffff";
    static final String DFA77_minS =
        "\1\25\12\uffff\2\46\2\uffff";
    static final String DFA77_maxS =
        "\1\50\12\uffff\1\46\1\50\2\uffff";
    static final String DFA77_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA77_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA77_transitionS = {
            "\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\11\uffff\1\13\1\12\1\1",
            "",
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

    static final short[] DFA77_eot = DFA.unpackEncodedString(DFA77_eotS);
    static final short[] DFA77_eof = DFA.unpackEncodedString(DFA77_eofS);
    static final char[] DFA77_min = DFA.unpackEncodedStringToUnsignedChars(DFA77_minS);
    static final char[] DFA77_max = DFA.unpackEncodedStringToUnsignedChars(DFA77_maxS);
    static final short[] DFA77_accept = DFA.unpackEncodedString(DFA77_acceptS);
    static final short[] DFA77_special = DFA.unpackEncodedString(DFA77_specialS);
    static final short[][] DFA77_transition;

    static {
        int numStates = DFA77_transitionS.length;
        DFA77_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA77_transition[i] = DFA.unpackEncodedString(DFA77_transitionS[i]);
        }
    }

    class DFA77 extends DFA {

        public DFA77(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 77;
            this.eot = DFA77_eot;
            this.eof = DFA77_eof;
            this.min = DFA77_min;
            this.max = DFA77_max;
            this.accept = DFA77_accept;
            this.special = DFA77_special;
            this.transition = DFA77_transition;
        }
        public String getDescription() {
            return "420:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA77_12 = input.LA(1);

                         
                        int index77_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA77_12==GREATER) && (synpred38_DRLExpressions())) {s = 13;}

                        else if ( (LA77_12==EQUALS_ASSIGN) && (synpred39_DRLExpressions())) {s = 14;}

                         
                        input.seek(index77_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 77, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList189 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList192 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeList194 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_typeMatch_in_type216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_typeMatch247 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch257 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch259 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ID_in_typeMatch273 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch280 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_DOT_in_typeMatch285 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_typeMatch287 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_typeMatch294 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_typeMatch309 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_typeMatch311 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments336 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments338 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments341 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments343 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument372 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument376 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_typeArgument380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeArgument383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy407 = new BitSet(new long[]{0x0000000100100000L,0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_dummy409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_dummy2443 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_dummy2445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression468 = new BitSet(new long[]{0x000001C01FE00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression489 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression520 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression534 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression538 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression540 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression569 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression578 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression582 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression619 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression627 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression631 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression667 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression675 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression679 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression715 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression723 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression727 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression763 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression771 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression775 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andOrRestriction815 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_andOrRestriction836 = new BitSet(new long[]{0x000000FC00000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_andOrRestriction840 = new BitSet(new long[]{0x000000FC00000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_operator_in_andOrRestriction845 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_andOrRestriction849 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_EOF_in_andOrRestriction868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression892 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_EQUALS_in_equalityExpression904 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_equalityExpression910 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression916 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression952 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression1000 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_not_key_in_inExpression1010 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_in_key_in_inExpression1014 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1016 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression1018 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1021 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression1023 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_key_in_inExpression1037 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression1039 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression1041 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression1044 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression1046 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression1050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1077 = new BitSet(new long[]{0x000000FC00000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression1092 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression1096 = new BitSet(new long[]{0x000000FC00000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_EQUALS_in_operator1128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator1144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp1167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp1193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp1201 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp1203 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp1221 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp1230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1263 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression1277 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression1279 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1299 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1314 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1316 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1331 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1361 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression1382 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1390 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1418 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression1430 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1444 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1470 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1480 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression1492 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression1504 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1543 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1554 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1582 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus1599 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1649 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression1651 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1653 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1675 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_castExpression1677 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1679 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType1712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType1720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType1728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType1736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType1744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType1752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType1760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary1788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1805 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary1812 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_primary1814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary1850 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1852 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary1868 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_creator_in_primary1870 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1886 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1889 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1891 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_primary1895 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_primary1897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary1934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1948 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_DOT_in_primary1957 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_primary1959 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1990 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1992 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression2017 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression2019 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression2021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList2039 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList2042 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList2044 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry2067 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry2069 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mapEntry2071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression2093 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_parExpression2097 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix2134 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix2136 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix2140 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix2142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix2157 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix2159 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix2161 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix2174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator2197 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_createdName_in_creator2200 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator2211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator2215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName2233 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName2235 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName2248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_createdName2250 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName2252 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName2267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator2287 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator2289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2308 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2318 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2321 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2323 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest2327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2341 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2343 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2348 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest2350 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2352 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest2364 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest2366 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer2395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer2409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer2426 = new BitSet(new long[]{0x18306A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2429 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2432 = new BitSet(new long[]{0x18302A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer2434 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer2439 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer2446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest2463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation2481 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation2483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments2500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments2502 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments2504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix2521 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix2534 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix2536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2561 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_selector2563 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector2565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2581 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_selector2583 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector2586 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_innerCreator_in_selector2590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2606 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_selector2608 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_selector2617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector2638 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_selector2640 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector2642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix2670 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_superSuffix2672 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments2704 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments2706 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments2709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments2726 = new BitSet(new long[]{0x18300E80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_arguments2728 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments2731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2748 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList2751 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expressionList2753 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator2780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator2788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator2796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator2804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2844 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2846 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2866 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2868 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2870 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2888 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2890 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key2924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key2951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key2978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key3006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key3034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key3062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key3090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key3118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key3146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key3174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key3201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key3229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key3257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key3284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key3311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key3338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key3363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key3387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key3411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions251 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions303 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred7_DRLExpressions824 = new BitSet(new long[]{0x000000FC00000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_operator_in_synpred7_DRLExpressions830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred8_DRLExpressions1086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred9_DRLExpressions1207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred10_DRLExpressions1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred11_DRLExpressions1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred12_DRLExpressions1375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred13_DRLExpressions1567 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred14_DRLExpressions1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred15_DRLExpressions1604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1642 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_synpred16_DRLExpressions1644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1668 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_synpred17_DRLExpressions1670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred18_DRLExpressions1782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1801 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred20_DRLExpressions1826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred21_DRLExpressions1846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred22_DRLExpressions1864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred23_DRLExpressions1882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred26_DRLExpressions1945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred27_DRLExpressions1952 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRLExpressions1954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions2128 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions2130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions2152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions2358 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions2360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred32_DRLExpressions2556 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_synpred32_DRLExpressions2558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred33_DRLExpressions2576 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_synpred33_DRLExpressions2578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred34_DRLExpressions2601 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_synpred34_DRLExpressions2603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2858 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2860 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2882 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2884 = new BitSet(new long[]{0x0000000000000002L});

}