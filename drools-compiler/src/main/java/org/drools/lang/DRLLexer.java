// $ANTLR 3.0.1 /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2008-08-28 15:09:01

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int COMMA=89;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=64;
    public static final int END=86;
    public static final int HexDigit=123;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=119;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=62;
    public static final int THEN=116;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=84;
    public static final int VK_IMPORT=59;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=114;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=126;
    public static final int VT_DATA_TYPE=38;
    public static final int VK_MATCHES=68;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=117;
    public static final int AT=91;
    public static final int LEFT_PAREN=88;
    public static final int DOUBLE_AMPER=98;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=94;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_SOUNDSLIKE=70;
    public static final int VK_SALIENCE=55;
    public static final int VT_FIELD=35;
    public static final int WS=121;
    public static final int OVER=100;
    public static final int STRING=87;
    public static final int VK_AND=76;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=65;
    public static final int VK_REVERSE=80;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=111;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=78;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=103;
    public static final int VK_ENABLED=56;
    public static final int EQUALS=93;
    public static final int VK_RESULT=81;
    public static final int UnicodeEscape=124;
    public static final int VK_PACKAGE=60;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=105;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=82;
    public static final int VK_TEMPLATE=61;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=113;
    public static final int COLON=92;
    public static final int MULTI_LINE_COMMENT=128;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=115;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=73;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=104;
    public static final int FLOAT=112;
    public static final int INIT=102;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=97;
    public static final int LESS=108;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=122;
    public static final int VK_EXISTS=77;
    public static final int INT=96;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=66;
    public static final int GREATER=106;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=83;
    public static final int FROM=99;
    public static final int NOT_EQUAL=110;
    public static final int RIGHT_CURLY=118;
    public static final int VK_ENTRY_POINT=72;
    public static final int VT_PARAM_LIST=44;
    public static final int VT_AND_INFIX=25;
    public static final int BOOL=95;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_CONTAINS=67;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=74;
    public static final int VT_RHS_CHUNK=17;
    public static final int VK_MEMBEROF=71;
    public static final int GREATER_EQUAL=107;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=85;
    public static final int VK_OR=75;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=109;
    public static final int ACCUMULATE=101;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=41;
    public static final int EOL=120;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int Tokens=129;
    public static final int OctalEscape=125;
    public static final int VK_ACTION=79;
    public static final int VK_EXCLUDES=69;
    public static final int RIGHT_PAREN=90;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=63;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=127;

    	/** The standard method called to automatically emit a token at the
    	 *  outermost lexical rule.  The token object should point into the
    	 *  char buffer start..stop.  If there is a text override in 'text',
    	 *  use that to set the token's text.  Override this method to emit
    	 *  custom Token objects.
    	 */
    	public Token emit() {
    		Token t = new DroolsToken(input, type, channel, tokenStartCharIndex, getCharIndex()-1);
    		t.setLine(tokenStartLine);
    		t.setText(text);
    		t.setCharPositionInLine(tokenStartCharPositionInLine);
    		emit(t);
    		return t;
    	}

    	/** Overrided this method to not output mesages */
    	public void emitErrorMessage(String msg) {
    	}

    public DRLLexer() {;} 
    public DRLLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[49+1];
     }
    public String getGrammarFileName() { return "/Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1655:9: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1655:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1655:17: ( ' ' | '\\t' | '\\f' | EOL )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1655:19: ' '
            	    {
            	    match(' '); if (failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1656:19: '\\t'
            	    {
            	    match('\t'); if (failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:19: '\\f'
            	    {
            	    match('\f'); if (failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1658:19: EOL
            	    {
            	    mEOL(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            if ( backtracking==0 ) {
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start EOL
    public final void mEOL() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1664:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1())) {
                    alt2=1;
                }
                else {
                    alt2=2;}
            }
            else if ( (LA2_0=='\n') ) {
                alt2=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1665:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1666:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1667:25: '\\n'
                    {
                    match('\n'); if (failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end EOL

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:2: ( ( '-' )? ( '0' .. '9' )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:4: ( '-' )? ( '0' .. '9' )+
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:10: ( '0' .. '9' )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1672:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start FLOAT
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:10: ( '0' .. '9' )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            match('.'); if (failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:26: ( '0' .. '9' )+
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
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1676:27: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOAT

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\"') ) {
                alt10=1;
            }
            else if ( (LA10_0=='\'') ) {
                alt10=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1679:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0>='\u0000' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='\uFFFE')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1680:32: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();
                    	    failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    match('\"'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0>='\u0000' && LA9_0<='&')||(LA9_0>='(' && LA9_0<='[')||(LA9_0>=']' && LA9_0<='\uFFFE')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1681:33: ~ ( '\\\\' | '\\'' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFE') ) {
                    	        input.consume();
                    	    failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    match('\''); if (failed) return ;

                    }


                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1685:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1685:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

        }
        finally {
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1689:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
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
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1687:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1687:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1689:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
                    {
                    match('\\'); if (failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)=='?'||(input.LA(1)>='A' && input.LA(1)<='B')||(input.LA(1)>='D' && input.LA(1)<='E')||input.LA(1)=='G'||input.LA(1)=='Q'||input.LA(1)=='S'||input.LA(1)=='W'||(input.LA(1)>='Z' && input.LA(1)<='^')||(input.LA(1)>='a' && input.LA(1)<='f')||(input.LA(1)>='n' && input.LA(1)<='p')||(input.LA(1)>='r' && input.LA(1)<='t')||(input.LA(1)>='w' && input.LA(1)<='x')||(input.LA(1)>='z' && input.LA(1)<='}') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1693:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1694:9: OctalEscape
                    {
                    mOctalEscape(); if (failed) return ;

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public final void mOctalEscape() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1697:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1697:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:14: ( '0' .. '3' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:25: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:36: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1699:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:14: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:25: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:14: ( '0' .. '7' )
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1706:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1706:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
            {
            match('\\'); if (failed) return ;
            match('u'); if (failed) return ;
            mHexDigit(); if (failed) return ;
            mHexDigit(); if (failed) return ;
            mHexDigit(); if (failed) return ;
            mHexDigit(); if (failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end UnicodeEscape

    // $ANTLR start BOOL
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:2: ( ( 'true' | 'false' ) )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:4: ( 'true' | 'false' )
            {
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:4: ( 'true' | 'false' )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='t') ) {
                alt13=1;
            }
            else if ( (LA13_0=='f') ) {
                alt13=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1710:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1710:12: 'false'
                    {
                    match("false"); if (failed) return ;


                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOOL

    // $ANTLR start ACCUMULATE
    public final void mACCUMULATE() throws RecognitionException {
        try {
            int _type = ACCUMULATE;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1714:2: ( 'accumulate' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1714:4: 'accumulate'
            {
            match("accumulate"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ACCUMULATE

    // $ANTLR start COLLECT
    public final void mCOLLECT() throws RecognitionException {
        try {
            int _type = COLLECT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1718:2: ( 'collect' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1718:4: 'collect'
            {
            match("collect"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLLECT

    // $ANTLR start END
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1721:5: ( 'end' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1721:7: 'end'
            {
            match("end"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end END

    // $ANTLR start FROM
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1725:2: ( 'from' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1725:4: 'from'
            {
            match("from"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FROM

    // $ANTLR start INIT
    public final void mINIT() throws RecognitionException {
        try {
            int _type = INIT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1729:2: ( 'init' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1729:4: 'init'
            {
            match("init"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INIT

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1733:2: ( 'null' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1733:4: 'null'
            {
            match("null"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start OVER
    public final void mOVER() throws RecognitionException {
        try {
            int _type = OVER;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1737:2: ( 'over' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1737:4: 'over'
            {
            match("over"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OVER

    // $ANTLR start THEN
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1741:2: ( 'then' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1741:4: 'then'
            {
            match("then"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THEN

    // $ANTLR start WHEN
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1745:2: ( 'when' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1745:4: 'when'
            {
            match("when"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHEN

    // $ANTLR start GRAVE_ACCENT
    public final void mGRAVE_ACCENT() throws RecognitionException {
        try {
            int _type = GRAVE_ACCENT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1749:2: ( '`' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1749:4: '`'
            {
            match('`'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GRAVE_ACCENT

    // $ANTLR start AT
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:4: ( '@' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:6: '@'
            {
            match('@'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AT

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1756:2: ( '=' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1756:4: '='
            {
            match('='); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start SEMICOLON
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1760:2: ( ';' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1760:4: ';'
            {
            match(';'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SEMICOLON

    // $ANTLR start DOT_STAR
    public final void mDOT_STAR() throws RecognitionException {
        try {
            int _type = DOT_STAR;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1764:2: ( '.*' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1764:4: '.*'
            {
            match(".*"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT_STAR

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:2: ( ':' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:4: ':'
            {
            match(':'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start EQUAL
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1772:2: ( '==' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1772:4: '=='
            {
            match("=="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUAL

    // $ANTLR start NOT_EQUAL
    public final void mNOT_EQUAL() throws RecognitionException {
        try {
            int _type = NOT_EQUAL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1776:2: ( '!=' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1776:4: '!='
            {
            match("!="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT_EQUAL

    // $ANTLR start GREATER
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1780:2: ( '>' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1780:4: '>'
            {
            match('>'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATER

    // $ANTLR start GREATER_EQUAL
    public final void mGREATER_EQUAL() throws RecognitionException {
        try {
            int _type = GREATER_EQUAL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1784:2: ( '>=' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1784:4: '>='
            {
            match(">="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATER_EQUAL

    // $ANTLR start LESS
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1788:2: ( '<' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1788:4: '<'
            {
            match('<'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESS

    // $ANTLR start LESS_EQUAL
    public final void mLESS_EQUAL() throws RecognitionException {
        try {
            int _type = LESS_EQUAL;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1792:2: ( '<=' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1792:4: '<='
            {
            match("<="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESS_EQUAL

    // $ANTLR start ARROW
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1796:2: ( '->' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1796:4: '->'
            {
            match("->"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ARROW

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1800:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* | '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='$'||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')||(LA16_0>='\u00C0' && LA16_0<='\u00FF')) ) {
                alt16=1;
            }
            else if ( (LA16_0=='%') ) {
                alt16=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1799:1: ID : ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* | '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%' );", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1800:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
                    {
                    if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1800:50: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                    	        input.consume();
                    	    failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1801:4: '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%'
                    {
                    match('%'); if (failed) return ;
                    if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                        input.consume();
                    failed=false;
                    }
                    else {
                        if (backtracking>0) {failed=true; return ;}
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1801:54: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00FF')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
                    	    {
                    	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                    	        input.consume();
                    	    failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recover(mse);    throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                    	    if (backtracking>0) {failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);

                    match('%'); if (failed) return ;
                    if ( backtracking==0 ) {
                      	text = getText().substring(1, getText().length() - 1);	
                    }

                    }
                    break;

            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ID

    // $ANTLR start LEFT_PAREN
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1806:9: ( '(' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1806:11: '('
            {
            match('('); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_PAREN

    // $ANTLR start RIGHT_PAREN
    public final void mRIGHT_PAREN() throws RecognitionException {
        try {
            int _type = RIGHT_PAREN;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1810:9: ( ')' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1810:11: ')'
            {
            match(')'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_PAREN

    // $ANTLR start LEFT_SQUARE
    public final void mLEFT_SQUARE() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1814:9: ( '[' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1814:11: '['
            {
            match('['); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_SQUARE

    // $ANTLR start RIGHT_SQUARE
    public final void mRIGHT_SQUARE() throws RecognitionException {
        try {
            int _type = RIGHT_SQUARE;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1818:9: ( ']' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1818:11: ']'
            {
            match(']'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_SQUARE

    // $ANTLR start LEFT_CURLY
    public final void mLEFT_CURLY() throws RecognitionException {
        try {
            int _type = LEFT_CURLY;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1822:9: ( '{' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1822:11: '{'
            {
            match('{'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_CURLY

    // $ANTLR start RIGHT_CURLY
    public final void mRIGHT_CURLY() throws RecognitionException {
        try {
            int _type = RIGHT_CURLY;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1826:9: ( '}' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1826:11: '}'
            {
            match('}'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1829:7: ( ',' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1829:9: ','
            {
            match(','); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1832:5: ( '.' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1832:7: '.'
            {
            match('.'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start DOUBLE_AMPER
    public final void mDOUBLE_AMPER() throws RecognitionException {
        try {
            int _type = DOUBLE_AMPER;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1836:2: ( '&&' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1836:4: '&&'
            {
            match("&&"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_AMPER

    // $ANTLR start DOUBLE_PIPE
    public final void mDOUBLE_PIPE() throws RecognitionException {
        try {
            int _type = DOUBLE_PIPE;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1840:2: ( '||' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1840:4: '||'
            {
            match("||"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_PIPE

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1844:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1844:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1844:8: ( options {greedy=false; } : . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='\r') ) {
                    alt17=2;
                }
                else if ( (LA17_0=='\n') ) {
                    alt17=2;
                }
                else if ( ((LA17_0>='\u0000' && LA17_0<='\t')||(LA17_0>='\u000B' && LA17_0<='\f')||(LA17_0>='\u000E' && LA17_0<='\uFFFE')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1844:35: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               channel=HIDDEN; setText("//"+getText().substring(1));
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SH_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start C_STYLE_SINGLE_LINE_COMMENT
    public final void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = C_STYLE_SINGLE_LINE_COMMENT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:9: ( options {greedy=false; } : . )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='\r') ) {
                    alt18=2;
                }
                else if ( (LA18_0=='\n') ) {
                    alt18=2;
                }
                else if ( ((LA18_0>='\u0000' && LA18_0<='\t')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='\uFFFE')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:36: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end C_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start MULTI_LINE_COMMENT
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:9: ( options {greedy=false; } : . )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0=='*') ) {
                    int LA19_1 = input.LA(2);

                    if ( (LA19_1=='/') ) {
                        alt19=2;
                    }
                    else if ( ((LA19_1>='\u0000' && LA19_1<='.')||(LA19_1>='0' && LA19_1<='\uFFFE')) ) {
                        alt19=1;
                    }


                }
                else if ( ((LA19_0>='\u0000' && LA19_0<=')')||(LA19_0>='+' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:35: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            match("*/"); if (failed) return ;

            if ( backtracking==0 ) {
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MULTI_LINE_COMMENT

    // $ANTLR start MISC
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1859:7: ( '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | '/' | '\\'' | '\\\\' | '|' | '&' )
            // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='\'')||(input.LA(1)>='*' && input.LA(1)<='+')||input.LA(1)=='-'||input.LA(1)=='/'||input.LA(1)=='?'||input.LA(1)=='\\'||(input.LA(1)>='^' && input.LA(1)<='_')||input.LA(1)=='|' ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MISC

    public void mTokens() throws RecognitionException {
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:8: ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | END | FROM | INIT | NULL | OVER | THEN | WHEN | GRAVE_ACCENT | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC )
        int alt20=42;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:10: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 2 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:13: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 3 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:17: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 4 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:23: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 5 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:30: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 6 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:35: ACCUMULATE
                {
                mACCUMULATE(); if (failed) return ;

                }
                break;
            case 7 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:46: COLLECT
                {
                mCOLLECT(); if (failed) return ;

                }
                break;
            case 8 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:54: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 9 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:58: FROM
                {
                mFROM(); if (failed) return ;

                }
                break;
            case 10 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:63: INIT
                {
                mINIT(); if (failed) return ;

                }
                break;
            case 11 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:68: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 12 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:73: OVER
                {
                mOVER(); if (failed) return ;

                }
                break;
            case 13 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:78: THEN
                {
                mTHEN(); if (failed) return ;

                }
                break;
            case 14 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:83: WHEN
                {
                mWHEN(); if (failed) return ;

                }
                break;
            case 15 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:88: GRAVE_ACCENT
                {
                mGRAVE_ACCENT(); if (failed) return ;

                }
                break;
            case 16 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:101: AT
                {
                mAT(); if (failed) return ;

                }
                break;
            case 17 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:104: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 18 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:111: SEMICOLON
                {
                mSEMICOLON(); if (failed) return ;

                }
                break;
            case 19 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:121: DOT_STAR
                {
                mDOT_STAR(); if (failed) return ;

                }
                break;
            case 20 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:130: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 21 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:136: EQUAL
                {
                mEQUAL(); if (failed) return ;

                }
                break;
            case 22 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:142: NOT_EQUAL
                {
                mNOT_EQUAL(); if (failed) return ;

                }
                break;
            case 23 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:152: GREATER
                {
                mGREATER(); if (failed) return ;

                }
                break;
            case 24 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:160: GREATER_EQUAL
                {
                mGREATER_EQUAL(); if (failed) return ;

                }
                break;
            case 25 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:174: LESS
                {
                mLESS(); if (failed) return ;

                }
                break;
            case 26 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:179: LESS_EQUAL
                {
                mLESS_EQUAL(); if (failed) return ;

                }
                break;
            case 27 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:190: ARROW
                {
                mARROW(); if (failed) return ;

                }
                break;
            case 28 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:196: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 29 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:199: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 30 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:210: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 31 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:222: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 32 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:234: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 33 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:247: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 34 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:258: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 35 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:270: COMMA
                {
                mCOMMA(); if (failed) return ;

                }
                break;
            case 36 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:276: DOT
                {
                mDOT(); if (failed) return ;

                }
                break;
            case 37 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:280: DOUBLE_AMPER
                {
                mDOUBLE_AMPER(); if (failed) return ;

                }
                break;
            case 38 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:293: DOUBLE_PIPE
                {
                mDOUBLE_PIPE(); if (failed) return ;

                }
                break;
            case 39 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:305: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 40 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:334: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 41 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:362: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 42 :
                // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:381: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:14: ( '\\r\\n' )
        // /Users/porcelli/Documents/dev/drools-trunk/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:16: '\\r\\n'
        {
        match("\r\n"); if (failed) return ;


        }
    }
    // $ANTLR end synpred1

    public final boolean synpred1() {
        backtracking++;
        int start = input.mark();
        try {
            synpred1_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }


    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA20_eotS =
        "\2\uffff\1\46\1\50\1\uffff\1\46\11\45\2\uffff\1\66\1\uffff\1\70"+
        "\1\uffff\1\46\1\73\1\75\1\uffff\1\46\7\uffff\2\46\1\uffff\1\46\5"+
        "\uffff\13\45\15\uffff\6\45\1\123\4\45\1\130\1\131\1\45\1\133\2\45"+
        "\1\uffff\1\136\1\137\1\140\1\141\2\uffff\1\130\1\uffff\2\45\4\uffff"+
        "\3\45\1\147\1\45\1\uffff\1\45\1\152\1\uffff";
    static final String DFA20_eofS =
        "\153\uffff";
    static final String DFA20_minS =
        "\1\11\1\uffff\1\60\1\56\1\uffff\1\0\1\150\1\141\1\143\1\157\2\156"+
        "\1\165\1\166\1\150\2\uffff\1\75\1\uffff\1\52\1\uffff\3\75\1\uffff"+
        "\1\44\7\uffff\1\46\1\174\1\uffff\1\52\5\uffff\1\165\1\145\1\154"+
        "\1\157\1\143\1\154\1\144\1\151\1\154\2\145\15\uffff\1\145\1\156"+
        "\1\163\1\155\1\165\1\154\1\60\1\164\1\154\1\162\1\156\2\60\1\145"+
        "\1\60\1\155\1\145\1\uffff\4\60\2\uffff\1\60\1\uffff\1\165\1\143"+
        "\4\uffff\1\154\1\164\1\141\1\60\1\164\1\uffff\1\145\1\60\1\uffff";
    static final String DFA20_maxS =
        "\1\u00ff\1\uffff\1\76\1\71\1\uffff\1\ufffe\2\162\1\143\1\157\2\156"+
        "\1\165\1\166\1\150\2\uffff\1\75\1\uffff\1\52\1\uffff\3\75\1\uffff"+
        "\1\u00ff\7\uffff\1\46\1\174\1\uffff\1\57\5\uffff\1\165\1\145\1\154"+
        "\1\157\1\143\1\154\1\144\1\151\1\154\2\145\15\uffff\1\145\1\156"+
        "\1\163\1\155\1\165\1\154\1\u00ff\1\164\1\154\1\162\1\156\2\u00ff"+
        "\1\145\1\u00ff\1\155\1\145\1\uffff\4\u00ff\2\uffff\1\u00ff\1\uffff"+
        "\1\165\1\143\4\uffff\1\154\1\164\1\141\1\u00ff\1\164\1\uffff\1\145"+
        "\1\u00ff\1\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\12\uffff\1\17\1\20\1\uffff\1\22\1\uffff"+
        "\1\24\3\uffff\1\34\1\uffff\1\35\1\36\1\37\1\40\1\41\1\42\1\43\2"+
        "\uffff\1\47\1\uffff\1\34\1\52\1\33\1\2\1\3\13\uffff\1\25\1\21\1"+
        "\23\1\44\1\26\1\30\1\27\1\32\1\31\1\45\1\46\1\50\1\51\21\uffff\1"+
        "\10\4\uffff\1\5\1\15\1\uffff\1\11\2\uffff\1\12\1\13\1\14\1\16\5"+
        "\uffff\1\7\2\uffff\1\6";
    static final String DFA20_specialS =
        "\153\uffff}>";
    static final String[] DFA20_transitionS = {
            "\2\1\1\uffff\2\1\22\uffff\1\1\1\25\1\4\1\43\1\30\1\31\1\41\1"+
            "\5\1\32\1\33\2\46\1\40\1\2\1\23\1\44\12\3\1\24\1\22\1\27\1\21"+
            "\1\26\1\46\1\20\32\45\1\34\1\46\1\35\1\46\1\30\1\17\1\10\1\45"+
            "\1\11\1\45\1\12\1\7\2\45\1\13\4\45\1\14\1\15\4\45\1\6\2\45\1"+
            "\16\3\45\1\36\1\42\1\37\102\uffff\100\45",
            "",
            "\12\3\4\uffff\1\47",
            "\1\51\1\uffff\12\3",
            "",
            "\uffff\4",
            "\1\53\11\uffff\1\52",
            "\1\54\20\uffff\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "",
            "",
            "\1\65",
            "",
            "\1\67",
            "",
            "\1\71",
            "\1\72",
            "\1\74",
            "",
            "\1\45\34\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\76",
            "\1\77",
            "",
            "\1\101\4\uffff\1\100",
            "",
            "",
            "",
            "",
            "",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\1\132",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\1\134",
            "\1\135",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "",
            "\1\142",
            "\1\143",
            "",
            "",
            "",
            "",
            "\1\144",
            "\1\145",
            "\1\146",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
            "\1\150",
            "",
            "\1\151",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45\105\uffff\100"+
            "\45",
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
            return "1:1: Tokens : ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | END | FROM | INIT | NULL | OVER | THEN | WHEN | GRAVE_ACCENT | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC );";
        }
    }
 

}