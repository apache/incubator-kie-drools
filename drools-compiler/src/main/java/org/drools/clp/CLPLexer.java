// $ANTLR 3.0b7 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-22 15:54:13

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int EXISTS=14;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=39;
    public static final int BOOL=24;
    public static final int DEFRULE=5;
    public static final int HexDigit=30;
    public static final int WS=27;
    public static final int STRING=7;
    public static final int FLOAT=23;
    public static final int TILDE=20;
    public static final int OR=12;
    public static final int PIPE=19;
    public static final int EQUALS=22;
    public static final int VAR=16;
    public static final int ASSIGN_OP=17;
    public static final int AND=11;
    public static final int UnicodeEscape=31;
    public static final int EscapeSequence=29;
    public static final int INT=10;
    public static final int EOF=-1;
    public static final int NULL=25;
    public static final int EOL=26;
    public static final int SYMBOL=33;
    public static final int LEFT_SQUARE=36;
    public static final int Tokens=42;
    public static final int COLON=21;
    public static final int SALIENCE=9;
    public static final int OctalEscape=32;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int AMPERSAND=18;
    public static final int TEST=15;
    public static final int NAME=6;
    public static final int T41=41;
    public static final int NOT=13;
    public static final int RIGHT_PAREN=8;
    public static final int DECLARE=28;
    public static final int LEFT_CURLY=38;
    public static final int RIGHT_SQUARE=37;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[40+1];
     }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T41
    public void mT41() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = T41;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ( '=>' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: '=>'
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

    // $ANTLR start WS
    public void mWS() throws RecognitionException {
        try {
            ruleNestingLevel++;
            int _type = WS;
            int _start = getCharIndex();
            int _line = getLine();
            int _charPosition = getCharPositionInLine();
            int _channel = Token.DEFAULT_CHANNEL;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("606:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:606:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:607:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:608:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:19: EOL
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:11: ( 'defrule' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:614:11: 'defrule'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:615:7: ( 'or' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:615:7: 'or'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:8: ( 'and' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:616:8: 'and'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:8: ( 'not' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:8: 'not'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:11: ( 'exists' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:11: 'exists'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:619:9: ( 'test' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:619:9: 'test'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:8: ( 'null' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:8: 'null'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:11: ( 'declare' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:623:11: 'declare'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:11: ( 'salience' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:625:11: 'salience'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("631:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:631:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:633:25: '\\n'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:4: ( ( '-' )? ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:4: ( '-' )? ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:638:11: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:10: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:11: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:26: ( '0' .. '9' )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:27: '0' .. '9'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:8: ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) )
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
                    new NoViableAltException("645:1: STRING : ( ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' ) | ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:8: ( '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:9: '\"' ( EscapeSequence | ~ ('\\\\'|'\"'))* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:13: ( EscapeSequence | ~ ('\\\\'|'\"'))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:646:32: ~ ('\\\\'|'\"')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:8: ( '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\'' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:9: '\\'' ( EscapeSequence | ~ ('\\\\'|'\\''))* '\\''
                    {
                    match('\''); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:14: ( EscapeSequence | ~ ('\\\\'|'\\''))*
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
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:647:33: ~ ('\\\\'|'\\'')
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:651:12: ( ('0'..'9'|'a'..'f'|'A'..'F'))
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:651:12: ('0'..'9'|'a'..'f'|'A'..'F')
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("653:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("653:1: fragment EscapeSequence : ( '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\') | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:9: '\\\\' ('b'|'t'|'n'|'f'|'r'|'\\\"'|'\\''|'\\\\')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:657:9: OctalEscape
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("660:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("660:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:14: ( '0' .. '3' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:36: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:25: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:14: ( '0' .. '7' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:664:15: '0' .. '7'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:669:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: ( ( 'true' | 'false' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: ( 'true' | 'false' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:4: ( 'true' | 'false' )
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
                    new NoViableAltException("673:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:12: 'false'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:8: ( '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( SYMBOL )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:8: '?' ('a'..'z'|'A'..'Z'|'_'|'$') ( SYMBOL )*
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:38: ( SYMBOL )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>='\u0000' && LA14_0<='\b')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\u001F')||LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||LA14_0=='\''||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='<' && LA14_0<='>')||(LA14_0>='@' && LA14_0<='{')||LA14_0=='}'||(LA14_0>='\u007F' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:676:38: SYMBOL
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:8: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:35: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:686:36: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: ( '(' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:4: '('
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:696:4: ( ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:696:4: ')'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:700:4: ( '[' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:700:4: '['
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: ( ']' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:4: ']'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: ( '{' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:708:4: '{'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: ( '}' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: '}'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:9: ( '~' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:715:9: '~'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: ( '&' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:719:4: '&'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: ( '|' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:723:4: '|'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:727:4: ( '<-' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:727:4: '<-'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:9: ( ':' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:9: ':'
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:732:10: ( '=' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:732:10: '='
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:9: ( options {greedy=false; } : . )*
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:735:35: .
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:8: ( SYMBOL )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:739:8: SYMBOL
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:4: ( ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) ) (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )
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
                    new NoViableAltException("743:4: ( (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')) | ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')) )", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:5: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$'))
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:6: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'?'|'$')
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:65: ( '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'))
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:66: '$' ~ ('?'|' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<')
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

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:11: (~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?'))*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\u001F')||LA19_0=='!'||(LA19_0>='#' && LA19_0<='%')||LA19_0=='\''||(LA19_0>='*' && LA19_0<=':')||(LA19_0>='=' && LA19_0<='>')||(LA19_0>='@' && LA19_0<='{')||LA19_0=='}'||(LA19_0>='\u007F' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:744:12: ~ (' '|'\\t'|'\\n'|'\\r'|'\"'|'('|')'|';'|'&'|'|'|'~'|'<'|'?')
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
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T41 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt20=32;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 2 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 3 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:17: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 4 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:25: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 5 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:28: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 6 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:32: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 7 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:36: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 8 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:43: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 9 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:48: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 10 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:53: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 11 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:61: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 12 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:70: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 13 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:74: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 14 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:80: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 15 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:87: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 16 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:92: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 17 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:96: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 18 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:125: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 19 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:153: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 20 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:164: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 21 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:176: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 22 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:188: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 23 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:201: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 24 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:212: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 25 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:224: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 26 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:230: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 27 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:240: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 28 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:245: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 29 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:255: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 30 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:261: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 31 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:268: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 32 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:287: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }


    protected DFA20 dfa20 = new DFA20(this);
    public static final String DFA20_eotS =
        "\1\uffff\1\40\1\uffff\1\2\10\36\1\54\1\uffff\2\36\1\uffff\2\36\2"+
        "\uffff\1\65\1\66\1\67\1\70\3\uffff\1\36\1\72\1\uffff\1\73\1\uffff"+
        "\1\36\1\76\7\36\1\54\1\36\1\uffff\2\36\1\15\1\36\1\uffff\3\36\4"+
        "\uffff\1\121\2\uffff\2\36\1\uffff\1\124\1\125\5\36\1\133\7\36\1"+
        "\uffff\1\36\2\uffff\2\36\2\uffff\1\143\1\36\1\145\1\146\1\36\1\uffff"+
        "\4\36\1\116\2\36\1\uffff\1\36\2\uffff\3\36\1\145\2\36\1\162\2\36"+
        "\1\165\1\166\1\uffff\2\36\2\uffff\1\170\1\uffff";
    public static final String DFA20_eofS =
        "\171\uffff";
    public static final String DFA20_minS =
        "\2\0\1\uffff\1\0\1\145\1\162\1\156\1\157\1\170\1\145\1\141\1\60"+
        "\1\0\1\uffff\1\0\1\141\1\uffff\1\0\1\52\2\uffff\4\0\3\uffff\1\55"+
        "\1\0\1\uffff\1\0\1\uffff\1\143\1\0\1\144\1\164\1\154\1\151\1\165"+
        "\1\163\1\154\1\0\1\60\1\uffff\1\42\2\0\1\154\1\uffff\3\0\4\uffff"+
        "\1\0\2\uffff\1\154\1\162\1\uffff\2\0\1\154\1\163\1\145\1\164\1\151"+
        "\2\0\1\60\2\0\1\163\2\0\1\uffff\1\0\2\uffff\1\141\1\165\2\uffff"+
        "\1\0\1\164\2\0\1\145\1\uffff\1\60\2\0\1\145\1\0\1\162\1\154\1\uffff"+
        "\1\163\2\uffff\1\156\1\60\2\0\2\145\1\0\1\143\1\60\2\0\1\uffff\1"+
        "\145\1\0\2\uffff\1\0\1\uffff";
    public static final String DFA20_maxS =
        "\2\ufffe\1\uffff\1\ufffe\1\145\1\162\1\156\1\165\1\170\1\162\1\141"+
        "\1\71\1\ufffe\1\uffff\1\ufffe\1\141\1\uffff\1\ufffe\1\57\2\uffff"+
        "\4\ufffe\3\uffff\1\55\1\ufffe\1\uffff\1\ufffe\1\uffff\1\146\1\ufffe"+
        "\1\144\1\164\1\154\1\151\1\165\1\163\1\154\1\ufffe\1\71\1\uffff"+
        "\1\165\2\ufffe\1\154\1\uffff\3\ufffe\4\uffff\1\ufffe\2\uffff\1\154"+
        "\1\162\1\uffff\2\ufffe\1\154\1\163\1\145\1\164\1\151\2\ufffe\1\146"+
        "\2\ufffe\1\163\2\ufffe\1\uffff\1\ufffe\2\uffff\1\141\1\165\2\uffff"+
        "\1\ufffe\1\164\2\ufffe\1\145\1\uffff\1\146\2\ufffe\1\145\1\ufffe"+
        "\1\162\1\154\1\uffff\1\163\2\uffff\1\156\1\146\2\ufffe\2\145\1\ufffe"+
        "\1\143\1\146\2\ufffe\1\uffff\1\145\1\ufffe\2\uffff\1\ufffe\1\uffff";
    public static final String DFA20_acceptS =
        "\2\uffff\1\2\12\uffff\1\16\2\uffff\1\20\2\uffff\1\23\1\24\4\uffff"+
        "\1\31\1\32\1\33\2\uffff\1\40\1\uffff\1\36\13\uffff\1\14\4\uffff"+
        "\1\21\3\uffff\1\25\1\26\1\27\1\30\1\uffff\1\35\1\1\2\uffff\1\4\17"+
        "\uffff\1\37\1\uffff\1\22\1\34\2\uffff\1\5\1\6\5\uffff\1\15\7\uffff"+
        "\1\11\1\uffff\1\17\1\10\13\uffff\1\7\2\uffff\1\12\1\3\1\uffff\1"+
        "\13";
    public static final String DFA20_specialS =
        "\171\uffff}>";
    public static final String[] DFA20_transition = {
        "\11\36\2\2\1\36\1\3\1\2\22\36\1\2\1\36\1\15\1\21\2\36\1\32\1\16"+
        "\1\23\1\24\3\36\1\13\1\36\1\22\12\14\1\35\1\uffff\1\34\1\1\1\36"+
        "\1\20\33\36\1\25\1\36\1\26\3\36\1\6\2\36\1\4\1\10\1\17\7\36\1\7"+
        "\1\5\3\36\1\12\1\11\6\36\1\27\1\33\1\30\1\31\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\1\36\1\37\1\uffff\74\36\1\uffff\1\36"+
        "\1\uffff\uff80\36",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\41",
        "\1\42",
        "\1\43",
        "\1\44\5\uffff\1\45",
        "\1\46",
        "\1\50\14\uffff\1\47",
        "\1\51",
        "\12\52",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\4\36\1\53\1\36\12\52\1\36\2\uffff\2\36\1\uffff\74"+
        "\36\1\uffff\1\36\1\uffff\uff80\36",
        "",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "\1\60",
        "",
        "\11\62\2\61\2\62\1\61\22\62\1\61\1\62\1\61\3\62\1\61\1\62\2\61\21"+
        "\62\2\61\2\62\1\61\74\62\1\61\1\62\1\61\uff80\62",
        "\1\63\4\uffff\1\64",
        "",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "",
        "",
        "",
        "\1\71",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "",
        "\1\74\2\uffff\1\75",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\77",
        "\1\100",
        "\1\101",
        "\1\102",
        "\1\103",
        "\1\104",
        "\1\105",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\4\36\1\53\1\36\12\52\1\36\2\uffff\2\36\1\uffff\74"+
        "\36\1\uffff\1\36\1\uffff\uff80\36",
        "\12\106",
        "",
        "\1\15\4\uffff\1\107\10\uffff\4\111\4\112\44\uffff\1\107\5\uffff"+
        "\1\107\3\uffff\1\107\7\uffff\1\107\3\uffff\1\107\1\uffff\1\107\1"+
        "\110",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\113",
        "",
        "\11\62\2\61\2\62\1\61\22\62\1\61\1\62\1\61\3\62\1\61\1\62\2\61\21"+
        "\62\2\61\2\62\1\61\74\62\1\61\1\62\1\61\uff80\62",
        "\11\115\2\116\2\115\1\116\22\115\1\116\1\115\1\116\3\115\1\116\1"+
        "\115\2\116\1\114\20\115\2\116\2\115\1\116\74\115\1\116\1\115\1\116"+
        "\uff80\115",
        "\11\117\2\120\2\117\1\120\22\117\1\120\1\117\1\120\3\117\1\120\1"+
        "\117\2\120\21\117\2\120\2\117\1\120\74\117\1\120\1\117\1\120\uff80"+
        "\117",
        "",
        "",
        "",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "",
        "",
        "\1\122",
        "\1\123",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\126",
        "\1\127",
        "\1\130",
        "\1\131",
        "\1\132",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\6\36\12\106\1\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
        "\1\36\1\uffff\uff80\36",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "\12\134\7\uffff\6\134\32\uffff\6\134",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\6"+
        "\56\10\135\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
        "\56",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\6"+
        "\56\10\136\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
        "\56",
        "\1\137",
        "\11\115\2\116\2\115\1\116\22\115\1\116\1\115\1\116\3\115\1\116\1"+
        "\115\2\116\1\114\4\115\1\140\13\115\2\116\2\115\1\116\74\115\1\116"+
        "\1\115\1\116\uff80\115",
        "\11\115\2\116\2\115\1\116\22\115\1\116\1\115\1\116\3\115\1\116\1"+
        "\115\2\116\1\114\20\115\2\116\2\115\1\116\74\115\1\116\1\115\1\116"+
        "\uff80\115",
        "",
        "\11\117\2\120\2\117\1\120\22\117\1\120\1\117\1\120\3\117\1\120\1"+
        "\117\2\120\21\117\2\120\2\117\1\120\74\117\1\120\1\117\1\120\uff80"+
        "\117",
        "",
        "",
        "\1\141",
        "\1\142",
        "",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\144",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\147",
        "",
        "\12\150\7\uffff\6\150\32\uffff\6\150",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\6"+
        "\56\10\151\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
        "\56",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "\1\152",
        "\11\115\2\uffff\2\115\1\uffff\22\115\1\uffff\1\115\1\uffff\3\115"+
        "\1\uffff\1\115\2\uffff\1\114\20\115\2\uffff\2\115\1\uffff\74\115"+
        "\1\uffff\1\115\1\uffff\uff80\115",
        "\1\153",
        "\1\154",
        "",
        "\1\155",
        "",
        "",
        "\1\156",
        "\12\157\7\uffff\6\157\32\uffff\6\157",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\160",
        "\1\161",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\1\163",
        "\12\164\7\uffff\6\164\32\uffff\6\164",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
        "",
        "\1\167",
        "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2\15\21"+
        "\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80\56",
        "",
        "",
        "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36\1\uffff"+
        "\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff\1\36\1\uffff"+
        "\uff80\36",
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
            return "1:1: Tokens : ( T41 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}