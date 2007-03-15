// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-15 13:31:31

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
    public static final int HexDigit=28;
    public static final int MISC=23;
    public static final int FLOAT=20;
    public static final int TILDE=19;
    public static final int OR=12;
    public static final int PIPE=18;
    public static final int AND=11;
    public static final int EscapeSequence=27;
    public static final int INT=10;
    public static final int SYMBOL=6;
    public static final int LEFT_SQUARE=33;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=31;
    public static final int AMPERSAND=17;
    public static final int T38=38;
    public static final int DECLARE=26;
    public static final int T41=41;
    public static final int LEFT_CURLY=35;
    public static final int T39=39;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=36;
    public static final int BOOL=21;
    public static final int WS=25;
    public static final int STRING=7;
    public static final int T43=43;
    public static final int T42=42;
    public static final int T40=40;
    public static final int VAR=16;
    public static final int UnicodeEscape=29;
    public static final int EOF=-1;
    public static final int EOL=24;
    public static final int NULL=22;
    public static final int Tokens=44;
    public static final int OctalEscape=30;
    public static final int SALIENCE=9;
    public static final int MULTI_LINE_COMMENT=37;
    public static final int TEST=15;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=13;
    public static final int RIGHT_SQUARE=34;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=32;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[42+1];
     }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T38
    public void mT38() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T38;
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: ( '=>' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '=>'
            {
            match("=>"); if (failed) return ;


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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: ( '<-' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:8:7: '<-'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ( ':' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:9:7: ':'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: ( '=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:10:7: '='
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: ( 'modify' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:11:7: 'modify'
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
    // $ANTLR end T43

    // $ANTLR start DEFRULE
    public void mDEFRULE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DEFRULE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:598:11: ( 'defrule' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:598:11: 'defrule'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:7: ( 'or' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:7: 'or'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:600:8: ( 'and' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:600:8: 'and'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:8: ( 'not' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:8: 'not'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:11: ( 'exists' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:11: 'exists'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: ( 'test' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: 'test'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:8: ( 'null' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:8: 'null'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("608:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:19: EOL
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

    // $ANTLR start DECLARE
    public void mDECLARE() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = DECLARE;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:4: ( 'declare' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:4: 'declare'
            {
            match("declare"); if (failed) return ;


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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:4: ( 'salience' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:620:4: 'salience'
            {
            match("salience"); if (failed) return ;


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
    // $ANTLR end SALIENCE

    // $ANTLR start EOL
    public void mEOL() throws RecognitionException {
        try {
            ruleNestingLevel++;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("624:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:626:25: '\\n'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:4: ( ( '-' )? ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:4: ( '-' )? ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:11: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:11: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:26: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:635:27: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("638:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:639:32: ~ ('\\\\'|'\"')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:33: ~ ('\\\\'|'\\'')
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("646:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("646:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:649:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:650:9: OctalEscape
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("653:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("653:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:14: ( '0' .. '3' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:36: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:657:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:657:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:657:15: '0' .. '7'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:4: ( ( 'true' | 'false' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:4: ( 'true' | 'false' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:4: ( 'true' | 'false' )
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
                    new NoViableAltException("666:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:12: 'false'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:38: ( ('a'..'z'|'A'..'Z'|'_'|'0'..'9'))*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:39: ('a'..'z'|'A'..'Z'|'_'|'0'..'9')
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

                      
            }    }
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:8: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:677:35: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:36: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:4: ( '(' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:4: '('
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:4: ( ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:4: ')'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: ( '[' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:4: '['
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:701:4: ( ']' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:701:4: ']'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:4: ( '{' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:4: '{'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:4: ( '}' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:709:4: '}'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:9: ( '~' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:9: '~'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: ( '&' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: '&'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:4: ( '|' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:4: '|'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:724:35: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' )
            int alt18=19;
            switch ( input.LA(1) ) {
            case '!':
                alt18=1;
                break;
            case '@':
                alt18=2;
                break;
            case '$':
                alt18=3;
                break;
            case '%':
                alt18=4;
                break;
            case '^':
                alt18=5;
                break;
            case '*':
                alt18=6;
                break;
            case '_':
                alt18=7;
                break;
            case '-':
                alt18=8;
                break;
            case '+':
                alt18=9;
                break;
            case '?':
                alt18=10;
                break;
            case ',':
                alt18=11;
                break;
            case '=':
                alt18=12;
                break;
            case '/':
                alt18=13;
                break;
            case '\'':
                alt18=14;
                break;
            case '\\':
                alt18=15;
                break;
            case '<':
                int LA18_16 = input.LA(2);
                if ( (LA18_16=='=') ) {
                    alt18=18;
                }
                else {
                    alt18=16;}
                break;
            case '>':
                int LA18_17 = input.LA(2);
                if ( (LA18_17=='=') ) {
                    alt18=19;
                }
                else {
                    alt18=17;}
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("728:1: MISC : ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | ',' | '=' | '/' | '\\'' | '\\\\' | '<' | '>' | '<=' | '>=' );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:3: '!'
                    {
                    match('!'); if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:9: '@'
                    {
                    match('@'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:15: '$'
                    {
                    match('$'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:21: '%'
                    {
                    match('%'); if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:27: '^'
                    {
                    match('^'); if (failed) return ;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:33: '*'
                    {
                    match('*'); if (failed) return ;

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:39: '_'
                    {
                    match('_'); if (failed) return ;

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:45: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:51: '+'
                    {
                    match('+'); if (failed) return ;

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:58: '?'
                    {
                    match('?'); if (failed) return ;

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:64: ','
                    {
                    match(','); if (failed) return ;

                    }
                    break;
                case 12 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:70: '='
                    {
                    match('='); if (failed) return ;

                    }
                    break;
                case 13 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:76: '/'
                    {
                    match('/'); if (failed) return ;

                    }
                    break;
                case 14 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:82: '\\''
                    {
                    match('\''); if (failed) return ;

                    }
                    break;
                case 15 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:89: '\\\\'
                    {
                    match('\\'); if (failed) return ;

                    }
                    break;
                case 16 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:3: '<'
                    {
                    match('<'); if (failed) return ;

                    }
                    break;
                case 17 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:9: '>'
                    {
                    match('>'); if (failed) return ;

                    }
                    break;
                case 18 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:15: '<='
                    {
                    match("<="); if (failed) return ;


                    }
                    break;
                case 19 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:22: '>='
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\u001F')||LA19_0=='!'||LA19_0=='#'||LA19_0=='%'||LA19_0=='\''||(LA19_0>='*' && LA19_0<=':')||(LA19_0>='<' && LA19_0<='>')||(LA19_0>='@' && LA19_0<='{')||LA19_0=='}'||(LA19_0>='\u007F' && LA19_0<='\uFFFE')) ) {
                alt19=1;
            }
            else if ( (LA19_0=='$') ) {
                alt19=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("734:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:734:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                if ( ((LA20_0>='\u0000' && LA20_0<='\b')||(LA20_0>='\u000B' && LA20_0<='\f')||(LA20_0>='\u000E' && LA20_0<='\u001F')||LA20_0=='!'||(LA20_0>='#' && LA20_0<='%')||LA20_0=='\''||(LA20_0>='*' && LA20_0<=':')||(LA20_0>='=' && LA20_0<='>')||(LA20_0>='@' && LA20_0<='{')||LA20_0=='}'||(LA20_0>='\u007F' && LA20_0<='\uFFFE')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
            	    break loop20;
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
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T38 | T39 | T40 | T41 | T42 | T43 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL )
        int alt21=35;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T38
                {
                mT38(); if (failed) return ;

                }
                break;
            case 2 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T39
                {
                mT39(); if (failed) return ;

                }
                break;
            case 3 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: T40
                {
                mT40(); if (failed) return ;

                }
                break;
            case 4 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:22: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 5 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:26: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 6 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:30: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 7 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:34: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 8 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:42: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 9 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:45: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 10 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:49: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 11 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:53: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 12 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:60: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 13 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:65: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 14 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:70: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 15 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:73: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 16 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:81: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 17 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:90: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 18 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:94: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 19 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:100: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 20 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:107: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 21 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:112: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 22 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:116: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 23 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:145: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 24 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:173: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 25 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:184: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 26 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:196: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 27 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:208: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 28 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:221: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 29 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:232: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 30 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:244: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 31 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:250: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 32 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:260: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 33 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:265: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 34 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:284: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;
            case 35 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:289: SYMBOL
                {
                mSYMBOL(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA21 dfa21 = new DFA21(this);
    public static final String DFA21_eotS =
        "\2\uffff\1\55\1\60\1\61\7\53\1\uffff\1\14\1\53\1\60\1\75\1\uffff"+
        "\1\60\1\53\1\60\1\53\1\60\2\uffff\1\110\1\111\1\112\1\113\3\uffff"+
        "\13\60\1\uffff\1\115\1\uffff\1\60\1\116\2\uffff\2\53\1\122\7\53"+
        "\1\75\1\uffff\3\53\1\21\1\53\2\uffff\3\53\4\uffff\1\60\2\uffff\3"+
        "\53\1\uffff\1\150\1\53\1\152\4\53\1\157\5\53\1\uffff\2\53\1\uffff"+
        "\4\53\1\uffff\1\170\1\uffff\1\53\1\172\1\173\1\53\1\uffff\4\53\1"+
        "\143\3\53\1\uffff\1\53\2\uffff\3\53\1\173\1\u0086\2\53\1\u0089\2"+
        "\53\1\uffff\1\u008c\1\u008d\1\uffff\2\53\2\uffff\1\u008f\1\uffff";
    public static final String DFA21_eofS =
        "\u0090\uffff";
    public static final String DFA21_minS =
        "\1\0\1\uffff\3\0\1\157\1\145\1\162\1\156\1\157\1\170\1\145\1\uffff"+
        "\1\0\1\141\2\0\1\uffff\1\0\1\141\1\44\2\0\2\uffff\4\0\3\uffff\13"+
        "\0\1\uffff\1\0\1\uffff\2\0\2\uffff\1\144\1\143\1\0\1\144\1\154\1"+
        "\164\1\151\1\163\1\165\1\154\1\0\1\uffff\1\60\1\42\2\0\1\154\2\uffff"+
        "\3\0\4\uffff\1\0\2\uffff\1\151\1\154\1\162\1\uffff\1\0\1\154\1\0"+
        "\1\163\1\164\1\145\1\151\1\0\1\60\3\0\1\163\1\uffff\2\0\1\uffff"+
        "\1\0\1\146\1\141\1\165\1\uffff\1\0\1\uffff\1\164\2\0\1\145\1\uffff"+
        "\1\60\2\0\1\145\1\0\1\171\1\162\1\154\1\uffff\1\163\2\uffff\1\156"+
        "\1\60\3\0\2\145\1\0\1\143\1\60\1\uffff\2\0\1\uffff\1\145\1\0\2\uffff"+
        "\1\0\1\uffff";
    public static final String DFA21_maxS =
        "\1\ufffe\1\uffff\3\ufffe\1\157\1\145\1\162\1\156\1\165\1\170\1\162"+
        "\1\uffff\1\ufffe\1\141\2\ufffe\1\uffff\1\ufffe\1\141\1\172\2\ufffe"+
        "\2\uffff\4\ufffe\3\uffff\13\ufffe\1\uffff\1\ufffe\1\uffff\2\ufffe"+
        "\2\uffff\1\144\1\146\1\ufffe\1\144\1\154\1\164\1\151\1\163\1\165"+
        "\1\154\1\ufffe\1\uffff\1\71\1\165\2\ufffe\1\154\2\uffff\3\ufffe"+
        "\4\uffff\1\ufffe\2\uffff\1\151\1\154\1\162\1\uffff\1\ufffe\1\154"+
        "\1\ufffe\1\163\1\164\1\145\1\151\1\ufffe\1\146\3\ufffe\1\163\1\uffff"+
        "\2\ufffe\1\uffff\1\ufffe\1\146\1\141\1\165\1\uffff\1\ufffe\1\uffff"+
        "\1\164\2\ufffe\1\145\1\uffff\1\146\2\ufffe\1\145\1\ufffe\1\171\1"+
        "\162\1\154\1\uffff\1\163\2\uffff\1\156\1\146\3\ufffe\2\145\1\ufffe"+
        "\1\143\1\146\1\uffff\2\ufffe\1\uffff\1\145\1\ufffe\2\uffff\1\ufffe"+
        "\1\uffff";
    public static final String DFA21_acceptS =
        "\1\uffff\1\1\12\uffff\1\16\4\uffff\1\23\5\uffff\1\30\1\31\4\uffff"+
        "\1\36\1\37\1\40\13\uffff\1\43\1\uffff\1\5\2\uffff\1\42\1\4\13\uffff"+
        "\1\21\5\uffff\1\25\1\26\3\uffff\1\32\1\33\1\34\1\35\1\uffff\1\2"+
        "\1\3\3\uffff\1\10\15\uffff\1\27\2\uffff\1\41\4\uffff\1\11\1\uffff"+
        "\1\12\4\uffff\1\22\10\uffff\1\15\1\uffff\1\14\1\24\12\uffff\1\6"+
        "\2\uffff\1\13\2\uffff\1\17\1\7\1\uffff\1\20";
    public static final String DFA21_specialS =
        "\u0090\uffff}>";
    public static final String[] DFA21_transition = {
        "\11\53\2\14\1\53\1\15\1\14\22\53\1\14\1\40\1\21\1\25\1\42\1\43\1"+
        "\36\1\22\1\27\1\30\1\45\1\47\1\50\1\17\1\53\1\26\12\20\1\4\1\1\1"+
        "\3\1\2\1\52\1\24\1\41\32\53\1\31\1\51\1\32\1\44\1\46\1\53\1\10\2"+
        "\53\1\6\1\12\1\23\6\53\1\5\1\11\1\7\3\53\1\16\1\13\6\53\1\33\1\37"+
        "\1\34\1\35\uff80\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\1\53\1\54\1\uffff\74\53\1\uffff\1\53"+
        "\1\uffff\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\3\53\1\57\15\53\2\uffff\1\56\1\53\1\uffff\74\53\1"+
        "\uffff\1\53\1\uffff\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\62",
        "\1\63",
        "\1\64",
        "\1\65",
        "\1\67\5\uffff\1\66",
        "\1\70",
        "\1\71\14\uffff\1\72",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\73",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\74\1\53\2\uffff\2\53\1\uffff\74\53\1\uffff"+
        "\1\53\1\uffff\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\4\53\1\76\1\53\12\74\1\53\2\uffff\2\53\1\uffff\74"+
        "\53\1\uffff\1\53\1\uffff\uff80\53",
        "",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "\1\102",
        "\1\103\34\uffff\32\103\4\uffff\1\103\1\uffff\32\103",
        "\11\105\2\104\2\105\1\104\22\105\1\104\1\105\1\104\3\105\1\104\1"+
        "\105\2\104\21\105\2\104\2\105\1\104\74\105\1\104\1\105\1\104\uff80"+
        "\105",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\1\107\4\53\1\106\13\53\2\uffff\2\53\1\uffff\74\53"+
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
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\1\114\1\53\1\uffff\74\53\1\uffff\1\53"+
        "\1\uffff\uff80\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "",
        "\1\117",
        "\1\120\2\uffff\1\121",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\123",
        "\1\124",
        "\1\125",
        "\1\126",
        "\1\127",
        "\1\130",
        "\1\131",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\4\53\1\76\1\53\12\74\1\53\2\uffff\2\53\1\uffff\74"+
        "\53\1\uffff\1\53\1\uffff\uff80\53",
        "",
        "\12\132",
        "\1\21\4\uffff\1\134\10\uffff\4\135\4\136\44\uffff\1\134\5\uffff"+
        "\1\134\3\uffff\1\134\7\uffff\1\134\3\uffff\1\134\1\uffff\1\134\1"+
        "\133",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\137",
        "",
        "",
        "\11\105\2\104\2\105\1\104\22\105\1\104\1\105\1\104\3\105\1\104\1"+
        "\105\2\104\21\105\2\104\2\105\1\104\74\105\1\104\1\105\1\104\uff80"+
        "\105",
        "\11\141\2\140\2\141\1\140\22\141\1\140\1\141\1\140\3\141\1\140\1"+
        "\141\2\140\21\141\2\140\2\141\1\140\74\141\1\140\1\141\1\140\uff80"+
        "\141",
        "\11\144\2\143\2\144\1\143\22\144\1\143\1\144\1\143\3\144\1\143\1"+
        "\144\2\143\1\142\20\144\2\143\2\144\1\143\74\144\1\143\1\144\1\143"+
        "\uff80\144",
        "",
        "",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "",
        "\1\145",
        "\1\146",
        "\1\147",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\151",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\153",
        "\1\154",
        "\1\155",
        "\1\156",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\6\53\12\132\1\53\2\uffff\2\53\1\uffff\74\53\1\uffff"+
        "\1\53\1\uffff\uff80\53",
        "\12\160\7\uffff\6\160\32\uffff\6\160",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\6\100\10\161\3\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21"+
        "\1\100\1\21\uff80\100",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\6\100\10\162\3\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21"+
        "\1\100\1\21\uff80\100",
        "\1\163",
        "",
        "\11\141\2\140\2\141\1\140\22\141\1\140\1\141\1\140\3\141\1\140\1"+
        "\141\2\140\21\141\2\140\2\141\1\140\74\141\1\140\1\141\1\140\uff80"+
        "\141",
        "\11\144\2\143\2\144\1\143\22\144\1\143\1\144\1\143\3\144\1\143\1"+
        "\144\2\143\1\142\4\144\1\164\13\144\2\143\2\144\1\143\74\144\1\143"+
        "\1\144\1\143\uff80\144",
        "",
        "\11\144\2\143\2\144\1\143\22\144\1\143\1\144\1\143\3\144\1\143\1"+
        "\144\2\143\1\142\20\144\2\143\2\144\1\143\74\144\1\143\1\144\1\143"+
        "\uff80\144",
        "\1\165",
        "\1\166",
        "\1\167",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "\1\171",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\174",
        "",
        "\12\175\7\uffff\6\175\32\uffff\6\175",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\6\100\10\176\3\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21"+
        "\1\100\1\21\uff80\100",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "\1\177",
        "\11\144\2\uffff\2\144\1\uffff\22\144\1\uffff\1\144\1\uffff\3\144"+
        "\1\uffff\1\144\2\uffff\1\142\20\144\2\uffff\2\144\1\uffff\74\144"+
        "\1\uffff\1\144\1\uffff\uff80\144",
        "\1\u0080",
        "\1\u0081",
        "\1\u0082",
        "",
        "\1\u0083",
        "",
        "",
        "\1\u0084",
        "\12\u0085\7\uffff\6\u0085\32\uffff\6\u0085",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\u0087",
        "\1\u0088",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\1\u008a",
        "\12\u008b\7\uffff\6\u008b\32\uffff\6\u008b",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
        "",
        "\1\u008e",
        "\11\100\2\21\2\100\1\21\22\100\1\21\1\100\1\21\3\100\1\21\1\101"+
        "\2\21\21\100\2\21\2\100\1\21\34\100\1\77\37\100\1\21\1\100\1\21"+
        "\uff80\100",
        "",
        "",
        "\11\53\2\uffff\2\53\1\uffff\22\53\1\uffff\1\53\1\uffff\3\53\1\uffff"+
        "\1\53\2\uffff\21\53\2\uffff\2\53\1\uffff\74\53\1\uffff\1\53\1\uffff"+
        "\uff80\53",
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
            return "1:1: Tokens : ( T38 | T39 | T40 | T41 | T42 | T43 | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | WS | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | MULTI_LINE_COMMENT | MISC | SYMBOL );";
        }
    }
 

}