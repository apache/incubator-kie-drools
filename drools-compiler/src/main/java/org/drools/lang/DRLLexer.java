// $ANTLR 3.0.1 src/main/resources/org/drools/lang/DRL.g 2008-11-13 19:22:17

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int COMMA=90;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=65;
    public static final int END=87;
    public static final int HexDigit=124;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=120;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=63;
    public static final int THEN=117;
    public static final int VK_AUTO_FOCUS=49;
    public static final int TILDE=112;
    public static final int DOT=85;
    public static final int VK_IMPORT=60;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=115;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=128;
    public static final int VT_DATA_TYPE=38;
    public static final int VK_MATCHES=69;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=118;
    public static final int AT=92;
    public static final int LEFT_PAREN=89;
    public static final int DOUBLE_AMPER=99;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=95;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_SOUNDSLIKE=71;
    public static final int VK_SALIENCE=55;
    public static final int VT_FIELD=35;
    public static final int WS=122;
    public static final int OVER=101;
    public static final int STRING=88;
    public static final int VK_AND=77;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VK_GLOBAL=66;
    public static final int VK_REVERSE=81;
    public static final int VT_BEHAVIOR=21;
    public static final int GRAVE_ACCENT=127;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=79;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=104;
    public static final int VK_ENABLED=56;
    public static final int EQUALS=94;
    public static final int VK_RESULT=82;
    public static final int UnicodeEscape=125;
    public static final int VK_PACKAGE=61;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=106;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=83;
    public static final int VK_TEMPLATE=62;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=114;
    public static final int COLON=93;
    public static final int MULTI_LINE_COMMENT=130;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=116;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=74;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=105;
    public static final int FLOAT=113;
    public static final int INIT=103;
    public static final int VK_EXTEND=59;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=98;
    public static final int LESS=109;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=123;
    public static final int VK_EXISTS=78;
    public static final int INT=97;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=67;
    public static final int GREATER=107;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=84;
    public static final int FROM=100;
    public static final int NOT_EQUAL=111;
    public static final int RIGHT_CURLY=119;
    public static final int VK_ENTRY_POINT=73;
    public static final int VT_PARAM_LIST=44;
    public static final int VT_AND_INFIX=25;
    public static final int BOOL=96;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_CONTAINS=68;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=75;
    public static final int VT_RHS_CHUNK=17;
    public static final int VK_MEMBEROF=72;
    public static final int GREATER_EQUAL=108;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=86;
    public static final int VK_OR=76;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=110;
    public static final int ACCUMULATE=102;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=41;
    public static final int EOL=121;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int Tokens=131;
    public static final int OctalEscape=126;
    public static final int VK_ACTION=80;
    public static final int VK_EXCLUDES=70;
    public static final int RIGHT_PAREN=91;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=64;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=129;
    
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
        ruleMemo = new HashMap[50+1];
     }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRL.g"; }

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // src/main/resources/org/drools/lang/DRL.g:1693:9: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // src/main/resources/org/drools/lang/DRL.g:1693:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1693:17: ( ' ' | '\\t' | '\\f' | EOL )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1693:19: ' '
            	    {
            	    match(' '); if (failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL.g:1694:19: '\\t'
            	    {
            	    match('\t'); if (failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/DRL.g:1695:19: '\\f'
            	    {
            	    match('\f'); if (failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/main/resources/org/drools/lang/DRL.g:1696:19: EOL
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
            // src/main/resources/org/drools/lang/DRL.g:1702:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // src/main/resources/org/drools/lang/DRL.g:1703:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // src/main/resources/org/drools/lang/DRL.g:1703:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("1703:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1703:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1704:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1705:25: '\\n'
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
            // src/main/resources/org/drools/lang/DRL.g:1710:2: ( ( '-' )? ( '0' .. '9' )+ )
            // src/main/resources/org/drools/lang/DRL.g:1710:4: ( '-' )? ( '0' .. '9' )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1710:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1710:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1710:10: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1710:11: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1714:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // src/main/resources/org/drools/lang/DRL.g:1714:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1714:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1714:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1714:10: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1714:11: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1714:26: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1714:27: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1718:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("1717:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1718:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // src/main/resources/org/drools/lang/DRL.g:1718:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // src/main/resources/org/drools/lang/DRL.g:1718:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1718:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // src/main/resources/org/drools/lang/DRL.g:1718:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRL.g:1718:32: ~ ( '\\\\' | '\"' )
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
                    // src/main/resources/org/drools/lang/DRL.g:1719:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // src/main/resources/org/drools/lang/DRL.g:1719:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // src/main/resources/org/drools/lang/DRL.g:1719:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1719:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // src/main/resources/org/drools/lang/DRL.g:1719:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRL.g:1719:33: ~ ( '\\\\' | '\\'' )
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
            // src/main/resources/org/drools/lang/DRL.g:1723:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // src/main/resources/org/drools/lang/DRL.g:1723:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // src/main/resources/org/drools/lang/DRL.g:1727:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("1725:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1725:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1727:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
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
                    // src/main/resources/org/drools/lang/DRL.g:1731:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1732:9: OctalEscape
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
            // src/main/resources/org/drools/lang/DRL.g:1737:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("1735:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1735:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1737:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1737:14: ( '0' .. '3' )
                    // src/main/resources/org/drools/lang/DRL.g:1737:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1737:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1737:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1737:36: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1737:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1738:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1738:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1738:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1738:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1738:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1739:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1739:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1739:15: '0' .. '7'
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
            // src/main/resources/org/drools/lang/DRL.g:1744:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // src/main/resources/org/drools/lang/DRL.g:1744:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // src/main/resources/org/drools/lang/DRL.g:1748:2: ( ( 'true' | 'false' ) )
            // src/main/resources/org/drools/lang/DRL.g:1748:4: ( 'true' | 'false' )
            {
            // src/main/resources/org/drools/lang/DRL.g:1748:4: ( 'true' | 'false' )
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
                    new NoViableAltException("1748:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1748:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1748:12: 'false'
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
            // src/main/resources/org/drools/lang/DRL.g:1752:2: ( 'accumulate' )
            // src/main/resources/org/drools/lang/DRL.g:1752:4: 'accumulate'
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
            // src/main/resources/org/drools/lang/DRL.g:1756:2: ( 'collect' )
            // src/main/resources/org/drools/lang/DRL.g:1756:4: 'collect'
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
            // src/main/resources/org/drools/lang/DRL.g:1759:5: ( 'end' )
            // src/main/resources/org/drools/lang/DRL.g:1759:7: 'end'
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
            // src/main/resources/org/drools/lang/DRL.g:1763:2: ( 'from' )
            // src/main/resources/org/drools/lang/DRL.g:1763:4: 'from'
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
            // src/main/resources/org/drools/lang/DRL.g:1767:2: ( 'init' )
            // src/main/resources/org/drools/lang/DRL.g:1767:4: 'init'
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
            // src/main/resources/org/drools/lang/DRL.g:1771:2: ( 'null' )
            // src/main/resources/org/drools/lang/DRL.g:1771:4: 'null'
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
            // src/main/resources/org/drools/lang/DRL.g:1775:2: ( 'over' )
            // src/main/resources/org/drools/lang/DRL.g:1775:4: 'over'
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
            // src/main/resources/org/drools/lang/DRL.g:1779:2: ( 'then' )
            // src/main/resources/org/drools/lang/DRL.g:1779:4: 'then'
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
            // src/main/resources/org/drools/lang/DRL.g:1783:2: ( 'when' )
            // src/main/resources/org/drools/lang/DRL.g:1783:4: 'when'
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
            // src/main/resources/org/drools/lang/DRL.g:1787:2: ( '`' )
            // src/main/resources/org/drools/lang/DRL.g:1787:4: '`'
            {
            match('`'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GRAVE_ACCENT

    // $ANTLR start TILDE
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            // src/main/resources/org/drools/lang/DRL.g:1791:2: ( '~' )
            // src/main/resources/org/drools/lang/DRL.g:1791:4: '~'
            {
            match('~'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TILDE

    // $ANTLR start AT
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            // src/main/resources/org/drools/lang/DRL.g:1794:4: ( '@' )
            // src/main/resources/org/drools/lang/DRL.g:1794:6: '@'
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
            // src/main/resources/org/drools/lang/DRL.g:1798:2: ( '=' )
            // src/main/resources/org/drools/lang/DRL.g:1798:4: '='
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
            // src/main/resources/org/drools/lang/DRL.g:1802:2: ( ';' )
            // src/main/resources/org/drools/lang/DRL.g:1802:4: ';'
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
            // src/main/resources/org/drools/lang/DRL.g:1806:2: ( '.*' )
            // src/main/resources/org/drools/lang/DRL.g:1806:4: '.*'
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
            // src/main/resources/org/drools/lang/DRL.g:1810:2: ( ':' )
            // src/main/resources/org/drools/lang/DRL.g:1810:4: ':'
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
            // src/main/resources/org/drools/lang/DRL.g:1814:2: ( '==' )
            // src/main/resources/org/drools/lang/DRL.g:1814:4: '=='
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
            // src/main/resources/org/drools/lang/DRL.g:1818:2: ( '!=' )
            // src/main/resources/org/drools/lang/DRL.g:1818:4: '!='
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
            // src/main/resources/org/drools/lang/DRL.g:1822:2: ( '>' )
            // src/main/resources/org/drools/lang/DRL.g:1822:4: '>'
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
            // src/main/resources/org/drools/lang/DRL.g:1826:2: ( '>=' )
            // src/main/resources/org/drools/lang/DRL.g:1826:4: '>='
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
            // src/main/resources/org/drools/lang/DRL.g:1830:2: ( '<' )
            // src/main/resources/org/drools/lang/DRL.g:1830:4: '<'
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
            // src/main/resources/org/drools/lang/DRL.g:1834:2: ( '<=' )
            // src/main/resources/org/drools/lang/DRL.g:1834:4: '<='
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
            // src/main/resources/org/drools/lang/DRL.g:1838:2: ( '->' )
            // src/main/resources/org/drools/lang/DRL.g:1838:4: '->'
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
            // src/main/resources/org/drools/lang/DRL.g:1842:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* | '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%' )
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
                    new NoViableAltException("1841:1: ID : ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* | '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%' );", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1842:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
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

                    // src/main/resources/org/drools/lang/DRL.g:1842:50: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRL.g:
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
                    // src/main/resources/org/drools/lang/DRL.g:1843:4: '%' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '%'
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

                    // src/main/resources/org/drools/lang/DRL.g:1843:54: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+
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
                    	    // src/main/resources/org/drools/lang/DRL.g:
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
            // src/main/resources/org/drools/lang/DRL.g:1848:9: ( '(' )
            // src/main/resources/org/drools/lang/DRL.g:1848:11: '('
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
            // src/main/resources/org/drools/lang/DRL.g:1852:9: ( ')' )
            // src/main/resources/org/drools/lang/DRL.g:1852:11: ')'
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
            // src/main/resources/org/drools/lang/DRL.g:1856:9: ( '[' )
            // src/main/resources/org/drools/lang/DRL.g:1856:11: '['
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
            // src/main/resources/org/drools/lang/DRL.g:1860:9: ( ']' )
            // src/main/resources/org/drools/lang/DRL.g:1860:11: ']'
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
            // src/main/resources/org/drools/lang/DRL.g:1864:9: ( '{' )
            // src/main/resources/org/drools/lang/DRL.g:1864:11: '{'
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
            // src/main/resources/org/drools/lang/DRL.g:1868:9: ( '}' )
            // src/main/resources/org/drools/lang/DRL.g:1868:11: '}'
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
            // src/main/resources/org/drools/lang/DRL.g:1871:7: ( ',' )
            // src/main/resources/org/drools/lang/DRL.g:1871:9: ','
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
            // src/main/resources/org/drools/lang/DRL.g:1874:5: ( '.' )
            // src/main/resources/org/drools/lang/DRL.g:1874:7: '.'
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
            // src/main/resources/org/drools/lang/DRL.g:1878:2: ( '&&' )
            // src/main/resources/org/drools/lang/DRL.g:1878:4: '&&'
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
            // src/main/resources/org/drools/lang/DRL.g:1882:2: ( '||' )
            // src/main/resources/org/drools/lang/DRL.g:1882:4: '||'
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
            // src/main/resources/org/drools/lang/DRL.g:1886:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/DRL.g:1886:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // src/main/resources/org/drools/lang/DRL.g:1886:8: ( options {greedy=false; } : . )*
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
            	    // src/main/resources/org/drools/lang/DRL.g:1886:35: .
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
            // src/main/resources/org/drools/lang/DRL.g:1892:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/DRL.g:1892:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // src/main/resources/org/drools/lang/DRL.g:1892:9: ( options {greedy=false; } : . )*
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
            	    // src/main/resources/org/drools/lang/DRL.g:1892:36: .
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
            // src/main/resources/org/drools/lang/DRL.g:1897:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // src/main/resources/org/drools/lang/DRL.g:1897:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // src/main/resources/org/drools/lang/DRL.g:1897:9: ( options {greedy=false; } : . )*
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
            	    // src/main/resources/org/drools/lang/DRL.g:1897:35: .
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
            // src/main/resources/org/drools/lang/DRL.g:1901:7: ( '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | '/' | '\\'' | '\\\\' | '|' | '&' )
            // src/main/resources/org/drools/lang/DRL.g:
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
        // src/main/resources/org/drools/lang/DRL.g:1:8: ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | END | FROM | INIT | NULL | OVER | THEN | WHEN | GRAVE_ACCENT | TILDE | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC )
        int alt20=43;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL.g:1:10: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:1:13: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 3 :
                // src/main/resources/org/drools/lang/DRL.g:1:17: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 4 :
                // src/main/resources/org/drools/lang/DRL.g:1:23: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 5 :
                // src/main/resources/org/drools/lang/DRL.g:1:30: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 6 :
                // src/main/resources/org/drools/lang/DRL.g:1:35: ACCUMULATE
                {
                mACCUMULATE(); if (failed) return ;

                }
                break;
            case 7 :
                // src/main/resources/org/drools/lang/DRL.g:1:46: COLLECT
                {
                mCOLLECT(); if (failed) return ;

                }
                break;
            case 8 :
                // src/main/resources/org/drools/lang/DRL.g:1:54: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 9 :
                // src/main/resources/org/drools/lang/DRL.g:1:58: FROM
                {
                mFROM(); if (failed) return ;

                }
                break;
            case 10 :
                // src/main/resources/org/drools/lang/DRL.g:1:63: INIT
                {
                mINIT(); if (failed) return ;

                }
                break;
            case 11 :
                // src/main/resources/org/drools/lang/DRL.g:1:68: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 12 :
                // src/main/resources/org/drools/lang/DRL.g:1:73: OVER
                {
                mOVER(); if (failed) return ;

                }
                break;
            case 13 :
                // src/main/resources/org/drools/lang/DRL.g:1:78: THEN
                {
                mTHEN(); if (failed) return ;

                }
                break;
            case 14 :
                // src/main/resources/org/drools/lang/DRL.g:1:83: WHEN
                {
                mWHEN(); if (failed) return ;

                }
                break;
            case 15 :
                // src/main/resources/org/drools/lang/DRL.g:1:88: GRAVE_ACCENT
                {
                mGRAVE_ACCENT(); if (failed) return ;

                }
                break;
            case 16 :
                // src/main/resources/org/drools/lang/DRL.g:1:101: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 17 :
                // src/main/resources/org/drools/lang/DRL.g:1:107: AT
                {
                mAT(); if (failed) return ;

                }
                break;
            case 18 :
                // src/main/resources/org/drools/lang/DRL.g:1:110: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 19 :
                // src/main/resources/org/drools/lang/DRL.g:1:117: SEMICOLON
                {
                mSEMICOLON(); if (failed) return ;

                }
                break;
            case 20 :
                // src/main/resources/org/drools/lang/DRL.g:1:127: DOT_STAR
                {
                mDOT_STAR(); if (failed) return ;

                }
                break;
            case 21 :
                // src/main/resources/org/drools/lang/DRL.g:1:136: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 22 :
                // src/main/resources/org/drools/lang/DRL.g:1:142: EQUAL
                {
                mEQUAL(); if (failed) return ;

                }
                break;
            case 23 :
                // src/main/resources/org/drools/lang/DRL.g:1:148: NOT_EQUAL
                {
                mNOT_EQUAL(); if (failed) return ;

                }
                break;
            case 24 :
                // src/main/resources/org/drools/lang/DRL.g:1:158: GREATER
                {
                mGREATER(); if (failed) return ;

                }
                break;
            case 25 :
                // src/main/resources/org/drools/lang/DRL.g:1:166: GREATER_EQUAL
                {
                mGREATER_EQUAL(); if (failed) return ;

                }
                break;
            case 26 :
                // src/main/resources/org/drools/lang/DRL.g:1:180: LESS
                {
                mLESS(); if (failed) return ;

                }
                break;
            case 27 :
                // src/main/resources/org/drools/lang/DRL.g:1:185: LESS_EQUAL
                {
                mLESS_EQUAL(); if (failed) return ;

                }
                break;
            case 28 :
                // src/main/resources/org/drools/lang/DRL.g:1:196: ARROW
                {
                mARROW(); if (failed) return ;

                }
                break;
            case 29 :
                // src/main/resources/org/drools/lang/DRL.g:1:202: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 30 :
                // src/main/resources/org/drools/lang/DRL.g:1:205: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 31 :
                // src/main/resources/org/drools/lang/DRL.g:1:216: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 32 :
                // src/main/resources/org/drools/lang/DRL.g:1:228: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 33 :
                // src/main/resources/org/drools/lang/DRL.g:1:240: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 34 :
                // src/main/resources/org/drools/lang/DRL.g:1:253: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 35 :
                // src/main/resources/org/drools/lang/DRL.g:1:264: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 36 :
                // src/main/resources/org/drools/lang/DRL.g:1:276: COMMA
                {
                mCOMMA(); if (failed) return ;

                }
                break;
            case 37 :
                // src/main/resources/org/drools/lang/DRL.g:1:282: DOT
                {
                mDOT(); if (failed) return ;

                }
                break;
            case 38 :
                // src/main/resources/org/drools/lang/DRL.g:1:286: DOUBLE_AMPER
                {
                mDOUBLE_AMPER(); if (failed) return ;

                }
                break;
            case 39 :
                // src/main/resources/org/drools/lang/DRL.g:1:299: DOUBLE_PIPE
                {
                mDOUBLE_PIPE(); if (failed) return ;

                }
                break;
            case 40 :
                // src/main/resources/org/drools/lang/DRL.g:1:311: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 41 :
                // src/main/resources/org/drools/lang/DRL.g:1:340: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 42 :
                // src/main/resources/org/drools/lang/DRL.g:1:368: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 43 :
                // src/main/resources/org/drools/lang/DRL.g:1:387: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:1703:14: ( '\\r\\n' )
        // src/main/resources/org/drools/lang/DRL.g:1703:16: '\\r\\n'
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
        "\2\uffff\1\47\1\52\1\uffff\1\47\11\46\3\uffff\1\67\1\uffff\1\71"+
        "\1\uffff\1\47\1\74\1\76\1\uffff\1\47\7\uffff\2\47\1\uffff\1\47\5"+
        "\uffff\13\46\15\uffff\6\46\1\124\4\46\1\131\1\132\1\133\3\46\1\uffff"+
        "\1\137\1\140\1\141\1\142\3\uffff\1\131\2\46\4\uffff\3\46\1\150\1"+
        "\46\1\uffff\1\46\1\153\1\uffff";
    static final String DFA20_eofS =
        "\154\uffff";
    static final String DFA20_minS =
        "\1\11\1\uffff\1\60\1\56\1\uffff\1\0\1\150\1\141\1\143\1\157\2\156"+
        "\1\165\1\166\1\150\3\uffff\1\75\1\uffff\1\52\1\uffff\3\75\1\uffff"+
        "\1\44\7\uffff\1\46\1\174\1\uffff\1\52\5\uffff\1\165\1\145\1\157"+
        "\1\154\1\143\1\154\1\144\1\151\1\154\2\145\15\uffff\1\145\1\156"+
        "\1\155\1\163\1\165\1\154\1\60\1\164\1\154\1\162\1\156\3\60\1\145"+
        "\1\155\1\145\1\uffff\4\60\3\uffff\1\60\1\165\1\143\4\uffff\1\154"+
        "\1\164\1\141\1\60\1\164\1\uffff\1\145\1\60\1\uffff";
    static final String DFA20_maxS =
        "\1\u00ff\1\uffff\1\76\1\71\1\uffff\1\ufffe\2\162\1\143\1\157\2\156"+
        "\1\165\1\166\1\150\3\uffff\1\75\1\uffff\1\52\1\uffff\3\75\1\uffff"+
        "\1\u00ff\7\uffff\1\46\1\174\1\uffff\1\57\5\uffff\1\165\1\145\1\157"+
        "\1\154\1\143\1\154\1\144\1\151\1\154\2\145\15\uffff\1\145\1\156"+
        "\1\155\1\163\1\165\1\154\1\u00ff\1\164\1\154\1\162\1\156\3\u00ff"+
        "\1\145\1\155\1\145\1\uffff\4\u00ff\3\uffff\1\u00ff\1\165\1\143\4"+
        "\uffff\1\154\1\164\1\141\1\u00ff\1\164\1\uffff\1\145\1\u00ff\1\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\12\uffff\1\17\1\20\1\21\1\uffff\1\23\1"+
        "\uffff\1\25\3\uffff\1\35\1\uffff\1\36\1\37\1\40\1\41\1\42\1\43\1"+
        "\44\2\uffff\1\50\1\uffff\1\35\1\53\1\34\1\3\1\2\13\uffff\1\26\1"+
        "\22\1\24\1\45\1\27\1\31\1\30\1\33\1\32\1\46\1\47\1\52\1\51\21\uffff"+
        "\1\10\4\uffff\1\5\1\15\1\11\3\uffff\1\12\1\13\1\14\1\16\5\uffff"+
        "\1\7\2\uffff\1\6";
    static final String DFA20_specialS =
        "\154\uffff}>";
    static final String[] DFA20_transitionS = {
            "\2\1\1\uffff\2\1\22\uffff\1\1\1\26\1\4\1\44\1\31\1\32\1\42\1"+
            "\5\1\33\1\34\2\47\1\41\1\2\1\24\1\45\12\3\1\25\1\23\1\30\1\22"+
            "\1\27\1\47\1\21\32\46\1\35\1\47\1\36\1\47\1\31\1\17\1\10\1\46"+
            "\1\11\1\46\1\12\1\7\2\46\1\13\4\46\1\14\1\15\4\46\1\6\2\46\1"+
            "\16\3\46\1\37\1\43\1\40\1\20\101\uffff\100\46",
            "",
            "\12\3\4\uffff\1\50",
            "\1\51\1\uffff\12\3",
            "",
            "\uffff\4",
            "\1\54\11\uffff\1\53",
            "\1\56\20\uffff\1\55",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "",
            "",
            "",
            "\1\66",
            "",
            "\1\70",
            "",
            "\1\72",
            "\1\73",
            "\1\75",
            "",
            "\1\46\34\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\77",
            "\1\100",
            "",
            "\1\101\4\uffff\1\102",
            "",
            "",
            "",
            "",
            "",
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
            "\1\115",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\1\134",
            "\1\135",
            "\1\136",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "",
            "",
            "",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\1\143",
            "\1\144",
            "",
            "",
            "",
            "",
            "\1\145",
            "\1\146",
            "\1\147",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
            "\1\151",
            "",
            "\1\152",
            "\12\46\7\uffff\32\46\4\uffff\1\46\1\uffff\32\46\105\uffff\100"+
            "\46",
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
            return "1:1: Tokens : ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | END | FROM | INIT | NULL | OVER | THEN | WHEN | GRAVE_ACCENT | TILDE | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC );";
        }
    }
 

}
