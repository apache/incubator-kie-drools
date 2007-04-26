// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-04-26 01:37:19

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int RIGHT_SQUARE=37;
    public static final int RIGHT_CURLY=39;
    public static final int SYMBOL=33;
    public static final int NULL=25;
    public static final int BOOL=24;
    public static final int SALIENCE=9;
    public static final int AMPERSAND=18;
    public static final int FLOAT=23;
    public static final int EQUALS=22;
    public static final int INT=10;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int NOT=13;
    public static final int AND=11;
    public static final int Tokens=42;
    public static final int EOF=-1;
    public static final int T41=41;
    public static final int HexDigit=30;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int ASSIGN_OP=17;
    public static final int COLON=21;
    public static final int RIGHT_PAREN=8;
    public static final int NAME=6;
    public static final int EOL=26;
    public static final int WS=27;
    public static final int UnicodeEscape=31;
    public static final int DEFRULE=5;
    public static final int LEFT_CURLY=38;
    public static final int OR=12;
    public static final int TILDE=20;
    public static final int TEST=15;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=28;
    public static final int PIPE=19;
    public static final int VAR=16;
    public static final int EXISTS=14;
    public static final int LEFT_SQUARE=36;
    public static final int EscapeSequence=29;
    public static final int OctalEscape=32;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int STRING=7;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[40+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T41
    public final void mT41() throws RecognitionException {
        try {
            int _type = T41;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: ( '=>' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: '=>'
            {
            match("=>"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T41

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:17: ( ' ' | '\\t' | '\\f' | EOL )
            int alt1=4;
            switch ( input.LA(1) ) {
            case ' ':
                {
                alt1=1;
                }
                break;
            case '\t':
                {
                alt1=2;
                }
                break;
            case '\f':
                {
                alt1=3;
                }
                break;
            case '\n':
            case '\r':
                {
                alt1=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("554:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:19: EOL
                    {
                    mEOL(); if (failed) return ;

                    }
                    break;

            }

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

    // $ANTLR start DEFRULE
    public final void mDEFRULE() throws RecognitionException {
        try {
            int _type = DEFRULE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:562:11: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:562:11: 'defrule'
            {
            match("defrule"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DEFRULE

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:7: 'or'
            {
            match("or"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:8: ( 'and' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:8: 'and'
            {
            match("and"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:565:8: ( 'not' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:565:8: 'not'
            {
            match("not"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start EXISTS
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:566:11: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:566:11: 'exists'
            {
            match("exists"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXISTS

    // $ANTLR start TEST
    public final void mTEST() throws RecognitionException {
        try {
            int _type = TEST;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:567:9: ( 'test' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:567:9: 'test'
            {
            match("test"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TEST

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:569:8: ( 'null' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:569:8: 'null'
            {
            match("null"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start DECLARE
    public final void mDECLARE() throws RecognitionException {
        try {
            int _type = DECLARE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:11: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:11: 'declare'
            {
            match("declare"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DECLARE

    // $ANTLR start SALIENCE
    public final void mSALIENCE() throws RecognitionException {
        try {
            int _type = SALIENCE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:11: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:11: 'salience'
            {
            match("salience"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SALIENCE

    // $ANTLR start EOL
    public final void mEOL() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') && (synpred1())) {
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
                    new NoViableAltException("579:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:581:25: '\\n'
                    {
                    match('\n'); if (failed) return ;

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end EOL

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:4: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:11: '0' .. '9'
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

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start FLOAT
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:27: '0' .. '9'
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

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOAT

    // $ANTLR start STRING
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:8: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("593:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:32: ~ ( '\\\\' | '\"' )
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
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:33: ~ ( '\\\\' | '\\'' )
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
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING

    // $ANTLR start HexDigit
    public final void mHexDigit() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:12: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
        }
    }
    // $ANTLR end HexDigit

    // $ANTLR start EscapeSequence
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case 'u':
                    {
                    alt11=2;
                    }
                    break;
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt11=1;
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
                    {
                    alt11=3;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("601:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("601:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:604:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:605:9: OctalEscape
                    {
                    mOctalEscape(); if (failed) return ;

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end EscapeSequence

    // $ANTLR start OctalEscape
    public final void mOctalEscape() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("608:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("608:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:610:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:611:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:612:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end OctalEscape

    // $ANTLR start UnicodeEscape
    public final void mUnicodeEscape() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
        }
    }
    // $ANTLR end UnicodeEscape

    // $ANTLR start BOOL
    public final void mBOOL() throws RecognitionException {
        try {
            int _type = BOOL;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:4: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:4: ( 'true' | 'false' )
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
                    new NoViableAltException("621:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:621:12: 'false'
                    {
                    match("false"); if (failed) return ;


                    }
                    break;

            }


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOOL

    // $ANTLR start VAR
    public final void mVAR() throws RecognitionException {
        try {
            int _type = VAR;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:8: ( '?' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( SYMBOL )* )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:8: '?' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( SYMBOL )*
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

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:38: ( SYMBOL )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='\u0000' && LA14_0<='\b')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\u001F')||LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||LA14_0=='\''||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='<' && LA14_0<='>')||(LA14_0>='@' && LA14_0<='{')||LA14_0=='}'||(LA14_0>='\u007F' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:624:38: SYMBOL
            	    {
            	    mSYMBOL(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VAR

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:628:35: .
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
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SH_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start C_STYLE_SINGLE_LINE_COMMENT
    public final void mC_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = C_STYLE_SINGLE_LINE_COMMENT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:634:36: .
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
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end C_STYLE_SINGLE_LINE_COMMENT

    // $ANTLR start LEFT_PAREN
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:4: ( '(' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:640:4: '('
            {
            match('('); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_PAREN

    // $ANTLR start RIGHT_PAREN
    public final void mRIGHT_PAREN() throws RecognitionException {
        try {
            int _type = RIGHT_PAREN;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:4: ( ')' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:4: ')'
            {
            match(')'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_PAREN

    // $ANTLR start LEFT_SQUARE
    public final void mLEFT_SQUARE() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:4: ( '[' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:648:4: '['
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
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:652:4: ( ']' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:652:4: ']'
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
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:4: ( '{' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:4: '{'
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
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: ( '}' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:4: '}'
            {
            match('}'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start TILDE
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: ( '~' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:663:9: '~'
            {
            match('~'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TILDE

    // $ANTLR start AMPERSAND
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:667:4: ( '&' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:667:4: '&'
            {
            match('&'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AMPERSAND

    // $ANTLR start PIPE
    public final void mPIPE() throws RecognitionException {
        try {
            int _type = PIPE;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:4: ( '|' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:4: '|'
            {
            match('|'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PIPE

    // $ANTLR start ASSIGN_OP
    public final void mASSIGN_OP() throws RecognitionException {
        try {
            int _type = ASSIGN_OP;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:675:4: ( '<-' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:675:4: '<-'
            {
            match("<-"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASSIGN_OP

    // $ANTLR start COLON
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:678:9: ( ':' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:678:9: ':'
            {
            match(':'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLON

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:10: ( '=' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:10: '='
            {
            match('='); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start MULTI_LINE_COMMENT
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:683:35: .
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
               channel=HIDDEN; 
            }

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MULTI_LINE_COMMENT

    // $ANTLR start NAME
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:8: ( SYMBOL )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:8: SYMBOL
            {
            mSYMBOL(); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NAME

    // $ANTLR start SYMBOL
    public final void mSYMBOL() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:4: ( ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) ) (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )* )
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) ) (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )*
            {
            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) )
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
                    new NoViableAltException("691:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) )", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:5: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) )
                    {
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:5: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:6: ~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' )
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
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:65: ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) )
                    {
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:65: ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) )
                    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:66: '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' )
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

            // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:11: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\u001F')||LA19_0=='!'||(LA19_0>='#' && LA19_0<='%')||LA19_0=='\''||(LA19_0>='*' && LA19_0<=':')||(LA19_0>='=' && LA19_0<='>')||(LA19_0>='@' && LA19_0<='{')||LA19_0=='}'||(LA19_0>='\u007F' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:12: ~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' )
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
        }
    }
    // $ANTLR end SYMBOL

    public void mTokens() throws RecognitionException {
        // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: ( T41 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt20=32;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T41
                {
                mT41(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:17: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:25: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:28: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:32: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:36: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:43: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:48: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:53: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:61: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:70: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:74: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:80: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:87: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:92: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:96: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:125: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:153: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:164: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:176: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:188: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:201: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:212: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:224: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:230: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:240: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:245: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:255: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:261: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:268: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:287: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:14: ( '\\r\\n' )
        // C:\\dev\\jbossrules\\trunk4\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:16: '\\r\\n'
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


    protected DFA20 dfa20 = new DFA20(this);
    static final String DFA20_eotS =
        "\1\uffff\1\40\1\uffff\1\2\10\36\1\54\1\uffff\2\36\1\uffff\2\36\2"+
        "\uffff\1\65\1\66\1\67\1\70\3\uffff\1\36\1\72\1\uffff\1\73\1\uffff"+
        "\1\36\1\76\7\36\1\54\1\36\1\uffff\2\36\1\15\2\36\1\uffff\2\36\4"+
        "\uffff\1\121\2\uffff\2\36\1\uffff\1\124\1\125\5\36\1\133\6\36\1"+
        "\uffff\1\36\1\uffff\1\36\1\uffff\2\36\2\uffff\1\143\1\36\1\145\1"+
        "\146\1\36\1\uffff\4\36\1\117\2\36\1\uffff\1\36\2\uffff\3\36\1\146"+
        "\2\36\1\162\2\36\1\165\1\166\1\uffff\2\36\2\uffff\1\170\1\uffff";
    static final String DFA20_eofS =
        "\171\uffff";
    static final String DFA20_minS =
        "\2\0\1\uffff\1\0\1\145\1\162\1\156\1\157\1\170\1\145\1\141\1\60"+
        "\1\0\1\uffff\1\0\1\141\1\uffff\1\0\1\52\2\uffff\4\0\3\uffff\1\55"+
        "\1\0\1\uffff\1\0\1\uffff\1\143\1\0\1\144\1\164\1\154\1\151\1\163"+
        "\1\165\1\154\1\0\1\60\1\uffff\1\42\2\0\1\154\1\0\1\uffff\2\0\4\uffff"+
        "\1\0\2\uffff\1\154\1\162\1\uffff\2\0\1\154\1\163\1\164\1\145\1\151"+
        "\2\0\1\60\2\0\1\163\1\0\1\uffff\1\0\1\uffff\1\0\1\uffff\1\141\1"+
        "\165\2\uffff\1\0\1\164\2\0\1\145\1\uffff\1\60\2\0\1\145\1\0\1\162"+
        "\1\154\1\uffff\1\163\2\uffff\1\156\1\60\2\0\2\145\1\0\1\143\1\60"+
        "\2\0\1\uffff\1\145\1\0\2\uffff\1\0\1\uffff";
    static final String DFA20_maxS =
        "\2\ufffe\1\uffff\1\ufffe\1\145\1\162\1\156\1\165\1\170\1\162\1\141"+
        "\1\71\1\ufffe\1\uffff\1\ufffe\1\141\1\uffff\1\ufffe\1\57\2\uffff"+
        "\4\ufffe\3\uffff\1\55\1\ufffe\1\uffff\1\ufffe\1\uffff\1\146\1\ufffe"+
        "\1\144\1\164\1\154\1\151\1\163\1\165\1\154\1\ufffe\1\71\1\uffff"+
        "\1\165\2\ufffe\1\154\1\ufffe\1\uffff\2\ufffe\4\uffff\1\ufffe\2\uffff"+
        "\1\154\1\162\1\uffff\2\ufffe\1\154\1\163\1\164\1\145\1\151\2\ufffe"+
        "\1\146\2\ufffe\1\163\1\ufffe\1\uffff\1\ufffe\1\uffff\1\ufffe\1\uffff"+
        "\1\141\1\165\2\uffff\1\ufffe\1\164\2\ufffe\1\145\1\uffff\1\146\2"+
        "\ufffe\1\145\1\ufffe\1\162\1\154\1\uffff\1\163\2\uffff\1\156\1\146"+
        "\2\ufffe\2\145\1\ufffe\1\143\1\146\2\ufffe\1\uffff\1\145\1\ufffe"+
        "\2\uffff\1\ufffe\1\uffff";
    static final String DFA20_acceptS =
        "\2\uffff\1\2\12\uffff\1\16\2\uffff\1\20\2\uffff\1\23\1\24\4\uffff"+
        "\1\31\1\32\1\33\2\uffff\1\40\1\uffff\1\36\13\uffff\1\14\5\uffff"+
        "\1\21\2\uffff\1\25\1\26\1\27\1\30\1\uffff\1\35\1\1\2\uffff\1\4\16"+
        "\uffff\1\22\1\uffff\1\37\1\uffff\1\34\2\uffff\1\5\1\6\5\uffff\1"+
        "\15\7\uffff\1\11\1\uffff\1\10\1\17\13\uffff\1\7\2\uffff\1\12\1\3"+
        "\1\uffff\1\13";
    static final String DFA20_specialS =
        "\171\uffff}>";
    static final String[] DFA20_transitionS = {
            "\11\36\2\2\1\36\1\3\1\2\22\36\1\2\1\36\1\15\1\21\2\36\1\32\1"+
            "\16\1\23\1\24\3\36\1\13\1\36\1\22\12\14\1\35\1\uffff\1\34\1"+
            "\1\1\36\1\20\33\36\1\25\1\36\1\26\3\36\1\6\2\36\1\4\1\10\1\17"+
            "\7\36\1\7\1\5\3\36\1\12\1\11\6\36\1\27\1\33\1\30\1\31\uff80"+
            "\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\1\36\1\37\1\uffff\74\36"+
            "\1\uffff\1\36\1\uffff\uff80\36",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44\5\uffff\1\45",
            "\1\46",
            "\1\47\14\uffff\1\50",
            "\1\51",
            "\12\52",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\4\36\1\53\1\36\12\52\1\36\2\uffff\2\36"+
            "\1\uffff\74\36\1\uffff\1\36\1\uffff\uff80\36",
            "",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "\1\60",
            "",
            "\11\61\2\62\2\61\1\62\22\61\1\62\1\61\1\62\3\61\1\62\1\61\2"+
            "\62\21\61\2\62\2\61\1\62\74\61\1\62\1\61\1\62\uff80\61",
            "\1\64\4\uffff\1\63",
            "",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "",
            "",
            "",
            "\1\71",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "",
            "\1\74\2\uffff\1\75",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\4\36\1\53\1\36\12\52\1\36\2\uffff\2\36"+
            "\1\uffff\74\36\1\uffff\1\36\1\uffff\uff80\36",
            "\12\106",
            "",
            "\1\15\4\uffff\1\107\10\uffff\4\111\4\112\44\uffff\1\107\5\uffff"+
            "\1\107\3\uffff\1\107\7\uffff\1\107\3\uffff\1\107\1\uffff\1\107"+
            "\1\110",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\113",
            "\11\61\2\62\2\61\1\62\22\61\1\62\1\61\1\62\3\61\1\62\1\61\2"+
            "\62\21\61\2\62\2\61\1\62\74\61\1\62\1\61\1\62\uff80\61",
            "",
            "\11\114\2\115\2\114\1\115\22\114\1\115\1\114\1\115\3\114\1\115"+
            "\1\114\2\115\21\114\2\115\2\114\1\115\74\114\1\115\1\114\1\115"+
            "\uff80\114",
            "\11\120\2\117\2\120\1\117\22\120\1\117\1\120\1\117\3\120\1\117"+
            "\1\120\2\117\1\116\20\120\2\117\2\120\1\117\74\120\1\117\1\120"+
            "\1\117\uff80\120",
            "",
            "",
            "",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "",
            "",
            "\1\122",
            "\1\123",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\6\36\12\106\1\36\2\uffff\2\36\1\uffff"+
            "\74\36\1\uffff\1\36\1\uffff\uff80\36",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "\12\134\7\uffff\6\134\32\uffff\6\134",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\6\56\10\135\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1"+
            "\56\1\15\uff80\56",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\6\56\10\136\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1"+
            "\56\1\15\uff80\56",
            "\1\137",
            "\11\114\2\115\2\114\1\115\22\114\1\115\1\114\1\115\3\114\1\115"+
            "\1\114\2\115\21\114\2\115\2\114\1\115\74\114\1\115\1\114\1\115"+
            "\uff80\114",
            "",
            "\11\120\2\117\2\120\1\117\22\120\1\117\1\120\1\117\3\120\1\117"+
            "\1\120\2\117\1\116\4\120\1\140\13\120\2\117\2\120\1\117\74\120"+
            "\1\117\1\120\1\117\uff80\120",
            "",
            "\11\120\2\117\2\120\1\117\22\120\1\117\1\120\1\117\3\120\1\117"+
            "\1\120\2\117\1\116\20\120\2\117\2\120\1\117\74\120\1\117\1\120"+
            "\1\117\uff80\120",
            "",
            "\1\141",
            "\1\142",
            "",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\144",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\147",
            "",
            "\12\150\7\uffff\6\150\32\uffff\6\150",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\6\56\10\151\3\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1"+
            "\56\1\15\uff80\56",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "\1\152",
            "\11\120\2\uffff\2\120\1\uffff\22\120\1\uffff\1\120\1\uffff\3"+
            "\120\1\uffff\1\120\2\uffff\1\116\20\120\2\uffff\2\120\1\uffff"+
            "\74\120\1\uffff\1\120\1\uffff\uff80\120",
            "\1\153",
            "\1\154",
            "",
            "\1\155",
            "",
            "",
            "\1\156",
            "\12\157\7\uffff\6\157\32\uffff\6\157",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\160",
            "\1\161",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\1\163",
            "\12\164\7\uffff\6\164\32\uffff\6\164",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            "",
            "\1\167",
            "\11\56\2\15\2\56\1\15\22\56\1\15\1\56\1\15\3\56\1\15\1\57\2"+
            "\15\21\56\2\15\2\56\1\15\34\56\1\55\37\56\1\15\1\56\1\15\uff80"+
            "\56",
            "",
            "",
            "\11\36\2\uffff\2\36\1\uffff\22\36\1\uffff\1\36\1\uffff\3\36"+
            "\1\uffff\1\36\2\uffff\21\36\2\uffff\2\36\1\uffff\74\36\1\uffff"+
            "\1\36\1\uffff\uff80\36",
            ""
    };

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T41 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}