// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-01-21 13:58:16

	package org.drools.lang;
	
	import java.util.LinkedList;
	import org.drools.compiler.DroolsParserException;
	import org.drools.lang.ParserHelper;


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
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g"; }


        private ParserXHelper helper = null;
                                                        
        public DRLExpressions(TokenStream input,
                              RecognizerSharedState state,
                              ParserXHelper helper ) {
            this( input,
                  state );
            this.helper = helper;
        }

        public ParserXHelper getHelper()                          { return helper; }
        public boolean hasErrors()                                { return helper.hasErrors(); }
        public List<DroolsParserException> getErrors()            { return helper.getErrors(); }
        public List<String> getErrorMessages()                    { return helper.getErrorMessages(); }
        public void enableEditorInterface()                       {        helper.enableEditorInterface(); }
        public void disableEditorInterface()                      {        helper.disableEditorInterface(); }
        public LinkedList<DroolsSentence> getEditorInterface()    { return helper.getEditorInterface(); }
        public void reportError(RecognitionException ex)          {        helper.reportError( ex ); }
        public void emitErrorMessage(String msg)                  {}




    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:50:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL );
    public final void literal() throws RecognitionException {
        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:51:2: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL )
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:51:4: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal74); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:52:4: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal97); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:53:4: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal106); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:54:4: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal119); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:55:4: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal130); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:56:4: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal155); if (state.failed) return ;
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:59:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:2: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:4: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList186);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:9: ( COMMA type )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:10: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList189); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList191);
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


    // $ANTLR start "type"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:63:1: type : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void type() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:2: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                int LA8_1 = input.LA(2);

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))))) ) {
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:5: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:24: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:26: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type214);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:40: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==LEFT_SQUARE) ) {
                            int LA3_2 = input.LA(2);

                            if ( (LA3_2==RIGHT_SQUARE) ) {
                                int LA3_3 = input.LA(3);

                                if ( (synpred2_DRLExpressions()) ) {
                                    alt3=1;
                                }


                            }


                        }


                        switch (alt3) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:41: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type224); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type226); if (state.failed) return ;

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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:4: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:4: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:6: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_type237); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:9: ( ( typeArguments )=> typeArguments )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:10: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type244);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:43: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==DOT) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:44: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_type249); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_type251); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:51: ( ( typeArguments )=> typeArguments )?
                    	    int alt5=2;
                    	    alt5 = dfa5.predict(input);
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:52: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_type258);
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

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:88: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==LEFT_SQUARE) ) {
                            int LA7_2 = input.LA(2);

                            if ( (LA7_2==RIGHT_SQUARE) ) {
                                int LA7_3 = input.LA(3);

                                if ( (synpred5_DRLExpressions()) ) {
                                    alt7=1;
                                }


                            }


                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:89: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type273); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type275); if (state.failed) return ;

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
    // $ANTLR end "type"


    // $ANTLR start "typeArguments"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:68:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:2: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:4: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments290); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments292);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:22: ( COMMA typeArgument )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:23: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments295); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments297);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments301); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:72:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:73:2: ( type | QUESTION ( ( extends_key | super_key ) type )? )
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:73:4: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument313);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:4: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument318); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:13: ( ( extends_key | super_key ) type )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:14: ( extends_key | super_key ) type
                            {
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:14: ( extends_key | super_key )
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
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:15: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument322);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:29: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument326);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument329);
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


    // $ANTLR start "embeddedExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:1: embeddedExpression : conditionalOrExpression ( ( AT )=> dummy )? ;
    public final void embeddedExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:2: ( conditionalOrExpression ( ( AT )=> dummy )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:4: conditionalOrExpression ( ( AT )=> dummy )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_embeddedExpression345);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:28: ( ( AT )=> dummy )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==EOF) ) {
                int LA13_1 = input.LA(2);

                if ( (synpred6_DRLExpressions()) ) {
                    alt13=1;
                }
            }
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:29: ( AT )=> dummy
                    {
                    pushFollow(FOLLOW_dummy_in_embeddedExpression352);
                    dummy();

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
    // $ANTLR end "embeddedExpression"


    // $ANTLR start "dummy"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:1: dummy : ;
    public final void dummy() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:7: ()
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:84:9: 
            {
            }

        }
        finally {
        }
        return ;
    }
    // $ANTLR end "dummy"


    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:86:1: expression : conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? ;
    public final void expression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:2: ( conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:4: conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression374);
            conditionalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:26: ( ( assignmentOperator )=> assignmentOperator expression )?
            int alt14=2;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:27: ( assignmentOperator )=> assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression383);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_expression385);
                    expression();

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
    // $ANTLR end "expression"


    // $ANTLR start "conditionalExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:90:1: conditionalExpression : conditionalOrExpression ( QUESTION expression COLON expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:91:9: ( conditionalOrExpression ( QUESTION expression COLON expression )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:91:17: conditionalOrExpression ( QUESTION expression COLON expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression411);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:91:41: ( QUESTION expression COLON expression )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==QUESTION) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:91:43: QUESTION expression COLON expression
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_conditionalExpression415); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression417);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression419); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression421);
                    expression();

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
    // $ANTLR end "conditionalExpression"


    // $ANTLR start "conditionalOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:93:1: conditionalOrExpression : conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:5: ( conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:9: conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression439);
            conditionalAndExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:34: ( DOUBLE_PIPE conditionalAndExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==DOUBLE_PIPE) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:36: DOUBLE_PIPE conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression443); if (state.failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression445);
            	    conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:97:1: conditionalAndExpression : inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:5: ( inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:9: inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression464);
            inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:31: ( DOUBLE_AMPER inclusiveOrExpression )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==DOUBLE_AMPER) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:33: DOUBLE_AMPER inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression468); if (state.failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression470);
            	    inclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:1: inclusiveOrExpression : exclusiveOrExpression ( PIPE exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:5: ( exclusiveOrExpression ( PIPE exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:9: exclusiveOrExpression ( PIPE exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression489);
            exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:31: ( PIPE exclusiveOrExpression )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==PIPE) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:33: PIPE exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression493); if (state.failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression495);
            	    exclusiveOrExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:1: exclusiveOrExpression : andExpression ( XOR andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:5: ( andExpression ( XOR andExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:9: andExpression ( XOR andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression514);
            andExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:23: ( XOR andExpression )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==XOR) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:25: XOR andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression518); if (state.failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression520);
            	    andExpression();

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
    // $ANTLR end "exclusiveOrExpression"


    // $ANTLR start "andExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:109:1: andExpression : equalityExpression ( AMPER equalityExpression )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:5: ( equalityExpression ( AMPER equalityExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:9: equalityExpression ( AMPER equalityExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andExpression539);
            equalityExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:28: ( AMPER equalityExpression )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==AMPER) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:30: AMPER equalityExpression
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression543); if (state.failed) return ;
            	    pushFollow(FOLLOW_equalityExpression_in_andExpression545);
            	    equalityExpression();

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
    // $ANTLR end "andExpression"


    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:113:1: equalityExpression : instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:5: ( instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:9: instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression564);
            instanceOfExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:30: ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=EQUALS && LA21_0<=NOT_EQUALS)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:32: ( EQUALS | NOT_EQUALS ) instanceOfExpression
            	    {
            	    if ( (input.LA(1)>=EQUALS && input.LA(1)<=NOT_EQUALS) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression578);
            	    instanceOfExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "equalityExpression"


    // $ANTLR start "instanceOfExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:117:1: instanceOfExpression : relationalExpression ( instanceof_key type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:5: ( relationalExpression ( instanceof_key type )? )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:9: relationalExpression ( instanceof_key type )?
            {
            pushFollow(FOLLOW_relationalExpression_in_instanceOfExpression597);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:30: ( instanceof_key type )?
            int alt22=2;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:31: instanceof_key type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression600);
                    instanceof_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression602);
                    type();

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
    // $ANTLR end "instanceOfExpression"


    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:121:1: relationalExpression : shiftExpression ( ( LESS )=> relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:5: ( shiftExpression ( ( LESS )=> relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:9: shiftExpression ( ( LESS )=> relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression620);
            shiftExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:25: ( ( LESS )=> relationalOp shiftExpression )*
            loop23:
            do {
                int alt23=2;
                alt23 = dfa23.predict(input);
                switch (alt23) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:27: ( LESS )=> relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression629);
            	    relationalOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression631);
            	    shiftExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "relationalExpression"


    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:125:1: relationalOp : ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER ) ;
    public final void relationalOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:2: ( ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:4: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER )
            {
            if ( (input.LA(1)>=GREATER_EQUALS && input.LA(1)<=LESS) ) {
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
    // $ANTLR end "relationalOp"


    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:129:1: shiftExpression : additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:5: ( additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:9: additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression678);
            additiveExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:28: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop24:
            do {
                int alt24=2;
                alt24 = dfa24.predict(input);
                switch (alt24) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:30: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression686);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression688);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop24;
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
    // $ANTLR end "shiftExpression"


    // $ANTLR start "shiftOp"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:2: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:4: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:4: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt25=3;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==LESS) ) {
                alt25=1;
            }
            else if ( (LA25_0==GREATER) ) {
                int LA25_2 = input.LA(2);

                if ( (LA25_2==GREATER) ) {
                    int LA25_3 = input.LA(3);

                    if ( (LA25_3==GREATER) ) {
                        alt25=2;
                    }
                    else if ( (LA25_3==EOF||LA25_3==FLOAT||(LA25_3>=HEX && LA25_3<=DECIMAL)||LA25_3==STRING||(LA25_3>=BOOL && LA25_3<=NULL)||(LA25_3>=DECR && LA25_3<=INCR)||LA25_3==LESS||LA25_3==LEFT_PAREN||LA25_3==LEFT_SQUARE||(LA25_3>=NEGATION && LA25_3<=TILDE)||(LA25_3>=MINUS && LA25_3<=PLUS)||LA25_3==ID) ) {
                        alt25=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 25, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:5: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp703); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp705); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:17: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp709); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp711); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp713); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:43: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp717); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp719); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:137:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:5: ( multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:9: multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression737);
            multiplicativeExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:34: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop26:
            do {
                int alt26=2;
                alt26 = dfa26.predict(input);
                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:36: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression756);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:1: multiplicativeExpression : unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:5: ( unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:9: unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression775);
            unaryExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:25: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>=MOD && LA27_0<=STAR)||LA27_0==DIV) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:27: ( STAR | DIV | MOD ) unaryExpression
            	    {
            	    if ( (input.LA(1)>=MOD && input.LA(1)<=STAR)||input.LA(1)==DIV ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression793);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "multiplicativeExpression"


    // $ANTLR start "unaryExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:145:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus )
            int alt28=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt28=1;
                }
                break;
            case MINUS:
                {
                alt28=2;
                }
                break;
            case INCR:
                {
                alt28=3;
                }
                break;
            case DECR:
                {
                alt28=4;
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
                alt28=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:146:9: PLUS unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression813); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression815);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:147:7: MINUS unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression823); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression825);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression835); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression837);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression847); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression849);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:150:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression859);
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
    // $ANTLR end "unaryExpression"


    // $ANTLR start "unaryExpressionNotPlusMinus"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ({...}? => selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ({...}? => selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt31=4;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus878); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus880);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:155:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus889); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus891);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus905);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:9: primary ({...}? => selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus915);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:17: ({...}? => selector )*
                    loop29:
                    do {
                        int alt29=2;
                        alt29 = dfa29.predict(input);
                        switch (alt29) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:18: {...}? => selector
                    	    {
                    	    if ( !((helper.validateLT(1,".")||helper.validateLT(1,"["))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "unaryExpressionNotPlusMinus", "helper.validateLT(1,\".\")||helper.validateLT(1,\"[\")");
                    	    }
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus920);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop29;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:84: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt30=2;
                    alt30 = dfa30.predict(input);
                    switch (alt30) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:85: ( INCR | DECR )=> ( INCR | DECR )
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
    // $ANTLR end "unaryExpressionNotPlusMinus"


    // $ANTLR start "castExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:160:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus | LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus | LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt32=3;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LEFT_PAREN) ) {
                int LA32_1 = input.LA(2);

                if ( (synpred13_DRLExpressions()) ) {
                    alt32=1;
                }
                else if ( (synpred14_DRLExpressions()) ) {
                    alt32=2;
                }
                else if ( (true) ) {
                    alt32=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression968); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression970);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression972); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression974);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression991); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression993);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression995); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression997);
                    unaryExpressionNotPlusMinus();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:8: LEFT_PAREN expression RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1006); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_castExpression1008);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1010); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1012);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt33=8;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:9: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType1035);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:168:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType1043);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:169:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType1051);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType1059);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType1067);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:172:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType1075);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:173:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType1083);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType1091);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final void primary() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:5: ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt38=9;
            alt38 = dfa38.predict(input);
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:7: ( parExpression )=> parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary1113);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary1128);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==ID) ) {
                        int LA34_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt34=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt34=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 34, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 34, 0, input);

                        throw nvae;
                    }
                    switch (alt34) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary1131);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary1135);
                            this_key();

                            state._fsp--;
                            if (state.failed) return ;
                            pushFollow(FOLLOW_arguments_in_primary1137);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary1153);
                    literal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary1173);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary1175);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary1190);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_creator_in_primary1192);
                    creator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary1207);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);

                        if ( (LA35_0==LEFT_SQUARE) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary1210); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary1212); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop35;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary1216); if (state.failed) return ;
                    pushFollow(FOLLOW_class_key_in_primary1218);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary1238);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:187:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary1253);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    match(input,ID,FOLLOW_ID_in_primary1267); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:18: ( ( DOT ID )=> DOT ID )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {
                            int LA36_2 = input.LA(2);

                            if ( (LA36_2==ID) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {
                                int LA36_3 = input.LA(3);

                                if ( (synpred24_DRLExpressions()) ) {
                                    alt36=1;
                                }


                            }


                        }


                        switch (alt36) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:19: ( DOT ID )=> DOT ID
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary1276); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_primary1278); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop36;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt37=2;
                    alt37 = dfa37.predict(input);
                    switch (alt37) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:39: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary1287);
                            identifierSuffix();

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
    // $ANTLR end "primary"


    // $ANTLR start "inlineListExpression"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression1308); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:21: ( expressionList )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==FLOAT||(LA39_0>=HEX && LA39_0<=DECIMAL)||LA39_0==STRING||(LA39_0>=BOOL && LA39_0<=NULL)||(LA39_0>=DECR && LA39_0<=INCR)||LA39_0==LESS||LA39_0==LEFT_PAREN||LA39_0==LEFT_SQUARE||(LA39_0>=NEGATION && LA39_0<=TILDE)||(LA39_0>=MINUS && LA39_0<=PLUS)||LA39_0==ID) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:192:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression1310);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression1313); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:1: inlineMapExpression : LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:5: ( LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:7: LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression1335); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:19: ( mapExpressionList )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==FLOAT||(LA40_0>=HEX && LA40_0<=DECIMAL)||LA40_0==STRING||(LA40_0>=BOOL && LA40_0<=NULL)||(LA40_0>=DECR && LA40_0<=INCR)||LA40_0==LESS||LA40_0==LEFT_PAREN||LA40_0==LEFT_SQUARE||(LA40_0>=NEGATION && LA40_0<=TILDE)||(LA40_0>=MINUS && LA40_0<=PLUS)||LA40_0==ID) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:19: mapExpressionList
            	    {
            	    pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression1337);
            	    mapExpressionList();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt40 >= 1 ) break loop40;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1340); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList1357);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:16: ( COMMA mapEntry )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList1360); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList1362);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop41;
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:203:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:204:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry1385);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry1387); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry1389);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:1: parExpression : LEFT_PAREN expression RIGHT_PAREN ;
    public final void parExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:2: ( LEFT_PAREN expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:4: LEFT_PAREN expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression1403); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_parExpression1405);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression1407); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "parExpression"


    // $ANTLR start "identifierSuffix"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt44=3;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==LEFT_SQUARE) ) {
                int LA44_1 = input.LA(2);

                if ( (LA44_1==RIGHT_SQUARE) && (synpred26_DRLExpressions())) {
                    alt44=1;
                }
                else if ( (LA44_1==FLOAT||(LA44_1>=HEX && LA44_1<=DECIMAL)||LA44_1==STRING||(LA44_1>=BOOL && LA44_1<=NULL)||(LA44_1>=DECR && LA44_1<=INCR)||LA44_1==LESS||LA44_1==LEFT_PAREN||LA44_1==LEFT_SQUARE||(LA44_1>=NEGATION && LA44_1<=TILDE)||(LA44_1>=MINUS && LA44_1<=PLUS)||LA44_1==ID) ) {
                    alt44=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA44_0==LEFT_PAREN) ) {
                alt44=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt42=0;
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0==LEFT_SQUARE) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1429); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1431); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt42 >= 1 ) break loop42;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(42, input);
                                throw eee;
                        }
                        cnt42++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1435); if (state.failed) return ;
                    pushFollow(FOLLOW_class_key_in_identifierSuffix1437);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt43=0;
                    loop43:
                    do {
                        int alt43=2;
                        alt43 = dfa43.predict(input);
                        switch (alt43) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1452); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix1454);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1456); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt43 >= 1 ) break loop43;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(43, input);
                                throw eee;
                        }
                        cnt43++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix1469);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:222:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:2: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:4: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:4: ( nonWildcardTypeArguments )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==LESS) ) {
                alt45=1;
            }
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:4: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator1487);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator1490);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:9: ( arrayCreatorRest | classCreatorRest )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==LEFT_SQUARE) ) {
                alt46=1;
            }
            else if ( (LA46_0==LEFT_PAREN) ) {
                alt46=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator1501);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator1505);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:2: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA50_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt50=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt50=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:4: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName1517); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:7: ( typeArguments )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==LESS) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:7: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName1519);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:9: ( DOT ID ( typeArguments )? )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==DOT) ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName1532); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName1534); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:18: ( typeArguments )?
                    	    int alt48=2;
                    	    int LA48_0 = input.LA(1);

                    	    if ( (LA48_0==LESS) ) {
                    	        alt48=1;
                    	    }
                    	    switch (alt48) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:229:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName1536);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop49;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:230:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName1551);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:233:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:2: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:234:4: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator1566); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator1568);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:237:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:2: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:238:6: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1581); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:2: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==RIGHT_SQUARE) ) {
                alt54=1;
            }
            else if ( (LA54_0==FLOAT||(LA54_0>=HEX && LA54_0<=DECIMAL)||LA54_0==STRING||(LA54_0>=BOOL && LA54_0<=NULL)||(LA54_0>=DECR && LA54_0<=INCR)||LA54_0==LESS||LA54_0==LEFT_PAREN||LA54_0==LEFT_SQUARE||(LA54_0>=NEGATION && LA54_0<=TILDE)||(LA54_0>=MINUS && LA54_0<=PLUS)||LA54_0==ID) ) {
                alt54=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:6: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1589); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:19: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==LEFT_SQUARE) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:20: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1592); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1594); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop51;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest1598);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest1612);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1614); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop52:
                    do {
                        int alt52=2;
                        alt52 = dfa52.predict(input);
                        switch (alt52) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1619); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest1621);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1623); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop52;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop53:
                    do {
                        int alt53=2;
                        int LA53_0 = input.LA(1);

                        if ( (LA53_0==LEFT_SQUARE) ) {
                            int LA53_2 = input.LA(2);

                            if ( (LA53_2==RIGHT_SQUARE) ) {
                                int LA53_4 = input.LA(3);

                                if ( (synpred28_DRLExpressions()) ) {
                                    alt53=1;
                                }


                            }


                        }


                        switch (alt53) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1635); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1637); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop53;
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:244:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:245:2: ( arrayInitializer | expression )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==LEFT_CURLY) ) {
                alt55=1;
            }
            else if ( (LA55_0==FLOAT||(LA55_0>=HEX && LA55_0<=DECIMAL)||LA55_0==STRING||(LA55_0>=BOOL && LA55_0<=NULL)||(LA55_0>=DECR && LA55_0<=INCR)||LA55_0==LESS||LA55_0==LEFT_PAREN||LA55_0==LEFT_SQUARE||(LA55_0>=NEGATION && LA55_0<=TILDE)||(LA55_0>=MINUS && LA55_0<=PLUS)||LA55_0==ID) ) {
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:245:4: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1660);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:246:10: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1671);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:249:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:2: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:4: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer1683); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:15: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==FLOAT||(LA58_0>=HEX && LA58_0<=DECIMAL)||LA58_0==STRING||(LA58_0>=BOOL && LA58_0<=NULL)||(LA58_0>=DECR && LA58_0<=INCR)||LA58_0==LESS||LA58_0==LEFT_PAREN||LA58_0==LEFT_SQUARE||LA58_0==LEFT_CURLY||(LA58_0>=NEGATION && LA58_0<=TILDE)||(LA58_0>=MINUS && LA58_0<=PLUS)||LA58_0==ID) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:16: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1686);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:36: ( COMMA variableInitializer )*
                    loop56:
                    do {
                        int alt56=2;
                        int LA56_0 = input.LA(1);

                        if ( (LA56_0==COMMA) ) {
                            int LA56_1 = input.LA(2);

                            if ( (LA56_1==FLOAT||(LA56_1>=HEX && LA56_1<=DECIMAL)||LA56_1==STRING||(LA56_1>=BOOL && LA56_1<=NULL)||(LA56_1>=DECR && LA56_1<=INCR)||LA56_1==LESS||LA56_1==LEFT_PAREN||LA56_1==LEFT_SQUARE||LA56_1==LEFT_CURLY||(LA56_1>=NEGATION && LA56_1<=TILDE)||(LA56_1>=MINUS && LA56_1<=PLUS)||LA56_1==ID) ) {
                                alt56=1;
                            }


                        }


                        switch (alt56) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:37: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer1689); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1691);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop56;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:65: ( COMMA )?
                    int alt57=2;
                    int LA57_0 = input.LA(1);

                    if ( (LA57_0==COMMA) ) {
                        alt57=1;
                    }
                    switch (alt57) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:66: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer1696); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer1703); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:253:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:2: ( arguments )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:254:4: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest1714);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:257:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:258:2: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:258:4: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1727);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation1729);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:2: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:4: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments1741); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments1743);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments1745); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:2: ( super_key superSuffix | ID arguments )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==ID) ) {
                int LA59_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt59=1;
                }
                else if ( (true) ) {
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:4: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix1757);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix1759);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:7: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix1767); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix1769);
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:270:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | DOT ID ( ( LEFT_PAREN )=> arguments )? | LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:2: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | DOT ID ( ( LEFT_PAREN )=> arguments )? | LEFT_SQUARE expression RIGHT_SQUARE )
            int alt62=4;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==DOT) ) {
                int LA62_1 = input.LA(2);

                if ( (synpred29_DRLExpressions()) ) {
                    alt62=1;
                }
                else if ( (synpred30_DRLExpressions()) ) {
                    alt62=2;
                }
                else if ( (true) ) {
                    alt62=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA62_0==LEFT_SQUARE) ) {
                alt62=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:6: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1788); if (state.failed) return ;
                    pushFollow(FOLLOW_super_key_in_selector1790);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector1792);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:6: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1805); if (state.failed) return ;
                    pushFollow(FOLLOW_new_key_in_selector1807);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:33: ( nonWildcardTypeArguments )?
                    int alt60=2;
                    int LA60_0 = input.LA(1);

                    if ( (LA60_0==LESS) ) {
                        alt60=1;
                    }
                    switch (alt60) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:34: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector1810);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector1814);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:6: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector1821); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_selector1823); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:13: ( ( LEFT_PAREN )=> arguments )?
                    int alt61=2;
                    alt61 = dfa61.predict(input);
                    switch (alt61) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:14: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector1832);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:275:6: LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector1843); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_selector1845);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector1847); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:2: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFT_PAREN) ) {
                alt64=1;
            }
            else if ( (LA64_0==DOT) ) {
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
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:279:4: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix1859);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:7: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix1867); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix1869); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:14: ( ( LEFT_PAREN )=> arguments )?
                    int alt63=2;
                    alt63 = dfa63.predict(input);
                    switch (alt63) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:15: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix1878);
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


    // $ANTLR start "arguments"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:283:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:2: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:4: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments1898); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:15: ( expressionList )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==FLOAT||(LA65_0>=HEX && LA65_0<=DECIMAL)||LA65_0==STRING||(LA65_0>=BOOL && LA65_0<=NULL)||(LA65_0>=DECR && LA65_0<=INCR)||LA65_0==LESS||LA65_0==LEFT_PAREN||LA65_0==LEFT_SQUARE||(LA65_0>=NEGATION && LA65_0<=TILDE)||(LA65_0>=MINUS && LA65_0<=PLUS)||LA65_0==ID) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:284:15: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments1900);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments1903); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:287:1: expressionList : expression ( COMMA expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:5: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:9: expression ( COMMA expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList1919);
            expression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:20: ( COMMA expression )*
            loop66:
            do {
                int alt66=2;
                int LA66_0 = input.LA(1);

                if ( (LA66_0==COMMA) ) {
                    alt66=1;
                }


                switch (alt66) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:288:21: COMMA expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList1922); if (state.failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList1924);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop66;
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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:291:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:2: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt67=12;
            alt67 = dfa67.predict(input);
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:292:6: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator1942); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:13: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator1956); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:13: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator1970); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:13: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator1984); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:296:13: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator1998); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:297:13: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator2012); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:298:13: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator2026); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:13: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator2040); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:13: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator2054); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:13: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2068); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2070); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2072); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:13: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2095); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2097); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2099); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2101); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:13: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2122); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2124); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2126); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:309:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:310:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key2150); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:313:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:314:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key2171); if (state.failed) return ;

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


    // $ANTLR start "instanceof_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:317:1: instanceof_key : {...}? =>id= ID ;
    public final void instanceof_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:318:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:318:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key2192); if (state.failed) return ;

            }

        }

        catch (RecognitionException re) {
            throw re;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "instanceof_key"


    // $ANTLR start "boolean_key"
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:321:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:322:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key2213); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:325:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key2234); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:329:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key2255); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:333:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key2276); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:337:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:338:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key2297); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key2318); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key2339); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:350:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:350:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key2360); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key2381); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key2402); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:361:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key2423); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:366:2: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:366:9: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key2444); if (state.failed) return ;

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

    // $ANTLR start synpred1_DRLExpressions
    public final void synpred1_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:5: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:6: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRLExpressions207);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:41: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:64:42: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions218); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions220); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:10: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:11: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRLExpressions241);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:52: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:53: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRLExpressions255);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:89: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:90: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions267); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions269); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:29: ( AT )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:81:30: AT
        {
        match(input,AT,FOLLOW_AT_in_synpred6_DRLExpressions349); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:27: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:87:28: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred7_DRLExpressions378);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:27: ( LESS )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:28: LESS
        {
        match(input,LESS,FOLLOW_LESS_in_synpred8_DRLExpressions625); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:30: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:31: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred9_DRLExpressions683);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:36: ( PLUS | MINUS )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:156:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred11_DRLExpressions902);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:157:85: ( INCR | DECR )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:
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
    // $ANTLR end synpred12_DRLExpressions

    // $ANTLR start synpred13_DRLExpressions
    public final void synpred13_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:161:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred13_DRLExpressions961); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred13_DRLExpressions963);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred14_DRLExpressions984); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred14_DRLExpressions986);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred15_DRLExpressions1109);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_DRLExpressions

    // $ANTLR start synpred16_DRLExpressions
    public final void synpred16_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred16_DRLExpressions1124);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:9: ( literal )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:180:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred17_DRLExpressions1149);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred18_DRLExpressions1169);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred19_DRLExpressions1186);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred20_DRLExpressions1203);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred21_DRLExpressions1234);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:187:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:187:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred22_DRLExpressions1249);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:9: ( ID )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred23_DRLExpressions1264); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:20: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred24_DRLExpressions1271); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred24_DRLExpressions1273); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:188:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred25_DRLExpressions1284);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred26_DRLExpressions1423); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred26_DRLExpressions1425); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:213:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred27_DRLExpressions1447); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred28_DRLExpressions1629); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred28_DRLExpressions1631); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:6: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:271:7: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred29_DRLExpressions1783); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred29_DRLExpressions1785);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:6: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:7: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred30_DRLExpressions1800); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred30_DRLExpressions1802);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:14: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:15: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred31_DRLExpressions1827); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:15: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:280:16: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred32_DRLExpressions1873); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:13: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:302:14: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred33_DRLExpressions2087); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred33_DRLExpressions2089); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred33_DRLExpressions2091); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:13: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:14: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred34_DRLExpressions2116); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred34_DRLExpressions2118); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

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


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA24 dfa24 = new DFA24(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA30 dfa30 = new DFA30(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA38 dfa38 = new DFA38(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA52 dfa52 = new DFA52(this);
    protected DFA61 dfa61 = new DFA61(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA67 dfa67 = new DFA67(this);
    static final String DFA4_eotS =
        "\52\uffff";
    static final String DFA4_eofS =
        "\1\2\51\uffff";
    static final String DFA4_minS =
        "\1\10\1\0\50\uffff";
    static final String DFA4_maxS =
        "\1\102\1\0\50\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\46\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\50\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\3\2\2\uffff\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1"+
            "\2",
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
            return "65:9: ( ( typeArguments )=> typeArguments )?";
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
                        if ( (synpred3_DRLExpressions()) ) {s = 41;}

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
    static final String DFA5_eotS =
        "\52\uffff";
    static final String DFA5_eofS =
        "\1\2\51\uffff";
    static final String DFA5_minS =
        "\1\10\1\0\50\uffff";
    static final String DFA5_maxS =
        "\1\102\1\0\50\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\46\uffff\1\1";
    static final String DFA5_specialS =
        "\1\uffff\1\0\50\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\3\2\2\uffff\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1"+
            "\2",
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
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "65:51: ( ( typeArguments )=> typeArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA14_eotS =
        "\16\uffff";
    static final String DFA14_eofS =
        "\16\uffff";
    static final String DFA14_minS =
        "\1\10\13\0\2\uffff";
    static final String DFA14_maxS =
        "\1\102\13\0\2\uffff";
    static final String DFA14_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA14_specialS =
        "\1\uffff\1\4\1\2\1\6\1\5\1\10\1\7\1\12\1\11\1\3\1\0\1\1\2\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\14\2\uffff\2\14\1\uffff\1\14\3\uffff\2\14\1\uffff\1\2\1\3"+
            "\1\4\1\5\1\6\1\7\1\10\1\11\2\14\2\uffff\1\14\4\uffff\1\13\1"+
            "\12\1\1\4\14\1\uffff\2\14\4\uffff\2\14\5\uffff\2\14\5\uffff"+
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

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "87:26: ( ( assignmentOperator )=> assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_10 = input.LA(1);

                         
                        int index14_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_11 = input.LA(1);

                         
                        int index14_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA14_2 = input.LA(1);

                         
                        int index14_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA14_9 = input.LA(1);

                         
                        int index14_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_9);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA14_1 = input.LA(1);

                         
                        int index14_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_1);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA14_4 = input.LA(1);

                         
                        int index14_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_4);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA14_3 = input.LA(1);

                         
                        int index14_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_3);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA14_6 = input.LA(1);

                         
                        int index14_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA14_5 = input.LA(1);

                         
                        int index14_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_5);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA14_8 = input.LA(1);

                         
                        int index14_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_8);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA14_7 = input.LA(1);

                         
                        int index14_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index14_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA22_eotS =
        "\51\uffff";
    static final String DFA22_eofS =
        "\1\2\50\uffff";
    static final String DFA22_minS =
        "\1\10\1\0\47\uffff";
    static final String DFA22_maxS =
        "\1\102\1\0\47\uffff";
    static final String DFA22_acceptS =
        "\2\uffff\1\2\45\uffff\1\1";
    static final String DFA22_specialS =
        "\1\uffff\1\0\47\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\3\2\2\uffff\7\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff"+
            "\1\1",
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
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "118:30: ( instanceof_key type )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_1 = input.LA(1);

                         
                        int index22_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {s = 40;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index22_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA23_eotS =
        "\51\uffff";
    static final String DFA23_eofS =
        "\1\1\50\uffff";
    static final String DFA23_minS =
        "\1\10\22\uffff\2\0\24\uffff";
    static final String DFA23_maxS =
        "\1\102\22\uffff\2\0\24\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\2\46\uffff\1\1";
    static final String DFA23_specialS =
        "\1\0\22\uffff\1\1\1\2\24\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff"+
            "\3\1\2\50\1\24\1\23\5\1\1\uffff\2\1\1\uffff\10\1\2\uffff\2\1"+
            "\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "()* loopback of 122:25: ( ( LESS )=> relationalOp shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA23_0 = input.LA(1);

                         
                        int index23_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA23_0==EOF||LA23_0==FLOAT||(LA23_0>=HEX && LA23_0<=DECIMAL)||LA23_0==STRING||(LA23_0>=BOOL && LA23_0<=NULL)||(LA23_0>=PLUS_ASSIGN && LA23_0<=INCR)||(LA23_0>=COLON && LA23_0<=NOT_EQUALS)||(LA23_0>=EQUALS_ASSIGN && LA23_0<=RIGHT_SQUARE)||(LA23_0>=RIGHT_CURLY && LA23_0<=COMMA)||(LA23_0>=DOUBLE_AMPER && LA23_0<=XOR)||(LA23_0>=MINUS && LA23_0<=PLUS)||LA23_0==ID) ) {s = 1;}

                        else if ( (LA23_0==LESS) ) {s = 19;}

                        else if ( (LA23_0==GREATER) ) {s = 20;}

                        else if ( ((LA23_0>=GREATER_EQUALS && LA23_0<=LESS_EQUALS)) && (synpred8_DRLExpressions())) {s = 40;}

                         
                        input.seek(index23_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA23_19 = input.LA(1);

                         
                        int index23_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 40;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index23_19);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA23_20 = input.LA(1);

                         
                        int index23_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 40;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index23_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 23, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA24_eotS =
        "\52\uffff";
    static final String DFA24_eofS =
        "\1\2\51\uffff";
    static final String DFA24_minS =
        "\1\10\1\0\22\uffff\1\0\25\uffff";
    static final String DFA24_maxS =
        "\1\102\1\0\22\uffff\1\0\25\uffff";
    static final String DFA24_acceptS =
        "\2\uffff\1\2\46\uffff\1\1";
    static final String DFA24_specialS =
        "\1\uffff\1\0\22\uffff\1\1\25\uffff}>";
    static final String[] DFA24_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\5\2\1\24\1\1\5\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff"+
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
            ""
    };

    static final short[] DFA24_eot = DFA.unpackEncodedString(DFA24_eotS);
    static final short[] DFA24_eof = DFA.unpackEncodedString(DFA24_eofS);
    static final char[] DFA24_min = DFA.unpackEncodedStringToUnsignedChars(DFA24_minS);
    static final char[] DFA24_max = DFA.unpackEncodedStringToUnsignedChars(DFA24_maxS);
    static final short[] DFA24_accept = DFA.unpackEncodedString(DFA24_acceptS);
    static final short[] DFA24_special = DFA.unpackEncodedString(DFA24_specialS);
    static final short[][] DFA24_transition;

    static {
        int numStates = DFA24_transitionS.length;
        DFA24_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA24_transition[i] = DFA.unpackEncodedString(DFA24_transitionS[i]);
        }
    }

    class DFA24 extends DFA {

        public DFA24(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 24;
            this.eot = DFA24_eot;
            this.eof = DFA24_eof;
            this.min = DFA24_min;
            this.max = DFA24_max;
            this.accept = DFA24_accept;
            this.special = DFA24_special;
            this.transition = DFA24_transition;
        }
        public String getDescription() {
            return "()* loopback of 130:28: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA24_1 = input.LA(1);

                         
                        int index24_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index24_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA24_20 = input.LA(1);

                         
                        int index24_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index24_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 24, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA26_eotS =
        "\52\uffff";
    static final String DFA26_eofS =
        "\1\1\51\uffff";
    static final String DFA26_minS =
        "\1\10\31\uffff\2\0\16\uffff";
    static final String DFA26_maxS =
        "\1\102\31\uffff\2\0\16\uffff";
    static final String DFA26_acceptS =
        "\1\uffff\1\2\47\uffff\1\1";
    static final String DFA26_specialS =
        "\32\uffff\1\0\1\1\16\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff"+
            "\14\1\1\uffff\2\1\1\uffff\10\1\2\uffff\1\33\1\32\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            "",
            "",
            ""
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "()* loopback of 138:34: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA26_26 = input.LA(1);

                         
                        int index26_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index26_26);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA26_27 = input.LA(1);

                         
                        int index26_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index26_27);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 26, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA31_eotS =
        "\16\uffff";
    static final String DFA31_eofS =
        "\16\uffff";
    static final String DFA31_minS =
        "\1\10\2\uffff\1\0\12\uffff";
    static final String DFA31_maxS =
        "\1\102\2\uffff\1\0\12\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\10\uffff\1\3";
    static final String DFA31_specialS =
        "\3\uffff\1\0\12\uffff}>";
    static final String[] DFA31_transitionS = {
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
            return "153:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ({...}? => selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_3 = input.LA(1);

                         
                        int index31_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index31_3);
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
    static final String DFA29_eotS =
        "\53\uffff";
    static final String DFA29_eofS =
        "\1\1\52\uffff";
    static final String DFA29_minS =
        "\1\10\47\uffff\1\0\2\uffff";
    static final String DFA29_maxS =
        "\1\103\47\uffff\1\0\2\uffff";
    static final String DFA29_acceptS =
        "\1\uffff\1\2\50\uffff\1\1";
    static final String DFA29_specialS =
        "\1\0\47\uffff\1\1\2\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff"+
            "\12\1\1\50\1\1\1\uffff\2\1\1\52\14\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()* loopback of 157:17: ({...}? => selector )*";
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
                        if ( (LA29_0==EOF||LA29_0==FLOAT||(LA29_0>=HEX && LA29_0<=DECIMAL)||LA29_0==STRING||(LA29_0>=BOOL && LA29_0<=NULL)||(LA29_0>=PLUS_ASSIGN && LA29_0<=INCR)||(LA29_0>=COLON && LA29_0<=RIGHT_PAREN)||LA29_0==RIGHT_SQUARE||(LA29_0>=RIGHT_CURLY && LA29_0<=COMMA)||(LA29_0>=DOUBLE_AMPER && LA29_0<=PLUS)||(LA29_0>=ID && LA29_0<=DIV)) ) {s = 1;}

                        else if ( (LA29_0==LEFT_SQUARE) ) {s = 40;}

                        else if ( (LA29_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index29_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_40 = input.LA(1);

                         
                        int index29_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((helper.validateLT(1,".")||helper.validateLT(1,"["))) ) {s = 42;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index29_40);
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
    static final String DFA30_eotS =
        "\53\uffff";
    static final String DFA30_eofS =
        "\1\2\52\uffff";
    static final String DFA30_minS =
        "\1\10\1\0\34\uffff\1\0\14\uffff";
    static final String DFA30_maxS =
        "\1\103\1\0\34\uffff\1\0\14\uffff";
    static final String DFA30_acceptS =
        "\2\uffff\1\2\47\uffff\1\1";
    static final String DFA30_specialS =
        "\1\uffff\1\0\34\uffff\1\1\14\uffff}>";
    static final String[] DFA30_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\10\2\1\36\1"+
            "\1\2\uffff\14\2\1\uffff\2\2\1\uffff\14\2\5\uffff\2\2",
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
            return "157:84: ( ( INCR | DECR )=> ( INCR | DECR ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA30_1 = input.LA(1);

                         
                        int index30_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index30_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA30_30 = input.LA(1);

                         
                        int index30_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index30_30);
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
    static final String DFA33_eotS =
        "\12\uffff";
    static final String DFA33_eofS =
        "\12\uffff";
    static final String DFA33_minS =
        "\1\102\1\0\10\uffff";
    static final String DFA33_maxS =
        "\1\102\1\0\10\uffff";
    static final String DFA33_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA33_specialS =
        "\1\1\1\0\10\uffff}>";
    static final String[] DFA33_transitionS = {
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
            return "166:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_1 = input.LA(1);

                         
                        int index33_1 = input.index();
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

                         
                        input.seek(index33_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA33_0 = input.LA(1);

                         
                        int index33_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA33_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index33_0);
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
    static final String DFA38_eotS =
        "\21\uffff";
    static final String DFA38_eofS =
        "\21\uffff";
    static final String DFA38_minS =
        "\1\10\10\uffff\2\0\6\uffff";
    static final String DFA38_maxS =
        "\1\102\10\uffff\2\0\6\uffff";
    static final String DFA38_acceptS =
        "\1\uffff\1\1\1\2\6\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA38_specialS =
        "\1\0\10\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA38_transitionS = {
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

    static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
    static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
    static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
    static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
    static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
    static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
    static final short[][] DFA38_transition;

    static {
        int numStates = DFA38_transitionS.length;
        DFA38_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
        }
    }

    class DFA38 extends DFA {

        public DFA38(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 38;
            this.eot = DFA38_eot;
            this.eof = DFA38_eof;
            this.min = DFA38_min;
            this.max = DFA38_max;
            this.accept = DFA38_accept;
            this.special = DFA38_special;
            this.transition = DFA38_transition;
        }
        public String getDescription() {
            return "177:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA38_0 = input.LA(1);

                         
                        int index38_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA38_0==LEFT_PAREN) && (synpred15_DRLExpressions())) {s = 1;}

                        else if ( (LA38_0==LESS) && (synpred16_DRLExpressions())) {s = 2;}

                        else if ( (LA38_0==STRING) && (synpred17_DRLExpressions())) {s = 3;}

                        else if ( (LA38_0==DECIMAL) && (synpred17_DRLExpressions())) {s = 4;}

                        else if ( (LA38_0==HEX) && (synpred17_DRLExpressions())) {s = 5;}

                        else if ( (LA38_0==FLOAT) && (synpred17_DRLExpressions())) {s = 6;}

                        else if ( (LA38_0==BOOL) && (synpred17_DRLExpressions())) {s = 7;}

                        else if ( (LA38_0==NULL) && (synpred17_DRLExpressions())) {s = 8;}

                        else if ( (LA38_0==ID) ) {s = 9;}

                        else if ( (LA38_0==LEFT_SQUARE) ) {s = 10;}

                         
                        input.seek(index38_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA38_9 = input.LA(1);

                         
                        int index38_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred18_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 11;}

                        else if ( ((synpred19_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 12;}

                        else if ( (((synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred20_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))))) ) {s = 13;}

                        else if ( (synpred23_DRLExpressions()) ) {s = 14;}

                         
                        input.seek(index38_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA38_10 = input.LA(1);

                         
                        int index38_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred21_DRLExpressions()) ) {s = 15;}

                        else if ( (synpred22_DRLExpressions()) ) {s = 16;}

                         
                        input.seek(index38_10);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 38, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA37_eotS =
        "\54\uffff";
    static final String DFA37_eofS =
        "\1\3\53\uffff";
    static final String DFA37_minS =
        "\1\10\2\0\51\uffff";
    static final String DFA37_maxS =
        "\1\103\2\0\51\uffff";
    static final String DFA37_acceptS =
        "\3\uffff\1\2\46\uffff\1\2\1\1";
    static final String DFA37_specialS =
        "\1\0\1\1\1\2\51\uffff}>";
    static final String[] DFA37_transitionS = {
            "\1\3\2\uffff\2\3\1\uffff\1\3\3\uffff\2\3\1\uffff\12\3\2\uffff"+
            "\10\3\1\2\1\3\1\1\1\3\1\uffff\2\3\1\52\14\3\5\uffff\2\3",
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
            return "188:38: ( ( identifierSuffix )=> identifierSuffix )?";
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
                        if ( (LA37_0==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA37_0==LEFT_PAREN) ) {s = 2;}

                        else if ( (LA37_0==EOF||LA37_0==FLOAT||(LA37_0>=HEX && LA37_0<=DECIMAL)||LA37_0==STRING||(LA37_0>=BOOL && LA37_0<=NULL)||(LA37_0>=PLUS_ASSIGN && LA37_0<=INCR)||(LA37_0>=COLON && LA37_0<=EQUALS_ASSIGN)||LA37_0==RIGHT_PAREN||LA37_0==RIGHT_SQUARE||(LA37_0>=RIGHT_CURLY && LA37_0<=COMMA)||(LA37_0>=DOUBLE_AMPER && LA37_0<=PLUS)||(LA37_0>=ID && LA37_0<=DIV)) ) {s = 3;}

                        else if ( (LA37_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index37_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA37_1 = input.LA(1);

                         
                        int index37_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred25_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
                        input.seek(index37_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA37_2 = input.LA(1);

                         
                        int index37_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred25_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
                        input.seek(index37_2);
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
    static final String DFA43_eotS =
        "\54\uffff";
    static final String DFA43_eofS =
        "\1\1\53\uffff";
    static final String DFA43_minS =
        "\1\10\47\uffff\1\0\3\uffff";
    static final String DFA43_maxS =
        "\1\103\47\uffff\1\0\3\uffff";
    static final String DFA43_acceptS =
        "\1\uffff\1\2\50\uffff\1\2\1\1";
    static final String DFA43_specialS =
        "\1\0\47\uffff\1\1\3\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\2\1\1\uffff\12\1\2\uffff"+
            "\12\1\1\50\1\1\1\uffff\2\1\1\52\14\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()+ loopback of 213:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA43_0 = input.LA(1);

                         
                        int index43_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA43_0==EOF||LA43_0==FLOAT||(LA43_0>=HEX && LA43_0<=DECIMAL)||LA43_0==STRING||(LA43_0>=BOOL && LA43_0<=NULL)||(LA43_0>=PLUS_ASSIGN && LA43_0<=INCR)||(LA43_0>=COLON && LA43_0<=RIGHT_PAREN)||LA43_0==RIGHT_SQUARE||(LA43_0>=RIGHT_CURLY && LA43_0<=COMMA)||(LA43_0>=DOUBLE_AMPER && LA43_0<=PLUS)||(LA43_0>=ID && LA43_0<=DIV)) ) {s = 1;}

                        else if ( (LA43_0==LEFT_SQUARE) ) {s = 40;}

                        else if ( (LA43_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index43_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA43_40 = input.LA(1);

                         
                        int index43_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred27_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
                        input.seek(index43_40);
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
    static final String DFA52_eotS =
        "\54\uffff";
    static final String DFA52_eofS =
        "\1\2\53\uffff";
    static final String DFA52_minS =
        "\1\10\1\0\52\uffff";
    static final String DFA52_maxS =
        "\1\103\1\0\52\uffff";
    static final String DFA52_acceptS =
        "\2\uffff\1\2\47\uffff\1\2\1\1";
    static final String DFA52_specialS =
        "\1\0\1\1\52\uffff}>";
    static final String[] DFA52_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\12\2\1\1\1\2\1\uffff\2\2\1\52\14\2\5\uffff\2\2",
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
            "",
            "",
            "",
            "",
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
            return "()* loopback of 240:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA52_0 = input.LA(1);

                         
                        int index52_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA52_0==LEFT_SQUARE) ) {s = 1;}

                        else if ( (LA52_0==EOF||LA52_0==FLOAT||(LA52_0>=HEX && LA52_0<=DECIMAL)||LA52_0==STRING||(LA52_0>=BOOL && LA52_0<=NULL)||(LA52_0>=PLUS_ASSIGN && LA52_0<=INCR)||(LA52_0>=COLON && LA52_0<=RIGHT_PAREN)||LA52_0==RIGHT_SQUARE||(LA52_0>=RIGHT_CURLY && LA52_0<=COMMA)||(LA52_0>=DOUBLE_AMPER && LA52_0<=PLUS)||(LA52_0>=ID && LA52_0<=DIV)) ) {s = 2;}

                        else if ( (LA52_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index52_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA52_1 = input.LA(1);

                         
                        int index52_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
                        input.seek(index52_1);
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
        "\54\uffff";
    static final String DFA61_eofS =
        "\1\2\53\uffff";
    static final String DFA61_minS =
        "\1\10\1\0\52\uffff";
    static final String DFA61_maxS =
        "\1\103\1\0\52\uffff";
    static final String DFA61_acceptS =
        "\2\uffff\1\2\47\uffff\1\2\1\1";
    static final String DFA61_specialS =
        "\1\0\1\1\52\uffff}>";
    static final String[] DFA61_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\10\2\1\1\3\2\1\uffff\2\2\1\52\14\2\5\uffff\2\2",
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
            return "273:13: ( ( LEFT_PAREN )=> arguments )?";
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
                        if ( (LA61_0==LEFT_PAREN) ) {s = 1;}

                        else if ( (LA61_0==EOF||LA61_0==FLOAT||(LA61_0>=HEX && LA61_0<=DECIMAL)||LA61_0==STRING||(LA61_0>=BOOL && LA61_0<=NULL)||(LA61_0>=PLUS_ASSIGN && LA61_0<=INCR)||(LA61_0>=COLON && LA61_0<=EQUALS_ASSIGN)||(LA61_0>=RIGHT_PAREN && LA61_0<=RIGHT_SQUARE)||(LA61_0>=RIGHT_CURLY && LA61_0<=COMMA)||(LA61_0>=DOUBLE_AMPER && LA61_0<=PLUS)||(LA61_0>=ID && LA61_0<=DIV)) ) {s = 2;}

                        else if ( (LA61_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index61_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA61_1 = input.LA(1);

                         
                        int index61_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred31_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
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
    static final String DFA63_eotS =
        "\54\uffff";
    static final String DFA63_eofS =
        "\1\2\53\uffff";
    static final String DFA63_minS =
        "\1\10\1\0\52\uffff";
    static final String DFA63_maxS =
        "\1\103\1\0\52\uffff";
    static final String DFA63_acceptS =
        "\2\uffff\1\2\47\uffff\1\2\1\1";
    static final String DFA63_specialS =
        "\1\0\1\1\52\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\2\2\1\uffff\12\2\2\uffff"+
            "\10\2\1\1\3\2\1\uffff\2\2\1\52\14\2\5\uffff\2\2",
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
            return "280:14: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_0 = input.LA(1);

                         
                        int index63_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA63_0==LEFT_PAREN) ) {s = 1;}

                        else if ( (LA63_0==EOF||LA63_0==FLOAT||(LA63_0>=HEX && LA63_0<=DECIMAL)||LA63_0==STRING||(LA63_0>=BOOL && LA63_0<=NULL)||(LA63_0>=PLUS_ASSIGN && LA63_0<=INCR)||(LA63_0>=COLON && LA63_0<=EQUALS_ASSIGN)||(LA63_0>=RIGHT_PAREN && LA63_0<=RIGHT_SQUARE)||(LA63_0>=RIGHT_CURLY && LA63_0<=COMMA)||(LA63_0>=DOUBLE_AMPER && LA63_0<=PLUS)||(LA63_0>=ID && LA63_0<=DIV)) ) {s = 2;}

                        else if ( (LA63_0==DOT) && ((helper.validateLT(1,".")||helper.validateLT(1,"[")))) {s = 42;}

                         
                        input.seek(index63_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA63_1 = input.LA(1);

                         
                        int index63_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred32_DRLExpressions()) ) {s = 43;}

                        else if ( (true) ) {s = 42;}

                         
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
    static final String DFA67_eotS =
        "\17\uffff";
    static final String DFA67_eofS =
        "\17\uffff";
    static final String DFA67_minS =
        "\1\25\12\uffff\2\46\2\uffff";
    static final String DFA67_maxS =
        "\1\50\12\uffff\1\46\1\50\2\uffff";
    static final String DFA67_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA67_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA67_transitionS = {
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

    static final short[] DFA67_eot = DFA.unpackEncodedString(DFA67_eotS);
    static final short[] DFA67_eof = DFA.unpackEncodedString(DFA67_eofS);
    static final char[] DFA67_min = DFA.unpackEncodedStringToUnsignedChars(DFA67_minS);
    static final char[] DFA67_max = DFA.unpackEncodedStringToUnsignedChars(DFA67_maxS);
    static final short[] DFA67_accept = DFA.unpackEncodedString(DFA67_acceptS);
    static final short[] DFA67_special = DFA.unpackEncodedString(DFA67_specialS);
    static final short[][] DFA67_transition;

    static {
        int numStates = DFA67_transitionS.length;
        DFA67_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA67_transition[i] = DFA.unpackEncodedString(DFA67_transitionS[i]);
        }
    }

    class DFA67 extends DFA {

        public DFA67(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 67;
            this.eot = DFA67_eot;
            this.eof = DFA67_eof;
            this.min = DFA67_min;
            this.max = DFA67_max;
            this.accept = DFA67_accept;
            this.special = DFA67_special;
            this.transition = DFA67_transition;
        }
        public String getDescription() {
            return "291:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA67_12 = input.LA(1);

                         
                        int index67_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA67_12==GREATER) && (synpred33_DRLExpressions())) {s = 13;}

                        else if ( (LA67_12==EQUALS_ASSIGN) && (synpred34_DRLExpressions())) {s = 14;}

                         
                        input.seek(index67_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 67, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal74 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList186 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList189 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeList191 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type214 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type224 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type226 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ID_in_type237 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type244 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_DOT_in_type249 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_type251 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type258 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type273 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type275 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments290 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments292 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments295 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments297 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument318 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_typeArgument326 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeArgument329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_embeddedExpression345 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_dummy_in_embeddedExpression352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression374 = new BitSet(new long[]{0x000001C01FE00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression383 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression411 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression415 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression417 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression419 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression439 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression443 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression445 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression464 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression468 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression470 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression489 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression493 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression495 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression514 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression518 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression520 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression539 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression543 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_equalityExpression_in_andExpression545 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression564 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression568 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression578 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_relationalExpression_in_instanceOfExpression597 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression620 = new BitSet(new long[]{0x000000F000000002L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression629 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression631 = new BitSet(new long[]{0x000000F000000002L});
    public static final BitSet FOLLOW_set_in_relationalOp649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression678 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression686 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression688 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp703 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp709 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp711 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp717 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression737 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression748 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression756 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression775 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression779 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression793 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression813 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression823 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression835 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression847 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus878 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus889 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus915 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus920 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression968 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression970 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression972 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression991 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_castExpression993 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression995 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1006 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_castExpression1008 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1010 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType1043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType1075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary1113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1128 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary1135 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_primary1137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary1173 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary1190 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_creator_in_primary1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1207 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1210 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1212 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_primary1216 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_primary1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary1253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1267 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_DOT_in_primary1276 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_primary1278 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1308 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1310 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression1335 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression1337 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1357 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList1360 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1362 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry1385 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry1387 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mapEntry1389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression1403 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_parExpression1405 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression1407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1429 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1431 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix1437 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1452 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix1454 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1456 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator1487 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_createdName_in_creator1490 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName1517 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1519 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName1532 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_createdName1534 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1536 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator1566 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator1568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1581 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1589 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1592 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1594 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest1598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1612 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1614 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1619 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1621 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1623 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1635 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1637 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer1683 = new BitSet(new long[]{0x18306A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1686 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1689 = new BitSet(new long[]{0x18302A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1691 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1696 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1727 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments1741 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments1743 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments1745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix1757 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix1767 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix1769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1788 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_selector1790 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1805 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_selector1807 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector1810 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_innerCreator_in_selector1814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector1821 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_selector1823 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_selector1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector1843 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_selector1845 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector1847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix1859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix1867 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_superSuffix1869 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments1898 = new BitSet(new long[]{0x18300E80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_arguments1900 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList1919 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList1922 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expressionList1924 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator1942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator1956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator1970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator1984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator1998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2068 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2070 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2095 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2097 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2099 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2122 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2124 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key2150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key2171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key2192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key2213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key2234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key2255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key2276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key2297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key2318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key2339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key2360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key2381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key2402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key2423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key2444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions218 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions267 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_synpred6_DRLExpressions349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred7_DRLExpressions378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_synpred8_DRLExpressions625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred9_DRLExpressions683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred10_DRLExpressions741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred11_DRLExpressions902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred12_DRLExpressions925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred13_DRLExpressions961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_synpred13_DRLExpressions963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred14_DRLExpressions984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_synpred14_DRLExpressions986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred15_DRLExpressions1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred16_DRLExpressions1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred17_DRLExpressions1149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred18_DRLExpressions1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred19_DRLExpressions1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred20_DRLExpressions1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred21_DRLExpressions1234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred22_DRLExpressions1249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred23_DRLExpressions1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred24_DRLExpressions1271 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_synpred24_DRLExpressions1273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred25_DRLExpressions1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred26_DRLExpressions1423 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred26_DRLExpressions1425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred27_DRLExpressions1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred28_DRLExpressions1629 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred28_DRLExpressions1631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred29_DRLExpressions1783 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_synpred29_DRLExpressions1785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred30_DRLExpressions1800 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_synpred30_DRLExpressions1802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred31_DRLExpressions1827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred32_DRLExpressions1873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred33_DRLExpressions2087 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred33_DRLExpressions2089 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred33_DRLExpressions2091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred34_DRLExpressions2116 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred34_DRLExpressions2118 = new BitSet(new long[]{0x0000000000000002L});

}