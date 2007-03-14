// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-13 02:48:08

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=10;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=35;
    public static final int BOOL=19;
    public static final int DEFRULE=23;
    public static final int HexDigit=27;
    public static final int WS=25;
    public static final int MISC=21;
    public static final int STRING=16;
    public static final int FLOAT=17;
    public static final int T40=40;
    public static final int TILDE=15;
    public static final int OR=8;
    public static final int PIPE=14;
    public static final int VAR=12;
    public static final int UnicodeEscape=28;
    public static final int AND=7;
    public static final int T37=37;
    public static final int EscapeSequence=26;
    public static final int INT=18;
    public static final int EOF=-1;
    public static final int NULL=20;
    public static final int EOL=24;
    public static final int SYMBOL=22;
    public static final int LEFT_SQUARE=32;
    public static final int Tokens=42;
    public static final int OctalEscape=29;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=30;
    public static final int MULTI_LINE_COMMENT=36;
    public static final int AMPERSAND=13;
    public static final int TEST=11;
    public static final int T38=38;
    public static final int T41=41;
    public static final int NOT=9;
    public static final int RIGHT_PAREN=6;
    public static final int LEFT_CURLY=34;
    public static final int T39=39;
    public static final int RIGHT_SQUARE=33;
    public static final int ID=5;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=31;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[40+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T37
    public void mT37() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T37;
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
    // $ANTLR end T37

    // $ANTLR start T38
    public void mT38() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T38;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: ( '<-' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '<-'
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
    // $ANTLR end T38

    // $ANTLR start T39
    public void mT39() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T39;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( ':' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ':'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ( '=' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: '='
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ( 'modify' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: 'modify'
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
    // $ANTLR end T41

    // $ANTLR start DEFRULE
    public void mDEFRULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DEFRULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:11: 'defrule'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:546:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:546:7: 'or'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:547:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:547:8: 'and'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:548:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:548:8: 'not'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:11: 'exists'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:550:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:550:9: 'test'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:552:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:552:8: 'null'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("554:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:19: EOL
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("564:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:565:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:566:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:27: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("578:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:32: ~ ('\\\\'|'\"')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:33: ~ ('\\\\'|'\\'')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:584:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:584:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:588:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);
            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'u':
                    alt11=2;
                    break;
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
                        new NoViableAltException("586:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("586:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:588:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:589:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("593:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("593:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:597:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:597:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:597:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:4: ( 'true' | 'false' )
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
                    new NoViableAltException("606:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:12: 'false'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:57: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:58: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:4: ( ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:4: ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:50: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00FF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:613:51: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:36: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:629:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:629:4: '('
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:633:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:633:4: ')'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:4: '['
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:641:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:641:4: ']'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:4: '{'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:4: '}'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:652:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:652:9: '~'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:4: '&'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: '|'
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:35: .
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
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
                    new NoViableAltException("668:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:22: '>='
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
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
                    new NoViableAltException("674:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:675:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( ((LA21_0>='\u0000' && LA21_0<='\b')||(LA21_0>='\u000B' && LA21_0<='\f')||(LA21_0>='\u000E' && LA21_0<='\u001F')||LA21_0=='!'||(LA21_0>='#' && LA21_0<='%')||LA21_0=='\''||(LA21_0>='*' && LA21_0<=':')||(LA21_0>='=' && LA21_0<='>')||(LA21_0>='@' && LA21_0<='{')||LA21_0=='}'||(LA21_0>='\u007F' && LA21_0<='\uFFFE')) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:675:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T37 | T38 | T39 | T40 | T41 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt22=33;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T37
                {
                mT37(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T38
                {
                mT38(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T39
                {
                mT39(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:26: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:30: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:38: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:41: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:45: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:49: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:56: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:61: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:66: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:69: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:73: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:79: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:86: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:91: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:95: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:98: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:127: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:155: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:166: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:178: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:190: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:203: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:214: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:226: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:232: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:242: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:247: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:266: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:271: SYMBOL
                {
                mSYMBOL(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA22 dfa22 = new DFA22(this);
    public static final String DFA22_eotS =
        "\2\uffff\1\56\1\57\1\60\7\63\1\uffff\1\14\1\56\1\76\1\uffff\1\56"+
        "\1\63\1\56\1\63\1\53\1\56\2\uffff\1\111\1\112\1\113\1\114\3\uffff"+
        "\2\56\1\63\3\56\1\63\4\56\1\uffff\1\116\1\56\3\uffff\2\63\1\uffff"+
        "\1\63\1\121\6\63\1\76\1\53\1\uffff\2\53\1\20\1\63\1\uffff\1\63\1"+
        "\53\1\uffff\2\53\4\uffff\1\56\1\uffff\2\63\1\uffff\1\145\1\146\4"+
        "\63\1\153\4\53\1\63\2\53\2\uffff\1\53\2\63\2\uffff\1\163\1\63\1"+
        "\165\1\166\1\uffff\3\53\1\63\1\140\2\63\1\uffff\1\63\2\uffff\2\53"+
        "\1\166\1\176\1\63\1\u0080\1\53\1\uffff\1\u0082\1\uffff\1\53\1\uffff";
    public static final String DFA22_eofS =
        "\u0083\uffff";
    public static final String DFA22_minS =
        "\1\0\1\uffff\12\0\1\uffff\3\0\1\uffff\2\0\1\44\3\0\2\uffff\4\0\3"+
        "\uffff\13\0\1\uffff\2\0\3\uffff\2\0\1\uffff\11\0\1\60\1\uffff\1"+
        "\42\3\0\1\uffff\2\0\1\uffff\2\0\4\uffff\1\0\1\uffff\2\0\1\uffff"+
        "\10\0\1\60\5\0\2\uffff\3\0\2\uffff\4\0\1\uffff\1\60\6\0\1\uffff"+
        "\1\0\2\uffff\1\60\5\0\1\60\1\uffff\1\0\1\uffff\1\0\1\uffff";
    public static final String DFA22_maxS =
        "\1\ufffe\1\uffff\12\ufffe\1\uffff\3\ufffe\1\uffff\2\ufffe\1\u00ff"+
        "\3\ufffe\2\uffff\4\ufffe\3\uffff\13\ufffe\1\uffff\2\ufffe\3\uffff"+
        "\2\ufffe\1\uffff\11\ufffe\1\71\1\uffff\1\165\3\ufffe\1\uffff\2\ufffe"+
        "\1\uffff\2\ufffe\4\uffff\1\ufffe\1\uffff\2\ufffe\1\uffff\10\ufffe"+
        "\1\146\5\ufffe\2\uffff\3\ufffe\2\uffff\4\ufffe\1\uffff\1\146\6\ufffe"+
        "\1\uffff\1\ufffe\2\uffff\1\146\5\ufffe\1\146\1\uffff\1\ufffe\1\uffff"+
        "\1\ufffe\1\uffff";
    public static final String DFA22_acceptS =
        "\1\uffff\1\1\12\uffff\1\15\3\uffff\1\20\6\uffff\1\26\1\27\4\uffff"+
        "\1\34\1\35\1\36\13\uffff\1\41\2\uffff\1\40\1\3\1\4\2\uffff\1\23"+
        "\12\uffff\1\16\4\uffff\1\22\2\uffff\1\24\2\uffff\1\30\1\31\1\32"+
        "\1\33\1\uffff\1\2\2\uffff\1\7\16\uffff\1\37\1\25\3\uffff\1\10\1"+
        "\11\4\uffff\1\17\7\uffff\1\14\1\uffff\1\13\1\21\7\uffff\1\5\1\uffff"+
        "\1\12\1\uffff\1\6";
    public static final String DFA22_specialS =
        "\u0083\uffff}>";
    public static final String[] DFA22_transition = {
        "\11\53\2\14\1\53\1\15\1\14\22\53\1\14\1\40\1\20\1\25\1\24\1\43\1"+
        "\36\1\21\1\27\1\30\1\45\1\47\1\50\1\16\1\53\1\26\12\17\1\3\1\1\1"+
        "\2\1\4\1\52\1\23\1\41\32\46\1\31\1\51\1\32\1\44\1\42\1\53\1\10\2"+
        "\46\1\6\1\12\1\22\6\46\1\5\1\11\1\7\4\46\1\13\6\46\1\33\1\37\1\34"+
        "\1\35\101\53\100\46\ufeff\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\3\53\1\54\15\53\2\uffff\1\55\1\53\1\uffff\74\53\1"+
        "\uffff\1\53\1\uffff\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\16\62\1\61\13\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\4\62\1\64\25\62\1\53\1\uffff\1\53\1\uffff\101\53\100"+
        "\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\21\62\1\65\10\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\15\62\1\66\14\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\16\62\1\67\5\62\1\70\5\62\1\53\1\uffff\1\53\1\uffff"+
        "\101\53\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\27\62\1\71\2\62\1\53\1\uffff\1\53\1\uffff\101\53\100"+
        "\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\4\62\1\72\14\62\1\73\10\62\1\53\1\uffff\1\53\1\uffff"+
        "\101\53\100\62\ufeff\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\74\1\53\2\uffff\2\53\1\uffff\74\53\1\uffff"+
        "\1\53\1\uffff\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\4\53\1\75\1\53\12\74\1\53\2\uffff\2\53\1\uffff\74"+
        "\53\1\uffff\1\53\1\uffff\uff80\53",
        "",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\1\102\31\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62"+
        "\ufeff\53",
        "\1\103\34\uffff\32\103\4\uffff\1\103\1\uffff\32\103\105\uffff\100"+
        "\103",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\104\1\53\2\uffff\2\53\1\uffff\1\53\32\104"+
        "\4\53\1\104\1\53\32\104\1\53\1\uffff\1\53\1\uffff\101\53\100\104"+
        "\ufeff\53",
        "\11\105\2\106\2\105\1\106\22\105\1\106\1\105\1\106\3\105\1\106\1"+
        "\105\2\106\21\105\2\106\2\105\1\106\74\105\1\106\1\105\1\106\uff80"+
        "\105",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\1\107\4\53\1\110\13\53\2\uffff\2\53\1\uffff\74\53"+
        "\1\uffff\1\53\1\uffff\uff80\53",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\1\115\1\53\1\uffff\74\53\1\uffff\1\53"+
        "\1\uffff\uff80\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\3\62\1\117\26\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\5\62\1\120\24\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\3\62\1\122\26\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\23\62\1\123\6\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\13\62\1\124\16\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\10\62\1\125\21\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\22\62\1\126\7\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\24\62\1\127\5\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\4\53\1\75\1\53\12\74\1\53\2\uffff\2\53\1\uffff\74"+
        "\53\1\uffff\1\53\1\uffff\uff80\53",
        "\12\130",
        "",
        "\1\20\4\uffff\1\131\10\uffff\4\133\4\134\44\uffff\1\131\5\uffff"+
        "\1\131\3\uffff\1\131\7\uffff\1\131\3\uffff\1\131\1\uffff\1\131\1"+
        "\132",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\13\62\1\135\16\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\105\2\106\2\105\1\106\22\105\1\106\1\105\1\106\3\105\1\106\1"+
        "\105\2\106\21\105\2\106\2\105\1\106\74\105\1\106\1\105\1\106\uff80"+
        "\105",
        "",
        "\11\137\2\140\2\137\1\140\22\137\1\140\1\137\1\140\3\137\1\140\1"+
        "\137\2\140\1\136\20\137\2\140\2\137\1\140\74\137\1\140\1\137\1\140"+
        "\uff80\137",
        "\11\142\2\141\2\142\1\141\22\142\1\141\1\142\1\141\3\142\1\141\1"+
        "\142\2\141\21\142\2\141\2\142\1\141\74\142\1\141\1\142\1\141\uff80"+
        "\142",
        "",
        "",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\10\62\1\143\21\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\21\62\1\144\10\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\13\62\1\147\16\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\22\62\1\150\7\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\23\62\1\151\6\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\4\62\1\152\25\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\130\1\53\2\uffff\2\53\1\uffff\74\53\1\uffff"+
        "\1\53\1\uffff\uff80\53",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
        "\12\154\7\uffff\6\154\32\uffff\6\154",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\6\100\10\155\3\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20"+
        "\1\100\1\20\uff80\100",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\6\100\10\156\3\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20"+
        "\1\100\1\20\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\22\62\1\157\7\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\137\2\140\2\137\1\140\22\137\1\140\1\137\1\140\3\137\1\140\1"+
        "\137\2\140\1\136\4\137\1\160\13\137\2\140\2\137\1\140\74\137\1\140"+
        "\1\137\1\140\uff80\137",
        "\11\137\2\140\2\137\1\140\22\137\1\140\1\137\1\140\3\137\1\140\1"+
        "\137\2\140\1\136\20\137\2\140\2\137\1\140\74\137\1\140\1\137\1\140"+
        "\uff80\137",
        "",
        "",
        "\11\142\2\141\2\142\1\141\22\142\1\141\1\142\1\141\3\142\1\141\1"+
        "\142\2\141\21\142\2\141\2\142\1\141\74\142\1\141\1\142\1\141\uff80"+
        "\142",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\5\62\1\161\24\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\24\62\1\162\5\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\23\62\1\164\6\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "",
        "\12\167\7\uffff\6\167\32\uffff\6\167",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\6\100\10\170\3\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20"+
        "\1\100\1\20\uff80\100",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\4\62\1\171\25\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\137\2\uffff\2\137\1\uffff\22\137\1\uffff\1\137\1\uffff\3\137"+
        "\1\uffff\1\137\2\uffff\1\136\20\137\2\uffff\2\137\1\uffff\74\137"+
        "\1\uffff\1\137\1\uffff\uff80\137",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\30\62\1\172\1\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\13\62\1\173\16\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\22\62\1\174\7\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "",
        "",
        "\12\175\7\uffff\6\175\32\uffff\6\175",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\4\62\1\177\25\62\1\53\1\uffff\1\53\1\uffff\101\53"+
        "\100\62\ufeff\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "\12\u0081\7\uffff\6\u0081\32\uffff\6\u0081",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\62\1\53\2\uffff\2\53\1\uffff\1\53\32\62\4"+
        "\53\1\62\1\53\32\62\1\53\1\uffff\1\53\1\uffff\101\53\100\62\ufeff"+
        "\53",
        "",
        "\11\100\2\20\2\100\1\20\22\100\1\20\1\100\1\20\3\100\1\20\1\101"+
        "\2\20\21\100\2\20\2\100\1\20\34\100\1\77\37\100\1\20\1\100\1\20"+
        "\uff80\100",
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
            return "1:1: Tokens : ( T37 | T38 | T39 | T40 | T41 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}