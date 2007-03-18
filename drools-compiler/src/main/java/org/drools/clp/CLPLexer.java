// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-18 12:17:59

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=14;
    public static final int DEFRULE=5;
    public static final int HexDigit=30;
    public static final int FLOAT=23;
    public static final int TILDE=20;
    public static final int OR=12;
    public static final int PIPE=19;
    public static final int ASSIGN_OP=17;
    public static final int AND=11;
    public static final int EscapeSequence=29;
    public static final int INT=10;
    public static final int SYMBOL=33;
    public static final int LEFT_SQUARE=36;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int AMPERSAND=18;
    public static final int DECLARE=28;
    public static final int T41=41;
    public static final int LEFT_CURLY=38;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=39;
    public static final int BOOL=24;
    public static final int WS=27;
    public static final int STRING=7;
    public static final int T43=43;
    public static final int T42=42;
    public static final int VAR=16;
    public static final int EQUALS=22;
    public static final int UnicodeEscape=31;
    public static final int EOF=-1;
    public static final int EOL=26;
    public static final int NULL=25;
    public static final int Tokens=44;
    public static final int COLON=21;
    public static final int OctalEscape=32;
    public static final int SALIENCE=9;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int TEST=15;
    public static final int NAME=6;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=13;
    public static final int RIGHT_SQUARE=37;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[42+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T41
    public void mT41() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T41;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( 'modify' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: 'modify'
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
    // $ANTLR end T43

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("582:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:584:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:585:19: EOL
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

    // $ANTLR start DEFRULE
    public void mDEFRULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DEFRULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:11: 'defrule'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:591:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:591:7: 'or'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:592:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:592:8: 'and'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:593:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:593:8: 'not'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:11: 'exists'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: 'test'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:597:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:597:8: 'null'
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

    // $ANTLR start DECLARE
    public void mDECLARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DECLARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:11: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:11: 'declare'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:11: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:11: 'salience'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("605:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:27: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("619:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:32: ~ ('\\\\'|'\"')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:33: ~ ('\\\\'|'\\'')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:629:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("627:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("627:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:629:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:630:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt12=3;
            int LA12_0 = input.LA(1);
            if ( (LA12_0=='\\') ) {
                int LA12_1 = input.LA(2);
                if ( ((LA12_1>='0' && LA12_1<='3')) ) {
                    int LA12_2 = input.LA(3);
                    if ( ((LA12_2>='0' && LA12_2<='7')) ) {
                        int LA12_5 = input.LA(4);
                        if ( ((LA12_5>='0' && LA12_5<='7')) ) {
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
                        new NoViableAltException("634:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("634:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:4: ( 'true' | 'false' )
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
                    new NoViableAltException("647:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:12: 'false'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( SYMBOL )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( SYMBOL )*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:38: ( SYMBOL )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='\u0000' && LA14_0<='\b')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\u001F')||LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||LA14_0=='\''||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='<' && LA14_0<='>')||(LA14_0>='@' && LA14_0<='{')||LA14_0=='}'||(LA14_0>='\u007F' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:38: SYMBOL
            	    {
            	    mSYMBOL(); if (failed) return ;

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

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:654:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:36: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:4: '('
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:4: ')'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:4: '['
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:678:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:678:4: ']'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:4: '{'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:4: '}'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:9: '~'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:4: '&'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: '|'
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

    // $ANTLR start ASSIGN_OP
    public void mASSIGN_OP() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = ASSIGN_OP;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:701:4: ( '<-' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:701:4: '<-'
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
    // $ANTLR end ASSIGN_OP

    // $ANTLR start COLON
    public void mCOLON() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = COLON;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:9: ( ':' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:9: ':'
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
    // $ANTLR end COLON

    // $ANTLR start EQUALS
    public void mEQUALS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = EQUALS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:706:10: ( '=' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:706:10: '='
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
    // $ANTLR end EQUALS

    // $ANTLR start MULTI_LINE_COMMENT
    public void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = MULTI_LINE_COMMENT;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:35: .
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

    // $ANTLR start NAME
    public void mNAME() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = NAME;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:713:8: ( SYMBOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:713:8: SYMBOL
            {
            mSYMBOL(); if (failed) return ;

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
    // $ANTLR end NAME

    // $ANTLR start SYMBOL
    public void mSYMBOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
            int alt18=2;
            int LA18_0 = input.LA(1);
            if ( ((LA18_0>='\u0000' && LA18_0<='\b')||(LA18_0>='\u000B' && LA18_0<='\f')||(LA18_0>='\u000E' && LA18_0<='\u001F')||LA18_0=='!'||LA18_0=='#'||LA18_0=='%'||LA18_0=='\''||(LA18_0>='*' && LA18_0<=':')||(LA18_0>='<' && LA18_0<='>')||(LA18_0>='@' && LA18_0<='{')||LA18_0=='}'||(LA18_0>='\u007F' && LA18_0<='\uFFFE')) ) {
                alt18=1;
            }
            else if ( (LA18_0=='$') ) {
                alt18=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("717:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:717:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:718:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\u001F')||LA19_0=='!'||(LA19_0>='#' && LA19_0<='%')||LA19_0=='\''||(LA19_0>='*' && LA19_0<=':')||(LA19_0>='=' && LA19_0<='>')||(LA19_0>='@' && LA19_0<='{')||LA19_0=='}'||(LA19_0>='\u007F' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:718:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
            	    break loop19;
                }
            } while (true);


            }

        }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T41 | T42 | T43 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt20=34;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:25: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:33: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:36: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:40: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:44: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:51: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:56: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:61: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:69: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:78: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:82: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:88: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:95: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:100: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:104: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:133: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:161: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:172: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:184: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:196: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:209: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:220: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:232: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:238: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:248: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:253: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:263: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:269: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:276: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 34 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:295: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA20 dfa20 = new DFA20(this);
    public static final String DFA20_eotS =
        "\2\uffff\1\42\1\40\1\uffff\1\4\10\40\1\57\1\uffff\2\40\1\uffff\2"+
        "\40\2\uffff\1\70\1\71\1\72\1\73\3\uffff\1\40\1\75\1\uffff\1\76\1"+
        "\uffff\2\40\1\102\7\40\1\57\1\40\1\uffff\2\40\1\17\2\40\1\uffff"+
        "\2\40\4\uffff\1\125\2\uffff\3\40\1\uffff\1\131\1\40\1\133\4\40\1"+
        "\140\6\40\1\uffff\2\40\2\uffff\3\40\1\uffff\1\151\1\uffff\1\40\1"+
        "\153\1\154\1\40\1\uffff\4\40\1\121\3\40\1\uffff\1\40\2\uffff\3\40"+
        "\1\153\1\167\2\40\1\172\2\40\1\uffff\1\175\1\176\1\uffff\2\40\2"+
        "\uffff\1\u0080\1\uffff";
    public static final String DFA20_eofS =
        "\u0081\uffff";
    public static final String DFA20_minS =
        "\1\0\1\uffff\1\0\1\157\1\uffff\1\0\1\145\1\162\1\156\1\157\1\170"+
        "\1\145\1\141\1\60\1\0\1\uffff\1\0\1\141\1\uffff\1\0\1\52\2\uffff"+
        "\4\0\3\uffff\1\55\1\0\1\uffff\1\0\1\uffff\1\144\1\143\1\0\1\144"+
        "\1\154\1\164\1\151\1\165\1\163\1\154\1\0\1\60\1\uffff\1\42\2\0\1"+
        "\154\1\0\1\uffff\2\0\4\uffff\1\0\2\uffff\1\151\1\162\1\154\1\uffff"+
        "\1\0\1\154\1\0\1\163\1\145\1\164\1\151\1\0\1\60\3\0\1\163\1\0\1"+
        "\uffff\2\0\2\uffff\1\146\1\165\1\141\1\uffff\1\0\1\uffff\1\164\2"+
        "\0\1\145\1\uffff\1\60\2\0\1\145\1\0\1\171\1\154\1\162\1\uffff\1"+
        "\163\2\uffff\1\156\1\60\3\0\2\145\1\0\1\143\1\60\1\uffff\2\0\1\uffff"+
        "\1\145\1\0\2\uffff\1\0\1\uffff";
    public static final String DFA20_maxS =
        "\1\ufffe\1\uffff\1\ufffe\1\157\1\uffff\1\ufffe\1\145\1\162\1\156"+
        "\1\165\1\170\1\162\1\141\1\71\1\ufffe\1\uffff\1\ufffe\1\141\1\uffff"+
        "\1\ufffe\1\57\2\uffff\4\ufffe\3\uffff\1\55\1\ufffe\1\uffff\1\ufffe"+
        "\1\uffff\1\144\1\146\1\ufffe\1\144\1\154\1\164\1\151\1\165\1\163"+
        "\1\154\1\ufffe\1\71\1\uffff\1\165\2\ufffe\1\154\1\ufffe\1\uffff"+
        "\2\ufffe\4\uffff\1\ufffe\2\uffff\1\151\1\162\1\154\1\uffff\1\ufffe"+
        "\1\154\1\ufffe\1\163\1\145\1\164\1\151\1\ufffe\1\146\3\ufffe\1\163"+
        "\1\ufffe\1\uffff\2\ufffe\2\uffff\1\146\1\165\1\141\1\uffff\1\ufffe"+
        "\1\uffff\1\164\2\ufffe\1\145\1\uffff\1\146\2\ufffe\1\145\1\ufffe"+
        "\1\171\1\154\1\162\1\uffff\1\163\2\uffff\1\156\1\146\3\ufffe\2\145"+
        "\1\ufffe\1\143\1\146\1\uffff\2\ufffe\1\uffff\1\145\1\ufffe\2\uffff"+
        "\1\ufffe\1\uffff";
    public static final String DFA20_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\12\uffff\1\20\2\uffff\1\22\2\uffff\1\25"+
        "\1\26\4\uffff\1\33\1\34\1\35\2\uffff\1\42\1\uffff\1\40\14\uffff"+
        "\1\16\5\uffff\1\23\2\uffff\1\27\1\30\1\31\1\32\1\uffff\1\37\1\2"+
        "\3\uffff\1\6\16\uffff\1\41\2\uffff\1\24\1\36\3\uffff\1\7\1\uffff"+
        "\1\10\4\uffff\1\17\10\uffff\1\13\1\uffff\1\21\1\12\12\uffff\1\3"+
        "\2\uffff\1\11\2\uffff\1\5\1\14\1\uffff\1\15";
    public static final String DFA20_specialS =
        "\u0081\uffff}>";
    public static final String[] DFA20_transition = {
        "\11\40\2\4\1\40\1\5\1\4\22\40\1\4\1\40\1\17\1\23\2\40\1\34\1\20"+
        "\1\25\1\26\3\40\1\15\1\40\1\24\12\16\1\37\1\1\1\36\1\2\1\40\1\22"+
        "\33\40\1\27\1\40\1\30\3\40\1\10\2\40\1\6\1\12\1\21\6\40\1\3\1\11"+
        "\1\7\3\40\1\14\1\13\6\40\1\31\1\35\1\32\1\33\uff80\40",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\1\40\1\41\1\uffff\74\40\1\uffff\1\40"+
        "\1\uffff\uff80\40",
        "\1\43",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\44",
        "\1\45",
        "\1\46",
        "\1\50\5\uffff\1\47",
        "\1\51",
        "\1\53\14\uffff\1\52",
        "\1\54",
        "\12\55",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\4\40\1\56\1\40\12\55\1\40\2\uffff\2\40\1\uffff\74"+
        "\40\1\uffff\1\40\1\uffff\uff80\40",
        "",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "\1\63",
        "",
        "\11\64\2\65\2\64\1\65\22\64\1\65\1\64\1\65\3\64\1\65\1\64\2\65\21"+
        "\64\2\65\2\64\1\65\74\64\1\65\1\64\1\65\uff80\64",
        "\1\66\4\uffff\1\67",
        "",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "",
        "",
        "\1\74",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "\1\77",
        "\1\101\2\uffff\1\100",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\103",
        "\1\104",
        "\1\105",
        "\1\106",
        "\1\107",
        "\1\110",
        "\1\111",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\4\40\1\56\1\40\12\55\1\40\2\uffff\2\40\1\uffff\74"+
        "\40\1\uffff\1\40\1\uffff\uff80\40",
        "\12\112",
        "",
        "\1\17\4\uffff\1\114\10\uffff\4\115\4\116\44\uffff\1\114\5\uffff"+
        "\1\114\3\uffff\1\114\7\uffff\1\114\3\uffff\1\114\1\uffff\1\114\1"+
        "\113",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\117",
        "\11\64\2\65\2\64\1\65\22\64\1\65\1\64\1\65\3\64\1\65\1\64\2\65\21"+
        "\64\2\65\2\64\1\65\74\64\1\65\1\64\1\65\uff80\64",
        "",
        "\11\122\2\121\2\122\1\121\22\122\1\121\1\122\1\121\3\122\1\121\1"+
        "\122\2\121\1\120\20\122\2\121\2\122\1\121\74\122\1\121\1\122\1\121"+
        "\uff80\122",
        "\11\123\2\124\2\123\1\124\22\123\1\124\1\123\1\124\3\123\1\124\1"+
        "\123\2\124\21\123\2\124\2\123\1\124\74\123\1\124\1\123\1\124\uff80"+
        "\123",
        "",
        "",
        "",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "",
        "\1\126",
        "\1\127",
        "\1\130",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\132",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\134",
        "\1\135",
        "\1\136",
        "\1\137",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\6\40\12\112\1\40\2\uffff\2\40\1\uffff\74\40\1\uffff"+
        "\1\40\1\uffff\uff80\40",
        "\12\141\7\uffff\6\141\32\uffff\6\141",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\6"+
        "\61\10\142\3\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80"+
        "\61",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\6"+
        "\61\10\143\3\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80"+
        "\61",
        "\1\144",
        "\11\122\2\121\2\122\1\121\22\122\1\121\1\122\1\121\3\122\1\121\1"+
        "\122\2\121\1\120\4\122\1\145\13\122\2\121\2\122\1\121\74\122\1\121"+
        "\1\122\1\121\uff80\122",
        "",
        "\11\122\2\121\2\122\1\121\22\122\1\121\1\122\1\121\3\122\1\121\1"+
        "\122\2\121\1\120\20\122\2\121\2\122\1\121\74\122\1\121\1\122\1\121"+
        "\uff80\122",
        "\11\123\2\124\2\123\1\124\22\123\1\124\1\123\1\124\3\123\1\124\1"+
        "\123\2\124\21\123\2\124\2\123\1\124\74\123\1\124\1\123\1\124\uff80"+
        "\123",
        "",
        "",
        "\1\146",
        "\1\147",
        "\1\150",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "\1\152",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\155",
        "",
        "\12\156\7\uffff\6\156\32\uffff\6\156",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\6"+
        "\61\10\157\3\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80"+
        "\61",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "\1\160",
        "\11\122\2\uffff\2\122\1\uffff\22\122\1\uffff\1\122\1\uffff\3\122"+
        "\1\uffff\1\122\2\uffff\1\120\20\122\2\uffff\2\122\1\uffff\74\122"+
        "\1\uffff\1\122\1\uffff\uff80\122",
        "\1\161",
        "\1\162",
        "\1\163",
        "",
        "\1\164",
        "",
        "",
        "\1\165",
        "\12\166\7\uffff\6\166\32\uffff\6\166",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\170",
        "\1\171",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\1\173",
        "\12\174\7\uffff\6\174\32\uffff\6\174",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        "",
        "\1\177",
        "\11\61\2\17\2\61\1\17\22\61\1\17\1\61\1\17\3\61\1\17\1\62\2\17\21"+
        "\61\2\17\2\61\1\17\34\61\1\60\37\61\1\17\1\61\1\17\uff80\61",
        "",
        "",
        "\11\40\2\uffff\2\40\1\uffff\22\40\1\uffff\1\40\1\uffff\3\40\1\uffff"+
        "\1\40\2\uffff\21\40\2\uffff\2\40\1\uffff\74\40\1\uffff\1\40\1\uffff"+
        "\uff80\40",
        ""
    };

    class DFA20 extends DFA {
        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA.unpackEncodedString(DFA20_eotS);
            this.eof = DFA.unpackEncodedString(DFA20_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
            this.accept = DFA.unpackEncodedString(DFA20_acceptS);
            this.special = DFA.unpackEncodedString(DFA20_specialS);
            int numStates = DFA20_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA20_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( T41 | T42 | T43 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}