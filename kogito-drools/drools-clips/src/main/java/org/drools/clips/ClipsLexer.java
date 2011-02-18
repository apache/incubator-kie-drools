/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $ANTLR 3.1.1 /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g 2008-11-24 17:53:59

	package org.drools.clips;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class ClipsLexer extends Lexer {
    public static final int EXISTS=15;
    public static final int DEFRULE=8;
    public static final int SYMBOL_CHAR=35;
    public static final int HexDigit=32;
    public static final int FLOAT=24;
    public static final int TILDE=21;
    public static final int OR=13;
    public static final int PIPE=19;
    public static final int ASSIGN_OP=18;
    public static final int AND=12;
    public static final int T__46=46;
    public static final int FIRST_SYMBOL_CHAR=44;
    public static final int DEFTEMPLATE=27;
    public static final int EscapeSequence=31;
    public static final int INT=11;
    public static final int SYMBOL=43;
    public static final int LEFT_SQUARE=38;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=36;
    public static final int AMPERSAND=20;
    public static final int DECLARE=30;
    public static final int LEFT_CURLY=40;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=41;
    public static final int BOOL=25;
    public static final int DEFFUNCTION=7;
    public static final int WS=29;
    public static final int STRING=9;
    public static final int T__45=45;
    public static final int VAR=17;
    public static final int EQUALS=23;
    public static final int UnicodeEscape=33;
    public static final int EOF=-1;
    public static final int NULL=26;
    public static final int EOL=28;
    public static final int COLON=22;
    public static final int SALIENCE=10;
    public static final int OctalEscape=34;
    public static final int MULTI_LINE_COMMENT=42;
    public static final int TEST=16;
    public static final int NAME=5;
    public static final int RIGHT_PAREN=6;
    public static final int NOT=14;
    public static final int RIGHT_SQUARE=39;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=37;

    // delegates
    // delegators

    public ClipsLexer() {;}
    public ClipsLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ClipsLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g"; }

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:7:7: ( 'import' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:7:9: 'import'
            {
            match("import"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:8:7: ( '=>' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:8:9: '=>'
            {
            match("=>"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:802:9: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:802:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:802:17: ( ' ' | '\\t' | '\\f' | EOL )
            int alt1=4;
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
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:802:19: ' '
                    {
                    match(' '); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:803:19: '\\t'
                    {
                    match('\t'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:804:19: '\\f'
                    {
                    match('\f'); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:805:19: EOL
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
    // $ANTLR end "WS"

    // $ANTLR start "DEFTEMPLATE"
    public final void mDEFTEMPLATE() throws RecognitionException {
        try {
            int _type = DEFTEMPLATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:811:13: ( 'deftemplate' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:811:17: 'deftemplate'
            {
            match("deftemplate"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFTEMPLATE"

    // $ANTLR start "DEFRULE"
    public final void mDEFRULE() throws RecognitionException {
        try {
            int _type = DEFRULE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:814:10: ( 'defrule' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:814:12: 'defrule'
            {
            match("defrule"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFRULE"

    // $ANTLR start "DEFFUNCTION"
    public final void mDEFFUNCTION() throws RecognitionException {
        try {
            int _type = DEFFUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:815:13: ( 'deffunction' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:815:15: 'deffunction'
            {
            match("deffunction"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEFFUNCTION"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:816:7: ( 'or' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:816:9: 'or'
            {
            match("or"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:817:7: ( 'and' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:817:9: 'and'
            {
            match("and"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:818:7: ( 'not' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:818:9: 'not'
            {
            match("not"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "EXISTS"
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:819:10: ( 'exists' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:819:12: 'exists'
            {
            match("exists"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXISTS"

    // $ANTLR start "TEST"
    public final void mTEST() throws RecognitionException {
        try {
            int _type = TEST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:820:8: ( 'test' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:820:10: 'test'
            {
            match("test"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEST"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:821:7: ( 'null' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:821:9: 'null'
            {
            match("null"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "DECLARE"
    public final void mDECLARE() throws RecognitionException {
        try {
            int _type = DECLARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:823:10: ( 'declare' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:823:12: 'declare'
            {
            match("declare"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECLARE"

    // $ANTLR start "SALIENCE"
    public final void mSALIENCE() throws RecognitionException {
        try {
            int _type = SALIENCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:825:10: ( 'salience' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:825:12: 'salience'
            {
            match("salience"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SALIENCE"

    // $ANTLR start "EOL"
    public final void mEOL() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:830:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:831:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:831:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1_Clips())) {
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:831:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:832:25: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:833:25: '\\n'
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

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:2: ( ( '-' )? ( '0' .. '9' )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:4: ( '-' )? ( '0' .. '9' )+
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:5: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:10: ( '0' .. '9' )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:838:11: '0' .. '9'
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


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:5: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:10: ( '0' .. '9' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            match('.'); if (state.failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:26: ( '0' .. '9' )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:842:27: '0' .. '9'
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
    // $ANTLR end "FLOAT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:846:32: ~ ( '\\\\' | '\"' )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:847:33: ~ ( '\\\\' | '\\'' )
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:851:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:851:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:855:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:855:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); if (state.failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:856:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:857:9: OctalEscape
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:14: ( '0' .. '3' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (state.failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:25: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:36: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:862:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:863:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:863:14: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:863:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:863:25: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:863:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:864:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:864:14: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:864:15: '0' .. '7'
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:869:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:869:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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

    // $ANTLR start "BOOL"
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:873:2: ( ( 'true' | 'false' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:873:4: ( 'true' | 'false' )
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:873:4: ( 'true' | 'false' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='t') ) {
                alt13=1;
            }
            else if ( (LA13_0=='f') ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:873:5: 'true'
                    {
                    match("true"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:873:12: 'false'
                    {
                    match("false"); if (state.failed) return ;


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
    // $ANTLR end "BOOL"

    // $ANTLR start "VAR"
    public final void mVAR() throws RecognitionException {
        try {
            int _type = VAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:876:6: ( '?' ( SYMBOL_CHAR )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:876:8: '?' ( SYMBOL_CHAR )+
            {
            match('?'); if (state.failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:876:12: ( SYMBOL_CHAR )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='=' && LA14_0<='_')||(LA14_0>='a' && LA14_0<='{')||LA14_0=='}') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:876:12: SYMBOL_CHAR
            	    {
            	    mSYMBOL_CHAR(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VAR"

    // $ANTLR start "SH_STYLE_SINGLE_LINE_COMMENT"
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:880:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:880:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (state.failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:880:8: ( options {greedy=false; } : . )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0=='\r') ) {
                    alt15=2;
                }
                else if ( (LA15_0=='\n') ) {
                    alt15=2;
                }
                else if ( ((LA15_0>='\u0000' && LA15_0<='\t')||(LA15_0>='\u000B' && LA15_0<='\f')||(LA15_0>='\u000E' && LA15_0<='\uFFFF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:880:35: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            mEOL(); if (state.failed) return ;
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
    // $ANTLR end "SH_STYLE_SINGLE_LINE_COMMENT"

    // $ANTLR start "C_STYLE_SINGLE_LINE_COMMENT"
    public final void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = C_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:886:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:886:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (state.failed) return ;

            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:886:9: ( options {greedy=false; } : . )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='\r') ) {
                    alt16=2;
                }
                else if ( (LA16_0=='\n') ) {
                    alt16=2;
                }
                else if ( ((LA16_0>='\u0000' && LA16_0<='\t')||(LA16_0>='\u000B' && LA16_0<='\f')||(LA16_0>='\u000E' && LA16_0<='\uFFFF')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:886:36: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            mEOL(); if (state.failed) return ;
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

    // $ANTLR start "LEFT_PAREN"
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:892:2: ( '(' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:892:4: '('
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:896:2: ( ')' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:896:4: ')'
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:900:2: ( '[' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:900:4: '['
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:904:2: ( ']' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:904:4: ']'
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:908:2: ( '{' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:908:4: '{'
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
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:912:2: ( '}' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:912:4: '}'
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

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:915:7: ( '~' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:915:9: '~'
            {
            match('~'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "AMPERSAND"
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:919:2: ( '&' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:919:4: '&'
            {
            match('&'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPERSAND"

    // $ANTLR start "PIPE"
    public final void mPIPE() throws RecognitionException {
        try {
            int _type = PIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:923:2: ( '|' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:923:4: '|'
            {
            match('|'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PIPE"

    // $ANTLR start "ASSIGN_OP"
    public final void mASSIGN_OP() throws RecognitionException {
        try {
            int _type = ASSIGN_OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:927:2: ( '<-' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:927:4: '<-'
            {
            match("<-"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN_OP"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:930:7: ( ':' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:930:9: ':'
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

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:932:8: ( '=' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:932:10: '='
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

    // $ANTLR start "MULTI_LINE_COMMENT"
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:935:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:935:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:935:9: ( options {greedy=false; } : . )*
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:935:35: .
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

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:939:6: ( SYMBOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:939:8: SYMBOL
            {
            mSYMBOL(); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "SYMBOL"
    public final void mSYMBOL() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:942:8: ( FIRST_SYMBOL_CHAR ( SYMBOL_CHAR )* )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:942:10: FIRST_SYMBOL_CHAR ( SYMBOL_CHAR )*
            {
            mFIRST_SYMBOL_CHAR(); if (state.failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:942:28: ( SYMBOL_CHAR )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='!'||(LA18_0>='#' && LA18_0<='%')||(LA18_0>='*' && LA18_0<=':')||(LA18_0>='=' && LA18_0<='_')||(LA18_0>='a' && LA18_0<='{')||LA18_0=='}') ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:942:28: SYMBOL_CHAR
            	    {
            	    mSYMBOL_CHAR(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "SYMBOL"

    // $ANTLR start "FIRST_SYMBOL_CHAR"
    public final void mFIRST_SYMBOL_CHAR() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:947:19: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | '<' | ',' | '.' | '[' | ']' | '{' | '}' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:947:21: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | '<' | ',' | '.' | '[' | ']' | '{' | '}' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='<' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
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
    // $ANTLR end "FIRST_SYMBOL_CHAR"

    // $ANTLR start "SYMBOL_CHAR"
    public final void mSYMBOL_CHAR() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:952:13: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' | '?' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:952:15: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' | '?' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='=' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
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
    // $ANTLR end "SYMBOL_CHAR"

    public void mTokens() throws RecognitionException {
        // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:8: ( T__45 | T__46 | WS | DEFTEMPLATE | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt19=35;
        alt19 = dfa19.predict(input);
        switch (alt19) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:10: T__45
                {
                mT__45(); if (state.failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:16: T__46
                {
                mT__46(); if (state.failed) return ;

                }
                break;
            case 3 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:22: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 4 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:25: DEFTEMPLATE
                {
                mDEFTEMPLATE(); if (state.failed) return ;

                }
                break;
            case 5 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:37: DEFRULE
                {
                mDEFRULE(); if (state.failed) return ;

                }
                break;
            case 6 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:45: DEFFUNCTION
                {
                mDEFFUNCTION(); if (state.failed) return ;

                }
                break;
            case 7 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:57: OR
                {
                mOR(); if (state.failed) return ;

                }
                break;
            case 8 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:60: AND
                {
                mAND(); if (state.failed) return ;

                }
                break;
            case 9 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:64: NOT
                {
                mNOT(); if (state.failed) return ;

                }
                break;
            case 10 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:68: EXISTS
                {
                mEXISTS(); if (state.failed) return ;

                }
                break;
            case 11 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:75: TEST
                {
                mTEST(); if (state.failed) return ;

                }
                break;
            case 12 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:80: NULL
                {
                mNULL(); if (state.failed) return ;

                }
                break;
            case 13 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:85: DECLARE
                {
                mDECLARE(); if (state.failed) return ;

                }
                break;
            case 14 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:93: SALIENCE
                {
                mSALIENCE(); if (state.failed) return ;

                }
                break;
            case 15 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:102: INT
                {
                mINT(); if (state.failed) return ;

                }
                break;
            case 16 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:106: FLOAT
                {
                mFLOAT(); if (state.failed) return ;

                }
                break;
            case 17 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:112: STRING
                {
                mSTRING(); if (state.failed) return ;

                }
                break;
            case 18 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:119: BOOL
                {
                mBOOL(); if (state.failed) return ;

                }
                break;
            case 19 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:124: VAR
                {
                mVAR(); if (state.failed) return ;

                }
                break;
            case 20 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:128: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 21 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:157: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 22 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:185: LEFT_PAREN
                {
                mLEFT_PAREN(); if (state.failed) return ;

                }
                break;
            case 23 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:196: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (state.failed) return ;

                }
                break;
            case 24 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:208: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 25 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:220: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 26 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:233: LEFT_CURLY
                {
                mLEFT_CURLY(); if (state.failed) return ;

                }
                break;
            case 27 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:244: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (state.failed) return ;

                }
                break;
            case 28 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:256: TILDE
                {
                mTILDE(); if (state.failed) return ;

                }
                break;
            case 29 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:262: AMPERSAND
                {
                mAMPERSAND(); if (state.failed) return ;

                }
                break;
            case 30 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:272: PIPE
                {
                mPIPE(); if (state.failed) return ;

                }
                break;
            case 31 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:277: ASSIGN_OP
                {
                mASSIGN_OP(); if (state.failed) return ;

                }
                break;
            case 32 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:287: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 33 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:293: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 34 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:300: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 35 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:1:319: NAME
                {
                mNAME(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_Clips
    public final void synpred1_Clips_fragment() throws RecognitionException {
        // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:831:14: ( '\\r\\n' )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-clips/src/main/resources/org/drools/clips/Clips.g:831:16: '\\r\\n'
        {
        match("\r\n"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_Clips

    public final boolean synpred1_Clips() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_Clips_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA19_eotS =
        "\1\uffff\1\35\1\40\1\uffff\10\35\1\54\1\uffff\1\35\1\uffff\2\35"+
        "\2\uffff\1\62\1\63\1\64\1\65\3\uffff\1\35\1\67\1\uffff\1\35\1\71"+
        "\1\uffff\1\35\1\74\7\35\1\54\1\35\1\uffff\1\35\1\uffff\3\35\4\uffff"+
        "\1\113\1\uffff\1\35\1\uffff\2\35\1\uffff\1\121\1\122\5\35\1\130"+
        "\2\35\1\uffff\1\35\1\uffff\1\35\1\uffff\5\35\2\uffff\1\140\1\35"+
        "\1\142\1\143\1\35\1\uffff\1\35\1\111\5\35\1\uffff\1\35\2\uffff\1"+
        "\35\1\143\1\155\4\35\1\162\1\35\1\uffff\1\35\1\165\1\35\1\167\1"+
        "\uffff\2\35\1\uffff\1\35\1\uffff\1\173\2\35\1\uffff\2\35\1\u0080"+
        "\1\u0081\2\uffff";
    static final String DFA19_eofS =
        "\u0082\uffff";
    static final String DFA19_minS =
        "\1\11\1\155\1\41\1\uffff\1\145\1\162\1\156\1\157\1\170\1\145\1\141"+
        "\1\60\1\41\1\uffff\1\141\1\uffff\1\0\1\52\2\uffff\4\41\3\uffff\1"+
        "\55\1\41\1\uffff\1\160\1\41\1\uffff\1\143\1\41\1\144\1\164\1\154"+
        "\1\151\1\163\1\165\1\154\1\41\1\60\1\uffff\1\154\1\uffff\3\0\4\uffff"+
        "\1\41\1\uffff\1\157\1\uffff\1\146\1\154\1\uffff\2\41\1\154\1\163"+
        "\1\164\1\145\1\151\1\41\1\163\1\0\1\uffff\1\0\1\uffff\1\0\1\uffff"+
        "\1\162\1\145\2\165\1\141\2\uffff\1\41\1\164\2\41\1\145\1\uffff\1"+
        "\145\1\41\1\164\1\155\1\154\1\156\1\162\1\uffff\1\163\2\uffff\1"+
        "\156\2\41\1\160\1\145\1\143\1\145\1\41\1\143\1\uffff\1\154\1\41"+
        "\1\164\1\41\1\uffff\1\145\1\141\1\uffff\1\151\1\uffff\1\41\1\164"+
        "\1\157\1\uffff\1\145\1\156\2\41\2\uffff";
    static final String DFA19_maxS =
        "\1\176\1\155\1\175\1\uffff\1\145\1\162\1\156\1\165\1\170\1\162\1"+
        "\141\1\71\1\175\1\uffff\1\141\1\uffff\1\uffff\1\57\2\uffff\4\175"+
        "\3\uffff\1\55\1\175\1\uffff\1\160\1\175\1\uffff\1\146\1\175\1\144"+
        "\1\164\1\154\1\151\1\163\1\165\1\154\1\175\1\71\1\uffff\1\154\1"+
        "\uffff\3\uffff\4\uffff\1\175\1\uffff\1\157\1\uffff\1\164\1\154\1"+
        "\uffff\2\175\1\154\1\163\1\164\1\145\1\151\1\175\1\163\1\uffff\1"+
        "\uffff\1\uffff\1\uffff\1\uffff\1\uffff\1\162\1\145\2\165\1\141\2"+
        "\uffff\1\175\1\164\2\175\1\145\1\uffff\1\145\1\175\1\164\1\155\1"+
        "\154\1\156\1\162\1\uffff\1\163\2\uffff\1\156\2\175\1\160\1\145\1"+
        "\143\1\145\1\175\1\143\1\uffff\1\154\1\175\1\164\1\175\1\uffff\1"+
        "\145\1\141\1\uffff\1\151\1\uffff\1\175\1\164\1\157\1\uffff\1\145"+
        "\1\156\2\175\2\uffff";
    static final String DFA19_acceptS =
        "\3\uffff\1\3\11\uffff\1\21\1\uffff\1\23\2\uffff\1\26\1\27\4\uffff"+
        "\1\34\1\35\1\36\2\uffff\1\43\2\uffff\1\41\13\uffff\1\17\1\uffff"+
        "\1\24\3\uffff\1\30\1\31\1\32\1\33\1\uffff\1\40\1\uffff\1\2\2\uffff"+
        "\1\7\12\uffff\1\25\1\uffff\1\42\1\uffff\1\37\5\uffff\1\10\1\11\5"+
        "\uffff\1\20\7\uffff\1\14\1\uffff\1\13\1\22\11\uffff\1\1\4\uffff"+
        "\1\12\2\uffff\1\5\1\uffff\1\15\3\uffff\1\16\4\uffff\1\4\1\6";
    static final String DFA19_specialS =
        "\20\uffff\1\5\36\uffff\1\0\1\4\1\1\24\uffff\1\2\1\uffff\1\3\1\uffff"+
        "\1\6\67\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\3\1\uffff\2\3\22\uffff\1\3\1\35\1\15\1\20\2\35\1\31\1\15"+
            "\1\22\1\23\3\35\1\13\1\35\1\21\12\14\1\34\1\uffff\1\33\1\2\1"+
            "\35\1\17\33\35\1\24\1\35\1\25\2\35\1\uffff\1\6\2\35\1\4\1\10"+
            "\1\16\2\35\1\1\4\35\1\7\1\5\3\35\1\12\1\11\6\35\1\26\1\32\1"+
            "\27\1\30",
            "\1\36",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\1\35\1\37\41\35\1"+
            "\uffff\33\35\1\uffff\1\35",
            "",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44\5\uffff\1\45",
            "\1\46",
            "\1\47\14\uffff\1\50",
            "\1\51",
            "\12\52",
            "\1\35\1\uffff\3\35\4\uffff\4\35\1\53\1\35\12\52\1\35\2\uffff"+
            "\43\35\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\55",
            "",
            "\41\56\1\57\1\56\3\57\4\56\21\57\2\56\43\57\1\56\33\57\1\56"+
            "\1\57\uff82\56",
            "\1\61\4\uffff\1\60",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "",
            "",
            "\1\66",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\70",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\73\2\uffff\1\72",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\35\1\uffff\3\35\4\uffff\4\35\1\53\1\35\12\52\1\35\2\uffff"+
            "\43\35\1\uffff\33\35\1\uffff\1\35",
            "\12\104",
            "",
            "\1\105",
            "",
            "\41\56\1\57\1\56\3\57\4\56\21\57\2\56\43\57\1\56\33\57\1\56"+
            "\1\57\uff82\56",
            "\41\107\1\106\1\107\3\106\4\107\21\106\2\107\43\106\1\107\33"+
            "\106\1\107\1\106\uff82\107",
            "\41\111\1\112\1\111\3\112\4\111\1\110\20\112\2\111\43\112\1"+
            "\111\33\112\1\111\1\112\uff82\111",
            "",
            "",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\114",
            "",
            "\1\117\13\uffff\1\116\1\uffff\1\115",
            "\1\120",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\35\1\uffff\3\35\4\uffff\6\35\12\104\1\35\2\uffff\43\35\1"+
            "\uffff\33\35\1\uffff\1\35",
            "\1\131",
            "\41\107\1\106\1\107\3\106\4\107\21\106\2\107\43\106\1\107\33"+
            "\106\1\107\1\106\uff82\107",
            "",
            "\41\111\1\112\1\111\3\112\4\111\1\110\4\112\1\132\13\112\2"+
            "\111\43\112\1\111\33\112\1\111\1\112\uff82\111",
            "",
            "\41\111\1\112\1\111\3\112\4\111\1\110\20\112\2\111\43\112\1"+
            "\111\33\112\1\111\1\112\uff82\111",
            "",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\141",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\144",
            "",
            "\1\145",
            "\1\112\1\uffff\3\112\4\uffff\1\110\20\112\2\uffff\43\112\1"+
            "\uffff\33\112\1\uffff\1\112",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "",
            "\1\153",
            "",
            "",
            "\1\154",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\163",
            "",
            "\1\164",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\166",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\170",
            "\1\171",
            "",
            "\1\172",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\174",
            "\1\175",
            "",
            "\1\176",
            "\1\177",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__45 | T__46 | WS | DEFTEMPLATE | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_47 = input.LA(1);

                        s = -1;
                        if ( ((LA19_47>='\u0000' && LA19_47<=' ')||LA19_47=='\"'||(LA19_47>='&' && LA19_47<=')')||(LA19_47>=';' && LA19_47<='<')||LA19_47=='`'||LA19_47=='|'||(LA19_47>='~' && LA19_47<='\uFFFF')) ) {s = 46;}

                        else if ( (LA19_47=='!'||(LA19_47>='#' && LA19_47<='%')||(LA19_47>='*' && LA19_47<=':')||(LA19_47>='=' && LA19_47<='_')||(LA19_47>='a' && LA19_47<='{')||LA19_47=='}') ) {s = 47;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA19_49 = input.LA(1);

                        s = -1;
                        if ( (LA19_49=='*') ) {s = 72;}

                        else if ( ((LA19_49>='\u0000' && LA19_49<=' ')||LA19_49=='\"'||(LA19_49>='&' && LA19_49<=')')||(LA19_49>=';' && LA19_49<='<')||LA19_49=='`'||LA19_49=='|'||(LA19_49>='~' && LA19_49<='\uFFFF')) ) {s = 73;}

                        else if ( (LA19_49=='!'||(LA19_49>='#' && LA19_49<='%')||(LA19_49>='+' && LA19_49<=':')||(LA19_49>='=' && LA19_49<='_')||(LA19_49>='a' && LA19_49<='{')||LA19_49=='}') ) {s = 74;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA19_70 = input.LA(1);

                        s = -1;
                        if ( (LA19_70=='!'||(LA19_70>='#' && LA19_70<='%')||(LA19_70>='*' && LA19_70<=':')||(LA19_70>='=' && LA19_70<='_')||(LA19_70>='a' && LA19_70<='{')||LA19_70=='}') ) {s = 70;}

                        else if ( ((LA19_70>='\u0000' && LA19_70<=' ')||LA19_70=='\"'||(LA19_70>='&' && LA19_70<=')')||(LA19_70>=';' && LA19_70<='<')||LA19_70=='`'||LA19_70=='|'||(LA19_70>='~' && LA19_70<='\uFFFF')) ) {s = 71;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA19_72 = input.LA(1);

                        s = -1;
                        if ( (LA19_72=='/') ) {s = 90;}

                        else if ( (LA19_72=='*') ) {s = 72;}

                        else if ( (LA19_72=='!'||(LA19_72>='#' && LA19_72<='%')||(LA19_72>='+' && LA19_72<='.')||(LA19_72>='0' && LA19_72<=':')||(LA19_72>='=' && LA19_72<='_')||(LA19_72>='a' && LA19_72<='{')||LA19_72=='}') ) {s = 74;}

                        else if ( ((LA19_72>='\u0000' && LA19_72<=' ')||LA19_72=='\"'||(LA19_72>='&' && LA19_72<=')')||(LA19_72>=';' && LA19_72<='<')||LA19_72=='`'||LA19_72=='|'||(LA19_72>='~' && LA19_72<='\uFFFF')) ) {s = 73;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA19_48 = input.LA(1);

                        s = -1;
                        if ( (LA19_48=='!'||(LA19_48>='#' && LA19_48<='%')||(LA19_48>='*' && LA19_48<=':')||(LA19_48>='=' && LA19_48<='_')||(LA19_48>='a' && LA19_48<='{')||LA19_48=='}') ) {s = 70;}

                        else if ( ((LA19_48>='\u0000' && LA19_48<=' ')||LA19_48=='\"'||(LA19_48>='&' && LA19_48<=')')||(LA19_48>=';' && LA19_48<='<')||LA19_48=='`'||LA19_48=='|'||(LA19_48>='~' && LA19_48<='\uFFFF')) ) {s = 71;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA19_16 = input.LA(1);

                        s = -1;
                        if ( ((LA19_16>='\u0000' && LA19_16<=' ')||LA19_16=='\"'||(LA19_16>='&' && LA19_16<=')')||(LA19_16>=';' && LA19_16<='<')||LA19_16=='`'||LA19_16=='|'||(LA19_16>='~' && LA19_16<='\uFFFF')) ) {s = 46;}

                        else if ( (LA19_16=='!'||(LA19_16>='#' && LA19_16<='%')||(LA19_16>='*' && LA19_16<=':')||(LA19_16>='=' && LA19_16<='_')||(LA19_16>='a' && LA19_16<='{')||LA19_16=='}') ) {s = 47;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA19_74 = input.LA(1);

                        s = -1;
                        if ( (LA19_74=='*') ) {s = 72;}

                        else if ( (LA19_74=='!'||(LA19_74>='#' && LA19_74<='%')||(LA19_74>='+' && LA19_74<=':')||(LA19_74>='=' && LA19_74<='_')||(LA19_74>='a' && LA19_74<='{')||LA19_74=='}') ) {s = 74;}

                        else if ( ((LA19_74>='\u0000' && LA19_74<=' ')||LA19_74=='\"'||(LA19_74>='&' && LA19_74<=')')||(LA19_74>=';' && LA19_74<='<')||LA19_74=='`'||LA19_74=='|'||(LA19_74>='~' && LA19_74<='\uFFFF')) ) {s = 73;}

                        else s = 29;

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}
