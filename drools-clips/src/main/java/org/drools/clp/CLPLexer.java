// $ANTLR 3.0 C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-08-20 01:44:15

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPLexer extends Lexer {
    public static final int RIGHT_SQUARE=38;
    public static final int RIGHT_CURLY=40;
    public static final int EQUALS=23;
    public static final int FLOAT=24;
    public static final int NOT=15;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int AND=13;
    public static final int EOF=-1;
    public static final int HexDigit=31;
    public static final int DEFFUNCTION=7;
    public static final int ASSIGN_OP=18;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
    public static final int EOL=27;
    public static final int DEFRULE=9;
    public static final int TILDE=21;
    public static final int PIPE=19;
    public static final int VAR=8;
    public static final int EXISTS=16;
    public static final int SYMBOL=34;
    public static final int NULL=26;
    public static final int BOOL=25;
    public static final int SALIENCE=11;
    public static final int AMPERSAND=20;
    public static final int INT=12;
    public static final int T43=43;
    public static final int Tokens=44;
    public static final int T42=42;
    public static final int MULTI_LINE_COMMENT=41;
    public static final int COLON=22;
    public static final int WS=28;
    public static final int UnicodeEscape=32;
    public static final int LEFT_CURLY=39;
    public static final int OR=14;
    public static final int TEST=17;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=29;
    public static final int LEFT_SQUARE=37;
    public static final int OctalEscape=33;
    public static final int EscapeSequence=30;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=36;
    public static final int STRING=10;
    public CLPLexer() {;} 
    public CLPLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[42+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    // $ANTLR start T42
    public final void mT42() throws RecognitionException {
        try {
            int _type = T42;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:5: ( 'import' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:6:7: 'import'
            {
            match("import"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T42

    // $ANTLR start T43
    public final void mT43() throws RecognitionException {
        try {
            int _type = T43;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:5: ( '=>' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:7:7: '=>'
            {
            match("=>"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T43

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:679:9: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:679:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:679:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("679:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:679:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:680:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:681:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:682:19: EOL
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:10: ( 'defrule' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:687:12: 'defrule'
            {
            match("defrule"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DEFRULE

    // $ANTLR start DEFFUNCTION
    public final void mDEFFUNCTION() throws RecognitionException {
        try {
            int _type = DEFFUNCTION;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:13: ( 'deffunction' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:688:15: 'deffunction'
            {
            match("deffunction"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DEFFUNCTION

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:7: ( 'or' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:689:9: 'or'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:690:7: ( 'and' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:690:9: 'and'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:7: ( 'not' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:691:9: 'not'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:10: ( 'exists' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:692:12: 'exists'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:8: ( 'test' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:693:10: 'test'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:695:7: ( 'null' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:695:9: 'null'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:10: ( 'declare' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:697:12: 'declare'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:699:10: ( 'salience' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:699:12: 'salience'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:704:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            int alt2=3;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
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
                    new NoViableAltException("705:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:706:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:707:25: '\\n'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:2: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:712:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:11: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:716:27: '0' .. '9'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("719:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:720:32: ~ ( '\\\\' | '\"' )
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
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:721:33: ~ ( '\\\\' | '\\'' )
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:725:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:725:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("727:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("727:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:729:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:730:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:731:9: OctalEscape
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("734:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("734:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:14: ( '0' .. '3' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:36: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:736:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:737:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:737:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:737:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:737:25: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:737:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:14: ( '0' .. '7' )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:738:15: '0' .. '7'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:743:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:747:2: ( ( 'true' | 'false' ) )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:747:4: ( 'true' | 'false' )
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:747:4: ( 'true' | 'false' )
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
                    new NoViableAltException("747:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:747:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:747:12: 'false'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:6: ( '?' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( SYMBOL )* )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:8: '?' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' ) ( SYMBOL )*
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

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:38: ( SYMBOL )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='\u0000' && LA14_0<='\b')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\u001F')||LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||LA14_0=='\''||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='<' && LA14_0<='>')||(LA14_0>='@' && LA14_0<='{')||LA14_0=='}'||(LA14_0>='\u007F' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:750:38: SYMBOL
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:754:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:754:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:754:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:754:35: .
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:760:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:760:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:760:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:760:36: .
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:766:2: ( '(' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:766:4: '('
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:770:2: ( ')' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:770:4: ')'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:774:2: ( '[' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:774:4: '['
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:778:2: ( ']' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:778:4: ']'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:782:2: ( '{' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:782:4: '{'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:786:2: ( '}' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:786:4: '}'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:789:7: ( '~' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:789:9: '~'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:793:2: ( '&' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:793:4: '&'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:797:2: ( '|' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:797:4: '|'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:801:2: ( '<-' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:801:4: '<-'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:804:7: ( ':' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:804:9: ':'
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:806:8: ( '=' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:806:10: '='
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:809:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:809:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:809:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:809:35: .
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:813:6: ( SYMBOL )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:813:8: SYMBOL
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
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:2: ( ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) ) (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )* )
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) ) (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )*
            {
            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) )
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
                    new NoViableAltException("817:4: ( (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) ) | ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) ) )", 18, 0, input);

                throw nvae;
            }
            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:5: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) )
                    {
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:5: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' ) )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:6: ~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' | '$' )
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
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:65: ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) )
                    {
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:65: ( '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' ) )
                    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:817:66: '$' ~ ( '?' | ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' )
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

            // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:818:11: (~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='\u0000' && LA19_0<='\b')||(LA19_0>='\u000B' && LA19_0<='\f')||(LA19_0>='\u000E' && LA19_0<='\u001F')||LA19_0=='!'||(LA19_0>='#' && LA19_0<='%')||LA19_0=='\''||(LA19_0>='*' && LA19_0<=':')||(LA19_0>='=' && LA19_0<='>')||(LA19_0>='@' && LA19_0<='{')||LA19_0=='}'||(LA19_0>='\u007F' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:818:12: ~ ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' | '?' )
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
        // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:8: ( T42 | T43 | WS | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt20=34;
        alt20 = dfa20.predict(input);
        switch (alt20) {
            case 1 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:10: T42
                {
                mT42(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:14: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:18: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:21: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:29: DEFFUNCTION
                {
                mDEFFUNCTION(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:41: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:44: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:48: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:52: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:59: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:64: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:69: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:77: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:86: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:90: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:96: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:103: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:108: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:112: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:141: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:169: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:180: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:192: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:204: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:217: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:228: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:240: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:246: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:256: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:261: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:271: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:277: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:284: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 34 :
                // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:1:303: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:14: ( '\\r\\n' )
        // C:\\dev\\jbossrules\\trunk07\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:705:16: '\\r\\n'
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
        "\1\uffff\1\37\1\42\1\uffff\1\3\10\37\1\55\1\uffff\2\37\1\uffff\2"+
        "\37\2\uffff\1\67\1\70\1\71\1\72\3\uffff\1\37\1\74\1\uffff\1\37\1"+
        "\76\1\uffff\1\37\1\101\7\37\1\55\1\uffff\3\37\1\16\1\37\1\uffff"+
        "\3\37\4\uffff\1\124\1\uffff\1\37\1\uffff\2\37\1\uffff\1\131\1\132"+
        "\5\37\1\140\5\37\1\uffff\2\37\1\uffff\1\37\1\uffff\4\37\2\uffff"+
        "\1\152\1\37\1\154\1\155\1\37\1\uffff\4\37\1\122\4\37\1\uffff\1\37"+
        "\2\uffff\3\37\1\154\1\171\3\37\1\175\2\37\1\uffff\1\37\1\u0081\1"+
        "\u0082\1\uffff\3\37\2\uffff\1\u0085\1\37\1\uffff\1\37\1\u0088\1"+
        "\uffff";
    static final String DFA20_eofS =
        "\u0089\uffff";
    static final String DFA20_minS =
        "\1\0\1\155\1\0\1\uffff\1\0\1\145\1\162\1\156\1\157\1\170\1\145\1"+
        "\141\1\60\1\0\1\uffff\1\0\1\141\1\uffff\1\0\1\52\2\uffff\4\0\3\uffff"+
        "\1\55\1\0\1\uffff\1\160\1\0\1\uffff\1\143\1\0\1\144\1\164\1\154"+
        "\1\151\1\165\1\163\1\154\1\0\1\uffff\1\60\1\42\2\0\1\154\1\uffff"+
        "\3\0\4\uffff\1\0\1\uffff\1\157\1\uffff\1\146\1\154\1\uffff\2\0\1"+
        "\154\1\163\1\145\1\164\1\151\2\0\1\60\2\0\1\163\1\uffff\2\0\1\uffff"+
        "\1\0\1\uffff\1\162\2\165\1\141\2\uffff\1\0\1\164\2\0\1\145\1\uffff"+
        "\1\60\2\0\1\145\1\0\1\164\1\156\1\154\1\162\1\uffff\1\163\2\uffff"+
        "\1\156\1\60\3\0\1\143\2\145\1\0\1\143\1\60\1\uffff\1\164\2\0\1\uffff"+
        "\1\145\1\0\1\151\2\uffff\1\0\1\157\1\uffff\1\156\1\0\1\uffff";
    static final String DFA20_maxS =
        "\1\ufffe\1\155\1\ufffe\1\uffff\1\ufffe\1\145\1\162\1\156\1\165\1"+
        "\170\1\162\1\141\1\71\1\ufffe\1\uffff\1\ufffe\1\141\1\uffff\1\ufffe"+
        "\1\57\2\uffff\4\ufffe\3\uffff\1\55\1\ufffe\1\uffff\1\160\1\ufffe"+
        "\1\uffff\1\146\1\ufffe\1\144\1\164\1\154\1\151\1\165\1\163\1\154"+
        "\1\ufffe\1\uffff\1\71\1\165\2\ufffe\1\154\1\uffff\3\ufffe\4\uffff"+
        "\1\ufffe\1\uffff\1\157\1\uffff\1\162\1\154\1\uffff\2\ufffe\1\154"+
        "\1\163\1\145\1\164\1\151\2\ufffe\1\146\2\ufffe\1\163\1\uffff\2\ufffe"+
        "\1\uffff\1\ufffe\1\uffff\1\162\2\165\1\141\2\uffff\1\ufffe\1\164"+
        "\2\ufffe\1\145\1\uffff\1\146\2\ufffe\1\145\1\ufffe\1\164\1\156\1"+
        "\154\1\162\1\uffff\1\163\2\uffff\1\156\1\146\3\ufffe\1\143\2\145"+
        "\1\ufffe\1\143\1\146\1\uffff\1\164\2\ufffe\1\uffff\1\145\1\ufffe"+
        "\1\151\2\uffff\1\ufffe\1\157\1\uffff\1\156\1\ufffe\1\uffff";
    static final String DFA20_acceptS =
        "\3\uffff\1\3\12\uffff\1\20\2\uffff\1\22\2\uffff\1\25\1\26\4\uffff"+
        "\1\33\1\34\1\35\2\uffff\1\42\2\uffff\1\40\12\uffff\1\16\5\uffff"+
        "\1\23\3\uffff\1\27\1\30\1\31\1\32\1\uffff\1\37\1\uffff\1\2\2\uffff"+
        "\1\6\15\uffff\1\24\2\uffff\1\41\1\uffff\1\36\4\uffff\1\7\1\10\5"+
        "\uffff\1\17\11\uffff\1\13\1\uffff\1\21\1\12\13\uffff\1\1\3\uffff"+
        "\1\11\3\uffff\1\4\1\14\2\uffff\1\15\2\uffff\1\5";
    static final String DFA20_specialS =
        "\u0089\uffff}>";
    static final String[] DFA20_transitionS = {
            "\11\37\2\3\1\37\1\4\1\3\22\37\1\3\1\37\1\16\1\22\2\37\1\33\1"+
            "\17\1\24\1\25\3\37\1\14\1\37\1\23\12\15\1\36\1\uffff\1\35\1"+
            "\2\1\37\1\21\33\37\1\26\1\37\1\27\3\37\1\7\2\37\1\5\1\11\1\20"+
            "\2\37\1\1\4\37\1\10\1\6\3\37\1\13\1\12\6\37\1\30\1\34\1\31\1"+
            "\32\uff80\37",
            "\1\40",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\1\37\1\41\1\uffff\74\37"+
            "\1\uffff\1\37\1\uffff\uff80\37",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46\5\uffff\1\47",
            "\1\50",
            "\1\52\14\uffff\1\51",
            "\1\53",
            "\12\54",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\4\37\1\56\1\37\12\54\1\37\2\uffff\2\37"+
            "\1\uffff\74\37\1\uffff\1\37\1\uffff\uff80\37",
            "",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\1\62",
            "",
            "\11\64\2\63\2\64\1\63\22\64\1\63\1\64\1\63\3\64\1\63\1\64\2"+
            "\63\21\64\2\63\2\64\1\63\74\64\1\63\1\64\1\63\uff80\64",
            "\1\66\4\uffff\1\65",
            "",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "",
            "",
            "",
            "\1\73",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "",
            "\1\75",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "",
            "\1\100\2\uffff\1\77",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\4\37\1\56\1\37\12\54\1\37\2\uffff\2\37"+
            "\1\uffff\74\37\1\uffff\1\37\1\uffff\uff80\37",
            "",
            "\12\111",
            "\1\16\4\uffff\1\112\10\uffff\4\114\4\115\44\uffff\1\112\5\uffff"+
            "\1\112\3\uffff\1\112\7\uffff\1\112\3\uffff\1\112\1\uffff\1\112"+
            "\1\113",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\116",
            "",
            "\11\64\2\63\2\64\1\63\22\64\1\63\1\64\1\63\3\64\1\63\1\64\2"+
            "\63\21\64\2\63\2\64\1\63\74\64\1\63\1\64\1\63\uff80\64",
            "\11\120\2\117\2\120\1\117\22\120\1\117\1\120\1\117\3\120\1\117"+
            "\1\120\2\117\21\120\2\117\2\120\1\117\74\120\1\117\1\120\1\117"+
            "\uff80\120",
            "\11\123\2\122\2\123\1\122\22\123\1\122\1\123\1\122\3\123\1\122"+
            "\1\123\2\122\1\121\20\123\2\122\2\123\1\122\74\123\1\122\1\123"+
            "\1\122\uff80\123",
            "",
            "",
            "",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "",
            "\1\125",
            "",
            "\1\126\13\uffff\1\127",
            "\1\130",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\6\37\12\111\1\37\2\uffff\2\37\1\uffff"+
            "\74\37\1\uffff\1\37\1\uffff\uff80\37",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\12\141\7\uffff\6\141\32\uffff\6\141",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\6\60\10\142\3\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1"+
            "\60\1\16\uff80\60",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\6\60\10\143\3\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1"+
            "\60\1\16\uff80\60",
            "\1\144",
            "",
            "\11\120\2\117\2\120\1\117\22\120\1\117\1\120\1\117\3\120\1\117"+
            "\1\120\2\117\21\120\2\117\2\120\1\117\74\120\1\117\1\120\1\117"+
            "\uff80\120",
            "\11\123\2\122\2\123\1\122\22\123\1\122\1\123\1\122\3\123\1\122"+
            "\1\123\2\122\1\121\4\123\1\145\13\123\2\122\2\123\1\122\74\123"+
            "\1\122\1\123\1\122\uff80\123",
            "",
            "\11\123\2\122\2\123\1\122\22\123\1\122\1\123\1\122\3\123\1\122"+
            "\1\123\2\122\1\121\20\123\2\122\2\123\1\122\74\123\1\122\1\123"+
            "\1\122\uff80\123",
            "",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\153",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\156",
            "",
            "\12\157\7\uffff\6\157\32\uffff\6\157",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\6\60\10\160\3\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1"+
            "\60\1\16\uff80\60",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\1\161",
            "\11\123\2\uffff\2\123\1\uffff\22\123\1\uffff\1\123\1\uffff\3"+
            "\123\1\uffff\1\123\2\uffff\1\121\20\123\2\uffff\2\123\1\uffff"+
            "\74\123\1\uffff\1\123\1\uffff\uff80\123",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "",
            "\1\166",
            "",
            "",
            "\1\167",
            "\12\170\7\uffff\6\170\32\uffff\6\170",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\172",
            "\1\173",
            "\1\174",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\176",
            "\12\177\7\uffff\6\177\32\uffff\6\177",
            "",
            "\1\u0080",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "",
            "\1\u0083",
            "\11\60\2\16\2\60\1\16\22\60\1\16\1\60\1\16\3\60\1\16\1\61\2"+
            "\16\21\60\2\16\2\60\1\16\34\60\1\57\37\60\1\16\1\60\1\16\uff80"+
            "\60",
            "\1\u0084",
            "",
            "",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
            "\1\u0086",
            "",
            "\1\u0087",
            "\11\37\2\uffff\2\37\1\uffff\22\37\1\uffff\1\37\1\uffff\3\37"+
            "\1\uffff\1\37\2\uffff\21\37\2\uffff\2\37\1\uffff\74\37\1\uffff"+
            "\1\37\1\uffff\uff80\37",
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
            return "1:1: Tokens : ( T42 | T43 | WS | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}