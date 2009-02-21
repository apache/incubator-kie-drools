// $ANTLR 3.1.1 src/main/resources/org/drools/lang/dsl/DSLMap.g 2009-02-20 18:38:47

	package org.drools.lang.dsl;
	import java.util.List;
	import java.util.ArrayList;
//	import org.drools.lang.dsl.DSLMappingParseException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DSLMapLexer extends Lexer {
    public static final int COMMA=27;
    public static final int RIGHT_CURLY=30;
    public static final int VT_ENTRY_VAL=14;
    public static final int WS=31;
    public static final int MISC=35;
    public static final int VT_META=12;
    public static final int VT_CONSEQUENCE=9;
    public static final int VT_SPACE=20;
    public static final int LINE_COMMENT=22;
    public static final int VT_ANY=11;
    public static final int VT_LITERAL=17;
    public static final int DOT=33;
    public static final int EQUALS=23;
    public static final int VT_DSL_GRAMMAR=4;
    public static final int VT_CONDITION=8;
    public static final int VT_VAR_DEF=15;
    public static final int VT_ENTRY=6;
    public static final int VT_PATTERN=18;
    public static final int LITERAL=26;
    public static final int EscapeSequence=32;
    public static final int VT_COMMENT=5;
    public static final int EOF=-1;
    public static final int EOL=21;
    public static final int LEFT_SQUARE=24;
    public static final int VT_ENTRY_KEY=13;
    public static final int VT_SCOPE=7;
    public static final int COLON=28;
    public static final int VT_KEYWORD=10;
    public static final int VT_QUAL=19;
    public static final int VT_VAR_REF=16;
    public static final int LEFT_CURLY=29;
    public static final int POUND=34;
    public static final int RIGHT_SQUARE=25;

    // delegates
    // delegators

    public DSLMapLexer() {;} 
    public DSLMapLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DSLMapLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/dsl/DSLMap.g"; }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:269:9: ( ( ' ' | '\\t' | '\\f' )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:269:17: ( ' ' | '\\t' | '\\f' )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:269:17: ( ' ' | '\\t' | '\\f' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\t'||LA1_0=='\f'||LA1_0==' ') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
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
            int _type = EOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:276:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1_DSLMap())) {
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
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:278:25: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:279:25: '\\n'
                    {
                    match('\n'); if (state.failed) return ;

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EOL"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:285:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' | '=' | 'u' | '0' | '#' ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:285:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' | '=' | 'u' | '0' | '#' )
            {
            match('\\'); if (state.failed) return ;
            if ( (input.LA(1)>='\"' && input.LA(1)<='$')||(input.LA(1)>='&' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)=='0'||input.LA(1)=='='||input.LA(1)=='?'||(input.LA(1)>='A' && input.LA(1)<='B')||(input.LA(1)>='D' && input.LA(1)<='E')||input.LA(1)=='G'||input.LA(1)=='Q'||input.LA(1)=='S'||input.LA(1)=='W'||(input.LA(1)>='Z' && input.LA(1)<='^')||(input.LA(1)>='a' && input.LA(1)<='f')||(input.LA(1)>='n' && input.LA(1)<='p')||(input.LA(1)>='r' && input.LA(1)<='u')||(input.LA(1)>='w' && input.LA(1)<='x')||(input.LA(1)>='z' && input.LA(1)<='}') ) {
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
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "LEFT_SQUARE"
    public final void mLEFT_SQUARE() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:292:9: ( '[' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:292:11: '['
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
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:296:9: ( ']' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:296:11: ']'
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

    // $ANTLR start "LEFT_CURLY"
    public final void mLEFT_CURLY() throws RecognitionException {
        try {
            int _type = LEFT_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:300:9: ( '{' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:300:11: '{'
            {
            match('{'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_CURLY"

    // $ANTLR start "RIGHT_CURLY"
    public final void mRIGHT_CURLY() throws RecognitionException {
        try {
            int _type = RIGHT_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:304:9: ( '}' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:304:11: '}'
            {
            match('}'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_CURLY"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:307:8: ( '=' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:307:10: '='
            {
            match('='); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:310:5: ( '.' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:310:7: '.'
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

    // $ANTLR start "POUND"
    public final void mPOUND() throws RecognitionException {
        try {
            int _type = POUND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:313:9: ( '#' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:313:11: '#'
            {
            match('#'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POUND"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:316:7: ( ':' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:316:9: ':'
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

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:319:7: ( ',' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:319:9: ','
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

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:2: ( POUND ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:4: POUND ( options {greedy=false; } : . )* EOL
            {
            mPOUND(); if (state.failed) return ;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:10: ( options {greedy=false; } : . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\r') ) {
                    alt3=2;
                }
                else if ( (LA3_0=='\n') ) {
                    alt3=2;
                }
                else if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:37: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            mEOL(); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "LITERAL"
    public final void mLITERAL() throws RecognitionException {
        try {
            int _type = LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+
            int cnt4=0;
            loop4:
            do {
                int alt4=9;
                alt4 = dfa4.predict(input);
                switch (alt4) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:5: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:14: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:23: '_'
            	    {
            	    match('_'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:27: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:36: '\\u00c0' .. '\\u00ff'
            	    {
            	    matchRange('\u00C0','\u00FF'); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:55: MISC
            	    {
            	    mMISC(); if (state.failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:60: EscapeSequence
            	    {
            	    mEscapeSequence(); if (state.failed) return ;

            	    }
            	    break;
            	case 8 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:75: DOT
            	    {
            	    mDOT(); if (state.failed) return ;

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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LITERAL"

    // $ANTLR start "MISC"
    public final void mMISC() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:339:7: ( '>' | '<' | '!' | '@' | '$' | '%' | '^' | '*' | '-' | '+' | '?' | COMMA | '/' | '\\'' | '\"' | '|' | '&' | '(' | ')' | ';' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:
            {
            if ( (input.LA(1)>='!' && input.LA(1)<='\"')||(input.LA(1)>='$' && input.LA(1)<='-')||input.LA(1)=='/'||(input.LA(1)>=';' && input.LA(1)<='<')||(input.LA(1)>='>' && input.LA(1)<='@')||input.LA(1)=='^'||input.LA(1)=='|' ) {
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
    // $ANTLR end "MISC"

    public void mTokens() throws RecognitionException {
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:8: ( WS | EOL | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | EQUALS | DOT | POUND | COLON | COMMA | LINE_COMMENT | LITERAL )
        int alt5=13;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:10: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:13: EOL
                {
                mEOL(); if (state.failed) return ;

                }
                break;
            case 3 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:17: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 4 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:29: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 5 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:42: LEFT_CURLY
                {
                mLEFT_CURLY(); if (state.failed) return ;

                }
                break;
            case 6 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:53: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (state.failed) return ;

                }
                break;
            case 7 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:65: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 8 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:72: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 9 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:76: POUND
                {
                mPOUND(); if (state.failed) return ;

                }
                break;
            case 10 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:82: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 11 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:88: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 12 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:94: LINE_COMMENT
                {
                mLINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 13 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:107: LITERAL
                {
                mLITERAL(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_DSLMap
    public final void synpred1_DSLMap_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:14: ( '\\r\\n' )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:16: '\\r\\n'
        {
        match("\r\n"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_DSLMap

    public final boolean synpred1_DSLMap() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DSLMap_fragment(); // can never throw exception
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
    static final String DFA4_eotS =
        "\1\1\11\uffff";
    static final String DFA4_eofS =
        "\12\uffff";
    static final String DFA4_minS =
        "\1\41\11\uffff";
    static final String DFA4_maxS =
        "\1\u00ff\11\uffff";
    static final String DFA4_acceptS =
        "\1\uffff\1\11\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA4_specialS =
        "\12\uffff}>";
    static final String[] DFA4_transitionS = {
            "\2\7\1\uffff\12\7\1\11\1\7\12\5\1\uffff\2\7\1\uffff\3\7\32"+
            "\3\1\uffff\1\10\1\uffff\1\7\1\4\1\uffff\32\2\1\uffff\1\7\103"+
            "\uffff\100\6",
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
            return "()+ loopback of 335:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+";
        }
    }
    static final String DFA5_eotS =
        "\10\uffff\1\15\1\16\1\uffff\1\20\5\uffff";
    static final String DFA5_eofS =
        "\21\uffff";
    static final String DFA5_minS =
        "\1\11\7\uffff\1\41\1\0\1\uffff\1\41\5\uffff";
    static final String DFA5_maxS =
        "\1\u00ff\7\uffff\1\u00ff\1\uffff\1\uffff\1\u00ff\5\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff\1\12\1\uffff\1\15"+
        "\1\10\1\11\1\14\1\13";
    static final String DFA5_specialS =
        "\11\uffff\1\0\7\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\2\1\uffff\1\1\1\2\22\uffff\1\1\2\14\1\11\10\14\1\13"+
            "\1\14\1\10\13\14\1\12\2\14\1\7\35\14\1\3\1\14\1\4\2\14\1\uffff"+
            "\32\14\1\5\1\14\1\6\102\uffff\100\14",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\2\14\1\uffff\26\14\1\uffff\2\14\1\uffff\35\14\1\uffff\1\14"+
            "\1\uffff\2\14\1\uffff\32\14\1\uffff\1\14\103\uffff\100\14",
            "\0\17",
            "",
            "\2\14\1\uffff\26\14\1\uffff\2\14\1\uffff\35\14\1\uffff\1\14"+
            "\1\uffff\2\14\1\uffff\32\14\1\uffff\1\14\103\uffff\100\14",
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
            return "1:1: Tokens : ( WS | EOL | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | EQUALS | DOT | POUND | COLON | COMMA | LINE_COMMENT | LITERAL );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_9 = input.LA(1);

                        s = -1;
                        if ( ((LA5_9>='\u0000' && LA5_9<='\uFFFF')) ) {s = 15;}

                        else s = 14;

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
 

}