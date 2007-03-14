// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-14 22:20:00

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=13;
    public static final int DEFRULE=24;
    public static final int HexDigit=29;
    public static final int MISC=22;
    public static final int FLOAT=19;
    public static final int TILDE=18;
    public static final int T45=45;
    public static final int OR=11;
    public static final int PIPE=17;
    public static final int AND=10;
    public static final int EscapeSequence=28;
    public static final int INT=9;
    public static final int SYMBOL=23;
    public static final int LEFT_SQUARE=34;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=32;
    public static final int AMPERSAND=16;
    public static final int DECLARE=27;
    public static final int T41=41;
    public static final int LEFT_CURLY=36;
    public static final int T39=39;
    public static final int ID=5;
    public static final int T44=44;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=37;
    public static final int BOOL=20;
    public static final int WS=26;
    public static final int STRING=6;
    public static final int T43=43;
    public static final int T42=42;
    public static final int T40=40;
    public static final int VAR=15;
    public static final int UnicodeEscape=30;
    public static final int EOF=-1;
    public static final int EOL=25;
    public static final int NULL=21;
    public static final int Tokens=46;
    public static final int OctalEscape=31;
    public static final int SALIENCE=8;
    public static final int MULTI_LINE_COMMENT=38;
    public static final int TEST=14;
    public static final int RIGHT_PAREN=7;
    public static final int NOT=12;
    public static final int RIGHT_SQUARE=35;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=33;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[44+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T39
    public void mT39() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T39;
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
    // $ANTLR end T39

    // $ANTLR start T40
    public void mT40() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T40;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: ( '::' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '::'
            {
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( '=>' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: '=>'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ( '<-' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: '<-'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ( ':' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ':'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: ( '=' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: '='
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:12:7: ( 'modify' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:12:7: 'modify'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:598:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:598:11: 'defrule'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:7: 'or'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:600:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:600:8: 'and'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:8: 'not'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:11: 'exists'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: 'test'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:8: 'null'
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

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("607:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:19: EOL
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: 'declare'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:619:4: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:619:4: 'salience'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("623:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:27: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("637:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:32: ~ ('\\\\'|'\"')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:33: ~ ('\\\\'|'\\'')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("645:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("645:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("652:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("652:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:661:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:661:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:4: ( 'true' | 'false' )
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
                    new NoViableAltException("665:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:665:12: 'false'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            {
            match('?'); if (failed) return ;
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:57: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:58: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:4: ( ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:4: ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:50: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00FF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:51: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:36: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:4: '('
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: ')'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:696:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:696:4: '['
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:700:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:700:4: ']'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: '{'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: '}'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:711:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:711:9: '~'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:4: '&'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: '|'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:9: ( options {greedy=false; } : . )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                if ( (LA18_0=='*') ) {
                    int LA18_1 = input.LA(2);
                    if ( (LA18_1=='/') ) {
                        alt18=2;
                    }
                    else if ( ((LA18_1>='\u0000' && LA18_1<='.')||(LA18_1>='0' && LA18_1<='\uFFFE')) ) {
                        alt18=1;
                    }


                }
                else if ( ((LA18_0>='\u0000' && LA18_0<=')')||(LA18_0>='+' && LA18_0<='\uFFFE')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:35: .
            	    {
            	    matchAny(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
            int alt19=19;
            switch ( input.LA(1) ) {
            case '!':
                alt19=1;
                break;
            case '@':
                alt19=2;
                break;
            case '$':
                alt19=3;
                break;
            case '%':
                alt19=4;
                break;
            case '^':
                alt19=5;
                break;
            case '*':
                alt19=6;
                break;
            case '_':
                alt19=7;
                break;
            case '-':
                alt19=8;
                break;
            case '+':
                alt19=9;
                break;
            case '?':
                alt19=10;
                break;
            case ',':
                alt19=11;
                break;
            case '=':
                alt19=12;
                break;
            case '/':
                alt19=13;
                break;
            case '\'':
                alt19=14;
                break;
            case '\\':
                alt19=15;
                break;
            case '<':
                int LA19_16 = input.LA(2);
                if ( (LA19_16=='=') ) {
                    alt19=18;
                }
                else {
                    alt19=16;}
                break;
            case '>':
                int LA19_17 = input.LA(2);
                if ( (LA19_17=='=') ) {
                    alt19=19;
                }
                else {
                    alt19=17;}
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("727:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:728:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:22: '>='
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( ((LA20_0>='\u0000' && LA20_0<='\b')||(LA20_0>='\u000B' && LA20_0<='\f')||(LA20_0>='\u000E' && LA20_0<='\u001F')||LA20_0=='!'||LA20_0=='#'||LA20_0=='%'||LA20_0=='\''||(LA20_0>='*' && LA20_0<=':')||(LA20_0>='<' && LA20_0<='>')||(LA20_0>='@' && LA20_0<='{')||LA20_0=='}'||(LA20_0>='\u007F' && LA20_0<='\uFFFE')) ) {
                alt20=1;
            }
            else if ( (LA20_0=='$') ) {
                alt20=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("733:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:733:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( ((LA21_0>='\u0000' && LA21_0<='\b')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\u001F')||LA21_0=='!'||(LA21_0>='#' && LA21_0<='%')||LA21_0=='\''||(LA21_0>='*' && LA21_0<=':')||(LA21_0>='=' && LA21_0<='>')||(LA21_0>='@' && LA21_0<='{')||LA21_0=='}'||(LA21_0>='\u007F' && LA21_0<='\uFFFE')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
            	    break loop21;
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
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T39 | T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt22=37;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T39
                {
                mT39(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:26: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:30: T44
                {
                mT44(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:34: T45
                {
                mT45(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:38: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:46: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:49: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:53: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:57: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:64: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:69: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:74: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:77: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:85: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:94: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:98: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:104: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:111: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:116: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:120: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:123: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:152: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:180: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:191: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:203: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:215: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:228: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:239: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:251: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:257: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 34 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:267: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 35 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:272: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 36 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:291: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 37 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:296: SYMBOL
                {
                mSYMBOL(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA22 dfa22 = new DFA22(this);
    public static final String DFA22_eotS =
        "\2\uffff\1\56\1\60\1\63\7\66\1\uffff\1\14\1\66\1\63\1\102\1\uffff"+
        "\1\63\1\66\1\63\1\66\1\54\1\63\2\uffff\1\115\1\116\1\117\1\120\3"+
        "\uffff\2\63\1\66\3\63\1\66\4\63\1\uffff\1\122\1\uffff\1\123\1\uffff"+
        "\1\124\1\63\1\uffff\2\66\1\uffff\1\66\1\130\7\66\1\102\1\54\1\uffff"+
        "\2\54\1\21\1\66\1\uffff\1\66\1\uffff\3\54\4\uffff\1\63\3\uffff\3"+
        "\66\1\uffff\1\156\1\66\1\160\4\66\1\165\4\54\1\66\1\uffff\3\54\1"+
        "\uffff\3\66\1\uffff\1\176\1\uffff\1\66\1\u0080\1\u0081\1\66\1\uffff"+
        "\3\54\1\66\1\152\3\66\1\uffff\1\66\2\uffff\1\66\2\54\1\u0081\1\u008c"+
        "\2\66\1\u008f\1\66\1\54\1\uffff\1\u0092\1\u0093\1\uffff\1\66\1\54"+
        "\2\uffff\1\u0095\1\uffff";
    public static final String DFA22_eofS =
        "\u0096\uffff";
    public static final String DFA22_minS =
        "\1\0\1\uffff\12\0\1\uffff\4\0\1\uffff\2\0\1\44\3\0\2\uffff\4\0\3"+
        "\uffff\13\0\1\uffff\1\0\1\uffff\1\0\1\uffff\2\0\1\uffff\2\0\1\uffff"+
        "\12\0\1\60\1\uffff\1\42\3\0\1\uffff\1\0\1\uffff\3\0\4\uffff\1\0"+
        "\3\uffff\3\0\1\uffff\10\0\1\60\4\0\1\uffff\3\0\1\uffff\3\0\1\uffff"+
        "\1\0\1\uffff\4\0\1\uffff\1\60\7\0\1\uffff\1\0\2\uffff\1\0\1\60\7"+
        "\0\1\60\1\uffff\2\0\1\uffff\2\0\2\uffff\1\0\1\uffff";
    public static final String DFA22_maxS =
        "\1\ufffe\1\uffff\12\ufffe\1\uffff\4\ufffe\1\uffff\2\ufffe\1\u00ff"+
        "\3\ufffe\2\uffff\4\ufffe\3\uffff\13\ufffe\1\uffff\1\ufffe\1\uffff"+
        "\1\ufffe\1\uffff\2\ufffe\1\uffff\2\ufffe\1\uffff\12\ufffe\1\71\1"+
        "\uffff\1\165\3\ufffe\1\uffff\1\ufffe\1\uffff\3\ufffe\4\uffff\1\ufffe"+
        "\3\uffff\3\ufffe\1\uffff\10\ufffe\1\146\4\ufffe\1\uffff\3\ufffe"+
        "\1\uffff\3\ufffe\1\uffff\1\ufffe\1\uffff\4\ufffe\1\uffff\1\146\7"+
        "\ufffe\1\uffff\1\ufffe\2\uffff\1\ufffe\1\146\7\ufffe\1\146\1\uffff"+
        "\2\ufffe\1\uffff\2\ufffe\2\uffff\1\ufffe\1\uffff";
    public static final String DFA22_acceptS =
        "\1\uffff\1\1\12\uffff\1\17\4\uffff\1\24\6\uffff\1\32\1\33\4\uffff"+
        "\1\40\1\41\1\42\13\uffff\1\45\1\uffff\1\5\1\uffff\1\6\2\uffff\1"+
        "\44\2\uffff\1\27\13\uffff\1\22\4\uffff\1\26\1\uffff\1\30\3\uffff"+
        "\1\34\1\35\1\36\1\37\1\uffff\1\2\1\3\1\4\3\uffff\1\11\15\uffff\1"+
        "\31\3\uffff\1\43\3\uffff\1\12\1\uffff\1\13\4\uffff\1\23\10\uffff"+
        "\1\16\1\uffff\1\15\1\25\12\uffff\1\7\2\uffff\1\14\2\uffff\1\20\1"+
        "\10\1\uffff\1\21";
    public static final String DFA22_specialS =
        "\u0096\uffff}>";
    public static final String[] DFA22_transition = {
        "\11\54\2\14\1\54\1\15\1\14\22\54\1\14\1\41\1\21\1\26\1\25\1\44\1"+
        "\37\1\22\1\30\1\31\1\46\1\50\1\51\1\17\1\54\1\27\12\20\1\2\1\1\1"+
        "\4\1\3\1\53\1\24\1\42\32\47\1\32\1\52\1\33\1\45\1\43\1\54\1\10\2"+
        "\47\1\6\1\12\1\23\6\47\1\5\1\11\1\7\3\47\1\16\1\13\6\47\1\34\1\40"+
        "\1\35\1\36\101\54\100\47\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\20\54\1\55\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54"+
        "\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\1\54\1\57\1\uffff\74\54\1\uffff\1\54"+
        "\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\3\54\1\61\15\54\2\uffff\1\62\1\54\1\uffff\74\54\1"+
        "\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\16\65\1\64\13\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\67\25\65\1\54\1\uffff\1\54\1\uffff\101\54\100"+
        "\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\21\65\1\70\10\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\15\65\1\71\14\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\16\65\1\73\5\65\1\72\5\65\1\54\1\uffff\1\54\1\uffff"+
        "\101\54\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\27\65\1\74\2\65\1\54\1\uffff\1\54\1\uffff\101\54\100"+
        "\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\75\14\65\1\76\10\65\1\54\1\uffff\1\54\1\uffff"+
        "\101\54\100\65\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\1\77\31\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65"+
        "\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\100\1\54\2\uffff\2\54\1\uffff\74\54\1\uffff"+
        "\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\4\54\1\101\1\54\12\100\1\54\2\uffff\2\54\1\uffff\74"+
        "\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\1\106\31\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65"+
        "\ufeff\54",
        "\1\107\34\uffff\32\107\4\uffff\1\107\1\uffff\32\107\105\uffff\100"+
        "\107",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\110\1\54\2\uffff\2\54\1\uffff\1\54\32\110"+
        "\4\54\1\110\1\54\32\110\1\54\1\uffff\1\54\1\uffff\101\54\100\110"+
        "\ufeff\54",
        "\11\112\2\111\2\112\1\111\22\112\1\111\1\112\1\111\3\112\1\111\1"+
        "\112\2\111\21\112\2\111\2\112\1\111\74\112\1\111\1\112\1\111\uff80"+
        "\112",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\1\114\4\54\1\113\13\54\2\uffff\2\54\1\uffff\74\54"+
        "\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\1\121\1\54\1\uffff\74\54\1\uffff\1\54"+
        "\1\uffff\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\3\65\1\125\26\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\2\65\1\126\2\65\1\127\24\65\1\54\1\uffff\1\54\1\uffff"+
        "\101\54\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\3\65\1\131\26\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\132\16\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\23\65\1\133\6\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\10\65\1\134\21\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\22\65\1\135\7\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\24\65\1\136\5\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\137\16\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\4\54\1\101\1\54\12\100\1\54\2\uffff\2\54\1\uffff\74"+
        "\54\1\uffff\1\54\1\uffff\uff80\54",
        "\12\140",
        "",
        "\1\21\4\uffff\1\142\10\uffff\4\143\4\144\44\uffff\1\142\5\uffff"+
        "\1\142\3\uffff\1\142\7\uffff\1\142\3\uffff\1\142\1\uffff\1\142\1"+
        "\141",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\145\16\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "",
        "\11\112\2\111\2\112\1\111\22\112\1\111\1\112\1\111\3\112\1\111\1"+
        "\112\2\111\21\112\2\111\2\112\1\111\74\112\1\111\1\112\1\111\uff80"+
        "\112",
        "\11\147\2\146\2\147\1\146\22\147\1\146\1\147\1\146\3\147\1\146\1"+
        "\147\2\146\21\147\2\146\2\147\1\146\74\147\1\146\1\147\1\146\uff80"+
        "\147",
        "\11\151\2\152\2\151\1\152\22\151\1\152\1\151\1\152\3\151\1\152\1"+
        "\151\2\152\1\150\20\151\2\152\2\151\1\152\74\151\1\152\1\151\1\152"+
        "\uff80\151",
        "",
        "",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\10\65\1\153\21\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\154\16\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\21\65\1\155\10\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\157\16\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\22\65\1\161\7\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\23\65\1\162\6\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\163\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\10\65\1\164\21\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\140\1\54\2\uffff\2\54\1\uffff\74\54\1\uffff"+
        "\1\54\1\uffff\uff80\54",
        "\12\166\7\uffff\6\166\32\uffff\6\166",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\6\104\10\167\3\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21"+
        "\1\104\1\21\uff80\104",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\6\104\10\170\3\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21"+
        "\1\104\1\21\uff80\104",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\22\65\1\171\7\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "\11\147\2\146\2\147\1\146\22\147\1\146\1\147\1\146\3\147\1\146\1"+
        "\147\2\146\21\147\2\146\2\147\1\146\74\147\1\146\1\147\1\146\uff80"+
        "\147",
        "\11\151\2\152\2\151\1\152\22\151\1\152\1\151\1\152\3\151\1\152\1"+
        "\151\2\152\1\150\4\151\1\172\13\151\2\152\2\151\1\152\74\151\1\152"+
        "\1\151\1\152\uff80\151",
        "\11\151\2\152\2\151\1\152\22\151\1\152\1\151\1\152\3\151\1\152\1"+
        "\151\2\152\1\150\20\151\2\152\2\151\1\152\74\151\1\152\1\151\1\152"+
        "\uff80\151",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\5\65\1\173\24\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\1\174\31\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65"+
        "\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\24\65\1\175\5\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\23\65\1\177\6\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\u0082\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "\12\u0083\7\uffff\6\u0083\32\uffff\6\u0083",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\6\104\10\u0084\3\104\2\21\2\104\1\21\34\104\1\103\37\104\1"+
        "\21\1\104\1\21\uff80\104",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\u0085\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\151\2\uffff\2\151\1\uffff\22\151\1\uffff\1\151\1\uffff\3\151"+
        "\1\uffff\1\151\2\uffff\1\150\20\151\2\uffff\2\151\1\uffff\74\151"+
        "\1\uffff\1\151\1\uffff\uff80\151",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\30\65\1\u0086\1\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\21\65\1\u0087\10\65\1\54\1\uffff\1\54\1\uffff\101"+
        "\54\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\13\65\1\u0088\16\65\1\54\1\uffff\1\54\1\uffff\101"+
        "\54\100\65\ufeff\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\22\65\1\u0089\7\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\15\65\1\u008a\14\65\1\54\1\uffff\1\54\1\uffff\101"+
        "\54\100\65\ufeff\54",
        "\12\u008b\7\uffff\6\u008b\32\uffff\6\u008b",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\u008d\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\u008e\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\2\65\1\u0090\27\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\12\u0091\7\uffff\6\u0091\32\uffff\6\u0091",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\4\65\1\u0094\25\65\1\54\1\uffff\1\54\1\uffff\101\54"+
        "\100\65\ufeff\54",
        "\11\104\2\21\2\104\1\21\22\104\1\21\1\104\1\21\3\104\1\21\1\105"+
        "\2\21\21\104\2\21\2\104\1\21\34\104\1\103\37\104\1\21\1\104\1\21"+
        "\uff80\104",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\65\1\54\2\uffff\2\54\1\uffff\1\54\32\65\4"+
        "\54\1\65\1\54\32\65\1\54\1\uffff\1\54\1\uffff\101\54\100\65\ufeff"+
        "\54",
        ""
    };

    class DFA22 extends DFA {
        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA.unpackEncodedString(DFA22_eotS);
            this.eof = DFA.unpackEncodedString(DFA22_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
            this.accept = DFA.unpackEncodedString(DFA22_acceptS);
            this.special = DFA.unpackEncodedString(DFA22_specialS);
            int numStates = DFA22_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA22_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( T39 | T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}