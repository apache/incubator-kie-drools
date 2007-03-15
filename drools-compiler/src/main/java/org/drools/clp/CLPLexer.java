// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-15 03:41:54

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=14;
    public static final int DEFRULE=25;
    public static final int HexDigit=30;
    public static final int MISC=23;
    public static final int FLOAT=20;
    public static final int TILDE=19;
    public static final int T45=45;
    public static final int OR=12;
    public static final int PIPE=18;
    public static final int AND=11;
    public static final int EscapeSequence=29;
    public static final int INT=10;
    public static final int MODULE=8;
    public static final int SYMBOL=24;
    public static final int LEFT_SQUARE=35;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=33;
    public static final int AMPERSAND=17;
    public static final int DECLARE=28;
    public static final int T41=41;
    public static final int LEFT_CURLY=37;
    public static final int ID=5;
    public static final int T44=44;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=38;
    public static final int BOOL=21;
    public static final int WS=27;
    public static final int STRING=6;
    public static final int T43=43;
    public static final int T42=42;
    public static final int T40=40;
    public static final int VAR=16;
    public static final int UnicodeEscape=31;
    public static final int EOF=-1;
    public static final int EOL=26;
    public static final int NULL=22;
    public static final int Tokens=46;
    public static final int OctalEscape=32;
    public static final int SALIENCE=9;
    public static final int MULTI_LINE_COMMENT=39;
    public static final int TEST=15;
    public static final int RIGHT_PAREN=7;
    public static final int NOT=13;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:11: 'defrule'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:7: 'or'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:8: 'and'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:8: 'not'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:11: 'exists'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:9: 'test'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:8: 'null'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("618:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:619:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:19: EOL
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

    // $ANTLR start MODULE
    public void mMODULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MODULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:627:4: ( ID '::' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:627:4: ID '::'
            {
            mID(); if (failed) return ;
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

    // $ANTLR start DECLARE
    public void mDECLARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DECLARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:4: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:4: 'declare'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: 'salience'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("638:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:27: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("652:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:653:32: ~ ('\\\\'|'\"')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:33: ~ ('\\\\'|'\\'')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:658:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:658:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("660:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("660:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("667:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("667:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:4: ( 'true' | 'false' )
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
                    new NoViableAltException("680:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:12: 'false'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:38: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:39: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:4: ( ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:4: ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:31: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:32: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:36: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:703:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:703:4: '('
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:707:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:707:4: ')'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:711:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:711:4: '['
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:4: ']'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: '{'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: '}'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:726:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:726:9: '~'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:4: '&'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:4: '|'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
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
                    new NoViableAltException("742:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:22: '>='
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
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
                    new NoViableAltException("748:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:748:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( ((LA21_0>='\u0000' && LA21_0<='\b')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\u001F')||LA21_0=='!'||(LA21_0>='#' && LA21_0<='%')||LA21_0=='\''||(LA21_0>='*' && LA21_0<=':')||(LA21_0>='=' && LA21_0<='>')||(LA21_0>='@' && LA21_0<='{')||LA21_0=='}'||(LA21_0>='\u007F' && LA21_0<='\uFFFE')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:749:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | MODULE | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt22=37;
        alt22 = dfa22.predict(input);
        switch (alt22) {
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
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:70: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:73: MODULE
                {
                mMODULE(); if (failed) return ;

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


    protected DFA22 dfa22 = new DFA22(this);
    public static final String DFA22_eotS =
        "\2\uffff\1\56\1\61\1\62\7\65\1\uffff\1\14\2\65\1\61\1\103\1\uffff"+
        "\1\61\1\65\1\61\1\54\1\61\2\uffff\1\116\1\117\1\120\1\121\3\uffff"+
        "\2\61\1\65\3\61\1\65\4\61\1\uffff\1\123\1\uffff\1\124\1\61\2\uffff"+
        "\2\65\1\uffff\1\54\1\65\1\131\10\65\1\103\1\54\1\uffff\2\54\1\22"+
        "\1\65\1\54\1\uffff\1\54\1\uffff\2\54\4\uffff\1\61\2\uffff\1\65\1"+
        "\155\2\65\1\uffff\1\160\1\161\6\65\1\170\4\54\1\uffff\2\54\1\uffff"+
        "\1\54\1\65\1\uffff\2\65\2\uffff\1\u0080\1\65\1\u0082\1\u0083\2\65"+
        "\1\uffff\3\54\1\152\3\65\1\uffff\1\65\2\uffff\1\65\1\u0082\2\54"+
        "\1\u008e\2\65\1\u0091\1\65\1\54\1\uffff\1\u0094\1\u0095\1\uffff"+
        "\1\65\1\54\2\uffff\1\u0097\1\uffff";
    public static final String DFA22_eofS =
        "\u0098\uffff";
    public static final String DFA22_minS =
        "\1\0\1\uffff\12\0\1\uffff\5\0\1\uffff\2\0\1\44\2\0\2\uffff\4\0\3"+
        "\uffff\13\0\1\uffff\1\0\1\uffff\2\0\2\uffff\2\0\1\uffff\1\72\13"+
        "\0\1\60\1\uffff\1\42\3\0\1\72\1\uffff\1\0\1\uffff\2\0\4\uffff\1"+
        "\0\2\uffff\4\0\1\uffff\12\0\1\60\2\0\1\uffff\2\0\1\uffff\2\0\1\uffff"+
        "\2\0\2\uffff\6\0\1\uffff\1\60\6\0\1\uffff\1\0\2\uffff\2\0\1\60\6"+
        "\0\1\60\1\uffff\2\0\1\uffff\2\0\2\uffff\1\0\1\uffff";
    public static final String DFA22_maxS =
        "\1\ufffe\1\uffff\12\ufffe\1\uffff\5\ufffe\1\uffff\2\ufffe\1\172"+
        "\2\ufffe\2\uffff\4\ufffe\3\uffff\13\ufffe\1\uffff\1\ufffe\1\uffff"+
        "\2\ufffe\2\uffff\2\ufffe\1\uffff\1\72\13\ufffe\1\71\1\uffff\1\165"+
        "\3\ufffe\1\72\1\uffff\1\ufffe\1\uffff\2\ufffe\4\uffff\1\ufffe\2"+
        "\uffff\4\ufffe\1\uffff\12\ufffe\1\146\2\ufffe\1\uffff\2\ufffe\1"+
        "\uffff\2\ufffe\1\uffff\2\ufffe\2\uffff\6\ufffe\1\uffff\1\146\6\ufffe"+
        "\1\uffff\1\ufffe\2\uffff\2\ufffe\1\146\6\ufffe\1\146\1\uffff\2\ufffe"+
        "\1\uffff\2\ufffe\2\uffff\1\ufffe\1\uffff";
    public static final String DFA22_acceptS =
        "\1\uffff\1\1\12\uffff\1\16\5\uffff\1\24\5\uffff\1\32\1\33\4\uffff"+
        "\1\40\1\41\1\42\13\uffff\1\45\1\uffff\1\5\2\uffff\1\44\1\4\2\uffff"+
        "\1\27\15\uffff\1\22\5\uffff\1\26\1\uffff\1\30\2\uffff\1\34\1\35"+
        "\1\36\1\37\1\uffff\1\2\1\3\4\uffff\1\10\15\uffff\1\31\2\uffff\1"+
        "\43\2\uffff\1\17\2\uffff\1\11\1\12\6\uffff\1\23\7\uffff\1\15\1\uffff"+
        "\1\25\1\14\12\uffff\1\6\2\uffff\1\13\2\uffff\1\20\1\7\1\uffff\1"+
        "\21";
    public static final String DFA22_specialS =
        "\u0098\uffff}>";
    public static final String[] DFA22_transition = {
        "\11\54\2\14\1\54\1\15\1\14\22\54\1\14\1\41\1\22\1\26\1\24\1\44\1"+
        "\37\1\23\1\30\1\31\1\46\1\50\1\51\1\20\1\54\1\27\12\21\1\4\1\1\1"+
        "\3\1\2\1\53\1\25\1\42\32\47\1\32\1\52\1\33\1\45\1\43\1\54\1\10\2"+
        "\47\1\6\1\12\1\17\6\47\1\5\1\11\1\7\3\47\1\16\1\13\6\47\1\34\1\40"+
        "\1\35\1\36\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\1\54\1\55\1\uffff\74\54\1\uffff\1\54"+
        "\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\3\54\1\57\15\54\2\uffff\1\60\1\54\1\uffff\74\54\1"+
        "\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\16\64\1\63\13\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\67\25\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\21\64\1\70\10\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\15\64\1\71\14\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\16\64\1\72\5\64\1\73\5\64\1\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\27\64\1\74\2\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\76\14\64\1\75\10\64\1\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\1\77\31\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\1\100\31\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\101\1\54\2\uffff\2\54\1\uffff\74\54\1\uffff"+
        "\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\4\54\1\102\1\54\12\101\1\54\2\uffff\2\54\1\uffff\74"+
        "\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\107\1\110\2\uffff\2\54\1\uffff\1\54\32\107"+
        "\4\54\1\107\1\54\32\107\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\1\111\34\uffff\32\111\4\uffff\1\111\1\uffff\32\111",
        "\11\112\2\113\2\112\1\113\22\112\1\113\1\112\1\113\3\112\1\113\1"+
        "\112\2\113\21\112\2\113\2\112\1\113\74\112\1\113\1\112\1\113\uff80"+
        "\112",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\1\115\4\54\1\114\13\54\2\uffff\2\54\1\uffff\74\54"+
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
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
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
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
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
        "\1\54\2\uffff\21\54\2\uffff\1\122\1\54\1\uffff\74\54\1\uffff\1\54"+
        "\1\uffff\uff80\54",
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
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\3\64\1\125\26\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "\1\126",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\2\64\1\127\2\64\1\130\24\64\1\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\3\64\1\132\26\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\23\64\1\133\6\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\134\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\10\64\1\135\21\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\24\64\1\136\5\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\22\64\1\137\7\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\140\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\141\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\4\54\1\102\1\54\12\101\1\54\2\uffff\2\54\1\uffff\74"+
        "\54\1\uffff\1\54\1\uffff\uff80\54",
        "\12\142",
        "",
        "\1\22\4\uffff\1\143\10\uffff\4\145\4\146\44\uffff\1\143\5\uffff"+
        "\1\143\3\uffff\1\143\7\uffff\1\143\3\uffff\1\143\1\uffff\1\143\1"+
        "\144",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\1\126",
        "",
        "\11\112\2\113\2\112\1\113\22\112\1\113\1\112\1\113\3\112\1\113\1"+
        "\112\2\113\21\112\2\113\2\112\1\113\74\112\1\113\1\112\1\113\uff80"+
        "\112",
        "",
        "\11\150\2\147\2\150\1\147\22\150\1\147\1\150\1\147\3\150\1\147\1"+
        "\150\2\147\21\150\2\147\2\150\1\147\74\150\1\147\1\150\1\147\uff80"+
        "\150",
        "\11\153\2\152\2\153\1\152\22\153\1\152\1\153\1\152\3\153\1\152\1"+
        "\153\2\152\1\151\20\153\2\152\2\153\1\152\74\153\1\152\1\153\1\152"+
        "\uff80\153",
        "",
        "",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\10\64\1\154\21\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\21\54\2\uffff\2\54\1\uffff\74\54\1\uffff\1\54\1\uffff"+
        "\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\156\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\21\64\1\157\10\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\162\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\22\64\1\163\7\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\164\25\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\23\64\1\165\6\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\10\64\1\166\21\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\22\64\1\167\7\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\142\1\54\2\uffff\2\54\1\uffff\74\54\1\uffff"+
        "\1\54\1\uffff\uff80\54",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "\12\171\7\uffff\6\171\32\uffff\6\171",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\6\105\10\172\3\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22"+
        "\1\105\1\22\uff80\105",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\6\105\10\173\3\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22"+
        "\1\105\1\22\uff80\105",
        "",
        "\11\150\2\147\2\150\1\147\22\150\1\147\1\150\1\147\3\150\1\147\1"+
        "\150\2\147\21\150\2\147\2\150\1\147\74\150\1\147\1\150\1\147\uff80"+
        "\150",
        "\11\153\2\152\2\153\1\152\22\153\1\152\1\153\1\152\3\153\1\152\1"+
        "\153\2\152\1\151\4\153\1\174\13\153\2\152\2\153\1\152\74\153\1\152"+
        "\1\153\1\152\uff80\153",
        "",
        "\11\153\2\152\2\153\1\152\22\153\1\152\1\153\1\152\3\153\1\152\1"+
        "\153\2\152\1\151\20\153\2\152\2\153\1\152\74\153\1\152\1\153\1\152"+
        "\uff80\153",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\5\64\1\175\24\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\1\176\31\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\24\64\1\177\5\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\23\64\1\u0081\6\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\u0084\25\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\u0085\25\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "",
        "\12\u0086\7\uffff\6\u0086\32\uffff\6\u0086",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\6\105\10\u0087\3\105\2\22\2\105\1\22\34\105\1\104\37\105\1"+
        "\22\1\105\1\22\uff80\105",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "\11\153\2\uffff\2\153\1\uffff\22\153\1\uffff\1\153\1\uffff\3\153"+
        "\1\uffff\1\153\2\uffff\1\151\20\153\2\uffff\2\153\1\uffff\74\153"+
        "\1\uffff\1\153\1\uffff\uff80\153",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\30\64\1\u0088\1\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\21\64\1\u0089\10\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\13\64\1\u008a\16\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\22\64\1\u008b\7\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\15\64\1\u008c\14\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\12\u008d\7\uffff\6\u008d\32\uffff\6\u008d",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\u008f\25\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\u0090\25\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\2\64\1\u0092\27\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\12\u0093\7\uffff\6\u0093\32\uffff\6\u0093",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\4\64\1\u0096\25\64\1\54\1\uffff\1\54\1\uffff\uff80"+
        "\54",
        "\11\105\2\22\2\105\1\22\22\105\1\22\1\105\1\22\3\105\1\22\1\106"+
        "\2\22\21\105\2\22\2\105\1\22\34\105\1\104\37\105\1\22\1\105\1\22"+
        "\uff80\105",
        "",
        "",
        "\11\54\2\uffff\2\54\1\uffff\22\54\1\uffff\1\54\1\uffff\3\54\1\uffff"+
        "\1\54\2\uffff\6\54\12\64\1\66\2\uffff\2\54\1\uffff\1\54\32\64\4"+
        "\54\1\64\1\54\32\64\1\54\1\uffff\1\54\1\uffff\uff80\54",
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
            return "1:1: Tokens : ( T40 | T41 | T42 | T43 | T44 | T45 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | MODULE | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}