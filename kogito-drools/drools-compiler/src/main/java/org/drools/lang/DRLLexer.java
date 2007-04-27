// $ANTLR 3.0b7 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-04-27 11:20:36

	package org.drools.lang;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class DRLLexer extends Lexer {
    public static final int EXISTS=49;
    public static final int T79=79;
    public static final int AUTO_FOCUS=22;
    public static final int END=10;
    public static final int HexDigit=57;
    public static final int FORALL=52;
    public static final int TEMPLATE=11;
    public static final int T70=70;
    public static final int MISC=63;
    public static final int FLOAT=41;
    public static final int T74=74;
    public static final int QUERY=9;
    public static final int THEN=53;
    public static final int RULE=12;
    public static final int INIT=30;
    public static final int IMPORT=6;
    public static final int OR=35;
    public static final int PACKAGE=5;
    public static final int DATE_EFFECTIVE=14;
    public static final int T64=64;
    public static final int AND=47;
    public static final int FUNCTION=7;
    public static final int GLOBAL=8;
    public static final int EscapeSequence=56;
    public static final int DIALECT=27;
    public static final int INT=20;
    public static final int LOCK_ON_ACTIVE=28;
    public static final int DATE_EXPIRES=16;
    public static final int LEFT_SQUARE=45;
    public static final int CONTAINS=38;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=60;
    public static final int T77=77;
    public static final int ATTRIBUTES=4;
    public static final int RESULT=32;
    public static final int LEFT_CURLY=43;
    public static final int T69=69;
    public static final int FROM=48;
    public static final int ID=34;
    public static final int ACTIVATION_GROUP=23;
    public static final int LEFT_PAREN=36;
    public static final int RIGHT_CURLY=44;
    public static final int BOOL=18;
    public static final int EXCLUDES=40;
    public static final int T73=73;
    public static final int T68=68;
    public static final int WHEN=13;
    public static final int T78=78;
    public static final int RULEFLOW_GROUP=24;
    public static final int WS=55;
    public static final int STRING=15;
    public static final int T66=66;
    public static final int ACTION=31;
    public static final int T71=71;
    public static final int T72=72;
    public static final int T65=65;
    public static final int COLLECT=33;
    public static final int T76=76;
    public static final int ACCUMULATE=29;
    public static final int NO_LOOP=21;
    public static final int UnicodeEscape=58;
    public static final int T75=75;
    public static final int DURATION=26;
    public static final int EVAL=51;
    public static final int MATCHES=39;
    public static final int EOF=-1;
    public static final int T67=67;
    public static final int NULL=42;
    public static final int AGENDA_GROUP=25;
    public static final int EOL=54;
    public static final int Tokens=80;
    public static final int SALIENCE=19;
    public static final int OctalEscape=59;
    public static final int MULTI_LINE_COMMENT=62;
    public static final int RIGHT_PAREN=37;
    public static final int NOT=50;
    public static final int ENABLED=17;
    public static final int RIGHT_SQUARE=46;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=61;
    public DRLLexer() {;} 
    public DRLLexer(CharStream input) {
        super(input);
        ruleMemo = new HashMap[78+1];
     }
    public String getGrammarFileName() { return "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }

    // $ANTLR start T64
    public final void mT64() throws RecognitionException {
        try {
            int _type = T64;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:6:7: ( ';' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:6:7: ';'
            {
            match(';'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T64

    // $ANTLR start T65
    public final void mT65() throws RecognitionException {
        try {
            int _type = T65;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:7:7: ( ':' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:7:7: ':'
            {
            match(':'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T65

    // $ANTLR start T66
    public final void mT66() throws RecognitionException {
        try {
            int _type = T66;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:8:7: ( ',' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:8:7: ','
            {
            match(','); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T66

    // $ANTLR start T67
    public final void mT67() throws RecognitionException {
        try {
            int _type = T67;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:9:7: ( '.' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:9:7: '.'
            {
            match('.'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T67

    // $ANTLR start T68
    public final void mT68() throws RecognitionException {
        try {
            int _type = T68;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:10:7: ( '.*' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:10:7: '.*'
            {
            match(".*"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T68

    // $ANTLR start T69
    public final void mT69() throws RecognitionException {
        try {
            int _type = T69;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:11:7: ( '||' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:11:7: '||'
            {
            match("||"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T69

    // $ANTLR start T70
    public final void mT70() throws RecognitionException {
        try {
            int _type = T70;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:12:7: ( '&' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:12:7: '&'
            {
            match('&'); if (failed) return ;

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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:13:7: ( '|' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:13:7: '|'
            {
            match('|'); if (failed) return ;

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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:14:7: ( '->' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:14:7: '->'
            {
            match("->"); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:15:7: ( '==' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:15:7: '=='
            {
            match("=="); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:16:7: ( '>' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:16:7: '>'
            {
            match('>'); if (failed) return ;

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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:17:7: ( '>=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:17:7: '>='
            {
            match(">="); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:18:7: ( '<' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:18:7: '<'
            {
            match('<'); if (failed) return ;

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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:19:7: ( '<=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:19:7: '<='
            {
            match("<="); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:20:7: ( '!=' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:20:7: '!='
            {
            match("!="); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:21:7: ( '&&' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:21:7: '&&'
            {
            match("&&"); if (failed) return ;


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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:17: ( ( ' ' | '\\t' | '\\f' | EOL ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:17: ( ' ' | '\\t' | '\\f' | EOL )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:17: ( ' ' | '\\t' | '\\f' | EOL )
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
                    new NoViableAltException("1489:17: ( ' ' | '\\t' | '\\f' | EOL )", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1489:19: ' '
                    {
                    match(' '); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1490:19: '\\t'
                    {
                    match('\t'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1491:19: '\\f'
                    {
                    match('\f'); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1492:19: EOL
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

    // $ANTLR start EOL
    public final void mEOL() throws RecognitionException {
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:6: ( ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )
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
                    new NoViableAltException("1499:6: ( ( '\\r\\n' )=> '\\r\\n' | '\\r' | '\\n' )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:14: ( '\\r\\n' )=> '\\r\\n'
                    {
                    match("\r\n"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1500:25: '\\r'
                    {
                    match('\r'); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:25: '\\n'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:4: ( ( '-' )? ( '0' .. '9' )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:4: ( '-' )? ( '0' .. '9' )+
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:4: ( '-' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='-') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:10: ( '0' .. '9' )+
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1506:11: '0' .. '9'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:4: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:4: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )+
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:4: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:5: '-'
                    {
                    match('-'); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:10: ( '0' .. '9' )+
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:11: '0' .. '9'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:26: ( '0' .. '9' )+
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:27: '0' .. '9'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:8: ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) )
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
                    new NoViableAltException("1513:1: STRING : ( ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' ) | ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' ) );", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:8: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:9: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:13: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
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
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:15: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1514:32: ~ ( '\\\\' | '\"' )
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:8: ( '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\'' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:9: '\\'' ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:14: ( EscapeSequence | ~ ( '\\\\' | '\\'' ) )*
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
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:16: EscapeSequence
                    	    {
                    	    mEscapeSequence(); if (failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1515:33: ~ ( '\\\\' | '\\'' )
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:12: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1519:12: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1523:9: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' ) | UnicodeEscape | OctalEscape )
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
                case '.':
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
                        new NoViableAltException("1521:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' ) | UnicodeEscape | OctalEscape );", 11, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1521:1: fragment EscapeSequence : ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' ) | UnicodeEscape | OctalEscape );", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1523:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | '.' )
                    {
                    match('\\'); if (failed) return ;
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='.'||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1524:9: UnicodeEscape
                    {
                    mUnicodeEscape(); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1525:9: OctalEscape
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:9: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
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
                        new NoViableAltException("1528:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1528:1: fragment OctalEscape : ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) );", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:14: ( '0' .. '3' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:15: '0' .. '3'
                    {
                    matchRange('0','3'); if (failed) return ;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:25: ( '0' .. '7' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:36: ( '0' .. '7' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1530:37: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:14: ( '0' .. '7' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:15: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:25: ( '0' .. '7' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1531:26: '0' .. '7'
                    {
                    matchRange('0','7'); if (failed) return ;

                    }


                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); if (failed) return ;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:14: ( '0' .. '7' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1532:15: '0' .. '7'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1537:9: ( '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1537:9: '\\\\' 'u' HexDigit HexDigit HexDigit HexDigit
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:4: ( ( 'true' | 'false' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:4: ( 'true' | 'false' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:4: ( 'true' | 'false' )
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
                    new NoViableAltException("1541:4: ( 'true' | 'false' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:5: 'true'
                    {
                    match("true"); if (failed) return ;


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1541:12: 'false'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1544:11: ( 'package' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1544:11: 'package'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1546:10: ( 'import' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1546:10: 'import'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1548:12: ( 'function' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1548:12: 'function'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:10: ( 'global' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1550:10: 'global'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:11: ( 'rule' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1552:11: 'rule'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1554:9: ( 'query' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1554:9: 'query'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1556:12: ( 'template' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1556:12: 'template'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1558:14: ( 'attributes' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1558:14: 'attributes'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1561:4: ( 'date-effective' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1561:4: 'date-effective'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1564:4: ( 'date-expires' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1564:4: 'date-expires'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1566:11: ( 'enabled' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1566:11: 'enabled'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1569:4: ( 'salience' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1569:4: 'salience'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:11: ( 'no-loop' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1571:11: 'no-loop'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1574:4: ( 'auto-focus' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1574:4: 'auto-focus'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1577:4: ( 'activation-group' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1577:4: 'activation-group'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1580:4: ( 'agenda-group' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1580:4: 'agenda-group'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1583:4: ( 'dialect' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1583:4: 'dialect'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1586:4: ( 'ruleflow-group' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1586:4: 'ruleflow-group'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1589:4: ( 'duration' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1589:4: 'duration'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:4: ( 'lock-on-active' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1592:4: 'lock-on-active'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:8: ( 'from' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1594:8: 'from'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1597:4: ( 'accumulate' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1597:4: 'accumulate'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1599:8: ( 'init' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1599:8: 'init'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1601:10: ( 'action' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1601:10: 'action'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1603:10: ( 'result' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1603:10: 'result'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1605:11: ( 'collect' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1605:11: 'collect'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1607:6: ( 'or' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1607:6: 'or'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1609:7: ( 'and' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1609:7: 'and'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:4: ( 'contains' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:4: 'contains'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1615:4: ( 'excludes' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1615:4: 'excludes'
            {
            match("excludes"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXCLUDES

    // $ANTLR start MATCHES
    public final void mMATCHES() throws RecognitionException {
        try {
            int _type = MATCHES;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1617:11: ( 'matches' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1617:11: 'matches'
            {
            match("matches"); if (failed) return ;


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MATCHES

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:8: ( 'null' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1619:8: 'null'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1621:10: ( 'exists' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1621:10: 'exists'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1623:7: ( 'not' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1623:7: 'not'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1625:8: ( 'eval' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1625:8: 'eval'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:10: ( 'forall' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:10: 'forall'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:11: ( 'when' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1629:11: 'when'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1631:12: ( 'then' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1631:12: 'then'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1633:11: ( 'end' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1633:11: 'end'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:4: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '\\u00c0' .. '\\u00ff' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1636:50: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' | '\\u00c0' .. '\\u00ff' )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='0' && LA14_0<='9')||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')||(LA14_0>='\u00C0' && LA14_0<='\u00FF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
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

    // $ANTLR start SH_STYLE_SINGLE_LINE_COMMENT
    public final void mSH_STYLE_SINGLE_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = SH_STYLE_SINGLE_LINE_COMMENT;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1641:4: ( '#' ( options {greedy=false; } : . )* EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1641:4: '#' ( options {greedy=false; } : . )* EOL
            {
            match('#'); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1641:8: ( options {greedy=false; } : . )*
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1641:35: .
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:4: ( '//' ( options {greedy=false; } : . )* EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:4: '//' ( options {greedy=false; } : . )* EOL
            {
            match("//"); if (failed) return ;

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:9: ( options {greedy=false; } : . )*
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1647:36: .
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1653:11: ( '(' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1653:11: '('
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:11: ( ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1657:11: ')'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1661:11: ( '[' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1661:11: '['
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:11: ( ']' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1665:11: ']'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1669:11: ( '{' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1669:11: '{'
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1673:11: ( '}' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1673:11: '}'
            {
            match('}'); if (failed) return ;

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY

    // $ANTLR start MULTI_LINE_COMMENT
    public final void mMULTI_LINE_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_LINE_COMMENT;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:4: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (failed) return ;

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:9: ( options {greedy=false; } : . )*
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1677:35: .
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1682:3: ( '!' | '@' | '$' | '%' | '^' | '&' | '*' | '_' | '-' | '+' | '?' | '|' | ',' | '=' | '/' | '\\'' | '\\\\' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='\'')||(input.LA(1)>='*' && input.LA(1)<='-')||input.LA(1)=='/'||input.LA(1)=='='||(input.LA(1)>='?' && input.LA(1)<='@')||input.LA(1)=='\\'||(input.LA(1)>='^' && input.LA(1)<='_')||input.LA(1)=='|' ) {
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
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:10: ( T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | DIALECT | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MATCHES | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | MULTI_LINE_COMMENT | MISC )
        int alt18=71;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:10: T64
                {
                mT64(); if (failed) return ;

                }
                break;
            case 2 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:14: T65
                {
                mT65(); if (failed) return ;

                }
                break;
            case 3 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:18: T66
                {
                mT66(); if (failed) return ;

                }
                break;
            case 4 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:22: T67
                {
                mT67(); if (failed) return ;

                }
                break;
            case 5 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:26: T68
                {
                mT68(); if (failed) return ;

                }
                break;
            case 6 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:30: T69
                {
                mT69(); if (failed) return ;

                }
                break;
            case 7 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:34: T70
                {
                mT70(); if (failed) return ;

                }
                break;
            case 8 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:38: T71
                {
                mT71(); if (failed) return ;

                }
                break;
            case 9 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:42: T72
                {
                mT72(); if (failed) return ;

                }
                break;
            case 10 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:46: T73
                {
                mT73(); if (failed) return ;

                }
                break;
            case 11 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:50: T74
                {
                mT74(); if (failed) return ;

                }
                break;
            case 12 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:54: T75
                {
                mT75(); if (failed) return ;

                }
                break;
            case 13 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:58: T76
                {
                mT76(); if (failed) return ;

                }
                break;
            case 14 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:62: T77
                {
                mT77(); if (failed) return ;

                }
                break;
            case 15 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:66: T78
                {
                mT78(); if (failed) return ;

                }
                break;
            case 16 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:70: T79
                {
                mT79(); if (failed) return ;

                }
                break;
            case 17 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:74: WS
                {
                mWS(); if (failed) return ;

                }
                break;
            case 18 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:77: INT
                {
                mINT(); if (failed) return ;

                }
                break;
            case 19 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:81: FLOAT
                {
                mFLOAT(); if (failed) return ;

                }
                break;
            case 20 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:87: STRING
                {
                mSTRING(); if (failed) return ;

                }
                break;
            case 21 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:94: BOOL
                {
                mBOOL(); if (failed) return ;

                }
                break;
            case 22 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:99: PACKAGE
                {
                mPACKAGE(); if (failed) return ;

                }
                break;
            case 23 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:107: IMPORT
                {
                mIMPORT(); if (failed) return ;

                }
                break;
            case 24 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:114: FUNCTION
                {
                mFUNCTION(); if (failed) return ;

                }
                break;
            case 25 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:123: GLOBAL
                {
                mGLOBAL(); if (failed) return ;

                }
                break;
            case 26 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:130: RULE
                {
                mRULE(); if (failed) return ;

                }
                break;
            case 27 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:135: QUERY
                {
                mQUERY(); if (failed) return ;

                }
                break;
            case 28 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:141: TEMPLATE
                {
                mTEMPLATE(); if (failed) return ;

                }
                break;
            case 29 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:150: ATTRIBUTES
                {
                mATTRIBUTES(); if (failed) return ;

                }
                break;
            case 30 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:161: DATE_EFFECTIVE
                {
                mDATE_EFFECTIVE(); if (failed) return ;

                }
                break;
            case 31 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:176: DATE_EXPIRES
                {
                mDATE_EXPIRES(); if (failed) return ;

                }
                break;
            case 32 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:189: ENABLED
                {
                mENABLED(); if (failed) return ;

                }
                break;
            case 33 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:197: SALIENCE
                {
                mSALIENCE(); if (failed) return ;

                }
                break;
            case 34 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:206: NO_LOOP
                {
                mNO_LOOP(); if (failed) return ;

                }
                break;
            case 35 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:214: AUTO_FOCUS
                {
                mAUTO_FOCUS(); if (failed) return ;

                }
                break;
            case 36 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:225: ACTIVATION_GROUP
                {
                mACTIVATION_GROUP(); if (failed) return ;

                }
                break;
            case 37 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:242: AGENDA_GROUP
                {
                mAGENDA_GROUP(); if (failed) return ;

                }
                break;
            case 38 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:255: DIALECT
                {
                mDIALECT(); if (failed) return ;

                }
                break;
            case 39 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:263: RULEFLOW_GROUP
                {
                mRULEFLOW_GROUP(); if (failed) return ;

                }
                break;
            case 40 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:278: DURATION
                {
                mDURATION(); if (failed) return ;

                }
                break;
            case 41 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:287: LOCK_ON_ACTIVE
                {
                mLOCK_ON_ACTIVE(); if (failed) return ;

                }
                break;
            case 42 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:302: FROM
                {
                mFROM(); if (failed) return ;

                }
                break;
            case 43 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:307: ACCUMULATE
                {
                mACCUMULATE(); if (failed) return ;

                }
                break;
            case 44 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:318: INIT
                {
                mINIT(); if (failed) return ;

                }
                break;
            case 45 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:323: ACTION
                {
                mACTION(); if (failed) return ;

                }
                break;
            case 46 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:330: RESULT
                {
                mRESULT(); if (failed) return ;

                }
                break;
            case 47 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:337: COLLECT
                {
                mCOLLECT(); if (failed) return ;

                }
                break;
            case 48 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:345: OR
                {
                mOR(); if (failed) return ;

                }
                break;
            case 49 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:348: AND
                {
                mAND(); if (failed) return ;

                }
                break;
            case 50 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:352: CONTAINS
                {
                mCONTAINS(); if (failed) return ;

                }
                break;
            case 51 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:361: EXCLUDES
                {
                mEXCLUDES(); if (failed) return ;

                }
                break;
            case 52 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:370: MATCHES
                {
                mMATCHES(); if (failed) return ;

                }
                break;
            case 53 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:378: NULL
                {
                mNULL(); if (failed) return ;

                }
                break;
            case 54 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:383: EXISTS
                {
                mEXISTS(); if (failed) return ;

                }
                break;
            case 55 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:390: NOT
                {
                mNOT(); if (failed) return ;

                }
                break;
            case 56 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:394: EVAL
                {
                mEVAL(); if (failed) return ;

                }
                break;
            case 57 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:399: FORALL
                {
                mFORALL(); if (failed) return ;

                }
                break;
            case 58 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:406: WHEN
                {
                mWHEN(); if (failed) return ;

                }
                break;
            case 59 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:411: THEN
                {
                mTHEN(); if (failed) return ;

                }
                break;
            case 60 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:416: END
                {
                mEND(); if (failed) return ;

                }
                break;
            case 61 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:420: ID
                {
                mID(); if (failed) return ;

                }
                break;
            case 62 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:423: SH_STYLE_SINGLE_LINE_COMMENT
                {
                mSH_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 63 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:452: C_STYLE_SINGLE_LINE_COMMENT
                {
                mC_STYLE_SINGLE_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 64 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:480: LEFT_PAREN
                {
                mLEFT_PAREN(); if (failed) return ;

                }
                break;
            case 65 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:491: RIGHT_PAREN
                {
                mRIGHT_PAREN(); if (failed) return ;

                }
                break;
            case 66 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:503: LEFT_SQUARE
                {
                mLEFT_SQUARE(); if (failed) return ;

                }
                break;
            case 67 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:515: RIGHT_SQUARE
                {
                mRIGHT_SQUARE(); if (failed) return ;

                }
                break;
            case 68 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:528: LEFT_CURLY
                {
                mLEFT_CURLY(); if (failed) return ;

                }
                break;
            case 69 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:539: RIGHT_CURLY
                {
                mRIGHT_CURLY(); if (failed) return ;

                }
                break;
            case 70 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:551: MULTI_LINE_COMMENT
                {
                mMULTI_LINE_COMMENT(); if (failed) return ;

                }
                break;
            case 71 :
                // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1:570: MISC
                {
                mMISC(); if (failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1
    public final void synpred1_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:14: ( '\\r\\n' )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1499:16: '\\r\\n'
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
        "\4\uffff\1\56\1\60\1\62\2\53\1\66\1\70\1\53\1\uffff\1\72\1\uffff"+
        "\1\53\21\52\2\uffff\1\53\30\uffff\36\52\1\u0082\2\52\2\uffff\23"+
        "\52\1\u0098\7\52\1\u00a0\1\52\1\uffff\1\u00a2\4\52\1\uffff\2\52"+
        "\1\u00a9\1\u00aa\3\52\1\u00ae\2\52\1\u00b1\3\52\1\u00b6\6\52\1\uffff"+
        "\3\52\1\u00c1\3\52\1\uffff\1\52\1\uffff\1\u00c6\4\52\1\u00cb\2\uffff"+
        "\1\52\1\u00a9\1\52\1\uffff\2\52\1\uffff\4\52\1\uffff\1\u00d4\4\52"+
        "\1\uffff\1\52\1\uffff\2\52\1\uffff\4\52\2\uffff\3\52\1\uffff\2\52"+
        "\1\u00e6\1\52\1\u00e8\1\u00e9\1\u00ea\1\52\1\uffff\1\52\1\u00ed"+
        "\3\52\1\uffff\2\52\1\u00f5\10\52\1\uffff\1\u00fe\3\uffff\2\52\1"+
        "\uffff\1\52\1\uffff\1\52\2\uffff\1\u0103\1\52\1\uffff\1\52\1\u0106"+
        "\2\52\1\u0109\1\u010a\1\u010b\1\u010c\1\uffff\4\52\1\uffff\1\u0111"+
        "\1\u0112\1\uffff\1\u0113\1\u0114\5\uffff\3\52\4\uffff\1\u0118\1"+
        "\52\1\u011a\3\uffff";
    static final String DFA18_eofS =
        "\u011b\uffff";
    static final String DFA18_minS =
        "\1\11\3\uffff\1\52\1\174\1\46\1\60\4\75\1\uffff\1\56\1\uffff\1\0"+
        "\1\145\2\141\1\155\1\154\1\145\1\165\1\143\1\141\1\156\1\141\3\157"+
        "\1\162\1\141\1\150\2\uffff\1\52\30\uffff\1\165\1\145\1\155\1\154"+
        "\1\156\1\157\1\162\1\143\1\151\1\160\1\157\1\163\1\154\1\145\1\143"+
        "\1\145\2\164\1\144\1\164\1\141\1\162\1\141\1\143\1\141\1\154\1\55"+
        "\1\154\1\143\1\154\1\60\1\164\1\145\2\uffff\1\145\1\156\1\160\1"+
        "\163\1\143\1\155\1\141\1\153\1\164\1\157\1\142\1\165\1\145\1\162"+
        "\1\165\1\151\1\156\1\157\1\162\1\60\1\145\1\154\1\141\1\154\1\163"+
        "\1\154\1\142\1\60\1\151\1\uffff\1\60\1\154\1\153\1\164\1\154\1\uffff"+
        "\1\143\1\156\2\60\1\154\1\145\1\164\1\60\1\154\1\141\1\60\1\162"+
        "\1\141\1\154\1\60\1\171\1\155\1\157\1\144\1\55\1\151\1\uffff\1\55"+
        "\1\145\1\164\1\60\1\164\1\165\1\154\1\uffff\1\145\1\uffff\1\60\1"+
        "\55\1\141\1\145\1\150\1\60\2\uffff\1\141\1\60\1\151\1\uffff\1\154"+
        "\1\147\1\uffff\1\164\1\154\1\164\1\154\1\uffff\1\60\1\165\1\156"+
        "\2\141\1\uffff\1\142\1\145\1\143\1\151\1\uffff\1\163\1\144\1\145"+
        "\1\156\2\uffff\1\151\1\143\1\145\1\uffff\1\164\1\157\1\60\1\145"+
        "\3\60\1\157\1\uffff\1\154\1\60\1\164\1\55\1\165\1\146\1\164\1\157"+
        "\1\60\1\145\1\144\1\143\1\156\1\164\1\163\1\145\1\156\1\uffff\1"+
        "\60\3\uffff\1\167\1\141\1\uffff\1\151\1\uffff\1\164\2\uffff\1\60"+
        "\1\156\1\uffff\1\163\1\60\1\145\1\163\4\60\1\uffff\1\55\1\164\1"+
        "\157\1\145\1\uffff\2\60\1\uffff\2\60\5\uffff\1\145\1\156\1\163\4"+
        "\uffff\1\60\1\55\1\60\3\uffff";
    static final String DFA18_maxS =
        "\1\u00ff\3\uffff\1\52\1\174\1\46\1\76\4\75\1\uffff\1\71\1\uffff"+
        "\1\ufffe\1\162\1\165\1\141\1\156\1\154\4\165\1\170\1\141\1\165\2"+
        "\157\1\162\1\141\1\150\2\uffff\1\57\30\uffff\1\165\1\145\1\155\1"+
        "\154\1\156\1\157\1\162\1\143\1\151\1\160\1\157\1\163\1\154\1\145"+
        "\1\164\1\145\2\164\1\144\1\164\1\141\1\162\1\141\1\151\1\144\1\154"+
        "\1\164\1\154\1\143\1\156\1\u00ff\1\164\1\145\2\uffff\1\145\1\156"+
        "\1\160\1\163\1\143\1\155\1\141\1\153\1\164\1\157\1\142\1\165\1\145"+
        "\1\162\1\165\1\151\1\156\1\157\1\162\1\u00ff\1\145\1\154\1\141\1"+
        "\154\1\163\1\154\1\142\1\u00ff\1\151\1\uffff\1\u00ff\1\154\1\153"+
        "\1\164\1\154\1\uffff\1\143\1\156\2\u00ff\1\154\1\145\1\164\1\u00ff"+
        "\1\154\1\141\1\u00ff\1\162\1\141\1\154\1\u00ff\1\171\1\155\1\166"+
        "\1\144\1\55\1\151\1\uffff\1\55\1\145\1\164\1\u00ff\1\164\1\165\1"+
        "\154\1\uffff\1\145\1\uffff\1\u00ff\1\55\1\141\1\145\1\150\1\u00ff"+
        "\2\uffff\1\141\1\u00ff\1\151\1\uffff\1\154\1\147\1\uffff\1\164\1"+
        "\154\1\164\1\154\1\uffff\1\u00ff\1\165\1\156\2\141\1\uffff\1\142"+
        "\1\145\1\143\1\151\1\uffff\1\163\1\144\1\145\1\156\2\uffff\1\151"+
        "\1\143\1\145\1\uffff\1\164\1\157\1\u00ff\1\145\3\u00ff\1\157\1\uffff"+
        "\1\154\1\u00ff\1\164\1\55\1\165\1\170\1\164\1\157\1\u00ff\1\145"+
        "\1\144\1\143\1\156\1\164\1\163\1\145\1\156\1\uffff\1\u00ff\3\uffff"+
        "\1\167\1\141\1\uffff\1\151\1\uffff\1\164\2\uffff\1\u00ff\1\156\1"+
        "\uffff\1\163\1\u00ff\1\145\1\163\4\u00ff\1\uffff\1\55\1\164\1\157"+
        "\1\145\1\uffff\2\u00ff\1\uffff\2\u00ff\5\uffff\1\145\1\156\1\163"+
        "\4\uffff\1\u00ff\1\55\1\u00ff\3\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\1\1\2\1\3\10\uffff\1\21\1\uffff\1\24\22\uffff\1\75\1"+
        "\76\1\uffff\1\100\1\101\1\102\1\103\1\104\1\105\1\75\1\107\1\3\1"+
        "\5\1\4\1\6\1\10\1\20\1\7\1\11\1\12\1\14\1\13\1\16\1\15\1\17\1\22"+
        "\1\23\41\uffff\1\106\1\77\35\uffff\1\42\5\uffff\1\60\25\uffff\1"+
        "\61\7\uffff\1\74\1\uffff\1\67\6\uffff\1\25\1\73\3\uffff\1\52\2\uffff"+
        "\1\54\4\uffff\1\32\5\uffff\1\43\4\uffff\1\70\4\uffff\1\65\1\51\3"+
        "\uffff\1\72\10\uffff\1\33\21\uffff\1\71\1\uffff\1\27\1\31\1\56\2"+
        "\uffff\1\55\1\uffff\1\45\1\uffff\1\36\1\37\2\uffff\1\66\10\uffff"+
        "\1\26\4\uffff\1\46\2\uffff\1\40\2\uffff\1\57\1\64\1\34\1\30\1\47"+
        "\3\uffff\1\50\1\63\1\41\1\62\3\uffff\1\53\1\44\1\35";
    static final String DFA18_specialS =
        "\u011b\uffff}>";
    static final String[] DFA18_transitionS = {
            "\2\14\1\uffff\2\14\22\uffff\1\14\1\13\1\16\1\42\1\41\1\53\1"+
            "\6\1\17\1\44\1\45\2\53\1\3\1\7\1\4\1\43\12\15\1\2\1\1\1\12\1"+
            "\10\1\11\2\53\32\52\1\46\1\53\1\47\1\53\1\41\1\uffff\1\27\1"+
            "\52\1\35\1\30\1\31\1\21\1\24\1\52\1\23\2\52\1\34\1\37\1\33\1"+
            "\36\1\22\1\26\1\25\1\32\1\20\2\52\1\40\3\52\1\50\1\5\1\51\102"+
            "\uffff\100\52",
            "",
            "",
            "",
            "\1\55",
            "\1\57",
            "\1\61",
            "\12\15\4\uffff\1\63",
            "\1\64",
            "\1\65",
            "\1\67",
            "\1\71",
            "",
            "\1\73\1\uffff\12\15",
            "",
            "\uffff\16",
            "\1\76\2\uffff\1\75\11\uffff\1\74",
            "\1\77\15\uffff\1\102\2\uffff\1\101\2\uffff\1\100",
            "\1\103",
            "\1\105\1\104",
            "\1\106",
            "\1\107\17\uffff\1\110",
            "\1\111",
            "\1\112\3\uffff\1\113\6\uffff\1\116\5\uffff\1\115\1\114",
            "\1\117\7\uffff\1\120\13\uffff\1\121",
            "\1\124\7\uffff\1\122\1\uffff\1\123",
            "\1\125",
            "\1\126\5\uffff\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "",
            "",
            "\1\135\4\uffff\1\136",
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
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "\1\155\20\uffff\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\170\5\uffff\1\167",
            "\1\171\2\uffff\1\172",
            "\1\173",
            "\1\174\106\uffff\1\175",
            "\1\176",
            "\1\177",
            "\1\u0081\1\uffff\1\u0080",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0083",
            "\1\u0084",
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
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "\1\u0096",
            "\1\u0097",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00a1",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "",
            "\1\u00a7",
            "\1\u00a8",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00af",
            "\1\u00b0",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\5\52\1\u00b5\24\52"+
            "\105\uffff\100\52",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9\6\uffff\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c4",
            "",
            "\1\u00c5",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "\1\u00cc",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00cd",
            "",
            "\1\u00ce",
            "\1\u00cf",
            "",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\1\u00dc",
            "",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "",
            "",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "",
            "\1\u00e4",
            "\1\u00e5",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00e7",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00eb",
            "",
            "\1\u00ec",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00ee",
            "\1\u00ef",
            "\1\u00f0",
            "\1\u00f1\21\uffff\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\u00f9",
            "\1\u00fa",
            "\1\u00fb",
            "\1\u00fc",
            "\1\u00fd",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "",
            "\1\u00ff",
            "\1\u0100",
            "",
            "\1\u0101",
            "",
            "\1\u0102",
            "",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0104",
            "",
            "\1\u0105",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0107",
            "\1\u0108",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\1\u010d",
            "\1\u010e",
            "\1\u010f",
            "\1\u0110",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "",
            "",
            "",
            "",
            "",
            "\1\u0115",
            "\1\u0116",
            "\1\u0117",
            "",
            "",
            "",
            "",
            "\12\52\7\uffff\32\52\4\uffff\1\52\1\uffff\32\52\105\uffff\100"+
            "\52",
            "\1\u0119",
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
            return "1:1: Tokens : ( T64 | T65 | T66 | T67 | T68 | T69 | T70 | T71 | T72 | T73 | T74 | T75 | T76 | T77 | T78 | T79 | WS | INT | FLOAT | STRING | BOOL | PACKAGE | IMPORT | FUNCTION | GLOBAL | RULE | QUERY | TEMPLATE | ATTRIBUTES | DATE_EFFECTIVE | DATE_EXPIRES | ENABLED | SALIENCE | NO_LOOP | AUTO_FOCUS | ACTIVATION_GROUP | AGENDA_GROUP | DIALECT | RULEFLOW_GROUP | DURATION | LOCK_ON_ACTIVE | FROM | ACCUMULATE | INIT | ACTION | RESULT | COLLECT | OR | AND | CONTAINS | EXCLUDES | MATCHES | NULL | EXISTS | NOT | EVAL | FORALL | WHEN | THEN | END | ID | SH_STYLE_SINGLE_LINE_COMMENT | C_STYLE_SINGLE_LINE_COMMENT | LEFT_PAREN | RIGHT_PAREN | LEFT_SQUARE | RIGHT_SQUARE | LEFT_CURLY | RIGHT_CURLY | MULTI_LINE_COMMENT | MISC );";
        }
    }
 

}