// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-15 04:40:35

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=15;
    public static final int DEFRULE=5;
    public static final int HexDigit=30;
    public static final int MISC=24;
    public static final int FLOAT=21;
    public static final int TILDE=20;
    public static final int T45=45;
    public static final int OR=13;
    public static final int PIPE=19;
    public static final int AND=12;
    public static final int EscapeSequence=29;
    public static final int INT=11;
    public static final int MODULE=9;
    public static final int SYMBOL=25;
    public static final int LEFT_SQUARE=35;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=33;
    public static final int AMPERSAND=18;
    public static final int DECLARE=28;
    public static final int T41=41;
    public static final int LEFT_CURLY=37;
    public static final int ID=6;
    public static final int T44=44;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=38;
    public static final int BOOL=22;
    public static final int WS=27;
    public static final int STRING=7;
    public static final int T43=43;
    public static final int T42=42;
    public static final int T40=40;
    public static final int VAR=17;
    public static final int UnicodeEscape=31;
    public static final int EOF=-1;
    public static final int EOL=26;
    public static final int NULL=23;
    public static final int Tokens=46;
    public static final int OctalEscape=32;
    public static final int SALIENCE=10;
    public static final int MULTI_LINE_COMMENT=39;
    public static final int TEST=16;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=14;
    public static final int RIGHT_SQUARE=36;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=34;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[44+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T40
    public void mT40() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T40;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ( ';' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ';'
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
    // $ANTLR end T40

    // $ANTLR start T41
    public void mT41() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T41;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: ( '=>' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '=>'
            {
            match("=>"); if (failed) return ;


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
    // $ANTLR end T41

    // $ANTLR start T42
    public void mT42() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T42;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( '<-' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: '<-'
            {
            match("<-"); if (failed) return ;


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
    // $ANTLR end T42

    // $ANTLR start T43
    public void mT43() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T43;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ( ':' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ':'
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
    // $ANTLR end T43

    // $ANTLR start T44
    public void mT44() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T44;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ( '=' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: '='
            {
            match('='); if (failed) return ;

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
    // $ANTLR end T44

    // $ANTLR start T45
    public void mT45() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T45;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: ( 'modify' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: 'modify'
            {
            match("modify"); if (failed) return ;


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
    // $ANTLR end T45

    // $ANTLR start DEFRULE
    public void mDEFRULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DEFRULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:11: 'defrule'
            {
            match("defrule"); if (failed) return ;


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
    // $ANTLR end DEFRULE

    // $ANTLR start OR
    public void mOR() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = OR;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:7: 'or'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:8: 'and'
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

    // $ANTLR start NOT
    public void mNOT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NOT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:8: 'not'
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

    // $ANTLR start EXISTS
    public void mEXISTS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = EXISTS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:11: 'exists'
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

    // $ANTLR start TEST
    public void mTEST() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = TEST;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:615:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:615:9: 'test'
            {
            match("test"); if (failed) return ;


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
    // $ANTLR end TEST

    // $ANTLR start NULL
    public void mNULL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NULL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:8: 'null'
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

    // $ANTLR start MODULE
    public void mMODULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MODULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:4: ( ( 'A' .. 'Z' )+ '::' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:4: ( 'A' .. 'Z' )+ '::'
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:4: ( 'A' .. 'Z' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( ((LA1_0>='A' && LA1_0<='Z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:5: 'A' .. 'Z'
            	    {
            	    matchRange('A','Z'); if (failed) return ;

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

            match("::"); if (failed) return ;


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
    // $ANTLR end MODULE

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:17: ( ' ' | '\\t' | '\\f' | EOL )
            int alt2=4;
            switch ( input.LA(1) ) {
            case ' ':
                alt2=1;
                break;
            case '\t':
                alt2=2;
                break;
            case '\f':
                alt2=3;
                break;
            case '\n':
            case '\r':
                alt2=4;
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("623:17: ( ' ' | '\\t' | '\\f' | EOL )", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:626:19: EOL
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

    // $ANTLR start DECLARE
    public void mDECLARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DECLARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:4: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:4: 'declare'
            {
            match("declare"); if (failed) return ;


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
    // $ANTLR end DECLARE

    // $ANTLR start SALIENCE
    public void mSALIENCE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = SALIENCE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:4: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:4: 'salience'
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

    // $ANTLR start EOL
    public void mEOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt3=3;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='\r') ) {
                int LA3_1 = input.LA(2);
                if ( (LA3_1=='\n') ) {
                    alt3=1;
                }
                else {
                    alt3=2;}
            }
            else if ( (LA3_0=='\n') ) {
                alt3=3;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("639:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:641:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:4: ( '-' )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            if ( (LA4_0=='-') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:11: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:4: ( '-' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0=='-') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:11: '0' .. '9'
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

            match('.'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:26: ( '0' .. '9' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:27: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0=='\"') ) {
                alt11=1;
            }
            else if ( (LA11_0=='\'') ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("653:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);
                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0>='\u0000' && LA9_0<='!')||(LA9_0>='#' && LA9_0<='[')||(LA9_0>=']' && LA9_0<='\uFFFE')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:32: ~ ('\\\\'|'\"')
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
                    	    break loop9;
                        }
                    } while (true);

                    match('\"'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
                    loop10:
                    do {
                        int alt10=3;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0=='\\') ) {
                            alt10=1;
                        }
                        else if ( ((LA10_0>='\u0000' && LA10_0<='&')||(LA10_0>='(' && LA10_0<='[')||(LA10_0>=']' && LA10_0<='\uFFFE')) ) {
                            alt10=2;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:33: ~ ('\\\\'|'\\'')
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
                    	    break loop10;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:659:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:659:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
            int alt12=3;
            int LA12_0 = input.LA(1);
            if ( (LA12_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    alt12=1;
                    break;
                case 'u':
                    alt12=2;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    alt12=3;
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("661:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 12, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("661:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
                    {
                    match('\\'); if (failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt13=3;
            int LA13_0 = input.LA(1);
            if ( (LA13_0=='\\') ) {
                int LA13_1 = input.LA(2);
                if ( ((LA13_1>='0' && LA13_1<='3')) ) {
                    int LA13_2 = input.LA(3);
                    if ( ((LA13_2>='0' && LA13_2<='7')) ) {
                        int LA13_4 = input.LA(4);
                        if ( ((LA13_4>='0' && LA13_4<='7')) ) {
                            alt13=1;
                        }
                        else {
                            alt13=2;}
                    }
                    else {
                        alt13=3;}
                }
                else if ( ((LA13_1>='4' && LA13_1<='7')) ) {
                    int LA13_3 = input.LA(3);
                    if ( ((LA13_3>='0' && LA13_3<='7')) ) {
                        alt13=2;
                    }
                    else {
                        alt13=3;}
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("668:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("668:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:4: ( 'true' | 'false' )
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0=='t') ) {
                alt14=1;
            }
            else if ( (LA14_0=='f') ) {
                alt14=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("681:4: ( 'true' | 'false' )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:12: 'false'
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

    // $ANTLR start VAR
    public void mVAR() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = VAR;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:684:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:684:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            {
            match('?'); if (failed) return ;
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:684:38: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:684:39: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
            	    break loop15;
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
    // $ANTLR end VAR

    // $ANTLR start ID
    public void mID() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ID;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:4: ( ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:4: ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            {
            if ( input.LA(1)=='$'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:31: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:32: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
            	    break loop16;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:698:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:698:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:698:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:698:36: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: '('
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: ')'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: '['
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: ']'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:4: '{'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:4: '}'
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

    // $ANTLR start TILDE
    public void mTILDE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = TILDE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:727:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:727:9: '~'
            {
            match('~'); if (failed) return ;

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
    // $ANTLR end TILDE

    // $ANTLR start AMPERSAND
    public void mAMPERSAND() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = AMPERSAND;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:731:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:731:4: '&'
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
    // $ANTLR end AMPERSAND

    // $ANTLR start PIPE
    public void mPIPE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = PIPE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:4: '|'
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
    // $ANTLR end PIPE

    // $ANTLR start MULTI_LINE_COMMENT
    public void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MULTI_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
            int alt20=19;
            switch ( input.LA(1) ) {
            case '!':
                alt20=1;
                break;
            case '@':
                alt20=2;
                break;
            case '$':
                alt20=3;
                break;
            case '%':
                alt20=4;
                break;
            case '^':
                alt20=5;
                break;
            case '*':
                alt20=6;
                break;
            case '_':
                alt20=7;
                break;
            case '-':
                alt20=8;
                break;
            case '+':
                alt20=9;
                break;
            case '?':
                alt20=10;
                break;
            case ',':
                alt20=11;
                break;
            case '=':
                alt20=12;
                break;
            case '/':
                alt20=13;
                break;
            case '\'':
                alt20=14;
                break;
            case '\\':
                alt20=15;
                break;
            case '<':
                int LA20_16 = input.LA(2);
                if ( (LA20_16=='=') ) {
                    alt20=18;
                }
                else {
                    alt20=16;}
                break;
            case '>':
                int LA20_17 = input.LA(2);
                if ( (LA20_17=='=') ) {
                    alt20=19;
                }
                else {
                    alt20=17;}
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("743:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:745:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:745:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:745:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:745:22: '>='
                    {
                    match(">="); if (failed) return ;


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
    // $ANTLR end MISC

    // $ANTLR start SYMBOL
    public void mSYMBOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = SYMBOL;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
            int alt21=2;
            int LA21_0 = input.LA(1);
            if ( ((LA21_0>='\u0000' && LA21_0<='\b')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\u001F')||LA21_0=='!'||LA21_0=='#'||LA21_0=='%'||LA21_0=='\''||(LA21_0>='*' && LA21_0<=':')||(LA21_0>='<' && LA21_0<='>')||(LA21_0>='@' && LA21_0<='{')||LA21_0=='}'||(LA21_0>='\u007F' && LA21_0<='\uFFFE')) ) {
                alt21=1;
            }
            else if ( (LA21_0=='$') ) {
                alt21=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("749:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||input.LA(1)=='!'||input.LA(1)=='#'||input.LA(1)=='%'||input.LA(1)=='\''||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='<' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='{')||input.LA(1)=='}'||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
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
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
                    {
                    match('$'); if (failed) return ;
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||input.LA(1)=='\''||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='=' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='{')||input.LA(1)=='}'||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
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
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);
                if ( ((LA22_0>='\u0000' && LA22_0<='\b')||(LA22_0>='\u000B' && LA22_0<='\f')||(LA22_0>='\u000E' && LA22_0<='\u001F')||LA22_0=='!'||(LA22_0>='#' && LA22_0<='%')||LA22_0=='\''||(LA22_0>='*' && LA22_0<=':')||(LA22_0>='=' && LA22_0<='>')||(LA22_0>='@' && LA22_0<='{')||LA22_0=='}'||(LA22_0>='\u007F' && LA22_0<='\uFFFE')) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\b')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||input.LA(1)=='\''||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='=' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='{')||input.LA(1)=='}'||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
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
            	    break loop22;
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
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | MODULE | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt23=37;
        alt23 = dfa23.predict(input);
        switch (alt23) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:26: T44
                {
                mT44(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:30: T45
                {
                mT45(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:34: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:42: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:45: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:49: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:53: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:60: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:65: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:70: MODULE
                {
                mMODULE(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:77: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:80: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:88: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:97: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:101: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:107: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:114: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:119: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:123: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:126: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:155: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:183: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:194: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:206: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:218: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:231: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:242: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:254: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:260: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 34 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:270: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 35 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:275: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 36 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:294: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 37 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:299: SYMBOL
                {
                mSYMBOL(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA23 dfa23 = new DFA23(this);
    public static final String DFA23_eotS =
        "\2\uffff\1\57\1\62\1\63\10\66\1\uffff\1\15\1\66\1\62\1\104\1\uffff"+
        "\1\62\1\66\1\62\1\66\1\55\1\62\2\uffff\1\117\1\120\1\121\1\122\3"+
        "\uffff\2\62\1\66\3\62\1\66\4\62\1\uffff\1\124\1\uffff\1\62\1\125"+
        "\2\uffff\2\66\1\uffff\1\66\1\131\7\66\1\55\1\66\1\104\1\55\1\uffff"+
        "\2\55\1\22\1\66\1\uffff\1\66\1\55\1\uffff\2\55\4\uffff\1\62\2\uffff"+
        "\3\66\1\uffff\1\160\1\66\1\162\3\66\1\166\1\66\1\170\4\55\1\66\1"+
        "\55\1\uffff\1\55\1\uffff\1\55\3\66\1\uffff\1\u0081\1\uffff\1\66"+
        "\1\u0083\1\u0084\1\uffff\1\66\1\uffff\3\55\1\66\1\153\3\66\1\uffff"+
        "\1\66\2\uffff\1\66\2\55\1\u0084\1\u008f\2\66\1\u0092\1\66\1\55\1"+
        "\uffff\1\u0095\1\u0096\1\uffff\1\66\1\55\2\uffff\1\u0098\1\uffff";
    public static final String DFA23_eofS =
        "\u0099\uffff";
    public static final String DFA23_minS =
        "\1\0\1\uffff\13\0\1\uffff\4\0\1\uffff\2\0\1\44\3\0\2\uffff\4\0\3"+
        "\uffff\13\0\1\uffff\1\0\1\uffff\2\0\2\uffff\2\0\1\uffff\11\0\1\72"+
        "\2\0\1\60\1\uffff\1\42\3\0\1\uffff\2\0\1\uffff\2\0\4\uffff\1\0\2"+
        "\uffff\3\0\1\uffff\11\0\1\60\5\0\1\uffff\1\0\1\uffff\4\0\1\uffff"+
        "\1\0\1\uffff\3\0\1\uffff\1\0\1\uffff\1\60\7\0\1\uffff\1\0\2\uffff"+
        "\1\0\1\60\7\0\1\60\1\uffff\2\0\1\uffff\2\0\2\uffff\1\0\1\uffff";
    public static final String DFA23_maxS =
        "\1\ufffe\1\uffff\13\ufffe\1\uffff\4\ufffe\1\uffff\2\ufffe\1\172"+
        "\3\ufffe\2\uffff\4\ufffe\3\uffff\13\ufffe\1\uffff\1\ufffe\1\uffff"+
        "\2\ufffe\2\uffff\2\ufffe\1\uffff\11\ufffe\1\72\2\ufffe\1\71\1\uffff"+
        "\1\165\3\ufffe\1\uffff\2\ufffe\1\uffff\2\ufffe\4\uffff\1\ufffe\2"+
        "\uffff\3\ufffe\1\uffff\11\ufffe\1\146\5\ufffe\1\uffff\1\ufffe\1"+
        "\uffff\4\ufffe\1\uffff\1\ufffe\1\uffff\3\ufffe\1\uffff\1\ufffe\1"+
        "\uffff\1\146\7\ufffe\1\uffff\1\ufffe\2\uffff\1\ufffe\1\146\7\ufffe"+
        "\1\146\1\uffff\2\ufffe\1\uffff\2\ufffe\2\uffff\1\ufffe\1\uffff";
    public static final String DFA23_acceptS =
        "\1\uffff\1\1\13\uffff\1\17\4\uffff\1\24\6\uffff\1\32\1\33\4\uffff"+
        "\1\40\1\41\1\42\13\uffff\1\45\1\uffff\1\5\2\uffff\1\44\1\4\2\uffff"+
        "\1\27\15\uffff\1\22\4\uffff\1\26\2\uffff\1\30\2\uffff\1\34\1\35"+
        "\1\36\1\37\1\uffff\1\2\1\3\3\uffff\1\10\17\uffff\1\31\1\uffff\1"+
        "\43\4\uffff\1\11\1\uffff\1\12\3\uffff\1\16\1\uffff\1\23\10\uffff"+
        "\1\15\1\uffff\1\14\1\25\12\uffff\1\6\2\uffff\1\13\2\uffff\1\7\1"+
        "\20\1\uffff\1\21";
    public static final String DFA23_specialS =
        "\u0099\uffff}>";
    public static final String[] DFA23_transition = {
        "\11\55\2\15\1\55\1\16\1\15\22\55\1\15\1\42\1\22\1\27\1\26\1\45\1"+
        "\40\1\23\1\31\1\32\1\47\1\51\1\52\1\20\1\55\1\30\12\21\1\4\1\1\1"+
        "\3\1\2\1\54\1\25\1\43\32\14\1\33\1\53\1\34\1\46\1\44\1\55\1\10\2"+
        "\50\1\6\1\12\1\24\6\50\1\5\1\11\1\7\3\50\1\17\1\13\6\50\1\35\1\41"+
        "\1\36\1\37\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\1\55\1\56\1\uffff\74\55\1\uffff\1\55"+
        "\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\3\55\1\61\15\55\2\uffff\1\60\1\55\1\uffff\74\55\1"+
        "\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\16\65\1\64\13\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\67\25\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\21\65\1\70\10\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\15\65\1\71\14\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\16\65\1\73\5\65\1\72\5\65\1\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\27\65\1\74\2\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\75\14\65\1\76\10\65\1\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\100\2\uffff\2\55\1\uffff\1\55\32\77\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\1\101\31\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\102\1\55\2\uffff\2\55\1\uffff\74\55\1\uffff"+
        "\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\4\55\1\103\1\55\12\102\1\55\2\uffff\2\55\1\uffff\74"+
        "\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\1\110\31\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\1\111\34\uffff\32\111\4\uffff\1\111\1\uffff\32\111",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\112\1\55\2\uffff\2\55\1\uffff\1\55\32\112"+
        "\4\55\1\112\1\55\32\112\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\113\2\114\2\113\1\114\22\113\1\114\1\113\1\114\3\113\1\114\1"+
        "\113\2\114\21\113\2\114\2\113\1\114\74\113\1\114\1\113\1\114\uff80"+
        "\113",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\1\116\4\55\1\115\13\55\2\uffff\2\55\1\uffff\74\55"+
        "\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\1\123\1\55\1\uffff\74\55\1\uffff\1\55"+
        "\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\3\65\1\126\26\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\2\65\1\130\2\65\1\127\24\65\1\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\3\65\1\132\26\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\133\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\23\65\1\134\6\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\10\65\1\135\21\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\22\65\1\136\7\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\24\65\1\137\5\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\100\2\uffff\2\55\1\uffff\1\55\32\77\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\1\140",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\141\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\4\55\1\103\1\55\12\102\1\55\2\uffff\2\55\1\uffff\74"+
        "\55\1\uffff\1\55\1\uffff\uff80\55",
        "\12\142",
        "",
        "\1\22\4\uffff\1\144\10\uffff\4\145\4\146\44\uffff\1\144\5\uffff"+
        "\1\144\3\uffff\1\144\7\uffff\1\144\3\uffff\1\144\1\uffff\1\144\1"+
        "\143",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\147\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\113\2\114\2\113\1\114\22\113\1\114\1\113\1\114\3\113\1\114\1"+
        "\113\2\114\21\113\2\114\2\113\1\114\74\113\1\114\1\113\1\114\uff80"+
        "\113",
        "",
        "\11\150\2\151\2\150\1\151\22\150\1\151\1\150\1\151\3\150\1\151\1"+
        "\150\2\151\21\150\2\151\2\150\1\151\74\150\1\151\1\150\1\151\uff80"+
        "\150",
        "\11\154\2\153\2\154\1\153\22\154\1\153\1\154\1\153\3\154\1\153\1"+
        "\154\2\153\1\152\20\154\2\153\2\154\1\153\74\154\1\153\1\154\1\153"+
        "\uff80\154",
        "",
        "",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\10\65\1\155\21\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\21\65\1\156\10\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\157\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\161\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\22\65\1\163\7\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\23\65\1\164\6\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\165\25\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\21\55\2\uffff\2\55\1\uffff\74\55\1\uffff\1\55\1\uffff"+
        "\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\10\65\1\167\21\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\142\1\55\2\uffff\2\55\1\uffff\74\55\1\uffff"+
        "\1\55\1\uffff\uff80\55",
        "\12\171\7\uffff\6\171\32\uffff\6\171",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\6\106\10\172\3\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22"+
        "\1\106\1\22\uff80\106",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\6\106\10\173\3\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22"+
        "\1\106\1\22\uff80\106",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\22\65\1\174\7\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\150\2\151\2\150\1\151\22\150\1\151\1\150\1\151\3\150\1\151\1"+
        "\150\2\151\21\150\2\151\2\150\1\151\74\150\1\151\1\150\1\151\uff80"+
        "\150",
        "",
        "\11\154\2\153\2\154\1\153\22\154\1\153\1\154\1\153\3\154\1\153\1"+
        "\154\2\153\1\152\4\154\1\175\13\154\2\153\2\154\1\153\74\154\1\153"+
        "\1\154\1\153\uff80\154",
        "",
        "\11\154\2\153\2\154\1\153\22\154\1\153\1\154\1\153\3\154\1\153\1"+
        "\154\2\153\1\152\20\154\2\153\2\154\1\153\74\154\1\153\1\154\1\153"+
        "\uff80\154",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\5\65\1\176\24\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\24\65\1\177\5\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\1\u0080\31\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\23\65\1\u0082\6\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\u0085\25\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "",
        "\12\u0086\7\uffff\6\u0086\32\uffff\6\u0086",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\6\106\10\u0087\3\106\2\22\2\106\1\22\34\106\1\105\37\106\1"+
        "\22\1\106\1\22\uff80\106",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\u0088\25\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\154\2\uffff\2\154\1\uffff\22\154\1\uffff\1\154\1\uffff\3\154"+
        "\1\uffff\1\154\2\uffff\1\152\20\154\2\uffff\2\154\1\uffff\74\154"+
        "\1\uffff\1\154\1\uffff\uff80\154",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\30\65\1\u0089\1\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\13\65\1\u008a\16\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\21\65\1\u008b\10\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\22\65\1\u008c\7\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\15\65\1\u008d\14\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\12\u008e\7\uffff\6\u008e\32\uffff\6\u008e",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\u0090\25\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\u0091\25\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\2\65\1\u0093\27\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\12\u0094\7\uffff\6\u0094\32\uffff\6\u0094",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\4\65\1\u0097\25\65\1\55\1\uffff\1\55\1\uffff\uff80"+
        "\55",
        "\11\106\2\22\2\106\1\22\22\106\1\22\1\106\1\22\3\106\1\22\1\107"+
        "\2\22\21\106\2\22\2\106\1\22\34\106\1\105\37\106\1\22\1\106\1\22"+
        "\uff80\106",
        "",
        "",
        "\11\55\2\uffff\2\55\1\uffff\22\55\1\uffff\1\55\1\uffff\3\55\1\uffff"+
        "\1\55\2\uffff\6\55\12\65\1\55\2\uffff\2\55\1\uffff\1\55\32\65\4"+
        "\55\1\65\1\55\32\65\1\55\1\uffff\1\55\1\uffff\uff80\55",
        ""
    };

    class DFA23 extends DFA {
        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA.unpackEncodedString(DFA23_eotS);
            this.eof = DFA.unpackEncodedString(DFA23_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
            this.accept = DFA.unpackEncodedString(DFA23_acceptS);
            this.special = DFA.unpackEncodedString(DFA23_specialS);
            int numStates = DFA23_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA23_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | MODULE | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}