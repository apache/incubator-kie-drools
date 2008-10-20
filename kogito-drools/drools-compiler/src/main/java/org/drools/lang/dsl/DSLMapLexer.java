// $ANTLR 3.0.1 src/main/resources/org/drools/lang/dsl/DSLMap.g 2008-10-20 12:49:42

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
    public static final int Tokens=36;
    public static final int VT_ENTRY_KEY=13;
    public static final int VT_SCOPE=7;
    public static final int COLON=28;
    public static final int VT_KEYWORD=10;
    public static final int VT_QUAL=19;
    public static final int VT_VAR_REF=16;
    public static final int LEFT_CURLY=29;
    public static final int POUND=34;
    public static final int RIGHT_SQUARE=25;
    public DSLMapLexer() {;} 
    public DSLMapLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[17+1];
     }
    public String getGrammarFileName() { return "src/main/resources/org/drools/lang/dsl/DSLMap.g"; }

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
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
            int _type = EOL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:276:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("277:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:278:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // src/main/resources/org/drools/lang/dsl/DSLMap.g:279:25: '\\n'
                    {
                    match('\n'); if (failed) return ;

                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EOL

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:285:5: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' | '=' | 'u' | '0' | '#' ) )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:285:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' | '=' | 'u' | '0' | '#' )
            {
            match('\\'); if (failed) return ;
            if ( (input.LA(1)>='\"' && input.LA(1)<='$')||(input.LA(1)>='&' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)=='0'||input.LA(1)=='='||input.LA(1)=='?'||(input.LA(1)>='A' && input.LA(1)<='B')||(input.LA(1)>='D' && input.LA(1)<='E')||input.LA(1)=='G'||input.LA(1)=='Q'||input.LA(1)=='S'||input.LA(1)=='W'||(input.LA(1)>='Z' && input.LA(1)<='^')||(input.LA(1)>='a' && input.LA(1)<='f')||(input.LA(1)>='n' && input.LA(1)<='p')||(input.LA(1)>='r' && input.LA(1)<='u')||(input.LA(1)>='w' && input.LA(1)<='x')||(input.LA(1)>='z' && input.LA(1)<='}') ) {
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
    // $ANTLR end EscapeSequence

    // $ANTLR start LEFT_SQUARE
    public final void mLEFT_SQUARE() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:292:9: ( '[' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:292:11: '['
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
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:296:9: ( ']' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:296:11: ']'
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
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:300:9: ( '{' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:300:11: '{'
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
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:304:9: ( '}' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:304:11: '}'
            {
            match('}'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:307:8: ( '=' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:307:10: '='
            {
            match('='); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:310:5: ( '.' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:310:7: '.'
            {
            match('.'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start POUND
    public final void mPOUND() throws RecognitionException {
        try {
            int _type = POUND;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:313:9: ( '#' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:313:11: '#'
            {
            match('#'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end POUND

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:316:7: ( ':' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:316:9: ':'
            {
            match(':'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:319:7: ( ',' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:319:9: ','
            {
            match(','); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start LINE_COMMENT
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:2: ( POUND ( options {greedy=false; } : . )* EOL )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:4: POUND ( options {greedy=false; } : . )* EOL
            {
            mPOUND(); if (failed) return ;
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
                else if ( ((LA3_0>='\u0000' && LA3_0<='\t')||(LA3_0>='\u000B' && LA3_0<='\f')||(LA3_0>='\u000E' && LA3_0<='\uFFFE')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:327:37: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            mEOL(); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LINE_COMMENT

    // $ANTLR start LITERAL
    public final void mLITERAL() throws RecognitionException {
        try {
            int _type = LITERAL;
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+ )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+
            {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' | MISC | EscapeSequence | DOT )+
            int cnt4=0;
            loop4:
            do {
                int alt4=9;
                switch ( input.LA(1) ) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt4=1;
                    }
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                    {
                    alt4=2;
                    }
                    break;
                case '_':
                    {
                    alt4=3;
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
                case '8':
                case '9':
                    {
                    alt4=4;
                    }
                    break;
                case '\u00C0':
                case '\u00C1':
                case '\u00C2':
                case '\u00C3':
                case '\u00C4':
                case '\u00C5':
                case '\u00C6':
                case '\u00C7':
                case '\u00C8':
                case '\u00C9':
                case '\u00CA':
                case '\u00CB':
                case '\u00CC':
                case '\u00CD':
                case '\u00CE':
                case '\u00CF':
                case '\u00D0':
                case '\u00D1':
                case '\u00D2':
                case '\u00D3':
                case '\u00D4':
                case '\u00D5':
                case '\u00D6':
                case '\u00D7':
                case '\u00D8':
                case '\u00D9':
                case '\u00DA':
                case '\u00DB':
                case '\u00DC':
                case '\u00DD':
                case '\u00DE':
                case '\u00DF':
                case '\u00E0':
                case '\u00E1':
                case '\u00E2':
                case '\u00E3':
                case '\u00E4':
                case '\u00E5':
                case '\u00E6':
                case '\u00E7':
                case '\u00E8':
                case '\u00E9':
                case '\u00EA':
                case '\u00EB':
                case '\u00EC':
                case '\u00ED':
                case '\u00EE':
                case '\u00EF':
                case '\u00F0':
                case '\u00F1':
                case '\u00F2':
                case '\u00F3':
                case '\u00F4':
                case '\u00F5':
                case '\u00F6':
                case '\u00F7':
                case '\u00F8':
                case '\u00F9':
                case '\u00FA':
                case '\u00FB':
                case '\u00FC':
                case '\u00FD':
                case '\u00FE':
                case '\u00FF':
                    {
                    alt4=5;
                    }
                    break;
                case '!':
                case '\"':
                case '$':
                case '%':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case ',':
                case '-':
                case '/':
                case ';':
                case '<':
                case '>':
                case '?':
                case '@':
                case '^':
                case '|':
                    {
                    alt4=6;
                    }
                    break;
                case '\\':
                    {
                    alt4=7;
                    }
                    break;
                case '.':
                    {
                    alt4=8;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:5: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); if (failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:14: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:23: '_'
            	    {
            	    match('_'); if (failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:27: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:36: '\\u00c0' .. '\\u00ff'
            	    {
            	    matchRange('\u00C0','\u00FF'); if (failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:55: MISC
            	    {
            	    mMISC(); if (failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:60: EscapeSequence
            	    {
            	    mEscapeSequence(); if (failed) return ;

            	    }
            	    break;
            	case 8 :
            	    // src/main/resources/org/drools/lang/dsl/DSLMap.g:335:75: DOT
            	    {
            	    mDOT(); if (failed) return ;

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
    // $ANTLR end LITERAL

    // $ANTLR start MISC
    public final void mMISC() throws RecognitionException {
        try {
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:339:7: ( '>' | '<' | '!' | '@' | '$' | '%' | '^' | '*' | '-' | '+' | '?' | COMMA | '/' | '\\'' | '\"' | '|' | '&' | '(' | ')' | ';' )
            // src/main/resources/org/drools/lang/dsl/DSLMap.g:
            {
            if ( (input.LA(1)>='!' && input.LA(1)<='\"')||(input.LA(1)>='$' && input.LA(1)<='-')||input.LA(1)=='/'||(input.LA(1)>=';' && input.LA(1)<='<')||(input.LA(1)>='>' && input.LA(1)<='@')||input.LA(1)=='^'||input.LA(1)=='|' ) {
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
    // $ANTLR end MISC

    public void mTokens() throws RecognitionException {
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:8: ( WS | EOL | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | EQUALS | DOT | POUND | COLON | COMMA | LINE_COMMENT | LITERAL )
        int alt5=13;
        switch ( input.LA(1) ) {
        case '\t':
        case '\f':
        case ' ':
            {
            alt5=1;
            }
            break;
        case '\n':
        case '\r':
            {
            alt5=2;
            }
            break;
        case '[':
            {
            alt5=3;
            }
            break;
        case ']':
            {
            alt5=4;
            }
            break;
        case '{':
            {
            alt5=5;
            }
            break;
        case '}':
            {
            alt5=6;
            }
            break;
        case '=':
            {
            alt5=7;
            }
            break;
        case '.':
            {
            int LA5_8 = input.LA(2);

            if ( ((LA5_8>='!' && LA5_8<='\"')||(LA5_8>='$' && LA5_8<='9')||(LA5_8>=';' && LA5_8<='<')||(LA5_8>='>' && LA5_8<='Z')||LA5_8=='\\'||(LA5_8>='^' && LA5_8<='_')||(LA5_8>='a' && LA5_8<='z')||LA5_8=='|'||(LA5_8>='\u00C0' && LA5_8<='\u00FF')) ) {
                alt5=13;
            }
            else {
                alt5=8;}
            }
            break;
        case '#':
            {
            int LA5_9 = input.LA(2);

            if ( ((LA5_9>='\u0000' && LA5_9<='\uFFFE')) ) {
                alt5=12;
            }
            else {
                alt5=9;}
            }
            break;
        case ':':
            {
            alt5=10;
            }
            break;
        case ',':
            {
            int LA5_11 = input.LA(2);

            if ( ((LA5_11>='!' && LA5_11<='\"')||(LA5_11>='$' && LA5_11<='9')||(LA5_11>=';' && LA5_11<='<')||(LA5_11>='>' && LA5_11<='Z')||LA5_11=='\\'||(LA5_11>='^' && LA5_11<='_')||(LA5_11>='a' && LA5_11<='z')||LA5_11=='|'||(LA5_11>='\u00C0' && LA5_11<='\u00FF')) ) {
                alt5=13;
            }
            else {
                alt5=11;}
            }
            break;
        case '!':
        case '\"':
        case '$':
        case '%':
        case '&':
        case '\'':
        case '(':
        case ')':
        case '*':
        case '+':
        case '-':
        case '/':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case ';':
        case '<':
        case '>':
        case '?':
        case '@':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '\\':
        case '^':
        case '_':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
        case '|':
        case '\u00C0':
        case '\u00C1':
        case '\u00C2':
        case '\u00C3':
        case '\u00C4':
        case '\u00C5':
        case '\u00C6':
        case '\u00C7':
        case '\u00C8':
        case '\u00C9':
        case '\u00CA':
        case '\u00CB':
        case '\u00CC':
        case '\u00CD':
        case '\u00CE':
        case '\u00CF':
        case '\u00D0':
        case '\u00D1':
        case '\u00D2':
        case '\u00D3':
        case '\u00D4':
        case '\u00D5':
        case '\u00D6':
        case '\u00D7':
        case '\u00D8':
        case '\u00D9':
        case '\u00DA':
        case '\u00DB':
        case '\u00DC':
        case '\u00DD':
        case '\u00DE':
        case '\u00DF':
        case '\u00E0':
        case '\u00E1':
        case '\u00E2':
        case '\u00E3':
        case '\u00E4':
        case '\u00E5':
        case '\u00E6':
        case '\u00E7':
        case '\u00E8':
        case '\u00E9':
        case '\u00EA':
        case '\u00EB':
        case '\u00EC':
        case '\u00ED':
        case '\u00EE':
        case '\u00EF':
        case '\u00F0':
        case '\u00F1':
        case '\u00F2':
        case '\u00F3':
        case '\u00F4':
        case '\u00F5':
        case '\u00F6':
        case '\u00F7':
        case '\u00F8':
        case '\u00F9':
        case '\u00FA':
        case '\u00FB':
        case '\u00FC':
        case '\u00FD':
        case '\u00FE':
        case '\u00FF':
            {
            alt5=13;
            }
            break;
        default:
            if (backtracking>0) {failed=true; return ;}
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( WS | EOL | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | EQUALS | DOT | POUND | COLON | COMMA | LINE_COMMENT | LITERAL );", 5, 0, input);

            throw nvae;
        }

        switch (alt5) {
            case 1 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:10: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 2 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:13: EOL
                {
                mEOL(); if (failed) return ;

                }
                break;
            case 3 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:17: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 4 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:29: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 5 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:42: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 6 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:53: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 7 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:65: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 8 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:72: DOT
                {
                mDOT(); if (failed) return ;

                }
                break;
            case 9 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:76: POUND
                {
                mPOUND(); if (failed) return ;

                }
                break;
            case 10 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:82: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 11 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:88: COMMA
                {
                mCOMMA(); if (failed) return ;

                }
                break;
            case 12 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:94: LINE_COMMENT
                {
                mLINE_COMMENT(); if (failed) return ;

                }
                break;
            case 13 :
                // src/main/resources/org/drools/lang/dsl/DSLMap.g:1:107: LITERAL
                {
                mLITERAL(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:14: ( '\\r\\n' )
        // src/main/resources/org/drools/lang/dsl/DSLMap.g:277:16: '\\r\\n'
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


 

}