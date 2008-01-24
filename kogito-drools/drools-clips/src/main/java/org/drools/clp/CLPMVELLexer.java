// $ANTLR 3.0.1 /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g 2008-01-24 17:41:14

	package org.drools.clp;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPMVELLexer extends Lexer {
    public static final int EXISTS=16;
    public static final int DEFRULE=9;
    public static final int HexDigit=31;
    public static final int FLOAT=24;
    public static final int TILDE=21;
    public static final int T45=45;
    public static final int OR=14;
    public static final int PIPE=19;
    public static final int ASSIGN_OP=18;
    public static final int AND=13;
    public static final int EscapeSequence=30;
    public static final int INT=12;
    public static final int START_DELIM=44;
    public static final int SYMBOL=42;
    public static final int LEFT_SQUARE=37;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int T46=46;
    public static final int AMPERSAND=20;
    public static final int DECLARE=29;
    public static final int DELIM=34;
    public static final int LEFT_CURLY=39;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=40;
    public static final int BOOL=25;
    public static final int DEFFUNCTION=7;
    public static final int WS=28;
    public static final int CHAR=43;
    public static final int STRING=10;
    public static final int VAR=8;
    public static final int EQUALS=23;
    public static final int UnicodeEscape=32;
    public static final int EOF=-1;
    public static final int NULL=26;
    public static final int EOL=27;
    public static final int Tokens=47;
    public static final int COLON=22;
    public static final int SALIENCE=11;
    public static final int OctalEscape=33;
    public static final int MULTI_LINE_COMMENT=41;
    public static final int TEST=17;
    public static final int NAME=5;
    public static final int NOT=15;
    public static final int RIGHT_PAREN=6;
    public static final int RIGHT_SQUARE=38;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=36;
    public CLPMVELLexer() {;} 
    public CLPMVELLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[45+1];
     }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g"; }

    // $ANTLR start T45
    public final void mT45() throws RecognitionException {
        try {
            int _type = T45;
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:6:5: ( 'import' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:6:7: 'import'
            {
            match("import"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T45

    // $ANTLR start T46
    public final void mT46() throws RecognitionException {
        try {
            int _type = T46;
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:7:5: ( '=>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:7:7: '=>'
            {
            match("=>"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T46

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:732:9: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:732:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:732:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("732:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:732:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:733:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:734:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:735:19: EOL
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:740:10: ( 'defrule' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:740:12: 'defrule'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:741:13: ( 'deffunction' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:741:15: 'deffunction'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:742:7: ( 'or' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:742:9: 'or'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:743:7: ( 'and' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:743:9: 'and'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:744:7: ( 'not' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:744:9: 'not'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:745:10: ( 'exists' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:745:12: 'exists'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:746:8: ( 'test' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:746:10: 'test'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:748:7: ( 'null' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:748:9: 'null'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:750:10: ( 'declare' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:750:12: 'declare'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:752:10: ( 'salience' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:752:12: 'salience'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:757:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:758:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:758:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("758:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:758:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:759:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:760:25: '\\n'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:2: ( ( '-' )? ( '0' .. '9' )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:4: ( '-' )? ( '0' .. '9' )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:10: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:765:11: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:2: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:10: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:11: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:26: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:769:27: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:5: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("772:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:773:32: ~ ( '\\\\' | '\"' )
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:774:33: ~ ( '\\\\' | '\\'' )
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:778:10: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:778:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:782:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape )
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
                        new NoViableAltException("780:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("780:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:782:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:783:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:784:9: OctalEscape
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("787:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("787:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:14: ( '0' .. '3' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:36: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:789:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:790:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:790:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:790:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:790:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:790:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:791:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:791:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:791:15: '0' .. '7'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:796:5: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:796:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:800:2: ( ( 'true' | 'false' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:800:4: ( 'true' | 'false' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:800:4: ( 'true' | 'false' )
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
                    new NoViableAltException("800:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:800:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:800:12: 'false'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:803:6: ( '?' (~ DELIM )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:803:8: '?' (~ DELIM )+
            {
            match('?'); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:803:11: (~ DELIM )+
            int cnt14=0;
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='\u0000' && LA14_0<='\b')||(LA14_0>='\u000B' && LA14_0<='\f')||(LA14_0>='\u000E' && LA14_0<='\u001F')||LA14_0=='!'||(LA14_0>='#' && LA14_0<='%')||LA14_0=='\''||(LA14_0>='*' && LA14_0<=':')||(LA14_0>='=' && LA14_0<='{')||LA14_0=='}'||(LA14_0>='\u007F' && LA14_0<='\uFFFE')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:803:11: ~ DELIM
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFE') ) {
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:807:2: ( '#' ( options {greedy=false; } : . )* EOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:807:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:807:8: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:807:35: .
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:813:2: ( '//' ( options {greedy=false; } : . )* EOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:813:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:813:9: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:813:36: .
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:819:2: ( '(' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:819:4: '('
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:823:2: ( ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:823:4: ')'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:827:2: ( '[' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:827:4: '['
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:831:2: ( ']' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:831:4: ']'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:835:2: ( '{' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:835:4: '{'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:839:2: ( '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:839:4: '}'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:842:7: ( '~' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:842:9: '~'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:846:2: ( '&' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:846:4: '&'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:850:2: ( '|' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:850:4: '|'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:854:2: ( '<-' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:854:4: '<-'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:857:7: ( ':' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:857:9: ':'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:859:8: ( '=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:859:10: '='
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:862:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:862:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:862:9: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:862:35: .
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:866:6: ( SYMBOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:866:8: SYMBOL
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:869:8: ( ( CHAR )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:869:10: ( CHAR )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:869:10: ( CHAR )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0=='!'||(LA18_0>='#' && LA18_0<='%')||(LA18_0>='*' && LA18_0<=':')||(LA18_0>='=' && LA18_0<='>')||(LA18_0>='@' && LA18_0<='_')||(LA18_0>='a' && LA18_0<='{')||LA18_0=='}') ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:869:10: CHAR
            	    {
            	    mCHAR(); if (failed) return ;

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

    // $ANTLR start START_DELIM
    public final void mSTART_DELIM() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:872:13: ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '?' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' '||input.LA(1)=='\"'||input.LA(1)=='&'||(input.LA(1)>='(' && input.LA(1)<=')')||input.LA(1)==';'||input.LA(1)=='?'||input.LA(1)=='|'||input.LA(1)=='~' ) {
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
    // $ANTLR end START_DELIM

    // $ANTLR start DELIM
    public final void mDELIM() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:875:11: ( ' ' | '\\t' | '\\n' | '\\r' | '\"' | '(' | ')' | ';' | '&' | '|' | '~' | '<' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' '||input.LA(1)=='\"'||input.LA(1)=='&'||(input.LA(1)>='(' && input.LA(1)<=')')||(input.LA(1)>=';' && input.LA(1)<='<')||input.LA(1)=='|'||input.LA(1)=='~' ) {
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
    // $ANTLR end DELIM

    // $ANTLR start CHAR
    public final void mCHAR() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:878:6: ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:878:8: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '!' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '=' | '\\\\' | '/' | '@' | '#' | ':' | '>' | ',' | '.' | '[' | ']' | '{' | '}' )
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='#' && input.LA(1)<='%')||(input.LA(1)>='*' && input.LA(1)<=':')||(input.LA(1)>='=' && input.LA(1)<='>')||(input.LA(1)>='@' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='{')||input.LA(1)=='}' ) {
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
    // $ANTLR end CHAR

    public void mTokens() throws RecognitionException {
        // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:8: ( T45 | T46 | WS | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME )
        int alt19=34;
        alt19 = dfa19.predict(input);
        switch (alt19) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:10: T45
                {
                mT45(); if (failed) return ;

                }
                break;
            case 2 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:14: T46
                {
                mT46(); if (failed) return ;

                }
                break;
            case 3 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:18: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 4 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:21: DEFRULE
                {
                mDEFRULE(); if (failed) return ;

                }
                break;
            case 5 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:29: DEFFUNCTION
                {
                mDEFFUNCTION(); if (failed) return ;

                }
                break;
            case 6 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:41: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 7 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:44: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 8 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:48: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 9 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:52: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 10 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:59: TEST
                {
                mTEST(); if (failed) return ;

                }
                break;
            case 11 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:64: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 12 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:69: DECLARE
                {
                mDECLARE(); if (failed) return ;

                }
                break;
            case 13 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:77: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 14 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:86: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 15 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:90: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 16 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:96: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 17 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:103: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 18 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:108: VAR
                {
                mVAR(); if (failed) return ;

                }
                break;
            case 19 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:112: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 20 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:141: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 21 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:169: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 22 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:180: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 23 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:192: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 24 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:204: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 25 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:217: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 26 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:228: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 27 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:240: TILDE
                {
                mTILDE(); if (failed) return ;

                }
                break;
            case 28 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:246: AMPERSAND
                {
                mAMPERSAND(); if (failed) return ;

                }
                break;
            case 29 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:256: PIPE
                {
                mPIPE(); if (failed) return ;

                }
                break;
            case 30 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:261: ASSIGN_OP
                {
                mASSIGN_OP(); if (failed) return ;

                }
                break;
            case 31 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:271: COLON
                {
                mCOLON(); if (failed) return ;

                }
                break;
            case 32 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:277: EQUALS
                {
                mEQUALS(); if (failed) return ;

                }
                break;
            case 33 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:284: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 34 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:1:303: NAME
                {
                mNAME(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:758:14: ( '\\r\\n' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-clips/src/main/resources/org/drools/clp/CLPMVEL.g:758:16: '\\r\\n'
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
        "\2\35\1\40\1\uffff\10\35\1\52\1\uffff\1\35\1\uffff\2\35\2\uffff"+
        "\1\61\1\62\1\63\1\64\4\uffff\1\65\1\uffff\1\35\1\67\1\uffff\1\35"+
        "\1\72\7\35\1\uffff\3\35\1\uffff\2\35\5\uffff\1\35\1\uffff\2\35\1"+
        "\uffff\1\115\1\35\1\117\4\35\1\124\2\35\1\uffff\2\35\1\uffff\4\35"+
        "\1\uffff\1\133\1\uffff\1\35\1\135\1\136\1\35\1\uffff\1\35\1\105"+
        "\4\35\1\uffff\1\35\2\uffff\1\35\1\136\1\147\3\35\1\153\1\35\1\uffff"+
        "\1\155\1\35\1\157\1\uffff\1\35\1\uffff\1\35\1\uffff\1\162\1\35\1"+
        "\uffff\1\35\1\165\1\uffff";
    static final String DFA19_eofS =
        "\166\uffff";
    static final String DFA19_minS =
        "\1\11\1\155\1\41\1\uffff\1\145\1\162\1\156\1\157\1\170\1\145\1\141"+
        "\1\60\1\41\1\uffff\1\141\1\uffff\1\0\1\52\2\uffff\4\41\4\uffff\1"+
        "\41\1\uffff\1\160\1\41\1\uffff\1\143\1\41\1\144\1\154\1\164\1\151"+
        "\1\163\1\165\1\154\1\uffff\1\60\1\154\1\0\1\uffff\2\0\5\uffff\1"+
        "\157\1\uffff\1\146\1\154\1\uffff\1\41\1\154\1\41\1\163\1\164\1\145"+
        "\1\151\1\41\1\163\1\0\1\uffff\2\0\1\uffff\1\162\2\165\1\141\1\uffff"+
        "\1\41\1\uffff\1\164\2\41\1\145\1\uffff\1\145\1\41\1\164\1\154\1"+
        "\156\1\162\1\uffff\1\163\2\uffff\1\156\2\41\1\145\1\143\1\145\1"+
        "\41\1\143\1\uffff\1\41\1\164\1\41\1\uffff\1\145\1\uffff\1\151\1"+
        "\uffff\1\41\1\157\1\uffff\1\156\1\41\1\uffff";
    static final String DFA19_maxS =
        "\1\176\1\155\1\175\1\uffff\1\145\1\162\1\156\1\165\1\170\1\162\1"+
        "\141\1\71\1\175\1\uffff\1\141\1\uffff\1\ufffe\1\57\2\uffff\4\175"+
        "\4\uffff\1\175\1\uffff\1\160\1\175\1\uffff\1\146\1\175\1\144\1\154"+
        "\1\164\1\151\1\163\1\165\1\154\1\uffff\1\71\1\154\1\ufffe\1\uffff"+
        "\2\ufffe\5\uffff\1\157\1\uffff\1\162\1\154\1\uffff\1\175\1\154\1"+
        "\175\1\163\1\164\1\145\1\151\1\175\1\163\1\ufffe\1\uffff\2\ufffe"+
        "\1\uffff\1\162\2\165\1\141\1\uffff\1\175\1\uffff\1\164\2\175\1\145"+
        "\1\uffff\1\145\1\175\1\164\1\154\1\156\1\162\1\uffff\1\163\2\uffff"+
        "\1\156\2\175\1\145\1\143\1\145\1\175\1\143\1\uffff\1\175\1\164\1"+
        "\175\1\uffff\1\145\1\uffff\1\151\1\uffff\1\175\1\157\1\uffff\1\156"+
        "\1\175\1\uffff";
    static final String DFA19_acceptS =
        "\3\uffff\1\3\11\uffff\1\20\1\uffff\1\22\2\uffff\1\25\1\26\4\uffff"+
        "\1\33\1\34\1\35\1\36\1\uffff\1\42\2\uffff\1\40\11\uffff\1\16\3\uffff"+
        "\1\23\2\uffff\1\27\1\30\1\31\1\32\1\37\1\uffff\1\2\2\uffff\1\6\12"+
        "\uffff\1\41\2\uffff\1\24\4\uffff\1\7\1\uffff\1\10\4\uffff\1\17\6"+
        "\uffff\1\13\1\uffff\1\12\1\21\10\uffff\1\1\3\uffff\1\11\1\uffff"+
        "\1\4\1\uffff\1\14\2\uffff\1\15\2\uffff\1\5";
    static final String DFA19_specialS =
        "\166\uffff}>";
    static final String[] DFA19_transitionS = {
            "\2\3\1\uffff\2\3\22\uffff\1\3\1\uffff\1\15\1\20\2\uffff\1\31"+
            "\1\15\1\22\1\23\3\uffff\1\13\1\uffff\1\21\12\14\1\34\1\uffff"+
            "\1\33\1\2\1\uffff\1\17\33\uffff\1\24\1\uffff\1\25\3\uffff\1"+
            "\6\2\uffff\1\4\1\10\1\16\2\uffff\1\1\4\uffff\1\7\1\5\3\uffff"+
            "\1\12\1\11\6\uffff\1\26\1\32\1\27\1\30",
            "\1\36",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\1\35\1\37\1\uffff\40"+
            "\35\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\45\5\uffff\1\44",
            "\1\46",
            "\1\47\14\uffff\1\50",
            "\1\51",
            "\12\14",
            "\1\35\1\uffff\3\35\4\uffff\4\35\1\53\1\35\12\14\1\35\2\uffff"+
            "\2\35\1\uffff\40\35\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\54",
            "",
            "\41\56\1\55\1\56\3\55\4\56\21\55\2\56\2\55\1\56\40\55\1\56\33"+
            "\55\1\56\1\55\uff81\56",
            "\1\57\4\uffff\1\60",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "",
            "",
            "",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\66",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\71\2\uffff\1\70",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "",
            "\12\102",
            "\1\103",
            "\41\56\1\55\1\56\3\55\4\56\21\55\2\56\2\55\1\56\40\55\1\56\33"+
            "\55\1\56\1\55\uff81\56",
            "",
            "\41\105\1\106\1\105\3\106\4\105\1\104\20\106\2\105\2\106\1\105"+
            "\40\106\1\105\33\106\1\105\1\106\uff81\105",
            "\41\110\1\107\1\110\3\107\4\110\21\107\2\110\2\107\1\110\40"+
            "\107\1\110\33\107\1\110\1\107\uff81\110",
            "",
            "",
            "",
            "",
            "",
            "\1\111",
            "",
            "\1\113\13\uffff\1\112",
            "\1\114",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\116",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\35\1\uffff\3\35\4\uffff\6\35\12\102\1\35\2\uffff\2\35\1\uffff"+
            "\40\35\1\uffff\33\35\1\uffff\1\35",
            "\1\125",
            "\41\105\1\106\1\105\3\106\4\105\1\104\4\106\1\126\13\106\2\105"+
            "\2\106\1\105\40\106\1\105\33\106\1\105\1\106\uff81\105",
            "",
            "\41\105\1\106\1\105\3\106\4\105\1\104\20\106\2\105\2\106\1\105"+
            "\40\106\1\105\33\106\1\105\1\106\uff81\105",
            "\41\110\1\107\1\110\3\107\4\110\21\107\2\110\2\107\1\110\40"+
            "\107\1\110\33\107\1\110\1\107\uff81\110",
            "",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\134",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\137",
            "",
            "\1\140",
            "\1\106\1\uffff\3\106\4\uffff\1\104\20\106\2\uffff\2\106\1\uffff"+
            "\40\106\1\uffff\33\106\1\uffff\1\106",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "",
            "\1\145",
            "",
            "",
            "\1\146",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\154",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\156",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "",
            "\1\160",
            "",
            "\1\161",
            "",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
            "\1\163",
            "",
            "\1\164",
            "\1\35\1\uffff\3\35\4\uffff\21\35\2\uffff\2\35\1\uffff\40\35"+
            "\1\uffff\33\35\1\uffff\1\35",
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
            return "1:1: Tokens : ( T45 | T46 | WS | DEFRULE | DEFFUNCTION | OR | AND | NOT | EXISTS | TEST | NULL | DECLARE | SALIENCE | INT | FLOAT | STRING | BOOL | VAR | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | TILDE | AMPERSAND | PIPE | ASSIGN_OP | COLON | EQUALS | MULTI_LINE_COMMENT | NAME );";
        }
    }
 

}