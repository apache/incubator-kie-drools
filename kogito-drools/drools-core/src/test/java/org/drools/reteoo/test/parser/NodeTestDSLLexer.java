// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g 2011-06-27 02:28:08

    package org.drools.reteoo.test.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class NodeTestDSLLexer extends Lexer {
    public static final int EOF=-1;
    public static final int VT_TEST_CASE=4;
    public static final int VT_PARAMS=5;
    public static final int VT_QUALIFIED_ID=6;
    public static final int VT_SYMBOL=7;
    public static final int VT_CHUNK=8;
    public static final int VK_TEST_CASE=9;
    public static final int VK_IMPORT=10;
    public static final int VK_SETUP=11;
    public static final int VK_TEARDOWN=12;
    public static final int VK_TEST=13;
    public static final int STRING=14;
    public static final int SEMI_COLON=15;
    public static final int ID=16;
    public static final int COLON=17;
    public static final int COMMA=18;
    public static final int INT=19;
    public static final int FLOAT=20;
    public static final int MISC=21;
    public static final int STAR=22;
    public static final int DOT=23;
    public static final int LEFT_SQUARE=24;
    public static final int RIGHT_SQUARE=25;
    public static final int LEFT_PAREN=26;
    public static final int RIGHT_PAREN=27;
    public static final int EOL=28;
    public static final int WS=29;
    public static final int EscapeSequence=30;
    public static final int HexDigit=31;
    public static final int UnicodeEscape=32;
    public static final int OctalEscape=33;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int MULTI_LINE_COMMENT=36;
    public static final int IdentifierStart=37;
    public static final int IdentifierPart=38;



    // delegates
    // delegators

    public NodeTestDSLLexer() {;} 
    public NodeTestDSLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public NodeTestDSLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g"; }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:233:9: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:233:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:233:17: ( ' ' | '\\t' | '\\f' | EOL )+
            int cnt1=0;
            loop1:
            do {
                int alt1=5;
                switch ( input.LA(1) ) {
                case ' ':
                    {
                    alt1=1;
                    }
                    break;
                case '\t':
                    {
                    alt1=2;
                    }
                    break;
                case '\f':
                    {
                    alt1=3;
                    }
                    break;
                case '\n':
                case '\r':
                    {
                    alt1=4;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:233:19: ' '
            	    {
            	    match(' '); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:234:19: '\\t'
            	    {
            	    match('\t'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:235:19: '\\f'
            	    {
            	    match('\f'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:236:19: EOL
            	    {
            	    mEOL(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            if ( state.backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "EOL"
    public final void mEOL() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:242:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:243:12: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:243:12: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1_NodeTestDSL())) {
                    alt2=1;
                }
                else {
                    alt2=2;}
            }
            else if ( (LA2_0=='\n') ) {
                alt2=3;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:243:20: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:244:25: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:245:25: '\\n'
                    {
                    match('\n'); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "EOL"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:5: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:7: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:7: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:8: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:13: ( '0' .. '9' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:14: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            match('.'); if (state.failed) return ;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:29: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:250:30: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:5: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:7: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:7: ( '-' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='-') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:8: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:13: ( '0' .. '9' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:254:14: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\"') ) {
                alt10=1;
            }
            else if ( (LA10_0=='\'') ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0>='\u0000' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='\uFFFF')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:258:32: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    match('\"'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0>='\u0000' && LA9_0<='&')||(LA9_0>='(' && LA9_0<='[')||(LA9_0>=']' && LA9_0<='\uFFFF')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:259:33: ~ ( '\\\\' | '\\'' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();
                    	    state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    match('\''); if (state.failed) return ;

                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:263:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:263:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:267:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '$':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case '-':
                case '.':
                case '?':
                case 'A':
                case 'B':
                case 'D':
                case 'E':
                case 'G':
                case 'Q':
                case 'S':
                case 'W':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'n':
                case 'o':
                case 'p':
                case 'r':
                case 's':
                case 't':
                case 'w':
                case 'x':
                case 'z':
                case '{':
                case '|':
                case '}':
                    {
                    alt11=1;
                    }
                    break;
                case 'u':
                    {
                    alt11=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt11=3;
                    }
                    break;
                default:
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
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:267:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
                    {
                    match('\\'); if (state.failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)=='?'||(input.LA(1)>='A' && input.LA(1)<='B')||(input.LA(1)>='D' && input.LA(1)<='E')||input.LA(1)=='G'||input.LA(1)=='Q'||input.LA(1)=='S'||input.LA(1)=='W'||(input.LA(1)>='Z' && input.LA(1)<='^')||(input.LA(1)>='a' && input.LA(1)<='f')||(input.LA(1)>='n' && input.LA(1)<='p')||(input.LA(1)>='r' && input.LA(1)<='t')||(input.LA(1)>='w' && input.LA(1)<='x')||(input.LA(1)>='z' && input.LA(1)<='}') ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:271:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:272:9: OctalEscape
                    {
                    mOctalEscape(); if (state.failed) return ;

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "OctalEscape"
    public final void mOctalEscape() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt12=3;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='\\') ) {
                int LA12_1 = input.LA(2);

                if ( ((LA12_1>='0' && LA12_1<='3')) ) {
                    int LA12_2 = input.LA(3);

                    if ( ((LA12_2>='0' && LA12_2<='7')) ) {
                        int LA12_4 = input.LA(4);

                        if ( ((LA12_4>='0' && LA12_4<='7')) ) {
                            alt12=1;
                        }
                        else {
                            alt12=2;}
                    }
                    else {
                        alt12=3;}
                }
                else if ( ((LA12_1>='4' && LA12_1<='7')) ) {
                    int LA12_3 = input.LA(3);

                    if ( ((LA12_3>='0' && LA12_3<='7')) ) {
                        alt12=2;
                    }
                    else {
                        alt12=3;}
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
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:14: ( '0' .. '3' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (state.failed) return ;

                    }

                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:25: ( '0' .. '7' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:36: ( '0' .. '7' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:277:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:278:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:278:14: ( '0' .. '7' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:278:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:278:25: ( '0' .. '7' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:278:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:279:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:279:14: ( '0' .. '7' )
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:279:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OctalEscape"

    // $ANTLR start "UnicodeEscape"
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:284:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:284:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); if (state.failed) return ;
            match('u'); if (state.failed) return ;
            mHexDigit(); if (state.failed) return ;
            mHexDigit(); if (state.failed) return ;
            mHexDigit(); if (state.failed) return ;
            mHexDigit(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "UnicodeEscape"

    // $ANTLR start "SH_STYLE_SINGLE_LINE_COMMENT"
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:5: ( '#' (~ ( '\\r' | '\\n' ) )* ( EOL )? )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:7: '#' (~ ( '\\r' | '\\n' ) )* ( EOL )?
            {
            match('#'); if (state.failed) return ;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:11: (~ ( '\\r' | '\\n' ) )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0>='\u0000' && LA13_0<='\t')||(LA13_0>='\u000B' && LA13_0<='\f')||(LA13_0>='\u000E' && LA13_0<='\uFFFF')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:12: ~ ( '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:27: ( EOL )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='\n'||LA14_0=='\r') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:288:27: EOL
                    {
                    mEOL(); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               _channel=HIDDEN; setText("//"+getText().substring(1));
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SH_STYLE_SINGLE_LINE_COMMENT"

    // $ANTLR start "C_STYLE_SINGLE_LINE_COMMENT"
    public final void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = C_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:5: ( '//' (~ ( '\\r' | '\\n' ) )* ( EOL )? )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:7: '//' (~ ( '\\r' | '\\n' ) )* ( EOL )?
            {
            match("//"); if (state.failed) return ;

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:12: (~ ( '\\r' | '\\n' ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='\u0000' && LA15_0<='\t')||(LA15_0>='\u000B' && LA15_0<='\f')||(LA15_0>='\u000E' && LA15_0<='\uFFFF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:13: ~ ( '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:28: ( EOL )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='\n'||LA16_0=='\r') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:293:28: EOL
                    {
                    mEOL(); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "C_STYLE_SINGLE_LINE_COMMENT"

    // $ANTLR start "MULTI_LINE_COMMENT"
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:298:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:298:7: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:298:12: ( options {greedy=false; } : . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='*') ) {
                    int LA17_1 = input.LA(2);

                    if ( (LA17_1=='/') ) {
                        alt17=2;
                    }
                    else if ( ((LA17_1>='\u0000' && LA17_1<='.')||(LA17_1>='0' && LA17_1<='\uFFFF')) ) {
                        alt17=1;
                    }


                }
                else if ( ((LA17_0>='\u0000' && LA17_0<=')')||(LA17_0>='+' && LA17_0<='\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:298:38: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            match("*/"); if (state.failed) return ;

            if ( state.backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULTI_LINE_COMMENT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:303:5: ( IdentifierStart ( IdentifierPart )* | '`' IdentifierStart ( IdentifierPart )* '`' )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='$'||(LA20_0>='A' && LA20_0<='Z')||LA20_0=='_'||(LA20_0>='a' && LA20_0<='z')||(LA20_0>='\u00A2' && LA20_0<='\u00A5')||LA20_0=='\u00AA'||LA20_0=='\u00B5'||LA20_0=='\u00BA'||(LA20_0>='\u00C0' && LA20_0<='\u00D6')||(LA20_0>='\u00D8' && LA20_0<='\u00F6')||(LA20_0>='\u00F8' && LA20_0<='\u0236')||(LA20_0>='\u0250' && LA20_0<='\u02C1')||(LA20_0>='\u02C6' && LA20_0<='\u02D1')||(LA20_0>='\u02E0' && LA20_0<='\u02E4')||LA20_0=='\u02EE'||LA20_0=='\u037A'||LA20_0=='\u0386'||(LA20_0>='\u0388' && LA20_0<='\u038A')||LA20_0=='\u038C'||(LA20_0>='\u038E' && LA20_0<='\u03A1')||(LA20_0>='\u03A3' && LA20_0<='\u03CE')||(LA20_0>='\u03D0' && LA20_0<='\u03F5')||(LA20_0>='\u03F7' && LA20_0<='\u03FB')||(LA20_0>='\u0400' && LA20_0<='\u0481')||(LA20_0>='\u048A' && LA20_0<='\u04CE')||(LA20_0>='\u04D0' && LA20_0<='\u04F5')||(LA20_0>='\u04F8' && LA20_0<='\u04F9')||(LA20_0>='\u0500' && LA20_0<='\u050F')||(LA20_0>='\u0531' && LA20_0<='\u0556')||LA20_0=='\u0559'||(LA20_0>='\u0561' && LA20_0<='\u0587')||(LA20_0>='\u05D0' && LA20_0<='\u05EA')||(LA20_0>='\u05F0' && LA20_0<='\u05F2')||(LA20_0>='\u0621' && LA20_0<='\u063A')||(LA20_0>='\u0640' && LA20_0<='\u064A')||(LA20_0>='\u066E' && LA20_0<='\u066F')||(LA20_0>='\u0671' && LA20_0<='\u06D3')||LA20_0=='\u06D5'||(LA20_0>='\u06E5' && LA20_0<='\u06E6')||(LA20_0>='\u06EE' && LA20_0<='\u06EF')||(LA20_0>='\u06FA' && LA20_0<='\u06FC')||LA20_0=='\u06FF'||LA20_0=='\u0710'||(LA20_0>='\u0712' && LA20_0<='\u072F')||(LA20_0>='\u074D' && LA20_0<='\u074F')||(LA20_0>='\u0780' && LA20_0<='\u07A5')||LA20_0=='\u07B1'||(LA20_0>='\u0904' && LA20_0<='\u0939')||LA20_0=='\u093D'||LA20_0=='\u0950'||(LA20_0>='\u0958' && LA20_0<='\u0961')||(LA20_0>='\u0985' && LA20_0<='\u098C')||(LA20_0>='\u098F' && LA20_0<='\u0990')||(LA20_0>='\u0993' && LA20_0<='\u09A8')||(LA20_0>='\u09AA' && LA20_0<='\u09B0')||LA20_0=='\u09B2'||(LA20_0>='\u09B6' && LA20_0<='\u09B9')||LA20_0=='\u09BD'||(LA20_0>='\u09DC' && LA20_0<='\u09DD')||(LA20_0>='\u09DF' && LA20_0<='\u09E1')||(LA20_0>='\u09F0' && LA20_0<='\u09F3')||(LA20_0>='\u0A05' && LA20_0<='\u0A0A')||(LA20_0>='\u0A0F' && LA20_0<='\u0A10')||(LA20_0>='\u0A13' && LA20_0<='\u0A28')||(LA20_0>='\u0A2A' && LA20_0<='\u0A30')||(LA20_0>='\u0A32' && LA20_0<='\u0A33')||(LA20_0>='\u0A35' && LA20_0<='\u0A36')||(LA20_0>='\u0A38' && LA20_0<='\u0A39')||(LA20_0>='\u0A59' && LA20_0<='\u0A5C')||LA20_0=='\u0A5E'||(LA20_0>='\u0A72' && LA20_0<='\u0A74')||(LA20_0>='\u0A85' && LA20_0<='\u0A8D')||(LA20_0>='\u0A8F' && LA20_0<='\u0A91')||(LA20_0>='\u0A93' && LA20_0<='\u0AA8')||(LA20_0>='\u0AAA' && LA20_0<='\u0AB0')||(LA20_0>='\u0AB2' && LA20_0<='\u0AB3')||(LA20_0>='\u0AB5' && LA20_0<='\u0AB9')||LA20_0=='\u0ABD'||LA20_0=='\u0AD0'||(LA20_0>='\u0AE0' && LA20_0<='\u0AE1')||LA20_0=='\u0AF1'||(LA20_0>='\u0B05' && LA20_0<='\u0B0C')||(LA20_0>='\u0B0F' && LA20_0<='\u0B10')||(LA20_0>='\u0B13' && LA20_0<='\u0B28')||(LA20_0>='\u0B2A' && LA20_0<='\u0B30')||(LA20_0>='\u0B32' && LA20_0<='\u0B33')||(LA20_0>='\u0B35' && LA20_0<='\u0B39')||LA20_0=='\u0B3D'||(LA20_0>='\u0B5C' && LA20_0<='\u0B5D')||(LA20_0>='\u0B5F' && LA20_0<='\u0B61')||LA20_0=='\u0B71'||LA20_0=='\u0B83'||(LA20_0>='\u0B85' && LA20_0<='\u0B8A')||(LA20_0>='\u0B8E' && LA20_0<='\u0B90')||(LA20_0>='\u0B92' && LA20_0<='\u0B95')||(LA20_0>='\u0B99' && LA20_0<='\u0B9A')||LA20_0=='\u0B9C'||(LA20_0>='\u0B9E' && LA20_0<='\u0B9F')||(LA20_0>='\u0BA3' && LA20_0<='\u0BA4')||(LA20_0>='\u0BA8' && LA20_0<='\u0BAA')||(LA20_0>='\u0BAE' && LA20_0<='\u0BB5')||(LA20_0>='\u0BB7' && LA20_0<='\u0BB9')||LA20_0=='\u0BF9'||(LA20_0>='\u0C05' && LA20_0<='\u0C0C')||(LA20_0>='\u0C0E' && LA20_0<='\u0C10')||(LA20_0>='\u0C12' && LA20_0<='\u0C28')||(LA20_0>='\u0C2A' && LA20_0<='\u0C33')||(LA20_0>='\u0C35' && LA20_0<='\u0C39')||(LA20_0>='\u0C60' && LA20_0<='\u0C61')||(LA20_0>='\u0C85' && LA20_0<='\u0C8C')||(LA20_0>='\u0C8E' && LA20_0<='\u0C90')||(LA20_0>='\u0C92' && LA20_0<='\u0CA8')||(LA20_0>='\u0CAA' && LA20_0<='\u0CB3')||(LA20_0>='\u0CB5' && LA20_0<='\u0CB9')||LA20_0=='\u0CBD'||LA20_0=='\u0CDE'||(LA20_0>='\u0CE0' && LA20_0<='\u0CE1')||(LA20_0>='\u0D05' && LA20_0<='\u0D0C')||(LA20_0>='\u0D0E' && LA20_0<='\u0D10')||(LA20_0>='\u0D12' && LA20_0<='\u0D28')||(LA20_0>='\u0D2A' && LA20_0<='\u0D39')||(LA20_0>='\u0D60' && LA20_0<='\u0D61')||(LA20_0>='\u0D85' && LA20_0<='\u0D96')||(LA20_0>='\u0D9A' && LA20_0<='\u0DB1')||(LA20_0>='\u0DB3' && LA20_0<='\u0DBB')||LA20_0=='\u0DBD'||(LA20_0>='\u0DC0' && LA20_0<='\u0DC6')||(LA20_0>='\u0E01' && LA20_0<='\u0E30')||(LA20_0>='\u0E32' && LA20_0<='\u0E33')||(LA20_0>='\u0E3F' && LA20_0<='\u0E46')||(LA20_0>='\u0E81' && LA20_0<='\u0E82')||LA20_0=='\u0E84'||(LA20_0>='\u0E87' && LA20_0<='\u0E88')||LA20_0=='\u0E8A'||LA20_0=='\u0E8D'||(LA20_0>='\u0E94' && LA20_0<='\u0E97')||(LA20_0>='\u0E99' && LA20_0<='\u0E9F')||(LA20_0>='\u0EA1' && LA20_0<='\u0EA3')||LA20_0=='\u0EA5'||LA20_0=='\u0EA7'||(LA20_0>='\u0EAA' && LA20_0<='\u0EAB')||(LA20_0>='\u0EAD' && LA20_0<='\u0EB0')||(LA20_0>='\u0EB2' && LA20_0<='\u0EB3')||LA20_0=='\u0EBD'||(LA20_0>='\u0EC0' && LA20_0<='\u0EC4')||LA20_0=='\u0EC6'||(LA20_0>='\u0EDC' && LA20_0<='\u0EDD')||LA20_0=='\u0F00'||(LA20_0>='\u0F40' && LA20_0<='\u0F47')||(LA20_0>='\u0F49' && LA20_0<='\u0F6A')||(LA20_0>='\u0F88' && LA20_0<='\u0F8B')||(LA20_0>='\u1000' && LA20_0<='\u1021')||(LA20_0>='\u1023' && LA20_0<='\u1027')||(LA20_0>='\u1029' && LA20_0<='\u102A')||(LA20_0>='\u1050' && LA20_0<='\u1055')||(LA20_0>='\u10A0' && LA20_0<='\u10C5')||(LA20_0>='\u10D0' && LA20_0<='\u10F8')||(LA20_0>='\u1100' && LA20_0<='\u1159')||(LA20_0>='\u115F' && LA20_0<='\u11A2')||(LA20_0>='\u11A8' && LA20_0<='\u11F9')||(LA20_0>='\u1200' && LA20_0<='\u1206')||(LA20_0>='\u1208' && LA20_0<='\u1246')||LA20_0=='\u1248'||(LA20_0>='\u124A' && LA20_0<='\u124D')||(LA20_0>='\u1250' && LA20_0<='\u1256')||LA20_0=='\u1258'||(LA20_0>='\u125A' && LA20_0<='\u125D')||(LA20_0>='\u1260' && LA20_0<='\u1286')||LA20_0=='\u1288'||(LA20_0>='\u128A' && LA20_0<='\u128D')||(LA20_0>='\u1290' && LA20_0<='\u12AE')||LA20_0=='\u12B0'||(LA20_0>='\u12B2' && LA20_0<='\u12B5')||(LA20_0>='\u12B8' && LA20_0<='\u12BE')||LA20_0=='\u12C0'||(LA20_0>='\u12C2' && LA20_0<='\u12C5')||(LA20_0>='\u12C8' && LA20_0<='\u12CE')||(LA20_0>='\u12D0' && LA20_0<='\u12D6')||(LA20_0>='\u12D8' && LA20_0<='\u12EE')||(LA20_0>='\u12F0' && LA20_0<='\u130E')||LA20_0=='\u1310'||(LA20_0>='\u1312' && LA20_0<='\u1315')||(LA20_0>='\u1318' && LA20_0<='\u131E')||(LA20_0>='\u1320' && LA20_0<='\u1346')||(LA20_0>='\u1348' && LA20_0<='\u135A')||(LA20_0>='\u13A0' && LA20_0<='\u13F4')||(LA20_0>='\u1401' && LA20_0<='\u166C')||(LA20_0>='\u166F' && LA20_0<='\u1676')||(LA20_0>='\u1681' && LA20_0<='\u169A')||(LA20_0>='\u16A0' && LA20_0<='\u16EA')||(LA20_0>='\u16EE' && LA20_0<='\u16F0')||(LA20_0>='\u1700' && LA20_0<='\u170C')||(LA20_0>='\u170E' && LA20_0<='\u1711')||(LA20_0>='\u1720' && LA20_0<='\u1731')||(LA20_0>='\u1740' && LA20_0<='\u1751')||(LA20_0>='\u1760' && LA20_0<='\u176C')||(LA20_0>='\u176E' && LA20_0<='\u1770')||(LA20_0>='\u1780' && LA20_0<='\u17B3')||LA20_0=='\u17D7'||(LA20_0>='\u17DB' && LA20_0<='\u17DC')||(LA20_0>='\u1820' && LA20_0<='\u1877')||(LA20_0>='\u1880' && LA20_0<='\u18A8')||(LA20_0>='\u1900' && LA20_0<='\u191C')||(LA20_0>='\u1950' && LA20_0<='\u196D')||(LA20_0>='\u1970' && LA20_0<='\u1974')||(LA20_0>='\u1D00' && LA20_0<='\u1D6B')||(LA20_0>='\u1E00' && LA20_0<='\u1E9B')||(LA20_0>='\u1EA0' && LA20_0<='\u1EF9')||(LA20_0>='\u1F00' && LA20_0<='\u1F15')||(LA20_0>='\u1F18' && LA20_0<='\u1F1D')||(LA20_0>='\u1F20' && LA20_0<='\u1F45')||(LA20_0>='\u1F48' && LA20_0<='\u1F4D')||(LA20_0>='\u1F50' && LA20_0<='\u1F57')||LA20_0=='\u1F59'||LA20_0=='\u1F5B'||LA20_0=='\u1F5D'||(LA20_0>='\u1F5F' && LA20_0<='\u1F7D')||(LA20_0>='\u1F80' && LA20_0<='\u1FB4')||(LA20_0>='\u1FB6' && LA20_0<='\u1FBC')||LA20_0=='\u1FBE'||(LA20_0>='\u1FC2' && LA20_0<='\u1FC4')||(LA20_0>='\u1FC6' && LA20_0<='\u1FCC')||(LA20_0>='\u1FD0' && LA20_0<='\u1FD3')||(LA20_0>='\u1FD6' && LA20_0<='\u1FDB')||(LA20_0>='\u1FE0' && LA20_0<='\u1FEC')||(LA20_0>='\u1FF2' && LA20_0<='\u1FF4')||(LA20_0>='\u1FF6' && LA20_0<='\u1FFC')||(LA20_0>='\u203F' && LA20_0<='\u2040')||LA20_0=='\u2054'||LA20_0=='\u2071'||LA20_0=='\u207F'||(LA20_0>='\u20A0' && LA20_0<='\u20B1')||LA20_0=='\u2102'||LA20_0=='\u2107'||(LA20_0>='\u210A' && LA20_0<='\u2113')||LA20_0=='\u2115'||(LA20_0>='\u2119' && LA20_0<='\u211D')||LA20_0=='\u2124'||LA20_0=='\u2126'||LA20_0=='\u2128'||(LA20_0>='\u212A' && LA20_0<='\u212D')||(LA20_0>='\u212F' && LA20_0<='\u2131')||(LA20_0>='\u2133' && LA20_0<='\u2139')||(LA20_0>='\u213D' && LA20_0<='\u213F')||(LA20_0>='\u2145' && LA20_0<='\u2149')||(LA20_0>='\u2160' && LA20_0<='\u2183')||(LA20_0>='\u3005' && LA20_0<='\u3007')||(LA20_0>='\u3021' && LA20_0<='\u3029')||(LA20_0>='\u3031' && LA20_0<='\u3035')||(LA20_0>='\u3038' && LA20_0<='\u303C')||(LA20_0>='\u3041' && LA20_0<='\u3096')||(LA20_0>='\u309D' && LA20_0<='\u309F')||(LA20_0>='\u30A1' && LA20_0<='\u30FF')||(LA20_0>='\u3105' && LA20_0<='\u312C')||(LA20_0>='\u3131' && LA20_0<='\u318E')||(LA20_0>='\u31A0' && LA20_0<='\u31B7')||(LA20_0>='\u31F0' && LA20_0<='\u31FF')||(LA20_0>='\u3400' && LA20_0<='\u4DB5')||(LA20_0>='\u4E00' && LA20_0<='\u9FA5')||(LA20_0>='\uA000' && LA20_0<='\uA48C')||(LA20_0>='\uAC00' && LA20_0<='\uD7A3')||(LA20_0>='\uF900' && LA20_0<='\uFA2D')||(LA20_0>='\uFA30' && LA20_0<='\uFA6A')||(LA20_0>='\uFB00' && LA20_0<='\uFB06')||(LA20_0>='\uFB13' && LA20_0<='\uFB17')||LA20_0=='\uFB1D'||(LA20_0>='\uFB1F' && LA20_0<='\uFB28')||(LA20_0>='\uFB2A' && LA20_0<='\uFB36')||(LA20_0>='\uFB38' && LA20_0<='\uFB3C')||LA20_0=='\uFB3E'||(LA20_0>='\uFB40' && LA20_0<='\uFB41')||(LA20_0>='\uFB43' && LA20_0<='\uFB44')||(LA20_0>='\uFB46' && LA20_0<='\uFBB1')||(LA20_0>='\uFBD3' && LA20_0<='\uFD3D')||(LA20_0>='\uFD50' && LA20_0<='\uFD8F')||(LA20_0>='\uFD92' && LA20_0<='\uFDC7')||(LA20_0>='\uFDF0' && LA20_0<='\uFDFC')||(LA20_0>='\uFE33' && LA20_0<='\uFE34')||(LA20_0>='\uFE4D' && LA20_0<='\uFE4F')||LA20_0=='\uFE69'||(LA20_0>='\uFE70' && LA20_0<='\uFE74')||(LA20_0>='\uFE76' && LA20_0<='\uFEFC')||LA20_0=='\uFF04'||(LA20_0>='\uFF21' && LA20_0<='\uFF3A')||LA20_0=='\uFF3F'||(LA20_0>='\uFF41' && LA20_0<='\uFF5A')||(LA20_0>='\uFF65' && LA20_0<='\uFFBE')||(LA20_0>='\uFFC2' && LA20_0<='\uFFC7')||(LA20_0>='\uFFCA' && LA20_0<='\uFFCF')||(LA20_0>='\uFFD2' && LA20_0<='\uFFD7')||(LA20_0>='\uFFDA' && LA20_0<='\uFFDC')||(LA20_0>='\uFFE0' && LA20_0<='\uFFE1')||(LA20_0>='\uFFE5' && LA20_0<='\uFFE6')) ) {
                alt20=1;
            }
            else if ( (LA20_0=='`') ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:303:7: IdentifierStart ( IdentifierPart )*
                    {
                    mIdentifierStart(); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:303:23: ( IdentifierPart )*
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( ((LA18_0>='\u0000' && LA18_0<='\b')||(LA18_0>='\u000E' && LA18_0<='\u001B')||LA18_0=='$'||(LA18_0>='0' && LA18_0<='9')||(LA18_0>='A' && LA18_0<='Z')||LA18_0=='_'||(LA18_0>='a' && LA18_0<='z')||(LA18_0>='\u007F' && LA18_0<='\u009F')||(LA18_0>='\u00A2' && LA18_0<='\u00A5')||LA18_0=='\u00AA'||LA18_0=='\u00AD'||LA18_0=='\u00B5'||LA18_0=='\u00BA'||(LA18_0>='\u00C0' && LA18_0<='\u00D6')||(LA18_0>='\u00D8' && LA18_0<='\u00F6')||(LA18_0>='\u00F8' && LA18_0<='\u0236')||(LA18_0>='\u0250' && LA18_0<='\u02C1')||(LA18_0>='\u02C6' && LA18_0<='\u02D1')||(LA18_0>='\u02E0' && LA18_0<='\u02E4')||LA18_0=='\u02EE'||(LA18_0>='\u0300' && LA18_0<='\u0357')||(LA18_0>='\u035D' && LA18_0<='\u036F')||LA18_0=='\u037A'||LA18_0=='\u0386'||(LA18_0>='\u0388' && LA18_0<='\u038A')||LA18_0=='\u038C'||(LA18_0>='\u038E' && LA18_0<='\u03A1')||(LA18_0>='\u03A3' && LA18_0<='\u03CE')||(LA18_0>='\u03D0' && LA18_0<='\u03F5')||(LA18_0>='\u03F7' && LA18_0<='\u03FB')||(LA18_0>='\u0400' && LA18_0<='\u0481')||(LA18_0>='\u0483' && LA18_0<='\u0486')||(LA18_0>='\u048A' && LA18_0<='\u04CE')||(LA18_0>='\u04D0' && LA18_0<='\u04F5')||(LA18_0>='\u04F8' && LA18_0<='\u04F9')||(LA18_0>='\u0500' && LA18_0<='\u050F')||(LA18_0>='\u0531' && LA18_0<='\u0556')||LA18_0=='\u0559'||(LA18_0>='\u0561' && LA18_0<='\u0587')||(LA18_0>='\u0591' && LA18_0<='\u05A1')||(LA18_0>='\u05A3' && LA18_0<='\u05B9')||(LA18_0>='\u05BB' && LA18_0<='\u05BD')||LA18_0=='\u05BF'||(LA18_0>='\u05C1' && LA18_0<='\u05C2')||LA18_0=='\u05C4'||(LA18_0>='\u05D0' && LA18_0<='\u05EA')||(LA18_0>='\u05F0' && LA18_0<='\u05F2')||(LA18_0>='\u0600' && LA18_0<='\u0603')||(LA18_0>='\u0610' && LA18_0<='\u0615')||(LA18_0>='\u0621' && LA18_0<='\u063A')||(LA18_0>='\u0640' && LA18_0<='\u0658')||(LA18_0>='\u0660' && LA18_0<='\u0669')||(LA18_0>='\u066E' && LA18_0<='\u06D3')||(LA18_0>='\u06D5' && LA18_0<='\u06DD')||(LA18_0>='\u06DF' && LA18_0<='\u06E8')||(LA18_0>='\u06EA' && LA18_0<='\u06FC')||LA18_0=='\u06FF'||(LA18_0>='\u070F' && LA18_0<='\u074A')||(LA18_0>='\u074D' && LA18_0<='\u074F')||(LA18_0>='\u0780' && LA18_0<='\u07B1')||(LA18_0>='\u0901' && LA18_0<='\u0939')||(LA18_0>='\u093C' && LA18_0<='\u094D')||(LA18_0>='\u0950' && LA18_0<='\u0954')||(LA18_0>='\u0958' && LA18_0<='\u0963')||(LA18_0>='\u0966' && LA18_0<='\u096F')||(LA18_0>='\u0981' && LA18_0<='\u0983')||(LA18_0>='\u0985' && LA18_0<='\u098C')||(LA18_0>='\u098F' && LA18_0<='\u0990')||(LA18_0>='\u0993' && LA18_0<='\u09A8')||(LA18_0>='\u09AA' && LA18_0<='\u09B0')||LA18_0=='\u09B2'||(LA18_0>='\u09B6' && LA18_0<='\u09B9')||(LA18_0>='\u09BC' && LA18_0<='\u09C4')||(LA18_0>='\u09C7' && LA18_0<='\u09C8')||(LA18_0>='\u09CB' && LA18_0<='\u09CD')||LA18_0=='\u09D7'||(LA18_0>='\u09DC' && LA18_0<='\u09DD')||(LA18_0>='\u09DF' && LA18_0<='\u09E3')||(LA18_0>='\u09E6' && LA18_0<='\u09F3')||(LA18_0>='\u0A01' && LA18_0<='\u0A03')||(LA18_0>='\u0A05' && LA18_0<='\u0A0A')||(LA18_0>='\u0A0F' && LA18_0<='\u0A10')||(LA18_0>='\u0A13' && LA18_0<='\u0A28')||(LA18_0>='\u0A2A' && LA18_0<='\u0A30')||(LA18_0>='\u0A32' && LA18_0<='\u0A33')||(LA18_0>='\u0A35' && LA18_0<='\u0A36')||(LA18_0>='\u0A38' && LA18_0<='\u0A39')||LA18_0=='\u0A3C'||(LA18_0>='\u0A3E' && LA18_0<='\u0A42')||(LA18_0>='\u0A47' && LA18_0<='\u0A48')||(LA18_0>='\u0A4B' && LA18_0<='\u0A4D')||(LA18_0>='\u0A59' && LA18_0<='\u0A5C')||LA18_0=='\u0A5E'||(LA18_0>='\u0A66' && LA18_0<='\u0A74')||(LA18_0>='\u0A81' && LA18_0<='\u0A83')||(LA18_0>='\u0A85' && LA18_0<='\u0A8D')||(LA18_0>='\u0A8F' && LA18_0<='\u0A91')||(LA18_0>='\u0A93' && LA18_0<='\u0AA8')||(LA18_0>='\u0AAA' && LA18_0<='\u0AB0')||(LA18_0>='\u0AB2' && LA18_0<='\u0AB3')||(LA18_0>='\u0AB5' && LA18_0<='\u0AB9')||(LA18_0>='\u0ABC' && LA18_0<='\u0AC5')||(LA18_0>='\u0AC7' && LA18_0<='\u0AC9')||(LA18_0>='\u0ACB' && LA18_0<='\u0ACD')||LA18_0=='\u0AD0'||(LA18_0>='\u0AE0' && LA18_0<='\u0AE3')||(LA18_0>='\u0AE6' && LA18_0<='\u0AEF')||LA18_0=='\u0AF1'||(LA18_0>='\u0B01' && LA18_0<='\u0B03')||(LA18_0>='\u0B05' && LA18_0<='\u0B0C')||(LA18_0>='\u0B0F' && LA18_0<='\u0B10')||(LA18_0>='\u0B13' && LA18_0<='\u0B28')||(LA18_0>='\u0B2A' && LA18_0<='\u0B30')||(LA18_0>='\u0B32' && LA18_0<='\u0B33')||(LA18_0>='\u0B35' && LA18_0<='\u0B39')||(LA18_0>='\u0B3C' && LA18_0<='\u0B43')||(LA18_0>='\u0B47' && LA18_0<='\u0B48')||(LA18_0>='\u0B4B' && LA18_0<='\u0B4D')||(LA18_0>='\u0B56' && LA18_0<='\u0B57')||(LA18_0>='\u0B5C' && LA18_0<='\u0B5D')||(LA18_0>='\u0B5F' && LA18_0<='\u0B61')||(LA18_0>='\u0B66' && LA18_0<='\u0B6F')||LA18_0=='\u0B71'||(LA18_0>='\u0B82' && LA18_0<='\u0B83')||(LA18_0>='\u0B85' && LA18_0<='\u0B8A')||(LA18_0>='\u0B8E' && LA18_0<='\u0B90')||(LA18_0>='\u0B92' && LA18_0<='\u0B95')||(LA18_0>='\u0B99' && LA18_0<='\u0B9A')||LA18_0=='\u0B9C'||(LA18_0>='\u0B9E' && LA18_0<='\u0B9F')||(LA18_0>='\u0BA3' && LA18_0<='\u0BA4')||(LA18_0>='\u0BA8' && LA18_0<='\u0BAA')||(LA18_0>='\u0BAE' && LA18_0<='\u0BB5')||(LA18_0>='\u0BB7' && LA18_0<='\u0BB9')||(LA18_0>='\u0BBE' && LA18_0<='\u0BC2')||(LA18_0>='\u0BC6' && LA18_0<='\u0BC8')||(LA18_0>='\u0BCA' && LA18_0<='\u0BCD')||LA18_0=='\u0BD7'||(LA18_0>='\u0BE7' && LA18_0<='\u0BEF')||LA18_0=='\u0BF9'||(LA18_0>='\u0C01' && LA18_0<='\u0C03')||(LA18_0>='\u0C05' && LA18_0<='\u0C0C')||(LA18_0>='\u0C0E' && LA18_0<='\u0C10')||(LA18_0>='\u0C12' && LA18_0<='\u0C28')||(LA18_0>='\u0C2A' && LA18_0<='\u0C33')||(LA18_0>='\u0C35' && LA18_0<='\u0C39')||(LA18_0>='\u0C3E' && LA18_0<='\u0C44')||(LA18_0>='\u0C46' && LA18_0<='\u0C48')||(LA18_0>='\u0C4A' && LA18_0<='\u0C4D')||(LA18_0>='\u0C55' && LA18_0<='\u0C56')||(LA18_0>='\u0C60' && LA18_0<='\u0C61')||(LA18_0>='\u0C66' && LA18_0<='\u0C6F')||(LA18_0>='\u0C82' && LA18_0<='\u0C83')||(LA18_0>='\u0C85' && LA18_0<='\u0C8C')||(LA18_0>='\u0C8E' && LA18_0<='\u0C90')||(LA18_0>='\u0C92' && LA18_0<='\u0CA8')||(LA18_0>='\u0CAA' && LA18_0<='\u0CB3')||(LA18_0>='\u0CB5' && LA18_0<='\u0CB9')||(LA18_0>='\u0CBC' && LA18_0<='\u0CC4')||(LA18_0>='\u0CC6' && LA18_0<='\u0CC8')||(LA18_0>='\u0CCA' && LA18_0<='\u0CCD')||(LA18_0>='\u0CD5' && LA18_0<='\u0CD6')||LA18_0=='\u0CDE'||(LA18_0>='\u0CE0' && LA18_0<='\u0CE1')||(LA18_0>='\u0CE6' && LA18_0<='\u0CEF')||(LA18_0>='\u0D02' && LA18_0<='\u0D03')||(LA18_0>='\u0D05' && LA18_0<='\u0D0C')||(LA18_0>='\u0D0E' && LA18_0<='\u0D10')||(LA18_0>='\u0D12' && LA18_0<='\u0D28')||(LA18_0>='\u0D2A' && LA18_0<='\u0D39')||(LA18_0>='\u0D3E' && LA18_0<='\u0D43')||(LA18_0>='\u0D46' && LA18_0<='\u0D48')||(LA18_0>='\u0D4A' && LA18_0<='\u0D4D')||LA18_0=='\u0D57'||(LA18_0>='\u0D60' && LA18_0<='\u0D61')||(LA18_0>='\u0D66' && LA18_0<='\u0D6F')||(LA18_0>='\u0D82' && LA18_0<='\u0D83')||(LA18_0>='\u0D85' && LA18_0<='\u0D96')||(LA18_0>='\u0D9A' && LA18_0<='\u0DB1')||(LA18_0>='\u0DB3' && LA18_0<='\u0DBB')||LA18_0=='\u0DBD'||(LA18_0>='\u0DC0' && LA18_0<='\u0DC6')||LA18_0=='\u0DCA'||(LA18_0>='\u0DCF' && LA18_0<='\u0DD4')||LA18_0=='\u0DD6'||(LA18_0>='\u0DD8' && LA18_0<='\u0DDF')||(LA18_0>='\u0DF2' && LA18_0<='\u0DF3')||(LA18_0>='\u0E01' && LA18_0<='\u0E3A')||(LA18_0>='\u0E3F' && LA18_0<='\u0E4E')||(LA18_0>='\u0E50' && LA18_0<='\u0E59')||(LA18_0>='\u0E81' && LA18_0<='\u0E82')||LA18_0=='\u0E84'||(LA18_0>='\u0E87' && LA18_0<='\u0E88')||LA18_0=='\u0E8A'||LA18_0=='\u0E8D'||(LA18_0>='\u0E94' && LA18_0<='\u0E97')||(LA18_0>='\u0E99' && LA18_0<='\u0E9F')||(LA18_0>='\u0EA1' && LA18_0<='\u0EA3')||LA18_0=='\u0EA5'||LA18_0=='\u0EA7'||(LA18_0>='\u0EAA' && LA18_0<='\u0EAB')||(LA18_0>='\u0EAD' && LA18_0<='\u0EB9')||(LA18_0>='\u0EBB' && LA18_0<='\u0EBD')||(LA18_0>='\u0EC0' && LA18_0<='\u0EC4')||LA18_0=='\u0EC6'||(LA18_0>='\u0EC8' && LA18_0<='\u0ECD')||(LA18_0>='\u0ED0' && LA18_0<='\u0ED9')||(LA18_0>='\u0EDC' && LA18_0<='\u0EDD')||LA18_0=='\u0F00'||(LA18_0>='\u0F18' && LA18_0<='\u0F19')||(LA18_0>='\u0F20' && LA18_0<='\u0F29')||LA18_0=='\u0F35'||LA18_0=='\u0F37'||LA18_0=='\u0F39'||(LA18_0>='\u0F3E' && LA18_0<='\u0F47')||(LA18_0>='\u0F49' && LA18_0<='\u0F6A')||(LA18_0>='\u0F71' && LA18_0<='\u0F84')||(LA18_0>='\u0F86' && LA18_0<='\u0F8B')||(LA18_0>='\u0F90' && LA18_0<='\u0F97')||(LA18_0>='\u0F99' && LA18_0<='\u0FBC')||LA18_0=='\u0FC6'||(LA18_0>='\u1000' && LA18_0<='\u1021')||(LA18_0>='\u1023' && LA18_0<='\u1027')||(LA18_0>='\u1029' && LA18_0<='\u102A')||(LA18_0>='\u102C' && LA18_0<='\u1032')||(LA18_0>='\u1036' && LA18_0<='\u1039')||(LA18_0>='\u1040' && LA18_0<='\u1049')||(LA18_0>='\u1050' && LA18_0<='\u1059')||(LA18_0>='\u10A0' && LA18_0<='\u10C5')||(LA18_0>='\u10D0' && LA18_0<='\u10F8')||(LA18_0>='\u1100' && LA18_0<='\u1159')||(LA18_0>='\u115F' && LA18_0<='\u11A2')||(LA18_0>='\u11A8' && LA18_0<='\u11F9')||(LA18_0>='\u1200' && LA18_0<='\u1206')||(LA18_0>='\u1208' && LA18_0<='\u1246')||LA18_0=='\u1248'||(LA18_0>='\u124A' && LA18_0<='\u124D')||(LA18_0>='\u1250' && LA18_0<='\u1256')||LA18_0=='\u1258'||(LA18_0>='\u125A' && LA18_0<='\u125D')||(LA18_0>='\u1260' && LA18_0<='\u1286')||LA18_0=='\u1288'||(LA18_0>='\u128A' && LA18_0<='\u128D')||(LA18_0>='\u1290' && LA18_0<='\u12AE')||LA18_0=='\u12B0'||(LA18_0>='\u12B2' && LA18_0<='\u12B5')||(LA18_0>='\u12B8' && LA18_0<='\u12BE')||LA18_0=='\u12C0'||(LA18_0>='\u12C2' && LA18_0<='\u12C5')||(LA18_0>='\u12C8' && LA18_0<='\u12CE')||(LA18_0>='\u12D0' && LA18_0<='\u12D6')||(LA18_0>='\u12D8' && LA18_0<='\u12EE')||(LA18_0>='\u12F0' && LA18_0<='\u130E')||LA18_0=='\u1310'||(LA18_0>='\u1312' && LA18_0<='\u1315')||(LA18_0>='\u1318' && LA18_0<='\u131E')||(LA18_0>='\u1320' && LA18_0<='\u1346')||(LA18_0>='\u1348' && LA18_0<='\u135A')||(LA18_0>='\u1369' && LA18_0<='\u1371')||(LA18_0>='\u13A0' && LA18_0<='\u13F4')||(LA18_0>='\u1401' && LA18_0<='\u166C')||(LA18_0>='\u166F' && LA18_0<='\u1676')||(LA18_0>='\u1681' && LA18_0<='\u169A')||(LA18_0>='\u16A0' && LA18_0<='\u16EA')||(LA18_0>='\u16EE' && LA18_0<='\u16F0')||(LA18_0>='\u1700' && LA18_0<='\u170C')||(LA18_0>='\u170E' && LA18_0<='\u1714')||(LA18_0>='\u1720' && LA18_0<='\u1734')||(LA18_0>='\u1740' && LA18_0<='\u1753')||(LA18_0>='\u1760' && LA18_0<='\u176C')||(LA18_0>='\u176E' && LA18_0<='\u1770')||(LA18_0>='\u1772' && LA18_0<='\u1773')||(LA18_0>='\u1780' && LA18_0<='\u17D3')||LA18_0=='\u17D7'||(LA18_0>='\u17DB' && LA18_0<='\u17DD')||(LA18_0>='\u17E0' && LA18_0<='\u17E9')||(LA18_0>='\u180B' && LA18_0<='\u180D')||(LA18_0>='\u1810' && LA18_0<='\u1819')||(LA18_0>='\u1820' && LA18_0<='\u1877')||(LA18_0>='\u1880' && LA18_0<='\u18A9')||(LA18_0>='\u1900' && LA18_0<='\u191C')||(LA18_0>='\u1920' && LA18_0<='\u192B')||(LA18_0>='\u1930' && LA18_0<='\u193B')||(LA18_0>='\u1946' && LA18_0<='\u196D')||(LA18_0>='\u1970' && LA18_0<='\u1974')||(LA18_0>='\u1D00' && LA18_0<='\u1D6B')||(LA18_0>='\u1E00' && LA18_0<='\u1E9B')||(LA18_0>='\u1EA0' && LA18_0<='\u1EF9')||(LA18_0>='\u1F00' && LA18_0<='\u1F15')||(LA18_0>='\u1F18' && LA18_0<='\u1F1D')||(LA18_0>='\u1F20' && LA18_0<='\u1F45')||(LA18_0>='\u1F48' && LA18_0<='\u1F4D')||(LA18_0>='\u1F50' && LA18_0<='\u1F57')||LA18_0=='\u1F59'||LA18_0=='\u1F5B'||LA18_0=='\u1F5D'||(LA18_0>='\u1F5F' && LA18_0<='\u1F7D')||(LA18_0>='\u1F80' && LA18_0<='\u1FB4')||(LA18_0>='\u1FB6' && LA18_0<='\u1FBC')||LA18_0=='\u1FBE'||(LA18_0>='\u1FC2' && LA18_0<='\u1FC4')||(LA18_0>='\u1FC6' && LA18_0<='\u1FCC')||(LA18_0>='\u1FD0' && LA18_0<='\u1FD3')||(LA18_0>='\u1FD6' && LA18_0<='\u1FDB')||(LA18_0>='\u1FE0' && LA18_0<='\u1FEC')||(LA18_0>='\u1FF2' && LA18_0<='\u1FF4')||(LA18_0>='\u1FF6' && LA18_0<='\u1FFC')||(LA18_0>='\u200C' && LA18_0<='\u200F')||(LA18_0>='\u202A' && LA18_0<='\u202E')||(LA18_0>='\u203F' && LA18_0<='\u2040')||LA18_0=='\u2054'||(LA18_0>='\u2060' && LA18_0<='\u2063')||(LA18_0>='\u206A' && LA18_0<='\u206F')||LA18_0=='\u2071'||LA18_0=='\u207F'||(LA18_0>='\u20A0' && LA18_0<='\u20B1')||(LA18_0>='\u20D0' && LA18_0<='\u20DC')||LA18_0=='\u20E1'||(LA18_0>='\u20E5' && LA18_0<='\u20EA')||LA18_0=='\u2102'||LA18_0=='\u2107'||(LA18_0>='\u210A' && LA18_0<='\u2113')||LA18_0=='\u2115'||(LA18_0>='\u2119' && LA18_0<='\u211D')||LA18_0=='\u2124'||LA18_0=='\u2126'||LA18_0=='\u2128'||(LA18_0>='\u212A' && LA18_0<='\u212D')||(LA18_0>='\u212F' && LA18_0<='\u2131')||(LA18_0>='\u2133' && LA18_0<='\u2139')||(LA18_0>='\u213D' && LA18_0<='\u213F')||(LA18_0>='\u2145' && LA18_0<='\u2149')||(LA18_0>='\u2160' && LA18_0<='\u2183')||(LA18_0>='\u3005' && LA18_0<='\u3007')||(LA18_0>='\u3021' && LA18_0<='\u302F')||(LA18_0>='\u3031' && LA18_0<='\u3035')||(LA18_0>='\u3038' && LA18_0<='\u303C')||(LA18_0>='\u3041' && LA18_0<='\u3096')||(LA18_0>='\u3099' && LA18_0<='\u309A')||(LA18_0>='\u309D' && LA18_0<='\u309F')||(LA18_0>='\u30A1' && LA18_0<='\u30FF')||(LA18_0>='\u3105' && LA18_0<='\u312C')||(LA18_0>='\u3131' && LA18_0<='\u318E')||(LA18_0>='\u31A0' && LA18_0<='\u31B7')||(LA18_0>='\u31F0' && LA18_0<='\u31FF')||(LA18_0>='\u3400' && LA18_0<='\u4DB5')||(LA18_0>='\u4E00' && LA18_0<='\u9FA5')||(LA18_0>='\uA000' && LA18_0<='\uA48C')||(LA18_0>='\uAC00' && LA18_0<='\uD7A3')||(LA18_0>='\uF900' && LA18_0<='\uFA2D')||(LA18_0>='\uFA30' && LA18_0<='\uFA6A')||(LA18_0>='\uFB00' && LA18_0<='\uFB06')||(LA18_0>='\uFB13' && LA18_0<='\uFB17')||(LA18_0>='\uFB1D' && LA18_0<='\uFB28')||(LA18_0>='\uFB2A' && LA18_0<='\uFB36')||(LA18_0>='\uFB38' && LA18_0<='\uFB3C')||LA18_0=='\uFB3E'||(LA18_0>='\uFB40' && LA18_0<='\uFB41')||(LA18_0>='\uFB43' && LA18_0<='\uFB44')||(LA18_0>='\uFB46' && LA18_0<='\uFBB1')||(LA18_0>='\uFBD3' && LA18_0<='\uFD3D')||(LA18_0>='\uFD50' && LA18_0<='\uFD8F')||(LA18_0>='\uFD92' && LA18_0<='\uFDC7')||(LA18_0>='\uFDF0' && LA18_0<='\uFDFC')||(LA18_0>='\uFE00' && LA18_0<='\uFE0F')||(LA18_0>='\uFE20' && LA18_0<='\uFE23')||(LA18_0>='\uFE33' && LA18_0<='\uFE34')||(LA18_0>='\uFE4D' && LA18_0<='\uFE4F')||LA18_0=='\uFE69'||(LA18_0>='\uFE70' && LA18_0<='\uFE74')||(LA18_0>='\uFE76' && LA18_0<='\uFEFC')||LA18_0=='\uFEFF'||LA18_0=='\uFF04'||(LA18_0>='\uFF10' && LA18_0<='\uFF19')||(LA18_0>='\uFF21' && LA18_0<='\uFF3A')||LA18_0=='\uFF3F'||(LA18_0>='\uFF41' && LA18_0<='\uFF5A')||(LA18_0>='\uFF65' && LA18_0<='\uFFBE')||(LA18_0>='\uFFC2' && LA18_0<='\uFFC7')||(LA18_0>='\uFFCA' && LA18_0<='\uFFCF')||(LA18_0>='\uFFD2' && LA18_0<='\uFFD7')||(LA18_0>='\uFFDA' && LA18_0<='\uFFDC')||(LA18_0>='\uFFE0' && LA18_0<='\uFFE1')||(LA18_0>='\uFFE5' && LA18_0<='\uFFE6')||(LA18_0>='\uFFF9' && LA18_0<='\uFFFB')) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:303:23: IdentifierPart
                    	    {
                    	    mIdentifierPart(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:304:7: '`' IdentifierStart ( IdentifierPart )* '`'
                    {
                    match('`'); if (state.failed) return ;
                    mIdentifierStart(); if (state.failed) return ;
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:304:27: ( IdentifierPart )*
                    loop19:
                    do {
                        int alt19=2;
                        int LA19_0 = input.LA(1);

                        if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000E' && LA19_0<='\u001B')||LA19_0=='$'||(LA19_0>='0' && LA19_0<='9')||(LA19_0>='A' && LA19_0<='Z')||LA19_0=='_'||(LA19_0>='a' && LA19_0<='z')||(LA19_0>='\u007F' && LA19_0<='\u009F')||(LA19_0>='\u00A2' && LA19_0<='\u00A5')||LA19_0=='\u00AA'||LA19_0=='\u00AD'||LA19_0=='\u00B5'||LA19_0=='\u00BA'||(LA19_0>='\u00C0' && LA19_0<='\u00D6')||(LA19_0>='\u00D8' && LA19_0<='\u00F6')||(LA19_0>='\u00F8' && LA19_0<='\u0236')||(LA19_0>='\u0250' && LA19_0<='\u02C1')||(LA19_0>='\u02C6' && LA19_0<='\u02D1')||(LA19_0>='\u02E0' && LA19_0<='\u02E4')||LA19_0=='\u02EE'||(LA19_0>='\u0300' && LA19_0<='\u0357')||(LA19_0>='\u035D' && LA19_0<='\u036F')||LA19_0=='\u037A'||LA19_0=='\u0386'||(LA19_0>='\u0388' && LA19_0<='\u038A')||LA19_0=='\u038C'||(LA19_0>='\u038E' && LA19_0<='\u03A1')||(LA19_0>='\u03A3' && LA19_0<='\u03CE')||(LA19_0>='\u03D0' && LA19_0<='\u03F5')||(LA19_0>='\u03F7' && LA19_0<='\u03FB')||(LA19_0>='\u0400' && LA19_0<='\u0481')||(LA19_0>='\u0483' && LA19_0<='\u0486')||(LA19_0>='\u048A' && LA19_0<='\u04CE')||(LA19_0>='\u04D0' && LA19_0<='\u04F5')||(LA19_0>='\u04F8' && LA19_0<='\u04F9')||(LA19_0>='\u0500' && LA19_0<='\u050F')||(LA19_0>='\u0531' && LA19_0<='\u0556')||LA19_0=='\u0559'||(LA19_0>='\u0561' && LA19_0<='\u0587')||(LA19_0>='\u0591' && LA19_0<='\u05A1')||(LA19_0>='\u05A3' && LA19_0<='\u05B9')||(LA19_0>='\u05BB' && LA19_0<='\u05BD')||LA19_0=='\u05BF'||(LA19_0>='\u05C1' && LA19_0<='\u05C2')||LA19_0=='\u05C4'||(LA19_0>='\u05D0' && LA19_0<='\u05EA')||(LA19_0>='\u05F0' && LA19_0<='\u05F2')||(LA19_0>='\u0600' && LA19_0<='\u0603')||(LA19_0>='\u0610' && LA19_0<='\u0615')||(LA19_0>='\u0621' && LA19_0<='\u063A')||(LA19_0>='\u0640' && LA19_0<='\u0658')||(LA19_0>='\u0660' && LA19_0<='\u0669')||(LA19_0>='\u066E' && LA19_0<='\u06D3')||(LA19_0>='\u06D5' && LA19_0<='\u06DD')||(LA19_0>='\u06DF' && LA19_0<='\u06E8')||(LA19_0>='\u06EA' && LA19_0<='\u06FC')||LA19_0=='\u06FF'||(LA19_0>='\u070F' && LA19_0<='\u074A')||(LA19_0>='\u074D' && LA19_0<='\u074F')||(LA19_0>='\u0780' && LA19_0<='\u07B1')||(LA19_0>='\u0901' && LA19_0<='\u0939')||(LA19_0>='\u093C' && LA19_0<='\u094D')||(LA19_0>='\u0950' && LA19_0<='\u0954')||(LA19_0>='\u0958' && LA19_0<='\u0963')||(LA19_0>='\u0966' && LA19_0<='\u096F')||(LA19_0>='\u0981' && LA19_0<='\u0983')||(LA19_0>='\u0985' && LA19_0<='\u098C')||(LA19_0>='\u098F' && LA19_0<='\u0990')||(LA19_0>='\u0993' && LA19_0<='\u09A8')||(LA19_0>='\u09AA' && LA19_0<='\u09B0')||LA19_0=='\u09B2'||(LA19_0>='\u09B6' && LA19_0<='\u09B9')||(LA19_0>='\u09BC' && LA19_0<='\u09C4')||(LA19_0>='\u09C7' && LA19_0<='\u09C8')||(LA19_0>='\u09CB' && LA19_0<='\u09CD')||LA19_0=='\u09D7'||(LA19_0>='\u09DC' && LA19_0<='\u09DD')||(LA19_0>='\u09DF' && LA19_0<='\u09E3')||(LA19_0>='\u09E6' && LA19_0<='\u09F3')||(LA19_0>='\u0A01' && LA19_0<='\u0A03')||(LA19_0>='\u0A05' && LA19_0<='\u0A0A')||(LA19_0>='\u0A0F' && LA19_0<='\u0A10')||(LA19_0>='\u0A13' && LA19_0<='\u0A28')||(LA19_0>='\u0A2A' && LA19_0<='\u0A30')||(LA19_0>='\u0A32' && LA19_0<='\u0A33')||(LA19_0>='\u0A35' && LA19_0<='\u0A36')||(LA19_0>='\u0A38' && LA19_0<='\u0A39')||LA19_0=='\u0A3C'||(LA19_0>='\u0A3E' && LA19_0<='\u0A42')||(LA19_0>='\u0A47' && LA19_0<='\u0A48')||(LA19_0>='\u0A4B' && LA19_0<='\u0A4D')||(LA19_0>='\u0A59' && LA19_0<='\u0A5C')||LA19_0=='\u0A5E'||(LA19_0>='\u0A66' && LA19_0<='\u0A74')||(LA19_0>='\u0A81' && LA19_0<='\u0A83')||(LA19_0>='\u0A85' && LA19_0<='\u0A8D')||(LA19_0>='\u0A8F' && LA19_0<='\u0A91')||(LA19_0>='\u0A93' && LA19_0<='\u0AA8')||(LA19_0>='\u0AAA' && LA19_0<='\u0AB0')||(LA19_0>='\u0AB2' && LA19_0<='\u0AB3')||(LA19_0>='\u0AB5' && LA19_0<='\u0AB9')||(LA19_0>='\u0ABC' && LA19_0<='\u0AC5')||(LA19_0>='\u0AC7' && LA19_0<='\u0AC9')||(LA19_0>='\u0ACB' && LA19_0<='\u0ACD')||LA19_0=='\u0AD0'||(LA19_0>='\u0AE0' && LA19_0<='\u0AE3')||(LA19_0>='\u0AE6' && LA19_0<='\u0AEF')||LA19_0=='\u0AF1'||(LA19_0>='\u0B01' && LA19_0<='\u0B03')||(LA19_0>='\u0B05' && LA19_0<='\u0B0C')||(LA19_0>='\u0B0F' && LA19_0<='\u0B10')||(LA19_0>='\u0B13' && LA19_0<='\u0B28')||(LA19_0>='\u0B2A' && LA19_0<='\u0B30')||(LA19_0>='\u0B32' && LA19_0<='\u0B33')||(LA19_0>='\u0B35' && LA19_0<='\u0B39')||(LA19_0>='\u0B3C' && LA19_0<='\u0B43')||(LA19_0>='\u0B47' && LA19_0<='\u0B48')||(LA19_0>='\u0B4B' && LA19_0<='\u0B4D')||(LA19_0>='\u0B56' && LA19_0<='\u0B57')||(LA19_0>='\u0B5C' && LA19_0<='\u0B5D')||(LA19_0>='\u0B5F' && LA19_0<='\u0B61')||(LA19_0>='\u0B66' && LA19_0<='\u0B6F')||LA19_0=='\u0B71'||(LA19_0>='\u0B82' && LA19_0<='\u0B83')||(LA19_0>='\u0B85' && LA19_0<='\u0B8A')||(LA19_0>='\u0B8E' && LA19_0<='\u0B90')||(LA19_0>='\u0B92' && LA19_0<='\u0B95')||(LA19_0>='\u0B99' && LA19_0<='\u0B9A')||LA19_0=='\u0B9C'||(LA19_0>='\u0B9E' && LA19_0<='\u0B9F')||(LA19_0>='\u0BA3' && LA19_0<='\u0BA4')||(LA19_0>='\u0BA8' && LA19_0<='\u0BAA')||(LA19_0>='\u0BAE' && LA19_0<='\u0BB5')||(LA19_0>='\u0BB7' && LA19_0<='\u0BB9')||(LA19_0>='\u0BBE' && LA19_0<='\u0BC2')||(LA19_0>='\u0BC6' && LA19_0<='\u0BC8')||(LA19_0>='\u0BCA' && LA19_0<='\u0BCD')||LA19_0=='\u0BD7'||(LA19_0>='\u0BE7' && LA19_0<='\u0BEF')||LA19_0=='\u0BF9'||(LA19_0>='\u0C01' && LA19_0<='\u0C03')||(LA19_0>='\u0C05' && LA19_0<='\u0C0C')||(LA19_0>='\u0C0E' && LA19_0<='\u0C10')||(LA19_0>='\u0C12' && LA19_0<='\u0C28')||(LA19_0>='\u0C2A' && LA19_0<='\u0C33')||(LA19_0>='\u0C35' && LA19_0<='\u0C39')||(LA19_0>='\u0C3E' && LA19_0<='\u0C44')||(LA19_0>='\u0C46' && LA19_0<='\u0C48')||(LA19_0>='\u0C4A' && LA19_0<='\u0C4D')||(LA19_0>='\u0C55' && LA19_0<='\u0C56')||(LA19_0>='\u0C60' && LA19_0<='\u0C61')||(LA19_0>='\u0C66' && LA19_0<='\u0C6F')||(LA19_0>='\u0C82' && LA19_0<='\u0C83')||(LA19_0>='\u0C85' && LA19_0<='\u0C8C')||(LA19_0>='\u0C8E' && LA19_0<='\u0C90')||(LA19_0>='\u0C92' && LA19_0<='\u0CA8')||(LA19_0>='\u0CAA' && LA19_0<='\u0CB3')||(LA19_0>='\u0CB5' && LA19_0<='\u0CB9')||(LA19_0>='\u0CBC' && LA19_0<='\u0CC4')||(LA19_0>='\u0CC6' && LA19_0<='\u0CC8')||(LA19_0>='\u0CCA' && LA19_0<='\u0CCD')||(LA19_0>='\u0CD5' && LA19_0<='\u0CD6')||LA19_0=='\u0CDE'||(LA19_0>='\u0CE0' && LA19_0<='\u0CE1')||(LA19_0>='\u0CE6' && LA19_0<='\u0CEF')||(LA19_0>='\u0D02' && LA19_0<='\u0D03')||(LA19_0>='\u0D05' && LA19_0<='\u0D0C')||(LA19_0>='\u0D0E' && LA19_0<='\u0D10')||(LA19_0>='\u0D12' && LA19_0<='\u0D28')||(LA19_0>='\u0D2A' && LA19_0<='\u0D39')||(LA19_0>='\u0D3E' && LA19_0<='\u0D43')||(LA19_0>='\u0D46' && LA19_0<='\u0D48')||(LA19_0>='\u0D4A' && LA19_0<='\u0D4D')||LA19_0=='\u0D57'||(LA19_0>='\u0D60' && LA19_0<='\u0D61')||(LA19_0>='\u0D66' && LA19_0<='\u0D6F')||(LA19_0>='\u0D82' && LA19_0<='\u0D83')||(LA19_0>='\u0D85' && LA19_0<='\u0D96')||(LA19_0>='\u0D9A' && LA19_0<='\u0DB1')||(LA19_0>='\u0DB3' && LA19_0<='\u0DBB')||LA19_0=='\u0DBD'||(LA19_0>='\u0DC0' && LA19_0<='\u0DC6')||LA19_0=='\u0DCA'||(LA19_0>='\u0DCF' && LA19_0<='\u0DD4')||LA19_0=='\u0DD6'||(LA19_0>='\u0DD8' && LA19_0<='\u0DDF')||(LA19_0>='\u0DF2' && LA19_0<='\u0DF3')||(LA19_0>='\u0E01' && LA19_0<='\u0E3A')||(LA19_0>='\u0E3F' && LA19_0<='\u0E4E')||(LA19_0>='\u0E50' && LA19_0<='\u0E59')||(LA19_0>='\u0E81' && LA19_0<='\u0E82')||LA19_0=='\u0E84'||(LA19_0>='\u0E87' && LA19_0<='\u0E88')||LA19_0=='\u0E8A'||LA19_0=='\u0E8D'||(LA19_0>='\u0E94' && LA19_0<='\u0E97')||(LA19_0>='\u0E99' && LA19_0<='\u0E9F')||(LA19_0>='\u0EA1' && LA19_0<='\u0EA3')||LA19_0=='\u0EA5'||LA19_0=='\u0EA7'||(LA19_0>='\u0EAA' && LA19_0<='\u0EAB')||(LA19_0>='\u0EAD' && LA19_0<='\u0EB9')||(LA19_0>='\u0EBB' && LA19_0<='\u0EBD')||(LA19_0>='\u0EC0' && LA19_0<='\u0EC4')||LA19_0=='\u0EC6'||(LA19_0>='\u0EC8' && LA19_0<='\u0ECD')||(LA19_0>='\u0ED0' && LA19_0<='\u0ED9')||(LA19_0>='\u0EDC' && LA19_0<='\u0EDD')||LA19_0=='\u0F00'||(LA19_0>='\u0F18' && LA19_0<='\u0F19')||(LA19_0>='\u0F20' && LA19_0<='\u0F29')||LA19_0=='\u0F35'||LA19_0=='\u0F37'||LA19_0=='\u0F39'||(LA19_0>='\u0F3E' && LA19_0<='\u0F47')||(LA19_0>='\u0F49' && LA19_0<='\u0F6A')||(LA19_0>='\u0F71' && LA19_0<='\u0F84')||(LA19_0>='\u0F86' && LA19_0<='\u0F8B')||(LA19_0>='\u0F90' && LA19_0<='\u0F97')||(LA19_0>='\u0F99' && LA19_0<='\u0FBC')||LA19_0=='\u0FC6'||(LA19_0>='\u1000' && LA19_0<='\u1021')||(LA19_0>='\u1023' && LA19_0<='\u1027')||(LA19_0>='\u1029' && LA19_0<='\u102A')||(LA19_0>='\u102C' && LA19_0<='\u1032')||(LA19_0>='\u1036' && LA19_0<='\u1039')||(LA19_0>='\u1040' && LA19_0<='\u1049')||(LA19_0>='\u1050' && LA19_0<='\u1059')||(LA19_0>='\u10A0' && LA19_0<='\u10C5')||(LA19_0>='\u10D0' && LA19_0<='\u10F8')||(LA19_0>='\u1100' && LA19_0<='\u1159')||(LA19_0>='\u115F' && LA19_0<='\u11A2')||(LA19_0>='\u11A8' && LA19_0<='\u11F9')||(LA19_0>='\u1200' && LA19_0<='\u1206')||(LA19_0>='\u1208' && LA19_0<='\u1246')||LA19_0=='\u1248'||(LA19_0>='\u124A' && LA19_0<='\u124D')||(LA19_0>='\u1250' && LA19_0<='\u1256')||LA19_0=='\u1258'||(LA19_0>='\u125A' && LA19_0<='\u125D')||(LA19_0>='\u1260' && LA19_0<='\u1286')||LA19_0=='\u1288'||(LA19_0>='\u128A' && LA19_0<='\u128D')||(LA19_0>='\u1290' && LA19_0<='\u12AE')||LA19_0=='\u12B0'||(LA19_0>='\u12B2' && LA19_0<='\u12B5')||(LA19_0>='\u12B8' && LA19_0<='\u12BE')||LA19_0=='\u12C0'||(LA19_0>='\u12C2' && LA19_0<='\u12C5')||(LA19_0>='\u12C8' && LA19_0<='\u12CE')||(LA19_0>='\u12D0' && LA19_0<='\u12D6')||(LA19_0>='\u12D8' && LA19_0<='\u12EE')||(LA19_0>='\u12F0' && LA19_0<='\u130E')||LA19_0=='\u1310'||(LA19_0>='\u1312' && LA19_0<='\u1315')||(LA19_0>='\u1318' && LA19_0<='\u131E')||(LA19_0>='\u1320' && LA19_0<='\u1346')||(LA19_0>='\u1348' && LA19_0<='\u135A')||(LA19_0>='\u1369' && LA19_0<='\u1371')||(LA19_0>='\u13A0' && LA19_0<='\u13F4')||(LA19_0>='\u1401' && LA19_0<='\u166C')||(LA19_0>='\u166F' && LA19_0<='\u1676')||(LA19_0>='\u1681' && LA19_0<='\u169A')||(LA19_0>='\u16A0' && LA19_0<='\u16EA')||(LA19_0>='\u16EE' && LA19_0<='\u16F0')||(LA19_0>='\u1700' && LA19_0<='\u170C')||(LA19_0>='\u170E' && LA19_0<='\u1714')||(LA19_0>='\u1720' && LA19_0<='\u1734')||(LA19_0>='\u1740' && LA19_0<='\u1753')||(LA19_0>='\u1760' && LA19_0<='\u176C')||(LA19_0>='\u176E' && LA19_0<='\u1770')||(LA19_0>='\u1772' && LA19_0<='\u1773')||(LA19_0>='\u1780' && LA19_0<='\u17D3')||LA19_0=='\u17D7'||(LA19_0>='\u17DB' && LA19_0<='\u17DD')||(LA19_0>='\u17E0' && LA19_0<='\u17E9')||(LA19_0>='\u180B' && LA19_0<='\u180D')||(LA19_0>='\u1810' && LA19_0<='\u1819')||(LA19_0>='\u1820' && LA19_0<='\u1877')||(LA19_0>='\u1880' && LA19_0<='\u18A9')||(LA19_0>='\u1900' && LA19_0<='\u191C')||(LA19_0>='\u1920' && LA19_0<='\u192B')||(LA19_0>='\u1930' && LA19_0<='\u193B')||(LA19_0>='\u1946' && LA19_0<='\u196D')||(LA19_0>='\u1970' && LA19_0<='\u1974')||(LA19_0>='\u1D00' && LA19_0<='\u1D6B')||(LA19_0>='\u1E00' && LA19_0<='\u1E9B')||(LA19_0>='\u1EA0' && LA19_0<='\u1EF9')||(LA19_0>='\u1F00' && LA19_0<='\u1F15')||(LA19_0>='\u1F18' && LA19_0<='\u1F1D')||(LA19_0>='\u1F20' && LA19_0<='\u1F45')||(LA19_0>='\u1F48' && LA19_0<='\u1F4D')||(LA19_0>='\u1F50' && LA19_0<='\u1F57')||LA19_0=='\u1F59'||LA19_0=='\u1F5B'||LA19_0=='\u1F5D'||(LA19_0>='\u1F5F' && LA19_0<='\u1F7D')||(LA19_0>='\u1F80' && LA19_0<='\u1FB4')||(LA19_0>='\u1FB6' && LA19_0<='\u1FBC')||LA19_0=='\u1FBE'||(LA19_0>='\u1FC2' && LA19_0<='\u1FC4')||(LA19_0>='\u1FC6' && LA19_0<='\u1FCC')||(LA19_0>='\u1FD0' && LA19_0<='\u1FD3')||(LA19_0>='\u1FD6' && LA19_0<='\u1FDB')||(LA19_0>='\u1FE0' && LA19_0<='\u1FEC')||(LA19_0>='\u1FF2' && LA19_0<='\u1FF4')||(LA19_0>='\u1FF6' && LA19_0<='\u1FFC')||(LA19_0>='\u200C' && LA19_0<='\u200F')||(LA19_0>='\u202A' && LA19_0<='\u202E')||(LA19_0>='\u203F' && LA19_0<='\u2040')||LA19_0=='\u2054'||(LA19_0>='\u2060' && LA19_0<='\u2063')||(LA19_0>='\u206A' && LA19_0<='\u206F')||LA19_0=='\u2071'||LA19_0=='\u207F'||(LA19_0>='\u20A0' && LA19_0<='\u20B1')||(LA19_0>='\u20D0' && LA19_0<='\u20DC')||LA19_0=='\u20E1'||(LA19_0>='\u20E5' && LA19_0<='\u20EA')||LA19_0=='\u2102'||LA19_0=='\u2107'||(LA19_0>='\u210A' && LA19_0<='\u2113')||LA19_0=='\u2115'||(LA19_0>='\u2119' && LA19_0<='\u211D')||LA19_0=='\u2124'||LA19_0=='\u2126'||LA19_0=='\u2128'||(LA19_0>='\u212A' && LA19_0<='\u212D')||(LA19_0>='\u212F' && LA19_0<='\u2131')||(LA19_0>='\u2133' && LA19_0<='\u2139')||(LA19_0>='\u213D' && LA19_0<='\u213F')||(LA19_0>='\u2145' && LA19_0<='\u2149')||(LA19_0>='\u2160' && LA19_0<='\u2183')||(LA19_0>='\u3005' && LA19_0<='\u3007')||(LA19_0>='\u3021' && LA19_0<='\u302F')||(LA19_0>='\u3031' && LA19_0<='\u3035')||(LA19_0>='\u3038' && LA19_0<='\u303C')||(LA19_0>='\u3041' && LA19_0<='\u3096')||(LA19_0>='\u3099' && LA19_0<='\u309A')||(LA19_0>='\u309D' && LA19_0<='\u309F')||(LA19_0>='\u30A1' && LA19_0<='\u30FF')||(LA19_0>='\u3105' && LA19_0<='\u312C')||(LA19_0>='\u3131' && LA19_0<='\u318E')||(LA19_0>='\u31A0' && LA19_0<='\u31B7')||(LA19_0>='\u31F0' && LA19_0<='\u31FF')||(LA19_0>='\u3400' && LA19_0<='\u4DB5')||(LA19_0>='\u4E00' && LA19_0<='\u9FA5')||(LA19_0>='\uA000' && LA19_0<='\uA48C')||(LA19_0>='\uAC00' && LA19_0<='\uD7A3')||(LA19_0>='\uF900' && LA19_0<='\uFA2D')||(LA19_0>='\uFA30' && LA19_0<='\uFA6A')||(LA19_0>='\uFB00' && LA19_0<='\uFB06')||(LA19_0>='\uFB13' && LA19_0<='\uFB17')||(LA19_0>='\uFB1D' && LA19_0<='\uFB28')||(LA19_0>='\uFB2A' && LA19_0<='\uFB36')||(LA19_0>='\uFB38' && LA19_0<='\uFB3C')||LA19_0=='\uFB3E'||(LA19_0>='\uFB40' && LA19_0<='\uFB41')||(LA19_0>='\uFB43' && LA19_0<='\uFB44')||(LA19_0>='\uFB46' && LA19_0<='\uFBB1')||(LA19_0>='\uFBD3' && LA19_0<='\uFD3D')||(LA19_0>='\uFD50' && LA19_0<='\uFD8F')||(LA19_0>='\uFD92' && LA19_0<='\uFDC7')||(LA19_0>='\uFDF0' && LA19_0<='\uFDFC')||(LA19_0>='\uFE00' && LA19_0<='\uFE0F')||(LA19_0>='\uFE20' && LA19_0<='\uFE23')||(LA19_0>='\uFE33' && LA19_0<='\uFE34')||(LA19_0>='\uFE4D' && LA19_0<='\uFE4F')||LA19_0=='\uFE69'||(LA19_0>='\uFE70' && LA19_0<='\uFE74')||(LA19_0>='\uFE76' && LA19_0<='\uFEFC')||LA19_0=='\uFEFF'||LA19_0=='\uFF04'||(LA19_0>='\uFF10' && LA19_0<='\uFF19')||(LA19_0>='\uFF21' && LA19_0<='\uFF3A')||LA19_0=='\uFF3F'||(LA19_0>='\uFF41' && LA19_0<='\uFF5A')||(LA19_0>='\uFF65' && LA19_0<='\uFFBE')||(LA19_0>='\uFFC2' && LA19_0<='\uFFC7')||(LA19_0>='\uFFCA' && LA19_0<='\uFFCF')||(LA19_0>='\uFFD2' && LA19_0<='\uFFD7')||(LA19_0>='\uFFDA' && LA19_0<='\uFFDC')||(LA19_0>='\uFFE0' && LA19_0<='\uFFE1')||(LA19_0>='\uFFE5' && LA19_0<='\uFFE6')||(LA19_0>='\uFFF9' && LA19_0<='\uFFFB')) ) {
                            alt19=1;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:304:27: IdentifierPart
                    	    {
                    	    mIdentifierPart(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);

                    match('`'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      	state.text = getText().substring(1, getText().length() - 1);	
                    }

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "SEMI_COLON"
    public final void mSEMI_COLON() throws RecognitionException {
        try {
            int _type = SEMI_COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:309:5: ( ';' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:309:8: ';'
            {
            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI_COLON"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:312:7: ( ',' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:312:9: ','
            {
            match(','); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:315:5: ( '.' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:315:7: '.'
            {
            match('.'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "LEFT_PAREN"
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:319:9: ( '(' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:319:11: '('
            {
            match('('); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_PAREN"

    // $ANTLR start "RIGHT_PAREN"
    public final void mRIGHT_PAREN() throws RecognitionException {
        try {
            int _type = RIGHT_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:323:9: ( ')' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:323:11: ')'
            {
            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_PAREN"

    // $ANTLR start "LEFT_SQUARE"
    public final void mLEFT_SQUARE() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:327:9: ( '[' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:327:11: '['
            {
            match('['); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_SQUARE"

    // $ANTLR start "RIGHT_SQUARE"
    public final void mRIGHT_SQUARE() throws RecognitionException {
        try {
            int _type = RIGHT_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:331:9: ( ']' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:331:11: ']'
            {
            match(']'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_SQUARE"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:335:5: ( ':' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:335:7: ':'
            {
            match(':'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:338:9: ( '*' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:338:11: '*'
            {
            match('*'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "MISC"
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:341:7: ( '&&' | '||' | '<=' | '>=' | '==' | ':=' | '!=' | '->' | '!' | '%' | '^' | '-' | '+' | '?' | '/' | '\\'' | '\\\\' | '|' | '&' | '$' | '@' | '=' | '>' | '<' | '{' | '}' )
            int alt21=26;
            alt21 = dfa21.predict(input);
            switch (alt21) {
                case 1 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:9: '&&'
                    {
                    match("&&"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:16: '||'
                    {
                    match("||"); if (state.failed) return ;


                    }
                    break;
                case 3 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:23: '<='
                    {
                    match("<="); if (state.failed) return ;


                    }
                    break;
                case 4 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:30: '>='
                    {
                    match(">="); if (state.failed) return ;


                    }
                    break;
                case 5 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:37: '=='
                    {
                    match("=="); if (state.failed) return ;


                    }
                    break;
                case 6 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:44: ':='
                    {
                    match(":="); if (state.failed) return ;


                    }
                    break;
                case 7 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:51: '!='
                    {
                    match("!="); if (state.failed) return ;


                    }
                    break;
                case 8 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:342:58: '->'
                    {
                    match("->"); if (state.failed) return ;


                    }
                    break;
                case 9 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:9: '!'
                    {
                    match('!'); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:15: '%'
                    {
                    match('%'); if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:21: '^'
                    {
                    match('^'); if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:27: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:33: '+'
                    {
                    match('+'); if (state.failed) return ;

                    }
                    break;
                case 14 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:40: '?'
                    {
                    match('?'); if (state.failed) return ;

                    }
                    break;
                case 15 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:46: '/'
                    {
                    match('/'); if (state.failed) return ;

                    }
                    break;
                case 16 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:52: '\\''
                    {
                    match('\''); if (state.failed) return ;

                    }
                    break;
                case 17 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:59: '\\\\'
                    {
                    match('\\'); if (state.failed) return ;

                    }
                    break;
                case 18 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:66: '|'
                    {
                    match('|'); if (state.failed) return ;

                    }
                    break;
                case 19 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:72: '&'
                    {
                    match('&'); if (state.failed) return ;

                    }
                    break;
                case 20 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:343:78: '$'
                    {
                    match('$'); if (state.failed) return ;

                    }
                    break;
                case 21 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:9: '@'
                    {
                    match('@'); if (state.failed) return ;

                    }
                    break;
                case 22 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:16: '='
                    {
                    match('='); if (state.failed) return ;

                    }
                    break;
                case 23 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:22: '>'
                    {
                    match('>'); if (state.failed) return ;

                    }
                    break;
                case 24 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:29: '<'
                    {
                    match('<'); if (state.failed) return ;

                    }
                    break;
                case 25 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:35: '{'
                    {
                    match('{'); if (state.failed) return ;

                    }
                    break;
                case 26 :
                    // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:344:41: '}'
                    {
                    match('}'); if (state.failed) return ;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MISC"

    // $ANTLR start "IdentifierStart"
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:349:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u064a' | '\\u066e' .. '\\u066f' | '\\u0671' .. '\\u06d3' | '\\u06d5' | '\\u06e5' .. '\\u06e6' | '\\u06ee' .. '\\u06ef' | '\\u06fa' .. '\\u06fc' | '\\u06ff' | '\\u0710' | '\\u0712' .. '\\u072f' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07a5' | '\\u07b1' | '\\u0904' .. '\\u0939' | '\\u093d' | '\\u0950' | '\\u0958' .. '\\u0961' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bd' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e1' | '\\u09f0' .. '\\u09f3' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a72' .. '\\u0a74' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae1' | '\\u0af1' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3d' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b71' | '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bf9' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c60' .. '\\u0c61' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbd' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d60' .. '\\u0d61' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0e01' .. '\\u0e30' | '\\u0e32' .. '\\u0e33' | '\\u0e3f' .. '\\u0e46' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb0' | '\\u0eb2' .. '\\u0eb3' | '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f40' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f88' .. '\\u0f8b' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u1050' .. '\\u1055' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1711' | '\\u1720' .. '\\u1731' | '\\u1740' .. '\\u1751' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1780' .. '\\u17b3' | '\\u17d7' | '\\u17db' .. '\\u17dc' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a8' | '\\u1900' .. '\\u191c' | '\\u1950' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u3029' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' | '\\ufb1f' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff04' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00A2' && input.LA(1)<='\u00A5')||input.LA(1)=='\u00AA'||input.LA(1)=='\u00B5'||input.LA(1)=='\u00BA'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u0236')||(input.LA(1)>='\u0250' && input.LA(1)<='\u02C1')||(input.LA(1)>='\u02C6' && input.LA(1)<='\u02D1')||(input.LA(1)>='\u02E0' && input.LA(1)<='\u02E4')||input.LA(1)=='\u02EE'||input.LA(1)=='\u037A'||input.LA(1)=='\u0386'||(input.LA(1)>='\u0388' && input.LA(1)<='\u038A')||input.LA(1)=='\u038C'||(input.LA(1)>='\u038E' && input.LA(1)<='\u03A1')||(input.LA(1)>='\u03A3' && input.LA(1)<='\u03CE')||(input.LA(1)>='\u03D0' && input.LA(1)<='\u03F5')||(input.LA(1)>='\u03F7' && input.LA(1)<='\u03FB')||(input.LA(1)>='\u0400' && input.LA(1)<='\u0481')||(input.LA(1)>='\u048A' && input.LA(1)<='\u04CE')||(input.LA(1)>='\u04D0' && input.LA(1)<='\u04F5')||(input.LA(1)>='\u04F8' && input.LA(1)<='\u04F9')||(input.LA(1)>='\u0500' && input.LA(1)<='\u050F')||(input.LA(1)>='\u0531' && input.LA(1)<='\u0556')||input.LA(1)=='\u0559'||(input.LA(1)>='\u0561' && input.LA(1)<='\u0587')||(input.LA(1)>='\u05D0' && input.LA(1)<='\u05EA')||(input.LA(1)>='\u05F0' && input.LA(1)<='\u05F2')||(input.LA(1)>='\u0621' && input.LA(1)<='\u063A')||(input.LA(1)>='\u0640' && input.LA(1)<='\u064A')||(input.LA(1)>='\u066E' && input.LA(1)<='\u066F')||(input.LA(1)>='\u0671' && input.LA(1)<='\u06D3')||input.LA(1)=='\u06D5'||(input.LA(1)>='\u06E5' && input.LA(1)<='\u06E6')||(input.LA(1)>='\u06EE' && input.LA(1)<='\u06EF')||(input.LA(1)>='\u06FA' && input.LA(1)<='\u06FC')||input.LA(1)=='\u06FF'||input.LA(1)=='\u0710'||(input.LA(1)>='\u0712' && input.LA(1)<='\u072F')||(input.LA(1)>='\u074D' && input.LA(1)<='\u074F')||(input.LA(1)>='\u0780' && input.LA(1)<='\u07A5')||input.LA(1)=='\u07B1'||(input.LA(1)>='\u0904' && input.LA(1)<='\u0939')||input.LA(1)=='\u093D'||input.LA(1)=='\u0950'||(input.LA(1)>='\u0958' && input.LA(1)<='\u0961')||(input.LA(1)>='\u0985' && input.LA(1)<='\u098C')||(input.LA(1)>='\u098F' && input.LA(1)<='\u0990')||(input.LA(1)>='\u0993' && input.LA(1)<='\u09A8')||(input.LA(1)>='\u09AA' && input.LA(1)<='\u09B0')||input.LA(1)=='\u09B2'||(input.LA(1)>='\u09B6' && input.LA(1)<='\u09B9')||input.LA(1)=='\u09BD'||(input.LA(1)>='\u09DC' && input.LA(1)<='\u09DD')||(input.LA(1)>='\u09DF' && input.LA(1)<='\u09E1')||(input.LA(1)>='\u09F0' && input.LA(1)<='\u09F3')||(input.LA(1)>='\u0A05' && input.LA(1)<='\u0A0A')||(input.LA(1)>='\u0A0F' && input.LA(1)<='\u0A10')||(input.LA(1)>='\u0A13' && input.LA(1)<='\u0A28')||(input.LA(1)>='\u0A2A' && input.LA(1)<='\u0A30')||(input.LA(1)>='\u0A32' && input.LA(1)<='\u0A33')||(input.LA(1)>='\u0A35' && input.LA(1)<='\u0A36')||(input.LA(1)>='\u0A38' && input.LA(1)<='\u0A39')||(input.LA(1)>='\u0A59' && input.LA(1)<='\u0A5C')||input.LA(1)=='\u0A5E'||(input.LA(1)>='\u0A72' && input.LA(1)<='\u0A74')||(input.LA(1)>='\u0A85' && input.LA(1)<='\u0A8D')||(input.LA(1)>='\u0A8F' && input.LA(1)<='\u0A91')||(input.LA(1)>='\u0A93' && input.LA(1)<='\u0AA8')||(input.LA(1)>='\u0AAA' && input.LA(1)<='\u0AB0')||(input.LA(1)>='\u0AB2' && input.LA(1)<='\u0AB3')||(input.LA(1)>='\u0AB5' && input.LA(1)<='\u0AB9')||input.LA(1)=='\u0ABD'||input.LA(1)=='\u0AD0'||(input.LA(1)>='\u0AE0' && input.LA(1)<='\u0AE1')||input.LA(1)=='\u0AF1'||(input.LA(1)>='\u0B05' && input.LA(1)<='\u0B0C')||(input.LA(1)>='\u0B0F' && input.LA(1)<='\u0B10')||(input.LA(1)>='\u0B13' && input.LA(1)<='\u0B28')||(input.LA(1)>='\u0B2A' && input.LA(1)<='\u0B30')||(input.LA(1)>='\u0B32' && input.LA(1)<='\u0B33')||(input.LA(1)>='\u0B35' && input.LA(1)<='\u0B39')||input.LA(1)=='\u0B3D'||(input.LA(1)>='\u0B5C' && input.LA(1)<='\u0B5D')||(input.LA(1)>='\u0B5F' && input.LA(1)<='\u0B61')||input.LA(1)=='\u0B71'||input.LA(1)=='\u0B83'||(input.LA(1)>='\u0B85' && input.LA(1)<='\u0B8A')||(input.LA(1)>='\u0B8E' && input.LA(1)<='\u0B90')||(input.LA(1)>='\u0B92' && input.LA(1)<='\u0B95')||(input.LA(1)>='\u0B99' && input.LA(1)<='\u0B9A')||input.LA(1)=='\u0B9C'||(input.LA(1)>='\u0B9E' && input.LA(1)<='\u0B9F')||(input.LA(1)>='\u0BA3' && input.LA(1)<='\u0BA4')||(input.LA(1)>='\u0BA8' && input.LA(1)<='\u0BAA')||(input.LA(1)>='\u0BAE' && input.LA(1)<='\u0BB5')||(input.LA(1)>='\u0BB7' && input.LA(1)<='\u0BB9')||input.LA(1)=='\u0BF9'||(input.LA(1)>='\u0C05' && input.LA(1)<='\u0C0C')||(input.LA(1)>='\u0C0E' && input.LA(1)<='\u0C10')||(input.LA(1)>='\u0C12' && input.LA(1)<='\u0C28')||(input.LA(1)>='\u0C2A' && input.LA(1)<='\u0C33')||(input.LA(1)>='\u0C35' && input.LA(1)<='\u0C39')||(input.LA(1)>='\u0C60' && input.LA(1)<='\u0C61')||(input.LA(1)>='\u0C85' && input.LA(1)<='\u0C8C')||(input.LA(1)>='\u0C8E' && input.LA(1)<='\u0C90')||(input.LA(1)>='\u0C92' && input.LA(1)<='\u0CA8')||(input.LA(1)>='\u0CAA' && input.LA(1)<='\u0CB3')||(input.LA(1)>='\u0CB5' && input.LA(1)<='\u0CB9')||input.LA(1)=='\u0CBD'||input.LA(1)=='\u0CDE'||(input.LA(1)>='\u0CE0' && input.LA(1)<='\u0CE1')||(input.LA(1)>='\u0D05' && input.LA(1)<='\u0D0C')||(input.LA(1)>='\u0D0E' && input.LA(1)<='\u0D10')||(input.LA(1)>='\u0D12' && input.LA(1)<='\u0D28')||(input.LA(1)>='\u0D2A' && input.LA(1)<='\u0D39')||(input.LA(1)>='\u0D60' && input.LA(1)<='\u0D61')||(input.LA(1)>='\u0D85' && input.LA(1)<='\u0D96')||(input.LA(1)>='\u0D9A' && input.LA(1)<='\u0DB1')||(input.LA(1)>='\u0DB3' && input.LA(1)<='\u0DBB')||input.LA(1)=='\u0DBD'||(input.LA(1)>='\u0DC0' && input.LA(1)<='\u0DC6')||(input.LA(1)>='\u0E01' && input.LA(1)<='\u0E30')||(input.LA(1)>='\u0E32' && input.LA(1)<='\u0E33')||(input.LA(1)>='\u0E3F' && input.LA(1)<='\u0E46')||(input.LA(1)>='\u0E81' && input.LA(1)<='\u0E82')||input.LA(1)=='\u0E84'||(input.LA(1)>='\u0E87' && input.LA(1)<='\u0E88')||input.LA(1)=='\u0E8A'||input.LA(1)=='\u0E8D'||(input.LA(1)>='\u0E94' && input.LA(1)<='\u0E97')||(input.LA(1)>='\u0E99' && input.LA(1)<='\u0E9F')||(input.LA(1)>='\u0EA1' && input.LA(1)<='\u0EA3')||input.LA(1)=='\u0EA5'||input.LA(1)=='\u0EA7'||(input.LA(1)>='\u0EAA' && input.LA(1)<='\u0EAB')||(input.LA(1)>='\u0EAD' && input.LA(1)<='\u0EB0')||(input.LA(1)>='\u0EB2' && input.LA(1)<='\u0EB3')||input.LA(1)=='\u0EBD'||(input.LA(1)>='\u0EC0' && input.LA(1)<='\u0EC4')||input.LA(1)=='\u0EC6'||(input.LA(1)>='\u0EDC' && input.LA(1)<='\u0EDD')||input.LA(1)=='\u0F00'||(input.LA(1)>='\u0F40' && input.LA(1)<='\u0F47')||(input.LA(1)>='\u0F49' && input.LA(1)<='\u0F6A')||(input.LA(1)>='\u0F88' && input.LA(1)<='\u0F8B')||(input.LA(1)>='\u1000' && input.LA(1)<='\u1021')||(input.LA(1)>='\u1023' && input.LA(1)<='\u1027')||(input.LA(1)>='\u1029' && input.LA(1)<='\u102A')||(input.LA(1)>='\u1050' && input.LA(1)<='\u1055')||(input.LA(1)>='\u10A0' && input.LA(1)<='\u10C5')||(input.LA(1)>='\u10D0' && input.LA(1)<='\u10F8')||(input.LA(1)>='\u1100' && input.LA(1)<='\u1159')||(input.LA(1)>='\u115F' && input.LA(1)<='\u11A2')||(input.LA(1)>='\u11A8' && input.LA(1)<='\u11F9')||(input.LA(1)>='\u1200' && input.LA(1)<='\u1206')||(input.LA(1)>='\u1208' && input.LA(1)<='\u1246')||input.LA(1)=='\u1248'||(input.LA(1)>='\u124A' && input.LA(1)<='\u124D')||(input.LA(1)>='\u1250' && input.LA(1)<='\u1256')||input.LA(1)=='\u1258'||(input.LA(1)>='\u125A' && input.LA(1)<='\u125D')||(input.LA(1)>='\u1260' && input.LA(1)<='\u1286')||input.LA(1)=='\u1288'||(input.LA(1)>='\u128A' && input.LA(1)<='\u128D')||(input.LA(1)>='\u1290' && input.LA(1)<='\u12AE')||input.LA(1)=='\u12B0'||(input.LA(1)>='\u12B2' && input.LA(1)<='\u12B5')||(input.LA(1)>='\u12B8' && input.LA(1)<='\u12BE')||input.LA(1)=='\u12C0'||(input.LA(1)>='\u12C2' && input.LA(1)<='\u12C5')||(input.LA(1)>='\u12C8' && input.LA(1)<='\u12CE')||(input.LA(1)>='\u12D0' && input.LA(1)<='\u12D6')||(input.LA(1)>='\u12D8' && input.LA(1)<='\u12EE')||(input.LA(1)>='\u12F0' && input.LA(1)<='\u130E')||input.LA(1)=='\u1310'||(input.LA(1)>='\u1312' && input.LA(1)<='\u1315')||(input.LA(1)>='\u1318' && input.LA(1)<='\u131E')||(input.LA(1)>='\u1320' && input.LA(1)<='\u1346')||(input.LA(1)>='\u1348' && input.LA(1)<='\u135A')||(input.LA(1)>='\u13A0' && input.LA(1)<='\u13F4')||(input.LA(1)>='\u1401' && input.LA(1)<='\u166C')||(input.LA(1)>='\u166F' && input.LA(1)<='\u1676')||(input.LA(1)>='\u1681' && input.LA(1)<='\u169A')||(input.LA(1)>='\u16A0' && input.LA(1)<='\u16EA')||(input.LA(1)>='\u16EE' && input.LA(1)<='\u16F0')||(input.LA(1)>='\u1700' && input.LA(1)<='\u170C')||(input.LA(1)>='\u170E' && input.LA(1)<='\u1711')||(input.LA(1)>='\u1720' && input.LA(1)<='\u1731')||(input.LA(1)>='\u1740' && input.LA(1)<='\u1751')||(input.LA(1)>='\u1760' && input.LA(1)<='\u176C')||(input.LA(1)>='\u176E' && input.LA(1)<='\u1770')||(input.LA(1)>='\u1780' && input.LA(1)<='\u17B3')||input.LA(1)=='\u17D7'||(input.LA(1)>='\u17DB' && input.LA(1)<='\u17DC')||(input.LA(1)>='\u1820' && input.LA(1)<='\u1877')||(input.LA(1)>='\u1880' && input.LA(1)<='\u18A8')||(input.LA(1)>='\u1900' && input.LA(1)<='\u191C')||(input.LA(1)>='\u1950' && input.LA(1)<='\u196D')||(input.LA(1)>='\u1970' && input.LA(1)<='\u1974')||(input.LA(1)>='\u1D00' && input.LA(1)<='\u1D6B')||(input.LA(1)>='\u1E00' && input.LA(1)<='\u1E9B')||(input.LA(1)>='\u1EA0' && input.LA(1)<='\u1EF9')||(input.LA(1)>='\u1F00' && input.LA(1)<='\u1F15')||(input.LA(1)>='\u1F18' && input.LA(1)<='\u1F1D')||(input.LA(1)>='\u1F20' && input.LA(1)<='\u1F45')||(input.LA(1)>='\u1F48' && input.LA(1)<='\u1F4D')||(input.LA(1)>='\u1F50' && input.LA(1)<='\u1F57')||input.LA(1)=='\u1F59'||input.LA(1)=='\u1F5B'||input.LA(1)=='\u1F5D'||(input.LA(1)>='\u1F5F' && input.LA(1)<='\u1F7D')||(input.LA(1)>='\u1F80' && input.LA(1)<='\u1FB4')||(input.LA(1)>='\u1FB6' && input.LA(1)<='\u1FBC')||input.LA(1)=='\u1FBE'||(input.LA(1)>='\u1FC2' && input.LA(1)<='\u1FC4')||(input.LA(1)>='\u1FC6' && input.LA(1)<='\u1FCC')||(input.LA(1)>='\u1FD0' && input.LA(1)<='\u1FD3')||(input.LA(1)>='\u1FD6' && input.LA(1)<='\u1FDB')||(input.LA(1)>='\u1FE0' && input.LA(1)<='\u1FEC')||(input.LA(1)>='\u1FF2' && input.LA(1)<='\u1FF4')||(input.LA(1)>='\u1FF6' && input.LA(1)<='\u1FFC')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||input.LA(1)=='\u2054'||input.LA(1)=='\u2071'||input.LA(1)=='\u207F'||(input.LA(1)>='\u20A0' && input.LA(1)<='\u20B1')||input.LA(1)=='\u2102'||input.LA(1)=='\u2107'||(input.LA(1)>='\u210A' && input.LA(1)<='\u2113')||input.LA(1)=='\u2115'||(input.LA(1)>='\u2119' && input.LA(1)<='\u211D')||input.LA(1)=='\u2124'||input.LA(1)=='\u2126'||input.LA(1)=='\u2128'||(input.LA(1)>='\u212A' && input.LA(1)<='\u212D')||(input.LA(1)>='\u212F' && input.LA(1)<='\u2131')||(input.LA(1)>='\u2133' && input.LA(1)<='\u2139')||(input.LA(1)>='\u213D' && input.LA(1)<='\u213F')||(input.LA(1)>='\u2145' && input.LA(1)<='\u2149')||(input.LA(1)>='\u2160' && input.LA(1)<='\u2183')||(input.LA(1)>='\u3005' && input.LA(1)<='\u3007')||(input.LA(1)>='\u3021' && input.LA(1)<='\u3029')||(input.LA(1)>='\u3031' && input.LA(1)<='\u3035')||(input.LA(1)>='\u3038' && input.LA(1)<='\u303C')||(input.LA(1)>='\u3041' && input.LA(1)<='\u3096')||(input.LA(1)>='\u309D' && input.LA(1)<='\u309F')||(input.LA(1)>='\u30A1' && input.LA(1)<='\u30FF')||(input.LA(1)>='\u3105' && input.LA(1)<='\u312C')||(input.LA(1)>='\u3131' && input.LA(1)<='\u318E')||(input.LA(1)>='\u31A0' && input.LA(1)<='\u31B7')||(input.LA(1)>='\u31F0' && input.LA(1)<='\u31FF')||(input.LA(1)>='\u3400' && input.LA(1)<='\u4DB5')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FA5')||(input.LA(1)>='\uA000' && input.LA(1)<='\uA48C')||(input.LA(1)>='\uAC00' && input.LA(1)<='\uD7A3')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFA2D')||(input.LA(1)>='\uFA30' && input.LA(1)<='\uFA6A')||(input.LA(1)>='\uFB00' && input.LA(1)<='\uFB06')||(input.LA(1)>='\uFB13' && input.LA(1)<='\uFB17')||input.LA(1)=='\uFB1D'||(input.LA(1)>='\uFB1F' && input.LA(1)<='\uFB28')||(input.LA(1)>='\uFB2A' && input.LA(1)<='\uFB36')||(input.LA(1)>='\uFB38' && input.LA(1)<='\uFB3C')||input.LA(1)=='\uFB3E'||(input.LA(1)>='\uFB40' && input.LA(1)<='\uFB41')||(input.LA(1)>='\uFB43' && input.LA(1)<='\uFB44')||(input.LA(1)>='\uFB46' && input.LA(1)<='\uFBB1')||(input.LA(1)>='\uFBD3' && input.LA(1)<='\uFD3D')||(input.LA(1)>='\uFD50' && input.LA(1)<='\uFD8F')||(input.LA(1)>='\uFD92' && input.LA(1)<='\uFDC7')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFDFC')||(input.LA(1)>='\uFE33' && input.LA(1)<='\uFE34')||(input.LA(1)>='\uFE4D' && input.LA(1)<='\uFE4F')||input.LA(1)=='\uFE69'||(input.LA(1)>='\uFE70' && input.LA(1)<='\uFE74')||(input.LA(1)>='\uFE76' && input.LA(1)<='\uFEFC')||input.LA(1)=='\uFF04'||(input.LA(1)>='\uFF21' && input.LA(1)<='\uFF3A')||input.LA(1)=='\uFF3F'||(input.LA(1)>='\uFF41' && input.LA(1)<='\uFF5A')||(input.LA(1)>='\uFF65' && input.LA(1)<='\uFFBE')||(input.LA(1)>='\uFFC2' && input.LA(1)<='\uFFC7')||(input.LA(1)>='\uFFCA' && input.LA(1)<='\uFFCF')||(input.LA(1)>='\uFFD2' && input.LA(1)<='\uFFD7')||(input.LA(1)>='\uFFDA' && input.LA(1)<='\uFFDC')||(input.LA(1)>='\uFFE0' && input.LA(1)<='\uFFE1')||(input.LA(1)>='\uFFE5' && input.LA(1)<='\uFFE6') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IdentifierStart"

    // $ANTLR start "IdentifierPart"
    public final void mIdentifierPart() throws RecognitionException {
        try {
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:647:5: ( '\\u0000' .. '\\u0008' | '\\u000e' .. '\\u001b' | '\\u0024' | '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u007f' .. '\\u009f' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00ad' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u0483' .. '\\u0486' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05bd' | '\\u05bf' | '\\u05c1' .. '\\u05c2' | '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0600' .. '\\u0603' | '\\u0610' .. '\\u0615' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u06d3' | '\\u06d5' .. '\\u06dd' | '\\u06df' .. '\\u06e8' | '\\u06ea' .. '\\u06fc' | '\\u06ff' | '\\u070f' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0963' | '\\u0966' .. '\\u096f' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09f3' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b6f' | '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' | '\\u0be7' .. '\\u0bef' | '\\u0bf9' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df3' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e4e' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f18' .. '\\u0f19' | '\\u0f20' .. '\\u0f29' | '\\u0f35' | '\\u0f37' | '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f84' | '\\u0f86' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fc6' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u1371' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1734' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17d3' | '\\u17d7' | '\\u17db' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u180b' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u200c' .. '\\u200f' | '\\u202a' .. '\\u202e' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2060' .. '\\u2063' | '\\u206a' .. '\\u206f' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20dc' | '\\u20e1' | '\\u20e5' .. '\\u20ea' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u302f' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\ufeff' | '\\uff04' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | '\\ufff9' .. '\\ufffb' )
            // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001B')||input.LA(1)=='$'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u007F' && input.LA(1)<='\u009F')||(input.LA(1)>='\u00A2' && input.LA(1)<='\u00A5')||input.LA(1)=='\u00AA'||input.LA(1)=='\u00AD'||input.LA(1)=='\u00B5'||input.LA(1)=='\u00BA'||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u0236')||(input.LA(1)>='\u0250' && input.LA(1)<='\u02C1')||(input.LA(1)>='\u02C6' && input.LA(1)<='\u02D1')||(input.LA(1)>='\u02E0' && input.LA(1)<='\u02E4')||input.LA(1)=='\u02EE'||(input.LA(1)>='\u0300' && input.LA(1)<='\u0357')||(input.LA(1)>='\u035D' && input.LA(1)<='\u036F')||input.LA(1)=='\u037A'||input.LA(1)=='\u0386'||(input.LA(1)>='\u0388' && input.LA(1)<='\u038A')||input.LA(1)=='\u038C'||(input.LA(1)>='\u038E' && input.LA(1)<='\u03A1')||(input.LA(1)>='\u03A3' && input.LA(1)<='\u03CE')||(input.LA(1)>='\u03D0' && input.LA(1)<='\u03F5')||(input.LA(1)>='\u03F7' && input.LA(1)<='\u03FB')||(input.LA(1)>='\u0400' && input.LA(1)<='\u0481')||(input.LA(1)>='\u0483' && input.LA(1)<='\u0486')||(input.LA(1)>='\u048A' && input.LA(1)<='\u04CE')||(input.LA(1)>='\u04D0' && input.LA(1)<='\u04F5')||(input.LA(1)>='\u04F8' && input.LA(1)<='\u04F9')||(input.LA(1)>='\u0500' && input.LA(1)<='\u050F')||(input.LA(1)>='\u0531' && input.LA(1)<='\u0556')||input.LA(1)=='\u0559'||(input.LA(1)>='\u0561' && input.LA(1)<='\u0587')||(input.LA(1)>='\u0591' && input.LA(1)<='\u05A1')||(input.LA(1)>='\u05A3' && input.LA(1)<='\u05B9')||(input.LA(1)>='\u05BB' && input.LA(1)<='\u05BD')||input.LA(1)=='\u05BF'||(input.LA(1)>='\u05C1' && input.LA(1)<='\u05C2')||input.LA(1)=='\u05C4'||(input.LA(1)>='\u05D0' && input.LA(1)<='\u05EA')||(input.LA(1)>='\u05F0' && input.LA(1)<='\u05F2')||(input.LA(1)>='\u0600' && input.LA(1)<='\u0603')||(input.LA(1)>='\u0610' && input.LA(1)<='\u0615')||(input.LA(1)>='\u0621' && input.LA(1)<='\u063A')||(input.LA(1)>='\u0640' && input.LA(1)<='\u0658')||(input.LA(1)>='\u0660' && input.LA(1)<='\u0669')||(input.LA(1)>='\u066E' && input.LA(1)<='\u06D3')||(input.LA(1)>='\u06D5' && input.LA(1)<='\u06DD')||(input.LA(1)>='\u06DF' && input.LA(1)<='\u06E8')||(input.LA(1)>='\u06EA' && input.LA(1)<='\u06FC')||input.LA(1)=='\u06FF'||(input.LA(1)>='\u070F' && input.LA(1)<='\u074A')||(input.LA(1)>='\u074D' && input.LA(1)<='\u074F')||(input.LA(1)>='\u0780' && input.LA(1)<='\u07B1')||(input.LA(1)>='\u0901' && input.LA(1)<='\u0939')||(input.LA(1)>='\u093C' && input.LA(1)<='\u094D')||(input.LA(1)>='\u0950' && input.LA(1)<='\u0954')||(input.LA(1)>='\u0958' && input.LA(1)<='\u0963')||(input.LA(1)>='\u0966' && input.LA(1)<='\u096F')||(input.LA(1)>='\u0981' && input.LA(1)<='\u0983')||(input.LA(1)>='\u0985' && input.LA(1)<='\u098C')||(input.LA(1)>='\u098F' && input.LA(1)<='\u0990')||(input.LA(1)>='\u0993' && input.LA(1)<='\u09A8')||(input.LA(1)>='\u09AA' && input.LA(1)<='\u09B0')||input.LA(1)=='\u09B2'||(input.LA(1)>='\u09B6' && input.LA(1)<='\u09B9')||(input.LA(1)>='\u09BC' && input.LA(1)<='\u09C4')||(input.LA(1)>='\u09C7' && input.LA(1)<='\u09C8')||(input.LA(1)>='\u09CB' && input.LA(1)<='\u09CD')||input.LA(1)=='\u09D7'||(input.LA(1)>='\u09DC' && input.LA(1)<='\u09DD')||(input.LA(1)>='\u09DF' && input.LA(1)<='\u09E3')||(input.LA(1)>='\u09E6' && input.LA(1)<='\u09F3')||(input.LA(1)>='\u0A01' && input.LA(1)<='\u0A03')||(input.LA(1)>='\u0A05' && input.LA(1)<='\u0A0A')||(input.LA(1)>='\u0A0F' && input.LA(1)<='\u0A10')||(input.LA(1)>='\u0A13' && input.LA(1)<='\u0A28')||(input.LA(1)>='\u0A2A' && input.LA(1)<='\u0A30')||(input.LA(1)>='\u0A32' && input.LA(1)<='\u0A33')||(input.LA(1)>='\u0A35' && input.LA(1)<='\u0A36')||(input.LA(1)>='\u0A38' && input.LA(1)<='\u0A39')||input.LA(1)=='\u0A3C'||(input.LA(1)>='\u0A3E' && input.LA(1)<='\u0A42')||(input.LA(1)>='\u0A47' && input.LA(1)<='\u0A48')||(input.LA(1)>='\u0A4B' && input.LA(1)<='\u0A4D')||(input.LA(1)>='\u0A59' && input.LA(1)<='\u0A5C')||input.LA(1)=='\u0A5E'||(input.LA(1)>='\u0A66' && input.LA(1)<='\u0A74')||(input.LA(1)>='\u0A81' && input.LA(1)<='\u0A83')||(input.LA(1)>='\u0A85' && input.LA(1)<='\u0A8D')||(input.LA(1)>='\u0A8F' && input.LA(1)<='\u0A91')||(input.LA(1)>='\u0A93' && input.LA(1)<='\u0AA8')||(input.LA(1)>='\u0AAA' && input.LA(1)<='\u0AB0')||(input.LA(1)>='\u0AB2' && input.LA(1)<='\u0AB3')||(input.LA(1)>='\u0AB5' && input.LA(1)<='\u0AB9')||(input.LA(1)>='\u0ABC' && input.LA(1)<='\u0AC5')||(input.LA(1)>='\u0AC7' && input.LA(1)<='\u0AC9')||(input.LA(1)>='\u0ACB' && input.LA(1)<='\u0ACD')||input.LA(1)=='\u0AD0'||(input.LA(1)>='\u0AE0' && input.LA(1)<='\u0AE3')||(input.LA(1)>='\u0AE6' && input.LA(1)<='\u0AEF')||input.LA(1)=='\u0AF1'||(input.LA(1)>='\u0B01' && input.LA(1)<='\u0B03')||(input.LA(1)>='\u0B05' && input.LA(1)<='\u0B0C')||(input.LA(1)>='\u0B0F' && input.LA(1)<='\u0B10')||(input.LA(1)>='\u0B13' && input.LA(1)<='\u0B28')||(input.LA(1)>='\u0B2A' && input.LA(1)<='\u0B30')||(input.LA(1)>='\u0B32' && input.LA(1)<='\u0B33')||(input.LA(1)>='\u0B35' && input.LA(1)<='\u0B39')||(input.LA(1)>='\u0B3C' && input.LA(1)<='\u0B43')||(input.LA(1)>='\u0B47' && input.LA(1)<='\u0B48')||(input.LA(1)>='\u0B4B' && input.LA(1)<='\u0B4D')||(input.LA(1)>='\u0B56' && input.LA(1)<='\u0B57')||(input.LA(1)>='\u0B5C' && input.LA(1)<='\u0B5D')||(input.LA(1)>='\u0B5F' && input.LA(1)<='\u0B61')||(input.LA(1)>='\u0B66' && input.LA(1)<='\u0B6F')||input.LA(1)=='\u0B71'||(input.LA(1)>='\u0B82' && input.LA(1)<='\u0B83')||(input.LA(1)>='\u0B85' && input.LA(1)<='\u0B8A')||(input.LA(1)>='\u0B8E' && input.LA(1)<='\u0B90')||(input.LA(1)>='\u0B92' && input.LA(1)<='\u0B95')||(input.LA(1)>='\u0B99' && input.LA(1)<='\u0B9A')||input.LA(1)=='\u0B9C'||(input.LA(1)>='\u0B9E' && input.LA(1)<='\u0B9F')||(input.LA(1)>='\u0BA3' && input.LA(1)<='\u0BA4')||(input.LA(1)>='\u0BA8' && input.LA(1)<='\u0BAA')||(input.LA(1)>='\u0BAE' && input.LA(1)<='\u0BB5')||(input.LA(1)>='\u0BB7' && input.LA(1)<='\u0BB9')||(input.LA(1)>='\u0BBE' && input.LA(1)<='\u0BC2')||(input.LA(1)>='\u0BC6' && input.LA(1)<='\u0BC8')||(input.LA(1)>='\u0BCA' && input.LA(1)<='\u0BCD')||input.LA(1)=='\u0BD7'||(input.LA(1)>='\u0BE7' && input.LA(1)<='\u0BEF')||input.LA(1)=='\u0BF9'||(input.LA(1)>='\u0C01' && input.LA(1)<='\u0C03')||(input.LA(1)>='\u0C05' && input.LA(1)<='\u0C0C')||(input.LA(1)>='\u0C0E' && input.LA(1)<='\u0C10')||(input.LA(1)>='\u0C12' && input.LA(1)<='\u0C28')||(input.LA(1)>='\u0C2A' && input.LA(1)<='\u0C33')||(input.LA(1)>='\u0C35' && input.LA(1)<='\u0C39')||(input.LA(1)>='\u0C3E' && input.LA(1)<='\u0C44')||(input.LA(1)>='\u0C46' && input.LA(1)<='\u0C48')||(input.LA(1)>='\u0C4A' && input.LA(1)<='\u0C4D')||(input.LA(1)>='\u0C55' && input.LA(1)<='\u0C56')||(input.LA(1)>='\u0C60' && input.LA(1)<='\u0C61')||(input.LA(1)>='\u0C66' && input.LA(1)<='\u0C6F')||(input.LA(1)>='\u0C82' && input.LA(1)<='\u0C83')||(input.LA(1)>='\u0C85' && input.LA(1)<='\u0C8C')||(input.LA(1)>='\u0C8E' && input.LA(1)<='\u0C90')||(input.LA(1)>='\u0C92' && input.LA(1)<='\u0CA8')||(input.LA(1)>='\u0CAA' && input.LA(1)<='\u0CB3')||(input.LA(1)>='\u0CB5' && input.LA(1)<='\u0CB9')||(input.LA(1)>='\u0CBC' && input.LA(1)<='\u0CC4')||(input.LA(1)>='\u0CC6' && input.LA(1)<='\u0CC8')||(input.LA(1)>='\u0CCA' && input.LA(1)<='\u0CCD')||(input.LA(1)>='\u0CD5' && input.LA(1)<='\u0CD6')||input.LA(1)=='\u0CDE'||(input.LA(1)>='\u0CE0' && input.LA(1)<='\u0CE1')||(input.LA(1)>='\u0CE6' && input.LA(1)<='\u0CEF')||(input.LA(1)>='\u0D02' && input.LA(1)<='\u0D03')||(input.LA(1)>='\u0D05' && input.LA(1)<='\u0D0C')||(input.LA(1)>='\u0D0E' && input.LA(1)<='\u0D10')||(input.LA(1)>='\u0D12' && input.LA(1)<='\u0D28')||(input.LA(1)>='\u0D2A' && input.LA(1)<='\u0D39')||(input.LA(1)>='\u0D3E' && input.LA(1)<='\u0D43')||(input.LA(1)>='\u0D46' && input.LA(1)<='\u0D48')||(input.LA(1)>='\u0D4A' && input.LA(1)<='\u0D4D')||input.LA(1)=='\u0D57'||(input.LA(1)>='\u0D60' && input.LA(1)<='\u0D61')||(input.LA(1)>='\u0D66' && input.LA(1)<='\u0D6F')||(input.LA(1)>='\u0D82' && input.LA(1)<='\u0D83')||(input.LA(1)>='\u0D85' && input.LA(1)<='\u0D96')||(input.LA(1)>='\u0D9A' && input.LA(1)<='\u0DB1')||(input.LA(1)>='\u0DB3' && input.LA(1)<='\u0DBB')||input.LA(1)=='\u0DBD'||(input.LA(1)>='\u0DC0' && input.LA(1)<='\u0DC6')||input.LA(1)=='\u0DCA'||(input.LA(1)>='\u0DCF' && input.LA(1)<='\u0DD4')||input.LA(1)=='\u0DD6'||(input.LA(1)>='\u0DD8' && input.LA(1)<='\u0DDF')||(input.LA(1)>='\u0DF2' && input.LA(1)<='\u0DF3')||(input.LA(1)>='\u0E01' && input.LA(1)<='\u0E3A')||(input.LA(1)>='\u0E3F' && input.LA(1)<='\u0E4E')||(input.LA(1)>='\u0E50' && input.LA(1)<='\u0E59')||(input.LA(1)>='\u0E81' && input.LA(1)<='\u0E82')||input.LA(1)=='\u0E84'||(input.LA(1)>='\u0E87' && input.LA(1)<='\u0E88')||input.LA(1)=='\u0E8A'||input.LA(1)=='\u0E8D'||(input.LA(1)>='\u0E94' && input.LA(1)<='\u0E97')||(input.LA(1)>='\u0E99' && input.LA(1)<='\u0E9F')||(input.LA(1)>='\u0EA1' && input.LA(1)<='\u0EA3')||input.LA(1)=='\u0EA5'||input.LA(1)=='\u0EA7'||(input.LA(1)>='\u0EAA' && input.LA(1)<='\u0EAB')||(input.LA(1)>='\u0EAD' && input.LA(1)<='\u0EB9')||(input.LA(1)>='\u0EBB' && input.LA(1)<='\u0EBD')||(input.LA(1)>='\u0EC0' && input.LA(1)<='\u0EC4')||input.LA(1)=='\u0EC6'||(input.LA(1)>='\u0EC8' && input.LA(1)<='\u0ECD')||(input.LA(1)>='\u0ED0' && input.LA(1)<='\u0ED9')||(input.LA(1)>='\u0EDC' && input.LA(1)<='\u0EDD')||input.LA(1)=='\u0F00'||(input.LA(1)>='\u0F18' && input.LA(1)<='\u0F19')||(input.LA(1)>='\u0F20' && input.LA(1)<='\u0F29')||input.LA(1)=='\u0F35'||input.LA(1)=='\u0F37'||input.LA(1)=='\u0F39'||(input.LA(1)>='\u0F3E' && input.LA(1)<='\u0F47')||(input.LA(1)>='\u0F49' && input.LA(1)<='\u0F6A')||(input.LA(1)>='\u0F71' && input.LA(1)<='\u0F84')||(input.LA(1)>='\u0F86' && input.LA(1)<='\u0F8B')||(input.LA(1)>='\u0F90' && input.LA(1)<='\u0F97')||(input.LA(1)>='\u0F99' && input.LA(1)<='\u0FBC')||input.LA(1)=='\u0FC6'||(input.LA(1)>='\u1000' && input.LA(1)<='\u1021')||(input.LA(1)>='\u1023' && input.LA(1)<='\u1027')||(input.LA(1)>='\u1029' && input.LA(1)<='\u102A')||(input.LA(1)>='\u102C' && input.LA(1)<='\u1032')||(input.LA(1)>='\u1036' && input.LA(1)<='\u1039')||(input.LA(1)>='\u1040' && input.LA(1)<='\u1049')||(input.LA(1)>='\u1050' && input.LA(1)<='\u1059')||(input.LA(1)>='\u10A0' && input.LA(1)<='\u10C5')||(input.LA(1)>='\u10D0' && input.LA(1)<='\u10F8')||(input.LA(1)>='\u1100' && input.LA(1)<='\u1159')||(input.LA(1)>='\u115F' && input.LA(1)<='\u11A2')||(input.LA(1)>='\u11A8' && input.LA(1)<='\u11F9')||(input.LA(1)>='\u1200' && input.LA(1)<='\u1206')||(input.LA(1)>='\u1208' && input.LA(1)<='\u1246')||input.LA(1)=='\u1248'||(input.LA(1)>='\u124A' && input.LA(1)<='\u124D')||(input.LA(1)>='\u1250' && input.LA(1)<='\u1256')||input.LA(1)=='\u1258'||(input.LA(1)>='\u125A' && input.LA(1)<='\u125D')||(input.LA(1)>='\u1260' && input.LA(1)<='\u1286')||input.LA(1)=='\u1288'||(input.LA(1)>='\u128A' && input.LA(1)<='\u128D')||(input.LA(1)>='\u1290' && input.LA(1)<='\u12AE')||input.LA(1)=='\u12B0'||(input.LA(1)>='\u12B2' && input.LA(1)<='\u12B5')||(input.LA(1)>='\u12B8' && input.LA(1)<='\u12BE')||input.LA(1)=='\u12C0'||(input.LA(1)>='\u12C2' && input.LA(1)<='\u12C5')||(input.LA(1)>='\u12C8' && input.LA(1)<='\u12CE')||(input.LA(1)>='\u12D0' && input.LA(1)<='\u12D6')||(input.LA(1)>='\u12D8' && input.LA(1)<='\u12EE')||(input.LA(1)>='\u12F0' && input.LA(1)<='\u130E')||input.LA(1)=='\u1310'||(input.LA(1)>='\u1312' && input.LA(1)<='\u1315')||(input.LA(1)>='\u1318' && input.LA(1)<='\u131E')||(input.LA(1)>='\u1320' && input.LA(1)<='\u1346')||(input.LA(1)>='\u1348' && input.LA(1)<='\u135A')||(input.LA(1)>='\u1369' && input.LA(1)<='\u1371')||(input.LA(1)>='\u13A0' && input.LA(1)<='\u13F4')||(input.LA(1)>='\u1401' && input.LA(1)<='\u166C')||(input.LA(1)>='\u166F' && input.LA(1)<='\u1676')||(input.LA(1)>='\u1681' && input.LA(1)<='\u169A')||(input.LA(1)>='\u16A0' && input.LA(1)<='\u16EA')||(input.LA(1)>='\u16EE' && input.LA(1)<='\u16F0')||(input.LA(1)>='\u1700' && input.LA(1)<='\u170C')||(input.LA(1)>='\u170E' && input.LA(1)<='\u1714')||(input.LA(1)>='\u1720' && input.LA(1)<='\u1734')||(input.LA(1)>='\u1740' && input.LA(1)<='\u1753')||(input.LA(1)>='\u1760' && input.LA(1)<='\u176C')||(input.LA(1)>='\u176E' && input.LA(1)<='\u1770')||(input.LA(1)>='\u1772' && input.LA(1)<='\u1773')||(input.LA(1)>='\u1780' && input.LA(1)<='\u17D3')||input.LA(1)=='\u17D7'||(input.LA(1)>='\u17DB' && input.LA(1)<='\u17DD')||(input.LA(1)>='\u17E0' && input.LA(1)<='\u17E9')||(input.LA(1)>='\u180B' && input.LA(1)<='\u180D')||(input.LA(1)>='\u1810' && input.LA(1)<='\u1819')||(input.LA(1)>='\u1820' && input.LA(1)<='\u1877')||(input.LA(1)>='\u1880' && input.LA(1)<='\u18A9')||(input.LA(1)>='\u1900' && input.LA(1)<='\u191C')||(input.LA(1)>='\u1920' && input.LA(1)<='\u192B')||(input.LA(1)>='\u1930' && input.LA(1)<='\u193B')||(input.LA(1)>='\u1946' && input.LA(1)<='\u196D')||(input.LA(1)>='\u1970' && input.LA(1)<='\u1974')||(input.LA(1)>='\u1D00' && input.LA(1)<='\u1D6B')||(input.LA(1)>='\u1E00' && input.LA(1)<='\u1E9B')||(input.LA(1)>='\u1EA0' && input.LA(1)<='\u1EF9')||(input.LA(1)>='\u1F00' && input.LA(1)<='\u1F15')||(input.LA(1)>='\u1F18' && input.LA(1)<='\u1F1D')||(input.LA(1)>='\u1F20' && input.LA(1)<='\u1F45')||(input.LA(1)>='\u1F48' && input.LA(1)<='\u1F4D')||(input.LA(1)>='\u1F50' && input.LA(1)<='\u1F57')||input.LA(1)=='\u1F59'||input.LA(1)=='\u1F5B'||input.LA(1)=='\u1F5D'||(input.LA(1)>='\u1F5F' && input.LA(1)<='\u1F7D')||(input.LA(1)>='\u1F80' && input.LA(1)<='\u1FB4')||(input.LA(1)>='\u1FB6' && input.LA(1)<='\u1FBC')||input.LA(1)=='\u1FBE'||(input.LA(1)>='\u1FC2' && input.LA(1)<='\u1FC4')||(input.LA(1)>='\u1FC6' && input.LA(1)<='\u1FCC')||(input.LA(1)>='\u1FD0' && input.LA(1)<='\u1FD3')||(input.LA(1)>='\u1FD6' && input.LA(1)<='\u1FDB')||(input.LA(1)>='\u1FE0' && input.LA(1)<='\u1FEC')||(input.LA(1)>='\u1FF2' && input.LA(1)<='\u1FF4')||(input.LA(1)>='\u1FF6' && input.LA(1)<='\u1FFC')||(input.LA(1)>='\u200C' && input.LA(1)<='\u200F')||(input.LA(1)>='\u202A' && input.LA(1)<='\u202E')||(input.LA(1)>='\u203F' && input.LA(1)<='\u2040')||input.LA(1)=='\u2054'||(input.LA(1)>='\u2060' && input.LA(1)<='\u2063')||(input.LA(1)>='\u206A' && input.LA(1)<='\u206F')||input.LA(1)=='\u2071'||input.LA(1)=='\u207F'||(input.LA(1)>='\u20A0' && input.LA(1)<='\u20B1')||(input.LA(1)>='\u20D0' && input.LA(1)<='\u20DC')||input.LA(1)=='\u20E1'||(input.LA(1)>='\u20E5' && input.LA(1)<='\u20EA')||input.LA(1)=='\u2102'||input.LA(1)=='\u2107'||(input.LA(1)>='\u210A' && input.LA(1)<='\u2113')||input.LA(1)=='\u2115'||(input.LA(1)>='\u2119' && input.LA(1)<='\u211D')||input.LA(1)=='\u2124'||input.LA(1)=='\u2126'||input.LA(1)=='\u2128'||(input.LA(1)>='\u212A' && input.LA(1)<='\u212D')||(input.LA(1)>='\u212F' && input.LA(1)<='\u2131')||(input.LA(1)>='\u2133' && input.LA(1)<='\u2139')||(input.LA(1)>='\u213D' && input.LA(1)<='\u213F')||(input.LA(1)>='\u2145' && input.LA(1)<='\u2149')||(input.LA(1)>='\u2160' && input.LA(1)<='\u2183')||(input.LA(1)>='\u3005' && input.LA(1)<='\u3007')||(input.LA(1)>='\u3021' && input.LA(1)<='\u302F')||(input.LA(1)>='\u3031' && input.LA(1)<='\u3035')||(input.LA(1)>='\u3038' && input.LA(1)<='\u303C')||(input.LA(1)>='\u3041' && input.LA(1)<='\u3096')||(input.LA(1)>='\u3099' && input.LA(1)<='\u309A')||(input.LA(1)>='\u309D' && input.LA(1)<='\u309F')||(input.LA(1)>='\u30A1' && input.LA(1)<='\u30FF')||(input.LA(1)>='\u3105' && input.LA(1)<='\u312C')||(input.LA(1)>='\u3131' && input.LA(1)<='\u318E')||(input.LA(1)>='\u31A0' && input.LA(1)<='\u31B7')||(input.LA(1)>='\u31F0' && input.LA(1)<='\u31FF')||(input.LA(1)>='\u3400' && input.LA(1)<='\u4DB5')||(input.LA(1)>='\u4E00' && input.LA(1)<='\u9FA5')||(input.LA(1)>='\uA000' && input.LA(1)<='\uA48C')||(input.LA(1)>='\uAC00' && input.LA(1)<='\uD7A3')||(input.LA(1)>='\uF900' && input.LA(1)<='\uFA2D')||(input.LA(1)>='\uFA30' && input.LA(1)<='\uFA6A')||(input.LA(1)>='\uFB00' && input.LA(1)<='\uFB06')||(input.LA(1)>='\uFB13' && input.LA(1)<='\uFB17')||(input.LA(1)>='\uFB1D' && input.LA(1)<='\uFB28')||(input.LA(1)>='\uFB2A' && input.LA(1)<='\uFB36')||(input.LA(1)>='\uFB38' && input.LA(1)<='\uFB3C')||input.LA(1)=='\uFB3E'||(input.LA(1)>='\uFB40' && input.LA(1)<='\uFB41')||(input.LA(1)>='\uFB43' && input.LA(1)<='\uFB44')||(input.LA(1)>='\uFB46' && input.LA(1)<='\uFBB1')||(input.LA(1)>='\uFBD3' && input.LA(1)<='\uFD3D')||(input.LA(1)>='\uFD50' && input.LA(1)<='\uFD8F')||(input.LA(1)>='\uFD92' && input.LA(1)<='\uFDC7')||(input.LA(1)>='\uFDF0' && input.LA(1)<='\uFDFC')||(input.LA(1)>='\uFE00' && input.LA(1)<='\uFE0F')||(input.LA(1)>='\uFE20' && input.LA(1)<='\uFE23')||(input.LA(1)>='\uFE33' && input.LA(1)<='\uFE34')||(input.LA(1)>='\uFE4D' && input.LA(1)<='\uFE4F')||input.LA(1)=='\uFE69'||(input.LA(1)>='\uFE70' && input.LA(1)<='\uFE74')||(input.LA(1)>='\uFE76' && input.LA(1)<='\uFEFC')||input.LA(1)=='\uFEFF'||input.LA(1)=='\uFF04'||(input.LA(1)>='\uFF10' && input.LA(1)<='\uFF19')||(input.LA(1)>='\uFF21' && input.LA(1)<='\uFF3A')||input.LA(1)=='\uFF3F'||(input.LA(1)>='\uFF41' && input.LA(1)<='\uFF5A')||(input.LA(1)>='\uFF65' && input.LA(1)<='\uFFBE')||(input.LA(1)>='\uFFC2' && input.LA(1)<='\uFFC7')||(input.LA(1)>='\uFFCA' && input.LA(1)<='\uFFCF')||(input.LA(1)>='\uFFD2' && input.LA(1)<='\uFFD7')||(input.LA(1)>='\uFFDA' && input.LA(1)<='\uFFDC')||(input.LA(1)>='\uFFE0' && input.LA(1)<='\uFFE1')||(input.LA(1)>='\uFFE5' && input.LA(1)<='\uFFE6')||(input.LA(1)>='\uFFF9' && input.LA(1)<='\uFFFB') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "IdentifierPart"

    public void mTokens() throws RecognitionException {
        // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:8: ( WS | FLOAT | INT | STRING | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | ID | SEMI_COLON | COMMA | DOT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | COLON | STAR | MISC )
        int alt22=18;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:10: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:13: FLOAT
                {
                mFLOAT(); if (state.failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:19: INT
                {
                mINT(); if (state.failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:23: STRING
                {
                mSTRING(); if (state.failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:30: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:59: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:87: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:106: ID
                {
                mID(); if (state.failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:109: SEMI_COLON
                {
                mSEMI_COLON(); if (state.failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:120: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:126: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:130: LEFT_PAREN
                {
                mLEFT_PAREN(); if (state.failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:141: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (state.failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:153: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:165: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:178: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:184: STAR
                {
                mSTAR(); if (state.failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:1:189: MISC
                {
                mMISC(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_NodeTestDSL
    public final void synpred1_NodeTestDSL_fragment() throws RecognitionException {   
        // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:243:20: ( '\\r\\n' )
        // C:\\dev\\droolsjbpm\\drools\\drools-core\\src\\test\\resources\\org\\drools\\reteoo\\test\\parser\\NodeTestDSL.g:243:22: '\\r\\n'
        {
        match("\r\n"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_NodeTestDSL

    public final boolean synpred1_NodeTestDSL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_NodeTestDSL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA21 dfa21 = new DFA21(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA21_eotS =
        "\1\uffff\1\25\1\27\1\31\1\33\1\35\1\uffff\1\37\1\41\31\uffff";
    static final String DFA21_eofS =
        "\42\uffff";
    static final String DFA21_minS =
        "\1\41\1\46\1\174\3\75\1\uffff\1\75\1\76\31\uffff";
    static final String DFA21_maxS =
        "\1\175\1\46\1\174\3\75\1\uffff\1\75\1\76\31\uffff";
    static final String DFA21_acceptS =
        "\6\uffff\1\6\2\uffff\1\12\1\13\1\15\1\16\1\17\1\20\1\21\1\24\1"+
        "\25\1\31\1\32\1\1\1\23\1\2\1\22\1\3\1\30\1\4\1\27\1\5\1\26\1\7\1"+
        "\11\1\10\1\14";
    static final String DFA21_specialS =
        "\42\uffff}>";
    static final String[] DFA21_transitionS = {
            "\1\7\2\uffff\1\20\1\11\1\1\1\16\3\uffff\1\13\1\uffff\1\10\1"+
            "\uffff\1\15\12\uffff\1\6\1\uffff\1\3\1\5\1\4\1\14\1\21\33\uffff"+
            "\1\17\1\uffff\1\12\34\uffff\1\22\1\2\1\23",
            "\1\24",
            "\1\26",
            "\1\30",
            "\1\32",
            "\1\34",
            "",
            "\1\36",
            "\1\40",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
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
            return "341:1: MISC : ( '&&' | '||' | '<=' | '>=' | '==' | ':=' | '!=' | '->' | '!' | '%' | '^' | '-' | '+' | '?' | '/' | '\\'' | '\\\\' | '|' | '&' | '$' | '@' | '=' | '>' | '<' | '{' | '}' );";
        }
    }
    static final String DFA22_eotS =
        "\2\uffff\1\23\1\25\1\uffff\1\23\1\uffff\1\23\11\uffff\1\30\7\uffff";
    static final String DFA22_eofS =
        "\31\uffff";
    static final String DFA22_minS =
        "\1\11\1\uffff\1\60\1\56\1\uffff\1\0\1\uffff\1\52\11\uffff\1\75"+
        "\7\uffff";
    static final String DFA22_maxS =
        "\1\uffe6\1\uffff\2\71\1\uffff\1\uffff\1\uffff\1\57\11\uffff\1\75"+
        "\7\uffff";
    static final String DFA22_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\uffff\1\5\1\uffff\2\10\1\11\1\12\1\13"+
        "\1\14\1\15\1\16\1\17\1\uffff\1\21\1\22\1\2\1\3\1\6\1\7\1\20";
    static final String DFA22_specialS =
        "\5\uffff\1\0\23\uffff}>";
    static final String[] DFA22_transitionS = {
            "\2\1\1\uffff\2\1\22\uffff\1\1\1\23\1\4\1\6\1\10\2\23\1\5\1"+
            "\15\1\16\1\22\1\23\1\13\1\2\1\14\1\7\12\3\1\21\1\12\5\23\32"+
            "\11\1\17\1\23\1\20\1\23\34\11\3\23\44\uffff\4\11\4\uffff\1\11"+
            "\12\uffff\1\11\4\uffff\1\11\5\uffff\27\11\1\uffff\37\11\1\uffff"+
            "\u013f\11\31\uffff\162\11\4\uffff\14\11\16\uffff\5\11\11\uffff"+
            "\1\11\u008b\uffff\1\11\13\uffff\1\11\1\uffff\3\11\1\uffff\1"+
            "\11\1\uffff\24\11\1\uffff\54\11\1\uffff\46\11\1\uffff\5\11\4"+
            "\uffff\u0082\11\10\uffff\105\11\1\uffff\46\11\2\uffff\2\11\6"+
            "\uffff\20\11\41\uffff\46\11\2\uffff\1\11\7\uffff\47\11\110\uffff"+
            "\33\11\5\uffff\3\11\56\uffff\32\11\5\uffff\13\11\43\uffff\2"+
            "\11\1\uffff\143\11\1\uffff\1\11\17\uffff\2\11\7\uffff\2\11\12"+
            "\uffff\3\11\2\uffff\1\11\20\uffff\1\11\1\uffff\36\11\35\uffff"+
            "\3\11\60\uffff\46\11\13\uffff\1\11\u0152\uffff\66\11\3\uffff"+
            "\1\11\22\uffff\1\11\7\uffff\12\11\43\uffff\10\11\2\uffff\2\11"+
            "\2\uffff\26\11\1\uffff\7\11\1\uffff\1\11\3\uffff\4\11\3\uffff"+
            "\1\11\36\uffff\2\11\1\uffff\3\11\16\uffff\4\11\21\uffff\6\11"+
            "\4\uffff\2\11\2\uffff\26\11\1\uffff\7\11\1\uffff\2\11\1\uffff"+
            "\2\11\1\uffff\2\11\37\uffff\4\11\1\uffff\1\11\23\uffff\3\11"+
            "\20\uffff\11\11\1\uffff\3\11\1\uffff\26\11\1\uffff\7\11\1\uffff"+
            "\2\11\1\uffff\5\11\3\uffff\1\11\22\uffff\1\11\17\uffff\2\11"+
            "\17\uffff\1\11\23\uffff\10\11\2\uffff\2\11\2\uffff\26\11\1\uffff"+
            "\7\11\1\uffff\2\11\1\uffff\5\11\3\uffff\1\11\36\uffff\2\11\1"+
            "\uffff\3\11\17\uffff\1\11\21\uffff\1\11\1\uffff\6\11\3\uffff"+
            "\3\11\1\uffff\4\11\3\uffff\2\11\1\uffff\1\11\1\uffff\2\11\3"+
            "\uffff\2\11\3\uffff\3\11\3\uffff\10\11\1\uffff\3\11\77\uffff"+
            "\1\11\13\uffff\10\11\1\uffff\3\11\1\uffff\27\11\1\uffff\12\11"+
            "\1\uffff\5\11\46\uffff\2\11\43\uffff\10\11\1\uffff\3\11\1\uffff"+
            "\27\11\1\uffff\12\11\1\uffff\5\11\3\uffff\1\11\40\uffff\1\11"+
            "\1\uffff\2\11\43\uffff\10\11\1\uffff\3\11\1\uffff\27\11\1\uffff"+
            "\20\11\46\uffff\2\11\43\uffff\22\11\3\uffff\30\11\1\uffff\11"+
            "\11\1\uffff\1\11\2\uffff\7\11\72\uffff\60\11\1\uffff\2\11\13"+
            "\uffff\10\11\72\uffff\2\11\1\uffff\1\11\2\uffff\2\11\1\uffff"+
            "\1\11\2\uffff\1\11\6\uffff\4\11\1\uffff\7\11\1\uffff\3\11\1"+
            "\uffff\1\11\1\uffff\1\11\2\uffff\2\11\1\uffff\4\11\1\uffff\2"+
            "\11\11\uffff\1\11\2\uffff\5\11\1\uffff\1\11\25\uffff\2\11\42"+
            "\uffff\1\11\77\uffff\10\11\1\uffff\42\11\35\uffff\4\11\164\uffff"+
            "\42\11\1\uffff\5\11\1\uffff\2\11\45\uffff\6\11\112\uffff\46"+
            "\11\12\uffff\51\11\7\uffff\132\11\5\uffff\104\11\5\uffff\122"+
            "\11\6\uffff\7\11\1\uffff\77\11\1\uffff\1\11\1\uffff\4\11\2\uffff"+
            "\7\11\1\uffff\1\11\1\uffff\4\11\2\uffff\47\11\1\uffff\1\11\1"+
            "\uffff\4\11\2\uffff\37\11\1\uffff\1\11\1\uffff\4\11\2\uffff"+
            "\7\11\1\uffff\1\11\1\uffff\4\11\2\uffff\7\11\1\uffff\7\11\1"+
            "\uffff\27\11\1\uffff\37\11\1\uffff\1\11\1\uffff\4\11\2\uffff"+
            "\7\11\1\uffff\47\11\1\uffff\23\11\105\uffff\125\11\14\uffff"+
            "\u026c\11\2\uffff\10\11\12\uffff\32\11\5\uffff\113\11\3\uffff"+
            "\3\11\17\uffff\15\11\1\uffff\4\11\16\uffff\22\11\16\uffff\22"+
            "\11\16\uffff\15\11\1\uffff\3\11\17\uffff\64\11\43\uffff\1\11"+
            "\3\uffff\2\11\103\uffff\130\11\10\uffff\51\11\127\uffff\35\11"+
            "\63\uffff\36\11\2\uffff\5\11\u038b\uffff\154\11\u0094\uffff"+
            "\u009c\11\4\uffff\132\11\6\uffff\26\11\2\uffff\6\11\2\uffff"+
            "\46\11\2\uffff\6\11\2\uffff\10\11\1\uffff\1\11\1\uffff\1\11"+
            "\1\uffff\1\11\1\uffff\37\11\2\uffff\65\11\1\uffff\7\11\1\uffff"+
            "\1\11\3\uffff\3\11\1\uffff\7\11\3\uffff\4\11\2\uffff\6\11\4"+
            "\uffff\15\11\5\uffff\3\11\1\uffff\7\11\102\uffff\2\11\23\uffff"+
            "\1\11\34\uffff\1\11\15\uffff\1\11\40\uffff\22\11\120\uffff\1"+
            "\11\4\uffff\1\11\2\uffff\12\11\1\uffff\1\11\3\uffff\5\11\6\uffff"+
            "\1\11\1\uffff\1\11\1\uffff\1\11\1\uffff\4\11\1\uffff\3\11\1"+
            "\uffff\7\11\3\uffff\3\11\5\uffff\5\11\26\uffff\44\11\u0e81\uffff"+
            "\3\11\31\uffff\11\11\7\uffff\5\11\2\uffff\5\11\4\uffff\126\11"+
            "\6\uffff\3\11\1\uffff\137\11\5\uffff\50\11\4\uffff\136\11\21"+
            "\uffff\30\11\70\uffff\20\11\u0200\uffff\u19b6\11\112\uffff\u51a6"+
            "\11\132\uffff\u048d\11\u0773\uffff\u2ba4\11\u215c\uffff\u012e"+
            "\11\2\uffff\73\11\u0095\uffff\7\11\14\uffff\5\11\5\uffff\1\11"+
            "\1\uffff\12\11\1\uffff\15\11\1\uffff\5\11\1\uffff\1\11\1\uffff"+
            "\2\11\1\uffff\2\11\1\uffff\154\11\41\uffff\u016b\11\22\uffff"+
            "\100\11\2\uffff\66\11\50\uffff\15\11\66\uffff\2\11\30\uffff"+
            "\3\11\31\uffff\1\11\6\uffff\5\11\1\uffff\u0087\11\7\uffff\1"+
            "\11\34\uffff\32\11\4\uffff\1\11\1\uffff\32\11\12\uffff\132\11"+
            "\3\uffff\6\11\2\uffff\6\11\2\uffff\6\11\2\uffff\3\11\3\uffff"+
            "\2\11\3\uffff\2\11",
            "",
            "\12\3",
            "\1\24\1\uffff\12\3",
            "",
            "\0\4",
            "",
            "\1\27\4\uffff\1\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\23",
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
            return "1:1: Tokens : ( WS | FLOAT | INT | STRING | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | ID | SEMI_COLON | COMMA | DOT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | COLON | STAR | MISC );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_5 = input.LA(1);

                        s = -1;
                        if ( ((LA22_5>='\u0000' && LA22_5<='\uFFFF')) ) {s = 4;}

                        else s = 19;

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
 

}