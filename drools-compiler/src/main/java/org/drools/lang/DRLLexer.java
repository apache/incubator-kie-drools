// $ANTLR 3.3 Nov 30, 2010 12:46:29 src/main/resources/org/drools/lang/DRLLexer.g 2012-07-03 15:13:56

    package org.drools.lang;

    import org.drools.compiler.DroolsParserException;
    import org.drools.core.util.StringUtils;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
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
    public static final int DOUBLE_AMPER=50;
    public static final int DOUBLE_PIPE=51;
    public static final int QUESTION=52;
    public static final int NEGATION=53;
    public static final int TILDE=54;
    public static final int PIPE=55;
    public static final int AMPER=56;
    public static final int XOR=57;
    public static final int MOD=58;
    public static final int STAR=59;
    public static final int MINUS=60;
    public static final int PLUS=61;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=62;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=63;
    public static final int MULTI_LINE_COMMENT=64;
    public static final int IdentifierStart=65;
    public static final int IdentifierPart=66;
    public static final int ID=67;
    public static final int DIV=68;
    public static final int MISC=69;

        private List<DroolsParserException> errors = new ArrayList<DroolsParserException>();
        private DroolsParserExceptionFactory errorMessageFactory = new DroolsParserExceptionFactory(null);

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
        
        public String normalizeString( String input ) {
            if( input != null && input.length() >= 4 ) {
                input = input.substring( 1, input.length() - 1 ); 
                input = input.replaceAll( "\'", "'" );
                input = input.replaceAll( "\"", "\\\"" );
                input = "\"" + input + "\"";
            }
            return input;
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
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/DRLLexer.g"; }

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:57:9: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // src/main/resources/org/drools/lang/DRLLexer.g:57:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // src/main/resources/org/drools/lang/DRLLexer.g:57:17: ( ' ' | '\\t' | '\\f' | EOL )+
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
            	    // src/main/resources/org/drools/lang/DRLLexer.g:57:19: ' '
            	    {
            	    match(' '); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:58:19: '\\t'
            	    {
            	    match('\t'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:59:19: '\\f'
            	    {
            	    match('\f'); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:60:19: EOL
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
            // src/main/resources/org/drools/lang/DRLLexer.g:66:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // src/main/resources/org/drools/lang/DRLLexer.g:67:12: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // src/main/resources/org/drools/lang/DRLLexer.g:67:12: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1=='\n') && (synpred1_DRLLexer())) {
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
                    // src/main/resources/org/drools/lang/DRLLexer.g:67:20: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:68:25: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:69:25: '\\n'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:74:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ FloatTypeSuffix )
            int alt13=4;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:74:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:74:9: ( '0' .. '9' )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:74:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);

                    match('.'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:74:25: ( '0' .. '9' )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:74:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // src/main/resources/org/drools/lang/DRLLexer.g:74:37: ( Exponent )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='E'||LA5_0=='e') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:74:37: Exponent
                            {
                            mExponent(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:74:47: ( FloatTypeSuffix )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='B'||LA6_0=='D'||LA6_0=='F'||LA6_0=='d'||LA6_0=='f') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:74:47: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:75:9: '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )?
                    {
                    match('.'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:75:13: ( '0' .. '9' )+
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
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:75:14: '0' .. '9'
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

                    // src/main/resources/org/drools/lang/DRLLexer.g:75:25: ( Exponent )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:75:25: Exponent
                            {
                            mExponent(); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:75:35: ( FloatTypeSuffix )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='B'||LA9_0=='D'||LA9_0=='F'||LA9_0=='d'||LA9_0=='f') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:75:35: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:76:9: ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:76:9: ( '0' .. '9' )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:76:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);

                    mExponent(); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:76:30: ( FloatTypeSuffix )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0=='B'||LA11_0=='D'||LA11_0=='F'||LA11_0=='d'||LA11_0=='f') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:76:30: FloatTypeSuffix
                            {
                            mFloatTypeSuffix(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:77:9: ( '0' .. '9' )+ FloatTypeSuffix
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:77:9: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:77:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    mFloatTypeSuffix(); if (state.failed) return ;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLLexer.g:81:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // src/main/resources/org/drools/lang/DRLLexer.g:81:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // src/main/resources/org/drools/lang/DRLLexer.g:81:22: ( '+' | '-' )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='+'||LA14_0=='-') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
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

            }

            // src/main/resources/org/drools/lang/DRLLexer.g:81:33: ( '0' .. '9' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:81:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

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


            }

        }
        finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "FloatTypeSuffix"
    public final void mFloatTypeSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLLexer.g:84:17: ( ( 'f' | 'F' | 'd' | 'D' | 'B' ) )
            // src/main/resources/org/drools/lang/DRLLexer.g:84:19: ( 'f' | 'F' | 'd' | 'D' | 'B' )
            {
            if ( input.LA(1)=='B'||input.LA(1)=='D'||input.LA(1)=='F'||input.LA(1)=='d'||input.LA(1)=='f' ) {
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
    // $ANTLR end "FloatTypeSuffix"

    // $ANTLR start "HEX"
    public final void mHEX() throws RecognitionException {
        try {
            int _type = HEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:86:6: ( '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )? )
            // src/main/resources/org/drools/lang/DRLLexer.g:86:8: '0' ( 'x' | 'X' ) ( HexDigit )+ ( IntegerTypeSuffix )?
            {
            match('0'); if (state.failed) return ;
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // src/main/resources/org/drools/lang/DRLLexer.g:86:22: ( HexDigit )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='F')||(LA16_0>='a' && LA16_0<='f')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:86:22: HexDigit
            	    {
            	    mHexDigit(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);

            // src/main/resources/org/drools/lang/DRLLexer.g:86:32: ( IntegerTypeSuffix )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='I'||LA17_0=='L'||LA17_0=='l') ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:86:32: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); if (state.failed) return ;

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
    // $ANTLR end "HEX"

    // $ANTLR start "DECIMAL"
    public final void mDECIMAL() throws RecognitionException {
        try {
            int _type = DECIMAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:88:9: ( ( '0' .. '9' )+ ( IntegerTypeSuffix )? )
            // src/main/resources/org/drools/lang/DRLLexer.g:88:11: ( '0' .. '9' )+ ( IntegerTypeSuffix )?
            {
            // src/main/resources/org/drools/lang/DRLLexer.g:88:11: ( '0' .. '9' )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>='0' && LA18_0<='9')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:88:12: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);

            // src/main/resources/org/drools/lang/DRLLexer.g:88:23: ( IntegerTypeSuffix )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='I'||LA19_0=='L'||LA19_0=='l') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:88:23: IntegerTypeSuffix
                    {
                    mIntegerTypeSuffix(); if (state.failed) return ;

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
    // $ANTLR end "DECIMAL"

    // $ANTLR start "IntegerTypeSuffix"
    public final void mIntegerTypeSuffix() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLLexer.g:91:19: ( ( 'l' | 'L' | 'I' ) )
            // src/main/resources/org/drools/lang/DRLLexer.g:91:21: ( 'l' | 'L' | 'I' )
            {
            if ( input.LA(1)=='I'||input.LA(1)=='L'||input.LA(1)=='l' ) {
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
    // $ANTLR end "IntegerTypeSuffix"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:94:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='\"') ) {
                alt22=1;
            }
            else if ( (LA22_0=='\'') ) {
                alt22=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:94:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:94:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:94:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:94:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
                    loop20:
                    do {
                        int alt20=3;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0=='\\') ) {
                            alt20=1;
                        }
                        else if ( ((LA20_0>='\u0000' && LA20_0<='!')||(LA20_0>='#' && LA20_0<='[')||(LA20_0>=']' && LA20_0<='\uFFFF')) ) {
                            alt20=2;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:94:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:94:32: ~ ( '\\\\' | '\"' )
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
                    	    break loop20;
                        }
                    } while (true);

                    match('\"'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:95:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:95:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:95:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:95:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
                    loop21:
                    do {
                        int alt21=3;
                        int LA21_0 = input.LA(1);

                        if ( (LA21_0=='\\') ) {
                            alt21=1;
                        }
                        else if ( ((LA21_0>='\u0000' && LA21_0<='&')||(LA21_0>='(' && LA21_0<='[')||(LA21_0>=']' && LA21_0<='\uFFFF')) ) {
                            alt21=2;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:95:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:95:33: ~ ( '\\\\' | '\\'' )
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
                    	    break loop21;
                        }
                    } while (true);

                    match('\''); if (state.failed) return ;

                    }

                    if ( state.backtracking==0 ) {
                       setText( normalizeString( getText() ) ); 
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

    // $ANTLR start "TIME_INTERVAL"
    public final void mTIME_INTERVAL() throws RecognitionException {
        try {
            int _type = TIME_INTERVAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:100:5: ( ( ( '0' .. '9' )+ 'd' ) ( ( '0' .. '9' )+ 'h' )? ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 'h' ) ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 'm' ) ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 's' ) ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ ( 'ms' )? ) )
            int alt53=5;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:100:7: ( ( '0' .. '9' )+ 'd' ) ( ( '0' .. '9' )+ 'h' )? ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:100:7: ( ( '0' .. '9' )+ 'd' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:100:8: ( '0' .. '9' )+ 'd'
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:100:8: ( '0' .. '9' )+
                    int cnt23=0;
                    loop23:
                    do {
                        int alt23=2;
                        int LA23_0 = input.LA(1);

                        if ( ((LA23_0>='0' && LA23_0<='9')) ) {
                            alt23=1;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:100:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt23 >= 1 ) break loop23;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(23, input);
                                throw eee;
                        }
                        cnt23++;
                    } while (true);

                    match('d'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:100:25: ( ( '0' .. '9' )+ 'h' )?
                    int alt25=2;
                    alt25 = dfa25.predict(input);
                    switch (alt25) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:26: ( '0' .. '9' )+ 'h'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:26: ( '0' .. '9' )+
                            int cnt24=0;
                            loop24:
                            do {
                                int alt24=2;
                                int LA24_0 = input.LA(1);

                                if ( ((LA24_0>='0' && LA24_0<='9')) ) {
                                    alt24=1;
                                }


                                switch (alt24) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:100:27: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt24 >= 1 ) break loop24;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(24, input);
                                        throw eee;
                                }
                                cnt24++;
                            } while (true);

                            match('h'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:100:43: ( ( '0' .. '9' )+ 'm' )?
                    int alt27=2;
                    alt27 = dfa27.predict(input);
                    switch (alt27) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:44: ( '0' .. '9' )+ 'm'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:44: ( '0' .. '9' )+
                            int cnt26=0;
                            loop26:
                            do {
                                int alt26=2;
                                int LA26_0 = input.LA(1);

                                if ( ((LA26_0>='0' && LA26_0<='9')) ) {
                                    alt26=1;
                                }


                                switch (alt26) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:100:45: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt26 >= 1 ) break loop26;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(26, input);
                                        throw eee;
                                }
                                cnt26++;
                            } while (true);

                            match('m'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:100:61: ( ( '0' .. '9' )+ 's' )?
                    int alt29=2;
                    alt29 = dfa29.predict(input);
                    switch (alt29) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:62: ( '0' .. '9' )+ 's'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:62: ( '0' .. '9' )+
                            int cnt28=0;
                            loop28:
                            do {
                                int alt28=2;
                                int LA28_0 = input.LA(1);

                                if ( ((LA28_0>='0' && LA28_0<='9')) ) {
                                    alt28=1;
                                }


                                switch (alt28) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:100:63: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt28 >= 1 ) break loop28;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(28, input);
                                        throw eee;
                                }
                                cnt28++;
                            } while (true);

                            match('s'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:100:79: ( ( '0' .. '9' )+ ( 'ms' )? )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( ((LA32_0>='0' && LA32_0<='9')) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:80: ( '0' .. '9' )+ ( 'ms' )?
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:100:80: ( '0' .. '9' )+
                            int cnt30=0;
                            loop30:
                            do {
                                int alt30=2;
                                int LA30_0 = input.LA(1);

                                if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                                    alt30=1;
                                }


                                switch (alt30) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:100:81: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt30 >= 1 ) break loop30;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(30, input);
                                        throw eee;
                                }
                                cnt30++;
                            } while (true);

                            // src/main/resources/org/drools/lang/DRLLexer.g:100:92: ( 'ms' )?
                            int alt31=2;
                            int LA31_0 = input.LA(1);

                            if ( (LA31_0=='m') ) {
                                alt31=1;
                            }
                            switch (alt31) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DRLLexer.g:100:92: 'ms'
                                    {
                                    match("ms"); if (state.failed) return ;


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:101:7: ( ( '0' .. '9' )+ 'h' ) ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:101:7: ( ( '0' .. '9' )+ 'h' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:101:8: ( '0' .. '9' )+ 'h'
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:101:8: ( '0' .. '9' )+
                    int cnt33=0;
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( ((LA33_0>='0' && LA33_0<='9')) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:101:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt33 >= 1 ) break loop33;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(33, input);
                                throw eee;
                        }
                        cnt33++;
                    } while (true);

                    match('h'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:101:25: ( ( '0' .. '9' )+ 'm' )?
                    int alt35=2;
                    alt35 = dfa35.predict(input);
                    switch (alt35) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:26: ( '0' .. '9' )+ 'm'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:26: ( '0' .. '9' )+
                            int cnt34=0;
                            loop34:
                            do {
                                int alt34=2;
                                int LA34_0 = input.LA(1);

                                if ( ((LA34_0>='0' && LA34_0<='9')) ) {
                                    alt34=1;
                                }


                                switch (alt34) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:101:27: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt34 >= 1 ) break loop34;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(34, input);
                                        throw eee;
                                }
                                cnt34++;
                            } while (true);

                            match('m'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:101:43: ( ( '0' .. '9' )+ 's' )?
                    int alt37=2;
                    alt37 = dfa37.predict(input);
                    switch (alt37) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:44: ( '0' .. '9' )+ 's'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:44: ( '0' .. '9' )+
                            int cnt36=0;
                            loop36:
                            do {
                                int alt36=2;
                                int LA36_0 = input.LA(1);

                                if ( ((LA36_0>='0' && LA36_0<='9')) ) {
                                    alt36=1;
                                }


                                switch (alt36) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:101:45: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt36 >= 1 ) break loop36;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(36, input);
                                        throw eee;
                                }
                                cnt36++;
                            } while (true);

                            match('s'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:101:61: ( ( '0' .. '9' )+ ( 'ms' )? )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( ((LA40_0>='0' && LA40_0<='9')) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:62: ( '0' .. '9' )+ ( 'ms' )?
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:101:62: ( '0' .. '9' )+
                            int cnt38=0;
                            loop38:
                            do {
                                int alt38=2;
                                int LA38_0 = input.LA(1);

                                if ( ((LA38_0>='0' && LA38_0<='9')) ) {
                                    alt38=1;
                                }


                                switch (alt38) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:101:63: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt38 >= 1 ) break loop38;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(38, input);
                                        throw eee;
                                }
                                cnt38++;
                            } while (true);

                            // src/main/resources/org/drools/lang/DRLLexer.g:101:74: ( 'ms' )?
                            int alt39=2;
                            int LA39_0 = input.LA(1);

                            if ( (LA39_0=='m') ) {
                                alt39=1;
                            }
                            switch (alt39) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DRLLexer.g:101:74: 'ms'
                                    {
                                    match("ms"); if (state.failed) return ;


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:102:7: ( ( '0' .. '9' )+ 'm' ) ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:102:7: ( ( '0' .. '9' )+ 'm' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:102:8: ( '0' .. '9' )+ 'm'
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:102:8: ( '0' .. '9' )+
                    int cnt41=0;
                    loop41:
                    do {
                        int alt41=2;
                        int LA41_0 = input.LA(1);

                        if ( ((LA41_0>='0' && LA41_0<='9')) ) {
                            alt41=1;
                        }


                        switch (alt41) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:102:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt41 >= 1 ) break loop41;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(41, input);
                                throw eee;
                        }
                        cnt41++;
                    } while (true);

                    match('m'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:102:25: ( ( '0' .. '9' )+ 's' )?
                    int alt43=2;
                    alt43 = dfa43.predict(input);
                    switch (alt43) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:102:26: ( '0' .. '9' )+ 's'
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:102:26: ( '0' .. '9' )+
                            int cnt42=0;
                            loop42:
                            do {
                                int alt42=2;
                                int LA42_0 = input.LA(1);

                                if ( ((LA42_0>='0' && LA42_0<='9')) ) {
                                    alt42=1;
                                }


                                switch (alt42) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:102:27: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

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

                            match('s'); if (state.failed) return ;

                            }
                            break;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:102:43: ( ( '0' .. '9' )+ ( 'ms' )? )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( ((LA46_0>='0' && LA46_0<='9')) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:102:44: ( '0' .. '9' )+ ( 'ms' )?
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:102:44: ( '0' .. '9' )+
                            int cnt44=0;
                            loop44:
                            do {
                                int alt44=2;
                                int LA44_0 = input.LA(1);

                                if ( ((LA44_0>='0' && LA44_0<='9')) ) {
                                    alt44=1;
                                }


                                switch (alt44) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:102:45: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt44 >= 1 ) break loop44;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(44, input);
                                        throw eee;
                                }
                                cnt44++;
                            } while (true);

                            // src/main/resources/org/drools/lang/DRLLexer.g:102:56: ( 'ms' )?
                            int alt45=2;
                            int LA45_0 = input.LA(1);

                            if ( (LA45_0=='m') ) {
                                alt45=1;
                            }
                            switch (alt45) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DRLLexer.g:102:56: 'ms'
                                    {
                                    match("ms"); if (state.failed) return ;


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:103:7: ( ( '0' .. '9' )+ 's' ) ( ( '0' .. '9' )+ ( 'ms' )? )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:103:7: ( ( '0' .. '9' )+ 's' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:103:8: ( '0' .. '9' )+ 's'
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:103:8: ( '0' .. '9' )+
                    int cnt47=0;
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( ((LA47_0>='0' && LA47_0<='9')) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:103:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

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

                    match('s'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:103:25: ( ( '0' .. '9' )+ ( 'ms' )? )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( ((LA50_0>='0' && LA50_0<='9')) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:103:26: ( '0' .. '9' )+ ( 'ms' )?
                            {
                            // src/main/resources/org/drools/lang/DRLLexer.g:103:26: ( '0' .. '9' )+
                            int cnt48=0;
                            loop48:
                            do {
                                int alt48=2;
                                int LA48_0 = input.LA(1);

                                if ( ((LA48_0>='0' && LA48_0<='9')) ) {
                                    alt48=1;
                                }


                                switch (alt48) {
                            	case 1 :
                            	    // src/main/resources/org/drools/lang/DRLLexer.g:103:27: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); if (state.failed) return ;

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt48 >= 1 ) break loop48;
                            	    if (state.backtracking>0) {state.failed=true; return ;}
                                        EarlyExitException eee =
                                            new EarlyExitException(48, input);
                                        throw eee;
                                }
                                cnt48++;
                            } while (true);

                            // src/main/resources/org/drools/lang/DRLLexer.g:103:38: ( 'ms' )?
                            int alt49=2;
                            int LA49_0 = input.LA(1);

                            if ( (LA49_0=='m') ) {
                                alt49=1;
                            }
                            switch (alt49) {
                                case 1 :
                                    // src/main/resources/org/drools/lang/DRLLexer.g:103:38: 'ms'
                                    {
                                    match("ms"); if (state.failed) return ;


                                    }
                                    break;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:104:7: ( ( '0' .. '9' )+ ( 'ms' )? )
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:104:7: ( ( '0' .. '9' )+ ( 'ms' )? )
                    // src/main/resources/org/drools/lang/DRLLexer.g:104:8: ( '0' .. '9' )+ ( 'ms' )?
                    {
                    // src/main/resources/org/drools/lang/DRLLexer.g:104:8: ( '0' .. '9' )+
                    int cnt51=0;
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( ((LA51_0>='0' && LA51_0<='9')) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:104:9: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); if (state.failed) return ;

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

                    // src/main/resources/org/drools/lang/DRLLexer.g:104:20: ( 'ms' )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0=='m') ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // src/main/resources/org/drools/lang/DRLLexer.g:104:20: 'ms'
                            {
                            match("ms"); if (state.failed) return ;


                            }
                            break;

                    }


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
    // $ANTLR end "TIME_INTERVAL"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLLexer.g:108:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // src/main/resources/org/drools/lang/DRLLexer.g:108:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // src/main/resources/org/drools/lang/DRLLexer.g:112:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
            int alt54=3;
            int LA54_0 = input.LA(1);

            if ( (LA54_0=='\\') ) {
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
                    alt54=1;
                    }
                    break;
                case 'u':
                    {
                    alt54=2;
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
                    alt54=3;
                    }
                    break;
                default:
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
                    // src/main/resources/org/drools/lang/DRLLexer.g:112:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
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
                    // src/main/resources/org/drools/lang/DRLLexer.g:116:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:117:9: OctalEscape
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
            // src/main/resources/org/drools/lang/DRLLexer.g:122:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt55=3;
            int LA55_0 = input.LA(1);

            if ( (LA55_0=='\\') ) {
                int LA55_1 = input.LA(2);

                if ( ((LA55_1>='0' && LA55_1<='3')) ) {
                    int LA55_2 = input.LA(3);

                    if ( ((LA55_2>='0' && LA55_2<='7')) ) {
                        int LA55_4 = input.LA(4);

                        if ( ((LA55_4>='0' && LA55_4<='7')) ) {
                            alt55=1;
                        }
                        else {
                            alt55=2;}
                    }
                    else {
                        alt55=3;}
                }
                else if ( ((LA55_1>='4' && LA55_1<='7')) ) {
                    int LA55_3 = input.LA(3);

                    if ( ((LA55_3>='0' && LA55_3<='7')) ) {
                        alt55=2;
                    }
                    else {
                        alt55=3;}
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 55, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:122:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:122:14: ( '0' .. '3' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:122:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:122:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:122:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:122:36: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:122:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:123:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:123:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:123:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }

                    // src/main/resources/org/drools/lang/DRLLexer.g:123:25: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:123:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (state.failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:124:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:124:14: ( '0' .. '7' )
                    // src/main/resources/org/drools/lang/DRLLexer.g:124:15: '0' .. '7'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:129:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // src/main/resources/org/drools/lang/DRLLexer.g:129:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // src/main/resources/org/drools/lang/DRLLexer.g:132:6: ( ( 'true' | 'false' ) )
            // src/main/resources/org/drools/lang/DRLLexer.g:132:8: ( 'true' | 'false' )
            {
            // src/main/resources/org/drools/lang/DRLLexer.g:132:8: ( 'true' | 'false' )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0=='t') ) {
                alt56=1;
            }
            else if ( (LA56_0=='f') ) {
                alt56=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:132:9: 'true'
                    {
                    match("true"); if (state.failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:132:16: 'false'
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

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:135:6: ( 'null' )
            // src/main/resources/org/drools/lang/DRLLexer.g:135:8: 'null'
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

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:138:4: ( '@' )
            // src/main/resources/org/drools/lang/DRLLexer.g:138:6: '@'
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

    // $ANTLR start "PLUS_ASSIGN"
    public final void mPLUS_ASSIGN() throws RecognitionException {
        try {
            int _type = PLUS_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:142:5: ( '+=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:142:7: '+='
            {
            match("+="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS_ASSIGN"

    // $ANTLR start "MINUS_ASSIGN"
    public final void mMINUS_ASSIGN() throws RecognitionException {
        try {
            int _type = MINUS_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:146:5: ( '-=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:146:7: '-='
            {
            match("-="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS_ASSIGN"

    // $ANTLR start "MULT_ASSIGN"
    public final void mMULT_ASSIGN() throws RecognitionException {
        try {
            int _type = MULT_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:150:5: ( '*=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:150:7: '*='
            {
            match("*="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULT_ASSIGN"

    // $ANTLR start "DIV_ASSIGN"
    public final void mDIV_ASSIGN() throws RecognitionException {
        try {
            int _type = DIV_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:154:5: ( '/=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:154:7: '/='
            {
            match("/="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV_ASSIGN"

    // $ANTLR start "AND_ASSIGN"
    public final void mAND_ASSIGN() throws RecognitionException {
        try {
            int _type = AND_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:158:5: ( '&=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:158:7: '&='
            {
            match("&="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND_ASSIGN"

    // $ANTLR start "OR_ASSIGN"
    public final void mOR_ASSIGN() throws RecognitionException {
        try {
            int _type = OR_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:162:5: ( '|=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:162:7: '|='
            {
            match("|="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR_ASSIGN"

    // $ANTLR start "XOR_ASSIGN"
    public final void mXOR_ASSIGN() throws RecognitionException {
        try {
            int _type = XOR_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:166:5: ( '^=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:166:7: '^='
            {
            match("^="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "XOR_ASSIGN"

    // $ANTLR start "MOD_ASSIGN"
    public final void mMOD_ASSIGN() throws RecognitionException {
        try {
            int _type = MOD_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:170:5: ( '%=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:170:7: '%='
            {
            match("%="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MOD_ASSIGN"

    // $ANTLR start "UNIFY"
    public final void mUNIFY() throws RecognitionException {
        try {
            int _type = UNIFY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:174:5: ( ':=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:174:7: ':='
            {
            match(":="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNIFY"

    // $ANTLR start "DECR"
    public final void mDECR() throws RecognitionException {
        try {
            int _type = DECR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:177:6: ( '--' )
            // src/main/resources/org/drools/lang/DRLLexer.g:177:8: '--'
            {
            match("--"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DECR"

    // $ANTLR start "INCR"
    public final void mINCR() throws RecognitionException {
        try {
            int _type = INCR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:180:6: ( '++' )
            // src/main/resources/org/drools/lang/DRLLexer.g:180:8: '++'
            {
            match("++"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INCR"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:184:5: ( '->' )
            // src/main/resources/org/drools/lang/DRLLexer.g:184:7: '->'
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

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:188:5: ( ';' )
            // src/main/resources/org/drools/lang/DRLLexer.g:188:7: ';'
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

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:192:5: ( ':' )
            // src/main/resources/org/drools/lang/DRLLexer.g:192:7: ':'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:196:5: ( '==' )
            // src/main/resources/org/drools/lang/DRLLexer.g:196:7: '=='
            {
            match("=="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "NOT_EQUALS"
    public final void mNOT_EQUALS() throws RecognitionException {
        try {
            int _type = NOT_EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:200:5: ( '!=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:200:7: '!='
            {
            match("!="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT_EQUALS"

    // $ANTLR start "GREATER_EQUALS"
    public final void mGREATER_EQUALS() throws RecognitionException {
        try {
            int _type = GREATER_EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:204:5: ( '>=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:204:7: '>='
            {
            match(">="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_EQUALS"

    // $ANTLR start "LESS_EQUALS"
    public final void mLESS_EQUALS() throws RecognitionException {
        try {
            int _type = LESS_EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:208:5: ( '<=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:208:7: '<='
            {
            match("<="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_EQUALS"

    // $ANTLR start "GREATER"
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:212:5: ( '>' )
            // src/main/resources/org/drools/lang/DRLLexer.g:212:7: '>'
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

    // $ANTLR start "LESS"
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:216:5: ( '<' )
            // src/main/resources/org/drools/lang/DRLLexer.g:216:7: '<'
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

    // $ANTLR start "EQUALS_ASSIGN"
    public final void mEQUALS_ASSIGN() throws RecognitionException {
        try {
            int _type = EQUALS_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:220:5: ( '=' )
            // src/main/resources/org/drools/lang/DRLLexer.g:220:7: '='
            {
            match('='); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS_ASSIGN"

    // $ANTLR start "LEFT_PAREN"
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:224:9: ( '(' )
            // src/main/resources/org/drools/lang/DRLLexer.g:224:11: '('
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
            // src/main/resources/org/drools/lang/DRLLexer.g:228:9: ( ')' )
            // src/main/resources/org/drools/lang/DRLLexer.g:228:11: ')'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:232:9: ( '[' )
            // src/main/resources/org/drools/lang/DRLLexer.g:232:11: '['
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
            // src/main/resources/org/drools/lang/DRLLexer.g:236:9: ( ']' )
            // src/main/resources/org/drools/lang/DRLLexer.g:236:11: ']'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:240:9: ( '{' )
            // src/main/resources/org/drools/lang/DRLLexer.g:240:11: '{'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:244:9: ( '}' )
            // src/main/resources/org/drools/lang/DRLLexer.g:244:11: '}'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:247:7: ( ',' )
            // src/main/resources/org/drools/lang/DRLLexer.g:247:9: ','
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
            // src/main/resources/org/drools/lang/DRLLexer.g:250:5: ( '.' )
            // src/main/resources/org/drools/lang/DRLLexer.g:250:7: '.'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:254:5: ( '&&' )
            // src/main/resources/org/drools/lang/DRLLexer.g:254:7: '&&'
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
            // src/main/resources/org/drools/lang/DRLLexer.g:258:5: ( '||' )
            // src/main/resources/org/drools/lang/DRLLexer.g:258:7: '||'
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

    // $ANTLR start "QUESTION"
    public final void mQUESTION() throws RecognitionException {
        try {
            int _type = QUESTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:262:5: ( '?' )
            // src/main/resources/org/drools/lang/DRLLexer.g:262:7: '?'
            {
            match('?'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUESTION"

    // $ANTLR start "NEGATION"
    public final void mNEGATION() throws RecognitionException {
        try {
            int _type = NEGATION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:266:5: ( '!' )
            // src/main/resources/org/drools/lang/DRLLexer.g:266:7: '!'
            {
            match('!'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEGATION"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:270:5: ( '~' )
            // src/main/resources/org/drools/lang/DRLLexer.g:270:7: '~'
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

    // $ANTLR start "PIPE"
    public final void mPIPE() throws RecognitionException {
        try {
            int _type = PIPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:274:5: ( '|' )
            // src/main/resources/org/drools/lang/DRLLexer.g:274:7: '|'
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

    // $ANTLR start "AMPER"
    public final void mAMPER() throws RecognitionException {
        try {
            int _type = AMPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:278:5: ( '&' )
            // src/main/resources/org/drools/lang/DRLLexer.g:278:7: '&'
            {
            match('&'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPER"

    // $ANTLR start "XOR"
    public final void mXOR() throws RecognitionException {
        try {
            int _type = XOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:282:5: ( '^' )
            // src/main/resources/org/drools/lang/DRLLexer.g:282:7: '^'
            {
            match('^'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "XOR"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:286:5: ( '%' )
            // src/main/resources/org/drools/lang/DRLLexer.g:286:7: '%'
            {
            match('%'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:289:6: ( '*' )
            // src/main/resources/org/drools/lang/DRLLexer.g:289:8: '*'
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

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:292:7: ( '-' )
            // src/main/resources/org/drools/lang/DRLLexer.g:292:9: '-'
            {
            match('-'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:295:6: ( '+' )
            // src/main/resources/org/drools/lang/DRLLexer.g:295:8: '+'
            {
            match('+'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "SH_STYLE_SINGLE_LINE_COMMENT"
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:299:5: ( '#' (~ ( '\\r' | '\\n' ) )* EOL )
            // src/main/resources/org/drools/lang/DRLLexer.g:299:7: '#' (~ ( '\\r' | '\\n' ) )* EOL
            {
            match('#'); if (state.failed) return ;
            // src/main/resources/org/drools/lang/DRLLexer.g:299:11: (~ ( '\\r' | '\\n' ) )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( ((LA57_0>='\u0000' && LA57_0<='\t')||(LA57_0>='\u000B' && LA57_0<='\f')||(LA57_0>='\u000E' && LA57_0<='\uFFFF')) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:299:12: ~ ( '\\r' | '\\n' )
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
            	    break loop57;
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
            // src/main/resources/org/drools/lang/DRLLexer.g:305:5: ( '//' (~ ( '\\r' | '\\n' ) )* EOL )
            // src/main/resources/org/drools/lang/DRLLexer.g:305:7: '//' (~ ( '\\r' | '\\n' ) )* EOL
            {
            match("//"); if (state.failed) return ;

            // src/main/resources/org/drools/lang/DRLLexer.g:305:12: (~ ( '\\r' | '\\n' ) )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( ((LA58_0>='\u0000' && LA58_0<='\t')||(LA58_0>='\u000B' && LA58_0<='\f')||(LA58_0>='\u000E' && LA58_0<='\uFFFF')) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:305:13: ~ ( '\\r' | '\\n' )
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
            	    break loop58;
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
            // src/main/resources/org/drools/lang/DRLLexer.g:310:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // src/main/resources/org/drools/lang/DRLLexer.g:310:7: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // src/main/resources/org/drools/lang/DRLLexer.g:310:12: ( options {greedy=false; } : . )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0=='*') ) {
                    int LA59_1 = input.LA(2);

                    if ( (LA59_1=='/') ) {
                        alt59=2;
                    }
                    else if ( ((LA59_1>='\u0000' && LA59_1<='.')||(LA59_1>='0' && LA59_1<='\uFFFF')) ) {
                        alt59=1;
                    }


                }
                else if ( ((LA59_0>='\u0000' && LA59_0<=')')||(LA59_0>='+' && LA59_0<='\uFFFF')) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/DRLLexer.g:310:38: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop59;
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
            // src/main/resources/org/drools/lang/DRLLexer.g:315:5: ( IdentifierStart ( IdentifierPart )* | '`' IdentifierStart ( IdentifierPart )* '`' )
            int alt62=2;
            int LA62_0 = input.LA(1);

            if ( (LA62_0=='$'||(LA62_0>='A' && LA62_0<='Z')||LA62_0=='_'||(LA62_0>='a' && LA62_0<='z')||(LA62_0>='\u00A2' && LA62_0<='\u00A5')||LA62_0=='\u00AA'||LA62_0=='\u00B5'||LA62_0=='\u00BA'||(LA62_0>='\u00C0' && LA62_0<='\u00D6')||(LA62_0>='\u00D8' && LA62_0<='\u00F6')||(LA62_0>='\u00F8' && LA62_0<='\u0236')||(LA62_0>='\u0250' && LA62_0<='\u02C1')||(LA62_0>='\u02C6' && LA62_0<='\u02D1')||(LA62_0>='\u02E0' && LA62_0<='\u02E4')||LA62_0=='\u02EE'||LA62_0=='\u037A'||LA62_0=='\u0386'||(LA62_0>='\u0388' && LA62_0<='\u038A')||LA62_0=='\u038C'||(LA62_0>='\u038E' && LA62_0<='\u03A1')||(LA62_0>='\u03A3' && LA62_0<='\u03CE')||(LA62_0>='\u03D0' && LA62_0<='\u03F5')||(LA62_0>='\u03F7' && LA62_0<='\u03FB')||(LA62_0>='\u0400' && LA62_0<='\u0481')||(LA62_0>='\u048A' && LA62_0<='\u04CE')||(LA62_0>='\u04D0' && LA62_0<='\u04F5')||(LA62_0>='\u04F8' && LA62_0<='\u04F9')||(LA62_0>='\u0500' && LA62_0<='\u050F')||(LA62_0>='\u0531' && LA62_0<='\u0556')||LA62_0=='\u0559'||(LA62_0>='\u0561' && LA62_0<='\u0587')||(LA62_0>='\u05D0' && LA62_0<='\u05EA')||(LA62_0>='\u05F0' && LA62_0<='\u05F2')||(LA62_0>='\u0621' && LA62_0<='\u063A')||(LA62_0>='\u0640' && LA62_0<='\u064A')||(LA62_0>='\u066E' && LA62_0<='\u066F')||(LA62_0>='\u0671' && LA62_0<='\u06D3')||LA62_0=='\u06D5'||(LA62_0>='\u06E5' && LA62_0<='\u06E6')||(LA62_0>='\u06EE' && LA62_0<='\u06EF')||(LA62_0>='\u06FA' && LA62_0<='\u06FC')||LA62_0=='\u06FF'||LA62_0=='\u0710'||(LA62_0>='\u0712' && LA62_0<='\u072F')||(LA62_0>='\u074D' && LA62_0<='\u074F')||(LA62_0>='\u0780' && LA62_0<='\u07A5')||LA62_0=='\u07B1'||(LA62_0>='\u0904' && LA62_0<='\u0939')||LA62_0=='\u093D'||LA62_0=='\u0950'||(LA62_0>='\u0958' && LA62_0<='\u0961')||(LA62_0>='\u0985' && LA62_0<='\u098C')||(LA62_0>='\u098F' && LA62_0<='\u0990')||(LA62_0>='\u0993' && LA62_0<='\u09A8')||(LA62_0>='\u09AA' && LA62_0<='\u09B0')||LA62_0=='\u09B2'||(LA62_0>='\u09B6' && LA62_0<='\u09B9')||LA62_0=='\u09BD'||(LA62_0>='\u09DC' && LA62_0<='\u09DD')||(LA62_0>='\u09DF' && LA62_0<='\u09E1')||(LA62_0>='\u09F0' && LA62_0<='\u09F3')||(LA62_0>='\u0A05' && LA62_0<='\u0A0A')||(LA62_0>='\u0A0F' && LA62_0<='\u0A10')||(LA62_0>='\u0A13' && LA62_0<='\u0A28')||(LA62_0>='\u0A2A' && LA62_0<='\u0A30')||(LA62_0>='\u0A32' && LA62_0<='\u0A33')||(LA62_0>='\u0A35' && LA62_0<='\u0A36')||(LA62_0>='\u0A38' && LA62_0<='\u0A39')||(LA62_0>='\u0A59' && LA62_0<='\u0A5C')||LA62_0=='\u0A5E'||(LA62_0>='\u0A72' && LA62_0<='\u0A74')||(LA62_0>='\u0A85' && LA62_0<='\u0A8D')||(LA62_0>='\u0A8F' && LA62_0<='\u0A91')||(LA62_0>='\u0A93' && LA62_0<='\u0AA8')||(LA62_0>='\u0AAA' && LA62_0<='\u0AB0')||(LA62_0>='\u0AB2' && LA62_0<='\u0AB3')||(LA62_0>='\u0AB5' && LA62_0<='\u0AB9')||LA62_0=='\u0ABD'||LA62_0=='\u0AD0'||(LA62_0>='\u0AE0' && LA62_0<='\u0AE1')||LA62_0=='\u0AF1'||(LA62_0>='\u0B05' && LA62_0<='\u0B0C')||(LA62_0>='\u0B0F' && LA62_0<='\u0B10')||(LA62_0>='\u0B13' && LA62_0<='\u0B28')||(LA62_0>='\u0B2A' && LA62_0<='\u0B30')||(LA62_0>='\u0B32' && LA62_0<='\u0B33')||(LA62_0>='\u0B35' && LA62_0<='\u0B39')||LA62_0=='\u0B3D'||(LA62_0>='\u0B5C' && LA62_0<='\u0B5D')||(LA62_0>='\u0B5F' && LA62_0<='\u0B61')||LA62_0=='\u0B71'||LA62_0=='\u0B83'||(LA62_0>='\u0B85' && LA62_0<='\u0B8A')||(LA62_0>='\u0B8E' && LA62_0<='\u0B90')||(LA62_0>='\u0B92' && LA62_0<='\u0B95')||(LA62_0>='\u0B99' && LA62_0<='\u0B9A')||LA62_0=='\u0B9C'||(LA62_0>='\u0B9E' && LA62_0<='\u0B9F')||(LA62_0>='\u0BA3' && LA62_0<='\u0BA4')||(LA62_0>='\u0BA8' && LA62_0<='\u0BAA')||(LA62_0>='\u0BAE' && LA62_0<='\u0BB5')||(LA62_0>='\u0BB7' && LA62_0<='\u0BB9')||LA62_0=='\u0BF9'||(LA62_0>='\u0C05' && LA62_0<='\u0C0C')||(LA62_0>='\u0C0E' && LA62_0<='\u0C10')||(LA62_0>='\u0C12' && LA62_0<='\u0C28')||(LA62_0>='\u0C2A' && LA62_0<='\u0C33')||(LA62_0>='\u0C35' && LA62_0<='\u0C39')||(LA62_0>='\u0C60' && LA62_0<='\u0C61')||(LA62_0>='\u0C85' && LA62_0<='\u0C8C')||(LA62_0>='\u0C8E' && LA62_0<='\u0C90')||(LA62_0>='\u0C92' && LA62_0<='\u0CA8')||(LA62_0>='\u0CAA' && LA62_0<='\u0CB3')||(LA62_0>='\u0CB5' && LA62_0<='\u0CB9')||LA62_0=='\u0CBD'||LA62_0=='\u0CDE'||(LA62_0>='\u0CE0' && LA62_0<='\u0CE1')||(LA62_0>='\u0D05' && LA62_0<='\u0D0C')||(LA62_0>='\u0D0E' && LA62_0<='\u0D10')||(LA62_0>='\u0D12' && LA62_0<='\u0D28')||(LA62_0>='\u0D2A' && LA62_0<='\u0D39')||(LA62_0>='\u0D60' && LA62_0<='\u0D61')||(LA62_0>='\u0D85' && LA62_0<='\u0D96')||(LA62_0>='\u0D9A' && LA62_0<='\u0DB1')||(LA62_0>='\u0DB3' && LA62_0<='\u0DBB')||LA62_0=='\u0DBD'||(LA62_0>='\u0DC0' && LA62_0<='\u0DC6')||(LA62_0>='\u0E01' && LA62_0<='\u0E30')||(LA62_0>='\u0E32' && LA62_0<='\u0E33')||(LA62_0>='\u0E3F' && LA62_0<='\u0E46')||(LA62_0>='\u0E81' && LA62_0<='\u0E82')||LA62_0=='\u0E84'||(LA62_0>='\u0E87' && LA62_0<='\u0E88')||LA62_0=='\u0E8A'||LA62_0=='\u0E8D'||(LA62_0>='\u0E94' && LA62_0<='\u0E97')||(LA62_0>='\u0E99' && LA62_0<='\u0E9F')||(LA62_0>='\u0EA1' && LA62_0<='\u0EA3')||LA62_0=='\u0EA5'||LA62_0=='\u0EA7'||(LA62_0>='\u0EAA' && LA62_0<='\u0EAB')||(LA62_0>='\u0EAD' && LA62_0<='\u0EB0')||(LA62_0>='\u0EB2' && LA62_0<='\u0EB3')||LA62_0=='\u0EBD'||(LA62_0>='\u0EC0' && LA62_0<='\u0EC4')||LA62_0=='\u0EC6'||(LA62_0>='\u0EDC' && LA62_0<='\u0EDD')||LA62_0=='\u0F00'||(LA62_0>='\u0F40' && LA62_0<='\u0F47')||(LA62_0>='\u0F49' && LA62_0<='\u0F6A')||(LA62_0>='\u0F88' && LA62_0<='\u0F8B')||(LA62_0>='\u1000' && LA62_0<='\u1021')||(LA62_0>='\u1023' && LA62_0<='\u1027')||(LA62_0>='\u1029' && LA62_0<='\u102A')||(LA62_0>='\u1050' && LA62_0<='\u1055')||(LA62_0>='\u10A0' && LA62_0<='\u10C5')||(LA62_0>='\u10D0' && LA62_0<='\u10F8')||(LA62_0>='\u1100' && LA62_0<='\u1159')||(LA62_0>='\u115F' && LA62_0<='\u11A2')||(LA62_0>='\u11A8' && LA62_0<='\u11F9')||(LA62_0>='\u1200' && LA62_0<='\u1206')||(LA62_0>='\u1208' && LA62_0<='\u1246')||LA62_0=='\u1248'||(LA62_0>='\u124A' && LA62_0<='\u124D')||(LA62_0>='\u1250' && LA62_0<='\u1256')||LA62_0=='\u1258'||(LA62_0>='\u125A' && LA62_0<='\u125D')||(LA62_0>='\u1260' && LA62_0<='\u1286')||LA62_0=='\u1288'||(LA62_0>='\u128A' && LA62_0<='\u128D')||(LA62_0>='\u1290' && LA62_0<='\u12AE')||LA62_0=='\u12B0'||(LA62_0>='\u12B2' && LA62_0<='\u12B5')||(LA62_0>='\u12B8' && LA62_0<='\u12BE')||LA62_0=='\u12C0'||(LA62_0>='\u12C2' && LA62_0<='\u12C5')||(LA62_0>='\u12C8' && LA62_0<='\u12CE')||(LA62_0>='\u12D0' && LA62_0<='\u12D6')||(LA62_0>='\u12D8' && LA62_0<='\u12EE')||(LA62_0>='\u12F0' && LA62_0<='\u130E')||LA62_0=='\u1310'||(LA62_0>='\u1312' && LA62_0<='\u1315')||(LA62_0>='\u1318' && LA62_0<='\u131E')||(LA62_0>='\u1320' && LA62_0<='\u1346')||(LA62_0>='\u1348' && LA62_0<='\u135A')||(LA62_0>='\u13A0' && LA62_0<='\u13F4')||(LA62_0>='\u1401' && LA62_0<='\u166C')||(LA62_0>='\u166F' && LA62_0<='\u1676')||(LA62_0>='\u1681' && LA62_0<='\u169A')||(LA62_0>='\u16A0' && LA62_0<='\u16EA')||(LA62_0>='\u16EE' && LA62_0<='\u16F0')||(LA62_0>='\u1700' && LA62_0<='\u170C')||(LA62_0>='\u170E' && LA62_0<='\u1711')||(LA62_0>='\u1720' && LA62_0<='\u1731')||(LA62_0>='\u1740' && LA62_0<='\u1751')||(LA62_0>='\u1760' && LA62_0<='\u176C')||(LA62_0>='\u176E' && LA62_0<='\u1770')||(LA62_0>='\u1780' && LA62_0<='\u17B3')||LA62_0=='\u17D7'||(LA62_0>='\u17DB' && LA62_0<='\u17DC')||(LA62_0>='\u1820' && LA62_0<='\u1877')||(LA62_0>='\u1880' && LA62_0<='\u18A8')||(LA62_0>='\u1900' && LA62_0<='\u191C')||(LA62_0>='\u1950' && LA62_0<='\u196D')||(LA62_0>='\u1970' && LA62_0<='\u1974')||(LA62_0>='\u1D00' && LA62_0<='\u1D6B')||(LA62_0>='\u1E00' && LA62_0<='\u1E9B')||(LA62_0>='\u1EA0' && LA62_0<='\u1EF9')||(LA62_0>='\u1F00' && LA62_0<='\u1F15')||(LA62_0>='\u1F18' && LA62_0<='\u1F1D')||(LA62_0>='\u1F20' && LA62_0<='\u1F45')||(LA62_0>='\u1F48' && LA62_0<='\u1F4D')||(LA62_0>='\u1F50' && LA62_0<='\u1F57')||LA62_0=='\u1F59'||LA62_0=='\u1F5B'||LA62_0=='\u1F5D'||(LA62_0>='\u1F5F' && LA62_0<='\u1F7D')||(LA62_0>='\u1F80' && LA62_0<='\u1FB4')||(LA62_0>='\u1FB6' && LA62_0<='\u1FBC')||LA62_0=='\u1FBE'||(LA62_0>='\u1FC2' && LA62_0<='\u1FC4')||(LA62_0>='\u1FC6' && LA62_0<='\u1FCC')||(LA62_0>='\u1FD0' && LA62_0<='\u1FD3')||(LA62_0>='\u1FD6' && LA62_0<='\u1FDB')||(LA62_0>='\u1FE0' && LA62_0<='\u1FEC')||(LA62_0>='\u1FF2' && LA62_0<='\u1FF4')||(LA62_0>='\u1FF6' && LA62_0<='\u1FFC')||(LA62_0>='\u203F' && LA62_0<='\u2040')||LA62_0=='\u2054'||LA62_0=='\u2071'||LA62_0=='\u207F'||(LA62_0>='\u20A0' && LA62_0<='\u20B1')||LA62_0=='\u2102'||LA62_0=='\u2107'||(LA62_0>='\u210A' && LA62_0<='\u2113')||LA62_0=='\u2115'||(LA62_0>='\u2119' && LA62_0<='\u211D')||LA62_0=='\u2124'||LA62_0=='\u2126'||LA62_0=='\u2128'||(LA62_0>='\u212A' && LA62_0<='\u212D')||(LA62_0>='\u212F' && LA62_0<='\u2131')||(LA62_0>='\u2133' && LA62_0<='\u2139')||(LA62_0>='\u213D' && LA62_0<='\u213F')||(LA62_0>='\u2145' && LA62_0<='\u2149')||(LA62_0>='\u2160' && LA62_0<='\u2183')||(LA62_0>='\u3005' && LA62_0<='\u3007')||(LA62_0>='\u3021' && LA62_0<='\u3029')||(LA62_0>='\u3031' && LA62_0<='\u3035')||(LA62_0>='\u3038' && LA62_0<='\u303C')||(LA62_0>='\u3041' && LA62_0<='\u3096')||(LA62_0>='\u309D' && LA62_0<='\u309F')||(LA62_0>='\u30A1' && LA62_0<='\u30FF')||(LA62_0>='\u3105' && LA62_0<='\u312C')||(LA62_0>='\u3131' && LA62_0<='\u318E')||(LA62_0>='\u31A0' && LA62_0<='\u31B7')||(LA62_0>='\u31F0' && LA62_0<='\u31FF')||(LA62_0>='\u3400' && LA62_0<='\u4DB5')||(LA62_0>='\u4E00' && LA62_0<='\u9FA5')||(LA62_0>='\uA000' && LA62_0<='\uA48C')||(LA62_0>='\uAC00' && LA62_0<='\uD7A3')||(LA62_0>='\uF900' && LA62_0<='\uFA2D')||(LA62_0>='\uFA30' && LA62_0<='\uFA6A')||(LA62_0>='\uFB00' && LA62_0<='\uFB06')||(LA62_0>='\uFB13' && LA62_0<='\uFB17')||LA62_0=='\uFB1D'||(LA62_0>='\uFB1F' && LA62_0<='\uFB28')||(LA62_0>='\uFB2A' && LA62_0<='\uFB36')||(LA62_0>='\uFB38' && LA62_0<='\uFB3C')||LA62_0=='\uFB3E'||(LA62_0>='\uFB40' && LA62_0<='\uFB41')||(LA62_0>='\uFB43' && LA62_0<='\uFB44')||(LA62_0>='\uFB46' && LA62_0<='\uFBB1')||(LA62_0>='\uFBD3' && LA62_0<='\uFD3D')||(LA62_0>='\uFD50' && LA62_0<='\uFD8F')||(LA62_0>='\uFD92' && LA62_0<='\uFDC7')||(LA62_0>='\uFDF0' && LA62_0<='\uFDFC')||(LA62_0>='\uFE33' && LA62_0<='\uFE34')||(LA62_0>='\uFE4D' && LA62_0<='\uFE4F')||LA62_0=='\uFE69'||(LA62_0>='\uFE70' && LA62_0<='\uFE74')||(LA62_0>='\uFE76' && LA62_0<='\uFEFC')||LA62_0=='\uFF04'||(LA62_0>='\uFF21' && LA62_0<='\uFF3A')||LA62_0=='\uFF3F'||(LA62_0>='\uFF41' && LA62_0<='\uFF5A')||(LA62_0>='\uFF65' && LA62_0<='\uFFBE')||(LA62_0>='\uFFC2' && LA62_0<='\uFFC7')||(LA62_0>='\uFFCA' && LA62_0<='\uFFCF')||(LA62_0>='\uFFD2' && LA62_0<='\uFFD7')||(LA62_0>='\uFFDA' && LA62_0<='\uFFDC')||(LA62_0>='\uFFE0' && LA62_0<='\uFFE1')||(LA62_0>='\uFFE5' && LA62_0<='\uFFE6')) ) {
                alt62=1;
            }
            else if ( (LA62_0=='`') ) {
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
                    // src/main/resources/org/drools/lang/DRLLexer.g:315:7: IdentifierStart ( IdentifierPart )*
                    {
                    mIdentifierStart(); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:315:23: ( IdentifierPart )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);

                        if ( ((LA60_0>='\u0000' && LA60_0<='\b')||(LA60_0>='\u000E' && LA60_0<='\u001B')||LA60_0=='$'||(LA60_0>='0' && LA60_0<='9')||(LA60_0>='A' && LA60_0<='Z')||LA60_0=='_'||(LA60_0>='a' && LA60_0<='z')||(LA60_0>='\u007F' && LA60_0<='\u009F')||(LA60_0>='\u00A2' && LA60_0<='\u00A5')||LA60_0=='\u00AA'||LA60_0=='\u00AD'||LA60_0=='\u00B5'||LA60_0=='\u00BA'||(LA60_0>='\u00C0' && LA60_0<='\u00D6')||(LA60_0>='\u00D8' && LA60_0<='\u00F6')||(LA60_0>='\u00F8' && LA60_0<='\u0236')||(LA60_0>='\u0250' && LA60_0<='\u02C1')||(LA60_0>='\u02C6' && LA60_0<='\u02D1')||(LA60_0>='\u02E0' && LA60_0<='\u02E4')||LA60_0=='\u02EE'||(LA60_0>='\u0300' && LA60_0<='\u0357')||(LA60_0>='\u035D' && LA60_0<='\u036F')||LA60_0=='\u037A'||LA60_0=='\u0386'||(LA60_0>='\u0388' && LA60_0<='\u038A')||LA60_0=='\u038C'||(LA60_0>='\u038E' && LA60_0<='\u03A1')||(LA60_0>='\u03A3' && LA60_0<='\u03CE')||(LA60_0>='\u03D0' && LA60_0<='\u03F5')||(LA60_0>='\u03F7' && LA60_0<='\u03FB')||(LA60_0>='\u0400' && LA60_0<='\u0481')||(LA60_0>='\u0483' && LA60_0<='\u0486')||(LA60_0>='\u048A' && LA60_0<='\u04CE')||(LA60_0>='\u04D0' && LA60_0<='\u04F5')||(LA60_0>='\u04F8' && LA60_0<='\u04F9')||(LA60_0>='\u0500' && LA60_0<='\u050F')||(LA60_0>='\u0531' && LA60_0<='\u0556')||LA60_0=='\u0559'||(LA60_0>='\u0561' && LA60_0<='\u0587')||(LA60_0>='\u0591' && LA60_0<='\u05A1')||(LA60_0>='\u05A3' && LA60_0<='\u05B9')||(LA60_0>='\u05BB' && LA60_0<='\u05BD')||LA60_0=='\u05BF'||(LA60_0>='\u05C1' && LA60_0<='\u05C2')||LA60_0=='\u05C4'||(LA60_0>='\u05D0' && LA60_0<='\u05EA')||(LA60_0>='\u05F0' && LA60_0<='\u05F2')||(LA60_0>='\u0600' && LA60_0<='\u0603')||(LA60_0>='\u0610' && LA60_0<='\u0615')||(LA60_0>='\u0621' && LA60_0<='\u063A')||(LA60_0>='\u0640' && LA60_0<='\u0658')||(LA60_0>='\u0660' && LA60_0<='\u0669')||(LA60_0>='\u066E' && LA60_0<='\u06D3')||(LA60_0>='\u06D5' && LA60_0<='\u06DD')||(LA60_0>='\u06DF' && LA60_0<='\u06E8')||(LA60_0>='\u06EA' && LA60_0<='\u06FC')||LA60_0=='\u06FF'||(LA60_0>='\u070F' && LA60_0<='\u074A')||(LA60_0>='\u074D' && LA60_0<='\u074F')||(LA60_0>='\u0780' && LA60_0<='\u07B1')||(LA60_0>='\u0901' && LA60_0<='\u0939')||(LA60_0>='\u093C' && LA60_0<='\u094D')||(LA60_0>='\u0950' && LA60_0<='\u0954')||(LA60_0>='\u0958' && LA60_0<='\u0963')||(LA60_0>='\u0966' && LA60_0<='\u096F')||(LA60_0>='\u0981' && LA60_0<='\u0983')||(LA60_0>='\u0985' && LA60_0<='\u098C')||(LA60_0>='\u098F' && LA60_0<='\u0990')||(LA60_0>='\u0993' && LA60_0<='\u09A8')||(LA60_0>='\u09AA' && LA60_0<='\u09B0')||LA60_0=='\u09B2'||(LA60_0>='\u09B6' && LA60_0<='\u09B9')||(LA60_0>='\u09BC' && LA60_0<='\u09C4')||(LA60_0>='\u09C7' && LA60_0<='\u09C8')||(LA60_0>='\u09CB' && LA60_0<='\u09CD')||LA60_0=='\u09D7'||(LA60_0>='\u09DC' && LA60_0<='\u09DD')||(LA60_0>='\u09DF' && LA60_0<='\u09E3')||(LA60_0>='\u09E6' && LA60_0<='\u09F3')||(LA60_0>='\u0A01' && LA60_0<='\u0A03')||(LA60_0>='\u0A05' && LA60_0<='\u0A0A')||(LA60_0>='\u0A0F' && LA60_0<='\u0A10')||(LA60_0>='\u0A13' && LA60_0<='\u0A28')||(LA60_0>='\u0A2A' && LA60_0<='\u0A30')||(LA60_0>='\u0A32' && LA60_0<='\u0A33')||(LA60_0>='\u0A35' && LA60_0<='\u0A36')||(LA60_0>='\u0A38' && LA60_0<='\u0A39')||LA60_0=='\u0A3C'||(LA60_0>='\u0A3E' && LA60_0<='\u0A42')||(LA60_0>='\u0A47' && LA60_0<='\u0A48')||(LA60_0>='\u0A4B' && LA60_0<='\u0A4D')||(LA60_0>='\u0A59' && LA60_0<='\u0A5C')||LA60_0=='\u0A5E'||(LA60_0>='\u0A66' && LA60_0<='\u0A74')||(LA60_0>='\u0A81' && LA60_0<='\u0A83')||(LA60_0>='\u0A85' && LA60_0<='\u0A8D')||(LA60_0>='\u0A8F' && LA60_0<='\u0A91')||(LA60_0>='\u0A93' && LA60_0<='\u0AA8')||(LA60_0>='\u0AAA' && LA60_0<='\u0AB0')||(LA60_0>='\u0AB2' && LA60_0<='\u0AB3')||(LA60_0>='\u0AB5' && LA60_0<='\u0AB9')||(LA60_0>='\u0ABC' && LA60_0<='\u0AC5')||(LA60_0>='\u0AC7' && LA60_0<='\u0AC9')||(LA60_0>='\u0ACB' && LA60_0<='\u0ACD')||LA60_0=='\u0AD0'||(LA60_0>='\u0AE0' && LA60_0<='\u0AE3')||(LA60_0>='\u0AE6' && LA60_0<='\u0AEF')||LA60_0=='\u0AF1'||(LA60_0>='\u0B01' && LA60_0<='\u0B03')||(LA60_0>='\u0B05' && LA60_0<='\u0B0C')||(LA60_0>='\u0B0F' && LA60_0<='\u0B10')||(LA60_0>='\u0B13' && LA60_0<='\u0B28')||(LA60_0>='\u0B2A' && LA60_0<='\u0B30')||(LA60_0>='\u0B32' && LA60_0<='\u0B33')||(LA60_0>='\u0B35' && LA60_0<='\u0B39')||(LA60_0>='\u0B3C' && LA60_0<='\u0B43')||(LA60_0>='\u0B47' && LA60_0<='\u0B48')||(LA60_0>='\u0B4B' && LA60_0<='\u0B4D')||(LA60_0>='\u0B56' && LA60_0<='\u0B57')||(LA60_0>='\u0B5C' && LA60_0<='\u0B5D')||(LA60_0>='\u0B5F' && LA60_0<='\u0B61')||(LA60_0>='\u0B66' && LA60_0<='\u0B6F')||LA60_0=='\u0B71'||(LA60_0>='\u0B82' && LA60_0<='\u0B83')||(LA60_0>='\u0B85' && LA60_0<='\u0B8A')||(LA60_0>='\u0B8E' && LA60_0<='\u0B90')||(LA60_0>='\u0B92' && LA60_0<='\u0B95')||(LA60_0>='\u0B99' && LA60_0<='\u0B9A')||LA60_0=='\u0B9C'||(LA60_0>='\u0B9E' && LA60_0<='\u0B9F')||(LA60_0>='\u0BA3' && LA60_0<='\u0BA4')||(LA60_0>='\u0BA8' && LA60_0<='\u0BAA')||(LA60_0>='\u0BAE' && LA60_0<='\u0BB5')||(LA60_0>='\u0BB7' && LA60_0<='\u0BB9')||(LA60_0>='\u0BBE' && LA60_0<='\u0BC2')||(LA60_0>='\u0BC6' && LA60_0<='\u0BC8')||(LA60_0>='\u0BCA' && LA60_0<='\u0BCD')||LA60_0=='\u0BD7'||(LA60_0>='\u0BE7' && LA60_0<='\u0BEF')||LA60_0=='\u0BF9'||(LA60_0>='\u0C01' && LA60_0<='\u0C03')||(LA60_0>='\u0C05' && LA60_0<='\u0C0C')||(LA60_0>='\u0C0E' && LA60_0<='\u0C10')||(LA60_0>='\u0C12' && LA60_0<='\u0C28')||(LA60_0>='\u0C2A' && LA60_0<='\u0C33')||(LA60_0>='\u0C35' && LA60_0<='\u0C39')||(LA60_0>='\u0C3E' && LA60_0<='\u0C44')||(LA60_0>='\u0C46' && LA60_0<='\u0C48')||(LA60_0>='\u0C4A' && LA60_0<='\u0C4D')||(LA60_0>='\u0C55' && LA60_0<='\u0C56')||(LA60_0>='\u0C60' && LA60_0<='\u0C61')||(LA60_0>='\u0C66' && LA60_0<='\u0C6F')||(LA60_0>='\u0C82' && LA60_0<='\u0C83')||(LA60_0>='\u0C85' && LA60_0<='\u0C8C')||(LA60_0>='\u0C8E' && LA60_0<='\u0C90')||(LA60_0>='\u0C92' && LA60_0<='\u0CA8')||(LA60_0>='\u0CAA' && LA60_0<='\u0CB3')||(LA60_0>='\u0CB5' && LA60_0<='\u0CB9')||(LA60_0>='\u0CBC' && LA60_0<='\u0CC4')||(LA60_0>='\u0CC6' && LA60_0<='\u0CC8')||(LA60_0>='\u0CCA' && LA60_0<='\u0CCD')||(LA60_0>='\u0CD5' && LA60_0<='\u0CD6')||LA60_0=='\u0CDE'||(LA60_0>='\u0CE0' && LA60_0<='\u0CE1')||(LA60_0>='\u0CE6' && LA60_0<='\u0CEF')||(LA60_0>='\u0D02' && LA60_0<='\u0D03')||(LA60_0>='\u0D05' && LA60_0<='\u0D0C')||(LA60_0>='\u0D0E' && LA60_0<='\u0D10')||(LA60_0>='\u0D12' && LA60_0<='\u0D28')||(LA60_0>='\u0D2A' && LA60_0<='\u0D39')||(LA60_0>='\u0D3E' && LA60_0<='\u0D43')||(LA60_0>='\u0D46' && LA60_0<='\u0D48')||(LA60_0>='\u0D4A' && LA60_0<='\u0D4D')||LA60_0=='\u0D57'||(LA60_0>='\u0D60' && LA60_0<='\u0D61')||(LA60_0>='\u0D66' && LA60_0<='\u0D6F')||(LA60_0>='\u0D82' && LA60_0<='\u0D83')||(LA60_0>='\u0D85' && LA60_0<='\u0D96')||(LA60_0>='\u0D9A' && LA60_0<='\u0DB1')||(LA60_0>='\u0DB3' && LA60_0<='\u0DBB')||LA60_0=='\u0DBD'||(LA60_0>='\u0DC0' && LA60_0<='\u0DC6')||LA60_0=='\u0DCA'||(LA60_0>='\u0DCF' && LA60_0<='\u0DD4')||LA60_0=='\u0DD6'||(LA60_0>='\u0DD8' && LA60_0<='\u0DDF')||(LA60_0>='\u0DF2' && LA60_0<='\u0DF3')||(LA60_0>='\u0E01' && LA60_0<='\u0E3A')||(LA60_0>='\u0E3F' && LA60_0<='\u0E4E')||(LA60_0>='\u0E50' && LA60_0<='\u0E59')||(LA60_0>='\u0E81' && LA60_0<='\u0E82')||LA60_0=='\u0E84'||(LA60_0>='\u0E87' && LA60_0<='\u0E88')||LA60_0=='\u0E8A'||LA60_0=='\u0E8D'||(LA60_0>='\u0E94' && LA60_0<='\u0E97')||(LA60_0>='\u0E99' && LA60_0<='\u0E9F')||(LA60_0>='\u0EA1' && LA60_0<='\u0EA3')||LA60_0=='\u0EA5'||LA60_0=='\u0EA7'||(LA60_0>='\u0EAA' && LA60_0<='\u0EAB')||(LA60_0>='\u0EAD' && LA60_0<='\u0EB9')||(LA60_0>='\u0EBB' && LA60_0<='\u0EBD')||(LA60_0>='\u0EC0' && LA60_0<='\u0EC4')||LA60_0=='\u0EC6'||(LA60_0>='\u0EC8' && LA60_0<='\u0ECD')||(LA60_0>='\u0ED0' && LA60_0<='\u0ED9')||(LA60_0>='\u0EDC' && LA60_0<='\u0EDD')||LA60_0=='\u0F00'||(LA60_0>='\u0F18' && LA60_0<='\u0F19')||(LA60_0>='\u0F20' && LA60_0<='\u0F29')||LA60_0=='\u0F35'||LA60_0=='\u0F37'||LA60_0=='\u0F39'||(LA60_0>='\u0F3E' && LA60_0<='\u0F47')||(LA60_0>='\u0F49' && LA60_0<='\u0F6A')||(LA60_0>='\u0F71' && LA60_0<='\u0F84')||(LA60_0>='\u0F86' && LA60_0<='\u0F8B')||(LA60_0>='\u0F90' && LA60_0<='\u0F97')||(LA60_0>='\u0F99' && LA60_0<='\u0FBC')||LA60_0=='\u0FC6'||(LA60_0>='\u1000' && LA60_0<='\u1021')||(LA60_0>='\u1023' && LA60_0<='\u1027')||(LA60_0>='\u1029' && LA60_0<='\u102A')||(LA60_0>='\u102C' && LA60_0<='\u1032')||(LA60_0>='\u1036' && LA60_0<='\u1039')||(LA60_0>='\u1040' && LA60_0<='\u1049')||(LA60_0>='\u1050' && LA60_0<='\u1059')||(LA60_0>='\u10A0' && LA60_0<='\u10C5')||(LA60_0>='\u10D0' && LA60_0<='\u10F8')||(LA60_0>='\u1100' && LA60_0<='\u1159')||(LA60_0>='\u115F' && LA60_0<='\u11A2')||(LA60_0>='\u11A8' && LA60_0<='\u11F9')||(LA60_0>='\u1200' && LA60_0<='\u1206')||(LA60_0>='\u1208' && LA60_0<='\u1246')||LA60_0=='\u1248'||(LA60_0>='\u124A' && LA60_0<='\u124D')||(LA60_0>='\u1250' && LA60_0<='\u1256')||LA60_0=='\u1258'||(LA60_0>='\u125A' && LA60_0<='\u125D')||(LA60_0>='\u1260' && LA60_0<='\u1286')||LA60_0=='\u1288'||(LA60_0>='\u128A' && LA60_0<='\u128D')||(LA60_0>='\u1290' && LA60_0<='\u12AE')||LA60_0=='\u12B0'||(LA60_0>='\u12B2' && LA60_0<='\u12B5')||(LA60_0>='\u12B8' && LA60_0<='\u12BE')||LA60_0=='\u12C0'||(LA60_0>='\u12C2' && LA60_0<='\u12C5')||(LA60_0>='\u12C8' && LA60_0<='\u12CE')||(LA60_0>='\u12D0' && LA60_0<='\u12D6')||(LA60_0>='\u12D8' && LA60_0<='\u12EE')||(LA60_0>='\u12F0' && LA60_0<='\u130E')||LA60_0=='\u1310'||(LA60_0>='\u1312' && LA60_0<='\u1315')||(LA60_0>='\u1318' && LA60_0<='\u131E')||(LA60_0>='\u1320' && LA60_0<='\u1346')||(LA60_0>='\u1348' && LA60_0<='\u135A')||(LA60_0>='\u1369' && LA60_0<='\u1371')||(LA60_0>='\u13A0' && LA60_0<='\u13F4')||(LA60_0>='\u1401' && LA60_0<='\u166C')||(LA60_0>='\u166F' && LA60_0<='\u1676')||(LA60_0>='\u1681' && LA60_0<='\u169A')||(LA60_0>='\u16A0' && LA60_0<='\u16EA')||(LA60_0>='\u16EE' && LA60_0<='\u16F0')||(LA60_0>='\u1700' && LA60_0<='\u170C')||(LA60_0>='\u170E' && LA60_0<='\u1714')||(LA60_0>='\u1720' && LA60_0<='\u1734')||(LA60_0>='\u1740' && LA60_0<='\u1753')||(LA60_0>='\u1760' && LA60_0<='\u176C')||(LA60_0>='\u176E' && LA60_0<='\u1770')||(LA60_0>='\u1772' && LA60_0<='\u1773')||(LA60_0>='\u1780' && LA60_0<='\u17D3')||LA60_0=='\u17D7'||(LA60_0>='\u17DB' && LA60_0<='\u17DD')||(LA60_0>='\u17E0' && LA60_0<='\u17E9')||(LA60_0>='\u180B' && LA60_0<='\u180D')||(LA60_0>='\u1810' && LA60_0<='\u1819')||(LA60_0>='\u1820' && LA60_0<='\u1877')||(LA60_0>='\u1880' && LA60_0<='\u18A9')||(LA60_0>='\u1900' && LA60_0<='\u191C')||(LA60_0>='\u1920' && LA60_0<='\u192B')||(LA60_0>='\u1930' && LA60_0<='\u193B')||(LA60_0>='\u1946' && LA60_0<='\u196D')||(LA60_0>='\u1970' && LA60_0<='\u1974')||(LA60_0>='\u1D00' && LA60_0<='\u1D6B')||(LA60_0>='\u1E00' && LA60_0<='\u1E9B')||(LA60_0>='\u1EA0' && LA60_0<='\u1EF9')||(LA60_0>='\u1F00' && LA60_0<='\u1F15')||(LA60_0>='\u1F18' && LA60_0<='\u1F1D')||(LA60_0>='\u1F20' && LA60_0<='\u1F45')||(LA60_0>='\u1F48' && LA60_0<='\u1F4D')||(LA60_0>='\u1F50' && LA60_0<='\u1F57')||LA60_0=='\u1F59'||LA60_0=='\u1F5B'||LA60_0=='\u1F5D'||(LA60_0>='\u1F5F' && LA60_0<='\u1F7D')||(LA60_0>='\u1F80' && LA60_0<='\u1FB4')||(LA60_0>='\u1FB6' && LA60_0<='\u1FBC')||LA60_0=='\u1FBE'||(LA60_0>='\u1FC2' && LA60_0<='\u1FC4')||(LA60_0>='\u1FC6' && LA60_0<='\u1FCC')||(LA60_0>='\u1FD0' && LA60_0<='\u1FD3')||(LA60_0>='\u1FD6' && LA60_0<='\u1FDB')||(LA60_0>='\u1FE0' && LA60_0<='\u1FEC')||(LA60_0>='\u1FF2' && LA60_0<='\u1FF4')||(LA60_0>='\u1FF6' && LA60_0<='\u1FFC')||(LA60_0>='\u200C' && LA60_0<='\u200F')||(LA60_0>='\u202A' && LA60_0<='\u202E')||(LA60_0>='\u203F' && LA60_0<='\u2040')||LA60_0=='\u2054'||(LA60_0>='\u2060' && LA60_0<='\u2063')||(LA60_0>='\u206A' && LA60_0<='\u206F')||LA60_0=='\u2071'||LA60_0=='\u207F'||(LA60_0>='\u20A0' && LA60_0<='\u20B1')||(LA60_0>='\u20D0' && LA60_0<='\u20DC')||LA60_0=='\u20E1'||(LA60_0>='\u20E5' && LA60_0<='\u20EA')||LA60_0=='\u2102'||LA60_0=='\u2107'||(LA60_0>='\u210A' && LA60_0<='\u2113')||LA60_0=='\u2115'||(LA60_0>='\u2119' && LA60_0<='\u211D')||LA60_0=='\u2124'||LA60_0=='\u2126'||LA60_0=='\u2128'||(LA60_0>='\u212A' && LA60_0<='\u212D')||(LA60_0>='\u212F' && LA60_0<='\u2131')||(LA60_0>='\u2133' && LA60_0<='\u2139')||(LA60_0>='\u213D' && LA60_0<='\u213F')||(LA60_0>='\u2145' && LA60_0<='\u2149')||(LA60_0>='\u2160' && LA60_0<='\u2183')||(LA60_0>='\u3005' && LA60_0<='\u3007')||(LA60_0>='\u3021' && LA60_0<='\u302F')||(LA60_0>='\u3031' && LA60_0<='\u3035')||(LA60_0>='\u3038' && LA60_0<='\u303C')||(LA60_0>='\u3041' && LA60_0<='\u3096')||(LA60_0>='\u3099' && LA60_0<='\u309A')||(LA60_0>='\u309D' && LA60_0<='\u309F')||(LA60_0>='\u30A1' && LA60_0<='\u30FF')||(LA60_0>='\u3105' && LA60_0<='\u312C')||(LA60_0>='\u3131' && LA60_0<='\u318E')||(LA60_0>='\u31A0' && LA60_0<='\u31B7')||(LA60_0>='\u31F0' && LA60_0<='\u31FF')||(LA60_0>='\u3400' && LA60_0<='\u4DB5')||(LA60_0>='\u4E00' && LA60_0<='\u9FA5')||(LA60_0>='\uA000' && LA60_0<='\uA48C')||(LA60_0>='\uAC00' && LA60_0<='\uD7A3')||(LA60_0>='\uF900' && LA60_0<='\uFA2D')||(LA60_0>='\uFA30' && LA60_0<='\uFA6A')||(LA60_0>='\uFB00' && LA60_0<='\uFB06')||(LA60_0>='\uFB13' && LA60_0<='\uFB17')||(LA60_0>='\uFB1D' && LA60_0<='\uFB28')||(LA60_0>='\uFB2A' && LA60_0<='\uFB36')||(LA60_0>='\uFB38' && LA60_0<='\uFB3C')||LA60_0=='\uFB3E'||(LA60_0>='\uFB40' && LA60_0<='\uFB41')||(LA60_0>='\uFB43' && LA60_0<='\uFB44')||(LA60_0>='\uFB46' && LA60_0<='\uFBB1')||(LA60_0>='\uFBD3' && LA60_0<='\uFD3D')||(LA60_0>='\uFD50' && LA60_0<='\uFD8F')||(LA60_0>='\uFD92' && LA60_0<='\uFDC7')||(LA60_0>='\uFDF0' && LA60_0<='\uFDFC')||(LA60_0>='\uFE00' && LA60_0<='\uFE0F')||(LA60_0>='\uFE20' && LA60_0<='\uFE23')||(LA60_0>='\uFE33' && LA60_0<='\uFE34')||(LA60_0>='\uFE4D' && LA60_0<='\uFE4F')||LA60_0=='\uFE69'||(LA60_0>='\uFE70' && LA60_0<='\uFE74')||(LA60_0>='\uFE76' && LA60_0<='\uFEFC')||LA60_0=='\uFEFF'||LA60_0=='\uFF04'||(LA60_0>='\uFF10' && LA60_0<='\uFF19')||(LA60_0>='\uFF21' && LA60_0<='\uFF3A')||LA60_0=='\uFF3F'||(LA60_0>='\uFF41' && LA60_0<='\uFF5A')||(LA60_0>='\uFF65' && LA60_0<='\uFFBE')||(LA60_0>='\uFFC2' && LA60_0<='\uFFC7')||(LA60_0>='\uFFCA' && LA60_0<='\uFFCF')||(LA60_0>='\uFFD2' && LA60_0<='\uFFD7')||(LA60_0>='\uFFDA' && LA60_0<='\uFFDC')||(LA60_0>='\uFFE0' && LA60_0<='\uFFE1')||(LA60_0>='\uFFE5' && LA60_0<='\uFFE6')||(LA60_0>='\uFFF9' && LA60_0<='\uFFFB')) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:315:23: IdentifierPart
                    	    {
                    	    mIdentifierPart(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/DRLLexer.g:316:7: '`' IdentifierStart ( IdentifierPart )* '`'
                    {
                    match('`'); if (state.failed) return ;
                    mIdentifierStart(); if (state.failed) return ;
                    // src/main/resources/org/drools/lang/DRLLexer.g:316:27: ( IdentifierPart )*
                    loop61:
                    do {
                        int alt61=2;
                        int LA61_0 = input.LA(1);

                        if ( ((LA61_0>='\u0000' && LA61_0<='\b')||(LA61_0>='\u000E' && LA61_0<='\u001B')||LA61_0=='$'||(LA61_0>='0' && LA61_0<='9')||(LA61_0>='A' && LA61_0<='Z')||LA61_0=='_'||(LA61_0>='a' && LA61_0<='z')||(LA61_0>='\u007F' && LA61_0<='\u009F')||(LA61_0>='\u00A2' && LA61_0<='\u00A5')||LA61_0=='\u00AA'||LA61_0=='\u00AD'||LA61_0=='\u00B5'||LA61_0=='\u00BA'||(LA61_0>='\u00C0' && LA61_0<='\u00D6')||(LA61_0>='\u00D8' && LA61_0<='\u00F6')||(LA61_0>='\u00F8' && LA61_0<='\u0236')||(LA61_0>='\u0250' && LA61_0<='\u02C1')||(LA61_0>='\u02C6' && LA61_0<='\u02D1')||(LA61_0>='\u02E0' && LA61_0<='\u02E4')||LA61_0=='\u02EE'||(LA61_0>='\u0300' && LA61_0<='\u0357')||(LA61_0>='\u035D' && LA61_0<='\u036F')||LA61_0=='\u037A'||LA61_0=='\u0386'||(LA61_0>='\u0388' && LA61_0<='\u038A')||LA61_0=='\u038C'||(LA61_0>='\u038E' && LA61_0<='\u03A1')||(LA61_0>='\u03A3' && LA61_0<='\u03CE')||(LA61_0>='\u03D0' && LA61_0<='\u03F5')||(LA61_0>='\u03F7' && LA61_0<='\u03FB')||(LA61_0>='\u0400' && LA61_0<='\u0481')||(LA61_0>='\u0483' && LA61_0<='\u0486')||(LA61_0>='\u048A' && LA61_0<='\u04CE')||(LA61_0>='\u04D0' && LA61_0<='\u04F5')||(LA61_0>='\u04F8' && LA61_0<='\u04F9')||(LA61_0>='\u0500' && LA61_0<='\u050F')||(LA61_0>='\u0531' && LA61_0<='\u0556')||LA61_0=='\u0559'||(LA61_0>='\u0561' && LA61_0<='\u0587')||(LA61_0>='\u0591' && LA61_0<='\u05A1')||(LA61_0>='\u05A3' && LA61_0<='\u05B9')||(LA61_0>='\u05BB' && LA61_0<='\u05BD')||LA61_0=='\u05BF'||(LA61_0>='\u05C1' && LA61_0<='\u05C2')||LA61_0=='\u05C4'||(LA61_0>='\u05D0' && LA61_0<='\u05EA')||(LA61_0>='\u05F0' && LA61_0<='\u05F2')||(LA61_0>='\u0600' && LA61_0<='\u0603')||(LA61_0>='\u0610' && LA61_0<='\u0615')||(LA61_0>='\u0621' && LA61_0<='\u063A')||(LA61_0>='\u0640' && LA61_0<='\u0658')||(LA61_0>='\u0660' && LA61_0<='\u0669')||(LA61_0>='\u066E' && LA61_0<='\u06D3')||(LA61_0>='\u06D5' && LA61_0<='\u06DD')||(LA61_0>='\u06DF' && LA61_0<='\u06E8')||(LA61_0>='\u06EA' && LA61_0<='\u06FC')||LA61_0=='\u06FF'||(LA61_0>='\u070F' && LA61_0<='\u074A')||(LA61_0>='\u074D' && LA61_0<='\u074F')||(LA61_0>='\u0780' && LA61_0<='\u07B1')||(LA61_0>='\u0901' && LA61_0<='\u0939')||(LA61_0>='\u093C' && LA61_0<='\u094D')||(LA61_0>='\u0950' && LA61_0<='\u0954')||(LA61_0>='\u0958' && LA61_0<='\u0963')||(LA61_0>='\u0966' && LA61_0<='\u096F')||(LA61_0>='\u0981' && LA61_0<='\u0983')||(LA61_0>='\u0985' && LA61_0<='\u098C')||(LA61_0>='\u098F' && LA61_0<='\u0990')||(LA61_0>='\u0993' && LA61_0<='\u09A8')||(LA61_0>='\u09AA' && LA61_0<='\u09B0')||LA61_0=='\u09B2'||(LA61_0>='\u09B6' && LA61_0<='\u09B9')||(LA61_0>='\u09BC' && LA61_0<='\u09C4')||(LA61_0>='\u09C7' && LA61_0<='\u09C8')||(LA61_0>='\u09CB' && LA61_0<='\u09CD')||LA61_0=='\u09D7'||(LA61_0>='\u09DC' && LA61_0<='\u09DD')||(LA61_0>='\u09DF' && LA61_0<='\u09E3')||(LA61_0>='\u09E6' && LA61_0<='\u09F3')||(LA61_0>='\u0A01' && LA61_0<='\u0A03')||(LA61_0>='\u0A05' && LA61_0<='\u0A0A')||(LA61_0>='\u0A0F' && LA61_0<='\u0A10')||(LA61_0>='\u0A13' && LA61_0<='\u0A28')||(LA61_0>='\u0A2A' && LA61_0<='\u0A30')||(LA61_0>='\u0A32' && LA61_0<='\u0A33')||(LA61_0>='\u0A35' && LA61_0<='\u0A36')||(LA61_0>='\u0A38' && LA61_0<='\u0A39')||LA61_0=='\u0A3C'||(LA61_0>='\u0A3E' && LA61_0<='\u0A42')||(LA61_0>='\u0A47' && LA61_0<='\u0A48')||(LA61_0>='\u0A4B' && LA61_0<='\u0A4D')||(LA61_0>='\u0A59' && LA61_0<='\u0A5C')||LA61_0=='\u0A5E'||(LA61_0>='\u0A66' && LA61_0<='\u0A74')||(LA61_0>='\u0A81' && LA61_0<='\u0A83')||(LA61_0>='\u0A85' && LA61_0<='\u0A8D')||(LA61_0>='\u0A8F' && LA61_0<='\u0A91')||(LA61_0>='\u0A93' && LA61_0<='\u0AA8')||(LA61_0>='\u0AAA' && LA61_0<='\u0AB0')||(LA61_0>='\u0AB2' && LA61_0<='\u0AB3')||(LA61_0>='\u0AB5' && LA61_0<='\u0AB9')||(LA61_0>='\u0ABC' && LA61_0<='\u0AC5')||(LA61_0>='\u0AC7' && LA61_0<='\u0AC9')||(LA61_0>='\u0ACB' && LA61_0<='\u0ACD')||LA61_0=='\u0AD0'||(LA61_0>='\u0AE0' && LA61_0<='\u0AE3')||(LA61_0>='\u0AE6' && LA61_0<='\u0AEF')||LA61_0=='\u0AF1'||(LA61_0>='\u0B01' && LA61_0<='\u0B03')||(LA61_0>='\u0B05' && LA61_0<='\u0B0C')||(LA61_0>='\u0B0F' && LA61_0<='\u0B10')||(LA61_0>='\u0B13' && LA61_0<='\u0B28')||(LA61_0>='\u0B2A' && LA61_0<='\u0B30')||(LA61_0>='\u0B32' && LA61_0<='\u0B33')||(LA61_0>='\u0B35' && LA61_0<='\u0B39')||(LA61_0>='\u0B3C' && LA61_0<='\u0B43')||(LA61_0>='\u0B47' && LA61_0<='\u0B48')||(LA61_0>='\u0B4B' && LA61_0<='\u0B4D')||(LA61_0>='\u0B56' && LA61_0<='\u0B57')||(LA61_0>='\u0B5C' && LA61_0<='\u0B5D')||(LA61_0>='\u0B5F' && LA61_0<='\u0B61')||(LA61_0>='\u0B66' && LA61_0<='\u0B6F')||LA61_0=='\u0B71'||(LA61_0>='\u0B82' && LA61_0<='\u0B83')||(LA61_0>='\u0B85' && LA61_0<='\u0B8A')||(LA61_0>='\u0B8E' && LA61_0<='\u0B90')||(LA61_0>='\u0B92' && LA61_0<='\u0B95')||(LA61_0>='\u0B99' && LA61_0<='\u0B9A')||LA61_0=='\u0B9C'||(LA61_0>='\u0B9E' && LA61_0<='\u0B9F')||(LA61_0>='\u0BA3' && LA61_0<='\u0BA4')||(LA61_0>='\u0BA8' && LA61_0<='\u0BAA')||(LA61_0>='\u0BAE' && LA61_0<='\u0BB5')||(LA61_0>='\u0BB7' && LA61_0<='\u0BB9')||(LA61_0>='\u0BBE' && LA61_0<='\u0BC2')||(LA61_0>='\u0BC6' && LA61_0<='\u0BC8')||(LA61_0>='\u0BCA' && LA61_0<='\u0BCD')||LA61_0=='\u0BD7'||(LA61_0>='\u0BE7' && LA61_0<='\u0BEF')||LA61_0=='\u0BF9'||(LA61_0>='\u0C01' && LA61_0<='\u0C03')||(LA61_0>='\u0C05' && LA61_0<='\u0C0C')||(LA61_0>='\u0C0E' && LA61_0<='\u0C10')||(LA61_0>='\u0C12' && LA61_0<='\u0C28')||(LA61_0>='\u0C2A' && LA61_0<='\u0C33')||(LA61_0>='\u0C35' && LA61_0<='\u0C39')||(LA61_0>='\u0C3E' && LA61_0<='\u0C44')||(LA61_0>='\u0C46' && LA61_0<='\u0C48')||(LA61_0>='\u0C4A' && LA61_0<='\u0C4D')||(LA61_0>='\u0C55' && LA61_0<='\u0C56')||(LA61_0>='\u0C60' && LA61_0<='\u0C61')||(LA61_0>='\u0C66' && LA61_0<='\u0C6F')||(LA61_0>='\u0C82' && LA61_0<='\u0C83')||(LA61_0>='\u0C85' && LA61_0<='\u0C8C')||(LA61_0>='\u0C8E' && LA61_0<='\u0C90')||(LA61_0>='\u0C92' && LA61_0<='\u0CA8')||(LA61_0>='\u0CAA' && LA61_0<='\u0CB3')||(LA61_0>='\u0CB5' && LA61_0<='\u0CB9')||(LA61_0>='\u0CBC' && LA61_0<='\u0CC4')||(LA61_0>='\u0CC6' && LA61_0<='\u0CC8')||(LA61_0>='\u0CCA' && LA61_0<='\u0CCD')||(LA61_0>='\u0CD5' && LA61_0<='\u0CD6')||LA61_0=='\u0CDE'||(LA61_0>='\u0CE0' && LA61_0<='\u0CE1')||(LA61_0>='\u0CE6' && LA61_0<='\u0CEF')||(LA61_0>='\u0D02' && LA61_0<='\u0D03')||(LA61_0>='\u0D05' && LA61_0<='\u0D0C')||(LA61_0>='\u0D0E' && LA61_0<='\u0D10')||(LA61_0>='\u0D12' && LA61_0<='\u0D28')||(LA61_0>='\u0D2A' && LA61_0<='\u0D39')||(LA61_0>='\u0D3E' && LA61_0<='\u0D43')||(LA61_0>='\u0D46' && LA61_0<='\u0D48')||(LA61_0>='\u0D4A' && LA61_0<='\u0D4D')||LA61_0=='\u0D57'||(LA61_0>='\u0D60' && LA61_0<='\u0D61')||(LA61_0>='\u0D66' && LA61_0<='\u0D6F')||(LA61_0>='\u0D82' && LA61_0<='\u0D83')||(LA61_0>='\u0D85' && LA61_0<='\u0D96')||(LA61_0>='\u0D9A' && LA61_0<='\u0DB1')||(LA61_0>='\u0DB3' && LA61_0<='\u0DBB')||LA61_0=='\u0DBD'||(LA61_0>='\u0DC0' && LA61_0<='\u0DC6')||LA61_0=='\u0DCA'||(LA61_0>='\u0DCF' && LA61_0<='\u0DD4')||LA61_0=='\u0DD6'||(LA61_0>='\u0DD8' && LA61_0<='\u0DDF')||(LA61_0>='\u0DF2' && LA61_0<='\u0DF3')||(LA61_0>='\u0E01' && LA61_0<='\u0E3A')||(LA61_0>='\u0E3F' && LA61_0<='\u0E4E')||(LA61_0>='\u0E50' && LA61_0<='\u0E59')||(LA61_0>='\u0E81' && LA61_0<='\u0E82')||LA61_0=='\u0E84'||(LA61_0>='\u0E87' && LA61_0<='\u0E88')||LA61_0=='\u0E8A'||LA61_0=='\u0E8D'||(LA61_0>='\u0E94' && LA61_0<='\u0E97')||(LA61_0>='\u0E99' && LA61_0<='\u0E9F')||(LA61_0>='\u0EA1' && LA61_0<='\u0EA3')||LA61_0=='\u0EA5'||LA61_0=='\u0EA7'||(LA61_0>='\u0EAA' && LA61_0<='\u0EAB')||(LA61_0>='\u0EAD' && LA61_0<='\u0EB9')||(LA61_0>='\u0EBB' && LA61_0<='\u0EBD')||(LA61_0>='\u0EC0' && LA61_0<='\u0EC4')||LA61_0=='\u0EC6'||(LA61_0>='\u0EC8' && LA61_0<='\u0ECD')||(LA61_0>='\u0ED0' && LA61_0<='\u0ED9')||(LA61_0>='\u0EDC' && LA61_0<='\u0EDD')||LA61_0=='\u0F00'||(LA61_0>='\u0F18' && LA61_0<='\u0F19')||(LA61_0>='\u0F20' && LA61_0<='\u0F29')||LA61_0=='\u0F35'||LA61_0=='\u0F37'||LA61_0=='\u0F39'||(LA61_0>='\u0F3E' && LA61_0<='\u0F47')||(LA61_0>='\u0F49' && LA61_0<='\u0F6A')||(LA61_0>='\u0F71' && LA61_0<='\u0F84')||(LA61_0>='\u0F86' && LA61_0<='\u0F8B')||(LA61_0>='\u0F90' && LA61_0<='\u0F97')||(LA61_0>='\u0F99' && LA61_0<='\u0FBC')||LA61_0=='\u0FC6'||(LA61_0>='\u1000' && LA61_0<='\u1021')||(LA61_0>='\u1023' && LA61_0<='\u1027')||(LA61_0>='\u1029' && LA61_0<='\u102A')||(LA61_0>='\u102C' && LA61_0<='\u1032')||(LA61_0>='\u1036' && LA61_0<='\u1039')||(LA61_0>='\u1040' && LA61_0<='\u1049')||(LA61_0>='\u1050' && LA61_0<='\u1059')||(LA61_0>='\u10A0' && LA61_0<='\u10C5')||(LA61_0>='\u10D0' && LA61_0<='\u10F8')||(LA61_0>='\u1100' && LA61_0<='\u1159')||(LA61_0>='\u115F' && LA61_0<='\u11A2')||(LA61_0>='\u11A8' && LA61_0<='\u11F9')||(LA61_0>='\u1200' && LA61_0<='\u1206')||(LA61_0>='\u1208' && LA61_0<='\u1246')||LA61_0=='\u1248'||(LA61_0>='\u124A' && LA61_0<='\u124D')||(LA61_0>='\u1250' && LA61_0<='\u1256')||LA61_0=='\u1258'||(LA61_0>='\u125A' && LA61_0<='\u125D')||(LA61_0>='\u1260' && LA61_0<='\u1286')||LA61_0=='\u1288'||(LA61_0>='\u128A' && LA61_0<='\u128D')||(LA61_0>='\u1290' && LA61_0<='\u12AE')||LA61_0=='\u12B0'||(LA61_0>='\u12B2' && LA61_0<='\u12B5')||(LA61_0>='\u12B8' && LA61_0<='\u12BE')||LA61_0=='\u12C0'||(LA61_0>='\u12C2' && LA61_0<='\u12C5')||(LA61_0>='\u12C8' && LA61_0<='\u12CE')||(LA61_0>='\u12D0' && LA61_0<='\u12D6')||(LA61_0>='\u12D8' && LA61_0<='\u12EE')||(LA61_0>='\u12F0' && LA61_0<='\u130E')||LA61_0=='\u1310'||(LA61_0>='\u1312' && LA61_0<='\u1315')||(LA61_0>='\u1318' && LA61_0<='\u131E')||(LA61_0>='\u1320' && LA61_0<='\u1346')||(LA61_0>='\u1348' && LA61_0<='\u135A')||(LA61_0>='\u1369' && LA61_0<='\u1371')||(LA61_0>='\u13A0' && LA61_0<='\u13F4')||(LA61_0>='\u1401' && LA61_0<='\u166C')||(LA61_0>='\u166F' && LA61_0<='\u1676')||(LA61_0>='\u1681' && LA61_0<='\u169A')||(LA61_0>='\u16A0' && LA61_0<='\u16EA')||(LA61_0>='\u16EE' && LA61_0<='\u16F0')||(LA61_0>='\u1700' && LA61_0<='\u170C')||(LA61_0>='\u170E' && LA61_0<='\u1714')||(LA61_0>='\u1720' && LA61_0<='\u1734')||(LA61_0>='\u1740' && LA61_0<='\u1753')||(LA61_0>='\u1760' && LA61_0<='\u176C')||(LA61_0>='\u176E' && LA61_0<='\u1770')||(LA61_0>='\u1772' && LA61_0<='\u1773')||(LA61_0>='\u1780' && LA61_0<='\u17D3')||LA61_0=='\u17D7'||(LA61_0>='\u17DB' && LA61_0<='\u17DD')||(LA61_0>='\u17E0' && LA61_0<='\u17E9')||(LA61_0>='\u180B' && LA61_0<='\u180D')||(LA61_0>='\u1810' && LA61_0<='\u1819')||(LA61_0>='\u1820' && LA61_0<='\u1877')||(LA61_0>='\u1880' && LA61_0<='\u18A9')||(LA61_0>='\u1900' && LA61_0<='\u191C')||(LA61_0>='\u1920' && LA61_0<='\u192B')||(LA61_0>='\u1930' && LA61_0<='\u193B')||(LA61_0>='\u1946' && LA61_0<='\u196D')||(LA61_0>='\u1970' && LA61_0<='\u1974')||(LA61_0>='\u1D00' && LA61_0<='\u1D6B')||(LA61_0>='\u1E00' && LA61_0<='\u1E9B')||(LA61_0>='\u1EA0' && LA61_0<='\u1EF9')||(LA61_0>='\u1F00' && LA61_0<='\u1F15')||(LA61_0>='\u1F18' && LA61_0<='\u1F1D')||(LA61_0>='\u1F20' && LA61_0<='\u1F45')||(LA61_0>='\u1F48' && LA61_0<='\u1F4D')||(LA61_0>='\u1F50' && LA61_0<='\u1F57')||LA61_0=='\u1F59'||LA61_0=='\u1F5B'||LA61_0=='\u1F5D'||(LA61_0>='\u1F5F' && LA61_0<='\u1F7D')||(LA61_0>='\u1F80' && LA61_0<='\u1FB4')||(LA61_0>='\u1FB6' && LA61_0<='\u1FBC')||LA61_0=='\u1FBE'||(LA61_0>='\u1FC2' && LA61_0<='\u1FC4')||(LA61_0>='\u1FC6' && LA61_0<='\u1FCC')||(LA61_0>='\u1FD0' && LA61_0<='\u1FD3')||(LA61_0>='\u1FD6' && LA61_0<='\u1FDB')||(LA61_0>='\u1FE0' && LA61_0<='\u1FEC')||(LA61_0>='\u1FF2' && LA61_0<='\u1FF4')||(LA61_0>='\u1FF6' && LA61_0<='\u1FFC')||(LA61_0>='\u200C' && LA61_0<='\u200F')||(LA61_0>='\u202A' && LA61_0<='\u202E')||(LA61_0>='\u203F' && LA61_0<='\u2040')||LA61_0=='\u2054'||(LA61_0>='\u2060' && LA61_0<='\u2063')||(LA61_0>='\u206A' && LA61_0<='\u206F')||LA61_0=='\u2071'||LA61_0=='\u207F'||(LA61_0>='\u20A0' && LA61_0<='\u20B1')||(LA61_0>='\u20D0' && LA61_0<='\u20DC')||LA61_0=='\u20E1'||(LA61_0>='\u20E5' && LA61_0<='\u20EA')||LA61_0=='\u2102'||LA61_0=='\u2107'||(LA61_0>='\u210A' && LA61_0<='\u2113')||LA61_0=='\u2115'||(LA61_0>='\u2119' && LA61_0<='\u211D')||LA61_0=='\u2124'||LA61_0=='\u2126'||LA61_0=='\u2128'||(LA61_0>='\u212A' && LA61_0<='\u212D')||(LA61_0>='\u212F' && LA61_0<='\u2131')||(LA61_0>='\u2133' && LA61_0<='\u2139')||(LA61_0>='\u213D' && LA61_0<='\u213F')||(LA61_0>='\u2145' && LA61_0<='\u2149')||(LA61_0>='\u2160' && LA61_0<='\u2183')||(LA61_0>='\u3005' && LA61_0<='\u3007')||(LA61_0>='\u3021' && LA61_0<='\u302F')||(LA61_0>='\u3031' && LA61_0<='\u3035')||(LA61_0>='\u3038' && LA61_0<='\u303C')||(LA61_0>='\u3041' && LA61_0<='\u3096')||(LA61_0>='\u3099' && LA61_0<='\u309A')||(LA61_0>='\u309D' && LA61_0<='\u309F')||(LA61_0>='\u30A1' && LA61_0<='\u30FF')||(LA61_0>='\u3105' && LA61_0<='\u312C')||(LA61_0>='\u3131' && LA61_0<='\u318E')||(LA61_0>='\u31A0' && LA61_0<='\u31B7')||(LA61_0>='\u31F0' && LA61_0<='\u31FF')||(LA61_0>='\u3400' && LA61_0<='\u4DB5')||(LA61_0>='\u4E00' && LA61_0<='\u9FA5')||(LA61_0>='\uA000' && LA61_0<='\uA48C')||(LA61_0>='\uAC00' && LA61_0<='\uD7A3')||(LA61_0>='\uF900' && LA61_0<='\uFA2D')||(LA61_0>='\uFA30' && LA61_0<='\uFA6A')||(LA61_0>='\uFB00' && LA61_0<='\uFB06')||(LA61_0>='\uFB13' && LA61_0<='\uFB17')||(LA61_0>='\uFB1D' && LA61_0<='\uFB28')||(LA61_0>='\uFB2A' && LA61_0<='\uFB36')||(LA61_0>='\uFB38' && LA61_0<='\uFB3C')||LA61_0=='\uFB3E'||(LA61_0>='\uFB40' && LA61_0<='\uFB41')||(LA61_0>='\uFB43' && LA61_0<='\uFB44')||(LA61_0>='\uFB46' && LA61_0<='\uFBB1')||(LA61_0>='\uFBD3' && LA61_0<='\uFD3D')||(LA61_0>='\uFD50' && LA61_0<='\uFD8F')||(LA61_0>='\uFD92' && LA61_0<='\uFDC7')||(LA61_0>='\uFDF0' && LA61_0<='\uFDFC')||(LA61_0>='\uFE00' && LA61_0<='\uFE0F')||(LA61_0>='\uFE20' && LA61_0<='\uFE23')||(LA61_0>='\uFE33' && LA61_0<='\uFE34')||(LA61_0>='\uFE4D' && LA61_0<='\uFE4F')||LA61_0=='\uFE69'||(LA61_0>='\uFE70' && LA61_0<='\uFE74')||(LA61_0>='\uFE76' && LA61_0<='\uFEFC')||LA61_0=='\uFEFF'||LA61_0=='\uFF04'||(LA61_0>='\uFF10' && LA61_0<='\uFF19')||(LA61_0>='\uFF21' && LA61_0<='\uFF3A')||LA61_0=='\uFF3F'||(LA61_0>='\uFF41' && LA61_0<='\uFF5A')||(LA61_0>='\uFF65' && LA61_0<='\uFFBE')||(LA61_0>='\uFFC2' && LA61_0<='\uFFC7')||(LA61_0>='\uFFCA' && LA61_0<='\uFFCF')||(LA61_0>='\uFFD2' && LA61_0<='\uFFD7')||(LA61_0>='\uFFDA' && LA61_0<='\uFFDC')||(LA61_0>='\uFFE0' && LA61_0<='\uFFE1')||(LA61_0>='\uFFE5' && LA61_0<='\uFFE6')||(LA61_0>='\uFFF9' && LA61_0<='\uFFFB')) ) {
                            alt61=1;
                        }


                        switch (alt61) {
                    	case 1 :
                    	    // src/main/resources/org/drools/lang/DRLLexer.g:316:27: IdentifierPart
                    	    {
                    	    mIdentifierPart(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop61;
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

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:321:5: ( '/' )
            // src/main/resources/org/drools/lang/DRLLexer.g:321:7: '/'
            {
            match('/'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MISC"
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // src/main/resources/org/drools/lang/DRLLexer.g:324:7: ( '\\'' | '\\\\' | '$' )
            // src/main/resources/org/drools/lang/DRLLexer.g:
            {
            if ( input.LA(1)=='$'||input.LA(1)=='\''||input.LA(1)=='\\' ) {
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

    // $ANTLR start "IdentifierStart"
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/DRLLexer.g:330:5: ( '\\u0024' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u064a' | '\\u066e' .. '\\u066f' | '\\u0671' .. '\\u06d3' | '\\u06d5' | '\\u06e5' .. '\\u06e6' | '\\u06ee' .. '\\u06ef' | '\\u06fa' .. '\\u06fc' | '\\u06ff' | '\\u0710' | '\\u0712' .. '\\u072f' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07a5' | '\\u07b1' | '\\u0904' .. '\\u0939' | '\\u093d' | '\\u0950' | '\\u0958' .. '\\u0961' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bd' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e1' | '\\u09f0' .. '\\u09f3' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a72' .. '\\u0a74' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae1' | '\\u0af1' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3d' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b71' | '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bf9' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c60' .. '\\u0c61' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbd' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d60' .. '\\u0d61' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0e01' .. '\\u0e30' | '\\u0e32' .. '\\u0e33' | '\\u0e3f' .. '\\u0e46' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb0' | '\\u0eb2' .. '\\u0eb3' | '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f40' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f88' .. '\\u0f8b' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u1050' .. '\\u1055' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1711' | '\\u1720' .. '\\u1731' | '\\u1740' .. '\\u1751' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1780' .. '\\u17b3' | '\\u17d7' | '\\u17db' .. '\\u17dc' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a8' | '\\u1900' .. '\\u191c' | '\\u1950' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u3029' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' | '\\ufb1f' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\uff04' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' )
            // src/main/resources/org/drools/lang/DRLLexer.g:
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
            // src/main/resources/org/drools/lang/DRLLexer.g:628:5: ( '\\u0000' .. '\\u0008' | '\\u000e' .. '\\u001b' | '\\u0024' | '\\u0030' .. '\\u0039' | '\\u0041' .. '\\u005a' | '\\u005f' | '\\u0061' .. '\\u007a' | '\\u007f' .. '\\u009f' | '\\u00a2' .. '\\u00a5' | '\\u00aa' | '\\u00ad' | '\\u00b5' | '\\u00ba' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00f6' | '\\u00f8' .. '\\u0236' | '\\u0250' .. '\\u02c1' | '\\u02c6' .. '\\u02d1' | '\\u02e0' .. '\\u02e4' | '\\u02ee' | '\\u0300' .. '\\u0357' | '\\u035d' .. '\\u036f' | '\\u037a' | '\\u0386' | '\\u0388' .. '\\u038a' | '\\u038c' | '\\u038e' .. '\\u03a1' | '\\u03a3' .. '\\u03ce' | '\\u03d0' .. '\\u03f5' | '\\u03f7' .. '\\u03fb' | '\\u0400' .. '\\u0481' | '\\u0483' .. '\\u0486' | '\\u048a' .. '\\u04ce' | '\\u04d0' .. '\\u04f5' | '\\u04f8' .. '\\u04f9' | '\\u0500' .. '\\u050f' | '\\u0531' .. '\\u0556' | '\\u0559' | '\\u0561' .. '\\u0587' | '\\u0591' .. '\\u05a1' | '\\u05a3' .. '\\u05b9' | '\\u05bb' .. '\\u05bd' | '\\u05bf' | '\\u05c1' .. '\\u05c2' | '\\u05c4' | '\\u05d0' .. '\\u05ea' | '\\u05f0' .. '\\u05f2' | '\\u0600' .. '\\u0603' | '\\u0610' .. '\\u0615' | '\\u0621' .. '\\u063a' | '\\u0640' .. '\\u0658' | '\\u0660' .. '\\u0669' | '\\u066e' .. '\\u06d3' | '\\u06d5' .. '\\u06dd' | '\\u06df' .. '\\u06e8' | '\\u06ea' .. '\\u06fc' | '\\u06ff' | '\\u070f' .. '\\u074a' | '\\u074d' .. '\\u074f' | '\\u0780' .. '\\u07b1' | '\\u0901' .. '\\u0939' | '\\u093c' .. '\\u094d' | '\\u0950' .. '\\u0954' | '\\u0958' .. '\\u0963' | '\\u0966' .. '\\u096f' | '\\u0981' .. '\\u0983' | '\\u0985' .. '\\u098c' | '\\u098f' .. '\\u0990' | '\\u0993' .. '\\u09a8' | '\\u09aa' .. '\\u09b0' | '\\u09b2' | '\\u09b6' .. '\\u09b9' | '\\u09bc' .. '\\u09c4' | '\\u09c7' .. '\\u09c8' | '\\u09cb' .. '\\u09cd' | '\\u09d7' | '\\u09dc' .. '\\u09dd' | '\\u09df' .. '\\u09e3' | '\\u09e6' .. '\\u09f3' | '\\u0a01' .. '\\u0a03' | '\\u0a05' .. '\\u0a0a' | '\\u0a0f' .. '\\u0a10' | '\\u0a13' .. '\\u0a28' | '\\u0a2a' .. '\\u0a30' | '\\u0a32' .. '\\u0a33' | '\\u0a35' .. '\\u0a36' | '\\u0a38' .. '\\u0a39' | '\\u0a3c' | '\\u0a3e' .. '\\u0a42' | '\\u0a47' .. '\\u0a48' | '\\u0a4b' .. '\\u0a4d' | '\\u0a59' .. '\\u0a5c' | '\\u0a5e' | '\\u0a66' .. '\\u0a74' | '\\u0a81' .. '\\u0a83' | '\\u0a85' .. '\\u0a8d' | '\\u0a8f' .. '\\u0a91' | '\\u0a93' .. '\\u0aa8' | '\\u0aaa' .. '\\u0ab0' | '\\u0ab2' .. '\\u0ab3' | '\\u0ab5' .. '\\u0ab9' | '\\u0abc' .. '\\u0ac5' | '\\u0ac7' .. '\\u0ac9' | '\\u0acb' .. '\\u0acd' | '\\u0ad0' | '\\u0ae0' .. '\\u0ae3' | '\\u0ae6' .. '\\u0aef' | '\\u0af1' | '\\u0b01' .. '\\u0b03' | '\\u0b05' .. '\\u0b0c' | '\\u0b0f' .. '\\u0b10' | '\\u0b13' .. '\\u0b28' | '\\u0b2a' .. '\\u0b30' | '\\u0b32' .. '\\u0b33' | '\\u0b35' .. '\\u0b39' | '\\u0b3c' .. '\\u0b43' | '\\u0b47' .. '\\u0b48' | '\\u0b4b' .. '\\u0b4d' | '\\u0b56' .. '\\u0b57' | '\\u0b5c' .. '\\u0b5d' | '\\u0b5f' .. '\\u0b61' | '\\u0b66' .. '\\u0b6f' | '\\u0b71' | '\\u0b82' .. '\\u0b83' | '\\u0b85' .. '\\u0b8a' | '\\u0b8e' .. '\\u0b90' | '\\u0b92' .. '\\u0b95' | '\\u0b99' .. '\\u0b9a' | '\\u0b9c' | '\\u0b9e' .. '\\u0b9f' | '\\u0ba3' .. '\\u0ba4' | '\\u0ba8' .. '\\u0baa' | '\\u0bae' .. '\\u0bb5' | '\\u0bb7' .. '\\u0bb9' | '\\u0bbe' .. '\\u0bc2' | '\\u0bc6' .. '\\u0bc8' | '\\u0bca' .. '\\u0bcd' | '\\u0bd7' | '\\u0be7' .. '\\u0bef' | '\\u0bf9' | '\\u0c01' .. '\\u0c03' | '\\u0c05' .. '\\u0c0c' | '\\u0c0e' .. '\\u0c10' | '\\u0c12' .. '\\u0c28' | '\\u0c2a' .. '\\u0c33' | '\\u0c35' .. '\\u0c39' | '\\u0c3e' .. '\\u0c44' | '\\u0c46' .. '\\u0c48' | '\\u0c4a' .. '\\u0c4d' | '\\u0c55' .. '\\u0c56' | '\\u0c60' .. '\\u0c61' | '\\u0c66' .. '\\u0c6f' | '\\u0c82' .. '\\u0c83' | '\\u0c85' .. '\\u0c8c' | '\\u0c8e' .. '\\u0c90' | '\\u0c92' .. '\\u0ca8' | '\\u0caa' .. '\\u0cb3' | '\\u0cb5' .. '\\u0cb9' | '\\u0cbc' .. '\\u0cc4' | '\\u0cc6' .. '\\u0cc8' | '\\u0cca' .. '\\u0ccd' | '\\u0cd5' .. '\\u0cd6' | '\\u0cde' | '\\u0ce0' .. '\\u0ce1' | '\\u0ce6' .. '\\u0cef' | '\\u0d02' .. '\\u0d03' | '\\u0d05' .. '\\u0d0c' | '\\u0d0e' .. '\\u0d10' | '\\u0d12' .. '\\u0d28' | '\\u0d2a' .. '\\u0d39' | '\\u0d3e' .. '\\u0d43' | '\\u0d46' .. '\\u0d48' | '\\u0d4a' .. '\\u0d4d' | '\\u0d57' | '\\u0d60' .. '\\u0d61' | '\\u0d66' .. '\\u0d6f' | '\\u0d82' .. '\\u0d83' | '\\u0d85' .. '\\u0d96' | '\\u0d9a' .. '\\u0db1' | '\\u0db3' .. '\\u0dbb' | '\\u0dbd' | '\\u0dc0' .. '\\u0dc6' | '\\u0dca' | '\\u0dcf' .. '\\u0dd4' | '\\u0dd6' | '\\u0dd8' .. '\\u0ddf' | '\\u0df2' .. '\\u0df3' | '\\u0e01' .. '\\u0e3a' | '\\u0e3f' .. '\\u0e4e' | '\\u0e50' .. '\\u0e59' | '\\u0e81' .. '\\u0e82' | '\\u0e84' | '\\u0e87' .. '\\u0e88' | '\\u0e8a' | '\\u0e8d' | '\\u0e94' .. '\\u0e97' | '\\u0e99' .. '\\u0e9f' | '\\u0ea1' .. '\\u0ea3' | '\\u0ea5' | '\\u0ea7' | '\\u0eaa' .. '\\u0eab' | '\\u0ead' .. '\\u0eb9' | '\\u0ebb' .. '\\u0ebd' | '\\u0ec0' .. '\\u0ec4' | '\\u0ec6' | '\\u0ec8' .. '\\u0ecd' | '\\u0ed0' .. '\\u0ed9' | '\\u0edc' .. '\\u0edd' | '\\u0f00' | '\\u0f18' .. '\\u0f19' | '\\u0f20' .. '\\u0f29' | '\\u0f35' | '\\u0f37' | '\\u0f39' | '\\u0f3e' .. '\\u0f47' | '\\u0f49' .. '\\u0f6a' | '\\u0f71' .. '\\u0f84' | '\\u0f86' .. '\\u0f8b' | '\\u0f90' .. '\\u0f97' | '\\u0f99' .. '\\u0fbc' | '\\u0fc6' | '\\u1000' .. '\\u1021' | '\\u1023' .. '\\u1027' | '\\u1029' .. '\\u102a' | '\\u102c' .. '\\u1032' | '\\u1036' .. '\\u1039' | '\\u1040' .. '\\u1049' | '\\u1050' .. '\\u1059' | '\\u10a0' .. '\\u10c5' | '\\u10d0' .. '\\u10f8' | '\\u1100' .. '\\u1159' | '\\u115f' .. '\\u11a2' | '\\u11a8' .. '\\u11f9' | '\\u1200' .. '\\u1206' | '\\u1208' .. '\\u1246' | '\\u1248' | '\\u124a' .. '\\u124d' | '\\u1250' .. '\\u1256' | '\\u1258' | '\\u125a' .. '\\u125d' | '\\u1260' .. '\\u1286' | '\\u1288' | '\\u128a' .. '\\u128d' | '\\u1290' .. '\\u12ae' | '\\u12b0' | '\\u12b2' .. '\\u12b5' | '\\u12b8' .. '\\u12be' | '\\u12c0' | '\\u12c2' .. '\\u12c5' | '\\u12c8' .. '\\u12ce' | '\\u12d0' .. '\\u12d6' | '\\u12d8' .. '\\u12ee' | '\\u12f0' .. '\\u130e' | '\\u1310' | '\\u1312' .. '\\u1315' | '\\u1318' .. '\\u131e' | '\\u1320' .. '\\u1346' | '\\u1348' .. '\\u135a' | '\\u1369' .. '\\u1371' | '\\u13a0' .. '\\u13f4' | '\\u1401' .. '\\u166c' | '\\u166f' .. '\\u1676' | '\\u1681' .. '\\u169a' | '\\u16a0' .. '\\u16ea' | '\\u16ee' .. '\\u16f0' | '\\u1700' .. '\\u170c' | '\\u170e' .. '\\u1714' | '\\u1720' .. '\\u1734' | '\\u1740' .. '\\u1753' | '\\u1760' .. '\\u176c' | '\\u176e' .. '\\u1770' | '\\u1772' .. '\\u1773' | '\\u1780' .. '\\u17d3' | '\\u17d7' | '\\u17db' .. '\\u17dd' | '\\u17e0' .. '\\u17e9' | '\\u180b' .. '\\u180d' | '\\u1810' .. '\\u1819' | '\\u1820' .. '\\u1877' | '\\u1880' .. '\\u18a9' | '\\u1900' .. '\\u191c' | '\\u1920' .. '\\u192b' | '\\u1930' .. '\\u193b' | '\\u1946' .. '\\u196d' | '\\u1970' .. '\\u1974' | '\\u1d00' .. '\\u1d6b' | '\\u1e00' .. '\\u1e9b' | '\\u1ea0' .. '\\u1ef9' | '\\u1f00' .. '\\u1f15' | '\\u1f18' .. '\\u1f1d' | '\\u1f20' .. '\\u1f45' | '\\u1f48' .. '\\u1f4d' | '\\u1f50' .. '\\u1f57' | '\\u1f59' | '\\u1f5b' | '\\u1f5d' | '\\u1f5f' .. '\\u1f7d' | '\\u1f80' .. '\\u1fb4' | '\\u1fb6' .. '\\u1fbc' | '\\u1fbe' | '\\u1fc2' .. '\\u1fc4' | '\\u1fc6' .. '\\u1fcc' | '\\u1fd0' .. '\\u1fd3' | '\\u1fd6' .. '\\u1fdb' | '\\u1fe0' .. '\\u1fec' | '\\u1ff2' .. '\\u1ff4' | '\\u1ff6' .. '\\u1ffc' | '\\u200c' .. '\\u200f' | '\\u202a' .. '\\u202e' | '\\u203f' .. '\\u2040' | '\\u2054' | '\\u2060' .. '\\u2063' | '\\u206a' .. '\\u206f' | '\\u2071' | '\\u207f' | '\\u20a0' .. '\\u20b1' | '\\u20d0' .. '\\u20dc' | '\\u20e1' | '\\u20e5' .. '\\u20ea' | '\\u2102' | '\\u2107' | '\\u210a' .. '\\u2113' | '\\u2115' | '\\u2119' .. '\\u211d' | '\\u2124' | '\\u2126' | '\\u2128' | '\\u212a' .. '\\u212d' | '\\u212f' .. '\\u2131' | '\\u2133' .. '\\u2139' | '\\u213d' .. '\\u213f' | '\\u2145' .. '\\u2149' | '\\u2160' .. '\\u2183' | '\\u3005' .. '\\u3007' | '\\u3021' .. '\\u302f' | '\\u3031' .. '\\u3035' | '\\u3038' .. '\\u303c' | '\\u3041' .. '\\u3096' | '\\u3099' .. '\\u309a' | '\\u309d' .. '\\u309f' | '\\u30a1' .. '\\u30ff' | '\\u3105' .. '\\u312c' | '\\u3131' .. '\\u318e' | '\\u31a0' .. '\\u31b7' | '\\u31f0' .. '\\u31ff' | '\\u3400' .. '\\u4db5' | '\\u4e00' .. '\\u9fa5' | '\\ua000' .. '\\ua48c' | '\\uac00' .. '\\ud7a3' | '\\uf900' .. '\\ufa2d' | '\\ufa30' .. '\\ufa6a' | '\\ufb00' .. '\\ufb06' | '\\ufb13' .. '\\ufb17' | '\\ufb1d' .. '\\ufb28' | '\\ufb2a' .. '\\ufb36' | '\\ufb38' .. '\\ufb3c' | '\\ufb3e' | '\\ufb40' .. '\\ufb41' | '\\ufb43' .. '\\ufb44' | '\\ufb46' .. '\\ufbb1' | '\\ufbd3' .. '\\ufd3d' | '\\ufd50' .. '\\ufd8f' | '\\ufd92' .. '\\ufdc7' | '\\ufdf0' .. '\\ufdfc' | '\\ufe00' .. '\\ufe0f' | '\\ufe20' .. '\\ufe23' | '\\ufe33' .. '\\ufe34' | '\\ufe4d' .. '\\ufe4f' | '\\ufe69' | '\\ufe70' .. '\\ufe74' | '\\ufe76' .. '\\ufefc' | '\\ufeff' | '\\uff04' | '\\uff10' .. '\\uff19' | '\\uff21' .. '\\uff3a' | '\\uff3f' | '\\uff41' .. '\\uff5a' | '\\uff65' .. '\\uffbe' | '\\uffc2' .. '\\uffc7' | '\\uffca' .. '\\uffcf' | '\\uffd2' .. '\\uffd7' | '\\uffda' .. '\\uffdc' | '\\uffe0' .. '\\uffe1' | '\\uffe5' .. '\\uffe6' | '\\ufff9' .. '\\ufffb' )
            // src/main/resources/org/drools/lang/DRLLexer.g:
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
        // src/main/resources/org/drools/lang/DRLLexer.g:1:8: ( WS | FLOAT | HEX | DECIMAL | STRING | TIME_INTERVAL | BOOL | NULL | AT | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | UNIFY | DECR | INCR | ARROW | SEMICOLON | COLON | EQUALS | NOT_EQUALS | GREATER_EQUALS | LESS_EQUALS | GREATER | LESS | EQUALS_ASSIGN | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | QUESTION | NEGATION | TILDE | PIPE | AMPER | XOR | MOD | STAR | MINUS | PLUS | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | ID | DIV | MISC )
        int alt63=56;
        alt63 = dfa63.predict(input);
        switch (alt63) {
            case 1 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:10: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:13: FLOAT
                {
                mFLOAT(); if (state.failed) return ;

                }
                break;
            case 3 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:19: HEX
                {
                mHEX(); if (state.failed) return ;

                }
                break;
            case 4 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:23: DECIMAL
                {
                mDECIMAL(); if (state.failed) return ;

                }
                break;
            case 5 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:31: STRING
                {
                mSTRING(); if (state.failed) return ;

                }
                break;
            case 6 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:38: TIME_INTERVAL
                {
                mTIME_INTERVAL(); if (state.failed) return ;

                }
                break;
            case 7 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:52: BOOL
                {
                mBOOL(); if (state.failed) return ;

                }
                break;
            case 8 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:57: NULL
                {
                mNULL(); if (state.failed) return ;

                }
                break;
            case 9 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:62: AT
                {
                mAT(); if (state.failed) return ;

                }
                break;
            case 10 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:65: PLUS_ASSIGN
                {
                mPLUS_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 11 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:77: MINUS_ASSIGN
                {
                mMINUS_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 12 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:90: MULT_ASSIGN
                {
                mMULT_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 13 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:102: DIV_ASSIGN
                {
                mDIV_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 14 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:113: AND_ASSIGN
                {
                mAND_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 15 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:124: OR_ASSIGN
                {
                mOR_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 16 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:134: XOR_ASSIGN
                {
                mXOR_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 17 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:145: MOD_ASSIGN
                {
                mMOD_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 18 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:156: UNIFY
                {
                mUNIFY(); if (state.failed) return ;

                }
                break;
            case 19 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:162: DECR
                {
                mDECR(); if (state.failed) return ;

                }
                break;
            case 20 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:167: INCR
                {
                mINCR(); if (state.failed) return ;

                }
                break;
            case 21 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:172: ARROW
                {
                mARROW(); if (state.failed) return ;

                }
                break;
            case 22 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:178: SEMICOLON
                {
                mSEMICOLON(); if (state.failed) return ;

                }
                break;
            case 23 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:188: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 24 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:194: EQUALS
                {
                mEQUALS(); if (state.failed) return ;

                }
                break;
            case 25 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:201: NOT_EQUALS
                {
                mNOT_EQUALS(); if (state.failed) return ;

                }
                break;
            case 26 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:212: GREATER_EQUALS
                {
                mGREATER_EQUALS(); if (state.failed) return ;

                }
                break;
            case 27 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:227: LESS_EQUALS
                {
                mLESS_EQUALS(); if (state.failed) return ;

                }
                break;
            case 28 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:239: GREATER
                {
                mGREATER(); if (state.failed) return ;

                }
                break;
            case 29 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:247: LESS
                {
                mLESS(); if (state.failed) return ;

                }
                break;
            case 30 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:252: EQUALS_ASSIGN
                {
                mEQUALS_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 31 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:266: LEFT_PAREN
                {
                mLEFT_PAREN(); if (state.failed) return ;

                }
                break;
            case 32 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:277: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (state.failed) return ;

                }
                break;
            case 33 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:289: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 34 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:301: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (state.failed) return ;

                }
                break;
            case 35 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:314: LEFT_CURLY
                {
                mLEFT_CURLY(); if (state.failed) return ;

                }
                break;
            case 36 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:325: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (state.failed) return ;

                }
                break;
            case 37 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:337: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 38 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:343: DOT
                {
                mDOT(); if (state.failed) return ;

                }
                break;
            case 39 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:347: DOUBLE_AMPER
                {
                mDOUBLE_AMPER(); if (state.failed) return ;

                }
                break;
            case 40 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:360: DOUBLE_PIPE
                {
                mDOUBLE_PIPE(); if (state.failed) return ;

                }
                break;
            case 41 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:372: QUESTION
                {
                mQUESTION(); if (state.failed) return ;

                }
                break;
            case 42 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:381: NEGATION
                {
                mNEGATION(); if (state.failed) return ;

                }
                break;
            case 43 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:390: TILDE
                {
                mTILDE(); if (state.failed) return ;

                }
                break;
            case 44 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:396: PIPE
                {
                mPIPE(); if (state.failed) return ;

                }
                break;
            case 45 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:401: AMPER
                {
                mAMPER(); if (state.failed) return ;

                }
                break;
            case 46 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:407: XOR
                {
                mXOR(); if (state.failed) return ;

                }
                break;
            case 47 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:411: MOD
                {
                mMOD(); if (state.failed) return ;

                }
                break;
            case 48 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:415: STAR
                {
                mSTAR(); if (state.failed) return ;

                }
                break;
            case 49 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:420: MINUS
                {
                mMINUS(); if (state.failed) return ;

                }
                break;
            case 50 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:426: PLUS
                {
                mPLUS(); if (state.failed) return ;

                }
                break;
            case 51 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:431: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 52 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:460: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 53 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:488: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 54 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:507: ID
                {
                mID(); if (state.failed) return ;

                }
                break;
            case 55 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:510: DIV
                {
                mDIV(); if (state.failed) return ;

                }
                break;
            case 56 :
                // src/main/resources/org/drools/lang/DRLLexer.g:1:514: MISC
                {
                mMISC(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_DRLLexer
    public final void synpred1_DRLLexer_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/DRLLexer.g:67:20: ( '\\r\\n' )
        // src/main/resources/org/drools/lang/DRLLexer.g:67:22: '\\r\\n'
        {
        match("\r\n"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_DRLLexer

    public final boolean synpred1_DRLLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_DRLLexer_fragment(); // can never throw exception
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
    protected DFA53 dfa53 = new DFA53(this);
    protected DFA25 dfa25 = new DFA25(this);
    protected DFA27 dfa27 = new DFA27(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA35 dfa35 = new DFA35(this);
    protected DFA37 dfa37 = new DFA37(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA63 dfa63 = new DFA63(this);
    static final String DFA13_eotS =
        "\6\uffff";
    static final String DFA13_eofS =
        "\6\uffff";
    static final String DFA13_minS =
        "\2\56\4\uffff";
    static final String DFA13_maxS =
        "\1\71\1\146\4\uffff";
    static final String DFA13_acceptS =
        "\2\uffff\1\2\1\1\1\3\1\4";
    static final String DFA13_specialS =
        "\6\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\10\uffff\1\5\1\uffff\1\5\1\4\1\5\35\uffff"+
            "\1\5\1\4\1\5",
            "",
            "",
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
            return "73:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? ( FloatTypeSuffix )? | '.' ( '0' .. '9' )+ ( Exponent )? ( FloatTypeSuffix )? | ( '0' .. '9' )+ Exponent ( FloatTypeSuffix )? | ( '0' .. '9' )+ FloatTypeSuffix );";
        }
    }
    static final String DFA53_eotS =
        "\1\uffff\1\6\2\uffff\1\7\3\uffff";
    static final String DFA53_eofS =
        "\10\uffff";
    static final String DFA53_minS =
        "\2\60\2\uffff\1\163\3\uffff";
    static final String DFA53_maxS =
        "\1\71\1\163\2\uffff\1\163\3\uffff";
    static final String DFA53_acceptS =
        "\2\uffff\1\1\1\2\1\uffff\1\4\1\5\1\3";
    static final String DFA53_specialS =
        "\10\uffff}>";
    static final String[] DFA53_transitionS = {
            "\12\1",
            "\12\1\52\uffff\1\2\3\uffff\1\3\4\uffff\1\4\5\uffff\1\5",
            "",
            "",
            "\1\6",
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
            return "99:1: TIME_INTERVAL : ( ( ( '0' .. '9' )+ 'd' ) ( ( '0' .. '9' )+ 'h' )? ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 'h' ) ( ( '0' .. '9' )+ 'm' )? ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 'm' ) ( ( '0' .. '9' )+ 's' )? ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ 's' ) ( ( '0' .. '9' )+ ( 'ms' )? )? | ( ( '0' .. '9' )+ ( 'ms' )? ) );";
        }
    }
    static final String DFA25_eotS =
        "\2\2\2\uffff";
    static final String DFA25_eofS =
        "\4\uffff";
    static final String DFA25_minS =
        "\2\60\2\uffff";
    static final String DFA25_maxS =
        "\1\71\1\150\2\uffff";
    static final String DFA25_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA25_specialS =
        "\4\uffff}>";
    static final String[] DFA25_transitionS = {
            "\12\1",
            "\12\1\56\uffff\1\3",
            "",
            ""
    };

    static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
    static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
    static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
    static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
    static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
    static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }
        public String getDescription() {
            return "100:25: ( ( '0' .. '9' )+ 'h' )?";
        }
    }
    static final String DFA27_eotS =
        "\2\2\1\uffff\1\4\1\uffff";
    static final String DFA27_eofS =
        "\5\uffff";
    static final String DFA27_minS =
        "\2\60\1\uffff\1\163\1\uffff";
    static final String DFA27_maxS =
        "\1\71\1\155\1\uffff\1\163\1\uffff";
    static final String DFA27_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA27_specialS =
        "\5\uffff}>";
    static final String[] DFA27_transitionS = {
            "\12\1",
            "\12\1\63\uffff\1\3",
            "",
            "\1\2",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "100:43: ( ( '0' .. '9' )+ 'm' )?";
        }
    }
    static final String DFA29_eotS =
        "\2\2\2\uffff";
    static final String DFA29_eofS =
        "\4\uffff";
    static final String DFA29_minS =
        "\2\60\2\uffff";
    static final String DFA29_maxS =
        "\1\71\1\163\2\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA29_specialS =
        "\4\uffff}>";
    static final String[] DFA29_transitionS = {
            "\12\1",
            "\12\1\71\uffff\1\3",
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
            return "100:61: ( ( '0' .. '9' )+ 's' )?";
        }
    }
    static final String DFA35_eotS =
        "\2\2\1\uffff\1\4\1\uffff";
    static final String DFA35_eofS =
        "\5\uffff";
    static final String DFA35_minS =
        "\2\60\1\uffff\1\163\1\uffff";
    static final String DFA35_maxS =
        "\1\71\1\155\1\uffff\1\163\1\uffff";
    static final String DFA35_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA35_specialS =
        "\5\uffff}>";
    static final String[] DFA35_transitionS = {
            "\12\1",
            "\12\1\63\uffff\1\3",
            "",
            "\1\2",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "101:25: ( ( '0' .. '9' )+ 'm' )?";
        }
    }
    static final String DFA37_eotS =
        "\2\2\2\uffff";
    static final String DFA37_eofS =
        "\4\uffff";
    static final String DFA37_minS =
        "\2\60\2\uffff";
    static final String DFA37_maxS =
        "\1\71\1\163\2\uffff";
    static final String DFA37_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA37_specialS =
        "\4\uffff}>";
    static final String[] DFA37_transitionS = {
            "\12\1",
            "\12\1\71\uffff\1\3",
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
            return "101:43: ( ( '0' .. '9' )+ 's' )?";
        }
    }
    static final String DFA43_eotS =
        "\2\2\2\uffff";
    static final String DFA43_eofS =
        "\4\uffff";
    static final String DFA43_minS =
        "\2\60\2\uffff";
    static final String DFA43_maxS =
        "\1\71\1\163\2\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA43_specialS =
        "\4\uffff}>";
    static final String[] DFA43_transitionS = {
            "\12\1",
            "\12\1\71\uffff\1\3",
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
            return "102:25: ( ( '0' .. '9' )+ 's' )?";
        }
    }
    static final String DFA63_eotS =
        "\2\uffff\1\51\1\53\1\51\1\uffff\1\45\3\44\1\uffff\1\61\1\65\1\67"+
        "\1\73\1\76\1\101\1\103\1\105\1\107\1\uffff\1\111\1\113\1\115\1\117"+
        "\17\uffff\1\47\3\uffff\3\44\41\uffff\3\44\1\126\1\44\1\130\1\uffff"+
        "\1\126\1\uffff";
    static final String DFA63_eofS =
        "\131\uffff";
    static final String DFA63_minS =
        "\1\11\1\uffff\1\56\1\60\1\56\1\uffff\1\0\1\162\1\141\1\165\1\uffff"+
        "\1\53\1\55\1\75\1\52\1\46\4\75\1\uffff\4\75\17\uffff\1\60\3\uffff"+
        "\1\165\2\154\41\uffff\1\145\1\163\1\154\1\0\1\145\1\0\1\uffff\1"+
        "\0\1\uffff";
    static final String DFA63_maxS =
        "\1\uffe6\1\uffff\1\170\1\71\1\163\1\uffff\1\uffff\1\162\1\141\1"+
        "\165\1\uffff\1\75\1\76\3\75\1\174\3\75\1\uffff\4\75\17\uffff\1\71"+
        "\3\uffff\1\165\2\154\41\uffff\1\145\1\163\1\154\1\ufffb\1\145\1"+
        "\ufffb\1\uffff\1\ufffb\1\uffff";
    static final String DFA63_acceptS =
        "\1\uffff\1\1\3\uffff\1\5\4\uffff\1\11\11\uffff\1\26\4\uffff\1\37"+
        "\1\40\1\41\1\42\1\43\1\44\1\45\1\51\1\53\1\63\2\66\1\70\1\3\1\2"+
        "\1\uffff\1\4\1\6\1\46\3\uffff\1\12\1\24\1\62\1\13\1\23\1\25\1\61"+
        "\1\14\1\60\1\15\1\64\1\65\1\67\1\16\1\47\1\55\1\17\1\50\1\54\1\20"+
        "\1\56\1\21\1\57\1\22\1\27\1\30\1\36\1\31\1\52\1\32\1\34\1\33\1\35"+
        "\6\uffff\1\7\1\uffff\1\10";
    static final String DFA63_specialS =
        "\6\uffff\1\0\122\uffff}>";
    static final String[] DFA63_transitionS = {
            "\2\1\1\uffff\2\1\22\uffff\1\1\1\26\1\5\1\42\1\43\1\22\1\17\1"+
            "\6\1\31\1\32\1\15\1\13\1\37\1\14\1\3\1\16\1\2\11\4\1\23\1\24"+
            "\1\30\1\25\1\27\1\40\1\12\32\44\1\33\1\45\1\34\1\21\7\44\1\10"+
            "\7\44\1\11\5\44\1\7\6\44\1\35\1\20\1\36\1\41\43\uffff\4\44\4"+
            "\uffff\1\44\12\uffff\1\44\4\uffff\1\44\5\uffff\27\44\1\uffff"+
            "\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14\44\16\uffff"+
            "\5\44\11\uffff\1\44\u008b\uffff\1\44\13\uffff\1\44\1\uffff\3"+
            "\44\1\uffff\1\44\1\uffff\24\44\1\uffff\54\44\1\uffff\46\44\1"+
            "\uffff\5\44\4\uffff\u0082\44\10\uffff\105\44\1\uffff\46\44\2"+
            "\uffff\2\44\6\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff"+
            "\47\44\110\uffff\33\44\5\uffff\3\44\56\uffff\32\44\5\uffff\13"+
            "\44\43\uffff\2\44\1\uffff\143\44\1\uffff\1\44\17\uffff\2\44"+
            "\7\uffff\2\44\12\uffff\3\44\2\uffff\1\44\20\uffff\1\44\1\uffff"+
            "\36\44\35\uffff\3\44\60\uffff\46\44\13\uffff\1\44\u0152\uffff"+
            "\66\44\3\uffff\1\44\22\uffff\1\44\7\uffff\12\44\43\uffff\10"+
            "\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\1\44\3\uffff"+
            "\4\44\3\uffff\1\44\36\uffff\2\44\1\uffff\3\44\16\uffff\4\44"+
            "\21\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff"+
            "\2\44\1\uffff\2\44\1\uffff\2\44\37\uffff\4\44\1\uffff\1\44\23"+
            "\uffff\3\44\20\uffff\11\44\1\uffff\3\44\1\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\22\uffff\1\44\17"+
            "\uffff\2\44\17\uffff\1\44\23\uffff\10\44\2\uffff\2\44\2\uffff"+
            "\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\3\uffff\1\44\36"+
            "\uffff\2\44\1\uffff\3\44\17\uffff\1\44\21\uffff\1\44\1\uffff"+
            "\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff\1\44\1"+
            "\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1\uffff"+
            "\3\44\77\uffff\1\44\13\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\12\44\1\uffff\5\44\46\uffff\2\44\43\uffff\10\44\1\uffff"+
            "\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\3\uffff\1\44"+
            "\40\uffff\1\44\1\uffff\2\44\43\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\20\44\46\uffff\2\44\43\uffff\22\44\3\uffff\30"+
            "\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\72\uffff\60\44\1"+
            "\uffff\2\44\13\uffff\10\44\72\uffff\2\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4\44\1\uffff\7\44\1"+
            "\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff\2\44\1\uffff\4"+
            "\44\1\uffff\2\44\11\uffff\1\44\2\uffff\5\44\1\uffff\1\44\25"+
            "\uffff\2\44\42\uffff\1\44\77\uffff\10\44\1\uffff\42\44\35\uffff"+
            "\4\44\164\uffff\42\44\1\uffff\5\44\1\uffff\2\44\45\uffff\6\44"+
            "\112\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff\104\44"+
            "\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\47\44\1"+
            "\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff\7\44\1"+
            "\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44\1\uffff"+
            "\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\105\uffff\125"+
            "\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff\32\44\5\uffff\113"+
            "\44\3\uffff\3\44\17\uffff\15\44\1\uffff\4\44\16\uffff\22\44"+
            "\16\uffff\22\44\16\uffff\15\44\1\uffff\3\44\17\uffff\64\44\43"+
            "\uffff\1\44\3\uffff\2\44\103\uffff\130\44\10\uffff\51\44\127"+
            "\uffff\35\44\63\uffff\36\44\2\uffff\5\44\u038b\uffff\154\44"+
            "\u0094\uffff\u009c\44\4\uffff\132\44\6\uffff\26\44\2\uffff\6"+
            "\44\2\uffff\46\44\2\uffff\6\44\2\uffff\10\44\1\uffff\1\44\1"+
            "\uffff\1\44\1\uffff\1\44\1\uffff\37\44\2\uffff\65\44\1\uffff"+
            "\7\44\1\uffff\1\44\3\uffff\3\44\1\uffff\7\44\3\uffff\4\44\2"+
            "\uffff\6\44\4\uffff\15\44\5\uffff\3\44\1\uffff\7\44\102\uffff"+
            "\2\44\23\uffff\1\44\34\uffff\1\44\15\uffff\1\44\40\uffff\22"+
            "\44\120\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3"+
            "\uffff\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4"+
            "\44\1\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\26\uffff"+
            "\44\44\u0e81\uffff\3\44\31\uffff\11\44\7\uffff\5\44\2\uffff"+
            "\5\44\4\uffff\126\44\6\uffff\3\44\1\uffff\137\44\5\uffff\50"+
            "\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44\u0200\uffff"+
            "\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44\u0773\uffff"+
            "\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095\uffff\7\44"+
            "\14\uffff\5\44\5\uffff\1\44\1\uffff\12\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\15"+
            "\44\66\uffff\2\44\30\uffff\3\44\31\uffff\1\44\6\uffff\5\44\1"+
            "\uffff\u0087\44\7\uffff\1\44\34\uffff\32\44\4\uffff\1\44\1\uffff"+
            "\32\44\12\uffff\132\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\3\44\3\uffff\2\44\3\uffff\2\44",
            "",
            "\1\47\1\uffff\12\4\10\uffff\1\47\1\uffff\3\47\21\uffff\1\46"+
            "\13\uffff\1\50\2\47\1\uffff\1\52\4\uffff\1\52\5\uffff\1\52\4"+
            "\uffff\1\46",
            "\12\47",
            "\1\47\1\uffff\12\4\10\uffff\1\47\1\uffff\3\47\35\uffff\1\50"+
            "\2\47\1\uffff\1\52\4\uffff\1\52\5\uffff\1\52",
            "",
            "\0\5",
            "\1\54",
            "\1\55",
            "\1\56",
            "",
            "\1\60\21\uffff\1\57",
            "\1\63\17\uffff\1\62\1\64",
            "\1\66",
            "\1\72\4\uffff\1\71\15\uffff\1\70",
            "\1\75\26\uffff\1\74",
            "\1\77\76\uffff\1\100",
            "\1\102",
            "\1\104",
            "\1\106",
            "",
            "\1\110",
            "\1\112",
            "\1\114",
            "\1\116",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\52",
            "",
            "",
            "",
            "\1\120",
            "\1\121",
            "\1\122",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\123",
            "\1\124",
            "\1\125",
            "\11\44\5\uffff\16\44\10\uffff\1\44\13\uffff\12\44\7\uffff\32"+
            "\44\4\uffff\1\44\1\uffff\32\44\4\uffff\41\44\2\uffff\4\44\4"+
            "\uffff\1\44\2\uffff\1\44\7\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\21\uffff\130\44\5\uffff\23\44"+
            "\12\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff"+
            "\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082"+
            "\44\1\uffff\4\44\3\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\11\uffff"+
            "\21\44\1\uffff\27\44\1\uffff\3\44\1\uffff\1\44\1\uffff\2\44"+
            "\1\uffff\1\44\13\uffff\33\44\5\uffff\3\44\15\uffff\4\44\14\uffff"+
            "\6\44\13\uffff\32\44\5\uffff\31\44\7\uffff\12\44\4\uffff\146"+
            "\44\1\uffff\11\44\1\uffff\12\44\1\uffff\23\44\2\uffff\1\44\17"+
            "\uffff\74\44\2\uffff\3\44\60\uffff\62\44\u014f\uffff\71\44\2"+
            "\uffff\22\44\2\uffff\5\44\3\uffff\14\44\2\uffff\12\44\21\uffff"+
            "\3\44\1\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\1\44\3\uffff\4\44\2\uffff\11\44\2\uffff\2\44\2\uffff"+
            "\3\44\11\uffff\1\44\4\uffff\2\44\1\uffff\5\44\2\uffff\16\44"+
            "\15\uffff\3\44\1\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\2\uffff\1\44\1"+
            "\uffff\5\44\4\uffff\2\44\2\uffff\3\44\13\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\17\44\14\uffff\3\44\1\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2\uffff"+
            "\12\44\1\uffff\3\44\1\uffff\3\44\2\uffff\1\44\17\uffff\4\44"+
            "\2\uffff\12\44\1\uffff\1\44\17\uffff\3\44\1\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2"+
            "\uffff\10\44\3\uffff\2\44\2\uffff\3\44\10\uffff\2\44\4\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\20\uffff\2\44"+
            "\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff"+
            "\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1"+
            "\uffff\3\44\4\uffff\5\44\3\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\17\uffff\11\44\11\uffff\1\44\7\uffff\3\44\1\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\4\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\4\44\7\uffff\2\44\11\uffff\2\44\4"+
            "\uffff\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\2\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\4\44\7\uffff\2\44\7\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\4\uffff\6\44\2\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\10\uffff\2\44\4\uffff\12\44\22\uffff\2\44\1\uffff\22\44"+
            "\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\3\uffff"+
            "\1\44\4\uffff\6\44\1\uffff\1\44\1\uffff\10\44\22\uffff\2\44"+
            "\15\uffff\72\44\4\uffff\20\44\1\uffff\12\44\47\uffff\2\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4"+
            "\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\15\44\1\uffff\3\44\2\uffff\5\44\1\uffff\1\44\1"+
            "\uffff\6\44\2\uffff\12\44\2\uffff\2\44\42\uffff\1\44\27\uffff"+
            "\2\44\6\uffff\12\44\13\uffff\1\44\1\uffff\1\44\1\uffff\1\44"+
            "\4\uffff\12\44\1\uffff\42\44\6\uffff\24\44\1\uffff\6\44\4\uffff"+
            "\10\44\1\uffff\44\44\11\uffff\1\44\71\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\1\uffff\7\44\3\uffff\4\44\6\uffff\12\44\6\uffff"+
            "\12\44\106\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\3\uffff\3\44\17\uffff\15\44\1\uffff\7"+
            "\44\13\uffff\25\44\13\uffff\24\44\14\uffff\15\44\1\uffff\3\44"+
            "\1\uffff\2\44\14\uffff\124\44\3\uffff\1\44\3\uffff\3\44\2\uffff"+
            "\12\44\41\uffff\3\44\2\uffff\12\44\6\uffff\130\44\10\uffff\52"+
            "\44\126\uffff\35\44\3\uffff\14\44\4\uffff\14\44\12\uffff\50"+
            "\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff"+
            "\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44"+
            "\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44"+
            "\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff"+
            "\3\44\1\uffff\7\44\17\uffff\4\44\32\uffff\5\44\20\uffff\2\44"+
            "\23\uffff\1\44\13\uffff\4\44\6\uffff\6\44\1\uffff\1\44\15\uffff"+
            "\1\44\40\uffff\22\44\36\uffff\15\44\4\uffff\1\44\3\uffff\6\44"+
            "\27\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff"+
            "\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1"+
            "\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\26\uffff"+
            "\44\44\u0e81\uffff\3\44\31\uffff\17\44\1\uffff\5\44\2\uffff"+
            "\5\44\4\uffff\126\44\2\uffff\2\44\2\uffff\3\44\1\uffff\137\44"+
            "\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44"+
            "\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44"+
            "\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095"+
            "\uffff\7\44\14\uffff\5\44\5\uffff\14\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\15"+
            "\44\3\uffff\20\44\20\uffff\4\44\17\uffff\2\44\30\uffff\3\44"+
            "\31\uffff\1\44\6\uffff\5\44\1\uffff\u0087\44\2\uffff\1\44\4"+
            "\uffff\1\44\13\uffff\12\44\7\uffff\32\44\4\uffff\1\44\1\uffff"+
            "\32\44\12\uffff\132\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\3\44\3\uffff\2\44\3\uffff\2\44\22\uffff\3\44",
            "\1\127",
            "\11\44\5\uffff\16\44\10\uffff\1\44\13\uffff\12\44\7\uffff\32"+
            "\44\4\uffff\1\44\1\uffff\32\44\4\uffff\41\44\2\uffff\4\44\4"+
            "\uffff\1\44\2\uffff\1\44\7\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\21\uffff\130\44\5\uffff\23\44"+
            "\12\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff"+
            "\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082"+
            "\44\1\uffff\4\44\3\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\11\uffff"+
            "\21\44\1\uffff\27\44\1\uffff\3\44\1\uffff\1\44\1\uffff\2\44"+
            "\1\uffff\1\44\13\uffff\33\44\5\uffff\3\44\15\uffff\4\44\14\uffff"+
            "\6\44\13\uffff\32\44\5\uffff\31\44\7\uffff\12\44\4\uffff\146"+
            "\44\1\uffff\11\44\1\uffff\12\44\1\uffff\23\44\2\uffff\1\44\17"+
            "\uffff\74\44\2\uffff\3\44\60\uffff\62\44\u014f\uffff\71\44\2"+
            "\uffff\22\44\2\uffff\5\44\3\uffff\14\44\2\uffff\12\44\21\uffff"+
            "\3\44\1\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\1\44\3\uffff\4\44\2\uffff\11\44\2\uffff\2\44\2\uffff"+
            "\3\44\11\uffff\1\44\4\uffff\2\44\1\uffff\5\44\2\uffff\16\44"+
            "\15\uffff\3\44\1\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\2\uffff\1\44\1"+
            "\uffff\5\44\4\uffff\2\44\2\uffff\3\44\13\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\17\44\14\uffff\3\44\1\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2\uffff"+
            "\12\44\1\uffff\3\44\1\uffff\3\44\2\uffff\1\44\17\uffff\4\44"+
            "\2\uffff\12\44\1\uffff\1\44\17\uffff\3\44\1\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2"+
            "\uffff\10\44\3\uffff\2\44\2\uffff\3\44\10\uffff\2\44\4\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\20\uffff\2\44"+
            "\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff"+
            "\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1"+
            "\uffff\3\44\4\uffff\5\44\3\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\17\uffff\11\44\11\uffff\1\44\7\uffff\3\44\1\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\4\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\4\44\7\uffff\2\44\11\uffff\2\44\4"+
            "\uffff\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\2\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\4\44\7\uffff\2\44\7\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\4\uffff\6\44\2\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\10\uffff\2\44\4\uffff\12\44\22\uffff\2\44\1\uffff\22\44"+
            "\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\3\uffff"+
            "\1\44\4\uffff\6\44\1\uffff\1\44\1\uffff\10\44\22\uffff\2\44"+
            "\15\uffff\72\44\4\uffff\20\44\1\uffff\12\44\47\uffff\2\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4"+
            "\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\15\44\1\uffff\3\44\2\uffff\5\44\1\uffff\1\44\1"+
            "\uffff\6\44\2\uffff\12\44\2\uffff\2\44\42\uffff\1\44\27\uffff"+
            "\2\44\6\uffff\12\44\13\uffff\1\44\1\uffff\1\44\1\uffff\1\44"+
            "\4\uffff\12\44\1\uffff\42\44\6\uffff\24\44\1\uffff\6\44\4\uffff"+
            "\10\44\1\uffff\44\44\11\uffff\1\44\71\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\1\uffff\7\44\3\uffff\4\44\6\uffff\12\44\6\uffff"+
            "\12\44\106\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\3\uffff\3\44\17\uffff\15\44\1\uffff\7"+
            "\44\13\uffff\25\44\13\uffff\24\44\14\uffff\15\44\1\uffff\3\44"+
            "\1\uffff\2\44\14\uffff\124\44\3\uffff\1\44\3\uffff\3\44\2\uffff"+
            "\12\44\41\uffff\3\44\2\uffff\12\44\6\uffff\130\44\10\uffff\52"+
            "\44\126\uffff\35\44\3\uffff\14\44\4\uffff\14\44\12\uffff\50"+
            "\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff"+
            "\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44"+
            "\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44"+
            "\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff"+
            "\3\44\1\uffff\7\44\17\uffff\4\44\32\uffff\5\44\20\uffff\2\44"+
            "\23\uffff\1\44\13\uffff\4\44\6\uffff\6\44\1\uffff\1\44\15\uffff"+
            "\1\44\40\uffff\22\44\36\uffff\15\44\4\uffff\1\44\3\uffff\6\44"+
            "\27\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff"+
            "\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1"+
            "\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\26\uffff"+
            "\44\44\u0e81\uffff\3\44\31\uffff\17\44\1\uffff\5\44\2\uffff"+
            "\5\44\4\uffff\126\44\2\uffff\2\44\2\uffff\3\44\1\uffff\137\44"+
            "\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44"+
            "\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44"+
            "\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095"+
            "\uffff\7\44\14\uffff\5\44\5\uffff\14\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\15"+
            "\44\3\uffff\20\44\20\uffff\4\44\17\uffff\2\44\30\uffff\3\44"+
            "\31\uffff\1\44\6\uffff\5\44\1\uffff\u0087\44\2\uffff\1\44\4"+
            "\uffff\1\44\13\uffff\12\44\7\uffff\32\44\4\uffff\1\44\1\uffff"+
            "\32\44\12\uffff\132\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\3\44\3\uffff\2\44\3\uffff\2\44\22\uffff\3\44",
            "",
            "\11\44\5\uffff\16\44\10\uffff\1\44\13\uffff\12\44\7\uffff\32"+
            "\44\4\uffff\1\44\1\uffff\32\44\4\uffff\41\44\2\uffff\4\44\4"+
            "\uffff\1\44\2\uffff\1\44\7\uffff\1\44\4\uffff\1\44\5\uffff\27"+
            "\44\1\uffff\37\44\1\uffff\u013f\44\31\uffff\162\44\4\uffff\14"+
            "\44\16\uffff\5\44\11\uffff\1\44\21\uffff\130\44\5\uffff\23\44"+
            "\12\uffff\1\44\13\uffff\1\44\1\uffff\3\44\1\uffff\1\44\1\uffff"+
            "\24\44\1\uffff\54\44\1\uffff\46\44\1\uffff\5\44\4\uffff\u0082"+
            "\44\1\uffff\4\44\3\uffff\105\44\1\uffff\46\44\2\uffff\2\44\6"+
            "\uffff\20\44\41\uffff\46\44\2\uffff\1\44\7\uffff\47\44\11\uffff"+
            "\21\44\1\uffff\27\44\1\uffff\3\44\1\uffff\1\44\1\uffff\2\44"+
            "\1\uffff\1\44\13\uffff\33\44\5\uffff\3\44\15\uffff\4\44\14\uffff"+
            "\6\44\13\uffff\32\44\5\uffff\31\44\7\uffff\12\44\4\uffff\146"+
            "\44\1\uffff\11\44\1\uffff\12\44\1\uffff\23\44\2\uffff\1\44\17"+
            "\uffff\74\44\2\uffff\3\44\60\uffff\62\44\u014f\uffff\71\44\2"+
            "\uffff\22\44\2\uffff\5\44\3\uffff\14\44\2\uffff\12\44\21\uffff"+
            "\3\44\1\uffff\10\44\2\uffff\2\44\2\uffff\26\44\1\uffff\7\44"+
            "\1\uffff\1\44\3\uffff\4\44\2\uffff\11\44\2\uffff\2\44\2\uffff"+
            "\3\44\11\uffff\1\44\4\uffff\2\44\1\uffff\5\44\2\uffff\16\44"+
            "\15\uffff\3\44\1\uffff\6\44\4\uffff\2\44\2\uffff\26\44\1\uffff"+
            "\7\44\1\uffff\2\44\1\uffff\2\44\1\uffff\2\44\2\uffff\1\44\1"+
            "\uffff\5\44\4\uffff\2\44\2\uffff\3\44\13\uffff\4\44\1\uffff"+
            "\1\44\7\uffff\17\44\14\uffff\3\44\1\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2\uffff"+
            "\12\44\1\uffff\3\44\1\uffff\3\44\2\uffff\1\44\17\uffff\4\44"+
            "\2\uffff\12\44\1\uffff\1\44\17\uffff\3\44\1\uffff\10\44\2\uffff"+
            "\2\44\2\uffff\26\44\1\uffff\7\44\1\uffff\2\44\1\uffff\5\44\2"+
            "\uffff\10\44\3\uffff\2\44\2\uffff\3\44\10\uffff\2\44\4\uffff"+
            "\2\44\1\uffff\3\44\4\uffff\12\44\1\uffff\1\44\20\uffff\2\44"+
            "\1\uffff\6\44\3\uffff\3\44\1\uffff\4\44\3\uffff\2\44\1\uffff"+
            "\1\44\1\uffff\2\44\3\uffff\2\44\3\uffff\3\44\3\uffff\10\44\1"+
            "\uffff\3\44\4\uffff\5\44\3\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\17\uffff\11\44\11\uffff\1\44\7\uffff\3\44\1\uffff\10\44"+
            "\1\uffff\3\44\1\uffff\27\44\1\uffff\12\44\1\uffff\5\44\4\uffff"+
            "\7\44\1\uffff\3\44\1\uffff\4\44\7\uffff\2\44\11\uffff\2\44\4"+
            "\uffff\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff"+
            "\27\44\1\uffff\12\44\1\uffff\5\44\2\uffff\11\44\1\uffff\3\44"+
            "\1\uffff\4\44\7\uffff\2\44\7\uffff\1\44\1\uffff\2\44\4\uffff"+
            "\12\44\22\uffff\2\44\1\uffff\10\44\1\uffff\3\44\1\uffff\27\44"+
            "\1\uffff\20\44\4\uffff\6\44\2\uffff\3\44\1\uffff\4\44\11\uffff"+
            "\1\44\10\uffff\2\44\4\uffff\12\44\22\uffff\2\44\1\uffff\22\44"+
            "\3\uffff\30\44\1\uffff\11\44\1\uffff\1\44\2\uffff\7\44\3\uffff"+
            "\1\44\4\uffff\6\44\1\uffff\1\44\1\uffff\10\44\22\uffff\2\44"+
            "\15\uffff\72\44\4\uffff\20\44\1\uffff\12\44\47\uffff\2\44\1"+
            "\uffff\1\44\2\uffff\2\44\1\uffff\1\44\2\uffff\1\44\6\uffff\4"+
            "\44\1\uffff\7\44\1\uffff\3\44\1\uffff\1\44\1\uffff\1\44\2\uffff"+
            "\2\44\1\uffff\15\44\1\uffff\3\44\2\uffff\5\44\1\uffff\1\44\1"+
            "\uffff\6\44\2\uffff\12\44\2\uffff\2\44\42\uffff\1\44\27\uffff"+
            "\2\44\6\uffff\12\44\13\uffff\1\44\1\uffff\1\44\1\uffff\1\44"+
            "\4\uffff\12\44\1\uffff\42\44\6\uffff\24\44\1\uffff\6\44\4\uffff"+
            "\10\44\1\uffff\44\44\11\uffff\1\44\71\uffff\42\44\1\uffff\5"+
            "\44\1\uffff\2\44\1\uffff\7\44\3\uffff\4\44\6\uffff\12\44\6\uffff"+
            "\12\44\106\uffff\46\44\12\uffff\51\44\7\uffff\132\44\5\uffff"+
            "\104\44\5\uffff\122\44\6\uffff\7\44\1\uffff\77\44\1\uffff\1"+
            "\44\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\47\44\1\uffff\1\44\1\uffff\4\44\2\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\1\44\1\uffff\4\44\2\uffff"+
            "\7\44\1\uffff\7\44\1\uffff\27\44\1\uffff\37\44\1\uffff\1\44"+
            "\1\uffff\4\44\2\uffff\7\44\1\uffff\47\44\1\uffff\23\44\16\uffff"+
            "\11\44\56\uffff\125\44\14\uffff\u026c\44\2\uffff\10\44\12\uffff"+
            "\32\44\5\uffff\113\44\3\uffff\3\44\17\uffff\15\44\1\uffff\7"+
            "\44\13\uffff\25\44\13\uffff\24\44\14\uffff\15\44\1\uffff\3\44"+
            "\1\uffff\2\44\14\uffff\124\44\3\uffff\1\44\3\uffff\3\44\2\uffff"+
            "\12\44\41\uffff\3\44\2\uffff\12\44\6\uffff\130\44\10\uffff\52"+
            "\44\126\uffff\35\44\3\uffff\14\44\4\uffff\14\44\12\uffff\50"+
            "\44\2\uffff\5\44\u038b\uffff\154\44\u0094\uffff\u009c\44\4\uffff"+
            "\132\44\6\uffff\26\44\2\uffff\6\44\2\uffff\46\44\2\uffff\6\44"+
            "\2\uffff\10\44\1\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff"+
            "\37\44\2\uffff\65\44\1\uffff\7\44\1\uffff\1\44\3\uffff\3\44"+
            "\1\uffff\7\44\3\uffff\4\44\2\uffff\6\44\4\uffff\15\44\5\uffff"+
            "\3\44\1\uffff\7\44\17\uffff\4\44\32\uffff\5\44\20\uffff\2\44"+
            "\23\uffff\1\44\13\uffff\4\44\6\uffff\6\44\1\uffff\1\44\15\uffff"+
            "\1\44\40\uffff\22\44\36\uffff\15\44\4\uffff\1\44\3\uffff\6\44"+
            "\27\uffff\1\44\4\uffff\1\44\2\uffff\12\44\1\uffff\1\44\3\uffff"+
            "\5\44\6\uffff\1\44\1\uffff\1\44\1\uffff\1\44\1\uffff\4\44\1"+
            "\uffff\3\44\1\uffff\7\44\3\uffff\3\44\5\uffff\5\44\26\uffff"+
            "\44\44\u0e81\uffff\3\44\31\uffff\17\44\1\uffff\5\44\2\uffff"+
            "\5\44\4\uffff\126\44\2\uffff\2\44\2\uffff\3\44\1\uffff\137\44"+
            "\5\uffff\50\44\4\uffff\136\44\21\uffff\30\44\70\uffff\20\44"+
            "\u0200\uffff\u19b6\44\112\uffff\u51a6\44\132\uffff\u048d\44"+
            "\u0773\uffff\u2ba4\44\u215c\uffff\u012e\44\2\uffff\73\44\u0095"+
            "\uffff\7\44\14\uffff\5\44\5\uffff\14\44\1\uffff\15\44\1\uffff"+
            "\5\44\1\uffff\1\44\1\uffff\2\44\1\uffff\2\44\1\uffff\154\44"+
            "\41\uffff\u016b\44\22\uffff\100\44\2\uffff\66\44\50\uffff\15"+
            "\44\3\uffff\20\44\20\uffff\4\44\17\uffff\2\44\30\uffff\3\44"+
            "\31\uffff\1\44\6\uffff\5\44\1\uffff\u0087\44\2\uffff\1\44\4"+
            "\uffff\1\44\13\uffff\12\44\7\uffff\32\44\4\uffff\1\44\1\uffff"+
            "\32\44\12\uffff\132\44\3\uffff\6\44\2\uffff\6\44\2\uffff\6\44"+
            "\2\uffff\3\44\3\uffff\2\44\3\uffff\2\44\22\uffff\3\44",
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
            return "1:1: Tokens : ( WS | FLOAT | HEX | DECIMAL | STRING | TIME_INTERVAL | BOOL | NULL | AT | PLUS_ASSIGN | MINUS_ASSIGN | MULT_ASSIGN | DIV_ASSIGN | AND_ASSIGN | OR_ASSIGN | XOR_ASSIGN | MOD_ASSIGN | UNIFY | DECR | INCR | ARROW | SEMICOLON | COLON | EQUALS | NOT_EQUALS | GREATER_EQUALS | LESS_EQUALS | GREATER | LESS | EQUALS_ASSIGN | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | QUESTION | NEGATION | TILDE | PIPE | AMPER | XOR | MOD | STAR | MINUS | PLUS | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | ID | DIV | MISC );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_6 = input.LA(1);

                        s = -1;
                        if ( ((LA63_6>='\u0000' && LA63_6<='\uFFFF')) ) {s = 5;}

                        else s = 37;

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
 

}