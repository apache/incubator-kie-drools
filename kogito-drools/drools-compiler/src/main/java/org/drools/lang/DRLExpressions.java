// $ANTLR 3.3 Nov 30, 2010 12:45:30 /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g 2011-02-21 15:12:40

    package org.drools.lang;

    import java.util.LinkedList;
    import org.drools.compiler.DroolsParserException;
    import org.drools.lang.ParserHelper;
    import org.drools.lang.DroolsParserExceptionFactory;
    import org.drools.CheckedDroolsException;


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
        
        private boolean buildConstraint;
        public void setBuildConstraint( boolean build ) { this.buildConstraint = build; }
        public boolean isBuildConstraint() { return this.buildConstraint; }




    // $ANTLR start "literal"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:56:1: literal : ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL );
    public final void literal() throws RecognitionException {
        Token STRING1=null;
        Token DECIMAL2=null;
        Token HEX3=null;
        Token FLOAT4=null;
        Token BOOL5=null;
        Token NULL6=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:57:5: ( STRING | DECIMAL | HEX | FLOAT | BOOL | NULL )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:57:7: STRING
                    {
                    STRING1=(Token)match(input,STRING,FOLLOW_STRING_in_literal83); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(STRING1, DroolsEditorType.STRING_CONST);	
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:58:7: DECIMAL
                    {
                    DECIMAL2=(Token)match(input,DECIMAL,FOLLOW_DECIMAL_in_literal98); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(DECIMAL2, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:59:7: HEX
                    {
                    HEX3=(Token)match(input,HEX,FOLLOW_HEX_in_literal110); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(HEX3, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:60:7: FLOAT
                    {
                    FLOAT4=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal126); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(FLOAT4, DroolsEditorType.NUMERIC_CONST);	
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:61:7: BOOL
                    {
                    BOOL5=(Token)match(input,BOOL,FOLLOW_BOOL_in_literal140); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	helper.emit(BOOL5, DroolsEditorType.BOOLEAN_CONST);	
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:62:7: NULL
                    {
                    NULL6=(Token)match(input,NULL,FOLLOW_NULL_in_literal157); if (state.failed) return ;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:65:1: typeList : type ( COMMA type )* ;
    public final void typeList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:5: ( type ( COMMA type )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:7: type ( COMMA type )*
            {
            pushFollow(FOLLOW_type_in_typeList183);
            type();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:12: ( COMMA type )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==COMMA) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:66:13: COMMA type
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeList186); if (state.failed) return ;
            	    pushFollow(FOLLOW_type_in_typeList188);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:69:1: type : ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) );
    public final void type() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:5: ( ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) | ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                int LA8_1 = input.LA(2);

                if ( (((synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred1_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) ) {
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:8: ( primitiveType )=> ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:27: ( primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:29: primitiveType ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_primitiveType_in_type216);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:43: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:44: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type226); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type228); if (state.failed) return ;

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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:7: ( ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:9: ID ( ( typeArguments )=> typeArguments )? ( DOT ID ( ( typeArguments )=> typeArguments )? )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    match(input,ID,FOLLOW_ID_in_type242); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:12: ( ( typeArguments )=> typeArguments )?
                    int alt4=2;
                    alt4 = dfa4.predict(input);
                    switch (alt4) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:13: ( typeArguments )=> typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_type249);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:46: ( DOT ID ( ( typeArguments )=> typeArguments )? )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==DOT) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:47: DOT ID ( ( typeArguments )=> typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_type254); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_type256); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:54: ( ( typeArguments )=> typeArguments )?
                    	    int alt5=2;
                    	    alt5 = dfa5.predict(input);
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:55: ( typeArguments )=> typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_type263);
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

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:91: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
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
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:92: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_type278); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_type280); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:74:1: typeArguments : LESS typeArgument ( COMMA typeArgument )* GREATER ;
    public final void typeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:5: ( LESS typeArgument ( COMMA typeArgument )* GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:7: LESS typeArgument ( COMMA typeArgument )* GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_typeArguments301); if (state.failed) return ;
            pushFollow(FOLLOW_typeArgument_in_typeArguments303);
            typeArgument();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:25: ( COMMA typeArgument )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:75:26: COMMA typeArgument
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_typeArguments306); if (state.failed) return ;
            	    pushFollow(FOLLOW_typeArgument_in_typeArguments308);
            	    typeArgument();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,GREATER,FOLLOW_GREATER_in_typeArguments312); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:78:1: typeArgument : ( type | QUESTION ( ( extends_key | super_key ) type )? );
    public final void typeArgument() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:5: ( type | QUESTION ( ( extends_key | super_key ) type )? )
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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:79:7: type
                    {
                    pushFollow(FOLLOW_type_in_typeArgument329);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:7: QUESTION ( ( extends_key | super_key ) type )?
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_typeArgument337); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:16: ( ( extends_key | super_key ) type )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))||((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))))) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:17: ( extends_key | super_key ) type
                            {
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:17: ( extends_key | super_key )
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
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:18: extends_key
                                    {
                                    pushFollow(FOLLOW_extends_key_in_typeArgument341);
                                    extends_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:80:32: super_key
                                    {
                                    pushFollow(FOLLOW_super_key_in_typeArgument345);
                                    super_key();

                                    state._fsp--;
                                    if (state.failed) return ;

                                    }
                                    break;

                            }

                            pushFollow(FOLLOW_type_in_typeArgument348);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:88:1: dummy : expression ( AT | SEMICOLON ) ;
    public final void dummy() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:5: ( expression ( AT | SEMICOLON ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:89:7: expression ( AT | SEMICOLON )
            {
            pushFollow(FOLLOW_expression_in_dummy372);
            expression();

            state._fsp--;
            if (state.failed) return ;
            if ( input.LA(1)==AT||input.LA(1)==SEMICOLON ) {
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


    // $ANTLR start "expression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:93:1: expression : conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? ;
    public final void expression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:5: ( conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:7: conditionalExpression ( ( assignmentOperator )=> assignmentOperator expression )?
            {
            pushFollow(FOLLOW_conditionalExpression_in_expression400);
            conditionalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:29: ( ( assignmentOperator )=> assignmentOperator expression )?
            int alt13=2;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:30: ( assignmentOperator )=> assignmentOperator expression
                    {
                    pushFollow(FOLLOW_assignmentOperator_in_expression409);
                    assignmentOperator();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_expression411);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:97:1: conditionalExpression : conditionalOrExpression ( QUESTION expression COLON expression )? ;
    public final void conditionalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:3: ( conditionalOrExpression ( QUESTION expression COLON expression )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:5: conditionalOrExpression ( QUESTION expression COLON expression )?
            {
            pushFollow(FOLLOW_conditionalOrExpression_in_conditionalExpression428);
            conditionalOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:29: ( QUESTION expression COLON expression )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==QUESTION) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:98:31: QUESTION expression COLON expression
                    {
                    match(input,QUESTION,FOLLOW_QUESTION_in_conditionalExpression432); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression434);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,COLON,FOLLOW_COLON_in_conditionalExpression436); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_conditionalExpression438);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:101:1: conditionalOrExpression : conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:3: ( conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:5: conditionalAndExpression ( DOUBLE_PIPE conditionalAndExpression )*
            {
            pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression456);
            conditionalAndExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:30: ( DOUBLE_PIPE conditionalAndExpression )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==DOUBLE_PIPE) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:102:32: DOUBLE_PIPE conditionalAndExpression
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression460); if (state.failed) return ;
            	    pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression462);
            	    conditionalAndExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "conditionalOrExpression"


    // $ANTLR start "conditionalAndExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:105:1: conditionalAndExpression : inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:3: ( inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:5: inclusiveOrExpression ( DOUBLE_AMPER inclusiveOrExpression )*
            {
            pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression481);
            inclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:27: ( DOUBLE_AMPER inclusiveOrExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==DOUBLE_AMPER) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:106:29: DOUBLE_AMPER inclusiveOrExpression
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression485); if (state.failed) return ;
            	    pushFollow(FOLLOW_inclusiveOrExpression_in_conditionalAndExpression487);
            	    inclusiveOrExpression();

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
    // $ANTLR end "conditionalAndExpression"


    // $ANTLR start "inclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:109:1: inclusiveOrExpression : exclusiveOrExpression ( PIPE exclusiveOrExpression )* ;
    public final void inclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:3: ( exclusiveOrExpression ( PIPE exclusiveOrExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:5: exclusiveOrExpression ( PIPE exclusiveOrExpression )*
            {
            pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression505);
            exclusiveOrExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:27: ( PIPE exclusiveOrExpression )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==PIPE) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:110:29: PIPE exclusiveOrExpression
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_inclusiveOrExpression509); if (state.failed) return ;
            	    pushFollow(FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression511);
            	    exclusiveOrExpression();

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
    // $ANTLR end "inclusiveOrExpression"


    // $ANTLR start "exclusiveOrExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:113:1: exclusiveOrExpression : andExpression ( XOR andExpression )* ;
    public final void exclusiveOrExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:3: ( andExpression ( XOR andExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:5: andExpression ( XOR andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression529);
            andExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:19: ( XOR andExpression )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==XOR) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:114:21: XOR andExpression
            	    {
            	    match(input,XOR,FOLLOW_XOR_in_exclusiveOrExpression533); if (state.failed) return ;
            	    pushFollow(FOLLOW_andExpression_in_exclusiveOrExpression535);
            	    andExpression();

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
    // $ANTLR end "exclusiveOrExpression"


    // $ANTLR start "andExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:117:1: andExpression : andOrRestriction ( AMPER andOrRestriction )* ;
    public final void andExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:3: ( andOrRestriction ( AMPER andOrRestriction )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:5: andOrRestriction ( AMPER andOrRestriction )*
            {
            pushFollow(FOLLOW_andOrRestriction_in_andExpression554);
            andOrRestriction();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:22: ( AMPER andOrRestriction )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AMPER) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:118:24: AMPER andOrRestriction
            	    {
            	    match(input,AMPER,FOLLOW_AMPER_in_andExpression558); if (state.failed) return ;
            	    pushFollow(FOLLOW_andOrRestriction_in_andExpression560);
            	    andOrRestriction();

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
    // $ANTLR end "andExpression"


    // $ANTLR start "andOrRestriction"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:121:1: andOrRestriction : equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression )* ;
    public final void andOrRestriction() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:4: ( equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:6: equalityExpression ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression )*
            {
            pushFollow(FOLLOW_equalityExpression_in_andOrRestriction583);
            equalityExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:25: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression )*
            loop20:
            do {
                int alt20=2;
                alt20 = dfa20.predict(input);
                switch (alt20) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:27: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression
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

            	    pushFollow(FOLLOW_operator_in_andOrRestriction603);
            	    operator();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_andOrRestriction605);
            	    shiftExpression();

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
    // $ANTLR end "andOrRestriction"


    // $ANTLR start "equalityExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:125:1: equalityExpression : instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* ;
    public final void equalityExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:3: ( instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:5: instanceOfExpression ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            {
            pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression628);
            instanceOfExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:26: ( ( EQUALS | NOT_EQUALS ) instanceOfExpression )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( ((LA21_0>=EQUALS && LA21_0<=NOT_EQUALS)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:126:28: ( EQUALS | NOT_EQUALS ) instanceOfExpression
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

            	    pushFollow(FOLLOW_instanceOfExpression_in_equalityExpression642);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:129:1: instanceOfExpression : inExpression ( instanceof_key type )? ;
    public final void instanceOfExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:3: ( inExpression ( instanceof_key type )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:5: inExpression ( instanceof_key type )?
            {
            pushFollow(FOLLOW_inExpression_in_instanceOfExpression661);
            inExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:18: ( instanceof_key type )?
            int alt22=2;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:130:19: instanceof_key type
                    {
                    pushFollow(FOLLOW_instanceof_key_in_instanceOfExpression664);
                    instanceof_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_instanceOfExpression666);
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


    // $ANTLR start "inExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:133:1: inExpression : relationalExpression ( ( not_key )? in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )? ;
    public final void inExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:3: ( relationalExpression ( ( not_key )? in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )? )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:5: relationalExpression ( ( not_key )? in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )?
            {
            pushFollow(FOLLOW_relationalExpression_in_inExpression683);
            relationalExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:26: ( ( not_key )? in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==ID) ) {
                int LA25_1 = input.LA(2);

                if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.IN))))) ) {
                    alt25=1;
                }
            }
            switch (alt25) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:27: ( not_key )? in_key LEFT_PAREN expression ( COMMA expression )* RIGHT_PAREN
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:27: ( not_key )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))))) {
                        int LA23_1 = input.LA(2);

                        if ( (LA23_1==ID) && (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT))))) {
                            alt23=1;
                        }
                    }
                    switch (alt23) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:27: not_key
                            {
                            pushFollow(FOLLOW_not_key_in_inExpression686);
                            not_key();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_in_key_in_inExpression689);
                    in_key();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_inExpression691); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_inExpression693);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:65: ( COMMA expression )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:134:66: COMMA expression
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression696); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_inExpression698);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_inExpression702); if (state.failed) return ;

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
    // $ANTLR end "inExpression"


    // $ANTLR start "relationalExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:137:1: relationalExpression : shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )* ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:3: ( shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:5: shiftExpression ( ( relationalOp )=> relationalOp shiftExpression )*
            {
            pushFollow(FOLLOW_shiftExpression_in_relationalExpression718);
            shiftExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:21: ( ( relationalOp )=> relationalOp shiftExpression )*
            loop26:
            do {
                int alt26=2;
                alt26 = dfa26.predict(input);
                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:23: ( relationalOp )=> relationalOp shiftExpression
            	    {
            	    pushFollow(FOLLOW_relationalOp_in_relationalExpression727);
            	    relationalOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_shiftExpression_in_relationalExpression729);
            	    shiftExpression();

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
    // $ANTLR end "relationalExpression"


    // $ANTLR start "operator"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:141:1: operator : ( EQUALS | NOT_EQUALS | relationalOp ) ;
    public final void operator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:3: ( ( EQUALS | NOT_EQUALS | relationalOp ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:5: ( EQUALS | NOT_EQUALS | relationalOp )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:5: ( EQUALS | NOT_EQUALS | relationalOp )
            int alt27=3;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==EQUALS) ) {
                alt27=1;
            }
            else if ( (LA27_0==NOT_EQUALS) ) {
                alt27=2;
            }
            else if ( ((LA27_0>=GREATER_EQUALS && LA27_0<=LESS)) ) {
                alt27=3;
            }
            else if ( (LA27_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                alt27=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:142:7: EQUALS
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_operator747); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:143:7: NOT_EQUALS
                    {
                    match(input,NOT_EQUALS,FOLLOW_NOT_EQUALS_in_operator755); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:144:7: relationalOp
                    {
                    pushFollow(FOLLOW_relationalOp_in_operator763);
                    relationalOp();

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
    // $ANTLR end "operator"


    // $ANTLR start "relationalOp"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:148:1: relationalOp : ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) ;
    public final void relationalOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:3: ( ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:5: ( LESS_EQUALS | GREATER_EQUALS | LESS | GREATER | not_key neg_operator_key ( ( squareArguments )=> squareArguments )? | operator_key ( ( squareArguments )=> squareArguments )? )
            int alt30=6;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==LESS_EQUALS) ) {
                alt30=1;
            }
            else if ( (LA30_0==GREATER_EQUALS) ) {
                alt30=2;
            }
            else if ( (LA30_0==LESS) ) {
                alt30=3;
            }
            else if ( (LA30_0==GREATER) ) {
                alt30=4;
            }
            else if ( (LA30_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))||((helper.isPluggableEvaluator(false)))))) {
                int LA30_5 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                    alt30=5;
                }
                else if ( (((helper.isPluggableEvaluator(false)))) ) {
                    alt30=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 5, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:149:7: LESS_EQUALS
                    {
                    match(input,LESS_EQUALS,FOLLOW_LESS_EQUALS_in_relationalOp786); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:150:7: GREATER_EQUALS
                    {
                    match(input,GREATER_EQUALS,FOLLOW_GREATER_EQUALS_in_relationalOp794); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:151:7: LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_relationalOp803); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:152:7: GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_relationalOp812); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:7: not_key neg_operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_not_key_in_relationalOp820);
                    not_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_neg_operator_key_in_relationalOp822);
                    neg_operator_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:32: ( ( squareArguments )=> squareArguments )?
                    int alt28=2;
                    alt28 = dfa28.predict(input);
                    switch (alt28) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:33: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp830);
                            squareArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:7: operator_key ( ( squareArguments )=> squareArguments )?
                    {
                    pushFollow(FOLLOW_operator_key_in_relationalOp840);
                    operator_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:21: ( ( squareArguments )=> squareArguments )?
                    int alt29=2;
                    alt29 = dfa29.predict(input);
                    switch (alt29) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:22: ( squareArguments )=> squareArguments
                            {
                            pushFollow(FOLLOW_squareArguments_in_relationalOp849);
                            squareArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

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
        return ;
    }
    // $ANTLR end "relationalOp"


    // $ANTLR start "shiftExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:158:1: shiftExpression : additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* ;
    public final void shiftExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:3: ( additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:5: additiveExpression ( ( shiftOp )=> shiftOp additiveExpression )*
            {
            pushFollow(FOLLOW_additiveExpression_in_shiftExpression872);
            additiveExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:24: ( ( shiftOp )=> shiftOp additiveExpression )*
            loop31:
            do {
                int alt31=2;
                alt31 = dfa31.predict(input);
                switch (alt31) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:26: ( shiftOp )=> shiftOp additiveExpression
            	    {
            	    pushFollow(FOLLOW_shiftOp_in_shiftExpression880);
            	    shiftOp();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_additiveExpression_in_shiftExpression882);
            	    additiveExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop31;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:162:1: shiftOp : ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) ;
    public final void shiftOp() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:5: ( ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:7: ( LESS LESS | GREATER GREATER GREATER | GREATER GREATER )
            int alt32=3;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LESS) ) {
                alt32=1;
            }
            else if ( (LA32_0==GREATER) ) {
                int LA32_2 = input.LA(2);

                if ( (LA32_2==GREATER) ) {
                    int LA32_3 = input.LA(3);

                    if ( (LA32_3==GREATER) ) {
                        alt32=2;
                    }
                    else if ( (LA32_3==EOF||LA32_3==FLOAT||(LA32_3>=HEX && LA32_3<=DECIMAL)||LA32_3==STRING||(LA32_3>=BOOL && LA32_3<=NULL)||(LA32_3>=DECR && LA32_3<=INCR)||LA32_3==LESS||LA32_3==LEFT_PAREN||LA32_3==LEFT_SQUARE||(LA32_3>=NEGATION && LA32_3<=TILDE)||(LA32_3>=MINUS && LA32_3<=PLUS)||LA32_3==ID) ) {
                        alt32=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 2, input);

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
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:8: LESS LESS
                    {
                    match(input,LESS,FOLLOW_LESS_in_shiftOp903); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_shiftOp905); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:20: GREATER GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp909); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp911); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp913); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:163:46: GREATER GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp917); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_shiftOp919); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:166:1: additiveExpression : multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:3: ( multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:7: multiplicativeExpression ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression938);
            multiplicativeExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*
            loop33:
            do {
                int alt33=2;
                alt33 = dfa33.predict(input);
                switch (alt33) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:34: ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression
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

            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression957);
            	    multiplicativeExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "additiveExpression"


    // $ANTLR start "multiplicativeExpression"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:170:1: multiplicativeExpression : unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:3: ( unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:7: unaryExpression ( ( STAR | DIV | MOD ) unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression977);
            unaryExpression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:23: ( ( STAR | DIV | MOD ) unaryExpression )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=MOD && LA34_0<=STAR)||LA34_0==DIV) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:171:25: ( STAR | DIV | MOD ) unaryExpression
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

            	    pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression995);
            	    unaryExpression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop34;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:174:1: unaryExpression : ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus );
    public final void unaryExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:175:5: ( PLUS unaryExpression | MINUS unaryExpression | INCR primary | DECR primary | unaryExpressionNotPlusMinus )
            int alt35=5;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt35=1;
                }
                break;
            case MINUS:
                {
                alt35=2;
                }
                break;
            case INCR:
                {
                alt35=3;
                }
                break;
            case DECR:
                {
                alt35=4;
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
                alt35=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:175:9: PLUS unaryExpression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unaryExpression1017); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1019);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:176:7: MINUS unaryExpression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unaryExpression1027); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpression1029);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:177:9: INCR primary
                    {
                    match(input,INCR,FOLLOW_INCR_in_unaryExpression1039); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression1041);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:178:9: DECR primary
                    {
                    match(input,DECR,FOLLOW_DECR_in_unaryExpression1051); if (state.failed) return ;
                    pushFollow(FOLLOW_primary_in_unaryExpression1053);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:179:9: unaryExpressionNotPlusMinus
                    {
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1063);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:182:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );
    public final void unaryExpressionNotPlusMinus() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:5: ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? )
            int alt38=4;
            alt38 = dfa38.predict(input);
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:183:9: TILDE unaryExpression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1082); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1084);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:184:8: NEGATION unaryExpression
                    {
                    match(input,NEGATION,FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1093); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1095);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:9: ( castExpression )=> castExpression
                    {
                    pushFollow(FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1109);
                    castExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:9: primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    {
                    pushFollow(FOLLOW_primary_in_unaryExpressionNotPlusMinus1119);
                    primary();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:17: ( ( selector )=> selector )*
                    loop36:
                    do {
                        int alt36=2;
                        alt36 = dfa36.predict(input);
                        switch (alt36) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:18: ( selector )=> selector
                    	    {
                    	    pushFollow(FOLLOW_selector_in_unaryExpressionNotPlusMinus1126);
                    	    selector();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop36;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?
                    int alt37=2;
                    alt37 = dfa37.predict(input);
                    switch (alt37) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:42: ( INCR | DECR )=> ( INCR | DECR )
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:189:1: castExpression : ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus );
    public final void castExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:5: ( ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression | ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==LEFT_PAREN) ) {
                int LA39_1 = input.LA(2);

                if ( (synpred16_DRLExpressions()) ) {
                    alt39=1;
                }
                else if ( (synpred17_DRLExpressions()) ) {
                    alt39=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 39, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:8: ( LEFT_PAREN primitiveType )=> LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1174); if (state.failed) return ;
                    pushFollow(FOLLOW_primitiveType_in_castExpression1176);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1178); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpression_in_castExpression1180);
                    unaryExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:8: ( LEFT_PAREN type )=> LEFT_PAREN type RIGHT_PAREN unaryExpressionNotPlusMinus
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_castExpression1197); if (state.failed) return ;
                    pushFollow(FOLLOW_type_in_castExpression1199);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_castExpression1201); if (state.failed) return ;
                    pushFollow(FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1203);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:194:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );
    public final void primitiveType() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:5: ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key )
            int alt40=8;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:195:7: boolean_key
                    {
                    pushFollow(FOLLOW_boolean_key_in_primitiveType1224);
                    boolean_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:196:7: char_key
                    {
                    pushFollow(FOLLOW_char_key_in_primitiveType1232);
                    char_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:197:7: byte_key
                    {
                    pushFollow(FOLLOW_byte_key_in_primitiveType1240);
                    byte_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:198:7: short_key
                    {
                    pushFollow(FOLLOW_short_key_in_primitiveType1248);
                    short_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:199:7: int_key
                    {
                    pushFollow(FOLLOW_int_key_in_primitiveType1256);
                    int_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:200:7: long_key
                    {
                    pushFollow(FOLLOW_long_key_in_primitiveType1264);
                    long_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:201:7: float_key
                    {
                    pushFollow(FOLLOW_float_key_in_primitiveType1272);
                    float_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:202:7: double_key
                    {
                    pushFollow(FOLLOW_double_key_in_primitiveType1280);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:205:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );
    public final void primary() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:5: ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? )
            int alt45=9;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:7: ( parExpression )=> parExpression
                    {
                    pushFollow(FOLLOW_parExpression_in_primary1302);
                    parExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:9: ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments )
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_primary1317);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:63: ( explicitGenericInvocationSuffix | this_key arguments )
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==ID) ) {
                        int LA41_1 = input.LA(2);

                        if ( (!((((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))))) ) {
                            alt41=1;
                        }
                        else if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                            alt41=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 41, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 41, 0, input);

                        throw nvae;
                    }
                    switch (alt41) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:64: explicitGenericInvocationSuffix
                            {
                            pushFollow(FOLLOW_explicitGenericInvocationSuffix_in_primary1320);
                            explicitGenericInvocationSuffix();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:98: this_key arguments
                            {
                            pushFollow(FOLLOW_this_key_in_primary1324);
                            this_key();

                            state._fsp--;
                            if (state.failed) return ;
                            pushFollow(FOLLOW_arguments_in_primary1326);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:9: ( literal )=> literal
                    {
                    pushFollow(FOLLOW_literal_in_primary1342);
                    literal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:210:9: ( super_key )=> super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_primary1362);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_primary1364);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:9: ( new_key )=> new_key creator
                    {
                    pushFollow(FOLLOW_new_key_in_primary1379);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_creator_in_primary1381);
                    creator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:9: ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key
                    {
                    pushFollow(FOLLOW_primitiveType_in_primary1396);
                    primitiveType();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:41: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);

                        if ( (LA42_0==LEFT_SQUARE) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:42: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_primary1399); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_primary1401); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop42;
                        }
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_primary1405); if (state.failed) return ;
                    pushFollow(FOLLOW_class_key_in_primary1407);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:9: ( inlineMapExpression )=> inlineMapExpression
                    {
                    pushFollow(FOLLOW_inlineMapExpression_in_primary1427);
                    inlineMapExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:9: ( inlineListExpression )=> inlineListExpression
                    {
                    pushFollow(FOLLOW_inlineListExpression_in_primary1442);
                    inlineListExpression();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:9: ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )?
                    {
                    match(input,ID,FOLLOW_ID_in_primary1456); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:18: ( ( DOT ID )=> DOT ID )*
                    loop43:
                    do {
                        int alt43=2;
                        int LA43_0 = input.LA(1);

                        if ( (LA43_0==DOT) ) {
                            int LA43_2 = input.LA(2);

                            if ( (LA43_2==ID) ) {
                                int LA43_3 = input.LA(3);

                                if ( (synpred27_DRLExpressions()) ) {
                                    alt43=1;
                                }


                            }


                        }


                        switch (alt43) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:19: ( DOT ID )=> DOT ID
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_primary1465); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_primary1467); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop43;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:38: ( ( identifierSuffix )=> identifierSuffix )?
                    int alt44=2;
                    alt44 = dfa44.predict(input);
                    switch (alt44) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:39: ( identifierSuffix )=> identifierSuffix
                            {
                            pushFollow(FOLLOW_identifierSuffix_in_primary1476);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:219:1: inlineListExpression : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void inlineListExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:9: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineListExpression1497); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:21: ( expressionList )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==FLOAT||(LA46_0>=HEX && LA46_0<=DECIMAL)||LA46_0==STRING||(LA46_0>=BOOL && LA46_0<=NULL)||(LA46_0>=DECR && LA46_0<=INCR)||LA46_0==LESS||LA46_0==LEFT_PAREN||LA46_0==LEFT_SQUARE||(LA46_0>=NEGATION && LA46_0<=TILDE)||(LA46_0>=MINUS && LA46_0<=PLUS)||LA46_0==ID) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:220:21: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_inlineListExpression1499);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineListExpression1502); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:223:1: inlineMapExpression : LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE ;
    public final void inlineMapExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:5: ( LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:7: LEFT_SQUARE ( mapExpressionList )+ RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_inlineMapExpression1524); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:19: ( mapExpressionList )+
            int cnt47=0;
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==FLOAT||(LA47_0>=HEX && LA47_0<=DECIMAL)||LA47_0==STRING||(LA47_0>=BOOL && LA47_0<=NULL)||(LA47_0>=DECR && LA47_0<=INCR)||LA47_0==LESS||LA47_0==LEFT_PAREN||LA47_0==LEFT_SQUARE||(LA47_0>=NEGATION && LA47_0<=TILDE)||(LA47_0>=MINUS && LA47_0<=PLUS)||LA47_0==ID) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:224:19: mapExpressionList
            	    {
            	    pushFollow(FOLLOW_mapExpressionList_in_inlineMapExpression1526);
            	    mapExpressionList();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt47 >= 1 ) break loop47;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(47, input);
                        throw eee;
                }
                cnt47++;
            } while (true);

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1529); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:227:1: mapExpressionList : mapEntry ( COMMA mapEntry )* ;
    public final void mapExpressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:5: ( mapEntry ( COMMA mapEntry )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:7: mapEntry ( COMMA mapEntry )*
            {
            pushFollow(FOLLOW_mapEntry_in_mapExpressionList1546);
            mapEntry();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:16: ( COMMA mapEntry )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==COMMA) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:228:17: COMMA mapEntry
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_mapExpressionList1549); if (state.failed) return ;
            	    pushFollow(FOLLOW_mapEntry_in_mapExpressionList1551);
            	    mapEntry();

            	    state._fsp--;
            	    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "mapExpressionList"


    // $ANTLR start "mapEntry"
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:231:1: mapEntry : expression COLON expression ;
    public final void mapEntry() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:5: ( expression COLON expression )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:232:7: expression COLON expression
            {
            pushFollow(FOLLOW_expression_in_mapEntry1574);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_mapEntry1576); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_mapEntry1578);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:235:1: parExpression : LEFT_PAREN expression RIGHT_PAREN ;
    public final void parExpression() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:236:5: ( LEFT_PAREN expression RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:236:7: LEFT_PAREN expression RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_parExpression1595); if (state.failed) return ;
            pushFollow(FOLLOW_expression_in_parExpression1597);
            expression();

            state._fsp--;
            if (state.failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_parExpression1599); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:239:1: identifierSuffix : ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments );
    public final void identifierSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:5: ( ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key | ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+ | arguments )
            int alt51=3;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==LEFT_SQUARE) ) {
                int LA51_1 = input.LA(2);

                if ( (LA51_1==RIGHT_SQUARE) && (synpred29_DRLExpressions())) {
                    alt51=1;
                }
                else if ( (LA51_1==FLOAT||(LA51_1>=HEX && LA51_1<=DECIMAL)||LA51_1==STRING||(LA51_1>=BOOL && LA51_1<=NULL)||(LA51_1>=DECR && LA51_1<=INCR)||LA51_1==LESS||LA51_1==LEFT_PAREN||LA51_1==LEFT_SQUARE||(LA51_1>=NEGATION && LA51_1<=TILDE)||(LA51_1>=MINUS && LA51_1<=PLUS)||LA51_1==ID) ) {
                    alt51=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA51_0==LEFT_PAREN) ) {
                alt51=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:7: ( LEFT_SQUARE RIGHT_SQUARE )=> ( LEFT_SQUARE RIGHT_SQUARE )+ DOT class_key
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:35: ( LEFT_SQUARE RIGHT_SQUARE )+
                    int cnt49=0;
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==LEFT_SQUARE) ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:36: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1623); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1625); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt49 >= 1 ) break loop49;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(49, input);
                                throw eee;
                        }
                        cnt49++;
                    } while (true);

                    match(input,DOT,FOLLOW_DOT_in_identifierSuffix1629); if (state.failed) return ;
                    pushFollow(FOLLOW_class_key_in_identifierSuffix1631);
                    class_key();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    {
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+
                    int cnt50=0;
                    loop50:
                    do {
                        int alt50=2;
                        alt50 = dfa50.predict(input);
                        switch (alt50) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:8: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_identifierSuffix1646); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_identifierSuffix1648);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_identifierSuffix1650); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt50 >= 1 ) break loop50;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(50, input);
                                throw eee;
                        }
                        cnt50++;
                    } while (true);


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:242:9: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_identifierSuffix1663);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:250:1: creator : ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) ;
    public final void creator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:5: ( ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:7: ( nonWildcardTypeArguments )? createdName ( arrayCreatorRest | classCreatorRest )
            {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:7: ( nonWildcardTypeArguments )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==LESS) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:251:7: nonWildcardTypeArguments
                    {
                    pushFollow(FOLLOW_nonWildcardTypeArguments_in_creator1686);
                    nonWildcardTypeArguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_createdName_in_creator1689);
            createdName();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:9: ( arrayCreatorRest | classCreatorRest )
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==LEFT_SQUARE) ) {
                alt53=1;
            }
            else if ( (LA53_0==LEFT_PAREN) ) {
                alt53=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:10: arrayCreatorRest
                    {
                    pushFollow(FOLLOW_arrayCreatorRest_in_creator1700);
                    arrayCreatorRest();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:252:29: classCreatorRest
                    {
                    pushFollow(FOLLOW_classCreatorRest_in_creator1704);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:255:1: createdName : ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType );
    public final void createdName() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:5: ( ID ( typeArguments )? ( DOT ID ( typeArguments )? )* | primitiveType )
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ID) && ((!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))||!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))))) {
                int LA57_1 = input.LA(2);

                if ( (!(((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))))) ) {
                    alt57=1;
                }
                else if ( ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))) ) {
                    alt57=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:7: ID ( typeArguments )? ( DOT ID ( typeArguments )? )*
                    {
                    match(input,ID,FOLLOW_ID_in_createdName1722); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:10: ( typeArguments )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==LESS) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:256:10: typeArguments
                            {
                            pushFollow(FOLLOW_typeArguments_in_createdName1724);
                            typeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:257:9: ( DOT ID ( typeArguments )? )*
                    loop56:
                    do {
                        int alt56=2;
                        int LA56_0 = input.LA(1);

                        if ( (LA56_0==DOT) ) {
                            alt56=1;
                        }


                        switch (alt56) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:257:11: DOT ID ( typeArguments )?
                    	    {
                    	    match(input,DOT,FOLLOW_DOT_in_createdName1737); if (state.failed) return ;
                    	    match(input,ID,FOLLOW_ID_in_createdName1739); if (state.failed) return ;
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:257:18: ( typeArguments )?
                    	    int alt55=2;
                    	    int LA55_0 = input.LA(1);

                    	    if ( (LA55_0==LESS) ) {
                    	        alt55=1;
                    	    }
                    	    switch (alt55) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:257:18: typeArguments
                    	            {
                    	            pushFollow(FOLLOW_typeArguments_in_createdName1741);
                    	            typeArguments();

                    	            state._fsp--;
                    	            if (state.failed) return ;

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop56;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:258:11: primitiveType
                    {
                    pushFollow(FOLLOW_primitiveType_in_createdName1756);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:261:1: innerCreator : {...}? => ID classCreatorRest ;
    public final void innerCreator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:5: ({...}? => ID classCreatorRest )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:262:7: {...}? => ID classCreatorRest
            {
            if ( !((!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "innerCreator", "!(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            match(input,ID,FOLLOW_ID_in_innerCreator1776); if (state.failed) return ;
            pushFollow(FOLLOW_classCreatorRest_in_innerCreator1778);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:265:1: arrayCreatorRest : LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) ;
    public final void arrayCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:5: ( LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* ) )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:266:9: LEFT_SQUARE ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1797); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:5: ( RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer | expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )* )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==RIGHT_SQUARE) ) {
                alt61=1;
            }
            else if ( (LA61_0==FLOAT||(LA61_0>=HEX && LA61_0<=DECIMAL)||LA61_0==STRING||(LA61_0>=BOOL && LA61_0<=NULL)||(LA61_0>=DECR && LA61_0<=INCR)||LA61_0==LESS||LA61_0==LEFT_PAREN||LA61_0==LEFT_SQUARE||(LA61_0>=NEGATION && LA61_0<=TILDE)||(LA61_0>=MINUS && LA61_0<=PLUS)||LA61_0==ID) ) {
                alt61=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:9: RIGHT_SQUARE ( LEFT_SQUARE RIGHT_SQUARE )* arrayInitializer
                    {
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1807); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:22: ( LEFT_SQUARE RIGHT_SQUARE )*
                    loop58:
                    do {
                        int alt58=2;
                        int LA58_0 = input.LA(1);

                        if ( (LA58_0==LEFT_SQUARE) ) {
                            alt58=1;
                        }


                        switch (alt58) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:267:23: LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1810); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1812); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);

                    pushFollow(FOLLOW_arrayInitializer_in_arrayCreatorRest1816);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:13: expression RIGHT_SQUARE ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )* ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    {
                    pushFollow(FOLLOW_expression_in_arrayCreatorRest1830);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1832); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*
                    loop59:
                    do {
                        int alt59=2;
                        alt59 = dfa59.predict(input);
                        switch (alt59) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:38: {...}? => LEFT_SQUARE expression RIGHT_SQUARE
                    	    {
                    	    if ( !((!helper.validateLT(2,"]"))) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "arrayCreatorRest", "!helper.validateLT(2,\"]\")");
                    	    }
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1837); if (state.failed) return ;
                    	    pushFollow(FOLLOW_expression_in_arrayCreatorRest1839);
                    	    expression();

                    	    state._fsp--;
                    	    if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1841); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop59;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:106: ( ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( (LA60_0==LEFT_SQUARE) ) {
                            int LA60_2 = input.LA(2);

                            if ( (LA60_2==RIGHT_SQUARE) ) {
                                int LA60_3 = input.LA(3);

                                if ( (synpred31_DRLExpressions()) ) {
                                    alt60=1;
                                }


                            }


                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:107: ( LEFT_SQUARE RIGHT_SQUARE )=> LEFT_SQUARE RIGHT_SQUARE
                    	    {
                    	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1853); if (state.failed) return ;
                    	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1855); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:272:1: variableInitializer : ( arrayInitializer | expression );
    public final void variableInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:5: ( arrayInitializer | expression )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LEFT_CURLY) ) {
                alt62=1;
            }
            else if ( (LA62_0==FLOAT||(LA62_0>=HEX && LA62_0<=DECIMAL)||LA62_0==STRING||(LA62_0>=BOOL && LA62_0<=NULL)||(LA62_0>=DECR && LA62_0<=INCR)||LA62_0==LESS||LA62_0==LEFT_PAREN||LA62_0==LEFT_SQUARE||(LA62_0>=NEGATION && LA62_0<=TILDE)||(LA62_0>=MINUS && LA62_0<=PLUS)||LA62_0==ID) ) {
                alt62=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:273:7: arrayInitializer
                    {
                    pushFollow(FOLLOW_arrayInitializer_in_variableInitializer1884);
                    arrayInitializer();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:274:13: expression
                    {
                    pushFollow(FOLLOW_expression_in_variableInitializer1898);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:277:1: arrayInitializer : LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY ;
    public final void arrayInitializer() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:5: ( LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:7: LEFT_CURLY ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )? RIGHT_CURLY
            {
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_arrayInitializer1915); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:18: ( variableInitializer ( COMMA variableInitializer )* ( COMMA )? )?
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==FLOAT||(LA65_0>=HEX && LA65_0<=DECIMAL)||LA65_0==STRING||(LA65_0>=BOOL && LA65_0<=NULL)||(LA65_0>=DECR && LA65_0<=INCR)||LA65_0==LESS||LA65_0==LEFT_PAREN||LA65_0==LEFT_SQUARE||LA65_0==LEFT_CURLY||(LA65_0>=NEGATION && LA65_0<=TILDE)||(LA65_0>=MINUS && LA65_0<=PLUS)||LA65_0==ID) ) {
                alt65=1;
            }
            switch (alt65) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:19: variableInitializer ( COMMA variableInitializer )* ( COMMA )?
                    {
                    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1918);
                    variableInitializer();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:39: ( COMMA variableInitializer )*
                    loop63:
                    do {
                        int alt63=2;
                        int LA63_0 = input.LA(1);

                        if ( (LA63_0==COMMA) ) {
                            int LA63_1 = input.LA(2);

                            if ( (LA63_1==FLOAT||(LA63_1>=HEX && LA63_1<=DECIMAL)||LA63_1==STRING||(LA63_1>=BOOL && LA63_1<=NULL)||(LA63_1>=DECR && LA63_1<=INCR)||LA63_1==LESS||LA63_1==LEFT_PAREN||LA63_1==LEFT_SQUARE||LA63_1==LEFT_CURLY||(LA63_1>=NEGATION && LA63_1<=TILDE)||(LA63_1>=MINUS && LA63_1<=PLUS)||LA63_1==ID) ) {
                                alt63=1;
                            }


                        }


                        switch (alt63) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:40: COMMA variableInitializer
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer1921); if (state.failed) return ;
                    	    pushFollow(FOLLOW_variableInitializer_in_arrayInitializer1923);
                    	    variableInitializer();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop63;
                        }
                    } while (true);

                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:68: ( COMMA )?
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==COMMA) ) {
                        alt64=1;
                    }
                    switch (alt64) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:278:69: COMMA
                            {
                            match(input,COMMA,FOLLOW_COMMA_in_arrayInitializer1928); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_arrayInitializer1935); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:281:1: classCreatorRest : arguments ;
    public final void classCreatorRest() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:5: ( arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:282:7: arguments
            {
            pushFollow(FOLLOW_arguments_in_classCreatorRest1952);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:285:1: explicitGenericInvocation : nonWildcardTypeArguments arguments ;
    public final void explicitGenericInvocation() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:5: ( nonWildcardTypeArguments arguments )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:286:7: nonWildcardTypeArguments arguments
            {
            pushFollow(FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1970);
            nonWildcardTypeArguments();

            state._fsp--;
            if (state.failed) return ;
            pushFollow(FOLLOW_arguments_in_explicitGenericInvocation1972);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:289:1: nonWildcardTypeArguments : LESS typeList GREATER ;
    public final void nonWildcardTypeArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:290:5: ( LESS typeList GREATER )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:290:7: LESS typeList GREATER
            {
            match(input,LESS,FOLLOW_LESS_in_nonWildcardTypeArguments1989); if (state.failed) return ;
            pushFollow(FOLLOW_typeList_in_nonWildcardTypeArguments1991);
            typeList();

            state._fsp--;
            if (state.failed) return ;
            match(input,GREATER,FOLLOW_GREATER_in_nonWildcardTypeArguments1993); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:293:1: explicitGenericInvocationSuffix : ( super_key superSuffix | ID arguments );
    public final void explicitGenericInvocationSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:5: ( super_key superSuffix | ID arguments )
            int alt66=2;
            int LA66_0 = input.LA(1);

            if ( (LA66_0==ID) ) {
                int LA66_1 = input.LA(2);

                if ( (((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                    alt66=1;
                }
                else if ( (true) ) {
                    alt66=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:294:7: super_key superSuffix
                    {
                    pushFollow(FOLLOW_super_key_in_explicitGenericInvocationSuffix2010);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2012);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:295:10: ID arguments
                    {
                    match(input,ID,FOLLOW_ID_in_explicitGenericInvocationSuffix2023); if (state.failed) return ;
                    pushFollow(FOLLOW_arguments_in_explicitGenericInvocationSuffix2025);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:298:1: selector : ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE );
    public final void selector() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:5: ( ( DOT super_key )=> DOT super_key superSuffix | ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator | ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )? | ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )
            int alt69=4;
            int LA69_0 = input.LA(1);

            if ( (LA69_0==DOT) ) {
                int LA69_1 = input.LA(2);

                if ( (synpred32_DRLExpressions()) ) {
                    alt69=1;
                }
                else if ( (synpred33_DRLExpressions()) ) {
                    alt69=2;
                }
                else if ( (synpred34_DRLExpressions()) ) {
                    alt69=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 69, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA69_0==LEFT_SQUARE) && (synpred36_DRLExpressions())) {
                alt69=4;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:9: ( DOT super_key )=> DOT super_key superSuffix
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2050); if (state.failed) return ;
                    pushFollow(FOLLOW_super_key_in_selector2052);
                    super_key();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_superSuffix_in_selector2054);
                    superSuffix();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:9: ( DOT new_key )=> DOT new_key ( nonWildcardTypeArguments )? innerCreator
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2070); if (state.failed) return ;
                    pushFollow(FOLLOW_new_key_in_selector2072);
                    new_key();

                    state._fsp--;
                    if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:36: ( nonWildcardTypeArguments )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==LESS) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:37: nonWildcardTypeArguments
                            {
                            pushFollow(FOLLOW_nonWildcardTypeArguments_in_selector2075);
                            nonWildcardTypeArguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_innerCreator_in_selector2079);
                    innerCreator();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:9: ( DOT ID )=> DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_selector2095); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_selector2097); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:26: ( ( LEFT_PAREN )=> arguments )?
                    int alt68=2;
                    alt68 = dfa68.predict(input);
                    switch (alt68) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:27: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_selector2106);
                            arguments();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:9: ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_selector2127); if (state.failed) return ;
                    pushFollow(FOLLOW_expression_in_selector2129);
                    expression();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_selector2131); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:306:1: superSuffix : ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? );
    public final void superSuffix() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:5: ( arguments | DOT ID ( ( LEFT_PAREN )=> arguments )? )
            int alt71=2;
            int LA71_0 = input.LA(1);

            if ( (LA71_0==LEFT_PAREN) ) {
                alt71=1;
            }
            else if ( (LA71_0==DOT) ) {
                alt71=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:307:7: arguments
                    {
                    pushFollow(FOLLOW_arguments_in_superSuffix2148);
                    arguments();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:10: DOT ID ( ( LEFT_PAREN )=> arguments )?
                    {
                    match(input,DOT,FOLLOW_DOT_in_superSuffix2159); if (state.failed) return ;
                    match(input,ID,FOLLOW_ID_in_superSuffix2161); if (state.failed) return ;
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:17: ( ( LEFT_PAREN )=> arguments )?
                    int alt70=2;
                    alt70 = dfa70.predict(input);
                    switch (alt70) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:18: ( LEFT_PAREN )=> arguments
                            {
                            pushFollow(FOLLOW_arguments_in_superSuffix2170);
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:311:1: squareArguments : LEFT_SQUARE ( expressionList )? RIGHT_SQUARE ;
    public final void squareArguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:5: ( LEFT_SQUARE ( expressionList )? RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:7: LEFT_SQUARE ( expressionList )? RIGHT_SQUARE
            {
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_squareArguments2193); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:19: ( expressionList )?
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==FLOAT||(LA72_0>=HEX && LA72_0<=DECIMAL)||LA72_0==STRING||(LA72_0>=BOOL && LA72_0<=NULL)||(LA72_0>=DECR && LA72_0<=INCR)||LA72_0==LESS||LA72_0==LEFT_PAREN||LA72_0==LEFT_SQUARE||(LA72_0>=NEGATION && LA72_0<=TILDE)||(LA72_0>=MINUS && LA72_0<=PLUS)||LA72_0==ID) ) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:312:19: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_squareArguments2195);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_squareArguments2198); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:315:1: arguments : LEFT_PAREN ( expressionList )? RIGHT_PAREN ;
    public final void arguments() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:5: ( LEFT_PAREN ( expressionList )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:7: LEFT_PAREN ( expressionList )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_arguments2215); if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:18: ( expressionList )?
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( (LA73_0==FLOAT||(LA73_0>=HEX && LA73_0<=DECIMAL)||LA73_0==STRING||(LA73_0>=BOOL && LA73_0<=NULL)||(LA73_0>=DECR && LA73_0<=INCR)||LA73_0==LESS||LA73_0==LEFT_PAREN||LA73_0==LEFT_SQUARE||(LA73_0>=NEGATION && LA73_0<=TILDE)||(LA73_0>=MINUS && LA73_0<=PLUS)||LA73_0==ID) ) {
                alt73=1;
            }
            switch (alt73) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:316:18: expressionList
                    {
                    pushFollow(FOLLOW_expressionList_in_arguments2217);
                    expressionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_arguments2220); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:319:1: expressionList : expression ( COMMA expression )* ;
    public final void expressionList() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:3: ( expression ( COMMA expression )* )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:7: expression ( COMMA expression )*
            {
            pushFollow(FOLLOW_expression_in_expressionList2237);
            expression();

            state._fsp--;
            if (state.failed) return ;
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:18: ( COMMA expression )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);

                if ( (LA74_0==COMMA) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:320:19: COMMA expression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionList2240); if (state.failed) return ;
            	    pushFollow(FOLLOW_expression_in_expressionList2242);
            	    expression();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop74;
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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:323:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );
    public final void assignmentOperator() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:5: ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN )
            int alt75=12;
            alt75 = dfa75.predict(input);
            switch (alt75) {
                case 1 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:324:9: EQUALS_ASSIGN
                    {
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2261); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:325:7: PLUS_ASSIGN
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_assignmentOperator2269); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:326:7: MINUS_ASSIGN
                    {
                    match(input,MINUS_ASSIGN,FOLLOW_MINUS_ASSIGN_in_assignmentOperator2277); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:327:7: MULT_ASSIGN
                    {
                    match(input,MULT_ASSIGN,FOLLOW_MULT_ASSIGN_in_assignmentOperator2285); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:328:7: DIV_ASSIGN
                    {
                    match(input,DIV_ASSIGN,FOLLOW_DIV_ASSIGN_in_assignmentOperator2293); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:329:7: AND_ASSIGN
                    {
                    match(input,AND_ASSIGN,FOLLOW_AND_ASSIGN_in_assignmentOperator2301); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:330:7: OR_ASSIGN
                    {
                    match(input,OR_ASSIGN,FOLLOW_OR_ASSIGN_in_assignmentOperator2309); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:331:7: XOR_ASSIGN
                    {
                    match(input,XOR_ASSIGN,FOLLOW_XOR_ASSIGN_in_assignmentOperator2317); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:332:7: MOD_ASSIGN
                    {
                    match(input,MOD_ASSIGN,FOLLOW_MOD_ASSIGN_in_assignmentOperator2325); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:333:7: LESS LESS EQUALS_ASSIGN
                    {
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2333); if (state.failed) return ;
                    match(input,LESS,FOLLOW_LESS_in_assignmentOperator2335); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2337); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:7: ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2354); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2356); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2358); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2360); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:7: ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2375); if (state.failed) return ;
                    match(input,GREATER,FOLLOW_GREATER_in_assignmentOperator2377); if (state.failed) return ;
                    match(input,EQUALS_ASSIGN,FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2379); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:341:1: extends_key : {...}? =>id= ID ;
    public final void extends_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:342:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "extends_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.EXTENDS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_extends_key2409); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:345:1: super_key : {...}? =>id= ID ;
    public final void super_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:346:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "super_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_super_key2436); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:349:1: instanceof_key : {...}? =>id= ID ;
    public final void instanceof_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:350:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:350:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "instanceof_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_instanceof_key2463); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:353:1: boolean_key : {...}? =>id= ID ;
    public final void boolean_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:354:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "boolean_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_boolean_key2490); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:357:1: char_key : {...}? =>id= ID ;
    public final void char_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:358:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "char_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_char_key2517); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:361:1: byte_key : {...}? =>id= ID ;
    public final void byte_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:362:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "byte_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.BYTE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_byte_key2544); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:365:1: short_key : {...}? =>id= ID ;
    public final void short_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:366:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:366:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "short_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_short_key2571); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:369:1: int_key : {...}? =>id= ID ;
    public final void int_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:370:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:370:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "int_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.INT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_int_key2598); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:373:1: float_key : {...}? =>id= ID ;
    public final void float_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:374:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:374:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "float_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_float_key2625); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:377:1: long_key : {...}? =>id= ID ;
    public final void long_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:378:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:378:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "long_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.LONG))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_long_key2652); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:381:1: double_key : {...}? =>id= ID ;
    public final void double_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:382:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:382:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "double_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_double_key2679); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:385:1: void_key : {...}? =>id= ID ;
    public final void void_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:386:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:386:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.VOID)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "void_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.VOID))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_void_key2706); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:389:1: this_key : {...}? =>id= ID ;
    public final void this_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:390:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:390:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.THIS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "this_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.THIS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_this_key2733); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:393:1: class_key : {...}? =>id= ID ;
    public final void class_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:394:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:394:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.CLASS)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "class_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.CLASS))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_class_key2760); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:397:1: new_key : {...}? =>id= ID ;
    public final void new_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:398:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NEW)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "new_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NEW))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_new_key2787); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:401:1: not_key : {...}? =>id= ID ;
    public final void not_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:402:5: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:402:12: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "not_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.NOT))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_not_key2814); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:405:1: in_key : {...}? =>id= ID ;
    public final void in_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:406:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:406:10: {...}? =>id= ID
            {
            if ( !(((helper.validateIdentifierKey(DroolsSoftKeywords.IN)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "in_key", "(helper.validateIdentifierKey(DroolsSoftKeywords.IN))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_in_key2839); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:409:1: operator_key : {...}? =>id= ID ;
    public final void operator_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:410:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:410:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(false)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "operator_key", "(helper.isPluggableEvaluator(false))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_operator_key2862); if (state.failed) return ;

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
    // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:413:1: neg_operator_key : {...}? =>id= ID ;
    public final void neg_operator_key() throws RecognitionException {
        Token id=null;

        try {
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:414:3: ({...}? =>id= ID )
            // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:414:10: {...}? =>id= ID
            {
            if ( !(((helper.isPluggableEvaluator(true)))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "neg_operator_key", "(helper.isPluggableEvaluator(true))");
            }
            id=(Token)match(input,ID,FOLLOW_ID_in_neg_operator_key2885); if (state.failed) return ;

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
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:8: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:9: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred1_DRLExpressions209);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_DRLExpressions

    // $ANTLR start synpred2_DRLExpressions
    public final void synpred2_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:44: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:70:45: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions220); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions222); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_DRLExpressions

    // $ANTLR start synpred3_DRLExpressions
    public final void synpred3_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:13: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:14: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred3_DRLExpressions246);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_DRLExpressions

    // $ANTLR start synpred4_DRLExpressions
    public final void synpred4_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:55: ( typeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:56: typeArguments
        {
        pushFollow(FOLLOW_typeArguments_in_synpred4_DRLExpressions260);
        typeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_DRLExpressions

    // $ANTLR start synpred5_DRLExpressions
    public final void synpred5_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:92: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:71:93: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions272); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions274); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_DRLExpressions

    // $ANTLR start synpred6_DRLExpressions
    public final void synpred6_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:30: ( assignmentOperator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:94:31: assignmentOperator
        {
        pushFollow(FOLLOW_assignmentOperator_in_synpred6_DRLExpressions404);
        assignmentOperator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_DRLExpressions

    // $ANTLR start synpred7_DRLExpressions
    public final void synpred7_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:27: ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:122:28: ( DOUBLE_PIPE | DOUBLE_AMPER ) operator
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

        pushFollow(FOLLOW_operator_in_synpred7_DRLExpressions594);
        operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_DRLExpressions

    // $ANTLR start synpred8_DRLExpressions
    public final void synpred8_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:23: ( relationalOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:138:24: relationalOp
        {
        pushFollow(FOLLOW_relationalOp_in_synpred8_DRLExpressions723);
        relationalOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_DRLExpressions

    // $ANTLR start synpred9_DRLExpressions
    public final void synpred9_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:33: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:153:34: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred9_DRLExpressions826);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_DRLExpressions

    // $ANTLR start synpred10_DRLExpressions
    public final void synpred10_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:22: ( squareArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:154:23: squareArguments
        {
        pushFollow(FOLLOW_squareArguments_in_synpred10_DRLExpressions845);
        squareArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_DRLExpressions

    // $ANTLR start synpred11_DRLExpressions
    public final void synpred11_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:26: ( shiftOp )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:159:27: shiftOp
        {
        pushFollow(FOLLOW_shiftOp_in_synpred11_DRLExpressions877);
        shiftOp();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_DRLExpressions

    // $ANTLR start synpred12_DRLExpressions
    public final void synpred12_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:167:34: ( PLUS | MINUS )
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
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:9: ( castExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:185:10: castExpression
        {
        pushFollow(FOLLOW_castExpression_in_synpred13_DRLExpressions1106);
        castExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_DRLExpressions

    // $ANTLR start synpred14_DRLExpressions
    public final void synpred14_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:18: ( selector )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:19: selector
        {
        pushFollow(FOLLOW_selector_in_synpred14_DRLExpressions1123);
        selector();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_DRLExpressions

    // $ANTLR start synpred15_DRLExpressions
    public final void synpred15_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:186:42: ( INCR | DECR )
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
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:8: ( LEFT_PAREN primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:190:9: LEFT_PAREN primitiveType
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1167); if (state.failed) return ;
        pushFollow(FOLLOW_primitiveType_in_synpred16_DRLExpressions1169);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_DRLExpressions

    // $ANTLR start synpred17_DRLExpressions
    public final void synpred17_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:8: ( LEFT_PAREN type )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:191:9: LEFT_PAREN type
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1190); if (state.failed) return ;
        pushFollow(FOLLOW_type_in_synpred17_DRLExpressions1192);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_DRLExpressions

    // $ANTLR start synpred18_DRLExpressions
    public final void synpred18_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:7: ( parExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:206:8: parExpression
        {
        pushFollow(FOLLOW_parExpression_in_synpred18_DRLExpressions1298);
        parExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_DRLExpressions

    // $ANTLR start synpred19_DRLExpressions
    public final void synpred19_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:9: ( nonWildcardTypeArguments )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:207:10: nonWildcardTypeArguments
        {
        pushFollow(FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1313);
        nonWildcardTypeArguments();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred19_DRLExpressions

    // $ANTLR start synpred20_DRLExpressions
    public final void synpred20_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:9: ( literal )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:208:10: literal
        {
        pushFollow(FOLLOW_literal_in_synpred20_DRLExpressions1338);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred20_DRLExpressions

    // $ANTLR start synpred21_DRLExpressions
    public final void synpred21_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:210:9: ( super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:210:10: super_key
        {
        pushFollow(FOLLOW_super_key_in_synpred21_DRLExpressions1358);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred21_DRLExpressions

    // $ANTLR start synpred22_DRLExpressions
    public final void synpred22_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:9: ( new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:211:10: new_key
        {
        pushFollow(FOLLOW_new_key_in_synpred22_DRLExpressions1375);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_DRLExpressions

    // $ANTLR start synpred23_DRLExpressions
    public final void synpred23_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:9: ( primitiveType )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:212:10: primitiveType
        {
        pushFollow(FOLLOW_primitiveType_in_synpred23_DRLExpressions1392);
        primitiveType();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_DRLExpressions

    // $ANTLR start synpred24_DRLExpressions
    public final void synpred24_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:9: ( inlineMapExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:214:10: inlineMapExpression
        {
        pushFollow(FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1423);
        inlineMapExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_DRLExpressions

    // $ANTLR start synpred25_DRLExpressions
    public final void synpred25_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:9: ( inlineListExpression )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:215:10: inlineListExpression
        {
        pushFollow(FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1438);
        inlineListExpression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_DRLExpressions

    // $ANTLR start synpred26_DRLExpressions
    public final void synpred26_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:9: ( ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:10: ID
        {
        match(input,ID,FOLLOW_ID_in_synpred26_DRLExpressions1453); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_DRLExpressions

    // $ANTLR start synpred27_DRLExpressions
    public final void synpred27_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:19: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:20: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred27_DRLExpressions1460); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred27_DRLExpressions1462); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_DRLExpressions

    // $ANTLR start synpred28_DRLExpressions
    public final void synpred28_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:39: ( identifierSuffix )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:216:40: identifierSuffix
        {
        pushFollow(FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1473);
        identifierSuffix();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_DRLExpressions

    // $ANTLR start synpred29_DRLExpressions
    public final void synpred29_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:7: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:240:8: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1617); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1619); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred29_DRLExpressions

    // $ANTLR start synpred30_DRLExpressions
    public final void synpred30_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:8: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:241:9: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1641); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred30_DRLExpressions

    // $ANTLR start synpred31_DRLExpressions
    public final void synpred31_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:107: ( LEFT_SQUARE RIGHT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:268:108: LEFT_SQUARE RIGHT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions1847); if (state.failed) return ;
        match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions1849); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred31_DRLExpressions

    // $ANTLR start synpred32_DRLExpressions
    public final void synpred32_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:9: ( DOT super_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:299:10: DOT super_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred32_DRLExpressions2045); if (state.failed) return ;
        pushFollow(FOLLOW_super_key_in_synpred32_DRLExpressions2047);
        super_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred32_DRLExpressions

    // $ANTLR start synpred33_DRLExpressions
    public final void synpred33_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:9: ( DOT new_key )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:300:10: DOT new_key
        {
        match(input,DOT,FOLLOW_DOT_in_synpred33_DRLExpressions2065); if (state.failed) return ;
        pushFollow(FOLLOW_new_key_in_synpred33_DRLExpressions2067);
        new_key();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred33_DRLExpressions

    // $ANTLR start synpred34_DRLExpressions
    public final void synpred34_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:9: ( DOT ID )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:10: DOT ID
        {
        match(input,DOT,FOLLOW_DOT_in_synpred34_DRLExpressions2090); if (state.failed) return ;
        match(input,ID,FOLLOW_ID_in_synpred34_DRLExpressions2092); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DRLExpressions

    // $ANTLR start synpred35_DRLExpressions
    public final void synpred35_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:27: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:301:28: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2101); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred35_DRLExpressions

    // $ANTLR start synpred36_DRLExpressions
    public final void synpred36_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:9: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:303:10: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2124); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred36_DRLExpressions

    // $ANTLR start synpred37_DRLExpressions
    public final void synpred37_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:18: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:308:19: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2165); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred37_DRLExpressions

    // $ANTLR start synpred38_DRLExpressions
    public final void synpred38_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:7: ( GREATER GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:334:8: GREATER GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2346); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2348); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred38_DRLExpressions2350); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred38_DRLExpressions

    // $ANTLR start synpred39_DRLExpressions
    public final void synpred39_DRLExpressions_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:7: ( GREATER GREATER )
        // /home/etirelli/workspace/jboss/drools-core/drools-compiler/src/main/resources/org/drools/lang/DRLExpressions.g:335:8: GREATER GREATER
        {
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2369); if (state.failed) return ;
        match(input,GREATER,FOLLOW_GREATER_in_synpred39_DRLExpressions2371); if (state.failed) return ;

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


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA5 dfa5 = new DFA5(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA28 dfa28 = new DFA28(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA38 dfa38 = new DFA38(this);
    protected DFA36 dfa36 = new DFA36(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA44 dfa44 = new DFA44(this);
    protected DFA50 dfa50 = new DFA50(this);
    protected DFA59 dfa59 = new DFA59(this);
    protected DFA68 dfa68 = new DFA68(this);
    protected DFA70 dfa70 = new DFA70(this);
    protected DFA75 dfa75 = new DFA75(this);
    static final String DFA4_eotS =
        "\53\uffff";
    static final String DFA4_eofS =
        "\1\2\52\uffff";
    static final String DFA4_minS =
        "\1\10\1\0\51\uffff";
    static final String DFA4_maxS =
        "\1\102\1\0\51\uffff";
    static final String DFA4_acceptS =
        "\2\uffff\1\2\47\uffff\1\1";
    static final String DFA4_specialS =
        "\1\uffff\1\0\51\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1\2",
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
            return "71:12: ( ( typeArguments )=> typeArguments )?";
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
                        if ( (synpred3_DRLExpressions()) ) {s = 42;}

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
        "\53\uffff";
    static final String DFA5_eofS =
        "\1\2\52\uffff";
    static final String DFA5_minS =
        "\1\10\1\0\51\uffff";
    static final String DFA5_maxS =
        "\1\102\1\0\51\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\1\2\47\uffff\1\1";
    static final String DFA5_specialS =
        "\1\uffff\1\0\51\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\1\2\1\1\5\2\1\uffff\13\2\2\uffff\2\2\5\uffff\1\2",
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
            return "71:54: ( ( typeArguments )=> typeArguments )?";
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
                        if ( (synpred4_DRLExpressions()) ) {s = 42;}

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
    static final String DFA13_eotS =
        "\16\uffff";
    static final String DFA13_eofS =
        "\16\uffff";
    static final String DFA13_minS =
        "\1\10\13\0\2\uffff";
    static final String DFA13_maxS =
        "\1\102\13\0\2\uffff";
    static final String DFA13_acceptS =
        "\14\uffff\1\2\1\1";
    static final String DFA13_specialS =
        "\1\uffff\1\5\1\2\1\1\1\11\1\12\1\7\1\10\1\3\1\6\1\4\1\0\2\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\14\2\uffff\2\14\1\uffff\1\14\3\uffff\3\14\1\2\1\3\1\4\1\5"+
            "\1\6\1\7\1\10\1\11\2\14\1\uffff\2\14\4\uffff\1\13\1\12\1\1\4"+
            "\14\1\uffff\2\14\4\uffff\2\14\5\uffff\2\14\5\uffff\1\14",
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
            return "94:29: ( ( assignmentOperator )=> assignmentOperator expression )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_11 = input.LA(1);

                         
                        int index13_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_11);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_3 = input.LA(1);

                         
                        int index13_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_3);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_8 = input.LA(1);

                         
                        int index13_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_8);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_10 = input.LA(1);

                         
                        int index13_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_10);
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
                        int LA13_9 = input.LA(1);

                         
                        int index13_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_9);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA13_6 = input.LA(1);

                         
                        int index13_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_6);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA13_7 = input.LA(1);

                         
                        int index13_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_7);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 12;}

                         
                        input.seek(index13_5);
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
    static final String DFA20_eotS =
        "\50\uffff";
    static final String DFA20_eofS =
        "\50\uffff";
    static final String DFA20_minS =
        "\1\10\3\uffff\2\0\42\uffff";
    static final String DFA20_maxS =
        "\1\102\3\uffff\2\0\42\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\2\45\uffff\1\1";
    static final String DFA20_specialS =
        "\4\uffff\1\0\1\1\42\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\2\1\4\uffff"+
            "\7\1\1\uffff\2\1\1\uffff\1\4\1\5\6\1\2\uffff\2\1\5\uffff\1\1",
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

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "()* loopback of 122:25: ( ( ( DOUBLE_PIPE | DOUBLE_AMPER ) operator )=> ( DOUBLE_PIPE | DOUBLE_AMPER ) operator shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA20_4 = input.LA(1);

                         
                        int index20_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index20_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA20_5 = input.LA(1);

                         
                        int index20_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_DRLExpressions()) ) {s = 39;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index20_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 20, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA22_eotS =
        "\51\uffff";
    static final String DFA22_eofS =
        "\51\uffff";
    static final String DFA22_minS =
        "\1\10\1\0\47\uffff";
    static final String DFA22_maxS =
        "\1\102\1\0\47\uffff";
    static final String DFA22_acceptS =
        "\2\uffff\1\2\45\uffff\1\1";
    static final String DFA22_specialS =
        "\1\uffff\1\0\47\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\2\uffff"+
            "\7\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff\1\1",
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
            return "130:18: ( instanceof_key type )?";
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
    static final String DFA26_eotS =
        "\52\uffff";
    static final String DFA26_eofS =
        "\52\uffff";
    static final String DFA26_minS =
        "\1\10\1\0\20\uffff\2\0\26\uffff";
    static final String DFA26_maxS =
        "\1\102\1\0\20\uffff\2\0\26\uffff";
    static final String DFA26_acceptS =
        "\2\uffff\1\2\45\uffff\2\1";
    static final String DFA26_specialS =
        "\1\0\1\1\20\uffff\1\2\1\3\26\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\4\2\1\51\1"+
            "\50\1\23\1\22\5\2\1\uffff\2\2\1\uffff\10\2\2\uffff\2\2\5\uffff"+
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
            return "()* loopback of 138:21: ( ( relationalOp )=> relationalOp shiftExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA26_0 = input.LA(1);

                         
                        int index26_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA26_0==ID) ) {s = 1;}

                        else if ( (LA26_0==FLOAT||(LA26_0>=HEX && LA26_0<=DECIMAL)||LA26_0==STRING||(LA26_0>=BOOL && LA26_0<=INCR)||(LA26_0>=SEMICOLON && LA26_0<=NOT_EQUALS)||(LA26_0>=EQUALS_ASSIGN && LA26_0<=RIGHT_SQUARE)||(LA26_0>=RIGHT_CURLY && LA26_0<=COMMA)||(LA26_0>=DOUBLE_AMPER && LA26_0<=XOR)||(LA26_0>=MINUS && LA26_0<=PLUS)) ) {s = 2;}

                        else if ( (LA26_0==LESS) ) {s = 18;}

                        else if ( (LA26_0==GREATER) ) {s = 19;}

                        else if ( (LA26_0==LESS_EQUALS) && (synpred8_DRLExpressions())) {s = 40;}

                        else if ( (LA26_0==GREATER_EQUALS) && (synpred8_DRLExpressions())) {s = 41;}

                         
                        input.seek(index26_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA26_1 = input.LA(1);

                         
                        int index26_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (((synpred8_DRLExpressions()&&((helper.isPluggableEvaluator(false))))||(synpred8_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NOT)))))) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA26_18 = input.LA(1);

                         
                        int index26_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_18);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA26_19 = input.LA(1);

                         
                        int index26_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_DRLExpressions()) ) {s = 41;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index26_19);
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
    static final String DFA28_eotS =
        "\23\uffff";
    static final String DFA28_eofS =
        "\1\2\22\uffff";
    static final String DFA28_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA28_maxS =
        "\1\102\1\0\21\uffff";
    static final String DFA28_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA28_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA28_transitionS = {
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

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "153:32: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_1 = input.LA(1);

                         
                        int index28_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred9_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index28_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA29_eotS =
        "\23\uffff";
    static final String DFA29_eofS =
        "\1\2\22\uffff";
    static final String DFA29_minS =
        "\1\10\1\0\21\uffff";
    static final String DFA29_maxS =
        "\1\102\1\0\21\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\17\uffff\1\1";
    static final String DFA29_specialS =
        "\1\uffff\1\0\21\uffff}>";
    static final String[] DFA29_transitionS = {
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
            return "154:21: ( ( squareArguments )=> squareArguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred10_DRLExpressions()) ) {s = 18;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_1);
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
        "\53\uffff";
    static final String DFA31_eofS =
        "\53\uffff";
    static final String DFA31_minS =
        "\1\10\17\uffff\2\0\31\uffff";
    static final String DFA31_maxS =
        "\1\102\17\uffff\2\0\31\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\1\2\50\uffff\1\1";
    static final String DFA31_specialS =
        "\20\uffff\1\0\1\1\31\uffff}>";
    static final String[] DFA31_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\6\1\1\21\1"+
            "\20\5\1\1\uffff\2\1\1\uffff\10\1\2\uffff\2\1\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()* loopback of 159:24: ( ( shiftOp )=> shiftOp additiveExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_16 = input.LA(1);

                         
                        int index31_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index31_16);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA31_17 = input.LA(1);

                         
                        int index31_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index31_17);
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
        "\53\uffff";
    static final String DFA33_eofS =
        "\53\uffff";
    static final String DFA33_minS =
        "\1\10\26\uffff\2\0\22\uffff";
    static final String DFA33_maxS =
        "\1\102\26\uffff\2\0\22\uffff";
    static final String DFA33_acceptS =
        "\1\uffff\1\2\50\uffff\1\1";
    static final String DFA33_specialS =
        "\27\uffff\1\0\1\1\22\uffff}>";
    static final String[] DFA33_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\15\1\1\uffff"+
            "\2\1\1\uffff\10\1\2\uffff\1\30\1\27\5\uffff\1\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()* loopback of 167:32: ( ( PLUS | MINUS )=> ( PLUS | MINUS ) multiplicativeExpression )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA33_23 = input.LA(1);

                         
                        int index33_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index33_23);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA33_24 = input.LA(1);

                         
                        int index33_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred12_DRLExpressions()) ) {s = 42;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index33_24);
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
        "\16\uffff";
    static final String DFA38_eofS =
        "\16\uffff";
    static final String DFA38_minS =
        "\1\10\2\uffff\1\0\12\uffff";
    static final String DFA38_maxS =
        "\1\102\2\uffff\1\0\12\uffff";
    static final String DFA38_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\10\uffff\1\3";
    static final String DFA38_specialS =
        "\3\uffff\1\0\12\uffff}>";
    static final String[] DFA38_transitionS = {
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
            return "182:1: unaryExpressionNotPlusMinus : ( TILDE unaryExpression | NEGATION unaryExpression | ( castExpression )=> castExpression | primary ( ( selector )=> selector )* ( ( INCR | DECR )=> ( INCR | DECR ) )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA38_3 = input.LA(1);

                         
                        int index38_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_DRLExpressions()) ) {s = 13;}

                        else if ( (true) ) {s = 4;}

                         
                        input.seek(index38_3);
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
    static final String DFA36_eotS =
        "\55\uffff";
    static final String DFA36_eofS =
        "\1\1\54\uffff";
    static final String DFA36_minS =
        "\1\10\45\uffff\1\0\6\uffff";
    static final String DFA36_maxS =
        "\1\103\45\uffff\1\0\6\uffff";
    static final String DFA36_acceptS =
        "\1\uffff\1\2\52\uffff\1\1";
    static final String DFA36_specialS =
        "\1\0\45\uffff\1\1\6\uffff}>";
    static final String[] DFA36_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\13\1\1\46"+
            "\1\1\1\uffff\2\1\1\54\14\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            ""
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "()* loopback of 186:17: ( ( selector )=> selector )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA36_0 = input.LA(1);

                         
                        int index36_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA36_0==EOF||LA36_0==FLOAT||(LA36_0>=HEX && LA36_0<=DECIMAL)||LA36_0==STRING||(LA36_0>=BOOL && LA36_0<=INCR)||(LA36_0>=SEMICOLON && LA36_0<=RIGHT_PAREN)||LA36_0==RIGHT_SQUARE||(LA36_0>=RIGHT_CURLY && LA36_0<=COMMA)||(LA36_0>=DOUBLE_AMPER && LA36_0<=PLUS)||(LA36_0>=ID && LA36_0<=DIV)) ) {s = 1;}

                        else if ( (LA36_0==LEFT_SQUARE) ) {s = 38;}

                        else if ( (LA36_0==DOT) && (synpred14_DRLExpressions())) {s = 44;}

                         
                        input.seek(index36_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA36_38 = input.LA(1);

                         
                        int index36_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred14_DRLExpressions()) ) {s = 44;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index36_38);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 36, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA37_eotS =
        "\55\uffff";
    static final String DFA37_eofS =
        "\1\2\54\uffff";
    static final String DFA37_minS =
        "\1\10\1\0\31\uffff\1\0\21\uffff";
    static final String DFA37_maxS =
        "\1\103\1\0\31\uffff\1\0\21\uffff";
    static final String DFA37_acceptS =
        "\2\uffff\1\2\51\uffff\1\1";
    static final String DFA37_specialS =
        "\1\uffff\1\0\31\uffff\1\1\21\uffff}>";
    static final String[] DFA37_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\13\2\1\33\1\1\1\uffff\15"+
            "\2\1\uffff\2\2\1\uffff\14\2\5\uffff\2\2",
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
            return "186:41: ( ( INCR | DECR )=> ( INCR | DECR ) )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA37_1 = input.LA(1);

                         
                        int index37_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_DRLExpressions()) ) {s = 44;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index37_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA37_27 = input.LA(1);

                         
                        int index37_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred15_DRLExpressions()) ) {s = 44;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index37_27);
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
        "\12\uffff";
    static final String DFA40_eofS =
        "\12\uffff";
    static final String DFA40_minS =
        "\1\102\1\0\10\uffff";
    static final String DFA40_maxS =
        "\1\102\1\0\10\uffff";
    static final String DFA40_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA40_specialS =
        "\1\1\1\0\10\uffff}>";
    static final String[] DFA40_transitionS = {
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
            return "194:1: primitiveType : ( boolean_key | char_key | byte_key | short_key | int_key | long_key | float_key | double_key );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA40_1 = input.LA(1);

                         
                        int index40_1 = input.index();
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

                         
                        input.seek(index40_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA40_0 = input.LA(1);

                         
                        int index40_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA40_0==ID) && ((((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.LONG)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF)))||((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE)))||((helper.validateIdentifierKey(DroolsSoftKeywords.INT)))||((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR)))))) {s = 1;}

                         
                        input.seek(index40_0);
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
    static final String DFA45_eotS =
        "\21\uffff";
    static final String DFA45_eofS =
        "\21\uffff";
    static final String DFA45_minS =
        "\1\10\10\uffff\2\0\6\uffff";
    static final String DFA45_maxS =
        "\1\102\10\uffff\2\0\6\uffff";
    static final String DFA45_acceptS =
        "\1\uffff\1\1\1\2\6\3\2\uffff\1\4\1\5\1\6\1\11\1\7\1\10";
    static final String DFA45_specialS =
        "\1\0\10\uffff\1\1\1\2\6\uffff}>";
    static final String[] DFA45_transitionS = {
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
            return "205:1: primary : ( ( parExpression )=> parExpression | ( nonWildcardTypeArguments )=> nonWildcardTypeArguments ( explicitGenericInvocationSuffix | this_key arguments ) | ( literal )=> literal | ( super_key )=> super_key superSuffix | ( new_key )=> new_key creator | ( primitiveType )=> primitiveType ( LEFT_SQUARE RIGHT_SQUARE )* DOT class_key | ( inlineMapExpression )=> inlineMapExpression | ( inlineListExpression )=> inlineListExpression | ( ID )=> ID ( ( DOT ID )=> DOT ID )* ( ( identifierSuffix )=> identifierSuffix )? );";
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
                        if ( (LA45_0==LEFT_PAREN) && (synpred18_DRLExpressions())) {s = 1;}

                        else if ( (LA45_0==LESS) && (synpred19_DRLExpressions())) {s = 2;}

                        else if ( (LA45_0==STRING) && (synpred20_DRLExpressions())) {s = 3;}

                        else if ( (LA45_0==DECIMAL) && (synpred20_DRLExpressions())) {s = 4;}

                        else if ( (LA45_0==HEX) && (synpred20_DRLExpressions())) {s = 5;}

                        else if ( (LA45_0==FLOAT) && (synpred20_DRLExpressions())) {s = 6;}

                        else if ( (LA45_0==BOOL) && (synpred20_DRLExpressions())) {s = 7;}

                        else if ( (LA45_0==NULL) && (synpred20_DRLExpressions())) {s = 8;}

                        else if ( (LA45_0==ID) ) {s = 9;}

                        else if ( (LA45_0==LEFT_SQUARE) ) {s = 10;}

                         
                        input.seek(index45_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA45_9 = input.LA(1);

                         
                        int index45_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((synpred21_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SUPER))))) ) {s = 11;}

                        else if ( ((synpred22_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.NEW))))) ) {s = 12;}

                        else if ( (((synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.LONG))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.SHORT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.CHAR))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.FLOAT))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.DOUBLE))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.INSTANCEOF))))||(synpred23_DRLExpressions()&&((helper.validateIdentifierKey(DroolsSoftKeywords.BYTE)))))) ) {s = 13;}

                        else if ( (synpred26_DRLExpressions()) ) {s = 14;}

                         
                        input.seek(index45_9);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA45_10 = input.LA(1);

                         
                        int index45_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred24_DRLExpressions()) ) {s = 15;}

                        else if ( (synpred25_DRLExpressions()) ) {s = 16;}

                         
                        input.seek(index45_10);
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
    static final String DFA44_eotS =
        "\56\uffff";
    static final String DFA44_eofS =
        "\1\3\55\uffff";
    static final String DFA44_minS =
        "\1\10\2\0\53\uffff";
    static final String DFA44_maxS =
        "\1\103\2\0\53\uffff";
    static final String DFA44_acceptS =
        "\3\uffff\1\2\51\uffff\1\1";
    static final String DFA44_specialS =
        "\1\uffff\1\0\1\1\53\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\3\2\uffff\2\3\1\uffff\1\3\3\uffff\15\3\1\uffff\11\3\1\2\1"+
            "\3\1\1\1\3\1\uffff\17\3\5\uffff\2\3",
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
            "",
            "",
            ""
    };

    static final short[] DFA44_eot = DFA.unpackEncodedString(DFA44_eotS);
    static final short[] DFA44_eof = DFA.unpackEncodedString(DFA44_eofS);
    static final char[] DFA44_min = DFA.unpackEncodedStringToUnsignedChars(DFA44_minS);
    static final char[] DFA44_max = DFA.unpackEncodedStringToUnsignedChars(DFA44_maxS);
    static final short[] DFA44_accept = DFA.unpackEncodedString(DFA44_acceptS);
    static final short[] DFA44_special = DFA.unpackEncodedString(DFA44_specialS);
    static final short[][] DFA44_transition;

    static {
        int numStates = DFA44_transitionS.length;
        DFA44_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA44_transition[i] = DFA.unpackEncodedString(DFA44_transitionS[i]);
        }
    }

    class DFA44 extends DFA {

        public DFA44(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 44;
            this.eot = DFA44_eot;
            this.eof = DFA44_eof;
            this.min = DFA44_min;
            this.max = DFA44_max;
            this.accept = DFA44_accept;
            this.special = DFA44_special;
            this.transition = DFA44_transition;
        }
        public String getDescription() {
            return "216:38: ( ( identifierSuffix )=> identifierSuffix )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA44_1 = input.LA(1);

                         
                        int index44_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index44_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA44_2 = input.LA(1);

                         
                        int index44_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index44_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 44, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA50_eotS =
        "\56\uffff";
    static final String DFA50_eofS =
        "\1\1\55\uffff";
    static final String DFA50_minS =
        "\1\10\45\uffff\1\0\7\uffff";
    static final String DFA50_maxS =
        "\1\103\45\uffff\1\0\7\uffff";
    static final String DFA50_acceptS =
        "\1\uffff\1\2\53\uffff\1\1";
    static final String DFA50_specialS =
        "\46\uffff\1\0\7\uffff}>";
    static final String[] DFA50_transitionS = {
            "\1\1\2\uffff\2\1\1\uffff\1\1\3\uffff\15\1\1\uffff\13\1\1\46"+
            "\1\1\1\uffff\17\1\5\uffff\2\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "()+ loopback of 241:7: ( ( LEFT_SQUARE )=> LEFT_SQUARE expression RIGHT_SQUARE )+";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA50_38 = input.LA(1);

                         
                        int index50_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred30_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index50_38);
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
    static final String DFA59_eotS =
        "\56\uffff";
    static final String DFA59_eofS =
        "\1\2\55\uffff";
    static final String DFA59_minS =
        "\1\10\1\0\54\uffff";
    static final String DFA59_maxS =
        "\1\103\1\0\54\uffff";
    static final String DFA59_acceptS =
        "\2\uffff\1\2\52\uffff\1\1";
    static final String DFA59_specialS =
        "\1\uffff\1\0\54\uffff}>";
    static final String[] DFA59_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\13\2\1\1\1"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            return "()* loopback of 268:37: ({...}? => LEFT_SQUARE expression RIGHT_SQUARE )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA59_1 = input.LA(1);

                         
                        int index59_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((!helper.validateLT(2,"]"))) ) {s = 45;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index59_1);
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
    static final String DFA68_eotS =
        "\56\uffff";
    static final String DFA68_eofS =
        "\1\2\55\uffff";
    static final String DFA68_minS =
        "\1\10\1\0\54\uffff";
    static final String DFA68_maxS =
        "\1\103\1\0\54\uffff";
    static final String DFA68_acceptS =
        "\2\uffff\1\2\52\uffff\1\1";
    static final String DFA68_specialS =
        "\1\uffff\1\0\54\uffff}>";
    static final String[] DFA68_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\11\2\1\1\3"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            "",
            "",
            ""
    };

    static final short[] DFA68_eot = DFA.unpackEncodedString(DFA68_eotS);
    static final short[] DFA68_eof = DFA.unpackEncodedString(DFA68_eofS);
    static final char[] DFA68_min = DFA.unpackEncodedStringToUnsignedChars(DFA68_minS);
    static final char[] DFA68_max = DFA.unpackEncodedStringToUnsignedChars(DFA68_maxS);
    static final short[] DFA68_accept = DFA.unpackEncodedString(DFA68_acceptS);
    static final short[] DFA68_special = DFA.unpackEncodedString(DFA68_specialS);
    static final short[][] DFA68_transition;

    static {
        int numStates = DFA68_transitionS.length;
        DFA68_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA68_transition[i] = DFA.unpackEncodedString(DFA68_transitionS[i]);
        }
    }

    class DFA68 extends DFA {

        public DFA68(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 68;
            this.eot = DFA68_eot;
            this.eof = DFA68_eof;
            this.min = DFA68_min;
            this.max = DFA68_max;
            this.accept = DFA68_accept;
            this.special = DFA68_special;
            this.transition = DFA68_transition;
        }
        public String getDescription() {
            return "301:26: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA68_1 = input.LA(1);

                         
                        int index68_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred35_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index68_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 68, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA70_eotS =
        "\56\uffff";
    static final String DFA70_eofS =
        "\1\2\55\uffff";
    static final String DFA70_minS =
        "\1\10\1\0\54\uffff";
    static final String DFA70_maxS =
        "\1\103\1\0\54\uffff";
    static final String DFA70_acceptS =
        "\2\uffff\1\2\52\uffff\1\1";
    static final String DFA70_specialS =
        "\1\uffff\1\0\54\uffff}>";
    static final String[] DFA70_transitionS = {
            "\1\2\2\uffff\2\2\1\uffff\1\2\3\uffff\15\2\1\uffff\11\2\1\1\3"+
            "\2\1\uffff\17\2\5\uffff\2\2",
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
            "",
            "",
            ""
    };

    static final short[] DFA70_eot = DFA.unpackEncodedString(DFA70_eotS);
    static final short[] DFA70_eof = DFA.unpackEncodedString(DFA70_eofS);
    static final char[] DFA70_min = DFA.unpackEncodedStringToUnsignedChars(DFA70_minS);
    static final char[] DFA70_max = DFA.unpackEncodedStringToUnsignedChars(DFA70_maxS);
    static final short[] DFA70_accept = DFA.unpackEncodedString(DFA70_acceptS);
    static final short[] DFA70_special = DFA.unpackEncodedString(DFA70_specialS);
    static final short[][] DFA70_transition;

    static {
        int numStates = DFA70_transitionS.length;
        DFA70_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA70_transition[i] = DFA.unpackEncodedString(DFA70_transitionS[i]);
        }
    }

    class DFA70 extends DFA {

        public DFA70(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 70;
            this.eot = DFA70_eot;
            this.eof = DFA70_eof;
            this.min = DFA70_min;
            this.max = DFA70_max;
            this.accept = DFA70_accept;
            this.special = DFA70_special;
            this.transition = DFA70_transition;
        }
        public String getDescription() {
            return "308:17: ( ( LEFT_PAREN )=> arguments )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA70_1 = input.LA(1);

                         
                        int index70_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred37_DRLExpressions()) ) {s = 45;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index70_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 70, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA75_eotS =
        "\17\uffff";
    static final String DFA75_eofS =
        "\17\uffff";
    static final String DFA75_minS =
        "\1\25\12\uffff\2\46\2\uffff";
    static final String DFA75_maxS =
        "\1\50\12\uffff\1\46\1\50\2\uffff";
    static final String DFA75_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\2\uffff\1\13"+
        "\1\14";
    static final String DFA75_specialS =
        "\14\uffff\1\0\2\uffff}>";
    static final String[] DFA75_transitionS = {
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

    static final short[] DFA75_eot = DFA.unpackEncodedString(DFA75_eotS);
    static final short[] DFA75_eof = DFA.unpackEncodedString(DFA75_eofS);
    static final char[] DFA75_min = DFA.unpackEncodedStringToUnsignedChars(DFA75_minS);
    static final char[] DFA75_max = DFA.unpackEncodedStringToUnsignedChars(DFA75_maxS);
    static final short[] DFA75_accept = DFA.unpackEncodedString(DFA75_acceptS);
    static final short[] DFA75_special = DFA.unpackEncodedString(DFA75_specialS);
    static final short[][] DFA75_transition;

    static {
        int numStates = DFA75_transitionS.length;
        DFA75_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA75_transition[i] = DFA.unpackEncodedString(DFA75_transitionS[i]);
        }
    }

    class DFA75 extends DFA {

        public DFA75(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 75;
            this.eot = DFA75_eot;
            this.eof = DFA75_eof;
            this.min = DFA75_min;
            this.max = DFA75_max;
            this.accept = DFA75_accept;
            this.special = DFA75_special;
            this.transition = DFA75_transition;
        }
        public String getDescription() {
            return "323:1: assignmentOperator : ( EQUALS_ASSIGN | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | LESS LESS EQUALS_ASSIGN | ( GREATER GREATER GREATER )=> GREATER GREATER GREATER EQUALS_ASSIGN | ( GREATER GREATER )=> GREATER GREATER EQUALS_ASSIGN );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA75_12 = input.LA(1);

                         
                        int index75_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA75_12==GREATER) && (synpred38_DRLExpressions())) {s = 13;}

                        else if ( (LA75_12==EQUALS_ASSIGN) && (synpred39_DRLExpressions())) {s = 14;}

                         
                        input.seek(index75_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 75, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_STRING_in_literal83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECIMAL_in_literal98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HEX_in_literal110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeList183 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_typeList186 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeList188 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_primitiveType_in_type216 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type226 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type228 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ID_in_type242 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type249 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_DOT_in_type254 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_type256 = new BitSet(new long[]{0x0001088000000002L});
    public static final BitSet FOLLOW_typeArguments_in_type263 = new BitSet(new long[]{0x0001080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_type278 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_type280 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LESS_in_typeArguments301 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments303 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_COMMA_in_typeArguments306 = new BitSet(new long[]{0x0008000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeArgument_in_typeArguments308 = new BitSet(new long[]{0x0000804000000000L});
    public static final BitSet FOLLOW_GREATER_in_typeArguments312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_type_in_typeArgument329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_typeArgument337 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_extends_key_in_typeArgument341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_typeArgument345 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_typeArgument348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_dummy372 = new BitSet(new long[]{0x0000000100100000L});
    public static final BitSet FOLLOW_set_in_dummy374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalExpression_in_expression400 = new BitSet(new long[]{0x000001C01FE00002L});
    public static final BitSet FOLLOW_assignmentOperator_in_expression409 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expression411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalOrExpression_in_conditionalExpression428 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_conditionalExpression432 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression434 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_conditionalExpression436 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_conditionalExpression438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression456 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_conditionalOrExpression460 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression462 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression481 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_conditionalAndExpression485 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_inclusiveOrExpression_in_conditionalAndExpression487 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression505 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_PIPE_in_inclusiveOrExpression509 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_exclusiveOrExpression_in_inclusiveOrExpression511 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression529 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_XOR_in_exclusiveOrExpression533 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_andExpression_in_exclusiveOrExpression535 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression554 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_AMPER_in_andExpression558 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_andOrRestriction_in_andExpression560 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_andOrRestriction583 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_set_in_andOrRestriction597 = new BitSet(new long[]{0x000000FC00000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_operator_in_andOrRestriction603 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_andOrRestriction605 = new BitSet(new long[]{0x0006000000000002L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression628 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_set_in_equalityExpression632 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceOfExpression_in_equalityExpression642 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_inExpression_in_instanceOfExpression661 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_instanceof_key_in_instanceOfExpression664 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_instanceOfExpression666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_inExpression683 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_not_key_in_inExpression686 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_in_key_in_inExpression689 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_inExpression691 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression693 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression696 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_inExpression698 = new BitSet(new long[]{0x0000840000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_inExpression702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression718 = new BitSet(new long[]{0x000000FC00000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_relationalOp_in_relationalExpression727 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_shiftExpression_in_relationalExpression729 = new BitSet(new long[]{0x000000FC00000002L,0x0000000000000004L});
    public static final BitSet FOLLOW_EQUALS_in_operator747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUALS_in_operator755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_operator763 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_EQUALS_in_relationalOp786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_EQUALS_in_relationalOp794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_relationalOp803 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_relationalOp812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_key_in_relationalOp820 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_neg_operator_key_in_relationalOp822 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_key_in_relationalOp840 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_squareArguments_in_relationalOp849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression872 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_shiftOp_in_shiftExpression880 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_additiveExpression_in_shiftExpression882 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_LESS_in_shiftOp903 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_shiftOp905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp909 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp911 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp917 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_shiftOp919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression938 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_set_in_additiveExpression949 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression957 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression977 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_multiplicativeExpression981 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression995 = new BitSet(new long[]{0x0600000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1017 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1027 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INCR_in_unaryExpression1039 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DECR_in_unaryExpression1051 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_primary_in_unaryExpression1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_unaryExpression1063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unaryExpressionNotPlusMinus1082 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEGATION_in_unaryExpressionNotPlusMinus1093 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpressionNotPlusMinus1095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_unaryExpressionNotPlusMinus1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpressionNotPlusMinus1119 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_selector_in_unaryExpressionNotPlusMinus1126 = new BitSet(new long[]{0x0001080060000002L});
    public static final BitSet FOLLOW_set_in_unaryExpressionNotPlusMinus1138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1174 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_castExpression1176 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1178 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpression_in_castExpression1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_castExpression1197 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_castExpression1199 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_castExpression1201 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_unaryExpressionNotPlusMinus_in_castExpression1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_key_in_primitiveType1224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_char_key_in_primitiveType1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_key_in_primitiveType1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_short_key_in_primitiveType1248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_int_key_in_primitiveType1256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_long_key_in_primitiveType1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_float_key_in_primitiveType1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_double_key_in_primitiveType1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary1302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_primary1317 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_explicitGenericInvocationSuffix_in_primary1320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_this_key_in_primary1324 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_primary1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_primary1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_primary1362 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_primary1364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_primary1379 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_creator_in_primary1381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_primary1396 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_primary1399 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_primary1401 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_primary1405 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_primary1407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_primary1427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_primary1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_primary1456 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_DOT_in_primary1465 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_primary1467 = new BitSet(new long[]{0x00010A0000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_primary1476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineListExpression1497 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_inlineListExpression1499 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineListExpression1502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_inlineMapExpression1524 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapExpressionList_in_inlineMapExpression1526 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_inlineMapExpression1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1546 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_mapExpressionList1549 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_mapEntry_in_mapExpressionList1551 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_expression_in_mapEntry1574 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_COLON_in_mapEntry1576 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_mapEntry1578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_parExpression1595 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_parExpression1597 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_parExpression1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1623 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1625 = new BitSet(new long[]{0x0001080000000000L});
    public static final BitSet FOLLOW_DOT_in_identifierSuffix1629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_class_key_in_identifierSuffix1631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_identifierSuffix1646 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix1648 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_identifierSuffix1650 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arguments_in_identifierSuffix1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_creator1686 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_createdName_in_creator1689 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_arrayCreatorRest_in_creator1700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classCreatorRest_in_creator1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_createdName1722 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1724 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_DOT_in_createdName1737 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_createdName1739 = new BitSet(new long[]{0x0001008000000002L});
    public static final BitSet FOLLOW_typeArguments_in_createdName1741 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_createdName1756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_innerCreator1776 = new BitSet(new long[]{0x00000A0000000000L});
    public static final BitSet FOLLOW_classCreatorRest_in_innerCreator1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1797 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1807 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1810 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1812 = new BitSet(new long[]{0x0000280000000000L});
    public static final BitSet FOLLOW_arrayInitializer_in_arrayCreatorRest1816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1830 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1832 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1837 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_arrayCreatorRest1839 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1841 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_arrayCreatorRest1853 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_arrayCreatorRest1855 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_arrayInitializer_in_variableInitializer1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_variableInitializer1898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_arrayInitializer1915 = new BitSet(new long[]{0x18306A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1918 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1921 = new BitSet(new long[]{0x18302A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableInitializer_in_arrayInitializer1923 = new BitSet(new long[]{0x0000C00000000000L});
    public static final BitSet FOLLOW_COMMA_in_arrayInitializer1928 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_arrayInitializer1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_classCreatorRest1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_explicitGenericInvocation1970 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocation1972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_nonWildcardTypeArguments1989 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_typeList_in_nonWildcardTypeArguments1991 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_nonWildcardTypeArguments1993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_explicitGenericInvocationSuffix2010 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_explicitGenericInvocationSuffix2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_explicitGenericInvocationSuffix2023 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_arguments_in_explicitGenericInvocationSuffix2025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_selector2052 = new BitSet(new long[]{0x0001020000000000L});
    public static final BitSet FOLLOW_superSuffix_in_selector2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_selector2072 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_selector2075 = new BitSet(new long[]{0x0000008000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_innerCreator_in_selector2079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_selector2095 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_selector2097 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_selector2106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_selector2127 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_selector2129 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_selector2131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_superSuffix2159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_superSuffix2161 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_arguments_in_superSuffix2170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_squareArguments2193 = new BitSet(new long[]{0x18301A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_squareArguments2195 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_squareArguments2198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_arguments2215 = new BitSet(new long[]{0x18300E80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expressionList_in_arguments2217 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_arguments2220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_expressionList2237 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_COMMA_in_expressionList2240 = new BitSet(new long[]{0x18300A80600C5900L,0x0000000000000004L});
    public static final BitSet FOLLOW_expression_in_expressionList2242 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_assignmentOperator2269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_ASSIGN_in_assignmentOperator2277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_ASSIGN_in_assignmentOperator2285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_ASSIGN_in_assignmentOperator2293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_ASSIGN_in_assignmentOperator2301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_ASSIGN_in_assignmentOperator2309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_XOR_ASSIGN_in_assignmentOperator2317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_ASSIGN_in_assignmentOperator2325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2333 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_LESS_in_assignmentOperator2335 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2354 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2356 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2358 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2375 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_assignmentOperator2377 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_EQUALS_ASSIGN_in_assignmentOperator2379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_extends_key2409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_key2436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_instanceof_key2463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_boolean_key2490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_char_key2517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_byte_key2544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_short_key2571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_int_key2598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_float_key2625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_long_key2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_double_key2679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_void_key2706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_this_key2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_class_key2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_new_key2787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_not_key2814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_in_key2839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_operator_key2862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_neg_operator_key2885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred1_DRLExpressions209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred2_DRLExpressions220 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred2_DRLExpressions222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred3_DRLExpressions246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeArguments_in_synpred4_DRLExpressions260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred5_DRLExpressions272 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred5_DRLExpressions274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignmentOperator_in_synpred6_DRLExpressions404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred7_DRLExpressions588 = new BitSet(new long[]{0x000000FC00000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_operator_in_synpred7_DRLExpressions594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_relationalOp_in_synpred8_DRLExpressions723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred9_DRLExpressions826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_squareArguments_in_synpred10_DRLExpressions845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shiftOp_in_synpred11_DRLExpressions877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred12_DRLExpressions942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_castExpression_in_synpred13_DRLExpressions1106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_synpred14_DRLExpressions1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred15_DRLExpressions1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred16_DRLExpressions1167 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_primitiveType_in_synpred16_DRLExpressions1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred17_DRLExpressions1190 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_type_in_synpred17_DRLExpressions1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_synpred18_DRLExpressions1298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonWildcardTypeArguments_in_synpred19_DRLExpressions1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred20_DRLExpressions1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_key_in_synpred21_DRLExpressions1358 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_new_key_in_synpred22_DRLExpressions1375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primitiveType_in_synpred23_DRLExpressions1392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineMapExpression_in_synpred24_DRLExpressions1423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inlineListExpression_in_synpred25_DRLExpressions1438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred26_DRLExpressions1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred27_DRLExpressions1460 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_synpred27_DRLExpressions1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_synpred28_DRLExpressions1473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred29_DRLExpressions1617 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred29_DRLExpressions1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred30_DRLExpressions1641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred31_DRLExpressions1847 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_synpred31_DRLExpressions1849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred32_DRLExpressions2045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_super_key_in_synpred32_DRLExpressions2047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred33_DRLExpressions2065 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_new_key_in_synpred33_DRLExpressions2067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_synpred34_DRLExpressions2090 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_synpred34_DRLExpressions2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred35_DRLExpressions2101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred36_DRLExpressions2124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred37_DRLExpressions2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2346 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2348 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred38_DRLExpressions2350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2369 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_GREATER_in_synpred39_DRLExpressions2371 = new BitSet(new long[]{0x0000000000000002L});

}