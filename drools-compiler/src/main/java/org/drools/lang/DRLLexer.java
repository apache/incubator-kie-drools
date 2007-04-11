// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-04-11 16:59:52

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int T75=75;
    public static final int PACKAGE=4;
    public static final int FUNCTION=6;
    public static final int ACCUMULATE=28;
    public static final int T76=76;
    public static final int RIGHT_SQUARE=45;
    public static final int T73=73;
    public static final int T74=74;
    public static final int ACTIVATION_GROUP=23;
    public static final int T77=77;
    public static final int RIGHT_CURLY=43;
    public static final int ATTRIBUTES=13;
    public static final int T78=78;
    public static final int CONTAINS=37;
    public static final int NO_LOOP=21;
    public static final int LOCK_ON_ACTIVE=27;
    public static final int AGENDA_GROUP=25;
    public static final int FLOAT=40;
    public static final int NOT=49;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=59;
    public static final int ID=33;
    public static final int AND=46;
    public static final int EOF=-1;
    public static final int HexDigit=56;
    public static final int DATE_EFFECTIVE=14;
    public static final int T72=72;
    public static final int ACTION=30;
    public static final int T71=71;
    public static final int T70=70;
    public static final int T63=63;
    public static final int T64=64;
    public static final int RIGHT_PAREN=36;
    public static final int T65=65;
    public static final int IMPORT=5;
    public static final int T66=66;
    public static final int EOL=53;
    public static final int T67=67;
    public static final int THEN=52;
    public static final int T68=68;
    public static final int T69=69;
    public static final int MATCHES=38;
    public static final int ENABLED=17;
    public static final int EXISTS=48;
    public static final int RULE=11;
    public static final int EXCLUDES=39;
    public static final int AUTO_FOCUS=22;
    public static final int NULL=41;
    public static final int BOOL=18;
    public static final int FORALL=51;
    public static final int SALIENCE=19;
    public static final int RULEFLOW_GROUP=24;
    public static final int RESULT=31;
    public static final int INT=20;
    public static final int Tokens=79;
    public static final int MULTI_LINE_COMMENT=61;
    public static final int DURATION=26;
    public static final int WS=54;
    public static final int TEMPLATE=10;
    public static final int EVAL=50;
    public static final int WHEN=12;
    public static final int UnicodeEscape=57;
    public static final int LEFT_CURLY=42;
    public static final int OR=34;
    public static final int LEFT_PAREN=35;
    public static final int QUERY=8;
    public static final int GLOBAL=7;
    public static final int END=9;
    public static final int FROM=47;
    public static final int MISC=62;
    public static final int COLLECT=32;
    public static final int INIT=29;
    public static final int LEFT_SQUARE=44;
    public static final int EscapeSequence=55;
    public static final int OctalEscape=58;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=60;
    public static final int STRING=15;
    public static final int DATE_EXPIRES=16;
    public DRLLexer() {;} 
    public DRLLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[77+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    // $ANTLR start T63
    public void mT63() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T63;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:6:7: ( ';' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:6:7: ';'
            {
            match(';'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T63

    // $ANTLR start T64
    public void mT64() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T64;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:7:7: ( '.' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:7:7: '.'
            {
            match('.'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T64

    // $ANTLR start T65
    public void mT65() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T65;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:8:7: ( '.*' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:8:7: '.*'
            {
            match(".*"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T65

    // $ANTLR start T66
    public void mT66() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T66;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:9:7: ( ',' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:9:7: ','
            {
            match(','); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T66

    // $ANTLR start T67
    public void mT67() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T67;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:10:7: ( ':' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:10:7: ':'
            {
            match(':'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T67

    // $ANTLR start T68
    public void mT68() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T68;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:11:7: ( '||' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:11:7: '||'
            {
            match("||"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T68

    // $ANTLR start T69
    public void mT69() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T69;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:12:7: ( '&' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:12:7: '&'
            {
            match('&'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T69

    // $ANTLR start T70
    public void mT70() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T70;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:13:7: ( '|' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:13:7: '|'
            {
            match('|'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T70

    // $ANTLR start T71
    public void mT71() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T71;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:14:7: ( '->' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:14:7: '->'
            {
            match("->"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T71

    // $ANTLR start T72
    public void mT72() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T72;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:15:7: ( '==' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:15:7: '=='
            {
            match("=="); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T72

    // $ANTLR start T73
    public void mT73() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T73;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:16:7: ( '>' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:16:7: '>'
            {
            match('>'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T73

    // $ANTLR start T74
    public void mT74() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T74;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:17:7: ( '>=' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:17:7: '>='
            {
            match(">="); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T74

    // $ANTLR start T75
    public void mT75() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T75;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:18:7: ( '<' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:18:7: '<'
            {
            match('<'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T75

    // $ANTLR start T76
    public void mT76() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T76;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:19:7: ( '<=' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:19:7: '<='
            {
            match("<="); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T76

    // $ANTLR start T77
    public void mT77() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T77;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:20:7: ( '!=' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:20:7: '!='
            {
            match("!="); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T77

    // $ANTLR start T78
    public void mT78() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T78;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:21:7: ( '&&' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:21:7: '&&'
            {
            match("&&"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end T78

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1465:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1465:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1465:17: ( ' ' | '\\t' | '\\f' | EOL )
            int alt1=4;
            switch ( input.LA(1) ) {
            case ' ':
                alt1=1;
                break;
            case '\t':
                alt1=2;
                break;
            case '\f':
                alt1=3;
                break;
            case '\n':
            case '\r':
                alt1=4;
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1465:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1465:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1466:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1467:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1468:19: EOL
                    {
                    mEOL(); if (failed) return ;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end WS

    // $ANTLR start EOL
    public void mEOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1475:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1475:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1475:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);
            if ( (LA2_0=='\r') ) {
                int LA2_1 = input.LA(2);
                if ( (LA2_1=='\n') ) {
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
                    new NoViableAltException("1475:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1475:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1476:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1477:25: '\\n'
                    {
                    match('\n'); if (failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EOL

    // $ANTLR start INT
    public void mINT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = INT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1482:11: '0' .. '9'
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


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end INT

    // $ANTLR start FLOAT
    public void mFLOAT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = FLOAT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1486:27: '0' .. '9'
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


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FLOAT

    // $ANTLR start STRING
    public void mSTRING() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = STRING;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("1489:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1490:32: ~ ('\\\\'|'\"')
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
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1491:33: ~ ('\\\\'|'\\'')
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

            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end STRING

    // $ANTLR start HexDigit
    public void mHexDigit() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1495:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1495:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            ruleNestingLevel--;
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start EscapeSequence
    public void mEscapeSequence() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1499:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\'|'.') | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);
            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '.':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    alt11=1;
                    break;
                case 'u':
                    alt11=2;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    alt11=3;
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1497:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\'|'.') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1497:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\'|'.') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1499:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\'|'.')
                    {
                    match('\\'); if (failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='.'||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
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
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1500:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1501:9: OctalEscape
                    {
                    mOctalEscape(); if (failed) return ;

                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public void mOctalEscape() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("1504:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1504:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1506:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1507:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1507:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1507:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1507:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1507:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1508:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1508:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1508:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;

            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public void mUnicodeEscape() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1513:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1513:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            ruleNestingLevel--;
        }
    }
    // $ANTLR end UnicodeEscape

    // $ANTLR start BOOL
    public void mBOOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = BOOL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1517:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1517:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1517:4: ( 'true' | 'false' )
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
                    new NoViableAltException("1517:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1517:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1517:12: 'false'
                    {
                    match("false"); if (failed) return ;


                    }
                    break;

            }


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end BOOL

    // $ANTLR start PACKAGE
    public void mPACKAGE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = PACKAGE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1520:11: ( 'package' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1520:11: 'package'
            {
            match("package"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end PACKAGE

    // $ANTLR start IMPORT
    public void mIMPORT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = IMPORT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1522:10: ( 'import' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1522:10: 'import'
            {
            match("import"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end IMPORT

    // $ANTLR start FUNCTION
    public void mFUNCTION() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = FUNCTION;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1524:12: ( 'function' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1524:12: 'function'
            {
            match("function"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start GLOBAL
    public void mGLOBAL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = GLOBAL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1526:10: ( 'global' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1526:10: 'global'
            {
            match("global"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end GLOBAL

    // $ANTLR start RULE
    public void mRULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1528:11: ( 'rule' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1528:11: 'rule'
            {
            match("rule"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RULE

    // $ANTLR start QUERY
    public void mQUERY() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = QUERY;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1530:9: ( 'query' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1530:9: 'query'
            {
            match("query"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end QUERY

    // $ANTLR start TEMPLATE
    public void mTEMPLATE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = TEMPLATE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:12: ( 'template' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:12: 'template'
            {
            match("template"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end TEMPLATE

    // $ANTLR start ATTRIBUTES
    public void mATTRIBUTES() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ATTRIBUTES;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1534:14: ( 'attributes' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1534:14: 'attributes'
            {
            match("attributes"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ATTRIBUTES

    // $ANTLR start DATE_EFFECTIVE
    public void mDATE_EFFECTIVE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DATE_EFFECTIVE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1537:4: ( 'date-effective' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1537:4: 'date-effective'
            {
            match("date-effective"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DATE_EFFECTIVE

    // $ANTLR start DATE_EXPIRES
    public void mDATE_EXPIRES() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DATE_EXPIRES;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1540:4: ( 'date-expires' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1540:4: 'date-expires'
            {
            match("date-expires"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DATE_EXPIRES

    // $ANTLR start ENABLED
    public void mENABLED() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ENABLED;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1542:11: ( 'enabled' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1542:11: 'enabled'
            {
            match("enabled"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ENABLED

    // $ANTLR start SALIENCE
    public void mSALIENCE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = SALIENCE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1545:4: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1545:4: 'salience'
            {
            match("salience"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end SALIENCE

    // $ANTLR start NO_LOOP
    public void mNO_LOOP() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NO_LOOP;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1547:11: ( 'no-loop' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1547:11: 'no-loop'
            {
            match("no-loop"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end NO_LOOP

    // $ANTLR start AUTO_FOCUS
    public void mAUTO_FOCUS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = AUTO_FOCUS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1550:4: ( 'auto-focus' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1550:4: 'auto-focus'
            {
            match("auto-focus"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end AUTO_FOCUS

    // $ANTLR start ACTIVATION_GROUP
    public void mACTIVATION_GROUP() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ACTIVATION_GROUP;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1553:4: ( 'activation-group' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1553:4: 'activation-group'
            {
            match("activation-group"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ACTIVATION_GROUP

    // $ANTLR start AGENDA_GROUP
    public void mAGENDA_GROUP() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = AGENDA_GROUP;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1556:4: ( 'agenda-group' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1556:4: 'agenda-group'
            {
            match("agenda-group"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end AGENDA_GROUP

    // $ANTLR start RULEFLOW_GROUP
    public void mRULEFLOW_GROUP() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RULEFLOW_GROUP;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1559:4: ( 'ruleflow-group' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1559:4: 'ruleflow-group'
            {
            match("ruleflow-group"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RULEFLOW_GROUP

    // $ANTLR start DURATION
    public void mDURATION() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DURATION;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1562:4: ( 'duration' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1562:4: 'duration'
            {
            match("duration"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end DURATION

    // $ANTLR start LOCK_ON_ACTIVE
    public void mLOCK_ON_ACTIVE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = LOCK_ON_ACTIVE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1565:4: ( 'lock-on-active' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1565:4: 'lock-on-active'
            {
            match("lock-on-active"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end LOCK_ON_ACTIVE

    // $ANTLR start FROM
    public void mFROM() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = FROM;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1567:8: ( 'from' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1567:8: 'from'
            {
            match("from"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FROM

    // $ANTLR start ACCUMULATE
    public void mACCUMULATE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ACCUMULATE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1570:4: ( 'accumulate' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1570:4: 'accumulate'
            {
            match("accumulate"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ACCUMULATE

    // $ANTLR start INIT
    public void mINIT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = INIT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1572:8: ( 'init' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1572:8: 'init'
            {
            match("init"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end INIT

    // $ANTLR start ACTION
    public void mACTION() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ACTION;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1574:10: ( 'action' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1574:10: 'action'
            {
            match("action"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ACTION

    // $ANTLR start RESULT
    public void mRESULT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RESULT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1576:10: ( 'result' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1576:10: 'result'
            {
            match("result"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RESULT

    // $ANTLR start COLLECT
    public void mCOLLECT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = COLLECT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1578:11: ( 'collect' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1578:11: 'collect'
            {
            match("collect"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end COLLECT

    // $ANTLR start OR
    public void mOR() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = OR;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1580:6: ( 'or' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1580:6: 'or'
            {
            match("or"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end OR

    // $ANTLR start AND
    public void mAND() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = AND;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1582:7: ( 'and' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1582:7: 'and'
            {
            match("and"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end AND

    // $ANTLR start CONTAINS
    public void mCONTAINS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = CONTAINS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1585:4: ( 'contains' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1585:4: 'contains'
            {
            match("contains"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end CONTAINS

    // $ANTLR start EXCLUDES
    public void mEXCLUDES() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = EXCLUDES;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1588:4: ( 'excludes' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1588:4: 'excludes'
            {
            match("excludes"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EXCLUDES

    // $ANTLR start MATCHES
    public void mMATCHES() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MATCHES;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1590:11: ( 'matches' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1590:11: 'matches'
            {
            match("matches"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MATCHES

    // $ANTLR start NULL
    public void mNULL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NULL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1592:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1592:8: 'null'
            {
            match("null"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end NULL

    // $ANTLR start EXISTS
    public void mEXISTS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = EXISTS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1594:10: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1594:10: 'exists'
            {
            match("exists"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EXISTS

    // $ANTLR start NOT
    public void mNOT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NOT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1596:7: ( 'not' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1596:7: 'not'
            {
            match("not"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end NOT

    // $ANTLR start EVAL
    public void mEVAL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = EVAL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1598:8: ( 'eval' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1598:8: 'eval'
            {
            match("eval"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end EVAL

    // $ANTLR start FORALL
    public void mFORALL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = FORALL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1600:10: ( 'forall' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1600:10: 'forall'
            {
            match("forall"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end FORALL

    // $ANTLR start WHEN
    public void mWHEN() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WHEN;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1602:11: ( 'when' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1602:11: 'when'
            {
            match("when"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end WHEN

    // $ANTLR start THEN
    public void mTHEN() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = THEN;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1604:12: ( 'then' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1604:12: 'then'
            {
            match("then"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end THEN

    // $ANTLR start END
    public void mEND() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = END;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1606:11: ( 'end' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1606:11: 'end'
            {
            match("end"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end END

    // $ANTLR start ID
    public void mID() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ID;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1609:4: ( ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1609:4: ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1609:50: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1609:51: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end ID

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1614:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1614:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1614:8: ( options {greedy=false; } : . )*
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
                else if ( ((LA15_0>='\u0000' && LA15_0<='\t')||(LA15_0>='\u000B' && LA15_0<='\f')||(LA15_0>='\u000E' && LA15_0<='\uFFFE')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1614:35: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end SH_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start C_STYLE_SINGLE_LINE_COMMENT
    public void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = C_STYLE_SINGLE_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1620:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1620:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1620:9: ( options {greedy=false; } : . )*
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
                else if ( ((LA16_0>='\u0000' && LA16_0<='\t')||(LA16_0>='\u000B' && LA16_0<='\f')||(LA16_0>='\u000E' && LA16_0<='\uFFFE')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1620:36: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            mEOL(); if (failed) return ;
            if ( backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end C_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start LEFT_PAREN
    public void mLEFT_PAREN() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = LEFT_PAREN;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1626:11: ( '(' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1626:11: '('
            {
            match('('); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end LEFT_PAREN

    // $ANTLR start RIGHT_PAREN
    public void mRIGHT_PAREN() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RIGHT_PAREN;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1630:11: ( ')' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1630:11: ')'
            {
            match(')'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RIGHT_PAREN

    // $ANTLR start LEFT_SQUARE
    public void mLEFT_SQUARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = LEFT_SQUARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1634:11: ( '[' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1634:11: '['
            {
            match('['); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end LEFT_SQUARE

    // $ANTLR start RIGHT_SQUARE
    public void mRIGHT_SQUARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RIGHT_SQUARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1638:11: ( ']' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1638:11: ']'
            {
            match(']'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RIGHT_SQUARE

    // $ANTLR start LEFT_CURLY
    public void mLEFT_CURLY() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = LEFT_CURLY;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1642:11: ( '{' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1642:11: '{'
            {
            match('{'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end LEFT_CURLY

    // $ANTLR start RIGHT_CURLY
    public void mRIGHT_CURLY() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = RIGHT_CURLY;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1646:11: ( '}' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1646:11: '}'
            {
            match('}'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start MULTI_LINE_COMMENT
    public void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MULTI_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1650:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1650:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1650:9: ( options {greedy=false; } : . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0=='*') ) {
                    int LA17_1 = input.LA(2);
                    if ( (LA17_1=='/') ) {
                        alt17=2;
                    }
                    else if ( ((LA17_1>='\u0000' && LA17_1<='.')||(LA17_1>='0' && LA17_1<='\uFFFE')) ) {
                        alt17=1;
                    }


                }
                else if ( ((LA17_0>='\u0000' && LA17_0<=')')||(LA17_0>='+' && LA17_0<='\uFFFE')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1650:35: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            match("*/"); if (failed) return ;

            if ( backtracking==0 ) {
               _channel=HIDDEN; 
            }

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MULTI_LINE_COMMENT

    // $ANTLR start MISC
    public void mMISC() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MISC;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1654:7: ( ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'?'|'|'|','|'='|'/'|'\\''|'\\\\'))
            // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1655:3: ('!'|'@'|'$'|'%'|'^'|'&'|'*'|'_'|'-'|'+'|'?'|'|'|','|'='|'/'|'\\''|'\\\\')
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='\'')||(input.LA(1)>='*' && input.LA(1)<='-')||input.LA(1)=='/'||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='@')||input.LA(1)=='\\'||(input.LA(1)>='^' && input.LA(1)<='_')||input.LA(1)=='|' ) {
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


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }
        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end MISC

    public void mTokens() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:10: ( T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MATCHES | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | MULTI_LINE_COMMENT | MISC )
        int alt18=70;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:10: T63
                {
                mT63(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:14: T64
                {
                mT64(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:18: T65
                {
                mT65(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:22: T66
                {
                mT66(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:26: T67
                {
                mT67(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:30: T68
                {
                mT68(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:34: T69
                {
                mT69(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:38: T70
                {
                mT70(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:42: T71
                {
                mT71(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:46: T72
                {
                mT72(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:50: T73
                {
                mT73(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:54: T74
                {
                mT74(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:58: T75
                {
                mT75(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:62: T76
                {
                mT76(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:66: T77
                {
                mT77(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:70: T78
                {
                mT78(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:74: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:77: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:81: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:87: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:94: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:99: PACKAGE
                {
                mPACKAGE(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:107: IMPORT
                {
                mIMPORT(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:114: FUNCTION
                {
                mFUNCTION(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:123: GLOBAL
                {
                mGLOBAL(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:130: RULE
                {
                mRULE(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:135: QUERY
                {
                mQUERY(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:141: TEMPLATE
                {
                mTEMPLATE(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:150: ATTRIBUTES
                {
                mATTRIBUTES(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:161: DATE_EFFECTIVE
                {
                mDATE_EFFECTIVE(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:176: DATE_EXPIRES
                {
                mDATE_EXPIRES(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:189: ENABLED
                {
                mENABLED(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:197: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 34 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:206: NO_LOOP
                {
                mNO_LOOP(); if (failed) return ;

                }
                break;
            case 35 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:214: AUTO_FOCUS
                {
                mAUTO_FOCUS(); if (failed) return ;

                }
                break;
            case 36 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:225: ACTIVATION_GROUP
                {
                mACTIVATION_GROUP(); if (failed) return ;

                }
                break;
            case 37 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:242: AGENDA_GROUP
                {
                mAGENDA_GROUP(); if (failed) return ;

                }
                break;
            case 38 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:255: RULEFLOW_GROUP
                {
                mRULEFLOW_GROUP(); if (failed) return ;

                }
                break;
            case 39 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:270: DURATION
                {
                mDURATION(); if (failed) return ;

                }
                break;
            case 40 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:279: LOCK_ON_ACTIVE
                {
                mLOCK_ON_ACTIVE(); if (failed) return ;

                }
                break;
            case 41 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:294: FROM
                {
                mFROM(); if (failed) return ;

                }
                break;
            case 42 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:299: ACCUMULATE
                {
                mACCUMULATE(); if (failed) return ;

                }
                break;
            case 43 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:310: INIT
                {
                mINIT(); if (failed) return ;

                }
                break;
            case 44 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:315: ACTION
                {
                mACTION(); if (failed) return ;

                }
                break;
            case 45 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:322: RESULT
                {
                mRESULT(); if (failed) return ;

                }
                break;
            case 46 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:329: COLLECT
                {
                mCOLLECT(); if (failed) return ;

                }
                break;
            case 47 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:337: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 48 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:340: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 49 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:344: CONTAINS
                {
                mCONTAINS(); if (failed) return ;

                }
                break;
            case 50 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:353: EXCLUDES
                {
                mEXCLUDES(); if (failed) return ;

                }
                break;
            case 51 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:362: MATCHES
                {
                mMATCHES(); if (failed) return ;

                }
                break;
            case 52 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:370: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 53 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:375: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 54 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:382: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 55 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:386: EVAL
                {
                mEVAL(); if (failed) return ;

                }
                break;
            case 56 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:391: FORALL
                {
                mFORALL(); if (failed) return ;

                }
                break;
            case 57 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:398: WHEN
                {
                mWHEN(); if (failed) return ;

                }
                break;
            case 58 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:403: THEN
                {
                mTHEN(); if (failed) return ;

                }
                break;
            case 59 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:408: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 60 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:412: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 61 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:415: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 62 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:444: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 63 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:472: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 64 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:483: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 65 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:495: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 66 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:507: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 67 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:520: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 68 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:531: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 69 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:543: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 70 :
                // C:\\dev\\jbossrules\\trunk3\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1:562: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    public static final String DFA18_eotS =
        "\2\uffff\1\55\2\uffff\1\60\1\62\2\53\1\66\1\70\1\53\1\uffff\1\73"+
        "\1\uffff\1\53\21\52\2\uffff\1\53\30\uffff\35\52\1\u0080\2\52\2\uffff"+
        "\16\52\1\u0091\13\52\1\u009d\1\52\1\u009f\1\uffff\4\52\1\uffff\2"+
        "\52\1\u00a6\1\u00a7\4\52\1\u00ac\2\52\1\u00af\2\52\1\u00b3\1\52"+
        "\1\uffff\7\52\1\u00bd\3\52\1\uffff\1\52\1\uffff\1\u00c2\4\52\1\u00c7"+
        "\2\uffff\2\52\1\u00a7\1\52\1\uffff\2\52\1\uffff\3\52\1\uffff\1\u00d0"+
        "\1\uffff\5\52\1\uffff\1\52\1\uffff\4\52\2\uffff\3\52\1\uffff\1\52"+
        "\1\u00e0\2\52\1\u00e3\1\u00e4\1\u00e5\1\52\1\uffff\2\52\1\u00e9"+
        "\2\52\1\uffff\1\52\1\u00ef\7\52\1\uffff\1\52\1\u00f8\3\uffff\3\52"+
        "\1\uffff\1\52\3\uffff\1\52\1\uffff\1\52\1\u00ff\1\52\1\u0101\1\52"+
        "\1\u0103\1\u0104\1\u0105\1\uffff\4\52\1\u010a\1\u010b\1\uffff\1"+
        "\u010c\1\uffff\1\u010d\4\uffff\3\52\4\uffff\1\u0111\1\52\1\u0113"+
        "\3\uffff";
    public static final String DFA18_eofS =
        "\u0114\uffff";
    public static final String DFA18_minS =
        "\1\11\1\uffff\1\52\2\uffff\1\174\1\46\1\60\4\75\1\uffff\1\56\1\uffff"+
        "\1\0\1\145\2\141\1\155\1\154\1\145\1\165\1\143\1\141\1\156\1\141"+
        "\3\157\1\162\1\141\1\150\2\uffff\1\52\30\uffff\1\145\1\165\1\155"+
        "\1\162\1\154\1\156\1\157\1\143\1\160\1\151\1\157\1\163\1\154\1\145"+
        "\1\144\2\164\1\143\1\145\1\164\1\162\1\141\1\143\1\141\1\154\1\55"+
        "\1\154\1\143\1\154\1\60\1\164\1\145\2\uffff\1\156\1\145\1\160\1"+
        "\141\1\163\1\143\1\155\1\153\1\157\1\164\1\142\1\165\1\145\1\162"+
        "\1\60\1\157\1\162\1\151\1\165\1\156\1\145\1\141\1\154\1\163\1\154"+
        "\1\142\1\60\1\151\1\60\1\uffff\1\154\1\153\1\154\1\164\1\uffff\1"+
        "\143\1\156\2\60\2\154\1\145\1\164\1\60\1\141\1\162\1\60\1\141\1"+
        "\154\1\60\1\171\1\uffff\1\55\1\151\1\157\1\155\1\144\1\55\1\164"+
        "\1\60\1\164\1\165\1\154\1\uffff\1\145\1\uffff\1\60\1\55\1\145\1"+
        "\141\1\150\1\60\2\uffff\1\141\1\154\1\60\1\151\1\uffff\1\147\1\164"+
        "\1\uffff\1\154\1\164\1\154\1\uffff\1\60\1\uffff\1\142\1\141\1\156"+
        "\1\165\1\141\1\145\1\151\1\uffff\1\163\1\144\1\145\1\156\2\uffff"+
        "\1\143\1\151\1\145\1\uffff\1\164\1\60\1\157\1\145\3\60\1\157\1\uffff"+
        "\1\165\1\164\1\60\1\154\1\55\1\146\1\157\1\60\1\145\1\144\1\143"+
        "\1\164\1\156\1\163\1\145\1\uffff\1\156\1\60\3\uffff\1\167\1\164"+
        "\1\151\1\uffff\1\141\3\uffff\1\156\1\uffff\1\163\1\60\1\145\1\60"+
        "\1\163\3\60\1\uffff\1\55\1\145\1\157\1\164\2\60\1\uffff\1\60\1\uffff"+
        "\1\60\4\uffff\1\163\1\156\1\145\4\uffff\1\60\1\55\1\60\3\uffff";
    public static final String DFA18_maxS =
        "\1\u00ff\1\uffff\1\52\2\uffff\1\174\1\46\1\76\4\75\1\uffff\1\71"+
        "\1\uffff\1\ufffe\1\162\1\165\1\141\1\156\1\154\4\165\1\170\1\141"+
        "\1\165\2\157\1\162\1\141\1\150\2\uffff\1\57\30\uffff\1\145\1\165"+
        "\1\155\1\162\1\154\1\156\1\157\1\143\1\160\1\151\1\157\1\163\1\154"+
        "\1\145\1\144\3\164\1\145\1\164\1\162\1\141\1\151\1\144\1\154\1\164"+
        "\1\154\1\143\1\156\1\u00ff\1\164\1\145\2\uffff\1\156\1\145\1\160"+
        "\1\141\1\163\1\143\1\155\1\153\1\157\1\164\1\142\1\165\1\145\1\162"+
        "\1\u00ff\1\157\1\162\1\151\1\165\1\156\1\145\1\141\1\154\1\163\1"+
        "\154\1\142\1\u00ff\1\151\1\u00ff\1\uffff\1\154\1\153\1\154\1\164"+
        "\1\uffff\1\143\1\156\2\u00ff\2\154\1\145\1\164\1\u00ff\1\141\1\162"+
        "\1\u00ff\1\141\1\154\1\u00ff\1\171\1\uffff\1\55\1\151\1\166\1\155"+
        "\1\144\1\55\1\164\1\u00ff\1\164\1\165\1\154\1\uffff\1\145\1\uffff"+
        "\1\u00ff\1\55\1\145\1\141\1\150\1\u00ff\2\uffff\1\141\1\154\1\u00ff"+
        "\1\151\1\uffff\1\147\1\164\1\uffff\1\154\1\164\1\154\1\uffff\1\u00ff"+
        "\1\uffff\1\142\1\141\1\156\1\165\1\141\1\145\1\151\1\uffff\1\163"+
        "\1\144\1\145\1\156\2\uffff\1\143\1\151\1\145\1\uffff\1\164\1\u00ff"+
        "\1\157\1\145\3\u00ff\1\157\1\uffff\1\165\1\164\1\u00ff\1\154\1\55"+
        "\1\170\1\157\1\u00ff\1\145\1\144\1\143\1\164\1\156\1\163\1\145\1"+
        "\uffff\1\156\1\u00ff\3\uffff\1\167\1\164\1\151\1\uffff\1\141\3\uffff"+
        "\1\156\1\uffff\1\163\1\u00ff\1\145\1\u00ff\1\163\3\u00ff\1\uffff"+
        "\1\55\1\145\1\157\1\164\2\u00ff\1\uffff\1\u00ff\1\uffff\1\u00ff"+
        "\4\uffff\1\163\1\156\1\145\4\uffff\1\u00ff\1\55\1\u00ff\3\uffff";
    public static final String DFA18_acceptS =
        "\1\uffff\1\1\1\uffff\1\4\1\5\7\uffff\1\21\1\uffff\1\24\22\uffff"+
        "\1\74\1\75\1\uffff\1\77\1\100\1\101\1\102\1\103\1\104\1\74\1\106"+
        "\1\3\1\2\1\4\1\6\1\10\1\20\1\7\1\11\1\12\1\14\1\13\1\16\1\15\1\17"+
        "\1\23\1\22\40\uffff\1\105\1\76\35\uffff\1\42\4\uffff\1\57\20\uffff"+
        "\1\60\13\uffff\1\73\1\uffff\1\66\6\uffff\1\72\1\25\4\uffff\1\51"+
        "\2\uffff\1\53\3\uffff\1\32\1\uffff\1\43\7\uffff\1\67\4\uffff\1\64"+
        "\1\50\3\uffff\1\71\10\uffff\1\33\17\uffff\1\70\2\uffff\1\27\1\31"+
        "\1\55\3\uffff\1\54\1\uffff\1\45\1\37\1\36\1\uffff\1\65\10\uffff"+
        "\1\26\6\uffff\1\40\1\uffff\1\56\1\uffff\1\63\1\34\1\30\1\46\3\uffff"+
        "\1\47\1\62\1\41\1\61\3\uffff\1\35\1\44\1\52";
    public static final String DFA18_specialS =
        "\u0114\uffff}>";
    public static final String[] DFA18_transition = {
        "\2\14\1\uffff\2\14\22\uffff\1\14\1\13\1\16\1\42\1\41\1\53\1\6\1"+
        "\17\1\44\1\45\2\53\1\3\1\7\1\2\1\43\12\15\1\4\1\1\1\12\1\10\1\11"+
        "\2\53\32\52\1\46\1\53\1\47\1\53\1\41\1\uffff\1\27\1\52\1\35\1\30"+
        "\1\31\1\21\1\24\1\52\1\23\2\52\1\34\1\37\1\33\1\36\1\22\1\26\1\25"+
        "\1\32\1\20\2\52\1\40\3\52\1\50\1\5\1\51\102\uffff\100\52",
        "",
        "\1\54",
        "",
        "",
        "\1\57",
        "\1\61",
        "\12\15\4\uffff\1\63",
        "\1\64",
        "\1\65",
        "\1\67",
        "\1\71",
        "",
        "\1\72\1\uffff\12\15",
        "",
        "\uffff\16",
        "\1\76\2\uffff\1\74\11\uffff\1\75",
        "\1\100\15\uffff\1\77\2\uffff\1\102\2\uffff\1\101",
        "\1\103",
        "\1\104\1\105",
        "\1\106",
        "\1\107\17\uffff\1\110",
        "\1\111",
        "\1\115\3\uffff\1\116\6\uffff\1\112\5\uffff\1\114\1\113",
        "\1\117\23\uffff\1\120",
        "\1\123\7\uffff\1\121\1\uffff\1\122",
        "\1\124",
        "\1\125\5\uffff\1\126",
        "\1\127",
        "\1\130",
        "\1\131",
        "\1\132",
        "\1\133",
        "",
        "",
        "\1\134\4\uffff\1\135",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "\1\136",
        "\1\137",
        "\1\140",
        "\1\141",
        "\1\142",
        "\1\143",
        "\1\144",
        "\1\145",
        "\1\146",
        "\1\147",
        "\1\150",
        "\1\151",
        "\1\152",
        "\1\153",
        "\1\154",
        "\1\155",
        "\1\156",
        "\1\160\20\uffff\1\157",
        "\1\161",
        "\1\162",
        "\1\163",
        "\1\164",
        "\1\166\5\uffff\1\165",
        "\1\167\2\uffff\1\170",
        "\1\171",
        "\1\173\106\uffff\1\172",
        "\1\174",
        "\1\175",
        "\1\176\1\uffff\1\177",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u0081",
        "\1\u0082",
        "",
        "",
        "\1\u0083",
        "\1\u0084",
        "\1\u0085",
        "\1\u0086",
        "\1\u0087",
        "\1\u0088",
        "\1\u0089",
        "\1\u008a",
        "\1\u008b",
        "\1\u008c",
        "\1\u008d",
        "\1\u008e",
        "\1\u008f",
        "\1\u0090",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u0092",
        "\1\u0093",
        "\1\u0094",
        "\1\u0095",
        "\1\u0096",
        "\1\u0097",
        "\1\u0098",
        "\1\u0099",
        "\1\u009a",
        "\1\u009b",
        "\1\u009c",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u009e",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "\1\u00a0",
        "\1\u00a1",
        "\1\u00a2",
        "\1\u00a3",
        "",
        "\1\u00a4",
        "\1\u00a5",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00a8",
        "\1\u00a9",
        "\1\u00aa",
        "\1\u00ab",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00ad",
        "\1\u00ae",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00b0",
        "\1\u00b1",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\5\52\1\u00b2\24\52\105"+
        "\uffff\100\52",
        "\1\u00b4",
        "",
        "\1\u00b5",
        "\1\u00b6",
        "\1\u00b8\6\uffff\1\u00b7",
        "\1\u00b9",
        "\1\u00ba",
        "\1\u00bb",
        "\1\u00bc",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00be",
        "\1\u00bf",
        "\1\u00c0",
        "",
        "\1\u00c1",
        "",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00c3",
        "\1\u00c4",
        "\1\u00c5",
        "\1\u00c6",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "",
        "\1\u00c8",
        "\1\u00c9",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00ca",
        "",
        "\1\u00cb",
        "\1\u00cc",
        "",
        "\1\u00cd",
        "\1\u00ce",
        "\1\u00cf",
        "",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "\1\u00d1",
        "\1\u00d2",
        "\1\u00d3",
        "\1\u00d4",
        "\1\u00d5",
        "\1\u00d6",
        "\1\u00d7",
        "",
        "\1\u00d8",
        "\1\u00d9",
        "\1\u00da",
        "\1\u00db",
        "",
        "",
        "\1\u00dc",
        "\1\u00dd",
        "\1\u00de",
        "",
        "\1\u00df",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00e1",
        "\1\u00e2",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00e6",
        "",
        "\1\u00e7",
        "\1\u00e8",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00ea",
        "\1\u00eb",
        "\1\u00ed\21\uffff\1\u00ec",
        "\1\u00ee",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u00f0",
        "\1\u00f1",
        "\1\u00f2",
        "\1\u00f3",
        "\1\u00f4",
        "\1\u00f5",
        "\1\u00f6",
        "",
        "\1\u00f7",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "",
        "",
        "\1\u00f9",
        "\1\u00fa",
        "\1\u00fb",
        "",
        "\1\u00fc",
        "",
        "",
        "",
        "\1\u00fd",
        "",
        "\1\u00fe",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u0100",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u0102",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "\1\u0106",
        "\1\u0107",
        "\1\u0108",
        "\1\u0109",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "",
        "",
        "",
        "\1\u010e",
        "\1\u010f",
        "\1\u0110",
        "",
        "",
        "",
        "",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "\1\u0112",
        "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100\52",
        "",
        "",
        ""
    };

    class DFA18 extends DFA {
        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA.unpackEncodedString(DFA18_eotS);
            this.eof = DFA.unpackEncodedString(DFA18_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
            this.accept = DFA.unpackEncodedString(DFA18_acceptS);
            this.special = DFA.unpackEncodedString(DFA18_specialS);
            int numStates = DFA18_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA18_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( T63 | T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MATCHES | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | MULTI_LINE_COMMENT | MISC );";
        }
    }
 

}