// $ANTLR 3.0 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-06-07 11:34:30

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int COMMA=12;
    public static final int EXISTS=39;
    public static final int T79=79;
    public static final int AUTO_FOCUS=27;
    public static final int END=15;
    public static final int HexDigit=63;
    public static final int FORALL=42;
    public static final int TEMPLATE=16;
    public static final int T70=70;
    public static final int MISC=69;
    public static final int FLOAT=53;
    public static final int T74=74;
    public static final int QUERY=14;
    public static final int THEN=59;
    public static final int RULE=17;
    public static final int INIT=44;
    public static final int IMPORT=6;
    public static final int PACKAGE=5;
    public static final int DATE_EFFECTIVE=19;
    public static final int OR=34;
    public static final int DOT=9;
    public static final int DOUBLE_PIPE=35;
    public static final int AND=36;
    public static final int FUNCTION=7;
    public static final int GLOBAL=10;
    public static final int EscapeSequence=62;
    public static final int DIALECT=32;
    public static final int INT=25;
    public static final int LOCK_ON_ACTIVE=33;
    public static final int DATE_EXPIRES=21;
    public static final int LEFT_SQUARE=57;
    public static final int CONTAINS=48;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=66;
    public static final int T77=77;
    public static final int ATTRIBUTES=4;
    public static final int LEFT_CURLY=55;
    public static final int RESULT=46;
    public static final int ID=8;
    public static final int FROM=38;
    public static final int LEFT_PAREN=11;
    public static final int ACTIVATION_GROUP=28;
    public static final int DOUBLE_AMPER=37;
    public static final int RIGHT_CURLY=56;
    public static final int BOOL=23;
    public static final int EXCLUDES=49;
    public static final int T73=73;
    public static final int MEMBEROF=51;
    public static final int WHEN=18;
    public static final int T78=78;
    public static final int RULEFLOW_GROUP=29;
    public static final int WS=61;
    public static final int STRING=20;
    public static final int ACTION=45;
    public static final int T71=71;
    public static final int T72=72;
    public static final int COLLECT=47;
    public static final int T76=76;
    public static final int IN=52;
    public static final int ACCUMULATE=43;
    public static final int NO_LOOP=26;
    public static final int UnicodeEscape=64;
    public static final int T75=75;
    public static final int DURATION=31;
    public static final int EVAL=41;
    public static final int MATCHES=50;
    public static final int EOF=-1;
    public static final int AGENDA_GROUP=30;
    public static final int NULL=54;
    public static final int EOL=60;
    public static final int Tokens=80;
    public static final int SALIENCE=24;
    public static final int OctalEscape=65;
    public static final int MULTI_LINE_COMMENT=68;
    public static final int NOT=40;
    public static final int RIGHT_PAREN=13;
    public static final int ENABLED=22;
    public static final int RIGHT_SQUARE=58;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=67;
    public DRLLexer() {;} 
    public DRLLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[78+1];
     }
    public String getGrammarFileName() { return "/home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }

    // $ANTLR start T70
    public final void mT70() throws RecognitionException {
        try {
            int _type = T70;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:6:7: ( ';' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:6:7: ';'
            {
            match(';'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T70

    // $ANTLR start T71
    public final void mT71() throws RecognitionException {
        try {
            int _type = T71;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:7:7: ( ':' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:7:7: ':'
            {
            match(':'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T71

    // $ANTLR start T72
    public final void mT72() throws RecognitionException {
        try {
            int _type = T72;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:8:7: ( '.*' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:8:7: '.*'
            {
            match(".*"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T72

    // $ANTLR start T73
    public final void mT73() throws RecognitionException {
        try {
            int _type = T73;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:9:7: ( '->' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:9:7: '->'
            {
            match("->"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T73

    // $ANTLR start T74
    public final void mT74() throws RecognitionException {
        try {
            int _type = T74;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:10:7: ( '==' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:10:7: '=='
            {
            match("=="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T74

    // $ANTLR start T75
    public final void mT75() throws RecognitionException {
        try {
            int _type = T75;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:11:7: ( '>' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:11:7: '>'
            {
            match('>'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T75

    // $ANTLR start T76
    public final void mT76() throws RecognitionException {
        try {
            int _type = T76;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:12:7: ( '>=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:12:7: '>='
            {
            match(">="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T76

    // $ANTLR start T77
    public final void mT77() throws RecognitionException {
        try {
            int _type = T77;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:13:7: ( '<' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:13:7: '<'
            {
            match('<'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T77

    // $ANTLR start T78
    public final void mT78() throws RecognitionException {
        try {
            int _type = T78;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:14:7: ( '<=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:14:7: '<='
            {
            match("<="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T78

    // $ANTLR start T79
    public final void mT79() throws RecognitionException {
        try {
            int _type = T79;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:15:7: ( '!=' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:15:7: '!='
            {
            match("!="); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T79

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1742:17: ( ( ' ' | '\\t' | '\\f' | EOL )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1742:17: ( ' ' | '\\t' | '\\f' | EOL )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1742:17: ( ' ' | '\\t' | '\\f' | EOL )+
            int cnt1=0;
            loop1:
            do {
                int alt1=5;
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

                }

                switch (alt1) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1742:19: ' '
            	    {
            	    match(' '); if (failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1743:19: '\\t'
            	    {
            	    match('\t'); if (failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1744:19: '\\f'
            	    {
            	    match('\f'); if (failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1745:19: EOL
            	    {
            	    mEOL(); if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

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

    // $ANTLR start EOL
    public final void mEOL() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("1752:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1753:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1754:25: '\\n'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:4: ( ( '-' )? ( '0' .. '9' )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:4: ( '-' )? ( '0' .. '9' )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:10: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1759:11: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:10: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:11: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:26: ( '0' .. '9' )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1763:27: '0' .. '9'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:8: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("1766:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1767:32: ~ ( '\\\\' | '\"' )
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1768:33: ~ ( '\\\\' | '\\'' )
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1772:12: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1772:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1776:9: ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape )
            int alt11=3;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '$':
                case '&':
                case '\'':
                case '(':
                case ')':
                case '*':
                case '+':
                case '-':
                case '.':
                case '?':
                case 'A':
                case 'B':
                case 'D':
                case 'E':
                case 'G':
                case 'Q':
                case 'S':
                case 'W':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'n':
                case 'o':
                case 'p':
                case 'r':
                case 's':
                case 't':
                case 'w':
                case 'x':
                case 'z':
                case '{':
                case '|':
                case '}':
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
                        new NoViableAltException("1774:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1774:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1776:9: '\\\\' ( 'b' | 'B' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' | 'o' | 'x' | 'a' | 'e' | 'c' | 'd' | 'D' | 's' | 'S' | 'w' | 'W' | 'p' | 'A' | 'G' | 'Z' | 'z' | 'Q' | 'E' | '*' | '[' | ']' | '(' | ')' | '$' | '^' | '{' | '}' | '?' | '+' | '-' | '&' | '|' )
                    {
                    match('\\'); if (failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='$'||(input.LA(1)>='&' && input.LA(1)<='+')||(input.LA(1)>='-' && input.LA(1)<='.')||input.LA(1)=='?'||(input.LA(1)>='A' && input.LA(1)<='B')||(input.LA(1)>='D' && input.LA(1)<='E')||input.LA(1)=='G'||input.LA(1)=='Q'||input.LA(1)=='S'||input.LA(1)=='W'||(input.LA(1)>='Z' && input.LA(1)<='^')||(input.LA(1)>='a' && input.LA(1)<='f')||(input.LA(1)>='n' && input.LA(1)<='p')||(input.LA(1)>='r' && input.LA(1)<='t')||(input.LA(1)>='w' && input.LA(1)<='x')||(input.LA(1)>='z' && input.LA(1)<='}') ) {
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1780:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1781:9: OctalEscape
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("1784:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1784:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:14: ( '0' .. '3' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:36: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1786:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1787:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1787:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1787:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1787:25: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1787:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1788:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1788:14: ( '0' .. '7' )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1788:15: '0' .. '7'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1793:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1793:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1797:4: ( ( 'true' | 'false' ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1797:4: ( 'true' | 'false' )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1797:4: ( 'true' | 'false' )
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
                    new NoViableAltException("1797:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1797:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1797:12: 'false'
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

    // $ANTLR start PACKAGE
    public final void mPACKAGE() throws RecognitionException {
        try {
            int _type = PACKAGE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1800:11: ( 'package' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1800:11: 'package'
            {
            match("package"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PACKAGE

    // $ANTLR start IMPORT
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1802:10: ( 'import' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1802:10: 'import'
            {
            match("import"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IMPORT

    // $ANTLR start FUNCTION
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1804:12: ( 'function' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1804:12: 'function'
            {
            match("function"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNCTION

    // $ANTLR start GLOBAL
    public final void mGLOBAL() throws RecognitionException {
        try {
            int _type = GLOBAL;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1806:10: ( 'global' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1806:10: 'global'
            {
            match("global"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GLOBAL

    // $ANTLR start RULE
    public final void mRULE() throws RecognitionException {
        try {
            int _type = RULE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1808:11: ( 'rule' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1808:11: 'rule'
            {
            match("rule"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULE

    // $ANTLR start QUERY
    public final void mQUERY() throws RecognitionException {
        try {
            int _type = QUERY;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1810:9: ( 'query' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1810:9: 'query'
            {
            match("query"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end QUERY

    // $ANTLR start TEMPLATE
    public final void mTEMPLATE() throws RecognitionException {
        try {
            int _type = TEMPLATE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1812:12: ( 'template' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1812:12: 'template'
            {
            match("template"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TEMPLATE

    // $ANTLR start ATTRIBUTES
    public final void mATTRIBUTES() throws RecognitionException {
        try {
            int _type = ATTRIBUTES;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1814:14: ( 'attributes' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1814:14: 'attributes'
            {
            match("attributes"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ATTRIBUTES

    // $ANTLR start DATE_EFFECTIVE
    public final void mDATE_EFFECTIVE() throws RecognitionException {
        try {
            int _type = DATE_EFFECTIVE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1817:4: ( 'date-effective' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1817:4: 'date-effective'
            {
            match("date-effective"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DATE_EFFECTIVE

    // $ANTLR start DATE_EXPIRES
    public final void mDATE_EXPIRES() throws RecognitionException {
        try {
            int _type = DATE_EXPIRES;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1820:4: ( 'date-expires' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1820:4: 'date-expires'
            {
            match("date-expires"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DATE_EXPIRES

    // $ANTLR start ENABLED
    public final void mENABLED() throws RecognitionException {
        try {
            int _type = ENABLED;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1822:11: ( 'enabled' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1822:11: 'enabled'
            {
            match("enabled"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ENABLED

    // $ANTLR start SALIENCE
    public final void mSALIENCE() throws RecognitionException {
        try {
            int _type = SALIENCE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1825:4: ( 'salience' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1825:4: 'salience'
            {
            match("salience"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SALIENCE

    // $ANTLR start NO_LOOP
    public final void mNO_LOOP() throws RecognitionException {
        try {
            int _type = NO_LOOP;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1827:11: ( 'no-loop' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1827:11: 'no-loop'
            {
            match("no-loop"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NO_LOOP

    // $ANTLR start AUTO_FOCUS
    public final void mAUTO_FOCUS() throws RecognitionException {
        try {
            int _type = AUTO_FOCUS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1830:4: ( 'auto-focus' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1830:4: 'auto-focus'
            {
            match("auto-focus"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AUTO_FOCUS

    // $ANTLR start ACTIVATION_GROUP
    public final void mACTIVATION_GROUP() throws RecognitionException {
        try {
            int _type = ACTIVATION_GROUP;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1833:4: ( 'activation-group' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1833:4: 'activation-group'
            {
            match("activation-group"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ACTIVATION_GROUP

    // $ANTLR start AGENDA_GROUP
    public final void mAGENDA_GROUP() throws RecognitionException {
        try {
            int _type = AGENDA_GROUP;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1836:4: ( 'agenda-group' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1836:4: 'agenda-group'
            {
            match("agenda-group"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AGENDA_GROUP

    // $ANTLR start DIALECT
    public final void mDIALECT() throws RecognitionException {
        try {
            int _type = DIALECT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1839:4: ( 'dialect' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1839:4: 'dialect'
            {
            match("dialect"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DIALECT

    // $ANTLR start RULEFLOW_GROUP
    public final void mRULEFLOW_GROUP() throws RecognitionException {
        try {
            int _type = RULEFLOW_GROUP;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1842:4: ( 'ruleflow-group' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1842:4: 'ruleflow-group'
            {
            match("ruleflow-group"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RULEFLOW_GROUP

    // $ANTLR start DURATION
    public final void mDURATION() throws RecognitionException {
        try {
            int _type = DURATION;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1845:4: ( 'duration' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1845:4: 'duration'
            {
            match("duration"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DURATION

    // $ANTLR start LOCK_ON_ACTIVE
    public final void mLOCK_ON_ACTIVE() throws RecognitionException {
        try {
            int _type = LOCK_ON_ACTIVE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1848:4: ( 'lock-on-active' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1848:4: 'lock-on-active'
            {
            match("lock-on-active"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LOCK_ON_ACTIVE

    // $ANTLR start FROM
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:8: ( 'from' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1850:8: 'from'
            {
            match("from"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FROM

    // $ANTLR start ACCUMULATE
    public final void mACCUMULATE() throws RecognitionException {
        try {
            int _type = ACCUMULATE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1853:4: ( 'accumulate' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1853:4: 'accumulate'
            {
            match("accumulate"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ACCUMULATE

    // $ANTLR start INIT
    public final void mINIT() throws RecognitionException {
        try {
            int _type = INIT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:8: ( 'init' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1855:8: 'init'
            {
            match("init"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INIT

    // $ANTLR start ACTION
    public final void mACTION() throws RecognitionException {
        try {
            int _type = ACTION;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1857:10: ( 'action' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1857:10: 'action'
            {
            match("action"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ACTION

    // $ANTLR start RESULT
    public final void mRESULT() throws RecognitionException {
        try {
            int _type = RESULT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1859:10: ( 'result' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1859:10: 'result'
            {
            match("result"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RESULT

    // $ANTLR start COLLECT
    public final void mCOLLECT() throws RecognitionException {
        try {
            int _type = COLLECT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1861:11: ( 'collect' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1861:11: 'collect'
            {
            match("collect"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COLLECT

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1863:6: ( 'or' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1863:6: 'or'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1865:7: ( 'and' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1865:7: 'and'
            {
            match("and"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start CONTAINS
    public final void mCONTAINS() throws RecognitionException {
        try {
            int _type = CONTAINS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1868:4: ( 'contains' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1868:4: 'contains'
            {
            match("contains"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONTAINS

    // $ANTLR start EXCLUDES
    public final void mEXCLUDES() throws RecognitionException {
        try {
            int _type = EXCLUDES;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1871:4: ( 'excludes' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1871:4: 'excludes'
            {
            match("excludes"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXCLUDES

    // $ANTLR start MEMBEROF
    public final void mMEMBEROF() throws RecognitionException {
        try {
            int _type = MEMBEROF;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1874:4: ( 'memberOf' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1874:4: 'memberOf'
            {
            match("memberOf"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MEMBEROF

    // $ANTLR start MATCHES
    public final void mMATCHES() throws RecognitionException {
        try {
            int _type = MATCHES;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1876:11: ( 'matches' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1876:11: 'matches'
            {
            match("matches"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MATCHES

    // $ANTLR start IN
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1878:6: ( 'in' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1878:6: 'in'
            {
            match("in"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IN

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1880:8: ( 'null' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1880:8: 'null'
            {
            match("null"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start EXISTS
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1882:10: ( 'exists' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1882:10: 'exists'
            {
            match("exists"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXISTS

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1884:7: ( 'not' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1884:7: 'not'
            {
            match("not"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start EVAL
    public final void mEVAL() throws RecognitionException {
        try {
            int _type = EVAL;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1886:8: ( 'eval' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1886:8: 'eval'
            {
            match("eval"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EVAL

    // $ANTLR start FORALL
    public final void mFORALL() throws RecognitionException {
        try {
            int _type = FORALL;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1888:10: ( 'forall' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1888:10: 'forall'
            {
            match("forall"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FORALL

    // $ANTLR start WHEN
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1890:11: ( 'when' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1890:11: 'when'
            {
            match("when"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHEN

    // $ANTLR start THEN
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1892:12: ( 'then' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1892:12: 'then'
            {
            match("then"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THEN

    // $ANTLR start END
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1894:11: ( 'end' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1894:11: 'end'
            {
            match("end"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end END

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1897:4: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1897:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1897:50: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
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

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ID

    // $ANTLR start LEFT_PAREN
    public final void mLEFT_PAREN() throws RecognitionException {
        try {
            int _type = LEFT_PAREN;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1901:11: ( '(' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1901:11: '('
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1905:11: ( ')' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1905:11: ')'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1909:11: ( '[' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1909:11: '['
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1913:11: ( ']' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1913:11: ']'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1917:11: ( '{' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1917:11: '{'
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1921:11: ( '}' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1921:11: '}'
            {
            match('}'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1924:9: ( ',' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1924:9: ','
            {
            match(','); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1927:7: ( '.' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1927:7: '.'
            {
            match('.'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start DOUBLE_AMPER
    public final void mDOUBLE_AMPER() throws RecognitionException {
        try {
            int _type = DOUBLE_AMPER;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1931:4: ( '&&' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1931:4: '&&'
            {
            match("&&"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_AMPER

    // $ANTLR start DOUBLE_PIPE
    public final void mDOUBLE_PIPE() throws RecognitionException {
        try {
            int _type = DOUBLE_PIPE;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1935:4: ( '||' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1935:4: '||'
            {
            match("||"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_PIPE

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1939:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1939:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1939:8: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1939:35: .
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
               channel=HIDDEN; setText("//"+getText().substring(1));
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1945:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1945:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1945:9: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1945:36: .
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

    // $ANTLR start MULTI_LINE_COMMENT
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1950:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1950:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1950:9: ( options {greedy=false; } : . )*
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1950:35: .
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

    // $ANTLR start MISC
    public final void mMISC() throws RecognitionException {
        try {
            int _type = MISC;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1955:3: ( '!' | '@' | '$' | '%' | '^' | '*' | '_' | '-' | '+' | '?' | '=' | '/' | '\\'' | '\\\\' | '|' | '&' )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='\'')||(input.LA(1)>='*' && input.LA(1)<='+')||input.LA(1)=='-'||input.LA(1)=='/'||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='@')||input.LA(1)=='\\'||(input.LA(1)>='^' && input.LA(1)<='_')||input.LA(1)=='|' ) {
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

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MISC

    public void mTokens() throws RecognitionException {
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:10: ( T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | DIALECT | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | IN | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC )
        int alt18=71;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:10: T70
                {
                mT70(); if (failed) return ;

                }
                break;
            case 2 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:14: T71
                {
                mT71(); if (failed) return ;

                }
                break;
            case 3 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:18: T72
                {
                mT72(); if (failed) return ;

                }
                break;
            case 4 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:22: T73
                {
                mT73(); if (failed) return ;

                }
                break;
            case 5 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:26: T74
                {
                mT74(); if (failed) return ;

                }
                break;
            case 6 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:30: T75
                {
                mT75(); if (failed) return ;

                }
                break;
            case 7 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:34: T76
                {
                mT76(); if (failed) return ;

                }
                break;
            case 8 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:38: T77
                {
                mT77(); if (failed) return ;

                }
                break;
            case 9 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:42: T78
                {
                mT78(); if (failed) return ;

                }
                break;
            case 10 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:46: T79
                {
                mT79(); if (failed) return ;

                }
                break;
            case 11 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:50: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 12 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:53: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 13 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:57: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 14 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:63: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 15 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:70: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 16 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:75: PACKAGE
                {
                mPACKAGE(); if (failed) return ;

                }
                break;
            case 17 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:83: IMPORT
                {
                mIMPORT(); if (failed) return ;

                }
                break;
            case 18 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:90: FUNCTION
                {
                mFUNCTION(); if (failed) return ;

                }
                break;
            case 19 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:99: GLOBAL
                {
                mGLOBAL(); if (failed) return ;

                }
                break;
            case 20 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:106: RULE
                {
                mRULE(); if (failed) return ;

                }
                break;
            case 21 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:111: QUERY
                {
                mQUERY(); if (failed) return ;

                }
                break;
            case 22 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:117: TEMPLATE
                {
                mTEMPLATE(); if (failed) return ;

                }
                break;
            case 23 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:126: ATTRIBUTES
                {
                mATTRIBUTES(); if (failed) return ;

                }
                break;
            case 24 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:137: DATE_EFFECTIVE
                {
                mDATE_EFFECTIVE(); if (failed) return ;

                }
                break;
            case 25 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:152: DATE_EXPIRES
                {
                mDATE_EXPIRES(); if (failed) return ;

                }
                break;
            case 26 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:165: ENABLED
                {
                mENABLED(); if (failed) return ;

                }
                break;
            case 27 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:173: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 28 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:182: NO_LOOP
                {
                mNO_LOOP(); if (failed) return ;

                }
                break;
            case 29 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:190: AUTO_FOCUS
                {
                mAUTO_FOCUS(); if (failed) return ;

                }
                break;
            case 30 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:201: ACTIVATION_GROUP
                {
                mACTIVATION_GROUP(); if (failed) return ;

                }
                break;
            case 31 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:218: AGENDA_GROUP
                {
                mAGENDA_GROUP(); if (failed) return ;

                }
                break;
            case 32 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:231: DIALECT
                {
                mDIALECT(); if (failed) return ;

                }
                break;
            case 33 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:239: RULEFLOW_GROUP
                {
                mRULEFLOW_GROUP(); if (failed) return ;

                }
                break;
            case 34 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:254: DURATION
                {
                mDURATION(); if (failed) return ;

                }
                break;
            case 35 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:263: LOCK_ON_ACTIVE
                {
                mLOCK_ON_ACTIVE(); if (failed) return ;

                }
                break;
            case 36 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:278: FROM
                {
                mFROM(); if (failed) return ;

                }
                break;
            case 37 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:283: ACCUMULATE
                {
                mACCUMULATE(); if (failed) return ;

                }
                break;
            case 38 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:294: INIT
                {
                mINIT(); if (failed) return ;

                }
                break;
            case 39 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:299: ACTION
                {
                mACTION(); if (failed) return ;

                }
                break;
            case 40 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:306: RESULT
                {
                mRESULT(); if (failed) return ;

                }
                break;
            case 41 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:313: COLLECT
                {
                mCOLLECT(); if (failed) return ;

                }
                break;
            case 42 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:321: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 43 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:324: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 44 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:328: CONTAINS
                {
                mCONTAINS(); if (failed) return ;

                }
                break;
            case 45 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:337: EXCLUDES
                {
                mEXCLUDES(); if (failed) return ;

                }
                break;
            case 46 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:346: MEMBEROF
                {
                mMEMBEROF(); if (failed) return ;

                }
                break;
            case 47 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:355: MATCHES
                {
                mMATCHES(); if (failed) return ;

                }
                break;
            case 48 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:363: IN
                {
                mIN(); if (failed) return ;

                }
                break;
            case 49 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:366: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 50 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:371: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 51 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:378: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 52 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:382: EVAL
                {
                mEVAL(); if (failed) return ;

                }
                break;
            case 53 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:387: FORALL
                {
                mFORALL(); if (failed) return ;

                }
                break;
            case 54 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:394: WHEN
                {
                mWHEN(); if (failed) return ;

                }
                break;
            case 55 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:399: THEN
                {
                mTHEN(); if (failed) return ;

                }
                break;
            case 56 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:404: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 57 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:408: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 58 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:411: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 59 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:422: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 60 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:434: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 61 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:446: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 62 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:459: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 63 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:470: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 64 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:482: COMMA
                {
                mCOMMA(); if (failed) return ;

                }
                break;
            case 65 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:488: DOT
                {
                mDOT(); if (failed) return ;

                }
                break;
            case 66 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:492: DOUBLE_AMPER
                {
                mDOUBLE_AMPER(); if (failed) return ;

                }
                break;
            case 67 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:505: DOUBLE_PIPE
                {
                mDOUBLE_PIPE(); if (failed) return ;

                }
                break;
            case 68 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:517: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 69 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:546: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 70 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:574: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 71 :
                // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:593: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:14: ( '\\r\\n' )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1752:16: '\\r\\n'
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


    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA18_eotS =
        "\3\uffff\1\55\2\53\1\61\1\63\1\53\1\uffff\1\65\1\uffff\1\53\21\52"+
        "\10\uffff\2\53\1\uffff\1\53\15\uffff\10\52\1\146\25\52\1\u0081\3"+
        "\52\4\uffff\11\52\1\uffff\7\52\1\u0095\7\52\1\u009d\4\52\1\u00a2"+
        "\1\uffff\4\52\1\uffff\4\52\1\u00ab\1\u00ac\3\52\1\u00b0\1\52\1\u00b2"+
        "\2\52\1\u00b6\4\52\1\uffff\7\52\1\uffff\2\52\1\u00c5\1\52\1\uffff"+
        "\1\u00c7\5\52\1\u00cd\1\52\2\uffff\2\52\1\u00ac\1\uffff\1\52\1\uffff"+
        "\3\52\1\uffff\1\52\1\u00d6\4\52\1\uffff\2\52\1\uffff\4\52\1\uffff"+
        "\1\52\2\uffff\4\52\1\uffff\1\52\1\u00e8\2\52\1\u00eb\1\u00ec\1\52"+
        "\1\u00ee\1\uffff\1\u00ef\5\52\1\uffff\3\52\1\u00fa\6\52\1\uffff"+
        "\1\52\1\u0102\2\uffff\1\52\2\uffff\3\52\1\uffff\1\u0107\2\uffff"+
        "\1\52\1\u0109\1\52\1\uffff\2\52\1\u010d\1\u010e\1\52\1\u0110\1\u0111"+
        "\1\uffff\4\52\1\uffff\1\u0116\1\uffff\1\u0117\1\u0118\1\u0119\2"+
        "\uffff\1\u011a\3\uffff\3\52\5\uffff\1\52\1\u011f\1\u0120\3\uffff";
    static final String DFA18_eofS =
        "\u0121\uffff";
    static final String DFA18_minS =
        "\1\11\2\uffff\1\52\1\60\4\75\1\uffff\1\56\1\uffff\1\0\1\145\2\141"+
        "\1\155\1\154\1\145\1\165\1\143\1\141\1\156\1\141\3\157\1\162\1\141"+
        "\1\150\10\uffff\1\46\1\174\1\uffff\1\52\15\uffff\1\155\1\145\1\165"+
        "\1\162\1\156\1\154\1\157\1\143\1\60\1\160\1\157\1\154\1\163\1\145"+
        "\1\143\1\144\2\164\1\145\1\141\1\164\1\162\1\141\1\143\1\141\1\154"+
        "\1\55\1\154\1\143\1\154\1\60\1\164\1\155\1\145\4\uffff\1\160\1\156"+
        "\1\145\1\141\1\143\1\163\1\155\1\153\1\164\1\uffff\1\157\1\142\1"+
        "\145\1\165\1\162\1\151\1\165\1\60\1\162\1\157\1\156\1\154\1\145"+
        "\1\141\1\142\1\60\1\154\1\163\1\154\1\151\1\60\1\uffff\1\154\1\153"+
        "\1\164\1\154\1\uffff\1\143\1\142\1\156\1\154\2\60\1\154\1\164\1"+
        "\145\1\60\1\141\1\60\1\162\1\141\1\60\1\154\1\171\1\157\1\155\1"+
        "\uffff\1\151\1\55\1\144\1\145\1\55\1\164\1\154\1\uffff\1\165\1\164"+
        "\1\60\1\145\1\uffff\1\60\1\55\1\141\1\145\1\150\1\145\1\60\1\141"+
        "\2\uffff\1\154\1\151\1\60\1\uffff\1\147\1\uffff\1\164\2\154\1\uffff"+
        "\1\164\1\60\1\156\1\141\1\165\1\142\1\uffff\1\141\1\143\1\145\1"+
        "\151\1\145\1\144\1\163\1\uffff\1\156\2\uffff\1\151\1\143\1\145\1"+
        "\162\1\uffff\1\164\1\60\1\157\1\145\2\60\1\157\1\60\1\uffff\1\60"+
        "\1\164\1\154\1\165\1\55\1\164\1\146\1\157\1\144\1\145\1\60\1\143"+
        "\1\156\1\164\1\163\1\117\1\145\1\uffff\1\156\1\60\2\uffff\1\167"+
        "\2\uffff\1\151\1\141\1\164\1\uffff\1\60\2\uffff\1\156\1\60\1\163"+
        "\1\uffff\1\145\1\163\2\60\1\146\2\60\1\uffff\1\55\1\157\1\164\1"+
        "\145\1\uffff\1\60\1\uffff\3\60\2\uffff\1\60\3\uffff\1\156\1\145"+
        "\1\163\5\uffff\1\55\2\60\3\uffff";
    static final String DFA18_maxS =
        "\1\u00ff\2\uffff\1\52\1\76\4\75\1\uffff\1\71\1\uffff\1\ufffe\1\162"+
        "\1\165\1\141\1\156\1\154\4\165\1\170\1\141\1\165\2\157\1\162\1\145"+
        "\1\150\10\uffff\1\46\1\174\1\uffff\1\57\15\uffff\1\155\1\145\1\165"+
        "\1\162\1\156\1\154\1\157\1\143\1\u00ff\1\160\1\157\1\154\1\163\1"+
        "\145\1\164\1\144\2\164\1\145\1\141\1\164\1\162\1\144\1\151\1\141"+
        "\1\154\1\164\1\154\1\143\1\156\1\u00ff\1\164\1\155\1\145\4\uffff"+
        "\1\160\1\156\1\145\1\141\1\143\1\163\1\155\1\153\1\164\1\uffff\1"+
        "\157\1\142\1\145\1\165\1\162\1\151\1\165\1\u00ff\1\162\1\157\1\156"+
        "\1\154\1\145\1\141\1\142\1\u00ff\1\154\1\163\1\154\1\151\1\u00ff"+
        "\1\uffff\1\154\1\153\1\164\1\154\1\uffff\1\143\1\142\1\156\1\154"+
        "\2\u00ff\1\154\1\164\1\145\1\u00ff\1\141\1\u00ff\1\162\1\141\1\u00ff"+
        "\1\154\1\171\1\166\1\155\1\uffff\1\151\1\55\1\144\1\145\1\55\1\164"+
        "\1\154\1\uffff\1\165\1\164\1\u00ff\1\145\1\uffff\1\u00ff\1\55\1"+
        "\141\1\145\1\150\1\145\1\u00ff\1\141\2\uffff\1\154\1\151\1\u00ff"+
        "\1\uffff\1\147\1\uffff\1\164\2\154\1\uffff\1\164\1\u00ff\1\156\1"+
        "\141\1\165\1\142\1\uffff\1\141\1\143\1\145\1\151\1\145\1\144\1\163"+
        "\1\uffff\1\156\2\uffff\1\151\1\143\1\145\1\162\1\uffff\1\164\1\u00ff"+
        "\1\157\1\145\2\u00ff\1\157\1\u00ff\1\uffff\1\u00ff\1\164\1\154\1"+
        "\165\1\55\1\164\1\170\1\157\1\144\1\145\1\u00ff\1\143\1\156\1\164"+
        "\1\163\1\117\1\145\1\uffff\1\156\1\u00ff\2\uffff\1\167\2\uffff\1"+
        "\151\1\141\1\164\1\uffff\1\u00ff\2\uffff\1\156\1\u00ff\1\163\1\uffff"+
        "\1\145\1\163\2\u00ff\1\146\2\u00ff\1\uffff\1\55\1\157\1\164\1\145"+
        "\1\uffff\1\u00ff\1\uffff\3\u00ff\2\uffff\1\u00ff\3\uffff\1\156\1"+
        "\145\1\163\5\uffff\1\55\2\u00ff\3\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\1\1\2\6\uffff\1\13\1\uffff\1\16\22\uffff\1\71\1\72\1"+
        "\73\1\74\1\75\1\76\1\77\1\100\2\uffff\1\104\1\uffff\1\71\1\107\1"+
        "\3\1\101\1\4\1\5\1\7\1\6\1\11\1\10\1\12\1\14\1\15\42\uffff\1\102"+
        "\1\103\1\106\1\105\11\uffff\1\60\25\uffff\1\34\4\uffff\1\52\23\uffff"+
        "\1\53\7\uffff\1\70\4\uffff\1\63\10\uffff\1\67\1\17\3\uffff\1\44"+
        "\1\uffff\1\46\3\uffff\1\24\6\uffff\1\35\7\uffff\1\64\1\uffff\1\61"+
        "\1\43\4\uffff\1\66\10\uffff\1\25\21\uffff\1\65\2\uffff\1\21\1\23"+
        "\1\uffff\1\50\1\47\3\uffff\1\37\1\uffff\1\30\1\31\3\uffff\1\62\7"+
        "\uffff\1\20\4\uffff\1\40\1\uffff\1\32\3\uffff\1\51\1\57\1\uffff"+
        "\1\26\1\22\1\41\3\uffff\1\42\1\55\1\33\1\54\1\56\3\uffff\1\36\1"+
        "\45\1\27";
    static final String DFA18_specialS =
        "\u0121\uffff}>";
    static final String[] DFA18_transitionS = {
            "\2\11\1\uffff\2\11\22\uffff\1\11\1\10\1\13\1\50\1\36\1\53\1"+
            "\46\1\14\1\37\1\40\2\53\1\45\1\4\1\3\1\51\12\12\1\2\1\1\1\7"+
            "\1\5\1\6\2\53\32\52\1\41\1\53\1\42\1\53\1\36\1\uffff\1\24\1"+
            "\52\1\32\1\25\1\26\1\16\1\21\1\52\1\20\2\52\1\31\1\34\1\30\1"+
            "\33\1\17\1\23\1\22\1\27\1\15\2\52\1\35\3\52\1\43\1\47\1\44\102"+
            "\uffff\100\52",
            "",
            "",
            "\1\54",
            "\12\12\4\uffff\1\56",
            "\1\57",
            "\1\60",
            "\1\62",
            "\1\64",
            "",
            "\1\66\1\uffff\12\12",
            "",
            "\uffff\13",
            "\1\67\2\uffff\1\70\11\uffff\1\71",
            "\1\74\15\uffff\1\72\2\uffff\1\75\2\uffff\1\73",
            "\1\76",
            "\1\100\1\77",
            "\1\101",
            "\1\103\17\uffff\1\102",
            "\1\104",
            "\1\105\3\uffff\1\111\6\uffff\1\106\5\uffff\1\107\1\110",
            "\1\113\7\uffff\1\112\13\uffff\1\114",
            "\1\115\7\uffff\1\117\1\uffff\1\116",
            "\1\120",
            "\1\121\5\uffff\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126\3\uffff\1\127",
            "\1\130",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\131",
            "\1\132",
            "",
            "\1\133\4\uffff\1\134",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\10\52\1\145\21\52"+
            "\105\uffff\100\52",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\155\20\uffff\1\154",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165\2\uffff\1\166",
            "\1\167\5\uffff\1\170",
            "\1\171",
            "\1\172",
            "\1\174\106\uffff\1\173",
            "\1\175",
            "\1\176",
            "\1\u0080\1\uffff\1\177",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0082",
            "\1\u0083",
            "\1\u0084",
            "",
            "",
            "",
            "",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00b1",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00b3",
            "\1\u00b4",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\5\52\1\u00b5\24\52"+
            "\105\uffff\100\52",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9\6\uffff\1\u00ba",
            "\1\u00bb",
            "",
            "\1\u00bc",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "",
            "\1\u00c3",
            "\1\u00c4",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00c6",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\1\u00cb",
            "\1\u00cc",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00ce",
            "",
            "",
            "\1\u00cf",
            "\1\u00d0",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\1\u00d1",
            "",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "",
            "\1\u00d5",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "",
            "\1\u00db",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "",
            "\1\u00e2",
            "",
            "",
            "\1\u00e3",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "",
            "\1\u00e7",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00e9",
            "\1\u00ea",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00ed",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\1\u00f5\21\uffff\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00fb",
            "\1\u00fc",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "",
            "\1\u0101",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "\1\u0103",
            "",
            "",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "\1\u0108",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u010a",
            "",
            "\1\u010b",
            "\1\u010c",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u010f",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\1\u0112",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "",
            "\1\u011b",
            "\1\u011c",
            "\1\u011d",
            "",
            "",
            "",
            "",
            "",
            "\1\u011e",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | DIALECT | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | IN | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | COMMA | DOT | DOUBLE_AMPER | DOUBLE_PIPE | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | MULTI_LINE_COMMENT | MISC );";
        }
    }
 

}