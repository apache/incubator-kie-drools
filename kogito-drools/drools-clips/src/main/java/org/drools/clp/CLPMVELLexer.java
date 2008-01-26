// $ANTLR 3.0.1 C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g 2008-01-26 17:55:37

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPMVELLexer extends Lexer {
    public static final int RIGHT_SQUARE=37;
    public static final int RIGHT_CURLY=39;
    public static final int EQUALS=22;
    public static final int FLOAT=23;
    public static final int NOT=13;
    public static final int SYMBOL_CHAR=33;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int AND=11;
    public static final int FIRST_SYMBOL_CHAR=42;
    public static final int EOF=-1;
    public static final int HexDigit=30;
    public static final int ASSIGN_OP=17;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
    public static final int EOL=26;
    public static final int DEFRULE=7;
    public static final int TILDE=20;
    public static final int PIPE=18;
    public static final int VAR=16;
    public static final int EXISTS=14;
    public static final int SYMBOL=41;
    public static final int NULL=25;
    public static final int BOOL=24;
    public static final int AMPERSAND=19;
    public static final int SALIENCE=9;
    public static final int INT=10;
    public static final int T43=43;
    public static final int Tokens=45;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int T44=44;
    public static final int COLON=21;
    public static final int WS=27;
    public static final int UnicodeEscape=31;
    public static final int LEFT_CURLY=38;
    public static final int OR=12;
    public static final int TEST=15;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=28;
    public static final int LEFT_SQUARE=36;
    public static final int OctalEscape=32;
    public static final int EscapeSequence=29;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int STRING=8;
    public CLPMVELLexer() {;} 
    public CLPMVELLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[43+1];
     }
    public String getGrammarFileName() { return "C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g"; }

    // $ANTLR start T43
    public final void mT43() throws RecognitionException {
        try {
            int _type = T43;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:6:5: ( 'import' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:6:7: 'import'
            {
            match("import"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T43

    // $ANTLR start T44
    public final void mT44() throws RecognitionException {
        try {
            int _type = T44;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:7:5: ( '=>' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:7:7: '=>'
            {
            match("=>"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T44

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:680:9: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:680:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:680:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("680:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:680:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:681:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:682:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:683:19: EOL
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:688:10: ( 'defrule' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:688:12: 'defrule'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:7: ( 'or' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:9: 'or'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:691:7: ( 'and' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:691:9: 'and'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:692:7: ( 'not' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:692:9: 'not'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:693:10: ( 'exists' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:693:12: 'exists'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:694:8: ( 'test' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:694:10: 'test'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:696:7: ( 'null' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:696:9: 'null'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:698:10: ( 'declare' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:698:12: 'declare'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:700:10: ( 'salience' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:700:12: 'salience'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:705:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:706:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:706:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("706:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:706:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:707:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:708:25: '\\n'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:2: ( ( '-' )? ( '0' .. '9' )+ )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:4: ( '-' )? ( '0' .. '9' )+
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:713:11: '0' .. '9'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:10: ( '0' .. '9' )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:11: '0' .. '9'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:26: ( '0' .. '9' )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:717:27: '0' .. '9'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("720:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:721:32: ~ ( '\\\\' | '\"' )
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
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:722:33: ~ ( '\\\\' | '\\'' )
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:726:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:726:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:730:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
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
                    {
                    alt11=1;
                    }
                    break;
                case 'u':
                    {
                    alt11=2;
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
                        new NoViableAltException("728:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("728:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:730:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:731:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:732:9: OctalEscape
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("735:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("735:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:14: ( '0' .. '3' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:25: ( '0' .. '7' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:36: ( '0' .. '7' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:737:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:738:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:738:14: ( '0' .. '7' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:738:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:738:25: ( '0' .. '7' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:738:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:739:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:739:14: ( '0' .. '7' )
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:739:15: '0' .. '7'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:744:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:744:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:748:2: ( ( 'true' | 'false' ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:748:4: ( 'true' | 'false' )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:748:4: ( 'true' | 'false' )
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
                    new NoViableAltException("748:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:748:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:748:12: 'false'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:751:6: ( '?' ( SYMBOL_CHAR )+ )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:751:8: '?' ( SYMBOL_CHAR )+
            {
            match('?'); if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:751:12: ( SYMBOL_CHAR )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='=' && LA14_0<='_')||(LA14_0>='a' && LA14_0<='{')||LA14_0=='}') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:751:12: SYMBOL_CHAR
            	    {
            	    mSYMBOL_CHAR(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt14 >= 1 ) break loop14;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(14, input);
                        throw eee;
                }
                cnt14++;
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:755:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:755:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:755:8: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:755:35: .
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:761:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:761:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:761:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:761:36: .
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:767:2: ( '(' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:767:4: '('
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:771:2: ( ')' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:771:4: ')'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:775:2: ( '[' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:775:4: '['
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:779:2: ( ']' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:779:4: ']'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:783:2: ( '{' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:783:4: '{'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:787:2: ( '}' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:787:4: '}'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:790:7: ( '~' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:790:9: '~'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:794:2: ( '&' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:794:4: '&'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:798:2: ( '|' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:798:4: '|'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:802:2: ( '<-' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:802:4: '<-'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:805:7: ( ':' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:805:9: ':'
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:807:8: ( '=' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:807:10: '='
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:810:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:810:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:810:9: ( options {greedy=false; } : . )*
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:810:35: .
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:814:6: ( SYMBOL )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:814:8: SYMBOL
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
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:817:8: ( FIRST_SYMBOL_CHAR ( SYMBOL_CHAR )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:817:10: FIRST_SYMBOL_CHAR ( SYMBOL_CHAR )*
            {
            mFIRST_SYMBOL_CHAR(); if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:817:28: ( SYMBOL_CHAR )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='!'||(LA18_0>='#' && LA18_0<='%')||(LA18_0>='*' && LA18_0<=':')||(LA18_0>='=' && LA18_0<='_')||(LA18_0>='a' && LA18_0<='{')||LA18_0=='}') ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:817:28: SYMBOL_CHAR
            	    {
            	    mSYMBOL_CHAR(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end SYMBOL

    // $ANTLR start FIRST_SYMBOL_CHAR
    public final void mFIRST_SYMBOL_CHAR() throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:822:19: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | '<' | ',' | '.' | '[' | ']' | '{' | '}' ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:822:21: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | '<' | ',' | '.' | '[' | ']' | '{' | '}' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='<' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
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
    // $ANTLR end FIRST_SYMBOL_CHAR

    // $ANTLR start SYMBOL_CHAR
    public final void mSYMBOL_CHAR() throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:827:13: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' | '?' ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:827:15: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' | '?' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='=' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
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
    // $ANTLR end SYMBOL_CHAR

    public void mTokens() throws RecognitionException {
        // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:8: ( T43 | T44 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt19=33;
        alt19 = dfa19.predict(input);
        switch (alt19) {
            case 1 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:10: T43
                {
                mT43(); if (failed) return ;

                }
                break;
            case 2 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:14: T44
                {
                mT44(); if (failed) return ;

                }
                break;
            case 3 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:18: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 4 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:21: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 5 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:29: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 6 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:32: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 7 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:36: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 8 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:40: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 9 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:47: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 10 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:52: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 11 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:57: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 12 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:65: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 13 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:74: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 14 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:78: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 15 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:84: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 16 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:91: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 17 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:96: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 18 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:100: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 19 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:129: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 20 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:157: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 21 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:168: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 22 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:180: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 23 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:192: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 24 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:205: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 25 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:216: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 26 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:228: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 27 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:234: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 28 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:244: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 29 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:249: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 30 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:259: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 31 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:265: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 32 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:272: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 33 :
                // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:1:291: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:706:14: ( '\\r\\n' )
        // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:706:16: '\\r\\n'
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


    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA19_eotS =
        "\1\uffff\1\35\1\40\1\uffff\10\35\1\54\1\uffff\1\35\1\uffff\2\35"+
        "\2\uffff\1\62\1\63\1\64\1\65\3\uffff\1\35\1\67\1\uffff\1\35\1\71"+
        "\1\uffff\1\35\1\74\7\35\1\54\1\35\1\uffff\2\35\1\uffff\2\35\4\uffff"+
        "\1\113\1\uffff\1\35\1\uffff\2\35\1\uffff\1\117\1\120\5\35\1\126"+
        "\2\35\1\uffff\1\35\1\uffff\1\35\1\uffff\3\35\2\uffff\1\134\1\35"+
        "\1\136\1\137\1\35\1\uffff\1\35\1\111\3\35\1\uffff\1\35\2\uffff\1"+
        "\35\1\136\1\147\2\35\1\152\1\35\1\uffff\1\154\1\155\1\uffff\1\35"+
        "\2\uffff\1\157\1\uffff";
    static final String DFA19_eofS =
        "\160\uffff";
    static final String DFA19_minS =
        "\1\11\1\155\1\41\1\uffff\1\145\1\162\1\156\1\157\1\170\1\145\1\141"+
        "\1\60\1\41\1\uffff\1\141\1\uffff\1\0\1\52\2\uffff\4\41\3\uffff\1"+
        "\55\1\41\1\uffff\1\160\1\41\1\uffff\1\143\1\41\1\144\1\164\1\154"+
        "\1\151\1\165\1\163\1\154\1\41\1\60\1\uffff\1\154\1\0\1\uffff\2\0"+
        "\4\uffff\1\41\1\uffff\1\157\1\uffff\1\154\1\162\1\uffff\2\41\1\154"+
        "\1\163\1\145\1\164\1\151\1\41\1\163\1\0\1\uffff\1\0\1\uffff\1\0"+
        "\1\uffff\1\162\1\141\1\165\2\uffff\1\41\1\164\2\41\1\145\1\uffff"+
        "\1\145\1\41\1\164\1\162\1\154\1\uffff\1\163\2\uffff\1\156\2\41\2"+
        "\145\1\41\1\143\1\uffff\2\41\1\uffff\1\145\2\uffff\1\41\1\uffff";
    static final String DFA19_maxS =
        "\1\176\1\155\1\175\1\uffff\1\145\1\162\1\156\1\165\1\170\1\162\1"+
        "\141\1\71\1\175\1\uffff\1\141\1\uffff\1\ufffe\1\57\2\uffff\4\175"+
        "\3\uffff\1\55\1\175\1\uffff\1\160\1\175\1\uffff\1\146\1\175\1\144"+
        "\1\164\1\154\1\151\1\165\1\163\1\154\1\175\1\71\1\uffff\1\154\1"+
        "\ufffe\1\uffff\2\ufffe\4\uffff\1\175\1\uffff\1\157\1\uffff\1\154"+
        "\1\162\1\uffff\2\175\1\154\1\163\1\145\1\164\1\151\1\175\1\163\1"+
        "\ufffe\1\uffff\1\ufffe\1\uffff\1\ufffe\1\uffff\1\162\1\141\1\165"+
        "\2\uffff\1\175\1\164\2\175\1\145\1\uffff\1\145\1\175\1\164\1\162"+
        "\1\154\1\uffff\1\163\2\uffff\1\156\2\175\2\145\1\175\1\143\1\uffff"+
        "\2\175\1\uffff\1\145\2\uffff\1\175\1\uffff";
    static final String DFA19_acceptS =
        "\3\uffff\1\3\11\uffff\1\17\1\uffff\1\21\2\uffff\1\24\1\25\4\uffff"+
        "\1\32\1\33\1\34\2\uffff\1\41\2\uffff\1\37\13\uffff\1\15\2\uffff"+
        "\1\22\2\uffff\1\26\1\27\1\30\1\31\1\uffff\1\36\1\uffff\1\2\2\uffff"+
        "\1\5\12\uffff\1\23\1\uffff\1\40\1\uffff\1\35\3\uffff\1\6\1\7\5\uffff"+
        "\1\16\5\uffff\1\12\1\uffff\1\20\1\11\7\uffff\1\1\2\uffff\1\10\1"+
        "\uffff\1\13\1\4\1\uffff\1\14";
    static final String DFA19_specialS =
        "\160\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\3\1\uffff\2\3\22\uffff\1\3\1\35\1\15\1\20\2\35\1\31\1\15"+
            "\1\22\1\23\3\35\1\13\1\35\1\21\12\14\1\34\1\uffff\1\33\1\2\1"+
            "\35\1\17\33\35\1\24\1\35\1\25\2\35\1\uffff\1\6\2\35\1\4\1\10"+
            "\1\16\2\35\1\1\4\35\1\7\1\5\3\35\1\12\1\11\6\35\1\26\1\32\1"+
            "\27\1\30",
            "\1\36",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\1\35\1\37\41\35\1\uffff"+
            "\33\35\1\uffff\1\35",
            "",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44\5\uffff\1\45",
            "\1\46",
            "\1\50\14\uffff\1\47",
            "\1\51",
            "\12\52",
            "\1\35\1\uffff\3\35\4\uffff\4\35\1\53\1\35\12\52\1\35\2\uffff"+
            "\43\35\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\55",
            "",
            "\41\57\1\56\1\57\3\56\4\57\21\56\2\57\43\56\1\57\33\56\1\57"+
            "\1\56\uff81\57",
            "\1\61\4\uffff\1\60",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "",
            "",
            "\1\66",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\70",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\72\2\uffff\1\73",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\35\1\uffff\3\35\4\uffff\4\35\1\53\1\35\12\52\1\35\2\uffff"+
            "\43\35\1\uffff\33\35\1\uffff\1\35",
            "\12\104",
            "",
            "\1\105",
            "\41\57\1\56\1\57\3\56\4\57\21\56\2\57\43\56\1\57\33\56\1\57"+
            "\1\56\uff81\57",
            "",
            "\41\107\1\106\1\107\3\106\4\107\21\106\2\107\43\106\1\107\33"+
            "\106\1\107\1\106\uff81\107",
            "\41\111\1\112\1\111\3\112\4\111\1\110\20\112\2\111\43\112\1"+
            "\111\33\112\1\111\1\112\uff81\111",
            "",
            "",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\114",
            "",
            "\1\115",
            "\1\116",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\35\1\uffff\3\35\4\uffff\6\35\12\104\1\35\2\uffff\43\35\1"+
            "\uffff\33\35\1\uffff\1\35",
            "\1\127",
            "\41\107\1\106\1\107\3\106\4\107\21\106\2\107\43\106\1\107\33"+
            "\106\1\107\1\106\uff81\107",
            "",
            "\41\111\1\112\1\111\3\112\4\111\1\110\4\112\1\130\13\112\2\111"+
            "\43\112\1\111\33\112\1\111\1\112\uff81\111",
            "",
            "\41\111\1\112\1\111\3\112\4\111\1\110\20\112\2\111\43\112\1"+
            "\111\33\112\1\111\1\112\uff81\111",
            "",
            "\1\131",
            "\1\132",
            "\1\133",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\135",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\140",
            "",
            "\1\141",
            "\1\112\1\uffff\3\112\4\uffff\1\110\20\112\2\uffff\43\112\1\uffff"+
            "\33\112\1\uffff\1\112",
            "\1\142",
            "\1\143",
            "\1\144",
            "",
            "\1\145",
            "",
            "",
            "\1\146",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\150",
            "\1\151",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\153",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            "",
            "\1\156",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\43\35\1\uffff\33\35"+
            "\1\uffff\1\35",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T43 | T44 | WS | DEFRULE | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}