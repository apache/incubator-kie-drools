// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-12 22:15:39

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
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T37
    public void mT37() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T37;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ( ';' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ';'
            {
            match(';'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: ( '<-' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '<-'
            {
            match("<-"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( ':' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ':'
            {
            match(':'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ( '=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: '='
            {
            match('='); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ( 'modify' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: 'modify'
            {
            match("modify"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:544:11: ( 'defrule' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:544:11: 'defrule'
            {
            match("defrule"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:7: ( 'or' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:7: 'or'
            {
            match("or"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:546:8: ( 'and' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:546:8: 'and'
            {
            match("and"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:547:8: ( 'not' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:547:8: 'not'
            {
            match("not"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:548:11: ( 'exists' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:548:11: 'exists'
            {
            match("exists"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:9: ( 'test' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:9: 'test'
            {
            match("test"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:8: ( 'null' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:8: 'null'
            {
            match("null"); if (failed) return ;


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:553:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:553:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:553:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("553:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:553:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:19: EOL
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

                      
            }    }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end WS

    // $ANTLR start EOL
    public void mEOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("563:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:565:25: '\\n'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:4: ( ( '-' )? ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:4: ( '-' )? ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:11: '0' .. '9'
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:11: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:26: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:27: '0' .. '9'
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("577:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:32: ~ ('\\\\'|'\"')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:33: ~ ('\\\\'|'\\'')
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

                      
            }    }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end STRING

    // $ANTLR start HexDigit
    public void mHexDigit() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:587:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("585:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("585:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:587:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:588:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:589:9: OctalEscape
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("592:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("592:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:14: ( '0' .. '3' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:36: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:596:15: '0' .. '7'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:4: ( ( 'true' | 'false' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:4: ( 'true' | 'false' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:4: ( 'true' | 'false' )
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
                    new NoViableAltException("605:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:12: 'false'
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:57: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:58: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:4: ( ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:4: ('a'..'z'|'A'..'Z'|'_'|'$'|'\\u00c0'..'\\u00ff') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:50: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff'))*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||LA15_0=='_'||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00FF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:51: ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'\\u00c0'..'\\u00ff')
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:8: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:35: .
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:622:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:622:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:622:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:622:36: .
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:4: ( '(' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:4: '('
            {
            match('('); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:4: ( ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:4: ')'
            {
            match(')'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:4: ( '[' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:4: '['
            {
            match('['); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:4: ( ']' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:4: ']'
            {
            match(']'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:4: ( '{' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:4: '{'
            {
            match('{'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:4: ( '}' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:4: '}'
            {
            match('}'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:651:9: ( '~' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:651:9: '~'
            {
            match('~'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:4: ( '&' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:4: '&'
            {
            match('&'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:659:4: ( '|' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:659:4: '|'
            {
            match('|'); if (failed) return ;

            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:35: .
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
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
                    new NoViableAltException("667:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:668:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:22: '>='
                    {
                    match(">="); if (failed) return ;


                    }
                    break;

            }

            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: ( (~ (' '|'('|')'|'~'|'\"'|'?'|'&'|'|'))+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: (~ (' '|'('|')'|'~'|'\"'|'?'|'&'|'|'))+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: (~ (' '|'('|')'|'~'|'\"'|'?'|'&'|'|'))+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                if ( ((LA20_0>='\u0000' && LA20_0<='\u001F')||LA20_0=='!'||(LA20_0>='#' && LA20_0<='%')||LA20_0=='\''||(LA20_0>='*' && LA20_0<='>')||(LA20_0>='@' && LA20_0<='{')||LA20_0=='}'||(LA20_0>='\u007F' && LA20_0<='\uFFFE')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:5: ~ (' '|'('|')'|'~'|'\"'|'?'|'&'|'|')
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u001F')||input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||input.LA(1)=='\''||(input.LA(1)>='*' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='{')||input.LA(1)=='}'||(input.LA(1)>='\u007F' && input.LA(1)<='\uFFFE') ) {
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
            	    if ( cnt20 >= 1 ) break loop20;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            }


            if ( backtracking==0 ) {

                      if ( token==null && ruleNestingLevel==1 ) {
                          emit(_type,_line,_charPosition,_channel,_start,getCharIndex()-1);
                      }

                      
            }    }
        finally {
            ruleNestingLevel--;
        }
    }
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T37 | T38 | T39 | T40 | T41 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt21=33;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T37
                {
                mT37(); if (failed) return ;

                }
                break;
            case 2 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T38
                {
                mT38(); if (failed) return ;

                }
                break;
            case 3 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T39
                {
                mT39(); if (failed) return ;

                }
                break;
            case 4 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 5 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:26: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 6 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:30: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 7 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:38: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 8 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:41: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 9 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:45: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 10 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:49: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 11 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:56: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 12 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:61: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 13 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:66: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 14 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:69: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 15 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:73: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 16 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:79: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 17 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:86: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 18 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:91: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 19 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:95: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 20 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:98: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 21 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:127: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 22 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:155: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 23 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:166: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 24 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:178: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 25 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:190: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 26 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:203: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 27 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:214: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 28 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:226: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 29 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:232: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 30 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:242: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 31 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:247: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 32 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:266: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 33 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:271: SYMBOL
                {
                mSYMBOL(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA21 dfa21 = new DFA21(this);
    public static final String DFA21_eotS =
        "\1\uffff\1\57\1\62\1\63\1\64\7\66\1\uffff\4\14\1\62\1\101\1\uffff"+
        "\1\62\1\66\1\62\1\66\1\56\1\62\2\uffff\1\116\1\117\1\120\1\121\3"+
        "\uffff\2\62\1\66\3\62\1\66\4\62\2\uffff\1\123\1\62\3\uffff\1\66"+
        "\1\uffff\2\66\1\126\6\66\1\14\1\uffff\3\56\1\23\1\66\1\uffff\1\111"+
        "\1\uffff\1\111\3\56\4\uffff\1\62\1\uffff\2\66\1\uffff\1\155\1\66"+
        "\1\157\3\66\1\163\4\56\1\66\1\111\1\145\1\uffff\1\145\2\56\1\uffff"+
        "\1\56\2\66\1\uffff\1\174\1\uffff\1\66\1\176\1\177\1\uffff\3\56\1"+
        "\66\1\145\1\151\2\66\1\uffff\1\66\2\uffff\2\56\1\176\1\u0087\1\66"+
        "\1\u0089\1\56\1\uffff\1\u008b\1\uffff\1\56\1\uffff";
    public static final String DFA21_eofS =
        "\u008c\uffff";
    public static final String DFA21_minS =
        "\14\0\1\uffff\6\0\1\uffff\2\0\1\44\3\0\2\uffff\4\0\3\uffff\13\0"+
        "\2\uffff\2\0\3\uffff\1\0\1\uffff\12\0\1\uffff\1\60\1\42\3\0\1\uffff"+
        "\1\0\1\uffff\4\0\4\uffff\1\0\1\uffff\2\0\1\uffff\10\0\1\60\5\0\1"+
        "\uffff\3\0\1\uffff\3\0\1\uffff\1\0\1\uffff\3\0\1\uffff\1\60\7\0"+
        "\1\uffff\1\0\2\uffff\1\60\5\0\1\60\1\uffff\1\0\1\uffff\1\0\1\uffff";
    public static final String DFA21_maxS =
        "\14\ufffe\1\uffff\6\ufffe\1\uffff\2\ufffe\1\u00ff\3\ufffe\2\uffff"+
        "\4\ufffe\3\uffff\13\ufffe\2\uffff\2\ufffe\3\uffff\1\ufffe\1\uffff"+
        "\12\ufffe\1\uffff\1\71\1\165\3\ufffe\1\uffff\1\ufffe\1\uffff\4\ufffe"+
        "\4\uffff\1\ufffe\1\uffff\2\ufffe\1\uffff\10\ufffe\1\146\5\ufffe"+
        "\1\uffff\3\ufffe\1\uffff\3\ufffe\1\uffff\1\ufffe\1\uffff\3\ufffe"+
        "\1\uffff\1\146\7\ufffe\1\uffff\1\ufffe\2\uffff\1\146\5\ufffe\1\146"+
        "\1\uffff\1\ufffe\1\uffff\1\ufffe\1\uffff";
    public static final String DFA21_acceptS =
        "\14\uffff\1\15\6\uffff\1\20\6\uffff\1\26\1\27\4\uffff\1\34\1\35"+
        "\1\36\13\uffff\1\41\1\1\2\uffff\1\40\1\3\1\4\1\uffff\1\23\12\uffff"+
        "\1\16\5\uffff\1\22\1\uffff\1\24\4\uffff\1\30\1\31\1\32\1\33\1\uffff"+
        "\1\2\2\uffff\1\7\16\uffff\1\25\3\uffff\1\37\3\uffff\1\10\1\uffff"+
        "\1\11\3\uffff\1\17\10\uffff\1\14\1\uffff\1\21\1\13\7\uffff\1\5\1"+
        "\uffff\1\12\1\uffff\1\6";
    public static final String DFA21_specialS =
        "\u008c\uffff}>";
    public static final String[] DFA21_transition = {
        "\11\56\1\15\1\20\1\56\1\16\1\17\22\56\1\14\1\43\1\23\1\30\1\27\1"+
        "\46\1\41\1\24\1\32\1\33\1\50\1\52\1\53\1\21\1\56\1\31\12\22\1\3"+
        "\1\1\1\2\1\4\1\55\1\26\1\44\32\51\1\34\1\54\1\35\1\47\1\45\1\56"+
        "\1\10\2\51\1\6\1\12\1\25\6\51\1\5\1\11\1\7\4\51\1\13\6\51\1\36\1"+
        "\42\1\37\1\40\101\56\100\51\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\3\56\1\60"+
        "\17\56\1\61\1\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\16\67\1\65\13\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\4\67\1\70\25\67\1\56\1\uffff"+
        "\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\21\67\1\71\10\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\15\67\1\72\14\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\16\67\1\74\5\67\1\73\5\67"+
        "\1\56\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\27\67\1\75\2\67\1\56\1\uffff"+
        "\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\4\67\1\77\14\67\1\76\10"+
        "\67\1\56\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\12\56\1\100\25\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff"+
        "\25\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\22"+
        "\5\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\4\56\1\102"+
        "\1\56\12\22\5\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\1\106\31\67\1\56\1\uffff"+
        "\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\1\107\34\uffff\32\107\4\uffff\1\107\1\uffff\32\107\105\uffff\100"+
        "\107",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\12\113\1\112\2\113\1\110\22\113\1\111\1\113\1\111\3\113\1\111\1"+
        "\113\2\111\25\113\1\111\74\113\1\111\1\113\1\111\uff80\113",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\1\115\4\56"+
        "\1\114\17\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\23\56\1\122"+
        "\1\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\3\67\1\124\26\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\5\67\1\125\24\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\3\67\1\127\26\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\13\67\1\130\16\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\23\67\1\131\6\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\10\67\1\132\21\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\24\67\1\133\5\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\22\67\1\134\7\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "\12\135",
        "\1\23\4\uffff\1\136\10\uffff\4\140\4\141\44\uffff\1\136\5\uffff"+
        "\1\136\3\uffff\1\136\7\uffff\1\136\3\uffff\1\136\1\uffff\1\136\1"+
        "\137",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\13\67\1\142\16\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\12\113\1\143\2\113\1\110\22\113\1\uffff\1\113\1\uffff\3\113\1\uffff"+
        "\1\113\2\uffff\25\113\1\uffff\74\113\1\uffff\1\113\1\uffff\uff80"+
        "\113",
        "",
        "\12\113\1\112\2\113\1\110\22\113\1\uffff\1\113\1\uffff\3\113\1\uffff"+
        "\1\113\2\uffff\25\113\1\uffff\74\113\1\uffff\1\113\1\uffff\uff80"+
        "\113",
        "\12\113\1\112\2\113\1\110\22\113\1\111\1\113\1\111\3\113\1\111\1"+
        "\113\2\111\25\113\1\111\74\113\1\111\1\113\1\111\uff80\113",
        "\12\147\1\146\2\147\1\144\22\147\1\145\1\147\1\145\3\147\1\145\1"+
        "\147\2\145\25\147\1\145\74\147\1\145\1\147\1\145\uff80\147",
        "\40\152\1\151\1\152\1\151\3\152\1\151\1\152\2\151\1\150\24\152\1"+
        "\151\74\152\1\151\1\152\1\151\uff80\152",
        "",
        "",
        "",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\25\56\1\uffff"+
        "\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\10\67\1\153\21\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\21\67\1\154\10\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\13\67\1\156\16\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\22\67\1\160\7\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\4\67\1\161\25\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\23\67\1\162\6\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\135"+
        "\5\56\1\uffff\74\56\1\uffff\1\56\1\uffff\uff80\56",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\12\164\7\uffff\6\164\32\uffff\6\164",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\6\104\10\165\7\104"+
        "\1\23\34\104\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\6\104\10\166\7\104"+
        "\1\23\34\104\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\22\67\1\167\7\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\12\113\1\112\2\113\1\110\22\113\1\uffff\1\113\1\uffff\3\113\1\uffff"+
        "\1\113\2\uffff\25\113\1\uffff\74\113\1\uffff\1\113\1\uffff\uff80"+
        "\113",
        "\12\147\1\170\2\147\1\144\22\147\1\uffff\1\147\1\uffff\3\147\1\uffff"+
        "\1\147\2\uffff\25\147\1\uffff\74\147\1\uffff\1\147\1\uffff\uff80"+
        "\147",
        "",
        "\12\147\1\146\2\147\1\144\22\147\1\uffff\1\147\1\uffff\3\147\1\uffff"+
        "\1\147\2\uffff\25\147\1\uffff\74\147\1\uffff\1\147\1\uffff\uff80"+
        "\147",
        "\12\147\1\146\2\147\1\144\22\147\1\145\1\147\1\145\3\147\1\145\1"+
        "\147\2\145\25\147\1\145\74\147\1\145\1\147\1\145\uff80\147",
        "\40\152\1\151\1\152\1\151\3\152\1\151\1\152\2\151\1\150\4\152\1"+
        "\171\17\152\1\151\74\152\1\151\1\152\1\151\uff80\152",
        "",
        "\40\152\1\151\1\152\1\151\3\152\1\151\1\152\2\151\1\150\24\152\1"+
        "\151\74\152\1\151\1\152\1\151\uff80\152",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\5\67\1\172\24\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\24\67\1\173\5\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\23\67\1\175\6\67\1\56\1"+
        "\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\12\u0080\7\uffff\6\u0080\32\uffff\6\u0080",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\6\104\10\u0081\7\104"+
        "\1\23\34\104\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\4\67\1\u0082\25\67\1\56"+
        "\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\12\147\1\146\2\147\1\144\22\147\1\uffff\1\147\1\uffff\3\147\1\uffff"+
        "\1\147\2\uffff\25\147\1\uffff\74\147\1\uffff\1\147\1\uffff\uff80"+
        "\147",
        "\40\152\1\uffff\1\152\1\uffff\3\152\1\uffff\1\152\2\uffff\1\150"+
        "\24\152\1\uffff\74\152\1\uffff\1\152\1\uffff\uff80\152",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\30\67\1\u0083\1\67\1\56"+
        "\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\13\67\1\u0084\16\67\1\56"+
        "\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\22\67\1\u0085\7\67\1\56"+
        "\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "",
        "",
        "\12\u0086\7\uffff\6\u0086\32\uffff\6\u0086",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\4\67\1\u0088\25\67\1\56"+
        "\1\uffff\1\56\1\uffff\101\56\100\67\ufeff\56",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "\12\u008a\7\uffff\6\u008a\32\uffff\6\u008a",
        "",
        "\40\56\1\uffff\1\56\1\uffff\3\56\1\uffff\1\56\2\uffff\6\56\12\67"+
        "\5\56\1\uffff\1\56\32\67\4\56\1\67\1\56\32\67\1\56\1\uffff\1\56"+
        "\1\uffff\101\56\100\67\ufeff\56",
        "",
        "\40\104\1\23\1\104\1\23\3\104\1\23\1\105\2\23\25\104\1\23\34\104"+
        "\1\103\37\104\1\23\1\104\1\23\uff80\104",
        ""
    };

    class DFA21 extends DFA {
        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA.unpackEncodedString(DFA21_eotS);
            this.eof = DFA.unpackEncodedString(DFA21_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
            this.accept = DFA.unpackEncodedString(DFA21_acceptS);
            this.special = DFA.unpackEncodedString(DFA21_specialS);
            int numStates = DFA21_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA21_transition[i]);
            }
        }
        public String getDescription() {
            return "1:1: Tokens : ( T37 | T38 | T39 | T40 | T41 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | INT | FLOAT | STRING | BOOL | VAR | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}