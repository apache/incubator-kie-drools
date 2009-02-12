// $ANTLR 3.1.1 src/main/resources/org/drools/lang/DRL.g 2009-02-11 15:20:22

	package org.drools.lang;

	import org.drools.compiler.DroolsParserException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int COMMA=87;
    public static final int VT_PATTERN_TYPE=39;
    public static final int VT_ACCUMULATE_ID_CLAUSE=28;
    public static final int VK_DIALECT=54;
    public static final int VK_FUNCTION=65;
    public static final int HexDigit=119;
    public static final int VK_ATTRIBUTES=57;
    public static final int VT_EXPRESSION_CHAIN=30;
    public static final int MISC=115;
    public static final int VT_AND_PREFIX=23;
    public static final int VK_QUERY=63;
    public static final int THEN=112;
    public static final int VK_AUTO_FOCUS=49;
    public static final int DOT=83;
    public static final int VK_IMPORT=60;
    public static final int VT_SLOT=15;
    public static final int VT_PACKAGE_ID=40;
    public static final int LEFT_SQUARE=110;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=122;
    public static final int VT_DATA_TYPE=38;
    public static final int VT_FACT=6;
    public static final int LEFT_CURLY=113;
    public static final int AT=89;
    public static final int LEFT_PAREN=86;
    public static final int DOUBLE_AMPER=96;
    public static final int VT_QUERY_ID=9;
    public static final int VT_ACCESSOR_PATH=36;
    public static final int VT_LABEL=8;
    public static final int WHEN=92;
    public static final int VT_ENTRYPOINT_ID=13;
    public static final int VK_SALIENCE=55;
    public static final int VT_FIELD=35;
    public static final int WS=117;
    public static final int OVER=98;
    public static final int STRING=85;
    public static final int VK_AND=72;
    public static final int VT_ACCESSOR_ELEMENT=37;
    public static final int VK_REVERSE=76;
    public static final int VK_GLOBAL=66;
    public static final int VT_ACCUMULATE_INIT_CLAUSE=27;
    public static final int VT_BEHAVIOR=21;
    public static final int VK_DURATION=53;
    public static final int VT_SQUARE_CHUNK=19;
    public static final int VK_FORALL=74;
    public static final int VT_PAREN_CHUNK=20;
    public static final int VT_COMPILATION_UNIT=4;
    public static final int COLLECT=100;
    public static final int VK_ENABLED=56;
    public static final int VK_RESULT=77;
    public static final int EQUALS=91;
    public static final int UnicodeEscape=120;
    public static final int VK_PACKAGE=61;
    public static final int VT_RULE_ID=12;
    public static final int EQUAL=102;
    public static final int VK_NO_LOOP=48;
    public static final int SEMICOLON=81;
    public static final int VK_TEMPLATE=62;
    public static final int VT_AND_IMPLICIT=22;
    public static final int NULL=109;
    public static final int COLON=90;
    public static final int MULTI_LINE_COMMENT=124;
    public static final int VT_RULE_ATTRIBUTES=16;
    public static final int RIGHT_SQUARE=111;
    public static final int VK_AGENDA_GROUP=51;
    public static final int VT_FACT_OR=33;
    public static final int VK_NOT=69;
    public static final int VK_DATE_EXPIRES=46;
    public static final int ARROW=101;
    public static final int FLOAT=108;
    public static final int VK_EXTEND=59;
    public static final int VT_SLOT_ID=14;
    public static final int VT_CURLY_CHUNK=18;
    public static final int VT_OR_PREFIX=24;
    public static final int DOUBLE_PIPE=95;
    public static final int VK_END=79;
    public static final int LESS=105;
    public static final int VT_TYPE_DECLARE_ID=11;
    public static final int VT_PATTERN=31;
    public static final int VK_DATE_EFFECTIVE=45;
    public static final int EscapeSequence=118;
    public static final int VK_EXISTS=73;
    public static final int INT=94;
    public static final int VT_BIND_FIELD=34;
    public static final int VK_RULE=58;
    public static final int VK_EVAL=67;
    public static final int GREATER=103;
    public static final int VT_FACT_BINDING=32;
    public static final int ID=82;
    public static final int FROM=97;
    public static final int NOT_EQUAL=107;
    public static final int RIGHT_CURLY=114;
    public static final int VK_OPERATOR=78;
    public static final int VK_ENTRY_POINT=68;
    public static final int VT_PARAM_LIST=44;
    public static final int VT_AND_INFIX=25;
    public static final int BOOL=93;
    public static final int VT_FROM_SOURCE=29;
    public static final int VK_LOCK_ON_ACTIVE=47;
    public static final int VT_FUNCTION_IMPORT=5;
    public static final int VK_IN=70;
    public static final int VT_RHS_CHUNK=17;
    public static final int GREATER_EQUAL=104;
    public static final int VT_OR_INFIX=26;
    public static final int DOT_STAR=84;
    public static final int VK_OR=71;
    public static final int VT_GLOBAL_ID=42;
    public static final int LESS_EQUAL=106;
    public static final int ACCUMULATE=99;
    public static final int VK_RULEFLOW_GROUP=52;
    public static final int VT_FUNCTION_ID=43;
    public static final int EOF=-1;
    public static final int VT_CONSTRAINTS=7;
    public static final int VT_IMPORT_ID=41;
    public static final int EOL=116;
    public static final int VK_INIT=80;
    public static final int VK_ACTIVATION_GROUP=50;
    public static final int OctalEscape=121;
    public static final int VK_ACTION=75;
    public static final int RIGHT_PAREN=88;
    public static final int VT_TEMPLATE_ID=10;
    public static final int VK_DECLARE=64;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=123;

    	private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
    	private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(null, null);

    	/** The standard method called to automatically emit a token at the
    	 *  outermost lexical rule.  The token object should point into the
    	 *  char buffer start..stop.  If there is a text override in 'text',
    	 *  use that to set the token's text.  Override this method to emit
    	 *  custom Token objects.
    	 */
    	public Token emit() {
    		Token t = new DroolsToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
    		t.setLine(state.tokenStartLine);
    		t.setText(state.text);
    		t.setCharPositionInLine(state.tokenStartCharPositionInLine);
    		emit(t);
    		return t;
    	}

    	public void reportError(RecognitionException ex) {
    		errors.add(errorMessageFactory.createDroolsException(ex));
    	}

    	/** return the raw DroolsParserException errors */
    	public List<DroolsParserException> getErrors() {
    		return errors;
    	}

    	/** Overrided this method to not output mesages */
    	public void emitErrorMessage(String msg) {
    	}


    // delegates
    // delegators

    public DRLLexer() {;} 
    public DRLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public DRLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRL.g"; }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1701:9: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // src/main/resources/org/drools/lang/DRL.g:1701:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1701:17: ( ' ' | '\\t' | '\\f' | EOL )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1701:19: ' '
            	    {
            	    match(' '); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRL.g:1702:19: '\\t'
            	    {
            	    match('\t'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/DRL.g:1703:19: '\\f'
            	    {
            	    match('\f'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/main/resources/org/drools/lang/DRL.g:1704:19: EOL
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
            // src/main/resources/org/drools/lang/DRL.g:1710:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // src/main/resources/org/drools/lang/DRL.g:1711:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // src/main/resources/org/drools/lang/DRL.g:1711:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1_DRL())) {
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
                    // src/main/resources/org/drools/lang/DRL.g:1711:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1712:25: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1713:25: '\\n'
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
            // src/main/resources/org/drools/lang/DRL.g:1718:2: ( ( '-' )? ( '0' .. '9' )+ )
            // src/main/resources/org/drools/lang/DRL.g:1718:4: ( '-' )? ( '0' .. '9' )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1718:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1718:5: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1718:10: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1718:11: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1722:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // src/main/resources/org/drools/lang/DRL.g:1722:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // src/main/resources/org/drools/lang/DRL.g:1722:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1722:5: '-'
                    {
                    match('-'); if (state.failed) return ;

                    }
                    break;

            }

            // src/main/resources/org/drools/lang/DRL.g:1722:10: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1722:11: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1722:26: ( '0' .. '9' )+
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
            	    // src/main/resources/org/drools/lang/DRL.g:1722:27: '0' .. '9'
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
            // src/main/resources/org/drools/lang/DRL.g:1726:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    // src/main/resources/org/drools/lang/DRL.g:1726:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // src/main/resources/org/drools/lang/DRL.g:1726:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // src/main/resources/org/drools/lang/DRL.g:1726:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1726:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // src/main/resources/org/drools/lang/DRL.g:1726:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRL.g:1726:32: ~ ( '\\\\' | '\"' )
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
                    // src/main/resources/org/drools/lang/DRL.g:1727:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // src/main/resources/org/drools/lang/DRL.g:1727:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // src/main/resources/org/drools/lang/DRL.g:1727:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1727:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // src/main/resources/org/drools/lang/DRL.g:1727:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRL.g:1727:33: ~ ( '\\\\' | '\\'' )
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
            // src/main/resources/org/drools/lang/DRL.g:1731:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // src/main/resources/org/drools/lang/DRL.g:1731:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // src/main/resources/org/drools/lang/DRL.g:1735:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
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
                    // src/main/resources/org/drools/lang/DRL.g:1735:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
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
                    // src/main/resources/org/drools/lang/DRL.g:1739:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1740:9: OctalEscape
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
            // src/main/resources/org/drools/lang/DRL.g:1745:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                    // src/main/resources/org/drools/lang/DRL.g:1745:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1745:14: ( '0' .. '3' )
                    // src/main/resources/org/drools/lang/DRL.g:1745:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1745:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1745:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1745:36: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1745:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1746:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1746:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1746:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRL.g:1746:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1746:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRL.g:1747:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRL.g:1747:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRL.g:1747:15: '0' .. '7'
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
            // src/main/resources/org/drools/lang/DRL.g:1752:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // src/main/resources/org/drools/lang/DRL.g:1752:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // src/main/resources/org/drools/lang/DRL.g:1756:2: ( ( 'true' | 'false' ) )
            // src/main/resources/org/drools/lang/DRL.g:1756:4: ( 'true' | 'false' )
            {
            // src/main/resources/org/drools/lang/DRL.g:1756:4: ( 'true' | 'false' )
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
                    // src/main/resources/org/drools/lang/DRL.g:1756:5: 'true'
                    {
                    match("true"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1756:12: 'false'
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

    // $ANTLR start "ACCUMULATE"
    public final void mACCUMULATE() throws RecognitionException {
        try {
            int _type = ACCUMULATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1760:2: ( 'accumulate' )
            // src/main/resources/org/drools/lang/DRL.g:1760:4: 'accumulate'
            {
            match("accumulate"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ACCUMULATE"

    // $ANTLR start "COLLECT"
    public final void mCOLLECT() throws RecognitionException {
        try {
            int _type = COLLECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1764:2: ( 'collect' )
            // src/main/resources/org/drools/lang/DRL.g:1764:4: 'collect'
            {
            match("collect"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLLECT"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1768:2: ( 'from' )
            // src/main/resources/org/drools/lang/DRL.g:1768:4: 'from'
            {
            match("from"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1772:2: ( 'null' )
            // src/main/resources/org/drools/lang/DRL.g:1772:4: 'null'
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

    // $ANTLR start "OVER"
    public final void mOVER() throws RecognitionException {
        try {
            int _type = OVER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1776:2: ( 'over' )
            // src/main/resources/org/drools/lang/DRL.g:1776:4: 'over'
            {
            match("over"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OVER"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1780:2: ( 'then' )
            // src/main/resources/org/drools/lang/DRL.g:1780:4: 'then'
            {
            match("then"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "WHEN"
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1784:2: ( 'when' )
            // src/main/resources/org/drools/lang/DRL.g:1784:4: 'when'
            {
            match("when"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHEN"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1787:4: ( '@' )
            // src/main/resources/org/drools/lang/DRL.g:1787:6: '@'
            {
            match('@'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1791:2: ( '=' )
            // src/main/resources/org/drools/lang/DRL.g:1791:4: '='
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

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1795:2: ( ';' )
            // src/main/resources/org/drools/lang/DRL.g:1795:4: ';'
            {
            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "DOT_STAR"
    public final void mDOT_STAR() throws RecognitionException {
        try {
            int _type = DOT_STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1799:2: ( '.*' )
            // src/main/resources/org/drools/lang/DRL.g:1799:4: '.*'
            {
            match(".*"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT_STAR"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1803:2: ( ':' )
            // src/main/resources/org/drools/lang/DRL.g:1803:4: ':'
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

    // $ANTLR start "EQUAL"
    public final void mEQUAL() throws RecognitionException {
        try {
            int _type = EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1807:2: ( '==' )
            // src/main/resources/org/drools/lang/DRL.g:1807:4: '=='
            {
            match("=="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUAL"

    // $ANTLR start "NOT_EQUAL"
    public final void mNOT_EQUAL() throws RecognitionException {
        try {
            int _type = NOT_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1811:2: ( '!=' )
            // src/main/resources/org/drools/lang/DRL.g:1811:4: '!='
            {
            match("!="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT_EQUAL"

    // $ANTLR start "GREATER"
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1815:2: ( '>' )
            // src/main/resources/org/drools/lang/DRL.g:1815:4: '>'
            {
            match('>'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER"

    // $ANTLR start "GREATER_EQUAL"
    public final void mGREATER_EQUAL() throws RecognitionException {
        try {
            int _type = GREATER_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1819:2: ( '>=' )
            // src/main/resources/org/drools/lang/DRL.g:1819:4: '>='
            {
            match(">="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_EQUAL"

    // $ANTLR start "LESS"
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1823:2: ( '<' )
            // src/main/resources/org/drools/lang/DRL.g:1823:4: '<'
            {
            match('<'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS"

    // $ANTLR start "LESS_EQUAL"
    public final void mLESS_EQUAL() throws RecognitionException {
        try {
            int _type = LESS_EQUAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1827:2: ( '<=' )
            // src/main/resources/org/drools/lang/DRL.g:1827:4: '<='
            {
            match("<="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_EQUAL"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1831:2: ( '->' )
            // src/main/resources/org/drools/lang/DRL.g:1831:4: '->'
            {
            match("->"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1835:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* | '`' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '`' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0=='$'||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')||(LA16_0>='\u00C0' && LA16_0<='\u00FF')) ) {
                alt16=1;
            }
            else if ( (LA16_0=='`') ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRL.g:1835:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
                    {
                    if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // src/main/resources/org/drools/lang/DRL.g:1835:50: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
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
                    	    break loop14;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRL.g:1836:4: '`' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+ '`'
                    {
                    match('`'); if (state.failed) return ;
                    if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00FF') ) {
                        input.consume();
                    state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}

                    // src/main/resources/org/drools/lang/DRL.g:1836:54: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )+
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
                    	    if ( cnt15 >= 1 ) break loop15;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
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

    // $ANTLR start "LEFT_PAREN"
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1841:9: ( '(' )
            // src/main/resources/org/drools/lang/DRL.g:1841:11: '('
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
            // src/main/resources/org/drools/lang/DRL.g:1845:9: ( ')' )
            // src/main/resources/org/drools/lang/DRL.g:1845:11: ')'
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
            // src/main/resources/org/drools/lang/DRL.g:1849:9: ( '[' )
            // src/main/resources/org/drools/lang/DRL.g:1849:11: '['
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
            // src/main/resources/org/drools/lang/DRL.g:1853:9: ( ']' )
            // src/main/resources/org/drools/lang/DRL.g:1853:11: ']'
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
            // src/main/resources/org/drools/lang/DRL.g:1857:9: ( '{' )
            // src/main/resources/org/drools/lang/DRL.g:1857:11: '{'
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
            // src/main/resources/org/drools/lang/DRL.g:1861:9: ( '}' )
            // src/main/resources/org/drools/lang/DRL.g:1861:11: '}'
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

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1864:7: ( ',' )
            // src/main/resources/org/drools/lang/DRL.g:1864:9: ','
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
            // src/main/resources/org/drools/lang/DRL.g:1867:5: ( '.' )
            // src/main/resources/org/drools/lang/DRL.g:1867:7: '.'
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

    // $ANTLR start "DOUBLE_AMPER"
    public final void mDOUBLE_AMPER() throws RecognitionException {
        try {
            int _type = DOUBLE_AMPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1871:2: ( '&&' )
            // src/main/resources/org/drools/lang/DRL.g:1871:4: '&&'
            {
            match("&&"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_AMPER"

    // $ANTLR start "DOUBLE_PIPE"
    public final void mDOUBLE_PIPE() throws RecognitionException {
        try {
            int _type = DOUBLE_PIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1875:2: ( '||' )
            // src/main/resources/org/drools/lang/DRL.g:1875:4: '||'
            {
            match("||"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_PIPE"

    // $ANTLR start "SH_STYLE_SINGLE_LINE_COMMENT"
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1879:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/DRL.g:1879:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRL.g:1879:8: ( options {greedy=false; } : . )*
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
                else if ( ((LA17_0>='\u0000' && LA17_0<='\t')||(LA17_0>='\u000B' && LA17_0<='\f')||(LA17_0>='\u000E' && LA17_0<='\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1879:35: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            mEOL(); if (state.failed) return ;
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
            // src/main/resources/org/drools/lang/DRL.g:1885:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/DRL.g:1885:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (state.failed) return ;

            // src/main/resources/org/drools/lang/DRL.g:1885:9: ( options {greedy=false; } : . )*
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
                else if ( ((LA18_0>='\u0000' && LA18_0<='\t')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='\uFFFF')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1885:36: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
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

    // $ANTLR start "MULTI_LINE_COMMENT"
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1890:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // src/main/resources/org/drools/lang/DRL.g:1890:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // src/main/resources/org/drools/lang/DRL.g:1890:9: ( options {greedy=false; } : . )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0=='*') ) {
                    int LA19_1 = input.LA(2);

                    if ( (LA19_1=='/') ) {
                        alt19=2;
                    }
                    else if ( ((LA19_1>='\u0000' && LA19_1<='.')||(LA19_1>='0' && LA19_1<='\uFFFF')) ) {
                        alt19=1;
                    }


                }
                else if ( ((LA19_0>='\u0000' && LA19_0<=')')||(LA19_0>='+' && LA19_0<='\uFFFF')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRL.g:1890:35: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop19;
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

    // $ANTLR start "MISC"
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRL.g:1894:7: ( '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | '/' | '\\'' | '\\\\' | '|' | '&' )
            // src/main/resources/org/drools/lang/DRL.g:
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='\'')||(input.LA(1)>='*' && input.LA(1)<='+')||input.LA(1)=='-'||input.LA(1)=='/'||input.LA(1)=='?'||input.LA(1)=='\\'||(input.LA(1)>='^' && input.LA(1)<='_')||input.LA(1)=='|' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MISC"

    public void mTokens() throws RecognitionException {
        // src/main/resources/org/drools/lang/DRL.g:1:8: ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | FROM | NULL | OVER | THEN | WHEN | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC )
        int alt20=39;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // src/main/resources/org/drools/lang/DRL.g:1:10: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRL.g:1:13: INT
                {
                mINT(); if (state.failed) return ;

                }
                break;
            case 3 :
                // src/main/resources/org/drools/lang/DRL.g:1:17: FLOAT
                {
                mFLOAT(); if (state.failed) return ;

                }
                break;
            case 4 :
                // src/main/resources/org/drools/lang/DRL.g:1:23: STRING
                {
                mSTRING(); if (state.failed) return ;

                }
                break;
            case 5 :
                // src/main/resources/org/drools/lang/DRL.g:1:30: BOOL
                {
                mBOOL(); if (state.failed) return ;

                }
                break;
            case 6 :
                // src/main/resources/org/drools/lang/DRL.g:1:35: ACCUMULATE
                {
                mACCUMULATE(); if (state.failed) return ;

                }
                break;
            case 7 :
                // src/main/resources/org/drools/lang/DRL.g:1:46: COLLECT
                {
                mCOLLECT(); if (state.failed) return ;

                }
                break;
            case 8 :
                // src/main/resources/org/drools/lang/DRL.g:1:54: FROM
                {
                mFROM(); if (state.failed) return ;

                }
                break;
            case 9 :
                // src/main/resources/org/drools/lang/DRL.g:1:59: NULL
                {
                mNULL(); if (state.failed) return ;

                }
                break;
            case 10 :
                // src/main/resources/org/drools/lang/DRL.g:1:64: OVER
                {
                mOVER(); if (state.failed) return ;

                }
                break;
            case 11 :
                // src/main/resources/org/drools/lang/DRL.g:1:69: THEN
                {
                mTHEN(); if (state.failed) return ;

                }
                break;
            case 12 :
                // src/main/resources/org/drools/lang/DRL.g:1:74: WHEN
                {
                mWHEN(); if (state.failed) return ;

                }
                break;
            case 13 :
                // src/main/resources/org/drools/lang/DRL.g:1:79: AT
                {
                mAT(); if (state.failed) return ;

                }
                break;
            case 14 :
                // src/main/resources/org/drools/lang/DRL.g:1:82: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 15 :
                // src/main/resources/org/drools/lang/DRL.g:1:89: SEMICOLON
                {
                mSEMICOLON(); if (state.failed) return ;

                }
                break;
            case 16 :
                // src/main/resources/org/drools/lang/DRL.g:1:99: DOT_STAR
                {
                mDOT_STAR(); if (state.failed) return ;

                }
                break;
            case 17 :
                // src/main/resources/org/drools/lang/DRL.g:1:108: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 18 :
                // src/main/resources/org/drools/lang/DRL.g:1:114: EQUAL
                {
                mEQUAL(); if (state.failed) return ;

                }
                break;
            case 19 :
                // src/main/resources/org/drools/lang/DRL.g:1:120: NOT_EQUAL
                {
                mNOT_EQUAL(); if (state.failed) return ;

                }
                break;
            case 20 :
                // src/main/resources/org/drools/lang/DRL.g:1:130: GREATER
                {
                mGREATER(); if (state.failed) return ;

                }
                break;
            case 21 :
                // src/main/resources/org/drools/lang/DRL.g:1:138: GREATER_EQUAL
                {
                mGREATER_EQUAL(); if (state.failed) return ;

                }
                break;
            case 22 :
                // src/main/resources/org/drools/lang/DRL.g:1:152: LESS
                {
                mLESS(); if (state.failed) return ;

                }
                break;
            case 23 :
                // src/main/resources/org/drools/lang/DRL.g:1:157: LESS_EQUAL
                {
                mLESS_EQUAL(); if (state.failed) return ;

                }
                break;
            case 24 :
                // src/main/resources/org/drools/lang/DRL.g:1:168: ARROW
                {
                mARROW(); if (state.failed) return ;

                }
                break;
            case 25 :
                // src/main/resources/org/drools/lang/DRL.g:1:174: ID
                {
                mID(); if (state.failed) return ;

                }
                break;
            case 26 :
                // src/main/resources/org/drools/lang/DRL.g:1:177: LEFT_PAREN
                {
                mLEFT_PAREN(); if (state.failed) return ;

                }
                break;
            case 27 :
                // src/main/resources/org/drools/lang/DRL.g:1:188: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (state.failed) return ;

                }
                break;
            case 28 :
                // src/main/resources/org/drools/lang/DRL.g:1:200: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 29 :
                // src/main/resources/org/drools/lang/DRL.g:1:212: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 30 :
                // src/main/resources/org/drools/lang/DRL.g:1:225: LEFT_CURLY
                {
                mLEFT_CURLY(); if (state.failed) return ;

                }
                break;
            case 31 :
                // src/main/resources/org/drools/lang/DRL.g:1:236: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (state.failed) return ;

                }
                break;
            case 32 :
                // src/main/resources/org/drools/lang/DRL.g:1:248: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 33 :
                // src/main/resources/org/drools/lang/DRL.g:1:254: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 34 :
                // src/main/resources/org/drools/lang/DRL.g:1:258: DOUBLE_AMPER
                {
                mDOUBLE_AMPER(); if (state.failed) return ;

                }
                break;
            case 35 :
                // src/main/resources/org/drools/lang/DRL.g:1:271: DOUBLE_PIPE
                {
                mDOUBLE_PIPE(); if (state.failed) return ;

                }
                break;
            case 36 :
                // src/main/resources/org/drools/lang/DRL.g:1:283: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 37 :
                // src/main/resources/org/drools/lang/DRL.g:1:312: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 38 :
                // src/main/resources/org/drools/lang/DRL.g:1:340: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 39 :
                // src/main/resources/org/drools/lang/DRL.g:1:359: MISC
                {
                mMISC(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_DRL
    public final void synpred1_DRL_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRL.g:1711:14: ( '\\r\\n' )
        // src/main/resources/org/drools/lang/DRL.g:1711:16: '\\r\\n'
        {
        match("\r\n"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_DRL

    public final boolean synpred1_DRL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA20_eotS =
        "\2\uffff\1\42\1\45\1\uffff\1\42\7\26\1\uffff\1\60\1\uffff\1\62"+
        "\1\uffff\1\42\1\65\1\67\11\uffff\2\42\1\uffff\1\42\4\uffff\11\26"+
        "\15\uffff\11\26\1\116\1\117\1\26\1\121\2\26\1\124\1\125\1\126\2"+
        "\uffff\1\116\1\uffff\2\26\3\uffff\3\26\1\134\1\26\1\uffff\1\26\1"+
        "\137\1\uffff";
    static final String DFA20_eofS =
        "\140\uffff";
    static final String DFA20_minS =
        "\1\11\1\uffff\1\60\1\56\1\uffff\1\0\1\150\1\141\1\143\1\157\1\165"+
        "\1\166\1\150\1\uffff\1\75\1\uffff\1\52\1\uffff\3\75\11\uffff\1\46"+
        "\1\174\1\uffff\1\52\4\uffff\1\165\1\145\1\154\1\157\1\143\2\154"+
        "\2\145\15\uffff\1\145\1\156\1\163\1\155\1\165\2\154\1\162\1\156"+
        "\2\60\1\145\1\60\1\155\1\145\3\60\2\uffff\1\60\1\uffff\1\165\1\143"+
        "\3\uffff\1\154\1\164\1\141\1\60\1\164\1\uffff\1\145\1\60\1\uffff";
    static final String DFA20_maxS =
        "\1\u00ff\1\uffff\1\76\1\71\1\uffff\1\uffff\2\162\1\143\1\157\1"+
        "\165\1\166\1\150\1\uffff\1\75\1\uffff\1\52\1\uffff\3\75\11\uffff"+
        "\1\46\1\174\1\uffff\1\57\4\uffff\1\165\1\145\1\154\1\157\1\143\2"+
        "\154\2\145\15\uffff\1\145\1\156\1\163\1\155\1\165\2\154\1\162\1"+
        "\156\2\u00ff\1\145\1\u00ff\1\155\1\145\3\u00ff\2\uffff\1\u00ff\1"+
        "\uffff\1\165\1\143\3\uffff\1\154\1\164\1\141\1\u00ff\1\164\1\uffff"+
        "\1\145\1\u00ff\1\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\10\uffff\1\15\1\uffff\1\17\1\uffff\1\21"+
        "\3\uffff\2\31\1\32\1\33\1\34\1\35\1\36\1\37\1\40\2\uffff\1\44\1"+
        "\uffff\1\47\1\30\1\3\1\2\11\uffff\1\22\1\16\1\20\1\41\1\23\1\25"+
        "\1\24\1\27\1\26\1\42\1\43\1\45\1\46\22\uffff\1\5\1\13\1\uffff\1"+
        "\10\2\uffff\1\11\1\12\1\14\5\uffff\1\7\2\uffff\1\6";
    static final String DFA20_specialS =
        "\5\uffff\1\0\132\uffff}>";
    static final String[] DFA20_transitionS = {
            "\2\1\1\uffff\2\1\22\uffff\1\1\1\22\1\4\1\40\1\25\1\42\1\36"+
            "\1\5\1\27\1\30\2\42\1\35\1\2\1\20\1\41\12\3\1\21\1\17\1\24\1"+
            "\16\1\23\1\42\1\15\32\26\1\31\1\42\1\32\1\42\1\25\1\26\1\10"+
            "\1\26\1\11\2\26\1\7\7\26\1\12\1\13\4\26\1\6\2\26\1\14\3\26\1"+
            "\33\1\37\1\34\102\uffff\100\26",
            "",
            "\12\3\4\uffff\1\43",
            "\1\44\1\uffff\12\3",
            "",
            "\0\4",
            "\1\47\11\uffff\1\46",
            "\1\50\20\uffff\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "",
            "\1\57",
            "",
            "\1\61",
            "",
            "\1\63",
            "\1\64",
            "\1\66",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\70",
            "\1\71",
            "",
            "\1\73\4\uffff\1\72",
            "",
            "",
            "",
            "",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\1\120",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\1\122",
            "\1\123",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "",
            "",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "",
            "\1\127",
            "\1\130",
            "",
            "",
            "",
            "\1\131",
            "\1\132",
            "\1\133",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
            "\1\135",
            "",
            "\1\136",
            "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26\105\uffff"+
            "\100\26",
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
            return "1:1: Tokens : ( WS | INT | FLOAT | STRING | BOOL | ACCUMULATE | COLLECT | FROM | NULL | OVER | THEN | WHEN | AT | EQUALS | SEMICOLON | DOT_STAR | COLON | EQUAL | NOT_EQUAL | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL | ARROW | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA20_5 = input.LA(1);

                        s = -1;
                        if ( ((LA20_5>='\u0000' && LA20_5<='\uFFFF')) ) {s = 4;}

                        else s = 34;

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
 

}